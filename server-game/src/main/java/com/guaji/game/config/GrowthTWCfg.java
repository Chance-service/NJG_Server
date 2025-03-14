package com.guaji.game.config;

import org.guaji.config.ConfigBase;
import org.guaji.config.ConfigManager;

@ConfigManager.XmlResource(file = "xml/GrowthTW102.xml", struct = "map")
public class GrowthTWCfg extends ConfigBase {
	/**
	 * 配置id
	 */
	@Id
	protected final int id;	
	/**
	 * 需求等級
	 */
	protected final int TWID;
	/**
	 * 免費獎勵
	 */
	protected final String FreeReward;
	/**
	 * 收費獎勵
	 */
	protected final String CostReward;
	
	public GrowthTWCfg() {
		id = 0;
		TWID = 0;
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
	
	public int getTWID() {
		return this.TWID;
	}
	
	public String getFreeReward() {
		return this.FreeReward;
	}
	
	public String getCostReward() {
		return this.CostReward;
	}
}
