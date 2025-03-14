package com.guaji.game.gm;

import java.lang.reflect.Field;

import org.guaji.os.MyException;
import org.guaji.os.GuaJiTime;
import org.guaji.script.GuaJiScript;
import org.guaji.script.GuaJiScriptManager;

import com.google.gson.JsonObject;
import com.guaji.game.manager.TeamBattleManager;
import com.sun.net.httpserver.HttpExchange;

public class ContinueTeamBattleHandler extends GuaJiScript {

	@Override
	public void action(String params, HttpExchange httpExchange) {
		TeamBattleManager instance = TeamBattleManager.getInstance();
		JsonObject jsonObject = new JsonObject();
		try {
			Field nextStatePeroidField = instance.getClass().getDeclaredField("nextStatePeroid");
			Field battleStateField = instance.getClass().getDeclaredField("battleState");
			if (nextStatePeroidField != null) {
				nextStatePeroidField.setAccessible(true);
				battleStateField.setAccessible(true);
				
				nextStatePeroidField.setInt(instance, GuaJiTime.getSeconds() + 2);
				//battleStateField.setInt(instance, -1);
				jsonObject.addProperty("curbattleState", instance.getCurBattleState());
				jsonObject.addProperty("reset", 1);
			}
		} catch (Exception e) {
			MyException.catchException(e);
		}
		jsonObject.addProperty("status", 1);
		GuaJiScriptManager.sendResponse(httpExchange, jsonObject.toString());
	}
}
