package com.guaji.game.module.activity.fortune;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.guaji.config.ConfigManager;
import org.guaji.os.GuaJiTime;

import com.guaji.game.config.FortuneCfg;

public class FortuneStatus {
	
	/**
	 * 已经抽过奖的配置Id
	 */
	private Map<Long,List<Integer>> rewardedList ;
	/**
	 * 充值钻石数
	 */
	private Map<Long, Integer> rechargeGoldValue ;

	public FortuneStatus() {
		this.rewardedList = new HashMap<Long, List<Integer>>();
		this.rechargeGoldValue = new HashMap<>();
	}
	
	public void addRecharge(int gold) {
		long curDay = GuaJiTime.getAM0Date().getTime();
		if(this.rechargeGoldValue.containsKey(curDay)) {
			this.rechargeGoldValue.put(curDay, this.rechargeGoldValue.get(curDay) + gold);
		}else{
			this.rechargeGoldValue.put(curDay, gold);
		}
	}
	
	public int getCurDayRechargeValue() {
		long curDay = GuaJiTime.getAM0Date().getTime();
		if(this.rechargeGoldValue.containsKey(curDay)) {
			return this.rechargeGoldValue.get(curDay);
		}
		return 0;
	}

	public void addReward(int cfgId) {
		long curDay = GuaJiTime.getAM0Date().getTime();
		List<Integer> cfgIds = this.rewardedList.get(curDay);
		if(cfgIds == null) {
			cfgIds = new LinkedList<>();
			this.rewardedList.put(curDay, cfgIds);
		}
		if(!cfgIds.contains(cfgId)) {
			cfgIds.add(cfgId);
		}
	}
	
	public List<Integer> getRewardedCfgIds() {
		long curDay = GuaJiTime.getAM0Date().getTime();
		if(this.rewardedList.containsKey(curDay)) {
			return this.rewardedList.get(curDay);
		}
		return null;
	}

	public FortuneCfg getCurActiveFortuneCfg() {
		List<Integer> lastRewardCfgIds = this.getRewardedCfgIds();
		List<FortuneCfg> fortuneCfgs = ConfigManager.getInstance().getConfigList(FortuneCfg.class);
		FortuneCfg curActiveFortuneCfg = null;
		for(FortuneCfg fortuneCfg : fortuneCfgs) {
			if(lastRewardCfgIds != null && lastRewardCfgIds.contains(fortuneCfg.getId())) {
				continue;
			}else{
				curActiveFortuneCfg = fortuneCfg;
				break;
			}
		}
		return curActiveFortuneCfg;
	}

	public int getLeftRechargeValue() {
		FortuneCfg curActiveFortuneCfg = this.getCurActiveFortuneCfg();
		if(curActiveFortuneCfg == null) {
			return -1;
		}
		int curRechargeValue = this.getCurDayRechargeValue();
		int totalBeforeGot = 0;
		if(this.getRewardedCfgIds() != null) {
			for(Integer rewardedCfgId : this.getRewardedCfgIds()) {
				FortuneCfg fortuneCfg = FortuneCfg.getFortuneCfg(rewardedCfgId);
				if(fortuneCfg != null) {
					totalBeforeGot += fortuneCfg.getNeedRechargeGold();
				}
			}
		}
		int value = curActiveFortuneCfg.getNeedRechargeGold() - (curRechargeValue - totalBeforeGot);
		return value < 0 ? 0 : value;
	}
	
	public int getNextRechargeValue() {
		int curRechargeValue = this.getCurDayRechargeValue();
		int totalBeforeGot = 0;
		if(this.getRewardedCfgIds() != null) {
			for(Integer rewardedCfgId : this.getRewardedCfgIds()) {
				FortuneCfg fortuneCfg = FortuneCfg.getFortuneCfg(rewardedCfgId);
				if(fortuneCfg != null) {
					totalBeforeGot += fortuneCfg.getNeedRechargeGold();
				}
			}
		}
		return curRechargeValue - totalBeforeGot;
	}
}
