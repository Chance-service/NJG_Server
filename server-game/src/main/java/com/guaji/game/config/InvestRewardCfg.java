package com.guaji.game.config;

import org.guaji.config.ConfigBase;
import org.guaji.config.ConfigManager;

import com.guaji.game.item.AwardItems;

@ConfigManager.XmlResource(file = "xml/investReward.xml", struct = "map")
public class InvestRewardCfg extends ConfigBase {

	@Id
	private final int group;

	private final String winReward ;
	
	private final String failReward ;

	private AwardItems winAwardItems;
	
	private AwardItems failAwardItems;
	
	private final int costGold;
	
	private final int costCoins;

	public InvestRewardCfg() {
		this.group = 0;
		this.winReward = null;
		this.failReward = null;
		this.costGold = 0;
		this.costCoins = 0;
	}

	/**
	 * 数据格式转化
	 */
	@Override
	protected boolean assemble() {
		if (this.winReward != null) {
			String rewardStr = this.winReward.trim();
			if (rewardStr.length() > 0) {
				this.setWinAwardItems(AwardItems.valueOf(winReward));
			}
		}
		
		if (this.failReward != null) {
			String rewardStr = this.failReward.trim();
			if (rewardStr.length() > 0) {
				this.setFailAwardItems(AwardItems.valueOf(failReward));
			}
		}
		
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

	public int getGroup() {
		return group;
	}

	public AwardItems getWinAwardItems() {
		return winAwardItems;
	}

	public void setWinAwardItems(AwardItems winAwardItems) {
		this.winAwardItems = winAwardItems;
	}

	public AwardItems getFailAwardItems() {
		return failAwardItems;
	}

	public void setFailAwardItems(AwardItems failAwardItems) {
		this.failAwardItems = failAwardItems;
	}

	public int getCostGold() {
		return costGold;
	}

	public int getCostCoins() {
		return costCoins;
	}
	
	/**
	 * 清理相关静态数据
	 */
	protected void clearStaticData() {
	}
}
