package com.guaji.game.module.activity.firstgiftpack;

import java.util.Date;
/**
 * 
 * @author leesong
 *
 */
public class FirstGiftPackStatus {
	
	/**
	 * 首充礼包领取状态
	 */
	private int giftStatus = 0;
	/**
	 * 是否是首次充值
	 */
	private int isFirstPay = 0;
	/**
	 * 最后领取时间
	 */
	private Date lastAwareTime;
	/**
	 * setter getter 
	 */
	public int getGiftStatus() {
		return giftStatus;
	}
	public void setGiftStatus(int giftStatus) {
		this.giftStatus = giftStatus;
	}
	public int getIsFirstPay() {
		return isFirstPay;
	}
	public void setIsFirstPay(int isFirstPay) {
		this.isFirstPay = isFirstPay;
	}
	public Date getLastAwareTime() {
		return lastAwareTime;
	}
	public void setLastAwareTime(Date lastAwareTime) {
		this.lastAwareTime = lastAwareTime;
	}
	

	
}
