package com.guaji.game.module.activity.activity173;

public class Activity173Status {
	/**
	 * 免费抽奖使用时间点
	 */
	private long lastFreeTime;
	/***
	 * 总次数
	 */
	private int totalTimes;
	
	public Activity173Status() {
		this.lastFreeTime = 0;
		this.totalTimes = 0;//(SysBasicCfg.getInstance().getChosenOneGuarant()-1); //69第一次抽就累積69保底
	}

	public long getLastFreeTime() {
		return lastFreeTime;
	}

	public void setLastFreeTime(long lastFreeTime) {
		this.lastFreeTime = lastFreeTime;
	}
	
	public int getTotalTimes() {
		return totalTimes;
	}

	public void setTotalTimes(int totalTimes) {
		this.totalTimes = totalTimes;
	}
	
	public void incTotalTimes() {
		this.totalTimes = this.totalTimes+1;
	}
	
}
