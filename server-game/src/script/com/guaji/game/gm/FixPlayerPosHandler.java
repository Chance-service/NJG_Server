package com.guaji.game.gm;

import java.util.Map;

import org.guaji.app.App;
import org.guaji.net.GuaJiNetManager;
import org.guaji.script.GuaJiScript;
import org.guaji.script.GuaJiScriptManager;

import com.guaji.game.ServerData;
import com.guaji.game.protocol.Const;
import com.sun.net.httpserver.HttpExchange;

/**
 * 玩家封号处理
 */
public class FixPlayerPosHandler extends GuaJiScript {
	@Override
	public void action(String params, HttpExchange httpExchange) {
		Map<String, String> paramsMap = GuaJiScriptManager.paramsToMap(params);
		//1.增加黑名單,2刪除黑名單,3增加device黑名單4移除device黑名單5.查ip黑名單6.查裝置黑名單
		int type = Integer.valueOf(paramsMap.get("type"));
		String value_s = paramsMap.get("value");
		String values[] = value_s.split(",");
		for (int i = 0; i < values.length; i++) {
			String value = values[i];
			switch (type) {
		        case 1:
		        	GuaJiNetManager.getInstance().addBlackIp(value);
		            break;
		        case 2:
		        	GuaJiNetManager.getInstance().removeBlackIp(value);
		        	break;
		        case 3:
		        	GuaJiNetManager.getInstance().addBlackDevice(value);
		        	break;
		        case 4:
		        	GuaJiNetManager.getInstance().removeBlackDevice(value);
		        	break;
		        case 5:
		        	break;
			}
		}
		
		GuaJiScriptManager.sendResponse(httpExchange, "{\"status\":1}");
		//ServerData.getInstance().addPuidAndPlayerId("ya_4227813", 6,Integer.parseInt(paramsMap.get("serverId")));
		//GuaJiScriptManager.sendResponse(httpExchange, "{\"status\":2,\"msg\":\"invalid params\"}");
	}
}
