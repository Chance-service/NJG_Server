package com.guaji.game.config;

import org.guaji.config.ConfigBase;
import org.guaji.config.ConfigManager;

@ConfigManager.XmlResource(file = "xml/vipPackage.xml", struct = "map")
public class VipPackageCfg extends ConfigBase {
	/**
	 * 配置id
	 */
	@Id
	protected final int id;
	
	/**
	 * 奖励字符串
	 */
	protected final String awardStr;
	
	/**
	 * vip等级限制
	 */
	protected final int vipLimit;
	
	/**
	 * 花费钻石数量
	 */
	protected final int price;

	
	public VipPackageCfg() {
		id = 0;
		awardStr = null;
		vipLimit = 0;
		price = 0;
		

	}

	public int getId() {
		return id;
	}
	
	public int getPrice()
	{
		return price;
	}
	
	public int getVipLimit()
	{
		return vipLimit;
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
