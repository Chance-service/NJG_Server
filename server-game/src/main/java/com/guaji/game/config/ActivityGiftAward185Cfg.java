package com.guaji.game.config;

import org.guaji.config.ConfigBase;
import org.guaji.config.ConfigManager;

@ConfigManager.XmlResource(file = "xml/characterGiftAward172.xml", struct = "map")
public class ActivityGiftAward185Cfg extends ConfigBase {
	/**
	 * 配置id
	 */
	@Id
	private final int id;
	/**
	 * 奖励
	 */
	private final String awards;

	public ActivityGiftAward185Cfg() {
		this.id = 0;
		this.awards = "";
	}

	public int getId() {
		return id;
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
