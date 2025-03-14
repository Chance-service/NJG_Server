package com.guaji.game.config;

import org.guaji.config.ConfigBase;
import org.guaji.config.ConfigManager;

@ConfigManager.XmlResource(file = "xml/GrowthCH101.xml", struct = "map")
public class GrowthCHCfg extends ConfigBase {
	/**
	 * 配置id
	 */
	@Id
	protected final int id;	
	/**
	 * 需求等級
	 */
	protected final int CHID;
	/**
	 * 付費type
	*/
	protected final int type;
	/**
	 * 免費獎勵
	 */
	protected final String FreeReward;
	/**
	 * 收費獎勵
	 */
	protected final String CostReward;
	
	public GrowthCHCfg() {
		id = 0;
		CHID = 0;
		type = 0;
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
	
	public int getCHID() {
		return this.CHID;
	}
	
	public int getType() {
		return this.type;
	}
	
	public String getFreeReward() {
		return this.FreeReward;
	}
	
	public String getCostReward() {
		return this.CostReward;
	}
}
