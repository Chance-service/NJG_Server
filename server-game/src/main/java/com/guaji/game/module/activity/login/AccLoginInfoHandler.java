package com.guaji.game.module.activity.login;

import org.guaji.app.AppObj;
import org.guaji.net.protocol.Protocol;
import org.guaji.net.protocol.IProtocolHandler;

import com.guaji.game.protocol.Activity.HPAccLoginInfoRet;
import com.guaji.game.config.ActivityTimeCfg;
import com.guaji.game.player.Player;
import com.guaji.game.util.ActivityUtil;
import com.guaji.game.protocol.Const;
import com.guaji.game.protocol.HP;
import com.guaji.game.protocol.Status;

/**
 * 累计登录初始化协议
 */
public class AccLoginInfoHandler implements IProtocolHandler {

	@Override
	public boolean onProtocol(AppObj appObj, Protocol protocol) {

		Player player = (Player) appObj;
		// 检测活动是否开放
		int activityId = Const.ActivityId.ACCUMULATIVE_LOGIN_VALUE;
		ActivityTimeCfg timeConfig = ActivityUtil.getCurActivityTimeCfg(activityId);
		if (timeConfig == null) {
			// 活动已关闭
			player.sendError(protocol.getType(), Status.error.ACTIVITY_CLOSE);
			return true;
		}
		// 提取活动数据
		AccLoginStatus status = ActivityUtil.getActivityStatus(player.getPlayerData(), activityId, timeConfig.getStageId(), AccLoginStatus.class);
		// 构建返回数据包
		HPAccLoginInfoRet.Builder builder = HPAccLoginInfoRet.newBuilder();
		int lastLoginDays = status.getTotalLoginDays();
		int curLoginDays = status.refreshLoginDays();
		if (curLoginDays > lastLoginDays) {
			player.getPlayerData().updateActivity(activityId, timeConfig.getStageId());
		}
		builder.setLoginDays(status.getTotalLoginDays());
		builder.setLeftTime(timeConfig.calcActivitySurplusTime());
		builder.addAllGotAwardCfgId(status.getGotAwardCfgIds());
		player.sendProtocol(Protocol.valueOf(HP.code.ACC_LOGIN_INFO_S_VALUE, builder));
		return true;
	}
}
