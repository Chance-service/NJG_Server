package com.guaji.game.config;

import org.guaji.config.ConfigBase;
import org.guaji.config.ConfigManager;

import com.guaji.game.item.AwardItems;

@ConfigManager.XmlResource(file = "xml/elementDecompose.xml", struct = "map")
public class ElementDecomposeCfg extends ConfigBase {
	/**
	 * 等级
	 */
	@Id
	protected final int quality;
	/**
	 * 分解所得
	 */
	protected final String awards; 

	public ElementDecomposeCfg() {
		quality = 0;
		awards = null;
	}

	/**
	 * 清理相关静态数据
	 */
	protected void clearStaticData() {
	}

	/**
	 * 获得分解的东西
	 * @return
	 */
	public static AwardItems getDecomposeAwards(int quality) {
		ElementDecomposeCfg elementDecomposeCfg = ConfigManager.getInstance().getConfigByKey(ElementDecomposeCfg.class, quality);
		if(elementDecomposeCfg != null) {
			return AwardItems.valueOf(elementDecomposeCfg.awards);
		}
		return null;
	}
	
}
