package com.guaji.game.module.activity.activity154;

public class Activity154Status {
	/**
	 * 免费刷新使用时间点
	 */
	private long lastFreeTime;
	/***
	 * 幸運值
	 */
	private int lucky;
	/***
	 * 各輪盤領獎順序
	 */
	private int take;

	
	public Activity154Status() {
		this.lastFreeTime = 0;
		this.lucky= 0;
		this.take = 0;
	}

	public long getLastFreeTime() {
		return this.lastFreeTime;
	}

	public void setLastFreeTime(long lastFreeTime) {
			this.lastFreeTime = lastFreeTime;
	}
	
	public  int getLucky(){
		return lucky;
	}
	
	public int getTake() {
		return this.take;
	}

	public void setTake(int value) {
		this.take = value;
	}
	
	public void incTake(int value) {
		this.take = this.take + value;
	}
	
	public int getLucky(int type) {
		return this.lucky;
	}
	
	public void setLucky(int value) {
		this.lucky = value;
	}
	
	public void incLucky(int value) {
		this.lucky = this.lucky+value;
	}
	
	public void decLucky(int value) {
		this.lucky = Math.max(this.lucky-value,0);
	}
	
}
