package com.guaji.game.module.alliance;

import org.guaji.app.AppObj;
import org.guaji.net.protocol.Protocol;
import org.guaji.net.protocol.IProtocolHandler;
import org.guaji.os.GuaJiTime;

import com.guaji.game.protocol.Alliance.HPAllianceEnterS;
import com.guaji.game.config.SysBasicCfg;
import com.guaji.game.entity.AllianceEntity;
import com.guaji.game.entity.PlayerAllianceEntity;
import com.guaji.game.item.AwardItems;
import com.guaji.game.log.BehaviorLogger;
import com.guaji.game.log.BehaviorLogger.Action;
import com.guaji.game.log.BehaviorLogger.Params;
import com.guaji.game.log.BehaviorLogger.Source;
import com.guaji.game.manager.AllianceManager;
import com.guaji.game.player.Player;
import com.guaji.game.util.AllianceUtil;
import com.guaji.game.protocol.HP;
import com.guaji.game.protocol.Status;

/**
 * 公会报道（签到）
 * 
 * @author zhenghuangfei
 */
public class AllianceReportHandler implements IProtocolHandler {
	@Override
	public boolean onProtocol(AppObj appObj, Protocol protocol) {
		Player player = (Player) appObj;
		int allianceId = player.getPlayerData().getPlayerAllianceEntity().getAllianceId();
		
		AllianceEntity allianceEntity = AllianceManager.getInstance().getAlliance(allianceId);
		if(allianceEntity == null){
			player.sendError(protocol.getType(), Status.error.ALLIANCE_NONEXISTENT);
			return true;
		}
		if(allianceId == 0){
			player.sendError(protocol.getType(), Status.error.ALLIANCE_NO_JOIN_VALUE);
			return true;
		}
		
		if(player.getPlayerData().getPlayerAllianceEntity().getReportTime() > System.currentTimeMillis()){
			player.sendError(protocol.getType(), Status.error.ALLIANCE_REPORT_ERROR);
			return true;
		}
		
		PlayerAllianceEntity playerAllianceEntity = player.getPlayerData().getPlayerAllianceEntity();
		playerAllianceEntity.setReportTime(GuaJiTime.getNextAM0Date());
		playerAllianceEntity.notifyUpdate(true);
		
		allianceEntity.setExp(allianceEntity.getExp() + SysBasicCfg.getInstance().getAllianceReprotExp());
		allianceEntity.notifyUpdate(true);
		
		if(AllianceManager.getInstance().checkAllianceLevelUp(allianceEntity)){
			player.sendProtocol(Protocol.valueOf(HP.code.ALLIANCE_CREATE_S_VALUE, AllianceManager.getInstance().getAllianceInfo(allianceEntity, player.getId(),player.getGold())));
			return true;
		}
		
		AwardItems awardItems = new AwardItems();
		awardItems.addAllianceExp(SysBasicCfg.getInstance().getAllianceReprotExp());
		awardItems.addCoin(AllianceUtil.calcSignInCoin(player.getLevel(), allianceEntity.getLevel()));
		awardItems.addContribution(SysBasicCfg.getInstance().getAllianceReprotExp());
		awardItems.rewardTakeAffectAndPush(player, Action.ALLIANCE_REPORT_OPER,1);

		AllianceManager.getInstance().sendSelfData(HPAllianceEnterS.newBuilder(), player, AllianceManager.getInstance().getAlliance(allianceId));
		player.sendProtocol(Protocol.valueOf(HP.code.ALLIANCE_CREATE_S_VALUE, AllianceManager.getInstance().getAllianceInfo(allianceEntity, player.getId(),player.getGold())));
		
		// 结算记录日志
		BehaviorLogger.log4Service(player.getId(), Source.USER_OPERATION, Action.ALLIANCE_REPORT_OPER, Params.valueOf("awards", awardItems.toString()));
		BehaviorLogger.log4Platform(player, Action.ALLIANCE_REPORT_OPER, Params.valueOf("awards", awardItems));
		return true;
	}
	

}
