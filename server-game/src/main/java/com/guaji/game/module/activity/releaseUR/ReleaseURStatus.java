package com.guaji.game.module.activity.releaseUR;

import java.util.HashSet;
import java.util.Set;

public class ReleaseURStatus {
	/**
	 * 免费抽奖使用时间点
	 */
	private long lastFreeTime;
	/***
	 * 翻倍
	 */
	private int multiple = 1;
	/**
	 * 翻倍开始时间
	 */
	private long multipStartTime;
	/***
	 * 翻倍超时时间
	 */
	private long multipOverTime;
	/**
	 * 幸运值
	 */
	private int luckyValue;
	/***
	 * 总次数
	 */
	private int totalTimes;
	
	/**
	 *   已抽取的奖励索引
	 */
	private Set<Integer> lotteryIndexs;

	public ReleaseURStatus() {
		this.lastFreeTime = 0;
		this.multiple = 1;
		this.multipStartTime = 0;
		this.multipOverTime = 0;
		this.luckyValue = 0;
		this.totalTimes = 0;
		this.lotteryIndexs=new HashSet<Integer>();
	}

	public long getLastFreeTime() {
		return lastFreeTime;
	}

	public void setLastFreeTime(long lastFreeTime) {
		this.lastFreeTime = lastFreeTime;
	}

	public int getMultiple() {
		return multiple;
	}

	public void setMultiple(int multiple) {
		this.multiple = multiple;
	}

	public long getMultipOverTime() {
		return multipOverTime;
	}

	public void setMultipOverTime(long multipOverTime) {
		this.multipOverTime = multipOverTime;
	}

	public long getMultipStartTime() {
		return multipStartTime;
	}

	public void setMultipStartTime(long multipStartTime) {
		this.multipStartTime = multipStartTime;
	}

	public int getLuckyValue() {
		return luckyValue;
	}

	public void setLuckyValue(int luckyValue) {
		this.luckyValue = luckyValue;
	}

	public int getTotalTimes() {
		return totalTimes;
	}

	public void setTotalTimes(int totalTimes) {
		this.totalTimes = totalTimes;
	}

	/**
	 * 检查翻版是否超时
	 * 
	 * @return
	 */
	public boolean checkMultipleTime() {
		if (getMultipOverTime() == 0) {
			return false;
		}
		if (getMultipStartTime() == 0) {
			return false;
		}
		long currentTime = System.currentTimeMillis();
		if (getMultipStartTime() + getMultipOverTime() > currentTime) {
			return true;
		} else {
			return false;
		}
	}

	public Set<Integer> getLotteryIndexs() {
		return lotteryIndexs;
	}

	public void setLotteryIndexs(Set<Integer> lotteryIndexs) {
		this.lotteryIndexs = lotteryIndexs;
	}
	
	public void AddLotteryIndexs(Integer index) {
		this.lotteryIndexs.add(index);
	}
	
}
