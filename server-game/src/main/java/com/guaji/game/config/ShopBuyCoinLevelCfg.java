package com.guaji.game.config;


import org.guaji.config.ConfigBase;
import org.guaji.config.ConfigManager;

@ConfigManager.XmlResource(file = "xml/shopBuyCoinLevel.xml", struct = "map")
public class ShopBuyCoinLevelCfg extends ConfigBase {
	/**
	 * 等级
	 */
	@Id
	private final int level;

	/**
	 * 金币系数
	 */
	private final int goldRatio;

	public ShopBuyCoinLevelCfg() {
		level = 0;
		goldRatio = 0;
	}

	public int getLevel() {
		return level;
	}

	public int getGoldRatio() {
		return goldRatio;
	}
	
	/**
	 * 清理相关静态数据
	 */
	protected void clearStaticData() {
	}

}
