package com.guaji.game.gm;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.guaji.script.GuaJiScript;
import org.guaji.script.GuaJiScriptManager;

import com.google.gson.JsonObject;
import com.guaji.game.entity.AllianceBattleItem;
import com.guaji.game.entity.AllianceEntity;
import com.guaji.game.manager.AllianceBattleManager;
import com.guaji.game.manager.AllianceManager;
import com.guaji.game.util.GsConst;
import com.guaji.game.util.GsonUtil;
import com.sun.net.httpserver.HttpExchange;

/**
 * 玩家封号处理
 */
public class FixAllianceMemberHandler extends GuaJiScript {
	@Override
	public void action(String params, HttpExchange httpExchange) {
		JsonObject jsonObject = new JsonObject();
		for(AllianceBattleItem allianceBattleItem : AllianceBattleManager.getInstance().getAllianceBattleItems()) {
			if(allianceBattleItem != null) {
				AllianceEntity allianceEntity = AllianceManager.getInstance().getAlliance(allianceBattleItem.getAllianceId());
				if(allianceEntity == null) {
					continue;
				}
				HashMap<Integer, Integer> existMap = new HashMap<>();
				List<Integer> memberIdList = allianceBattleItem.getMemberList();
				memberIdList.clear();
				for(int teamIndex : GsConst.AllianceBattle.ALL_TEAM) {
					List<Integer> memberIds = allianceBattleItem.getTeamMemberIds(teamIndex);
					if(memberIds != null) {
						Iterator<Integer> iter = memberIds.iterator();
						while(iter.hasNext()) {
							Integer memberId = iter.next();
							if(existMap.containsKey(memberId)) {
								iter.remove();
							}else{
								existMap.put(memberId, 1);
								memberIdList.add(memberId);
							}
						}
					}
				}
				allianceBattleItem.setMemberListStr(GsonUtil.getJsonInstance().toJson(memberIdList));
				allianceBattleItem.notifyUpdate(false);
			}
		}
		jsonObject.addProperty("status", 1);
		GuaJiScriptManager.sendResponse(httpExchange, jsonObject.toString());
	}
}
