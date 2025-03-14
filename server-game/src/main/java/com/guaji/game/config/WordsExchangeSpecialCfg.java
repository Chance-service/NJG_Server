package com.guaji.game.config;

import java.util.List;

import org.guaji.config.ConfigBase;
import org.guaji.config.ConfigManager;

import com.guaji.game.item.AwardItems;

@ConfigManager.XmlResource(file = "xml/wordsExchangeSpecial.xml", struct = "list")
public class WordsExchangeSpecialCfg extends ConfigBase{

	private final int days;
	
	private final String awards;
	
	private AwardItems awardItems ;
	
	public WordsExchangeSpecialCfg() {
		this.days = 0;
		this.awards = null;
	}

	@Override
	protected boolean assemble() {
		if(awards != null && !"0".equals(awards) && !"".equals(awards)) {
			this.setAwardItems(AwardItems.valueOf(awards));
		}
		return super.assemble();
	}

	@Override
	protected boolean checkValid() {
		return super.checkValid();
	}
	
	public static WordsExchangeSpecialCfg getExchangeSpecialCfg(int days) {
		if(days <= 0) {
			return null;
		}
		List<WordsExchangeSpecialCfg> wCfgs = ConfigManager.getInstance().getConfigList(WordsExchangeSpecialCfg.class);
		if(days >= wCfgs.get(wCfgs.size()-1).getDays()) {
			return wCfgs.get(wCfgs.size() - 1);
		}
		for(int i=0;i<wCfgs.size() - 1;i++) {
			if(days >= wCfgs.get(i).getDays() && days < wCfgs.get(i+1).getDays()) {
				return wCfgs.get(i);
			}
		}
		return null;
	}

	public int getDays() {
		return days;
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
