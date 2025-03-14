package com.guaji.game.gm;

import java.util.Calendar;
import java.util.List;
import java.util.Map;

import org.guaji.db.DBManager;
import org.guaji.os.GuaJiTime;
import org.guaji.script.GuaJiScript;
import org.guaji.script.GuaJiScriptManager;

import com.guaji.game.ServerData;
import com.guaji.game.entity.PlayerEntity;
import com.guaji.game.player.Player;
import com.guaji.game.util.PlayerUtil;
import com.sun.net.httpserver.HttpExchange;

/**
 * 禁言处理
 */
public class SilentPlayerHandler extends GuaJiScript {
	@Override
	public void action(String params, HttpExchange httpExchange) {
		// 参数是PUID
		Map<String, String> paramsMap = GuaJiScriptManager.paramsToMap(params);
		if (paramsMap.containsKey("playerid")) {
			int playerId = Integer.valueOf(paramsMap.get("playerid"));

			int type = 1;
			if (paramsMap.containsKey("type")) {
				type = Integer.valueOf(paramsMap.get("type"));
			}

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
				// 禁言
				Calendar calendar = GuaJiTime.getCalendar();
				calendar.add(Calendar.YEAR, 1);
				playerEntity.setSilentTime(calendar.getTime());
				ServerData.getInstance().addSilentPhone(playerEntity.getPuid());
			} else if (type == 2) {
				// 解禁
				playerEntity.setSilentTime(null);
				ServerData.getInstance().removeSilentPhone(playerEntity.getPuid());
			}
			playerEntity.notifyUpdate(false);
			// 回复状态
			GuaJiScriptManager.sendResponse(httpExchange, "{\"status\":1}");
		} else {
			GuaJiScriptManager.sendResponse(httpExchange, "{\"status\":2,\"msg\":\"invalid params\"}");
		}
	}
}
