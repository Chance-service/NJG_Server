package com.guaji.game.config;

import org.guaji.config.ConfigBase;
import org.guaji.config.ConfigManager;

@ConfigManager.XmlResource(file = "xml/gemCompound.xml", struct = "map")
public class GemCompoundCfg extends ConfigBase  {
	// 需要升级的宝石等级
	@Id
	private final int gemLevel;
	//消耗宝石的等级
	private final int costGemLevel;
	// 消耗相同宝石数量
	private final int sameCostGem;
	// 消耗不同宝石（等级相同）数量
	private final int diffCostGem;
	// 相同宝石消耗的钻石
	private final int sameCostMoney;
	// 不同宝石消耗的钻石
	private final int diffCostMoney;
	
	public GemCompoundCfg() {
		gemLevel = 1;
		costGemLevel = 1;
		sameCostGem = 1;
		diffCostGem = 1;
		sameCostMoney= 0;
		diffCostMoney = 0;
	}

	public int getGemLevel() {
		return gemLevel;
	}

	public int getCostGemLevel() {
		return costGemLevel;
	}

	public int getSameCostGem() {
		return sameCostGem;
	}

	public int getDiffCostGem() {
		return diffCostGem;
	}

	public int getSameCostMoney() {
		return sameCostMoney;
	}

	public int getDiffCostMoney() {
		return diffCostMoney;
	}
	
	/**
	 * 清理相关静态数据
	 */
	protected void clearStaticData() {
	}
}
