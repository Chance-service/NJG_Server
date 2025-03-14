package com.guaji.game.module.activity.activity146;

public class Activity146Status {
	/**
	 * 免费抽奖使用时间点
	 */
	private long lastFreeTime;
	/***
	 * 总次数
	 */
	private int totalTimes;
	/**
	 * 首抽
	 */
	private boolean firstgacha;
	
	public Activity146Status() {
		this.lastFreeTime = 0;
		this.totalTimes = 0;//(SysBasicCfg.getInstance().getChosenOneGuarant()-1); //69第一次抽就累積69保底
		this.firstgacha = false;
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

	public boolean isFirstgacha() {
		return firstgacha;
	}

	public void setFirstgacha(boolean firstgacha) {
		this.firstgacha = firstgacha;
	}
	
	
	
}
