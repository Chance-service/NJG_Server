package com.guaji.game.module.activity.turntable;

import org.guaji.app.AppObj;
import org.guaji.net.protocol.Protocol;
import org.guaji.net.protocol.IProtocolHandler;

import com.guaji.game.protocol.Activity3.TurntableReq;
import com.guaji.game.config.ActivityTimeCfg;
import com.guaji.game.player.Player;
import com.guaji.game.util.ActivityUtil;
import com.guaji.game.protocol.Const;
import com.guaji.game.protocol.Status;

/**
 * 大转盘活动抽奖
 */
public class TurntableHandler implements IProtocolHandler {

	private static final int OPERATE_SYNC = 0;// 同步
	private static final int OPERATE_ONCE_DRAW = 1;// 单抽
	private static final int OPERATE_COMBO_DRAW = 2;// 连抽
	private static final int OPERATE_OPEN_BOX = 3;// 开启宝箱

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

		// 获取活动数据
		int stageId = timeCfg.getStageId();
		TurntableStatus status = ActivityUtil.getActivityStatus(player.getPlayerData(), activityId, stageId, TurntableStatus.class);
		if (null == status) {
			player.sendError(protocol.getType(), Status.error.DATA_NOT_FOUND);
			return true;
		}

		// 解析请求参数
		TurntableReq request = protocol.parseProtocol(TurntableReq.getDefaultInstance());
		int type = request.getType();

		// 业务分支处理
		switch (type) {
		case OPERATE_SYNC:
			TurntableManager.sync(player, timeCfg, status);
			break;
		case OPERATE_ONCE_DRAW:
			TurntableManager.draw(player, timeCfg, status, TurntableManager.DRAW_TYPE_ONCE);
			break;
		case OPERATE_COMBO_DRAW:
			TurntableManager.draw(player, timeCfg, status, TurntableManager.DRAW_TYPE_COMBO);
			break;
		case OPERATE_OPEN_BOX:
			int boxId = request.getBoxId();
			TurntableManager.openBox(player, timeCfg, status, boxId);
			break;
		default:
			player.sendError(protocol.getType(), Status.error.PARAMS_INVALID_VALUE);
			break;
		}
		return true;
	}

}
