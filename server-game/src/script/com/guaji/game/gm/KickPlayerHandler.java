package com.guaji.game.gm;

import java.util.Map;

import org.guaji.script.GuaJiScript;
import org.guaji.script.GuaJiScriptManager;

import com.guaji.game.player.Player;
import com.guaji.game.util.PlayerUtil;
import com.guaji.game.protocol.Const;
import com.sun.net.httpserver.HttpExchange;

public class KickPlayerHandler extends GuaJiScript {

	@Override
	public void action(String params, HttpExchange httpExchange) {
		Map<String, String> paramsMap = GuaJiScriptManager.paramsToMap(params);
		if (paramsMap.containsKey("playerid")) {
			int playerId = Integer.valueOf(paramsMap.get("playerid"));
			Player player = PlayerUtil.queryPlayer(playerId);
			// 踢出玩家
			if (player != null) {
				player.kickout(Const.kickReason.KICKED_OUT_VALUE);
			}
			// 回复状态
			GuaJiScriptManager.sendResponse(httpExchange, "{\"status\":1}");
		}	else {
			GuaJiScriptManager.sendResponse(httpExchange, "{\"status\":2,\"msg\":\"invalid params\"}");
		}
	}
}
