package com.guaji.game.module.activity.turntable;

import org.guaji.app.AppObj;
import org.guaji.log.Log;
import org.guaji.net.protocol.Protocol;
import org.guaji.net.protocol.IProtocolHandler;

import com.guaji.game.protocol.Activity3.TurntableExchangeRes;
import com.guaji.game.config.ActivityTimeCfg;
import com.guaji.game.player.Player;
import com.guaji.game.util.ActivityUtil;
import com.guaji.game.protocol.Const;
import com.guaji.game.protocol.HP;
import com.guaji.game.protocol.Status;

/**
 * 大转盘活动碎片兑换请求
 */
public class TurntableExchangeSyncHandler implements IProtocolHandler {

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

		// 返回消息
		TurntableExchangeRes.Builder response = TurntableManager.getExchangeBuilders(status, timeCfg);
		Log.logPrintln(response.build().toString());
		player.sendProtocol(Protocol.valueOf(HP.code.TURNTABLE_EXCHANGE_S_VALUE, response));
		return true;
	}

}
