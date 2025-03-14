package com.guaji.game.module.activity.activity140;

import java.util.Arrays;
import java.util.List;

import org.guaji.app.AppObj;
import org.guaji.net.protocol.IProtocolHandler;
import org.guaji.net.protocol.Protocol;

import com.guaji.game.config.Activity140DishWheelRadioCfg;
import com.guaji.game.config.ActivityTimeCfg;
import com.guaji.game.config.SysBasicCfg;
import com.guaji.game.item.ConsumeItems;
import com.guaji.game.log.BehaviorLogger.Action;
import com.guaji.game.player.Player;
import com.guaji.game.protocol.Activity3.Activity140LotteryRep;
import com.guaji.game.protocol.Const;
import com.guaji.game.protocol.Const.changeType;
import com.guaji.game.protocol.HP;
import com.guaji.game.protocol.Status;
import com.guaji.game.util.ActivityUtil;

public class Activity140LotteryHandler implements IProtocolHandler {

	@Override
	public boolean onProtocol(AppObj appObj, Protocol protocol) {

		Player player = (Player) appObj;
		// 检测活动是否开放
		int activityId = Const.ActivityId.RUSSIADISHWHEEL_VALUE;
		ActivityTimeCfg timeCfg = ActivityUtil.getCurActivityTimeCfg(activityId);
		if (timeCfg == null) {
			// 活动已关闭
			player.sendError(protocol.getType(), Status.error.ACTIVITY_CLOSE);
			return true;
		}

		List<String> takeConditions = Arrays
				.asList(SysBasicCfg.getInstance().getActivity140OpenLoginCount().split(","));

		// 活动状态
		Activity140Status status = ActivityUtil.getActivityStatus(player.getPlayerData(), activityId,
				timeCfg.getStageId(), Activity140Status.class);
		if (status == null) {
			// 活动已关闭
			player.sendError(protocol.getType(), Status.error.ACTIVITY_CLOSE);
			return true;
		}

		// 登陆次数
		if (!takeConditions.contains(String.valueOf(status.getLoginTimes()))) {
			// 活动已关闭
			player.sendError(protocol.getType(), Status.error.ACTIVITY_CLOSE);
			return true;
		}
		
		long thisEndTime = status.getActivityTime() + SysBasicCfg.getInstance().getActivity140Continuetime() * 1000 * 60;
		long currentTime = System.currentTimeMillis();
		long activityEndTime = timeCfg.getlEndTime();

		// 如果活动结束未过期延长活动时间
		if (thisEndTime > activityEndTime) {
			activityEndTime = thisEndTime;
		}

		if (activityEndTime < currentTime || status.isUsed()) {
			// 活动已关闭
			player.sendError(protocol.getType(), Status.error.ACTIVITY_CLOSE);
			return true;
		}

		// 没有改名卡消耗钻石
		if (status.getLoginTimes() > 1) {
			ConsumeItems consumeItems = ConsumeItems.valueOf(changeType.CHANGE_GOLD,
					SysBasicCfg.getInstance().getActivity140LotteryCost());
			if (!consumeItems.checkConsume(player, protocol.getType())) {
				player.sendError(protocol.getType(), Status.error.GOLD_NOT_ENOUGH);
				return false;
			}
			consumeItems.consumeTakeAffect(player, Action.ACTIVITY140_LOTTERY);
		}

		int lotteryCount = status.getLotteryCount() + 1;
		Activity140DishWheelRadioCfg inCfg = Activity140DishWheelRadioCfg.getRandmCfg(1);
		Activity140DishWheelRadioCfg outCfg = Activity140DishWheelRadioCfg.getRandmCfg(2);
		if (inCfg == null || outCfg == null) {
			// 没有找到配置文件
			player.sendError(protocol.getType(), Status.error.CONFIG_NOT_FOUND_VALUE);
			return true;
		}

		status.setLotteryCount(lotteryCount);
		status.setUsed(false);
		status.setInIndex(inCfg.getIndex());
		status.setOutIndex(outCfg.getIndex());
		player.getPlayerData().updateActivity(activityId, timeCfg.getStageId());

		Activity140LotteryRep.Builder retInfo = Activity140LotteryRep.newBuilder();
		retInfo.setInIndex(inCfg.getIndex());
		retInfo.setOutIndex(outCfg.getIndex());
		
		player.sendProtocol(Protocol.valueOf(HP.code.ACTIVITY140_DISHWHEEL_LOTTERY_S_VALUE, retInfo));
		return true;
	}
}
