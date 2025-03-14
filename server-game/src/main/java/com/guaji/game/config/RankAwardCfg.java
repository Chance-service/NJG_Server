package com.guaji.game.config;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.guaji.config.ConfigBase;
import org.guaji.config.ConfigManager;

/**
 * 竞技场排名奖励相关配置
 * 
 */
@ConfigManager.XmlResource(file = "xml/rankAward.xml", struct = "map")
public class RankAwardCfg extends ConfigBase {
	/**
	 * 配置id
	 */
	@Id
	protected final int id;
	/**
	 * 0.日排名獎勵 1.季排名獎勵
	 */
	protected final int type;
	/**
	 * 规则适用的最低排名
	 */
	protected final int minRank;
	/**
	 * 奖励字物品符串
	 */
	protected final String awardStr;
	
	protected static Map<Integer,List<RankAwardCfg>> typeAwardMap = new HashMap<>();

	public RankAwardCfg() {
		id = 0;
		type = 0;
		minRank = 0;
		awardStr = "";
	}

	public int getId() {
		return id;
	}
	
	public int getType() {
		return type;
	}

	public int getMinRank() {
		return minRank;
	}

	public String getAwardStr() {
		return awardStr;
	}

	@Override
	protected boolean assemble() {
		if (typeAwardMap.containsKey(this.getType())) {
			typeAwardMap.get(this.getType()).add(this);
		} else {
			List<RankAwardCfg> alist = new ArrayList<>();
			alist.add(this);
			typeAwardMap.put(this.getType(),alist);
		}
		return true;
	}

	@Override
	protected boolean checkValid() {
		return true;
	}
	
	/**
	 * 清理相关静态数据
	 */
	protected void clearStaticData() {
	}
	
	public static RankAwardCfg getAwardCfgByRank(int rank,int type){
		if (typeAwardMap.containsKey(type)) {
			for (RankAwardCfg cfg : typeAwardMap.get(type)) {
				if (rank <= cfg.getMinRank())
				return cfg;
			}
		}
		
		return typeAwardMap.get(type).get(typeAwardMap.get(type).size()-1);
	}
}
