package com.guaji.game.module.activity.monthcard;


import org.guaji.app.AppObj;
import org.guaji.config.ConfigManager;
import org.guaji.net.protocol.Protocol;
import org.guaji.net.protocol.IProtocolHandler;

import com.guaji.game.protocol.Activity.HPMonthCardAwardGetRet;
import com.guaji.game.config.MonthCardCfg;
import com.guaji.game.item.AwardItems;
import com.guaji.game.log.BehaviorLogger.Action;
import com.guaji.game.player.Player;
import com.guaji.game.util.ActivityUtil;
import com.guaji.game.protocol.Const;
import com.guaji.game.protocol.HP;
import com.guaji.game.protocol.Status;

public class MonthCardAwardGetHandler implements IProtocolHandler
{
	@Override
	public boolean onProtocol(AppObj appObj, Protocol protocol) 
	{
		
		Player player = (Player) appObj;
		
		MonthCardStatus monthCardStatus = ActivityUtil.getMonthCardStatus(player.getPlayerData());
		
		if(monthCardStatus == null) 
		{
			return true;
		}
				
		MonthCardCfg cfg = ConfigManager.getInstance().getConfigByKey(MonthCardCfg.class, 30);//1为唯一月卡标识，不想定义宏了
		
		if(cfg == null){
			player.sendError(protocol.getType(), Status.error.CONFIG_NOT_FOUND);
			return true;
		}
			
		if(!monthCardStatus.getMonthCardAward())
		{
			player.sendError(protocol.getType(), Status.error.NO_MULTIELITE_TIMES_VALUE);
			return false;
		}
		
		AwardItems awardItems = AwardItems.valueOf(cfg.getReward());//获取礼包
		awardItems.rewardTakeAffectAndPush(player, Action.MONTH_CARD_REWARD,1);//记录领取日志
		
		player.getPlayerData().updateActivity(Const.ActivityId.MONTH_CARD_VALUE, 0,true);
		
		//获取首充礼包的实体信息并设置新的领取状态
		HPMonthCardAwardGetRet.Builder builder = HPMonthCardAwardGetRet.newBuilder();
		builder.setMonthCardId(1);//礼包id
		builder.setGetAwardTime(monthCardStatus.getLastRewadTime().getTime());//礼包领取时间（状态）
		//发送服务器礼包协议
		player.sendProtocol(Protocol.valueOf(HP.code.MONTHCARD_AWARD_S, builder));

		return true;
	}

}
