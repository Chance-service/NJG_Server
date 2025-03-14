package com.guaji.game.module.activity.activity180;

import java.util.Date;

import org.guaji.os.GuaJiTime;

import com.guaji.game.config.FreeSummon180Cfg;

/**
 * 免費召喚900抽
 */
public class Activity180Status {
	/**
	 * 玩家活動開始時間
	 */
	private Date startDate;
	/**
	 * 紀錄已領天數
	 */
	private int takeDay;


	public Activity180Status() {
		this.startDate = null;
		this.takeDay = 0;
	}


	public Date getStartDate() {
		return startDate;
	}


	public int getTakeDay() {
		return takeDay;
	}


	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}


	public void setTakeDay(int takeDay) {
		this.takeDay = takeDay;
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
		int result =  Math.min(GuaJiTime.calcBetweenDays(GuaJiTime.getCalendar().getTime(), startDate)+1,FreeSummon180Cfg.getMaxDay()) ;
		return result;

	}
}
