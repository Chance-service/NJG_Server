package com.guaji.game.module.activity.recharge;

/**
 * 单笔充值活动信息
 * 
 * @author Nannan.Gao
 */
public class SingleRechargeOneDayStatus {

	private int id; // 档位ID

	private int getTimes; // 已领次数

	private int rechargeTimes; // 今天充值次数，每充值一次，rechargeTimes和surplusTimes都+1，rechargeTimes控制每日充值上限

	private int maxRechargeTimes;// 今日最大充值次数

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getGetTimes() {
		return getTimes;
	}

	public void setGetTimes(int getTimes) {
		this.getTimes = getTimes;
	}

	public int getRechargeTimes() {
		return rechargeTimes;
	}

	public void setRechargeTimes(int rechargeTimes) {
		this.rechargeTimes = rechargeTimes;
	}

	public int getMaxRechargeTimes() {
		return maxRechargeTimes;
	}

	public void setMaxRechargeTimes(int maxRechargeTimes) {
		this.maxRechargeTimes = maxRechargeTimes;
	}

	public SingleRechargeOneDayStatus() {
		super();
	}

	@Override
	public String toString() {
		return String.format("%d,%d,%d,%d", id, getTimes, rechargeTimes, maxRechargeTimes);
	}

	public SingleRechargeOneDayStatus(int id, int getTimes, int rechargeTimes, int maxRechargeTimes) {
		super();
		this.id = id;
		this.getTimes = getTimes;
		this.rechargeTimes = rechargeTimes;
		this.maxRechargeTimes = maxRechargeTimes;
	}

}
