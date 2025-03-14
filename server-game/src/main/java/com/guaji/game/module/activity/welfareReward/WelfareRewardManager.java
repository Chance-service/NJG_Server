package com.guaji.game.module.activity.welfareReward;

import java.util.ArrayList;
import java.util.List;

import org.guaji.net.protocol.Protocol;
import org.guaji.os.GuaJiRand;

import com.guaji.game.protocol.Activity3.WelfareRewardRes;
import com.guaji.game.config.ActivityTimeCfg;
import com.guaji.game.config.WelfareRewardCfg;
import com.guaji.game.item.AwardItems;
import com.guaji.game.item.ItemInfo;
import com.guaji.game.log.BehaviorLogger;
import com.guaji.game.log.BehaviorLogger.Action;
import com.guaji.game.log.BehaviorLogger.Params;
import com.guaji.game.player.Player;
import com.guaji.game.protocol.HP;
import com.guaji.game.protocol.Status;

/**
 * 天降元宝活动管理类
 */
public class WelfareRewardManager {

	/**
	 * 同步的时候并不做随机
	 */
	static void sync(Player player, WelfareRewardStatus status, ActivityTimeCfg timeCfg) {
		// 根据当前的阶段进入哪个奖池
		int pool = status.getCurrentStep();
		int cost = WelfareRewardCfg.costMap.get(pool);
		boolean canPlay = status.canPlay();
		int leftTime = timeCfg.calcActivitySurplusTime();
		WelfareRewardRes.Builder response = WelfareRewardRes.newBuilder();
		response.setCost(cost);
		response.setGold(0);
		response.setCanPlay(canPlay);
		response.setLeftTime(leftTime);
		player.sendProtocol(Protocol.valueOf(HP.code.WELFARE_REWARD_S_VALUE, response));
	}

	/**
	 * 只有在抽奖的时候才做随机
	 */
	static void play(Player player, WelfareRewardStatus status, ActivityTimeCfg timeCfg) {
		// 进入哪个随机
		WelfareRewardCfg cfg = getRandomCfg(status);
		if (null == cfg) {
			player.sendError(HP.code.WELFARE_REWARD_S_VALUE, Status.error.CONFIG_NOT_FOUND_VALUE);
			return;
		}
		// 判断消耗
		int costGold = cfg.getCost();
		if (player.getGold() < costGold) {
			player.sendError(HP.code.WELFARE_REWARD_S_VALUE, Status.error.GOLD_NOT_ENOUGH_VALUE);
			return;
		}
		// 随机出奖励多少钻石
		int min = cfg.getMin();
		int max = cfg.getMax();
		int rewardGold = GuaJiRand.random(min, max);
		// 消耗钻石
		player.consumeGold(costGold, Action.WELFARE_REWARD);
		// 发钻石
		AwardItems awardItems = new AwardItems();
		ItemInfo itemInfo = ItemInfo.valueOf(10000, 1001, rewardGold);
		awardItems.addItem(itemInfo);
		awardItems.rewardTakeAffectAndPush(player, Action.WELFARE_REWARD, 0);
		// 更新数据
		if (cfg.isMaxStage()) {
			status.setCanPlay(false);
		} else {
			status.setCurrentStep(status.getCurrentStep() + 1);
			// 获取下一次的消耗数据
			cfg = getRandomCfg(status);
			costGold = cfg.getCost();
		}
		player.getPlayerData().updateActivity(timeCfg.getActivityId(), timeCfg.getStageId());
		// 平台数据统计
		BehaviorLogger.log4Platform(player, Action.WELFARE_REWARD, Params.valueOf("stage", status.getCurrentStep()));
		// 返回同步
		WelfareRewardRes.Builder response = WelfareRewardRes.newBuilder();
		response.setCanPlay(status.canPlay());
		response.setLeftTime(timeCfg.calcActivitySurplusTime());
		response.setCost(costGold);
		response.setGold(rewardGold);
		player.sendProtocol(Protocol.valueOf(HP.code.WELFARE_REWARD_S, response));
	}

	public static WelfareRewardCfg getRandomCfg(WelfareRewardStatus status) {
		int pool = status.getCurrentStep();
		List<Integer> rateList = new ArrayList<Integer>();
		List<WelfareRewardCfg> cfgList = WelfareRewardCfg.poolMap.get(pool);
		for (WelfareRewardCfg cfg : cfgList) {
			rateList.add(cfg.getDrawRate());
		}
		// 随机出哪个配置
		return GuaJiRand.randonWeightObject(cfgList, rateList);
	}

}
