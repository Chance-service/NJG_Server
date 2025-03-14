package com.guaji.game.module.activity.discountGift;

/**
 * 一个折扣礼包数据
 *
 */
public class OneDiscountGiftData {

	// 当前购买次数
	private int buyTimes;
	// 当前状态，0：不可购买，也不可领取（达到购买次数，并且已领取），1：可购买，2：可领取
	private int status;

	public int getBuyTimes() {
		return buyTimes;
	}

	public void setBuyTimes(int buyTimes) {
		this.buyTimes = buyTimes;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}
}
