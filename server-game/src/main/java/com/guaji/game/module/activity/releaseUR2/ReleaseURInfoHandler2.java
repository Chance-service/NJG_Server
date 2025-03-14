package com.guaji.game.module.activity.releaseUR2;

import org.guaji.app.AppObj;
import org.guaji.net.protocol.IProtocolHandler;
import org.guaji.net.protocol.Protocol;

import com.guaji.game.config.ActivityTimeCfg;
import com.guaji.game.config.ReleaseURDropCfg;
import com.guaji.game.config.ReleaseURDropCfg2;
import com.guaji.game.config.ReleaseURTimesCfg;
import com.guaji.game.config.ReleaseURTimesCfg2;
import com.guaji.game.config.SysBasicCfg;
import com.guaji.game.module.activity.releaseUR.ReleaseURStatus;
import com.guaji.game.player.Player;
import com.guaji.game.protocol.Const;
import com.guaji.game.protocol.HP;
import com.guaji.game.protocol.Status;
import com.guaji.game.protocol.Activity3.ReleaseURInfo;
import com.guaji.game.protocol.Activity3.ReleaseURInfo2;
import com.guaji.game.util.ActivityUtil;

/**
* @author 作者 E-mail:Jeremy@163.com
* @version 创建时间：2019年4月5日 下午9:30:55
* 类说明
*/
public class ReleaseURInfoHandler2 implements IProtocolHandler{
	@Override
	public boolean onProtocol(AppObj appObj, Protocol protocol) {
		Player player = (Player) appObj;
		// 检测活动是否开放
		int activityId = Const.ActivityId.RELEASE_UR2_VALUE;
		ActivityTimeCfg activityTimeCfg = ActivityUtil.getCurActivityTimeCfg(activityId);
		if (activityTimeCfg == null) {
			// 活动已关闭
			player.sendError(protocol.getType(), Status.error.ACTIVITY_CLOSE);
			return true;
		}
		ReleaseURTimesCfg2 timesCfg = ReleaseURTimesCfg2.getTimesCfgByVipLevel(0);
		if (timesCfg == null) {
			player.sendError(protocol.getType(), Status.error.CONFIG_NOT_FOUND);
			return true;
		}

		ReleaseURInfo2.Builder builder = generateInfo(player);

		player.sendProtocol(Protocol.valueOf(HP.code.RELEASE_UR_INFO2_S, builder));
		return true;
	}

	public static int convertTimeToMillisecond(int hour) {
		return hour * 3600 * 1000;
	}

	public static ReleaseURInfo2.Builder generateInfo(Player player) {
		int activityId = Const.ActivityId.RELEASE_UR2_VALUE;
		long currentTime = System.currentTimeMillis();
		ReleaseURTimesCfg2 timesCfg = ReleaseURTimesCfg2.getTimesCfgByVipLevel(player.getVipLevel());
		ActivityTimeCfg activityTimeCfg = ActivityUtil.getCurActivityTimeCfg(activityId);
		ReleaseURStatu2 status = ActivityUtil.getActivityStatus(player.getPlayerData(), activityId, activityTimeCfg.getStageId(),
				ReleaseURStatu2.class);
		
		// 总次数
		int totalTimes = status.getTotalTimes();
		// 距离下次免费倒计时
		long lastFreeTime = status.getLastFreeTime();
		int freeCD = (int) Math.max(convertTimeToMillisecond(timesCfg.getFreeCountDown()) - (currentTime - lastFreeTime), 0);
		// 距离倍数失效倒计时
		int singleCost = timesCfg.getSingleCost();
		int tenCost = timesCfg.getTenCost();
		int randCost = timesCfg.getRandCost();
		int genLeftTimes = ReleaseURDropCfg2.getLimitTimes(totalTimes);
		int tenLeftTimes = (totalTimes / 10 + 1) * 10;
		if (genLeftTimes == -1) {
			genLeftTimes = tenLeftTimes;
		}
		int leftLimitTimes = Math.min(genLeftTimes - totalTimes, tenLeftTimes - totalTimes);

		ReleaseURInfo2.Builder builder = ReleaseURInfo2.newBuilder();
		builder.setLeftTime(activityTimeCfg.calcActivitySurplusTime());
		builder.setFreeTreasureTimes(0);
		builder.setLeftTreasureTimes(0);
		builder.setOnceCostGold(singleCost);
		builder.setTenCostGold(tenCost);
		builder.setRandCostGold(randCost);
		builder.setFreeCD(freeCD / 1000);
		builder.setLeftAwardTimes(leftLimitTimes);
		builder.setLotteryCost(SysBasicCfg.getInstance().getReleaseUrCostLucky2());
		builder.setLotterypoint(status.getLuckyValue());
	
		builder.addAllLatticeIndex(status.getLastRandomIndexs());
		
		return builder;
	}
}
