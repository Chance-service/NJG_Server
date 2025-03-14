package com.guaji.game.module.activity.activity157;

public class Activity157Status {
	/***
	 * 幸運值
	 */
	private boolean isGot;
	/***
	 * 取得次數
	 */
	private int times;

	
	public Activity157Status() {
		this.isGot = false;
		this.times= 0;
	}

	public boolean getIsGot() {
		return this.isGot;
	}

	public void setIsGot(boolean isGot) {
			this.isGot = isGot;
	}
	
	public int getTimes(){
		return this.times;
	}
	
//	public void setTimes(int value) {
//		this.times = value;
//	}
	
	public void incTimes(int value) {
		this.times = this.times + value;
	}
	
}
