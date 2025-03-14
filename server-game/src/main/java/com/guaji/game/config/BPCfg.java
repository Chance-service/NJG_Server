package com.guaji.game.config;

import org.guaji.config.ConfigBase;
import org.guaji.config.ConfigManager;

@ConfigManager.XmlResource(file = "xml/bp_New.xml", struct = "map")
public class BPCfg extends ConfigBase {
	/**
	 * 配置id
	 */
	@Id
	protected final int id;
	
	protected final double values ;
	
	public BPCfg() {
		id = 0;
		values =0.0;
	}
	
	public int getId() {
		return id;
	}
	
	public double getValuse() {
		return values;
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
