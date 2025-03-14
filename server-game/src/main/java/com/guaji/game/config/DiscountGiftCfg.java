package com.guaji.game.config;

import java.text.ParseException;
import java.text.SimpleDateFormat;

import org.guaji.config.ConfigBase;
import org.guaji.config.ConfigManager;
import org.guaji.os.GuaJiTime;
import org.guaji.os.MyException;

@ConfigManager.XmlResource(file = "xml/discountGift.xml", struct = "map")
public class DiscountGiftCfg extends ConfigBase {
	/**
	 * 配置id
	 */
	@Id
	protected final int id;
	/**
	 * 获得奖励
	 */
	protected final String salegift;
	/**
	 * 1为每日刷新，2为每周刷新，3为不刷新(仅能购买限定次数), 4為每月刷新
	 */
	protected final int LimitType;
	/**
	 * 获得奖励
	 */
	protected final int LimitNum;
	/**
	 * 可购买的最小等级
	 */
	protected final int MinLevel;
	/**
	 * 可购买的最大等级
	 */
	protected final int MaxLevel;
	/**
	 * 结束时间
	 */
	protected long lEndTime = 0l;

	/**
	 * 结束时间
	 */
	protected final String endTime;
	/**
	 * 结束时间
	 */
	protected final int activityid;
	/**
	 * 是否免費領取
	 */
	protected final int Free;

	public static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd_HH:mm:ss");

	public DiscountGiftCfg() {
		id = 0;
		salegift = "";
		LimitType = 0;
		LimitNum = 0;
		MinLevel = 0;
		MaxLevel = 0;
		endTime = "";
		activityid = 0;
		Free = 0 ;
	}

	public int getId() {
		return id;
	}

	public int getFree() {
		return Free;
	}

	public String getSalegift() {
		return salegift;
	}

	public int getLimitType() {
		return LimitType;
	}

	public int getLimitNum() {
		return LimitNum;
	}

	public int getMinLevel() {
		return MinLevel;
	}

	public int getMaxLevel() {
		return MaxLevel;
	}
	
	public int getActivityId() {
		return activityid;
	}

	@Override
	protected boolean assemble() {

		try {
			this.lEndTime = DATE_FORMAT.parse(this.endTime).getTime();
		} catch (ParseException e) {
			MyException.catchException(e);
		}

		return true;
	}

	@Override
	protected boolean checkValid() {
		return true;
	}

	public int getCountdownTime() {
		long currentTime = GuaJiTime.getMillisecond();
		int surplusTime = (int) ((this.lEndTime - currentTime) / 1000);
		return Math.max(surplusTime, 0);
	}
}
