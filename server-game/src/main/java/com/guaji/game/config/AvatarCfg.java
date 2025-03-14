package com.guaji.game.config;

import org.guaji.config.ConfigBase;

//@ConfigManager.XmlResource(file = "xml/avatar.xml", struct = "map")
public class AvatarCfg extends ConfigBase {
	/**
	 * 配置id
	 */
	@Id
	private final int avatarId;
	private final int days;
	private final String attrs;
	
 	public AvatarCfg() {
 		avatarId = 0;
		days = 0;
		attrs = "";
	}

	public int getAvatarId() {
		return avatarId;
	}

	public int getDays() {
		return days;
	}

	public String getAttrs() {
		return attrs;
	}
}
