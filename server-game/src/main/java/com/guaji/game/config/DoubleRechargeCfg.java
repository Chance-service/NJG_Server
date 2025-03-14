package com.guaji.game.config;

import org.guaji.config.ConfigBase;
import org.guaji.config.ConfigManager;
@ConfigManager.XmlResource(file = "xml/doubleRecharge.xml", struct = "map")
public class DoubleRechargeCfg extends ConfigBase {

	/**
	 * 商品标号
	 */
	@Id
	private
	final int goodsId ;
	/**
	 * 倍数
	 */
	private final int ratio;
	/**
	 * 类型
	 */
	private final int type;
	
	public DoubleRechargeCfg() {
		this.goodsId = 0;
		this.ratio = 0;
		this.type = 0;
	}

	public int getGoodsId() {
		return goodsId;
	}

	public int getRatio() {
		return ratio;
	}

	public int getType() {
		return type;
	}
	
	/**
	 * 清理相关静态数据
	 */
	protected void clearStaticData() {
	}
	
}
