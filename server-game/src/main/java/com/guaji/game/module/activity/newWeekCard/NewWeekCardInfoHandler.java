package com.guaji.game.module.activity.newWeekCard;

import java.util.Map;

import org.guaji.app.AppObj;
import org.guaji.config.ConfigManager;
import org.guaji.net.protocol.Protocol;
import org.guaji.net.protocol.IProtocolHandler;
import org.guaji.os.GuaJiTime;

import com.guaji.game.protocol.Activity2.HPGetNewWeekCardInfo;
import com.guaji.game.protocol.Activity2.NewWeekCard;
import com.guaji.game.config.NewWeekCardCfg;
import com.guaji.game.player.Player;
import com.guaji.game.util.ActivityUtil;
import com.guaji.game.protocol.Const;
import com.guaji.game.protocol.HP;

public class NewWeekCardInfoHandler implements IProtocolHandler {

	@Override
	public boolean onProtocol(AppObj appObj, Protocol protocol) 
	{
		
		Player player = (Player) appObj;
		
		NewWeekCardStatus newWeekCardStatus = ActivityUtil.getNewWeekCardStatus(player.getPlayerData());
		
		if(newWeekCardStatus == null) 
		{
			return true;
		}
			
		
		if(newWeekCardStatus.refreshNewWeekCard(player.getPlayerData()))
		{
			player.getPlayerData().updateActivity(Const.ActivityId.NEW_WEEK_CARD_VALUE, 0,true);
		}
				
	
		Map<Object, NewWeekCardCfg> cfgList = ConfigManager.getInstance().getConfigMap(NewWeekCardCfg.class);
		
		HPGetNewWeekCardInfo.Builder builder = HPGetNewWeekCardInfo.newBuilder();
		
		int i = 0;
		builder.setNewWeekCardCount(i);
		for(NewWeekCardCfg cfg:cfgList.values())
		{
			if(GuaJiTime.getMillisecond() < cfg.getStartTime() || GuaJiTime.getMillisecond() > cfg.getEndTime())
			{
				if(newWeekCardStatus.getLeftDays(cfg.getId())<=0)
				{
					continue;
				}
			}
			
			NewWeekCard.Builder cardBuilder = NewWeekCard.newBuilder();
			cardBuilder.setWeekCardId(cfg.getId());
			cardBuilder.setActivateFlag(newWeekCardStatus.checkNewWeekCardActivate(cfg.getId()));
			cardBuilder.setIsTodayTakeAward(newWeekCardStatus.isRewardToday(cfg.getId()));
			cardBuilder.setLeftDays(newWeekCardStatus.getLeftDays(cfg.getId()));
			cardBuilder.setShowTime(cfg.getEndTime()-GuaJiTime.getMillisecond());
			i++;
			builder.setNewWeekCardCount(i);
			builder.addNewWeekCardInfoList(cardBuilder);
			
		}

		player.sendProtocol(Protocol.valueOf(HP.code.NEW_WEEK_CARD_INFO_S_VALUE,builder));
		
		return true;
	}

}
