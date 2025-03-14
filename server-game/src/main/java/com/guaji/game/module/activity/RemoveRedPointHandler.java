package com.guaji.game.module.activity;

import org.guaji.app.AppObj;
import org.guaji.net.protocol.Protocol;
import org.guaji.net.protocol.IProtocolHandler;

import com.guaji.game.protocol.Activity3.RemoveSpecialRedPoint;
import com.guaji.game.config.ActivityTimeCfg;
import com.guaji.game.entity.ActivityEntity;
import com.guaji.game.module.activity.discountGift.DiscountGiftData;
import com.guaji.game.module.activity.timeLimit.PersonalTimeLimitStatus;
import com.guaji.game.player.Player;
import com.guaji.game.util.ActivityUtil;
import com.guaji.game.protocol.Const;
import com.guaji.game.protocol.Status;

/**
 * 去除特殊活动的红点
 */
public class RemoveRedPointHandler implements IProtocolHandler {

	@Override
	public boolean onProtocol(AppObj appObj, Protocol protocol) {

		Player player = (Player) appObj;
		RemoveSpecialRedPoint request = protocol.parseProtocol(RemoveSpecialRedPoint.getDefaultInstance());
		if (null == request)
			return true;
		int activityId = request.getActivityId();
		switch (activityId) {
		case Const.ActivityId.TIME_LIMIT_PURCHASE_VALUE:
			ActivityTimeCfg timeCfg = ActivityUtil.getCurActivityTimeCfg(activityId);
			if (null == timeCfg)
				break;
			PersonalTimeLimitStatus personalTimeLimitStatus = ActivityUtil.getActivityStatus(player.getPlayerData(), activityId, timeCfg.getStageId(), PersonalTimeLimitStatus.class);
			if (null == personalTimeLimitStatus)
				break;
			if (personalTimeLimitStatus.shouldShowPoint()) {
				personalTimeLimitStatus.setShouldShowPoint(false);
				player.getPlayerData().updateActivity(activityId, timeCfg.getStageId(), false);
			}
			break;
		case Const.ActivityId.DISCOUNT_GIFT_VALUE:
			ActivityTimeCfg activityTimeCfg = ActivityUtil.getCurActivityTimeCfg(activityId);
			if (activityTimeCfg == null) {
				player.sendError(protocol.getType(), Status.error.ACTIVITY_CLOSE);
				break;
			}
			@SuppressWarnings("unchecked")
			ActivityEntity<DiscountGiftData> activityEntity = (ActivityEntity<DiscountGiftData>) player.getPlayerData().getActivityEntity(activityId, activityTimeCfg.getStageId());
			if (null == activityEntity)
				break;
			DiscountGiftData data = activityEntity.getActivityStatus(DiscountGiftData.class);
			if (null == data)
				break;
			if (data.shouldShowPoint()) {
				data.setShouldShowPoint(false);
				activityEntity.notifyUpdate();
			}
			break;
		default:
			break;
		}

		return true;
	}

}
