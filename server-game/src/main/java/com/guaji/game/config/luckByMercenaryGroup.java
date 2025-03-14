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
 * @author
 * 
 */

@ConfigManager.XmlResource(file = "xml/luckByMercenaryGroup.xml", struct = "map")
public class luckByMercenaryGroup extends ConfigBase {
	/**
	 * ID
	 */
	@Id
	private final int id;

	/**
	 * 缘分所属副将编号
	 */
	private final int mercenaryId;

	/**
	 * 包含关联副将编号
	 */
	protected final String include;
	/**
	 * 缘分关联副将编号 refMercenaryId
	 */
	private List<Integer> refMercenaryIds;

	/**
	 * 奖励属性
	 */
	protected final String awardAttr;

	/**
	 * 所有加成属性
	 */
	private Map<Integer, Integer> attrs;

	/**
	 * 根据佣兵itemId
	 */
	private static Map<Integer, List<luckByMercenaryGroup>> roleLuckyItems;

	public luckByMercenaryGroup() {
		id = 0;
		include = "";
		mercenaryId = 0;
		awardAttr = "";
		attrs = new HashMap<>();
		refMercenaryIds = new ArrayList<>();
		roleLuckyItems = new HashMap<Integer, List<luckByMercenaryGroup>>();
	}

	@Override
	protected boolean assemble() {

		attrs.clear();
		refMercenaryIds.clear();

		String[] attrValues = awardAttr.split(",");
		for (String strAttr : attrValues) {
			String[] arrAttr = strAttr.split("_");
			if (arrAttr.length < 2) {
				return false;
			}
			int attrType = Integer.valueOf(arrAttr[0]);
			int attrValue = Integer.valueOf(arrAttr[1]);
			attrs.put(attrType, attrValue);
		}

		// 关联副将编号
		String[] arrMercenary = this.include.split(",");
		for (String strMercenary : arrMercenary) {
			this.refMercenaryIds.add(Integer.valueOf(strMercenary));
		}

		if (roleLuckyItems.containsKey(mercenaryId)) {
			roleLuckyItems.get(mercenaryId).add(this);
		} else {
			List<luckByMercenaryGroup> list = new ArrayList<>();
			list.add(this);
			roleLuckyItems.put(mercenaryId, list);
		}
		return true;
	}

	@Override
	protected boolean checkValid() {
		if (this.attrs.isEmpty() || this.refMercenaryIds.isEmpty()) {
			return false;
		}
		return super.checkValid();
	}

	public int getId() {
		return id;
	}

	public int getMercenaryId() {
		return mercenaryId;
	}

	public String getInclude() {
		return include;
	}

	public List<Integer> getRefMercenaryIds() {
		return refMercenaryIds;
	}

	public String getAwardAttr() {
		return awardAttr;
	}

	public Map<Integer, Integer> getAttrs() {
		return attrs;
	}

	public static Map<Integer, List<luckByMercenaryGroup>> getRoleLuckyItems() {
		return roleLuckyItems;
	}

}
