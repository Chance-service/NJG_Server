package com.guaji.game.config;

import org.guaji.config.ConfigBase;
import org.guaji.config.ConfigManager;

/**
 * 万圣节活动常量配表
 * @author Melvin.Mao
 * @date Oct 17, 2017 2:27:23 PM
 */
@ConfigManager.XmlResource(file = "xml/halloweenConst.xml", struct = "map")
public class HalloweenConstCfg extends ConfigBase {

	@Id
	protected final int id;
	/**
	 * CD时间
	 */
	protected final int freeCD;
	/**
	 * 道具消耗
	 */
	protected final String itemCost;
	/**
	 * 一次钻石消耗
	 */
	protected final int singleCost;
	/**
	 * 十次钻石消耗
	 */
	protected final int tenCost;

	public HalloweenConstCfg() {
		this.id = 0;
		this.freeCD = 0;
		this.itemCost = null;
		this.singleCost = 0;
		this.tenCost = 0;
	}

	public int getId() {
		return id;
	}

	public int getFreeCD() {
		return freeCD;
	}

	public int getSingleCost() {
		return singleCost;
	}

	public int getTenCost() {
		return tenCost;
	}
	
	public String getItemCost() {
		return itemCost;
	}

	@Override
	protected boolean assemble() {
		return super.assemble();
	}
}
