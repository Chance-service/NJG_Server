package com.guaji.game.module.activity.wordsexchange;

import org.guaji.app.AppObj;
import org.guaji.net.protocol.Protocol;
import org.guaji.net.protocol.IProtocolHandler;

import com.guaji.game.protocol.Activity.HPWordsExchange;
import com.guaji.game.protocol.Activity.HPWordsExchangeInfo;
import com.guaji.game.config.ActivityTimeCfg;
import com.guaji.game.config.WordsExchangeCfg;
import com.guaji.game.item.ConsumeItems;
import com.guaji.game.log.BehaviorLogger.Action;
import com.guaji.game.player.Player;
import com.guaji.game.util.ActivityUtil;
import com.guaji.game.protocol.Const;
import com.guaji.game.protocol.HP;
import com.guaji.game.protocol.Status;

public class WordsExchangeHandler implements IProtocolHandler {
	
	@Override
	public boolean onProtocol(AppObj appObj, Protocol protocol) {
		Player player = (Player) appObj;
		// 检测活动是否开放
		int activityId = Const.ActivityId.WORDS_EXCHANGE_VALUE;
		ActivityTimeCfg timeCfg = ActivityUtil.getCurActivityTimeCfg(activityId);
		if(timeCfg == null){
			// 活动已关闭
			player.sendError(protocol.getType(), Status.error.ACTIVITY_CLOSE);
			return true;
		}
		
		HPWordsExchange wordsExchange = protocol.parseProtocol(HPWordsExchange.getDefaultInstance());
		int type = wordsExchange.getType();
		int itemId = 0;
		if(wordsExchange.hasItemId()) {
			itemId = wordsExchange.getItemId();
		}
		if(!WordsExchangeCfg.getAllTypes().contains(type) || (type == 4 && itemId == 0) /* 单字兑换必须有itemId*/) {
			player.sendError(protocol.getType(), Status.error.PARAMS_INVALID_VALUE);
			return true;
		}
		
		WordsExchangeStatus wordsExchangeStatus = ActivityUtil.getActivityStatus(player.getPlayerData(), activityId, timeCfg.getStageId(), WordsExchangeStatus.class);
		if(wordsExchangeStatus == null) {
			player.sendError(protocol.getType(), Status.error.DATA_NOT_FOUND);
			return true;
		}
		
		WordsExchangeCfg wordsExchangeCfg = WordsExchangeCfg.getWordsExchangeCfg(type, itemId);
		if(wordsExchangeCfg == null) {
			player.sendError(protocol.getType(), Status.error.CONFIG_NOT_FOUND);
			return true;
		}
		
		if(wordsExchangeCfg.getDailyLimit() > 0 && wordsExchangeStatus.getLeftExchangeTimes(type) <=0) {
			player.sendError(protocol.getType(), Status.error.DAYLI_TIMES_LIMIT);
			return true;
		}
		
		ConsumeItems consumeItems = ConsumeItems.valueOf();
		if(!consumeItems.addConsumeInfo(player.getPlayerData(),wordsExchangeCfg.getNeedItems())){
			player.sendError(protocol.getType(), Status.error.ITEM_NOT_ENOUGH_VALUE);
			return true;
		}
		
		if(!consumeItems.checkConsume(player, protocol.getType())) {
			return true;
		}
		
		consumeItems.consumeTakeAffect(player, Action.WORDS_EXCHANGE);
		
		wordsExchangeCfg.getAwardItems().rewardTakeAffectAndPush(player, Action.WORDS_EXCHANGE,1);
		
		wordsExchangeStatus.increateExchangeTimes(type);
		
		player.getPlayerData().updateActivity(activityId, timeCfg.getStageId());
		
		HPWordsExchangeInfo.Builder exchangeInfo = HPWordsExchangeInfo.newBuilder();
		for(Integer exType : WordsExchangeCfg.getAllTypes()) {
			exchangeInfo.addLeftExchangeTimes(wordsExchangeStatus.getLeftExchangeTimes(exType));
			exchangeInfo.setActivityLeftTime(timeCfg.calcActivitySurplusTime());
		}
		player.sendProtocol(Protocol.valueOf(HP.code.WORDS_EXCHANGE_INFO_S, exchangeInfo));
		return true;
	}

}
