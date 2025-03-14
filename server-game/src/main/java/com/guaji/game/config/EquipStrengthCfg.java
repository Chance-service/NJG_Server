package com.guaji.game.config;

import java.util.HashMap;
import java.util.Map;

import org.guaji.config.ConfigBase;
import org.guaji.config.ConfigManager;

/**
 * 装备强化配置
 * 
 * @author xulinqs
 * 
 */
@ConfigManager.XmlResource(file = "xml/equipStrength.xml", struct = "map")
public class EquipStrengthCfg extends ConfigBase {
	/**
	 * 配置id
	 */
	@Id
	protected final int id;
	/**
	 * 主属性提高值
	 */
	protected final String primaryAttrValues;
	/**
	 * 转换主属性加成的数据结构
	 */
	protected final Map<Integer, Integer> primaryAttrMap;

	public EquipStrengthCfg() {
		id = 0;
		primaryAttrValues = null;
		primaryAttrMap = new HashMap<Integer, Integer>();
	}

	public int getId() {
		return id;
	}

	@Override
	protected boolean assemble() {
		primaryAttrMap.clear();
		
		int index = 0;
		String[] levelPrimaryAttrValues = primaryAttrValues.split(",");
		for (String levelPrimaryAttrStr : levelPrimaryAttrValues) {
			index++;
			primaryAttrMap.put(index, Integer.valueOf(levelPrimaryAttrStr));
		}
		return true;
	}

	/**
	 * 返回强化等级对应的主属性
	 * 
	 * @param strengthLevel
	 * @return
	 */
	public int getPrimaryAttrValue(int strengthLevel) {
		if (primaryAttrMap.containsKey(strengthLevel)) {
			return primaryAttrMap.get(strengthLevel);
		}
		return 0;
	}

	/**
	 * 清理相关静态数据
	 */
	protected void clearStaticData() {
	}
}