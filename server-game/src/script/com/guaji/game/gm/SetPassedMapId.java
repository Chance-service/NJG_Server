package com.guaji.game.gm;

import java.net.URLDecoder;
import java.util.List;
import java.util.Map;

import org.guaji.config.ConfigManager;
import org.guaji.db.DBManager;
import org.guaji.os.MyException;
import org.guaji.script.GuaJiScript;
import org.guaji.script.GuaJiScriptManager;

import com.google.gson.JsonObject;
import com.guaji.game.config.NewMapCfg;
import com.guaji.game.entity.MapStatisticsEntity;
import com.guaji.game.entity.StateEntity;
import com.guaji.game.player.Player;
import com.guaji.game.util.PlayerUtil;
import com.sun.net.httpserver.HttpExchange;

/**
 * 设置关卡
 * http://54.95.152.45:5132/setPassedMap?params=playerid:797;passedMapId:155&user=hawk
 */
public class SetPassedMapId extends GuaJiScript{

	@Override
	public void action(String params, HttpExchange httpExchange) {
		try {
			JsonObject jsonObject = new JsonObject();
			
			params = URLDecoder.decode(params, "UTF-8");
	
			Map<String, String> paramsMap = GuaJiScriptManager.paramsToMap(params);
			if((!paramsMap.containsKey("playerid")) ||(!paramsMap.containsKey("passedMapId"))){
				jsonObject.addProperty("status", -1);
				GuaJiScriptManager.sendResponse(httpExchange, jsonObject.toString());
				return;
			}
			int playerid = Integer.valueOf(paramsMap.get("playerid"));
			int passedMapId = Integer.valueOf(paramsMap.get("passedMapId"));
			Player player = PlayerUtil.queryPlayer(playerid);
			
			NewMapCfg mapCfg = ConfigManager.getInstance().getConfigByKey(NewMapCfg.class, passedMapId);
			
			if(mapCfg==null){
				jsonObject.addProperty("status", -2);
				GuaJiScriptManager.sendResponse(httpExchange, jsonObject.toString());
				return;
			}
			
			int curMapId = mapCfg.getNextMapId();
			StateEntity stateEntity = null;
			// 線上
			if(player!=null){
				stateEntity = player.getPlayerData().getStateEntity();
				
			} else {
				List<StateEntity> stateEntitys = DBManager.getInstance()
						.query("from StateEntity where playerId = ? and invalid = 0", playerid);
				if (stateEntitys != null && stateEntitys.size() > 0) {
					stateEntity = stateEntitys.get(0);
					stateEntity.convertData();
				} else {
					jsonObject.addProperty("status", -3);
					GuaJiScriptManager.sendResponse(httpExchange, jsonObject.toString());
					return;
				}
			}
			
			if (stateEntity != null) {
				int oldMapId = stateEntity.getPassMapId();
				stateEntity.setPassMapId(passedMapId);
				stateEntity.setCurBattleMap(curMapId);
				stateEntity.notifyUpdate(true);
				if(player!=null){
					player.notifyMapPass(oldMapId, passedMapId);
				}
			} else {
				jsonObject.addProperty("status", -4);
				GuaJiScriptManager.sendResponse(httpExchange, jsonObject.toString());
				return;
			}
			
			if(player!=null){
				player.getPlayerData().syncStateInfo();
				MapStatisticsEntity mapStatisticsEntity = player.getPlayerData().getMapStatisticsEntity();
				mapStatisticsEntity.reset(player.getPlayerData().getCurBattleMap());
				player.getPlayerData().syncMapStatistics();
			}
			
			jsonObject.addProperty("status", 1);
			GuaJiScriptManager.sendResponse(httpExchange, jsonObject.toString());
		} catch (Exception e) {
			MyException.catchException(e);
		}
		return ;
	}

}
