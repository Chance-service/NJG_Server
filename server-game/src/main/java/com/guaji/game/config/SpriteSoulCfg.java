package com.guaji.game.config;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.guaji.config.ConfigBase;
import org.guaji.config.ConfigManager;

import com.guaji.game.attribute.Attribute;
import com.guaji.game.item.ItemInfo;

@ConfigManager.XmlResource(file = "xml/spriteSoul.xml", struct = "map")
public class SpriteSoulCfg extends ConfigBase {

	/**
	 * 配置id
	 */
	@Id
	protected final int id;

	/**
	 * 星脉
	 */
	protected final int group;
	/**
	 * 星脉
	 */
	protected final double spriteratio;
	/**
	 * 星脉
	 */
	protected final double heroratio;

	/**
	 * 等级
	 */
	protected final int level;

	/**
	 * 激活所需要的消耗
	 */
	protected final String cost;

	/**
	 * 激活后主角提升的属性
	 */
	protected final String attr;

	/**
	 * 激活星魂所需要的材料集合
	 */
	private List<ItemInfo> activeCostList;

	/**
	 * 激活星魂后主角属性变化
	 */
	private Attribute attribute;


	public SpriteSoulCfg() {
		id = 0;
		group = 0;
		spriteratio = 0.0;
		heroratio = 0.0;
		level = 0;
		cost = null;
		attr = null;
		attribute = null;
		activeCostList = new ArrayList<ItemInfo>();
	}
	
	public List<ItemInfo> getActiveCostList() {
		return activeCostList;
	}

	public void setActiveCostList(List<ItemInfo> activeCostList) {
		this.activeCostList = activeCostList;
	}

	public Attribute getAttribute() {
		return attribute;
	}

	public void setAttribute(Attribute attribute) {
		this.attribute = attribute;
	}

	public int getId() {
		return id;
	}

	public int getGroup() {
		return group;
	}
	
	public double getSpriteratio() {
		return spriteratio;
	}
	
	public double getHeroratio() {
		return heroratio;
	}

	public int getLevel() {
		return level;
	}

	public String getCost() {
		return cost;
	}

	public String getAttr() {
		return attr;
	}

	/**
	 * 数据格式化
	 */
	@Override
	protected boolean assemble() {
		if (this.cost != null && !this.cost.equals("0") && this.cost.length() > 0) {
			this.activeCostList = ItemInfo.valueListOf(this.cost);
		}

		this.attribute = Attribute.valueOf(attr);
		return true;
	}

	/**
	 * 根据group和level获取整行数据
	 * 
	 * @param group
	 * @param level
	 */
	public static SpriteSoulCfg getCfg(int group, int level) {
		Map<Object, SpriteSoulCfg> map = ConfigManager.getInstance().getConfigMap(SpriteSoulCfg.class);
		for (Entry<Object, SpriteSoulCfg> entry : map.entrySet()) {
			if (entry.getValue().group == group && entry.getValue().level == level)
				return entry.getValue();
		}
		return null;
	}

	public static SpriteSoulCfg getSpriteSoulCfg(int id) {
		return ConfigManager.getInstance().getConfigByKey(SpriteSoulCfg.class, id);
	}


	/**
	 * 清理相关静态数据
	 */
	protected void clearStaticData() {
	}

}
