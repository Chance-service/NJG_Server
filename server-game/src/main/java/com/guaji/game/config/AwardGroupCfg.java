package com.guaji.game.config;

import java.util.LinkedList;
import java.util.List;

import org.guaji.config.ConfigBase;
import org.guaji.config.ConfigManager;

import com.guaji.game.item.ItemInfo;
import com.guaji.game.util.ConfigUtil;

@ConfigManager.XmlResource(file = "xml/award.xml", struct = "map")
public class AwardGroupCfg extends ConfigBase {
	@Id
	protected final int id;
	/**
	 * 奖励信息
	 */
	protected final String items;
	/**
	 * 物品id
	 */
	protected List<ItemInfo> itemInfos;
	/**
	 * 权重
	 */
	protected List<Integer> itemWeights;

	public AwardGroupCfg() {
		id = 0;
		items = null;

		itemInfos = new LinkedList<ItemInfo>();
		itemWeights = new LinkedList<Integer>();
	}

	public int getId() {
		return id;
	}

	public String getItems() {
		return items;
	}

	public List<ItemInfo> getItemInfos() {
		return itemInfos;
	}

	public List<Integer> getItemWeights() {
		return itemWeights;
	}

	@Override
	protected boolean assemble() {
		itemInfos.clear();
		itemWeights.clear();
		
		if (items != null && items.length() > 0) {
			String[] itemWeightArray = items.split(",");
			for (String itemWeight : itemWeightArray) {
				String[] items = itemWeight.split("_");
				if (items.length != 4) {
					return false;
				}
				itemInfos.add(ItemInfo.valueOf(Integer.valueOf(items[0]), Integer.valueOf(items[1]), Integer.valueOf(items[2])));
				itemWeights.add(Integer.valueOf(items[3]));
			}
		}
		return true;
	}

	@Override
	protected boolean checkValid() {
		for(ItemInfo itemInfo : itemInfos) {
			if(!ConfigUtil.check(itemInfo.getType(), itemInfo.getItemId())) {
				return false;
			}
		}
		return true;
	}
	
	/**
	 * 清理相关静态数据
	 */
	protected void clearStaticData() {
	}
}
