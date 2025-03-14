package com.guaji.game.module.activity.treasureRaider;

import org.guaji.app.AppObj;
import org.guaji.net.protocol.Protocol;
import org.guaji.net.protocol.IProtocolHandler;

import com.guaji.game.protocol.Activity.HPTreasureBoxConfirmRet;
import com.guaji.game.config.ActivityTimeCfg;
import com.guaji.game.item.AwardItems;
import com.guaji.game.log.BehaviorLogger.Action;
import com.guaji.game.player.Player;
import com.guaji.game.util.ActivityUtil;
import com.guaji.game.protocol.Const;
import com.guaji.game.protocol.HP;
import com.guaji.game.protocol.Status;

public class TreasureRaiderBoxAwardsHandler implements IProtocolHandler{
	@Override
	public boolean onProtocol(AppObj appObj, Protocol protocol) {
		Player player = (Player) appObj;
		// 检测活动是否开放
		int activityId = Const.ActivityId.TREASURE_RAIDER_VALUE;
		ActivityTimeCfg timeCfg = ActivityUtil.getCurActivityTimeCfg(activityId);
		if(timeCfg == null){
			// 活动已关闭
			player.sendError(protocol.getType(), Status.error.ACTIVITY_CLOSE);
			return true;
		}
		
		TreasureRaiderStatus treasureRaiderStatus =  ActivityUtil.getActivityStatus(player.getPlayerData(), activityId, timeCfg.getStageId(), TreasureRaiderStatus.class);
		
		if(treasureRaiderStatus.getLastBoxAwards().equals("")){
			player.sendError(protocol.getType(), Status.error.ACTIVITY_AWARDS_HAS_GOT);
			return true;
		}
		
		AwardItems awards = AwardItems.valueOf(treasureRaiderStatus.getLastBoxAwards());
		awards.rewardTakeAffectAndPush(player, Action.TREASURE_RAIDER_SEARCH,1);
		treasureRaiderStatus.setLastBoxAwards("");
		player.getPlayerData().updateActivity(activityId, timeCfg.getStageId());
		
		HPTreasureBoxConfirmRet.Builder ret = HPTreasureBoxConfirmRet.newBuilder();
		ret.setVersion(1);
		player.sendProtocol(Protocol.valueOf(HP.code.TREASURE_RAIDER_CONFIRM_S, ret));
		return true;
	}
}
