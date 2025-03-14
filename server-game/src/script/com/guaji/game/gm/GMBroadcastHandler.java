package com.guaji.game.gm;

import java.util.Map;

import org.guaji.script.GuaJiScript;
import org.guaji.script.GuaJiScriptManager;

import com.guaji.game.manager.ChatManager;
import com.guaji.game.manager.ChatMsg;
import com.guaji.game.protocol.Const;
import com.sun.net.httpserver.HttpExchange;

public class GMBroadcastHandler extends GuaJiScript {
	@Override
	public void action(String params, HttpExchange httpExchange) {
		Map<String, String> paramsMap = GuaJiScriptManager.paramsToMap(params);
		if (paramsMap.containsKey("type") && paramsMap.containsKey("message")) {
			int type = Integer.valueOf(paramsMap.get("type"));
			String msg = paramsMap.get("message");
			if (msg.length() > 0 && (type == Const.chatType.CHAT_BROADCAST_VALUE || type == Const.chatType.WORLD_BROADCAST_VALUE)) {
				ChatMsg chatMsg = new ChatMsg();
				chatMsg.setType(type);
				chatMsg.setChatMsg(msg);
				ChatManager.getInstance().postBroadcast(chatMsg);
				GuaJiScriptManager.sendResponse(httpExchange, "{\"status\":1}");
			}
		} else {
			GuaJiScriptManager.sendResponse(httpExchange, "{\"status\":2,\"msg\":\"invalid params\"}");
		}
	}
}
