package com.guaji.game.config;

import java.util.Collection;

import org.guaji.config.ConfigBase;
import org.guaji.config.ConfigManager;

@ConfigManager.XmlResource(file = "xml/levelGiftAward132.xml", struct = "map")
public class LevelGiftAward132Cfg extends ConfigBase {

	/**
	 * 配置id
	 */
	@Id
	private final int id;

	/**
	 * 领奖所需最小等级
	 */
	private final int minLevel;

	/**
	 * 领奖所需最大等级
	 */
	private final int maxLevel;

	/**
	 * 消耗钻石数
	 */
	private final int cost;

	/**
	 * 奖励
	 */
	private final String awards;
	/**
	 * 限時時間(小時)
	 */
	private final int hours;
	


	public LevelGiftAward132Cfg() {
		this.id = 0;
		this.minLevel = 0;
		this.maxLevel = 0;
		this.cost = 0;
		this.awards = "";
		this.hours = 0 ;
	}

	public int getId() {
		return id;
	}

	public int getMinLevel() {
		return minLevel;
	}

	public int getMaxLevel() {
		return maxLevel;
	}

	public int getCost() {
		return cost;
	}

	public String getAwards() {
		return awards;
	}
	
	public int getHours() {
		return hours;
	}

	/**
	 * 数据格式转化
	 */
	@Override
	protected boolean assemble() {
		return true;
	}

	/**
	 * 检测有消息
	 * 
	 * @return
	 */
	@Override
	protected boolean checkValid() {
		return true;
	}

	/**
	 * 清理相关静态数据
	 */
	protected void clearStaticData() {
	}
	
	/**
	 * 利用儲值禮包ID抓購買資料
	 * @param goodsId
	 * @return
	 */
	
	public static  LevelGiftAward132Cfg getCfgByGoodsId(int goodsId) {
		Collection<LevelGiftAward132Cfg> Cfgs = ConfigManager.getInstance().getConfigMap(LevelGiftAward132Cfg.class).values();
		for(LevelGiftAward132Cfg afg : Cfgs) {
			if (afg.getCost() == goodsId) {
				return afg;
			}
		}
		return null;
	}
}
