package com.guaji.game.gm;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Map;

import org.guaji.script.GuaJiScript;
import org.guaji.script.GuaJiScriptManager;

import com.guaji.game.config.NewMapCfg;
import com.guaji.game.entity.MapEntity;
import com.guaji.game.entity.PlayerEntity;
import com.guaji.game.entity.RoleEntity;
import com.guaji.game.entity.StateEntity;
import com.guaji.game.log.BehaviorLogger.Action;
import com.guaji.game.player.Player;
import com.guaji.game.player.PlayerData;
import com.guaji.game.util.PlayerUtil;
import com.sun.net.httpserver.HttpExchange;

import net.sf.json.JSONArray;
// http://localhost:5132/playerRebirth?params=playerId:106373;&user=efun
public class PlayerRebirthHandler extends GuaJiScript {

	@Override
	public void action(String params, HttpExchange httpExchange) {
		Map<String, String> paramsMap = GuaJiScriptManager.paramsToMap(params);
		try {
			if (paramsMap.containsKey("playerId")) {
				int playerId = Integer.valueOf(paramsMap.get("playerId"));
				Player player = PlayerUtil.queryPlayer(playerId);
				if(player.isOnline()){
					PlayerData playerData = player.getPlayerData();
					refreshAttrAfterRebirth(player);
					openNewMap(player); 
					
					Field mapField = PlayerData.class.getDeclaredField("mapEntity");
					mapField.setAccessible(true);
					MapEntity mapEntity = (MapEntity)mapField.get(playerData);
					
					String state = mapEntity.getState();
					JSONArray array = JSONArray.fromObject(state);
					array.add("{\"mapId\":1001,\"fightTimes\":1}");
					mapEntity.setState(array.toString());
					mapEntity.notifyUpdate(false);
					GuaJiScriptManager.sendResponse(httpExchange, "{\"status\":1}");
				}else{
					GuaJiScriptManager.sendResponse(httpExchange, "{\"status\":2,\"msg\":\"not online\"}");
				}
			}else{
				GuaJiScriptManager.sendResponse(httpExchange, "{\"status\":2,\"msg\":\"invalid params\"}");
			}
		} catch (NoSuchFieldException e) {
			e.printStackTrace();
		} catch (SecurityException e) {
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}

	}

	private void refreshAttrAfterRebirth(Player player) {
		RoleEntity roleEntity = player.getPlayerData().getMainRole();
		List<RoleEntity> roleEntities = player.getPlayerData().getRoleEntities();
		for (RoleEntity eacheRoleEntity : roleEntities) {
			eacheRoleEntity.setRebirthStage(1);
			eacheRoleEntity.notifyUpdate(true);
		}
		PlayerEntity playerEntity = player.getPlayerData().getPlayerEntity();
		playerEntity.setRebirthStage(1);
		roleEntity.setRebirthStage(1);
		playerEntity.notifyUpdate(false);
		roleEntity.notifyUpdate(false);
		player.getPlayerData().syncRoleInfo(0);
		PlayerUtil.refreshOnlineAttribute(player.getPlayerData(), roleEntity);
		player.increaseExp(1, Action.REBIRTH_TALENT);
		// 同步主角信息
		player.getPlayerData().syncRoleInfo(player.getPlayerData().getMainRole().getId());
	}

	/**
	 * 开启新地图
	 */
	private void openNewMap(Player player) {
		StateEntity stateEntity = player.getPlayerData().getStateEntity();
		stateEntity.setPassMapId(55);
		NewMapCfg mapCfg = player.getPlayerData().getBattleMap(stateEntity.getPassMapId());
		if (mapCfg != null) {
			stateEntity.setPassMapId(0);
			stateEntity.setCurBattleMap(mapCfg.getNextMapId());
		}
		stateEntity.notifyUpdate(false);
	}
}
