package com.guaji.game.config;

import org.guaji.config.ConfigBase;
import org.guaji.config.ConfigManager;

@ConfigManager.XmlResource(file = "xml/GrowthLV100.xml", struct = "map")
public class GrowthLVCfg extends ConfigBase {
	/**
	 * 配置id
	 */
	@Id
	protected final int id;	
	/**
	 * 需求等級
	 */
	protected final int LvNeed;
	/**
	 * 免費獎勵
	 */
	protected final String FreeReward;
	/**
	 * 收費獎勵
	 */
	protected final String CostReward;
	
	public GrowthLVCfg() {
		id = 0;
		LvNeed = 0;
		FreeReward = "";
		CostReward = "";
	}

	/**
	 * 数据格式转化
	 */
	@Override
	protected boolean assemble() {
		return true;
	}
	
	public int getId() {
		return id;
	}
	
	public int getLvNeed() {
		return this.LvNeed;
	}
	
	public String getFreeReward() {
		return this.FreeReward;
	}
	
	public String getCostReward() {
		return this.CostReward;
	}
}
