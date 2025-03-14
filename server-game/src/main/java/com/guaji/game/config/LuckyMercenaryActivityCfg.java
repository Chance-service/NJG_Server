package com.guaji.game.config;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.guaji.config.ConfigBase;
import org.guaji.config.ConfigManager;

/***
 * 幸运福将配置
 * 
 * @author zhanglin <!-- id="" fortype="作用功能" type="0自己，1自己+加主角  2作用全体上阵佣兵"
 *         attrs="具体数值" roleId="指定佣兵ID" "-->
 */
@ConfigManager.XmlResource(file = "xml/luckyMercenary.xml", struct = "map")
public class LuckyMercenaryActivityCfg extends ConfigBase {
	/**
	 * ID
	 */
	@Id
	private final int id;
	/***
	 * 作用功能
	 */
	private final int fortype;
	/***
	 * type 0给主将加，1给指定佣兵加 2作用全体上阵佣兵
	 */
	private final int type;
	/***
	 * buff参数（ 参考ring.xml）
	 */
	private final String attrs;
	/***
	 * 当type为1的时候，该roleID表示具体加buff的佣兵ID
	 */
	private final int roleID;

	/**
	 * 根据战斗类型（FORtYPE）
	 */
	private static Map<Integer, List<LuckyMercenaryActivityCfg>> battleTypeMap;

	/**
	 * 根据佣兵itemId
	 */
	private static Map<Integer, List<LuckyMercenaryActivityCfg>> roleLuckyItems;

	public LuckyMercenaryActivityCfg() {
		id = 0;
		fortype = -1;
		type = -1;
		attrs = "";
		roleID = -1;
		battleTypeMap = new HashMap<Integer, List<LuckyMercenaryActivityCfg>>();
		roleLuckyItems = new HashMap<Integer, List<LuckyMercenaryActivityCfg>>();
	}

	@Override
	protected boolean assemble() {
		if (battleTypeMap.containsKey(fortype)) {
			battleTypeMap.get(fortype).add(this);
		} else {
			List<LuckyMercenaryActivityCfg> list = new ArrayList<>();
			list.add(this);
			battleTypeMap.put(fortype, list);
		}

		if (roleLuckyItems.containsKey(roleID)) {
			roleLuckyItems.get(roleID).add(this);
		} else {
			List<LuckyMercenaryActivityCfg> list = new ArrayList<>();
			list.add(this);
			roleLuckyItems.put(roleID, list);
		}
		return true;
	}

	public int getId() {
		return id;
	}

	public int getFortype() {
		return fortype;
	}

	public int getType() {
		return type;
	}

	public String getAttrs() {
		return attrs;
	}

	public int getRoleID() {
		return roleID;
	}

	/**
	 * 获取对应战斗类型的配置
	 * 
	 * @return
	 */
	public static Map<Integer, List<LuckyMercenaryActivityCfg>> getBattleTypeMap() {
		return battleTypeMap;
	}

	public static Map<Integer, List<LuckyMercenaryActivityCfg>> getRoleLuckyItems() {
		return roleLuckyItems;
	}

}
