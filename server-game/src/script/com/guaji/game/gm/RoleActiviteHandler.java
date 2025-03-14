package com.guaji.game.gm;

import java.util.Map;

import org.guaji.msg.Msg;
import org.guaji.script.GuaJiScript;
import org.guaji.script.GuaJiScriptManager;
import org.guaji.xid.GuaJiXID;

import com.guaji.game.GsApp;
import com.guaji.game.util.GsConst;
import com.sun.net.httpserver.HttpExchange;

/**
 * 玩家发奖励
 * curl 'localhost:5132/roleActivite?params=playerId:9;roleId:10;&user=admin'
 */
public class RoleActiviteHandler extends GuaJiScript {
	@Override
	public void action(String params, HttpExchange httpExchange) {
		Map<String, String> paramsMap = GuaJiScriptManager.paramsToMap(params);
		if (paramsMap.containsKey("playerId") && paramsMap.containsKey("roleId")) {
			int playerId = Integer.valueOf(paramsMap.get("playerId"));
			int roleId = Integer.valueOf(paramsMap.get("roleId"));

			// 错误屏蔽
			if (playerId == 0 || roleId == 0) {
				GuaJiScriptManager.sendResponse(httpExchange, "{\"status\":2,\"msg\":\"playerid is 0\"}");
				return;
			}

			Msg hawkMsg = Msg.valueOf(GsConst.MsgType.AUTO_EMPLOY_ROLE, GuaJiXID.valueOf(GsConst.ObjType.PLAYER, playerId));
			hawkMsg.pushParam(roleId);
			GsApp.getInstance().postMsg(hawkMsg);
			GuaJiScriptManager.sendResponse(httpExchange, "{\"status\":1}");
		}
	}
}
