package com.guaji.game.gm;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.guaji.app.AppObj;
import org.guaji.db.DBManager;
import org.guaji.obj.ObjBase;
import org.guaji.script.GuaJiScript;
import org.guaji.script.GuaJiScriptManager;
import org.guaji.xid.GuaJiXID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.guaji.game.GsApp;
import com.guaji.game.entity.RoleEntity;
import com.guaji.game.player.Player;
import com.guaji.game.util.GsConst;
import com.sun.net.httpserver.HttpExchange;

/**
 * 修复玩家多余佣兵问题。
 * 需要手动添加玩家id
 */
public class DeleteErorrRoleHandler extends GuaJiScript{
	
	private static Logger logger = LoggerFactory.getLogger("Server");

	@Override
	public void action(String params, HttpExchange httpExchange) {
		
		Set<Integer> playerIds = getPlayerIds();
		Map<String, String> paramsMap = GuaJiScriptManager.paramsToMap(params);
		if(paramsMap.containsKey("playerids")) {
			String[] sPlayerIds = paramsMap.get("playerids").split(",");
			for(String sPlayerId : sPlayerIds) {
				int playerId = 0;
				try {
					playerId = Integer.parseInt(sPlayerId.trim());
				} catch (NumberFormatException e) {
					logger.info("delSkill message error- playerId:{}", playerId);
					continue;
				}
				playerIds.add(playerId);
			}
		}
		Player player = null;
		List<RoleEntity> roleEntitys = null;
		
		for(Integer playerId : playerIds) {
			boolean isOnline = false;
			GuaJiXID xid = GuaJiXID.valueOf(GsConst.ObjType.PLAYER, playerId);
			ObjBase<GuaJiXID, AppObj> objBase = GsApp.getInstance().lockObject(xid);
			try {
				if (objBase != null && objBase.isObjValid()) {
					player = (Player) objBase.getImpl();
					if(player != null) {
						roleEntitys = player.getPlayerData().getRoleEntities();
						
						Collection<RoleEntity> roles = deleteErrorRole(roleEntitys);
						for(RoleEntity roleEntity : roles) {
							roleEntity.delete();
							roleEntity.notifyUpdate(false);
							roleEntitys.remove(roleEntity);
							logger.info("delSkill message - playerId:{}, roleId:{}", playerId, roleEntity.getId());
						}
						
						player.getPlayerData().syncRoleInfo(0);
						isOnline = true;
					}
				}
			} finally {
				if (objBase != null) {
					objBase.unlockObj();
				}
			}
			
			if(!isOnline) {
				roleEntitys = DBManager.getInstance().query("from RoleEntity where playerId = ? and invalid = 0", playerId);
				Collection<RoleEntity> roles = deleteErrorRole(roleEntitys);
				for(RoleEntity roleEntity : roles) {
					roleEntity.delete();
					roleEntity.notifyUpdate(false);
					logger.info("delErrorRole message - playerId:{}, roleId:{}", playerId, roleEntity.getId());
				}
			}
		}
		GuaJiScriptManager.sendResponse(httpExchange, "{\"status\":1}");
			
	}
	
	public Collection<RoleEntity> deleteErrorRole(List<RoleEntity> roleEntitys) {
		
		Map<Integer, List<RoleEntity>> roles = new HashMap<Integer, List<RoleEntity>>();
		
		for(RoleEntity role : roleEntitys) {
			int item = role.getItemId();
			if(roles.containsKey(item)) {
				roles.get(item).add(role);
			} else {
				List<RoleEntity> list = new ArrayList<RoleEntity>();
				list.add(role);
				roles.put(item, list);
			}
		}
		
		Set<RoleEntity> needRemoveAll = new HashSet<>();
		Field attrInfo;
		try {
			attrInfo = RoleEntity.class.getDeclaredField("attrInfo");
			attrInfo.setAccessible(true);
		} catch (NoSuchFieldException e) {
			e.printStackTrace();
			return needRemoveAll;
		} catch (SecurityException e) {
			e.printStackTrace();
			return needRemoveAll;
		}
		
		Set<Map.Entry<Integer, List<RoleEntity>>> entrys = roles.entrySet();
		for(Map.Entry<Integer, List<RoleEntity>> entry : entrys) {
			if(entry.getValue().size() > 1) {
				List<RoleEntity> needRemoveList = new LinkedList<>();
				RoleEntity first = entry.getValue().get(0);
				for(int i= 1;i < entry.getValue().size();i++) {
					RoleEntity r = entry.getValue().get(i);
					try {
						String addValueStr = (String)attrInfo.get(r);
						if(addValueStr != null && !"".equals(addValueStr)) {
							attrInfo.set(first,((String)attrInfo.get(first)) + "," + addValueStr);
						}
						attrInfo.set(r, "");
						r.notifyUpdate(false);
					} catch (IllegalArgumentException e) {
						e.printStackTrace();
					} catch (IllegalAccessException e) {
						e.printStackTrace();
					}
				}
				first.notifyUpdate(false);
				for(RoleEntity r : entry.getValue()) {
					try {
						if(attrInfo.get(r).equals("")) {
							needRemoveList.add(r);
						}
					} catch (IllegalArgumentException e) {
						e.printStackTrace();
					} catch (IllegalAccessException e) {
						e.printStackTrace();
					}
				}
				if(entry.getValue().size() == needRemoveList.size()) {
					needRemoveList.remove(0);
				}
				needRemoveAll.addAll(needRemoveList);
			}
		}
		return needRemoveAll;
	
	}
	
	public Set<Integer> getPlayerIds() {
		
		Set<Integer> playerIds = new HashSet<Integer>();
		
		return playerIds;
		
	}
}
	
