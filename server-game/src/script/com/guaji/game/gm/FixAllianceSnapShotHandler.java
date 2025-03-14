package com.guaji.game.gm;

import java.util.List;
import java.util.Map;

import org.guaji.db.DBManager;
import org.guaji.script.GuaJiScript;
import org.guaji.script.GuaJiScriptManager;

import com.google.gson.JsonObject;
import com.guaji.game.entity.PlayerAllianceEntity;
import com.guaji.game.manager.SnapShotManager;
import com.guaji.game.util.BuilderUtil;
import com.guaji.game.protocol.Alliance.AllianceInfo;
import com.guaji.game.protocol.Snapshot.PlayerSnapshotInfo;
import com.sun.net.httpserver.HttpExchange;

public class FixAllianceSnapShotHandler extends GuaJiScript {

	@Override
	public void action(String params, HttpExchange httpExchange) {
		Map<String, String> paramsMap = GuaJiScriptManager.paramsToMap(params);
		JsonObject jsonObject = new JsonObject();
		if (paramsMap.containsKey("playerid")) {
			int playerId = Integer.valueOf(paramsMap.get("playerid"));
			PlayerSnapshotInfo.Builder playerSnapshotInfo = SnapShotManager.getInstance().getPlayerSnapShot(playerId);
			
			PlayerAllianceEntity playerAllianceEntity = null;
			List<PlayerAllianceEntity> playerEntitys = DBManager.getInstance().query("from PlayerAllianceEntity where playerId = ? and invalid = 0", playerId);
			if (playerEntitys != null && playerEntitys.size() > 0) {
				playerAllianceEntity = playerEntitys.get(0);
			}
			
			if(playerAllianceEntity == null){
				jsonObject.addProperty("playerAllianceEntity", "null");
				GuaJiScriptManager.sendResponse(httpExchange, jsonObject.toString());
				return;
			}
			
			AllianceInfo.Builder builder = BuilderUtil.genAllianceBuilder(playerAllianceEntity);
			playerSnapshotInfo.setAllianceInfo(builder.build());
			SnapShotManager.getInstance().cacheSnapshot(playerId, playerSnapshotInfo);
			
			jsonObject.addProperty("contribution", playerAllianceEntity.getContribution());
			jsonObject.addProperty("status", 1);
		}
		GuaJiScriptManager.sendResponse(httpExchange, jsonObject.toString());
	}
}
