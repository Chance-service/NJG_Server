package com.guaji.game.shop;

/**
 * 角色商城购买物品信息
 * 
 * @author ManGao
 * 
 */
public class PlayerShopItem {

	/**
	 * 物品唯一id
	 */
	private String id;

	/**
	 * 配置列表的id
	 */
	private int listCofigId;

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
	 * 是否是紫色
	 */
	private boolean isPurple = false;

	/**
	 * 购买折扣率
	 */
	private int discount;

	public int getListCofigId() {
		return listCofigId;
	}

	public void setListCofigId(int listCofigId) {
		this.listCofigId = listCofigId;
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

	public int getBuyType() {
		return buyType;
	}

	public void setBuyType(int buyType) {
		this.buyType = buyType;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public boolean isPurple() {
		return isPurple;
	}

	public void setPurple(boolean isPurple) {
		this.isPurple = isPurple;
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

	public int getItemCount() {
		return itemCount;
	}

	public void setItemCount(int itemCount) {
		this.itemCount = itemCount;
	}
	
}
