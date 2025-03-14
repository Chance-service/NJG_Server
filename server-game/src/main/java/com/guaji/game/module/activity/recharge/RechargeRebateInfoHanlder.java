package com.guaji.game.module.activity.recharge;

import java.util.Date;

import org.guaji.app.AppObj;
import org.guaji.net.protocol.Protocol;
import org.guaji.net.protocol.IProtocolHandler;
import org.guaji.os.GuaJiTime;

import com.guaji.game.protocol.Activity.HPRebateInfo;
import com.guaji.game.protocol.Activity.HPRebateInfoRet;
import com.guaji.game.config.ActivityTimeCfg;
import com.guaji.game.config.SysBasicCfg;
import com.guaji.game.player.Player;
import com.guaji.game.util.ActivityUtil;
import com.guaji.game.util.GsConst;
import com.guaji.game.protocol.Const;
import com.guaji.game.protocol.HP;
import com.guaji.game.protocol.Status;

public class RechargeRebateInfoHanlder implements IProtocolHandler {
	@Override
	public boolean onProtocol(AppObj appObj, Protocol protocol) {
		Player player = (Player) appObj;
		HPRebateInfo request = protocol.parseProtocol(HPRebateInfo.getDefaultInstance());
		int activityId = request.getActivityId();
		if (activityId == Const.ActivityId.RECHARGE_REBATE_VALUE) {
			registerDateRule(player, protocol, activityId);
		} else if (activityId == Const.ActivityId.RECHARGE_REBATE2_VALUE) {
			stageOpenRule(player, protocol, activityId);
		}
		return true;
	}

	/**
	 * 角色注册时间开放规则
	 * 
	 * @param player
	 * @param protocol
	 * @param activityId
	 */
	public void registerDateRule(Player player, Protocol protocol, int activityId) {
		Date registerDate = player.getPlayerData().getPlayerEntity().getCreateTime();
		int activityStatus = ActivityUtil.calcRechargeRebateActivityStatus(registerDate);
		if (activityStatus < GsConst.RechargeRebateActivity.STATUS_RECHARGE) {
			// 活动已关闭
			player.sendError(protocol.getType(), Status.error.ACTIVITY_CLOSE);
			return;
		}

		RechargeRebateStatus rebateStatus = ActivityUtil.getActivityStatus(player.getPlayerData(), activityId, -1, RechargeRebateStatus.class);
		SysBasicCfg sysCfg = SysBasicCfg.getInstance();

		HPRebateInfoRet.Builder ret = HPRebateInfoRet.newBuilder();
		ret.setSurplusTime(ActivityUtil.clacRechargeSurplusTime(registerDate));
		int accAmount = rebateStatus.getAccRechargeAmount();
		ret.setAccRechargeDiamond(accAmount);
		int returnGold = Math.min(sysCfg.getEverydayRebateMaxGold(), accAmount / sysCfg.getRechargeMinUnitGold() * sysCfg.getRebateMinUnitGold());
		ret.setCanReceiveDiamond(returnGold);
		if (activityStatus > GsConst.RechargeRebateActivity.STATUS_RECHARGE) {
			ret.setLeftDays(ActivityUtil.calcRebateLeftDays(registerDate));
			ret.setReceiveAward(rebateStatus.todayIsGet());
		}
		player.sendProtocol(Protocol.valueOf(HP.code.RECHARGE_REBATE_INFO_S_VALUE, ret));
	}

	/**
	 * 周期开放规则
	 * 
	 * @param player
	 * @param protocol
	 * @param activityId
	 */
	public void stageOpenRule(Player player, Protocol protocol, int activityId) {
		ActivityTimeCfg timeCfg = ActivityUtil.getCurActivityTimeCfg(activityId);
		if (timeCfg == null) {
			// 活动已关闭
			player.sendError(protocol.getType(), Status.error.ACTIVITY_CLOSE);
			return;
		}

		RechargeRebateStatus rebateStatus = ActivityUtil.getActivityStatus(player.getPlayerData(), activityId, timeCfg.getStageId(), RechargeRebateStatus.class);
		int accAmount = rebateStatus.getAccRechargeAmount();
		SysBasicCfg sysCfg = SysBasicCfg.getInstance();
		int returnGold = Math.min(sysCfg.getEverydayRebateMaxGold(), accAmount / sysCfg.getRechargeMinUnitGold() * sysCfg.getRebateMinUnitGold());
		if (timeCfg.isEnd() && returnGold <= 0) {
			// 活动已关闭
			player.sendError(protocol.getType(), Status.error.ACTIVITY_CLOSE);
			return;
		}

		HPRebateInfoRet.Builder ret = HPRebateInfoRet.newBuilder();
		ret.setSurplusTime(timeCfg.calcActivitySurplusTime());
		ret.setAccRechargeDiamond(accAmount);
		ret.setCanReceiveDiamond(returnGold);
		if (timeCfg.isEnd()) {
			int leftDays = 0;
			if (GuaJiTime.getMillisecond() < timeCfg.getlCloseTime()) {
				Date startDate = new Date(GuaJiTime.getMillisecond());
				Date closeDate = new Date(timeCfg.getlCloseTime());
				leftDays = GuaJiTime.calcBetweenDays(startDate, closeDate);
			}
			ret.setLeftDays(leftDays);
			ret.setReceiveAward(rebateStatus.todayIsGet());
		}
		player.sendProtocol(Protocol.valueOf(HP.code.RECHARGE_REBATE_INFO_S_VALUE, ret));
	}
}
