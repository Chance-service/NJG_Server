package com.guaji.game.gm;

import java.util.List;
import java.util.Map;

import org.guaji.app.AppObj;
import org.guaji.config.ConfigManager;
import org.guaji.db.DBManager;
import org.guaji.obj.ObjBase;
import org.guaji.os.MyException;
import org.guaji.script.GuaJiScript;
import org.guaji.script.GuaJiScriptManager;
import org.guaji.xid.GuaJiXID;

import com.guaji.game.GsApp;
import com.guaji.game.config.GodlyLevelExpCfg;
import com.guaji.game.entity.EquipEntity;
import com.guaji.game.entity.PlayerEntity;
import com.guaji.game.log.BehaviorLogger;
import com.guaji.game.log.BehaviorLogger.Action;
import com.guaji.game.log.BehaviorLogger.Params;
import com.guaji.game.log.BehaviorLogger.Source;
import com.guaji.game.player.Player;
import com.guaji.game.util.ConfigUtil;
import com.guaji.game.util.EquipUtil;
import com.guaji.game.util.GsConst;
import com.guaji.game.protocol.Const;
import com.sun.net.httpserver.HttpExchange;

/**
 * 配置重新加载
 * 
 * http://127.0.0.1:5132/genequip?user=admin&params=playerid:9;equipid:50350071;strength:10;starlevel1:15;starlevel2:15
 */
public class GenEquipHandler extends GuaJiScript {
	/**
	 * 生成装备
	 * 
	 * @param player
	 * @param equipId
	 * @param isGodly
	 * @param strength
	 * @param starLevel1
	 * @param starLevel2
	 * @return
	 */
	private EquipEntity genEquipEntity(Player player, int equipId, int strength, int starLevel1, int starLevel2) {
		if (!ConfigUtil.check(Const.itemType.EQUIP_VALUE, equipId)) {
			return null;
		}

		EquipEntity equipEntity = EquipUtil.generateEquip(player, equipId, starLevel1 > 0 ? 10000 : 0, starLevel2 > 0 ? true : false, 4, true);
		if (equipEntity != null) {
			for (int i = 0; i <= strength; i++) {
				equipEntity.setStrength(strength);
				//int costItemCount = 0; EquipStrengthRatioCfg.getStrengthItemCount(equipEntity);
				// equipEntity.setStrengthItemCount(equipEntity.getStrengthItemCount()
				// + costItemCount);
			}
			equipEntity.setStarLevel(starLevel1);
			equipEntity.setStarLevel2(starLevel2);
			if (starLevel1 > 1)
			{
				GodlyLevelExpCfg last = GodlyLevelExpCfg.getConfigByLevel(starLevel1-1);
				equipEntity.setStarExp(last.getExp());
			}
			if (starLevel2 > 1)
			{
				GodlyLevelExpCfg last2 = GodlyLevelExpCfg.getConfigByLevel(starLevel2-1);
				equipEntity.setStarExp2(last2.getExp2());
			}
			EquipUtil.refreshAttribute(equipEntity, player.getPlayerData());
			if (DBManager.getInstance().create(equipEntity)) {
				if (player.getPlayerData().getEquipEntities() != null) {
					player.getPlayerData().getEquipEntities().add(equipEntity);
				}

				BehaviorLogger.log4Service(player, Source.GM_OPERATION, Action.GM_AWARD, Params.valueOf("equipId", equipId),
						Params.valueOf("id", equipEntity.getId()), Params.valueOf("strength", strength), Params.valueOf("starLevel1", starLevel1),
						Params.valueOf("starLevel2", starLevel2), Params.valueOf("attr", equipEntity.getAttribute().toString()),
						Params.valueOf("godlyAttrId", equipEntity.getGodlyAttrId()), Params.valueOf("godlyAttrId2", equipEntity.getGodlyAttrId2()));

				return equipEntity;
			}
		}
		return null;
	}

	@Override
	public void action(String params, HttpExchange httpExchange) {
		try {
			Map<String, String> paramsMap = GuaJiScriptManager.paramsToMap(params);
			if (paramsMap.containsKey("playerid") && paramsMap.containsKey("equipid") && paramsMap.containsKey("strength")
					&& paramsMap.containsKey("starlevel1") && paramsMap.containsKey("starlevel2")) {

				int playerid = Integer.valueOf(paramsMap.get("playerid"));
				int equipid = Integer.valueOf(paramsMap.get("equipid"));
				int strength = Integer.valueOf(paramsMap.get("strength"));
				int starlevel1 = Integer.valueOf(paramsMap.get("starlevel1"));
				int starlevel2 = Integer.valueOf(paramsMap.get("starlevel2"));

				if (playerid == 0 || equipid == 0 || strength < 0 || starlevel1 < 0 || starlevel2 < 0 || starlevel1 > 40
						|| starlevel2 > 40) {
					GuaJiScriptManager.sendResponse(httpExchange, "{\"status\":-999}");
					return;
				}

				Player player = null;
				GuaJiXID xid = GuaJiXID.valueOf(GsConst.ObjType.PLAYER, playerid);
				ObjBase<GuaJiXID, AppObj> objBase = GsApp.getInstance().lockObject(xid);
				try {
					if (objBase != null) {
						player = (Player) objBase.getImpl();
					} else if (player == null) {
						PlayerEntity playerEntity = null;
						List<PlayerEntity> playerEntities = DBManager.getInstance().query("from PlayerEntity where id = ?", playerid);
						if (playerEntities.size() > 0) {
							playerEntity = (PlayerEntity) playerEntities.get(0);
						}

						if (playerEntity != null) {
							player = new Player(GuaJiXID.valueOf(GsConst.ObjType.PLAYER, playerid));
							player.getPlayerData().setPlayerEntity(playerEntity);
						}
					}

					if (player != null) {
						EquipEntity equipEntity = genEquipEntity(player, equipid, strength, starlevel1, starlevel2);
						if (equipEntity != null) {
							if (player.isOnline()) {
								player.getPlayerData().syncEquipInfo(equipEntity.getId());
							}
						} else {
							GuaJiScriptManager.sendResponse(httpExchange, "{\"status\":-1}");
						}
					}
				} finally {
					if (objBase != null) {
						objBase.unlockObj();
					}
				}
			}
			GuaJiScriptManager.sendResponse(httpExchange, "{\"status\":1}");
		} catch (Exception e) {
			MyException.catchException(e);
		}
	}
}
