package com.guaji.game.module.activity.grabRedEnvelope;

import java.util.Calendar;
import java.util.Map;

import org.guaji.app.AppObj;
import org.guaji.net.protocol.Protocol;
import org.guaji.net.protocol.IProtocolHandler;
import org.guaji.os.GuaJiTime;

import com.guaji.game.protocol.Activity2.HPGrabRedEnvelopeRet;
import com.guaji.game.ServerData;
import com.guaji.game.config.ActivityTimeCfg;
import com.guaji.game.config.SysBasicCfg;
import com.guaji.game.item.AwardItems;
import com.guaji.game.log.BehaviorLogger;
import com.guaji.game.log.BehaviorLogger.Action;
import com.guaji.game.log.BehaviorLogger.Params;
import com.guaji.game.manager.SnapShotManager;
import com.guaji.game.player.Player;
import com.guaji.game.util.ActivityUtil;
import com.guaji.game.util.GsConst;
import com.guaji.game.protocol.Const;
import com.guaji.game.protocol.HP;
import com.guaji.game.protocol.Player.RoleInfo;
import com.guaji.game.protocol.Snapshot.PlayerSnapshotInfo;
import com.guaji.game.protocol.Status;

public class GrabRedEnvelopeHandler implements IProtocolHandler {
	/**
	 * 上次抢红包是几点
	 */
	private int lastGrabHour = -1;
	
	
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
		
		int curHour = GuaJiTime.getCalendar().get(Calendar.HOUR_OF_DAY);
		if(lastGrabHour == curHour){
			// 下个整点时间你能再抢红包 
			player.sendError(protocol.getType(), Status.error.CUR_HOUR_ALREADY_GRAB);
			return true;
		}
		
		RedEnvelopeStatus personalStatus = ActivityUtil.getActivityStatus(player.getPlayerData(), 
				activityId, timeCfg.getStageId(), RedEnvelopeStatus.class);
		if(personalStatus.getTodayGrabAmount() >= SysBasicCfg.getInstance().getEveryDayGrabRedEnvelope()){
			// 你已达今日最大抢红包次数
			player.sendError(protocol.getType(), Status.error.REACH_MAX_GRAB_TIMES);
			return true;
		}
		
		RedEnvelopeServerStatus serverStatus = ServerData.getInstance().getServerStatus(
				GsConst.ServerStatusId.RED_ENVELOPE, RedEnvelopeServerStatus.class);
		if(serverStatus.getServerRedEnvelopeAmount() <= 0 || !serverStatus.deductServerRedEnvelopeAmount(1)){
			// 红包已被抢光
			player.sendError(protocol.getType(), Status.error.SERVER_RED_ENVELOPE_EMPTY);
			return true;
		}
		
		personalStatus.grabRedEnvelope(1);
		int redEnvelopeGold = RedEnvelopeStatus.randRedEnvelopeGold();
		AwardItems awards = new AwardItems();
		awards.addGold(redEnvelopeGold);
		awards.rewardTakeAffectAndPush(player, Action.GRAB_RED_ENVELOPE, 1);
		
		HPGrabRedEnvelopeRet.Builder ret = HPGrabRedEnvelopeRet.newBuilder();
		
		// 红包信息
		Map.Entry<Integer, String> redEnvelopePlayerWishes = GiveRedEnvelopeHandler.randomOneGiveRedEnvelopePlayerWishes();
		ret.setWishes("null");
		if(redEnvelopePlayerWishes != null){
			int giveRedEnvelopePlayerId = redEnvelopePlayerWishes.getKey();
			PlayerSnapshotInfo.Builder snapshotInfo = SnapShotManager.getInstance().getPlayerSnapShot(giveRedEnvelopePlayerId);
			RoleInfo mainRole = snapshotInfo.getMainRoleInfo();
			ret.setPlayerId(giveRedEnvelopePlayerId);			//红包playerId
			ret.setPlayerName(mainRole.getName());				//红包playerName
			ret.setRoleItemId(mainRole.getItemId());			//红包roleItemId
			ret.setRoleLevel(mainRole.getLevel());				//角色等级
			ret.setWishes(redEnvelopePlayerWishes.getValue());	//祝福语
		}
		ret.setGold(redEnvelopeGold);							//红包开出钻石
		ret.setLeftTimes(timeCfg.calcActivitySurplusTime());
		ret.setServerRedEnvelope(serverStatus.getServerRedEnvelopeAmount());
		ret.setTodayGrabRedEnvelope(personalStatus.getTodayGrabAmount());
		player.sendProtocol(Protocol.valueOf(HP.code.GRAB_RED_ENVELOPE_S, ret));
		
		player.getPlayerData().updateActivity(activityId, timeCfg.getStageId());
		ServerData.getInstance().updateServerData(GsConst.ServerStatusId.RED_ENVELOPE);
		lastGrabHour = GuaJiTime.getCalendar().get(Calendar.HOUR_OF_DAY);
		
		BehaviorLogger.log4Platform(player, Action.GRAB_RED_ENVELOPE, Params.valueOf("grabAmount", 1),
				Params.valueOf("redEnvelopeGold", redEnvelopeGold));
		return true;
	}
}
