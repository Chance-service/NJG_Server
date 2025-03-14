package com.guaji.game.module.alliance;

import java.util.HashMap;

import org.guaji.app.AppObj;
import org.guaji.config.ConfigManager;
import org.guaji.net.protocol.Protocol;
import org.guaji.net.protocol.IProtocolHandler;

import com.guaji.game.protocol.Alliance.HPAllianceEnterS;
import com.guaji.game.protocol.Alliance.HPAllianceShopBuyC;
import com.guaji.game.protocol.Const.changeType;
import com.guaji.game.config.AllianceShopCfg;
import com.guaji.game.entity.AllianceEntity;
import com.guaji.game.entity.PlayerAllianceEntity;
import com.guaji.game.item.AwardItems;
import com.guaji.game.item.ConsumeItems;
import com.guaji.game.log.BehaviorLogger;
import com.guaji.game.log.BehaviorLogger.Action;
import com.guaji.game.log.BehaviorLogger.Params;
import com.guaji.game.manager.AllianceManager;
import com.guaji.game.player.Player;
import com.guaji.game.util.GsConst;
import com.guaji.game.protocol.Status;

/**
 * 购买
 */
public class AllianceShopBuyHandler implements IProtocolHandler {
	@Override
	public boolean onProtocol(AppObj appObj, Protocol protocol) {
		Player player = (Player) appObj;
		
		HPAllianceShopBuyC par = protocol.parseProtocol(HPAllianceShopBuyC.getDefaultInstance());
		if(par.getId() < 0 || par.getType() < 0){
			player.sendError(protocol.getType(), Status.error.PARAMS_INVALID);
			return true;
		}

		PlayerAllianceEntity playerAllianceEntity = player.getPlayerData().getPlayerAllianceEntity();
		int allianceId = playerAllianceEntity.getAllianceId(); 
		if(allianceId == 0){
			player.sendError(protocol.getType(), Status.error.ALLIANCE_NO_JOIN);
			return true;
		}
		
		AllianceEntity allianceEntity = AllianceManager.getInstance().getAlliance(allianceId);
		if(allianceEntity == null){
			player.sendError(protocol.getType(), Status.error.ALLIANCE_NONEXISTENT);
			return true;
		}
		
		// 此时玩家肯定已经有自己的商品列表了;
		int myLuckScore = AllianceManager.getInstance().getConfigScore(playerAllianceEntity.getLuckyScore());
		AllianceShopCfg allianceShopCfg = ConfigManager.getInstance().getConfigByKey(AllianceShopCfg.class, myLuckScore);
		if(allianceShopCfg == null){
			player.sendError(protocol.getType(), Status.error.DATA_NOT_FOUND);
			return true;
		}
		
		//获取原数据
		HashMap<String,Integer> dataMap = getMapData(allianceShopCfg, par.getType(),par.getId());
		if(dataMap == null){
			player.sendError(protocol.getType(), Status.error.DATA_NOT_FOUND);
			return true;
		}
		String key = par.getType()+"_"+par.getId();
		Integer v = playerAllianceEntity.getShopMap().get(key);
		if(v == null)
			v = 0;
		if(v >= dataMap.get(GsConst.Alliance.ALLIANCE_NUMBER)){
			player.sendError(protocol.getType(), Status.error.ALLIANCE_SHOP_BUY_ERROR);
			return true;
		}
		
		Integer c = dataMap.get(GsConst.Alliance.ALLIANCE_CONTRIBUTION);
		if(playerAllianceEntity.getContribution() < c){
			player.sendError(protocol.getType(), Status.error.ALLIANCE_NOT_CONTRIBUTION);
			return true;
		}
		
		ConsumeItems.valueOf(changeType.CHANGE_CONTRIBUTION, c).consumeTakeAffect(player, Action.ALLIANCE_CONSUME_CONTRIBUTION);
		playerAllianceEntity.getShopMap().put(key, v+1);
		playerAllianceEntity.notifyUpdate(true);
		
		/**加物品 jht*/
		AwardItems awardItems = new AwardItems();
		/**
		 * 2014-12-29修改
		 * dataMap.get(GsConst.Alliance.ALLIANCE_NUMBER) 改为 1
		 */
		awardItems.addItem(dataMap.get(GsConst.Alliance.ALLIANCE_TYPE), dataMap.get(GsConst.Alliance.ALLIANCE_ID), 1);
		awardItems.rewardTakeAffectAndPush(player, Action.ALLIANCE_CONSUME_CONTRIBUTION,1);
		
//		player.sendProtocol(Protocol.valueOf(HP.code.ALLIANCE_SHOP_S, AllianceManager.getInstance().getAllianceShopItemList(protocol.getType(), player, playerAllianceEntity, allianceEntity)));
		AllianceManager.getInstance().sendSelfData(HPAllianceEnterS.newBuilder(), player, allianceEntity);
		
		BehaviorLogger.log4Platform(player, Action.ALLIANCE_CONSUME_CONTRIBUTION, 
				Params.valueOf("costContribution", c),
				Params.valueOf("awardItems", awardItems.toString()));
		return true;
	}

	
	public HashMap<String, Integer> getMapData(AllianceShopCfg allianceShopCfg,int type,int id){
//		for (HashMap<String, Integer> m : allianceShopCfg.getMap()) {
//			if(m.get(GsConst.Alliance.ALLIANCE_TYPE) == type && m.get(GsConst.Alliance.ALLIANCE_ID) == id)
//				return m;
//		}
		return null;
	}
}
