package com.guaji.game.config;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.Transient;

import org.apache.commons.lang.StringUtils;
import org.guaji.config.ConfigBase;
import org.guaji.config.ConfigManager;
import org.guaji.os.GuaJiRand;

@ConfigManager.XmlResource(file = "xml/fairyBless.xml", struct = "map")
public class FairyBlessCfg extends ConfigBase {

	/**
	 * 索引ID
	 */
	@Id
	private final int id;
	/**
	 * 活动类型
	 */
	private final int type;
	/**
	 * 奖励物品
	 */
	private final String rewards;
	/**
	 * 权重
	 */
	private final String weight;
	/**
	 * 每次祈祷消耗的花
	 */
	private final int costFlower;
	/**
	 * 总进度
	 */
	private final int totalProgress;
	/**
	 * 奖池
	 */
	@Transient
	public static Map<Integer, FairyBlessCfg> typeMap = new HashMap<>();

	@Transient
	public static int lowestLevelFlower = Integer.MAX_VALUE;

	public FairyBlessCfg() {
		id = 0;
		type = 0;
		rewards = "";
		weight = "";
		costFlower = 0;
		totalProgress = 0;
	}

	public int getId() {
		return id;
	}

	public int getType() {
		return type;
	}

	public String getRewards() {
		return rewards;
	}

	public String getWeight() {
		return weight;
	}

	public int getCostFlower() {
		return costFlower;
	}

	public int getTotalProgress() {
		return totalProgress;
	}

	/**
	 * 清理相关静态数据
	 */
	protected void clearStaticData() {
		typeMap.clear();
	}

	/**
	 * 随机该类型奖励
	 */
	public String getRandmReward(int type) {
		FairyBlessCfg cfg = typeMap.get(type);
		List<String> rewardList = new ArrayList<>();
		List<Integer> weightList = new ArrayList<>();

		String reward = cfg.getRewards();
		String weight = cfg.getWeight();
		if (StringUtils.isNotBlank(reward) && StringUtils.isNotBlank(weight)) {
			String[] rewards = StringUtils.split(reward, ";");
			rewardList.addAll(Arrays.asList(rewards));
			weightList = covertString2List(weight);
			if (rewardList.size() != weightList.size()) {
				return null;
			}
		}
		return GuaJiRand.randonWeightObject(rewardList, weightList);
	}
	
	//将String数组转化为Integer数组
	private static List<Integer> covertString2List(String string){
		List<Integer> weightList = new ArrayList<>();
		String[] weights = StringUtils.split(string, ",");
		for(String weight:weights){
			weightList.add(Integer.valueOf(weight));
		}
		return weightList;
	}

	@Override
	protected boolean assemble() {
		typeMap.put(this.type, this);
		if (this.costFlower < lowestLevelFlower)
			lowestLevelFlower = this.costFlower;
		return true;
	}

	@Override
	protected boolean checkValid() {
		return true;
	}

}
