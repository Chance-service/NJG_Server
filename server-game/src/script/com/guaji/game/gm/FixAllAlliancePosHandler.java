package com.guaji.game.gm;

import java.util.List;

import org.guaji.app.AppObj;
import org.guaji.cache.CacheObj;
import org.guaji.db.DBManager;
import org.guaji.log.Log;
import org.guaji.obj.ObjBase;
import org.guaji.script.GuaJiScript;
import org.guaji.script.GuaJiScriptManager;
import org.guaji.thread.GuaJiTask;
import org.guaji.util.GuaJiTickable;
import org.guaji.xid.GuaJiXID;

import com.guaji.game.GsApp;
import com.guaji.game.entity.AllianceEntity;
import com.guaji.game.entity.PlayerAllianceEntity;
import com.guaji.game.manager.AllianceManager;
import com.guaji.game.manager.SnapShotManager;
import com.guaji.game.player.Player;
import com.guaji.game.util.GsConst;
import com.sun.net.httpserver.HttpExchange;

/**
 * 玩家定时修复帮主处理
 */
public class FixAllAlliancePosHandler extends GuaJiScript {
	
	static class FixAllianceTask extends GuaJiTask {
		@Override
		protected int run() {
			for(AllianceEntity allianceEntity : AllianceManager.getInstance().getAllianceMap().values()){
				if(allianceEntity != null) {
					int huizhangId = allianceEntity.getPlayerId();
					boolean isTargetOnline = false;
					GuaJiXID targetXid = GuaJiXID.valueOf(GsConst.ObjType.PLAYER, huizhangId);
					if(targetXid != null){
						ObjBase<GuaJiXID, AppObj> objBase = GsApp.getInstance().lockObject(targetXid);
						try {
							if (objBase != null && objBase.isObjValid()) {
								Player targetPlayer = (Player)objBase.getImpl();
								if(targetPlayer.getPlayerData() != null) {
									PlayerAllianceEntity playerAllianceEntity = targetPlayer.getPlayerData().getPlayerAllianceEntity();
									playerAllianceEntity.setPostion(GsConst.Alliance.ALLIANCE_POS_MAIN);
									playerAllianceEntity.notifyUpdate(true);
									isTargetOnline = true;
									// 更新同步数据
									SnapShotManager.getInstance().getCurrentStrategy().onAllianceDataChanged(playerAllianceEntity);
									AllianceManager.getInstance().offlineRefresh(playerAllianceEntity);
								}
							} 
						} finally {
							if (objBase != null) {
								objBase.unlockObj();
							}
						}
					}
					if(!isTargetOnline) {
						// 修改target玩家快照工会数据
						List<PlayerAllianceEntity> playerEntitys = DBManager.getInstance().query("from PlayerAllianceEntity where invalid = 0 and playerId = ?", huizhangId);
						if (playerEntitys != null && playerEntitys.size() > 0) {
							PlayerAllianceEntity playerAllianceEntity = playerEntitys.get(0);
							playerAllianceEntity.init();
							playerAllianceEntity.setPostion(GsConst.Alliance.ALLIANCE_POS_MAIN);
							playerAllianceEntity.notifyUpdate(false);
							// 更新同步数据
							SnapShotManager.getInstance().getCurrentStrategy().onAllianceDataChanged(playerAllianceEntity);
							AllianceManager.getInstance().offlineRefresh(playerAllianceEntity);
						}
					}
				}
			}
			Log.logPrintln("fix all alliance success");
			return 0;
		}

		@Override
		protected CacheObj clone() {
			return null;
		}
		
	}
	
	@Override
	public void action(String params, final HttpExchange httpExchange) {
		// 参数解析: params=playerid:0;type:0
		GsApp.getInstance().addTickable(new GuaJiTickable() {
			private int i = 0;
			@Override
			public void onTick() {
				i++;
				if(i % 600 == 0) {
					GsApp.getInstance().postCommonTask(new FixAllianceTask(), 0);
				}
			}
			@Override
			public String getName() {
				return "FixAlliance";
			}
		});
		
		// 回复状态
		GuaJiScriptManager.sendResponse(httpExchange, "{\"status\":1}");
	}
}
