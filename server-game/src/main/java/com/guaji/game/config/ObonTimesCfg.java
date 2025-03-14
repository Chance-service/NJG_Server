package com.guaji.game.config;

import org.guaji.config.ConfigBase;
import org.guaji.config.ConfigManager;
/**
 * 夺宝奇兵每日次数与vip等级挂钩
 * @author rudy
 */
@ConfigManager.XmlResource(file = "xml/obonTimes.xml", struct = "map")
public class ObonTimesCfg extends ConfigBase{
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

	
	public ObonTimesCfg(){
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
	
	public static ObonTimesCfg getTimesCfgByVipLevel(int vipLevel){
		return ConfigManager.getInstance().getConfigByKey(ObonTimesCfg.class, vipLevel);
	}
	
	/**
	 * 清理相关静态数据
	 */
	protected void clearStaticData() {
	}
}
