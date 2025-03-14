package com.guaji.game.config;

import org.guaji.config.ConfigBase;
import org.guaji.config.ConfigManager;

@ConfigManager.XmlResource(file = "xml/EighteenDoubleBuy.xml", struct = "map")
public class EighteenPrincesDoubleBuyCfg extends ConfigBase{


	@Id
	private final int id;
	
	
	/**
	 * 奖励物品
	 */
	private final int cost;
	
	public EighteenPrincesDoubleBuyCfg() {
		id = 0;
		cost =0;
	}

	public int getId() {
		return id;
	}

	public int getCost() {
		return cost;
	}
	
	
	
}
