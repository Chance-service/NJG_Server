package com.guaji.game.bean;

/**
 * 英雄令商店购买信息
 */
public class HeroTokenShopBean {

	/**
	 * 商店物品ID
	 */
	private int itemId;
	
	/**
	 * 购买次数
	 */
	private int count;
	
	public HeroTokenShopBean() {
		
	}
	
	public HeroTokenShopBean(int itemId) {
		this.itemId = itemId;
		this.count = 1;
	}
	
	public void addCount() {
		this.count += 1;
	}

	public int getItemId() {
		return itemId;
	}

	public void setItemId(int itemId) {
		this.itemId = itemId;
	}

	public int getCount() {
		return count;
	}

	public void setCount(int count) {
		this.count = count;
	}
	
}
