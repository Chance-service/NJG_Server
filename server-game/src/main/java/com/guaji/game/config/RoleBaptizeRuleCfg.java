package com.guaji.game.config;

import org.guaji.config.ConfigBase;
import org.guaji.config.ConfigManager;

/**
 * 佣兵培养基础
 * 
 * @author zdz
 * 
 */
@ConfigManager.XmlResource(file = "xml/roleBaptizeRule.xml", struct = "map")
public class RoleBaptizeRuleCfg extends ConfigBase {

	@Id
	protected final int type;

	// 次级消耗
	protected final String cost;

	// 开放vip等级
	protected final int openVip;

	// 优先消耗
	protected final String costItem;

	// 区间上限
	protected final float uperLimit;

	// 区间下限
	protected final float lowerLimit;
	// 最高失败概率
	protected final int maxFailure;
	// 增幅区间
	protected final String upInterval;
	// 降幅区间
	protected final String dowmInterval;

	public RoleBaptizeRuleCfg() {
		type = 0;
		cost = null;
		lowerLimit = 0;
		upInterval = null;
		dowmInterval = null;
		maxFailure = 0;
		uperLimit = 0;
		openVip = 0;
		costItem = null;
	}

	public int getType() {
		return type;
	}

	public String getCost() {
		return cost;
	}

	public int getOpenVip() {
		return openVip;
	}

	public String getCostItem() {
		return costItem;
	}

	public float getUperLimit() {
		return uperLimit;
	}

	public float getLowerLimit() {
		return lowerLimit;
	}

	public int getMaxFailure() {
		return maxFailure;
	}

	public String getUpInterval() {
		return upInterval;
	}

	public String getDowmInterval() {
		return dowmInterval;
	}

	/**
	 * 清理相关静态数据
	 */
	protected void clearStaticData() {
	}
}
