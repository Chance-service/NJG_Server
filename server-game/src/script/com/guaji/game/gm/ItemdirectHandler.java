package com.guaji.game.gm;

import java.util.List;
import java.util.Map;

import org.guaji.app.AppObj;
import org.guaji.db.DBManager;
import org.guaji.net.protocol.Protocol;
import org.guaji.obj.ObjBase;
import org.guaji.script.GuaJiScript;
import org.guaji.script.GuaJiScriptManager;
import org.guaji.xid.GuaJiXID;

import com.guaji.game.GsApp;
import com.guaji.game.entity.PlayerEntity;
import com.guaji.game.log.BehaviorLogger;
import com.guaji.game.log.BehaviorLogger.Action;
import com.guaji.game.log.BehaviorLogger.Params;
import com.guaji.game.log.BehaviorLogger.Source;
import com.guaji.game.player.Player;
import com.guaji.game.util.GsConst;
import com.guaji.game.protocol.HP;
import com.guaji.game.protocol.Reward.HPPlayerReward;
import com.guaji.game.protocol.Reward.RewardInfo;
import com.guaji.game.protocol.Reward.RewardItem;
import com.sun.net.httpserver.HttpExchange;
/**
 * 参数解析: params=puid:10;type:5;value:10;serverid:1
 */
public class ItemdirectHandler extends GuaJiScript {
	
	@Override
	public void action(String params, HttpExchange httpExchange) {
		
		Map<String, String> paramsMap = GuaJiScriptManager.paramsToMap(params);
		if (paramsMap.containsKey("puid") && params.contains("type") 
				&& params.contains("value") && paramsMap.containsKey("serverid")) {
			
			int type = Integer.valueOf(paramsMap.get("type"));
			if (type != 5) {
				GuaJiScriptManager.sendResponse(httpExchange, "{\"status\":2,\"msg\":\"type != 5\"}");
				return;
			}
			
			String puid = paramsMap.get("puid");
			String serverid = paramsMap.get("serverid");
			int value = Integer.valueOf(paramsMap.get("value"));
			
			List<PlayerEntity> pEntities = DBManager.getInstance().query("from PlayerEntity where puid='" + puid + "' and serverId=" + serverid);
			if (pEntities.size() < 1) {
				GuaJiScriptManager.sendResponse(httpExchange, "{\"status\":2,\"msg\":\"invalid\"}");
				return;
			}

			PlayerEntity playerEntity = pEntities.get(0);
			int playerId = playerEntity.getId();
			Player player = null;
			GuaJiXID xid = GuaJiXID.valueOf(GsConst.ObjType.PLAYER, playerId);
			ObjBase<GuaJiXID, AppObj> objBase = GsApp.getInstance().lockObject(xid);
			
			// 设置信息并落地数据量
			int itemId = 0;
			try {
				if (objBase != null && objBase.isObjValid()) {
					player = (Player) objBase.getImpl();
					playerEntity = player.getEntity();
				}
				
				if (type == 5){
					playerEntity.setReputationValue(playerEntity.getReputationValue() + value);
					itemId = 1011;
				}
				
				// 玩家在线则通知同步
				if (player != null) {
					player.getPlayerData().syncPlayerInfo();
					if(itemId != 0) {
						RewardItem.Builder rewardItemBuilder = RewardItem.newBuilder();
						rewardItemBuilder.setItemId(itemId);
						rewardItemBuilder.setItemCount(value);
						rewardItemBuilder.setItemType(10000);
						RewardInfo.Builder builder = RewardInfo.newBuilder();
						builder.addShowItems(rewardItemBuilder);
						HPPlayerReward.Builder playerRewardBuilder = HPPlayerReward.newBuilder();
						playerRewardBuilder.setRewards(builder);
						playerRewardBuilder.setFlag(1);
						player.sendProtocol(Protocol.valueOf(HP.code.PLAYER_AWARD_S_VALUE, playerRewardBuilder));
					}
				}
				playerEntity.notifyUpdate(false);
			} finally {
				if (objBase != null) {
					objBase.unlockObj();
				}
			}
			
			// 日志记录
			BehaviorLogger.log4GM(playerEntity.getPuid(), Source.GM_OPERATION, Action.GM_AWARD, Params.valueOf("itemdirect_reward", "10000_"+ itemId +"_"+value));
			// 日志记录
			BehaviorLogger.log4Service(playerEntity, Source.GM_OPERATION, Action.GM_AWARD, Params.valueOf("itemdirect_reward", "10000_"+ itemId +"_"+value));
			
			GuaJiScriptManager.sendResponse(httpExchange, "{\"status\":1}");
		} else {
			GuaJiScriptManager.sendResponse(httpExchange, "{\"status\":2,\"msg\":\"invalid params\"}");
		}
	}
}
