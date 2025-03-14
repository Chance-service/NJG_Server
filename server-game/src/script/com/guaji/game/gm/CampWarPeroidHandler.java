package com.guaji.game.gm;

import java.lang.reflect.Field;

import org.guaji.os.GuaJiTime;
import org.guaji.script.GuaJiScript;
import org.guaji.script.GuaJiScriptManager;

import com.google.gson.JsonObject;
import com.guaji.game.manager.CampWarManager;
import com.sun.net.httpserver.HttpExchange;

public class CampWarPeroidHandler extends GuaJiScript {
	
	@Override
	public void action(String params, HttpExchange httpExchange) {
		
		JsonObject jsonObject = new JsonObject();
		try {
			Field nextPeriodField = CampWarManager.getInstance().getClass().getDeclaredField("nextPeriod");
			nextPeriodField.setAccessible(true);
			Integer period = nextPeriodField.getInt(CampWarManager.getInstance());
			jsonObject.addProperty("oldperiod", period);
			
			int nextPeriod = GuaJiTime.getSeconds() + 5;
			nextPeriodField.setInt(CampWarManager.getInstance(), nextPeriod);
			jsonObject.addProperty("nextperiod", nextPeriod);
			GuaJiScriptManager.sendResponse(httpExchange, jsonObject.toString());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
