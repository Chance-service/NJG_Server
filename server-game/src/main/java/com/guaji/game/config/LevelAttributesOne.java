package com.guaji.game.config;

import org.guaji.config.ConfigManager;

/**
 * 一阶转生配置数据源
 */
@ConfigManager.XmlResource(file = "xml/levelAttributesOne.xml", struct = "list")
public class LevelAttributesOne extends LevelAttributes {

	public LevelAttributesOne() {
	}

	/**
	 * 转生静态key---职业-转生等阶-配置等级
	 */
	public String mapKey() {
		return profession + "-" + 1 + "-" + level;
	}
}
