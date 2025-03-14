package com.guaji.game.module.activity.activity160;

import org.guaji.app.AppObj;
import org.guaji.config.ConfigManager;
import org.guaji.net.protocol.IProtocolHandler;
import org.guaji.net.protocol.Protocol;

import com.guaji.game.config.ActivityTimeCfg;
import com.guaji.game.config.NPcontinueRecharge160Cfg;
import com.guaji.game.item.AwardItems;
import com.guaji.game.log.BehaviorLogger;
import com.guaji.game.log.BehaviorLogger.Action;
import com.guaji.game.log.BehaviorLogger.Params;
import com.guaji.game.log.BehaviorLogger.Source;
import com.guaji.game.player.Player;
import com.guaji.game.protocol.Activity5.NPContinueRechargeReq;
import com.guaji.game.protocol.Activity5.NPContinueRechargeRes;
import com.guaji.game.protocol.Const;
import com.guaji.game.protocol.HP;
import com.guaji.game.protocol.Status;
import com.guaji.game.util.ActivityUtil;

/**
 * @author 作者 E-mail:Jeremy@163.com
 * @version 创建时间：2019年4月8日 下午4:20:29 类说明
 */
public class Activity160Handler implements IProtocolHandler {
	static final int SYNC_INFO = 0; // 同步
	static final int GET_AWARD = 1; // 領禮物
	@Override
	public boolean onProtocol(AppObj appObj, Protocol protocol) {
		Player player = (Player) appObj;
		// 检测活动是否开放
		int activityId = Const.ActivityId.ACTIVITY160_NP_CONTINUE_RECHARGE_VALUE;
		ActivityTimeCfg timeCfg = ActivityUtil.getCurActivityTimeCfg(activityId);

		// Date registerDate = player.getPlayerData().getPlayerEntity().getCreateTime();
		if (timeCfg == null) {
			// 活动已关闭
			player.sendError(protocol.getType(), Status.error.ACTIVITY_CLOSE);
			return true;
		}
		
		Activity160Status status = ActivityUtil.getActivityStatus(player.getPlayerData(), activityId, timeCfg.getStageId(), Activity160Status.class);
		
		if (status == null) {
			player.sendError(protocol.getType(), Status.error.DATA_NOT_FOUND);
			return true;
		}
		
		NPContinueRechargeReq req = protocol.parseProtocol(NPContinueRechargeReq.getDefaultInstance());
		int action = req.getAction();
	
		// 业务分支处理
		switch (action) {
		case SYNC_INFO:
			SyncInfo(action, player, status);
			break;
		case GET_AWARD:
			int cfgId = req.getAwardCfgId();
			Activty160_GetAward(protocol,action,cfgId,player,status,timeCfg);
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
	private static void SyncInfo(int action,Player player, Activity160Status status) {
		NPContinueRechargeRes.Builder builder = getBuilder(player,action,status);
		player.sendProtocol(Protocol.valueOf(HP.code.NP_CONTINUE_RECHARGE_MONEY_S, builder));
	}
	
	private static NPContinueRechargeRes.Builder getBuilder(Player player,int action ,Activity160Status status) {
		// 返回包
		NPContinueRechargeRes.Builder response = NPContinueRechargeRes.newBuilder();
		response.setAction(action);
		response.setRechargeTotal(status.getContinueRechargeMoney());
		response.addAllGotAwardCfgId(status.getGotAwardCfgIds());
		return response;
	}
	
	private static void Activty160_GetAward(Protocol protocol,int action,int cfgId,Player player, Activity160Status status,ActivityTimeCfg timeCfg) {
		
		NPcontinueRecharge160Cfg cfg = ConfigManager.getInstance().getConfigByKey(NPcontinueRecharge160Cfg.class,
				cfgId);
		if (cfg == null) {
			player.sendError(protocol.getType(), Status.error.CONFIG_NOT_FOUND);
			return;
		}
		
		if (status.getContinueRechargeMoney() < cfg.getnTotalMoney()) {
			// 连续充值金额未达到
			player.sendError(protocol.getType(), Status.error.RECHARGE_NUM_LACK);
			return;
		}

		if (status.isAlreadyGot(cfgId)) {
			// 活动奖励已领取
			player.sendError(protocol.getType(), Status.error.ACTIVITY_AWARDS_HAS_GOT);
			return ;
		}

		// 发放奖励并推送前端
		String awradStr = cfg.getAwards();
		AwardItems awardItems = AwardItems.valueOf(awradStr);
		awardItems.rewardTakeAffectAndPush(player, Action.ACTIVITY160_NP_RECHARGEMONEY_AWARDS, 2);

		status.addGotAwardCfgId(cfgId);
		player.getPlayerData().updateActivity(Const.ActivityId.ACTIVITY160_NP_CONTINUE_RECHARGE_VALUE, timeCfg.getStageId());
		
		BehaviorLogger.log4Service(player, Source.USER_OPERATION, Action.ACTIVITY160_NP_RECHARGEMONEY_AWARDS,
				Params.valueOf("cfgId", cfgId),
				Params.valueOf("RechargeMoney()", status.getContinueRechargeMoney()),
				Params.valueOf("awradStr",awradStr));
		
		NPContinueRechargeRes.Builder builder = getBuilder(player,action,status);
		builder.setReward(awradStr);
		
		player.sendProtocol(Protocol.valueOf(HP.code.NP_CONTINUE_RECHARGE_MONEY_S, builder));
	}

}
