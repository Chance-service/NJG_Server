package com.guaji.game.module.activity.wordsexchange;

import org.guaji.app.AppObj;
import org.guaji.net.protocol.Protocol;
import org.guaji.net.protocol.IProtocolHandler;

import com.guaji.game.protocol.Activity.HPWordsExchangeInfo;
import com.guaji.game.config.ActivityTimeCfg;
import com.guaji.game.config.WordsExchangeCfg;
import com.guaji.game.player.Player;
import com.guaji.game.util.ActivityUtil;
import com.guaji.game.protocol.Const;
import com.guaji.game.protocol.HP;
import com.guaji.game.protocol.Status;

public class WordsExchangeInfoHandler implements IProtocolHandler {

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
		
		WordsExchangeStatus wordsExchangeStatus = ActivityUtil.getActivityStatus(player.getPlayerData(), activityId, timeCfg.getStageId(), WordsExchangeStatus.class);
		if(wordsExchangeStatus == null) {
			player.sendError(protocol.getType(), Status.error.DATA_NOT_FOUND);
			return true;
		}
		HPWordsExchangeInfo.Builder exchangeInfo = HPWordsExchangeInfo.newBuilder();
		for(Integer exType : WordsExchangeCfg.getAllTypes()) {
			exchangeInfo.addLeftExchangeTimes(wordsExchangeStatus.getLeftExchangeTimes(exType));
			exchangeInfo.setActivityLeftTime(timeCfg.calcActivitySurplusTime());
		}
		player.sendProtocol(Protocol.valueOf(HP.code.WORDS_EXCHANGE_INFO_S, exchangeInfo));
		return true;
	}

}
