package com.guaji.game.module.activity.fortune;

import org.guaji.app.AppObj;
import org.guaji.net.protocol.Protocol;
import org.guaji.net.protocol.IProtocolHandler;
import org.guaji.os.MyException;
import org.guaji.os.GuaJiRand;

import com.guaji.game.protocol.Activity.HPFortuneInfo;
import com.guaji.game.config.ActivityTimeCfg;
import com.guaji.game.config.FortuneCfg;
import com.guaji.game.item.AwardItems;
import com.guaji.game.log.BehaviorLogger.Action;
import com.guaji.game.player.Player;
import com.guaji.game.util.ActivityUtil;
import com.guaji.game.util.BuilderUtil;
import com.guaji.game.protocol.Const;
import com.guaji.game.protocol.HP;
import com.guaji.game.protocol.Status;

public class FortuneHandler implements IProtocolHandler {
	
	@Override
	public boolean onProtocol(AppObj appObj, Protocol protocol) {
		Player player = (Player) appObj;
		// 检测活动是否开放
		int activityId = Const.ActivityId.FORTUNE_VALUE;
		ActivityTimeCfg timeCfg = ActivityUtil.getCurActivityTimeCfg(activityId);
		if (timeCfg == null) {
			// 活动已关闭
			player.sendError(protocol.getType(), Status.error.ACTIVITY_CLOSE);
			return true;
		}
		
		switch (protocol.getType()) {
			case HP.code.FORTUNE_INFO_C_VALUE:
				onFortuneInfo(player, timeCfg);
				break;
			case HP.code.FORTUNE_REWARD_C_VALUE:
				onFortuneReward(player, timeCfg);
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
	private void onFortuneInfo(Player player, ActivityTimeCfg timeCfg) {
		FortuneStatus fortuneStatus = ActivityUtil.getActivityStatus(player.getPlayerData(), 
				Const.ActivityId.FORTUNE_VALUE, timeCfg.getStageId(), FortuneStatus.class);
		if(fortuneStatus == null) {
			player.sendError(HP.code.FORTUNE_INFO_C_VALUE, Status.error.DATA_NOT_FOUND_VALUE);
			return ;
		}
		
		pushInfo(player, fortuneStatus, timeCfg);
	}

	/**
	 * 碰运气
	 * @param player
	 */
	private void onFortuneReward(Player player, ActivityTimeCfg timeCfg) {
		FortuneStatus fortuneStatus = ActivityUtil.getActivityStatus(player.getPlayerData(), 
				Const.ActivityId.FORTUNE_VALUE, timeCfg.getStageId(), FortuneStatus.class);
		if(fortuneStatus == null) {
			player.sendError(HP.code.FORTUNE_REWARD_C_VALUE, Status.error.DATA_NOT_FOUND_VALUE);
			return ;
		}
		
		FortuneCfg curActiveFortuneCfg = fortuneStatus.getCurActiveFortuneCfg();
		
		if(curActiveFortuneCfg == null) {
			player.sendError(HP.code.FORTUNE_REWARD_C_VALUE, Status.error.FORTUNE_REWARD_TIME_FULL);
			return ;
		}
		
		if(fortuneStatus.getNextRechargeValue() < curActiveFortuneCfg.getNeedRechargeGold()) {
			player.sendError(HP.code.FORTUNE_REWARD_C_VALUE, Status.error.FORTUNE_RECHARGE_NOT_ENOUGH);
			return ;
		}
		
		int gainGold;
		try {
			gainGold = GuaJiRand.randInt(curActiveFortuneCfg.getRewardMin(),curActiveFortuneCfg.getRewardMax());
			AwardItems awardItems = new AwardItems();
			awardItems.addGold(gainGold);
			awardItems.rewardTakeAffectAndPush(player, Action.FORTUNE,1);
		} catch (MyException e) {
			e.printStackTrace();
		}
		
		fortuneStatus.addReward(curActiveFortuneCfg.getId());
		player.getPlayerData().updateActivity(Const.ActivityId.FORTUNE_VALUE, timeCfg.getStageId());
		
		pushInfo(player, fortuneStatus, timeCfg);
	}

	/**
	 * 推送最新信息
	 * @param player
	 * @param fortuneStatus
	 * @param timeCfg
	 */
	private void pushInfo(Player player, FortuneStatus fortuneStatus, ActivityTimeCfg timeCfg) {
		HPFortuneInfo.Builder fortuneBuilder = BuilderUtil.genFortuneInfo(fortuneStatus);
		fortuneBuilder.setLeftTime(timeCfg.calcActivitySurplusTime());
		player.sendProtocol(Protocol.valueOf(HP.code.FORTUNE_INFO_S_VALUE, fortuneBuilder));
	}
}
