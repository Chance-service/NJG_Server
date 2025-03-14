package com.guaji.game.module.activity.maidenEncounter;

import org.guaji.app.AppObj;
import org.guaji.net.protocol.Protocol;
import org.guaji.net.protocol.IProtocolHandler;

import com.guaji.game.protocol.Activity2.SyncMaidenEncounterExchangeRes;
import com.guaji.game.config.ActivityTimeCfg;
import com.guaji.game.entity.MaidenEncounterEntity;
import com.guaji.game.player.Player;
import com.guaji.game.util.ActivityUtil;
import com.guaji.game.util.BuilderUtil;
import com.guaji.game.protocol.Const;
import com.guaji.game.protocol.HP;
import com.guaji.game.protocol.Status;

/**
 * 同步少女的邂逅兑换数据
 */
public class MaidenEncounterExchangeInfoHandler implements IProtocolHandler {

	@Override
	public boolean onProtocol(AppObj appObj, Protocol protocol) {
		Player player = (Player) appObj;
		int activityId = Const.ActivityId.MAIDEN_ENCOUNTER_VALUE;
		ActivityTimeCfg timeCfg = ActivityUtil.getCurActivityTimeCfg(activityId);
		if (timeCfg == null) {
			// 活动已关闭
			player.sendError(HP.code.SYNC_MAIDEN_ENCOUNTER_EXCHANGE_C_VALUE, Status.error.ACTIVITY_CLOSE);
			return true;
		}
		MaidenEncounterEntity entity = player.getPlayerData().getMaidenEncounterEntity();
		if (entity == null) {
			player.sendError(HP.code.SYNC_MAIDEN_ENCOUNTER_EXCHANGE_C_VALUE, Status.error.DATA_NOT_FOUND_VALUE);
			return true;
		}
		SyncMaidenEncounterExchangeRes.Builder response = BuilderUtil.getMaidenEncounterExchangeBuilders(entity);
		player.sendProtocol(Protocol.valueOf(HP.code.SYNC_MAIDEN_ENCOUNTER_EXCHANGE_S_VALUE, response));
		return true;
	}
}
