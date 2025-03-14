package com.guaji.game.module.activity.activity162;

import java.util.HashSet;
import java.util.Set;

/**
 * 成長獎勵
 */
public class Activity162Status {
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
	private boolean CostFlag;

	
	public Activity162Status() {
		FreeCfgId= new HashSet<>();
		CostCfgId = new HashSet<>();
		CostFlag = false;
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
	
	public boolean getCostFlag() {
		return this.CostFlag;
	}
	
	public void setCostFlag(boolean flag) {
		this.CostFlag = flag;
	}

}
