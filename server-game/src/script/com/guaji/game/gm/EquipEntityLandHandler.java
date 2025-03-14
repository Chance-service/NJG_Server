package com.guaji.game.gm;

import org.guaji.script.GuaJiScript;
import org.guaji.script.GuaJiScriptManager;

import com.sun.net.httpserver.HttpExchange;

public class EquipEntityLandHandler extends GuaJiScript{

	@Override
	public void action(String params, HttpExchange httpExchange) {
		GuaJiScriptManager.sendResponse(httpExchange, "{status : 1}");
	}
	
}
