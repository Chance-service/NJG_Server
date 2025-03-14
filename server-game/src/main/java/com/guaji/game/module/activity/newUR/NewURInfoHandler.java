package com.guaji.game.module.activity.newUR;

import org.guaji.app.AppObj;
import org.guaji.net.protocol.Protocol;
import org.guaji.net.protocol.IProtocolHandler;

import com.guaji.game.protocol.Activity3.SyncNewURInfo;
import com.guaji.game.config.NewURTimesCfg;
import com.guaji.game.player.Player;
import com.guaji.game.util.ActivityUtil;
import com.guaji.game.protocol.HP;
import com.guaji.game.protocol.Status;

public class NewURInfoHandler implements IProtocolHandler {

	@Override
	public boolean onProtocol(AppObj appObj, Protocol protocol) {
		Player player = (Player) appObj;
		NewURStatus status = ActivityUtil.getNewURStatus(player.getPlayerData());
		if(null == status){
			player.sendError(protocol.getType(), Status.error.DATA_NOT_FOUND_VALUE);
			return true;
		}

		if (!NewURManager.canPlay(player.getLevel(), status)) {
			player.sendError(protocol.getType(), Status.error.ACTIVITY_CLOSE_VALUE);
			return true;
		}

		NewURTimesCfg timesCfg = NewURTimesCfg.getTimesCfgByVipLevel(0);
		if (timesCfg == null) {
			player.sendError(protocol.getType(), Status.error.CONFIG_NOT_FOUND);
			return true;
		}

		SyncNewURInfo.Builder builder = NewURManager.generateInfo(player);
		if (null == builder) {
			player.sendError(protocol.getType(), Status.error.DATA_NOT_FOUND_VALUE);
			return true;
		}
		player.sendProtocol(Protocol.valueOf(HP.code.NEW_UR_INFO_S, builder));
		return true;
	}

}
