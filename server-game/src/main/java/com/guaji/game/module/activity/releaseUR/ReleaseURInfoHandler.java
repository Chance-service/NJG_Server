package com.guaji.game.module.activity.releaseUR;

import org.guaji.app.AppObj;
import org.guaji.net.protocol.Protocol;
import org.guaji.net.protocol.IProtocolHandler;

import com.guaji.game.protocol.Activity3.ReleaseURInfo;
import com.guaji.game.config.ActivityTimeCfg;
import com.guaji.game.config.ReleaseURDropCfg;
import com.guaji.game.config.ReleaseURTimesCfg;
import com.guaji.game.config.SysBasicCfg;
import com.guaji.game.player.Player;
import com.guaji.game.util.ActivityUtil;
import com.guaji.game.protocol.Const;
import com.guaji.game.protocol.HP;
import com.guaji.game.protocol.Status;

/**
 * 神将投放活动信息同步
 */
public class ReleaseURInfoHandler implements IProtocolHandler {

	@Override
	public boolean onProtocol(AppObj appObj, Protocol protocol) {
		Player player = (Player) appObj;
		// 检测活动是否开放
		int activityId = Const.ActivityId.RELEASE_UR_VALUE;
		ActivityTimeCfg activityTimeCfg = ActivityUtil.getCurActivityTimeCfg(activityId);
		if (activityTimeCfg == null) {
			// 活动已关闭
			player.sendError(protocol.getType(), Status.error.ACTIVITY_CLOSE);
			return true;
		}
		ReleaseURTimesCfg timesCfg = ReleaseURTimesCfg.getTimesCfgByVipLevel(0);
		if (timesCfg == null) {
			player.sendError(protocol.getType(), Status.error.CONFIG_NOT_FOUND);
			return true;
		}

		ReleaseURInfo.Builder builder = generateInfo(player);

		player.sendProtocol(Protocol.valueOf(HP.code.RELEASE_UR_INFO_S, builder));
		return true;
	}

	public static int convertTimeToMillisecond(int hour) {
		return hour * 3600 * 1000;
	}

	public static ReleaseURInfo.Builder generateInfo(Player player) {
		int activityId = Const.ActivityId.RELEASE_UR_VALUE;
		long currentTime = System.currentTimeMillis();
		ReleaseURTimesCfg timesCfg = ReleaseURTimesCfg.getTimesCfgByVipLevel(player.getVipLevel());
		ActivityTimeCfg activityTimeCfg = ActivityUtil.getCurActivityTimeCfg(activityId);
		ReleaseURStatus status = ActivityUtil.getActivityStatus(player.getPlayerData(), activityId, activityTimeCfg.getStageId(),
		        ReleaseURStatus.class);
		
		// 总次数
		int totalTimes = status.getTotalTimes();
		// 距离下次免费倒计时
		long lastFreeTime = status.getLastFreeTime();
		int freeCD = (int) Math.max(convertTimeToMillisecond(timesCfg.getFreeCountDown()) - (currentTime - lastFreeTime), 0);
		// 距离倍数失效倒计时
		long multipStartTime = status.getMultipStartTime();
		int multipOvertime = (int) Math.max(status.getMultipOverTime() - (currentTime - multipStartTime), 0);
		int multipTimes = status.getMultiple();
		int singleCost = timesCfg.getSingleCost();
		int tenCost = timesCfg.getTenCost();
		int randCost = timesCfg.getRandCost();
		int genLeftTimes = ReleaseURDropCfg.getLimitTimes(totalTimes);
		int tenLeftTimes = (totalTimes / 10 + 1) * 10;
		if (genLeftTimes == -1) {
			genLeftTimes = tenLeftTimes;
		}
		int leftLimitTimes = Math.min(genLeftTimes - totalTimes, tenLeftTimes - totalTimes);

		ReleaseURInfo.Builder builder = ReleaseURInfo.newBuilder();
		builder.setLeftTime(activityTimeCfg.calcActivitySurplusTime());
		builder.setFreeTreasureTimes(0);
		builder.setLeftTreasureTimes(0);
		builder.setOnceCostGold(singleCost);
		builder.setTenCostGold(tenCost);
		builder.setRandCostGold(randCost);
		builder.setLeftBuffTimes(multipOvertime / 1000);
		builder.setBufMultiple(multipTimes);
		builder.setFreeCD(freeCD / 1000);
		builder.setLeftAwardTimes(leftLimitTimes);
		builder.setLotteryCost(SysBasicCfg.getInstance().getReleaseUrLotteryCost());
		builder.setLotterypoint(status.getLuckyValue());
		for(Integer index:status.getLotteryIndexs())
		{
			builder.addLotteryindex(index);
		}
		
		return builder;
	}
}
