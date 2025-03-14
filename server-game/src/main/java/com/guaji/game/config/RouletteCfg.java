package com.guaji.game.config;

import java.util.Map;

import org.guaji.config.ConfigBase;
import org.guaji.config.ConfigManager;
import org.guaji.os.GuaJiRand;
import org.guaji.os.MyException;

@ConfigManager.XmlResource(file = "xml/roulette.xml", struct = "map")
public class RouletteCfg extends ConfigBase {
	/**
	 * 总权重
	 */
	private static int totalWeightValue = -1;
	
	@Id
	private final int id;
	/**
	 * 转盘物品权重
	 */
	private final int weightValue;
	/**
	 * 对应物品
	 */
	private final String items;
	
	public RouletteCfg() {
		id = 0;
		weightValue = 0;
		items = "";
	}

	public int getId() {
		return id;
	}

	public int getWeightValue() {
		return weightValue;
	}

	public String getItems() {
		return items;
	}
	
	/**
	 * 转盘随机奖励配置
	 * @return
	 */
	public static RouletteCfg rollAwardCfg(){
		if(totalWeightValue <= 0){
			totalWeightValue = statisticsTotalWeightValue();
		}
		
		try {
			int seed = GuaJiRand.randInt(1, totalWeightValue);
			int acc = 0;// 辅助计算累加器
			Map<Object, RouletteCfg> rouletteCfgMap = ConfigManager.getInstance().getConfigMap(RouletteCfg.class);
			for(Map.Entry<Object, RouletteCfg> entry : rouletteCfgMap.entrySet()){
				RouletteCfg cfg = entry.getValue();
				if(seed > acc && seed <= (acc + cfg.getWeightValue())){
					return cfg;
				}
				acc += cfg.getWeightValue();
			}
		} catch (Exception e) {
			MyException.catchException(e);
		}
		return null;
	}
	
	/**
	 * 统计转盘总权重
	 */
	private static int statisticsTotalWeightValue(){
		int total = 0;
		Map<Object, RouletteCfg> rouletteCfgMap = ConfigManager.getInstance().getConfigMap(RouletteCfg.class);
		for(Map.Entry<Object, RouletteCfg> entry : rouletteCfgMap.entrySet()){
			RouletteCfg cfg = entry.getValue();
			total += cfg.getWeightValue();
		}
		return total;
	}
	
	/**
	 * 清理相关静态数据
	 */
	protected void clearStaticData() {
	}
}
