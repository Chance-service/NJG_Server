package com.guaji.game.config;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.guaji.config.ConfigBase;
import org.guaji.config.ConfigManager;
import org.guaji.os.GuaJiRand;

import com.guaji.game.item.AwardItems;
import com.guaji.game.item.ItemInfo;
import com.guaji.game.protocol.Const;

/**
 * 特殊奖池配置
 */
@ConfigManager.XmlResource(file = "xml/haremDrop.xml", struct = "map")
public class HaremDropCfg extends ConfigBase {

	/**
	 * 特殊奖池类型
	 */
	private static final int ADVANCED_SPECIAL_POOL = 9;// 高级特殊奖池
	private static final int STRICT_SPECIAL_POOL = 8;// 限定特殊奖池
	private static final int NEW_STRICT_SPECIAL_POOL = 7;// 新手限定特殊奖池
	private static final int LIMIT_SPECIAL_POOL = 6;// 限时限定特殊奖池
	private static final int MIDDLE_SPECIAL_POOL = 10;// 限时限定特殊奖池
	/**
	 * 每几次进入必中奖池
	 */
	public static final int WILL_HIT_FACTOR = 10;

	/**
	 * 掉落物组ID
	 */
	@Id
	private final int id;
	/**
	 * 所属奖池
	 */
	private final int poolId;
	/**
	 * 奖励物品
	 */
	private final String rewards;
	/**
	 * 权重
	 */
	private final int drawRate;
	/**
	 * 特殊次数掉落
	 */
	private final String limitTimes;
	/**
	 * 奖池
	 */
	private static Map<Integer, List<HaremDropCfg>> totalMap = new HashMap<>();

	/**
	 * 特殊次数奖池 <pooid, Map<次数,奖励>>
	 */
	private static Map<Integer, Map<Integer, String>> specialTimesMap = new HashMap<>();

	public HaremDropCfg() {
		id = 0;
		poolId = 0;
		rewards = "";
		drawRate = 0;
		limitTimes = "";
	}

	public int getId() {
		return id;
	}

	public int getPoolId() {
		return poolId;
	}

	public String getRewards() {
		return rewards;
	}

	public int getDrawRate() {
		return drawRate;
	}

	public String getLimitTimes() {
		return limitTimes;
	}

	/**
	 * 清理相关静态数据
	 */
	protected void clearStaticData() {
		totalMap.clear();
		specialTimesMap.clear();
	}

	/**
	 * 随机该类型奖励
	 */
	private static String getRandmReward(int poolId) {
		List<HaremDropCfg> cfgs = totalMap.get(poolId);
		List<String> rewardList = new ArrayList<>();
		List<Integer> weightList = new ArrayList<>();

		for (HaremDropCfg cfg : cfgs) {
			rewardList.add(cfg.getRewards());
			weightList.add(cfg.getDrawRate());
		}

		return GuaJiRand.randonWeightObject(rewardList, weightList);
	}

	@Override
	protected boolean assemble() {

		// 普通奖池
		if (totalMap.containsKey(poolId)) {
			totalMap.get(poolId).add(this);
		} else {
			List<HaremDropCfg> cfgs = new ArrayList<>();
			cfgs.add(this);
			totalMap.put(poolId, cfgs);
		}

		// 特殊次数奖池
		String[] limitSt = new String[] {};

		if (StringUtils.isNotEmpty(limitTimes)) {
			limitSt = limitTimes.split(",");
		}

		if (specialTimesMap.containsKey(poolId)) {
			for (String times : limitSt) {
				specialTimesMap.get(poolId).put(Integer.valueOf(times), rewards);
			}
		} else {
			Map<Integer, String> rewardMap = new HashMap<>();
			for (String times : limitSt) {
				rewardMap.put(Integer.valueOf(times), rewards);
			}
			specialTimesMap.put(poolId, rewardMap);
		}
		return true;
	}

	@Override
	protected boolean checkValid() {
		return true;
	}

	/**
	 * 先判断是不是10的整数倍,如果是,进入必中奖池,如果是必中次数,进入必中奖池, 最后根据概率映射到普通奖池
	 */
	public static List<AwardItems> dropReward(int haremType, int totalTimes, int drawTimes) {
		List<AwardItems> items = new ArrayList<AwardItems>();
		int poolId = haremType;
		int specialPoolId = 0;
		/** 普通奖池 **/
		if (haremType == Const.HaremType.HAREM_TYPE_COMMON_VALUE) {
			for (int i = 1; i <= drawTimes; i++) {
				String itemInfoString = getRandmReward(poolId);
				List<ItemInfo> itemInfoList = ItemInfo.valueListOf(itemInfoString);
				AwardItems item = new AwardItems();
				item.addItemInfos(itemInfoList);
				items.add(item);
				totalTimes++;
			}
			return items;
		}
		/** 高级或限定 **/
		if (haremType == Const.HaremType.HAREM_TYPE_ADVANCED_VALUE) {
			specialPoolId = ADVANCED_SPECIAL_POOL;
		} else if (haremType == Const.HaremType.HAREM_TYPE_STRICT_VALUE) {
			specialPoolId = STRICT_SPECIAL_POOL;
		}else if(haremType == Const.HaremType.HAREM_TYPE_NEW_STRICT_VALUE){
			specialPoolId = NEW_STRICT_SPECIAL_POOL;
		}else if(haremType == Const.HaremType.HAREM_TYPE_LIMIT_VALUE){
			specialPoolId = LIMIT_SPECIAL_POOL;
		}else if(haremType ==Const.HaremType.HAREM_TYPE_MIDDLE_VALUE)
		{
			specialPoolId=MIDDLE_SPECIAL_POOL;
		}
		for (int i = 1; i <= drawTimes; i++) {
			totalTimes++;
			AwardItems item = new AwardItems();
			/** 优先从特殊奖池拿 **/
			if (totalTimes % WILL_HIT_FACTOR == 0) {
				//List<HaremDropCfg> list = totalMap.get(specialPoolId);
				//HaremDropCfg cfg = list.get(0);
				//String itemInfoString = cfg.getRewards();
				String itemInfoString = getRandmReward(specialPoolId);
				if (StringUtils.isNotEmpty(itemInfoString)) {
					items.add(item.addItemInfos(ItemInfo.valueListOf(itemInfoString)));
					continue;
				}
			}
			/** 特殊次数奖池 **/
			Map<Integer, String> map = specialTimesMap.get(poolId);
			String itemInfoString = map.get(totalTimes);
			if (StringUtils.isNotEmpty(itemInfoString)) {
				items.add(item.addItemInfos(ItemInfo.valueListOf(itemInfoString)));
				continue;
			}
			/** 随机奖池 **/
			itemInfoString = getRandmReward(poolId);
			if (StringUtils.isNotEmpty(itemInfoString)) {
				items.add(item.addItemInfos(ItemInfo.valueListOf(itemInfoString)));
			}
		}
		return items;
	}
}
