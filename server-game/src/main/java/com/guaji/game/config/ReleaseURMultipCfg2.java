package com.guaji.game.config;

import java.util.TreeMap;

import org.guaji.config.ConfigBase;
import org.guaji.config.ConfigManager;
import org.guaji.os.GuaJiRand;
import org.guaji.os.MyException;

/**
 * 新神将投放掉落组配表
 */
@ConfigManager.XmlResource(file = "xml/releaseURMultip2.xml", struct = "map")
public class ReleaseURMultipCfg2 extends ConfigBase {
	/**
	 * 掉落物组ID
	 */
	@Id
	private final int id;

	private final int startlucky;

	private final int endlucky;

	private final int rate;

	private static int totalRate;

	/**
	 * 奖池掉落物权重分布 Map
	 */
	private static TreeMap<Integer, ReleaseURMultipCfg2> itemsMap = new TreeMap<Integer, ReleaseURMultipCfg2>();

	public ReleaseURMultipCfg2() {
		id = 0;
		startlucky = 0;
		endlucky = 0;
		rate = 0;
	}

	public int getId() {
		return id;
	}

	public int getStartLucky() {
		return startlucky;
	}

	public int getEndLucky() {
		return endlucky;
	}

	public int getRate() {
		return rate;
	}

	/**
	 * 清理相关静态数据
	 */
	protected void clearStaticData() {
		totalRate = 0;
	}

	@Override
	protected boolean assemble() {
		// 总概率
		totalRate = 10000;
		// 奖池掉落权重
		itemsMap.put(this.endlucky, this);
		return true;
	}

	public static int getItemIdByLucky(int luckyValue) {
		if (itemsMap.size() == 0) {
			return 0;
		}
		Integer key = itemsMap.ceilingKey(luckyValue);
		if (key == null) {
			key = itemsMap.firstKey();
			return itemsMap.get(key).getId();
		}
		return itemsMap.get(key).getId();
	}

	public static boolean isActiveMultiple(int luckyValue) {
		int id = getItemIdByLucky(luckyValue);
		try {
			ReleaseURMultipCfg2 cfg = ConfigManager.getInstance().getConfigByKey(ReleaseURMultipCfg2.class, id);
			if (cfg == null) {
				return false;
			}
			int rand = GuaJiRand.randInt(1, totalRate);
			if (rand < cfg.rate) {
				return true;
			}
			return false;
		} catch (MyException e) {
			// TODO: handle exception
			MyException.catchException(e);
		}
		return false;
	}
}
