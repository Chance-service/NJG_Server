package com.guaji.game.config;

import org.guaji.config.ConfigBase;
import org.guaji.config.ConfigManager;
import org.guaji.os.GuaJiRand;
import org.guaji.os.MyException;

import com.guaji.game.protocol.Const;

@ConfigManager.XmlResource(file = "xml/shopCfg.xml", struct = "list")
public class ShopCfg extends ConfigBase {
	/**
	 * 最大幸运值
	 */
	private static int maxLuckyValue = 0;

	/**
	 * 最小等级
	 */
	protected final int minLevel;

	/**
	 * 最大等级
	 */
	protected final int maxLevel;

	/**
	 * 幸运值
	 */
	protected final String luckyRange;

	/**
	 * 基础商品列表
	 */
	protected final int baseItems;

	/**
	 * 战士商品列表
	 */
	protected final int warriorItems;

	/**
	 * 猎人商品列表
	 */
	protected final int hunterItems;

	/**
	 * 法师商品列表
	 */
	protected final int magicItems;

	/**
	 * 基本物品list和职业list的权重
	 */
	protected final String listRate;

	/**
	 * 所需最小幸运值
	 */
	protected int minNeedLuckyValue;

	/**
	 * 所需最大幸运值
	 */
	protected int maxNeedLuckyValue;

	/**
	 * 基本物品表权重
	 */
	protected int baseRate;

	/**
	 * 种族物品表权重
	 */
	protected int raceRate;

	public ShopCfg() {
		minLevel = 0;
		maxLevel = 0;
		luckyRange = null;
		baseItems = 0;
		warriorItems = 0;
		hunterItems = 0;
		magicItems = 0;
		minNeedLuckyValue = 0;
		maxNeedLuckyValue = 0;
		listRate = null;
		baseRate = 0;
		raceRate = 0;
	}

	public int getMinLevel() {
		return minLevel;
	}

	public int getMaxLevel() {
		return maxLevel;
	}

	public String getLuckyRange() {
		return luckyRange;
	}

	public int getBaseItems() {
		return baseItems;
	}

	public int getWarriorItems() {
		return warriorItems;
	}

	public int getHunterItems() {
		return hunterItems;
	}

	public int getMagicItems() {
		return magicItems;
	}

	public int getMinNeedLuckyValue() {
		return minNeedLuckyValue;
	}

	public int getMaxNeedLuckyValue() {
		return maxNeedLuckyValue;
	}

	public static int getMaxLuckValue() {
		return maxLuckyValue;
	}

	public int getBaseRate() {
		return baseRate;
	}

	public int getRaceRate() {
		return raceRate;
	}

	@Override
	protected boolean assemble() {
		assembaleLuckRange();// 解析幸运值
		assembaleListRate();// 解析基本和宗族物品表权重
		return true;
	}

	/**
	 * 解析幸运值
	 */
	private void assembaleLuckRange() {
		if (luckyRange != null && luckyRange.length() > 0) {
			String[] items = luckyRange.split(",");
			if (items.length != 2) {
				return;
			}
			minNeedLuckyValue = Integer.valueOf(items[0]);
			maxNeedLuckyValue = Integer.valueOf(items[1]);

			if (maxLuckyValue < maxNeedLuckyValue) {
				maxLuckyValue = maxNeedLuckyValue;
			}
		}
	}

	/**
	 * 解析权重
	 */
	private void assembaleListRate() {
		if (listRate != null && listRate.length() > 0) {
			String[] items = listRate.split(",");
			if (items.length != 2) {
				return;
			}
			baseRate = Integer.valueOf(items[0]);
			raceRate = Integer.valueOf(items[1]);
		}
	}

	/**
	 * 根据基本和宗族权重取得物品表id
	 * 
	 * @return
	 */
	public int getShopTtemListIdByRate(int prof) {
		try {
			int sumRate = getBaseRate() + getRaceRate();
			int rate = GuaJiRand.randInt(1, sumRate);
			if (rate <= getBaseRate()) {
				return getBaseItems();
			}
			
			if (prof == Const.prof.WARRIOR_VALUE) {
				return getWarriorItems();
			} else if (prof == Const.prof.ARCHER_VALUE) {
				return getHunterItems();
			} else if (prof == Const.prof.MAGICIAN_VALUE) {
				return getMagicItems();
			}
		} catch (Exception e) {
			MyException.catchException(e);
		}
		return -1;
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
