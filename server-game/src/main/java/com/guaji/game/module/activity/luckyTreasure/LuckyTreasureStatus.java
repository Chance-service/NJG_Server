package com.guaji.game.module.activity.luckyTreasure;

import org.guaji.os.GuaJiTime;

public class LuckyTreasureStatus {
	// 总领取次数
	private int rewardTimes;
	// 上次领取时间
	private long lastRewardTime;
	
	public LuckyTreasureStatus(){
		rewardTimes = 0;
		lastRewardTime = 0;
	}
	
	/**
	 * 今日奖励是否已领取
	 * @return
	 */
	public boolean todayIsGet(){
		if(GuaJiTime.getMillisecond() > lastRewardTime){
			return false;
		}
		return true;
	}
	
	/**
	 * 记录今日奖励已领取
	 * @return
	 */
	public void setTodayAwardGot(){
		rewardTimes ++;
		lastRewardTime = GuaJiTime.getNextAM0Date();
	}
	
	/**
	 * 获取本期活动领取总次数
	 */
	public int getRewardTimes(){
		return rewardTimes;
	}
	
}
