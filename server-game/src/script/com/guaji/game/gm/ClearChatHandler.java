package com.guaji.game.gm;

import org.guaji.script.GuaJiScript;
import org.guaji.script.GuaJiScriptManager;

import com.guaji.game.manager.ChatManager;
import com.sun.net.httpserver.HttpExchange;

/**
 * 清理聊天缓存
 */
public class ClearChatHandler extends GuaJiScript {

	@Override
	public void action(String params, HttpExchange httpExchange) {
		
		ChatManager.getInstance().clearCache();
		GuaJiScriptManager.sendResponse(httpExchange, "{\"status\":\"1\"}");
	}

}
