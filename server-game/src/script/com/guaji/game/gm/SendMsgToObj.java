package com.guaji.game.gm;

import java.net.URLDecoder;
import java.util.Map;

import org.guaji.msg.Msg;
import org.guaji.os.MyException;
import org.guaji.script.GuaJiScript;
import org.guaji.script.GuaJiScriptManager;
import org.guaji.xid.GuaJiXID;

import com.google.gson.JsonObject;
import com.guaji.game.GsApp;
import com.guaji.game.config.NewMapCfg;
import com.guaji.game.entity.MapStatisticsEntity;
import com.guaji.game.entity.StateEntity;
import com.guaji.game.player.Player;
import com.guaji.game.util.GsConst;
import com.guaji.game.util.PlayerUtil;
import com.sun.net.httpserver.HttpExchange;

/**
 * 設定傳送玩家內部消息
 * http://54.95.152.45:5132/setPassedMap?params=playerid:797;passedMapId:155&user=hawk
 */
public class SendMsgToObj extends GuaJiScript{

	@Override
	public void action(String params, HttpExchange httpExchange) {
		try {
			params = URLDecoder.decode(params, "UTF-8");
			Map<String, String> paramsMap = GuaJiScriptManager.paramsToMap(params);
			if(!paramsMap.containsKey("playerid")){
				JsonObject jsonObject = new JsonObject();
				jsonObject.addProperty("status", -1);
				GuaJiScriptManager.sendResponse(httpExchange, jsonObject.toString());
				return;
			}
			if(!paramsMap.containsKey("msgid")){
				JsonObject jsonObject = new JsonObject();
				jsonObject.addProperty("status", -2);
				GuaJiScriptManager.sendResponse(httpExchange, jsonObject.toString());
				return;
			}
			int playerid = Integer.valueOf(paramsMap.get("playerid"));
			int msgid = Integer.valueOf(paramsMap.get("msgid"));
			Player player = PlayerUtil.queryPlayer(playerid);
			if(player==null){
				JsonObject jsonObject = new JsonObject();
				jsonObject.addProperty("status", -3);
				GuaJiScriptManager.sendResponse(httpExchange, jsonObject.toString());
				return;
			}
			
			Msg hawkMsg = Msg.valueOf(msgid,
					GuaJiXID.valueOf(GsConst.ObjType.PLAYER, player.getId()));
			hawkMsg.pushParam(1);
			GsApp.getInstance().postMsg(hawkMsg);
			
			JsonObject jsonObject = new JsonObject();
			jsonObject.addProperty("status", 1);
			GuaJiScriptManager.sendResponse(httpExchange, jsonObject.toString());
		} catch (Exception e) {
			MyException.catchException(e);
		}
		return ;
	}

}
