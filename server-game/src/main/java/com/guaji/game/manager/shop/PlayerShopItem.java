package com.guaji.game.manager.shop;

/**
 * 角色商城购买物品信息
 * @author zdz
 *
 */
public class PlayerShopItem {

	/**
	 * 物品唯一id
	 */
	private int id;

	/**
	 * 角色商城物品类型
	 */
	private int itemType;

	/**
	 * 角色商城物品id
	 */
	private int itemId;

	/**
	 * 物品数量
	 */
	private int itemCount;
	
	/**
	 * 购买类型 1是金币，2是钻石
	 */
	private int buyType;

	/**
	 * 购买价格
	 */
	private int buyPrice;

	/**
	 * 购买折扣率
	 */
	private int discount;
	
	/**
	 * 是否显示紫色
	 */
	private boolean isPurple;
	
	
	public PlayerShopItem() {
		
	}
	
	public PlayerShopItem(int id, int itemType, int itemId, int itemCount, int buyType, int buyPrice, int discount, boolean isPurple) {
		
		this.id = id;
		this.itemType = itemType;
		this.itemId = itemId;
		this.itemCount = itemCount;
		this.buyType = buyType;
		this.buyPrice = buyPrice;
		this.discount = discount;
		this.isPurple = isPurple;
	}

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

	public int getItemCount() {
		return itemCount;
	}

	public void setItemCount(int itemCount) {
		this.itemCount = itemCount;
	}

	public int getBuyType() {
		return buyType;
	}

	public void setBuyType(int buyType) {
		this.buyType = buyType;
	}

	public int getBuyPrice() {
		return buyPrice;
	}

	public void setBuyPrice(int buyPrice) {
		this.buyPrice = buyPrice;
	}

	public int getDiscount() {
		return discount;
	}

	public void setDiscount(int discount) {
		this.discount = discount;
	}

	public boolean isPurple() {
		return isPurple;
	}

	public void setPurple(boolean isPurple) {
		this.isPurple = isPurple;
	}
	
}
