package com.guaji.game.gm;

import org.guaji.script.GuaJiScript;
import org.guaji.script.GuaJiScriptManager;

import com.guaji.game.entity.AllianceEntity;
import com.guaji.game.manager.AllianceManager;
import com.sun.net.httpserver.HttpExchange;

/**
 * 强制所有帮派数据落地
 */
public class ForceAllianceDbLanderHandler extends GuaJiScript {
	@Override
	public void action(String params, HttpExchange httpExchange) {
		// 参数解析: params=allianceId:0;exp:0
		for(AllianceEntity allianceEntity : AllianceManager.getInstance().getAllianceMap().values()){
			if(allianceEntity != null) {
				allianceEntity.notifyUpdate(false);
			}
		}
		// 回复状态
		GuaJiScriptManager.sendResponse(httpExchange, "{\"status\":1}");
	}
}
