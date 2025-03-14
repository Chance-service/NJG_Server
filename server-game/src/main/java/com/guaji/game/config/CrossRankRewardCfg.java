package com.guaji.game.config;

import org.guaji.config.ConfigBase;
import org.guaji.config.ConfigManager;

/**
 * 跨服竞技每日排名奖励配置
 * 
 * @author Nannan.Gao
 * @date 2017-8-21 15:59:31
 */
@ConfigManager.XmlResource(file = "xml/crossRankReward.xml", struct = "list")
public class CrossRankRewardCfg extends ConfigBase {
	
	/**
	 * 对应该项奖励的最低排名
	 */
	private final int minRank;
	
	/**
	 * 奖励
	 */
	private final String awards;
	
	public CrossRankRewardCfg() {
		this.minRank = 0;
		this.awards = "";
	}

	public int getMinRank() {
		return minRank;
	}

	public String getAwards() {
		return awards;
	}

}
