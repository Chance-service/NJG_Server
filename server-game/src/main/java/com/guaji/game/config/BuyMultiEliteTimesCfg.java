package com.guaji.game.config;

import java.util.List;

import org.guaji.config.ConfigBase;
import org.guaji.config.ConfigManager;

@ConfigManager.XmlResource(file = "xml/buyMultiEliteTimes.xml", struct = "list")
public class BuyMultiEliteTimesCfg extends ConfigBase {
	private final int id;
	/**
	 * 适用于该配置的最大购买次数
	 */
	private final int maxTimes;
	/**
	 * 购买价格
	 */
	private final int price;
	
	public BuyMultiEliteTimesCfg(){
		id = 0;
		maxTimes = 0;
		price = 0;
	}

	public int getId() {
		return id;
	}

	public int getMaxTimes() {
		return maxTimes;
	}

	public int getPrice() {
		return price;
	}
	
	@Override
	protected boolean checkValid() {
		return true;
	}
	
	/**
	 * 清理相关静态数据
	 */
	protected void clearStaticData() {
	}
	
	/**
	 * 通过当前购买次数获取对应的价格配置
	 * @param buyTimes
	 * @return
	 */
	public static BuyMultiEliteTimesCfg getBuyCfgByTimes(int buyTimes){
		List<BuyMultiEliteTimesCfg> cfgs = ConfigManager.getInstance().getConfigList(BuyMultiEliteTimesCfg.class);
		for(BuyMultiEliteTimesCfg cfg : cfgs){
			if(buyTimes <= cfg.getMaxTimes())
				return cfg;
		}
		return cfgs.get(cfgs.size()-1);
	}
}
