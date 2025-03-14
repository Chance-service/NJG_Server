package com.guaji.game.gm;

import org.guaji.script.GuaJiScript;
import org.guaji.script.GuaJiScriptManager;

import com.guaji.game.manager.MultiEliteManager;
import com.sun.net.httpserver.HttpExchange;

/**
 * 重新刷新多人副本地图列表
 * curl 'localhost:5132/multiEliteInitMap?&user=admin'
 */
public class MultiEliteInitMapHandler extends GuaJiScript {

	@Override
	public void action(String params, HttpExchange httpExchange) {
		
		MultiEliteManager.getInstance().init();
		GuaJiScriptManager.sendResponse(httpExchange, "{\"status\":\"1\"}");
	}

}
