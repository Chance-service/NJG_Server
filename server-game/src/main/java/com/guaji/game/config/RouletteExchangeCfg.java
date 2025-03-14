package com.guaji.game.config;

import org.guaji.config.ConfigBase;
import org.guaji.config.ConfigManager;

@ConfigManager.XmlResource(file = "xml/rouletteExchange.xml", struct = "map")
public class RouletteExchangeCfg extends ConfigBase {
	@Id
	private final int id;
	/**
	 * 兑换消耗积分
	 */
	private final int costCredits;
	/**
	 * 兑换物品列表
	 */
	private final String exchangeItems;
	
	public RouletteExchangeCfg(){
		id = 0;
		costCredits = 0;
		exchangeItems = "";
	}

	public int getId() {
		return id;
	}

	public int getCostCredits() {
		return costCredits;
	}

	public String getExchangeItems() {
		return exchangeItems;
	}
	
	/**
	 * 清理相关静态数据
	 */
	protected void clearStaticData() {
	}
}
