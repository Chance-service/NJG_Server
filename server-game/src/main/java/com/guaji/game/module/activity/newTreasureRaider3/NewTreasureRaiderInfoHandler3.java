package com.guaji.game.module.activity.newTreasureRaider3;

import org.guaji.app.AppObj;
import org.guaji.net.protocol.Protocol;
import org.guaji.net.protocol.IProtocolHandler;

import com.guaji.game.protocol.Activity2.HPNewTreasureRaiderInfoSync;
import com.guaji.game.config.ActivityTimeCfg;
import com.guaji.game.config.NewTreasureRaiderDropCfg;
import com.guaji.game.config.NewTreasureRaiderDropCfg3;
import com.guaji.game.config.NewTreasureRaiderTimesCfg;
import com.guaji.game.config.NewTreasureRaiderTimesCfg3;
import com.guaji.game.player.Player;
import com.guaji.game.util.ActivityUtil;
import com.guaji.game.protocol.Const;
import com.guaji.game.protocol.HP;
import com.guaji.game.protocol.Status;

public class NewTreasureRaiderInfoHandler3 implements IProtocolHandler {

	@Override
	public boolean onProtocol(AppObj appObj, Protocol protocol) {
		Player player = (Player) appObj;
		// 检测活动是否开放
		int activityId = Const.ActivityId.NEW_TREASURE_RAIDER3_VALUE;
		ActivityTimeCfg activityTimeCfg = ActivityUtil.getCurActivityTimeCfg(activityId);
		if (activityTimeCfg == null) {
			// 活动已关闭
			player.sendError(protocol.getType(), Status.error.ACTIVITY_CLOSE);
			return true;
		}
		NewTreasureRaiderTimesCfg3 timesCfg = NewTreasureRaiderTimesCfg3.getTimesCfgByVipLevel(0);
		if(timesCfg==null)
		{
			player.sendError(protocol.getType(), Status.error.CONFIG_NOT_FOUND);
			return true;
		}
		
		HPNewTreasureRaiderInfoSync.Builder builder = generateInfo(player);
		
		player.sendProtocol(Protocol.valueOf(HP.code.NEW_TREASURE_RAIDER_INFO3_S_VALUE, builder));
		return true;
	}

	public static int convertTimeToMillisecond(int hour)
	{
		return hour*3600*1000;
	}
	
	public static HPNewTreasureRaiderInfoSync.Builder generateInfo(Player player)
	{
		int activityId = Const.ActivityId.NEW_TREASURE_RAIDER3_VALUE;
		long currentTime = System.currentTimeMillis();
		NewTreasureRaiderTimesCfg3 timesCfg = NewTreasureRaiderTimesCfg3.getTimesCfgByVipLevel(player.getVipLevel());
		ActivityTimeCfg activityTimeCfg = ActivityUtil.getCurActivityTimeCfg(activityId);
		NewTreasureRaiderStatus3 status = ActivityUtil.getActivityStatus(player.getPlayerData(), activityId,
				activityTimeCfg.getStageId(), NewTreasureRaiderStatus3.class);
		// 总次数
		int totalTimes = status.getTotalTimes();
		// 距离下次免费倒计时
		long lastFreeTime = status.getLastFreeTime();
		int freeCD = (int)Math.max(convertTimeToMillisecond(timesCfg.getFreeCountDown())-(currentTime-lastFreeTime),0);
		// 距离倍数失效倒计时
		long multipStartTime = status.getMultipStartTime();
		int multipOvertime = (int)Math.max(status.getMultipOverTime()-(currentTime-multipStartTime), 0);
		int multipTimes = status.getMultiple();
		int singleCost = timesCfg.getSingleCost();
		int tenCost = timesCfg.getTenCost();
		int genLeftTimes = NewTreasureRaiderDropCfg3.getLimitTimes(totalTimes);
		int tenLeftTimes = (totalTimes/10+1)*10;
		if(genLeftTimes==-1)
		{
			genLeftTimes = tenLeftTimes;
		}
		int leftLimitTimes = Math.min(genLeftTimes-totalTimes, tenLeftTimes-totalTimes);

		HPNewTreasureRaiderInfoSync.Builder builder = HPNewTreasureRaiderInfoSync.newBuilder();
		builder.setLeftTime(activityTimeCfg.calcActivitySurplusTime());
		builder.setFreeTreasureTimes(0);
		builder.setLeftTreasureTimes(0);
		builder.setOnceCostGold(singleCost);
		builder.setTenCostGold(tenCost);
		builder.setLeftBuffTimes(multipOvertime/1000);
		builder.setBufMultiple(multipTimes);
		builder.setFreeCD(freeCD/1000);
		builder.setLeftAwardTimes(leftLimitTimes);
		return builder;
	}
}
