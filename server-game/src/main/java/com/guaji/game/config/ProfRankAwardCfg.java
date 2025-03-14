package com.guaji.game.config;

import org.guaji.config.ConfigBase;
import org.guaji.config.ConfigManager;

/**
 * 职业排行奖励配置
 * @author WeiY
 *
 */
@ConfigManager.XmlResource(file = "xml/profRankAward.xml", struct = "map")
public class ProfRankAwardCfg extends ConfigBase{
	
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
	protected final String awardStr;

	public ProfRankAwardCfg() {
		id = 0;
		minRank = 0;
		awardStr = "";
	}

	public int getId() {
		return id;
	}

	public int getMinRank() {
		return minRank;
	}

	public String getAwardStr() {
		return awardStr;
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
