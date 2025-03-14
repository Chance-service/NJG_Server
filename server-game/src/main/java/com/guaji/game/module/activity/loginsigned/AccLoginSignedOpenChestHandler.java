package com.guaji.game.module.activity.loginsigned;

import java.util.Calendar;

import org.guaji.app.AppObj;
import org.guaji.config.ConfigManager;
import org.guaji.net.protocol.IProtocolHandler;
import org.guaji.net.protocol.Protocol;
import org.guaji.os.GuaJiTime;

import com.guaji.game.config.ActivityTimeCfg;
import com.guaji.game.config.accLoginSignedPointCfg;
import com.guaji.game.item.AwardItems;
import com.guaji.game.log.BehaviorLogger;
import com.guaji.game.log.BehaviorLogger.Action;
import com.guaji.game.log.BehaviorLogger.Params;
import com.guaji.game.log.BehaviorLogger.Source;
import com.guaji.game.player.Player;
import com.guaji.game.protocol.Const;
import com.guaji.game.protocol.HP;
import com.guaji.game.protocol.Status;
import com.guaji.game.protocol.Activity4.LoginSignedAwardReq;
import com.guaji.game.protocol.Activity4.LoginSignedRep;
import com.guaji.game.util.ActivityUtil;

/**
 * 累计登录签到开箱子协议
 */
public class AccLoginSignedOpenChestHandler implements IProtocolHandler {

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

		// 解析接受到的数据
		LoginSignedAwardReq request = protocol.parseProtocol(LoginSignedAwardReq.getDefaultInstance());
		int pointNum = request.getLevel();
		int nowMonth = GuaJiTime.getCalendar().get(Calendar.MONTH) + 1;

		// 查找累计登录配置数据
		accLoginSignedPointCfg loginSignedConf = accLoginSignedPointCfg.getSignedPointCfg(nowMonth, pointNum);

		if (loginSignedConf == null) {
			// 活动奖励不存在
			player.sendError(protocol.getType(), Status.error.CONFIG_NOT_FOUND);
			return true;
		}
		// 玩家累计登录数据
		AccLoginSignedStatus status = ActivityUtil.getActivityStatus(player.getPlayerData(), activityId,
				timeConfig.getStageId(), AccLoginSignedStatus.class);

		// 总签到次数
		int totalSignedTimes = status.getSupplSignedDays().size() + status.getSignedDays().size();
		if (pointNum > totalSignedTimes) {
			// 不符合条件
			player.sendError(protocol.getType(), Status.error.ACC_LOGIN_SIGNED_NO_OPENCHEST);
			return true;
		}

		if (status.getGotAwardChest().contains(pointNum)) {
			// 不符合条件
			player.sendError(protocol.getType(), Status.error.ACC_LOGIN_SIGNED_HAVE_OPENCHEST);
			return true;
		}
		player.sendError(protocol.getType(), Status.error.ACC_LOGIN_SIGNED_HAVE_DAY_AWARD);

		status.addGotAwardChest(pointNum);

		int registerDays = ActivityUtil.calcReisterDates(player.getPlayerData().getPlayerEntity().getCreateTime());
		// 下发奖励
		AwardItems awards = AwardItems.valueOf(loginSignedConf.getAward());
		awards.rewardTakeAffectAndPush(player, Action.ACC_LOGIN_SIGNED_AWARDS, 2);

		LoginSignedRep.Builder response = LoginSignedRep.newBuilder();

		// response.setRegisterDays(registerDays);
		response.addAllSignedDays(status.getSignedDays());
		response.addAllSupplSignedDays(status.getSupplSignedDays());
		response.addAllGotAwardChest(status.getGotAwardChest());
		response.setCurMonth(nowMonth);
		player.sendProtocol(Protocol.valueOf(HP.code.ACC_LOGIN_SIGNED_INFO_S_VALUE, response));

		BehaviorLogger.log4Platform(player, Action.ACC_LOGIN_SIGNEDCHEST_AWARDS, Params.valueOf("rewardDay", pointNum),
				Params.valueOf("awardsCfgId", pointNum), Params.valueOf("awards", loginSignedConf.getAward()));

		BehaviorLogger.log4Service(player, Source.USER_OPERATION, Action.ACC_LOGIN_SIGNEDCHEST_AWARDS,
				Params.valueOf("rewardDay", pointNum), Params.valueOf("awardsCfgId", pointNum),
				Params.valueOf("awards", loginSignedConf.getAward()));

		return false;
	}

}
