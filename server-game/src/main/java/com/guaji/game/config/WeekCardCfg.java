package com.guaji.game.config;

import org.guaji.config.ConfigBase;
import org.guaji.config.ConfigManager;

/**
* 周卡配置
* 
* @author xulinqs
* 
*/
@ConfigManager.XmlResource(file = "xml/weekcard.xml", struct = "map")
public class WeekCardCfg extends ConfigBase {

	@Id
	private final int id;
	
	private final int days;

	private final String everydayAwards;
	
	private final int goodsId;
	
	private final int levelUpGoodsId;
	
	public WeekCardCfg() {
		this.id = 0;
		this.days = 0;
		this.goodsId = 0;
		this.levelUpGoodsId = 0;
		this.everydayAwards = null;
	}
	
	public int getId() {
		return id;
	}

	public int getDays() {
		return days;
	}

	public String getEverydayAwards() {
		return everydayAwards;
	}

	public int getGoodsId() {
		return goodsId;
	}

	public int getLevelUpGoodsId() {
		return levelUpGoodsId;
	}
	

	/**
	 * 根据商品编号获得配置
	 * @return
	 */
	public static WeekCardCfg getWeekCardCfgByGoodsId(int goodsId) {
		for(WeekCardCfg weekCardCfg : ConfigManager.getInstance().getConfigMap(WeekCardCfg.class).values()) {
			if(weekCardCfg.getGoodsId() == goodsId) {
				return weekCardCfg;
			}
		}
		return null;
	}
	
	/**
	 * 清理相关静态数据
	 */
	protected void clearStaticData() {
	}
}
