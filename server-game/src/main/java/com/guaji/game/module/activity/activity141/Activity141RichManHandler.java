package com.guaji.game.module.activity.activity141;

import org.guaji.app.AppObj;
import org.guaji.net.protocol.Protocol;
import org.guaji.net.protocol.IProtocolHandler;

import com.guaji.game.protocol.Activity3.Activity141RichManReq;
import com.guaji.game.config.ActivityTimeCfg;
import com.guaji.game.player.Player;
import com.guaji.game.util.ActivityUtil;
import com.guaji.game.protocol.Const;
import com.guaji.game.protocol.Status;

/**
 * 万圣节活动抽奖
 */
public class Activity141RichManHandler implements IProtocolHandler {

	private static final int OPERATE_SYNC = 0;// 同步
	private static final int OPERATE_ROLL_DICE = 1;// 擲骰


	@Override
	public boolean onProtocol(AppObj appObj, Protocol protocol) {

		// 检测活动是否开放
		Player player = (Player) appObj;
		int activityId = Const.ActivityId.ACTIVITY141_RICHMAN_VALUE;
		ActivityTimeCfg timeCfg = ActivityUtil.getCurActivityTimeCfg(activityId);
		if (timeCfg == null) {
			player.sendError(protocol.getType(), Status.error.ACTIVITY_CLOSE);
			return true;
		}

		// 获取活动数据
		int stageId = timeCfg.getStageId();
		Activity141Status status = ActivityUtil.getActivityStatus(player.getPlayerData(), activityId, stageId, Activity141Status.class);
		if (null == status) {
			player.sendError(protocol.getType(), Status.error.DATA_NOT_FOUND);
			return true;
		}

		// 解析请求参数
		Activity141RichManReq request = protocol.parseProtocol(Activity141RichManReq.getDefaultInstance());
		int type = request.getType();

		// 业务分支处理
		switch (type) {
		case OPERATE_SYNC:
			Activity141RichManManager.sync(player, timeCfg, status);
			break;
		case OPERATE_ROLL_DICE:
			Activity141RichManManager.roll(player, timeCfg, status);
			break;
		default:
			player.sendError(protocol.getType(), Status.error.PARAMS_INVALID_VALUE);
			break;
		}
		return true;
	}

}
