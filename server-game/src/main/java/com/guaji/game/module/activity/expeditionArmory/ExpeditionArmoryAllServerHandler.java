package com.guaji.game.module.activity.expeditionArmory;


import org.guaji.app.AppObj;
import org.guaji.net.protocol.Protocol;
import org.guaji.net.protocol.IProtocolHandler;
import org.guaji.util.services.PlatformService;

import com.guaji.game.protocol.Activity2.HPExpeditionArmoryAllScoreInfoRet;
import com.guaji.game.player.Player;
import com.guaji.game.protocol.HP;


public class ExpeditionArmoryAllServerHandler implements IProtocolHandler {
	
	@Override
	public boolean onProtocol(AppObj appObj, Protocol protocol) {
		Player player = (Player) appObj;

		HPExpeditionArmoryAllScoreInfoRet.Builder ret=  HPExpeditionArmoryAllScoreInfoRet.newBuilder();
		
		int score = PlatformService.getInstance().getServerActivityScore();
		ret.setScore(score);		
		player.sendProtocol(Protocol.valueOf(HP.code.EXPEDITION_ALL_SERVER_SCORE_INFO_S_VALUE, ret));
		return true;
	}


}
