package com.guaji.game.gm;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.guaji.script.GuaJiScript;
import org.guaji.script.GuaJiScriptManager;

import com.guaji.game.entity.EmailEntity;
import com.guaji.game.player.Player;
import com.guaji.game.util.GsonUtil;
import com.guaji.game.util.PlayerUtil;
import com.sun.net.httpserver.HttpExchange;

public class QueryrPlayerMailHandler extends GuaJiScript {

	@Override
	public void action(String params, HttpExchange httpExchange) {
		Map<String, String> paramsMap = GuaJiScriptManager.paramsToMap(params);
		List<Integer> mailTypes = new LinkedList<>();
		if (paramsMap.containsKey("playerid")) {
			int playerId = Integer.valueOf(paramsMap.get("playerid"));
			Player player = PlayerUtil.queryPlayer(playerId);
			if(player != null) {
				for(EmailEntity emailEntity : player.getPlayerData().getEmailEntities().values()) {
					mailTypes.add(emailEntity.getType());
				}
			}
		}
		
		GuaJiScriptManager.sendResponse(httpExchange, GsonUtil.getJsonInstance().toJson(mailTypes));
	}

}
