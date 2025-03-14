package com.guaji.game.config;

import java.util.ArrayList;
import java.util.List;

import org.guaji.config.ConfigBase;
import org.guaji.config.ConfigManager;

import com.guaji.game.item.ItemInfo;
import com.guaji.game.util.GsConst;

@ConfigManager.XmlResource(file = "xml/equipPunch.xml", struct = "map")
public class EquipPunchCfg extends ConfigBase {

	/**
	 * 配置id
	 */
	@Id
	protected final int id;

	/**
	 * 打孔消耗字符串
	 */
	protected final String punchCost;

	/**
	 * 消耗集合
	 */
	private List<ItemInfo> costList = new ArrayList<ItemInfo>();

	
	public EquipPunchCfg() {
		id = 0;
		punchCost = null;
	}

	public int getId() {
		return id;
	}

	public String getPunchCost() {
		return punchCost;
	}

	@Override
	protected boolean assemble() {

		if (this.punchCost == null || this.punchCost.equals("")) {
			return false;
		}

		String[] items = punchCost.split(",");
		if (items.length > GsConst.Equip.MAX_PUNCH_SIZE - 1) {
			return false;
		}

		for (int i = 0; i < items.length; i++) {
			costList.add(ItemInfo.valueOf(items[i]));
		}

		return true;
	}

	@Override
	protected boolean checkValid() {
		return true;
	}

	/**
	 * 根据孔的位置获得打孔消耗
	 * 
	 * @param punchPos
	 * @return
	 */
	public ItemInfo getCost(int punchPos) {
		return this.costList.get(punchPos - 2);
	}

	/**
	 * 清理相关静态数据
	 */
	protected void clearStaticData() {
	}

}
