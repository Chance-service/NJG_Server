package com.guaji.game.config;

import org.guaji.config.ConfigBase;
import org.guaji.config.ConfigManager;

/**
 * 捞金鱼活动排名奖励配置
 * 
 * @author Nannan.Gao
 * @date 2016-8-5 16:14:58
 */
@ConfigManager.XmlResource(file = "xml/goldfishRankReward.xml", struct = "list")
public class GoldfishRankRewardCfg extends ConfigBase {

	/**
	 * 排名
	 */
	private final int minRank;

	/**
	 * 奖励数据
	 */
	private final String awards;

	public GoldfishRankRewardCfg() {

		this.minRank = 0;
		this.awards = "";
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
	 * 
	 * @return
	 */
	@Override
	protected boolean checkValid() {
		return true;
	}

	/**
	 * 清理相关静态数据
	 */
	protected void clearStaticData() {
	}

	public int getMinRank() {
		return minRank;
	}

	public String getAwards() {
		return awards;
	}

}
