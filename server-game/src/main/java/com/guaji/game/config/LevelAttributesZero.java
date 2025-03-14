package com.guaji.game.config;

import org.guaji.config.ConfigManager;

/**
 * 零阶转生配置数据源
 * 
 */
@ConfigManager.XmlResource(file = "xml/levelAttributesZero.xml", struct = "list")
public class LevelAttributesZero extends LevelAttributes {

	public LevelAttributesZero() {
		
	}

	/**
	 * 转生静态key---职业-转生等阶-配置等级
	 */
	public String mapKey() {
		return profession + "-" + 0 + "-" + level;
	}
}
