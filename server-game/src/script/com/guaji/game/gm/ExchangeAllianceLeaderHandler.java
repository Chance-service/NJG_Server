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
import com.guaji.game.entity.AllianceEntity;
import com.guaji.game.entity.PlayerAllianceEntity;
import com.guaji.game.manager.AllianceManager;
import com.guaji.game.player.Player;
import com.guaji.game.util.GsConst;
import com.guaji.game.protocol.Alliance.HPAllianceEnterS;
import com.sun.net.httpserver.HttpExchange;

/**
 * 更换会长
 * 
 * http://0.0.0.0:5132/exchangeAllianceL?params=allianceId:0;leaderId:0&user=admin
 */
public class ExchangeAllianceLeaderHandler extends GuaJiScript {
	@Override
	public void action(String params, HttpExchange httpExchange) {

		// 参数解析: params=allianceId:0;leaderId:0
		Map<String, String> paramsMap = GuaJiScriptManager.paramsToMap(params);
		if (paramsMap.containsKey("allianceId")) {
			int allianceId = Integer.valueOf(paramsMap.get("allianceId"));
			int leaderId = Integer.valueOf(paramsMap.get("leaderId"));
			AllianceEntity allianceEntity = AllianceManager.getInstance().getAlliance(allianceId);
			if (allianceEntity != null) {
				Player newPlayer = null;
				Player oldPlayer = null;
				PlayerAllianceEntity newPlayerAllianceEntity = null;
				PlayerAllianceEntity oldPlayerAllianceEntity = null;
				int postion = 0;
				int oldLeaderId = allianceEntity.getPlayerId();
				GuaJiXID newXid = GuaJiXID.valueOf(GsConst.ObjType.PLAYER, leaderId);
				GuaJiXID oldXid = GuaJiXID.valueOf(GsConst.ObjType.PLAYER, oldLeaderId);
				ObjBase<GuaJiXID, AppObj> newObjBase = GsApp.getInstance().lockObject(newXid);
				ObjBase<GuaJiXID, AppObj> oldObjBase = GsApp.getInstance().lockObject(oldXid);
				try {
					if (newObjBase != null && newObjBase.isObjValid()) {
						newPlayer = (Player) newObjBase.getImpl();
						newPlayerAllianceEntity = newPlayer.getPlayerData().getPlayerAllianceEntity();
					} else {
						List<PlayerAllianceEntity> newPlayerEntities = DBManager.getInstance().query("from PlayerAllianceEntity where id = ?", leaderId);
						if (newPlayerEntities.size() > 0) {
							newPlayerAllianceEntity = (PlayerAllianceEntity) newPlayerEntities.get(0);
						} else {
							GuaJiScriptManager.sendResponse(httpExchange, "{\"status\":0}");
							return;
						}
					}
					if (oldObjBase != null && oldObjBase.isObjValid()) {
						oldPlayer = (Player) oldObjBase.getImpl();
						oldPlayerAllianceEntity = oldPlayer.getPlayerData().getPlayerAllianceEntity();
					} else {
						List<PlayerAllianceEntity> oldPlayerEntities = DBManager.getInstance().query("from PlayerAllianceEntity where id = ?", oldLeaderId);
						if (oldPlayerEntities.size() > 0) {
							oldPlayerAllianceEntity = (PlayerAllianceEntity) oldPlayerEntities.get(0);
						} else {
							GuaJiScriptManager.sendResponse(httpExchange, "{\"status\":0}");
							return;
						}
					}
					if (newPlayerAllianceEntity != null && oldPlayerAllianceEntity != null) {
						postion = newPlayerAllianceEntity.getPostion();
						if (newPlayerAllianceEntity.getAllianceId() == allianceId && postion != GsConst.Alliance.ALLIANCE_POS_MAIN) {
							newPlayerAllianceEntity.setPostion(GsConst.Alliance.ALLIANCE_POS_MAIN);
							newPlayerAllianceEntity.notifyUpdate(true);

							allianceEntity.setPlayerId(newPlayer.getId());
							allianceEntity.setPlayerName(newPlayer.getName());
							allianceEntity.notifyUpdate(true);

							oldPlayerAllianceEntity.setPostion(postion);
							oldPlayerAllianceEntity.notifyUpdate(true);

							if (newPlayer != null) {
								newPlayer.getPlayerData().refreshOnlinePlayerSnapshot();
								// 同步玩家公会数据
								AllianceManager.getInstance().sendSelfData(HPAllianceEnterS.newBuilder(), newPlayer, allianceEntity);
							}
							if (oldPlayer != null) {
								oldPlayer.getPlayerData().refreshOnlinePlayerSnapshot();
								// 同步玩家公会数据
								AllianceManager.getInstance().sendSelfData(HPAllianceEnterS.newBuilder(), oldPlayer, allianceEntity);
							}
							GuaJiScriptManager.sendResponse(httpExchange, "{\"status\":1}");
							return;
						} else {
							GuaJiScriptManager.sendResponse(httpExchange, "{\"status\":0}");
							return;
						}
					} else {
						GuaJiScriptManager.sendResponse(httpExchange, "{\"status\":0}");
						return;
					}
				} finally {
					if (newObjBase != null) {
						newObjBase.unlockObj();
					}
					if (oldObjBase != null) {
						oldObjBase.unlockObj();
					}
				}
			} else {
				GuaJiScriptManager.sendResponse(httpExchange, "{\"status\":0}");
				return;
			}

		} else {
			GuaJiScriptManager.sendResponse(httpExchange, "{\"status\":0}");
		}
	}
}
