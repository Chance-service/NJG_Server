package com.guaji.game.module.alliance;

import org.guaji.app.AppObj;
import org.guaji.log.Log;
import org.guaji.net.protocol.Protocol;
import org.guaji.net.protocol.IProtocolHandler;

import com.guaji.game.protocol.Alliance.HPAllianceJoinSetC;
import com.guaji.game.entity.AllianceEntity;
import com.guaji.game.manager.AllianceManager;
import com.guaji.game.player.Player;
import com.guaji.game.protocol.HP;
import com.guaji.game.protocol.Status;

/**
 * 加入公会条件设置
 */
public class AllianceJoinSetHandler implements IProtocolHandler {
	private final int MAXBATTION = 9999999;
	@Override
	public boolean onProtocol(AppObj appObj, Protocol protocol) {
		Player player = (Player) appObj;
		HPAllianceJoinSetC par = protocol.parseProtocol(HPAllianceJoinSetC.getDefaultInstance());
		if(par.getBattlePoint() > MAXBATTION){
			player.sendError(protocol.getType(), Status.error.PARAMS_INVALID);
			return true;
		}
		int allianceId = player.getPlayerData().getPlayerAllianceEntity().getAllianceId(); 
		AllianceEntity allianceEntity = AllianceManager.getInstance().getAlliance(allianceId);
		
		if(allianceEntity == null){
			player.sendError(protocol.getType(), Status.error.ALLIANCE_NONEXISTENT);
			return true;
		}
		
		if(player.getId() != allianceEntity.getPlayerId()){
			player.sendError(protocol.getType(), Status.error.ALLIANCE_NO_MAIN);
			return true;
		}
		
		allianceEntity.setJoinLimit(par.getBattlePoint());
		
		if (allianceEntity.getPlayerId() != player.getId()) {//判断会长
			//非会长不允许操作
			player.sendError(protocol.getType(), Status.error.ALLIANCE_NONEXISTENT);
			return true;
		}
		int type = par.getCheckButton();
		Log.logPrintln("client check Button :" + type);
		//设置勾选
		allianceEntity.setHasCheckLeaderMail(type);
		allianceEntity.notifyUpdate(true);
		player.sendProtocol(Protocol.valueOf(HP.code.ALLIANCE_CREATE_S_VALUE, AllianceManager.getInstance().getAllianceInfo(allianceEntity, player.getId(),player.getGold())));
		return true;
	}
}
