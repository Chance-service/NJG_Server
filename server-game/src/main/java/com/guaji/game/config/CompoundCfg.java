package com.guaji.game.config;

import java.util.LinkedList;
import java.util.List;

import org.guaji.config.ConfigBase;
import org.guaji.config.ConfigManager;

import com.guaji.game.item.ItemInfo;
import com.guaji.game.util.AwardUtil;
import com.guaji.game.util.WeightUtil;
import com.guaji.game.util.WeightUtil.WeightItem;

@ConfigManager.XmlResource(file = "xml/compound.xml", struct = "list")
public class CompoundCfg extends ConfigBase {

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
	 * 装备权重配置
	 */
	protected final String equips;

	private List<WeightItem<ItemInfo>> equipWeightItems ;
	
	
	public CompoundCfg() {
		itemId = 0;
		minLevel = 0;
		maxLevel = 0;
		itemCount = 0;
		equips = null;
		equipWeightItems = new LinkedList<>();
	}

	/**
	 * 根据玩家等级获得合成配置
	 * 
	 * @param playerLevel
	 * @return
	 */
	public static CompoundCfg getCompoundCfg(int playerLevel) {
		List<CompoundCfg> compoundCfgs = ConfigManager.getInstance().getConfigList(CompoundCfg.class);
		if (compoundCfgs != null) {
			for (CompoundCfg cc : compoundCfgs) {
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

	public String getEquips() {
		return equips;
	}

	@Override
	protected boolean assemble() {
		equipWeightItems.clear();
		
		if(equips != null && equips.length() > 0) {
			String[] itemStrs = equips.split(",");
			for (String itmeStr : itemStrs) {
				String[] es = itmeStr.split("_");
				ItemInfo itemInfo = new ItemInfo();
				itemInfo.setType(Integer.valueOf(es[0]));
				itemInfo.setItemId(Integer.valueOf(es[1]));
				itemInfo.setQuantity(Integer.valueOf(es[2]));
				WeightItem<ItemInfo> weightItem = WeightItem.valueOf(itemInfo, Integer.valueOf(es[3]));
				equipWeightItems.add(weightItem);
			}
		}
		return true;
	}

	/**
	 * 随机出装备Id
	 * @return
	 */
	public int randomEquipId() {
		return AwardUtil.randomDrop(WeightUtil.random(equipWeightItems).getType()).getItemId();
	}
	
	@Override
	protected boolean checkValid() {
		return true;
	}
	
	/**
	 * 清理相关静态数据
	 */
	protected void clearStaticData() {
	}
}
