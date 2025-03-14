package com.guaji.game.module.activity.recharge;

import org.guaji.msg.Msg;
import org.guaji.app.AppObj;
import org.guaji.config.ConfigManager;
import org.guaji.net.protocol.Protocol;
import org.guaji.net.protocol.IProtocolHandler;

import com.guaji.game.protocol.Activity.HPGetAccRechargeAward;
import com.guaji.game.protocol.Activity.HPGetAccRechargeAwardRet;
import com.guaji.game.GsApp;
import com.guaji.game.config.AccRechargeCfg;
import com.guaji.game.config.ActivityTimeCfg;
import com.guaji.game.item.AwardItems;
import com.guaji.game.log.BehaviorLogger;
import com.guaji.game.log.BehaviorLogger.Action;
import com.guaji.game.log.BehaviorLogger.Params;
import com.guaji.game.player.Player;
import com.guaji.game.util.ActivityUtil;
import com.guaji.game.util.GsConst;
import com.guaji.game.protocol.Const;
import com.guaji.game.protocol.HP;
import com.guaji.game.protocol.Status;

public class AccRechargeAwardHandler implements IProtocolHandler {

	@Override
	public boolean onProtocol(AppObj appObj, Protocol protocol) {
		Player player = (Player) appObj;
		
		// 检测活动是否开放
		int activityId = Const.ActivityId.ACCUMULATIVE_RECHARGE_VALUE;
		ActivityTimeCfg timeCfg = ActivityUtil.getCurActivityTimeCfg(activityId);
		if (timeCfg == null) {
			// 活动已关闭
			player.sendError(protocol.getType(), Status.error.ACTIVITY_CLOSE);
			return true;
		}

		HPGetAccRechargeAward request = protocol.parseProtocol(HPGetAccRechargeAward.getDefaultInstance());
		int cfgId = request.getAwardCfgId();
		AccRechargeCfg cfg = ConfigManager.getInstance().getConfigByKey(AccRechargeCfg.class, cfgId);
		if(cfg == null){
			player.sendError(protocol.getType(), Status.error.CONFIG_NOT_FOUND);
			return true;
		}
		
		AccRechargeStatus accRechargeStatues = ActivityUtil.getActivityStatus(player.getPlayerData(), 
				activityId, timeCfg.getStageId(), AccRechargeStatus.class);
		if(accRechargeStatues.getTodayRechargeAmount() < cfg.getSum()){
			// 累计充值额度未达到
			player.sendError(protocol.getType(), Status.error.RECHARGE_NUM_LACK);
			return true;
		}
		
		if(accRechargeStatues.isAlreadyGot(cfgId)){
			// 活动奖励已领取
			player.sendError(protocol.getType(), Status.error.ACTIVITY_AWARDS_HAS_GOT);
			return true;
		}
		
		// 发放奖励并推送前端
		AwardItems awardItems = AwardItems.valueOf(cfg.getAwards());
		awardItems.rewardTakeAffectAndPush(player, Action.ACC_RECHARGE_AWARDS,2);
		accRechargeStatues.addGotAwardCfgId(cfgId);
		player.getPlayerData().updateActivity(activityId, timeCfg.getStageId());
		
		HPGetAccRechargeAwardRet.Builder ret = HPGetAccRechargeAwardRet.newBuilder();
		ret.setGotAwardCfgId(cfgId);
		ret.setSurplusTime(timeCfg.calcActivitySurplusTime());
		player.sendProtocol(Protocol.valueOf(HP.code.GET_ACC_RECHARGE_AWARD_S, ret));
		
		// 是否所有奖励都已领取
		if(ActivityUtil.isCancelShowInClient(activityId, timeCfg.getStageId(), player.getPlayerData())){
			Msg msg = Msg.valueOf(GsConst.MsgType.ALL_ACTIVITY_AWARDS_GOT, player.getXid());
			GsApp.getInstance().postMsg(msg);
		}
		
		BehaviorLogger.log4Platform(player, Action.ACC_RECHARGE_AWARDS, Params.valueOf("awardsCfgId", cfgId),
				Params.valueOf("awards", cfg.getAwards()));
		
		return true;
	}

}
