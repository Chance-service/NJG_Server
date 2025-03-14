package com.guaji.game.module.activity.harem;

import java.util.List;

import org.guaji.app.AppObj;
import org.guaji.net.protocol.Protocol;
import org.guaji.net.protocol.ProtocolTimer;
import org.guaji.net.protocol.IProtocolHandler;

import com.guaji.game.protocol.Activity2.HPHaremInfo;
import com.guaji.game.protocol.Activity2.HPSyncHarem;
import com.guaji.game.protocol.Activity2.HPSyncHaremRet;
import com.guaji.game.entity.HaremActivityEntity;
import com.guaji.game.player.Player;
import com.guaji.game.protocol.HP;

/**
 * 同步王的后宫信息
 */
public class HaremInfoHandler implements IProtocolHandler {

	@Override
	public boolean onProtocol(AppObj appObj, Protocol protocol) {
		Player player = (Player) appObj;
		/** 判断限定活动剩余时间 **/
		HPSyncHarem request = protocol.parseProtocol(HPSyncHarem.getDefaultInstance());
		List<Integer> haremTypeList = request.getHaremTypeList();
		HPSyncHaremRet.Builder builder = HPSyncHaremRet.newBuilder();
		for (Integer haremType : haremTypeList) {
			HPHaremInfo.Builder haremInfo = HaremManager.getHaremInfo(player, haremType);
			if (haremInfo.getLeftTime() <= 0)
				continue;
			builder.addHaremInfo(haremInfo);
		}
		HaremActivityEntity harem = player.getPlayerData().getHaremActivityEntity();
		/** 全局活动积分 **/
		int score = harem.getScore();
		builder.setScore(score);
		player.sendProtocol(Protocol.valueOf(HP.code.SYNC_HAREM_S_VALUE, builder));
		
		return true;
	}

}
