package com.guaji.game.module.alliance;

import org.guaji.app.AppObj;
import org.guaji.net.protocol.Protocol;
import org.guaji.net.protocol.IProtocolHandler;

import com.guaji.game.entity.AllianceEntity;
import com.guaji.game.manager.AllianceManager;
import com.guaji.game.player.Player;
import com.guaji.game.protocol.HP;
import com.guaji.game.protocol.Status;

/**
 * 获取公会成员信息
 */
public class AllianceMemberHandler implements IProtocolHandler {
	@Override
	public boolean onProtocol(AppObj appObj, Protocol protocol) {
		Player player = (Player) appObj;

		int allianceId = player.getPlayerData().getPlayerAllianceEntity().getAllianceId(); 
		if(allianceId == 0){
			player.sendError(protocol.getType(), Status.error.ALLIANCE_NO_JOIN);
			return true;
		}
		
		AllianceEntity allianceEntity = AllianceManager.getInstance().getAlliance(allianceId);
		if(allianceEntity == null){
			player.sendError(protocol.getType(), Status.error.ALLIANCE_NONEXISTENT);
			return true;
		}
		player.sendProtocol(Protocol.valueOf(HP.code.ALLIANCE_MEMBER_S, AllianceManager.getInstance().getAllianceMember(allianceEntity)));
		
		return true;
	}
}
