package com.guaji.game.entity;


/**
 * 荣誉商店单项
 */
public class HonorShopItem {

	private int id;

	private int itemType;

	private int itemId;

	private int costHonor;

	public static HonorShopItem valueOf(String info) {
		HonorShopItem itemInfo = new HonorShopItem();
		if (itemInfo.initByString(info)) {
			return itemInfo;
		}
		return null;
	}
	
	public boolean initByString(String info) {
		if (info != null && info.length() > 0 && !info.equals("0") && !info.equals("none")) {
			String[] items = info.split("_");
			if (items.length < 3) {
				return false;
			}
			id = Integer.parseInt(items[1]);
			itemType = Integer.parseInt(items[0]);
			itemId = Integer.parseInt(items[1]);
			costHonor = Integer.parseInt(items[2]);
			return true;
		}
		return false;
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

	public int getCostHonor() {
		return costHonor;
	}

	public void setCostHonor(int costHonor) {
		this.costHonor = costHonor;
	}

}
