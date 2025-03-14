package com.guaji.game.config;

import java.text.ParseException;
import java.text.SimpleDateFormat;

import org.guaji.config.ConfigBase;
import org.guaji.config.ConfigManager;
import org.guaji.os.MyException;

@ConfigManager.XmlResource(file = "xml/newWeekCard.xml", struct = "map")
public class NewWeekCardCfg extends ConfigBase {

	@Id
	private final int id;
	/**
	 * 持续天数
	 */
	protected final int days;

	/**
	 * 奖励信息
	 */
	protected final String reward;

	/**
	 * 消耗人民币
	 */
	private final int costMoney;

	/**
	 * 商品Id
	 */
	private final int goodsId;

	/**
	 * 开始出售时间
	 */
	private final String startTime;
	
	/**
	 * 结束出售时间
	 */
	private final String endTime;
	
	private long lStarTime;
	
	private long lEndTime;
	/**
	 * 经验加成BUFF
	 */
	private final float addExpBuff;

	public static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd_HH:mm:ss");

	public NewWeekCardCfg() {
		this.id = 0;
		this.days = 0;
		this.costMoney = 0;
		this.goodsId = 0;
		this.reward = null;
		this.addExpBuff = 0;
		this.startTime = "";
		this.endTime = "";
	}

	public int getDays() {
		return days;
	}

	public String getReward() {
		return reward;
	}

	public int getId() {
		return id;
	}

	public int getGoodsId() {
		return goodsId;
	}

	public int getCostMoney() {
		return costMoney;
	}

	public float getAddExpBuff() {
		return addExpBuff;
	}
	
	public long getStartTime()
	{
		return this.lStarTime;
	}
	
	public long getEndTime()
	{
		return this.lEndTime;
	}

	/**
	 * 根据商品编号获得配置
	 * 
	 * @return
	 */
	public static NewWeekCardCfg getNewWeekCardCfgByGoodsId(int goodsId) {
		for (NewWeekCardCfg newWeekCardCfg : ConfigManager.getInstance().getConfigMap(NewWeekCardCfg.class).values()) {
			if (newWeekCardCfg.getGoodsId() == goodsId) {
				return newWeekCardCfg;
			}
		}
		return null;
	}

	@Override
	protected boolean assemble() {
		try
		{
			this.lStarTime = DATE_FORMAT.parse(this.startTime).getTime();
			this.lEndTime = DATE_FORMAT.parse(this.endTime).getTime();
		}
		catch (ParseException e) {
			MyException.catchException(e);
		}
		return true;
	}
	/**
	 * 清理相关静态数据
	 */
	protected void clearStaticData() {
	}

}

