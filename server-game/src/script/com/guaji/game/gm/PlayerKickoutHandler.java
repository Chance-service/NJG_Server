package com.guaji.game.gm;

import java.util.Map;

import org.guaji.script.GuaJiScript;
import org.guaji.script.GuaJiScriptManager;

import com.guaji.game.player.Player;
import com.guaji.game.util.PlayerUtil;
import com.guaji.game.protocol.Const;
import com.sun.net.httpserver.HttpExchange;

public class PlayerKickoutHandler extends GuaJiScript {

	@Override
	public void action(String params, HttpExchange httpExchange) {
		// 参数解析: params=playerid:0
		Map<String, String> paramsMap = GuaJiScriptManager.paramsToMap(params);
		if (paramsMap.containsKey("playerid")) {
			int playerId = Integer.valueOf(paramsMap.get("playerid"));

			Player player = PlayerUtil.queryPlayer(playerId);
			if (player != null) {
				player.kickout(Const.kickReason.KICKED_OUT_VALUE);
			}
		}
	}
}
