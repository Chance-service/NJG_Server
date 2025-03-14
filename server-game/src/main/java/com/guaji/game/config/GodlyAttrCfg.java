package com.guaji.game.config;

import java.util.LinkedList;
import java.util.List;

import org.guaji.config.ConfigBase;
import org.guaji.config.ConfigManager;

@ConfigManager.XmlResource(file = "xml/godlyAttr.xml", struct = "map")
public class GodlyAttrCfg extends ConfigBase {
	/**
	 * 配置id
	 */
	protected final int id;
	/**
	 * 神器属性1
	 */
	@Id
	protected final int attrId;

	protected final String levelAttr;

	private List<Integer> levelAttrList;

	protected final int baseAttr;

	protected final int growAttr;

	public GodlyAttrCfg() {
		id = 0;
		attrId = 0;
		levelAttr = null;
		baseAttr = 0;
		growAttr = 0;
		levelAttrList = new LinkedList<>();
	}

	public int getId() {
		return id;
	}

	public int getAttrId() {
		return attrId;
	}

	public String getLevelAttr() {
		return levelAttr;
	}

	public int getBaseAttr() {
		return baseAttr;
	}

	public int getGrowAttr() {
		return growAttr;
	}

	public int getLevelAttr(int strengthLevel) {
		if (strengthLevel == 0) {
			return levelAttrList.get(strengthLevel);
		}
		return levelAttrList.get(strengthLevel - 1);
	}

	@Override
	protected boolean assemble() {
		if (levelAttr != null) {
			String[] levelAttrs = levelAttr.split(",");
			for (String s : levelAttrs) {
				levelAttrList.add(Integer.valueOf(s));
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
