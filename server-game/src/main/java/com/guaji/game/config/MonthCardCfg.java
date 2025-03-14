package com.guaji.game.config;

import org.guaji.config.ConfigBase;
import org.guaji.config.ConfigManager;

@ConfigManager.XmlResource(file = "xml/monthcard.xml", struct = "map")
public class MonthCardCfg extends ConfigBase {

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
	 * 免费快速战斗次数
	 */
	private final int freeFastFightTimes;

	/**
	 * 免费商店刷新次数
	 */
	private final int freeRefreshShopTimes;

	/**
	 * 免费刷新打造装备次数
	 */
	private final int freeRefreshMakeEquipTimes;

	/**
	 * 经验加成BUFF
	 */
	private final float addExpBuff;

	public MonthCardCfg() {
		this.id = 0;
		this.days = 0;
		this.costMoney = 0;
		this.goodsId = 0;
		this.reward = null;
		this.freeFastFightTimes = 0;
		this.freeRefreshShopTimes = 0;
		this.freeRefreshMakeEquipTimes = 0;
		this.addExpBuff = 0;
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

	public int getFreeFastFightTimes() {
		return freeFastFightTimes;
	}

	public int getFreeRefreshShopTimes() {
		return freeRefreshShopTimes;
	}

	public int getFreeRefreshMakeEquipTimes() {
		return freeRefreshMakeEquipTimes;
	}

	public float getAddExpBuff() {
		return addExpBuff;
	}

	/**
	 * 根据商品编号获得配置
	 * 
	 * @return
	 */
	public static MonthCardCfg getMonthCardCfgByGoodsId(int goodsId) {
		for (MonthCardCfg monthCardCfg : ConfigManager.getInstance().getConfigMap(MonthCardCfg.class).values()) {
			if (monthCardCfg.getGoodsId() == goodsId) {
				return monthCardCfg;
			}
		}
		return null;
	}

	/**
	 * 根据金钱获得月卡配置
	 * 
	 * @param costMoney
	 * @return
	 */
	public static MonthCardCfg getMonthCardCfgByMoney(float costMoney) {
		for (MonthCardCfg monthCardCfg : ConfigManager.getInstance().getConfigMap(MonthCardCfg.class).values()) {
			if (monthCardCfg.getCostMoney() == costMoney) {
				return monthCardCfg;
			}
		}
		return null;
	}

	/**
	 * 清理相关静态数据
	 */
	protected void clearStaticData() {
	}

}
