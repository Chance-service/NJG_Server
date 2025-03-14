package com.guaji.game.gm;


import java.util.LinkedList;
import java.util.List;

import org.guaji.app.AppObj;
import org.guaji.db.DBManager;
import org.guaji.obj.ObjManager;
import org.guaji.script.GuaJiScript;
import org.guaji.script.GuaJiScriptManager;
import org.guaji.xid.GuaJiXID;

import com.guaji.game.GsApp;
import com.guaji.game.module.activity.recharge.FirstRechargeStatus;
import com.guaji.game.player.Player;
import com.guaji.game.util.ActivityUtil;
import com.guaji.game.util.GsConst;
import com.guaji.game.protocol.Const;
import com.sun.net.httpserver.HttpExchange;

/**
 * 清除所有人的首充状态
 */
// curl 'localhost:5132/clearrecharge?user=hawk'
public class ClearAllFirstRechargeInfoHandler extends GuaJiScript {
	@Override
	public void action(String params, HttpExchange httpExchange) {
		
		String sql = "update player_activity set statusStr = '{\"rechargeInfo\":{}}' where activityId = 4";
		DBManager.getInstance().executeUpdate(sql);
		ObjManager<GuaJiXID, AppObj> objManager = GsApp.getInstance().getObjMan(GsConst.ObjType.PLAYER);
		List<AppObj> pList = new LinkedList<>();
		objManager.collectObjValue(pList, null);
		for(AppObj appObj : pList) {
			Player player = (Player) appObj;
			if(player.getPlayerData() == null) {
				continue ;
			}
			FirstRechargeStatus firstRechargeStatus = ActivityUtil.getFirstRechargeStatus(player.getPlayerData());
			if(firstRechargeStatus != null) {
				firstRechargeStatus.clear();
			}
			player.getPlayerData().updateActivity(Const.ActivityId.RECHARGE_RATIO_VALUE, 0, true);
		}
		
		GuaJiScriptManager.sendResponse(httpExchange, "OK");
	}
}
