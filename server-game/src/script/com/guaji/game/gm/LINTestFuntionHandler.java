package com.guaji.game.gm;

import org.guaji.script.GuaJiScript;
import org.guaji.script.GuaJiScriptManager;

import com.guaji.game.manager.ChatManager;
import com.guaji.game.util.ActivityUtil;
import com.sun.net.httpserver.HttpExchange;

import net.sf.json.JSONObject;

/**
 * 測試函式用
 */
public class LINTestFuntionHandler extends GuaJiScript {

	@Override
	public void action(String params, HttpExchange httpExchange) {
		JSONObject json = new JSONObject();
		json.put("response","OK");
		
		//ActivityUtil.restGloryHoleDailyStatus();
		
		GuaJiScriptManager.sendResponse(httpExchange,json.toString());
	}

}
