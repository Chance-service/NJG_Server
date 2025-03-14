package com.guaji.game.module.activity.forging;

import org.guaji.os.GuaJiTime;

/**
 * 神装锻造活动数据
 */
public class ForgingStatus {

	/**
	 * 下次免费时间
	 */
	private long freeTime;
	
	/**
	 * 抽奖总次数
	 */
	private int totalTimes;
	

	public ForgingStatus() {

	}

	public long getFreeTime() {
		return freeTime;
	}

	public void setFreeTime(long freeTime) {
		this.freeTime = freeTime;
	}

	public int getTotalTimes() {
		return totalTimes;
	}

	public void setTotalTimes(int totalTimes) {
		this.totalTimes = totalTimes;
	}

	/**
	 * 是否能免费抽奖
	 * 
	 * @return
	 */
	public boolean isCanFree() {

		return this.freeTime <= GuaJiTime.getMillisecond();
	}
	
}
