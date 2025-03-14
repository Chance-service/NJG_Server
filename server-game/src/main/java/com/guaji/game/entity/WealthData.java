package com.guaji.game.entity;

/**
 * 每日的财富俱乐部数据
 */
public class WealthData {

	/**
	 * 当前服务器充值人数
	 */
	private int totalNumber;

	/**
	 * 自动检测时间戳
	 */
	private long tickTime;
	
	/**
	 * 结算状态
	 */
	private boolean isSettle;
	

	public WealthData() {
		totalNumber = 0;
		tickTime = 0;
	}

	public int getTotalNumber() {
		return totalNumber;
	}

	public void setTotalNumber(int totalNumber) {
		this.totalNumber = totalNumber;
	}

	public long getTickTime() {
		return tickTime;
	}

	public void setTickTime(long tickTime) {
		this.tickTime = tickTime;
	}

	public boolean isSettle() {
		return isSettle;
	}

	public void setSettle(boolean isSettle) {
		this.isSettle = isSettle;
	}

}
