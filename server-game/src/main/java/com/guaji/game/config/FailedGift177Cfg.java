package com.guaji.game.config;

import java.util.Collection;

import org.guaji.config.ConfigBase;
import org.guaji.config.ConfigManager;

@ConfigManager.XmlResource(file = "xml/FailedGiftAward177.xml", struct = "map")
public class FailedGift177Cfg extends ConfigBase {

	/**
	 * 配置id
	 */
	@Id
	private final int id;

	/**
	 * 领奖所需最小關卡
	 */
	private final int minstage;

	/**
	 * 领奖所需最大關卡
	 */
	private final int maxstage;

	/**
	 * goodsId
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
	


	public FailedGift177Cfg() {
		this.id = 0;
		this.minstage = 0;
		this.maxstage = 0;
		this.cost = 0;
		this.awards = "";
		this.hours = 0 ;
	}

	public int getId() {
		return id;
	}

	public int getMinstage() {
		return minstage;
	}

	public int getMaxstage() {
		return maxstage;
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
	
	public static  FailedGift177Cfg getCfgByGoodsId(int goodsId) {
		Collection<FailedGift177Cfg> Cfgs = ConfigManager.getInstance().getConfigMap(FailedGift177Cfg.class).values();
		for(FailedGift177Cfg afg : Cfgs) {
			if (afg.getCost() == goodsId) {
				return afg;
			}
		}
		return null;
	}
	
	public static  FailedGift177Cfg getCfgByCurMapId(int mapId) {
		Collection<FailedGift177Cfg> Cfgs = ConfigManager.getInstance().getConfigMap(FailedGift177Cfg.class).values();
		for(FailedGift177Cfg afg : Cfgs) {
			if ((mapId >=afg.getMinstage()) && (mapId <= afg.getMaxstage())) {
				return afg;
			}
		}
		return null;
	}
}
