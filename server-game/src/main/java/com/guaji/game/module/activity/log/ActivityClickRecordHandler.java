package com.guaji.game.module.activity.log;

import org.guaji.app.AppObj;
import org.guaji.net.protocol.Protocol;
import org.guaji.net.protocol.IProtocolHandler;

import com.guaji.game.config.ActivityTimeCfg;
import com.guaji.game.log.BehaviorLogger;
import com.guaji.game.log.BehaviorLogger.Action;
import com.guaji.game.log.BehaviorLogger.Params;
import com.guaji.game.player.Player;
import com.guaji.game.util.ActivityUtil;
import com.guaji.game.protocol.ActionLog.HPActionRecord;

/**
 * 活动入口按钮点击记录（老活动曝光需求）
 */
public class ActivityClickRecordHandler implements IProtocolHandler {
	@Override
	public boolean onProtocol(AppObj appObj, Protocol protocol) {
		Player player = (Player) appObj;
		HPActionRecord request = protocol.parseProtocol(HPActionRecord.getDefaultInstance());
		// 检测活动是否开放
		int activityId = request.getActivityId();
		ActivityTimeCfg timeCfg = ActivityUtil.getCurActivityTimeCfg(activityId);
		if (timeCfg == null) {
			// 活动已关闭
			// player.sendError(protocol.getType(), Status.error.ACTIVITY_CLOSE);
			return true;
		}
		int stageId = timeCfg.getStageId();
		if (!timeCfg.isActiveToEnd()) {
			// 不在starttime和endtime区间内
			// player.sendError(protocol.getType(), Status.error.ACTIVITY_CLOSE);
			return true;
		}
		int actionType = request.getActionType();
		// 记录日志
		BehaviorLogger.log4Platform(player, Action.ACTIVITY_ENTRY_RECORD, Params.valueOf("activtyId", activityId), Params.valueOf("stageId", stageId),
				Params.valueOf("actionType", actionType));
		return true;
	}

}
