package com.guaji.game.module.activity.obon;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.guaji.log.Log;
import org.guaji.os.GuaJiRand;
import org.guaji.os.GuaJiTime;
import org.guaji.config.ConfigManager;
import org.guaji.net.protocol.Protocol;

import com.guaji.game.protocol.Activity3.ObonRet;
import com.guaji.game.config.ActivityTimeCfg;
import com.guaji.game.config.ObonBuffCfg;
import com.guaji.game.config.ObonDropCfg;
import com.guaji.game.config.ObonStageCfg;
import com.guaji.game.config.ObonTimesCfg;
import com.guaji.game.entity.AllianceEntity;
import com.guaji.game.item.AwardItems;
import com.guaji.game.item.ConsumeItems;
import com.guaji.game.item.ItemInfo;
import com.guaji.game.item.AwardItems.Item;
import com.guaji.game.log.BehaviorLogger;
import com.guaji.game.log.BehaviorLogger.Action;
import com.guaji.game.log.BehaviorLogger.Params;
import com.guaji.game.manager.AllianceManager;
import com.guaji.game.manager.MailManager;
import com.guaji.game.player.Player;
import com.guaji.game.util.ActivityUtil;
import com.guaji.game.util.GsConst;
import com.guaji.game.protocol.Const;
import com.guaji.game.protocol.Const.changeType;
import com.guaji.game.protocol.HP;
import com.guaji.game.protocol.Mail;
import com.guaji.game.protocol.Status;

/**
 * 鬼节活动管理类
 */
public class ObonManager {

	static final int TIMES_TYPE_SINGLE = 1;
	static final int TIMES_TYPE_TEN = 10;

	/**
	 * 同步
	 */
	static void sync(Player player) {
		ObonRet.Builder builder = getBuilder(player);
		player.sendProtocol(Protocol.valueOf(HP.code.OBON_S_VALUE, builder));
	}

	/**
	 * 抽奖
	 */
	static int draw(Player player, ActivityTimeCfg timeCfg, ObonStatus status, int searchTimes) {
		int activityId = Const.ActivityId.OBON_VALUE;
		// 获取基础配表数据
		ObonTimesCfg timesCfg = ObonTimesCfg.getTimesCfgByVipLevel(player.getVipLevel());
		if (timesCfg == null) {
			return Status.error.CONFIG_NOT_FOUND_VALUE;
		}
		int singleCost = timesCfg.getSingleCost();
		int tenCost = timesCfg.getTenCost();

		// 使用免费抽奖的时间
		int lastFreeTime = status.getLastFreeTime();
		// 当前系统时间
		int currentTime = GuaJiTime.getSeconds();
		// 计算实际花费钻石数量
		int payGold = 0;

		if (searchTimes == TIMES_TYPE_TEN) {
			payGold = tenCost;
		} else {
			// 是否免费
			int freeCD = Math.max(hourToSec(timesCfg.getFreeCountDown()) - (currentTime - lastFreeTime), 0);
			if (freeCD == 0) {
				payGold = 0;
				status.setLastFreeTime(currentTime);
			} else {
				payGold = singleCost;
			}
		}
		if (payGold > player.getGold()) {
			// 钻石不足
			return Status.error.GOLD_NOT_ENOUGH_VALUE;
		}
		// 扣除钻石
		if (payGold > 0) {
			player.consumeGold(payGold, Action.OBON);
			ConsumeItems.valueOf(changeType.CHANGE_GOLD, payGold).pushChange(player);
		}
		List<AwardItems> awardsList = new ArrayList<AwardItems>();
		List<Integer> multiples = new ArrayList<Integer>();
		// 执行抽奖逻辑
		for (int i = 1; i <= searchTimes; i++) {
			AwardItems awards = new AwardItems();
			// 抽奖总次数
			int totalTimes = status.getTotalTimes();
			// 掉落物品
			List<ItemInfo> searchItems = ObonDropCfg.obonDropItem(totalTimes + 1);
			awards.addItemInfos(searchItems);
			// 处理翻倍
			//int multiple = status.canUseBuff() ? status.getMultiple() : 1;
			int multiple =1;
			multiple = Math.max(multiple, 1);
			multiples.add(multiple);
			for (Item item : awards.getAwardItems()) {
				item.count = item.count * multiple;
			}
			awardsList.add(awards);
			// 用完buff需要更新status
			status.setMultiple(1);
			status.setBuffOverTime(0);
			status.setTotalTimes(totalTimes + 1);
			int currentStage = status.getCurrentStage();
			int currentProgress = status.getProgress(currentStage);
			currentProgress += 1;
			status.setCurrentProgress(currentStage, currentProgress);
			// 随机下次是否获得buff
			if (canGetBuff(currentStage)) {
				int buffId = ObonBuffCfg.randomItemId();
				ObonBuffCfg buffCfg = ConfigManager.getInstance().getConfigByKey(ObonBuffCfg.class, buffId);
				if (buffCfg != null) {
					int nextMultiple = buffCfg.getMultiple();
					nextMultiple = Math.max(nextMultiple, 1);
					status.setMultiple(nextMultiple);
					int nextOverTime = ObonManager.hourToSec(buffCfg.getOverTime()) + currentTime;
					status.setBuffOverTime(nextOverTime);
				} else {
					Log.errPrintln(String.format("%s: %s=%s", "obonBuff.xml buffId not found.", "buffId", buffId));
				}
			}
			// 该阶段完成，如果是最大阶段的最大进度，需要将进度置为0
			ObonStageCfg cfg = ObonStageCfg.stageMap.get(currentStage);
			if (status.stageCompleted(currentStage)) {
				// 当当前进度大于总进度的时候说明是最后一个阶段的
				if (currentProgress == cfg.getTotalProgress())
					status.setCanGetGift(true, currentStage);
				if (currentStage == getMaxStage()) {
					status.setCurrentProgress(currentStage, cfg.getTotalProgress());
				}
				BehaviorLogger.log4Platform(player, Action.OBON, Params.valueOf("type", "completed"), Params.valueOf("stage", currentStage), Params.valueOf("awards", awards.toString()));
			}
			// 奖励物品
			awards.rewardTakeAffectAndPush(player, Action.OBON, 0);
		}
		// 更新玩家的活动数据
		player.getPlayerData().updateActivity(activityId, timeCfg.getStageId());
		// 返回消息
		ObonRet.Builder builder = ObonManager.getBuilder(player);
		for (AwardItems item : awardsList) {
			builder.addReward(item.toString());
		}
		for (Integer times : multiples) {
			builder.addRewardMultiple(times.intValue());
		}

		player.sendProtocol(Protocol.valueOf(HP.code.OBON_S_VALUE, builder));
		BehaviorLogger.log4Platform(player, Action.OBON, Params.valueOf("type", "draw"), Params.valueOf("times", searchTimes), Params.valueOf("awards", awardsList));
		return 0;
	}

	/**
	 * 领取礼包
	 */
	static int getGift(Player player, ActivityTimeCfg timeCfg, ObonStatus status, int stage) {
		int activityId = Const.ActivityId.OBON_VALUE;

		// 如果当前阶段的进度到达了最大值,才可以领取宝箱
		String reward = ObonManager.getGift(status, stage);
		if (null == reward) {
			return Status.error.PARAMS_INVALID_VALUE;
		}

		// 发奖
		AwardItems awards = AwardItems.valueOf(reward);
		if (null == awards) {
			return Status.error.CONFIG_ERROR_VALUE;
		}
		// 发送邮件给公会成员,如果没有参加公会,只通过邮件给自己发
		int allianceId = player.getPlayerData().getPlayerAllianceEntity().getAllianceId();
		if (allianceId == 0) {
			int playerId = player.getId();
			MailManager.createMail(player.getId(), Mail.MailType.Reward_VALUE, GsConst.MailId.OBON_GIFT_SELF, "", awards, player.getName(), String.valueOf(stage));
			Log.logPrintln(String.format("[ObonGiftMail]Player send email to self ,playerId:[%d]", playerId));
		} else {
			AllianceEntity allianceEntity = AllianceManager.getInstance().getAlliance(allianceId);
			if (allianceEntity == null) {
				return Status.error.ALLIANCE_NONEXISTENT_VALUE;
			}
			Set<Integer> memberSet = allianceEntity.getMemberList();
			for (Integer playerId : memberSet) {
				MailManager.createMail(playerId, Mail.MailType.Reward_VALUE, GsConst.MailId.OBON_GIFT_ALLIANCE, "", awards, player.getName(), String.valueOf(stage));
				Log.logPrintln(String.format("[ObonGiftMail]Player send email to members ,sender:[%d], receiver:[%d]", player.getId(), playerId));
			}
		}

		// 更新活动数据
		status.setCanGetGift(false, stage);
		player.getPlayerData().updateActivity(activityId, timeCfg.getStageId());

		// 返回消息包
		ObonRet.Builder builder = ObonManager.getBuilder(player);
		player.sendProtocol(Protocol.valueOf(HP.code.OBON_S, builder));
		return 0;
	}

	/**
	 * 将小时转化为秒
	 */
	public static int hourToSec(int hour) {
		return hour * 3600;
	}

	/**
	 * 生成同步信息
	 */
	static ObonRet.Builder getBuilder(Player player) {
		int activityId = Const.ActivityId.OBON_VALUE;
		int currentTime = GuaJiTime.getSeconds();
		ObonTimesCfg timesCfg = ObonTimesCfg.getTimesCfgByVipLevel(player.getVipLevel());
		ActivityTimeCfg activityTimeCfg = ActivityUtil.getCurActivityTimeCfg(activityId);
		ObonStatus status = ActivityUtil.getActivityStatus(player.getPlayerData(), activityId, activityTimeCfg.getStageId(), ObonStatus.class);
		// 距离下次免费倒计时
		int lastFreeTime = status.getLastFreeTime();
		int freeCD = Math.max(hourToSec(timesCfg.getFreeCountDown()) - (currentTime - lastFreeTime), 0);
		// 距离倍数失效倒计时
		int singleCost = timesCfg.getSingleCost();
		int tenCost = timesCfg.getTenCost();
		// 总次数
		int totalTimes = status.getTotalTimes();
		int genLeftTimes = ObonDropCfg.getLimitTimes(totalTimes);
		int tenLeftTimes = (totalTimes / 10 + 1) * 10;
		if (genLeftTimes == -1) {
			genLeftTimes = tenLeftTimes;
		}
		int leftLimitTimes = Math.min(genLeftTimes - totalTimes, tenLeftTimes - totalTimes);
		ObonRet.Builder builder = ObonRet.newBuilder();
		builder.setLeftTime(activityTimeCfg.calcActivitySurplusTime());
		int bufferOverTime = status.getBuffOverTime();
		builder.setLeftBuffTimes(Math.max(bufferOverTime - currentTime, 0));
		int multipTimes = status.getMultiple();
		builder.setBufMultiple(multipTimes);
		builder.setFreeCD(freeCD);
		builder.setLeftAwardTimes(leftLimitTimes);
		builder.setOnceCostGold(singleCost);
		builder.setTenCostGold(tenCost);
		builder.setCurrentStage(status.getCurrentStage());
		builder.addAllProgress(status.getAllProgress());
		builder.addAllCanGetGift(status.getAllGiftStatus());
		return builder;
	}

	/**
	 * 能不能领取礼包(如果返回null说明不能领取)
	 */
	public static String getGift(ObonStatus status, int stage) {
		ObonStageCfg cfg = ObonStageCfg.stageMap.get(stage);
		int total = cfg.getTotalProgress();
		int progress = status.getProgress(stage);
		if (total == progress && status.getCanGetGift(stage))
			return cfg.getStageReward();
		return null;
	}

	/**
	 * 获取最大阶段
	 */
	public static int getMaxStage() {
		Set<Integer> set = ObonStageCfg.stageMap.keySet();
		int max = 0;
		for (Integer stage : set) {
			if (stage >= max)
				max = stage;
		}
		return max;
	}

	/**
	 * 是否可以获得buff
	 */
	public static boolean canGetBuff(int stage) {
		ObonStageCfg cfg = ObonStageCfg.stageMap.get(stage);
		int rate = cfg.getLuckyRate();
		return GuaJiRand.randPercentRate(rate / 100);
	}

}
