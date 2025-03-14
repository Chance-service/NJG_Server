package com.guaji.game.module.activity.activity141;

import java.util.Date;
import org.guaji.os.GuaJiTime;

public class Activity141Status {

	/**
	 * 活动开启时间
	 */
	private Date dateTime;
	
	/**
	 * 現在所在位置1-25
	 */
	private int Index;
	/**
	 * 完成遊戲圈數
	 */	
	private int finish;

	/**
	 * 免費次數
	 */
	private int free;

	/**
	 * 加倍狀態
	 */
	private boolean isdouble;
	
	

	public Activity141Status() {
		dateTime = GuaJiTime.getAM0Date();
		this.Index = 1;
		this.finish = 0;
		this.free = 2;
		this.isdouble = false;
	}

	public int getfree() {
		return free;
	}

	public void setfree(int freenum) {
		this.free = freenum;
	}
	
	public int getfinish() {
		return finish;
	}

	public void setfinish(int finish) {
		this.finish = finish;
	}

	public boolean isdouble() {
		return isdouble;
	}
	
	public void setisdouble(boolean isdouble) {
		this.isdouble = isdouble;
	}
	
	public int getIndex() {
		return Index;
	}

	public void setIndex(int Index) {
		this.Index = Index;
	}


	public Date getDateTime() {
		return dateTime;
	}

	public void setDateTime(Date dateTime) {
		this.dateTime = dateTime;
	}

	
	
}
