package com.guaji.game.module.activity.activity140;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

import org.guaji.os.GuaJiTime;

import com.guaji.game.config.SysBasicCfg;

public class Activity140Status {

	/**
	 * 活动开启时间
	 */
	private Date dateTime;
	
	/**
	 * 活动开启时间
	 */
	private long activityTime;

	/**
	 * 当天第几次登录获得的活动机会
	 */
	private int  loginTimes;
	
	/**
	 * 俄罗斯转盘内环系数
	 */
	private int inIndex;
	/**
	 * 俄罗斯转盘外环系数
	 */	
	private int outIndex;

	/**
	 * 抽奖次数
	 */
	private int  lotteryCount;
	
	/**
	 * 当天累计登陆次数
	 */
	private int  todayLoginCount;

	/**
	 * 是否已经使用(是否使用过返利的权利)
	 */
	private boolean used;
	
	

	public Activity140Status() {
		dateTime = GuaJiTime.getAM0Date();
		this.loginTimes = 0;
		this.lotteryCount = 0;
		this.todayLoginCount=0;
		this.used = false;
		List<String> loginRule = Arrays
				.asList(SysBasicCfg.getInstance().getActivity140OpenLoginCount().split(","));
		if(loginRule.contains(String.valueOf(this.loginTimes))) {
			this.activityTime = System.currentTimeMillis();
		}else {
			this.activityTime=0;
		}
	}

	public Activity140Status init() {
		
		this.loginTimes = 1;
		this.todayLoginCount=1;
		this.lotteryCount = 0;
		this.inIndex=0;
		this.outIndex=0;
		this.used = false;
		List<String> loginRule = Arrays
				.asList(SysBasicCfg.getInstance().getActivity140OpenLoginCount().split(","));
		if(loginRule.contains(String.valueOf(this.loginTimes))) {
			this.activityTime = System.currentTimeMillis();
		}else {
			this.activityTime=0;
		}
		return this;
	}

	public Activity140Status init(int loginTimes) {
		this.loginTimes = loginTimes;
		this.todayLoginCount=loginTimes;
		this.lotteryCount = 0;
		this.inIndex=0;
		this.outIndex=0;
		this.used = false;
		List<String> loginRule = Arrays
				.asList(SysBasicCfg.getInstance().getActivity140OpenLoginCount().split(","));
		if(loginRule.contains(String.valueOf(this.loginTimes))) {
			this.activityTime = System.currentTimeMillis();
		}else {
			this.activityTime=0;
		}
		return this;
	}

	public int calcRate() {
		return 0;
	}

	public long getActivityTime() {
		return activityTime;
	}

	public void setActivityTime(long activityTime) {
		this.activityTime = activityTime;
	}

	public int getLoginTimes() {
		return loginTimes;
	}

	public void setLoginTimes(int loginTimes) {
		this.loginTimes = loginTimes;
	}

	public int getLotteryCount() {
		return lotteryCount;
	}

	public void setLotteryCount(int lotteryCount) {
		this.lotteryCount = lotteryCount;
	}

	public boolean isUsed() {
		return used;
	}
	
	

	public int getInIndex() {
		return inIndex;
	}

	public void setInIndex(int inIndex) {
		this.inIndex = inIndex;
	}

	public int getOutIndex() {
		return outIndex;
	}

	public void setOutIndex(int outIndex) {
		this.outIndex = outIndex;
	}

	public void setUsed(boolean used) {
		this.used = used;
	}

	public Date getDateTime() {
		return dateTime;
	}

	public void setDateTime(Date dateTime) {
		this.dateTime = dateTime;
	}

	public int getTodayLoginCount() {
		return todayLoginCount;
	}

	public void setTodayLoginCount(int todayLoginCount) {
		this.todayLoginCount = todayLoginCount;
	}
	
	
}
