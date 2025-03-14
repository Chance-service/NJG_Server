package com.guaji.game.module.activity.harem;

public class HaremStatus {

	private int harmType;
	private int drawTimes;
	private int lastFreeTime;
	private int freeChance;
	private int totalTimes;

	public int getHarmType() {
		return harmType;
	}

	public void setHarmType(int harmType) {
		this.harmType = harmType;
	}

	public int getDrawTimes() {
		return drawTimes;
	}

	public void setDrawTimes(int drawTimes) {
		this.drawTimes = drawTimes;
	}

	public int getLastFreeTime() {
		return lastFreeTime;
	}

	public void setLastFreeTime(int lastFreeTime) {
		this.lastFreeTime = lastFreeTime;
	}

	public int getFreeChance() {
		return freeChance;
	}

	public void setFreeChance(int freeChance) {
		this.freeChance = freeChance;
	}

	public int getTotalTimes() {
		return totalTimes;
	}

	public void setTotalTimes(int totalTimes) {
		this.totalTimes = totalTimes;
	}

	public HaremStatus(int harmType, int drawTimes, int lastFreeTime, int freeChance, int totalTimes) {
		super();
		this.harmType = harmType;
		this.drawTimes = drawTimes;
		this.lastFreeTime = lastFreeTime;
		this.freeChance = freeChance;
		this.totalTimes = totalTimes;
	}

	public HaremStatus() {
		super();
	}

}
