package com.guaji.game.config;

import org.guaji.config.ConfigBase;
import org.guaji.config.ConfigManager;

@ConfigManager.XmlResource(file = "xml/gmPuid.xml", struct = "map")
public class GmPuidCfg extends ConfigBase {
	/**
	 * gm账号
	 */
	@Id
	protected final String puid;
	protected final int type;
	
	public GmPuidCfg() {
		puid = null;
		type = 0;
	}

	public String getPuid() {
		return puid;
	}

	public int getType() {
		return type;
	}
	
	@Override
	protected boolean assemble() {
		return true;
	}

	@Override
	protected boolean checkValid() {
		return true;
	}
	
	/**
	 * 清理相关静态数据
	 */
	protected void clearStaticData() {
	}
}
