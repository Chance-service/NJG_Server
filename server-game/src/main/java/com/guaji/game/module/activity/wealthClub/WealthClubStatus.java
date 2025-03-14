package com.guaji.game.module.activity.wealthClub;

import java.util.HashMap;
import java.util.Map;

import org.guaji.os.GuaJiTime;

/**
 * 财富俱乐部活动数据
 * 
 * @author Nannan.Gao
 * @date 2016-9-13 10:51:19
 */
public class WealthClubStatus {

	/**
	 * 每日充值金额<key:日期 value:充值金额>
	 */
	private Map<String, Integer> rechargeMap = new HashMap<String, Integer>();

	
	/**
	 * 获取每日充值金额
	 * 
	 * @param dateFormat
	 * @return
	 */
	public Integer getRechargeMap(String dateFormat) {
		return this.rechargeMap.get(dateFormat);
	}

	/**
	 * 增加今日充值金额
	 * 
	 * @param recharge
	 */
	public void addRecharge(int recharge) {

		String date = GuaJiTime.DATE_FORMATOR_DAYNUM(GuaJiTime.getAM0Date());
		if (this.isFirstRecharge()) {
			rechargeMap.put(date, recharge);
		} else {
			int value = rechargeMap.get(date);
			value += recharge;
			rechargeMap.put(date, value);
		}
	}

	/**
	 * 当天第一次充值
	 * 
	 * @return
	 */
	public boolean isFirstRecharge() {

		String date = GuaJiTime.DATE_FORMATOR_DAYNUM(GuaJiTime.getAM0Date());
		return !rechargeMap.containsKey(date);
	}

}
