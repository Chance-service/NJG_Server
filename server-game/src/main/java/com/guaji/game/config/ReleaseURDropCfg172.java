package com.guaji.game.config;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.guaji.config.ConfigBase;
import org.guaji.config.ConfigManager;
import org.guaji.log.Log;
import org.guaji.os.GuaJiRand;
import org.guaji.os.MyException;

import com.guaji.game.item.AwardItems;
import com.guaji.game.item.ItemInfo;

/**
 * 新神将投放奖池
 */
@ConfigManager.XmlResource(file = "xml/releaseURDrop172.xml", struct = "map")
public class ReleaseURDropCfg172 extends ConfigBase {
	/**
	 * 奖池ID定义
	 */
	public static final int POOL_TYPE_NONE = 0;
	public static final int POOL_TYPE_SEARCH = 1;
	public static final int POOL_TYPE_BOX = 2;

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
	 * 是否為限制掉落物,一旦掉落會清空保底次數累積
	 */
	private final int limitTimes;
	
	/**
	 * Map<獎池ID, List<所有權重>>
	 */
	private static Map<Integer,ArrayList<Integer>> drawRateMap =  new HashMap<Integer, ArrayList<Integer>>();
	
	/**
	 * Map<獎池ID, List<索引ID>>
	 */
	private static Map<Integer,ArrayList<Integer>> idMap =  new HashMap<Integer, ArrayList<Integer>>();
	

	public ReleaseURDropCfg172() {
		id = 0;
		poolId = POOL_TYPE_NONE;
		rewards = "";
		drawRate = 0;
		limitTimes = 0;
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

	public int getLimitTimes() {
		return limitTimes;
	}

	/**
	 * 清理相关静态数据
	 */
	protected void clearStaticData() {
		drawRateMap.clear();
		idMap.clear();
	}

	@Override
	protected boolean assemble() {
		// 特殊次数掉落
				
		if (drawRateMap.containsKey(this.poolId)) {
			drawRateMap.get(this.poolId).add(this.getDrawRate());
		} else {
			ArrayList<Integer> drawList = new ArrayList<>();
			drawList.add(this.getDrawRate());
			drawRateMap.put(this.poolId,drawList);
		}
						
		if (idMap.containsKey(this.poolId)) {
			idMap.get(this.poolId).add(this.getId());
		} else {
			ArrayList<Integer> idList = new ArrayList<>();
			idList.add(this.getId());
			idMap.put(this.poolId,idList);
		}
		
		for (int pool = POOL_TYPE_SEARCH ; pool <= POOL_TYPE_BOX ; pool++) {
			if (idMap.containsKey(pool)){
				if (drawRateMap.get(pool).size() != idMap.get(pool).size()) {
					Log.errPrintln("activity146DropCfg reward size error");
					return false;
				}
			}
		}
				
		return true;
	}

	@Override
	protected boolean checkValid() {
		return true;
	}
	
	public static ReleaseURDropCfg172 RandomReward(int PoolId) {
		
		ReleaseURDropCfg172 acfg = null;
		
		if ((PoolId == POOL_TYPE_SEARCH)||(PoolId == POOL_TYPE_BOX)) {
		    int rid = GuaJiRand.randonWeightObject(idMap.get(PoolId),drawRateMap.get(PoolId));	
		
		    acfg = ConfigManager.getInstance().getConfigByKey(ReleaseURDropCfg172.class,rid);
		}
		return acfg;
	}
	
	public static List<ItemInfo> getDropItem(int dropId) {
		List<ItemInfo> items = new ArrayList<ItemInfo>();
		try {
			ReleaseURDropCfg172 dropCfg = ConfigManager.getInstance().getConfigByKey(ReleaseURDropCfg172.class, dropId);
			AwardItems awards = AwardItems.valueOf(dropCfg.getRewards());
			items.addAll(awards.getAwardItemInfos());
		} catch (Exception e) {
			MyException.catchException(e);
		}
		return items;
	}
}
