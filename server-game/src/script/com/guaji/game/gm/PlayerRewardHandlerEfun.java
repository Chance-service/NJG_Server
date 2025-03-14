package com.guaji.game.gm;

import java.util.List;
import java.util.Map;

import org.guaji.db.DBManager;
import org.guaji.script.GuaJiScript;
import org.guaji.script.GuaJiScriptManager;

import com.guaji.game.entity.PlayerEntity;
import com.guaji.game.item.AwardItems;
import com.guaji.game.log.BehaviorLogger;
import com.guaji.game.log.BehaviorLogger.Action;
import com.guaji.game.log.BehaviorLogger.Params;
import com.guaji.game.log.BehaviorLogger.Source;
import com.guaji.game.manager.MailManager;
import com.guaji.game.protocol.Mail;
import com.sun.net.httpserver.HttpExchange;
/**
 * http://*:5132/sendRewardForEfun?params=puid:xxx;serverid:xxxx;reward:xxx;title:xxx;content:xxx&user=admin 
 */
public class PlayerRewardHandlerEfun extends GuaJiScript {
	
	@Override
	public void action(String params, HttpExchange httpExchange) {
		
		Map<String, String> paramsMap = GuaJiScriptManager.paramsToMap(params);
		if (paramsMap.containsKey("puid") 
				&& paramsMap.containsKey("serverid")
				&& paramsMap.containsKey("reward")) {
			String content = "";
			if (paramsMap.containsKey("content")) {
				content = paramsMap.get("content");
			}
			
			PlayerEntity playerEntity = null;
			
			String puid = paramsMap.get("puid");
			String serverid = paramsMap.get("serverid");
			
			List<PlayerEntity> playerEntities = DBManager.getInstance().query("from PlayerEntity where puid='" + puid + "' and serverId=" + serverid);
			if (playerEntities.size() >= 1) {
				playerEntity = playerEntities.get(0);
			}
			
			if(playerEntity == null) {
				GuaJiScriptManager.sendResponse(httpExchange, "{\"status\":2,\"msg\":\"invalid params\"}");
				return;
			}
			
			int playerid = playerEntity.getId(); 	// playerId;
			String reward = paramsMap.get("reward");
			
			// 错误屏蔽
			if (playerid == 0) {
				GuaJiScriptManager.sendResponse(httpExchange, "{\"status\":3,\"msg\":\"playerid is 0\"}");
				return;
			}

			// 发放奖励
			if (reward.length() > 0) {
				AwardItems awardItems = AwardItems.valueOf(reward);
				
				// 发放传书
				if (awardItems != null) {
					MailManager.createMail(playerid, Mail.MailType.Reward_VALUE, 0, content, awardItems);
				} else {
					MailManager.createMail(playerid, Mail.MailType.Normal_VALUE, 0, content, null);
				}
				// 日志记录
				BehaviorLogger.log4GM("", Source.GM_OPERATION, Action.GM_AWARD, 
						Params.valueOf("reward", reward), 
						Params.valueOf("message", content));

				GuaJiScriptManager.sendResponse(httpExchange, "{\"status\":1}");
			}
		} else {
			GuaJiScriptManager.sendResponse(httpExchange, "{\"status\":2,\"msg\":\"invalid params\"}");
		}
	}
}
