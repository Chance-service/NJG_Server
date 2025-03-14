package com.guaji.game.module.activity.activity143;

import java.util.Date;
import org.guaji.os.GuaJiTime;

public class Activity143Status {

	/**
	 * 活动开启时间
	 */
	private Date dateTime;
	
	/**
	 * 現在所在關卡0.未開啟 1-20
	 */
	private int level;
	/**
	 * 免費次數
	 */
	private int free;
	/**
	 * 目前獲得獎勵 ownrewards
	 */
	private String rewards;

	/**
	 * 失敗狀態
	 */
	private boolean fail;
	
	/**
	 * 繼續狀態  // 一定開出獎品
	 */
	private boolean renew ;
	
	

	public Activity143Status() {
		dateTime = GuaJiTime.getAM0Date();
		this.level = 0;
		this.free = 2;
		this.fail = false;
		this.renew = false;
	}

	public int getfree() {
		return free;
	}

	public void setfree(int freenum) {
		this.free = freenum;
	}
	
	public boolean getisfail() {
		return fail;
	}
	
	public void setisfail(boolean fail) {
		this.fail = fail;
	}
	public boolean getrenew() {
		return renew;
	}
	
	public void setrenew(boolean renew) {
		this.renew = renew;
	}	
	public int getlevel() {
		return level;
	}

	public void setlevel(int level) {
		this.level = level;
	}
	
	public String getrewards() {
		return rewards;
	}

	public void setrewards(String rewards) {
		this.rewards = rewards;
	}



	public Date getDateTime() {
		return dateTime;
	}

	public void setDateTime(Date dateTime) {
		this.dateTime = dateTime;
	}

	
	
}
