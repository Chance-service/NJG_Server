package com.guaji.game.config;

import org.guaji.config.ConfigBase;
import org.guaji.config.ConfigManager;

@ConfigManager.XmlResource(file = "xml/accLogin.xml", struct = "map")
public class AccLoginCfg extends ConfigBase {

	/**
	 * 配置id
	 */
	@Id
	private final int id;
	
	/**
	 * 月份
	 */
	private final int month;
	
	/**
	 * 活动期间累计登录天数
	 */
	private final int days;
	
	/**
	 * 奖励物品
	 */
	private final String awards;

	public AccLoginCfg() {
		month=0;
		id = 0;
		days = 0;
		awards = "";
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

	public int getMonth() {
		return month;
	}

	/**
	 * 清理相关静态数据
	 */
	protected void clearStaticData() {
	}
}
