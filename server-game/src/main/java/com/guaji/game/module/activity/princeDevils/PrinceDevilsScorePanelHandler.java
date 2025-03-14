package com.guaji.game.module.activity.princeDevils;

import java.util.Map;

import org.guaji.app.AppObj;
import org.guaji.config.ConfigManager;
import org.guaji.net.protocol.Protocol;
import org.guaji.net.protocol.IProtocolHandler;

import com.guaji.game.protocol.Activity2.HPPrinceDevilsScoreExchangeRes;
import com.guaji.game.config.ActivityTimeCfg;
import com.guaji.game.config.PrinceDevilsExchangeCfg;
import com.guaji.game.entity.PlayerPrinceDevilsEntity;
import com.guaji.game.player.Player;
import com.guaji.game.util.ActivityUtil;
import com.guaji.game.util.BuilderUtil;
import com.guaji.game.protocol.Const;
import com.guaji.game.protocol.HP;
import com.guaji.game.protocol.Status;

/**
 * 魔王宝藏活动，积分兑换面板
 */
public class PrinceDevilsScorePanelHandler implements IProtocolHandler {

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

		PlayerPrinceDevilsEntity princeDevilsEntity = player.getPlayerData().loadPrinceDevilsEntity();
		if (princeDevilsEntity.getStageId() != timeCfg.getStageId()) {
			princeDevilsEntity.resetInfo(timeCfg.getStageId());
			princeDevilsEntity.notifyUpdate();
		}

		Map<Object, PrinceDevilsExchangeCfg> cfgMap = ConfigManager.getInstance().getConfigMap(PrinceDevilsExchangeCfg.class);
		if (cfgMap == null || cfgMap.size() <= 0) {
			player.sendError(protocol.getType(), Status.error.CONFIG_NOT_FOUND_VALUE);
			return true;
		}

		HPPrinceDevilsScoreExchangeRes.Builder ret = HPPrinceDevilsScoreExchangeRes.newBuilder();
		BuilderUtil.princeDevilsScoreBuilder(princeDevilsEntity, ret, cfgMap);

		int closeTime = timeCfg.calcActivityCloseTime();

		ret.setPanelCloseTime(closeTime);
		player.sendProtocol(Protocol.valueOf(HP.code.PRINCE_DEVILS_SCORE_PANEL_S_VALUE, ret));
		return true;
	}
}
