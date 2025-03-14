package com.guaji.game.config;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import org.guaji.config.ConfigBase;
import org.guaji.config.ConfigManager;
import org.guaji.os.GuaJiRand;

import com.guaji.game.protocol.Const;
import com.guaji.game.util.GsConst;

@ConfigManager.XmlResource(file = "xml/equipSmelt.xml", struct = "map")
public class EquipSmeltCfg extends ConfigBase {

	private class Item implements Comparable<Item> {
		/**
		 * 装备Id
		 */
		int equipLevel = 0;
		/**
		 * 权重值
		 */
		int weight = 0;

		@Override
		public int compareTo(Item o) {
			return this.weight - o.weight;
		}
	}

	/**
	 * 装备等级
	 */
	@Id
	protected final int equipLevel;

	/**
	 * 装备熔炼之后装备等级权重1
	 */
	protected final String smeltLevel1;

	/**
	 * 装备熔炼之后装备等级权重2
	 */
	protected final String smeltLevel2;

	/**
	 * 装备熔炼之后装备等级权重3
	 */
	protected final String smeltLevel3;

	private List<Item> itemList;

	/**
	 * 目标装备品质（绿）概率
	 */
	protected final int targetEquip2;

	/**
	 * 目标装备品质（蓝）概率
	 */
	protected final int targetEquip3;

	/**
	 * 目标装备品质（紫）概率
	 */
	protected final int targetEquip4;

	/**
	 * 目标装备品质（橙）概率
	 */
	protected final int targetEquip5;

	/**
	 * 目标装备品质（神器）概率
	 */
	protected final int godlyEquip;

	public EquipSmeltCfg() {
		equipLevel = 0;
		smeltLevel1 = null;
		smeltLevel2 = null;
		smeltLevel3 = null;
		targetEquip2 = 0;
		targetEquip3 = 0;
		targetEquip4 = 0;
		targetEquip5 = 0;
		godlyEquip = 0;

		itemList = new LinkedList<>();
	}

	/**
	 * 根据选择模式选择出熔炼目标装备的等级
	 * 
	 * @param selectMode
	 * @return
	 */
	public int selectEquipLevel(int selectMode) {
		List<Item> items = new LinkedList<>();
		int totalWeight = 0;
		if (selectMode == GsConst.Equip.EQUIP_SMELT_WEIGHT_SELECT_THERE) {
			for (Item item : itemList) {
				items.add(item);
				totalWeight += item.weight;
			}
		} else if (selectMode == GsConst.Equip.EQUIP_SMELT_WEIGHT_SELECT_TWO) {
			for (Item item : itemList.subList(0, 1)) {
				items.add(item);
				totalWeight += item.weight;
			}
		}
		Collections.sort(items);

		int rand = GuaJiRand.randInt(totalWeight);
		int index = 0;
		int tt = 0;
		do {
			tt += items.get(index).weight;
			index++;
		} while (tt < rand && items.get(index) != null);
		return items.get(--index).equipLevel;
	}

	public int getEquipLevel() {
		return equipLevel;
	}

	public String getSmeltLevel1() {
		return smeltLevel1;
	}

	public String getSmeltLevel2() {
		return smeltLevel2;
	}

	public String getSmeltLevel3() {
		return smeltLevel3;
	}

	public int getTargetEquip2() {
		return targetEquip2;
	}

	public int getTargetEquip3() {
		return targetEquip3;
	}

	public int getTargetEquip4() {
		return targetEquip4;
	}

	public int getTargetEquip5() {
		return targetEquip5;
	}

	public int getGodlyEquip() {
		return godlyEquip;
	}

	@Override
	protected boolean assemble() {
		itemList.clear();
		
		String[] es = smeltLevel1.split("_");
		Item item1 = new Item();
		item1.equipLevel = Integer.valueOf(es[0]);
		item1.weight = Integer.valueOf(es[1]);
		itemList.add(item1);

		es = smeltLevel1.split("_");
		Item item2 = new Item();
		item2.equipLevel = Integer.valueOf(es[0]);
		item2.weight = Integer.valueOf(es[1]);
		itemList.add(item2);

		es = smeltLevel1.split("_");
		Item item3 = new Item();
		item3.equipLevel = Integer.valueOf(es[0]);
		item3.weight = Integer.valueOf(es[1]);
		itemList.add(item3);
		return true;
	}

	@Override
	protected boolean checkValid() {
		return super.checkValid();
	}

	/**
	 * 根据品质获得出装备的概率
	 * 
	 * @param nextQuality
	 * @return
	 */
	public int getTargetEquipRatio(int quality) {
		switch (quality) {
		case Const.equipQuality.GREEN_VALUE:
			return targetEquip2;
		case Const.equipQuality.BLUE_VALUE:
			return targetEquip3;
		case Const.equipQuality.PURPLE_VALUE:
			return targetEquip4;
		case Const.equipQuality.ORANGE_VALUE:
			return targetEquip5;
		}
		return 0;
	}

	/**
	 * 清理相关静态数据
	 */
	protected void clearStaticData() {
	}
}
