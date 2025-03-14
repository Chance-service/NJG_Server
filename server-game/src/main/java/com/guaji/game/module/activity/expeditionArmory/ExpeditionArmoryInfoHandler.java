package com.guaji.game.module.activity.expeditionArmory;

import java.util.List;

import org.guaji.app.AppObj;
import org.guaji.net.protocol.Protocol;
import org.guaji.net.protocol.IProtocolHandler;

import com.guaji.game.protocol.Activity.ExpeditionArmoryStage;
import com.guaji.game.protocol.Activity.HPExpeditionArmoryInfoRet;
import com.guaji.game.config.ActivityTimeCfg;
import com.guaji.game.entity.ExpeditionArmoryEntity;
import com.guaji.game.manager.ExpeditionArmoryManager;
import com.guaji.game.player.Player;
import com.guaji.game.util.ActivityUtil;
import com.guaji.game.protocol.Const;
import com.guaji.game.protocol.HP;
import com.guaji.game.protocol.Status;

public class ExpeditionArmoryInfoHandler implements IProtocolHandler {

	@Override
	public boolean onProtocol(AppObj appObj, Protocol protocol) {
		Player player = (Player) appObj;
		// 检测活动是否开放
		int activityId = Const.ActivityId.EXPEDITION_ARMORY_VALUE;
		ActivityTimeCfg timeCfg = ActivityUtil.getCurActivityTimeCfg(activityId);
		if (timeCfg == null) {
			// 活动已关闭
			player.sendError(protocol.getType(), Status.error.ACTIVITY_CLOSE);
			return true;
		}

		ExpeditionArmoryManager activityMan = ExpeditionArmoryManager.getInstance();
		ExpeditionArmoryEntity expeditionArmoryEntity = activityMan.getCurrentActiveExpeditionArmory();
		if (expeditionArmoryEntity == null) {
			player.sendError(protocol.getType(), Status.error.ACTIVITY_CLOSE);
			return true;
		}

		HPExpeditionArmoryInfoRet.Builder ret = HPExpeditionArmoryInfoRet.newBuilder();
		ret.setCurStage(expeditionArmoryEntity.getCurDonateStage());
		ret.setLeftTime(timeCfg.calcActivitySurplusTime());
		// 活动阶段（服务器 + 个人）信息
		ExpeditionArmoryStatus expeditionArmoryStatus = ActivityUtil.getActivityStatus(player.getPlayerData(), activityId, timeCfg.getStageId(), ExpeditionArmoryStatus.class);
		activityMan.registerPersonalStatus(player, expeditionArmoryStatus);

		List<ExpeditionArmoryStage.Builder> stageBuilders = expeditionArmoryStatus.getAllStageStatusBuilder();
		for (ExpeditionArmoryStage.Builder stageBuilder : stageBuilders) {
			ret.addExpeditionArmoryStage(stageBuilder);
		}
		player.sendProtocol(Protocol.valueOf(HP.code.EXPEDITION_ARMORY_INFO_S_VALUE, ret));
		return true;
	}

}
