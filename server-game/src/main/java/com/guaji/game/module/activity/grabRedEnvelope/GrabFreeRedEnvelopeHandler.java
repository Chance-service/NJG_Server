package com.guaji.game.module.activity.grabRedEnvelope;

import org.guaji.app.AppObj;
import org.guaji.net.protocol.Protocol;
import org.guaji.net.protocol.IProtocolHandler;

import com.guaji.game.protocol.Activity2.HPGrabFreeRedEnvelopeRet;
import com.guaji.game.ServerData;
import com.guaji.game.config.ActivityTimeCfg;
import com.guaji.game.item.AwardItems;
import com.guaji.game.log.BehaviorLogger.Action;
import com.guaji.game.player.Player;
import com.guaji.game.util.ActivityUtil;
import com.guaji.game.util.GsConst;
import com.guaji.game.protocol.Const;
import com.guaji.game.protocol.HP;
import com.guaji.game.protocol.Status;

public class GrabFreeRedEnvelopeHandler implements IProtocolHandler {
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
		if(!personalStatus.recvToadySysRedEnvelope()){
			// 今日系统红包已经领取
			player.sendError(protocol.getType(), Status.error.TODAY_FREE_RED_ENVELOPE_GOT);
			return true;
		}
		
		int redEnvelopeGold = RedEnvelopeStatus.randRedEnvelopeGold();
		AwardItems awards = new AwardItems();
		awards.addGold(redEnvelopeGold);
		awards.rewardTakeAffectAndPush(player, Action.SYS_RED_ENVELOPE, 1);
		
		HPGrabFreeRedEnvelopeRet.Builder ret = HPGrabFreeRedEnvelopeRet.newBuilder();
		ret.setItemCfg(awards.toString());
		ret.setTodaySysRedEnvelopeStatus(personalStatus.getToaydSysRedEnvelopeStatus());
		player.sendProtocol(Protocol.valueOf(HP.code.GRAB_FREE_RED_ENVELOPE_S, ret));
		
		player.getPlayerData().updateActivity(activityId, timeCfg.getStageId());
		ServerData.getInstance().updateServerData(GsConst.ServerStatusId.RED_ENVELOPE);
		return true;
	}
}
