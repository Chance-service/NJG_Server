package com.guaji.game.module.activity.activity163;

import org.guaji.app.AppObj;
import org.guaji.config.ConfigManager;
import org.guaji.net.protocol.IProtocolHandler;
import org.guaji.net.protocol.Protocol;

import com.guaji.game.config.ActivityTimeCfg;
import com.guaji.game.config.GrowthCHCfg;
import com.guaji.game.item.AwardItems;
import com.guaji.game.log.BehaviorLogger;
import com.guaji.game.log.BehaviorLogger.Action;
import com.guaji.game.log.BehaviorLogger.Params;
import com.guaji.game.log.BehaviorLogger.Source;
import com.guaji.game.player.Player;
import com.guaji.game.protocol.Activity5.GrowthCHPassRes;
import com.guaji.game.protocol.Activity5.GrowthPassReq;
import com.guaji.game.protocol.Const;
import com.guaji.game.protocol.HP;
import com.guaji.game.protocol.Status;
import com.guaji.game.util.ActivityUtil;
import com.guaji.game.util.TapDBUtil.TapDBSource;

/**
 * 成長禮物領取協定
 */
public class Activity163Handler implements IProtocolHandler {
	static final int Sync = 0;
	static final int Get_Rward = 1;

	@Override
	public boolean onProtocol(AppObj appObj, Protocol protocol) {
		// TODO Auto-generated method stub

		Player player = (Player) appObj;
		// 检测活动是否开放
		int activityId = Const.ActivityId.ACTIVITY163_GROWTH_CH_VALUE;
		ActivityTimeCfg timeConfig = ActivityUtil.getCurActivityTimeCfg(activityId);
		if (timeConfig == null || player == null) {
			// 活动已关闭
			player.sendError(protocol.getType(), Status.error.ACTIVITY_CLOSE);
			return true;
		}

		// 解析接受到的数据
		GrowthPassReq request = protocol.parseProtocol(GrowthPassReq.getDefaultInstance());

		int action = request.getAction();
		
		int cfgId = request.getCfgId();
		
		// 数据人错误
		if ( action < 0 || action > 1) {
			// 无效参数
			player.sendError(protocol.getType(), Status.error.PARAMS_INVALID_VALUE);
			return true;
		}
				
		int stageId = timeConfig.getStageId();
		Activity163Status status = ActivityUtil.getActivityStatus(player.getPlayerData(), activityId, stageId, Activity163Status.class);
		
		if (status == null) {
			player.sendError(protocol.getType(), Status.error.DATA_NOT_FOUND_VALUE);
			return true;
		}
		
		// 业务分支处理
		switch (action) {
		case Sync:
			SyncInfo(action,player,status);
			break;
		case  Get_Rward:
			GetRward(protocol,action,cfgId,player,status);
			break;
		default:
			player.sendError(protocol.getType(), Status.error.PARAMS_INVALID_VALUE);
			break;
		}
		return true;
	}
	
	private static void GetRward(Protocol protocol,int action,int cfgId,Player player, Activity163Status status) {
		
		GrowthCHCfg cfg = ConfigManager.getInstance().getConfigByKey(GrowthCHCfg.class, cfgId);
		
		if (cfg == null) {
			player.sendError(protocol.getType(), Status.error.CONFIG_NOT_FOUND_VALUE);
			return;
		}
		
		if ((status.getFreeCfgId().contains(cfgId))&&(status.getCostCfgId().contains(cfgId))) {
			player.sendError(protocol.getType(), Status.error.PARAMS_INVALID_VALUE);
			return;
		}
		
		if (player.getPassMapId() < cfg.getCHID()) {
			player.sendError(protocol.getType(), Status.error.PARAMS_INVALID_VALUE);
			return;
		}
		
		String allaward = "";
		boolean free = (!status.getFreeCfgId().contains(cfgId));
		boolean cost = (!status.getCostCfgId().contains(cfgId)) && (status.getCostFlag().contains(cfg.getType()));
		
		if (free){
			status.setFreeCfgId(cfgId);
			allaward = cfg.getFreeReward();
		}
		
		if (cost){
			status.setCostCfgId(cfgId);
			if (allaward.isEmpty()) {
				allaward = cfg.getCostReward();
			} else {
				allaward = allaward+","+cfg.getCostReward();
			}
		}
				
		int activityId = Const.ActivityId.ACTIVITY163_GROWTH_CH_VALUE;
		ActivityTimeCfg timeCfg = ActivityUtil.getCurActivityTimeCfg(activityId);
		player.getPlayerData().updateActivity(activityId, timeCfg.getStageId());
		
		
		// 下发奖励
		AwardItems awards = AwardItems.valueOf(allaward);
		awards.rewardTakeAffectAndPush(player, Action.ACTIVITY163_GROWTH_CH, 2,TapDBSource.Pass_Port,
				Params.valueOf("activityId", activityId),
				Params.valueOf("cfgId", cfgId),
				Params.valueOf("free", free),
				Params.valueOf("cost", cost));
		
		GrowthCHPassRes.Builder response =  getBuilder(action,status);
		
		player.sendProtocol(Protocol.valueOf(HP.code.ACTIVITY163_Growth_CH_S, response));
		
//		BehaviorLogger.log4Platform(player, Action.ACTIVITY163_GROWTH_CH, Params.valueOf("cfgId", cfgId),
//				Params.valueOf("free", free),
//				Params.valueOf("cost", cost),
//				Params.valueOf("allaward", allaward));

		BehaviorLogger.log4Service(player, Source.USER_OPERATION, Action.ACTIVITY163_GROWTH_CH,Params.valueOf("cfgId", cfgId),
				Params.valueOf("free", free),
				Params.valueOf("cost", cost),
				Params.valueOf("allaward", allaward));
	}
	
	/**
	 * 同步資訊
	 * @param action
	 * @param player
	 * @param timeCfg
	 * @param status
	 */
	private static void SyncInfo(int action,Player player, Activity163Status status) {
		GrowthCHPassRes.Builder builder = getBuilder(action,status);
		player.sendProtocol(Protocol.valueOf(HP.code.ACTIVITY163_Growth_CH_S, builder));
	}
	
	private static GrowthCHPassRes.Builder getBuilder(int action,Activity163Status status) {
		// 返回包
		GrowthCHPassRes.Builder response = GrowthCHPassRes.newBuilder();
		
		response.setAction(action);
		response.addAllCostFlag(status.getCostFlag());//(status.getCostFlag());
		response.addAllFreeCfgId(status.getFreeCfgId());
		response.addAllCostCfgId(status.getCostCfgId());
		
		return response;
	}

}
