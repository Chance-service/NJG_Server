package com.guaji.game.module.activity.ActiveCompliance;

import org.guaji.app.AppObj;
import org.guaji.net.protocol.IProtocolHandler;
import org.guaji.net.protocol.Protocol;

import com.guaji.game.config.ActivityTimeCfg;
import com.guaji.game.entity.DailyQuestEntity;
import com.guaji.game.player.Player;
import com.guaji.game.protocol.Activity4.ActiveComplianceAwardRep;
import com.guaji.game.protocol.Const;
import com.guaji.game.protocol.HP;
import com.guaji.game.protocol.Status;
import com.guaji.game.util.ActivityUtil;

/**
 * @author 作者 E-mail:Jeremy@163.com
 * @version 创建时间：2019年5月6日 上午12:24:47 类说明
 */
public class ActiveComplianceInfoHandler implements IProtocolHandler {

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
		ActiveComplianceAwardRep.Builder response = ActiveComplianceAwardRep.newBuilder();

		// 返回获取
		DailyQuestEntity dailyQuestEntity = player.getPlayerData().loadDailyQuestEntity();
		if (dailyQuestEntity != null) {
			response.setCurActive(dailyQuestEntity.getDailyPoint());
		} else {
			response.setCurActive(0);
		}
		ActiveStatus activeStatus = ActivityUtil.getActiveComplianceStatus(player.getPlayerData());
		if (activeStatus == null) {
			player.sendError(protocol.getType(), Status.error.ACTIVITY_CLOSE);
			return true;
		}
		
        if (activeStatus.calcActivitySurplusTime() <= 0){
        	player.sendError(protocol.getType(), Status.error.ACTIVITY_CLOSE);
			return true;
		}
		
		response.setDays(activeStatus.getDays());

		response.addAllAwardDays(activeStatus.getAwardDays());
		response.setSurplusTime(activeStatus.calcActivitySurplusTime());
		player.sendProtocol(Protocol.valueOf(HP.code.ACTIVECOMPLIANCE_INFO_S_VALUE, response));

		return true;
	}

}
