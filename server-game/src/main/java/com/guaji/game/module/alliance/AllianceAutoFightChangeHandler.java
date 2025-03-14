package com.guaji.game.module.alliance;

import org.guaji.app.AppObj;
import org.guaji.net.protocol.Protocol;
import org.guaji.net.protocol.IProtocolHandler;

import com.guaji.game.protocol.Alliance.HPAllianceEnterS;
import com.guaji.game.config.SysBasicCfg;
import com.guaji.game.entity.AllianceEntity;
import com.guaji.game.entity.PlayerAllianceEntity;
import com.guaji.game.log.BehaviorLogger;
import com.guaji.game.log.BehaviorLogger.Action;
import com.guaji.game.log.BehaviorLogger.Params;
import com.guaji.game.log.BehaviorLogger.Source;
import com.guaji.game.manager.AllianceManager;
import com.guaji.game.player.Player;
import com.guaji.game.protocol.Status;

public class AllianceAutoFightChangeHandler implements IProtocolHandler {

	@Override
	public boolean onProtocol(AppObj appObj, Protocol protocol) {
		Player player = (Player) appObj;
		PlayerAllianceEntity myPlayerAllianceEntity = player.getPlayerData().getPlayerAllianceEntity();
		int allianceId = myPlayerAllianceEntity.getAllianceId();
		AllianceEntity allianceEntity = AllianceManager.getInstance().getAlliance(allianceId);
		if (allianceEntity == null) {
			player.sendError(protocol.getType(), Status.error.ALLIANCE_NONEXISTENT);
			return true;
		}

		int state = myPlayerAllianceEntity.getAutoFight();

		if (state == 0) {
			if (player.getPlayerData().getPlayerEntity().getVipLevel() < SysBasicCfg.getInstance().getAutoBossJoinVipLimit()) {
				// Vip 等级不足
				player.sendError(protocol.getType(), Status.error.VIP_NOT_ENOUGH);
				return true;
			}

			if (player.getPlayerData().getPlayerEntity().getTotalGold() < SysBasicCfg.getInstance().getAllianceAutoFightCostGold()) {
				// 钻石不足
				player.sendError(protocol.getType(), Status.error.GOLD_NOT_ENOUGH_VALUE);
				return true;
			}

			state = 1;
		} else {
			state = 0;
		}

		myPlayerAllianceEntity.setAutoFight(state);
		myPlayerAllianceEntity.notifyUpdate(true);

		// add by weiyong
		BehaviorLogger.log4Service(player, Source.USER_OPERATION, Action.AUTO_BOSS_FIGHTING, Params.valueOf("state", state));
		BehaviorLogger.log4Platform(player, Action.AUTO_BOSS_FIGHTING, Params.valueOf("state", state));

		// 在线玩家快照刷新
		player.getPlayerData().refreshOnlinePlayerSnapshot();

		HPAllianceEnterS.Builder ret = HPAllianceEnterS.newBuilder();
		AllianceManager.getInstance().sendSelfData(ret, player, allianceEntity);
		return true;
	}

}
