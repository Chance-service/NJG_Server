package com.guaji.game.config;

import org.guaji.config.ConfigBase;
import org.guaji.config.ConfigManager;

@ConfigManager.XmlResource(file = "xml/roleLevelExp.xml", struct = "map")
public class LevelExpCfg extends ConfigBase {
	/**
	 * 最大等级
	 */
	private static int maxLevel = 0;
	/**
	 * 等级
	 */
	@Id
	protected final int level;
	/**
	 * 所需经验
	 */
	protected final long exp;

	public LevelExpCfg() {
		level = 0;
		exp = 0;
	}

	public int getLevel() {
		return level;
	}

	public long getExp() {
		return exp;
	}

	/**
	 * 获取配置的最大等级
	 * 
	 * @return
	 */
	public static int getMaxLevel() {
		return maxLevel;
	}

	@Override
	protected boolean assemble() {
		if (maxLevel < level) {
			maxLevel = level;
		}
		return true;
	}

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
