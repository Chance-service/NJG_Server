package com.guaji.game.entity;

public class CrystalShopItem {

	private int id;

	private int itemType;

	private int itemId;

	private int itemCount;

	private int costType;

	private int costCount;

	private int level;

	private int discount;

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

	public int getLevel() {
		return level;
	}

	public void setLevel(int level) {
		this.level = level;
	}

	public int getDiscount() {
		return discount;
	}

	public void setDiscount(int discount) {
		this.discount = discount;
	}

}
