package com.guaji.game.config;

import org.guaji.config.ConfigManager;

@ConfigManager.XmlResource(file = "xml/luckyTreasureDrops.xml", struct = "map")
public class LuckyTreasureDropsCfg extends AwardGroupCfg{
	public LuckyTreasureDropsCfg(){
		super();
	}
	
	/**
	 * 清理相关静态数据
	 */
	protected void clearStaticData() {
	}
}
