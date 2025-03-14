package com.guaji.game.module.activity.maidenEncounter;

import org.guaji.app.AppObj;
import org.guaji.net.protocol.Protocol;
import org.guaji.net.protocol.IProtocolHandler;

import com.guaji.game.protocol.Activity2.SyncMaidenEncounterReq;
import com.guaji.game.config.ActivityTimeCfg;
import com.guaji.game.entity.MaidenEncounterEntity;
import com.guaji.game.player.Player;
import com.guaji.game.util.ActivityUtil;
import com.guaji.game.protocol.Const;
import com.guaji.game.protocol.HP;
import com.guaji.game.protocol.Status;

/**
 * 少女的邂逅互动逻辑
 */
public class MaidenEncounterHandler implements IProtocolHandler {

	private static final int REQUEST_TYPE_SYNC = 0;// 同步
	private static final int REQUEST_TYPE_INTERACT = 1;// 互动
	private static final int REQUEST_TYPE_REFRESH = 2;// 刷新
	private static final int REQUEST_TYPE_FULL = 3;// 加满

	@Override
	public boolean onProtocol(AppObj appObj, Protocol protocol) {
		Player player = (Player) appObj;
		int activityId = Const.ActivityId.MAIDEN_ENCOUNTER_VALUE;
		ActivityTimeCfg timeCfg = ActivityUtil.getCurActivityTimeCfg(activityId);
		if (timeCfg == null) {
			// 活动已关闭
			player.sendError(HP.code.SYNC_MAIDEN_ENCOUNTER_C_VALUE, Status.error.ACTIVITY_CLOSE);
			return true;
		}
		// 提取玩家数据
		MaidenEncounterEntity entity = player.getPlayerData().getMaidenEncounterEntity();
		if (entity == null) {
			entity = player.getPlayerData().loadMaidenEncounterEntity();
			if (null == entity) {
				player.sendError(HP.code.SYNC_MAIDEN_ENCOUNTER_C_VALUE, Status.error.DATA_NOT_FOUND_VALUE);
				return true;
			}
		}
		SyncMaidenEncounterReq request = protocol.parseProtocol(SyncMaidenEncounterReq.getDefaultInstance());
		int type = request.getType();
		switch (type) {
		case REQUEST_TYPE_SYNC:
			MaidenEncounterManager.sync(player, entity);
			break;
		case REQUEST_TYPE_INTERACT:
			if(!timeCfg.isEnd()){
				MaidenEncounterManager.interact(player, entity);
			}else{
				player.sendError(HP.code.SYNC_MAIDEN_ENCOUNTER_C_VALUE, Status.error.ACTIVITY_CLOSE);
			}
			break;
		case REQUEST_TYPE_REFRESH:
			if(!timeCfg.isEnd()){
				MaidenEncounterManager.refresh(player, entity);
			}else{
				player.sendError(HP.code.SYNC_MAIDEN_ENCOUNTER_C_VALUE, Status.error.ACTIVITY_CLOSE);
			}
			break;
		case REQUEST_TYPE_FULL:
			if(!timeCfg.isEnd()){
				MaidenEncounterManager.full(player, entity);
			}else{
				player.sendError(HP.code.SYNC_MAIDEN_ENCOUNTER_C_VALUE, Status.error.ACTIVITY_CLOSE);
			}
			break;
		default:
			player.sendError(HP.code.SYNC_MAIDEN_ENCOUNTER_C_VALUE, Status.error.ACTIVITY_CLOSE);
			break;
		}
		return true;
	}

}
