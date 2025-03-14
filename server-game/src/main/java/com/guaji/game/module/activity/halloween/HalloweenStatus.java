package com.guaji.game.module.activity.halloween;

import java.util.HashMap;
import java.util.Map;

/**
 * 万圣节活动活动实体
 * 
 * @author Melvin.Mao
 * @date Oct 17, 2017 5:00:47 PM
 */
public class HalloweenStatus {

	// 上次免费时间
	private int lastFreeTime;

	// 普通奖池总次数
	private int commonTimes;

	// 高级奖池总次数
	private int advancedTimes;

	// 总的抽奖次数
	private int totalTimes;

	// 兑换记录
	private Map<Integer, Integer> exchangeMap = new HashMap<>();

	public int getLastFreeTime() {
		return lastFreeTime;
	}

	public void setLastFreeTime(int lastFreeTime) {
		this.lastFreeTime = lastFreeTime;
	}

	public int getCommonTimes() {
		return commonTimes;
	}

	public void setCommonTimes(int commonTimes) {
		this.commonTimes = commonTimes;
	}

	public int getAdvancedTimes() {
		return advancedTimes;
	}

	public void setAdvancedTimes(int advancedTimes) {
		this.advancedTimes = advancedTimes;
	}

	public int getTotalTimes() {
		return totalTimes;
	}

	public void setTotalTimes(int totalTimes) {
		this.totalTimes = totalTimes;
	}

	public Map<Integer, Integer> getExchangeMap() {
		return exchangeMap;
	}

	public void setExchangeMap(Map<Integer, Integer> exchangeMap) {
		this.exchangeMap = exchangeMap;
	}

	public HalloweenStatus() {
		super();
		this.lastFreeTime = 0;
		this.commonTimes = 0;
		this.advancedTimes = 0;
		this.totalTimes = 0;
		HalloweenManager.initExchangeMap(this);
	}

}
