package com.guaji.game.config;

import org.guaji.config.ConfigBase;
import org.guaji.config.ConfigManager;

@ConfigManager.XmlResource(file = "xml/expeditionArmoryRanking.xml", struct = "map")
public class ExpeditionArmoryRankingCfg extends ConfigBase{
	@Id
	private final int id;
	/**
	 * 对应该项奖励的最低排名
	 */
	private final int minRank;
	private final String awards;
	
	public ExpeditionArmoryRankingCfg(){
		this.id = 0;
		this.minRank = 0;
		this.awards = "";
	}

	public int getId() {
		return id;
	}

	public int getMinRank() {
		return minRank;
	}

	public String getAwards() {
		return awards;
	}
	/**
	 * 清理相关静态数据
	 */
	protected void clearStaticData() {
	}
}
