package com.guaji.game.module.activity.recharge;

import com.guaji.game.config.ActivityTimeCfg;
import com.guaji.game.config.ContinueRecharge131Cfg;
import com.guaji.game.item.AwardItems;
import com.guaji.game.log.BehaviorLogger.Action;
import com.guaji.game.player.Player;
import com.guaji.game.protocol.Activity4.*;
import com.guaji.game.protocol.Const;
import com.guaji.game.protocol.HP;
import com.guaji.game.protocol.Status;
import com.guaji.game.util.ActivityUtil;
import org.guaji.app.AppObj;
import org.guaji.config.ConfigManager;
import org.guaji.net.protocol.IProtocolHandler;
import org.guaji.net.protocol.Protocol;

import java.util.Date;

public class ContinueRechargeDays131AwardHandler implements IProtocolHandler  {

	@Override
	public boolean onProtocol(AppObj appObj, Protocol protocol) {
		Player player = (Player) appObj;
		
		// 检测活动是否开放
		int activityId = Const.ActivityId.CONTINUE_RECHARGE131_VALUE;
		ActivityTimeCfg timeCfg = ActivityUtil.getCurActivityTimeCfg(activityId);

		if(timeCfg == null ){
			// 活动已关闭
			player.sendError(protocol.getType(), Status.error.ACTIVITY_CLOSE);
			return true;
		}
		int surplusTime = timeCfg.calcActivitySurplusTime();
		if ( surplusTime <= 0){
			// 活动已关闭
			player.sendError(protocol.getType(), Status.error.ACTIVITY_CLOSE);
			return true;
		}
		
		HPGetContinueRecharge131Award request = protocol.parseProtocol(HPGetContinueRecharge131Award.getDefaultInstance());
		int cfgId = request.getAwardCfgId();
		ContinueRecharge131Cfg cfg = ConfigManager.getInstance().getConfigByKey(ContinueRecharge131Cfg.class, cfgId);
		if(cfg == null){
			player.sendError(protocol.getType(), Status.error.CONFIG_NOT_FOUND);
			return true;
		}
		
		ContinueRechargeDays131Status continueRecharge131Status = ActivityUtil.getActivityStatus(player.getPlayerData(),
				activityId, timeCfg.getStageId(), ContinueRechargeDays131Status.class);
		if(continueRecharge131Status.getContinueRechargeDays() < cfg.getDay()){
			// 连续充值天数未达到
			player.sendError(protocol.getType(), Status.error.RECHARGE_NUM_LACK);
			return true;
		}
		
		if(continueRecharge131Status.isAlreadyGot(cfgId)){
			// 活动奖励已领取
			player.sendError(protocol.getType(), Status.error.ACTIVITY_AWARDS_HAS_GOT);
			return true;
		}
		
		// 发放奖励并推送前端
		AwardItems awardItems = AwardItems.valueOf(cfg.getAwards());
		awardItems.rewardTakeAffectAndPush(player, Action.CONTINUE_RECHARGE131_AWARDS,2);
		
		continueRecharge131Status.addGotAwardCfgId(cfgId);
		player.getPlayerData().updateActivity(Const.ActivityId.CONTINUE_RECHARGE131_VALUE, timeCfg.getStageId());
		
		HPGetContinueRecharge131AwardRet.Builder ret = HPGetContinueRecharge131AwardRet.newBuilder();
		ret.setGotAwardCfgId(cfgId);
		ret.setSurplusTime(surplusTime);
		player.sendProtocol(Protocol.valueOf(HP.code.GET_CONTINUE_RECHARGE131_AWARD_S, ret));
		return true;
	}
}
