package com.guaji.game.module.activity.chatSkin;

import java.util.List;

import org.guaji.app.AppObj;
import org.guaji.net.protocol.Protocol;
import org.guaji.net.protocol.IProtocolHandler;

import com.guaji.game.protocol.Activity2.HPChatSkinInfo;
import com.guaji.game.protocol.Activity2.HPChatSkinInfo.ChatSkinInfo;
import com.guaji.game.entity.ChatSkinEntity;
import com.guaji.game.entity.ChatSkinItem;
import com.guaji.game.player.Player;
import com.guaji.game.protocol.HP;

/**
 * 当前拥有的聊天皮肤信息
 */
public class ChatSkinOwnedInfoHandler implements IProtocolHandler {

	@Override
	public boolean onProtocol(AppObj appObj, Protocol protocol) {
		Player player = (Player) appObj;

		//聊天皮肤数据
		ChatSkinEntity entity = player.getPlayerData().getChatSkinEntity();

		//协议返回
		HPChatSkinInfo.Builder ret = HPChatSkinInfo.newBuilder();

		//可用的皮肤
		List<ChatSkinItem> availableSkins = entity.availableSkins(); 
		ret.setCurSkinId(entity.getCurSkinId());
		for(ChatSkinItem item : availableSkins){
			ChatSkinInfo.Builder b = ChatSkinInfo.newBuilder();
			b.setSkinId(item.getSkinId());
			b.setRemainTime(entity.getSkinRemainDays(item.getSkinId()));
			ret.addSkins(b);
		}
		
		player.sendProtocol(Protocol.valueOf(HP.code.CHAT_SKIN_OWNED_INFO_S_VALUE, ret));

		return true;
	}

}
