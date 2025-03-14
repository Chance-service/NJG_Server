package com.guaji.game.module.activity.activity127;

import java.util.HashSet;
import java.util.Set;

/**
 * @author 作者 E-mail:Jeremy@163.com
 * @version 创建时间：2019年5月28日 上午10:30:28 类说明
 */
public class Activity127Status {
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
	 * 已抽取的奖励索引
	 */
	private Set<Integer> lastRandomIndexs;

	public Activity127Status() {
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
