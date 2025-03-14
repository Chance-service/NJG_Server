package com.guaji.game.config;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.guaji.config.ConfigBase;
import org.guaji.config.ConfigManager;
import org.guaji.os.GuaJiRand;

/**
 * 大转盘活动开启宝箱奖励
 * 
 * @author Melvin.Mao
 * @date 2017年9月14日 下午5:14:52
 */
@ConfigManager.XmlResource(file = "xml/turntableDrop.xml", struct = "map")
public class TurntableDropCfg extends ConfigBase {
	/**
	 * 奖池ID定义
	 */
	public static final int POOL_TYPE_COMMON = 1; // 普通奖池
	public static final int POOL_TYPE_SPECIAL = 2; // 特殊奖池

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
	private static Map<Integer, List<TurntableDropCfg>> totalMap = new HashMap<>();

	/**
	 * 特殊次数奖池 <poolId, Map<次数,TurntableDropCfg>>
	 */
	private static Map<Integer, Map<Integer, TurntableDropCfg>> specialTimesMap = new HashMap<>();

	public TurntableDropCfg() {
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
	private static TurntableDropCfg getRandmReward(int poolId) {
		List<TurntableDropCfg> cfgs = totalMap.get(poolId);
		List<Integer> weightList = new ArrayList<>();

		for (TurntableDropCfg cfg : cfgs) {
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
			List<TurntableDropCfg> cfgs = new ArrayList<>();
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
			Map<Integer, TurntableDropCfg> rewardMap = new HashMap<>();
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
	 * 先判断是不是10的整数倍,如果是,进入必中奖池,如果是必中次数,进入必中奖池, 最后根据概率映射到普通奖池<br>
	 * 返回：ItemInfo形式的字符串,List集合
	 */
	public static List<TurntableDropCfg> dropReward(int totalTimes, int drawTimes) {
		List<TurntableDropCfg> cfgList = new ArrayList<TurntableDropCfg>();

		/** 高级或限定 **/
		for (int i = 1; i <= drawTimes; i++) {
			totalTimes++;
			/** 优先从特殊奖池拿 **/
			if (totalTimes % WILL_HIT_FACTOR == 0) {
				List<TurntableDropCfg> list = totalMap.get(POOL_TYPE_SPECIAL);
				TurntableDropCfg cfg = list.get(0);
				cfgList.add(cfg);
				continue;
			}
			/** 特殊次数奖池 **/
			Map<Integer, TurntableDropCfg> map = specialTimesMap.get(POOL_TYPE_SPECIAL);
			TurntableDropCfg cfg = map.get(totalTimes);
			if (null != cfg) {
				cfgList.add(cfg);
				continue;
			}
			/** 随机奖池 **/
			cfg = getRandmReward(POOL_TYPE_COMMON);
			if (null != cfg) {
				cfgList.add(cfg);
			}
		}
		return cfgList;
	}

}
