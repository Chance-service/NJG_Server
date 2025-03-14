package com.guaji.game.gm;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.HashSet;

import org.guaji.db.DBManager;
import org.guaji.script.GuaJiScript;
import org.guaji.script.GuaJiScriptManager;

import com.google.gson.JsonObject;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonParser;
import com.google.gson.JsonElement;
import com.guaji.game.entity.ActivityEntity;
import com.guaji.game.entity.PlayerEntity;
import com.guaji.game.module.activity.monthcard.MonthCardStatus;
import com.guaji.game.player.Player;
import com.guaji.game.player.PlayerData;
import com.guaji.game.util.ActivityUtil;
import com.guaji.game.util.PlayerUtil;
import com.guaji.game.protocol.Const;
import com.sun.net.httpserver.HttpExchange;

public class GachaTestHandler extends GuaJiScript {
	
	@SuppressWarnings("unchecked")
	@Override
	public void action(String params, HttpExchange httpExchange) {
		
		// 参数解析: params=playername:xulin
		Map<String, String> paramsMap = GuaJiScriptManager.paramsToMap(params);
		JsonObject jsonObject = new JsonObject();
		if (!paramsMap.containsKey("activityId")) {
			jsonObject.addProperty("status", 2);
			jsonObject.addProperty("msg", "invalid params activityId ");
			GuaJiScriptManager.sendResponse(httpExchange, jsonObject.toString());
			return;
		}
		if (!paramsMap.containsKey("drawtime")) {
			jsonObject.addProperty("status", 2);
			jsonObject.addProperty("msg", "invalid params drawtime ");
			GuaJiScriptManager.sendResponse(httpExchange, jsonObject.toString());
			return;
		}
		int activityId = Integer.valueOf(paramsMap.get("activityId")) ;
		int drawtime = Integer.valueOf(paramsMap.get("drawtime"));
		
		List<String> awardList = ActivityUtil.testGacha(activityId, drawtime);
		Map<String,Integer> amap = new HashMap<>();
		int index = 0;
		if (awardList.size() > 0) {
			jsonObject.addProperty("status", 0);
			for (String awardStr : awardList) {
				String [] strArray = awardStr.split("_");
				String key = strArray[0]+"_"+strArray[1];
				int vaule = Integer.valueOf(strArray[2]);
				if (amap.containsKey(key)) {
					amap.put(key,amap.get(key)+vaule);
				} else {
					amap.put(key,vaule);
				}
			}
			for (Map.Entry<String,Integer> entry : amap.entrySet()) {
				jsonObject.addProperty(entry.getKey(),entry.getValue());
			}
		} else {
			jsonObject.addProperty("status", 2);
			jsonObject.addProperty("msg", "not found data");
		}
		
		String jsonData = jsonObject.toString();
		JsonElement jsonElement = new JsonParser().parse(jsonData);

	    Gson gson = new GsonBuilder().setPrettyPrinting().create();
	    String json = gson.toJson(jsonElement);
		
		GuaJiScriptManager.sendResponse(httpExchange, json);

	}
	
}
