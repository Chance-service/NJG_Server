package com.guaji.game.gm;

import java.util.List;
import java.util.Map;

import org.guaji.app.AppObj;
import org.guaji.db.DBManager;
import org.guaji.obj.ObjBase;
import org.guaji.script.GuaJiScript;
import org.guaji.script.GuaJiScriptManager;
import org.guaji.xid.GuaJiXID;

import com.guaji.game.GsApp;
import com.guaji.game.entity.TitleEntity;
import com.guaji.game.player.Player;
import com.guaji.game.util.GsConst;
import com.sun.net.httpserver.HttpExchange;

public class FixTitleHandler extends GuaJiScript{

	@Override
	public void action(String params, HttpExchange httpExchange) {
		// 参数解析: params=playerid:xxxx,titles:x,x
		Map<String, String> paramsMap = GuaJiScriptManager.paramsToMap(params);
		Player player = null;
		if (paramsMap.containsKey("playerid") && paramsMap.containsKey("titles")) {
			int playerId = Integer.valueOf(paramsMap.get("playerid"));
			String[] titles = paramsMap.get("titles").split(",");
			boolean isOnline = false;
			GuaJiXID xid = GuaJiXID.valueOf(GsConst.ObjType.PLAYER, playerId);
			ObjBase<GuaJiXID, AppObj> objBase = GsApp.getInstance().lockObject(xid);
			try {
				if (objBase != null && objBase.isObjValid()) {
					player = (Player) objBase.getImpl();
					if(player != null) {
						for(String t : titles) {
							player.getPlayerData().getTitleEntity().addFinishId(Integer.parseInt(t));
						}
						player.getPlayerData().getTitleEntity().notifyUpdate(true);
						isOnline = true;
					}
				}
			} finally {
				if (objBase != null) {
					objBase.unlockObj();
				}
			}
			
			if(!isOnline) {
				List<TitleEntity> titleEntitys = DBManager.getInstance().query("from TitleEntity where playerId = ? and invalid = 0", playerId);
				if(titleEntitys.size() > 0) {
					TitleEntity title = titleEntitys.get(0);
					title.convert();
					for(String t : titles) {
						title.addFinishId(Integer.parseInt(t));
					}
					title.notifyUpdate(false);
				}
			}
			GuaJiScriptManager.sendResponse(httpExchange, "{\"status\":1}");
		} else {
			GuaJiScriptManager.sendResponse(httpExchange, "{\"status\":2,\"msg\":\"invalid params\"}");
		}
	}

}
