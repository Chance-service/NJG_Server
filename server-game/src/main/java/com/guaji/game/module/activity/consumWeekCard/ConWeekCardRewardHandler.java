package com.guaji.game.module.activity.consumWeekCard;

import org.guaji.app.AppObj;
import org.guaji.config.ConfigManager;
import org.guaji.net.protocol.IProtocolHandler;
import org.guaji.net.protocol.Protocol;

import com.guaji.game.config.ActivityTimeCfg;
import com.guaji.game.config.ConsumeWeekCardCfg;
import com.guaji.game.config.WeekCardCfg;
import com.guaji.game.item.AwardItems;
import com.guaji.game.log.BehaviorLogger.Action;
import com.guaji.game.log.BehaviorLogger.Params;
import com.guaji.game.player.Player;
import com.guaji.game.protocol.Const;
import com.guaji.game.protocol.HP;
import com.guaji.game.protocol.Status;
import com.guaji.game.protocol.Activity4.ConsumeWeekCardInfoRet;
import com.guaji.game.util.ActivityUtil;
import com.guaji.game.util.BuilderUtil;
import com.guaji.game.util.TapDBUtil.TapDBSource;

public class ConWeekCardRewardHandler implements IProtocolHandler {

	@Override
	public boolean onProtocol(AppObj appObj, Protocol protocol) {
		Player player = (Player)appObj;
		int activityId = Const.ActivityId.CONSUME_WEEK_CARD_VALUE;
		ActivityTimeCfg activityTimeCfg = ActivityUtil.getCurActivityTimeCfg(activityId); 
		if(activityTimeCfg == null) {
			player.sendError(protocol.getType(), Status.error.ACTIVITY_CLOSE);
			return true;
		}
		
		ConWeekCardStatus weekCardStatus = ActivityUtil.getActivityStatus(player.getPlayerData(),
				activityId, activityTimeCfg.getStageId(), ConWeekCardStatus.class);
		ConsumeWeekCardCfg weekCardCfg = ConfigManager.getInstance().getConfigByKey(ConsumeWeekCardCfg.class,
				weekCardStatus.getCurrentActiveCfgId());
		
		if(weekCardCfg == null) {
			// 尚未购买周卡
			player.sendError(protocol.getType(), Status.error.NO_WEEK_CARD);
			return true;
		}
	
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
		// 是否為領取購買禮
		//boolean buyReward = (!weekCardStatus.isBuyReward());
		// 设置今日已领取
		weekCardStatus.putRewardToday();
		player.getPlayerData().updateActivity(activityId, activityTimeCfg.getStageId());
		
		// 发奖励
		AwardItems awardItems = AwardItems.valueOf(weekCardCfg.getEverydayAwards());
//		if (buyReward) {  // 有購買禮
//			String info = weekCardCfg.getBuyReward();
//			awardItems.initByString(info);
//		}
		awardItems.rewardTakeAffectAndPush(player, Action.CONSUME_WEEK_CARD_DAILY_REWARD,2,TapDBSource.Month_Card,Params.valueOf("activityId", activityId));
		
		// 同步活动状态
		ConsumeWeekCardInfoRet.Builder ret = BuilderUtil.genConsumeWeekCardInfo(weekCardStatus, activityTimeCfg);
		player.sendProtocol(Protocol.valueOf(HP.code.CONSUME_WEEK_CARD_REWARD_S_VALUE, ret));
		return true;
	}

}
