package com.guaji.game.module.activity.commendationTribe;

import org.guaji.app.AppObj;
import org.guaji.config.ConfigManager;
import org.guaji.net.protocol.Protocol;

import org.guaji.net.protocol.IProtocolHandler;
import org.guaji.os.GuaJiRand;

import com.guaji.game.protocol.Activity.HPCommendationTribe;
import com.guaji.game.protocol.Activity.HPCommendationTribeLuck;
import com.guaji.game.config.ActivityTimeCfg;
import com.guaji.game.config.CommentdationTribeCfg;
import com.guaji.game.item.AwardItems;
import com.guaji.game.log.BehaviorLogger;
import com.guaji.game.log.BehaviorLogger.Action;
import com.guaji.game.log.BehaviorLogger.Params;
import com.guaji.game.player.Player;
import com.guaji.game.util.ActivityUtil;
import com.guaji.game.util.BuilderUtil;
import com.guaji.game.protocol.Const;
import com.guaji.game.protocol.HP;
import com.guaji.game.protocol.Status;

/**
 * 部族的嘉奖
 * @author Administrator
 */

public class CommendationTribeHandler implements IProtocolHandler {
	
	@Override
	public boolean onProtocol(AppObj appObj, Protocol protocol) {
		
		Player player = (Player) appObj;
		// 检测活动是否开放
		int activityId = Const.ActivityId.COMMENDATION_TRIBE_VALUE;
		ActivityTimeCfg timeCfg = ActivityUtil.getCurActivityTimeCfg(activityId);
		if (timeCfg == null) {
			// 活动已关闭
			player.sendError(protocol.getType(), Status.error.ACTIVITY_CLOSE);
			return true;
		}
		
		switch (protocol.getType()) {
			case HP.code.COMMENDATION_TRIBE_INFO_C_VALUE:
				onCommendationInfo(player, timeCfg);
				break;
			case HP.code.COMMENDATION_TRIBE_LUCK_C_VALUE:
				onCommendationLuck(player, timeCfg);
				break;
			default:
				break;
		}
		return true;
	}
	
	/**
	 * 查询活动信息
	 * @param player
	 */
	private void onCommendationInfo(Player player, ActivityTimeCfg timeCfg) {
		CommendationTribeStatus commendationTribeStatus = ActivityUtil.getActivityStatus(player.getPlayerData(), 
				Const.ActivityId.COMMENDATION_TRIBE_VALUE, timeCfg.getStageId(), CommendationTribeStatus.class);
		if(commendationTribeStatus == null) {
			player.sendError(HP.code.COMMENDATION_TRIBE_INFO_C_VALUE, Status.error.DATA_NOT_FOUND_VALUE);
			return ;
		}
		
		HPCommendationTribe.Builder commendationBuilder = BuilderUtil.genCommendationTribe(commendationTribeStatus);
		commendationBuilder.setLeftTime(timeCfg.calcActivitySurplusTime());
		
		player.sendProtocol(Protocol.valueOf(HP.code.COMMENDATION_TRIBE_INFO_S_VALUE,commendationBuilder));
	}

	/**
	 * 碰运气
	 * @param player
	 */
	private void onCommendationLuck(Player player, ActivityTimeCfg timeCfg) {
		CommendationTribeStatus commendationTribeStatus = ActivityUtil.getActivityStatus(player.getPlayerData(), 
				Const.ActivityId.COMMENDATION_TRIBE_VALUE, timeCfg.getStageId(), CommendationTribeStatus.class);
		if(commendationTribeStatus == null) {
			player.sendError(HP.code.COMMENDATION_TRIBE_INFO_C_VALUE, Status.error.DATA_NOT_FOUND_VALUE);
			return ;
		}
		
		CommentdationTribeCfg commentdationTribeCfg = ConfigManager.getInstance().getConfigByKey(CommentdationTribeCfg.class, commendationTribeStatus.getCurStage());
		if(commentdationTribeCfg == null) {
			player.sendError(HP.code.COMMENDATION_TRIBE_INFO_C_VALUE, Status.error.CONFIG_NOT_FOUND_VALUE);
			return ;
		}
		
		if(commendationTribeStatus.getLeftCount() < commentdationTribeCfg.getScoreCost()) {
			player.sendError(HP.code.COMMENDATION_TRIBE_INFO_C_VALUE, Status.error.COMMENDATION_USE_COUNT_NOT_ENOUGH);
			return ;
		}
		
		commendationTribeStatus.setCurUseTimes(commendationTribeStatus.getCurUseTimes() + commentdationTribeCfg.getScoreCost());
		
		int rand = GuaJiRand.randInt(10000);
		boolean isPass = false;
		if(rand < commentdationTribeCfg.getPassRate()) {
			isPass = true;
		} else {
			// 增加幸运值
			commendationTribeStatus.addLuckyValue(commentdationTribeCfg.getPerClickLuckyAdd());
			// 检测是否运气值满
			if(commendationTribeStatus.getLuckyValue() >= commentdationTribeCfg.getNeedLuckyValue()) {
				isPass = true;
			}
		}
		String awardsStr = "";
		if(isPass) {
			// 跳到下一个阶段
			commendationTribeStatus.pass();
			
			HPCommendationTribeLuck.Builder tribeBuilder = HPCommendationTribeLuck.newBuilder();
			tribeBuilder.setRewards(commentdationTribeCfg.getAwards());
			player.sendProtocol(Protocol.valueOf(HP.code.COMMENDATION_TRIBE_LUCK_S_VALUE,tribeBuilder));
			
			AwardItems awardItems = AwardItems.valueOf(commentdationTribeCfg.getAwards());
			awardItems.rewardTakeAffectAndPush(player, Action.COMMENDATION_RWARD,2);
			awardsStr = awardItems.toString();
		}
		
		player.getPlayerData().updateActivity(Const.ActivityId.COMMENDATION_TRIBE_VALUE, timeCfg.getStageId());
		pushInfo(player, commendationTribeStatus, timeCfg);
		
		BehaviorLogger.log4Platform(player, Action.COMMENDATION_RWARD, 
				Params.valueOf("curStage", commentdationTribeCfg.getId()),
				Params.valueOf("costScore", commentdationTribeCfg.getScoreCost()), 
				Params.valueOf("isSucc", isPass), Params.valueOf("awards", awardsStr));
	}

	private void pushInfo(Player player, CommendationTribeStatus commendationTribeStatus, ActivityTimeCfg timeCfg) {
		HPCommendationTribe.Builder commendationBuilder = BuilderUtil.genCommendationTribe(commendationTribeStatus);
		commendationBuilder.setLeftTime(timeCfg.calcActivitySurplusTime());
		
		player.sendProtocol(Protocol.valueOf(HP.code.COMMENDATION_TRIBE_INFO_S_VALUE,commendationBuilder));
	}
}
