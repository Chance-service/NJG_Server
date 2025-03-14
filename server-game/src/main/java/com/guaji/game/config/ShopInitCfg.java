/**
 * 
 */
package com.guaji.game.config;

import org.guaji.config.ConfigBase;
import org.guaji.config.ConfigManager;

/**
 * @author dell
 * 
 */
@ConfigManager.XmlResource(file = "xml/shopInitItemsCfg.xml", struct = "list")
public class ShopInitCfg extends ConfigBase {

	private final int job;
	private final int itemType;
	private final int itemId;
	private final int itemCount;
	private final int price;
	private final int buyType;
	private final int discount;

	public ShopInitCfg() {
		this.job = 0;
		this.itemType = 0;
		this.itemId = 0;
		this.itemCount = 0;
		this.discount = 0;
		this.price = 0;
		this.buyType = 0;
	}

	public int getJob() {
		return job;
	}

	public int getItemType() {
		return itemType;
	}

	public int getItemId() {
		return itemId;
	}

	public int getItemCount() {
		return itemCount;
	}

	public int getDiscount() {
		return discount;
	}

	public int getPrice() {
		return price;
	}

	public int getBuyType() {
		return buyType;
	}
	
	/**
	 * 清理相关静态数据
	 */
	protected void clearStaticData() {
	}

}
