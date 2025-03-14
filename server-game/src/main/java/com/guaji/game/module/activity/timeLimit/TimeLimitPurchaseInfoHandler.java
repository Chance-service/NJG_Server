package com.guaji.game.module.activity.timeLimit;


import org.guaji.app.AppObj;
import org.guaji.config.ConfigManager;
import org.guaji.net.protocol.Protocol;
import org.guaji.net.protocol.IProtocolHandler;

import com.guaji.game.protocol.Activity.TimeLimitItem;
import com.guaji.game.protocol.Activity.TimeLimitPurchase;
import com.guaji.game.config.ActivityTimeCfg;
import com.guaji.game.config.TimeLimitCfg;
import com.guaji.game.entity.ServerTimeLimitEntity;
import com.guaji.game.player.Player;
import com.guaji.game.util.ActivityUtil;
import com.guaji.game.protocol.Const;
import com.guaji.game.protocol.HP;
import com.guaji.game.protocol.Status;

public class TimeLimitPurchaseInfoHandler implements IProtocolHandler {

	@Override
	public boolean onProtocol(AppObj appObj, Protocol protocol) {
		Player player = (Player) appObj;
		// 检测活动是否开放
		int activityId = Const.ActivityId.TIME_LIMIT_PURCHASE_VALUE;
		ActivityTimeCfg timeCfg = ActivityUtil.getCurActivityTimeCfg(activityId);
		if(timeCfg == null){
//			//七夕限时限购
//			int activityId2  = Const.ActivityId.LIMIT_RECHARGE_VALUE;
//			ActivityTimeCfg timeCfg2 = ActivityUtil.getCurActivityTimeCfg(activityId2);
//			if(timeCfg2 == null)
//			{
			// 活动已关闭
				player.sendError(protocol.getType(), Status.error.ACTIVITY_CLOSE);
				return true;
//			}
		
		}
		
		PersonalTimeLimitStatus personalTimeLimitStatus = ActivityUtil.getActivityStatus(
				player.getPlayerData(), activityId, timeCfg.getStageId(), PersonalTimeLimitStatus.class);
		
		if(personalTimeLimitStatus == null) {
			player.sendError(protocol.getType(), Status.error.DATA_NOT_FOUND_VALUE);
			return true;
		}
		
		ServerTimeLimitEntity timeLimitEntity = TimeLimitManager.getInstance().getCurTimeLimitEntity();
		
		if(timeLimitEntity == null) {
			player.sendError(protocol.getType(), Status.error.DATA_NOT_FOUND_VALUE);
			return true;
		}
		
		pushTimeLimitInfo(player, personalTimeLimitStatus, timeLimitEntity, timeCfg);
		
		return true;
	}

	public static void pushTimeLimitInfo(Player player,PersonalTimeLimitStatus personalTimeLimitStatus,ServerTimeLimitEntity timeLimitEntity,ActivityTimeCfg timeCfg) {
		TimeLimitPurchase.Builder builder = TimeLimitPurchase.newBuilder();
		for(TimeLimitCfg timeLimitCfg : ConfigManager.getInstance().getConfigMap(TimeLimitCfg.class).values()) {
			
			
			TimeLimitItem.Builder itemBuilder = TimeLimitItem.newBuilder();
			itemBuilder.setId(timeLimitCfg.getId());
			if(timeLimitCfg.getPersonalLimitType() == 2) {
				itemBuilder.setBuyTimes(personalTimeLimitStatus.getTodayBuyTimes(timeLimitCfg.getId()));
			}else{
				itemBuilder.setBuyTimes(personalTimeLimitStatus.getTotalBuyTimes(timeLimitCfg.getId()));
			}
			
			if(timeLimitCfg.getServerLimitType() == 3) {
				itemBuilder.setLeftBuyTimes(-1);	
			}else if(timeLimitCfg.getServerLimitType() == 2){
				itemBuilder.setLeftBuyTimes(timeLimitCfg.getServerLimit() - timeLimitEntity.getTodayBuyTimes(timeLimitCfg.getId()));
			}else if(timeLimitCfg.getServerLimitType() == 1){
				itemBuilder.setLeftBuyTimes(timeLimitCfg.getServerLimit() - timeLimitEntity.getTotalBuyTimes(timeLimitCfg.getId()));
			}
			builder.setLeftTime(timeCfg.calcActivitySurplusTime());
			builder.addTimeLimitItems(itemBuilder);
		}
		
		player.sendProtocol(Protocol.valueOf(HP.code.TIME_LIMIT_PURCHASE_INFO_S_VALUE, builder));
	}
	
}
