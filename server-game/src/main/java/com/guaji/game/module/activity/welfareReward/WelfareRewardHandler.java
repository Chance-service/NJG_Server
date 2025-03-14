package com.guaji.game.module.activity.welfareReward;

import org.guaji.app.AppObj;
import org.guaji.net.protocol.Protocol;
import org.guaji.net.protocol.IProtocolHandler;

import com.guaji.game.protocol.Activity3.WelfareRewardReq;
import com.guaji.game.config.ActivityTimeCfg;
import com.guaji.game.player.Player;
import com.guaji.game.util.ActivityUtil;
import com.guaji.game.protocol.Const;
import com.guaji.game.protocol.Status;

/**
 * 天降元宝活动
 */
public class WelfareRewardHandler implements IProtocolHandler {

	private static final int HANDLER_SYNC = 0;
	private static final int HANDLER_PLAY = 1;

	@Override
	public boolean onProtocol(AppObj appObj, Protocol protocol) {
		Player player = (Player) appObj;
		// 检测活动是否开放
		int activityId = Const.ActivityId.WELFARE_REWARD_VALUE;
		ActivityTimeCfg timeCfg = ActivityUtil.getCurActivityTimeCfg(activityId);
		if (timeCfg == null) {
			// 活动已关闭
			player.sendError(protocol.getType(), Status.error.ACTIVITY_CLOSE);
			return true;
		}

		WelfareRewardStatus status = ActivityUtil.getActivityStatus(player.getPlayerData(), activityId, timeCfg.getStageId(), WelfareRewardStatus.class);
		if (status == null) {
			player.sendError(protocol.getType(), Status.error.DATA_NOT_FOUND);
			return true;
		}

		// 解析请求
		WelfareRewardReq request = protocol.parseProtocol(WelfareRewardReq.getDefaultInstance());
		int type = request.getType();
		switch (type) {
		case HANDLER_SYNC:
			WelfareRewardManager.sync(player, status, timeCfg);
			break;
		case HANDLER_PLAY:
			if (status.canPlay())
				WelfareRewardManager.play(player, status, timeCfg);
			else
				player.sendError(protocol.getType(), Status.error.PARAMS_INVALID_VALUE);
			break;
		default:
			player.sendError(protocol.getType(), Status.error.PARAMS_INVALID_VALUE);
			break;
		}
		return true;
	}

}
