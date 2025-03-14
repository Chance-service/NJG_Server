package com.guaji.game.config;

import java.util.ArrayList;
import java.util.List;

import org.guaji.config.ConfigBase;
import org.guaji.config.ConfigManager;

import com.guaji.game.item.ItemInfo;

/**
 * 装备强化配置
 * 
 * @author xulinqs
 * 
 */
@ConfigManager.XmlResource(file = "xml/equipStrengthRatio.xml", struct = "list")
public class EquipStrengthRatioCfg extends ConfigBase {

	/**
	 * 配置条的类型 1 表示基础强化等级系数 2品质修正系数 3极品修正系数 4部位修正系数
	 */
	protected final int type;

	protected final int value;
	
	protected final int  quality;

	protected final String costItem;

	protected final String costCoin;

	protected final float ratio;

	public EquipStrengthRatioCfg() {
		this.type = 0;
		this.value = 0;
		this.quality= 0;
		this.costItem = "";
		this.costCoin = "";
		this.ratio = 0;
	}

	public int getType() {
		return type;
	}

	public int getValue() {
		return value;
	}
	
	public int getQuality() {
		return quality;
	}

	public String getCostItem() {
		return costItem;
	}

	public String getCostCoin() {
		return costCoin;
	}

	public float getRatio() {
		return ratio;
	}

	/**
	 * 获得强化所需要的道具
	 * 
	 * @param equipId
	 * @param strength
	 * @return
	 */
	public static List<ItemInfo> getStrengthConstItem(int equipId, int strength) {
		
		EquipCfg equipCfg = ConfigManager.getInstance().getConfigByKey(EquipCfg.class, equipId);
		List<ItemInfo> items = new ArrayList<ItemInfo>();

		EquipStrengthRatioCfg itemInfoA = getEquipItemInfoCfg(1, strength,1);
		EquipStrengthRatioCfg partRatio = getEquipItemInfoCfg(4, equipCfg.getPart(),0);
		
		if (itemInfoA == null || partRatio == null) {
			return items;
		}

		String itemInfo = itemInfoA.getCostItem();
		if (!itemInfo.isEmpty()) {
			// 扣除的道具
			List<ItemInfo> infos = ItemInfo.valueListOf(itemInfo);
			for (ItemInfo item : infos) {
				int itemCount = (int) (item.getQuantity() * partRatio.getRatio());
				item.setQuantity(itemCount);
			}
			items.addAll(infos);
		}
		return items;
	}

	/**
	 * 获取扣除的道具配置
	 * 
	 * @param type
	 * @param value
	 * @return
	 */
	public static EquipStrengthRatioCfg getEquipItemInfoCfg(int type, int value, int quality) {
		List<EquipStrengthRatioCfg> strengthRatioCfgs = ConfigManager.getInstance().getConfigList(EquipStrengthRatioCfg.class);
		if (strengthRatioCfgs != null) {
			for (EquipStrengthRatioCfg ratioCfg : strengthRatioCfgs) {
				if (quality != 0) {
					if (ratioCfg.type == type && ratioCfg.value == value && ratioCfg.quality == quality) {
						return ratioCfg;
					}
				} else {
					if (ratioCfg.type == type && ratioCfg.value == value) {
						return ratioCfg;
					}
				}
			}
		}
		return null;
	}

	/**
	 * 清理相关静态数据
	 */
	protected void clearStaticData() {
	}
}
