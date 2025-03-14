package com.guaji.game.module.activity.newTreasureRaider4;

public class NewTreasureRaiderStatus4 {
	/**
	 * 免费抽奖使用时间点
	 */
	private long lastFreeTime;
	/**
	 * 幸运值
	 */
	private int luckyValue;
	/***
	 * 基础卡抽奖总次数
	 */
	private int basicTotalTimes;

	/**
	 * 皮肤卡抽奖总次数
	 */
	private int skinTotalTimes;

	public NewTreasureRaiderStatus4() {
		this.lastFreeTime = 0;
		this.luckyValue = 0;
		this.basicTotalTimes = 0;
		this.skinTotalTimes = 0;
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

	public int getBasicTotalTimes() {
		return basicTotalTimes;
	}

	public void setBasicTotalTimes(int basicTotalTimes) {
		this.basicTotalTimes = basicTotalTimes;
	}

	public int getSkinTotalTimes() {
		return skinTotalTimes;
	}

	public void setSkinTotalTimes(int skinTotalTimes) {
		this.skinTotalTimes = skinTotalTimes;
	}

}
