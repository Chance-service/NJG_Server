package com.guaji.game.config;

import java.util.List;

import org.guaji.config.ConfigBase;
import org.guaji.config.ConfigManager;

import com.guaji.game.item.ItemInfo;

/**
 * 英雄升級表
 * 
 * @author Administrator
 *
 */
@ConfigManager.XmlResource(file = "xml/Hero_Level.xml", struct = "map")
public class HeroUpLevelCfg extends ConfigBase {
	/**
	 * 等級索引
	 */
	@Id
	protected final int Level;
	/**
	 * 消耗物品验证
	 */
	protected final String needItem;
	/**
	 * 
	 */
	protected List<ItemInfo> needItemList;
	/**
	 * 最高等級
	 */
	private static int maxLevel ;

	public HeroUpLevelCfg() {
		Level = 0;
		needItem = null;
		needItemList = null;
	}

	public int getLevel() {
		return Level;
	}
	
	public List<ItemInfo> getItemList(){
		return needItemList;
	}

	public static int getMaxLevel() {
		return maxLevel;
	}

	public static void setMaxLevel(int Level) {
		if (Level > maxLevel) {
			maxLevel = Level;
		}
	}

	@Override
	protected boolean assemble() {
		setMaxLevel(Level);
		if (!needItem.isEmpty()) {
			needItemList = ItemInfo.valueListOf(needItem);
		}
		return true;
	}

	@Override
	protected boolean checkValid() {
		return true;
	}

	/**
	 * 获取对应配置
	 * 
	 * @param id
	 * @param lv
	 * @return
	 */
	public static HeroUpLevelCfg getRoleUpLevelCfg(int lv) {
		HeroUpLevelCfg cfg = ConfigManager.getInstance().getConfigByKey(HeroUpLevelCfg.class,lv);
		return cfg;
	}
	
	/**
	 * 清理相关静态数据
	 */
	protected void clearStaticData() {
		
	}
}
