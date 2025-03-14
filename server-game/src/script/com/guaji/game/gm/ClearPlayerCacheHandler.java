package com.guaji.game.gm;

import java.util.Map;

import org.guaji.app.AppObj;
import org.guaji.obj.ObjBase;
import org.guaji.obj.ObjManager;
import org.guaji.script.GuaJiScript;
import org.guaji.script.GuaJiScriptManager;
import org.guaji.xid.GuaJiXID;

import com.guaji.game.GsApp;
import com.guaji.game.util.GsConst;
import com.sun.net.httpserver.HttpExchange;
/**
 * 清除角色快取 curl 'localhost:5132/clearplayer?params=playerid:5&user=hawk'
 */
public class ClearPlayerCacheHandler extends GuaJiScript {

	@Override
	public void action(String params, HttpExchange httpExchange) {
		
		Map<String, String> paramsMap = GuaJiScriptManager.paramsToMap(params);
		if (paramsMap.containsKey("playerid")) {
			int playerId = Integer.valueOf(paramsMap.get("playerid"));
			GuaJiXID xid = GuaJiXID.valueOf(GsConst.ObjType.PLAYER, playerId);
			ObjBase<GuaJiXID, AppObj> objBase = GsApp.getInstance().queryObject(xid);
			if (objBase != null && objBase.isObjValid()) {
				objBase.setVisitTime(0);
			}
		}
		
		ObjManager<GuaJiXID, AppObj> objMan = GsApp.getInstance().getObjMan(GsConst.ObjType.PLAYER);
		if (objMan != null && objMan.getObjTimeout() > 0) {
			objMan.removeTimeoutObj(GsApp.getInstance().getCurrentTime());			
		}
		
		GuaJiScriptManager.sendResponse(httpExchange, "{\"status\":\"1\"}");
	}

}
