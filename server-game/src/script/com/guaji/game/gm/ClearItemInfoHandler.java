package com.guaji.game.gm;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.guaji.app.AppObj;
import org.guaji.db.DBManager;
import org.guaji.obj.ObjBase;
import org.guaji.script.GuaJiScript;
import org.guaji.script.GuaJiScriptManager;
import org.guaji.xid.GuaJiXID;

import com.guaji.game.GsApp;
import com.guaji.game.entity.ItemEntity;
import com.guaji.game.item.ConsumeItems;
import com.guaji.game.item.ItemInfo;
import com.guaji.game.log.BehaviorLogger.Action;
import com.guaji.game.player.Player;
import com.guaji.game.util.GsConst;
import com.guaji.game.protocol.Const.changeType;
import com.sun.net.httpserver.HttpExchange;

/**
 * 清理背包 curl 'localhost:5132/clearItemInfo?params=playerid:5;itemId:3&user=hawk'
 */
public class ClearItemInfoHandler extends GuaJiScript {
	@Override
	public void action(String params, HttpExchange httpExchange) {

		Map<String, String> paramsMap = GuaJiScriptManager.paramsToMap(params);
		if (paramsMap.containsKey("playerid") && params.contains("itemId")) {
			int playerId = Integer.valueOf(paramsMap.get("playerid"));
			int itemId = Integer.valueOf(paramsMap.get("itemId"));

			Player player = null;
			GuaJiXID xid = GuaJiXID.valueOf(GsConst.ObjType.PLAYER, playerId);
			ObjBase<GuaJiXID, AppObj> objBase = GsApp.getInstance().lockObject(xid);
			long count = 0;
			try {
				if (objBase != null && objBase.isObjValid()) {
					player = (Player) objBase.getImpl();
				}

				if (player != null) {
					ConsumeItems consumeItems = ConsumeItems.valueOf();
					if (itemId != 0) {
						ItemEntity itemEntity = player.getPlayerData().getItemByItemId(itemId);
						if (itemEntity != null) {
							consumeItems.addChangeInfo(changeType.CHANGE_TOOLS, itemEntity.getId(), itemId, itemEntity.getItemCount());
							consumeItems.consumeTakeAffect(player, Action.NULL);
							count = itemEntity.getItemCount();
						}
					} else {
						List<ItemEntity> itemEntitys = player.getPlayerData().getItemEntities();
						List<ItemInfo> itemInfoList = new ArrayList<>();
						for (ItemEntity item : itemEntitys) {
							itemInfoList.add(ItemInfo.valueOf(30000, item.getItemId(), item.getItemCount()));
							count += item.getItemCount();
						}
						consumeItems.addConsumeInfo(player.getPlayerData(), itemInfoList);
						consumeItems.consumeTakeAffect(player, Action.NULL);
					}
				} else {
					if (itemId != 0) {
						String sql = "update item set invalid = 1 where itemId = " + itemId + " and playerId = " + playerId;
						count = DBManager.getInstance().executeUpdate(sql);
					} else {
						count = DBManager.getInstance().executeUpdate("update item set invalid = 1 where playerId = " + playerId);
					}
				}
			} finally {
				if (objBase != null) {
					objBase.unlockObj();
				}
			}
			GuaJiScriptManager.sendResponse(httpExchange, "{\"status\":1,\"count\":" + count + "}");
		} else {
			GuaJiScriptManager.sendResponse(httpExchange, "{\"status\":2,\"msg\":\"invalid params\"}");
		}
	}

}
