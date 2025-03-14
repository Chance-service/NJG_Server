package com.guaji.game.gm;

import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.guaji.db.DBManager;
import org.guaji.script.GuaJiScript;
import org.guaji.script.GuaJiScriptManager;

import com.guaji.game.entity.AllianceEntity;
import com.guaji.game.entity.PlayerAllianceEntity;
import com.guaji.game.manager.AllianceManager;
import com.guaji.game.manager.SnapShotManager;
import com.guaji.game.player.Player;
import com.guaji.game.util.GsConst;
import com.guaji.game.util.PlayerUtil;
import com.guaji.game.protocol.Snapshot.PlayerSnapshotInfo;
import com.sun.net.httpserver.HttpExchange;

import net.sf.json.JSONObject;

/**
 * 会长转让
 */
// 参数解析: params=allianceId:xxx;sourceId:xxx;targetId:xxxx;&user=efun
// 公会ID 转让者的playerId 目标者的playerId
public class ChangePostionHandler extends GuaJiScript {

	private static final Logger logger = Logger.getLogger("GM");
	
	@Override
	public void action(String params, HttpExchange httpExchange) {
		
		Map<String, String> paramsMap = GuaJiScriptManager.paramsToMap(params);

		String allianceId=paramsMap.get("allianceId");
		String sourceId=paramsMap.get("sourceId");
		String targetId=paramsMap.get("targetId");
		
		JSONObject json = new JSONObject();
		if(allianceId == null || sourceId == null  || targetId == null){
			json.put("status", "2");
			json.put("msg", String.format("value is null?allianceId %s, sourceId %s, targetId %s",allianceId, sourceId, targetId));
			GuaJiScriptManager.sendResponse(httpExchange, json.toString());
			return ;
		}

		AllianceEntity allianceEntity = AllianceManager.getInstance().getAlliance(Integer.parseInt(allianceId));
		if (null == allianceEntity) {
			json.put("status", "3");
			json.put("msg", String.format("alliance is null? allianceId %s", allianceId));
			GuaJiScriptManager.sendResponse(httpExchange, json.toString());
			return ;
		}
		
		List<Object[]> allianceList = DBManager.getInstance().executeQuery("select playerId,postion from player_alliance where allianceId="+ allianceId);
		
		if(allianceList.size() < 2){
			json.put("status", "4");
			json.put("msg", String.format("alliance size is error %d", allianceList.size()));
			GuaJiScriptManager.sendResponse(httpExchange, json.toString());
			return ;
		}
		
		boolean isEquals = false;
		boolean isMain = false;
		for (Object[] obj : allianceList) {
			String strId = String.valueOf(obj[0]);
			int postion = Integer.parseInt(String.valueOf(obj[1]));
			if (strId.equals(sourceId) && postion == GsConst.Alliance.ALLIANCE_POS_MAIN) {
				isMain = true;
			}
			if (strId.equals(targetId)) {
				isEquals = true;
			}
		}
		
		if (!isMain) {
			json.put("status", "5");
			json.put("msg", String.format("sourceId %s is not target pos main", sourceId));
			GuaJiScriptManager.sendResponse(httpExchange, json.toString());
			return;
		}
		
		if (!isEquals) {
			json.put("status", "6");
			json.put("msg", "allianceId do not equals");
			GuaJiScriptManager.sendResponse(httpExchange, json.toString());
			return;
		}
		
		// 转让者的公会数据
		int playerId = Integer.valueOf(sourceId);
		Player player = PlayerUtil.queryPlayer(playerId);
		if (null != player) {
			PlayerAllianceEntity p_allianceEntity = player.getPlayerData().getPlayerAllianceEntity();
			p_allianceEntity.setPostion(GsConst.Alliance.ALLIANCE_POS_COMMON);
		}
		player = null;
		// 目标者的公会数据
		playerId = Integer.valueOf(targetId);
		player = PlayerUtil.queryPlayer(playerId);
		if (null != player) {
			PlayerAllianceEntity p_allianceEntity = player.getPlayerData().getPlayerAllianceEntity();
			p_allianceEntity.setPostion(GsConst.Alliance.ALLIANCE_POS_MAIN);
			
			allianceEntity.setPlayerId(player.getId());
			allianceEntity.setPlayerName(player.getName());
			allianceEntity.notifyUpdate(true);
		} else {
			PlayerSnapshotInfo.Builder builder = SnapShotManager.getInstance().getPlayerSnapShot(playerId);
			allianceEntity.setPlayerId(playerId);
			allianceEntity.setPlayerName(builder.getMainRoleInfo().getName());
			allianceEntity.notifyUpdate(true);
		}
		
		json.put("status", "1");
		json.put("msg", "change postion successfully");
		GuaJiScriptManager.sendResponse(httpExchange, json.toString());
		
		DBManager.getInstance().executeUpdate(String.format("update player_alliance set postion=%d where playerId=%d", GsConst.Alliance.ALLIANCE_POS_COMMON, Integer.parseInt(sourceId)));
		DBManager.getInstance().executeUpdate(String.format("update player_alliance set postion=%d where playerId=%d", GsConst.Alliance.ALLIANCE_POS_MAIN, Integer.parseInt(targetId)));

		logger.info(String.format("ChangePostionHandler sourceId=%s,targetId=%s", sourceId, targetId));
	}
}
