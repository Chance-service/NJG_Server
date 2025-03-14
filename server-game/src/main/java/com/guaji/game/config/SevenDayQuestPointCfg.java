package com.guaji.game.config;

import org.guaji.config.ConfigBase;
import org.guaji.config.ConfigManager;

@ConfigManager.XmlResource(file = "xml/sevenDayQuestPoint.xml", struct = "map")
public class SevenDayQuestPointCfg extends ConfigBase{
	
	/**
	 * 活跃点数
	 */
	@Id
	protected final int pointNumber;
	/**
	 * 任务奖励
	 */
	protected final String award;

	public SevenDayQuestPointCfg() {
		pointNumber = 0;
		award = null;

	}

	@Override
	protected boolean assemble() {

		return super.assemble();
	}

	@Override
	protected boolean checkValid() {
		return super.checkValid();
	}
	
	public int getPointNumber()
	{
		return pointNumber;
	}
	
	public String getAward()
	{
		return award;
	}
	
	/**
	 * 清理相关静态数据
	 */
	protected void clearStaticData() {
	}
	
}
