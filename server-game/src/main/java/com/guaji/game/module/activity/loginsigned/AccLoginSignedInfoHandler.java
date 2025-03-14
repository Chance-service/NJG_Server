package com.guaji.game.module.activity.loginsigned;

import java.util.Calendar;

import org.guaji.app.AppObj;
import org.guaji.net.protocol.IProtocolHandler;
import org.guaji.net.protocol.Protocol;
import org.guaji.os.GuaJiTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.guaji.game.config.ActivityTimeCfg;
import com.guaji.game.module.activity.login.AccLoginStatus;
import com.guaji.game.player.Player;
import com.guaji.game.protocol.Const;
import com.guaji.game.protocol.HP;
import com.guaji.game.protocol.Status;
import com.guaji.game.protocol.Activity4.LoginSignedRep;
import com.guaji.game.util.ActivityUtil;

/**
 * 累计登录签到协议
 */
public class AccLoginSignedInfoHandler implements IProtocolHandler {
	private static Logger logger = LoggerFactory.getLogger("AccLoginSignedInfoHandler");

	@Override
	public boolean onProtocol(AppObj appObj, Protocol protocol) {
		// TODO Auto-generated method stub

		Player player = (Player) appObj;
		// 检测活动是否开放
		int activityId = Const.ActivityId.ACCUMULATIVE_LOGIN_SIGNED_VALUE;
		ActivityTimeCfg timeConfig = ActivityUtil.getCurActivityTimeCfg(activityId);
		if (timeConfig == null || player == null) {
			// 活动已关闭
			player.sendError(protocol.getType(), Status.error.ACTIVITY_CLOSE);
			return true;
		}
		int nowMonth=GuaJiTime.getCalendar().get(Calendar.MONTH)+1;
		
		// 提取活动数据
		AccLoginSignedStatus status = ActivityUtil.getAccLoginSignedStatus(player.getPlayerData());
		if (status == null) {
			logger.error(String.format("Player=%d have not ActivietyId=%d AccLoginSignedStatus", player.getId(),
					activityId));
			return false;
		}

		//自然月已过度 重置数据
		if (status.getCurMonth() != GuaJiTime.getCalendar().get(Calendar.MONTH)) {
			status.reSet();
			player.getPlayerData().updateActivity(activityId, timeConfig.getStageId());
		}
		
		LoginSignedRep.Builder builder = LoginSignedRep.newBuilder();
		builder.setMonthOfDay(GuaJiTime.getMonthDay());
		builder.addAllGotAwardChest(status.getGotAwardChest());
		builder.setCurMonth(nowMonth);
		builder.addAllSupplSignedDays(status.getSupplSignedDays());
		builder.addAllSignedDays(status.getSignedDays());
		player.sendProtocol(Protocol.valueOf(HP.code.ACC_LOGIN_SIGNED_INFO_S_VALUE, builder));

		return true;
	}

}
