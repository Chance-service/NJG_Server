package com.guaji.game.gm;

import java.util.List;

import org.guaji.db.DBManager;
import org.guaji.script.GuaJiScript;
import org.guaji.script.GuaJiScriptManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.JsonObject;
import com.guaji.game.entity.TeamEntity;
import com.guaji.game.item.AwardItems;
import com.guaji.game.manager.MailManager;
import com.guaji.game.protocol.Mail.MailType;
import com.sun.net.httpserver.HttpExchange;

public class TeamBattleCompensateHandler extends GuaJiScript {

	@Override
	public void action(String params, HttpExchange httpExchange) {
		Logger logger = LoggerFactory.getLogger("Server");
		JsonObject jsonObject = new JsonObject();
		List<TeamEntity> teamEntities = DBManager.getInstance().query("from TeamEntity where stageId = 201412121");
		if(teamEntities.size() > 0){
			String cIds = "";
			for(TeamEntity team : teamEntities){
				if(team.getIsWeedOut() <= 0){
					int captainId = team.getCaptainId();
					int id = team.getId();
					List<Integer> members = team.getTeamMembers();
					// 发队长奖励
					if(captainId > 0){
						cIds += captainId + ",";
						String message = "12月12日14点团战异常处理，队长玩家补偿：";
						AwardItems awardItems = AwardItems.valueOf("10000_1002_500000,30000_41001_10,30000_41002_1");
						MailManager.createMail(captainId, MailType.Reward_VALUE, 0, message, awardItems);
						logger.info("1212 TeamBattleCompensate - teamId : {}, captainId : {}", id , captainId);
						members.remove((Integer)captainId);
					}
					
					// 发队员奖励
					for(int memberId : members){
						String message = "12月12日14点团战异常处理，队员玩家补偿：";
						AwardItems awardItems = AwardItems.valueOf("10000_1002_300000,30000_41001_3");
						MailManager.createMail(memberId, MailType.Reward_VALUE, 0, message, awardItems);
						logger.info("1212 TeamBattleCompensate - teamId : {}, memberId : {}",  id , memberId);
					}
					jsonObject.addProperty(id + "-memberNum", members.size());
					jsonObject.addProperty(id + "-memberIds", members.toString());
					jsonObject.addProperty("captainId", cIds);
					team.setIsWeedOut(1);
					team.notifyUpdate(false);
				}
			}
		}
		jsonObject.addProperty("status", 1);
		GuaJiScriptManager.sendResponse(httpExchange, jsonObject.toString());
	}
}
