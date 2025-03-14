package com.guaji.game.module.activity.recharge;

import java.util.Date;

import org.guaji.app.AppObj;
import org.guaji.net.protocol.Protocol;
import org.guaji.net.protocol.IProtocolHandler;
import org.guaji.os.GuaJiTime;

import com.guaji.game.protocol.Activity.HPRebateAward;
import com.guaji.game.protocol.Activity.HPRebateAwardRet;
import com.guaji.game.config.ActivityTimeCfg;
import com.guaji.game.config.SysBasicCfg;
import com.guaji.game.item.AwardItems;
import com.guaji.game.log.BehaviorLogger.Action;
import com.guaji.game.player.Player;
import com.guaji.game.util.ActivityUtil;
import com.guaji.game.util.GsConst;
import com.guaji.game.protocol.Const;
import com.guaji.game.protocol.HP;
import com.guaji.game.protocol.Status;

public class RechargeRebateAwardHanlder implements IProtocolHandler{

	@Override
	public boolean onProtocol(AppObj appObj, Protocol protocol) {
		Player player = (Player) appObj;
		HPRebateAward request = protocol.parseProtocol(HPRebateAward.getDefaultInstance());
		int activityId = request.getActivityId();
		if(	activityId == Const.ActivityId.RECHARGE_REBATE_VALUE){
			registerDateRule(player, protocol, activityId);
		} else if(activityId == Const.ActivityId.RECHARGE_REBATE2_VALUE){
			stageOpenRule(player, protocol, activityId);
		}
		return true;
	}

	/**
	 * 角色注册时间开放规则
	 * @param player
	 * @param protocol
	 * @param activityId
	 */
	public void registerDateRule(Player player, Protocol protocol, int activityId){
		Date registerDate = player.getPlayerData().getPlayerEntity().getCreateTime();
		int activityStatus = ActivityUtil.calcRechargeRebateActivityStatus(registerDate);
		if (activityStatus < GsConst.RechargeRebateActivity.STATUS_RECHARGE){
			// 活动已关闭
			player.sendError(protocol.getType(), Status.error.ACTIVITY_CLOSE);
			return;
		}
		
		RechargeRebateStatus rebateStatus = ActivityUtil.getActivityStatus(player.getPlayerData(), activityId, -1, RechargeRebateStatus.class);
		SysBasicCfg sysCfg = SysBasicCfg.getInstance();
		if(activityStatus > GsConst.RechargeRebateActivity.STATUS_RECHARGE && rebateStatus.getAccRechargeAmount() < sysCfg.getRechargeMinUnitGold()){
			// 活动已关闭
			player.sendError(protocol.getType(), Status.error.ACTIVITY_CLOSE);
			return;
		}
		
		if(activityStatus == GsConst.RechargeRebateActivity.STATUS_RECHARGE){
			// 充值阶段还未结束, 暂不能领取
			player.sendError(protocol.getType(), Status.error.RECHARGE_STATUS_NOT_FINISH);
			return;
		}
		
		if(rebateStatus.todayIsGet() > 0){
			// 今日返利已领取
			player.sendError(protocol.getType(), Status.error.TODAY_REBATE_GOT);
			return;
		}
		
		AwardItems awards = new AwardItems();
		int accAmount = rebateStatus.getAccRechargeAmount();
		int returnGold = Math.min(sysCfg.getEverydayRebateMaxGold(), accAmount / sysCfg.getRechargeMinUnitGold() * sysCfg.getRebateMinUnitGold());
		awards.addGold(returnGold);
		awards.rewardTakeAffectAndPush(player, Action.RECHARGE_REBATE_EVERYDAY_AWARDS,1);
		rebateStatus.setLastRebateTime(GuaJiTime.getSeconds());
		player.getPlayerData().updateActivity(activityId, -1);
		
		HPRebateAwardRet.Builder ret = HPRebateAwardRet.newBuilder().setReceiveAward(rebateStatus.todayIsGet());
		player.sendProtocol(Protocol.valueOf(HP.code.RECHARGE_REBATE_AWARD_S_VALUE, ret));
	}
	
	/**
	 * 周期开放规则
	 * @param player
	 * @param protocol
	 * @param activityId
	 */
	public void stageOpenRule(Player player, Protocol protocol, int activityId){
		ActivityTimeCfg timeCfg = ActivityUtil.getCurActivityTimeCfg(activityId);
		if(timeCfg == null){
			// 活动已关闭
			player.sendError(protocol.getType(), Status.error.ACTIVITY_CLOSE);
			return;
		}
		
		if(!timeCfg.isEnd()){
			// 充值阶段还未结束, 暂不能领取
			player.sendError(protocol.getType(), Status.error.RECHARGE_STATUS_NOT_FINISH);
			return;
		}
		
		RechargeRebateStatus rebateStatus = ActivityUtil.getActivityStatus(player.getPlayerData(), activityId, 
				timeCfg.getStageId(), RechargeRebateStatus.class);
		int accAmount = rebateStatus.getAccRechargeAmount();
		SysBasicCfg sysCfg = SysBasicCfg.getInstance();
		int returnGold = Math.min(sysCfg.getEverydayRebateMaxGold(), accAmount / sysCfg.getRechargeMinUnitGold() * sysCfg.getRebateMinUnitGold());
		if(returnGold <= 0){
			// 活动已关闭
			player.sendError(protocol.getType(), Status.error.ACTIVITY_CLOSE);
			return;
		}
		
		if(rebateStatus.todayIsGet() > 0){
			// 今日返利已领取
			player.sendError(protocol.getType(), Status.error.TODAY_REBATE_GOT);
			return;
		}
		
		AwardItems awards = new AwardItems();
		awards.addGold(returnGold);
		awards.rewardTakeAffectAndPush(player, Action.RECHARGE_REBATE_EVERYDAY_AWARDS,1);
		rebateStatus.setLastRebateTime(GuaJiTime.getSeconds());
		player.getPlayerData().updateActivity(activityId, timeCfg.getStageId());
		
		HPRebateAwardRet.Builder ret = HPRebateAwardRet.newBuilder().setReceiveAward(rebateStatus.todayIsGet());
		player.sendProtocol(Protocol.valueOf(HP.code.RECHARGE_REBATE_AWARD_S_VALUE, ret));
	}
}
