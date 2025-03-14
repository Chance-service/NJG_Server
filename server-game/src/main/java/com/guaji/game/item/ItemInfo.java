package com.guaji.game.item;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * 物品定义结构体
 * 
 * @author xulinqs
 * 
 */
public class ItemInfo {
	/**
	 * 种类
	 */
	int type;
	/**
	 * id
	 */
	int itemId;
	/**
	 * 数量
	 */
	long quantity;

	public ItemInfo() {
		super();
	}

	public ItemInfo(int type, int itemId, long quantity) {
		super();
		this.type = type;
		this.itemId = itemId;
		this.quantity = quantity;
	}

	public ItemInfo(String info) {
		initByString(info);
	}
	/**
	 * 
	 * @return
	 */
	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public int getItemId() {
		return itemId;
	}

	public void setItemId(int itemId) {
		this.itemId = itemId;
	}

	public long getQuantity() {
		return quantity;
	}

	public void setQuantity(long quantity) {
		this.quantity = quantity;
	}

	public ItemInfo clone() {
		ItemInfo ret = new ItemInfo(type, itemId, quantity);
		return ret;
	}

	@Override
	public String toString() {
		return String.format("%d_%d_%d", type, itemId, quantity);
	}
	
	public String toShortString(){
		return String.format("%d_%d", type, itemId);
	}
	
	public static ItemInfo valueOf(int type, int itemId, long quantity) {
		return new ItemInfo(type, itemId, quantity);
	}

	public boolean initByString(String info) {
		if (info != null && info.length() > 0 && !info.equals("0") && !info.equals("none")) {
			String[] items = info.split("_");
			if (items.length < 3) {
				return false;
			}
			type = Integer.parseInt(items[0]);//商店里 货币类型
			itemId = Integer.parseInt(items[1]);//商店里为价格
			quantity = Integer.parseInt(items[2]);//商店里为折扣
			return true;
		}
		return false;
	}

	public static ItemInfo valueOf(String info) {
		ItemInfo itemInfo = new ItemInfo();
		if (itemInfo.initByString(info)) {
			return itemInfo;
		}
		return null;
	}
	
	public static List<ItemInfo> valueListOf(String info) {
		List<ItemInfo> items = new LinkedList<>();
		String[] ss = info.split(",");
		for(String s : ss) {
			ItemInfo itemInfo = valueOf(s);
			if(itemInfo != null) {
				items.add(itemInfo);
			}
		}
		return items;
	}
	
	public static List<ItemInfo> mergeItem(List<ItemInfo> alist){
		List<ItemInfo> items = new LinkedList<>();
		if (alist.size() > 1) {
			Map<String,Long> comparaMap = new HashMap<>(); 
			for (ItemInfo aItemInfo :alist) {
				String key = aItemInfo.toShortString(); 
				Long counts = comparaMap.get(key);
				comparaMap.put(key,counts == null ? aItemInfo.getQuantity() : aItemInfo.getQuantity()+counts);
			}
			if (comparaMap.size() != alist.size()) {
				for (Map.Entry<String,Long> entry : comparaMap.entrySet()) {
					String ItemStr = String.format(entry.getKey()+"_%d", entry.getValue());
					items.add(ItemInfo.valueOf(ItemStr));
				}
			} else {
				return alist;
			}
		} else {
			return alist;
		}
		return items;
	}
}
