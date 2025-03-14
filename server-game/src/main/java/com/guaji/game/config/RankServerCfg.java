package com.guaji.game.config;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.guaji.config.ConfigBase;
import org.guaji.config.ConfigManager;
/**
 * 排行献礼活动配置
 * @author qianhang
 *
 */
@ConfigManager.XmlResource(file = "xml/rankServer_NG.xml", struct = "map")
public class RankServerCfg extends ConfigBase {
	
	@Id
	protected final int id;
	/** 類型 GsConst.SvrMissionType（type 1.玩家總戰力 2.等級 3.通關關卡 (英雄屬性 4.光5.暗 6.水,7.火,8.風）*/
	protected final int type;
	/** 達成值 */
	protected final int mission;
	/** 奖励 */
	protected final String award;
	
	protected static Map<Integer,List<RankServerCfg>> missionMap;
	
	//protected static Map<Integer,Integer> MaxMission;
	
	public RankServerCfg() {
		id = 0;
		type = 0;
		mission = 0;
		award = "";
		missionMap = new HashMap<>();
		//MaxMission = new HashMap<>();
	}
	
	@Override
	protected boolean assemble() {
		if (missionMap.containsKey(this.getType())) {
			missionMap.get(this.getType()).add(this);
		} else {
			List<RankServerCfg> alist = new ArrayList<>();
			alist.add(this);
			missionMap.put(this.getType(), alist);
		}
		
//		if (MaxMission.containsKey(this.getType())) {
//			if (MaxMission.get(this.getType()) < this.getMission()) {
//				MaxMission.replace(this.getType(),this.getMission());
//			}
//		} else {
//			MaxMission.put(this.getType(),this.getMission());
//		}
		return true;
	}
	
	public int getId() {
		return id;
	}
	
	public int getType() {
		return type;
	}
	
	public int getMission() {
		return mission;
	}
	
	public String getAward() {
		return award;
	}
		
	protected void clearStaticData() {
	}
	
	public static List<Integer> getCompleteCfgId(int atype,int value) {
		List<Integer> clist = new ArrayList<>();
		if (missionMap.containsKey(atype)) {
			for (RankServerCfg cfg : missionMap.get(atype)) {
				if (cfg.getMission() > value ) {
					break;
				}
				clist.add(cfg.getId());
			}
		}
		return clist;
	}
	
	public static boolean isTypeAllDone(int atype, Set<Integer> aSet) {
		if (missionMap.containsKey(atype)) {
			for (RankServerCfg cfg : missionMap.get(atype)) {
				if (!aSet.contains(cfg.getId())) {
					return false;
				}
			}
		} else {
			return false;
		}
		return true;
	}
}
