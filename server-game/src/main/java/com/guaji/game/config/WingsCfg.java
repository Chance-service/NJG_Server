package com.guaji.game.config;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.guaji.config.ConfigBase;
import org.guaji.config.ConfigManager;

import com.guaji.game.item.ItemInfo;
import com.guaji.game.protocol.Const;
import com.guaji.game.protocol.Const.attr;

@ConfigManager.XmlResource(file = "xml/wings.xml", struct = "map")
public class WingsCfg extends ConfigBase {
	/** 组装key系数1 */
	private static final int KEY_RATE_1 = 10000;
	/** 组装key系数2 */
	private static final int KEY_RATE_2 = 100;
	/** 翅膀升级的结果 */
	public static final int SUCCESS = 1;
	public static final int LOSED = 2;
	public static final int BACKED = 3;

	/**
	 * 配置id;
	 */
	@Id
	private final int id;
	/**
	 * 翅膀类型;
	 */
	private final int type;
	/**
	 * 翅膀等级;
	 */
	private final int level;
	/**
	 * 对应职业;
	 */
	private final int profession;
	/**
	 * 升级概率;
	 */
	private final int levelupRate;
	/**
	 * 失败概率;
	 */
	private final int levelRate;
	/**
	 * 掉级概率;
	 */
	private final int leveldownRate;
	/**
	 * 升级花费;
	 */
	private final String levelupCost;
	/**
	 * 幸运值;
	 */
	private final int luckyNum;
	/**
	 * 属性加成1;
	 */
	private final String attrs;

	public WingsCfg() {
		this.id = 0;
		this.level = 0;
		this.leveldownRate = 0;
		// 初始化值
		this.levelRate = 100;
		this.levelupRate = 0;
		this.levelupCost = "10000_1001_0";
		this.profession = 0;
		this.type = 0;
		this.luckyNum = 10;
		this.attrs = "1_0";
	}

	public static List<ItemInfo> getCostItem(int wingType, int level2, int prof) {
		WingsCfg wingsCfg = getWingCfgByKey(wingType, level2, prof);
		List<ItemInfo> list = new ArrayList<ItemInfo>();
		String cost = wingsCfg.getLevelupCost();
		String[] split = cost.split(",");
		for (String eachSplit : split) {
			list.add(ItemInfo.valueOf(eachSplit));
		}
		return list;
	}

	public static WingsCfg getWingCfgByKey(int wingType, int level2, int prof) {
		int key = wingType * KEY_RATE_1 + level2 * KEY_RATE_2 + prof;
		WingsCfg wingsCfg = ConfigManager.getInstance().getConfigByKey(WingsCfg.class, key);
		if (wingsCfg == null) {
			wingsCfg = new WingsCfg();
		}
		return wingsCfg;
	}

	public static Map<attr, Integer> getAttrMap(int wingType, int level2, int prof) {
		WingsCfg wingsCfg = getWingCfgByKey(wingType, level2, prof);
		Map<Const.attr, Integer> attrMap = new HashMap<Const.attr, Integer>();
		String attrs2 = wingsCfg.getAttrs();
		String[] split = attrs2.split(",");
		for (String eachAttr : split) {
			String[] split2 = eachAttr.split("_");
			Const.attr at = Const.attr.valueOf(Integer.parseInt(split2[0]));
			int num = Integer.parseInt(split2[1]);
			if (num <= 0) {
				continue;
			}
			if (attrMap.containsKey(at)) {
				attrMap.put(at, attrMap.get(at) + num);
			} else {
				attrMap.put(at, num);
			}
		}
		return attrMap;
	}

	public int getType() {
		return type;
	}

	public int getLevel() {
		return level;
	}

	public int getProfession() {
		return profession;
	}

	public int getLevelupRate() {
		return levelupRate;
	}

	public int getLevelRate() {
		return levelRate;
	}

	public int getLeveldownRate() {
		return leveldownRate;
	}

	public String getLevelupCost() {
		return levelupCost;
	}

	public int getId() {
		return id;
	}

	public String getAttrs() {
		return attrs;
	}

	/**
	 * 清理相关静态数据
	 */
	protected void clearStaticData() {
	}

	public int getLuckyNum() {
		return luckyNum;
	}

	public int getRate(int rateType) {
		switch (rateType) {
		case BACKED:
			return this.leveldownRate;
		case LOSED:
			return this.levelRate;
		case SUCCESS:
			return this.levelupRate;
		default:
			return 0;
		}
	}

}
