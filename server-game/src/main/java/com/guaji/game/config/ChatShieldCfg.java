package com.guaji.game.config;

import org.guaji.config.ConfigBase;
import org.guaji.config.ConfigManager;

@ConfigManager.XmlResource(file = "xml/chatShield.xml", struct = "list")
public class ChatShieldCfg extends ConfigBase {
	/**
	 * 关键字
	 */
	protected final String key;
	
	public ChatShieldCfg() {
		key = null;
	}

	public String getKey() {
		return key;
	}
	
	/**
	 * 清理相关静态数据
	 */
	protected void clearStaticData() {
	}
}
