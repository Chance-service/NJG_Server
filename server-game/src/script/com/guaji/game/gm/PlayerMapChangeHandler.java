package com.guaji.game.gm;

import java.lang.reflect.Field;
import java.util.Map;

import org.guaji.script.GuaJiScript;
import org.guaji.script.GuaJiScriptManager;

import com.guaji.game.entity.StateEntity;
import com.guaji.game.player.Player;
import com.guaji.game.player.PlayerData;
import com.guaji.game.util.PlayerUtil;
import com.sun.net.httpserver.HttpExchange;
/**
 * http://localhost:5132/playerMapChange?params=playerId:106373;curMapId:1001;passMapId:55;&user=admin
 */
public class PlayerMapChangeHandler extends GuaJiScript {

	@Override
	public void action(String params, HttpExchange httpExchange) {

		try {
			Map<String, String> paramsMap = GuaJiScriptManager.paramsToMap(params);
			if (paramsMap.containsKey("playerId") && paramsMap.containsKey("curMapId") && paramsMap.containsKey("passMapId")) {
				int playerId = Integer.valueOf(paramsMap.get("playerId"));
				int curMapId = Integer.valueOf(paramsMap.get("curMapId"));
				int passMapId = Integer.valueOf(paramsMap.get("passMapId"));
				Player player = PlayerUtil.queryPlayer(playerId);
				PlayerData playerData = player.getPlayerData();
				Field field = PlayerData.class.getDeclaredField("stateEntity");
				field.setAccessible(true);
				StateEntity stateEntity = (StateEntity)field.get(playerData);
				stateEntity.setCurBattleMap(curMapId);
				stateEntity.setPassMapId(passMapId);
				stateEntity.notifyUpdate(false);
				GuaJiScriptManager.sendResponse(httpExchange, "{\"status\":1}");
			} else {
				GuaJiScriptManager.sendResponse(httpExchange,"{\"status\":2,\"msg\":\"invalid params playerId\"}");
			}
		} catch (NoSuchFieldException | SecurityException e) {
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}

	}

}
