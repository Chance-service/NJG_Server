package com.guaji.game.gm;

import java.util.Map;

import org.guaji.app.AppObj;
import org.guaji.obj.ObjBase;
import org.guaji.os.MyException;
import org.guaji.script.GuaJiScript;
import org.guaji.script.GuaJiScriptManager;
import org.guaji.xid.GuaJiXID;

import com.guaji.game.GsApp;
import com.guaji.game.player.Player;
import com.guaji.game.util.GsConst;
import com.sun.net.httpserver.HttpExchange;

public class PlayerDailyResetHandler extends GuaJiScript {

	@Override
	public void action(String params, HttpExchange httpExchange) {
		Map<String, String> paramsMap = GuaJiScriptManager.paramsToMap(params);
		if (paramsMap.containsKey("playerid")) {
			boolean isWeekly = false;
			if (paramsMap.containsKey("isWeekly")){
				String isWeekStr = paramsMap.get("isWeekly");
				isWeekly = Boolean.parseBoolean(isWeekStr);
			}
			int playerId = Integer.valueOf(paramsMap.get("playerid"));
			GuaJiXID xid = GuaJiXID.valueOf(GsConst.ObjType.PLAYER, playerId);
			ObjBase<GuaJiXID, AppObj> objBase = GsApp.getInstance().lockObject(xid);
			if (objBase != null) {
				try {
					if (objBase.isObjValid()) {
						Player player = (Player) objBase.getImpl();
						if (isWeekly) {
							player.getPlayerData().getPlayerEntity().setResetTime(null);
						}
						player.handleDailyFirstLogin(player.isOnline());
					}
				} catch (Exception e) {
					MyException.catchException(e);
				} finally {
					objBase.unlockObj();
				}
			}
		}

		GuaJiScriptManager.sendResponse(httpExchange, "{\"status\":\"1\"}");
	}

}
