package com.guaji.game.module.activity.recharge;


import org.guaji.app.AppObj;
import org.guaji.config.ConfigManager;
import org.guaji.net.protocol.IProtocolHandler;
import org.guaji.net.protocol.Protocol;

import com.guaji.game.config.ActivityTimeCfg;
import com.guaji.game.config.ContinueRechargeMoneyCfg;
import com.guaji.game.item.AwardItems;
import com.guaji.game.log.BehaviorLogger.Action;
import com.guaji.game.player.Player;
import com.guaji.game.protocol.Const;
import com.guaji.game.protocol.HP;
import com.guaji.game.protocol.Status;
import com.guaji.game.protocol.Activity.HPGetContinueRechargeMoneyAward;
import com.guaji.game.protocol.Activity.HPGetContinueRechargeMoneyAwardRet;
import com.guaji.game.util.ActivityUtil;

/**
 * @author 作者 E-mail:Jeremy@163.com
 * @version 创建时间：2019年4月8日 下午4:20:49 类说明
 */
public class ContinueRechargeMoneyAwardHandler implements IProtocolHandler {

	@Override
	public boolean onProtocol(AppObj appObj, Protocol protocol) {
		Player player = (Player) appObj;

		// 检测活动是否开放
		int activityId = Const.ActivityId.CONTINUE_RECHARGE_MONEY_VALUE;
		ActivityTimeCfg timeCfg = ActivityUtil.getCurActivityTimeCfg(activityId);

		if (timeCfg == null) {
			// 活动已关闭
			player.sendError(protocol.getType(), Status.error.ACTIVITY_CLOSE);
			return true;
		}

		HPGetContinueRechargeMoneyAward request = protocol
				.parseProtocol(HPGetContinueRechargeMoneyAward.getDefaultInstance());
		int cfgId = request.getAwardCfgId();
		ContinueRechargeMoneyCfg cfg = ConfigManager.getInstance().getConfigByKey(ContinueRechargeMoneyCfg.class,
				cfgId);
		if (cfg == null) {
			player.sendError(protocol.getType(), Status.error.CONFIG_NOT_FOUND);
			return true;
		}

		ContinueMoneyRechargeStatus continueRechargeStatues = ActivityUtil.getActivityStatus(player.getPlayerData(),
				activityId, timeCfg.getStageId(), ContinueMoneyRechargeStatus.class);
		if (continueRechargeStatues.getContinueRechargeMoney() < cfg.getnTotalMoney()) {
			// 连续充值金额未达到
			player.sendError(protocol.getType(), Status.error.RECHARGE_NUM_LACK);
			return true;
		}

		if (continueRechargeStatues.isAlreadyGot(cfgId)) {
			// 活动奖励已领取
			player.sendError(protocol.getType(), Status.error.ACTIVITY_AWARDS_HAS_GOT);
			return true;
		}

		// 发放奖励并推送前端
		AwardItems awardItems = AwardItems.valueOf(cfg.getAwards());
		awardItems.rewardTakeAffectAndPush(player, Action.CONTINUE_RECHARGEMONEY_AWARDS, 2);

		continueRechargeStatues.addGotAwardCfgId(cfgId);
		player.getPlayerData().updateActivity(Const.ActivityId.CONTINUE_RECHARGE_MONEY_VALUE, timeCfg.getStageId());

		HPGetContinueRechargeMoneyAwardRet.Builder ret = HPGetContinueRechargeMoneyAwardRet.newBuilder();
		int surplusTime = ActivityUtil.clacContinueRechargeMoneySurplusTime();
		ret.setGotAwardCfgId(cfgId);
		ret.setSurplusTime(surplusTime);
		player.sendProtocol(Protocol.valueOf(HP.code.GET_CONTINUE_RECHARGEMONEY_AWARD_S_VALUE, ret));
		return true;
	}

}
