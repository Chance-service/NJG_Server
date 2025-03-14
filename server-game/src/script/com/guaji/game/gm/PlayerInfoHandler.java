package com.guaji.game.gm;

import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.guaji.db.DBManager;
import org.guaji.script.GuaJiScript;
import org.guaji.script.GuaJiScriptManager;

import com.google.gson.JsonObject;
import com.guaji.game.entity.PlayerEntity;
import com.guaji.game.player.Player;
import com.guaji.game.util.PlayerUtil;
import com.sun.net.httpserver.HttpExchange;
/**
 * 角色查詢
 * http://54.95.152.45:5132/playerinfo?params=playername:;playerid:1;puid:&user=hawk
 */
public class PlayerInfoHandler extends GuaJiScript {
	private static final Logger logger = Logger.getLogger("GM");
	@Override
	public void action(String params, HttpExchange httpExchange) {
	
		Map<String, String> paramsMap = GuaJiScriptManager.paramsToMap(params);
		if (paramsMap.containsKey("playername") || paramsMap.containsKey("playerid") || paramsMap.containsKey("puid")) {
			Player player = null;
			PlayerEntity playerEntity = null;
			if (paramsMap.containsKey("playername")) {
				String playerName = paramsMap.get("playername");
//				List<PlayerEntity> playerEntities = DBManager.getInstance().limitQuery("from PlayerEntity where name like '%" + playerName + "%' ",0,20);
				List<PlayerEntity> playerEntities = DBManager.getInstance().query("from PlayerEntity where name = '" + playerName + "' ");
				for(PlayerEntity pe : playerEntities) {
					if(pe.getName().equals(playerName)) {
						playerEntity = pe;
					}
				}
				if(playerEntity == null && playerEntities.size() > 0) {
					playerEntity = playerEntities.get(0);
				}
			} else if (paramsMap.containsKey("playerid")) {
				int playerid = Integer.valueOf(paramsMap.get("playerid"));
				List<PlayerEntity> playerEntities = DBManager.getInstance().query("from PlayerEntity where id=" + playerid);
				if (playerEntities.size() >= 1) {
					playerEntity = playerEntities.get(0);
				}
			} else if (paramsMap.containsKey("puid")) {
				String puid = paramsMap.get("puid");
				List<PlayerEntity> playerEntities = DBManager.getInstance().query("from PlayerEntity where puid='" + puid+"'");
				if (playerEntities.size() >= 1) {
					playerEntity = playerEntities.get(0);
				}
			}
	
			if (playerEntity != null) {
				player = PlayerUtil.queryPlayer(playerEntity.getId());
				if (player != null) {
					playerEntity = player.getPlayerData().getPlayerEntity();
				}
			}
	
			JsonObject jsonObject = new JsonObject();
			if (playerEntity != null) {
				jsonObject.addProperty("status", 1);
				jsonObject.addProperty("id", playerEntity.getId());
				jsonObject.addProperty("puid", playerEntity.getPuid());
				jsonObject.addProperty("name", playerEntity.getName());
				jsonObject.addProperty("level", playerEntity.getLevel());
				jsonObject.addProperty("exp", playerEntity.getExp());
				jsonObject.addProperty("golden", playerEntity.getTotalGold());
				jsonObject.addProperty("vip", playerEntity.getVipLevel());
				jsonObject.addProperty("silver", playerEntity.getCoin());
				if(playerEntity.getForbidenTime() != null)
					jsonObject.addProperty("ForbidenTime", playerEntity.getForbidenTime().toString());
				if(playerEntity.getSilentTime() != null)
					jsonObject.addProperty("SilentTime", playerEntity.getSilentTime().toString());
				jsonObject.addProperty("loginTime", playerEntity.getLoginTime().toString());
				jsonObject.addProperty("prof", playerEntity.getProf());
				jsonObject.addProperty("recharge", playerEntity.getRecharge());
				jsonObject.addProperty("payMoney", playerEntity.getPayMoney());
				jsonObject.addProperty("device", playerEntity.getDevice());
				
				if (player == null || !player.isOnline()) {
					jsonObject.addProperty("isOnline", "false");
				} else {
					jsonObject.addProperty("isOnline", "true");
				}
			} else {
				jsonObject.addProperty("status", 0);
			}
			GuaJiScriptManager.sendResponse(httpExchange, jsonObject.toString());
			logger.info(String.format("PlayerInfoHandler sourceId=%s,targetId=%s", jsonObject.toString(), jsonObject.toString()));
		} else {
			GuaJiScriptManager.sendResponse(httpExchange, "{\"status\":2,\"msg\":\"invalid params\"}");
		}
	}
}
