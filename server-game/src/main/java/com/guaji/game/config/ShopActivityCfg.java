package com.guaji.game.config;

import org.guaji.config.ConfigManager;

@ConfigManager.XmlResource(file = "xml/shopActivityCfg.xml", struct = "list")
public class ShopActivityCfg extends ShopCfg {
	/**
	 * 清理相关静态数据
	 */
	protected void clearStaticData() {
	}
}
