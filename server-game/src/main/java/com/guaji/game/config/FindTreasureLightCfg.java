package com.guaji.game.config;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.guaji.config.ConfigBase;
import org.guaji.config.ConfigManager;
import org.guaji.os.GuaJiRand;
import org.guaji.os.MyException;

import com.guaji.game.item.AwardItems;
import com.guaji.game.item.ItemInfo;

@ConfigManager.XmlResource(file = "xml/findTreasureLight.xml", struct = "map")
public class FindTreasureLightCfg extends ConfigBase {
	/**
	 * 奖池ID定义
	 */
	public static final int POOL_TYPE_ONE = 1;
	public static final int POOL_TYPE_TWO = 2;
	public static final int POOL_TYPE_THREE = 3;

	/**
	 * 掉落物(组)Id
	 */
	@Id
	private final int id;
	/**
	 * 探宝（奖池）类型
	 */
	private final int poolType;
	/**
	 * 池内物品编号
	 */
	private final int itemNo;
	/**
	 * 奖励物品
	 */
	private final String items;
	/**
	 * 权重
	 */
	private final int drawRate;
	/**
	 * 特殊次数掉落
	 */
	private final String limitTimes;

	/**
	 * 不同奖励特殊次数集合 Map<奖池ID, Set<特殊次数掉落, 掉落物组ID>>
	 */
	private static Map<Integer, TreeMap<Integer, Integer>> limitTimesMap = new HashMap<Integer, TreeMap<Integer, Integer>>();
	/**
	 * 奖池掉落物权重分布 Map<奖池ID, Map<掉落物组ID, 权重分布>>
	 */
	private static Map<Integer, TreeMap<Integer, Integer>> poolItemsMap = new HashMap<Integer, TreeMap<Integer, Integer>>();
	/**
	 * 奖池总权重
	 */
	private static Map<Integer, Integer> poolTotalRateMap = new HashMap<Integer, Integer>();

	public FindTreasureLightCfg() {
		id = 0;
		poolType = 0;
		itemNo = 0;
		items = "";
		drawRate = 0;
		limitTimes = "";
	}

	public int getId() {
		return id;
	}

	public int getPoolType() {
		return poolType;
	}

	public int getItemNo() {
		return itemNo;
	}

	public String getItems() {
		return items;
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
		limitTimesMap.clear();
		poolItemsMap.clear();
		poolTotalRateMap.clear();
	}

	@Override
	protected boolean assemble() {
		// 特殊次数掉落
		String[] specialTimes = limitTimes.trim().split(",");
		TreeMap<Integer, Integer> timesIdMap = limitTimesMap.get(this.poolType);
		if (timesIdMap == null) {
			timesIdMap = new TreeMap<Integer, Integer>();
		}
		for (String times : specialTimes) {
			if (Integer.valueOf(times) != 0) {
				timesIdMap.put(Integer.valueOf(times), this.id);
			}
		}
		limitTimesMap.put(this.poolType, timesIdMap);

		// 奖池掉落权重
		TreeMap<Integer, Integer> dropIdRateMap = poolItemsMap.get(this.poolType);
		if (dropIdRateMap == null) {
			dropIdRateMap = new TreeMap<Integer, Integer>();
		}
		dropIdRateMap.put(this.id, this.drawRate);
		poolItemsMap.put(this.poolType, dropIdRateMap);

		// 奖池总权重计算
		Integer totalRate = poolTotalRateMap.get(this.poolType);
		if (totalRate == null) {
			totalRate = 0;
		}
		totalRate += this.drawRate;
		poolTotalRateMap.put(this.poolType, totalRate);
		return true;
	}

	@Override
	protected boolean checkValid() {
		return true;
	}

	/**
	 * 寻宝掉落物配置
	 */
	public static FindTreasureLightCfg findLight(int poolType, int curTypeTotalTimes) {
		int totalRate = poolTotalRateMap.get(poolType);
		TreeMap<Integer, Integer> poolSpecialTimes = limitTimesMap.get(poolType);
		int dropId = 0;
		int times = curTypeTotalTimes + 1;
		if (poolSpecialTimes.containsKey(times)) {
			dropId = poolSpecialTimes.get(times);
		} else {
			try {
				int rand = GuaJiRand.randInt(1, totalRate);
				dropId = calcDropId(poolType, rand);
			} catch (MyException e) {
				MyException.catchException(e);
			}
		}
		FindTreasureLightCfg dropCfg = ConfigManager.getInstance().getConfigByKey(FindTreasureLightCfg.class, dropId);
		return dropCfg;
	}

	/**
	 * 多次寻宝掉落
	 * @param curTotalSearchTimes 当前已经寻宝总次数
	 * @param searchTimes 需要寻宝次数
	 * @return
	 */
	public static List<ItemInfo> findLight(int poolType, int curTypeTotalTimes, int findTimes){
		int totalRate = poolTotalRateMap.get(poolType);
		TreeMap<Integer, Integer> poolSpecialTimes = limitTimesMap.get(poolType);
		List<ItemInfo> items = new ArrayList<ItemInfo>();
		for(int i = 1; i <= findTimes; i++){
			try {
				int times = curTypeTotalTimes + i;
				int dropId = 0;
				if(poolSpecialTimes.containsKey(times)){
					dropId = poolSpecialTimes.get(times);
				}else{
					int rand = GuaJiRand.randInt(1, totalRate);
					dropId = calcDropId(poolType, rand);
				}
				TreasureRaiderDropCfg dropCfg = ConfigManager.getInstance().getConfigByKey(TreasureRaiderDropCfg.class, dropId);
				AwardItems awards = AwardItems.valueOf(dropCfg.getRewards());
				items.addAll(awards.getAwardItemInfos());
			} catch (MyException e) {
				MyException.catchException(e);
				continue;
			}
		}
		return items;
	}
	
	/**
	 * 计算掉落组id
	 * 
	 * @param poolType
	 *            奖池类型
	 * @param rand
	 *            权重区间随机数
	 * @return
	 */
	private static int calcDropId(int poolType, int rand) {
		TreeMap<Integer, Integer> rateMap = poolItemsMap.get(poolType);
		int acc = 0;
		for (Map.Entry<Integer, Integer> entry : rateMap.entrySet()) {
			acc += entry.getValue();
			if (rand <= acc) {
				return entry.getKey();
			}
		}
		return 0;
	}
}
