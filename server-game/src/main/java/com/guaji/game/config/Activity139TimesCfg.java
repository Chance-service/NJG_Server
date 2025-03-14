package com.guaji.game.config;

import java.util.List;

import org.guaji.config.ConfigBase;
import org.guaji.config.ConfigManager;

import com.guaji.game.item.ItemInfo;

/**
 * 夺宝奇兵每日次数与vip等级挂钩
 * 
 * @author rudy
 */
@ConfigManager.XmlResource(file = "xml/Activity139Times.xml", struct = "map")
public class Activity139TimesCfg extends ConfigBase {
	/**
	 * vip等级
	 */
	@Id
	private final int vipLevel;
	/**
	 * 免费时间CD
	 */
	private final int freeCD;
	/***
	 * 单次抽奖花费元宝
	 */
	private final String singleCost;
	/***
	 * 十次抽奖花费元宝
	 */
	private final String tenCost;



	/**
	 * 单次消耗
	 */
	private List<ItemInfo> singleCostItems;

	/**
	 * 每十次消耗
	 */
	private List<ItemInfo> tenCostItems;

	public Activity139TimesCfg() {
		vipLevel = 0;
		freeCD = 0;
		singleCost = null;
		tenCost = null;


	}

	@Override
	protected boolean assemble() {

		if (this.singleCost != null && this.singleCost.length() > 0 && !"0".equals(this.singleCost)) {
			this.singleCostItems = ItemInfo.valueListOf(this.singleCost);
		}
		if (this.tenCost != null && this.tenCost.length() > 0 && !"0".equals(this.tenCost)) {
			this.tenCostItems = ItemInfo.valueListOf(this.tenCost);
		}
		return super.assemble();
	}

	public int getVipLevel() {
		return vipLevel;
	}

	public int getFreeCountDown() {
		return freeCD;
	}

	

	public String getSingleCost() {
		return singleCost;
	}


	public String getTenCost() {
		return tenCost;
	}

	public List<ItemInfo> getSingleCostItems() {
		return singleCostItems;
	}

	public List<ItemInfo> getTenCostItems() {
		return tenCostItems;
	}

	public static Activity139TimesCfg getTimesCfgByVipLevel(int vipLevel) {
		return ConfigManager.getInstance().getConfigByKey(Activity139TimesCfg.class, vipLevel);
	}

	/**
	 * 清理相关静态数据
	 */
	protected void clearStaticData() {
	}
}
