package com.guaji.game.config;

import java.util.ArrayList;
import java.util.List;

import org.guaji.config.ConfigBase;
import org.guaji.config.ConfigManager;

/**
 * 捞金鱼功能配置
 * 
 */
@ConfigManager.XmlResource(file = "xml/goldfishFeatures.xml", struct = "list")
public class GoldfishFeaturesCfg extends ConfigBase {

	/**
	 * 免费次数
	 */
	private final int freeTimes;

	/**
	 * 单次捞鱼消耗
	 */
	private final int singleCost;

	/**
	 * 连续捞鱼消耗
	 */
	private final int continuousCost;

	/**
	 * 捞鱼概率
	 */
	private final int fishingWeight;

	/**
	 * 捞鱼总概率
	 */
	private final int totalWeight;

	/**
	 * 失败后获得积分
	 */
	private final int failScore;

	/**
	 * 奖池概率配置
	 */
	private final String poolWeight;

	/**
	 * 十连额外获得奖励配置
	 */
	private final String additional;

	/**
	 * 积分翻倍开始时间
	 */
	private final int startTime;

	/**
	 * 积分翻倍结束时间
	 */
	private final int endTime;

	/**
	 * 排行榜上榜个数
	 */
	private final int rankNumber;

	/**
	 * 奖池总概率
	 */
	private int poolTotalWeight;

	/**
	 * 额外获得总概率
	 */
	private int additionalTotalWeight;

	/**
	 * 奖池概率组
	 */
	private List<SingleWeightMessage> poolWeightList = new ArrayList<SingleWeightMessage>();

	/**
	 * 额外获得总概率组
	 */
	private List<SingleWeightMessage> additionalList = new ArrayList<SingleWeightMessage>();

	
	public GoldfishFeaturesCfg() {

		this.freeTimes = 0;
		this.singleCost = 0;
		this.continuousCost = 0;
		this.fishingWeight = 0;
		this.totalWeight = 0;
		this.failScore = 0;
		this.poolWeight = "";
		this.additional = "";
		this.startTime = 0;
		this.endTime = 0;
		this.rankNumber = 0;
	}

	/**
	 * 数据格式转化
	 */
	@Override
	protected boolean assemble() {

		if (null != this.poolWeight) {
			String[] split_1 = this.poolWeight.split(",");
			for (String single : split_1) {
				String[] split_2 = single.split("_");
				int listId = Integer.parseInt(split_2[0]);
				int weight = Integer.parseInt(split_2[1]);
				this.poolTotalWeight += weight;
				SingleWeightMessage message = new SingleWeightMessage(listId, weight);
				this.poolWeightList.add(message);
			}
		}

		if (null != this.additional) {
			String[] split_1 = this.additional.split(",");
			for (String single : split_1) {
				String[] split_2 = single.split("_");
				int listId = Integer.parseInt(split_2[0]);
				int weight = Integer.parseInt(split_2[1]);
				this.additionalTotalWeight += weight;
				SingleWeightMessage message = new SingleWeightMessage(listId, weight);
				this.additionalList.add(message);
			}
		}
		return true;
	}

	/**
	 * 检测有消息
	 * 
	 * @return
	 */
	@Override
	protected boolean checkValid() {
		return true;
	}

	public int getFreeTimes() {
		return freeTimes;
	}

	public int getSingleCost() {
		return singleCost;
	}

	public int getContinuousCost() {
		return continuousCost;
	}

	public int getFishingWeight() {
		return fishingWeight;
	}

	public int getTotalWeight() {
		return totalWeight;
	}

	public int getFailScore() {
		return failScore;
	}

	public String getPoolWeight() {
		return poolWeight;
	}

	public String getAdditional() {
		return additional;
	}

	public int getStartTime() {
		return startTime;
	}

	public int getEndTime() {
		return endTime;
	}

	public int getRankNumber() {
		return rankNumber;
	}

	public int getPoolTotalWeight() {
		return poolTotalWeight;
	}

	public int getAdditionalTotalWeight() {
		return additionalTotalWeight;
	}

	public List<SingleWeightMessage> getPoolWeightList() {
		return poolWeightList;
	}

	public List<SingleWeightMessage> getAdditionalList() {
		return additionalList;
	}

	/**
	 * 清理相关静态数据
	 */
	protected void clearStaticData() {
	}

	/**
	 * 单项权重信息
	 */
	public static class SingleWeightMessage {

		/**
		 * 分组类型ID
		 */
		private int listId;

		/**
		 * 单项权重值
		 */
		private int weight;

		public SingleWeightMessage(int listId, int weight) {

			this.listId = listId;
			this.weight = weight;
		}

		public int getListId() {
			return listId;
		}

		public int getWeight() {
			return weight;
		}

	}
}
