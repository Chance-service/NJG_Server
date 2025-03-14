package com.guaji.game.config;

import org.guaji.config.ConfigBase;
import org.guaji.config.ConfigManager;
/**
 * 新手UR活动每日次数与vip等级挂钩
 */
@ConfigManager.XmlResource(file = "xml/newURTimes.xml", struct = "map")
public class NewURTimesCfg extends ConfigBase{
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

	
	public NewURTimesCfg(){
		vipLevel = 0;
		freeCD = 0;
		singleCost = 0;
		tenCost = 0;
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
	
	public static NewURTimesCfg getTimesCfgByVipLevel(int vipLevel){
		return ConfigManager.getInstance().getConfigByKey(NewURTimesCfg.class, vipLevel);
	}
	
	/**
	 * 清理相关静态数据
	 */
	protected void clearStaticData() {
	}
}
