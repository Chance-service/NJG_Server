package com.guaji.game.module.activity.recharge;

import java.util.List;

import org.guaji.app.AppObj;
import org.guaji.net.protocol.Protocol;
import org.guaji.net.protocol.IProtocolHandler;

import com.guaji.game.protocol.Activity.HPSingleRechargeInfoRet;
import com.guaji.game.protocol.Activity.SingleRechargeInfo;
import com.guaji.game.config.ActivityTimeCfg;
import com.guaji.game.player.Player;
import com.guaji.game.util.ActivityUtil;
import com.guaji.game.protocol.Const;
import com.guaji.game.protocol.HP;
import com.guaji.game.protocol.Status;

/**
 * 同步每日充值活动，领取奖励的剩余次数
 */
public class SingleRechargeInfoHandler implements IProtocolHandler {

	@Override
	public boolean onProtocol(AppObj appObj, Protocol protocol) {

		Player player = (Player) appObj;
		// 检测活动是否开放
		int activityId = Const.ActivityId.SINGLE_RECHARGE_VALUE;
		ActivityTimeCfg timeCfg = ActivityUtil.getCurActivityTimeCfg(activityId);
		if (timeCfg == null) {
			// 活动已关闭
			player.sendError(protocol.getType(), Status.error.ACTIVITY_CLOSE);
			return true;
		}

		SingleRechargeStatus singleRechargeStatus = ActivityUtil.getActivityStatus(player.getPlayerData(), activityId, timeCfg.getStageId(),
		        SingleRechargeStatus.class);
		HPSingleRechargeInfoRet.Builder response = HPSingleRechargeInfoRet.newBuilder();
		// 获取剩余时间
		response.setSurplusTime(timeCfg.calcActivitySurplusTime());
		// 获取所有的领奖信息
		List<SingleRechargeInfo> infoList = singleRechargeStatus.getAllInfoList();
		response.addAllInfo(infoList);
		player.sendProtocol(Protocol.valueOf(HP.code.SINGLE_RECHARGE_INFO_S_VALUE, response));
		return true;
	}

}
