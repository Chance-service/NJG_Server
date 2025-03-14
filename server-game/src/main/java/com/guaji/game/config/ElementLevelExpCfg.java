package com.guaji.game.config;

import org.guaji.config.ConfigBase;
import org.guaji.config.ConfigManager;

@ConfigManager.XmlResource(file = "xml/elementLevelExp.xml", struct = "map")
public class ElementLevelExpCfg extends ConfigBase {
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
	protected final int exp;
	/**
	 * 吞噬提供的经验
	 */
	private final int swallowExp;
	/**
	 * 吞噬需要的金币数目
	 */
	private final int swallowCoins;

	public ElementLevelExpCfg() {
		level = 0;
		exp = 0;
		swallowExp = 0;
		swallowCoins = 0;
	}

	public int getLevel() {
		return level;
	}

	public int getExp() {
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

	public int getSwallowExp() {
		return swallowExp;
	}
	
	public static ElementLevelExpCfg getCfgByLevel(int level) {
		return ConfigManager.getInstance().getConfigByKey(ElementLevelExpCfg.class, level);
	}

	public int getSwallowCoins() {
		return swallowCoins;
	}
}
