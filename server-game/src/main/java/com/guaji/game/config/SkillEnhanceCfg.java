package com.guaji.game.config;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.guaji.config.ConfigBase;
import org.guaji.config.ConfigManager;

import com.guaji.game.item.ItemInfo;

/**
 * 技能专精模板;
 * 
 * @author crazyjohn
 *
 */
@ConfigManager.XmlResource(file = "xml/skillEnhance.xml", struct = "map")
public class SkillEnhanceCfg extends ConfigBase {
	/** 主键 */
	@Id
	protected final int id;
	/** 技能id */
	protected final int skillId;
	/** 技能等级 */
	protected final int skillLevel;
	/** 角色等级限制 */
	protected final int roleLevel;
	/** 专精等级 */
	protected final int specializeLevel;
	/** 技能经验 */
	protected final int skillExp;
	/** 升级消耗物品限制 */
	protected final String limitItem;
	protected final List<ItemInfo> limitItemList = new ArrayList<ItemInfo>();

	public SkillEnhanceCfg() {
		id = 0;
		skillId = 0;
		skillLevel = 0;
		roleLevel = 0;
		specializeLevel = 0;
		skillExp = 0;
		limitItem = null;
	}

	public String getLimitItem() {
		return limitItem;
	}

	public List<ItemInfo> getLimitItemList() {
		return Collections.unmodifiableList(limitItemList);
	}

	public int getSkillExp() {
		return skillExp;
	}

	public int getId() {
		return id;
	}


	public int getSkillId() {
		return skillId;
	}


	public int getSkillLevel() {
		return skillLevel;
	}


	public int getRoleLevel() {
		return roleLevel;
	}


	public int getSpecializeLevel() {
		return specializeLevel;
	}


	@Override
	protected boolean assemble() {
		if(this.limitItem != null) {
			String[] itemInfoStrs = this.limitItem.split(",");
			for(String itemInfoString : itemInfoStrs) {
				String[] item = itemInfoString.split("_");
				if (item == null || item.length < 2) {
					continue;
				}
				ItemInfo itemInfo = new ItemInfo();
				itemInfo.setType(Integer.parseInt(item[0]));
				itemInfo.setItemId(Integer.parseInt(item[1]));
				itemInfo.setQuantity(0);
				this.limitItemList.add(itemInfo);
			}
		}
		return true;
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
