package com.guaji.game.recharge;

import java.net.URLEncoder;
import java.nio.ByteBuffer;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpHeaders;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.guaji.app.App;
import org.guaji.app.AppObj;
import org.guaji.config.ConfigManager;
import org.guaji.cryption.Base64;
import org.guaji.db.DBManager;
import org.guaji.log.Log;
import org.guaji.msg.Msg;
import org.guaji.net.GuaJiSession;
import org.guaji.net.protocol.Protocol;
import org.guaji.obj.ObjBase;
import org.guaji.os.GuaJiTime;
import org.guaji.os.MyException;
import org.guaji.util.services.ReportService;
import org.guaji.xid.GuaJiXID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.guaji.game.GsApp;
import com.guaji.game.ServerData;
import com.guaji.game.config.ActivityTimeCfg;
import com.guaji.game.config.RechargeConfig;
import com.guaji.game.config.RechargeConfig.RechargeItem;
import com.guaji.game.config.RechargeDailyRebateCfg;
import com.guaji.game.config.SysBasicCfg;
import com.guaji.game.config.TitleCfg;
import com.guaji.game.entity.DailyQuestEntity;
import com.guaji.game.entity.PlayerEntity;
import com.guaji.game.entity.RechargeDailyCollet;
import com.guaji.game.entity.RechargeEntity;
import com.guaji.game.entity.StateEntity;
import com.guaji.game.item.AwardItems;
import com.guaji.game.log.BehaviorLogger;
import com.guaji.game.log.BehaviorLogger.Action;
import com.guaji.game.log.BehaviorLogger.Params;
import com.guaji.game.log.BehaviorLogger.Source;
import com.guaji.game.manager.MailManager;
import com.guaji.game.module.PlayerActivityModule;
import com.guaji.game.module.activity.fairyBless.FairyBlessHandler;
import com.guaji.game.module.sevendaylogin.SevenDayQuestEventBus;
import com.guaji.game.player.Player;
import com.guaji.game.player.PlayerData;
import com.guaji.game.protocol.ActionLog.HPActionLog;
import com.guaji.game.protocol.Const;
import com.guaji.game.protocol.Const.SevenDayEventType;
import com.guaji.game.protocol.HP;
import com.guaji.game.protocol.Mail;
import com.guaji.game.protocol.Player.HPDataNotify;
import com.guaji.game.protocol.Recharge.HPRechargeRet;
import com.guaji.game.util.AESSignUtil;
import com.guaji.game.util.ActivityUtil;
import com.guaji.game.util.GameUtil;
import com.guaji.game.util.GsConst;
import com.guaji.game.util.GsConst.GoodsType;
import com.guaji.game.util.PlayerUtil;

import net.sf.json.JSONObject;

/**
 * 充值管理器
 *
 * @author xulinqs
 */
public class RechargeManager extends AppObj {
	static String tapdbURL = "https://e.tapdb.net/event";
	/**
	 * 充值日志
	 */
	private final Logger logger = LoggerFactory.getLogger("Recharge");

	/**
	 * 充值协议回复状态
	 */
	private static int RECHARGE_SUCCESS = 0;
	private static int RECHARGE_FAILED = -1;

	/**
	 * 全局实例对象
	 */
	private static RechargeManager instance = null;

	public static RechargeManager getInstance() {
		return instance;
	}

	/**
	 * 构造
	 *
	 * @param xid
	 */
	public RechargeManager(GuaJiXID xid) {
		super(xid);

		instance = this;
	}

	/**
	 * 查询
	 */
	private Date yesterday;

	/**
	 * 奖励时间
	 */
	private long awardMillisecond;
	private static final long ONE_DAY_MILLISECOND = 24 * 3600 * 1000;
	private int tickIndex;

	@Override
	public boolean onTick() {

		if ((++tickIndex % 50) != 0) {
			return super.onTick();
		}
		tickIndex = 0;
		// 次日凌晨返前一日充值的奖励,不能凌晨更新服务器
		if (SysBasicCfg.getInstance().getDailyChargeRebateEnable() == 1) {
			colletDailyRecharge();
			// 每日充值返利
			rewardDailyChargeRebate();
		}
		return super.onTick();
	}

	/**
	 * 汇总当日充值数据
	 */
	private void colletDailyRecharge() {
		Date today = GuaJiTime.getAM0Date();
		// test
		Calendar cal = Calendar.getInstance();
		cal.setTime(today);
		cal.add(Calendar.DATE, -1);
		//Date yestDate = cal.getTime();
		// test
		// yesterday = yestDate;
		if (yesterday == null) {
			yesterday = today;
		}
		//
		if (yesterday.compareTo(today) < 0) {
			// 每日凌晨的时候，该条件成立，其他时间段不成立，发送返利奖励
			String todayStr = GuaJiTime.getTimeString(today);
			String yesterdayStr = GuaJiTime.getTimeString(yesterday);
			String sql = "insert into recharge_dailycollet "
					+ "select orderSerial,puid,serverId,playerId,sum(goodsCost) as goodsCost,platform,createTime,updateTime,invalid "
					+ "from recharge where createTime between '" + yesterdayStr + "' and '" + todayStr
					+ "' group by playerid";
			yesterday = today;
			// 将充值数据写入汇总表中
			DBManager.getInstance().executeUpdate(sql);
		}
	}

	/**
	 * 返利
	 */
	private void rewardDailyChargeRebate() {
		// 查询出昨日到今日的充值数据
		try {
			// 即便重启游戏服务器，进入发奖励流程，每次发完奖励，都会讲奖励资格表清空，不用担心重复发的问题
			// 对比当前时间是否超过发奖励的配置时间
			long currTime = GuaJiTime.getMillisecond();
			long cfgTime = GuaJiTime.getTimeHourMinute(SysBasicCfg.getInstance().getDailyChargeRebateTime());
			if (currTime < cfgTime) {
				return;
			}
			if (awardMillisecond > currTime) {
				return;
			}
			awardMillisecond = cfgTime + ONE_DAY_MILLISECOND;
			Date today = GuaJiTime.getAM0Date();
			Calendar cal = Calendar.getInstance();
			cal.setTime(today);
			cal.add(Calendar.DATE, -1);
			//Date yestDate = cal.getTime();

			List<RechargeDailyRebateCfg> cfgs = ConfigManager.getInstance().getConfigList(RechargeDailyRebateCfg.class);
			if (cfgs == null || cfgs.isEmpty()) {
				return;
			}
//			List<RechargeDailyCollet> list = DBManager.getInstance().query("from RechargeDailyCollet where createTime between ? and ?", yestDate,today);
			List<RechargeDailyCollet> list = DBManager.getInstance().query("from RechargeDailyCollet");
			if (list == null || list.isEmpty()) {
				return;
			}
			for (RechargeDailyRebateCfg cfg : cfgs) {
				int min = cfg.getMinRechargeSum();
				int max = cfg.getMaxRechargeSum();
				double rate = cfg.getRebateRate() / 10000D;
				String mailSubject = cfg.getMailSubject();
				String awards = cfg.getAwards();

				for (RechargeDailyCollet entity : list) {
					int playerId = entity.getPlayerId();
					int chargeSum = (int) entity.getGoodsCost();
					String platform = entity.getPlatform();
					RechargeConfig rechargeConfig = RechargeConfig.getRechargeConfig(platform);
					int goldRatio = rechargeConfig.getMoneyGoldRatio();
					if (!(chargeSum > min && chargeSum < max)) {
						continue;
					}
					AwardItems awardItems = null;
					StringBuffer awardBuffer = new StringBuffer();
					if (rate > 0) {
						int returnGold = (int) (chargeSum * goldRatio * rate);
						awardBuffer.append(Const.itemType.PLAYER_ATTR_VALUE * 10000 + "_");
						awardBuffer.append(Const.playerAttr.GOLD_VALUE + "_");
						awardBuffer.append(returnGold);
					}
					if (awards != null && awards.trim().length() > 0) {
						if (awardBuffer.length() > 0) {
							awardBuffer.append(",");
							awardBuffer.append(awards);
						}
					}
					if (awardBuffer.length() > 0) {
						awardItems = AwardItems.valueOf(awardBuffer.toString());
					}
					if (awardItems == null) {
						continue;
					}
					MailManager.createSysMail(playerId, Mail.MailType.Reward_VALUE, 0, mailSubject, awardItems);
				}
				// 发送完奖励，清空掉表,防止意外发生重复发奖励
				DBManager.getInstance().executeUpdate("truncate recharge_dailycollet");
			}
		} catch (Exception e) {
			// TODO: handle exception
			MyException.catchException(e);
			return;
		}

	}

	/**
	 * 处理充值协议
	 *
	 * @param protocol
	 */
	public boolean onProtocol(Protocol protocol) {

		if (protocol.checkType(HP.code.RECHARGE_REQUEST_S)) {
			try {
				ByteBuffer buffer = protocol.getOctets().getBuffer();
				String param = new String(Base64.decode(new String(buffer.array(), 0, protocol.getSize())));
				// 增加海外支付
				if (param != null) {
					logger.info("recharge json info: " + param);
					RechargeParam rechargeParam = RechargeParam.valueOf(param);
					if (rechargeParam != null) {

						HPRechargeRet.Builder builder = handleRecharge(protocol.getSession(), rechargeParam);
						String retJson = String.format("{\"orderSerial\":\"%s\",\"status\":1}",
								rechargeParam.getOrderSerial());

						if (builder != null && builder.getStatus() == RECHARGE_SUCCESS) {
							if (protocol.getSession() != null) {
								protocol.getSession().sendProtocol(Protocol.valueOf(HP.code.RECHARGE_REPLY_S, builder));
							} else {
								logger.info("recharge unsuccessful" + rechargeParam.getOrderSerial());
							}
							logger.info("recharge successful: " + param);

						} else {
							logger.info("recharge failed: " + param);
							retJson = String.format("{\"orderSerial\":\"%s\",\"status\":-1}",
									rechargeParam.getOrderSerial());
						}

						// 回复充值回调
						if (protocol.getSession() != null) {
							protocol.getSession().sendProtocol(Protocol.valueOf(0, retJson.getBytes()));
						}
					} else {
						logger.info("recharge json parse failed: " + param);
					}
				}
			} catch (Exception e) {
				MyException.catchException(e);
				// 记录进充值日志
				logger.error(MyException.formatStackMsg(e));
			}
			return true;
		}
		return false;
	}

	private HPRechargeRet.Builder handleRecharge(GuaJiSession session, RechargeParam rechargeParam) {
		HPRechargeRet.Builder builder = HPRechargeRet.newBuilder();

		try {
			String platform = rechargeParam.getPlatform().trim().toLowerCase();
			RechargeConfig rechargeConfig = RechargeConfig.getRechargeConfig(platform);

			if (rechargeConfig == null) {
				logger.info("recharge config cannot found, platform: {}", new Object[] { platform });
				throw new RuntimeException("recharge config cannot found");
			}
			Iterator<RechargeItem> iterator = rechargeConfig.getAllRechargeItems().iterator();

			int goodsId = 0;

			while (iterator.hasNext()) {
				RechargeItem item = iterator.next();

				if (item.getName().equals(rechargeParam.getGoodsId())) {
					goodsId = item.getId();
					break;
				}
			}

			// HPRechargeRet.Builder builder = HPRechargeRet.newBuilder();
			builder.setStatus(RECHARGE_FAILED);
			builder.setPayMoney(0);
			builder.setAddGold(0);
			builder.setPuid(rechargeParam.getPuid());
			builder.setGoodsId(String.valueOf(goodsId));
			builder.setOrderSerial(rechargeParam.getOrderSerial());

			// 取商品配置项
			RechargeItem rechargeItem = rechargeConfig.get(goodsId);
			if (rechargeItem == null) {
				logger.info("recharge item cannot found, platform: {}, goodsId: {}",
						new Object[] { platform, goodsId });
				throw new RuntimeException("recharge item cannot found");
			}

			/*
			 * if (rechargeParam.getPayMoney() < rechargeItem.getCostMoney()) { logger.info(
			 * "recharge pay money less than expect, platform: {}, goodsId: {}, pay: {}, expect: {}"
			 * , new Object[] { platform, goodsId, rechargeParam.getPayMoney(),
			 * rechargeItem.getCostMoney()}); throw new RuntimeException(
			 * "recharge pay money less than expect"); }
			 */

			// 真实充值
			int playerId = ServerData.getInstance().getPlayerIdByPuid(rechargeParam.getPuid(),
					rechargeParam.getServerId());

			RechargeEntity rechargeEntity = doRecharge(playerId, rechargeParam.getOrderSerial(),
					Float.parseFloat(rechargeParam.getPayMoney()), rechargeItem.getId(), rechargeItem.getCostMoney(),
					rechargeItem.getCurrency(), rechargeItem.getAmount(), rechargeItem.getAddNum(),
					rechargeConfig.getMoneyGoldRatio(), rechargeItem.getType(), rechargeItem.getCostAmount(), true,
					platform, rechargeParam.isTest(), rechargeParam.getExpirtTime());

			/*
			 * RechargeEntity rechargeEntity = doRecharge(playerId,
			 * rechargeParam.getOrderSerial(),
			 * Float.parseFloat(rechargeParam.getPayMoney()), rechargeItem.getId(),
			 * rechargeItem.getCostMoney(), rechargeItem.getCurrency(),
			 * rechargeItem.getAmount(), rechargeItem.getAddNum(),
			 * rechargeConfig.getMoneyGoldRatio(), rechargeItem.getType(),
			 * rechargeItem.getCostAmount(), true, platform,rechargeParam.getPlatDataStr(),
			 * rechargeParam.isTest());
			 */
			if (rechargeEntity != null) {
				// 设置回包信息
				builder.setStatus(RECHARGE_SUCCESS);
				builder.setPayMoney(rechargeItem.getCostMoney());
				builder.setAddGold(rechargeEntity.getAddGold());

				// 充值数据上报
				try {
					ReportService.RechargeData rechargeData = new ReportService.RechargeData(
							rechargeEntity.getServerId(), rechargeEntity.getPuid(), rechargeEntity.getDevice(),
							rechargeEntity.getDeviceInfo(), rechargeEntity.getPlayerId(),
							rechargeEntity.getPlayerName(), rechargeEntity.getLevel(), rechargeEntity.getVipLevel(),
							rechargeEntity.getOrderSerial(), rechargeParam.getGoodsId(), rechargeEntity.getGoodsCost(),
							rechargeEntity.getCurrency(), GuaJiTime.getTimeString());
					ReportService.getInstance().report(rechargeData);
				} catch (Exception e) {
					MyException.catchException(e);
				}
			}
			return builder;
		} catch (Exception e) {
			MyException.catchException(e);
			// 记录进充值日志
			logger.error(MyException.formatStackMsg(e));
		}
		return builder;

	}

	/**
	 * 支付处理 返回json对象 json对象中包含 {"errno":1,"errmsg":"失败原因"}
	 *
	 * @param rechargeParam
	 * @return
	 */
	public JSONObject handleRecharge(RechargeParam rechargeParam) {
		JSONObject retJson = new JSONObject();
		try {
			String platform = rechargeParam.getPlatform().trim().toLowerCase();
			RechargeConfig rechargeConfig = RechargeConfig.getRechargeConfig(platform);
			if (rechargeConfig == null) {
				// 充值商品配置未找到
				logger.info("recharge config cannot found, platform: {}", new Object[] { platform });
				retJson.put("errno", 1000);
				retJson.put("errmsg", String.format("recharge config cannot found, platform: %s", platform));
				return retJson;
			}
			Iterator<RechargeItem> iterator = rechargeConfig.getAllRechargeItems().iterator();

			int goodsId = 0;
			String goodsName = rechargeParam.getGoodsId();
//			if (rechargeParam.getPlatform().equals("aws_hutuo")) {
//				// 自动订阅类型的道具需要转换下
//				if (goodsName.indexOf("jp.co.school.battle.week4") == -1
//						&& goodsName.indexOf("jp.co.school.battle.month3") == -1) {
//					if (goodsName.indexOf("week") != -1)
//						goodsName = "week";
//					else if (goodsName.indexOf("month") != -1)
//						goodsName = "month";
//				}
//			}

			while (iterator.hasNext()) {
				RechargeItem item = iterator.next();
				if (item.getName().equals(goodsName)) {
					goodsId = item.getId();
					break;
				}
			}
			// 取商品配置项
			RechargeItem rechargeItem = rechargeConfig.get(goodsId);
			if (rechargeItem == null) {
				// 商品ID对应的配置未找到
				logger.info("recharge item cannot found, platform: {}, goodsName: {}",
						new Object[] { platform, goodsName });
				retJson.put("errno", 1001);
				retJson.put("errmsg",
						String.format("recharge config cannot found, platform: %s, goodsId: %s", platform, goodsName));
				return retJson;
			}
			float payMoney = Float.parseFloat(rechargeParam.getPayMoney());
			if (payMoney < rechargeItem.getCostMoney()) {
				// 支付金额和商品定价不同
				logger.info(
						"recharge pay money less than expect, platform: {}, goodsName: {}, goodsId: {}, pay: {}, expect: {}",
						new Object[] { platform, goodsName, goodsId, rechargeParam.getPayMoney(),
								rechargeItem.getCostMoney() });
				retJson.put("errno", 1002);
				retJson.put("errmsg",
						String.format(
								"recharge pay money less than expect, platform: %s, goodsName: %s, pay: %s, expect: %s",
								platform, goodsName, rechargeParam.getPayMoney(), rechargeItem.getCostMoney()));
				return retJson;
			}
			// 真实充值
			int playerId = ServerData.getInstance().getPlayerIdByPuid(rechargeParam.getPuid(),
					rechargeParam.getServerId());
			RechargeEntity rechargeEntity = doRecharge(playerId, rechargeParam.getOrderSerial(),
					Float.parseFloat(rechargeParam.getPayMoney()), rechargeItem.getId(), rechargeItem.getCostMoney(),
					rechargeItem.getCurrency(), rechargeItem.getAmount(), rechargeItem.getAddNum(),
					rechargeConfig.getMoneyGoldRatio(), rechargeItem.getType(), rechargeItem.getCostAmount(), true,
					platform, rechargeParam.isTest(), rechargeParam.getExpirtTime());
			int rechargeStatus = rechargeEntity.getRechageStatus();
			if (rechargeStatus == 0) {
				// 充值成功
				// 充值数据上报
//				try {
//					ReportService.RechargeData rechargeData = new ReportService.RechargeData(
//							rechargeEntity.getServerId(), rechargeEntity.getPuid(), rechargeEntity.getDevice(),
//							rechargeEntity.getDeviceInfo(), rechargeEntity.getPlayerId(),
//							rechargeEntity.getPlayerName(), rechargeEntity.getLevel(), rechargeEntity.getVipLevel(),
//							rechargeEntity.getOrderSerial(), rechargeParam.getGoodsId(), rechargeEntity.getGoodsCost(),
//							rechargeEntity.getCurrency(), GuaJiTime.getTimeString());
//					ReportService.getInstance().report(rechargeData);
//				} catch (Exception e) {
//					MyException.catchException(e);
//				}

				// 充值成功
				retJson.put("errno", 0);
				retJson.put("errmsg", "rechage success");
				retJson.put("productName", rechargeItem.getProductName());
			} else {
				switch (rechargeStatus) {
				case 1010:
					retJson.put("errno", 1010);
					retJson.put("errmsg", "uid not found");
					break;
				case 1011:
					retJson.put("errno", 1011);
					retJson.put("errmsg", "player not exist");
					break;
				case 1012:
					retJson.put("errno", 1012);
					retJson.put("errmsg", "recharge player entity is null");
					break;
				case 1013:
					retJson.put("errno", 1013);
					retJson.put("errmsg", "orderno duplicate");
					break;
				case 1014:
					retJson.put("errno", 1014);
					retJson.put("errmsg", "buy month card gift failed");
					break;
				case 1015:
					retJson.put("errno", 1015);
					retJson.put("errmsg", "buy week card gift failed");
					break;
				case 1016:
					retJson.put("errno", 1018);
					retJson.put("errmsg", "buy time limit gift failed");
					break;
				case 1017:
					retJson.put("errno", 1018);
					retJson.put("errmsg", "buy chat skin failed");
					break;
				case 1018:
					retJson.put("errno", 1018);
					retJson.put("errmsg", "buy level discount gift failed");
					break;
				case 1019:
					retJson.put("errno", 1019);
					retJson.put("errmsg", "rechage exception");
					break;
				}
			}
			return retJson;
		} catch (Exception e) {
			MyException.catchException(e);
			// 记录进充值日志
			logger.error(MyException.formatStackMsg(e));
			retJson.put("errno", 1019);
			retJson.put("errmsg", "rechage exception");
		}
		return retJson;
	}

	/**
	 * 处理充值
	 *
	 * @param playerId
	 * @param rechargeItem
	 * @param i
	 */
	public RechargeEntity doRecharge(int playerId, String orderSerial, float payMoney, int goodsId, float costMoney,
			String currency, int amount, int addNum, int moneyGoldRatio, int type, int costAmount,
			boolean calcFirstRecharge, String platform, boolean isTest, long subExprie) {
		// payMoney 花的钱 goodis 内购商品编号 costMoney 渠道定价 currency 货币单位 amount 商品得到钻石
		// addNum 额外获得砖石 moneyGoldRatio 换算比例 type 商品类型 calcFirstRecharge 首冲标识
		// returnGold 充值大于商品价值的返还
		RechargeEntity rechargeEntity = new RechargeEntity();
		try {
			if (orderSerial != null) {
				orderSerial = orderSerial.trim().replace("\t", "").replace("\r", "").replace("\n", "");
			}

			if (playerId <= 0) {
				logger.info(
						"recharge playerId is zero, orderSerial: {}, goodsId: {}, payMoney: {}, costMoney: {}, amount: {}, amount: {}",
						new Object[] { orderSerial, goodsId, payMoney, costMoney, amount, addNum });
				rechargeEntity.setRechageStatus(1010);
				return rechargeEntity;
			}
			
			 //type 商品类型
			int goodsType = type;

			// 取玩家实体对象
			PlayerEntity playerEntity = null;
			Player player = PlayerUtil.queryPlayer(playerId);
			PlayerData playerData = null;
			if (player != null) {// 存在player对象在游戏内
				playerEntity = player.getPlayerData().getPlayerEntity();
				playerData = player.getPlayerData();
			} else {
				List<PlayerEntity> playerEntities = DBManager.getInstance().query("from PlayerEntity where id = ?",
						playerId);
				if (playerEntities.size() > 0) {
					playerEntity = (PlayerEntity) playerEntities.get(0);
				} else {
					logger.info(
							"recharge player is not exist, playerId: {}, orderSerial: {}, goodsId: {}, payMoney: {}, costMoney: {}, amount: {}, amount: {}",
							new Object[] { playerId, orderSerial, goodsId, payMoney, costMoney, amount, addNum });
					rechargeEntity.setRechageStatus(1011);
					return rechargeEntity;
				}
				playerData = new PlayerData(null);
				playerData.setPlayerEntity(playerEntity);
				playerData.loadActivity();
			}

			// 实体对象不存在
			if (playerEntity == null) {
				logger.info(
						"recharge player entity is not null, playerId: {}, orderSerial: {}, goodsId: {}, payMoney: {}, costMoney: {}, amount: {}, amount: {}",
						new Object[] { playerId, orderSerial, goodsId, payMoney, costMoney, amount, addNum });
				rechargeEntity.setRechageStatus(1012);
				return rechargeEntity;
			}

			// 修正
			if (payMoney < costMoney) {
				amount = (int) Math.ceil(payMoney * moneyGoldRatio);
				addNum = 0;
			}
			
			if ((ActivityUtil.isFirstRecharge(playerData, goodsId))
					|| (ActivityUtil.isDoubleRecharge(playerData, goodsId))) {
				//活動與額外送鑽石互斥
				addNum = 0;
			} 
			
			// 月卡
			boolean monthCard = false;
			// 周卡
			boolean newWeekCard = false;
			// 计算充值获得钻石
			int addGold = amount + addNum;
			// 当充值金额大于商品金额，会把多余的货币所值的砖石按照比例计入充值金额
			int returnGold = (int) Math.max(0, (payMoney - costMoney) * moneyGoldRatio);

			// 创建充值实体对象
			int oldVipLevel = playerEntity.getVipLevel();
			boolean isFirstRecharge = (playerEntity.getRecharge() == 0 ? true : false) && calcFirstRecharge;
			rechargeEntity.setOrderSerial(orderSerial);
			rechargeEntity.setPuid(playerEntity.getPuid());
			rechargeEntity.setPlayerId(playerEntity.getId());
			rechargeEntity.setServerId(playerEntity.getServerId());
			rechargeEntity.setPlayerName(playerEntity.getName());
			rechargeEntity.setGoodsId(goodsId);
			rechargeEntity.setGoodsCount(1);
			rechargeEntity.setGoodsCost(payMoney);
			rechargeEntity.setAddGold(addGold + returnGold);
			rechargeEntity.setIsFirstPay(isFirstRecharge ? 1 : 0);
			rechargeEntity.setLevel(playerEntity.getLevel());
			rechargeEntity.setVipLevel(playerEntity.getVipLevel());
			rechargeEntity.setDevice(playerEntity.getDevice());
			rechargeEntity.setPlatform(playerEntity.getPlatform());
			rechargeEntity.setCurrency(currency);
			rechargeEntity.setTest(isTest);
			rechargeEntity.setDeviceInfo(playerEntity.getPhoneInfo());

			// 增加鲜花,如果玩家不在线,离线补单是不会增加鲜花的add by Melvin.Mao
			if (null != player) {
				FairyBlessHandler.onRechargeSuccessAddFlower(player, costAmount + amount);
			}
			// 创建充值记录对象(利用主键冲突机制创建不成功进行订单唯一性处理)
			if (orderSerial != null && orderSerial.length() > 0) {
				if (!DBManager.getInstance().create(rechargeEntity)) {
					logger.info(
							"recharge orderSerial has pay gold, playerId: {}, orderSerial: {}, goodsId: {}, payMoney: {}, costMoney: {}, amount: {}, addNum: {}, returnGold: {}",
							new Object[] { playerId, orderSerial, goodsId, payMoney, costMoney, amount, addNum,
									returnGold });

					rechargeEntity.setRechageStatus(1013);
					return rechargeEntity;
				}
			}

			// 商品id 是0 是efun 的非月卡充值 里面要分 web充值与游戏内充值 201web 充值, 202 游戏内充值
//			if (goodsId == 201 || goodsId == 202) {
//				if (goodsId == 201) {
//					// web 首充
//					if (playerEntity.getWebRecharge() == 0 && calcFirstRecharge) {
//						// 首次充值翻倍后的
//						addGold = amount * 2 + addNum;
//						playerEntity.setWebRecharge(1);
//						playerEntity.notifyUpdate(false);
//					}
//				} else {
//					// 游戏内
//					if (playerEntity.getGameRecharge() == 0 && calcFirstRecharge) {
//						// 首次充值翻倍后的
//						addGold = amount * 2 + addNum;
//						playerEntity.setGameRecharge(1);
//						for (int i = 1; i <= 6; i++) {
//							ActivityUtil.triggerFirstRecharge(playerData, i, 1);
//						}
//						playerEntity.notifyUpdate(false);
//					}
//
//				}
//			}

			if (addGold > 0) {
				int ratioAmount = ActivityUtil.triggerFirstRecharge(playerData, goodsId, amount);

				if (ratioAmount > 0) {
					// 判断首冲翻倍
					addGold += ratioAmount;
				} else {
					// 是否充值双倍
					ratioAmount = ActivityUtil.triggerDoubleRecharge(playerData, goodsId, amount);
					addGold += ratioAmount;
				}

				// 和首充双倍的活动互斥
				if (ratioAmount == 0) {
					int returnAmount = ActivityUtil.calcActivity124Return(playerData, amount);
					addGold += returnAmount;
					// 获取充值返利抽奖活动彩票使用状态
					if (returnAmount > 0) {
						ActivityUtil.notifyActivity124Info(player);
					}
					// 老虎机充值活动
					returnAmount = ActivityUtil.calcActivity137Return(playerData, amount);
					if (returnAmount > 0) {
						addGold += returnAmount;
						ActivityUtil.notifyActivity137Info(player, true);
					}
					// 俄罗斯轮盘活动
					returnAmount = ActivityUtil.calcActivity140Return(playerData, amount);
					if (returnAmount > 0) {
						addGold += returnAmount;
						ActivityUtil.notifyActivity140Info(player, true);
					}
				}
			}
			// 1.月卡   活動編號83
			if (goodsType == GoodsType.MonthCord)// 月卡
			{
				if (!ActivityUtil.getMonthCardStatus(playerData).activateMonthCard(goodsId, player, subExprie)) {
					rechargeEntity.setRechageStatus(1014);
					return rechargeEntity;
				} else {
					monthCard = true;
					playerEntity.setRecharge(playerEntity.getRecharge() + costAmount);
					playerEntity.setRechargeSoul(playerEntity.getRechargeSoul() + costAmount);
					playerData.updateActivity(Const.ActivityId.MONTH_CARD_VALUE, 0, true);
					MailManager.createSysMail(playerEntity.getId(), Mail.MailType.Normal_VALUE,
							GsConst.MailId.PLAYER_RECHARGE_MONTH_CARD, "", null, GuaJiTime.getTimeString());

					BehaviorLogger.log4Platform(playerEntity, Action.CONSUME_MONTH_RECHARGE,
							Params.valueOf("goodsId", goodsId), Params.valueOf("payMoney", payMoney),
							Params.valueOf("costMoney", costMoney));
				}
			}
			// 11.超值月卡 小月卡 goodsId:32
			if (goodsType == GoodsType.SmallMonthCord)//(超值月卡 小月卡)
			{
				// 消耗型月卡
				if (!ActivityUtil.getConMonthCardStatus(playerData).activateMonthCard(goodsId, player)) {
					rechargeEntity.setRechageStatus(1014);
					return rechargeEntity;
				} else {
					monthCard = true;
					playerEntity.setRecharge(playerEntity.getRecharge() + costAmount);
					playerEntity.setRechargeSoul(playerEntity.getRechargeSoul() + costAmount);
					playerData.updateActivity(Const.ActivityId.CONSUME_MONTH_CARD_VALUE, 0, true);

					GuaJiXID targetXID = GuaJiXID.valueOf(GsConst.ObjType.PLAYER, playerData.getId());
					Msg noticeWeekcardMsg = Msg.valueOf(GsConst.MsgType.CONSUME_MONTH_CARD_SUC, targetXID);
					GsApp.getInstance().postMsg(noticeWeekcardMsg);

					MailManager.createSysMail(playerEntity.getId(), Mail.MailType.Normal_VALUE,
							GsConst.MailId.PLAYER_RECHARGE_CONSUME_MONTH_CARD, "", null, GuaJiTime.getTimeString());

					BehaviorLogger.log4Platform(playerEntity, Action.CONSUME_MONTH_RECHARGE,
							Params.valueOf("goodsId", goodsId), Params.valueOf("payMoney", payMoney),
							Params.valueOf("costMoney", costMoney));
				}

			}
			// 6.周卡 活動編號96
			if (goodsType == GoodsType.NewWeekCard)// 周卡
			{
				if (!ActivityUtil.getNewWeekCardStatus(playerData).activateNewWeekCard(goodsId, player)) {
					rechargeEntity.setRechageStatus(1015);
					return rechargeEntity;
				} else {
					newWeekCard = true;
					playerEntity.setRecharge(playerEntity.getRecharge() + costAmount);
					playerEntity.setRechargeSoul(playerEntity.getRechargeSoul() + costAmount);
					playerData.updateActivity(Const.ActivityId.NEW_WEEK_CARD_VALUE, 0, true);
					MailManager.createSysMail(playerEntity.getId(), Mail.MailType.Normal_VALUE,
							GsConst.MailId.PLAYER_RECHARGE_NEW_WEEK_CARD, "", null, GuaJiTime.getTimeString(),
							String.valueOf(payMoney));

					BehaviorLogger.log4Platform(playerEntity, Action.NEW_WEEK_CARD_AWARD,
							Params.valueOf("goodsId", goodsId), Params.valueOf("payMoney", payMoney),
							Params.valueOf("costMoney", costMoney));
				}

			}
			// 3.打折禮包 活動編號82
			if (goodsType == GoodsType.SalePacket)// 打折礼包,（心态很重要，写过最恶心的代码，没有之一，非礼勿视）
			{
				ActivityUtil.tiggerSalePacket(playerData, goodsId);
				playerEntity.setRecharge(playerEntity.getRecharge() + costAmount);
				playerEntity.setRechargeSoul(playerEntity.getRechargeSoul() + costAmount);
				playerData.updateActivity(Const.ActivityId.SALE_PACKET_VALUE, 0, true);

				MailManager.createSysMail(playerEntity.getId(), Mail.MailType.Normal_VALUE,
						GsConst.MailId.PLAYER_RECHARGE_GIFT, "", null, GuaJiTime.getTimeString(),
						String.format("@PLAYER_RECHARGE_GIFT_%d", goodsId));

				BehaviorLogger.log4Platform(playerEntity, Action.SALEGIFT_RECHARGE, Params.valueOf("goodsId", goodsId),
						Params.valueOf("payMoney", payMoney), Params.valueOf("costMoney", costMoney));
			}
			// 4.限時限購  活動編號26
			if (goodsType == GoodsType.LimitRecharge) {
				if (!ActivityUtil.tiggerLimitRecharge(playerData, goodsId)) {
					rechargeEntity.setRechageStatus(1016);
					return rechargeEntity;
				}
				playerEntity.setRecharge(playerEntity.getRecharge() + costAmount);
				playerEntity.setRechargeSoul(playerEntity.getRechargeSoul() + costAmount);
				playerData.updateActivity(Const.ActivityId.LIMIT_RECHARGE_VALUE, 0, true);
				BehaviorLogger.log4Platform(playerEntity, Action.LIMIT_RECHARGE, Params.valueOf("goodsId", goodsId),
						Params.valueOf("payMoney", payMoney), Params.valueOf("costMoney", costMoney));
			}
			//  8.聊天皮膚 活動編號100
			if (goodsType == GoodsType.ChatSkin) {
				if (!ActivityUtil.triggerBuyChatSkin(playerData, goodsId, costMoney)) {
					rechargeEntity.setRechageStatus(1017);
					return rechargeEntity;
				}
				playerEntity.setRecharge(playerEntity.getRecharge() + costAmount);
				playerEntity.setRechargeSoul(playerEntity.getRechargeSoul() + costAmount);
				BehaviorLogger.log4Platform(playerEntity, Action.BUY_CHAT_SKIN, Params.valueOf("goodsId", goodsId),
						Params.valueOf("payMoney", payMoney), Params.valueOf("costMoney", costMoney));
			}

			// 重新设置真实增加钻石数
			rechargeEntity.setAddGold(addGold + returnGold);
			rechargeEntity.notifyUpdate(false);

			//0.鑽石郵件處裡
			if (goodsType == GoodsType.Mail) {
				MailManager.createSysMail(playerEntity.getId(), Mail.MailType.Normal_VALUE,
						GsConst.MailId.PLAYER_RECHARGE_GOLD, "", null, GuaJiTime.getTimeString(),
						String.valueOf(addGold + returnGold));
			}

			// 5.折扣禮包  活動編號94
			
			if (goodsType == GoodsType.DiscountGift) {
				if (!ActivityUtil.tiggerDiscountGift(playerData, goodsId)) {
					rechargeEntity.setRechageStatus(1018);
					return rechargeEntity;
				}
				playerEntity.setRecharge(playerEntity.getRecharge() + costAmount);
				playerEntity.setRechargeSoul(playerEntity.getRechargeSoul() + costAmount);
				ActivityTimeCfg timeCfg = ActivityUtil.getCurActivityTimeCfg(Const.ActivityId.DISCOUNT_GIFT_VALUE);
				if (timeCfg != null) {
					playerData.updateActivity(Const.ActivityId.DISCOUNT_GIFT_VALUE, timeCfg.getStageId(), true);
				} else {
					logger.error("充值折扣礼包活动(ActivityId=94)有误");
				}
				MailManager.createSysMail(playerEntity.getId(), Mail.MailType.Normal_VALUE,
						GsConst.MailId.PLAYER_RECHARGE_GIFT, "", null, GuaJiTime.getTimeString(),
						String.format("@PLAYER_RECHARGE_GIFT_%d", goodsId));

				BehaviorLogger.log4Platform(playerEntity, Action.DISCOUNT_GIFT_BUY, Params.valueOf("goodsId", goodsId),
						Params.valueOf("payMoney", payMoney), Params.valueOf("costMoney", costMoney));
			}
			// 12.等級成長通行證 活動編號162 , 13.關卡成長通行證 活動編號163 ,14.爬塔成長通行證 活動編號164,22.關卡通行證2 活動編號163
			if ((goodsType == GoodsType.LVPass)||(goodsType == GoodsType.MapPass)||(goodsType == GoodsType.TowerPass)||(goodsType == GoodsType.MapPass2)) { // 成長通行證
				if (!ActivityUtil.tiggerGrowthPass(playerData, goodsType,goodsId)) {
					rechargeEntity.setRechageStatus(1018);
					return rechargeEntity;
				}
				playerEntity.setRecharge(playerEntity.getRecharge() + costAmount);
				playerEntity.setRechargeSoul(playerEntity.getRechargeSoul() + costAmount);
				
				MailManager.createSysMail(playerEntity.getId(), Mail.MailType.Normal_VALUE,
						GsConst.MailId.PLAYER_RECHARGE_GROWTH_PASS, "", null, GuaJiTime.getTimeString(),
						String.format("@PLAYER_RECHARGE_GIFT_%d", goodsId));

				BehaviorLogger.log4Platform(playerEntity, Action.GROWTH_PASS_BUY, Params.valueOf("goodsId", goodsId),
						Params.valueOf("payMoney", payMoney), Params.valueOf("costMoney", costMoney));
			}
			// 15.每月付費獎勵(高級) 活動編號161 ,16.每月付費獎勵(奢華) 活動編號161
			if ((goodsType == GoodsType.SupportCalender1) || (goodsType == GoodsType.SupportCalender2)) {
				int costType = (goodsType == GoodsType.SupportCalender1)? 1 : 2;
				if (!ActivityUtil.tiggerSupportCalender(playerData,costType,goodsId)) {
					rechargeEntity.setRechageStatus(1018);
					return rechargeEntity;
				}
				playerEntity.setRecharge(playerEntity.getRecharge() + costAmount);
				playerEntity.setRechargeSoul(playerEntity.getRechargeSoul() + costAmount);
				
				MailManager.createSysMail(playerEntity.getId(), Mail.MailType.Normal_VALUE,
						GsConst.MailId.PLAYER_SUPPORT_CALENDER, "", null, GuaJiTime.getTimeString(),
						String.format("@PLAYER_RECHARGE_GIFT_%d",goodsId,costType));
			}
			
			// 17.特權購買 活動編號168
			if (goodsType == GoodsType.SubScription) {
				if (!ActivityUtil.tiggerSubScription(playerData,goodsId)) {
					rechargeEntity.setRechageStatus(1018);
					return rechargeEntity;
				}
				playerEntity.setRecharge(playerEntity.getRecharge() + costAmount);
				playerEntity.setRechargeSoul(playerEntity.getRechargeSoul() + costAmount);
				
				MailManager.createSysMail(playerEntity.getId(), Mail.MailType.Normal_VALUE,
						GsConst.MailId.PLAYER_SUBSCRIPTION, "", null, GuaJiTime.getTimeString(),
						String.format("@PLAYER_RECHARGE_GIFT_%d",goodsId));
			}
			
			// 18.等級彈跳禮包 活動編號132
			if (goodsType == GoodsType.LevelGift) {
				if (!ActivityUtil.tiggerLevelGift(playerData, goodsId)) {
					rechargeEntity.setRechageStatus(1018);
					return rechargeEntity;
				}
				playerEntity.setRecharge(playerEntity.getRecharge() + costAmount);
				playerEntity.setRechargeSoul(playerEntity.getRechargeSoul() + costAmount);
				
				MailManager.createSysMail(playerEntity.getId(), Mail.MailType.Normal_VALUE,
						GsConst.MailId.PLAYER_LEVEL_GIFT, "", null, GuaJiTime.getTimeString(),
						String.format("@PLAYER_RECHARGE_GIFT_%d",goodsId));
			}
			
			// 19.關卡彈跳禮包 活動編號151
			if (goodsType == GoodsType.StageGift) {
				if (!ActivityUtil.tiggerStageGift(playerData, goodsId)) {
					rechargeEntity.setRechageStatus(1018);
					return rechargeEntity;
				}
				playerEntity.setRecharge(playerEntity.getRecharge() + costAmount);
				playerEntity.setRechargeSoul(playerEntity.getRechargeSoul() + costAmount);
				
				MailManager.createSysMail(playerEntity.getId(), Mail.MailType.Normal_VALUE,
						GsConst.MailId.PLAYER_STAGE_GIFT, "", null, GuaJiTime.getTimeString(),
						String.format("@PLAYER_RECHARGE_GIFT_%d",goodsId));
			}
			
			// 20.活動彈跳禮包 活動編號169
			if (goodsType == GoodsType.ActivityGift) {
				if (!ActivityUtil.tiggerActivityGift(playerData, goodsId)) {
					rechargeEntity.setRechageStatus(1018);
					return rechargeEntity;
				}
				playerEntity.setRecharge(playerEntity.getRecharge() + costAmount);
				playerEntity.setRechargeSoul(playerEntity.getRechargeSoul() + costAmount);
				
				MailManager.createSysMail(playerEntity.getId(), Mail.MailType.Normal_VALUE,
						GsConst.MailId.PLAYER_STAGE_GIFT, "", null, GuaJiTime.getTimeString(),
						String.format("@PLAYER_RECHARGE_GIFT_%d",goodsId));
			}
			
			//21.活動彈跳禮包 活動編號170
			if (goodsType == GoodsType.JumpGift) {
				if (!ActivityUtil.tiggerJumpGift(playerData, goodsId)) {
					rechargeEntity.setRechageStatus(1018);
					return rechargeEntity;
				}
				playerEntity.setRecharge(playerEntity.getRecharge() + costAmount);
				playerEntity.setRechargeSoul(playerEntity.getRechargeSoul() + costAmount);
				
				MailManager.createSysMail(playerEntity.getId(), Mail.MailType.Normal_VALUE,
						GsConst.MailId.PLAYER_STAGE_GIFT, "", null, GuaJiTime.getTimeString(),
						String.format("@PLAYER_RECHARGE_GIFT_%d",goodsId));
			}
									
			// 23.階段禮包 失敗禮包 活動編號179
			if (goodsType == GoodsType.StepGift) {
				if (!ActivityUtil.tiggerStepGift(playerData, goodsId)) {
					rechargeEntity.setRechageStatus(1018);
					return rechargeEntity;
				}
				playerEntity.setRecharge(playerEntity.getRecharge() + costAmount);
				playerEntity.setRechargeSoul(playerEntity.getRechargeSoul() + costAmount);
				
				MailManager.createSysMail(playerEntity.getId(), Mail.MailType.Normal_VALUE,
						GsConst.MailId.PLAYER_STEP_GIFT, "", null, GuaJiTime.getTimeString(),
						String.format("@PLAYER_RECHARGE_GIFT_%d",goodsId));
			}
			
			// 24.失敗禮包 活動編號177
			if (goodsType == GoodsType.FailedGif) {
				if (!ActivityUtil.tiggerFailedGift(playerData, goodsId)) {
					rechargeEntity.setRechageStatus(1018);
					return rechargeEntity;
				}
				playerEntity.setRecharge(playerEntity.getRecharge() + costAmount);
				playerEntity.setRechargeSoul(playerEntity.getRechargeSoul() + costAmount);
				
				MailManager.createSysMail(playerEntity.getId(), Mail.MailType.Normal_VALUE,
						GsConst.MailId.PLAYER_FAILED_GIFT, "", null, GuaJiTime.getTimeString(),
						String.format("@PLAYER_RECHARGE_GIFT_%d",goodsId));
			}
			
			
			// 25.活動彈跳禮包 活動編號181
			if (goodsType == GoodsType.JumpGift181) {
				if (!ActivityUtil.tiggerJumpGift181(playerData, goodsId)) {
					rechargeEntity.setRechageStatus(1018);
					return rechargeEntity;
				}
				playerEntity.setRecharge(playerEntity.getRecharge() + costAmount);
				playerEntity.setRechargeSoul(playerEntity.getRechargeSoul() + costAmount);
				
				MailManager.createSysMail(playerEntity.getId(), Mail.MailType.Normal_VALUE,
						GsConst.MailId.PLAYER_STAGE_GIFT, "", null, GuaJiTime.getTimeString(),
						String.format("@PLAYER_RECHARGE_GIFT_%d",goodsId));
			}
			
			// 26.活動彈跳禮包 活動編號182
			if (goodsType == GoodsType.JumpGift182) {
				if (!ActivityUtil.tiggerJumpGift182(playerData, goodsId)) {
					rechargeEntity.setRechageStatus(1018);
					return rechargeEntity;
				}
				playerEntity.setRecharge(playerEntity.getRecharge() + costAmount);
				playerEntity.setRechargeSoul(playerEntity.getRechargeSoul() + costAmount);
				
				MailManager.createSysMail(playerEntity.getId(), Mail.MailType.Normal_VALUE,
						GsConst.MailId.PLAYER_STAGE_GIFT, "", null, GuaJiTime.getTimeString(),
						String.format("@PLAYER_RECHARGE_GIFT_%d",goodsId));
			}
			
			// 27.活動彈跳禮包 活動編號183
			if (goodsType == GoodsType.JumpGift183) {
				if (!ActivityUtil.tiggerJumpGift183(playerData, goodsId)) {
					rechargeEntity.setRechageStatus(1018);
					return rechargeEntity;
				}
				playerEntity.setRecharge(playerEntity.getRecharge() + costAmount);
				playerEntity.setRechargeSoul(playerEntity.getRechargeSoul() + costAmount);
				
				MailManager.createSysMail(playerEntity.getId(), Mail.MailType.Normal_VALUE,
						GsConst.MailId.PLAYER_STAGE_GIFT, "", null, GuaJiTime.getTimeString(),
						String.format("@PLAYER_RECHARGE_GIFT_%d",goodsId));
			}
			
			// 28.活動彈跳禮包 活動編號184
			if (goodsType == GoodsType.JumpGift184) {
				if (!ActivityUtil.tiggerJumpGift184(playerData, goodsId)) {
					rechargeEntity.setRechageStatus(1018);
					return rechargeEntity;
				}
				playerEntity.setRecharge(playerEntity.getRecharge() + costAmount);
				playerEntity.setRechargeSoul(playerEntity.getRechargeSoul() + costAmount);
				
				MailManager.createSysMail(playerEntity.getId(), Mail.MailType.Normal_VALUE,
						GsConst.MailId.PLAYER_STAGE_GIFT, "", null, GuaJiTime.getTimeString(),
						String.format("@PLAYER_RECHARGE_GIFT_%d",goodsId));
			}
			
			// 29.活動彈跳禮包 活動編號185
			if (goodsType == GoodsType.JumpGift185) {
				if (!ActivityUtil.tiggerJumpGift185(playerData, goodsId)) {
					rechargeEntity.setRechageStatus(1018);
					return rechargeEntity;
				}
				playerEntity.setRecharge(playerEntity.getRecharge() + costAmount);
				playerEntity.setRechargeSoul(playerEntity.getRechargeSoul() + costAmount);
				
				MailManager.createSysMail(playerEntity.getId(), Mail.MailType.Normal_VALUE,
						GsConst.MailId.PLAYER_STAGE_GIFT, "", null, GuaJiTime.getTimeString(),
						String.format("@PLAYER_RECHARGE_GIFT_%d",goodsId));
			}
			
			// 30.活動彈跳禮包 活動編號186
			if (goodsType == GoodsType.JumpGift186) {
				if (!ActivityUtil.tiggerJumpGift186(playerData, goodsId)) {
					rechargeEntity.setRechageStatus(1018);
					return rechargeEntity;
				}
				playerEntity.setRecharge(playerEntity.getRecharge() + costAmount);
				playerEntity.setRechargeSoul(playerEntity.getRechargeSoul() + costAmount);
				
				MailManager.createSysMail(playerEntity.getId(), Mail.MailType.Normal_VALUE,
						GsConst.MailId.PLAYER_STAGE_GIFT, "", null, GuaJiTime.getTimeString(),
						String.format("@PLAYER_RECHARGE_GIFT_%d",goodsId));
			}
			
			// 31.加強彈跳禮包 活動編號187
			if (goodsType == GoodsType.MaxJump187) {
				if (!ActivityUtil.tiggerMaxJump(playerData, goodsId)) {
					rechargeEntity.setRechageStatus(1018);
					return rechargeEntity;
				}
				playerEntity.setRecharge(playerEntity.getRecharge() + costAmount);
				playerEntity.setRechargeSoul(playerEntity.getRechargeSoul() + costAmount);
				
				MailManager.createSysMail(playerEntity.getId(), Mail.MailType.Normal_VALUE,
						GsConst.MailId.PLAYER_STAGE_GIFT, "", null, GuaJiTime.getTimeString(),
						String.format("@PLAYER_RECHARGE_GIFT_%d",goodsId));
			}
			
			//32.標記功能禮包
			if (goodsType == GoodsType.SignGift) {
				if (!ActivityUtil.tiggerSignGoods(playerData,goodsType,goodsId)) {
					rechargeEntity.setRechageStatus(1018);
					return rechargeEntity;
				}
				playerEntity.setRecharge(playerEntity.getRecharge() + costAmount);
				playerEntity.setRechargeSoul(playerEntity.getRechargeSoul() + costAmount);
				
				MailManager.createSysMail(playerEntity.getId(), Mail.MailType.Normal_VALUE,
						GsConst.MailId.PLAYER_STAGE_GIFT, "", null, GuaJiTime.getTimeString(),
						String.format("@PLAYER_RECHARGE_GIFT_%d",goodsId));
			}
			
			Date curDate = GuaJiTime.getCalendar().getTime();
			
			// 在线玩家
			boolean onlineRecharge = false;
			ObjBase<GuaJiXID, AppObj> objBase = GsApp.getInstance()
					.lockObject(GuaJiXID.valueOf(GsConst.ObjType.PLAYER, playerId));
			if (objBase != null) {
				try {
					// 获取玩家对象
					player = (Player) objBase.getImpl();
					if (player != null) {
						// 处理加钻石
						if (addGold + returnGold > 0) {
							// 加钻石, 改充值钻石额度, 更新vip等级
							player.increaseRmbGold(addGold + returnGold, Action.RECHARGE,goodsId);
						}
						player.increaseRechargeGold(goodsId,amount + returnGold,addGold + returnGold, payMoney, Action.RECHARGE);
						
//						if (goodsType == GoodsType.Mail) {
//							ActivityUtil.addRechargeBounce192(GsConst.RechargeBounceType.Deposit,(int)payMoney,player.getPlayerData());
//						}

						int todayRechargetNum = player.getPlayerData().getPlayerEntity().getTodayRechargeNum();
						if (monthCard || newWeekCard) {
							player.getPlayerData().getPlayerEntity()
									.setTodayRechargeNum(todayRechargetNum + amount + returnGold);
						} else {
							player.getPlayerData().getPlayerEntity()
									.setTodayRechargeNum(todayRechargetNum + addGold + returnGold);

						}
						ActivityUtil.addPalyerRechargeMoney(player.getPlayerData(), payMoney);

						SevenDayQuestEventBus.fireQuestEvent(SevenDayEventType.RECHARGE, amount,
								PlayerUtil.queryPlayer(playerId).getXid());
						
						StateEntity stateEntity = player.getPlayerData().getStateEntity();
						
						if ((stateEntity.getLuckyTime() == null) || 
							(curDate.getTime() - stateEntity.getLuckyTime().getTime() >= (SysBasicCfg.getInstance().getRechargeLuckyCD()*1000))) {
							if (stateEntity.getRechargeluckey() == 0) {
								stateEntity.setRechargeluckey(
										SysBasicCfg.getInstance().getReChargeLucky(GsConst.RechargeLuckyType.BASE));
								stateEntity.notifyUpdate(true);
							}
						}
												
						onlineRecharge = true;

						// 同步信息
						player.getPlayerData().syncPlayerInfo();

						// 充值通知
						HPDataNotify.Builder notifyBuilder = HPDataNotify.newBuilder();
						notifyBuilder.setType(Const.notifyType.NOTIFY_RECHARGE_VALUE);
						notifyBuilder.setValue(amount + returnGold);
						notifyBuilder.setGoodsId(goodsId);
						player.sendProtocol(Protocol.valueOf(HP.code.DATA_NOTIFY_S_VALUE, notifyBuilder));

						// 内部消息充值砖石
						Msg msg = Msg.valueOf(GsConst.MsgType.DailyQuestMsg.ON_RECHARGE,
								GuaJiXID.valueOf(GsConst.ObjType.PLAYER, player.getId()));
						msg.pushParam(amount + returnGold + costAmount);
						GsApp.getInstance().postMsg(msg);

						// // 通知任务
						// try {
						// if (calcFirstRecharge &&
						// MissionUtil.recharge(player.getPlayerData().getMissionEntity(),
						// amount + returnGold)) {
						// MissionUtil.sendNotice(player.getSession(),
						// NoticeType.GIFT_NEW_MSG);
						// }
						// } catch (Exception e) {
						// MyException.catchException(e);
						// }
						if (calcFirstRecharge) {
							try {
								// 增加每日累计充值额度
								// ActivityUtil.addPlayerAccRecharge(amount +
								// returnGold, player.getPlayerData());
								// 增加连续签到天数
								ActivityUtil.addPlayerRechargeDays(playerData);
								// 增加每日单笔充值
								if (goodsType == GoodsType.Mail) {
									ActivityUtil.addPlayerSingleRecharge(player.getPlayerData(), amount + returnGold);
								}
								// 增加充值返利额度
								ActivityUtil.addRechargeRebateAmount(player.getPlayerData(), amount + returnGold);
								// 触发周卡
								ActivityUtil.triggerWeekCard(player.getPlayerData(), goodsId, costMoney, subExprie);
								// (activityId:129 進階月卡:大月卡 goodsId 74)
								if (goodsType == GoodsType.BigMonthCord) {
									ActivityUtil.triggerConsumeWeekCard(playerData, goodsId, costMoney);
								}
								// 每日累积充值Money
								ActivityUtil.addPlayerAccRecharge(payMoney, player.getPlayerData());

								// 触发疯狂转轮
//								if (type != 0) {
//									
//									ActivityUtil.triggerCrazyRoulette(player.getPlayerData(), costAmount);
//								} else {
//
//									ActivityUtil.triggerCrazyRoulette(player.getPlayerData(), amount + returnGold);
//								}
								// 财富俱乐部
								ActivityUtil.triggerGoldClub(player.getPlayerData(), amount + returnGold);
								// 抢红包
								ActivityUtil.triggerRedEnvelope(player.getPlayerData(), amount + returnGold);
								// 终身卡累计今日充值
								ActivityUtil.addPlayerTodayRecharge(player.getPlayerData(), amount + returnGold);
							} catch (Exception e) {
								MyException.catchException(e);
							}

							try {
								// 检测称号是否达成
								List<Integer> titleIds = TitleCfg.getFinishTitleIdsByType(
										GsConst.MsgType.VIP_LEVEL_CHANGE, playerEntity.getVipLevel());
								for (int titleId : titleIds) {
									if (!player.getPlayerData().getTitleEntity().contains(titleId)) {
										msg = Msg.valueOf(GsConst.MsgType.VIP_LEVEL_CHANGE,
												GuaJiXID.valueOf(GsConst.ObjType.PLAYER, playerId));
										msg.pushParam(titleId);
										GsApp.getInstance().postMsg(msg);
									}
								}
							} catch (Exception e) {
								MyException.catchException(e);
							}
						}
					}
				} finally {
					objBase.unlockObj();
				}
			}

			HPActionLog.Builder actionLogBuilder = HPActionLog.newBuilder();
			actionLogBuilder.setActionType(1);
			if (platform.contains("sanguo_amazon")) {
				actionLogBuilder.setAndroidKey(GsConst.AndroidActionType.AMAZON_AREVENUE);
				actionLogBuilder.setIosKey("");
			} else if (platform.contains("android_hutuo")) {
				actionLogBuilder.setAndroidKey(GsConst.AndroidActionType.GOOGLE_REVENUE);
				actionLogBuilder.setIosKey("");
			} else if (platform.contains("ios_hutuo")) {
				actionLogBuilder.setAndroidKey("");
				actionLogBuilder.setIosKey(GsConst.IOSActionType.REVENUE);
			} else {
				actionLogBuilder.setAndroidKey("");
				actionLogBuilder.setIosKey("");
			}
			actionLogBuilder.setCount((int) costMoney);
			if (player != null) {
				player.sendProtocol(Protocol.valueOf(HP.code.ACTION_LOG_S_VALUE, actionLogBuilder));
			}

			if (!onlineRecharge) {
				// 加钻石, 改充值钻石额度, 更新vip等级
				playerEntity.setRmbGold(playerEntity.getRmbGold() + addGold + returnGold);// 总的要加的金币预设金币+赠送金币数量+返还数量
				playerEntity.setRecharge(playerEntity.getRecharge() + amount + returnGold);// 预设金币数量+返还数量
				playerEntity.setRechargeSoul(playerEntity.getRechargeSoul() + amount + returnGold);
				playerEntity.setPayMoney(playerEntity.getPayMoney() + payMoney);// 真是付费钱币
				//累加VIP
				StateEntity stateEntity = playerData.getStateEntity();
				stateEntity.setVipPoint(stateEntity.getVipPoint() + amount);
				
				if ((stateEntity.getLuckyTime() == null) || 
						(stateEntity.getLuckyTime().getTime() - curDate.getTime() >= (SysBasicCfg.getInstance().getRechargeLuckyCD()*1000))) {
						if (stateEntity.getRechargeluckey() == 0) {
							stateEntity.setRechargeluckey(
									SysBasicCfg.getInstance().getReChargeLucky(GsConst.RechargeLuckyType.BASE));
						}
					}
				stateEntity.notifyUpdate(true);
				
				// 增加今日充值数量
				if (monthCard || newWeekCard) {
					playerEntity.setTodayRechargeNum(playerEntity.getTodayRechargeNum() + amount + returnGold);
				} else {
					playerEntity.setTodayRechargeNum(playerEntity.getTodayRechargeNum() + addGold + returnGold);
				}

				SevenDayQuestEventBus.fireQuestEventWhenPlayerOffline(playerId, SevenDayEventType.RECHARGE,
						playerEntity.getTodayRechargeNum());
				
				playerEntity.setVipLevel(GameUtil.getVipLevelByRecharge(stateEntity.getVipPoint(),platform));
				playerEntity.notifyUpdate(false);
				// 成长基金活动红点
				if (oldVipLevel < 2 && playerEntity.getVipLevel() >= 2) {
					PlayerActivityModule.pushGrowthFundPoint(player);
				}
				if (calcFirstRecharge) {
					// // 通知任务
					// try {
					// MissionEntity missionEntity =
					// DBManager.getInstance().fetch(MissionEntity.class, "from
					// MissionEntity where playerId = ? and invalid = 0",
					// playerId);
					// if (missionEntity != null) {
					// missionEntity.loadMission();
					// MissionUtil.recharge(missionEntity, amount + returnGold);
					// }
					// } catch (Exception e) {
					// MyException.catchException(e);
					// }

					try {
						// 增加每日累计充值额度
						// ActivityUtil.addPlayerAccRecharge(amount +
						// returnGold,
						// playerData);
						// 增加连续签到天数
						ActivityUtil.addPlayerRechargeDays(playerData);
						// 增加每日单笔充值
						if (goodsId >= 1 && goodsId <= 6) {
							ActivityUtil.addPlayerSingleRecharge(playerData, amount + returnGold);
						}
						// 增加充值返利额度
						ActivityUtil.addRechargeRebateAmount(playerData, amount + returnGold);
						// 触发周卡
						ActivityUtil.triggerWeekCard(playerData, goodsId, costMoney, subExprie);
						// 消耗型周卡
						ActivityUtil.triggerConsumeWeekCard(playerData, goodsId, costMoney);

						// 每日累积充值Money
						ActivityUtil.addPlayerAccRecharge(payMoney, playerData);

						// 触发疯狂转轮
//						if (type != 0) {
//							
//							ActivityUtil.triggerCrazyRoulette(playerData, costAmount);
//						} else {
//							
//							ActivityUtil.triggerCrazyRoulette(playerData, amount + returnGold);
//
//						}
						// 财富俱乐部
						ActivityUtil.triggerGoldClub(playerData, amount + returnGold);
						// 抢红包
						ActivityUtil.triggerRedEnvelope(playerData, amount + returnGold);

						DailyQuestEntity dailyQuestEntity = playerData.loadDailyQuestEntity();
						dailyQuestEntity.offlineOnRecharge(amount + returnGold + costAmount);

					} catch (Exception e) {
						MyException.catchException(e);
					}
				}

				// 充值数据上报
				if (orderSerial != null && orderSerial.length() > 0) {
				}

				try {
					// 离线日志记录
					BehaviorLogger.log4Service(playerEntity, Source.PLAYER_ATTR_CHANGE, Action.RECHARGE,
							Params.valueOf("playerAttr", Const.playerAttr.GOLD_VALUE),
							Params.valueOf("orderSerial", orderSerial), Params.valueOf("goodsId", goodsId),
							Params.valueOf("payMoney", payMoney), Params.valueOf("costMoney", costMoney),
							Params.valueOf("add", addGold), Params.valueOf("returnGold", returnGold),
							Params.valueOf("after", playerEntity.getRmbGold()), Params.valueOf("oldVip", oldVipLevel),
							Params.valueOf("vipLevel", playerEntity.getVipLevel()));

					// 首充日志
					if (playerEntity != null) {
						BehaviorLogger.log4Platform(playerEntity, Action.FIRST_RECHARGE,
								Params.valueOf("goodsId", goodsId), Params.valueOf("payMoney", payMoney),
								Params.valueOf("costMoney", costMoney));
					}
				} catch (Exception e) {
					MyException.catchException(e);
				}
			}
		} catch (Exception e) {
			// TODO: handle exception
			MyException.catchException(e);
			rechargeEntity.setRechageStatus(1019);
		}
		return rechargeEntity;
	}

	public boolean dohtapdb(String appid, String orderid, String puid,String currency_type,  double amount, String goodid,String productName) {
		HttpClient httpClient = null;
		HttpPost httpPost = null;
		HttpResponse response = null;
		try {
			JSONObject pobj = new JSONObject();
			pobj.put("order_id", orderid);//order_id，可選，若傳遞此參數，需要保證order_id唯一，重複訂單不計入統計
			pobj.put("amount", (int)(amount*100));//充值金額（必須是整數，單位分，即無論什麼幣種，都需要乘以100），必傳
			pobj.put("virtual_currency_amount", 0);//獲贈虛擬幣數量，必傳，可爲0
			pobj.put("currency_type",currency_type);//貨幣類型，可選，不傳或者不是正確的貨幣類型，統一處理成人民幣分
			pobj.put("product", productName);//充值包名稱，可選
			//pobj.put("product", goodid);//充值包名稱，可選
			pobj.put("payment", "web");//充值途徑，可選
			
			JSONObject obj = new JSONObject();
			obj.put("module", "GameAnalysis");//固定
			//obj.put("ip", "8.8.8.8");
			obj.put("name", "charge");//固定
			obj.put("index", appid);//APPID注意替換成TapDB的appid
			obj.put("identify", puid);//user_id，必須和客戶端的setUser接口傳遞的user_id一樣，並且該用戶已經通過SDK接口進行過統計
			obj.put("properties", pobj.toString());
			Log.logPrintln("tapdbapi : " + tapdbURL + "?" + obj.toString());
			String postRequest = URLEncoder.encode(obj.toString(), "UTF-8");
			Log.logPrintln("tapdbapi : " + tapdbURL + "?" + postRequest);
			
			httpClient = HttpClients.custom().build();

			httpPost = new HttpPost(tapdbURL);
			RequestConfig reqConfig = RequestConfig.custom().setSocketTimeout(10000).setConnectTimeout(10000).build();
			httpPost.setConfig(reqConfig);
			httpPost.setHeader(HttpHeaders.CONNECTION, "close");
			
			StringEntity entity = new StringEntity(postRequest);
			entity.setContentEncoding("utf-8");
			entity.setContentType("application/json");
			httpPost.setEntity(entity);
			response = httpClient.execute(httpPost);
			int statusCode = response.getStatusLine().getStatusCode();
			HttpEntity httpEntity = response.getEntity();
			String resultStr = EntityUtils.toString(httpEntity);
			Log.logPrintln("tapdbapi result: " + resultStr + "code: " + statusCode);
			//JSONObject result = JSONObject.fromObject(resultStr);
			httpPost.releaseConnection();
			return true;
		
		} catch (Exception e) {
			MyException.catchException(e);
		}
		return false;
	}
	
//	public int getStatusByHeorID(String url, String api_id, String apitoken,String symbol, int heroid) {
//		HttpClient httpClient = null;
//		HttpPost httpPost = null;
//		HttpResponse response = null;
//		try {
////			JSONObject pobj = new JSONObject();
////			pobj.put("api_id", api_id);
////			pobj.put("api_token", apitoken);
////			pobj.put("hero_id", heroid);
////			pobj.put("collection_symbol", symbol);
//			
//	        StringBuffer reqBuffer = new StringBuffer();
//	        reqBuffer.append("api_id=");
//	        reqBuffer.append(api_id);
//	        reqBuffer.append("&api_token=");
//	        reqBuffer.append(apitoken);
//	        reqBuffer.append("&hero_id=");
//	        reqBuffer.append(String.valueOf(heroid));
//	        reqBuffer.append("&collection_symbol=");
//	        reqBuffer.append(symbol);
//
//			httpClient = HttpClients.custom().build();
//
//			httpPost = new HttpPost(url);
//			RequestConfig reqConfig = RequestConfig.custom().setSocketTimeout(10000).setConnectTimeout(10000).build();
//			httpPost.setConfig(reqConfig);
//			httpPost.setHeader(HttpHeaders.CONNECTION, "close");
//			
//			StringEntity entity = new StringEntity(reqBuffer.toString());
//			entity.setContentEncoding("utf-8");
//			entity.setContentType("application/x-www-form-urlencoded");
//			httpPost.setEntity(entity);
//			
//			response = httpClient.execute(httpPost);
//			int statusCode = response.getStatusLine().getStatusCode();
//			HttpEntity httpEntity = response.getEntity();
//			String resultStr = EntityUtils.toString(httpEntity);
//			Log.logPrintln("MarketAPI result: " + resultStr + "code: " + statusCode);
//			JSONObject resjos = JSONObject.fromObject(resultStr);
//			int errorcode = -1;
//			if (resjos.has("err")) {
//				errorcode = resjos.getInt("err");
//			}
//			httpPost.releaseConnection();
//			return errorcode;
//		} catch (Exception e) {
//			MyException.catchException(e);
//			return -1;
//		}
//	}

	public Boolean KusoQueryOrderId(String orderId) {
		String SanBox_BASE_URL = "https://dev-api.apluspay.io/payments/check/";
		String Production_BASE_URL = "https://api.apluspay.io/payments/check/";
		
		String mchKey = "MdXeK5Gydtjkl@Bd";
		String secret = "iHIFn@YJKcctt4PO";
		
		String BASE_URL = App.getInstance().getAppCfg().isDebug() ? SanBox_BASE_URL : Production_BASE_URL;
		
		try {
			
			StringBuffer plainBuffer = new StringBuffer();
			plainBuffer.append("mchKey=");
			plainBuffer.append(mchKey);
			plainBuffer.append("&order=");
			plainBuffer.append(orderId);

			String plainText  =  AESSignUtil.aesEncrypt(plainBuffer.toString(), mchKey, secret);
			String sign = AESSignUtil.getSHA256Str(plainText);
			
			StringBuffer reqBuffer = new StringBuffer();
			reqBuffer.append(BASE_URL);
			reqBuffer.append("?order=");
			reqBuffer.append(orderId);
			reqBuffer.append("&mchKey=");
			reqBuffer.append(mchKey);
			reqBuffer.append("&sign=");
			reqBuffer.append(sign);
			
			String reqUrl = reqBuffer.toString();
			
			HttpClient httpClient = HttpClients.custom().build();
			HttpGet httpGet = new HttpGet(reqUrl);
			RequestConfig reqConfig = RequestConfig.custom().setSocketTimeout(10000).setConnectTimeout(10000).build();
			httpGet.setConfig(reqConfig);
			httpGet.setHeader("Content-type", "application/json");
			HttpResponse response = null;
			
			response = httpClient.execute(httpGet);

			int statusCode = response.getStatusLine().getStatusCode();
			if (statusCode == HttpStatus.SC_OK) {
				return true;
			}
			return false;
		} catch (Exception e) {
			// TODO: handle exception
			MyException.catchException(e);
			return false;
		}
		
	}
	
	
	
}
