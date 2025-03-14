package com.guaji.game.module.activity.activity159;

import org.guaji.app.AppObj;
import org.guaji.config.ConfigManager;
import org.guaji.net.protocol.IProtocolHandler;
import org.guaji.net.protocol.Protocol;

import com.guaji.game.config.ActivityTimeCfg;
import com.guaji.game.config.DailyRecharge159Cfg;
import com.guaji.game.config.NPcontinueRecharge160Cfg;
import com.guaji.game.item.AwardItems;
import com.guaji.game.log.BehaviorLogger;
import com.guaji.game.log.BehaviorLogger.Action;
import com.guaji.game.log.BehaviorLogger.Params;
import com.guaji.game.log.BehaviorLogger.Source;
import com.guaji.game.player.Player;
import com.guaji.game.protocol.Activity5.NPContinueRechargeReq;
import com.guaji.game.protocol.Activity5.NPContinueRechargeRes;
import com.guaji.game.protocol.Activity5.VIPPointCollectReq;
import com.guaji.game.protocol.Activity5.VIPPointCollectResp;
import com.guaji.game.protocol.Const;
import com.guaji.game.protocol.HP;
import com.guaji.game.protocol.Status;
import com.guaji.game.util.ActivityUtil;

/**
 * @author 作者 TING LIN
 * @version 创建时间：2023年10月19日 下午03:42 类说明
 */
public class Activity159Handler implements IProtocolHandler {
	static final int SYNC_INFO = 0; // 同步
	static final int GET_AWARD = 1; // 領禮物
	@Override
	public boolean onProtocol(AppObj appObj, Protocol protocol) {
		Player player = (Player) appObj;
		// 检测活动是否开放
		int activityId = Const.ActivityId.ACTIVITY159_VIP_POINT_VALUE;
		ActivityTimeCfg timeCfg = ActivityUtil.getCurActivityTimeCfg(activityId);

		if (timeCfg == null) {
			// 活动已关闭
			player.sendError(protocol.getType(), Status.error.ACTIVITY_CLOSE);
			return true;
		}
		
		Activity159Status status = ActivityUtil.getActivityStatus(player.getPlayerData(), activityId, timeCfg.getStageId(), Activity159Status.class);
		
		if (status == null) {
			player.sendError(protocol.getType(), Status.error.DATA_NOT_FOUND);
			return true;
		}
		
		VIPPointCollectReq req = protocol.parseProtocol(VIPPointCollectReq.getDefaultInstance());
		int action = req.getAction();
	
		// 业务分支处理
		switch (action) {
		case SYNC_INFO:
			SyncInfo(action, player, status);
			break;
		case GET_AWARD:
			int cfgId = req.getAwardCfgId();
			Activty159_GetAward(protocol,action,cfgId,player,status,timeCfg);
			break;
		default:
			player.sendError(protocol.getType(), Status.error.PARAMS_INVALID_VALUE);
			break;
		}
		return true;
		
	}
	
	/**
	 * 同步資訊
	 * @param action
	 * @param player
	 * @param timeCfg
	 * @param status
	 */
	private static void SyncInfo(int action,Player player, Activity159Status status) {
		VIPPointCollectResp.Builder builder = getBuilder(player,action,status);
		player.sendProtocol(Protocol.valueOf(HP.code.ACTIVITY159_VIP_POINT_S, builder));
	}
	
	private static VIPPointCollectResp.Builder getBuilder(Player player,int action ,Activity159Status status) {
		// 返回包
		VIPPointCollectResp.Builder response = VIPPointCollectResp.newBuilder();
		response.setAction(action);
		response.setVipPoint(status.getVIPPoint());
		response.addAllGotAwardCfgId(status.getGotAwardCfgIds());
		return response;
	}
	
	private static void Activty159_GetAward(Protocol protocol,int action,int cfgId,Player player, Activity159Status status,ActivityTimeCfg timeCfg) {
		
		DailyRecharge159Cfg cfg =  ConfigManager.getInstance().getConfigByKey(DailyRecharge159Cfg.class,cfgId);
		
		if (cfg == null) {
			player.sendError(protocol.getType(), Status.error.CONFIG_NOT_FOUND);
			return;
		}
		
		if (status.getVIPPoint() < cfg.getConsume()) {
			// VIP點數不足
			player.sendError(protocol.getType(), Status.error.RECHARGE_NUM_LACK);
			return;
		}

		if (status.isAlreadyGot(cfgId)) {
			// 活动奖励已领取
			player.sendError(protocol.getType(), Status.error.ACTIVITY_AWARDS_HAS_GOT);
			return ;
		}

		// 发放奖励并推送前端
		String awradStr = cfg.getReward();
		AwardItems awardItems = AwardItems.valueOf(awradStr);
		awardItems.rewardTakeAffectAndPush(player, Action.ACTIVITY159_VIP_POINT_COLLECT_AWARDS, 2);

		status.addGotAwardCfgId(cfgId);
		player.getPlayerData().updateActivity(Const.ActivityId.ACTIVITY159_VIP_POINT_VALUE, timeCfg.getStageId());
		
		BehaviorLogger.log4Service(player, Source.USER_OPERATION, Action.ACTIVITY159_VIP_POINT_COLLECT_AWARDS,
				Params.valueOf("cfgId", cfgId),
				Params.valueOf("VIPPont", status.getVIPPoint()),
				Params.valueOf("awradStr",awradStr));
		
		VIPPointCollectResp.Builder builder = getBuilder(player,action,status);
		builder.setReward(awradStr);
		
		player.sendProtocol(Protocol.valueOf(HP.code.ACTIVITY159_VIP_POINT_S, builder));
	}

}
