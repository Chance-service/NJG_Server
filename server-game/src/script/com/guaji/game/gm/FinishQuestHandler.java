package com.guaji.game.gm;

import java.util.Map;

import org.guaji.config.ConfigManager;
import org.guaji.db.DBManager;
import org.guaji.script.GuaJiScript;
import org.guaji.script.GuaJiScriptManager;

import com.guaji.game.config.QuestCfg;
import com.guaji.game.entity.QuestEntity;
import com.guaji.game.entity.QuestItem;
import com.guaji.game.player.Player;
import com.guaji.game.util.PlayerUtil;
import com.guaji.game.protocol.Const.QuestState;
import com.sun.net.httpserver.HttpExchange;

/**
 * curl 'localhost:5132/finishQuest?params=playerId:14998;questId:2052001;type:2;questCount:0&user=admin'
 * type = 1 更改次数
 * type = 2 完成任务
 */
public class FinishQuestHandler extends GuaJiScript {
	@Override
	public void action(String params, HttpExchange httpExchange) {
		Map<String, String> paramsMap = GuaJiScriptManager.paramsToMap(params);
		if (paramsMap.containsKey("playerId") && paramsMap.containsKey("questId") && paramsMap.containsKey("type") && paramsMap.containsKey("questCount")) {
			int playerId = Integer.valueOf(paramsMap.get("playerId"));
			int questId = Integer.valueOf(paramsMap.get("questId"));
			int type = Integer.valueOf(paramsMap.get("type"));
			int questCount = Integer.valueOf(paramsMap.get("questCount"));
			
			Player player = PlayerUtil.queryPlayer(playerId);
			QuestEntity entity = null;
			if (player != null) {
				entity = player.getPlayerData().getQuestEntity();
			} else {
				entity = DBManager.getInstance().fetch(QuestEntity.class, "from QuestEntity where playerId = ? and invalid = 0", playerId);
			}

			if (entity != null) {
				QuestItem questItem = entity.getQuestMap().get(questId);
				if(type == 1){
					questItem.setFinishedCount(questCount);
					QuestCfg cfg = ConfigManager.getInstance().getConfigMap(QuestCfg.class).get(questId);
					if(cfg != null){
						if(questItem.getFinishedCount() >= cfg.getNeedCount()){
							questItem.setState(QuestState.FINISHED_VALUE);
						}
					}
				} else {
					questItem.setState(QuestState.FINISHED_VALUE);
				}
				entity.reConvert();
				entity.notifyUpdate(false);
				GuaJiScriptManager.sendResponse(httpExchange, "questFinishedCount = " + questItem.getFinishedCount() + "  state = " + questItem.getState());
			} else {
				GuaJiScriptManager.sendResponse(httpExchange, "{\"status\":2,\"msg\":\"QuestEntity is null\"}");
			}
		} else {
			GuaJiScriptManager.sendResponse(httpExchange, "{\"status\":2,\"msg\":\"invalid params\"}");
		}
	}
}
