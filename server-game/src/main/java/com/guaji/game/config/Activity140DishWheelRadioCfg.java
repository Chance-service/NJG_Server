package com.guaji.game.config;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.guaji.config.ConfigBase;
import org.guaji.config.ConfigManager;
import org.guaji.os.GuaJiRand;

/**
 * 夺宝奇兵每日次数与vip等级挂钩
 * 
 * @author rudy
 */
@ConfigManager.XmlResource(file = "xml/Activity140_DishWheelRadio.xml", struct = "map")
public class Activity140DishWheelRadioCfg extends ConfigBase {
	/**
	 * vip等级
	 */
	@Id
	private final int id;

	/**
	 * 类型 1 轮盘内环 2 轮盘外环
	 */
	private final int type;
	/**
	 * 对应配置的索引
	 */
	private final int index;

	/**
	 * 返利系数
	 */
	private final int ratio;

	/**
	 * 被转到的概率
	 */
	private final int weight;

	/**
	 * 各个分组的总权重值
	 */
	public static Map<Integer, Integer> totalWeight = new HashMap<Integer, Integer>();

	/**
	 * 各个分组中的配置
	 */
	public static Map<Integer, List<Activity140DishWheelRadioCfg>> donateMap = new HashMap<Integer, List<Activity140DishWheelRadioCfg>>();

	public Activity140DishWheelRadioCfg() {
		id = 0;
		type = 0;
		index = 0;
		ratio = 0;
		weight = 0;
	}

	@Override
	protected boolean assemble() {
		Integer value = totalWeight.get(this.getType());
		if (null == value) {
			value = this.weight;
		} else {
			value += this.weight;
		}
		totalWeight.put(this.getType(), value);

		if (donateMap.containsKey(this.getType())) {
			List<Activity140DishWheelRadioCfg> list = donateMap.get(this.getType());
			list.add(this);
		} else {
			List<Activity140DishWheelRadioCfg> list = new ArrayList<Activity140DishWheelRadioCfg>();
			list.add(this);
			donateMap.put(this.getType(), list);
		}
		return super.assemble();
	}

	public int getId() {
		return id;
	}

	public int getType() {
		return type;
	}

	public int getIndex() {
		return index;
	}

	public int getRatio() {
		return ratio;
	}

	public int getWeight() {
		return weight;
	}

	/**
	 * 随机该类型奖励
	 */
	public static Activity140DishWheelRadioCfg getRandmCfg(int type) {

		if (!donateMap.containsKey(type) || donateMap.get(type) == null) {
			return null;
		}
		// 随机出轮盘
		List<Integer> weightList = new ArrayList<>();
		donateMap.get(type).forEach(itemCfg -> {
			weightList.add(itemCfg.getWeight());
		});

		return GuaJiRand.randonWeightObject(donateMap.get(type), weightList);
	}

	public static Activity140DishWheelRadioCfg getDishWheelRadioCfg(int type, int index) {
		if (!donateMap.containsKey(type) || index > donateMap.get(type).size() || index <= 0 ) {
			return null;
		}
		return donateMap.get(type).get(index - 1);
	}

	/**
	 * 清理相关静态数据
	 */
	protected void clearStaticData() {
	}
}
