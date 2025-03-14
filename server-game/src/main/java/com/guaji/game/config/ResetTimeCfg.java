package com.guaji.game.config;

import org.guaji.config.ConfigBase;
import org.guaji.config.ConfigManager;

@ConfigManager.XmlResource(file = "xml/reset.xml", struct = "list")
public class ResetTimeCfg extends ConfigBase {
	/**
	 * 配置id
	 */
	@Id
	protected final int id;
	
	/**
	 * 检测时间小时
	 */
	protected final int resetHour ;
	
	/**
	 * 检测时间分钟
	 */
	protected final int resetMin ;
	
	public ResetTimeCfg() {
		id = 0;
		resetHour = 0 ;
		resetMin = 0 ;
	}

	public int getId() {
		return id;
	}


	@Override
	protected boolean assemble() {
		return true;
	}

	@Override
	protected boolean checkValid() {
		return true;
	}

	public int getResetHour() {
		return resetHour;
	}

	public int getResetMin() {
		return resetMin;
	}
	
	/**
	 * 清理相关静态数据
	 */
	protected void clearStaticData() {
	}
	
}
