package com.guaji.game.config;

import org.guaji.config.ConfigBase;
import org.guaji.config.ConfigManager;
/**
 * 夺宝奇兵每日次数与vip等级挂钩
 * @author rudy
 */
@ConfigManager.XmlResource(file = "xml/newTreasureRaiderTimes2.xml", struct = "map")
public class NewTreasureRaiderTimesCfg2 extends ConfigBase{
	/**
	 * vip等级
	 */
	@Id
	private final int vipLevel;
	/**
	 * 免费时间CD
	 */
	private final int freeCD;
	/***
	 * 单次抽奖花费元宝
	 */
	private final int singleCost;
	/***
	 * 十次抽奖花费元宝
	 */
	private final int tenCost;
	
	/** 
	* 抽卡折扣
	*/ 
	private final double discount;

	
	public NewTreasureRaiderTimesCfg2(){
		vipLevel = 0;
		freeCD = 0;
		singleCost = 0;
		tenCost = 0;
		discount=1.0;
	}

	public int getVipLevel() {
		return vipLevel;
	}

	public int getFreeCountDown() {
		return freeCD;
	}
	
	public int getSingleCost() {
		return singleCost;
	}
	
	public int getTenCost() {
		return tenCost;
	}
	
	
	

	public double getDiscount() {
		return discount;
	}

	public static NewTreasureRaiderTimesCfg2 getTimesCfgByVipLevel(int vipLevel){
		return ConfigManager.getInstance().getConfigByKey(NewTreasureRaiderTimesCfg2.class, vipLevel);
	}
	
	/**
	 * 清理相关静态数据
	 */
	protected void clearStaticData() {
	}
}
