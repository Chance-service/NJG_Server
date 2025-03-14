package com.guaji.game.gm;

import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.guaji.app.AppObj;
import org.guaji.obj.ObjManager;
import org.guaji.script.GuaJiScript;
import org.guaji.script.GuaJiScriptManager;
import org.guaji.xid.GuaJiXID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.guaji.game.GsApp;
import com.guaji.game.entity.EmailEntity;
import com.guaji.game.entity.EquipEntity;
import com.guaji.game.entity.FriendEntity;
import com.guaji.game.entity.ItemEntity;
import com.guaji.game.entity.MapEntity;
import com.guaji.game.entity.MapStatisticsEntity;
import com.guaji.game.entity.MsgEntity;
import com.guaji.game.entity.PlayerAllianceEntity;
import com.guaji.game.entity.PlayerEntity;
import com.guaji.game.entity.RoleEntity;
import com.guaji.game.entity.RoleRingEntity;
import com.guaji.game.entity.ShopEntity;
import com.guaji.game.entity.SkillEntity;
import com.guaji.game.entity.StateEntity;
import com.guaji.game.entity.TitleEntity;
import com.guaji.game.player.Player;
import com.guaji.game.player.PlayerData;
import com.guaji.game.util.GsConst;
import com.sun.net.httpserver.HttpExchange;

public class DBEntityLandHandler extends GuaJiScript{

	private static Logger logger = LoggerFactory.getLogger("Debug");
	
	@Override
	public void action(String params, HttpExchange httpExchange) {
		
		ObjManager<GuaJiXID, AppObj> objManager = GsApp.getInstance().getObjMan(GsConst.ObjType.PLAYER);
		List<AppObj> pList = new LinkedList<>();
		objManager.collectObjValue(pList, null);
		for(AppObj appObj : pList) {
			Player player = (Player) appObj;
			try{
				PlayerData playerData = player.getPlayerData();
				PlayerEntity playerEntity = playerData.getPlayerEntity();
				if(playerEntity != null && playerEntity.getEntityState().get()> 0) {
					playerEntity.notifyUpdate(false);
				};
				StateEntity stateEntity = playerData.getStateEntity();
				if(stateEntity != null && stateEntity.getEntityState().get()> 0) {
					stateEntity.notifyUpdate(false);
				};
				if(playerData.getEquipEntities() != null) {
					for(EquipEntity equipEntity : playerData.getEquipEntities()){
						if(equipEntity != null && equipEntity.getEntityState().get()> 0) {
							equipEntity.notifyUpdate(false);
						};
					}
				}
				
				for(EmailEntity emailEntity : playerData.getEmailEntities().values()){
					if(emailEntity != null && emailEntity.getEntityState().get()> 0) {
						emailEntity.notifyUpdate(false);
					};
				}
				
				FriendEntity friendEntity = playerData.getFriendEntity();
				if(friendEntity != null &&friendEntity.getEntityState().get()> 0) {
					friendEntity.notifyUpdate(false);
				};
				for(RoleEntity roleEntity : playerData.getRoleEntities()){
					if(roleEntity != null && roleEntity.getEntityState().get()> 0) {
						roleEntity.notifyUpdate(false);
					};
				};
			
				TitleEntity titleEntity = playerData.getTitleEntity();
				if(titleEntity != null && titleEntity.getEntityState().get()> 0) {
					titleEntity.notifyUpdate(false);
				};
				ShopEntity shopEntity = playerData.getShopEntity();
				if(shopEntity != null && shopEntity.getEntityState().get()> 0) {
					shopEntity.notifyUpdate(false);
				};
				for(RoleRingEntity roleRingEntity : playerData.getRingInfoEntities()){
					if(roleRingEntity != null && roleRingEntity.getEntityState().get()> 0) {
						roleRingEntity.notifyUpdate(false);
					};
				};
				
				for(SkillEntity skillEntity : playerData.getSkillEntities()){
					if(skillEntity != null && skillEntity.getEntityState().get()> 0) {
						skillEntity.notifyUpdate(false);
					};
				}
				
				for(Set<MsgEntity> msgEntities : playerData.getPlayerMsgs().values()){
					for(MsgEntity msgEntity : msgEntities) {
						if(msgEntity != null && msgEntity.getEntityState().get()> 0) {
							msgEntity.notifyUpdate(false);
						};
					}
				};
				
				for(ItemEntity itemEntity : playerData.getItemEntities()){
					if(itemEntity != null && itemEntity.getEntityState().get()> 0) {
						itemEntity.notifyUpdate(false);
					};
				}
				
				MapEntity mapEntity = playerData.getMapEntity();
				if(mapEntity != null && mapEntity.getEntityState().get()> 0) {
					mapEntity.notifyUpdate(false);
				};
				
				PlayerAllianceEntity playerAllianceEntity = playerData.getPlayerAllianceEntity();
				if(playerAllianceEntity != null && playerAllianceEntity.getEntityState().get()> 0) {
					playerAllianceEntity.notifyUpdate(false);
				};
				
				MapStatisticsEntity mapStatisticsEntity = playerData.getMapStatisticsEntity();
				if(mapStatisticsEntity != null && mapStatisticsEntity.getEntityState().get()> 0) {
					mapStatisticsEntity.notifyUpdate(false);
				};
				
//				MissionEntity missionEntity = playerData.getMissionEntity();
//				if(missionEntity != null && missionEntity.getEntityState().get()> 0) {
//					missionEntity.notifyUpdate(false);
//				};
			}catch(Exception e) {
				e.printStackTrace();
			}
			logger.info("dbland, playerId : " + player.getId());
		}
		GuaJiScriptManager.sendResponse(httpExchange, "{status : 1}");
	}
}
