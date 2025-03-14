package com.guaji.game.module.activity.princeDevils;

import org.guaji.app.AppObj;
import org.guaji.net.protocol.Protocol;
import org.guaji.net.protocol.IProtocolHandler;

import com.guaji.game.protocol.Activity2.HPPrinceDevilsPanelInfoRes;
import com.guaji.game.config.ActivityTimeCfg;
import com.guaji.game.entity.PlayerPrinceDevilsEntity;
import com.guaji.game.player.Player;
import com.guaji.game.util.ActivityUtil;
import com.guaji.game.util.BuilderUtil;
import com.guaji.game.protocol.Const;
import com.guaji.game.protocol.HP;
import com.guaji.game.protocol.Status;

/**
 * 魔王宝藏活动请求面板信息
 */
public class PrinceDevilsPanelInfoHandler implements IProtocolHandler {

	@Override
	public boolean onProtocol(AppObj appObj, Protocol protocol) {

		Player player = (Player) appObj;
		// 检测活动是否开放
		int activityId = Const.ActivityId.PRINCE_DEVILS_VALUE;
		ActivityTimeCfg timeCfg = ActivityUtil.getCurActivityTimeCfg(activityId);
		if (timeCfg == null) {
			// 活动已关闭
			player.sendError(protocol.getType(), Status.error.ACTIVITY_CLOSE);
			return true;
		}

		PlayerPrinceDevilsEntity entity = player.getPlayerData().loadPrinceDevilsEntity();
		if (entity.getStageId() != timeCfg.getStageId()) {
			entity.resetInfo(timeCfg.getStageId());
			entity.notifyUpdate();
		}

		HPPrinceDevilsPanelInfoRes.Builder ret = BuilderUtil.princeDevilsInfoBuilder(entity, false, timeCfg, null);
		player.sendProtocol(Protocol.valueOf(HP.code.PRINCE_DEVILS_PANEL_S_VALUE, ret));
		return true;
	}
}
