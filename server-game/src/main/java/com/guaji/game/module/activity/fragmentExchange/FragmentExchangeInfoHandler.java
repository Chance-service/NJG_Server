package com.guaji.game.module.activity.fragmentExchange;

import org.guaji.app.AppObj;
import org.guaji.net.protocol.Protocol;
import org.guaji.net.protocol.IProtocolHandler;

import com.guaji.game.protocol.Activity2.SyncFragmentExchangeRes;
import com.guaji.game.config.ActivityTimeCfg;
import com.guaji.game.player.Player;
import com.guaji.game.util.ActivityUtil;
import com.guaji.game.protocol.Const;
import com.guaji.game.protocol.HP;
import com.guaji.game.protocol.Status;

/**
 * 万能碎片限时兑换信息同步
 */
public class FragmentExchangeInfoHandler implements IProtocolHandler {

	@Override
	public boolean onProtocol(AppObj appObj, Protocol protocol) {

		Player player = (Player) appObj;
		int activityId = Const.ActivityId.FRAGMENT_EXCHANGE_VALUE;
		ActivityTimeCfg timeCfg = ActivityUtil.getCurActivityTimeCfg(activityId);
		if (timeCfg == null) {
			// 活动已关闭
			player.sendError(HP.code.SYNC_FRAGMENT_EXCHANGE_C_VALUE, Status.error.ACTIVITY_CLOSE);
			return true;
		}
		// 提取玩家数据
		FragmentExchangeStatus fragmentExchangeStatus = ActivityUtil.getActivityStatus(player.getPlayerData(), activityId, timeCfg.getStageId(), FragmentExchangeStatus.class);
		if (fragmentExchangeStatus == null) {
			player.sendError(HP.code.SYNC_FRAGMENT_EXCHANGE_C_VALUE, Status.error.DATA_NOT_FOUND_VALUE);
			return true;
		}

		SyncFragmentExchangeRes.Builder response = SyncFragmentExchangeRes.newBuilder();
		// 计算剩余时间
		long surplusTime = timeCfg.calcActivitySurplusTime();
		response.setSurplusTime(surplusTime);
		player.sendProtocol(Protocol.valueOf(HP.code.SYNC_FRAGMENT_EXCHANGE_S_VALUE, response));
		// 如果从来没有进过该活动,则改变状态
		if (!fragmentExchangeStatus.isActive()) {
			fragmentExchangeStatus.setActive(true);
			player.getPlayerData().updateActivity(activityId, timeCfg.getStageId(), true);
		}
		return true;
	}
}
