package com.guaji.game.module.activity.halloween;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.lang.StringUtils;
import org.guaji.config.ConfigManager;
import org.guaji.log.Log;
import org.guaji.net.protocol.Protocol;
import org.guaji.os.GuaJiTime;

import com.guaji.game.protocol.Activity3.HalloweenExchangeInfo;
import com.guaji.game.protocol.Activity3.HalloweenRes;
import com.guaji.game.config.ActivityTimeCfg;
import com.guaji.game.config.HalloweenConstCfg;
import com.guaji.game.config.HalloweenDropCfg;
import com.guaji.game.config.HalloweenExchangeCfg;
import com.guaji.game.item.AwardItems;
import com.guaji.game.item.ConsumeItems;
import com.guaji.game.item.ItemInfo;
import com.guaji.game.log.BehaviorLogger;
import com.guaji.game.log.BehaviorLogger.Action;
import com.guaji.game.log.BehaviorLogger.Params;
import com.guaji.game.player.Player;
import com.guaji.game.protocol.Const;
import com.guaji.game.protocol.Const.changeType;
import com.guaji.game.protocol.HP;
import com.guaji.game.protocol.Status;

/**
 * 万圣节活动管理类
 */
public class HalloweenManager {

	public static final int DRAW_TYPE_ONCE = 1;
	public static final int DRAW_TYPE_COMBO = 10;

	/**
	 * 同步
	 */
	static void sync(Player player, ActivityTimeCfg timeCfg, HalloweenStatus status) {
		HalloweenRes.Builder response = getBuilder(player, timeCfg, status);
		if (null != response) {
			player.sendProtocol(Protocol.valueOf(HP.code.HALLOWEEN_S_VALUE, response));
		}
	}

	/**
	 * 抽奖
	 */
	static void draw(Player player, ActivityTimeCfg timeCfg, HalloweenStatus status, int times) {

		// 如果活动进入展示期，只能同步和兑换
		if (timeCfg.isEnd()) {
			player.sendError(HP.code.HALLOWEEN_C_VALUE, Status.error.ACTIVITY_CLOSE);
			return;
		}
		// 获取抽奖常量
		HalloweenConstCfg constCfg = ConfigManager.getInstance().getConfigByKey(HalloweenConstCfg.class, 1);
		if (null == constCfg) {
			player.sendError(HP.code.HALLOWEEN_C_VALUE, Status.error.DATA_NOT_FOUND);
			return;
		}
		// 检测抽奖条件
		/** 单次抽奖券消耗 **/
		String costItems = constCfg.getItemCost();
		ConsumeItems consumeItems = ConsumeItems.valueOf();
		List<ItemInfo> itemInfoList = ItemInfo.valueListOf(costItems);
		
		//boolean isFree = false;
		if (times == HalloweenManager.DRAW_TYPE_COMBO) {
			/** 抽奖券需要10个 **/
			for (Iterator<ItemInfo> iterator = itemInfoList.iterator(); iterator.hasNext();) {
				ItemInfo itemInfo = iterator.next();
				itemInfo.setQuantity(itemInfo.getQuantity() * DRAW_TYPE_COMBO);
			}
		}

		/** 优先判断抽奖券消耗 **/
		boolean isCost;
		isCost = consumeItems.addConsumeInfo(player.getPlayerData(), itemInfoList);
		if (isCost && consumeItems.checkConsume(player)) {
			if (!consumeItems.consumeTakeAffect(player, Action.HAREM_ACTIVITY)) {
				player.sendError(HP.code.HALLOWEEN_C_VALUE, Status.error.ITEM_NOT_ENOUGH_VALUE);
				return;
			}	
		}
		else
		{
			player.sendError(HP.code.HALLOWEEN_C_VALUE, Status.error.ITEM_NOT_ENOUGH_VALUE);
			return;
		}
			
		/*	變更扣道具，把扣鑽石拿掉
		int costGold = 0;
		if (times == HalloweenManager.DRAW_TYPE_COMBO) {
			int ownGold = player.getGold();
			costGold = constCfg.getTenCost();
			if (ownGold < costGold) {
				player.sendError(HP.code.HALLOWEEN_C_VALUE, Status.error.GOLD_NOT_ENOUGH_VALUE);
				return;
			}
		} else {
			if (canFreeDraw(status, constCfg)) {
				status.setLastFreeTime(GuaJiTime.getSeconds());
			} else {
				int ownGold = player.getGold();
				costGold = constCfg.getSingleCost();
				if (ownGold < costGold) {
					player.sendError(HP.code.HALLOWEEN_C_VALUE, Status.error.GOLD_NOT_ENOUGH_VALUE);
					return;
				}
			}
		}*/

		// 随机物品，进入特殊奖池和普通奖池的历史记录是分开计算
		List<HalloweenDropCfg> cfgList = HalloweenDropCfg.dropReward(status, times);

		// 发奖
		List<AwardItems> awardItemsList = new ArrayList<AwardItems>();

		for (HalloweenDropCfg cfg : cfgList) {
			String reward = cfg.getRewards();
			if (StringUtils.isEmpty(reward)) {
				Log.errPrintln("HalloweenDropCfg reward is null");
				continue;
			}

			List<ItemInfo> itemInfos = ItemInfo.valueListOf(reward);
			AwardItems awardItems = new AwardItems();
			awardItems.addItemInfos(itemInfos);
			awardItems.rewardTakeAffectAndPush(player, Action.HALLOWEEN_DRAW, 0);

			awardItemsList.add(awardItems);
		}

		// 扣除钻石
		/*if (costGold > 0) {
			player.consumeGold(costGold, Action.HALLOWEEN_DRAW);
			ConsumeItems.valueOf(changeType.CHANGE_GOLD, costGold).pushChange(player);
		}*/

		// 更新活动实体
		player.getPlayerData().updateActivity(Const.ActivityId.HALLOWEEN_VALUE, timeCfg.getStageId());
		// 返回消息包
		HalloweenRes.Builder response = getBuilder(player, timeCfg, status);
		if (null != response) {
			for (AwardItems item : awardItemsList) {
				response.addReward(item.toString());
			}
			player.sendProtocol(Protocol.valueOf(HP.code.HALLOWEEN_S_VALUE, response));
		}
		// BI日志
		BehaviorLogger.log4Platform(player, Action.HALLOWEEN_DRAW, Params.valueOf("type", times));
	}

	/**
	 * 是否免费抽奖
	 */
	public static boolean canFreeDraw(HalloweenStatus status, HalloweenConstCfg constCfg) {
		int freeCD = getFreeCD(status, constCfg);
		if (freeCD == 0)
			return true;
		return false;
	}

	/**
	 * 清空每日兑换数据
	 */
	public static void initExchangeMap(HalloweenStatus status) {
		Map<Object, HalloweenExchangeCfg> exchangeCfgMap = ConfigManager.getInstance().getConfigMap(HalloweenExchangeCfg.class);
		Map<Integer, Integer> map = status.getExchangeMap();
		for (HalloweenExchangeCfg cfg : exchangeCfgMap.values()) {
			map.put(cfg.getId(), 0);
		}
	}

	/**
	 * 获取免费抽奖CD时间
	 */
	private static int getFreeCD(HalloweenStatus status, HalloweenConstCfg constCfg) {
		int lastFreeTime = status.getLastFreeTime();
		int freeConst = constCfg.getFreeCD() * 3600;
		int now = GuaJiTime.getSeconds();
		int freeCD = (int) Math.max(freeConst - (now - lastFreeTime), 0);
		return freeCD;
	}

	/**
	 * 获取返回结构体
	 */
	static HalloweenRes.Builder getBuilder(Player player, ActivityTimeCfg timeCfg, HalloweenStatus status) {

		// 返回包
		HalloweenRes.Builder response = HalloweenRes.newBuilder();
		// 获取抽奖常量
		HalloweenConstCfg constCfg = ConfigManager.getInstance().getConfigByKey(HalloweenConstCfg.class, 1);
		if (null == constCfg) {
			player.sendError(HP.code.HALLOWEEN_C_VALUE, Status.error.DATA_NOT_FOUND);
			return null;
		}
		// 抽奖耗费钻石
		response.setOnceCostGold(constCfg.getSingleCost());
		response.setTenCostGold(constCfg.getTenCost());
		// CD时间
		response.setFreeCD(-1);//修改為送-1為無freecd
		//response.setFreeCD(getFreeCD(status, constCfg));
		// 活动剩余时间
		response.setLeftTime(timeCfg.calcActivitySurplusTime());
		// 活动展示时间
		response.setLeftDisplayTime(timeCfg.calcActivityCloseTime());
		// 兑换信息展示
		List<HalloweenExchangeInfo> exchangeInfoList = new ArrayList<HalloweenExchangeInfo>();
		Map<Integer, Integer> exchangeMap = status.getExchangeMap();
		for (Entry<Integer, Integer> entry : exchangeMap.entrySet()) {
			HalloweenExchangeInfo.Builder info = HalloweenExchangeInfo.newBuilder();
			info.setId(entry.getKey());
			info.setExchangeTimes(entry.getValue());
			exchangeInfoList.add(info.build());
		}
		response.addAllInfo(exchangeInfoList);
		return response;
	}

	/**
	 * 万圣节兑换
	 */
	static void exchange(Player player, ActivityTimeCfg timeCfg, HalloweenStatus status, int exchangeId, int multiple) {

		// 如果活动进入展示期，只能同步和兑换
		if (timeCfg.calcActivityCloseTime() <= 0) {
			player.sendError(HP.code.HALLOWEEN_C_VALUE, Status.error.ACTIVITY_CLOSE);
			return;
		}
		int stageId = timeCfg.getStageId();

		// 检查配表数据
		Map<Object, HalloweenExchangeCfg> exchangeCfgMap = ConfigManager.getInstance().getConfigMap(HalloweenExchangeCfg.class);
		HalloweenExchangeCfg exchangeCfg = exchangeCfgMap.get(exchangeId);
		if (exchangeCfgMap == null || exchangeCfgMap.size() == 0 || exchangeCfg == null) {
			player.sendError(HP.code.HALLOWEEN_C_VALUE, Status.error.DATA_NOT_FOUND);
			return;
		}

		// 获取兑换数据
		Map<Integer, Integer> exchangeMap = status.getExchangeMap();
		Integer exchangeTimes = 0;
		if (exchangeMap != null && exchangeMap.size() > 0) {
			exchangeTimes = exchangeMap.get(exchangeId);
			exchangeTimes = exchangeTimes == null ? 0 : exchangeTimes;
		}

		// 验证兑换次数
		int alreadyExchangeTimes = exchangeTimes + multiple;
		int limitTimes = exchangeCfg.getLimitTimes();
		// 如果limitTimes为0则为不限制次数
		if (limitTimes > 0 && alreadyExchangeTimes > limitTimes) {
			player.sendError(HP.code.HALLOWEEN_C_VALUE, Status.error.DAYLI_TIMES_LIMIT);
			return;
		}

		// 验证所需物品
		String costItems = exchangeCfg.getCostItems();
		List<ItemInfo> itemInfos = ItemInfo.valueListOf(costItems);
		for (Iterator<ItemInfo> iterator = itemInfos.iterator(); iterator.hasNext();) {
			ItemInfo itemInfo = iterator.next();
			itemInfo.setQuantity(itemInfo.getQuantity() * multiple);
		}
		ConsumeItems consumeItems = ConsumeItems.valueOf();
		// 验证道具数量
		boolean isCost = consumeItems.addConsumeInfo(player.getPlayerData(), itemInfos);
		if (!isCost) {
			// 道具不足
			player.sendError(HP.code.HALLOWEEN_C_VALUE, Status.error.ITEM_NOT_ENOUGH_VALUE);
			return;
		}
		if (!consumeItems.checkConsume(player, HP.code.HALLOWEEN_C_VALUE)) {
			player.sendError(HP.code.HALLOWEEN_C_VALUE, Status.error.ITEM_NOT_ENOUGH_VALUE);
			return;
		}
		// 消耗道具
		consumeItems.consumeTakeAffect(player, Action.HAREM_EXCHANGE);

		// 获取并构造兑换奖励
		String reward = exchangeCfg.getExchangeItems();
		List<ItemInfo> rewardList = ItemInfo.valueListOf(reward);
		for (Iterator<ItemInfo> iterator = rewardList.iterator(); iterator.hasNext();) {
			ItemInfo itemInfo = iterator.next();
			itemInfo.setQuantity(itemInfo.getQuantity() * multiple);
		}

		// 发奖
		AwardItems awardItems = new AwardItems();
		awardItems.addItemInfos(rewardList);
		awardItems.rewardTakeAffectAndPush(player, Action.HALLOWEEN_EXCHANGE, 2);

		// 更新兑换记录
		status.getExchangeMap().put(exchangeId, alreadyExchangeTimes);

		// 更新活动数据
		player.getPlayerData().updateActivity(Const.ActivityId.HALLOWEEN_VALUE, stageId);

		// BI日志
		BehaviorLogger.log4Platform(player, Action.HALLOWEEN_EXCHANGE, Params.valueOf("exchangeId", exchangeId), Params.valueOf("multiple", multiple),
		        Params.valueOf("curTimes", status.getExchangeMap().get(exchangeId)));

		// 返回消息
		HalloweenRes.Builder response = HalloweenManager.getBuilder(player, timeCfg, status);
		player.sendProtocol(Protocol.valueOf(HP.code.HALLOWEEN_S_VALUE, response));

	}

}
