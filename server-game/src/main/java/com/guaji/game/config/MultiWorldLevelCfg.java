package com.guaji.game.config;

import java.util.Date;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeMap;

import org.guaji.config.ConfigBase;
import org.guaji.config.ConfigManager;
import org.guaji.os.GuaJiTime;

import com.guaji.game.GsConfig;

@ConfigManager.XmlResource(file = "xml/multiWorldLevel.xml", struct = "map")
public class MultiWorldLevelCfg extends ConfigBase {

	/**
	 * 最大天
	 */
	private final static int MAX_DAY = 2000;

	@Id
	private final int id;

	// 开服天数
	private final int days;

	// 对应天数集合
	private static Map<Integer, Integer> daysMap;

	public MultiWorldLevelCfg() {
		this.id = 0;
		this.days = 0;
		daysMap = new TreeMap<Integer, Integer>();
	}

	public int getId() {
		return id;
	}

	public int getDays() {
		return days;
	}

	/**
	 * 获取副本级别
	 * 
	 * @return
	 */
	public static int getServerMultiLevel() {
		Date openDate = GsConfig.getInstance().getServerOpenDate();
		Date curDate = new Date();
		int day = GuaJiTime.calcBetweenDays(openDate, curDate);
		Set<Entry<Integer, Integer>> set = daysMap.entrySet();
		Iterator<Entry<Integer, Integer>> it = set.iterator();
		while (it.hasNext()) {
			Entry<Integer, Integer> value = it.next();
			if (day <= value.getValue()) {
				return value.getKey();
			}
		}
		return MAX_DAY;
	}

	@Override
	protected boolean assemble() {
		daysMap.put(id, days);
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
}
