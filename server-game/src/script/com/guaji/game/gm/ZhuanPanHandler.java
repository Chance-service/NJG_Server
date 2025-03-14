package com.guaji.game.gm;

import java.util.LinkedList;
import java.util.List;

import org.guaji.app.AppObj;
import org.guaji.obj.ObjManager;
import org.guaji.script.GuaJiScript;
import org.guaji.script.GuaJiScriptManager;
import org.guaji.xid.GuaJiXID;

import com.guaji.game.GsApp;
import com.guaji.game.player.Player;
import com.guaji.game.util.GsConst;
import com.guaji.game.protocol.Const;
import com.sun.net.httpserver.HttpExchange;

public class ZhuanPanHandler extends GuaJiScript {
	@Override
	public void action(String params, HttpExchange httpExchange) {
		
		ObjManager<GuaJiXID, AppObj> objManager = GsApp.getInstance().getObjMan(GsConst.ObjType.PLAYER);
		List<AppObj> pList = new LinkedList<>();
		objManager.collectObjValue(pList, null);
		for(AppObj appObj : pList) {
			try{
				Player player = (Player)appObj;
				if(player != null && player.getPlayerData() != null) {
					player.getPlayerData().updateActivity(Const.ActivityId.CRAZY_ROULETTE_VALUE, 1);
				}
			}catch(Exception e) {
				e.printStackTrace();
			}
		}
		
		GuaJiScriptManager.sendResponse(httpExchange, "{\"status\":1}");
	}
}
