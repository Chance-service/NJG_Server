package com.guaji.game.config;

import java.util.Map;
import java.util.TreeMap;

import org.guaji.config.ConfigBase;
import org.guaji.config.ConfigManager;
import org.guaji.os.GuaJiRand;
import org.guaji.os.MyException;

/**
 * 新神将投放权重
 * 
 * @author Melvin.Mao
 * @date Nov 24, 2017 2:35:01 PM
 */
@ConfigManager.XmlResource(file = "xml/releaseURBuff.xml", struct = "map")
public class ReleaseURMultipBuffCfg extends ConfigBase {
	/**
	 * 掉落物组ID
	 */
	@Id
	private final int id;

	private final int multiple;

	private final int overtime;

	private final int rate;

	private static int totalRate;

	/**
	 * 奖池掉落物权重分布 Map
	 */
	private static TreeMap<Integer, Integer> itemsMap = new TreeMap<Integer, Integer>();

	public ReleaseURMultipBuffCfg() {
		id = 0;
		multiple = 1;
		overtime = 0;
		rate = 0;
	}

	public int getId() {
		return id;
	}

	public int getMultiple() {
		return multiple;
	}

	public int getOverTime() {
		return overtime;
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
		totalRate += this.rate;
		// 奖池掉落权重
		itemsMap.put(this.id, this.rate);
		return true;
	}

	public static int randomItemId() {
		if (totalRate == 0) {
			return 0;
		}
		try {
			int rand = GuaJiRand.randInt(1, totalRate);
			int acc = 0;
			for (Map.Entry<Integer, Integer> entry : itemsMap.entrySet()) {
				acc += entry.getValue();
				if (rand <= acc) {
					return entry.getKey();
				}
			}
		} catch (Exception e) {
			// TODO: handle exception
			MyException.catchException(e);
		}
		return 0;
	}
}
