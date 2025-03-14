package com.guaji.game.gm;

import org.guaji.db.DBManager;
import org.guaji.script.GuaJiScript;
import org.guaji.script.GuaJiScriptManager;

import com.sun.net.httpserver.HttpExchange;

public class DbLandHandler extends GuaJiScript {
	@Override
	public void action(String params, HttpExchange httpExchange) {
		DBManager.getInstance().landImmediately();
		GuaJiScriptManager.sendResponse(httpExchange, "{\"status\":\"1\"}");
	}
}
