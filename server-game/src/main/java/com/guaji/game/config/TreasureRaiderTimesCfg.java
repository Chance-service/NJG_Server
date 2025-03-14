package com.guaji.game.config;

import org.guaji.config.ConfigBase;
import org.guaji.config.ConfigManager;

/**
 * 夺宝奇兵每日次数与vip等级挂钩
 * @author rudy
 */
@ConfigManager.XmlResource(file = "xml/treasureRaiderTimes.xml", struct = "map")
public class TreasureRaiderTimesCfg extends ConfigBase {
	/**
	 * vip等级
	 */
	@Id
	private final int vipLevel;
	/**
	 * 每日免费次数
	 */
	private final int oneDayFreeTimes;
	/**
	 * 每日总次数
	 */
	private final int oneDayTotalTimes;
	
	public TreasureRaiderTimesCfg(){
		vipLevel = 0;
		oneDayFreeTimes = 0;
		oneDayTotalTimes = 0;
	}

	public int getVipLevel() {
		return vipLevel;
	}

	public int getOneDayFreeTimes() {
		return oneDayFreeTimes;
	}

	public int getOneDayTotalTimes() {
		return oneDayTotalTimes;
	}
	
	public static TreasureRaiderTimesCfg getTimesCfgByVipLevel(int vipLevel){
		return ConfigManager.getInstance().getConfigByKey(TreasureRaiderTimesCfg.class, vipLevel);
	}
	
	/**
	 * 清理相关静态数据
	 */
	protected void clearStaticData() {
	}
}
