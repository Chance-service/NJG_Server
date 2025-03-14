package com.guaji.game.config;

import org.guaji.config.ConfigBase;
import org.guaji.config.ConfigManager;

@ConfigManager.XmlResource(file = "xml/growthfund.xml", struct = "map")
public class GrowthFundCfg extends ConfigBase {

	@Id
	private final int id;
	/**
	 * 领取等级限制
	 */
	protected final int levelLimit;
	/**
	 * 奖励信息
	 */
	protected final String awardStr;

	public GrowthFundCfg() {
		this.id = 0;
		this.levelLimit = 0;
		this.awardStr = null;
	}

	public int getLevelLimit() {
		return levelLimit;
	}

	public String getAwardStr() {
		return awardStr;
	}

	public int getId() {
		return id;
	}
}
