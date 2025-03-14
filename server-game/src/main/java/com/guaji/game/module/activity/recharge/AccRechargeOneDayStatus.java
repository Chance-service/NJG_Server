package com.guaji.game.module.activity.recharge;

import java.util.HashSet;
import java.util.Set;

import org.guaji.os.GuaJiTime;

/**
 * 累计充值某一天的活动状态
 */
public class AccRechargeOneDayStatus {
	// 活动期间每日日期int
	private int date;
	// 活动期间累计充值Money数
	private int accRechargeMoney;
	// 该期活动已领取的奖励CfgId
	private Set<Integer> gotAwardCfgIds;
	
	public AccRechargeOneDayStatus(){
		this.date = Integer.valueOf(GuaJiTime.DATE_FORMATOR_DAYNUM(GuaJiTime.getAM0Date()));
		this.accRechargeMoney = 0;
		this.gotAwardCfgIds = new HashSet<Integer>();
	}
	
	public int getDate() {
		return date;
	}
	
	public int getAccRechargeMoney() {
		return accRechargeMoney;
	}
	
	public Set<Integer> getGotAwardCfgIds() {
		return gotAwardCfgIds;
	}

	/**
	 * 增加当日充值数额
	 */
	public void addAccRechargeAmount(int amount){
		this.accRechargeMoney += amount;
	}
	
	/**
	 * 当日对应配置的奖励是否已经领取过
	 * @param cfgId
	 * @return
	 */
	public boolean isAlreadyGot(int cfgId){
		if(this.gotAwardCfgIds.contains((Integer) cfgId)){
			return true;
		}
		return false;
	}
	
	/**
	 * 添加当日领取过的cfgId
	 */
	public void addGotAwardCfgId(int cfgId){
		this.gotAwardCfgIds.add(cfgId);
	}
}