package com.guaji.game.config;

import org.guaji.config.ConfigBase;
import org.guaji.config.ConfigManager;

/**
 * 勇气商店
 * @author Nannan.Gao
 */
@ConfigManager.XmlResource(file = "xml/valorShop.xml", struct = "map")
public class ValorShopCfg extends ConfigBase{
	
	/**
	 * 商品编号
	 */
	@Id
	private final int id;
	
	/**
	 * 商品ID
	 */
	private final int itemId;
	
	/**
	 * 物品类型
	 */
	private final int itemType;

	/**
	 * 物品数量
	 */
	private final int itemCount;
	
	/**
	 * 购买所需积分
	 */
	private final int needScore;
	
	/**
	 * 购买需达到的历史积分
	 */
	private final int needHistoryScore;
	
	
	public ValorShopCfg(){
		
		this.id = 0;
		this.itemId = 0;
		this.itemType = 0;
		this.itemCount = 0;
		this.needScore = 0;
		this.needHistoryScore = 0;
	}
	
	public int getId() {
		return id;
	}
	
	public int getItemId() {
		return itemId;
	}

	public int getItemType() {
		return itemType;
	}

	public int getItemCount() {
		return itemCount;
	}

	public int getNeedScore() {
		return needScore;
	}
	
	public int getNeedHistoryScore() {
		return needHistoryScore;
	}

	@Override
	protected boolean checkValid() {
		return true;
	}
	
}
