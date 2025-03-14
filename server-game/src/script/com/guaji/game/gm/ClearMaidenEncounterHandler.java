package com.guaji.game.gm;

import java.util.Map;

import org.guaji.app.AppObj;
import org.guaji.db.DBManager;
import org.guaji.obj.ObjBase;
import org.guaji.obj.ObjManager;
import org.guaji.script.GuaJiScript;
import org.guaji.script.GuaJiScriptManager;
import org.guaji.xid.GuaJiXID;

import com.guaji.game.GsApp;
import com.guaji.game.entity.MaidenEncounterEntity;
import com.guaji.game.util.GsConst;
import com.sun.net.httpserver.HttpExchange;

/**
 * 用于清除少女的邂逅活动的数据
 * @author Melvin
 *
 */
public class ClearMaidenEncounterHandler extends GuaJiScript {
	@Override
	public void action(String params, HttpExchange httpExchange) {
		// 清缓存
		Map<String, String> paramsMap = GuaJiScriptManager.paramsToMap(params);
		if (!paramsMap.containsKey("playerid")) {
			GuaJiScriptManager.sendResponse(httpExchange, "{\"notice\":\"Params invalid !\",\"useage\":\"http://182.254.166.103:9932/clearMaidenEncounter?params=playerid:${playerid};&user=hawk\"}");
			return;
		}
		int playerId = Integer.valueOf(paramsMap.get("playerid"));
		GuaJiXID xid = GuaJiXID.valueOf(GsConst.ObjType.PLAYER, playerId);
		ObjBase<GuaJiXID, AppObj> objBase = GsApp.getInstance().queryObject(xid);
		if (objBase != null && objBase.isObjValid()) {
			objBase.setVisitTime(0);
		}
		
		ObjManager<GuaJiXID, AppObj> objMan = GsApp.getInstance().getObjMan(GsConst.ObjType.PLAYER);
		if (objMan != null && objMan.getObjTimeout() > 0) {
			objMan.removeTimeoutObj(GsApp.getInstance().getCurrentTime());			
		}
		// 删数据
		String hql="from MaidenEncounterEntity where playerId = ?";
		MaidenEncounterEntity entity =	DBManager.getInstance().fetch(MaidenEncounterEntity.class, hql, playerId);
		DBManager.getInstance().delete(entity);
		GuaJiScriptManager.sendResponse(httpExchange, "{\"status\":\"success\"}");
	}
}
