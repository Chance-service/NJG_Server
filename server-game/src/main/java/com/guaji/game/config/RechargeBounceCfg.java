package com.guaji.game.config;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.guaji.config.ConfigBase;
import org.guaji.config.ConfigManager;

import com.guaji.game.util.GsConst;

@ConfigManager.XmlResource(file = "xml/RechargeBounceCfg.xml", struct = "map")
public class RechargeBounceCfg extends ConfigBase {
	/**
	 * 配置id
	 */
	@Id
	private final int id;
	/**
	 * 活動類型
	 */
	private final int type;
	/**
	 * typy 詳細時間索引(RechargeBounceTimeIndex.xml)
	 */
	private final int timeindex;
	/**
	 * 獎勵領取次數(just for type = 2)
	 */
	private final int count;
	/**
	 * 適用平台
	 */
	private final int platformtype;
	/**
	 * 所需條件
	 */
	private final int needcount;
	/**
	 * 奖励
	 */
	private final String reward;
	/**
	 * 規則檢查表 <platformId ,<type,Set<TimeIndex>>>
	 */
	private static Map<Integer,Map<Integer,Set<Integer>>> checkrule = new HashMap<>();

	public RechargeBounceCfg() {
		this.id = 0;
		this.type = 0;
		this.timeindex = 0;
		this.count = 0;
		this.platformtype = 0;
		this.needcount = 0;
		this.reward = "";
	}

	public int getId() {
		return id;
	}
	
	public int getType() {
		return type;
	}
	
	public int getTimeindex() {
		return timeindex;
	}

	public int getCount() {
		return count;
	}

	public int getPlatformtype() {
		return platformtype;
	}

	public int getNeedcount() {
		return needcount;
	}

	public String getReward() {
		return reward;
	}

	/**
	 * 数据格式转化
	 */
	@Override
	protected boolean assemble() {
		checkrule.clear();
		
		if (checkrule.containsKey(this.platformtype)) {
			if (checkrule.get(this.platformtype).containsKey(this.type)) {
				// just add timeIndex
				if (!checkrule.get(this.platformtype).get(this.type).contains(this.timeindex)) {
					checkrule.get(this.platformtype).get(this.type).add(this.timeindex);
				}
			} else { // add type timeIndex
				Set<Integer> TimeIdxList = new HashSet<>();
				TimeIdxList.add(this.timeindex);
				checkrule.get(this.platformtype).put(this.type,TimeIdxList);
			}
		} else { // add platform type timeIndex
			Set<Integer> TimeIdxList = new HashSet<>();
			TimeIdxList.add(this.timeindex);
			Map<Integer,Set<Integer>> typeMap = new HashMap<>();
			typeMap.put(this.type, TimeIdxList);
			checkrule.put(this.platformtype, typeMap);
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
		Map<Long,Long> timecheck = new HashMap<>();
		for (Integer pId:checkrule.keySet()) {
			for (Integer type : checkrule.get(pId).keySet()) {
				if (checkrule.get(pId).get(type).size() > 1) {
					// type time overlapping
					timecheck.clear();
					for (Integer timeIdx :checkrule.get(pId).get(type)) {
						RechargeBounceTimeIndexCfg indexCfg = ConfigManager.getInstance().getConfigByKey(RechargeBounceTimeIndexCfg.class, timeIdx);
						if (timecheck.containsKey(indexCfg.getlStartTime())) {
							// 開始時間相同
							throw new RuntimeException("same time of one type in RechargeBounceCfg, platform : " + pId +" type : " + type);
						} else {
							timecheck.put(indexCfg.getlStartTime(), indexCfg.getlEndTime());
						}
					}
					for (Map.Entry<Long, Long> aentry :timecheck.entrySet()) {
						long startTime = aentry.getKey();
						for(Map.Entry<Long, Long> bentry :timecheck.entrySet()) {
							if (startTime == bentry.getKey()) {
								continue;
							}
							if ((startTime > bentry.getKey()) && (startTime <= bentry.getValue())) {
								// 時間有重疊
								throw new RuntimeException("overlapping time of one type in RechargeBounceCfg, platform : " + pId +" type : " + type);
							}
						}
					}
				}
			}
		}
		return true;
	}

	/**
	 * 清理相关静态数据
	 */
	protected void clearStaticData() {
	}
	
	
	public static List<RechargeBounceCfg> getCfgByPlatform(int platformId){
		List<RechargeBounceCfg> platformCfg = new ArrayList<>();
		for (RechargeBounceCfg acfg : ConfigManager.getInstance().getConfigMap(RechargeBounceCfg.class).values()) {
			RechargeBounceTimeIndexCfg rbtcfg =  ConfigManager.getInstance().getConfigByKey(RechargeBounceTimeIndexCfg.class,acfg.getTimeindex());
			if ((acfg.getPlatformtype() == platformId)&&(rbtcfg.isValid())) {
				platformCfg.add(acfg);
			}
		}
		return platformCfg;
	}
	
	public static Map<Integer,RechargeBounceCfg> getSingleMark(int platformId,int timeIndex){
		Map<Integer,RechargeBounceCfg> aList = new HashMap<>();
		for (RechargeBounceCfg acfg : ConfigManager.getInstance().getConfigMap(RechargeBounceCfg.class).values()) {
			if ((acfg.getPlatformtype() == platformId) 
					&& (acfg.getType() == GsConst.RechargeBounceType.Single)
					&& (acfg.getTimeindex() == timeIndex)
					) {
				aList.put(acfg.getNeedcount(),acfg);
			}
		}
		return aList;
	}
	
	public static int getValidTimeIndex(int platformId,int type) {
		for (RechargeBounceCfg acfg : ConfigManager.getInstance().getConfigMap(RechargeBounceCfg.class).values()) {
			RechargeBounceTimeIndexCfg rbtcfg =  ConfigManager.getInstance().getConfigByKey(RechargeBounceTimeIndexCfg.class,acfg.getTimeindex());
			if ((acfg.getPlatformtype() == platformId)&&(acfg.getType() == type)&&(rbtcfg.isValid())) {
				return acfg.getTimeindex();
			}
		}
		return 0;
	}
}
