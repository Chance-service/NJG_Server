package com.guaji.game.config;

import org.guaji.config.ConfigBase;
import org.guaji.config.ConfigManager;
/**
 * 排行献礼活动配置
 * @author qianhang
 *
 */
@ConfigManager.XmlResource(file = "xml/rankGift.xml", struct = "list")
public class RankGiftCft extends ConfigBase {
	
	@Id
	protected final int id;
	/** 类型（type2是经验，1是竞技）*/
	protected final int type;
	/** 排行范围 */
	protected final String rank;
	/** 奖励 */
	protected final String reward;
	
	public RankGiftCft() {
		id = 0;
		type = 0;
		rank = "";
		reward = "";
	}
	
	public int getId() {
		return id;
	}
	public int getType() {
		return type;
	}
	public String getReward() {
		return reward;
	}
	
	/**
	 * 查看玩家的排名是否在本排名段
	 * @param playerRank
	 * @return
	 */
	public boolean isInStage(int playerRank) {
		String[] rankStage = this.rank.split(",");
		if (rankStage.length == 1) {
			int parse = Integer.parseInt(rankStage[0]);
			return playerRank == parse;
		} else if (rankStage.length == 2) {
			int head = Integer.parseInt(rankStage[0]);
			int tail = Integer.parseInt(rankStage[1]);
			return playerRank >= head && playerRank <= tail;
		}
		return false;
	}
	
	protected void clearStaticData() {
	}
}
