package com.guaji.game.module.activity.activity192;

import java.util.List;

import org.guaji.app.AppObj;
import org.guaji.config.ConfigManager;
import org.guaji.net.protocol.IProtocolHandler;
import org.guaji.net.protocol.Protocol;

import com.guaji.game.config.ActivityTimeCfg;
import com.guaji.game.config.RechargeBounceCfg;
import com.guaji.game.config.RechargeBounceTimeIndexCfg;
import com.guaji.game.item.AwardItems;
import com.guaji.game.log.BehaviorLogger;
import com.guaji.game.log.BehaviorLogger.Action;
import com.guaji.game.log.BehaviorLogger.Params;
import com.guaji.game.log.BehaviorLogger.Source;
import com.guaji.game.player.Player;
import com.guaji.game.protocol.Activity5.RechargeBounceItem;
import com.guaji.game.protocol.Activity5.RechargeBounceReq;
import com.guaji.game.protocol.Activity5.RechargeBounceResp;
import com.guaji.game.protocol.Activity5.RechargeBounceSingle;
import com.guaji.game.protocol.Activity5.RechargeBounceTime;
import com.guaji.game.protocol.Const;
import com.guaji.game.protocol.HP;
import com.guaji.game.protocol.Status;
import com.guaji.game.util.ActivityUtil;
import com.guaji.game.util.GsConst;
import com.guaji.game.util.TapDBUtil.TapDBSource;

/**
 * 192.累儲累消累充活動
 */
public class Activity192Handler implements IProtocolHandler {
	static final int Sync = 0;
	static final int Get_Rward = 1;

	@Override
	public boolean onProtocol(AppObj appObj, Protocol protocol) {
		// TODO Auto-generated method stub
		
		Player player = (Player) appObj;

		
		// 检测活动是否开放
		int activityId = Const.ActivityId.ACTIVITY192_RechargeBounce_VALUE;
		ActivityTimeCfg timeConfig = ActivityUtil.getCurActivityTimeCfg(activityId);
		if (timeConfig == null || player == null) {
			// 活动已关闭
			player.sendError(protocol.getType(), Status.error.ACTIVITY_CLOSE);
			return true;
		}

		// 解析接受到的数据
		RechargeBounceReq request = protocol.parseProtocol(RechargeBounceReq.getDefaultInstance());

		int action = request.getAction();
								
		int stageId = timeConfig.getStageId();
		Activity192Status status = ActivityUtil.getActivityStatus(player.getPlayerData(), activityId, stageId, Activity192Status.class);
		
		if (status == null) {
			player.sendError(protocol.getType(), Status.error.DATA_NOT_FOUND_VALUE);
			return true;
		}
		
		boolean save = status.checkTimeIndex(player.getPlatformId());
		
		if (save) {
			player.getPlayerData().updateActivity(activityId, timeConfig.getStageId());
		}
				
		// 业务分支处理
		switch (action) {
		case Sync:
			SyncInfo(timeConfig,action,player,status);
			break;
		case  Get_Rward:
			GetRward(protocol,timeConfig,action,player,status);
			break;
		default:
			player.sendError(protocol.getType(), Status.error.PARAMS_INVALID_VALUE);
			break;
		}
		return true;
	}
	
	private static void GetRward(Protocol protocol,ActivityTimeCfg timeConfig,int action,Player player, Activity192Status status) {
					
		RechargeBounceReq request = protocol.parseProtocol(RechargeBounceReq.getDefaultInstance());
		
		
		if (!request.hasCfgId()) {
			player.sendError(protocol.getType(), Status.error.PARAMS_INVALID_VALUE);
			return;
		}
		
		int cfgId = request.getCfgId();
		
		RechargeBounceCfg BounceCfg = ConfigManager.getInstance().getConfigByKey(RechargeBounceCfg.class, cfgId);
		
		if (BounceCfg == null) {
			player.sendError(protocol.getType(), Status.error.CONFIG_NOT_FOUND);
			return;
		}
		
		if (BounceCfg.getPlatformtype() != player.getPlatformId()) {
			// 平台檢查
			player.sendError(protocol.getType(), Status.error.PARAMS_INVALID_VALUE);
			return;
		}
		
		int type = BounceCfg.getType();
		
		if ((status.getTakeId().contains(cfgId)) && (type != GsConst.RechargeBounceType.Single)) {
			// 已經領過了
			player.sendError(protocol.getType(), Status.error.PARAMS_INVALID_VALUE);
			return;
		}
		
		int timeIndex =  status.getTimeIndex(type);
		
		if (BounceCfg.getTimeindex() != timeIndex) {
			player.sendError(protocol.getType(), Status.error.ACTIVITY_END_VALUE);
			return;
		}
		
		// 以下檢查條件
		int term = BounceCfg.getNeedcount();
		if (type == GsConst.RechargeBounceType.Deposit) {
			if (status.getDeposit() < term) {
				player.sendError(protocol.getType(), Status.error.PARAMS_INVALID_VALUE);
				return;
			}
		} else if (type == GsConst.RechargeBounceType.Single) {
			
			if (status.getTakeSingle(cfgId) >= BounceCfg.getCount()){
				//可領次數已經超過
				player.sendError(protocol.getType(), Status.error.PARAMS_INVALID_VALUE);
				return;
			}
			
			if (status.getSingleCount(cfgId) <= 0 ){
				//未達成
				player.sendError(protocol.getType(), Status.error.PARAMS_INVALID_VALUE);
				return;
			}
			
			if (status.getTakeSingle(cfgId) >= status.getSingleCount(cfgId) ){
				//已領超過範圍
				player.sendError(protocol.getType(), Status.error.PARAMS_INVALID_VALUE);
				return;
			}
			

		} else if (type == GsConst.RechargeBounceType.consums) {
			if (status.getConsume() < term) {
				player.sendError(protocol.getType(), Status.error.PARAMS_INVALID_VALUE);
				return;
			}
		} else {
			player.sendError(protocol.getType(), Status.error.CONFIG_NOT_FOUND);
			return;
		}
		
		if (type == GsConst.RechargeBounceType.Single) {
			status.addTakeSingle(cfgId);
		} else {
			status.getTakeId().add(cfgId);
		}
		
		int activityId = Const.ActivityId.ACTIVITY192_RechargeBounce_VALUE;
		ActivityTimeCfg timeCfg = ActivityUtil.getCurActivityTimeCfg(activityId);
		player.getPlayerData().updateActivity(activityId, timeCfg.getStageId());
		
		// 下发奖励
		AwardItems awards = AwardItems.valueOf(BounceCfg.getReward());
		awards.rewardTakeAffectAndPush(player, Action.ACTIVITY192_Recharge_Bounce, 2,TapDBSource.Recharage_Bounce,
				Params.valueOf("id", cfgId));
		
		SyncInfo(timeConfig,action,player,status);
		
		BehaviorLogger.log4Service(player, Source.USER_OPERATION, Action.ACTIVITY192_Recharge_Bounce,
				Params.valueOf("type", type),
				Params.valueOf("cfgId",cfgId),
				Params.valueOf("platformId",player.getPlatformId()),
				Params.valueOf("term",term),
				Params.valueOf("reward",BounceCfg.getReward()));
	}
	
	/**
	 * 同步資訊
	 * @param action
	 * @param player
	 * @param timeCfg
	 * @param status
	 */
	private static void SyncInfo(ActivityTimeCfg timeConfig,int action,Player player, Activity192Status status) {
		RechargeBounceResp.Builder builder = getBuilder(timeConfig,action,player,status);
		player.sendProtocol(Protocol.valueOf(HP.code.ACTIVITY192_RECHARGE_BOUNCE_S, builder));
	}
	
	private static RechargeBounceResp.Builder getBuilder(ActivityTimeCfg timeConfig,int action,Player player,Activity192Status status) {
		// 返回包
		RechargeBounceResp.Builder response = RechargeBounceResp.newBuilder();
		
		response.setAction(action);
		
		response.setDeposit(status.getDeposit());
		
		response.setConsume(status.getConsume());
		
		for (int type = 1 ; type <= GsConst.RechargeBounceType.typeCount ; type++) {
			RechargeBounceTime.Builder timeInfo = RechargeBounceTime.newBuilder();
			timeInfo.setType(type);
			int timeIndex = status.getTimeIndex(type);
			timeInfo.setTimeIndex(timeIndex);
			if (timeIndex > 0) {
				RechargeBounceTimeIndexCfg rbtcfg =  ConfigManager.getInstance().getConfigByKey(RechargeBounceTimeIndexCfg.class, timeIndex);
				if ((rbtcfg != null)&&(rbtcfg.isValid())) {
					timeInfo.setStarTime(rbtcfg.getlStartTime());
					timeInfo.setEndTime(rbtcfg.getlEndTime());
				}
			}
			response.addTimeInfo(timeInfo);
			
		}
				
		List<RechargeBounceCfg> cfgList = RechargeBounceCfg.getCfgByPlatform(player.getPlatformId());
		
		for (RechargeBounceCfg acfg :cfgList) {
			int type = acfg.getType();
			int term = acfg.getNeedcount();
			if (type == GsConst.RechargeBounceType.Deposit) {
				if (status.getDeposit() < term) {
					continue;
				}
			} else if (type == GsConst.RechargeBounceType.Single) {
				if (status.getSingleCount(acfg.getId()) <= 0){
					//未達成
					continue;
				}
				
				int checkcount = status.getSingleCount(acfg.getId());
				if ( checkcount > acfg.getCount()) {
					checkcount = acfg.getCount();
				}
				
				if (status.getTakeSingle(acfg.getId()) >= acfg.getCount()) {
					// 已領光
				} else {
					if (status.getTakeSingle(acfg.getId()) >= checkcount) {
						continue;
					}
				}

			} else if (type == GsConst.RechargeBounceType.consums) {
				if (status.getConsume() < term) {
					continue;
				}
			} else {
				continue;
			}
			RechargeBounceItem.Builder itemInfo = RechargeBounceItem.newBuilder();
			itemInfo.setCfgId(acfg.getId());
			int flag = -1;
			if (type == GsConst.RechargeBounceType.Single) {
				int checkcount = status.getSingleCount(acfg.getId());
				if ( checkcount > acfg.getCount()) {
					checkcount = acfg.getCount();
				}
				flag = (status.getTakeSingle(acfg.getId()) >= checkcount) ? 1 : 0;
			} else {
				flag = status.getTakeId().contains(acfg.getId()) ? 1 : 0;
			}
			itemInfo.setIsGot(flag);
			response.addItemInfo(itemInfo);
		}
		
		for (RechargeBounceCfg acfg :cfgList) {
			int type = acfg.getType();
			int cfgId = acfg.getId();
			if (type == GsConst.RechargeBounceType.Single) {
				RechargeBounceSingle.Builder Info = RechargeBounceSingle.newBuilder();
				if (status.getTakeSingle(cfgId) != 0) {
					Info.setCfgId(cfgId);
					Info.setLeft(status.getTakeSingle(cfgId));
					response.addSingleInfo(Info);
				}
			}
		}
				
		return response;
	}

}
