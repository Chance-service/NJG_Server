package com.guaji.game.module.activity.chatSkin;

import org.guaji.app.AppObj;
import org.guaji.net.protocol.Protocol;
import org.guaji.net.protocol.IProtocolHandler;

import com.guaji.game.protocol.Activity2.HPChatSkinChange;
import com.guaji.game.entity.ChatSkinEntity;
import com.guaji.game.player.Player;
import com.guaji.game.protocol.HP;

/**
 * 更换聊天皮肤
 */
public class ChatSkinChangeHandler implements IProtocolHandler {

	@Override
	public boolean onProtocol(AppObj appObj, Protocol protocol) {
		Player player = (Player) appObj;

		HPChatSkinChange req = protocol.parseProtocol(HPChatSkinChange.getDefaultInstance());
		int skinId = req.getSkinId();

		// 聊天皮肤数据
		ChatSkinEntity entity = player.getPlayerData().getChatSkinEntity();
		
		// 不可用
		if (skinId != 0 && !entity.isAvailable(skinId)) {
			return false;
		}
		// 设置当前皮肤
		entity.setCurSkinId(skinId);
		// 更新DB
		entity.reConvert();
		entity.notifyUpdate();

		// 协议返回
		HPChatSkinChange.Builder ret = HPChatSkinChange.newBuilder();
		ret.setSkinId(skinId);
		player.sendProtocol(Protocol.valueOf(HP.code.CHAT_SKIN_CHANGE_S_VALUE, ret));

		return true;
	}

}
