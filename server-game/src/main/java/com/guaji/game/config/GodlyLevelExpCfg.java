package com.guaji.game.config;

import java.util.List;

import org.guaji.config.ConfigBase;
import org.guaji.config.ConfigManager;

@ConfigManager.XmlResource(file = "xml/godlyLevelExp.xml", struct = "list")
public class GodlyLevelExpCfg extends ConfigBase {
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
	 * 第二条属性所需经验
	 */
	private final int exp2;
	
	public GodlyLevelExpCfg() {
		level = 0;
		exp = 0;
		exp2 = 0;
	}

	public int getLevel() {
		return level;
	}

	public int getExp() {
		return exp;
	}

	/**
	 * 根据总经验获得等级
	 * @param exp
	 * @return
	 */
	public static int getLevelByExp(int exp) {
		List<GodlyLevelExpCfg> godlyLevelExpCfgs = ConfigManager.getInstance().getConfigList(GodlyLevelExpCfg.class);
		int index  = 0;
		GodlyLevelExpCfg last = godlyLevelExpCfgs.get(godlyLevelExpCfgs.size() - 1);
		if(exp >= last.getExp()) {
			return last.getLevel();
		}
		for (index = 0; index < godlyLevelExpCfgs.size(); index++) {
			if (godlyLevelExpCfgs.get(index).getExp() > exp) {
				return godlyLevelExpCfgs.get(index).getLevel();
			}
		}
		return 0;
	}
	
	public static GodlyLevelExpCfg getConfigByLevel(int level) {
		List<GodlyLevelExpCfg> godlyLevelExpCfgs = ConfigManager.getInstance().getConfigList(GodlyLevelExpCfg.class);
		for(GodlyLevelExpCfg levelExpCfg : godlyLevelExpCfgs) {
			if(levelExpCfg.getLevel() == level) {
				return levelExpCfg;
			}
		}
		return null;
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

	public int getExp2() {
		return exp2;
	}

	public static int getLevelByExp2(int starExp) {
		List<GodlyLevelExpCfg> godlyLevelExpCfgs = ConfigManager.getInstance().getConfigList(GodlyLevelExpCfg.class);
		int index  = 0;
		GodlyLevelExpCfg last = godlyLevelExpCfgs.get(godlyLevelExpCfgs.size() - 1);
		if(starExp >= last.getExp2()) {
			return last.getLevel();
		}
		for (index = 0; index < godlyLevelExpCfgs.size(); index++) {
			if (godlyLevelExpCfgs.get(index).getExp2() > starExp) {
				return godlyLevelExpCfgs.get(index).getLevel();
			}
		}
		return 0;
	}

	/**
	 * 清理相关静态数据
	 */
	protected void clearStaticData() {
	}
}
