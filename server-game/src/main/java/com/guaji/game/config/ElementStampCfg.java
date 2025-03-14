package com.guaji.game.config;

import java.util.LinkedList;
import java.util.List;

import org.guaji.config.ConfigBase;
import org.guaji.config.ConfigManager;

import com.guaji.game.item.ItemInfo;
import com.guaji.game.util.AwardUtil;
import com.guaji.game.util.WeightUtil;
import com.guaji.game.util.WeightUtil.WeightItem;

@ConfigManager.XmlResource(file = "xml/elementStamp.xml", struct = "list")
public class ElementStampCfg extends ConfigBase {

	protected final int itemId;
	/**
	 * 等级下限
	 */
	protected final int minLevel;
	/**
	 * 等级上限
	 */
	protected final int maxLevel;
	/**
	 * 碎片数量
	 */
	protected final int itemCount;
	/**
	 * 元素权重配置
	 */
	protected final String elementItems;

	public ElementStampCfg() {
		itemId = 0;
		minLevel = 0;
		maxLevel = 0;
		itemCount = 0;
		elementItems = null;

	}

	/**
	 * 根据elementItems和职业获取合成元素ID
	 * 
	 * @param elementItems
	 * @param job
	 * @return
	 */
	public ItemInfo randomElementItemInfo(String elementItems, int job) {
		List<WeightItem<ItemInfo>> elementWeightItems = new LinkedList<WeightItem<ItemInfo>>();
		if (elementItems != null && elementItems.length() > 0) {
			String[] itemStrs = elementItems.split(",");
			for (String itmeStr : itemStrs) {
				String[] es = itmeStr.split("_");
				ItemInfo itemInfo = new ItemInfo();
				if (Integer.valueOf(es[3]) == job) {
					itemInfo.setType(Integer.valueOf(es[0]));
					itemInfo.setItemId(Integer.valueOf(es[1]));
					itemInfo.setQuantity(Integer.valueOf(es[2]));
					WeightItem<ItemInfo> weightItem = WeightItem.valueOf(itemInfo, Integer.valueOf(es[4]));
					elementWeightItems.add(weightItem);
				}
			}
		}
		
		return AwardUtil.randomDrop(WeightUtil.random(elementWeightItems).getType());
	}

	/**
	 * 根据玩家等级获得合成配置
	 * 
	 * @param playerLevel
	 * @return
	 */
	public static ElementStampCfg getCompoundCfg(int playerLevel) {
		List<ElementStampCfg> elementStampCfgs = ConfigManager.getInstance().getConfigList(ElementStampCfg.class);
		if (elementStampCfgs != null) {
			for (ElementStampCfg cc : elementStampCfgs) {
				if (cc.getMinLevel() <= playerLevel && cc.getMaxLevel() >= playerLevel) {
					return cc;
				}
			}
		}
		return null;
	}

	public int getItemId() {
		return itemId;
	}

	public int getMinLevel() {
		return minLevel;
	}

	public int getMaxLevel() {
		return maxLevel;
	}

	public int getItemCount() {
		return itemCount;
	}

	public String getElementItems() {
		return elementItems;
	}

	@Override
	protected boolean assemble() {
		return true;
	}

	@Override
	protected boolean checkValid() {
		return true;
	}

}
