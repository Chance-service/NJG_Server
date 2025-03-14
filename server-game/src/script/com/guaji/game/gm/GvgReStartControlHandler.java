package com.guaji.game.gm;

import java.util.Map;

import org.guaji.os.MyException;
import org.guaji.script.GuaJiScript;
import org.guaji.script.GuaJiScriptManager;

import com.google.gson.JsonObject;
import com.guaji.game.manager.gvg.GvgManager;
import com.sun.net.httpserver.HttpExchange;

import net.sf.json.JSONObject;

/**
 * GVG功能控制
 */
// 参数解析: params=starttime:10000000000
public class GvgReStartControlHandler extends GuaJiScript{

	@Override
	public void action(String params, HttpExchange httpExchange) {
		try {
			Map<String, String> paramsMap = GuaJiScriptManager.paramsToMap(params);
			if (paramsMap.containsKey("starttime")) {
				
			
				long startTime = Long.parseLong(paramsMap.get("starttime"));
				GvgManager.getInstance().resetGvgData(startTime);
				JsonObject jsonObject = new JsonObject();
				jsonObject.addProperty("status", 1);
				GuaJiScriptManager.sendResponse(httpExchange, jsonObject.toString());
				return;
			}
			JSONObject json = new JSONObject();
			json.put("status", 2);
			json.put("msg", String.format("isOpen value is %s", paramsMap.get("isOpen")));
			GuaJiScriptManager.sendResponse(httpExchange, json.toString());
			
		} catch (Exception e) {
			MyException.catchException(e);
		}
		return;
	}

}
