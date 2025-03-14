package com.guaji.game.config;

import org.guaji.config.ConfigBase;
import org.guaji.config.ConfigManager;

@ConfigManager.XmlResource(file = "xml/vipWelfare.xml", struct = "map")
public class VipWelfareCfg extends ConfigBase{
	
	/**
	 * 配置id
	 */
	@Id
	protected final int id;
	/**
	 * 经验倍率
	 */
	protected final float ratioExp;
	/**
	 * 金币倍率
	 */
	protected final float ratioCoin;
	/**
	 * 奖励字物品符串
	 */
	protected final String awardStr;

	public VipWelfareCfg() {
		id = 0;
		ratioExp = 0.0f;
		ratioCoin = 0.0f;
		awardStr = "";
	}
	
	public int getId() {
		return id;
	}
	public float getRatioExp() {
		return ratioExp;
	}

	public float getRatioCoin() {
		return ratioCoin;
	}

	public String getAwardStr() {
		return awardStr;
	}

	@Override
	protected boolean assemble() {
		return true;
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
}
