package com.guaji.game.module.activity.activity143;

import org.guaji.app.AppObj;
import org.guaji.net.protocol.Protocol;
import org.guaji.net.protocol.IProtocolHandler;

import com.guaji.game.protocol.Activity3.Activity143PirateReq;
import com.guaji.game.config.ActivityTimeCfg;
import com.guaji.game.player.Player;
import com.guaji.game.util.ActivityUtil;
import com.guaji.game.protocol.Const;
import com.guaji.game.protocol.Status;

/**
 * 万圣节活动抽奖
 */
public class Activity143PirateHandler implements IProtocolHandler {

	private static final int OPERATE_SYNC = 0;// 同步
	private static final int OPERATE_OPEN_BOX = 1;// 開箱
	private static final int OPERATE_Get_REWARD = 2; //	領獎
	private static final int OPERATE_GIVEUP = 3; 	//	放棄


	@Override
	public boolean onProtocol(AppObj appObj, Protocol protocol) {

		// 检测活动是否开放
		Player player = (Player) appObj;
		int activityId = Const.ActivityId.ACTIVITY143_PIRATE_VALUE;
		ActivityTimeCfg timeCfg = ActivityUtil.getCurActivityTimeCfg(activityId);
		if (timeCfg == null) {
			player.sendError(protocol.getType(), Status.error.ACTIVITY_CLOSE);
			return true;
		}

		// 获取活动数据
		int stageId = timeCfg.getStageId();
		Activity143Status status = ActivityUtil.getActivityStatus(player.getPlayerData(), activityId, stageId, Activity143Status.class);
		if (null == status) {
			player.sendError(protocol.getType(), Status.error.DATA_NOT_FOUND);
			return true;
		}

		// 解析请求参数
		Activity143PirateReq request = protocol.parseProtocol(Activity143PirateReq.getDefaultInstance());
		int type = request.getType();
		int ransom = request.getRansom();

		// 业务分支处理
		switch (type) {
		case OPERATE_SYNC:
			Activity143PirateManager.sync(player, timeCfg, status);
			break;
		case OPERATE_OPEN_BOX:
			Activity143PirateManager.openbox(player, timeCfg, status,ransom);
			break;
		case OPERATE_Get_REWARD:
			Activity143PirateManager.takegift(player, timeCfg, status);
			break;
		case OPERATE_GIVEUP:
			Activity143PirateManager.giveup(player, timeCfg, status);
			break;
		default:
			player.sendError(protocol.getType(), Status.error.PARAMS_INVALID_VALUE);
			break;
		}
		return true;
	}

}
