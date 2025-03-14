package com.guaji.game.module.activity.activity138;

import org.guaji.app.AppObj;
import org.guaji.net.protocol.IProtocolHandler;
import org.guaji.net.protocol.Protocol;

import com.guaji.game.config.Activity139DropCfg;
import com.guaji.game.config.Activity139TimesCfg;
import com.guaji.game.config.ActivityTimeCfg;
import com.guaji.game.player.Player;
import com.guaji.game.protocol.Activity4.Activity138TreasureRaiderInfoSync;
import com.guaji.game.protocol.Const;
import com.guaji.game.protocol.HP;
import com.guaji.game.protocol.Status;
import com.guaji.game.util.ActivityUtil;

public class NewTreasureRaiderInfoHandler139 implements IProtocolHandler {

	@Override
	public boolean onProtocol(AppObj appObj, Protocol protocol) {

		Player player = (Player) appObj;
		// 检测活动是否开放
		int activityId = Const.ActivityId.NEW_TREASURE_RAIDER139_VALUE;
		ActivityTimeCfg activityTimeCfg = ActivityUtil.getCurActivityTimeCfg(activityId);
		if (activityTimeCfg == null) {
			// 活动已关闭
			player.sendError(protocol.getType(), Status.error.ACTIVITY_CLOSE);
			return true;
		}
		Activity139TimesCfg timesCfg = Activity139TimesCfg.getTimesCfgByVipLevel(0);
		if (timesCfg == null) {
			player.sendError(protocol.getType(), Status.error.CONFIG_NOT_FOUND);
			return true;
		}

		Activity138TreasureRaiderInfoSync.Builder builder = generateInfo(player,1);

		player.sendProtocol(Protocol.valueOf(HP.code.ACTIVITY138_RAIDER_INFO_S, builder));
		return true;
	}

	public static int convertTimeToMillisecond(int hour) {
		return hour * 3600 * 1000;
	}

	public static Activity138TreasureRaiderInfoSync.Builder generateInfo(Player player,int searchType) {
		int activityId = Const.ActivityId.NEW_TREASURE_RAIDER139_VALUE;
		long currentTime = System.currentTimeMillis();
		Activity139TimesCfg timesCfg = Activity139TimesCfg.getTimesCfgByVipLevel(0);
		ActivityTimeCfg activityTimeCfg = ActivityUtil.getCurActivityTimeCfg(activityId);
		NewTreasureRaiderStatus139 status = ActivityUtil.getActivityStatus(player.getPlayerData(), activityId,
				activityTimeCfg.getStageId(), NewTreasureRaiderStatus139.class);
		// 总次数
		int totalTimes = status.getTotalTimes();
		// 距离下次免费倒计时
		long lastFreeTime = status.getLastFreeTime();
		int freeCD = (int) Math
				.max(convertTimeToMillisecond(timesCfg.getFreeCountDown()) - (currentTime - lastFreeTime), 0);
		// 距离倍数失效倒计时
		long multipStartTime = status.getMultipStartTime();
		int multipOvertime = (int) Math.max(status.getMultipOverTime() - (currentTime - multipStartTime), 0);
		int multipTimes = status.getMultiple();
		// timesCfg.getSingleCostItems().toString()
		String singleCost = timesCfg.getSingleCost();
		String tenCost = timesCfg.getTenCost();
		int genLeftTimes = Activity139DropCfg.getLimitTimes(searchType, totalTimes);
		int tenLeftTimes = (totalTimes / 10 + 1) * 10;
		if (genLeftTimes == -1) {
			genLeftTimes = tenLeftTimes;
		}
		int leftLimitTimes = Math.min(genLeftTimes - totalTimes, tenLeftTimes - totalTimes);

		Activity138TreasureRaiderInfoSync.Builder builder = Activity138TreasureRaiderInfoSync.newBuilder();
		builder.setLeftTime(activityTimeCfg.calcActivitySurplusTime());
		builder.setFreeTreasureTimes(0);
		builder.setLeftTreasureTimes(0);
		builder.setOnceCostGold(singleCost);
		builder.setTenCostGold(tenCost);
		builder.setLeftBuffTimes(multipOvertime / 1000);
		builder.setBufMultiple(multipTimes);
		builder.setFreeCD(freeCD / 1000);
		builder.addLuckyMercenary(1);

		builder.setLeftAwardTimes(leftLimitTimes);
		return builder;
	}
}
