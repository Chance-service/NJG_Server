package com.guaji.game.module.activity.activity197;

import java.util.HashMap;
import java.util.Map;

import org.guaji.os.GuaJiTime;

public class Activity197Status {
	/**
	 * 清除免費次數時間點
	 */
	private long resetFreeTime;
	/***
	 * 各池免費使用次數
	 */
	private Map<Integer,Integer> usefreeMap;
	/***
	 * 保底計數
	 */
	private Map<Integer,Integer> counter;
	
	public Activity197Status() {
		this.resetFreeTime = GuaJiTime.getMillisecond();
		this.usefreeMap = new HashMap<>();
		this.counter = new HashMap<>();
	}
	
	/**
	 * 檢查是否需每日免費次數使用
	 * @return
	 */
	public boolean checkDailyClear() {
		long currentTime =  GuaJiTime.getMillisecond();
		if(!GuaJiTime.isSameDay(this.resetFreeTime,currentTime)) {
			usefreeMap.clear();
			this.resetFreeTime = currentTime;
			return true;
		}
		return false;
	}
	
	/**
	 * 取得保底次數
	 * @param id
	 * @return
	 */
	public int getCounter(int id) {
		if (this.counter.containsKey(id)) {
			return this.counter.get(id);
		}
		return 0;
	}
	/**
	 * 累加保底次數
	 * @param id
	 */
	public void incCounter(int id) {
		this.counter.put(id,getCounter(id)+1);
	}
	
	/**
	 * 取得免費使用次數
	 * @param id
	 * @return
	 */
	public int getUsefree(int id) {
		if (this.usefreeMap.containsKey(id)) {
			return this.usefreeMap.get(id);
		}
		return 0;
	}
	/**
	 * 累加免費保底次數
	 * @param id
	 */
	public void incUsefree(int id) {
		this.usefreeMap.put(id,getUsefree(id)+1);
	}
	
	/*
	 * 清除保底計數
	 * 
	 */
	public void clearCounter(int id) {
		if (this.counter.containsKey(id)) {
			this.counter.remove(id);
		} 
	}
	
}
