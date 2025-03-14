package com.guaji.game.module.activity.turntable;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.lang.StringUtils;
import org.guaji.config.ConfigManager;
import org.guaji.log.Log;
import org.guaji.net.protocol.Protocol;
import org.guaji.os.GuaJiTime;

import com.guaji.game.protocol.Activity3.TurntableExchangeInfo;
import com.guaji.game.protocol.Activity3.TurntableExchangeRes;
import com.guaji.game.protocol.Activity3.TurntableRet;
import com.guaji.game.protocol.Const.changeType;
import com.guaji.game.config.ActivityTimeCfg;
import com.guaji.game.config.TurntableBoxRewardCfg;
import com.guaji.game.config.TurntableConstCfg;
import com.guaji.game.config.TurntableDropCfg;
import com.guaji.game.config.TurntableExchangeCfg;
import com.guaji.game.config.TurntableMultipleCfg;
import com.guaji.game.item.AwardItems;
import com.guaji.game.item.ConsumeItems;
import com.guaji.game.item.ItemInfo;
import com.guaji.game.log.BehaviorLogger;
import com.guaji.game.log.BehaviorLogger.Action;
import com.guaji.game.log.BehaviorLogger.Params;
import com.guaji.game.player.Player;
import com.guaji.game.protocol.Const;
import com.guaji.game.protocol.HP;
import com.guaji.game.protocol.Status;

/**
 * 大转盘活动管理类
 */
public class TurntableManager {

	public static final int DRAW_TYPE_ONCE = 1;
	public static final int DRAW_TYPE_COMBO = 10;

	/**
	 * 同步
	 */
	static void sync(Player player, ActivityTimeCfg timeCfg, TurntableStatus status) {
		TurntableRet.Builder response = getBuilder(player, timeCfg, status, 0);
		if (null != response) {
			Log.logPrintln(response.build().toString());
			player.sendProtocol(Protocol.valueOf(HP.code.TURNTABLE_S_VALUE, response));
		}
	}

	/**
	 * 抽奖
	 */
	static void draw(Player player, ActivityTimeCfg timeCfg, TurntableStatus status, int times) {

		// 这里如果活动进入展示期是不可以抽奖的、但是可以开启宝箱和同步
		if (timeCfg.isEnd()) {
			player.sendError(HP.code.TURNTABLE_C_VALUE, Status.error.ACTIVITY_CLOSE);
			return;
		}
		// 获取抽奖常量
		TurntableConstCfg constCfg = ConfigManager.getInstance().getConfigByKey(TurntableConstCfg.class, 1);
		if (null == constCfg) {
			player.sendError(HP.code.TURNTABLE_C_VALUE, Status.error.DATA_NOT_FOUND);
			return;
		}
		// 检测抽奖条件
		int costGold = 0;
		if (times == TurntableManager.DRAW_TYPE_COMBO) {
			int ownGold = player.getGold();
			costGold = constCfg.getTenCost();
			if (ownGold < costGold) {
				player.sendError(HP.code.TURNTABLE_C_VALUE, Status.error.GOLD_NOT_ENOUGH_VALUE);
				return;
			}
		} else {
			if (canFreeDraw(status, constCfg)) {
				status.setLastFreeTime(GuaJiTime.getSeconds());
			} else {
				int ownGold = player.getGold();
				costGold = constCfg.getOneCost();
				if (ownGold < costGold) {
					player.sendError(HP.code.TURNTABLE_C_VALUE, Status.error.GOLD_NOT_ENOUGH_VALUE);
					return;
				}
			}
		}
		// 获取总的抽奖次数
		int totalTimes = status.getTotalTimes();

		// 随机物品、倍数、发奖
		List<TurntableDropCfg> cfgList = TurntableDropCfg.dropReward(totalTimes, times);
		
		List<Integer> multipleList = new ArrayList<Integer>();	
		
		List<AwardItems> awardItemsList = new ArrayList<AwardItems>();
		
		int id = 0;
		for (TurntableDropCfg cfg : cfgList) {
			String reward = cfg.getRewards();
			if (StringUtils.isEmpty(reward)) {
				Log.errPrintln("TurntableDropCfg reward is null");
				continue;
			}

			// 随机倍数
			int multiple = TurntableMultipleCfg.getRewardMultiple();
			
			multipleList.add(multiple);
			
			ItemInfo itemInfo = ItemInfo.valueOf((reward));
			itemInfo.setQuantity(itemInfo.getQuantity() * multiple);
			
			AwardItems awardItems = new AwardItems();
			awardItems.addItem(itemInfo);
			awardItems.rewardTakeAffectAndPush(player, Action.TURNTABLE_DRAW, 0);
			
			awardItemsList.add(awardItems);
			id = cfg.getId();
		}

		// 扣除钻石
		if (costGold > 0) {
			player.consumeGold(costGold, Action.TURNTABLE_DRAW);
			ConsumeItems.valueOf(changeType.CHANGE_GOLD, costGold).pushChange(player);
		}
		// 更新积分
		int deltaCredits = 0;
		if (times == TurntableManager.DRAW_TYPE_ONCE) {
			deltaCredits = constCfg.getOneCredits();
		} else {
			deltaCredits = constCfg.getTenCredits();
		}
		int credits = status.getCredits();
		credits += deltaCredits;
		status.setCredits(credits);

		// 更新宝箱信息
		for (int i = 0; i < times; i++) {
			totalTimes += 1;
			List<TurntableBoxRewardCfg> boxCfgList = ConfigManager.getInstance().getConfigList(TurntableBoxRewardCfg.class);
			for (TurntableBoxRewardCfg boxRewardCfg : boxCfgList) {
				int boxId = boxRewardCfg.getId();
				if (boxRewardCfg.getCondition() == totalTimes) {
					if (!getBoxStatus(status, boxId)) {
						setBoxStatus(status, boxId, true);
					}
				}
			}
		}

		// 更新抽奖总次数
		status.setTotalTimes(totalTimes);
		// 更新活动实体
		player.getPlayerData().updateActivity(Const.ActivityId.TURNTABLE_VALUE, timeCfg.getStageId());
		// 返回消息包
		TurntableRet.Builder response = getBuilder(player, timeCfg, status, id);
		if (null != response) {
			response.addAllMultiple(multipleList);
			for (AwardItems item : awardItemsList) {
				response.addReward(item.toString());
			}
			Log.logPrintln(response.build().toString());
			player.sendProtocol(Protocol.valueOf(HP.code.TURNTABLE_S_VALUE, response));
		}
		// BI日志
		BehaviorLogger.log4Platform(player, Action.TURNTABLE_DRAW, Params.valueOf("type", times));
	}

	/**
	 * 开启宝箱
	 */
	static void openBox(Player player, ActivityTimeCfg timeCfg, TurntableStatus status, int boxId) {
		// 获取宝箱配表信息
		TurntableBoxRewardCfg boxRewardCfg = ConfigManager.getInstance().getConfigByIndex(TurntableBoxRewardCfg.class, boxId - 1);
		if (null == boxRewardCfg) {
			player.sendError(HP.code.TURNTABLE_C_VALUE, Status.error.DATA_NOT_FOUND);
			return;
		}

		// 验证宝箱开启条件
		int condition = boxRewardCfg.getCondition();
		int totalTimes = status.getTotalTimes();
		boolean boxStatus = getBoxStatus(status, boxId);
		if (!boxStatus || totalTimes < condition) {
			player.sendError(HP.code.TURNTABLE_C_VALUE, Status.error.PARAMS_INVALID_VALUE);
			return;
		}

		// 发奖
		String rewardsStr = boxRewardCfg.getRewards();
		AwardItems awardItems = new AwardItems();
		awardItems.addItemInfos(ItemInfo.valueListOf(rewardsStr));
		awardItems.rewardTakeAffectAndPush(player, Action.TURNTABLE_DRAW, 2);

		// 更新宝箱状态
		setBoxStatus(status, boxId, false);

		// 数据落地
		player.getPlayerData().updateActivity(Const.ActivityId.TURNTABLE_VALUE, timeCfg.getStageId());

		TurntableRet.Builder response = getBuilder(player, timeCfg, status, 0);
		if (null != response) {
			Log.logPrintln(response.build().toString());
			player.sendProtocol(Protocol.valueOf(HP.code.TURNTABLE_S_VALUE, response));
		}

	}

	/**
	 * 获取兑换信息
	 */
	static TurntableExchangeRes.Builder getExchangeBuilders(TurntableStatus status, ActivityTimeCfg timeCfg) {
		TurntableExchangeRes.Builder response = TurntableExchangeRes.newBuilder();
		List<TurntableExchangeInfo> list = new ArrayList<TurntableExchangeInfo>();
		Map<Integer, Integer> exchangeMap = status.getExchangeMap();
		for (Entry<Integer, Integer> entry : exchangeMap.entrySet()) {
			TurntableExchangeInfo.Builder info = TurntableExchangeInfo.newBuilder();
			info.setId(entry.getKey());
			info.setExchangeTimes(entry.getValue());
			list.add(info.build());
		}
		response.addAllInfo(list);
		int leftTime = 0;
		// 进入展示期
		if (timeCfg.isEnd()) {
			leftTime = timeCfg.calcActivityCloseTime();
		}
		response.setLeftTime(leftTime);
		response.setCredits(status.getCredits());
		return response;
	}

	/**
	 * 获取所有宝箱状态
	 */
	static List<Boolean> getAllBoxStatus(TurntableStatus status) {
		List<Boolean> list = new ArrayList<Boolean>();
		String[] boxStatus = StringUtils.split(status.getCanOpenBox(), ",");
		for (String bs : boxStatus) {
			list.add(Boolean.valueOf(bs));
		}
		return list;
	}

	/**
	 * 是否免费抽奖
	 */
	public static boolean canFreeDraw(TurntableStatus status, TurntableConstCfg constCfg) {
		int freeCD = getFreeCD(status, constCfg);
		if (freeCD == 0)
			return true;
		return false;
	}

	/**
	 * 清空每日兑换数据
	 */
	public static void initExchangeMap(TurntableStatus status) {
		Map<Object, TurntableExchangeCfg> exchangeCfgMap = ConfigManager.getInstance().getConfigMap(TurntableExchangeCfg.class);
		Map<Integer, Integer> map = status.getExchangeMap();
		for (TurntableExchangeCfg cfg : exchangeCfgMap.values()) {
			map.put(cfg.getId(), 0);
		}
	}

	/**
	 * 获取免费抽奖CD时间
	 */
	private static int getFreeCD(TurntableStatus status, TurntableConstCfg constCfg) {
		int lastFreeTime = status.getLastFreeTime();
		int freeConst = constCfg.getFreeCD() * 3600;
		int now = GuaJiTime.getSeconds();
		int freeCD = (int) Math.max(freeConst - (now - lastFreeTime), 0);
		return freeCD;
	}

	/**
	 * 设置某个宝箱的状态
	 */
	private static void setBoxStatus(TurntableStatus status, int boxId, boolean flag) {
		String boxStatusStr = status.getCanOpenBox();
		String[] statusArray = StringUtils.split(boxStatusStr, ",");
		statusArray[boxId - 1] = String.valueOf(flag);
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < statusArray.length; i++) {
			sb.append(statusArray[i]);
			if (i < statusArray.length - 1) {
				sb.append(",");
			}
		}
		status.setCanOpenBox(sb.toString());
	}

	/**
	 * 获取某个宝箱的状态
	 */
	private static boolean getBoxStatus(TurntableStatus status, int boxId) {
		String boxStatusStr = status.getCanOpenBox();
		String[] statusArray = StringUtils.split(boxStatusStr, ",");
		String boxStatus = statusArray[boxId - 1];
		if (boxStatus.equals("true") || boxStatus.equals("false")) {
			return Boolean.valueOf(boxStatus);
		}
		return false;
	}

	/**
	 * 获取返回结构体
	 */
	private static TurntableRet.Builder getBuilder(Player player, ActivityTimeCfg timeCfg, TurntableStatus status,int id) {

		// 返回更新包
		TurntableRet.Builder response = TurntableRet.newBuilder();
		// 总积分
		response.setCredits(status.getCredits());
		// 获取抽奖常量
		TurntableConstCfg constCfg = ConfigManager.getInstance().getConfigByKey(TurntableConstCfg.class, 1);
		if (null == constCfg) {
			player.sendError(HP.code.TURNTABLE_C_VALUE, Status.error.DATA_NOT_FOUND);
			return null;
		}
		// 抽奖耗费钻石
		response.setOneCost(constCfg.getOneCost());
		response.setTenCost(constCfg.getTenCost());
		// CD时间
		response.setFreeCD(getFreeCD(status, constCfg));
		// 当前宝箱状态
		response.addAllCanOpenBox(getAllBoxStatus(status));
		// 获取宝箱开启条件
		List<TurntableBoxRewardCfg> boxCfgList = ConfigManager.getInstance().getConfigList(TurntableBoxRewardCfg.class);
		List<Integer> condition = new ArrayList<Integer>();
		for (TurntableBoxRewardCfg cfg : boxCfgList) {
			condition.add(cfg.getCondition());
		}
		// 宝箱开启条件
		response.addAllCondition(condition);
		// 抽奖总次数
		int totalTimes = status.getTotalTimes();
		response.setTotalTimes(totalTimes);
		// 幸运值
		int factor = TurntableDropCfg.WILL_HIT_FACTOR;
		int luckyValue = factor - totalTimes % factor;
		response.setLuckyValue(luckyValue);
		// 剩余时间
		response.setLeftTime(timeCfg.calcActivitySurplusTime());
		// 转盘指针ID
		response.setId(id);
		return response;
	}

}
