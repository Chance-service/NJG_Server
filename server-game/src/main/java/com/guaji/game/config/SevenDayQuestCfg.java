package com.guaji.game.config;

import org.guaji.config.ConfigBase;
import org.guaji.config.ConfigManager;

@ConfigManager.XmlResource(file = "xml/sevenDayQuest.xml", struct = "map")
public class SevenDayQuestCfg extends ConfigBase {

	/**
	 * 配置id
	 */
	@Id
	private final int id;
	
	/**
	 * 任务类型
	 */
	private final int type;
	
	/**
	 * 任务目标类型
	 */
	private final int targetType;
	/**
	 * 活动期间累计登录天数
	 */
	private final int day;
	
	/**
	 * 任务目标
	 */
	protected final int needCount;
	
	
	/**
	 * 奖励活跃点数
	 */
	protected final int point;
	
	/**
	 * 冗余参数
	 */
	protected final String param;
	
	
	
	/**
	 * 任务奖励
	 */
	protected final String award;
	

	public SevenDayQuestCfg() {
		id = 0;
		day = 0;
		needCount=0;
		point=1;
		award="";
		type=1;
		targetType=0;
		param="";
	}

	public int getId() {
		return id;
	}

	public int getDays() {
		return day;
	}


	public int getType() {
		return type;
	}


	public int getTargetType() {
		return targetType;
	}

	public int getDay() {
		return day;
	}

	public int getNeedCount() {
		return needCount;
	}

	public int getPoint() {
		return point;
	}

	public String getAward() {
		return award;
	}

	
	
	public String getParam() {
		return param;
	}

	@Override
	protected boolean assemble() {
		// TODO Auto-generated method stub
		return super.assemble();
	}

	@Override
	protected boolean checkValid() {
		// TODO Auto-generated method stub
		return super.checkValid();
	}

	/**
	 * 清理相关静态数据
	 */
	protected void clearStaticData() {
		
	}
	
}
