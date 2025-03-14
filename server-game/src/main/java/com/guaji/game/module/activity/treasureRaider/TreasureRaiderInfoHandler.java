package com.guaji.game.module.activity.treasureRaider;

import org.guaji.app.AppObj;
import org.guaji.net.protocol.Protocol;
import org.guaji.net.protocol.IProtocolHandler;

import com.guaji.game.protocol.Activity.HPTreasureRaiderInfoSync;
import com.guaji.game.config.ActivityTimeCfg;
import com.guaji.game.config.SysBasicCfg;
import com.guaji.game.config.TreasureRaiderTimesCfg;
import com.guaji.game.player.Player;
import com.guaji.game.util.ActivityUtil;
import com.guaji.game.protocol.Const;
import com.guaji.game.protocol.HP;
import com.guaji.game.protocol.Status;

public class TreasureRaiderInfoHandler implements IProtocolHandler{
	@Override
	public boolean onProtocol(AppObj appObj, Protocol protocol) {
		Player player = (Player) appObj;
		// 检测活动是否开放
		int activityId = Const.ActivityId.TREASURE_RAIDER_VALUE;
		ActivityTimeCfg timeCfg = ActivityUtil.getCurActivityTimeCfg(activityId);
		if(timeCfg == null){
			// 活动已关闭
			player.sendError(protocol.getType(), Status.error.ACTIVITY_CLOSE);
			return true;
		}
		
		TreasureRaiderStatus treasureRaiderStatus =  ActivityUtil.getActivityStatus(player.getPlayerData(),
				activityId, timeCfg.getStageId(), TreasureRaiderStatus.class);
		int todayOpenTreasureTimes = treasureRaiderStatus.getTodaySearchTimes();
		TreasureRaiderTimesCfg treaRaiderTimesCfg = TreasureRaiderTimesCfg.getTimesCfgByVipLevel(player.getVipLevel());
		int todayLeftFreeTimes = Math.max(treaRaiderTimesCfg.getOneDayFreeTimes() - todayOpenTreasureTimes, 0);
		int todayLeftTimes = Math.max(treaRaiderTimesCfg.getOneDayTotalTimes() - todayOpenTreasureTimes, 0);
		
		HPTreasureRaiderInfoSync.Builder builder = HPTreasureRaiderInfoSync.newBuilder();
		builder.setLeftTime(timeCfg.calcActivitySurplusTime());
		// 奇遇宝箱
		if(!treasureRaiderStatus.getLastBoxAwards().equals("")){
			builder.setItems(treasureRaiderStatus.getLastBoxAwards());
		}
		builder.setFreeTreasureTimes(todayLeftFreeTimes);
		builder.setLeftTreasureTimes(todayLeftTimes);
		builder.setTotalTimes(treasureRaiderStatus.getTotalSearchTimes());
		builder.setOnceCostGold(SysBasicCfg.getInstance().getTreasureRaiderSinglePrice());
		builder.setTenCostGold(SysBasicCfg.getInstance().getTreasureRaiderTenPrice());
		player.sendProtocol(Protocol.valueOf(HP.code.TREASURE_RAIDER_INFO_S, builder));
		player.getPlayerData().updateActivity(activityId, timeCfg.getStageId());
		return true;
	}

}
