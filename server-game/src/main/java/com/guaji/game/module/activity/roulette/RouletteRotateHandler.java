package com.guaji.game.module.activity.roulette;

import org.guaji.app.AppObj;
import org.guaji.net.protocol.Protocol;
import org.guaji.net.protocol.IProtocolHandler;

import com.guaji.game.protocol.Activity.HPRouletteRotate;
import com.guaji.game.protocol.Activity.HPRouletteRotateRet;
import com.guaji.game.config.ActivityTimeCfg;
import com.guaji.game.config.RouletteCfg;
import com.guaji.game.item.AwardItems;
import com.guaji.game.log.BehaviorLogger;
import com.guaji.game.log.BehaviorLogger.Action;
import com.guaji.game.log.BehaviorLogger.Params;
import com.guaji.game.player.Player;
import com.guaji.game.util.ActivityUtil;
import com.guaji.game.protocol.Const;
import com.guaji.game.protocol.HP;
import com.guaji.game.protocol.Status;

/**
 * 疯狂转轮盘
 */
public class RouletteRotateHandler implements IProtocolHandler{
	@Override
	public boolean onProtocol(AppObj appObj, Protocol protocol) {
		Player player = (Player) appObj;
		// 检测活动是否开放
		int activityId = Const.ActivityId.CRAZY_ROULETTE_VALUE;
		ActivityTimeCfg timeCfg = ActivityUtil.getCurActivityTimeCfg(activityId);
		if (timeCfg == null) {
			// 活动已关闭
			player.sendError(protocol.getType(), Status.error.ACTIVITY_CLOSE);
			return true;
		}
		
		HPRouletteRotate req = protocol.parseProtocol(HPRouletteRotate.getDefaultInstance());
		int rotateTimes = req.getTimes();
		// 1次或10次
		if(rotateTimes != 1){
			rotateTimes = 10;
		}
		
		RouletteStatus status = ActivityUtil.getActivityStatus(player.getPlayerData(), activityId, timeCfg.getStageId(), RouletteStatus.class);
		if(!status.deductRouletteTimes(rotateTimes)){
			// 次数不足
			player.sendError(protocol.getType(), Status.error.ROULETTE_TIMES_LACK);
			return true;
		}
		player.getPlayerData().updateActivity(activityId, timeCfg.getStageId());
		
		String awardsStr = "";
		AwardItems totalAwards = new AwardItems();
		HPRouletteRotateRet.Builder ret = HPRouletteRotateRet.newBuilder();
		for(int i = 0 ; i < rotateTimes; i++){
			RouletteCfg cfg = RouletteCfg.rollAwardCfg();
			if(cfg != null){
				ret.addAwardsCfgId(cfg.getId());
				AwardItems awards = AwardItems.valueOf(cfg.getItems());
				totalAwards.addItemInfos(awards.getAwardItemInfos());
				
				// 构造奖励物品字符串
				awardsStr += cfg.getItems();
				awardsStr += ";";
			}
		}
		// 推送并不飘字
		totalAwards.rewardTakeAffectAndPush(player, Action.CRAZY_ROULETTE, 2);
		ret.setCurCredits(status.getCurCredits());
		ret.setRouletteLeftTimes(status.getRouletteLeftTimes());
		player.sendProtocol(Protocol.valueOf(HP.code.ROULETTE_ROTATE_S, ret));
		
		BehaviorLogger.log4Platform(player, Action.CRAZY_ROULETTE, Params.valueOf("rotateTimes", rotateTimes), Params.valueOf("awardItems", awardsStr));
		return true;
	}
}
