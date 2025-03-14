package com.guaji.game.gm;

import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.guaji.app.AppObj;
import org.guaji.db.DBManager;
import org.guaji.obj.ObjBase;
import org.guaji.script.GuaJiScript;
import org.guaji.script.GuaJiScriptManager;
import org.guaji.xid.GuaJiXID;

import com.guaji.game.GsApp;
import com.guaji.game.ServerData;
import com.guaji.game.player.Player;
import com.guaji.game.util.GsConst;
import com.sun.net.httpserver.HttpExchange;

import net.sf.json.JSONObject;

/**
 * 修改PUID
 */
// params=id1:xxx;id2:xxx;&user=efun
public class ChangePuidHangler extends GuaJiScript {

	private static final Logger logger = Logger.getLogger("GM");

	@Override
	public void action(String params, HttpExchange httpExchange) {

		Map<String, String> paramsMap = GuaJiScriptManager.paramsToMap(params);

		String puid = paramsMap.get("puid");
		String newpuid = paramsMap.get("newpuid");
		String serverId = paramsMap.get("serverId");
//		logger.info(String.format("ChangePuidHangler puid1=%s,puid2=%s,serverid=%s,", puid, newpuid, serverId));
		JSONObject json = new JSONObject();
		if (serverId == null || serverId.trim().length() == 0) {
			json.put("status", "-1");
			json.put("msg", "serverId is null");
			GuaJiScriptManager.sendResponse(httpExchange, json.toString());
			return;
		}

		List<Object[]> puids1 = DBManager.getInstance().executeQuery("select puid,id from player where isguest = 1 and puid=\"" + puid+"\"");
		List<Object[]> puids2 = DBManager.getInstance().executeQuery("select puid,id from player where puid=\"" + newpuid+"\"");

		if (puids1 == null || puids2 == null) {

			json.put("status", "-1");
			json.put("msg", String.format("puid1 is null ?puid1=%s,puid2=%s", puids1, puids2));
//			json.put("ids", String.format("id1=%s,id2=%s", id1, id2));
			GuaJiScriptManager.sendResponse(httpExchange, json.toString());
			return;
		}

		if (puids1.size() < 1 || puids2.size() >= 1) {

			json.put("status", "-1");
			json.put("msg", String.format("puid size is zero ?puid1=%s,puid2=%s", puids1.size(),puids2.size()));
			GuaJiScriptManager.sendResponse(httpExchange, json.toString());
			return;
		}
		String puid1 = String.valueOf(puids1.get(0)[0]);
		String puid2 = newpuid;
//		String puid2 = String.valueOf(puids2.get(0)[0]);
		String id1 = String.valueOf(puids1.get(0)[1]);
//		String id2 = String.valueOf(puids2.get(0)[1]);
//		if (device1 == null || device2 == null) {
//
//			json.put("status", "-1");
//			json.put("msg", String.format("device do not equals device1=%s,device2=%s", device1, device2));
//			GuaJiScriptManager.sendResponse(httpExchange, json.toString());
//			return;
//		}

//		ServerData.getInstance().addPuidAndPlayerId(puid1, Integer.parseInt(serverId), Integer.parseInt(id2));
		ServerData.getInstance().addPuidAndPlayerId(puid2, Integer.parseInt(serverId), Integer.parseInt(id1));
		ObjBase<GuaJiXID, AppObj> objBase1 = GsApp.getInstance().lockObject(GuaJiXID.valueOf(GsConst.ObjType.PLAYER, Integer.parseInt(id1)));
//		ObjBase<GuaJiXID, AppObj> objBase2 = GsApp.getInstance().lockObject( GuaJiXID.valueOf(GsConst.ObjType.PLAYER, Integer.parseInt(id2)));
		reSetPuid(id1, puid2, objBase1, json);
//		reSetPuid(id2, puid1, objBase2, json);

		json.put("status", "200");
		json.put("msg", String.format("puid1=%s,puid2=%s", puid1 + "_" + ServerData.getInstance().getPlayerIdByPuid(puid1, Integer.parseInt(serverId)),
														   puid2 + "_" + ServerData.getInstance().getPlayerIdByPuid(puid2, Integer.parseInt(serverId))));
//		DBManager.getInstance().executeUpdate(String.format("update player set puid='%s' where id=%d", "bak_" + puid1, Integer.parseInt(id1)));
//		DBManager.getInstance().executeUpdate(String.format("update player set puid='%s' where id=%d", "bak_" + puid2, Integer.parseInt(id2)));
		DBManager.getInstance().executeUpdate(String.format("update player set puid='%s',pwd=888888,isguest=1 where id=%d", puid2, Integer.parseInt(id1)));
//		DBManager.getInstance().executeUpdate(String.format("update player set puid='%s' where id=%d", puid1, Integer.parseInt(id2)));

		logger.info(String.format("ChangePuidHangler id1=%s,puid1=%s,puid2=%s", id1, puid1, puid2));
//		logger.info(String.format("ChangePuidHangler id1=%s,id2=%s,device1=%s,device2=%s,puid1=%s,puid2=%s", id1, id2, device1, device2, puid1, puid2));
		GuaJiScriptManager.sendResponse(httpExchange, json.toString());
	}

	private void reSetPuid(String id1, String puid2, ObjBase<GuaJiXID, AppObj> objBase1, JSONObject json) {
		try {
			if (objBase1 != null) {

				Player player = (Player) objBase1.getImpl();
				player.getPlayerData().getPlayerEntity().setPuid(puid2);
				player.getPlayerData().getPlayerEntity().setpwd("888888");
				player.getPlayerData().getPlayerEntity().setisguest(1);
				json.put(player.getId(), player.getPlayerData().getPlayerEntity().getPuid());
			}

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (objBase1 != null) {
				objBase1.unlockObj();
			}

		}
	}

}
