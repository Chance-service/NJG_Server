package com.guaji.game.module.activity.roulette;

import org.guaji.app.AppObj;
import org.guaji.config.ConfigManager;
import org.guaji.net.protocol.Protocol;
import org.guaji.net.protocol.IProtocolHandler;

import com.guaji.game.protocol.Activity.HPRouletteCreditsExchange;
import com.guaji.game.protocol.Activity.HPRouletteCreditsExchangeRet;
import com.guaji.game.config.ActivityTimeCfg;
import com.guaji.game.config.RouletteExchangeCfg;
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
public class RouletteCreditsExchangeHandler implements IProtocolHandler {
	
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
		
		HPRouletteCreditsExchange req = protocol.parseProtocol(HPRouletteCreditsExchange.getDefaultInstance());
		int exchangeCfgId = req.getCfgId();
		RouletteExchangeCfg exchangeCfg = ConfigManager.getInstance().getConfigByKey(RouletteExchangeCfg.class, exchangeCfgId);
		if(exchangeCfg == null){
			player.sendError(protocol.getType(), Status.error.CONFIG_NOT_FOUND);
			return true;
		}
		
		RouletteStatus status = ActivityUtil.getActivityStatus(player.getPlayerData(), activityId, timeCfg.getStageId(), RouletteStatus.class);
		if(status.getCurCredits() < exchangeCfg.getCostCredits()){
			player.sendError(protocol.getType(), Status.error.ROULETTE_CREDITS_LACK);
			return true;
		}
		
		if(status.deductCredits(exchangeCfg.getCostCredits())){
			AwardItems awards = AwardItems.valueOf(exchangeCfg.getExchangeItems());
			awards.rewardTakeAffectAndPush(player, Action.CRAZY_ROULETTE_EXCHANGE,2);
			player.getPlayerData().updateActivity(activityId, timeCfg.getStageId());
			
			BehaviorLogger.log4Platform(player, Action.CRAZY_ROULETTE_EXCHANGE, 
					Params.valueOf("costCredits", exchangeCfg.getCostCredits()), 
					Params.valueOf("exchangeItems", exchangeCfg.getExchangeItems()));
		}
		
		HPRouletteCreditsExchangeRet.Builder ret = HPRouletteCreditsExchangeRet.newBuilder();
		ret.setLeftTime(timeCfg.calcActivitySurplusTime());
		ret.setCurCredits(status.getCurCredits());
		player.sendProtocol(Protocol.valueOf(HP.code.ROULETTE_CREDITS_EXCHANGE_S, ret));
		return true;
	}
}
