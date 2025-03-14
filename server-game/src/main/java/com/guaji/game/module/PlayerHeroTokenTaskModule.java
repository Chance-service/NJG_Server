package com.guaji.game.module;

import java.util.List;
import java.util.Set;

import org.guaji.annotation.MessageHandlerAnno;
import org.guaji.annotation.ProtocolHandlerAnno;
import org.guaji.config.ConfigManager;
import org.guaji.msg.Msg;
import org.guaji.net.protocol.Protocol;

import com.guaji.game.bean.HeroTokenShopBean;
import com.guaji.game.bean.HeroTokenTaskBean;
import com.guaji.game.config.ActivityTimeCfg;
import com.guaji.game.config.BPHShopCfg;
import com.guaji.game.config.HeroTokenTaskCfg;
import com.guaji.game.config.SysBasicCfg;
import com.guaji.game.entity.HeroTokenTaskEntity;
import com.guaji.game.item.AwardItems;
import com.guaji.game.item.ConsumeItems;
import com.guaji.game.log.BehaviorLogger;
import com.guaji.game.log.BehaviorLogger.Action;
import com.guaji.game.log.BehaviorLogger.Params;
import com.guaji.game.log.BehaviorLogger.Source;
import com.guaji.game.module.activity.foreverCard.ForeverCardStatus;
import com.guaji.game.player.Player;
import com.guaji.game.player.PlayerModule;
import com.guaji.game.util.ActivityUtil;
import com.guaji.game.util.BuilderUtil;
import com.guaji.game.util.GsConst;
import com.guaji.game.protocol.Const;
import com.guaji.game.protocol.Const.changeType;
import com.guaji.game.protocol.HP;
import com.guaji.game.protocol.HeroToken.HPHeroTokenTaskCompleteRet;
import com.guaji.game.protocol.HeroToken.HPHeroTokenTaskInfoRet;
import com.guaji.game.protocol.HeroToken.HPShopBuyBean;
import com.guaji.game.protocol.HeroToken.HPShopBuyBeanRet;
import com.guaji.game.protocol.HeroToken.ShopStatusBean;
import com.guaji.game.protocol.HeroToken.TaskStatusBean;
import com.guaji.game.protocol.Status;

/**
 * 英雄令任务模块
 */
public class PlayerHeroTokenTaskModule extends PlayerModule {

	public PlayerHeroTokenTaskModule(Player player) {
		super(player);
		
		listenProto(HP.code.HERO_TOKEN_BUY_INFO_C);
	}

	@Override
	protected boolean onPlayerLogin() {
		player.getPlayerData().loadHeroTokenTaskEntity();
		return true;
	}

	@Override
	protected boolean onPlayerAssemble() {
		player.getPlayerData().syncHeroTokenTaskInfo();
		return true;
	}
	
	/**
	 * 协议响应
	 * @param protocol
	 * @return
	 */
	@Override
	public boolean onProtocol(Protocol protocol) {
		if (protocol.checkType(HP.code.HERO_TOKEN_BUY_INFO_C)) {
			HPShopBuyBean bindMsg = protocol.parseProtocol(HPShopBuyBean.getDefaultInstance());
			int itemId = bindMsg.getItemId();
			int price = bindMsg.getPrice();
			onShopBuy(itemId, price);
		}
		return super.onProtocol(protocol);
	}

	/**
	 * 获取英雄令任务信息
	 * @param protocol
	 * @return
	 */
	@ProtocolHandlerAnno(code = HP.code.HERO_TOKEN_TASK_INFO_C_VALUE)
	protected void onHeroTaskInfo(Protocol hawkProtocol) {
		
		HeroTokenTaskEntity entity = player.getPlayerData().getHeroTokenTaskEntity();
		if (entity == null) {
			sendError(HP.code.HERO_TOKEN_TASK_INFO_C_VALUE, Status.error.TOKEN_ENTITY_NOEXIST);
			return;
		}
		
		HPHeroTokenTaskInfoRet.Builder response = HPHeroTokenTaskInfoRet.newBuilder();
		List<HeroTokenTaskBean> taskList = entity.getTaskList();
		for (int i = 0; i < taskList.size(); i++) {
			HeroTokenTaskBean bean = taskList.get(i);
			TaskStatusBean.Builder builder = TaskStatusBean.newBuilder();
			builder.setTaskId(bean.getTaskId());
			builder.setStatus(bean.getCount());
			response.addTaskStatusBeanList(builder);
		}
		// 1是推送2是请求返回
		response.setVersion(2);
		
		int taskCount = SysBasicCfg.getInstance().getHeroTokenTaskLimit();
		// 终身卡的影响
		ForeverCardStatus foreverCardStatus = ActivityUtil.getActivityStatus(player.getPlayerData(), Const.ActivityId.FOREVER_CARD_VALUE,
																			-1, ForeverCardStatus.class);
		if (null != foreverCardStatus && foreverCardStatus.isOpen()) {
			taskCount +=  SysBasicCfg.getInstance().getHeroTokenTaskUpgrade();
		}
		response.setTaskFinishAlltimes(taskCount);
		response.setTaskFinishLefttimes(taskCount - taskList.size());
		// 构建商店物品
		boolean isOpenActivity = this.isOpenActivity();
		List<HeroTokenShopBean> shopList = entity.getShopList();
		List<BPHShopCfg> shopCfgs = ConfigManager.getInstance().getConfigList(BPHShopCfg.class);
		if (null != shopCfgs && shopCfgs.size() > 0) {
			for (BPHShopCfg shopCfg : shopCfgs) {
				if (null != shopCfg.getAwardItems()) {
					AwardItems.Item item = shopCfg.getAwardItems().getAwardItems().get(0);
					int buyTimes = shopCfg.getCount();
					// 剩余购买次数计算
					for (int i = 0; i < shopList.size(); i++) {
						if (shopList.get(i).getItemId() == item.getId()) {
							buyTimes -= shopList.get(i).getCount();
							break;
						}
					}
					buyTimes = buyTimes < 0 ? 0 : buyTimes;
					int price = isOpenActivity ? shopCfg.getActivityPrice() : shopCfg.getPrice();
					ShopStatusBean.Builder builder = BuilderUtil.createShopBuilder(item, price, buyTimes);
					response.addShopStatusBeanList(builder);
				}
			}
		}
		
		player.sendProtocol(Protocol.valueOf(HP.code.HERO_TOKEN_TASK_INFO_S_VALUE, response));
	}

	/**
	 * 提交任务信息
	 * @param level 怪物等级
	 * @param count 怪物数量
	 */
	@MessageHandlerAnno(code = GsConst.MsgType.HERO_TOKEN_BATTLE_TASK)
	private void onUpdataTaskInfo(Msg msg) {
		int level = (int) msg.getParam(0);
		int count = (int) msg.getParam(1);
		HeroTokenTaskEntity heroTokenTaskEntity = player.getPlayerData().getHeroTokenTaskEntity();
		if (heroTokenTaskEntity != null && level >= 0 && count > 0) {
			List<HeroTokenTaskBean> taskList = heroTokenTaskEntity.getTaskList();
			Set<Integer> completeSet = heroTokenTaskEntity.getCompleteTaskSet();
			for (int i = 0; i < taskList.size(); i++) {
				if (count == 0) {
					break;
				}
				if (completeSet.contains(i)) {
					// 已完成任务不考虑
					continue;
				}
				HeroTokenTaskBean bean = taskList.get(i);
				HeroTokenTaskCfg heroTokenTaskCfg = ConfigManager.getInstance().getConfigByKey(
						HeroTokenTaskCfg.class, bean.getTaskId());
				if (level < heroTokenTaskCfg.getLevelLimit()) {
					// 怪物等级不满足任务要求
					continue;
				}
				// 记录日志
				BehaviorLogger.log4Service(player, Source.USER_OPERATION, Action.HERO_TASK_UPDATE,
						Params.valueOf("taskId", bean.getTaskId()), Params.valueOf("taskIndex", i),
						Params.valueOf("battleLevel", level), Params.valueOf("battleCount", count));
				if (bean.getCount() + count >= heroTokenTaskCfg.getCount()) {
					int needCount = heroTokenTaskCfg.getCount() - bean.getCount();
					heroTokenTaskEntity.addCompleteTaskSet(i);
					heroTokenTaskEntity.addCount(i, needCount);
					sendcompleteTask(bean.getTaskId());
					count -= needCount;
					// 发奖励
					AwardItems awardItems = heroTokenTaskCfg.getAwardItems();
					awardItems.rewardTakeAffectAndPush(player, Action.HERO_TASK_COMPLETE,1);
					continue;
				} else {
					heroTokenTaskEntity.addCount(i, count);
					break;
				}
			}
			heroTokenTaskEntity.notifyUpdate(true);
		}
	}
	
	/**
	 * 发送完成任务指令
	 * @param taskId
	 */
	private void sendcompleteTask(int taskId) {
		HPHeroTokenTaskCompleteRet.Builder ret = HPHeroTokenTaskCompleteRet.newBuilder();
		ret.setTaskId(taskId);
		player.sendProtocol(Protocol.valueOf(HP.code.HERO_TOKEN_TASK_COMPLETE_S_VALUE, ret));
	}
	
	/**
	 * 英雄令购买
	 * @param itemId
	 * @param price
	 */
	private void onShopBuy(int itemId, int price) {
		
		HPShopBuyBeanRet.Builder response = HPShopBuyBeanRet.newBuilder();
		
		HeroTokenTaskEntity entity = player.getPlayerData().getHeroTokenTaskEntity();
		List<HeroTokenShopBean> shopList = entity.getShopList();
		List<BPHShopCfg> shopCfgs = ConfigManager.getInstance().getConfigList(BPHShopCfg.class);
		boolean isOpenActivity = this.isOpenActivity();
		int buyTimes = 0;
		int _price = 0;
		AwardItems items = null;
		for (BPHShopCfg shopCfg : shopCfgs) {
			items = shopCfg.getAwardItems();
			if (null != items) {
				AwardItems.Item item = items.getAwardItems().get(0);
				if (itemId == item.getId()) {
					buyTimes = shopCfg.getCount();
					_price = isOpenActivity ? shopCfg.getActivityPrice() : shopCfg.getPrice();
					break;
				}
			}
		}
		if (null == items.getAwardItems().get(0)) {
			sendError(HP.code.HERO_TOKEN_BUY_INFO_C_VALUE, Status.error.CONFIG_NOT_FOUND);
			return;
		}
		// 数据需要刷新判断
		if (_price != price) {
			response.setIsReset(true);
		} else {
			response.setIsReset(false);
			// 钻石够不够
			if (price > player.getGold()) {
				sendError(HP.code.HERO_TOKEN_BUY_INFO_C_VALUE, Status.error.GOLD_NOT_ENOUGH);
				return;
			}
			// 数据库数据是否需要添加或更新
			boolean isFind = false;
			for (HeroTokenShopBean shopBean : shopList) {
				if (itemId == shopBean.getItemId()) {
					isFind = true;
					// 是否还有购买次数
					if (buyTimes > shopBean.getCount()) {
						// 数据更新
						shopBean.addCount();
						buyTimes = shopBean.getCount();
					} else {
						sendError(HP.code.HERO_TOKEN_BUY_INFO_C_VALUE, Status.error.TIME_LIMIT_TODAY_BUY_TIMES_LIMIT);
						return;
					}
					break;
				}
			}
			// 添加新数据
			if (!isFind) {
				HeroTokenShopBean shopBean = new HeroTokenShopBean(itemId);
				shopList.add(shopBean);
			}
			entity.updataShopData();
			entity.notifyUpdate(true);
			// 符合购买条件
			ConsumeItems.valueOf(changeType.CHANGE_GOLD, price).consumeTakeAffect(player, Action.TOKEN_SHOP_BUY_TOOLS);
			items.rewardTakeAffectAndPush(player, Action.TOKEN_SHOP_BUY_TOOLS,1);
		}
		// 构建商店物品
		for (BPHShopCfg shopCfg : shopCfgs) {
			if (null != shopCfg.getAwardItems()) {
				AwardItems.Item item = shopCfg.getAwardItems().getAwardItems().get(0);
				buyTimes = shopCfg.getCount();
				// 剩余购买次数计算
				for (int i = 0; i < shopList.size(); i++) {
					if (shopList.get(i).getItemId() == item.getId()) {
						buyTimes -= shopList.get(i).getCount();
						break;
					}
				}
				buyTimes = buyTimes < 0 ? 0 : buyTimes;
				_price = isOpenActivity ? shopCfg.getActivityPrice() : shopCfg.getPrice();
				ShopStatusBean.Builder builder = BuilderUtil.createShopBuilder(item, _price, buyTimes);
				response.addShopStatusBeanList(builder);
			}
		}
		
		player.sendProtocol(Protocol.valueOf(HP.code.HERO_TOKEN_BUY_INFO_S_VALUE, response));
	}
	
	/**
	 * 英雄令商店打折活动是否开启
	 * @return
	 */
	private boolean isOpenActivity() {
		int activityId = Const.ActivityId.HERO_TOKEN_SHOP_VALUE;
		ActivityTimeCfg timeCfg = ActivityUtil.getCurActivityTimeCfg(activityId);
		if (null != timeCfg) {
			return true;
		}
		return false;
	}
	
}
