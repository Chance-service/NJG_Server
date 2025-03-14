package com.guaji.game.gm;


import java.util.Map;

import org.guaji.script.GuaJiScript;
import org.guaji.script.GuaJiScriptManager;

import com.guaji.game.entity.AllianceEntity;
import com.guaji.game.manager.AllianceManager;
import com.sun.net.httpserver.HttpExchange;

/**
 * 设置联盟等级
 * http://127.0.0.1:5132/setBHLevel?user=admin&params=bhID:10;level:1
 */
public class SetAllianceLevelHandler extends GuaJiScript {
	@Override
	public void action(String params, HttpExchange httpExchange) {
		Map<String, String> paramsMap = GuaJiScriptManager.paramsToMap(params);
		if (paramsMap.containsKey("bhID")) {
			int allianceId = Integer.valueOf(paramsMap.get("bhID"));
			int level = Integer.valueOf(paramsMap.get("level"));
			AllianceEntity allianceEntity = AllianceManager.getInstance().getAlliance(allianceId);
			if(allianceEntity != null) {
				allianceEntity.setLevel(level);
				allianceEntity.notifyUpdate(false);
			}
			// 回复状态
			GuaJiScriptManager.sendResponse(httpExchange, "{\"status\":1}");
		} else {
			GuaJiScriptManager.sendResponse(httpExchange, "{\"status\":2,\"msg\":\"invalid params\"}");
		}
	}
}
