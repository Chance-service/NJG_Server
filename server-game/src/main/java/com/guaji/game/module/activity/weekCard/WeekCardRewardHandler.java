package com.guaji.game.module.activity.weekCard;

import org.guaji.app.AppObj;
import org.guaji.config.ConfigManager;
import org.guaji.msg.Msg;
import org.guaji.net.protocol.Protocol;
import org.guaji.net.protocol.IProtocolHandler;

import com.guaji.game.protocol.Activity.HPWeekCardInfoRet;
import com.guaji.game.GsApp;
import com.guaji.game.config.ActivityTimeCfg;
import com.guaji.game.config.WeekCardCfg;
import com.guaji.game.item.AwardItems;
import com.guaji.game.log.BehaviorLogger.Action;
import com.guaji.game.player.Player;
import com.guaji.game.util.ActivityUtil;
import com.guaji.game.util.BuilderUtil;
import com.guaji.game.util.GsConst;
import com.guaji.game.protocol.Const;
import com.guaji.game.protocol.HP;
import com.guaji.game.protocol.Status;

public class WeekCardRewardHandler implements IProtocolHandler {

	@Override
	public boolean onProtocol(AppObj appObj, Protocol protocol) {
		Player player = (Player)appObj;
		int activityId = Const.ActivityId.WEEK_CARD_VALUE;
		ActivityTimeCfg activityTimeCfg = ActivityUtil.getCurActivityTimeCfg(activityId); 
		if(activityTimeCfg == null) {
			player.sendError(protocol.getType(), Status.error.ACTIVITY_CLOSE);
			return true;
		}
		
		WeekCardStatus weekCardStatus = ActivityUtil.getActivityStatus(player.getPlayerData(),
				activityId, activityTimeCfg.getStageId(), WeekCardStatus.class);
		WeekCardCfg weekCardCfg = ConfigManager.getInstance().getConfigByKey(WeekCardCfg.class,
				weekCardStatus.getCurrentActiveCfgId());
		
		if(weekCardCfg == null) {
			// 尚未购买周卡
			player.sendError(protocol.getType(), Status.error.NO_WEEK_CARD);
			return true;
		}
		 
		//只能当天领取此处逻辑不需要
		/*
		if(!weekCardStatus.isBetweenReward()) {
			// 周卡奖励已全部领取
			player.sendError(protocol.getType(), Status.error.ACTIVITY_AWARDS_HAS_GOT);
			return true;
		}
		*/
		
		//已过期不能领奖
		if(weekCardStatus.getLeftDays()<=0)
		{
			player.sendError(protocol.getType(), Status.error.ACTIVITY_AWARDS_HAS_GOT);
			return true;
		}
		
		if(weekCardStatus.isRewardToday()) {
			player.sendError(protocol.getType(), Status.error.ACTIVITY_AWARDS_HAS_GOT);
			return true;
		}
		
		// 设置今日已领取
		weekCardStatus.putRewardToday();
		player.getPlayerData().updateActivity(activityId, activityTimeCfg.getStageId());
		
		// 发奖励
		AwardItems awardItems = AwardItems.valueOf(weekCardCfg.getEverydayAwards());
		awardItems.rewardTakeAffectAndPush(player, Action.WEEK_CARD_DAILY_REWARD,1);
		
		// 是否所有奖励都已领取
		/*
		if(ActivityUtil.isCancelShowInClient(activityId, activityTimeCfg.getStageId(), player.getPlayerData())){
			Msg msg = Msg.valueOf(GsConst.MsgType.ALL_ACTIVITY_AWARDS_GOT, player.getXid());
			GsApp.getInstance().postMsg(msg);
		}
		*/
		
		// 同步活动状态
		HPWeekCardInfoRet.Builder ret = BuilderUtil.genWeekCardInfo(weekCardStatus, activityTimeCfg);
		player.sendProtocol(Protocol.valueOf(HP.code.WEEK_CARD_INFO_S_VALUE, ret));
		return true;
	}
}
