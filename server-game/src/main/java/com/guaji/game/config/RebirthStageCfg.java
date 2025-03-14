package com.guaji.game.config;

import org.guaji.config.ConfigBase;
import org.guaji.config.ConfigManager;

/**
 * 转生限定配置
 */
@ConfigManager.XmlResource(file = "xml/rebirthStage.xml", struct = "map")
public class RebirthStageCfg extends ConfigBase {

	private static int maxStage = 0;

	/**
	 * 转生等阶
	 */
	@Id
	private final int stage;

	/**
	 * 转生等级
	 */
	private final int levelLimit;

	/**
	 * 转生地图配置ID
	 */
	private final int rebirthMapId;

	public RebirthStageCfg() {
		this.stage = 0;
		this.levelLimit = 0;
		this.rebirthMapId = 0;
	}

	public int getStage() {
		return stage;
	}

	public int getLevelLimit() {
		return levelLimit;
	}

	public int getRebirthMapId() {
		return rebirthMapId;
	}

	/**
	 * 获取最大转生阶
	 * 
	 * @return
	 */
	public static int getMaxStage() {
		return maxStage;
	}

	@Override
	protected boolean assemble() {
		maxStage = maxStage < stage ? stage : maxStage;
		return true;
	}

	@Override
	protected boolean checkValid() {
		return true;
	}

}
