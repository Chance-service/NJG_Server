package com.guaji.game.config;

import org.guaji.config.ConfigBase;
import org.guaji.config.ConfigManager;

@ConfigManager.XmlResource(file = "xml/dailyQuest.xml", struct = "map")
public class DailyQuestCfg extends ConfigBase{
	
	/**
	 * 任务id
	 */
	@Id
	protected final int id;
	
	/**
	 * 任务类型
	 */
	protected final int type;
	
	/**
	 * 任务目标
	 */
	protected final int completeCountCfg;
	
	/**
	 * 任务最小接受等级
	 */
	protected final int minLevel;
	
	/**
	 * 任务最大接受等级
	 */
	protected final int maxLevel;
	
	/**
	 * 奖励活跃点数
	 */
	protected final int point;
	
	/**
	 * 任务奖励
	 */
	protected final String award;

	public DailyQuestCfg() {
		id = 0;
		type = 0;
		completeCountCfg = 0;
		minLevel=0;
		maxLevel= 0;
		point = 0;
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
	
	public int getId()
	{
		return id;
	}
	
	public int getType()
	{
		return type;
	}
	
	public int getCompleteCountCfg()
	{
		return completeCountCfg;
	}
	
	public int getMinLevel()
	{
		return minLevel;
	}
	
	public int getMaxlevel()
	{
		return maxLevel;
	}
	
	public String getAward()
	{
		return award;
	}
	
	public int getPoint()
	{
		return point;
	}
	
	/**
	 * 清理相关静态数据
	 */
	protected void clearStaticData() {
	}
	
}
