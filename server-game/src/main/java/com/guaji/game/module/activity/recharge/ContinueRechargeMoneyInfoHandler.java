package com.guaji.game.module.activity.recharge;

import java.util.Date;

import org.guaji.app.AppObj;
import org.guaji.net.protocol.IProtocolHandler;
import org.guaji.net.protocol.Protocol;

import com.guaji.game.config.ActivityTimeCfg;
import com.guaji.game.player.Player;
import com.guaji.game.protocol.Const;
import com.guaji.game.protocol.HP;
import com.guaji.game.protocol.Status;
import com.guaji.game.protocol.Activity.HPContinueRechargeInfoRet;
import com.guaji.game.protocol.Activity.HPContinueRechargeMoneyInfo;
import com.guaji.game.protocol.Activity.HPContinueRechargeMoneyInfoRet;
import com.guaji.game.util.ActivityUtil;

/**
 * @author 作者 E-mail:Jeremy@163.com
 * @version 创建时间：2019年4月8日 下午4:20:29 类说明
 */
public class ContinueRechargeMoneyInfoHandler implements IProtocolHandler {

	@Override
	public boolean onProtocol(AppObj appObj, Protocol protocol) {
		Player player = (Player) appObj;
		// 检测活动是否开放
		int activityId = Const.ActivityId.CONTINUE_RECHARGE_MONEY_VALUE;
		ActivityTimeCfg timeCfg = ActivityUtil.getCurActivityTimeCfg(activityId);

		// Date registerDate = player.getPlayerData().getPlayerEntity().getCreateTime();
		if (timeCfg == null) {
			// 活动已关闭
			player.sendError(protocol.getType(), Status.error.ACTIVITY_CLOSE);
			return true;
		}

		int surplusTime = ActivityUtil.clacContinueRechargeMoneySurplusTime();

		if (surplusTime == 0) {
			// 活动已关闭
			player.sendError(protocol.getType(), Status.error.ACTIVITY_CLOSE);
			return true;
		}

		ContinueMoneyRechargeStatus continueRechargeStatues = ActivityUtil.getActivityStatus(player.getPlayerData(),
				activityId, timeCfg.getStageId(), ContinueMoneyRechargeStatus.class);
		HPContinueRechargeMoneyInfoRet.Builder ret = HPContinueRechargeMoneyInfoRet.newBuilder();
		ret.setContinueRechargeTotal(continueRechargeStatues.getContinueRechargeMoney());
		ret.addAllGotAwardCfgId(continueRechargeStatues.getGotAwardCfgIds());
		ret.setSurplusTime(surplusTime);
		player.sendProtocol(Protocol.valueOf(HP.code.CONTINUE_RECHARGEMONEY_INFO_S, ret));
		return true;
	}

}
