package com.guaji.game.config;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;

import org.guaji.config.ConfigBase;
import org.guaji.config.ConfigManager;

/**
 * 神装锻造功能配置
 */
@ConfigManager.XmlResource(file = "xml/forgingFeatures.xml", struct = "list")
public class ForgingFeaturesCfg extends ConfigBase {

	/**
	 * 单次消耗
	 */
	private final int singleCost;

	/**
	 * 连续消耗
	 */
	private final int continuousCost;

	/**
	 * 再次免费时间间隔
	 */
	private final int interval;

	/**
	 * 累计N次抽奖获得指定奖励ID
	 */
	private final String givenRaward;

	/**
	 * 普通抽奖规则
	 */
	private final String prizeRule;
	
	/**
	 * 全服出现个数限制
	 */
	private final String limits;

	/**
	 * 累计N次抽奖转换<key:累计抽奖次数, value:奖励ID>
	 */
	private Map<Integer, Integer> givenMap = new HashMap<Integer, Integer>();

	/**
	 * 抽奖规则转换<key:奖励池ID, value:抽奖总次数>
	 */
	private Map<Integer, Integer> ruleMap = new TreeMap<Integer, Integer>();
	
	/**
	 * 全服出现个数限制转换<key:物品信息, value:出现次数>
	 */
	private Map<String, Integer> limitMap = new HashMap<String, Integer>();

	public ForgingFeaturesCfg() {

		this.singleCost = 0;
		this.continuousCost = 0;
		this.interval = 0;
		this.givenRaward = null;
		this.prizeRule = null;
		this.limits = null;
	}

	/**
	 * 数据格式转化
	 */
	@Override
	protected boolean assemble() {
		//givenRaward="4000_18"
		if (null != this.givenRaward) {
			String[] split_1 = this.givenRaward.split(",");
			for (String single : split_1) {
				String[] split_2 = single.split("_");
				int given = Integer.parseInt(split_2[0]);
				int rawardId = Integer.parseInt(split_2[1]);
				this.givenMap.put(given, rawardId);
			}
		}
		//prizeRule="1_31,2_91,3_121,4_161"
		if (null != this.prizeRule) {
			String[] split_1 = this.prizeRule.split(",");
			for (String single : split_1) {
				String[] split_2 = single.split("_");
				int poolId = Integer.parseInt(split_2[0]);
				int times = Integer.parseInt(split_2[1]);
				this.ruleMap.put(poolId, times);
			}
		}
		//limits="30000_2500001_1-5"
		if (null != this.limits) {
			String[] split_1 = this.limits.split(",");
			for (String single : split_1) {
				String[] split_2 = single.split("-");
				int times = Integer.parseInt(split_2[1]);
				this.limitMap.put(split_2[0], times);
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

	public int getSingleCost() {
		return singleCost;
	}

	public int getContinuousCost() {
		return continuousCost;
	}

	public int getInterval() {
		return interval;
	}

	public String getGivenRaward() {
		return givenRaward;
	}

	public String getPrizeRule() {
		return prizeRule;
	}

	public String getLimits() {
		return limits;
	}

	public Map<Integer, Integer> getGivenMap() {
		return givenMap;
	}

	public Map<Integer, Integer> getRuleMap() {
		return ruleMap;
	}

	public Map<String, Integer> getLimitMap() {
		return limitMap;
	}

	/**
	 * 获得奖励ID
	 * 
	 * @param totalTimes
	 * @return
	 */
	public int getRawardId(int totalTimes) {

		Integer value = this.givenMap.get(totalTimes);
		if (null == value) {
			return 0;
		}
		return value;
	}

	/**
	 * 获取奖励池抽奖总次数
	 */
	public int getPoolId(int totalTimes) {
		Iterator<Entry<Integer, Integer>> iterator = this.ruleMap.entrySet().iterator();
		while (iterator.hasNext()) {
			Entry<Integer, Integer> entry = iterator.next();
			if (totalTimes < entry.getValue()) {
				return entry.getKey();
			} else {
				if (!iterator.hasNext()) {
					return entry.getKey();
				}
			}
		}
		return 1;
	}

	/**
	 * 获取奖励ID出现次数
	 * 
	 * @param item
	 * @return
	 */
	public int getLimitNumber(String item) {

		Integer value = this.limitMap.get(item);
		if (null == value) {
			return 0;
		}
		return value;
	}
	
	/**
	 * 清理相关静态数据
	 */
	protected void clearStaticData() {
	}

}
