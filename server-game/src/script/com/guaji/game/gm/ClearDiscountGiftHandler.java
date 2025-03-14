package com.guaji.game.gm;

import java.util.Map;

import org.guaji.config.ConfigManager;
import org.guaji.db.DBManager;
import org.guaji.script.GuaJiScript;
import org.guaji.script.GuaJiScriptManager;

import com.guaji.game.config.ActivityTimeCfg;
import com.guaji.game.config.DiscountGiftCfg;
import com.guaji.game.entity.PlayerEntity;
import com.guaji.game.module.activity.discountGift.DiscountGiftData;
import com.guaji.game.module.activity.discountGift.OneDiscountGiftData;
import com.guaji.game.player.Player;
import com.guaji.game.player.PlayerData;
import com.guaji.game.util.ActivityUtil;
import com.guaji.game.util.PlayerUtil;
import com.guaji.game.protocol.Const;
import com.sun.net.httpserver.HttpExchange;

/**
 * 修复折扣礼包
 * @author zdz
 *
 *  curl 'localhost:5132/clearDiscountGift?params=playerid:12421;goodsId:44&user=hawk'
 */
public class ClearDiscountGiftHandler extends GuaJiScript {

	@Override
	public void action(String params, HttpExchange httpExchange) {
		Map<String, String> paramsMap = GuaJiScriptManager.paramsToMap(params);
		if (paramsMap.containsKey("playerid") && paramsMap.containsKey("goodsId")) {
			int playerId = Integer.valueOf(paramsMap.get("playerid"));
			int goodsId = Integer.valueOf(paramsMap.get("goodsId"));
			Player player = PlayerUtil.queryPlayer(playerId);
			PlayerEntity playerEntity = null;
			PlayerData playerData = null;
			if (player != null) {
				playerData = player.getPlayerData();
				playerEntity = player.getPlayerData().getPlayerEntity();
			} else {
				playerEntity = DBManager.getInstance().fetch(PlayerEntity.class, "from PlayerEntity where id = ?", playerId);
				playerData = new PlayerData(null);
				playerData.setPlayerEntity(playerEntity);
				playerData.loadActivity();
			}

			DiscountGiftData data = ActivityUtil.getDiscountGiftData(playerData);
			ActivityTimeCfg activityTimeCfg = ActivityUtil.getCurActivityTimeCfg(Const.ActivityId.DISCOUNT_GIFT_VALUE);
			if (activityTimeCfg != null) {
				DiscountGiftCfg cfg = ConfigManager.getInstance().getConfigByKey(DiscountGiftCfg.class, goodsId);
				if (cfg != null) {
					OneDiscountGiftData oneData = data.getOneDiscountGiftData(goodsId);
					if (oneData != null) {
						oneData.setBuyTimes(0);
						oneData.setStatus(1);
					}
				}
			}
			playerData.updateActivity(Const.ActivityId.DISCOUNT_GIFT_VALUE, activityTimeCfg.getStageId(), true);
			// 回复状态
			GuaJiScriptManager.sendResponse(httpExchange, "{\"status\":1}");
		} else {
			GuaJiScriptManager.sendResponse(httpExchange, "{\"status\":2,\"msg\":\"invalid params\"}");
		}
	}
}
