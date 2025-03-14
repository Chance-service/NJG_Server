package com.guaji.game.module.activity.activity180;

import org.guaji.app.AppObj;
import org.guaji.config.ConfigManager;
import org.guaji.net.protocol.IProtocolHandler;
import org.guaji.net.protocol.Protocol;
import org.guaji.os.GuaJiTime;

import com.guaji.game.config.ActivityTimeCfg;
import com.guaji.game.config.FreeSummon180Cfg;
import com.guaji.game.config.FunctionUnlockCfg;
import com.guaji.game.item.AwardItems;
import com.guaji.game.log.BehaviorLogger;
import com.guaji.game.log.BehaviorLogger.Action;
import com.guaji.game.log.BehaviorLogger.Params;
import com.guaji.game.log.BehaviorLogger.Source;
import com.guaji.game.player.Player;
import com.guaji.game.protocol.Activity5.FreeSummonReq;
import com.guaji.game.protocol.Activity5.FreeSummonResp;
import com.guaji.game.protocol.Const;
import com.guaji.game.protocol.HP;
import com.guaji.game.protocol.Status;
import com.guaji.game.util.ActivityUtil;
import com.guaji.game.util.TapDBUtil.TapDBSource;

/**
 * 免費召喚領取協定
 */
public class Activity180Handler implements IProtocolHandler {
	static final int Sync = 0;
	static final int Get_Rward = 1;

	@Override
	public boolean onProtocol(AppObj appObj, Protocol protocol) {
		// TODO Auto-generated method stub
		
		Player player = (Player) appObj;

		
		// 检测活动是否开放
		int activityId = Const.ActivityId.ACTIVITY180_Free_Summom_VALUE;
		ActivityTimeCfg timeConfig = ActivityUtil.getCurActivityTimeCfg(activityId);
		if (timeConfig == null || player == null) {
			// 活动已关闭
			player.sendError(protocol.getType(), Status.error.ACTIVITY_CLOSE);
			return true;
		}

		// 解析接受到的数据
		FreeSummonReq request = protocol.parseProtocol(FreeSummonReq.getDefaultInstance());

		int action = request.getAction();
				
		// 数据人错误
		if ( action < 0 || action > 1) {
			// 无效参数
			player.sendError(protocol.getType(), Status.error.PARAMS_INVALID_VALUE);
			return true;
		}
		
		if (action != Sync) {
			if (!FunctionUnlockCfg.checkUnlock(player,null,Const.FunctionType.free1500_Unlock)){
				player.sendError(protocol.getType(), Status.error.CONDITION_NOT_ENOUGH);
				return true;
			}
		}
				
		int stageId = timeConfig.getStageId();
		Activity180Status status = ActivityUtil.getActivityStatus(player.getPlayerData(), activityId, stageId, Activity180Status.class);
		
		if (status == null) {
			player.sendError(protocol.getType(), Status.error.DATA_NOT_FOUND_VALUE);
			return true;
		}
		
		if (FunctionUnlockCfg.checkUnlock(player,null,Const.FunctionType.free1500_Unlock)){
			//觸發免費召喚開始時間
			if (status.getTodayCount() == -1) {
				status.setStartDate(GuaJiTime.getCalendar().getTime());
				player.getPlayerData().updateActivity(activityId, stageId);
			}
		}
		
		// 业务分支处理
		switch (action) {
		case Sync:
			SyncInfo(action,player,status);
			break;
		case  Get_Rward:
			GetRward(protocol,action,player,status);
			break;
		default:
			player.sendError(protocol.getType(), Status.error.PARAMS_INVALID_VALUE);
			break;
		}
		return true;
	}
	
	private static void GetRward(Protocol protocol,int action,Player player, Activity180Status status) {
			
		if (status.getTodayCount() == -1) {
			// 活動未觸發
			player.sendError(protocol.getType(), Status.error.ACTIVITY_CLOSE_VALUE);
			return;
		}
		
		if (status.getTakeDay() >= status.getTodayCount()) {
			// 今天已經領了
			player.sendError(protocol.getType(), Status.error.PARAMS_INVALID_VALUE);
			return;
		}
		
		if (status.getTakeDay() >= FreeSummon180Cfg.getMaxDay()) {
			// 已經全領了
			player.sendError(protocol.getType(), Status.error.PARAMS_INVALID_VALUE);
			return;
		}
		
		int BefortakeDay = status.getTakeDay();
		
		String allaward = "";
		int takeday = 0;
		for (int i = (status.getTakeDay()+1) ; i <= status.getTodayCount() ; i++) {
			FreeSummon180Cfg cfg = ConfigManager.getInstance().getConfigByKey(FreeSummon180Cfg.class, i);
			if (cfg == null) {
				break;
			}
			takeday = i;
			if (allaward.isEmpty()) {
				allaward = cfg.getAwards();
			} else {
				allaward = allaward+","+cfg.getAwards();
			}
		}
		
		status.setTakeDay(takeday);
				
		int activityId = Const.ActivityId.ACTIVITY180_Free_Summom_VALUE;
		ActivityTimeCfg timeCfg = ActivityUtil.getCurActivityTimeCfg(activityId);
		player.getPlayerData().updateActivity(activityId, timeCfg.getStageId());
		
		// 下发奖励
		AwardItems awards = AwardItems.valueOf(allaward);
		awards.rewardTakeAffectAndPush(player, Action.ACTIVITY180_FREE_SUMMON, 2,TapDBSource.Activity180_SUMMON,Params.valueOf("activityId", activityId),
				Params.valueOf("BefortakeDay", BefortakeDay),
				Params.valueOf("takeDay",status.getTakeDay()),
				Params.valueOf("allaward", allaward));
		
		FreeSummonResp.Builder response =  getBuilder(action,status);
		response.setReward(allaward);
		
		player.sendProtocol(Protocol.valueOf(HP.code.ACTIVITY180_FREE_SUMMOM_S, response));
		
//		BehaviorLogger.log4Platform(player, Action.    	ACTIVITY180_FREE_SUMMON, Params.valueOf("cfgId", cfgId),
//				Params.valueOf("free", free),
//				Params.valueOf("cost", cost),
//				Params.valueOf("allaward", allaward));

		BehaviorLogger.log4Service(player, Source.USER_OPERATION, Action.ACTIVITY180_FREE_SUMMON,
				Params.valueOf("BefortakeDay", BefortakeDay),
				Params.valueOf("takeDay",status.getTakeDay()),
				Params.valueOf("allaward", allaward));
	}
	
	/**
	 * 同步資訊
	 * @param action
	 * @param player
	 * @param timeCfg
	 * @param status
	 */
	private static void SyncInfo(int action,Player player, Activity180Status status) {
		FreeSummonResp.Builder builder = getBuilder(action,status);
		player.sendProtocol(Protocol.valueOf(HP.code.ACTIVITY180_FREE_SUMMOM_S, builder));
	}
	
	private static FreeSummonResp.Builder getBuilder(int action,Activity180Status status) {
		// 返回包
		FreeSummonResp.Builder response = FreeSummonResp.newBuilder();
		
		response.setAction(action);
		response.setNowDay(status.getTodayCount());
		response.setTakeDay(status.getTakeDay());
		
		return response;
	}

}
