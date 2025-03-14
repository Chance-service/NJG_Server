package com.guaji.game.module.alliance;

import org.guaji.app.AppObj;
import org.guaji.net.protocol.Protocol;
import org.guaji.net.protocol.IProtocolHandler;

import com.guaji.game.protocol.Alliance.HPAllianceNoticeC;
import com.guaji.game.entity.AllianceEntity;
import com.guaji.game.manager.AllianceManager;
import com.guaji.game.player.Player;
import com.guaji.game.protocol.HP;
import com.guaji.game.protocol.Status;

/**
 * 设置公告内容
 */
public class AllianceNoticeHandler implements IProtocolHandler {
	@Override
	public boolean onProtocol(AppObj appObj, Protocol protocol) {
		Player player = (Player) appObj;
		HPAllianceNoticeC par = protocol.parseProtocol(HPAllianceNoticeC.getDefaultInstance());
		if(par.getNotice().length() <= 0){
			player.sendError(protocol.getType(), Status.error.ALLIANCE_NOTICE_ERROR);
			return true;
		}
		if(!checkName(par.getNotice())){
			player.sendError(protocol.getType(), Status.error.ALLIANCE_NOTICE_ERROR);
			return true;
		}
		int allianceId = player.getPlayerData().getPlayerAllianceEntity().getAllianceId(); 
		AllianceEntity allianceEntity = AllianceManager.getInstance().getAlliance(allianceId);
		if(allianceEntity == null){
			player.sendError(protocol.getType(), Status.error.ALLIANCE_NONEXISTENT);
			return true;
		}
		if(player.getPlayerData().getId() != allianceEntity.getPlayerId()){
			player.sendError(protocol.getType(), Status.error.ALLIANCE_NO_MAIN);
			return true;
		}
		
		allianceEntity.setNotice(par.getNotice());
		allianceEntity.notifyUpdate(true);
		
		player.sendProtocol(Protocol.valueOf(HP.code.ALLIANCE_CREATE_S_VALUE, AllianceManager.getInstance().getAllianceInfo(allianceEntity, player.getId(),player.getGold())));
		return true;
	}
	
	public boolean checkName(String name) {  
		return name.length() <= 200;     
      }  
}
