package com.guaji.game.module.activity.treasureRaider;

import java.util.List;

import org.guaji.app.AppObj;
import org.guaji.net.protocol.Protocol;
import org.guaji.net.protocol.IProtocolHandler;

import com.guaji.game.protocol.Activity.HPTreasureRaiderInfoSync;
import com.guaji.game.protocol.Activity.HPTreasureRaiderSearch;
import com.guaji.game.config.ActivityTimeCfg;
import com.guaji.game.config.SysBasicCfg;
import com.guaji.game.config.TreasureRaiderDropCfg;
import com.guaji.game.config.TreasureRaiderTimesCfg;
import com.guaji.game.item.AwardItems;
import com.guaji.game.item.ConsumeItems;
import com.guaji.game.item.ItemInfo;
import com.guaji.game.log.BehaviorLogger;
import com.guaji.game.log.BehaviorLogger.Action;
import com.guaji.game.log.BehaviorLogger.Params;
import com.guaji.game.player.Player;
import com.guaji.game.util.ActivityUtil;
import com.guaji.game.protocol.Const;
import com.guaji.game.protocol.Const.changeType;
import com.guaji.game.protocol.HP;
import com.guaji.game.protocol.Status;

public class TreasureRaiderSearchHandler implements IProtocolHandler {
	public static final int TIMES_TYPE_SINGLE = 1;
	public static final int TIMES_TYPE_TEN = 10;

	@Override
	public boolean onProtocol(AppObj appObj, Protocol protocol) {
		Player player = (Player) appObj;
		// 检测活动是否开放
		int activityId = Const.ActivityId.TREASURE_RAIDER_VALUE;
		ActivityTimeCfg timeCfg = ActivityUtil.getCurActivityTimeCfg(activityId);
		if (timeCfg == null) {
			// 活动已关闭
			player.sendError(protocol.getType(), Status.error.ACTIVITY_CLOSE);
			return true;
		}

		HPTreasureRaiderSearch req = protocol.parseProtocol(HPTreasureRaiderSearch.getDefaultInstance());
		int searchTimes = req.getSearchTimes();
		if (searchTimes != TIMES_TYPE_SINGLE && searchTimes != TIMES_TYPE_TEN) {
			// 活动参数错误
			player.sendError(protocol.getType(), Status.error.PARAMS_INVALID);
			return true;
		}

		TreasureRaiderStatus treasureRaiderStatus = ActivityUtil.getActivityStatus(player.getPlayerData(), activityId,
				timeCfg.getStageId(), TreasureRaiderStatus.class);
		int todaySearchTimes = treasureRaiderStatus.getTodaySearchTimes();
		TreasureRaiderTimesCfg treaRaiderTimesCfg = TreasureRaiderTimesCfg.getTimesCfgByVipLevel(player.getVipLevel());
		if (todaySearchTimes + searchTimes > treaRaiderTimesCfg.getOneDayTotalTimes()) {
			// 活动次数不足
			player.sendError(protocol.getType(), Status.error.SEARCH_TIME_NOT_ENOUTH);
			return true;
		}

		// 实际支付的次数
		int payTimes = searchTimes;
		int leftFreeTimes = treaRaiderTimesCfg.getOneDayFreeTimes() - todaySearchTimes;
		if (leftFreeTimes > 0) {
			payTimes = searchTimes - leftFreeTimes;
		}

		// 计算实际花费钻石数量
		int payGold = 0;
		if (payTimes > 0) {
			if (payTimes == TIMES_TYPE_TEN) {
				payGold = SysBasicCfg.getInstance().getTreasureRaiderTenPrice();
			} else {
				if (searchTimes == TIMES_TYPE_TEN) {
					payGold = SysBasicCfg.getInstance().getTreasureRaiderTenPrice()
							- leftFreeTimes * SysBasicCfg.getInstance().getTreasureRaiderSinglePrice();
				} else {
					payGold = SysBasicCfg.getInstance().getTreasureRaiderSinglePrice();
				}
			}
		}

		if (payGold > player.getGold()) {
			// 钻石不足
			player.sendError(protocol.getType(), Status.error.GOLD_NOT_ENOUGH_VALUE);
			return true;
		}

		// 扣除钻石
		if (payGold > 0) {
			player.consumeGold(payGold, Action.TREASURE_RAIDER_SEARCH);
			ConsumeItems.valueOf(changeType.CHANGE_GOLD, payGold).pushChange(player);
		}

		// 寻宝奖励
		AwardItems awards = new AwardItems();
		boolean canOpenBox = false;
		//修改10次必中的逻辑
		if (searchTimes == TIMES_TYPE_SINGLE) {
			int nowTotalTimes = treasureRaiderStatus.getTotalSingleSearchTimes() + 1;
			if ((nowTotalTimes % TIMES_TYPE_TEN) == 0 && nowTotalTimes >= TIMES_TYPE_TEN) {
				canOpenBox = true;
			}
		} else {
			canOpenBox = true;
		}

		List<ItemInfo> searchItems = TreasureRaiderDropCfg
				.treasureRaiderDropItems(treasureRaiderStatus.getTodaySearchTimes(), searchTimes);
		awards.addItemInfos(searchItems);

		// 宝箱奖励
		String boxAwardsStr = "";
		if (canOpenBox) {
			int totalOpenTreasureTimes = treasureRaiderStatus.getTotalOpenTreasureTimes();
			List<ItemInfo> boxItems = TreasureRaiderDropCfg.treasureRaiderOpenBoxItems(totalOpenTreasureTimes);

			AwardItems tmp = new AwardItems();
			tmp.addItemInfos(boxItems);
			boxAwardsStr = tmp.toString();
			awards.addItemInfos(boxItems);
			treasureRaiderStatus.setLastBoxAwards(boxAwardsStr);
			treasureRaiderStatus.setTotalOpenTreasureTimes(totalOpenTreasureTimes + 1);
		}
		awards.rewardTakeAffectAndPush(player, Action.TREASURE_RAIDER_SEARCH, 0);
		// 增加寻宝次数
		treasureRaiderStatus.addSearchTimes(searchTimes);

		HPTreasureRaiderInfoSync.Builder builder = HPTreasureRaiderInfoSync.newBuilder();
		todaySearchTimes = treasureRaiderStatus.getTodaySearchTimes();
		int todayLeftFreeTimes = Math.max(treaRaiderTimesCfg.getOneDayFreeTimes() - todaySearchTimes, 0);
		int todayLeftTimes = Math.max(treaRaiderTimesCfg.getOneDayTotalTimes() - todaySearchTimes, 0);
		builder.setLeftTime(timeCfg.calcActivitySurplusTime());
		// 奇遇宝箱
		if (!boxAwardsStr.equals("")) {
			builder.setItems(boxAwardsStr);
		}
		builder.setFreeTreasureTimes(todayLeftFreeTimes);
		builder.setLeftTreasureTimes(todayLeftTimes);
		builder.setOnceCostGold(SysBasicCfg.getInstance().getTreasureRaiderSinglePrice());
		builder.setTenCostGold(SysBasicCfg.getInstance().getTreasureRaiderTenPrice());
		builder.setTotalTimes(treasureRaiderStatus.getTotalSearchTimes());
		if (searchItems != null && searchItems.size() > 0) {
			for (ItemInfo item : searchItems) {
				builder.addReward(item.toString());
			}
		}
		player.sendProtocol(Protocol.valueOf(HP.code.TREASURE_RAIDER_SEARCH_S, builder));
		player.getPlayerData().updateActivity(activityId, timeCfg.getStageId());

		// BI 日志 (freeTimes 本次寻宝开始前今日剩余的免费次数)
		BehaviorLogger.log4Platform(player, Action.TREASURE_RAIDER_SEARCH, Params.valueOf("searchTimes", searchTimes),
				Params.valueOf("freeTimes", searchTimes - payTimes), Params.valueOf("costGold", payGold),
				Params.valueOf("awards", awards.toString()), Params.valueOf("isOpenTreasureBox", awards.toString()));

		return true;
	}
}
