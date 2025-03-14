package com.guaji.game.config;

import org.guaji.config.ConfigBase;
import org.guaji.config.ConfigManager;

@ConfigManager.XmlResource(file = "xml/worldBossAlianceAwards.xml", struct = "list")
public class WorldBossAllianceAwardsCfg extends ConfigBase {
	
	/**
	 * 奖励Id
	 */
	private final int awardsId;
	/**
	 * 最低排名
	 */
	private final int minRank;
	/**
	 * 最高排名
	 */
	private final int maxRank;
	/**
	 * 奖励内容
	 */
	private final String awards ;
	
	public WorldBossAllianceAwardsCfg(){
		this.awardsId = 0;
		this.minRank = 0;
		this.maxRank = 0;
		this.awards = "";
	}
	
	/**
	 * 获得伤害排行奖励
	 * @param awardsId
	 * @param rank
	 * @return
	 */
	public static String getPlayerRankAwards(int awardsId, int rank) {
		if(rank == 0) return null;
		for(WorldBossAllianceAwardsCfg awardsCfg : ConfigManager.getInstance().getConfigList(WorldBossAllianceAwardsCfg.class)) {
			if(awardsCfg.getAwardsId() == awardsId && awardsCfg.getMinRank() <= rank && awardsCfg.getMaxRank() >= rank) {
				return awardsCfg.awards;
			}
		}
		return "";
	}
	
	/**
	 * 获得伤害排行奖励
	 * @param awardsId
	 * @param rank
	 * @return
	 */
	public static String getAllianceRankAwards(int awardsId, int rank) {
		if(rank == 0) return null;
		for(WorldBossAllianceAwardsCfg awardsCfg : ConfigManager.getInstance().getConfigList(WorldBossAllianceAwardsCfg.class)) {
			if(awardsCfg.getAwardsId() == awardsId && awardsCfg.getMinRank() <= rank && awardsCfg.getMaxRank() >= rank) {
				return awardsCfg.awards;
			}
		}
		return "";
	}
	
	/**
	 * 获得补刀奖励
	 * @param awardsId
	 * @param rank
	 * @return
	 */
	public static String getKillAwards(int awardsId) {
		for(WorldBossAllianceAwardsCfg awardsCfg : ConfigManager.getInstance().getConfigList(WorldBossAllianceAwardsCfg.class)) {
			if(awardsCfg.getAwardsId() == awardsId &&awardsCfg.getMinRank() == 0 && awardsCfg.getMaxRank() == 0) {
				return awardsCfg.awards;
			}
		}
		return "";
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
	 * @return
	 */
	@Override
	protected boolean checkValid() {
		return true;
	}

	public int getAwardsId() {
		return awardsId;
	}

	public int getMinRank() {
		return minRank;
	}

	public int getMaxRank() {
		return maxRank;
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

