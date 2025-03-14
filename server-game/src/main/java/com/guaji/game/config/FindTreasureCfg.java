package com.guaji.game.config;

import org.guaji.config.ConfigBase;
import org.guaji.config.ConfigManager;

@ConfigManager.XmlResource(file = "xml/findTreasure.xml", struct = "list")
public class FindTreasureCfg extends ConfigBase{
	/**
	 * 类型
	 */
	@Id
	private final int type;
	/**
	 * 每日最大次数
	 */
	private final int oneDayMaxTimes;
	/**
	 * 消耗货币数量
	 */
	private final int oneTimeCost;
	/**
	 * 货币类型
	 */
	private final int currency;
	/**
	 * 货币类型注释
	 */
	private final String desc;
	
	public FindTreasureCfg(){
		type = 0;
		oneDayMaxTimes = 0;
		oneTimeCost = 0;
		currency= 0;
		desc = "";
	}

	public int getType() {
		return type;
	}

	public int getOneDayMaxTimes() {
		return oneDayMaxTimes;
	}
	
	public int getOneTimeCost() {
		return oneTimeCost;
	}

	public int getCurrency() {
		return currency;
	}

	public String getDesc() {
		return desc;
	}

	/**
	 * 清理相关静态数据
	 */
	protected void clearStaticData() {
	}
	
}
