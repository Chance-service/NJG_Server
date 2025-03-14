package com.guaji.game.module.activity.turntable;

import java.util.HashMap;
import java.util.Map;

/**
 * 大转盘活动活动实体
 * 
 * @author Melvin.Mao
 * @date 2017年9月14日 下午5:46:33
 */
public class TurntableStatus {

	// 上次免费时间
	private int lastFreeTime;

	// 总次数
	private int totalTimes;

	// 积分
	private int credits;

	// 宝箱开启状态(根据宝箱ID由逗号隔开)
	private String canOpenBox;

	// 兑换记录
	private Map<Integer, Integer> exchangeMap = new HashMap<>();

	public int getLastFreeTime() {
		return lastFreeTime;
	}

	public void setLastFreeTime(int lastFreeTime) {
		this.lastFreeTime = lastFreeTime;
	}

	public int getTotalTimes() {
		return totalTimes;
	}

	public void setTotalTimes(int totalTimes) {
		this.totalTimes = totalTimes;
	}

	public int getCredits() {
		return credits;
	}

	public void setCredits(int credits) {
		this.credits = credits;
	}

	public Map<Integer, Integer> getExchangeMap() {
		return exchangeMap;
	}

	public void setExchangeMap(Map<Integer, Integer> exchangeMap) {
		this.exchangeMap = exchangeMap;
	}

	public String getCanOpenBox() {
		return canOpenBox;
	}

	public void setCanOpenBox(String canOpenBox) {
		this.canOpenBox = canOpenBox;
	}

	public TurntableStatus() {
		super();
		this.lastFreeTime = 0;
		this.totalTimes = 0;
		this.credits = 0;
		// 这里面写灵活点没啥意义
		this.canOpenBox = "false,false,false";
		TurntableManager.initExchangeMap(this);
	}

}
