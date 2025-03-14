package com.guaji.game.gm;

import java.util.Calendar;
import java.util.List;
import java.util.Map;

import org.guaji.db.DBManager;
import org.guaji.os.GuaJiTime;
import org.guaji.script.GuaJiScript;
import org.guaji.script.GuaJiScriptManager;

import com.guaji.game.entity.PlayerEntity;
import com.guaji.game.manager.ArenaManager;
import com.guaji.game.player.Player;
import com.guaji.game.util.PlayerUtil;
import com.guaji.game.protocol.Const;
import com.sun.net.httpserver.HttpExchange;

/**
 * 玩家封号处理
 */
public class ForbidenPlayerHandler extends GuaJiScript {
	@Override
	public void action(String params, HttpExchange httpExchange) {
		// 参数解析: params=playerid:0;type:0
		Map<String, String> paramsMap = GuaJiScriptManager.paramsToMap(params);
		if (paramsMap.containsKey("playerid")) {
			int type = 1;
			if (paramsMap.containsKey("type")) {
				type = Integer.valueOf(paramsMap.get("type"));
			}
			String playerid_s = paramsMap.get("playerid");
			String values[] = playerid_s.split(",");
			for (int i = 0; i < values.length; i++) {
				String value = values[i];
				int playerId = Integer.valueOf(value);
				//int playerId = Integer.valueOf(paramsMap.get("playerid"));
	
				PlayerEntity playerEntity = null;
				Player player = PlayerUtil.queryPlayer(playerId);
				if (player == null) {
					List<PlayerEntity> playerEntities = DBManager.getInstance().query("from PlayerEntity where id = ?", playerId);
					if (playerEntities.size() > 0) {
						playerEntity = (PlayerEntity) playerEntities.get(0);
					} else {
						GuaJiScriptManager.sendResponse(httpExchange, "{\"status\":2,\"msg\":\"player not exist\"}");
					}
				} else {
					playerEntity = player.getPlayerData().getPlayerEntity();
				}
	
				if (type == 1) {
					Calendar calendar = GuaJiTime.getCalendar();
					calendar.add(Calendar.YEAR, 1);
					playerEntity.setForbidenTime(calendar.getTime());
					
					// 踢出玩家
					if (player != null) {
						player.kickout(Const.kickReason.LOGIN_FORBIDEN_VALUE);
					}
					// 从竞技场排行榜删除
					ArenaManager.getInstance().removeArenaRank(playerId);
				} else if (type == 2) {
					// 解禁
					playerEntity.setForbidenTime(null);
				}
				playerEntity.notifyUpdate(false);
			}
			// 回复状态
			GuaJiScriptManager.sendResponse(httpExchange, "{\"status\":1}");
		} else {
			GuaJiScriptManager.sendResponse(httpExchange, "{\"status\":2,\"msg\":\"invalid params\"}");
		}
	}
}
