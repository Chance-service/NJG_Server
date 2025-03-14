package com.guaji.game.entity;
/**
 * 联盟商店对象
 *
 */
public class AllianceShopItem {
	/**
	 * id
	 */
	private int id;
	/**
	 * 物品id
	 */
	private int itemId;
	/**
	 * 物品类型
	 */
	private int itemType;
	/**
	 * 物品数量
	 */
	private int itemCount;
	/**
	 * 价格
	 */
	private int price;
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public int getItemId() {
		return itemId;
	}
	public void setItemId(int itemId) {
		this.itemId = itemId;
	}
	public int getItemType() {
		return itemType;
	}
	public void setItemType(int itemType) {
		this.itemType = itemType;
	}
	public int getItemCount() {
		return itemCount;
	}
	public void setItemCount(int itemCount) {
		this.itemCount = itemCount;
	}
	public int getPrice() {
		return price;
	}
	public void setPrice(int price) {
		this.price = price;
	}
}
