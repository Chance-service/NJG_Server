package com.guaji.game.module.activity.roulette;

import org.guaji.app.AppObj;
import org.guaji.net.protocol.Protocol;
import org.guaji.net.protocol.IProtocolHandler;

import com.guaji.game.protocol.Activity.HPRouletteInfoRet;
import com.guaji.game.config.ActivityTimeCfg;
import com.guaji.game.player.Player;
import com.guaji.game.util.ActivityUtil;
import com.guaji.game.protocol.Const;
import com.guaji.game.protocol.HP;
import com.guaji.game.protocol.Status;

/**
 * 疯狂转轮盘
 */
public class RouletteInfoHandler implements IProtocolHandler {
	
	@Override
	public boolean onProtocol(AppObj appObj, Protocol protocol) {
		Player player = (Player) appObj;
		// 检测活动是否开放
		int activityId = Const.ActivityId.CRAZY_ROULETTE_VALUE;
		ActivityTimeCfg timeCfg = ActivityUtil.getCurActivityTimeCfg(activityId);
		if (timeCfg == null) {
			// 活动已关闭
			player.sendError(protocol.getType(), Status.error.ACTIVITY_CLOSE);
			return true;
		}
		
		RouletteStatus status = ActivityUtil.getActivityStatus(player.getPlayerData(), activityId, timeCfg.getStageId(), RouletteStatus.class);
		HPRouletteInfoRet.Builder ret = HPRouletteInfoRet.newBuilder();
		ret.setLeftTime(timeCfg.calcActivitySurplusTime());
		ret.setRouletteLeftTimes(status.getRouletteLeftTimes());
		ret.setCurCredits(status.getCurCredits());
		ret.setTodayAccRechargeNum(status.getTodayRechargeNum());
		player.sendProtocol(Protocol.valueOf(HP.code.ROULETTE_INFO_S, ret));
		return true;
	}
}
