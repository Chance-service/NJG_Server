package com.guaji.game.config;

import org.guaji.config.ConfigBase;
import org.guaji.config.ConfigManager;

@ConfigManager.XmlResource(file = "xml/headIcon.xml", struct = "map")
public class HeadIconCfg extends ConfigBase {

	/**
	 * 配置id
	 */
	@Id
	protected final int id;
	
	/**
	 *  购买价格 钻石
	 */
	protected final int buyPrice;
	

	@Override
	protected boolean assemble() {
		return super.assemble();
	}

	public HeadIconCfg() {
		id=0;
		buyPrice=0;
	}

	public int getId() {
		return id;
	}

	public int getBuyPrice() {
		return buyPrice;
	}

	
}
