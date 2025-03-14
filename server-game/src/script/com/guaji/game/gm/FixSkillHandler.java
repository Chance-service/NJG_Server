package com.guaji.game.gm;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.guaji.app.AppObj;
import org.guaji.obj.ObjBase;
import org.guaji.script.GuaJiScript;
import org.guaji.script.GuaJiScriptManager;
import org.guaji.xid.GuaJiXID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.guaji.game.GsApp;
import com.guaji.game.entity.RoleEntity;
import com.guaji.game.entity.SkillEntity;
import com.guaji.game.player.Player;
import com.guaji.game.util.GsConst;
import com.sun.net.httpserver.HttpExchange;

public class FixSkillHandler extends GuaJiScript{
	
	private static Logger logger = LoggerFactory.getLogger("Server");

	@Override
	public void action(String params, HttpExchange httpExchange) {
		
		Map<String, String> paramsMap = GuaJiScriptManager.paramsToMap(params);
		if (paramsMap.containsKey("playerid")) {
			Player player = null;
			int playerId = Integer.valueOf(paramsMap.get("playerid"));
			GuaJiXID xid = GuaJiXID.valueOf(GsConst.ObjType.PLAYER, playerId);
			ObjBase<GuaJiXID, AppObj> objBase = GsApp.getInstance().lockObject(xid);
			try {
				if (objBase != null && objBase.isObjValid()) {
					player = (Player) objBase.getImpl();
					if(player != null) {
						RoleEntity mainRole = player.getPlayerData().getMainRole();
						List<Integer> skill2List = mainRole.getSkill2idList();
						List<Integer> needRemoveSkill2 = new ArrayList<Integer>();
						List<Integer> skill3List = mainRole.getSkill3idList();
						List<Integer> needRemoveSkill3 = new ArrayList<Integer>();
						for(int id : skill2List) {
							SkillEntity skill = player.getPlayerData().getSkillById(id);
							if(skill == null) {
								needRemoveSkill2.add(id);
							}
						}
						
						for(int id : skill3List) {
							SkillEntity skill = player.getPlayerData().getSkillById(id);
							if(skill == null) {
								needRemoveSkill3.add(id);
							}
						}
						skill2List.removeAll(needRemoveSkill2);
						skill3List.removeAll(needRemoveSkill3);
						mainRole.setSkill2idList(skill2List);
						mainRole.setSkill3idList(skill3List);
						mainRole.notifyUpdate(false);
						GuaJiScriptManager.sendResponse(httpExchange, "{\"status\":1}");
						for(int id : skill2List){
							logger.info("delSkill2 message - playerId:{}, skillId:{}", playerId, id);
						}
						for(int id : skill3List){
							logger.info("delSkill3 message - playerId:{}, skillId:{}", playerId, id);
						}
						
					} else {
						GuaJiScriptManager.sendResponse(httpExchange, "{\"status\":error}");
					}
				}
			} finally {
				if (objBase != null) {
					objBase.unlockObj();
				}
			}	
		} else {
			GuaJiScriptManager.sendResponse(httpExchange, "{\"status\":params error}");
		}
	}
}
