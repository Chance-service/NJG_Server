package com.guaji.game.config;

import org.guaji.config.ConfigBase;
import org.guaji.config.ConfigManager;

@ConfigManager.XmlResource(file = "xml/clientSetting.xml", struct = "list")
public class ClientSettingCfg extends ConfigBase {
	
	private final String key;
	
	private final String value;
	
	public ClientSettingCfg(){
		this.key = null;
		this.value = null;
	}
	
	
	/**
	 * 数据格式转化
	 */
	@Override
	protected boolean assemble() {
		return true;
	}
	
	/**
	 * 检测有消息
	 * @return
	 */
	@Override
	protected boolean checkValid() {
		return true;
	}


	public String getKey() {
		return key;
	}


	public String getValue() {
		return value;
	}
	
	/**
	 * 清理相关静态数据
	 */
	protected void clearStaticData() {
	}
}

