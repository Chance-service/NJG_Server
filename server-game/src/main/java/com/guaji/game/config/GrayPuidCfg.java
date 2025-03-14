package com.guaji.game.config;

import org.guaji.config.ConfigBase;
import org.guaji.config.ConfigManager;

@ConfigManager.XmlResource(file = "xml/grayPuid.xml", struct = "map")
public class GrayPuidCfg extends ConfigBase {
	/**
	 * 灰度账号
	 */
	@Id
	protected final String puid;

	public GrayPuidCfg() {
		puid = null;
	}

	public String getPuid() {
		return puid;
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
