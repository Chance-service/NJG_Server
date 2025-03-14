package com.guaji.game.config;

import org.guaji.config.ConfigBase;
import org.guaji.config.ConfigManager;

/**
 * 阵营战连胜奖励配置
 * @author xpf
 */
@ConfigManager.XmlResource(file = "xml/campWarWinStreak.xml", struct = "map")
public class CampWarWinStreakCfg extends ConfigBase {
	
	/**
	 * 配置id
	 */
	@Id
	private final int id;
	/**
	 * 连胜次数
	 */
	private final int winStreak;
	/**
	 * 连胜奖励金币基数(金币 = winCoinsRatio * winner's level)
	 */
	private final int winCoinsRatio;
	/**
	 * 连胜奖励声望
	 */
	private final int winReputation;
	/**
	 * 连胜终结奖励金币基数(金币 = winCoinsRatio * loser's level)
	 */
	private final int loseCoinsRatio;
	/**
	 * 连胜终结奖励声望
	 */
	private final int loseReputation;
	
	public CampWarWinStreakCfg(){
		id = 0;
		winStreak = 0;
		winCoinsRatio = 0;
		winReputation = 0;
		loseCoinsRatio = 0;
		loseReputation = 0;
	}

	public int getId() {
		return id;
	}

	public int getWinStreak() {
		return winStreak;
	}

	public int getWinCoinsRatio() {
		return winCoinsRatio;
	}

	public int getWinReputation() {
		return winReputation;
	}

	public int getLoseCoinsRatio() {
		return loseCoinsRatio;
	}

	public int getLoseReputation() {
		return loseReputation;
	}
	
	@Override
	protected boolean assemble() {
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
