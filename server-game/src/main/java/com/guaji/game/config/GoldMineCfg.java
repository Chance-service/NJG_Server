package com.guaji.game.config;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.guaji.config.ConfigBase;
import org.guaji.config.ConfigManager;
import org.guaji.log.Log;
import org.guaji.os.GuaJiRand;

@ConfigManager.XmlResource(file = "xml/GoldMine.xml", struct = "map")
public class GoldMineCfg extends ConfigBase {
	/**
	 * 配置id
	 */
	@Id
	protected final int ID;	
	/**
	 * 最低需求等級
	 */
	protected final int MinLevel;
		/**
	 * 最高需求等級
	 */
	protected final int MaxLevel;
	/**
	 * 獎勵
	 */
	protected final String Awards;
	/**
	 * 獎勵比重
	 */
	protected final String Rate;
	/**
	 * 獎勵列表
	 */
	protected final List<String> AwardLsit;
	/**
	 * 比重列表
	 */
	protected final List<Integer> RateList;

	public GoldMineCfg() {
		ID = 0;
		MinLevel = 0;
		MaxLevel = 0;
		Awards = "";
		Rate = "";
		AwardLsit = new ArrayList<>();
		RateList = new ArrayList<>();
	}

	/**
	 * 数据格式转化
	 */
	@Override
	protected boolean assemble() {
		// 獎勵字串轉換為列表
		AwardLsit.clear();
		if (!Awards.isEmpty()) {
			String[] ss = Awards.split(",");
			for(String s : ss) {
				AwardLsit.add(s.trim());
			}
		}
		// 獎勵比重字串轉換為列表
		RateList.clear();
		if (!Rate.isEmpty()) {
			String[] ss = Rate.split(",");
			for(String s : ss) {
				RateList.add(Integer.valueOf(s.trim()));
			}
		}
		if (RateList.size() != AwardLsit.size()) {
			Log.errPrintln("activity165 GoldMineCfg reward size error");
			return false;
		}
		return true;
	}
	
	public int getId() {
		return ID;
	}
	
	public int getMinLevel() {
		return this.MinLevel;
	}
	
		public int getMaxLevel() {
		return this.MaxLevel;
	}
	
	public String getAwards() {
		return this.Awards;
	}
	
	public String getRandomAwardStr() {
		String AwardStr = GuaJiRand.randonWeightObject(AwardLsit, RateList);
		return AwardStr;
	}
	
	public static GoldMineCfg getGoldMineCfgByLv(int lv) {
		GoldMineCfg cfg = null;
		Map<Object,GoldMineCfg> cfgMap = ConfigManager.getInstance().getConfigMap(GoldMineCfg.class);
		for (GoldMineCfg acfg : cfgMap.values()) {
			if ((lv >= acfg.getMinLevel())&&(lv <= acfg.getMaxLevel())) {
				cfg = acfg;
				break;
			}
		}
		return cfg;
	}
}
