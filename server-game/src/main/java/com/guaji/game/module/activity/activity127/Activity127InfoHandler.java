package com.guaji.game.module.activity.activity127;

import org.guaji.app.AppObj;
import org.guaji.net.protocol.IProtocolHandler;
import org.guaji.net.protocol.Protocol;

import com.guaji.game.config.ActivityTimeCfg;
import com.guaji.game.config.ActivityURTimes127;
import com.guaji.game.config.ReleaseURDropCfg123;
import com.guaji.game.config.ReleaseURTimesCfg123;
import com.guaji.game.config.SysBasicCfg;
import com.guaji.game.module.activity.activity123.Activity123Status;
import com.guaji.game.player.Player;
import com.guaji.game.protocol.Const;
import com.guaji.game.protocol.HP;
import com.guaji.game.protocol.Status;
import com.guaji.game.protocol.Activity3.Activity123Info;
import com.guaji.game.protocol.Activity4.Activity127Info;
import com.guaji.game.util.ActivityUtil;

/**
* @author 作者 E-mail:Jeremy@163.com
* @version 创建时间：2019年5月28日 上午10:30:44
* 类说明
*/
public class Activity127InfoHandler implements IProtocolHandler{

	@Override
	public boolean onProtocol(AppObj appObj, Protocol protocol) {
		Player player = (Player) appObj;
		// 检测活动是否开放
		int activityId = Const.ActivityId.ACTIVITY127_UR_VALUE;
		ActivityTimeCfg activityTimeCfg = ActivityUtil.getCurActivityTimeCfg(activityId);
		if (activityTimeCfg == null) {
			// 活动已关闭
			player.sendError(protocol.getType(), Status.error.ACTIVITY_CLOSE);
			return true;
		}
		ActivityURTimes127 timesCfg = ActivityURTimes127.getTimesCfgByVipLevel(0);
		if (timesCfg == null) {
			player.sendError(protocol.getType(), Status.error.CONFIG_NOT_FOUND);
			return true;
		}

		Activity127Info.Builder builder = generateInfo(player);
		
		player.sendProtocol(Protocol.valueOf(HP.code.ACTIVITY127_UR_INFO_S_VALUE, builder));
		return true;
	}
	
	public static int convertTimeToMillisecond(int hour) {
		return hour * 3600 * 1000;
	}
	
	public static Activity127Info.Builder generateInfo(Player player) {
		int activityId = Const.ActivityId.ACTIVITY127_UR_VALUE;
		long currentTime = System.currentTimeMillis();
		ActivityURTimes127 timesCfg = ActivityURTimes127.getTimesCfgByVipLevel(player.getVipLevel());
		ActivityTimeCfg activityTimeCfg = ActivityUtil.getCurActivityTimeCfg(activityId);
		Activity127Status status = ActivityUtil.getActivityStatus(player.getPlayerData(), activityId, activityTimeCfg.getStageId(),
				Activity127Status.class);
		
		// 总次数
		int totalTimes = status.getTotalTimes();
		// 距离下次免费倒计时
		long lastFreeTime = status.getLastFreeTime();
		int freeCD = (int) Math.max(convertTimeToMillisecond(timesCfg.getFreeCountDown()) - (currentTime - lastFreeTime), 0);
		// 距离倍数失效倒计时
		int singleCost = timesCfg.getSingleCost();
		int tenCost = timesCfg.getTenCost();
		int randCost = timesCfg.getRandCost();
		int genLeftTimes = ReleaseURDropCfg123.getLimitTimes(totalTimes);
		int tenLeftTimes = (totalTimes / 10 + 1) * 10;
		if (genLeftTimes == -1) {
			genLeftTimes = tenLeftTimes;
		}
		int leftLimitTimes = Math.min(genLeftTimes - totalTimes, tenLeftTimes - totalTimes);

		Activity127Info.Builder builder = Activity127Info.newBuilder();
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
