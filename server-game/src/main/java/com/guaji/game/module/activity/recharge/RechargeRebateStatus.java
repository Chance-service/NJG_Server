package com.guaji.game.module.activity.recharge;


import org.guaji.os.GuaJiTime;

/**
 * 充值返利活动状态
 * @author xpf
 */
public class RechargeRebateStatus {
	// 活动期间累计充值数额
	private int accRechargeAmount;
	// 上次返利时间
	private int lastRebateTime;
	
	public int getLastRebateTime() {
		return lastRebateTime;
	}
	
	public void setLastRebateTime(int lastRebateTime) {
		this.lastRebateTime = lastRebateTime;
	}
	
	public int getAccRechargeAmount() {
		return accRechargeAmount;
	}
	
	/**
	 *  增加玩家活动期间的累计充值数额
	 * @param amount
	 */
	public void addAccRechargeAmount(int amount){
		accRechargeAmount += amount;
	}
	
	/**
	 * 今日返利是否已经领取
	 * @return
	 */
	public int todayIsGet(){
		int today0Time = (int)(GuaJiTime.getAM0Date().getTime()/1000);
		if(lastRebateTime >= today0Time){
			return 1;
		} else {
			return 0;
		}
	}
	
	/**
	 * 是否红点提示，是否领取过了
	 */
	public boolean showRedPoint()
	{
		if(todayIsGet()<=0)
		{
			return true;
		}
		return false;
	}
}
