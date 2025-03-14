package com.guaji.game.module.activity.halloween;

import org.guaji.app.AppObj;
import org.guaji.net.protocol.Protocol;
import org.guaji.net.protocol.IProtocolHandler;

import com.guaji.game.protocol.Activity3.HalloweenReq;
import com.guaji.game.config.ActivityTimeCfg;
import com.guaji.game.player.Player;
import com.guaji.game.util.ActivityUtil;
import com.guaji.game.protocol.Const;
import com.guaji.game.protocol.Status;

/**
 * 万圣节活动抽奖
 */
public class HalloweenHandler implements IProtocolHandler {

	private static final int OPERATE_SYNC = 0;// 同步
	private static final int OPERATE_ONCE_DRAW = 1;// 单抽
	private static final int OPERATE_COMBO_DRAW = 2;// 连抽
	private static final int OPERATE_EXCHANGE = 3;// 兑换

	@Override
	public boolean onProtocol(AppObj appObj, Protocol protocol) {

		// 检测活动是否开放
		Player player = (Player) appObj;
		int activityId = Const.ActivityId.HALLOWEEN_VALUE;
		ActivityTimeCfg timeCfg = ActivityUtil.getCurActivityTimeCfg(activityId);
		if (timeCfg == null) {
			player.sendError(protocol.getType(), Status.error.ACTIVITY_CLOSE);
			return true;
		}

		// 获取活动数据
		int stageId = timeCfg.getStageId();
		HalloweenStatus status = ActivityUtil.getActivityStatus(player.getPlayerData(), activityId, stageId, HalloweenStatus.class);
		if (null == status) {
			player.sendError(protocol.getType(), Status.error.DATA_NOT_FOUND);
			return true;
		}

		// 解析请求参数
		HalloweenReq request = protocol.parseProtocol(HalloweenReq.getDefaultInstance());
		int type = request.getType();

		// 业务分支处理
		switch (type) {
		case OPERATE_SYNC:
			HalloweenManager.sync(player, timeCfg, status);
			break;
		case OPERATE_ONCE_DRAW:
			HalloweenManager.draw(player, timeCfg, status, HalloweenManager.DRAW_TYPE_ONCE);
			break;
		case OPERATE_COMBO_DRAW:
			HalloweenManager.draw(player, timeCfg, status, HalloweenManager.DRAW_TYPE_COMBO);
			break;
		case OPERATE_EXCHANGE:
			int exchangeId = request.getExchangeId();// 兑换ID
			int multiple = request.getMultiple();// 兑换倍数
			HalloweenManager.exchange(player, timeCfg, status, exchangeId, multiple);
			break;
		default:
			player.sendError(protocol.getType(), Status.error.PARAMS_INVALID_VALUE);
			break;
		}
		return true;
	}

}
