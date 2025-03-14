package com.guaji.game.module.activity.chatSkin;

import org.guaji.app.AppObj;
import org.guaji.net.protocol.Protocol;
import org.guaji.net.protocol.IProtocolHandler;

import com.guaji.game.entity.ChatSkinEntity;
import com.guaji.game.player.Player;

/**
 * 清除小红点
 */
public class ChatSkinClearRedPointHandler implements IProtocolHandler {

	@Override
	public boolean onProtocol(AppObj appObj, Protocol protocol) {
		Player player = (Player) appObj;

		//聊天皮肤数据
		// FIXME 聊天数据
		ChatSkinEntity entity = player.getPlayerData().getChatSkinEntity();
		if(!entity.isRedPoint()){
			entity.setRedPoint(true);
			entity.notifyUpdate();
		}
		return true;
	}

}
