package com.guaji.game.module.alliance;

import org.guaji.app.AppObj;
import org.guaji.net.protocol.Protocol;
import org.guaji.net.protocol.IProtocolHandler;

import com.guaji.game.entity.AllianceEntity;
import com.guaji.game.entity.PlayerAllianceEntity;
import com.guaji.game.manager.AllianceManager;
import com.guaji.game.player.Player;
import com.guaji.game.protocol.Status;

/**
 * 公会商店列表
 */
public class AllianceShopHandler implements IProtocolHandler {
	@Override
	public boolean onProtocol(AppObj appObj, Protocol protocol) {
		Player player = (Player) appObj;
		// 玩家公会实体
		PlayerAllianceEntity playerAllianceEntity = player.getPlayerData().getPlayerAllianceEntity();
		int allianceId = playerAllianceEntity.getAllianceId(); 
		if(allianceId == 0){
			player.sendError(protocol.getType(), Status.error.ALLIANCE_NO_JOIN);
			return true;
		}
		// 公会实体
		AllianceEntity allianceEntity = AllianceManager.getInstance().getAlliance(allianceId);
		if(allianceEntity == null){
			player.sendError(protocol.getType(), Status.error.ALLIANCE_NONEXISTENT);
			return true;
		}
		
		// 重置玩家自己的公会数据;
		if(System.currentTimeMillis() > playerAllianceEntity.getShopTime()){
			playerAllianceEntity.clear();
			playerAllianceEntity.notifyUpdate(true);
		}
		// 获取商品列表;
//		HPAllianceShopS.Builder shopBuilder = AllianceManager.getInstance().getAllianceShopItemList(protocol.getType(), player, playerAllianceEntity, allianceEntity);
//		if (shopBuilder.getItemsCount() <= 0) {
//			return true;
//		}
//		player.sendProtocol(Protocol.valueOf(HP.code.ALLIANCE_SHOP_S, shopBuilder));
		return true;
	}

}
