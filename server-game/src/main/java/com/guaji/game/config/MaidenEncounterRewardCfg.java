package com.guaji.game.config;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.guaji.config.ConfigBase;
import org.guaji.config.ConfigManager;

/**
 * 奖励随机规则：
 * 	1、互动奖励：每完成一小步从2奖励池里面随机一个奖励
 *  2、阶段奖励：完成当前阶段，从1奖励池里面拿出当前stage对应的奖励，同时有小概率获得额外的专属奖励
 *  3、专属奖励：完成当前阶段并且当前阶段是最大阶段，给予专属奖励+互动奖励
 *
 */
@ConfigManager.XmlResource(file = "xml/maidenEncounterReward.xml", struct = "list")
public class MaidenEncounterRewardCfg extends ConfigBase {

	public static final int REWARD_TYPE_STAGE = 1;// 阶段奖励
	public static final int REWARD_TYPE_INTERACT = 2;// 互动奖励

	@Id
	protected final int id;

	/**
	 * 奖励池
	 */
	protected final int poolId;

	/**
	 * 阶段
	 */
	protected final int stage;

	/**
	 * 总进度
	 */
	protected final int totalProgress;

	/**
	 * 奖励
	 */
	protected final String rewards;

	/**
	 * 奖励概率
	 */
	protected final int drawRate;

	/**
	 * 将不同奖池的奖励归类
	 */
	public static Map<Integer, List<MaidenEncounterRewardCfg>> rewardMap = new HashMap<Integer, List<MaidenEncounterRewardCfg>>();

	/**
	 * 阶段和总进度的对应关系
	 */
	public static Map<Integer, Integer> stage2totalProgressMap = new HashMap<>();
	
	/**
	 * 阶段和奖励对应
	 */
	public static Map<Integer, String> stage2rewardMap = new HashMap<>();

	/**
	 * 阶段集合
	 */
	public static List<Integer> stageList = new ArrayList<Integer>();

	public MaidenEncounterRewardCfg() {
		this.id = 0;
		this.poolId = 0;
		this.stage = 0;
		this.totalProgress = 0;
		this.rewards = "";
		this.drawRate = 0;
	}

	@Override
	protected boolean assemble() {
		List<MaidenEncounterRewardCfg> list;
		if (rewardMap.containsKey(this.poolId)) {
			list = rewardMap.get(this.poolId);
		} else {
			list = new ArrayList<MaidenEncounterRewardCfg>();
		}
		list.add(this);
		rewardMap.put(this.poolId, list);
		if (this.poolId == REWARD_TYPE_STAGE) {
			stage2totalProgressMap.put(this.stage, this.totalProgress);
			stage2rewardMap.put(this.stage, this.rewards);
		}
		if (this.stage > 0) {
			stageList.add(this.stage);
		}
		return super.assemble();
	}

	public int getId() {
		return id;
	}

	public int getPoolId() {
		return poolId;
	}

	public int getStage() {
		return stage;
	}

	public int getTotalProgress() {
		return totalProgress;
	}

	public String getRewards() {
		return rewards;
	}

	public int getDrawRate() {
		return drawRate;
	}

	/**
	 * 判断是不是最后一个阶段
	 */
	public static boolean isMaxStage(int stage) {
		for (Integer var : stageList) {
			if (stage < var)
				return false;
		}
		return true;
	}
	


}
