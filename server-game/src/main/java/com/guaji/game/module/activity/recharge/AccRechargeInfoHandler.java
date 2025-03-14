package com.guaji.game.module.activity.recharge;

import org.guaji.app.AppObj;
import org.guaji.net.protocol.Protocol;
import org.guaji.net.protocol.IProtocolHandler;

import com.guaji.game.protocol.Activity.HPAccRechargeInfoRet;
import com.guaji.game.config.ActivityTimeCfg;
import com.guaji.game.player.Player;
import com.guaji.game.util.ActivityUtil;
import com.guaji.game.protocol.Const;
import com.guaji.game.protocol.HP;
import com.guaji.game.protocol.Status;

public class AccRechargeInfoHandler implements IProtocolHandler {

	@Override
	public boolean onProtocol(AppObj appObj, Protocol protocol) {
		Player player = (Player) appObj;
		// 检测活动是否开放
		int activityId = Const.ActivityId.ACCUMULATIVE_RECHARGE_VALUE;
		ActivityTimeCfg timeCfg = ActivityUtil.getCurActivityTimeCfg(activityId);
		if(timeCfg == null){
			// 活动已关闭
			player.sendError(protocol.getType(), Status.error.ACTIVITY_CLOSE);
			return true;
		}
		
		AccRechargeStatus accRechargeStatues =  ActivityUtil.getActivityStatus(player.getPlayerData(),
				activityId, timeCfg.getStageId(), AccRechargeStatus.class);
		HPAccRechargeInfoRet.Builder ret = HPAccRechargeInfoRet.newBuilder();
		ret.setAccRechargeGold(accRechargeStatues.getTodayRechargeAmount());
		ret.addAllGotAwardCfgId(accRechargeStatues.getTodayGotAwardCfgIds());
		// 常驻活动不显示剩余时间
		ret.setSurplusTime(timeCfg.calcActivitySurplusTime());
		player.sendProtocol(Protocol.valueOf(HP.code.ACC_RECHARGE_INFO_S, ret));
		return true;
	}
}
