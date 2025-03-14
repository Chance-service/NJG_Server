package com.guaji.game.module.activity.treasureRaider;

import org.guaji.os.GuaJiTime;

import com.guaji.game.config.TreasureRaiderTimesCfg;

public class TreasureRaiderStatus {
	// 今日寻宝次数
	private int todaySearchTimes;
	// 上次次数重置时间
	private long lastUpdateTime;
	// 活动期间总寻宝次数
	private int totalSearchTimes;
	// 活动期间单次寻宝次数
	private int totalSingleSearchTimes;
	// 活动期间总开宝箱次数
	private int totalOpenTreasureTimes;
	// 上次宝箱奖励
	private String lastBoxAwards;
	
	public TreasureRaiderStatus(){
		todaySearchTimes = 0;
		lastUpdateTime = 0;
		totalSearchTimes = 0;
		totalOpenTreasureTimes = 0;
		totalSingleSearchTimes = 0;
		lastBoxAwards = "";
	}

	public int getTodaySearchTimes() {
		refreshTimes();
		return todaySearchTimes;
	}

	public void addSearchTimes(int addTimes) {
		refreshTimes();
		todaySearchTimes += addTimes;
		totalSearchTimes += addTimes;
		if(addTimes == 1){
			totalSingleSearchTimes ++;
		}
	}
	
	private void refreshTimes(){
		if(lastUpdateTime < GuaJiTime.getAM0Date().getTime()){
			todaySearchTimes = 0;
			lastUpdateTime = GuaJiTime.getMillisecond();
		}
	}

	public int getTotalSearchTimes() {
		return totalSearchTimes;
	}
	
	public int getTotalSingleSearchTimes() {
		return totalSingleSearchTimes;
	}

	public int getTotalOpenTreasureTimes() {
		return totalOpenTreasureTimes;
	}

	public void setTotalOpenTreasureTimes(int totalOpenTreasureTimes) {
		this.totalOpenTreasureTimes = totalOpenTreasureTimes;
	}

	public String getLastBoxAwards() {
		return lastBoxAwards;
	}

	public void setLastBoxAwards(String lastBoxAwards) {
		this.lastBoxAwards = lastBoxAwards;
	}
	
	/**
	 * 是否红点提示，是否有免费次数
	 */
	public boolean showRedPoint(int vipLevel) {
		TreasureRaiderTimesCfg treaRaiderTimesCfg = TreasureRaiderTimesCfg.getTimesCfgByVipLevel(vipLevel);
		if (getTodaySearchTimes() < treaRaiderTimesCfg.getOneDayFreeTimes()) {
			return true;
		}
		return false;
	}
}
