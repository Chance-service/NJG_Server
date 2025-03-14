package com.guaji.game.module.activity.loginsigned;

import java.util.Calendar;
import java.util.HashSet;

import java.util.Set;

import org.guaji.os.GuaJiTime;


/**
 * 累计签到协议
 */
public class AccLoginSignedStatus {


	/**
	 * 当前月份
	 */
	private int curMonth;
	/**
	 * 已签到天数
	 */
	private Set<Integer> signedDays;
	/**
	 * 补签天数
	 */
	private Set<Integer> supplSignedDays;
	

	/**
	 * 已开箱子奖励
	 */
	private Set<Integer> gotAwardChest;


	public AccLoginSignedStatus() {
		curMonth=GuaJiTime.getCalendar().get(Calendar.MONTH);
		signedDays=new HashSet<Integer>();
		supplSignedDays=new HashSet<Integer>();
		gotAwardChest=new HashSet<Integer>();
	}

	public Set<Integer> getSignedDays() {
		return signedDays;
	}

	public void addSignedDay(int day) {
		signedDays.add(day);
	}

	public Set<Integer> getSupplSignedDays() {
		return supplSignedDays;
	}

	public void addSupplSignedDay(int day) {
		supplSignedDays.add(day);
	}



	public Set<Integer> getGotAwardChest() {
		return gotAwardChest;
	}

	public void addGotAwardChest(int level) {
		gotAwardChest.add(level);
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
	
	public void reSet()
	{
		signedDays.clear();
		supplSignedDays.clear();
		gotAwardChest.clear();
		curMonth=GuaJiTime.getCalendar().get(Calendar.MONTH);
		
	}

}
