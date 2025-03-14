package com.guaji.game.config;

import java.util.List;

import org.guaji.config.ConfigBase;
import org.guaji.config.ConfigManager;

import com.guaji.game.item.AwardItems;
import com.guaji.game.item.ItemInfo;

@ConfigManager.XmlResource(file = "xml/worship.xml", struct = "map")
public class WorshipCfg extends ConfigBase {
	/**
	 * 配置id
	 */
	@Id
	private final int worshipId;
	/**
	 * 
	 */
	private final String worshipName;
	/**
	 * xiaohao
	 */
	private final String worshipCost;
	
	private List<ItemInfo> costItems;
	/**
	 * 奖励
	 */
	private final String worshipReward;
	
	private AwardItems awardItems;
	
	public WorshipCfg(){
		this.worshipId = 0;
		this.worshipName = "";
		this.worshipCost = "";
		this.worshipReward = "";
	}
	
	/**
	 * 数据格式转化
	 */
	@Override
	protected boolean assemble() {
		if(this.worshipCost != null && this.worshipCost.length() > 0) {
			this.setCostItems(ItemInfo.valueListOf(this.worshipCost));
		}
		if(this.worshipReward != null && this.worshipReward.length() > 0) {
			this.setAwardItems(AwardItems.valueOf(this.worshipReward));
		}
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

	public String getWorshipName() {
		return worshipName;
	}

	public String getWorshipCost() {
		return worshipCost;
	}

	public String getWorshipReward() {
		return worshipReward;
	}

	public int getWorshipId() {
		return worshipId;
	}

	public List<ItemInfo> getCostItems() {
		return costItems;
	}

	public void setCostItems(List<ItemInfo> costItems) {
		this.costItems = costItems;
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

