package com.guaji.game.module.activity.activity163;

import java.util.HashSet;
import java.util.Set;

/**
 * 成長獎勵
 */
public class Activity163Status {
	/**
	 * 紀錄已領免費CfgId
	 */
	private Set<Integer> FreeCfgId;
	/**
	 * 紀錄已領收費CfgId
	 */
	private Set<Integer> CostCfgId;
	/**
	 * 收費狀態
	 */
	private Set<Integer> CostFlag;

	
	public Activity163Status() {
		FreeCfgId= new HashSet<>();
		CostCfgId = new HashSet<>();
		CostFlag = new HashSet<>();
	}
	
	public Set<Integer> getFreeCfgId(){
		return this.FreeCfgId;
	}
	
	public void setFreeCfgId(int cfgId) {
		if (!FreeCfgId.contains(cfgId)) {
			FreeCfgId.add(cfgId);
		}
	}
	
	public Set<Integer> getCostCfgId(){
		return this.CostCfgId;
	}
	
	public void setCostCfgId(int cfgId) {
		if (!CostCfgId.contains(cfgId)) {
			CostCfgId.add(cfgId);
		}
	}
	
	public Set<Integer> getCostFlag() {
		return this.CostFlag;
	}
	
	public void setCostFlag(int type) {
		if (!CostFlag.contains(type)) {
			CostFlag.add(type);
		}
	}

}
