package com.guaji.game.module.activity.timeLimit;

import org.guaji.app.AppObj;
import org.guaji.config.ConfigManager;
import org.guaji.net.protocol.Protocol;
import org.guaji.os.GuaJiRand;
import org.guaji.net.protocol.IProtocolHandler;

import com.guaji.game.protocol.Activity.HPTimeLimitBuy;
import com.guaji.game.config.ActivityTimeCfg;
import com.guaji.game.config.TimeLimitCfg;
import com.guaji.game.entity.ServerTimeLimitEntity;
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
import com.guaji.game.protocol.Status;

public class TimeLimitBuyHandler implements IProtocolHandler {

	@Override
	public boolean onProtocol(AppObj appObj, Protocol protocol) {
		HPTimeLimitBuy params = protocol.parseProtocol(HPTimeLimitBuy.getDefaultInstance());
		int cfgId = params.getCfgId();
		int count = params.getCount();

		Player player = (Player) appObj;
		// 检测活动是否开放(限时限购活动)
		int activityId = Const.ActivityId.TIME_LIMIT_PURCHASE_VALUE;
		ActivityTimeCfg timeCfg = ActivityUtil.getCurActivityTimeCfg(activityId);
		if (timeCfg == null) {
//			
//			//七夕限时限购
//			int activityId2  = Const.ActivityId.LIMIT_RECHARGE_VALUE;
//			ActivityTimeCfg timeCfg2 = ActivityUtil.getCurActivityTimeCfg(activityId2);
//			if(timeCfg2 == null)
//			{
			// 活动已关闭
			player.sendError(protocol.getType(), Status.error.ACTIVITY_CLOSE);
			return true;
//			}
		}

		PersonalTimeLimitStatus personalTimeLimitStatus = ActivityUtil.getActivityStatus(player.getPlayerData(),
				activityId, timeCfg.getStageId(), PersonalTimeLimitStatus.class);

		if (personalTimeLimitStatus == null) {
			player.sendError(protocol.getType(), Status.error.DATA_NOT_FOUND_VALUE);
			return true;
		}

		ServerTimeLimitEntity timeLimitEntity = TimeLimitManager.getInstance().getCurTimeLimitEntity();

		if (timeLimitEntity == null) {
			player.sendError(protocol.getType(), Status.error.DATA_NOT_FOUND_VALUE);
			return true;
		}

		if (timeLimitEntity.getStageId() != timeCfg.getStageId()) {
			player.sendError(protocol.getType(), Status.error.DATA_NOT_FOUND_VALUE);
			TimeLimitManager.getInstance().refresh();
			return true;
		}

		TimeLimitCfg timeLimitCfg = ConfigManager.getInstance().getConfigByKey(TimeLimitCfg.class, cfgId);
		if (timeLimitCfg == null) {
			player.sendError(protocol.getType(), Status.error.CONFIG_NOT_FOUND_VALUE);
			return true;
		}

		if (player.getVipLevel() < timeLimitCfg.getVipLimit()) {
			player.sendError(protocol.getType(), Status.error.VIP_NOT_ENOUGH_VALUE);
			return true;
		}

		if (player.getLevel() < timeLimitCfg.getLevelLimit()) {
			player.sendError(protocol.getType(), Status.error.LEVEL_NOT_LIMIT_VALUE);
			return true;
		}

		// 有限制
		if (timeLimitCfg.getPersonalLimitType() == 2) {
			if (personalTimeLimitStatus.getTodayBuyTimes(cfgId) + count > timeLimitCfg.getPersonalLimit()) {
				// 个人今日领取限制
				player.sendError(protocol.getType(), Status.error.TIME_LIMIT_TODAY_BUY_TIMES_LIMIT);
				return true;
			}
		}

		if (timeLimitCfg.getPersonalLimitType() == 1) {
			if (personalTimeLimitStatus.getTotalBuyTimes(cfgId) + count > timeLimitCfg.getPersonalLimit()) {
				// 个人全服领取限制
				player.sendError(protocol.getType(), Status.error.TIME_LIMIT_ALL_BUY_TIMES_LIMIT);
				return true;
			}
		}

		if (timeLimitCfg.getServerLimitType() == 2) {
			if (timeLimitEntity.getTodayBuyTimes(cfgId) + count > timeLimitCfg.getServerLimit()) {
				// 全服今日领取限制
				player.sendError(protocol.getType(), Status.error.SERVER_TIME_LIMIT_TODAY_BUY_TIMES_LIMIT);
				return true;
			}
		}

		if (timeLimitCfg.getServerLimitType() == 1) {
			if (timeLimitEntity.getTotalBuyTimes(cfgId) + count > timeLimitCfg.getServerLimit()) {
				// 全服领取限制
				player.sendError(protocol.getType(), Status.error.SERVER_TIME_LIMIT_ALL_BUY_TIMES_LIMIT);
				return true;
			}
		}

		int needGold = timeLimitCfg.getPrice() * count;
		if (player.getGold() < needGold) {
			// 钻石不足
			player.sendError(protocol.getType(), Status.error.GOLD_NOT_ENOUGH_VALUE);
			return true;
		}

		ConsumeItems consumeItems = ConsumeItems.valueOf(changeType.CHANGE_GOLD, needGold);
		consumeItems.consumeTakeAffect(player, Action.TIME_LIMIT_BUY);

		AwardItems awardItems = new AwardItems();
		for (int i = 0; i < count; i++) {
			awardItems.addItemInfos(ItemInfo.valueListOf(timeLimitCfg.getItems()));
		}


		awardItems.rewardTakeAffectAndPush(player, Action.TIME_LIMIT_BUY, 2);

		personalTimeLimitStatus.addTodayBuyTime(cfgId, count);

		player.getPlayerData().updateActivity(Const.ActivityId.TIME_LIMIT_PURCHASE_VALUE, timeCfg.getStageId());

		timeLimitEntity.addTodayBuyTime(cfgId, count);

		timeLimitEntity.notifyUpdate(false);

		TimeLimitPurchaseInfoHandler.pushTimeLimitInfo(player, personalTimeLimitStatus, timeLimitEntity, timeCfg);

		BehaviorLogger.log4Platform(player, Action.TIME_LIMIT_BUY, Params.valueOf("giftId", timeLimitCfg.getId()),
				Params.valueOf("price", timeLimitCfg.getPrice()), Params.valueOf("count", count),
				Params.valueOf("price", timeLimitCfg.getPrice()));

		return true;
	}

}
