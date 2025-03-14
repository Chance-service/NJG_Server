package com.guaji.game.config;

import java.util.Date;

import org.guaji.config.ConfigBase;
import org.guaji.config.ConfigManager;

import com.guaji.game.player.PlayerData;

@ConfigManager.XmlResource(file = "xml/rechargeRatio.xml", struct = "map")
public class RechargeRatioCfg extends ConfigBase {

	/**
	 * 商品标号
	 */
	@Id
	private final int goodsId;
	/**
	 * 倍数
	 */
	private final int ratio;

	/**
	 * 在指定day天之内给的奖励倍数
	 */
	private final int ratio1;

	/**
	 * 指定多少天之内
	 */
	private final int day;

	public RechargeRatioCfg() {
		this.goodsId = 0;
		this.ratio = 0;
		this.ratio1 = 0;
		this.day = 0;
	}

	public int getGoodsId() {
		return goodsId;
	}

	public int getRatio(PlayerData playerData) {
		// Date registerDate = playerData.getPlayerEntity().getCreateTime();
		// int days = ActivityUtil.getRegisterCycleActivityId(registerDate);
		// if (days <= day) {
		// return ratio1;
		// }
		long time = day * 24 * 60 * 60 * 1000;// 间隔的毫秒数
		long tempTime = getTempTime(playerData);
		int faultTolerantTime = 10 * 1000;// 容错时间是10秒
		if (tempTime > (time + faultTolerantTime)) {
			return ratio;
		}
		return ratio1;
	}

	public long getCountDownTime(PlayerData playerData) {
		long time = day * 24 * 60 * 60 * 1000;// 间隔的毫秒数
		long tempTime = getTempTime(playerData);
		return time - tempTime;
	}

	/**
	 * 是否还在倒计时
	 * 
	 * @param playerData
	 * @return
	 */
	public boolean isCountDown(PlayerData playerData) {
		long time = day * 24 * 60 * 60 * 1000;// 间隔的毫秒数
		long tempTime = getTempTime(playerData);
		if (tempTime > time) {
			return false;
		}
		return true;
	}

	private long getTempTime(PlayerData playerData) {
		Date registerDate = playerData.getPlayerEntity().getCreateTime();
		long registerTime = registerDate.getTime();
		long currentTime = System.currentTimeMillis();
		return currentTime - registerTime;
	}

	public int getRatio1() {
		return ratio1;
	}

	public int getDay() {
		return day;
	}

	/**
	 * 清理相关静态数据
	 */
	protected void clearStaticData() {
	}
}
