package com.guaji.game.gm;

import java.util.Map;

import org.guaji.script.GuaJiScript;
import org.guaji.script.GuaJiScriptManager;

import com.sun.net.httpserver.HttpExchange;

/**
 * 玩家封号处理
 */
public class TriggerRechargeMissionHandler extends GuaJiScript {
	@SuppressWarnings("unused")
	@Override
	public void action(String params, HttpExchange httpExchange) {
		// 参数解析: params=playerid:0;type:0
		Map<String, String> paramsMap = GuaJiScriptManager.paramsToMap(params);
		if (paramsMap.containsKey("playerId")) {
			int playerId = Integer.valueOf(paramsMap.get("playerId"));
			
			// 回复状态
			GuaJiScriptManager.sendResponse(httpExchange, "{\"status\":1}");
		} else {
			GuaJiScriptManager.sendResponse(httpExchange, "{\"status\":2,\"msg\":\"invalid params\"}");
		}
	}
}
