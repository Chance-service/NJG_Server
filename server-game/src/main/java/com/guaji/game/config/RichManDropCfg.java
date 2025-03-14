package com.guaji.game.config;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.guaji.config.ConfigBase;
import org.guaji.config.ConfigManager;
import org.guaji.os.GuaJiRand;

import com.guaji.game.module.activity.activity141.Activity141Status;

/**
 * 大富翁活动开启宝箱奖励
 */
@ConfigManager.XmlResource(file = "xml/richmanDrop.xml", struct = "map")
public class RichManDropCfg extends ConfigBase {
	/**
	 * 奖池ID定义
	 */
	public static final int POOL_TYPE_GIFT = 1; // 禮盒格
	public static final int POOL_TYPE_COIN = 2; // 紅包格
	public static final int POOL_TYPE_DICE = 3; // 骰子+1
	public static final int POOL_TYPE_DOUBLE = 4; // 獎勵加倍
	public static final int POOL_TYPE_OBJ = 5; // 兌換道具
	public static final int POOL_TYPE_ORIGIN = 6; // 原點獎勵
	public static final int OPERATE_ROUN_BONUS = 5;	//每5圈多給bonus

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
	 * 大富翁每五圈增加獎勵
	 */
	private final List<Integer> limitTimesList;
	/**
	 * 奖池
	 */
	private static Map<Integer, List<RichManDropCfg>> totalMap = new HashMap<>();
	/**
	 * 特殊次数奖池 <poolId, Map<次数,TurntableDropCfg>>
	 */
	//private static Map<Integer, Map<Integer, RichManDropCfg>> specialTimesMap = new HashMap<>();


	public RichManDropCfg() {
		id = 0;
		poolId = 0;
		rewards = "";
		drawRate = 0;
		limitTimes = "";
		limitTimesList = new LinkedList<>();
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

	/*
	 * public String getLimitTimes() { return limitTimes; }
	 */
	
	public int getLimitTimes(int step) {
		if (step >= limitTimesList.size()) {
			return limitTimesList.get(limitTimesList.size() - 1);
		}

		return limitTimesList.get(step);
	}

	/**
	 * 清理相关静态数据
	 */
	protected void clearStaticData() {
		totalMap.clear();
		//specialTimesMap.clear();
	}

	/**
	 * 随机该类型奖励
	 */
	public static RichManDropCfg getRandmReward(int poolId) {
		List<RichManDropCfg> cfgs = totalMap.get(poolId);
		List<Integer> weightList = new ArrayList<>();

		for (RichManDropCfg cfg : cfgs) {
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
			List<RichManDropCfg> cfgs = new ArrayList<>();
			cfgs.add(this);
			totalMap.put(poolId, cfgs);
		}
		
		if (StringUtils.isNotEmpty(limitTimes)){
			String[] items = limitTimes.split(",");
			for (String item : items) {
				limitTimesList.add(Integer.valueOf(item.trim()));
			}
		}
		/*
		 * // 特殊次数奖池 String[] limitSt = new String[] {};
		 * 
		 * if (StringUtils.isNotEmpty(limitTimes)) { limitSt = limitTimes.split(","); }
		 * 
		 * if (specialTimesMap.containsKey(poolId)) { for (String times : limitSt) {
		 * specialTimesMap.get(poolId).put(Integer.valueOf(times), this); } } else {
		 * Map<Integer, RichManDropCfg> rewardMap = new HashMap<>(); for (String times :
		 * limitSt) { rewardMap.put(Integer.valueOf(times), this); }
		 * specialTimesMap.put(poolId, rewardMap); }
		 */
		return true;
	}

	@Override
	protected boolean checkValid() {
		return true;
	}

	/**
	 * 是不是10的整数倍<br>
	 * 否：进入普通奖池，如果是普通奖池里的特殊次数，按特殊次数算，否则进行普通奖池随机<br>
	 */
	public static List<RichManDropCfg> dropReward(Activity141Status status, int dice) {
		List<RichManDropCfg> cfgList = new ArrayList<RichManDropCfg>();
		int index = status.getIndex()+dice;
		int finish = status.getfinish();
		
		//超過一圈給原點獎勵
		if (index > 24)
		{
			RichManDropCfg cfg = getRandmReward(POOL_TYPE_ORIGIN);
			if (null != cfg) {
				cfgList.add(cfg);
			}
			index = index % 24;
			finish = finish+1;
		}
		//int advancedTimes = status.getAdvancedTimes();
		//int totalTimes = status.getTotalTimes();
		
		int type = SysBasicCfg.getInstance().getRichManStpeType(index-1);

		/** 普通奖池纯随机 **/
		if (type != POOL_TYPE_ORIGIN)
		{
			RichManDropCfg cfg = getRandmReward(type);
			if (null != cfg) {
				cfgList.add(cfg);
			}
		}
		
		// 更新次数
		status.setIndex(index);
		status.setfinish(finish);
		//status.setisdouble(type == POOL_TYPE_DOUBLE);
		return cfgList;
	}
	
	/**
	 * 完成該圈獎勵<br>
	 */
	public static List<RichManDropCfg> getfinishReward() {
		List<RichManDropCfg> cfgList = new ArrayList<RichManDropCfg>();
		RichManDropCfg cfg = getRandmReward(POOL_TYPE_ORIGIN);
		if (null != cfg) {
			cfgList.add(cfg);
		}
		return cfgList;
	}
}
