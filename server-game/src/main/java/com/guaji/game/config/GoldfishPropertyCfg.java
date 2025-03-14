package com.guaji.game.config;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.guaji.config.ConfigBase;
import org.guaji.config.ConfigManager;

/**
 * 捞鱼活动中的鱼属性配置
 * 
 * @author Nannan.Gao
 * @date 2016-8-5 16:14:58
 */
@ConfigManager.XmlResource(file = "xml/goldfishProperty.xml", struct = "map")
public class GoldfishPropertyCfg extends ConfigBase {

	/**
	 * 配置id
	 */
	@Id
	private final int id;

	/**
	 * 分组类型
	 */
	private final int listId;

	/**
	 * 物品类型
	 */
	private final int itemType;

	/**
	 * 物品ID
	 */
	private final int itemId;

	/**
	 * 物品数量
	 */
	private final int itemCount;

	/**
	 * 进入该奖励池N次后获得当前鱼
	 */
	private final int limit;
	
	/**
	 * 捞中概率
	 */
	private final int weight;

	/**
	 * 捞中后获得积分
	 */
	private final int score;

	/**
	 * 各个分组的总权重值
	 */
	public static Map<Integer, Integer> totalWeight = new HashMap<Integer, Integer>();

	/**
	 * 各个分组集合
	 */
	public static Map<Integer, List<Integer>> groupMap = new HashMap<Integer, List<Integer>>();

	public GoldfishPropertyCfg() {

		this.id = 0;
		this.listId = 0;
		this.itemType = 0;
		this.itemId = 0;
		this.itemCount = 0;
		this.limit = 0;
		this.weight = 0;
		this.score = 0;
	}

	/**
	 * 数据格式转化
	 */
	@Override
	protected boolean assemble() {

		Integer value = totalWeight.get(this.listId);
		if (null == value) {
			value = this.weight;
		} else {
			value += this.weight;
		}
		totalWeight.put(this.listId, value);

		if (groupMap.containsKey(this.listId)) {
			List<Integer> list = groupMap.get(this.listId);
			list.add(this.id);
		} else {
			List<Integer> list = new ArrayList<Integer>();
			list.add(this.id);
			groupMap.put(this.listId, list);
		}
		return true;
	}

	/**
	 * 检测有消息
	 * 
	 * @return
	 */
	@Override
	protected boolean checkValid() {
		return true;
	}

	/**
	 * 清理相关静态数据
	 */
	protected void clearStaticData() {
	}

	public int getId() {
		return id;
	}

	public int getListId() {
		return listId;
	}

	public int getItemType() {
		return itemType;
	}

	public int getItemId() {
		return itemId;
	}

	public int getItemCount() {
		return itemCount;
	}

	public int getLimit() {
		return limit;
	}

	public int getWeight() {
		return weight;
	}

	public int getScore() {
		return score;
	}

}
