package com.guaji.game.module.activity.obon;

import org.guaji.app.AppObj;
import org.guaji.net.protocol.Protocol;
import org.guaji.net.protocol.IProtocolHandler;

import com.guaji.game.protocol.Activity3.ObonReq;
import com.guaji.game.config.ActivityTimeCfg;
import com.guaji.game.config.ObonTimesCfg;
import com.guaji.game.player.Player;
import com.guaji.game.util.ActivityUtil;
import com.guaji.game.protocol.Const;
import com.guaji.game.protocol.Status;

/**
 * 鬼节活动操作类
 */
public class ObonHandler implements IProtocolHandler {

	@Override
	public boolean onProtocol(AppObj appObj, Protocol protocol) {
		Player player = (Player) appObj;
		// 检测活动是否开放
		int activityId = Const.ActivityId.OBON_VALUE;
		ActivityTimeCfg timeCfg = ActivityUtil.getCurActivityTimeCfg(activityId);
		if (timeCfg == null) {
			player.sendError(protocol.getType(), Status.error.ACTIVITY_CLOSE);
			return true;
		}
		// 检测配置
		ObonTimesCfg cfg = ObonTimesCfg.getTimesCfgByVipLevel(player.getVipLevel());
		if (cfg == null) {
			player.sendError(protocol.getType(), Status.error.CONFIG_NOT_FOUND);
			return true;
		}

		// 获取活动信息
		ObonStatus status = ActivityUtil.getActivityStatus(player.getPlayerData(), activityId, timeCfg.getStageId(), ObonStatus.class);
		if (null == status) {
			player.sendError(protocol.getType(), Status.error.DATA_NOT_FOUND);
			return true;
		}

		ObonReq req = protocol.parseProtocol(ObonReq.getDefaultInstance());
		int type = req.getType();
		int retCode = 0;
		switch (type) {
		case 0:
			ObonManager.sync(player);
			break;
		case 1:
			retCode = ObonManager.draw(player, timeCfg, status, ObonManager.TIMES_TYPE_SINGLE);
			break;
		case 2:
			retCode = ObonManager.draw(player, timeCfg, status, ObonManager.TIMES_TYPE_TEN);
			break;
		case 3:
			int stage = req.getStage();
			retCode = ObonManager.getGift(player, timeCfg, status, stage);
			break;
		default:
			retCode = Status.error.PARAMS_INVALID_VALUE;
			break;
		}

		if (retCode != 0) {
			player.sendError(protocol.getType(), retCode);
		}
		return true;
	}
}
