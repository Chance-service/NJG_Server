package com.guaji.game.module.activity.maidenEncounter;

import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.guaji.app.AppObj;
import org.guaji.config.ConfigManager;
import org.guaji.net.protocol.Protocol;
import org.guaji.net.protocol.IProtocolHandler;

import com.guaji.game.protocol.Activity2.MaidenEncounterExchangeReq;
import com.guaji.game.protocol.Activity2.SyncMaidenEncounterExchangeRes;
import com.guaji.game.config.ActivityTimeCfg;
import com.guaji.game.config.MaidenEncounterExchangeCfg;
import com.guaji.game.entity.MaidenEncounterEntity;
import com.guaji.game.item.AwardItems;
import com.guaji.game.item.ConsumeItems;
import com.guaji.game.item.ItemInfo;
import com.guaji.game.log.BehaviorLogger;
import com.guaji.game.log.BehaviorLogger.Action;
import com.guaji.game.log.BehaviorLogger.Params;
import com.guaji.game.log.BehaviorLogger.Source;
import com.guaji.game.player.Player;
import com.guaji.game.util.ActivityUtil;
import com.guaji.game.util.BuilderUtil;
import com.guaji.game.protocol.Const;
import com.guaji.game.protocol.HP;
import com.guaji.game.protocol.Status;

/**
 * 少女的邂逅商品兑换
 */
public class MaidenEncounterExchangeHandler implements IProtocolHandler {
	@Override
	public boolean onProtocol(AppObj appObj, Protocol protocol) {
		Player player = (Player) appObj;
		int activityId = Const.ActivityId.MAIDEN_ENCOUNTER_VALUE;
		ActivityTimeCfg timeCfg = ActivityUtil.getCurActivityTimeCfg(activityId);
		if (timeCfg == null) {
			// 活动已关闭
			player.sendError(HP.code.MAIDEN_ENCOUNTER_EXCHANGE_C_VALUE, Status.error.ACTIVITY_CLOSE);
			return true;
		}
		MaidenEncounterEntity entity = player.getPlayerData().getMaidenEncounterEntity();
		if (entity == null) {
			player.sendError(HP.code.MAIDEN_ENCOUNTER_EXCHANGE_C_VALUE, Status.error.DATA_NOT_FOUND_VALUE);
			return true;
		}
		// 解析请求参数
		MaidenEncounterExchangeReq request = protocol.parseProtocol(MaidenEncounterExchangeReq.getDefaultInstance());
		int exchangeId = request.getId();// 对应配表id
		int multiple = request.getMultiple();// 倍数

		// 查找配置
		MaidenEncounterExchangeCfg exchangeCfg = ConfigManager.getInstance().getConfigByKey(MaidenEncounterExchangeCfg.class, exchangeId);
		if (exchangeCfg == null) {
			player.sendError(protocol.getType(), Status.error.DATA_NOT_FOUND);
			return false;
		}
		Map<Integer, Integer> exchangeMap = entity.getExchangeMap();
		// 已兑换次数
		Integer remainderTimes = exchangeCfg.getLimitTimes();
		if (exchangeMap != null && exchangeMap.size() > 0) {
			remainderTimes = exchangeMap.get(exchangeId);
		}

		// 验证兑换次数
		if (multiple > remainderTimes) {
			// 没有兑换次数
			player.sendError(protocol.getType(), Status.error.DAYLI_TIMES_LIMIT);
			return false;
		}

		// 消耗道具
		String costItems = exchangeCfg.getCostItems();

		if ((costItems == null || costItems.length() == 0)) {
			// 配置没找到
			player.sendError(protocol.getType(), Status.error.DATA_NOT_FOUND);
			return false;
		}

		// 组装消耗道具结构体
		List<ItemInfo> costItemList = ItemInfo.valueListOf(costItems);
		for (Iterator<ItemInfo> iterator = costItemList.iterator(); iterator.hasNext();) {
			ItemInfo itemInfo = iterator.next();
			itemInfo.setQuantity(itemInfo.getQuantity() * multiple);
		}
		// 奖励
		String reward = exchangeCfg.getExchangeItems();
		List<ItemInfo> rewardList = ItemInfo.valueListOf(reward);
		for (Iterator<ItemInfo> iterator = rewardList.iterator(); iterator.hasNext();) {
			ItemInfo itemInfo = iterator.next();
			itemInfo.setQuantity(itemInfo.getQuantity() * multiple);
		}
		AwardItems awardItems = new AwardItems();
		awardItems.addItemInfos(rewardList);

		ConsumeItems consumeItems = ConsumeItems.valueOf();
		// 验证道具数量
		boolean isCost = consumeItems.addConsumeInfo(player.getPlayerData(), costItemList);
		if (!isCost) {
			// 道具不足
			player.sendError(protocol.getType(), Status.error.ITEM_NOT_ENOUGH_VALUE);
			return false;
		}
		if (!consumeItems.checkConsume(player, protocol.getType())) {
			player.sendError(protocol.getType(), Status.error.ITEM_NOT_ENOUGH_VALUE);
			return false;
		}
		// 消耗道具
		consumeItems.consumeTakeAffect(player, Action.MAIDEN_ENCOUNTER);
		awardItems.rewardTakeAffectAndPush(player, Action.MAIDEN_ENCOUNTER, 1);
		remainderTimes -= multiple;
		exchangeMap.put(exchangeId, remainderTimes);
		entity.setExchangeMap(exchangeMap);
		entity.notifyUpdate();
		// 日志统计
		BehaviorLogger.log4Service(player, Source.USER_OPERATION, Action.MAIDEN_ENCOUNTER, Params.valueOf("type", "exchange"), Params.valueOf("consume", costItemList), Params.valueOf("reward", awardItems));
		BehaviorLogger.log4Platform(player, Action.MAIDEN_ENCOUNTER, Params.valueOf("type", "exchange"), Params.valueOf("consume", costItemList), Params.valueOf("reward", awardItems));
		// 组装协议
		SyncMaidenEncounterExchangeRes.Builder response = BuilderUtil.getMaidenEncounterExchangeBuilders(entity);
		player.sendProtocol(Protocol.valueOf(HP.code.SYNC_MAIDEN_ENCOUNTER_EXCHANGE_S_VALUE, response));
		return true;
	}
}
