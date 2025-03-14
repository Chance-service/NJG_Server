package com.guaji.game.config;

import org.guaji.config.ConfigBase;
import org.guaji.config.ConfigManager;

import com.guaji.game.item.AwardItems;

/**
 * 英雄令任务配置
 */
@ConfigManager.XmlResource(file = "xml/heroTokenTask.xml", struct = "map")
public class HeroTokenTaskCfg extends ConfigBase {

	@Id
	protected final int id;
	/** 奖励 */
	protected final String reward;

	/** 怪物等级限制 */
	protected final int levelLimit;
	
	/** 胜利次数  */
	protected final int count;

	private AwardItems awardItems;

	public HeroTokenTaskCfg() {
		id = 0;
		levelLimit = 0;
		count = 0;
		reward = "";
	}

	public int getId() {
		return id;
	}

	public int getLevelLimit() {
		return levelLimit;
	}

	public int getCount() {
		return count;
	}

	public String getReward() {
		return reward;
	}

	/**
	 * 数据格式转化
	 */
	@Override
	protected boolean assemble() {
		if (this.reward != null && this.reward.length() > 0) {
			this.setAwardItems(AwardItems.valueOf(this.reward));
		}
		return true;
	}

	public AwardItems getAwardItems() {
		return awardItems;
	}

	public void setAwardItems(AwardItems awardItems) {
		this.awardItems = awardItems;
	}

	protected void clearStaticData() {
	}
}
