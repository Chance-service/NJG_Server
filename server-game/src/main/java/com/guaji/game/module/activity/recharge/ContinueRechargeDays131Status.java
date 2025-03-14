package com.guaji.game.module.activity.recharge;

import com.guaji.game.config.ContinueRecharge131Cfg;
import org.guaji.config.ConfigManager;
import org.guaji.os.GuaJiTime;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * 累积充值天数
 */
public class ContinueRechargeDays131Status {
	// 活动期间累计充值天数
	private int continueRechargeDays;
	// 上次充值时间
	private int lastRechargeTime;
	// 该期活动已领取的奖励CfgId
	private Set<Integer> gotAwardCfgIds;

	public ContinueRechargeDays131Status(){
		continueRechargeDays = 0;
		lastRechargeTime = 0;
		gotAwardCfgIds = new HashSet<Integer>();
	}

	public int getContinueRechargeDays() {
		return continueRechargeDays;
	}

	public void setContinueRechargeDays(int continueRechargeDays) {
		this.continueRechargeDays = continueRechargeDays;
	}

	public int getLastRechargeTime() {
		return lastRechargeTime;
	}

	public void setLastRechargeTime(int lastRechargeTime) {
		this.lastRechargeTime = lastRechargeTime;
	}

	public Set<Integer> getGotAwardCfgIds() {
		return gotAwardCfgIds;
	}

	public void setGotAwardCfgIds(Set<Integer> gotAwardCfgIds) {
		this.gotAwardCfgIds = gotAwardCfgIds;
	}
	
	/**
	 * 增加连续充值天数
	 */
	public boolean addContinueRechargeDays(){
		int nextDay0Time = (int) (GuaJiTime.getNextAM0Date()/1000);
		if(nextDay0Time > lastRechargeTime){
			continueRechargeDays++;
			lastRechargeTime = nextDay0Time;
			return true;
		}
		return false;
	}
	
	/**
	 * 对应配置对应的奖励是否已经领取过
	 * @param cfgId
	 * @return
	 */
	public boolean isAlreadyGot(int cfgId){
		if(gotAwardCfgIds.contains((Integer) cfgId)){
			return true;
		}
		return false;
	}
	
	/**
	 * 是否红点提示，已经充值，且奖励没有领取
	 */
	public boolean showRedPoint()
	{
		Map<Object, ContinueRecharge131Cfg> cfgList = ConfigManager.getInstance().getConfigMap(ContinueRecharge131Cfg.class);

		for(ContinueRecharge131Cfg cfg:cfgList.values())
		{

			if(getContinueRechargeDays() < cfg.getDay()){
				// 连续充值天数未达到
				return false;
			}
			if(!isAlreadyGot(cfg.getId()))
			{
				return true;
			}
		}
		return false;
	}

	/**
	 * 添加领取过的cfgId
	 */
	public void addGotAwardCfgId(int cfgId){
		gotAwardCfgIds.add(cfgId);
	}
}
