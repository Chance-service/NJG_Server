package com.guaji.game.module.activity.releaseUR2;

import java.util.HashSet;
import java.util.Set;

public class ReleaseURStatu2 {
	/**
	 * 免费抽奖使用时间点
	 */
	private long lastFreeTime;
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
	private Set<Integer> lastRandomIndexs;

	public ReleaseURStatu2() {
		this.lastFreeTime = 0;
		this.luckyValue = 0;
		this.totalTimes = 0;
		this.lastRandomIndexs=new HashSet<Integer>();
	}

	public long getLastFreeTime() {
		return lastFreeTime;
	}

	public void setLastFreeTime(long lastFreeTime) {
		this.lastFreeTime = lastFreeTime;
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


	public Set<Integer> getLastRandomIndexs() {
		return lastRandomIndexs;
	}

	public void setLastRandomIndexs(Set<Integer> lastRandomIndexs) {
		this.lastRandomIndexs = lastRandomIndexs;
	}

	
}
