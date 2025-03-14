package com.guaji.game.config;

import org.guaji.config.ConfigBase;
import org.guaji.config.ConfigManager;

/**
* 周卡配置
* 
* @author xulinqs
* 
*/
@ConfigManager.XmlResource(file = "xml/consumeweekcard.xml", struct = "map")
public class ConsumeWeekCardCfg extends ConfigBase {

	@Id
	private final int id;
	
	private final int days;

	private final String everydayAwards;
	
	private final int goodsId;
	
	private final String buyReward;
	
//	private final int levelUpGoodsId;
//	
//	/** 
//	* 经验加成
//	*/ 
//	private final int addExpBuff;
	
	public ConsumeWeekCardCfg() {
		this.id = 0;
		this.days = 0;
		this.goodsId = 0;
		//this.levelUpGoodsId = 0;
		this.everydayAwards = null;
		this.buyReward = null;
		//this.addExpBuff=0;
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
	
	public String getBuyReward() {
		return buyReward;
	}

//	public int getLevelUpGoodsId() {
//		return levelUpGoodsId;
//	}
//	
//
//	public int getAddExpBuff() {
//		return addExpBuff;
//	}

	/**
	 * 根据商品编号获得配置
	 * @return
	 */
	public static ConsumeWeekCardCfg getWeekCardCfgByGoodsId(int goodsId) {
		for(ConsumeWeekCardCfg weekCardCfg : ConfigManager.getInstance().getConfigMap(ConsumeWeekCardCfg.class).values()) {
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
