package com.guaji.game.config;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.guaji.config.ConfigBase;
import org.guaji.config.ConfigManager;
import org.guaji.os.GuaJiRand;

/**
 * 天降福利活动配置(id:106)
 * 
 * @author Melvin.Mao
 * @date Aug 18, 2017 10:20:38 AM
 */
@ConfigManager.XmlResource(file = "xml/welfareReward.xml", struct = "map")
public class WelfareRewardCfg extends ConfigBase {

	@Id
	private final int id;

	/**
	 * 奖池
	 */
	private final int poolId;

	/**
	 * 消耗钻石
	 */
	private final int cost;

	/**
	 * 钻石奖励区间最小值
	 */
	private final int min;

	/**
	 * 钻石奖励区间最大值
	 */
	private final int max;

	/**
	 * 每个奖池的抽奖概率
	 */
	private final int drawRate;

	/**
	 * 奖池
	 */
	public static Map<Integer, List<WelfareRewardCfg>> poolMap = new HashMap<Integer, List<WelfareRewardCfg>>();

	/**
	 * 每一档的消耗钻石数量不变
	 */
	public static Map<Integer, Integer> costMap = new HashMap<Integer, Integer>();

	public int getId() {
		return id;
	}

	public int getPoolId() {
		return poolId;
	}

	public int getCost() {
		return cost;
	}

	public int getMin() {
		return min;
	}

	public int getMax() {
		return max;
	}

	public int getDrawRate() {
		return drawRate;
	}

	public WelfareRewardCfg() {
		this.id = 0;
		this.poolId = 0;
		this.cost = 0;
		this.min = 0;
		this.max = 0;
		this.drawRate = 0;
	}

	/**
	 * 数据格式转化
	 */
	@Override
	protected boolean assemble() {
		List<WelfareRewardCfg> list;
		if (!poolMap.containsKey(this.poolId)) {
			list = new ArrayList<WelfareRewardCfg>();
		} else {
			list = poolMap.get(poolId);
		}
		list.add(this);
		poolMap.put(this.poolId, list);
		if (!costMap.containsKey(this.poolId)) {
			costMap.put(this.poolId, this.cost);
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

	/**
	 * 获取是哪个奖励配置
	 */
	protected WelfareRewardCfg getRewardCfg(int pool) {
		List<Integer> rateList = new ArrayList<Integer>();
		List<WelfareRewardCfg> cfgList = new ArrayList<WelfareRewardCfg>();
		List<WelfareRewardCfg> poolList = poolMap.get(pool);
		for (WelfareRewardCfg cfg : poolList) {
			rateList.add(cfg.getDrawRate());
		}
		return GuaJiRand.randonWeightObject(cfgList, rateList);
	}

	/**
	 * 是不是最大的阶段
	 */
	public boolean isMaxStage() {
		Set<Integer> set = poolMap.keySet();
		int max = 1;
		for (Integer stage : set) {
			if (stage > max)
				max = stage;
		}
		if (this.poolId >= max)
			return true;
		else
			return false;
	}

}
