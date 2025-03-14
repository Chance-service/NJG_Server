package com.guaji.game.config;

import org.guaji.config.ConfigBase;
import org.guaji.config.ConfigManager;

/**
 * 竞技场排名奖励相关配置
 * 
 */
@ConfigManager.XmlResource(file = "xml/SeasonTowerRankAward.xml", struct = "list")
public class SeasonTowerRankAwardCfg extends ConfigBase {
	/**
	 * 配置id
	 */
	@Id
	protected final int id;
	/**
	 * 规则适用的最低排名
	 */
	protected final int minRank;
	/**
	 * 奖励字物品符串
	 */
	protected final String TotalAward;
	
	public SeasonTowerRankAwardCfg() {
		id = 0;
		minRank = 0;
		TotalAward = "";
	}

	public int getId() {
		return id;
	}
	

	public int getMinRank() {
		return minRank;
	}

	public String getTotalAward() {
		return TotalAward;
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
