package com.guaji.game.config;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.guaji.config.ConfigBase;
import org.guaji.config.ConfigManager;
import org.guaji.os.GuaJiRand;

import com.guaji.game.module.activity.halloween.HalloweenStatus;

/**
 * 大转盘活动开启宝箱奖励
 */
@ConfigManager.XmlResource(file = "xml/halloweenDrop.xml", struct = "map")
public class HalloweenDropCfg extends ConfigBase {
	/**
	 * 奖池ID定义
	 */
	public static final int POOL_TYPE_COMMON = 1; // 普通奖池
	public static final int POOL_TYPE_ADVANCED = 2; // 高级奖池

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
	private static Map<Integer, List<HalloweenDropCfg>> totalMap = new HashMap<>();

	/**
	 * 特殊次数奖池 <poolId, Map<次数,TurntableDropCfg>>
	 */
	private static Map<Integer, Map<Integer, HalloweenDropCfg>> specialTimesMap = new HashMap<>();

	public HalloweenDropCfg() {
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
	private static HalloweenDropCfg getRandmReward(int poolId) {
		List<HalloweenDropCfg> cfgs = totalMap.get(poolId);
		List<Integer> weightList = new ArrayList<>();

		for (HalloweenDropCfg cfg : cfgs) {
			weightList.add(cfg.getDrawRate());
		}
		return GuaJiRand.randonWeightObject(cfgs, weightList);
	}

	@Override
	protected boolean assemble() {

		// 普通奖池
		if (totalMap.containsKey(poolId)) {
			totalMap.get(poolId).add(this);
		} else {
			List<HalloweenDropCfg> cfgs = new ArrayList<>();
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
				specialTimesMap.get(poolId).put(Integer.valueOf(times), this);
			}
		} else {
			Map<Integer, HalloweenDropCfg> rewardMap = new HashMap<>();
			for (String times : limitSt) {
				rewardMap.put(Integer.valueOf(times), this);
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
	 * 是不是10的整数倍<br>
	 * 是：进入高级奖池，如果是高级奖池里的特殊次数，按特殊次数算，否则进行高级奖池随机<br>
	 * 否：进入普通奖池，如果是普通奖池里的特殊次数，按特殊次数算，否则进行普通奖池随机<br>
	 */
	public static List<HalloweenDropCfg> dropReward(HalloweenStatus status, int drawTimes) {
		List<HalloweenDropCfg> cfgList = new ArrayList<HalloweenDropCfg>();
		int commonTimes = status.getCommonTimes();
		int advancedTimes = status.getAdvancedTimes();
		int totalTimes = status.getTotalTimes();
		/** 高级或限定 **/
		for (int i = 1; i <= drawTimes; i++) {
			totalTimes++;
			if (totalTimes % WILL_HIT_FACTOR == 0) {
				/** 高级奖池的特殊次数奖池 **/
				Map<Integer, HalloweenDropCfg> map = specialTimesMap.get(POOL_TYPE_ADVANCED);
				if (null == map)
					continue;
				advancedTimes++;
				HalloweenDropCfg cfg = map.get(advancedTimes);
				if (null != cfg) {
					cfgList.add(cfg);
					continue;
				}
				/** 高级奖池纯随机 **/
				cfg = getRandmReward(POOL_TYPE_ADVANCED);
				if (null != cfg) {
					cfgList.add(cfg);
					continue;
				}
			} else {
				/** 普通奖池的特殊次数奖池 **/
				Map<Integer, HalloweenDropCfg> map = specialTimesMap.get(POOL_TYPE_COMMON);
				commonTimes++;
				HalloweenDropCfg cfg = map.get(commonTimes);
				if (null != cfg) {
					cfgList.add(cfg);
					continue;
				}
				/** 普通奖池纯随机 **/
				cfg = getRandmReward(POOL_TYPE_COMMON);
				if (null != cfg) {
					cfgList.add(cfg);
					continue;
				}
			}
		}
		// 更新次数
		status.setTotalTimes(totalTimes);
		status.setCommonTimes(commonTimes);
		status.setAdvancedTimes(advancedTimes);
		return cfgList;
	}

}
