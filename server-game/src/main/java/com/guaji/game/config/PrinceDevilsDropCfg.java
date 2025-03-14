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

@ConfigManager.XmlResource(file = "xml/princeDevilsDrop.xml", struct = "map")
public class PrinceDevilsDropCfg extends ConfigBase {
	/** 奖池ID定义 */
	public static final int POOL_TYPE_NONE = 0;
	public static final int POOL_TYPE_SEARCH = 1;
	public static final int POOL_TYPE_LUCK = 2;
	public static final int POOL_TYPE_BIG_AWARD = 3;

	/** 掉落物组ID */
	@Id
	private final int id;
	/** 所属奖池 */
	private final int poolId;
	/** 奖励物品 */
	private final String rewards;
	/** 权重 */
	private final int drawRate;
	/** 特殊次数掉落 */
	private final String limitTimes;

	/** 不同奖励特殊次数集合 Map<奖池ID, Set<特殊次数掉落, 掉落物组ID>> */
	private static Map<Integer, TreeMap<Integer, Integer>> limitTimesMap = new HashMap<Integer, TreeMap<Integer, Integer>>();
	/** 奖池掉落物权重分布 Map<奖池ID, Map<掉落物组ID, 权重分布>> */
	private static Map<Integer, TreeMap<Integer, Integer>> poolItemsMap = new HashMap<Integer, TreeMap<Integer, Integer>>();
	/** 奖池总权重 */
	private static Map<Integer, Integer> poolTotalRateMap = new HashMap<Integer, Integer>();

	public PrinceDevilsDropCfg() {
		id = 0;
		poolId = POOL_TYPE_NONE;
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
		limitTimesMap.clear();
		poolItemsMap.clear();
		poolTotalRateMap.clear();
	}

	@Override
	protected boolean assemble() {
		// 特殊次数掉落
		String[] specialTimes = limitTimes.trim().split(",");
		TreeMap<Integer, Integer> timesIdMap = limitTimesMap.get(this.poolId);
		if (timesIdMap == null) {
			timesIdMap = new TreeMap<Integer, Integer>();
		}
		for (String times : specialTimes) {
			if (Integer.valueOf(times) != 0) {
				timesIdMap.put(Integer.valueOf(times), this.id);
			}
		}
		limitTimesMap.put(this.poolId, timesIdMap);

		// 奖池掉落权重
		TreeMap<Integer, Integer> dropIdRateMap = poolItemsMap.get(this.poolId);
		if (dropIdRateMap == null) {
			dropIdRateMap = new TreeMap<Integer, Integer>();
		}
		dropIdRateMap.put(this.id, this.drawRate);
		poolItemsMap.put(this.poolId, dropIdRateMap);

		// 奖池总权重计算
		Integer totalRate = poolTotalRateMap.get(this.poolId);
		if (totalRate == null) {
			totalRate = 0;
		}
		totalRate += this.drawRate;
		poolTotalRateMap.put(this.poolId, totalRate);
		return true;
	}

	@Override
	protected boolean checkValid() {
		return true;
	}

	/**
	 * 计算掉落组id
	 * 
	 * @param poolId
	 *            奖池类型
	 * @param rand
	 *            权重区间随机数
	 * @return
	 */
	private static int calcDropId(int poolId, int rand) {
		TreeMap<Integer, Integer> rateMap = poolItemsMap.get(poolId);
		int acc = 0;
		for (Map.Entry<Integer, Integer> entry : rateMap.entrySet()) {
			acc += entry.getValue();
			if (rand <= acc) {
				return entry.getKey();
			}
		}
		return 0;
	}

	/**
	 * 开宝藏
	 * 
	 * @param totalCount
	 * @param isLuck
	 * @return
	 */
	public static List<ItemInfo> snowSearch(int totalCount,int poolID) {
		int totalRate = poolTotalRateMap.get(poolID);
		TreeMap<Integer, Integer> poolSpecialTimes = limitTimesMap.get(poolID);
		int times = totalCount + 1;
		int dropId = 0;
		try {
			if (poolSpecialTimes.containsKey(times)) {
				dropId = poolSpecialTimes.get(times);
			} else {
				int rand = GuaJiRand.randInt(1, totalRate);
				dropId = calcDropId(poolID, rand);
			}
			PrinceDevilsDropCfg dropCfg = ConfigManager.getInstance().getConfigByKey(PrinceDevilsDropCfg.class, dropId);
			AwardItems awards = AwardItems.valueOf(dropCfg.getRewards());
			return awards.getAwardItemInfos();
		} catch (MyException e) {
			MyException.catchException(e);
		}
		return new ArrayList<ItemInfo>();
	}
}
