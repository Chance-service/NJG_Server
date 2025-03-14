package com.guaji.game.module.activity.lights;

import java.util.HashMap;
import java.util.Map;

import org.guaji.os.GuaJiTime;

public class FindTreasureStatus {
	/**
	 * K-类型，V-今日进行次数
	 */
	private Map<Integer, Integer> todayFindTypeTimesMap = new HashMap<Integer, Integer>();
	/**
	 * K-类型，V-活动期间总次数
	 */
	private Map<Integer, Integer> findTypeTotalTimesMap = new HashMap<Integer, Integer>();
	/**
	 * 上次刷新时间
	 */
	private long lastRefreshTime = 0; 
	
	/**
	 * 获取对应类型当天已进行次数
	 * @param findTye
	 * @return
	 */
	public int getTodayFindTimes(int findTye){
		refresh();
		
		if(todayFindTypeTimesMap.containsKey(findTye)){
			return todayFindTypeTimesMap.get(findTye);
		}else{
			todayFindTypeTimesMap.put(findTye, 0);
			return 0;
		}
	}
	
	/**
	 * 获取活动期间对应类型总计已进行次数
	 * @param findTye
	 * @return
	 */
	public int getTotalFindTimes(int findTye){
		if(findTypeTotalTimesMap.containsKey(findTye)){
			return findTypeTotalTimesMap.get(findTye);
		}else{
			findTypeTotalTimesMap.put(findTye, 0);
			return 0;
		}
	}
	
	private void refresh(){
		if(lastRefreshTime < GuaJiTime.getAM0Date().getTime()){
			todayFindTypeTimesMap.clear();
			lastRefreshTime = GuaJiTime.getMillisecond();
		}
	}
	
	/**
	 * 增加对应类型进行次数
	 * @param findType
	 * @return
	 */
	public int addTimes(int findType, int times){
		addTotalTimes(findType, times);
		if(todayFindTypeTimesMap.containsKey(findType)){
			int alreadyTimes = todayFindTypeTimesMap.get(findType) + times;
			todayFindTypeTimesMap.put(findType, alreadyTimes);
			return alreadyTimes;
		}else{
			todayFindTypeTimesMap.put(findType, times);
			return times;
		}
	}
	
	private void addTotalTimes(int findType, int times){
		if(findTypeTotalTimesMap.containsKey(findType)){
			int alreadyTimes = findTypeTotalTimesMap.get(findType) + times;
			findTypeTotalTimesMap.put(findType, alreadyTimes);
		}else{
			findTypeTotalTimesMap.put(findType, times);
		}
	}
}
