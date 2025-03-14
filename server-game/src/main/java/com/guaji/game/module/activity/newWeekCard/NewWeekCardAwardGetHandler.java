package com.guaji.game.module.activity.newWeekCard;


import org.guaji.app.AppObj;
import org.guaji.config.ConfigManager;
import org.guaji.net.protocol.Protocol;
import org.guaji.net.protocol.IProtocolHandler;
import org.guaji.os.GuaJiTime;

import com.guaji.game.protocol.Activity2.HPGetNewWeekCardReward;
import com.guaji.game.protocol.Activity2.NewWeekCard;
import com.guaji.game.config.NewWeekCardCfg;
import com.guaji.game.item.AwardItems;
import com.guaji.game.log.BehaviorLogger.Action;
import com.guaji.game.player.Player;
import com.guaji.game.util.ActivityUtil;
import com.guaji.game.protocol.Const;
import com.guaji.game.protocol.HP;
import com.guaji.game.protocol.Status;

public class NewWeekCardAwardGetHandler implements IProtocolHandler
{
	@Override
	public boolean onProtocol(AppObj appObj, Protocol protocol) 
	{
		
		Player player = (Player) appObj;
		
		NewWeekCardStatus newWeekCardStatus = ActivityUtil.getNewWeekCardStatus(player.getPlayerData());
		
		if(newWeekCardStatus == null) 
		{
			return true;
		}
		
		HPGetNewWeekCardReward request = protocol.parseProtocol(HPGetNewWeekCardReward.getDefaultInstance());
		int newWeekCardId = request.getNewWeekCardId();
				
		NewWeekCardCfg cfg = ConfigManager.getInstance().getConfigByKey(NewWeekCardCfg.class, newWeekCardId);
		

		if(cfg == null){
			player.sendError(protocol.getType(), Status.error.CONFIG_NOT_FOUND);
			return true;
		}
			
		if(!newWeekCardStatus.getNewWeekCardAward(newWeekCardId))
		{
			player.sendError(protocol.getType(), Status.error.ACTIVITY_AWARDS_HAS_GOT_VALUE);
			return false;
		}
		
		AwardItems awardItems = AwardItems.valueOf(cfg.getReward());//获取礼包
		awardItems.rewardTakeAffectAndPush(player, Action.NEW_WEEK_CARD_AWARD,1);//记录领取日志
		
		player.getPlayerData().updateActivity(Const.ActivityId.NEW_WEEK_CARD_VALUE, 0,true);
		
		//获取首充礼包的实体信息并设置新的领取状态
		NewWeekCard.Builder builder = NewWeekCard.newBuilder();
		builder.setWeekCardId(newWeekCardId);
		builder.setActivateFlag(newWeekCardStatus.checkNewWeekCardActivate(newWeekCardId));
		builder.setIsTodayTakeAward(newWeekCardStatus.isRewardToday(newWeekCardId));
		builder.setLeftDays(newWeekCardStatus.getLeftDays(newWeekCardId));
		builder.setShowTime(cfg.getEndTime()-GuaJiTime.getMillisecond());

		//发送服务器礼包协议
		player.sendProtocol(Protocol.valueOf(HP.code.NEW_WEEK_CARD_GET_AWARD_S_VALUE, builder));

		return true;
	}

}