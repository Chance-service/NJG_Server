package com.guaji.game.config;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.guaji.config.ConfigBase;
import org.guaji.config.ConfigManager;
import org.guaji.os.GuaJiRand;

import com.guaji.game.entity.MaidenEncounterEntity;

/**
 */
@ConfigManager.XmlResource(file = "xml/maidenEncounter.xml", struct = "list")
public class MaidenEncounterCfg extends ConfigBase {

	public static final int TYPE_MAIDEN = 1;// 少女
	public static final int TYPE_DEVIL = 2;// 恶魔

	@Id
	protected final int id;

	/**
	 * 互动-类型1：少女 2：恶魔
	 */
	protected final int type;

	/**
	 * 一次互动消耗的物品
	 */
	protected final String interactCostItems;

	/**
	 * 一次互动消耗的金币
	 */
	protected final int interactCostGold;

	/**
	 * 刷新消耗的道具
	 */
	protected final String exclusiveReward;

	/**
	 * 阶段奖励获得专属奖励的概率
	 */
	protected final String exclusiveRate;

	/**
	 * 刷新消耗的钻石
	 */
	protected final int refreshGold;

	/**
	 * 随机到的概率
	 */
	protected final int maidenRate;

	/**
	 * 作弊必中次数
	 */
	protected final String cheat;

	/**
	 * 恶魔自动离开时间
	 */
	protected final int autoLeaveTime;

	/**
	 * 少女集合
	 */
	public static Map<Integer, MaidenEncounterCfg> maidenMap = new HashMap<Integer, MaidenEncounterCfg>();

	/**
	 * 每个少女对应的不同阶段、不同专属奖励概率集合Map<maidenId, Map<state,rate>
	 */
	public static Map<Integer, Map<Integer, Integer>> stageExclusiveMap = new HashMap<Integer, Map<Integer, Integer>>();
	
	/**
	 * 作弊集合
	 */
	public static Map<List<Integer>, Integer> cheatMap = new HashMap<List<Integer>, Integer>();
	
	public MaidenEncounterCfg() {
		this.id = 0;
		this.type = 1;
		this.interactCostItems = "";
		this.interactCostGold = 0;
		this.exclusiveReward = "";
		this.exclusiveRate = "";
		this.refreshGold = 0;
		this.maidenRate = 0;
		this.autoLeaveTime = 0;
		this.cheat = "";
	}

	@Override
	protected boolean assemble() {
		if (this.type == TYPE_MAIDEN) {
			maidenMap.put(this.id, this);
			Map<Integer, Integer> map = new HashMap<Integer, Integer>();
			String[] rates = this.exclusiveRate.split(",");
			for (String rate : rates) {
				String[] rateEntry = rate.split("_", 2);
				map.put(Integer.valueOf(rateEntry[0]), Integer.valueOf(rateEntry[1]));
			}
			stageExclusiveMap.put(this.id, map);
		}
		String[] cheats = this.cheat.split(",");
		List<Integer> list = new ArrayList<Integer>();
		for (String cheat : cheats) {
			list.add(Integer.valueOf(cheat));
		}
		cheatMap.put(list, this.id);
		return super.assemble();
	}

	public int getId() {
		return id;
	}

	public int getType() {
		return type;
	}

	public String getInteractCostItems() {
		return interactCostItems;
	}

	public int getInteractCostGold() {
		return interactCostGold;
	}

	public String getExclusiveReward() {
		return exclusiveReward;
	}

	public String getExclusiveRate() {
		return exclusiveRate;
	}

	public int getRefreshGold() {
		return refreshGold;
	}

	public int getMaidenRate() {
		return maidenRate;
	}

	public String getCheat() {
		return cheat;
	}

	public int getAutoLeaveTime() {
		return autoLeaveTime;
	}

	public static Map<Integer, MaidenEncounterCfg> getMaidenMap() {
		return maidenMap;
	}

	/**
	 * 随机一个少女
	 */
	public static MaidenEncounterCfg getRandomMaiden(MaidenEncounterEntity entity) {
		MaidenEncounterCfg cfg = cheatRandom(entity);
		if (null != cfg)
			return cfg;
		Collection<MaidenEncounterCfg> collection = maidenMap.values();
		List<Integer> rateList = new ArrayList<Integer>();
		List<MaidenEncounterCfg> cfgList = new ArrayList<MaidenEncounterCfg>();
		for (MaidenEncounterCfg maidenEncounterCfg : collection) {
			cfgList.add(maidenEncounterCfg);
			rateList.add(maidenEncounterCfg.getMaidenRate());
		}
		return GuaJiRand.randonWeightObject(cfgList, rateList);
	}

	/**
	 * 随机刷新一个对象
	 */
	public static MaidenEncounterCfg getRandomRefresh(MaidenEncounterEntity entity) {
		// 作弊
		MaidenEncounterCfg cfg = cheatRandom(entity);
		if (null != cfg)
			return cfg;
		List<MaidenEncounterCfg> list = ConfigManager.getInstance().getConfigList(MaidenEncounterCfg.class);
		List<Integer> rateList = new ArrayList<Integer>();
		List<MaidenEncounterCfg> cfgList = new ArrayList<MaidenEncounterCfg>();
		for (MaidenEncounterCfg maidenEncounterCfg : list) {
			// 新增需求，随机到的必须和上次的不一样
			if (entity.getCurrentIndex() == maidenEncounterCfg.getId())
				continue;
			cfgList.add(maidenEncounterCfg);
			rateList.add(maidenEncounterCfg.getMaidenRate());
		}
		return GuaJiRand.randonWeightObject(cfgList, rateList);
	}

	/**
	 * 随机作弊
	 */
	private static MaidenEncounterCfg cheatRandom(MaidenEncounterEntity entity) {
		for (List<Integer> list : cheatMap.keySet()) {
			if (list.contains(entity.getHistoryRandomTimes())) {
				int id = cheatMap.get(list);
				return ConfigManager.getInstance().getConfigByIndex(MaidenEncounterCfg.class, id);
			}
		}
		return null;
	}

	/**
	 * 是不是恶魔
	 */
	public boolean isDevil() {
		if (this.type == TYPE_DEVIL)
			return true;
		return false;
	}

	/**
	 * 随机额外的专属奖励，用于阶段完成时
	 */
	public static boolean randomExtraExclusiveReward(int maidenId, int stage) {
		Map<Integer, Integer> stageMap = stageExclusiveMap.get(maidenId);
		Integer rate = stageMap.get(stage);
		// 有可能没有概率
		if (null == rate)
			return false;
		return GuaJiRand.randPercentRate(rate);
	}

}
