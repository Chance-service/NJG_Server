package com.guaji.game.module.activity.activity190;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.guaji.os.GuaJiTime;

import com.guaji.game.config.FreeSummon167Cfg;

/**
 * 免費召喚抽加強版
 */
public class Activity190Status {
	/**
	 * 玩家活動開始時間
	 */
	private Date startDate;
	/**
	 * 紀錄已領天數 <種類,天數>
	 */ 
	private Map<Integer,Integer> takeDay;
	/**
	 * 	消費次數
	 */
	private int count;


	public Activity190Status() {
		this.startDate = null;
		this.takeDay = new HashMap<>();
	}


	public Date getStartDate() {
		return startDate;
	}
	

	public int getTakeDay(int type) {
		if (takeDay.containsKey(type)) {
			return takeDay.get(type);
		}
		return 0;
	}


	public void setTakeDay(int type ,int takeDay) {
		this.takeDay.put(type,takeDay);
	}


	public void setCount(int count) {
		this.count = count;
	}


	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}
	
	
	
	/**
	 * 今天是活動的第幾天
	 *
	 * @return 1..90
	 */
	public int getTodayCount() {
		
		if (this.startDate == null) {
			return -1;
		}
		int result =  Math.min(GuaJiTime.calcBetweenDays(GuaJiTime.getCalendar().getTime(), startDate)+1,FreeSummon167Cfg.getMaxDay()) ;
		return result;

	}
	
	public int getCount() {
		return count;
	}
	
	public void addCount() {
		this.count++;
	}
}
