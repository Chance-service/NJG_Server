package com.guaji.game.config;

import java.util.List;

import org.guaji.config.ConfigBase;
import org.guaji.config.ConfigManager;

import com.guaji.game.item.AwardItems;
import com.guaji.game.item.ItemInfo;

@ConfigManager.XmlResource(file = "xml/elementAscend.xml", struct = "map")
public class ElementAscendCfg extends ConfigBase {

	/**
	 * 配置quality
	 */
	@Id
	protected final int quality;
	
	/**
	 * 元素分解获得
	 */
	protected final String decompose ;
	
	private AwardItems decomposeAwardInfo;
	
	/**
	 * 元素进化所需获得
	 */
	protected final String evolution ;
	
	private List<ItemInfo> evolutionConsumeInfo;
	
	
	public ElementAscendCfg() {
		quality = 0;
		decompose = null;
		evolution = null;
	}
	
	public AwardItems getDecomposeAwardInfo() {
		return decomposeAwardInfo;
	}

	public void setDecomposeAwardInfo(AwardItems decomposeAwardInfo) {
		this.decomposeAwardInfo = decomposeAwardInfo;
	}

	public List<ItemInfo> getEvolutionConsumeInfo() {
		return evolutionConsumeInfo;
	}

	public void setEvolutionConsumeInfo(List<ItemInfo> evolutionConsumeInfo) {
		this.evolutionConsumeInfo = evolutionConsumeInfo;
	}

	public int getQuality() {
		return quality;
	}

	public String getDecompose() {
		return decompose;
	}

	public String getEvolution() {
		return evolution;
	}

	@Override
	protected boolean assemble() {
		if(this.evolution != null && this.evolution.length() > 0 && !this.evolution.equals("0")) {
			this.setEvolutionConsumeInfo(ItemInfo.valueListOf(this.evolution));
		}
		
		if(this.decompose != null && this.decompose.length() > 0 && !this.decompose.equals("0")) {
			this.setDecomposeAwardInfo(AwardItems.valueOf(this.decompose));
		}
		
		return super.assemble();
	}

	/**
	 * 清理相关静态数据
	 */
	protected void clearStaticData() {
	}

	
	public static ElementAscendCfg getAscendCfg(int quality) {
		return ConfigManager.getInstance().getConfigByKey(ElementAscendCfg.class, quality);
	}
	
}
