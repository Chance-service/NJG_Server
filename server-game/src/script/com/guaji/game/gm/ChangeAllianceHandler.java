package com.guaji.game.gm;


import java.util.Map;

import org.guaji.script.GuaJiScript;
import org.guaji.script.GuaJiScriptManager;

import com.guaji.game.entity.AllianceEntity;
import com.guaji.game.log.BehaviorLogger.Action;
import com.guaji.game.manager.AllianceManager;
import com.sun.net.httpserver.HttpExchange;

/**
 * 修改帮会数据（修改经验和元气值）
 * @date 2014-2-26
 */
// 参数解析: params=allianceId:0;exp:0;vitality:0
public class ChangeAllianceHandler extends GuaJiScript {

	@Override
	public void action(String params, HttpExchange httpExchange) {
		Map<String, String> paramsMap = GuaJiScriptManager.paramsToMap(params);
		if (paramsMap.containsKey("allianceId")) {
			int allianceId = Integer.valueOf(paramsMap.get("allianceId"));
			AllianceEntity allianceEntity = AllianceManager.getInstance().getAlliance(allianceId);
			int exp = 0;
			int vitality = 0;
			if(paramsMap.containsKey("exp")) {
				exp = Integer.valueOf(paramsMap.get("exp")).intValue();
				if(allianceEntity != null) {
					allianceEntity.setExp(allianceEntity.getExp() + exp);
					AllianceManager.getInstance().checkAllianceLevelUp(allianceEntity);
					allianceEntity.notifyUpdate(false);
				}
			}
			
			if(paramsMap.containsKey("vitality")) {
				vitality = Integer.valueOf(paramsMap.get("vitality")).intValue();
				allianceEntity.addBossVitality(vitality, Action.GM_AWARD);
				AllianceManager.getInstance().checkAllianceLevelUp(allianceEntity);
			}
			
			// 回复状态
			GuaJiScriptManager.sendResponse(httpExchange, "{\"status\":1}");
		} else {
			GuaJiScriptManager.sendResponse(httpExchange, "{\"status\":2,\"msg\":\"invalid params\"}");
		}
	}
}
