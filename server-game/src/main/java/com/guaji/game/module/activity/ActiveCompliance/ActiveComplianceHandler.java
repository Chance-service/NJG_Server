package com.guaji.game.module.activity.ActiveCompliance;

import org.guaji.app.AppObj;
import org.guaji.config.ConfigManager;
import org.guaji.net.protocol.IProtocolHandler;
import org.guaji.net.protocol.Protocol;

import com.guaji.game.config.AccLoginSignedCfg;
import com.guaji.game.config.ActiveComplianceCfg;
import com.guaji.game.config.ActivityTimeCfg;
import com.guaji.game.config.SysBasicCfg;
import com.guaji.game.entity.DailyQuestEntity;
import com.guaji.game.item.AwardItems;
import com.guaji.game.log.BehaviorLogger.Action;
import com.guaji.game.module.activity.activity121.ReleaseURStatus121;
import com.guaji.game.player.Player;
import com.guaji.game.protocol.Const;
import com.guaji.game.protocol.HP;
import com.guaji.game.protocol.Status;
import com.guaji.game.protocol.Activity3.ReleaseURDraw;
import com.guaji.game.protocol.Activity4.ActiveComplianceAwardRep;
import com.guaji.game.protocol.Activity4.ActiveComplianceAwardReq;
import com.guaji.game.util.ActivityUtil;

/**
 * @author 作者 E-mail:Jeremy@163.com
 * @version 创建时间：2019年5月6日 上午12:25:04 类说明
 */
public class ActiveComplianceHandler implements IProtocolHandler {

	@Override
	public boolean onProtocol(AppObj appObj, Protocol protocol) {

		Player player = (Player) appObj;
		// 检测活动是否开放
		int activityId = Const.ActivityId.ACTIVECOMPLIANCE_VALUE;
		ActivityTimeCfg activityTimeCfg = ActivityUtil.getCurActivityTimeCfg(activityId);
		if (activityTimeCfg == null) {
			// 活动已关闭
			player.sendError(protocol.getType(), Status.error.ACTIVITY_CLOSE);
			return true;

		}

		ActiveComplianceAwardReq req = protocol.parseProtocol(ActiveComplianceAwardReq.getDefaultInstance());
		// 周期内地几天奖励
		int day = req.getDay();
		ActiveComplianceCfg activeConf = ConfigManager.getInstance().getConfigByKey(ActiveComplianceCfg.class, day);
		if (activeConf == null) {
			player.sendError(protocol.getType(), Status.error.CONFIG_NOT_FOUND);
			return true;
		}

		// 获取活动状态
		ActiveStatus status = ActivityUtil.getActiveComplianceStatus(player.getPlayerData());
		if (status == null) {
			return true;
		}
		
        if (status.calcActivitySurplusTime() <= 0){
			return true;
		}
		
		if (day > status.getDays()) {
			// 参数错误
			player.sendError(protocol.getType(), Status.error.PARAMS_INVALID);
			return true;
		}

		if (status.getAwardDays().contains(day)) {
			// 活动奖励已领取
			player.sendError(protocol.getType(), Status.error.CONFIG_NOT_FOUND);
			return true;
		}

		AwardItems awards = AwardItems.valueOf(activeConf.getAward());
		awards.rewardTakeAffectAndPush(player, Action.ACTIVECOMPLIANCE_AWARDS, 2);
		status.setIsfirst(true);
		status.addAwardDays(day);
		player.getPlayerData().updateActivity(activityId, activityTimeCfg.getStageId());
		ActiveComplianceAwardRep.Builder response = ActiveComplianceAwardRep.newBuilder();

		// 返回获取
		DailyQuestEntity dailyQuestEntity = player.getPlayerData().loadDailyQuestEntity();
		if (dailyQuestEntity != null) {
			response.setCurActive(dailyQuestEntity.getDailyPoint());
		} else {
			response.setCurActive(0);
		}
		response.setDays(status.getDays());

		response.addAllAwardDays(status.getAwardDays());
		response.setSurplusTime(status.calcActivitySurplusTime());
		player.sendProtocol(Protocol.valueOf(HP.code.ACTIVECOMPLIANCE_AWARD_S_VALUE, response));

		return true;
	}

}
