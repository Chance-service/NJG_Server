package com.guaji.game.module.activity.login;

import java.util.Set;

import org.guaji.app.AppObj;
import org.guaji.config.ConfigManager;
import org.guaji.net.protocol.Protocol;
import org.guaji.net.protocol.IProtocolHandler;

import com.guaji.game.protocol.Activity.HPAccLoginAwards;
import com.guaji.game.protocol.Activity.HPAccLoginAwardsRet;
import com.guaji.game.config.AccLoginCfg;
import com.guaji.game.config.ActivityTimeCfg;
import com.guaji.game.item.AwardItems;
import com.guaji.game.log.BehaviorLogger;
import com.guaji.game.log.BehaviorLogger.Action;
import com.guaji.game.log.BehaviorLogger.Params;
import com.guaji.game.log.BehaviorLogger.Source;
import com.guaji.game.player.Player;
import com.guaji.game.util.ActivityUtil;
import com.guaji.game.protocol.Const;
import com.guaji.game.protocol.HP;
import com.guaji.game.protocol.Status;

/**
 * 领取累计登录活动奖励
 */
public class AccLoginAwardsHandler implements IProtocolHandler {

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
		// 解析接受到的数据
		HPAccLoginAwards request = protocol.parseProtocol(HPAccLoginAwards.getDefaultInstance());
		int rewardDay = request.getRewwardDay();
		// 查找累计登录配置数据
		AccLoginCfg loginConfig = ConfigManager.getInstance().getConfigByKey(AccLoginCfg.class, rewardDay);
		if (loginConfig == null) {
			// 活动奖励不存在
			player.sendError(protocol.getType(), Status.error.CONFIG_NOT_FOUND);
			return true;
		}
		// 玩家累计登录数据
		AccLoginStatus status = ActivityUtil.getActivityStatus(player.getPlayerData(), activityId, timeConfig.getStageId(), AccLoginStatus.class);
		if (status.getTotalLoginDays() < rewardDay) {
			// 累计登录天数不足
			player.sendError(protocol.getType(), Status.error.ACC_LOGIN_DAYS_LACK);
			return true;
		}
		// 奖励是否领取过
		Set<Integer> gotIds = status.getGotAwardCfgIds();
		if (gotIds.contains(rewardDay)) {
			// 活动奖励已领取
			player.sendError(protocol.getType(), Status.error.ACTIVITY_AWARDS_HAS_GOT);
			return true;
		} else {
			status.addGotAwardCfgId(rewardDay);
			player.getPlayerData().updateActivity(activityId, timeConfig.getStageId());
		}
		// 下发奖励
		AwardItems awards = AwardItems.valueOf(loginConfig.getAwards());
		awards.rewardTakeAffectAndPush(player, Action.ACC_LOGIN_AWARDS,2);
		// 构建返回数据包
		HPAccLoginAwardsRet.Builder response = HPAccLoginAwardsRet.newBuilder();
		response.setLeftTime(timeConfig.calcActivitySurplusTime());
		response.addAllGotAwardCfgId(status.getGotAwardCfgIds());
		player.sendProtocol(Protocol.valueOf(HP.code.ACC_LOGIN_AWARDS_S_VALUE, response));

		BehaviorLogger.log4Platform(player, Action.ACC_LOGIN_AWARDS, Params.valueOf("rewardDay", rewardDay), Params.valueOf("awardsCfgId", rewardDay),
				Params.valueOf("awards", loginConfig.getAwards()));

		BehaviorLogger.log4Service(player, Source.USER_OPERATION, Action.ACC_LOGIN_AWARDS, Params.valueOf("rewardDay", rewardDay),
				Params.valueOf("awardsCfgId", rewardDay), Params.valueOf("awards", loginConfig.getAwards()));
		return true;
	}

}
