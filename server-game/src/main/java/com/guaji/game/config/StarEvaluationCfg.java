package com.guaji.game.config;

import org.guaji.config.ConfigBase;
import org.guaji.config.ConfigManager;

import com.guaji.game.item.AwardItems;

@ConfigManager.XmlResource(file = "xml/starEvaluationReward.xml", struct = "list")
public class StarEvaluationCfg extends ConfigBase{

	private final String awardItemsStr ;
	
	private AwardItems awardItems;
	
	public StarEvaluationCfg() {
		this.awardItemsStr = null;
	}

	public static StarEvaluationCfg getStarEvaluationCfg() {
		return ConfigManager.getInstance().getConfigList(StarEvaluationCfg.class).get(0);
	}
	
	@Override
	protected boolean assemble() {
		if(this.awardItemsStr != null) {
			this.awardItems = AwardItems.valueOf(this.awardItemsStr);
		}
		return true;
	}
	
	public String getAwardItemsStr() {
		return awardItemsStr;
	}

	public AwardItems getAwardItems() {
		return awardItems;
	}

	public void setAwardItems(AwardItems awardItems) {
		this.awardItems = awardItems;
	}
	
	/**
	 * 清理相关静态数据
	 */
	protected void clearStaticData() {
	}
}
