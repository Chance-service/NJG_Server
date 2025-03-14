package com.guaji.game.module.activity.timeLimit;

import java.util.HashMap;
import java.util.Map;

import org.guaji.os.GuaJiTime;

/**
 * 限时限购个人状态存储
 */
public class PersonalTimeLimitStatus {

	private Map<Long, Map<Integer, Integer>> buyMap = new HashMap<>();
	
	private boolean shouldShowPoint = true;
	
	public void addTodayBuyTime(int cfgId, int count) {
		long curTime = GuaJiTime.getAM0Date().getTime();
		Map<Integer, Integer> buyTimesMap = buyMap.get(curTime);
		if(buyTimesMap == null) {
			buyTimesMap = new HashMap<>(); 
			buyMap.put(curTime, buyTimesMap);
		}
		
		if(!buyTimesMap.containsKey(cfgId)) {
			buyTimesMap.put(cfgId, count);
		}else{
			buyTimesMap.put(cfgId, buyTimesMap.get(cfgId) + count);
		}
		
	}
	
	public int getTodayBuyTimes(int cfgId) {
		long curTime = GuaJiTime.getAM0Date().getTime();
		Map<Integer, Integer> buyTimesMap = buyMap.get(curTime);
		if(buyTimesMap == null) {
			return 0;
		}
		
		if(buyTimesMap.containsKey(cfgId)) {
			return  buyTimesMap.get(cfgId);
		}
		
		return 0;
	}
	
	public int getTotalBuyTimes(int cfgId) {
		int count = 0;
		for(Map<Integer,Integer> buyTimesMap : buyMap.values()) {
			if(buyTimesMap.containsKey(cfgId)) {
				count += buyTimesMap.get(cfgId);
			}
		}
		return count;
	}

	public boolean shouldShowPoint() {
		return shouldShowPoint;
	}

	public void setShouldShowPoint(boolean shouldShowPoint) {
		this.shouldShowPoint = shouldShowPoint;
	}
}
