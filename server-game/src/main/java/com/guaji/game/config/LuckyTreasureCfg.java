package com.guaji.game.config;

import org.guaji.config.ConfigBase;
import org.guaji.config.ConfigManager;

@ConfigManager.XmlResource(file = "xml/luckyTreasure.xml", struct = "map")
public class LuckyTreasureCfg extends ConfigBase {
	/**
	 * 配置id
	 */
	@Id
	private final int id;
	/**
	 * 奖励天数
	 */
	private final int days;
	/**
	 * 奖励物品
	 */
	private final String awards;
	
	public LuckyTreasureCfg(){
		this.id = 0;
		this.days = 0;
		this.awards = "";
	}
	
	public int getId() {
		return id;
	}

	public int getDays() {
		return days;
	}

	public String getAwards() {
		return awards;
	}
	
	/**
	 * 清理相关静态数据
	 */
	protected void clearStaticData() {
	}
}

