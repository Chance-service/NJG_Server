package com.guaji.game.gm;

import java.net.URLDecoder;
import java.util.Map;

import org.guaji.os.MyException;
import org.guaji.script.GuaJiScript;
import org.guaji.script.GuaJiScriptManager;

import com.google.gson.JsonObject;
import com.guaji.game.log.BehaviorLogger.Action;
import com.guaji.game.player.Player;
import com.guaji.game.util.PlayerUtil;
import com.sun.net.httpserver.HttpExchange;

/**
 * 给玩家发神器
 */
// 参数解析: params=playerid:1
public class AddGodlyInfoHandler extends GuaJiScript{

	@Override
	public void action(String params, HttpExchange httpExchange) {
		try {
			params = URLDecoder.decode(params, "UTF-8");
			Map<String, String> paramsMap = GuaJiScriptManager.paramsToMap(params);
			
			if(!paramsMap.containsKey("playerid")){
				JsonObject jsonObject = new JsonObject();
				jsonObject.addProperty("status", -1);
				GuaJiScriptManager.sendResponse(httpExchange, jsonObject.toString());
				return;
			}
			int playerid = Integer.valueOf(paramsMap.get("playerid"));
			Player player = PlayerUtil.queryPlayer(playerid);
			if(player != null) {
				//玩家在线  发神器
				int[] equipIds = new int[]{50010080,50010090,50011050,50011051,50011060,50012050,50012051,50012060,50013050,50013051,50013060,50010040,50050010,50050020,50050070};
				for(int eId : equipIds) {
					player.getPlayerData().syncEquipInfo(player.increaseEquip(eId, 10000, Action.UNKONWN_ACTION).getId());
				}
			}
			JsonObject jsonObject = new JsonObject();
			jsonObject.addProperty("status", 1);
			GuaJiScriptManager.sendResponse(httpExchange, jsonObject.toString());
		} catch (Exception e) {
			MyException.catchException(e);
		}
		return ;
	}

}
