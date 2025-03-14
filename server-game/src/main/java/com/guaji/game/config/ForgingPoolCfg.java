package com.guaji.game.config;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.guaji.config.ConfigBase;
import org.guaji.config.ConfigManager;

/**
 * 神装锻造活动奖励池配置
 * 
 * @author Nannan.Gao
 * @date 2016-8-14 16:14:58
 */
@ConfigManager.XmlResource(file = "xml/forgingPool.xml", struct = "map")
public class ForgingPoolCfg extends ConfigBase {
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
	 * 是否赠送
	 */
	private final boolean isDonate;

	/**
	 * 物品信息
	 */
	private final String item;

	/**
	 * 捞中概率
	 */
	private final int weight;

	/**
	 * 世界邮件附带奖励
	 */
	private final String raward;

	/**
	 * 是否重置奖励池
	 */
	private final boolean isReset;

	/**
	 * 各个分组的总权重值
	 */
	public static Map<Integer, Integer> totalWeight = new HashMap<Integer, Integer>();

	/**
	 * 各个分组集合
	 */
	public static Map<Integer, List<Integer>> groupMap = new HashMap<Integer, List<Integer>>();

	/**
	 * 必送奖池分组集合，size必须是1啊
	 */
	public static Map<Integer,List<ForgingPoolCfg>> donateMap = new HashMap<Integer, List<ForgingPoolCfg>>();

	/**
	 * 各个分组集合--不含不含本服限量配置
	 */
	public static Map<Integer, List<Integer>> groupNoLimitMap = new HashMap<Integer, List<Integer>>();

	public ForgingPoolCfg() {
		this.id = 0;
		this.listId = 0;
		this.item = "";
		this.weight = 0;
		this.raward = "";
		this.isReset = false;
		this.isDonate = false;
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

		// 赠送奖池
		if (this.isDonate) {
			if (donateMap.containsKey(this.listId)) {
				List<ForgingPoolCfg> list = donateMap.get(this.listId);
				list.add(this);
			} else {
				List<ForgingPoolCfg> list = new ArrayList<ForgingPoolCfg>();
				list.add(this);
				donateMap.put(this.listId, list);
			}
			return true;
		}

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

		ForgingFeaturesCfg config = ConfigManager.getInstance().getConfigByIndex(ForgingFeaturesCfg.class, 0);
		Map<String, Integer> limitMap = config.getLimitMap();

		Map<Object, ForgingPoolCfg> configMap = ConfigManager.getInstance().getConfigMap(ForgingPoolCfg.class);
		for (ForgingPoolCfg poolCfg : configMap.values()) {
			if (!limitMap.containsKey(poolCfg.getItem())) {
				if (groupNoLimitMap.containsKey(poolCfg.getListId())) {
					List<Integer> list = groupNoLimitMap.get(poolCfg.getListId());
					list.add(poolCfg.getId());
				} else {
					List<Integer> list = new ArrayList<Integer>();
					list.add(poolCfg.getId());
					groupNoLimitMap.put(poolCfg.getListId(), list);
				}
			}
		}
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

	public boolean isDonate() {
		return isDonate;
	}

	public String getItem() {
		return item;
	}

	public int getWeight() {
		return weight;
	}

	public String getRaward() {
		return raward;
	}

	public boolean isReset() {
		return isReset;
	}

}
