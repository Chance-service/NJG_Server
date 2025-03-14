package com.guaji.game.config;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.guaji.config.ConfigBase;
import org.guaji.config.ConfigManager;

@ConfigManager.XmlResource(file = "xml/haremConst.xml", struct = "map")
public class HaremConstCfg extends ConfigBase {

	@Id
	protected final int id;

	/**
	 * 奖池类型
	 */
	protected final int poolType;

	/**
	 * 免费次数
	 */
	protected final int freeChance;

	/**
	 * CD时间
	 */
	protected final int freeCd;

	/**
	 * 道具消耗
	 */
	protected final String itemCost;

	/**
	 * 一次钻石消耗
	 */
	protected final int oneTimeCost;

	/**
	 * 十次钻石消耗
	 */
	protected final int tenTimesCost;

	/**
	 * 增加的积分
	 */
	protected final int addScore;
	
	/**
	 * 每天最多抽取次数
	 */
	protected final int maxDayTotalTimes;
	

	/**
	 * 开始时间
	 */
	protected final String startTime;

	/**
	 * 结束时间
	 */
	protected final String endTime;
	
	/**
	 * 开始时间
	 */
	private Date startDate;

	/**
	 * 结束时间
	 */
	private Date endDate;


	public HaremConstCfg() {
		this.id = 0;
		this.poolType = 0;
		this.freeCd = 0;
		this.freeChance = 0;
		this.oneTimeCost = 0;
		this.tenTimesCost = 0;
		this.addScore = 0;
		this.itemCost = null;
		this.startTime = null;
		this.endTime = null;
		this.maxDayTotalTimes=0;
	}

	@Override
	protected boolean assemble() {
		return super.assemble();
	}

	public int getId() {
		return id;
	}

	public int getPoolType() {
		return poolType;
	}

	public int getFreeChance() {
		return freeChance;
	}

	public int getFreeCd() {
		return freeCd;
	}

	public String getItemCost() {
		return itemCost;
	}

	public int getOneTimeCost() {
		return oneTimeCost;
	}

	public int getTenTimesCost() {
		return tenTimesCost;
	}

	public int getAddScore() {
		return addScore;
	}

	public String getStartTime() {
		return startTime;
	}

	public String getEndTime() {
		return endTime;
	}

	
	public int getMaxDayTotalTimes() {
		return maxDayTotalTimes;
	}

	/**
	 * 开始时间
	 * 
	 * @return
	 */
	public Date getStartDate() {
		SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd_HH:mm:ss");
		try {
			startDate = DATE_FORMAT.parse(this.startTime);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return startDate;
	}

	/**
	 * 结束时间
	 * 
	 * @return
	 */
	public Date getEndDate() {
		SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd_HH:mm:ss");
		try {
			endDate = DATE_FORMAT.parse(this.endTime);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return endDate;
	}

}
