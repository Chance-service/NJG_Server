package com.guaji.game.module.activity.turntable;

import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.guaji.app.AppObj;
import org.guaji.config.ConfigManager;
import org.guaji.log.Log;
import org.guaji.net.protocol.Protocol;
import org.guaji.net.protocol.IProtocolHandler;

import com.guaji.game.protocol.Activity3.TurntableExchangeReq;
import com.guaji.game.protocol.Activity3.TurntableExchangeRes;
import com.guaji.game.config.ActivityTimeCfg;
import com.guaji.game.config.TurntableExchangeCfg;
import com.guaji.game.item.AwardItems;
import com.guaji.game.item.ItemInfo;
import com.guaji.game.log.BehaviorLogger;
import com.guaji.game.log.BehaviorLogger.Action;
import com.guaji.game.log.BehaviorLogger.Params;
import com.guaji.game.player.Player;
import com.guaji.game.util.ActivityUtil;
import com.guaji.game.protocol.Const;
import com.guaji.game.protocol.HP;
import com.guaji.game.protocol.Status;

/**
 * 大转盘活动碎片兑换请求
 */
public class TurntableExchangeHandler implements IProtocolHandler {

	@Override
	public boolean onProtocol(AppObj appObj, Protocol protocol) {

		// 检测活动是否开放
		Player player = (Player) appObj;
		int activityId = Const.ActivityId.TURNTABLE_VALUE;
		ActivityTimeCfg timeCfg = ActivityUtil.getCurActivityTimeCfg(activityId);
		if (timeCfg == null) {
			player.sendError(protocol.getType(), Status.error.ACTIVITY_CLOSE);
			return true;
		}
		int stageId = timeCfg.getStageId();

		// 解析兑换请求参数
		TurntableExchangeReq request = protocol.parseProtocol(TurntableExchangeReq.getDefaultInstance());
		int exchangeId = request.getId();// 兑换ID
		int times = request.getTimes();// 兑换次数

		// 检查配表数据
		Map<Object, TurntableExchangeCfg> exchangeCfgMap = ConfigManager.getInstance().getConfigMap(TurntableExchangeCfg.class);
		TurntableExchangeCfg exchangeCfg = exchangeCfgMap.get(exchangeId);
		if (exchangeCfgMap == null || exchangeCfgMap.size() == 0 || exchangeCfg == null) {
			player.sendError(protocol.getType(), Status.error.DATA_NOT_FOUND);
			return true;
		}

		// 获取活动数据
		TurntableStatus status = ActivityUtil.getActivityStatus(player.getPlayerData(), activityId, stageId, TurntableStatus.class);
		if (null == status) {
			player.sendError(protocol.getType(), Status.error.DATA_NOT_FOUND);
			return true;
		}

		// 获取兑换数据
		Map<Integer, Integer> exchangeMap = status.getExchangeMap();
		Integer exchangeTimes = 0;
		if (exchangeMap != null && exchangeMap.size() > 0) {
			exchangeTimes = exchangeMap.get(exchangeId);
			exchangeTimes = exchangeTimes == null ? 0 : exchangeTimes;
		}

		// 验证兑换次数
		int deltaExchangeTimes = exchangeTimes + times;
		if (deltaExchangeTimes > exchangeCfg.getLimitTimes()) {
			player.sendError(protocol.getType(), Status.error.DAYLI_TIMES_LIMIT);
			return false;
		}

		// 验证所需积分
		int ownCredits = status.getCredits();
		int needCredites = exchangeCfg.getCostCredits() * times;
		if (ownCredits < needCredites) {
			player.sendError(protocol.getType(), Status.error.ROULETTE_CREDITS_LACK_VALUE);
			return false;
		}

		// 获取并构造兑换奖励
		String reward = exchangeCfg.getExchangeItems();
		List<ItemInfo> rewardList = ItemInfo.valueListOf(reward);
		for (Iterator<ItemInfo> iterator = rewardList.iterator(); iterator.hasNext();) {
			ItemInfo itemInfo = iterator.next();
			itemInfo.setQuantity(itemInfo.getQuantity() * times);
		}

		// 扣除积分
		status.setCredits(ownCredits - needCredites);

		// 发奖
		AwardItems awardItems = new AwardItems();
		awardItems.addItemInfos(rewardList);
		awardItems.rewardTakeAffectAndPush(player, Action.TURNTABLE_EXCHANGE, 2);

		// 更新兑换记录
		status.getExchangeMap().put(exchangeId, deltaExchangeTimes);

		// 更新活动数据
		player.getPlayerData().updateActivity(activityId, stageId);

		// BI日志
		BehaviorLogger.log4Platform(player, Action.TURNTABLE_EXCHANGE, Params.valueOf("exchangeId", exchangeId), Params.valueOf("times", times),
		        Params.valueOf("curTimes", status.getExchangeMap().get(exchangeId)));

		// 返回消息
		TurntableExchangeRes.Builder response = TurntableManager.getExchangeBuilders(status, timeCfg);
		Log.logPrintln(response.build().toString());
		player.sendProtocol(Protocol.valueOf(HP.code.TURNTABLE_EXCHANGE_S_VALUE, response));
		return true;
	}

}
