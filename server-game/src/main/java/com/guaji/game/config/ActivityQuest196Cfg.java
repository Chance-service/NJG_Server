package com.guaji.game.config;

import org.guaji.config.ConfigBase;
import org.guaji.config.ConfigManager;

@ConfigManager.XmlResource(file = "xml/ActivityQuest_2.xml", struct = "map")
public class ActivityQuest196Cfg extends ConfigBase {
	
	/**
	 * 配置id
	 */
	@Id
	private final int id;
	/**
	 * 種類
	 */
	private final int type;
	/**
	 * 目標
	 */
	private final int target;
	
	/**
	 * 奖励
	 */
	private final String awards;

	public ActivityQuest196Cfg() {
		this.id = 0;
		this.type = 0;
		this.target = 0;
		this.awards = "";
	}

	public int getId() {
		return id;
	}

	public int getType() {
		return type;
	}

	public int getTarget() {
		return target;
	}

	public String getAwards() {
		return awards;
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
	 * 
	 * @return
	 */
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
