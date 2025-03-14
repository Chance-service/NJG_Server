package com.guaji.game.entity;

public class DailyShopItem {

	private int id;

	private int itemType;

	private int itemId;

	private int itemCount;

	private int costType;

	private int costCount;
	
	private int initCount;

	private int left;

	private int discount;
	
	private String priceStr;
	
	private int cfgIndex;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getItemType() {
		return itemType;
	}

	public void setItemType(int itemType) {
		this.itemType = itemType;
	}

	public int getItemId() {
		return itemId;
	}

	public void setItemId(int itemId) {
		this.itemId = itemId;
	}

	public void setItemCount(int count) {
		this.itemCount = count;
		return;
	}

	public int getItemCount() {
		return this.itemCount;
	}

	public void setCostType(int type) {
		this.costType = type;
		return;
	}

	public int getCostType() {
		return this.costType;
	}

	public void setCostCount(int count) {
		this.costCount = count;
		return;
	}
	
	/**
	 * 获得原始价格
	 * @return
	 */
	public int getCostCount() {
		return this.costCount;
	}
	
	public int getInitCount() {
		return this.initCount;
	}

	public void setInitCount(int initcount) {
		this.initCount = initcount;
		return;
	}

	public int getLeft() {
		return left;
	}

	public void setLeft(int left) {
		this.left = left;
	}

	public int getDiscount() {
		return discount;
	}

	public void setDiscount(int discount) {
		this.discount = discount;
	}
	
	public void setPriceStr(String str) {
		this.priceStr = str;
	}
	
	public String getPriceStr() {
		return priceStr;
	}
	
	public int getCfgIndex() {
		return cfgIndex;
	}

	public void setCfgIndex(int cfgIndex) {
		this.cfgIndex = cfgIndex;
	}
	

}
