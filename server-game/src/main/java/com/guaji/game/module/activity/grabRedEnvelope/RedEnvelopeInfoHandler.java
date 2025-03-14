package com.guaji.game.module.activity.grabRedEnvelope;

import org.guaji.app.AppObj;
import org.guaji.net.protocol.Protocol;
import org.guaji.net.protocol.IProtocolHandler;

import com.guaji.game.protocol.Activity2.HPRedEnvelopeInfoRet;
import com.guaji.game.ServerData;
import com.guaji.game.config.ActivityTimeCfg;
import com.guaji.game.player.Player;
import com.guaji.game.util.ActivityUtil;
import com.guaji.game.util.GsConst;
import com.guaji.game.protocol.Const;
import com.guaji.game.protocol.HP;
import com.guaji.game.protocol.Status;

public class RedEnvelopeInfoHandler implements IProtocolHandler {
	@Override
	public boolean onProtocol(AppObj appObj, Protocol protocol) {
		Player player = (Player) appObj;
		// 检测活动是否开放
		int activityId = Const.ActivityId.GRAB_RED_ENVELOPE_VALUE;
		ActivityTimeCfg timeCfg = ActivityUtil.getCurActivityTimeCfg(activityId);
		if(timeCfg == null){
			// 活动已关闭
			player.sendError(protocol.getType(), Status.error.ACTIVITY_CLOSE);
			return true;
		}
		
		RedEnvelopeStatus personalStatus = ActivityUtil.getActivityStatus(player.getPlayerData(), 
				activityId, timeCfg.getStageId(), RedEnvelopeStatus.class);
		
		RedEnvelopeServerStatus serverStatus = ServerData.getInstance().getServerStatus(
				GsConst.ServerStatusId.RED_ENVELOPE, RedEnvelopeServerStatus.class);
		if(serverStatus.getStageId() != timeCfg.getStageId()){
			serverStatus.reset(timeCfg.getStageId());
		}
		
		HPRedEnvelopeInfoRet.Builder ret = HPRedEnvelopeInfoRet.newBuilder();
		ret.setMyRedEnvelope(personalStatus.getMyRedEnvelope());
		ret.setTodaySysRedEnvelopeStatus(personalStatus.getToaydSysRedEnvelopeStatus());
		ret.setTodayGrabRedEnvelope(personalStatus.getTodayGrabAmount());
		
		ret.setServerRedEnvelope(serverStatus.getServerRedEnvelopeAmount());
		ret.setPersonalRechargeNum(personalStatus.getTotalRechargeAmount());
		ret.setLeftTimes(timeCfg.calcActivitySurplusTime());
		player.sendProtocol(Protocol.valueOf(HP.code.RED_ENVELOPE_INFO_S, ret));
		
		player.getPlayerData().updateActivity(activityId, timeCfg.getStageId());
		ServerData.getInstance().updateServerData(GsConst.ServerStatusId.RED_ENVELOPE);
		return true;
	}
}
