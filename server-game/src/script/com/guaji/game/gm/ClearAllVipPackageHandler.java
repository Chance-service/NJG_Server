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
import com.guaji.game.module.activity.vipPackage.VipPackageStatus;
import com.guaji.game.player.Player;
import com.guaji.game.util.ActivityUtil;
import com.guaji.game.util.GsConst;
import com.guaji.game.protocol.Const;
import com.sun.net.httpserver.HttpExchange;

/**
 * 清除所有人的VIP折扣礼包购买状态
 */
// curl 'localhost:5132/clearVipPackage?user=hawk'
public class ClearAllVipPackageHandler extends GuaJiScript {
	@Override
	public void action(String params, HttpExchange httpExchange) {

		String sql = "update player_activity set statusStr = '{\"packetTable\":{}}' where activityId = 95";
		DBManager.getInstance().executeUpdate(sql);
		ObjManager<GuaJiXID, AppObj> objManager = GsApp.getInstance().getObjMan(GsConst.ObjType.PLAYER);
		List<AppObj> pList = new LinkedList<>();
		objManager.collectObjValue(pList, null);
		for (AppObj appObj : pList) {
			Player player = (Player) appObj;
			if (player.getPlayerData() == null) {
				continue;
			}
			VipPackageStatus vipPackageStatus = ActivityUtil.getVipPackageStatus(player.getPlayerData());
			if (vipPackageStatus != null) {
				vipPackageStatus.getInfo().clear();
			}
			player.getPlayerData().updateActivity(Const.ActivityId.VIP_PACKAGE_VALUE, 0, true);
		}
		GuaJiScriptManager.sendResponse(httpExchange, "OK");
	}
}
