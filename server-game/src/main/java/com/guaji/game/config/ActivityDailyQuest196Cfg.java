package com.guaji.game.config;

import org.guaji.config.ConfigBase;
import org.guaji.config.ConfigManager;

@ConfigManager.XmlResource(file = "xml/ActivityDailyQuest_2.xml", struct = "map")
public class ActivityDailyQuest196Cfg extends ConfigBase{
	
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
	 * 奖励活跃点数
	 */
	protected final int point;
	
	/**
	 * 任务奖励
	 */
	protected final String award;

	public ActivityDailyQuest196Cfg() {
		id = 0;
		type = 0;
		completeCountCfg = 0;
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
