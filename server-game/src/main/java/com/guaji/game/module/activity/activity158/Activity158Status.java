package com.guaji.game.module.activity.activity158;

public class Activity158Status {
	/***
	 * 累積點數
	 */
	private int point;

	
	public Activity158Status() {
		this.point= 0;
	}

	public int getPoint() {
		return this.point;
	}

	public void setPoint(int value) {
			this.point = value;
	}
	
	public void incPoint(int value) {
		this.point = this.point + value;
	}
	
	public void decPoint(int value) {
		this.point = Math.max(this.point - value,0);
	}
	
}
