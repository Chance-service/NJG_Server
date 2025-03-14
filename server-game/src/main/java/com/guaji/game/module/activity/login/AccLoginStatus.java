package com.guaji.game.module.activity.login;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.guaji.config.ConfigManager;
import org.guaji.os.GuaJiTime;

import com.guaji.game.config.AccLoginCfg;

/**
 * 累计登录活动数据
 */
public class AccLoginStatus {

	/**
	 * 活动期间累计登录天数
	 */
	private int totalLoginDays;

	/**
	 * 上次登录时间
	 */
	private int lastLoginTime;

	/**
	 * 已领取的奖励配置ID
	 */
	private Set<Integer> gotAwardCfgIds;

	
	public AccLoginStatus() {

		totalLoginDays = 0;
		lastLoginTime = 0;
		gotAwardCfgIds = new HashSet<Integer>();
	}

	public int getTotalLoginDays() {
		return totalLoginDays;
	}

	public int getLastLoginTime() {
		return lastLoginTime;
	}

	public void setLastLoginTime(int lastLoginTime) {
		this.lastLoginTime = lastLoginTime;
	}

	public Set<Integer> getGotAwardCfgIds() {
		return gotAwardCfgIds;
	}

	public void addGotAwardCfgId(int cfgId) {
		gotAwardCfgIds.add(cfgId);
	}

	public int refreshLoginDays() {
		
		int today0Time = (int) (GuaJiTime.getAM0Date().getTime() / 1000);
		if (lastLoginTime < today0Time) {
			totalLoginDays += 1;
			lastLoginTime = GuaJiTime.getSeconds();
		}
		return totalLoginDays;
	}
	
	/**
	 * 是否红点提示，是否领取过了
	 */
	public boolean showRedPoint(){
		Map<Object, AccLoginCfg> cfgs = ConfigManager.getInstance().getConfigMap(AccLoginCfg.class);
		if(cfgs == null || cfgs.isEmpty()){
			return false;
		}
		refreshLoginDays();
		for(AccLoginCfg cfg : cfgs.values()){
			//累计登录达到，并且没有领取
			if(getTotalLoginDays() >= cfg.getDays() && !getGotAwardCfgIds().contains(cfg.getId())){
				return true;
			}
		}
		return false;
	}
}
