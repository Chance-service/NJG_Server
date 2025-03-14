package com.guaji.game.config;

import org.guaji.config.ConfigBase;
import org.guaji.config.ConfigManager;

@ConfigManager.XmlResource(file = "xml/koreanBroadcast.xml", struct = "list")
public class KoreanBroadcastCfg extends ConfigBase{
	
	/**
	 * 领奖所需累计充值数额
	 */
	private final String message;
	
	public KoreanBroadcastCfg() {
		this.message = "";
	}

	public String getMessage() {
		return message;
	}
	
}
