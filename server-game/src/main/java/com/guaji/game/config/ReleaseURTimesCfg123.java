package com.guaji.game.config;


import java.util.HashMap;
import java.util.Map;

import org.guaji.config.ConfigBase;
import org.guaji.config.ConfigManager;

/**
 * 新神将投放活动与vip等级挂钩
 */
/**
 * @author hanchao
 *
 */
@ConfigManager.XmlResource(file = "xml/releaseURTimes123.xml", struct = "map")
public class ReleaseURTimesCfg123 extends ConfigBase {
	/**
	 * vip等级
	 */
	@Id
	private final int vipLevel;
	/**
	 * 免费时间CD
	 */
	private final int freeCD;
	/***
	 * 单次抽奖花费元宝
	 */
	private final int singleCost;
	/***
	 * 十次抽奖花费元宝
	 */
	private final int tenCost;
	
	/***
	 * 随机抽取花费元宝
	 */
	private final int randCost;
	/***
	 * 随机概率配置
	 */
	private final String randRate;
	
	/**
	 * 技能列表
	 */
	Map<Integer,Integer> rateMap;


	public ReleaseURTimesCfg123() {
		vipLevel = 0;
		freeCD = 0;
		singleCost = 0;
		tenCost = 0;
		randCost=0;
		randRate="";
		rateMap = new HashMap<Integer,Integer>();
		
	}

	public int getVipLevel() {
		return vipLevel;
	}

	public int getFreeCountDown() {
		return freeCD;
	}

	public int getSingleCost() {
		return singleCost;
	}

	public int getTenCost() {
		return tenCost;
	}

	public int getRandCost() {
		return randCost;
	}

	public Map<Integer, Integer> getRateMap() {
		return rateMap;
	}

	public void setRateMap(Map<Integer, Integer> rateMap) {
		this.rateMap = rateMap;
	}

	public int getFreeCD() {
		return freeCD;
	}

	public String getRandRate() {
		return randRate;
	}

	@Override
	protected boolean assemble() {
		rateMap.clear();
		if (randRate!= null && randRate.length() > 0) {
			String[] items = randRate.split(",");
			for (String item : items) {
				String[] infos = item.split("_");
				if(infos.length==2)
					rateMap.put(Integer.valueOf(infos[0].trim()), Integer.valueOf(infos[1].trim()));
			}
		}
		return super.assemble();
	}

	public static ReleaseURTimesCfg123 getTimesCfgByVipLevel(int vipLevel) {
		return ConfigManager.getInstance().getConfigByKey(ReleaseURTimesCfg123.class, vipLevel);
	}

	/**
	 * 清理相关静态数据
	 */
	protected void clearStaticData() {
	}
}
