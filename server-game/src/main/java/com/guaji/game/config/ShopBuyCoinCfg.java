package com.guaji.game.config;

import org.guaji.config.ConfigBase;
import org.guaji.config.ConfigManager;

@ConfigManager.XmlResource(file = "xml/shopBuyCoinCfg.xml", struct = "map")
public class ShopBuyCoinCfg extends ConfigBase {
	/**
	 * 购买次数
	 */
	@Id
	private final int buyCount;

	/**
	 * 所需钻石
	 */
	private final int needGold;

	/**
	 * 产出金币系数
	 */
	private final int coinFactor;

	public ShopBuyCoinCfg() {
		buyCount = 0;
		needGold = 0;
		coinFactor = 0;
	}

	public int getBuyCount() {
		return buyCount;
	}

	public int getNeedGold() {
		return needGold;
	}

	public int getCoinFactor() {
		return coinFactor;
	}
	
	/**
	 * 清理相关静态数据
	 */
	protected void clearStaticData() {
	}
}
