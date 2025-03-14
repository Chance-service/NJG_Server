package com.guaji.game.module.activity.activity161;

import java.util.Calendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.guaji.os.GuaJiTime;

import com.guaji.game.protocol.Const.SupportCalendarType;

/**
 * 累计签到协议
 */
public class Activity161Status {
	/**
	 * 当前月份
	 */
	private int curMonth;
	/**
	 * 已签到天数
	 */
	private Map<Integer,Set<Integer>> signedDays;
	/**
	 * 是否購買活動
	 */
	private Map<Integer,Integer> Buy;

	
	public Activity161Status() {
		curMonth=0;
		signedDays= new HashMap<>();
		Buy = new HashMap<>();
	}

	public Set<Integer> getSignedDays(int type) {
		if (signedDays.containsKey(type)) {
			return signedDays.get(type);
		} else {
			Set<Integer> daySet = new HashSet<>();
			signedDays.put(type, daySet);
			return signedDays.get(type);
		}
	}

	public void addSignedDay(int type,int day) {
		if (signedDays.containsKey(type)) {
			signedDays.get(type).add(day);
		} else {
			Set<Integer> daySet = new HashSet<>();
			signedDays.put(type, daySet);
		}
	}

	/**
	 * 是否红点提示，是否领取过了
	 */
	public boolean showRedPoint() {

		return false;
	}

	public int getCurMonth() {
		return curMonth;
	}

	public void setCurMonth(int curMonth) {
		this.curMonth = curMonth;
	}
	
	public void reSet(int type)
	{
		int oldMonth = curMonth ;
		
		curMonth= GuaJiTime.getNowMonth() ;
		
		if (oldMonth != curMonth) {
			signedDays.clear();	
			Buy.clear();
		} else {
			if (signedDays.containsKey(type)) {
				signedDays.get(type).clear();
			}
			if (Buy.containsKey(type)) {
				Buy.replace(type,0);
			}
		}
		
		
		
	}
	
	public boolean isBuy(int type) {
		
		if (curMonth == GuaJiTime.getNowMonth()) { 
			if (Buy.containsKey(type)) {
				return (Buy.get(type) == 1);
			}
		}
		return  false;
	}
	
	public void setBuy(int type,int buy) {
		if (Buy.containsKey(type)) {
			Buy.replace(type, buy);
		} else {
			Buy.put(type, buy);
		}
	}

}
