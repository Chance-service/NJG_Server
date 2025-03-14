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
import com.guaji.game.manager.SnapShotManager;
import com.guaji.game.player.Player;
import com.guaji.game.util.GsConst;
import com.guaji.game.protocol.Snapshot.PlayerSnapshotInfo;
import com.sun.net.httpserver.HttpExchange;

/**
 * 玩家封号处理
 */
public class FixAlliancePosHandler extends GuaJiScript {
	@Override
	public void action(String params, HttpExchange httpExchange) {
		// 参数解析: params=playerid:0;type:0
		Map<String, String> paramsMap = GuaJiScriptManager.paramsToMap(params);
		if (paramsMap.containsKey("allianceId")) {
			int allianceId = Integer.valueOf(paramsMap.get("allianceId"));
			int playerId = paramsMap.containsKey("playerId") ? Integer.valueOf(paramsMap.get("playerId")) : 0;
			PlayerSnapshotInfo.Builder playerSnapShot = SnapShotManager.getInstance().getPlayerSnapShot(playerId);
			AllianceEntity allianceEntity = AllianceManager.getInstance().getAlliance(allianceId);
			if(allianceEntity != null) {
				int oldHuizhangId = allianceEntity.getPlayerId();
				if(playerId > 0) {
					if(!allianceEntity.getMemberList().contains(playerId)) {
						allianceEntity.getMemberList().add(playerId);
					}
					allianceEntity.setPlayerId(playerId);
					allianceEntity.setPlayerName(playerSnapShot.getMainRoleInfo().getName());
					allianceEntity.notifyUpdate(true);
				}
				int huizhangId = allianceEntity.getPlayerId();
				for(int pid : allianceEntity.getMemberList()) {
					if(pid == huizhangId || oldHuizhangId == pid) {
						boolean isTargetOnline = false;
						GuaJiXID targetXid = GuaJiXID.valueOf(GsConst.ObjType.PLAYER, pid);
						if(targetXid != null){
							ObjBase<GuaJiXID, AppObj> objBase = GsApp.getInstance().lockObject(targetXid);
							try {
								if (objBase != null && objBase.isObjValid()) {
									Player targetPlayer = (Player)objBase.getImpl();
									PlayerAllianceEntity playerAllianceEntity = targetPlayer.getPlayerData().getPlayerAllianceEntity();
									playerAllianceEntity.setAllianceId(allianceEntity.getId());
									playerAllianceEntity.setPostion(pid == huizhangId ? GsConst.Alliance.ALLIANCE_POS_MAIN : GsConst.Alliance.ALLIANCE_POS_COMMON);
									playerAllianceEntity.notifyUpdate(false);
									isTargetOnline = true;
									// 更新同步数据
									SnapShotManager.getInstance().getCurrentStrategy().onAllianceDataChanged(playerAllianceEntity);
									AllianceManager.getInstance().offlineRefresh(playerAllianceEntity);
								} 
							} finally {
								if (objBase != null) {
									objBase.unlockObj();
								}
							}
						}
						if(!isTargetOnline) {
							// 修改target玩家快照工会数据
							List<PlayerAllianceEntity> playerEntitys = DBManager.getInstance().query("from PlayerAllianceEntity where invalid = 0 and playerId = ?", pid);
							if (playerEntitys != null && playerEntitys.size() > 0) {
								PlayerAllianceEntity playerAllianceEntity = playerEntitys.get(0);
								playerAllianceEntity.init();
								playerAllianceEntity.setAllianceId(allianceEntity.getId());
								playerAllianceEntity.setPostion(pid == huizhangId ? GsConst.Alliance.ALLIANCE_POS_MAIN : GsConst.Alliance.ALLIANCE_POS_COMMON);
								playerAllianceEntity.notifyUpdate(false);
								// 更新同步数据
								SnapShotManager.getInstance().getCurrentStrategy().onAllianceDataChanged(playerAllianceEntity);
								AllianceManager.getInstance().offlineRefresh(playerAllianceEntity);
							}
						}
					}
				}
			}
			// 回复状态
			GuaJiScriptManager.sendResponse(httpExchange, "{\"status\":1}");
		} else {
			GuaJiScriptManager.sendResponse(httpExchange, "{\"status\":2,\"msg\":\"invalid params\"}");
		}
	}
}
