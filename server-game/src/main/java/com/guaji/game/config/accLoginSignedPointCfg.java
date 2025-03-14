package com.guaji.game.config;

import java.util.Map;

import org.guaji.config.ConfigBase;
import org.guaji.config.ConfigManager;

/**
 * @author 作者 E-mail:Jeremy@163.com
 * @version 创建时间：2019年1月24日 上午11:01:00 类说明
 */

@ConfigManager.XmlResource(file = "xml/accLoginSignedPoint.xml", struct = "map")
public class accLoginSignedPointCfg extends ConfigBase {

	/**
	 * 活跃点数
	 */
	@Id
	protected final int id;
	
	
	protected final int pointNumber;
	/**
	 * 月份
	 */
	private final int month;
	/**
	 * 任务奖励
	 */
	protected final String awards;

	public accLoginSignedPointCfg() {
		month = 0;
		pointNumber = 0;
		awards = "";
		id=0;
	}

	public int getPointNumber() {
		return pointNumber;
	}

	public String getAward() {
		return awards;
	}

	public int getMonth() {
		return month;
	}

	public int getId() {
		return id;
	}

	/**
	 * @param month 月份
	 * @param day   第几天
	 * @return
	 */
	public static accLoginSignedPointCfg getSignedPointCfg(int month, int pointNum) {

		Map<Object, accLoginSignedPointCfg> rewardCfgs = ConfigManager.getInstance()
				.getConfigMap(accLoginSignedPointCfg.class);

		for (accLoginSignedPointCfg itemCfg : rewardCfgs.values()) {
			if (itemCfg.getMonth() == month && pointNum == itemCfg.getPointNumber())
				return itemCfg;
		}
		return null;
	}

}
