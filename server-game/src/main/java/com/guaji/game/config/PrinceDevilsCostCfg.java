package com.guaji.game.config;

import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

import org.guaji.config.ConfigBase;
import org.guaji.config.ConfigManager;
import org.guaji.os.GuaJiRand;

@ConfigManager.XmlResource(file = "xml/princeDevilsConst.xml", struct = "list")
public class PrinceDevilsCostCfg extends ConfigBase {

	/** 魔王宝藏活动宝藏个数 */
	private final int cellCount;
	/**奖池概率配置*/
	private final String poolRateStr;
	/** 魔王宝藏活动每次增加积分 */
	private final String addScoreStr;
	/** 魔王宝藏活动免费次数刷新时间 */
	private final int freeTime;
	/** 每次抽取所需钻石 */
	private final String priceStr;
	/**最高奖励衰减概率*/
	private final String topAwardRateDampingStr;
	/**最高奖励的最低概率*/
	private final String topAwardMinRateLimitStr;
	/**大奖池概率，key标识奖池ID，value概率区间*/
	private static TreeMap<Integer,Integer> poolRate = new TreeMap<Integer,Integer>();
	/**最高奖励概率衰减率，每中一次奖励，概率衰减指定值*/
	private static int topAwardRateDamping;
	/**最高奖励概率下限，当衰减到一定的值的时候不会在衰减*/
	private static int topAwardMinRateLimit;
	/** 幸运奖励概率 */
	private static Map<Integer, Integer> princeDevilsLuckRateMap = new HashMap<Integer, Integer>();

	/** 积分增加 */
	private static Map<Integer, Integer> princeDevilsScoreMap = new HashMap<Integer, Integer>();

	/** 每次抽取所需钻石 */
	private static Map<Integer, Integer> princeDevilsGoldMap = new HashMap<Integer, Integer>();

	public PrinceDevilsCostCfg() {
		cellCount = 0;
		poolRateStr = null;
		addScoreStr = null;
		freeTime = 0;
		priceStr = null;
		topAwardRateDampingStr=null;
		topAwardMinRateLimitStr=null;
	}

	/**
	 * 清理相关静态数据
	 */
	protected void clearStaticData() {
		princeDevilsLuckRateMap.clear();
		princeDevilsScoreMap.clear();
	}

	@Override
	protected boolean assemble() {
		if (topAwardRateDampingStr != null) {
			topAwardRateDamping = Integer.valueOf(topAwardRateDampingStr);
		}
		if (topAwardMinRateLimitStr != null) {
			topAwardMinRateLimit = Integer.valueOf(topAwardMinRateLimitStr);
		}
		if(poolRateStr!=null){
			String[] ss = poolRateStr.split(",");
			int totalRate = 0;
			for (String s : ss) {
				String[] sv = s.split("_");
				int poolID = Integer.valueOf(sv[0]);
				int rate = Integer.valueOf(sv[1]);
				totalRate+=rate;
				poolRate.put(poolID, totalRate);
			}
		}
		if (addScoreStr != null) {
			String[] ss = addScoreStr.split(",");
			for (String s : ss) {
				String[] sv = s.split("_");
				princeDevilsScoreMap.put(Integer.valueOf(sv[0]), Integer.valueOf(sv[1]));
			}
		}

		if (priceStr != null) {
			String[] ss = priceStr.split(",");
			for (String s : ss) {
				String[] sv = s.split("_");
				princeDevilsGoldMap.put(Integer.valueOf(sv[0]), Integer.valueOf(sv[1]));
			}
		}
		return true;
	}

	@Override
	protected boolean checkValid() {
		return true;
	}

	public int getCellCount() {
		return cellCount;
	}

	public String getAddScoreStr() {
		return addScoreStr;
	}

	public int getFreeTime() {
		return freeTime;
	}

	public String getPriceStr() {
		return priceStr;
	}

	/**
	 * 是否幸运
	 * 
	 * @param count
	 * @return
	 */
	public static boolean isPrinceDevilsLuck(int count) {
		if (princeDevilsLuckRateMap.containsKey(count)) {
			return GuaJiRand.randPercentRate(princeDevilsLuckRateMap.get(count));
		}
		return false;
	}

	/**
	 * 次数增加的积分
	 * 
	 * @param count
	 * @return
	 */
	public static int getPrinceDevilsScoreMap(int count) {
		return princeDevilsScoreMap.get(count);
	}

	/**
	 * 次数扣除的钻石
	 * 
	 * @param count
	 * @return
	 */
	public static int getPrinceDevilsGoldMap(int count) {
		return princeDevilsGoldMap.get(count);
	}

	/**
	 * 免费次数刷新时间(秒)
	 * 
	 * @return
	 */
	public int getRefreshTime() {
		return this.freeTime * 60 * 60;
	}
	/**
	 * 最高奖励概率衰减
	 * @return
	 */
	public int getTopAwardRateDamping() {
		return topAwardRateDamping;
	}
	/**
	 * 最高奖励概率下限
	 * @return
	 */
	public int getTopAwardMinRateLimit() {
		return topAwardMinRateLimit;
	}
	/**
	 * 大奖池概率
	 * @param rate
	 * @return
	 */
	public TreeMap<Integer, Integer> getPoolRate() {
		return poolRate;
	}
	
	/**
	 * 是否大奖
	 * @param poolID 奖池ID
	 * @return
	 */
	public boolean isBigAward(int poolID){
		if(poolID==poolRate.lastKey()){
			return true;
		}
		return false;
	}
}
