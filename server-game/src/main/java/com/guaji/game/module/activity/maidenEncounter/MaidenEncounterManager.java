package com.guaji.game.module.activity.maidenEncounter;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.guaji.config.ConfigManager;
import org.guaji.net.protocol.Protocol;
import org.guaji.os.GuaJiRand;
import org.guaji.os.GuaJiTime;

import com.guaji.game.config.ActivityTimeCfg;
import com.guaji.game.config.MaidenEncounterCfg;
import com.guaji.game.config.MaidenEncounterExchangeCfg;
import com.guaji.game.config.MaidenEncounterRewardCfg;
import com.guaji.game.config.SysBasicCfg;
import com.guaji.game.entity.MaidenEncounterEntity;
import com.guaji.game.item.AwardItems;
import com.guaji.game.item.ConsumeItems;
import com.guaji.game.item.ItemInfo;
import com.guaji.game.log.BehaviorLogger;
import com.guaji.game.log.BehaviorLogger.Action;
import com.guaji.game.log.BehaviorLogger.Params;
import com.guaji.game.log.BehaviorLogger.Source;
import com.guaji.game.player.Player;
import com.guaji.game.util.ActivityUtil;
import com.guaji.game.protocol.Const;
import com.guaji.game.protocol.HP;
import com.guaji.game.protocol.Status;
import com.guaji.game.protocol.Activity2.SyncMaidenEncounterRes;
import com.guaji.game.protocol.Const.changeType;

/**
 * 少女的邂逅管理类
 */
public class MaidenEncounterManager {

	/**
	 * 初始化，随机一个少女
	 */
	public static void init(MaidenEncounterEntity entity) {
		initMaidenStage(entity);
		initProgress(entity);
		initExchange(entity);
		// 初始一个少女
		MaidenEncounterCfg maidenCfg = MaidenEncounterCfg.getRandomMaiden(entity);
		entity.setCurrentIndex(maidenCfg.getId());
		entity.setHistoryRandomTimes(1);

		// 初始免费次数
		int freeInteractTime = SysBasicCfg.getInstance().getFreeInteractTimes();
		int freeRefreshTime = SysBasicCfg.getInstance().getFreeRefreshTimes();
		entity.setSurplusFreeInteractTimes(freeInteractTime);
		entity.setSurplusFreeRefreshTimes(freeRefreshTime);

		// 更新少女阶段信息
		Map<Integer, Integer> maidenStageMap = entity.getMaidenStageMap();
		maidenStageMap.put(maidenCfg.getId(), 1);
		entity.setMaidenStageMap(maidenStageMap);
	}

	private static void initMaidenStage(MaidenEncounterEntity entity) {
		Map<Integer, Integer> maidenStageMap = new HashMap<Integer, Integer>();
		Collection<MaidenEncounterCfg> stageCollection = MaidenEncounterCfg.maidenMap.values();
		for (MaidenEncounterCfg cfg : stageCollection) {
			maidenStageMap.put(cfg.getId(), 0);
		}
		entity.setMaidenStageMap(maidenStageMap);
	}

	private static void initProgress(MaidenEncounterEntity entity) {
		Map<Integer, Integer> progressMap = new HashMap<Integer, Integer>();
		Collection<MaidenEncounterCfg> progressCollection = MaidenEncounterCfg.maidenMap.values();
		for (MaidenEncounterCfg cfg : progressCollection) {
			progressMap.put(cfg.getId(), 0);
		}
		entity.setProgressMap(progressMap);
	}

	public static void initExchange(MaidenEncounterEntity entity) {
		Map<Integer, Integer> exchangeMap = new HashMap<Integer, Integer>();
		Collection<MaidenEncounterExchangeCfg> configList = ConfigManager.getInstance().getConfigMap(MaidenEncounterExchangeCfg.class).values();
		for (MaidenEncounterExchangeCfg exchangeCfg : configList) {
			exchangeMap.put(exchangeCfg.getId(), exchangeCfg.getLimitTimes());
		}
		entity.setExchangeMap(exchangeMap);
	}

	/**
	 * 同步
	 */
	static void sync(Player player, MaidenEncounterEntity entity) {
		// 如果没有初始化，则需要随机一个少女，并直接返回
		int index = entity.getCurrentIndex();
		MaidenEncounterCfg maidenCfg = ConfigManager.getInstance().getConfigByIndex(MaidenEncounterCfg.class, index - 1);
		if (null == maidenCfg) {
			player.sendError(HP.code.SYNC_MAIDEN_ENCOUNTER_C_VALUE, Status.error.PARAMS_INVALID_VALUE);
			return;
		}
		// 如果当前是恶魔
		if (maidenCfg.isDevil()) {
			int now = (int) (System.currentTimeMillis() / 1000);
			// 自动离开时间到了，无消耗刷新
			if (now >= entity.getDevilRefreshTime()) {
				refreshWithoutConsume(player, entity);
			}
		}
		responseMaidenEncounterInfo(player, entity);
	}

	/**
	 * 互动(对这个消耗处理方式不太满意,暂时没有想到更好的)
	 */
	static void interact(Player player, MaidenEncounterEntity entity) {
		int index = entity.getCurrentIndex();
		MaidenEncounterCfg maidenCfg = ConfigManager.getInstance().getConfigByIndex(MaidenEncounterCfg.class, index - 1);
		if (null == maidenCfg) {
			player.sendError(HP.code.SYNC_MAIDEN_ENCOUNTER_C_VALUE, Status.error.PARAMS_INVALID_VALUE);
			return;
		}
		// 是恶魔走驱赶
		if (maidenCfg.isDevil()) {
			expel(player, maidenCfg, entity);
			return;
		}
		// 进度到达100%不可互动
		Map<Integer, Integer> progressMap = entity.getProgressMap();
		int progress = progressMap.get(index) + 1;
		// 当前的阶段，和随机到的次数是相等的
		Integer stage = entity.getMaidenStageMap().get(index);
		Map<Integer, Integer> totalProgressMap = MaidenEncounterRewardCfg.stage2totalProgressMap;
		int totalProgress = totalProgressMap.get(stage);
		if (progress > totalProgress) {
			player.sendError(HP.code.SYNC_MAIDEN_ENCOUNTER_C_VALUE, Status.error.PARAMS_INVALID_VALUE);
			return;
		}
		boolean isConsumed = false;
		// 消耗免费互动次数
		int surplusFreeInteractTimes = entity.getSurplusFreeInteractTimes();
		if (surplusFreeInteractTimes > 0) {
			entity.setSurplusFreeInteractTimes(surplusFreeInteractTimes - 1);
			isConsumed = true;
		}
		// 消耗道具
		if (!isConsumed) {
			ConsumeItems consumeItems = ConsumeItems.valueOf();
			String itemInfoString = maidenCfg.getInteractCostItems();
			if (consumeItems.addConsumeInfo(player.getPlayerData(), ItemInfo.valueListOf(itemInfoString))) {
				if (consumeItems.checkConsume(player, HP.code.SYNC_MAIDEN_ENCOUNTER_C_VALUE)) {
					consumeItems.consumeTakeAffect(player, Action.MAIDEN_ENCOUNTER);
					isConsumed = true;
				}
			}
		}

		// 消耗钻石
		if (!isConsumed) {
			int costGold = maidenCfg.getInteractCostGold();
			if (player.getGold() < costGold) {
				player.sendError(HP.code.SYNC_MAIDEN_ENCOUNTER_C_VALUE, Status.error.GOLD_NOT_ENOUGH_VALUE);
				return;
			}
			player.consumeGold(costGold, Action.MAIDEN_ENCOUNTER);
			ConsumeItems.valueOf(changeType.CHANGE_GOLD, costGold).pushChange(player);
		}
		// 发奖
		reward(player, entity, 1);
		// 更新数据
		progressMap.put(index, progress);
		entity.setProgressMap(progressMap);
		// 如果是完成当前阶段、进行刷新
		if (progress == totalProgress) {
			refreshWithoutConsume(player, entity);
		}
		responseMaidenEncounterInfo(player, entity);
	}

	/**
	 * 驱赶
	 */
	private static void expel(Player player, MaidenEncounterCfg maidenCfg, MaidenEncounterEntity entity) {
		boolean isConsumed = false;
		// 消耗道具
		ConsumeItems consumeItems = ConsumeItems.valueOf();
		String itemInfoString = maidenCfg.getInteractCostItems();
		if (consumeItems.addConsumeInfo(player.getPlayerData(), ItemInfo.valueListOf(itemInfoString))) {
			if (consumeItems.checkConsume(player, HP.code.SYNC_MAIDEN_ENCOUNTER_C_VALUE)) {
				consumeItems.consumeTakeAffect(player, Action.MAIDEN_ENCOUNTER);
				isConsumed = true;
			}
		}
		// 消耗钻石
		if (!isConsumed) {
			int costGold = maidenCfg.getInteractCostGold();
			if (player.getGold() < costGold) {
				player.sendError(HP.code.SYNC_MAIDEN_ENCOUNTER_C_VALUE, Status.error.GOLD_NOT_ENOUGH_VALUE);
				return;
			}
			player.consumeGold(costGold, Action.MAIDEN_ENCOUNTER);
			ConsumeItems.valueOf(changeType.CHANGE_GOLD, costGold).pushChange(player);
		}
		refreshWithoutConsume(player, entity);
		responseMaidenEncounterInfo(player, entity);
	}

	/**
	 * 加满
	 */
	static void full(Player player, MaidenEncounterEntity entity) {
		int index = entity.getCurrentIndex();
		MaidenEncounterCfg maidenCfg = ConfigManager.getInstance().getConfigByIndex(MaidenEncounterCfg.class, index - 1);
		if (null == maidenCfg) {
			player.sendError(HP.code.SYNC_MAIDEN_ENCOUNTER_C_VALUE, Status.error.PARAMS_INVALID_VALUE);
			return;
		}
		// 判断进度是不是满的
		Map<Integer, Integer> totalProgressMap = MaidenEncounterRewardCfg.stage2totalProgressMap;
		int stage = entity.getMaidenStageMap().get(index);
		int totalProgress = totalProgressMap.get(stage);
		Map<Integer, Integer> progressMap = entity.getProgressMap();
		int progress = progressMap.get(index);
		if (progress >= totalProgress) {
			player.sendError(HP.code.SYNC_MAIDEN_ENCOUNTER_C_VALUE, Status.error.PARAMS_INVALID_VALUE);
			return;
		}
		// 判断钻石
		int costGold = maidenCfg.getInteractCostGold() * (totalProgress - progress);
		if (player.getGold() < costGold) {
			player.sendError(HP.code.SYNC_MAIDEN_ENCOUNTER_C_VALUE, Status.error.GOLD_NOT_ENOUGH_VALUE);
			return;
		}
		player.consumeGold(costGold, Action.MAIDEN_ENCOUNTER);
		ConsumeItems.valueOf(changeType.CHANGE_GOLD, costGold).pushChange(player);
		// 发阶段奖励
		reward(player, entity, totalProgress - progress);
		// 更新数据
		progressMap.put(index, totalProgress);
		entity.setProgressMap(progressMap);
		// 无消耗自动刷新
		refreshWithoutConsume(player, entity);
		// 返回
		responseMaidenEncounterInfo(player, entity);
	}

	/**
	 * 刷新
	 */
	static void refresh(Player player, MaidenEncounterEntity entity) {
		int index = entity.getCurrentIndex();
		MaidenEncounterCfg maidenCfg = ConfigManager.getInstance().getConfigByIndex(MaidenEncounterCfg.class, index - 1);
		if (null == maidenCfg) {
			player.sendError(HP.code.SYNC_MAIDEN_ENCOUNTER_C_VALUE, Status.error.PARAMS_INVALID_VALUE);
			return;
		}
		boolean isConsumed = false;
		// 消耗免费次数
		int surplusFreeRefreshTimes = entity.getSurplusFreeRefreshTimes();
		if (surplusFreeRefreshTimes > 0) {
			entity.setSurplusFreeRefreshTimes(surplusFreeRefreshTimes - 1);
			isConsumed = true;
		}
		// 消耗钻石
		if (!isConsumed) {
			int costGold = maidenCfg.getRefreshGold();
			if (player.getGold() < costGold) {
				player.sendError(HP.code.SYNC_MAIDEN_ENCOUNTER_C_VALUE, Status.error.GOLD_NOT_ENOUGH_VALUE);
				return;
			}
			player.consumeGold(costGold, Action.MAIDEN_ENCOUNTER);
			ConsumeItems.valueOf(changeType.CHANGE_GOLD, costGold).pushChange(player);
			// 日志统计
			BehaviorLogger.log4Service(player, Source.USER_OPERATION, Action.MAIDEN_ENCOUNTER, Params.valueOf("type", "refresh"), Params.valueOf("playerAttr", Const.playerAttr.GOLD_VALUE), Params.valueOf("sub", costGold), Params.valueOf("after", player.getGold()));
			BehaviorLogger.log4Platform(player, Action.MAIDEN_ENCOUNTER, Params.valueOf("type", "refresh"), Params.valueOf("playerAttr", Const.playerAttr.GOLD_VALUE), Params.valueOf("sub", costGold), Params.valueOf("after", player.getGold()));
		}
		refreshWithoutConsume(player, entity);
		// 返回
		responseMaidenEncounterInfo(player, entity);
	}

	/**
	 * 无消耗的刷新(如果少女当前的进度没有完成，下次随到该少女的时候阶段还是不变但是进度归零)
	 * 填满时的刷新默认进入该少女的下一个阶段，如果当前阶段为最终阶段，则下次重新随机
	 */
	private static void refreshWithoutConsume(Player player, MaidenEncounterEntity entity) {
		// 先获取当前的处理对象
		int index = entity.getCurrentIndex();
		MaidenEncounterCfg maidenCfg = ConfigManager.getInstance().getConfigByIndex(MaidenEncounterCfg.class, index - 1);
		if (null == maidenCfg) {
			player.sendError(HP.code.SYNC_MAIDEN_ENCOUNTER_C_VALUE, Status.error.PARAMS_INVALID_VALUE);
			return;
		}
		Map<Integer, Integer> maidenStageMap = entity.getMaidenStageMap();
		Map<Integer, Integer> progressMap = entity.getProgressMap();
		// 如果当前是少女，先判断少女是不是完成了非最大阶段
		boolean canRefresh = true;
		if (!maidenCfg.isDevil()) {
			int progress = progressMap.get(index);
			int stage = maidenStageMap.get(index);
			int totalProgress = MaidenEncounterRewardCfg.stage2totalProgressMap.get(stage);
			// 直接进入下一阶段
			if (progress == totalProgress && !MaidenEncounterRewardCfg.isMaxStage(stage)) {
				maidenStageMap.put(index, stage + 1);
				entity.setMaidenStageMap(maidenStageMap);
				progressMap.put(index, 0);
				entity.setProgressMap(progressMap);
				canRefresh = false;
			}
		}

		if (canRefresh) {
			// 进行随机
			MaidenEncounterCfg newObj = MaidenEncounterCfg.getRandomRefresh(entity);
			if (null == newObj) {
				player.sendError(HP.code.SYNC_MAIDEN_ENCOUNTER_C_VALUE, Status.error.PARAMS_INVALID_VALUE);
				return;
			}
			index = newObj.getId();
			entity.setCurrentIndex(index);
			int historyRandomTimes = entity.getHistoryRandomTimes();
			entity.setHistoryRandomTimes(historyRandomTimes + 1);
			if (newObj.isDevil()) {
				int devilRefreshTime = newObj.getAutoLeaveTime();
				int now = GuaJiTime.getSeconds();
				entity.setDevilRefreshTime(devilRefreshTime + now);
			} else {
				int progress = progressMap.get(index);
				int stage = maidenStageMap.get(index);
				if (stage <= 0) {
					// 如果第一次随到该少女自然就是1
					stage = 1;
				} else {
					// 不是第一次随到,则需要判断少女的上次进度,如果上次进度没完成则progress归零,阶段不会变化
					int totalProgress = MaidenEncounterRewardCfg.stage2totalProgressMap.get(stage);
					if (totalProgress == progress) {
						// 是最大阶段那么需要将阶段置为初始值
						if (MaidenEncounterRewardCfg.isMaxStage(stage)) {
							stage = 1;
						} else {
							stage += 1;
						}
					}
				}
				maidenStageMap.put(index, stage);
				entity.setMaidenStageMap(maidenStageMap);
				progressMap.put(index, 0);
				entity.setProgressMap(progressMap);
			}
		}

	}

	/**
	 * 基础奖励
	 */
	private static AwardItems baseReward(Player player, MaidenEncounterEntity entity, int multiple) {
		List<MaidenEncounterRewardCfg> rewardList = MaidenEncounterRewardCfg.rewardMap.get(MaidenEncounterRewardCfg.REWARD_TYPE_INTERACT);
		List<Integer> weightList = new ArrayList<Integer>();
		for (MaidenEncounterRewardCfg maidenEncounterRewardCfg : rewardList) {
			weightList.add(maidenEncounterRewardCfg.getDrawRate());
		}
		AwardItems awardItems = new AwardItems();
		while (multiple-- > 0) {
			MaidenEncounterRewardCfg rewardCfg = GuaJiRand.randonWeightObject(rewardList, weightList);
			String rewards = rewardCfg.getRewards();
			awardItems.addItem(ItemInfo.valueOf(rewards));
		}
		return awardItems;
	}

	/**
	 * 发奖
	 */
	private static void reward(Player player, MaidenEncounterEntity entity, int multiple) {
		int index = entity.getCurrentIndex();
		int step = entity.getMaidenStageMap().get(index);
		int totalProgress = MaidenEncounterRewardCfg.stage2totalProgressMap.get(step);
		MaidenEncounterCfg maidenCfg = ConfigManager.getInstance().getConfigByIndex(MaidenEncounterCfg.class, index - 1);
		if (null == maidenCfg) {
			player.sendError(HP.code.SYNC_MAIDEN_ENCOUNTER_C_VALUE, Status.error.PARAMS_INVALID_VALUE);
			return;
		}
		// 互动一次给的小奖励
		AwardItems awardItems = new AwardItems();
		int progress = entity.getProgressMap().get(index) + multiple;
		if (progress == totalProgress) {
			// 先获取自己的阶段奖励
			String stepReward = MaidenEncounterRewardCfg.stage2rewardMap.get(step);
			awardItems.appendAward(AwardItems.valueOf(stepReward));
			// 有一定概率掉落专属奖励
			if (MaidenEncounterCfg.randomExtraExclusiveReward(index, step)) {
				String exclusiveReward = maidenCfg.getExclusiveReward();
				List<ItemInfo> exclusiveRewardList = ItemInfo.valueListOf(exclusiveReward);
				awardItems.addItemInfos(exclusiveRewardList);
			}
		}
		AwardItems baseAwardItems = baseReward(player, entity, multiple);
		awardItems.appendAward(baseAwardItems);
		awardItems.rewardTakeAffectAndPush(player, Action.MAIDEN_ENCOUNTER, 2);
	}

	/**
	 * 返回活动信息
	 */
	static void responseMaidenEncounterInfo(Player player, MaidenEncounterEntity entity) {
		entity.notifyUpdate();
		SyncMaidenEncounterRes.Builder response = SyncMaidenEncounterRes.newBuilder();
		ActivityTimeCfg timeCfg = ActivityUtil.getCurActivityTimeCfg(Const.ActivityId.MAIDEN_ENCOUNTER_VALUE);
		if (timeCfg == null) {
			player.sendError(HP.code.SYNC_MAIDEN_ENCOUNTER_C_VALUE, Status.error.ACTIVITY_CLOSE);
			return;
		}
		int activitySurplusTime = timeCfg.calcActivitySurplusTime();
		if (activitySurplusTime == 0) {
			response.setIsShow(true);
			response.setRemainderTime(timeCfg.calcActivityCloseTime());
		} else {
			response.setRemainderTime(activitySurplusTime);
			response.setIsShow(false);
		}
		int index = entity.getCurrentIndex();
		MaidenEncounterCfg maidenCfg = ConfigManager.getInstance().getConfigByIndex(MaidenEncounterCfg.class, index - 1);
		if (!maidenCfg.isDevil()) {
			int stage = entity.getMaidenStageMap().get(index);
			response.setStage(stage);
			response.setProgress(entity.getProgressMap().get(index));
		} else {
			int now = (int) (System.currentTimeMillis() / 1000);
			response.setDevilRefreshTime(entity.getDevilRefreshTime() - now);
		}
		response.setId(index);
		response.setFreeInteractTimes(entity.getSurplusFreeInteractTimes());
		response.setFreeRefreshTimes(entity.getSurplusFreeRefreshTimes());
		player.sendProtocol(Protocol.valueOf(HP.code.SYNC_MAIDEN_ENCOUNTER_S_VALUE, response));
	}
}
