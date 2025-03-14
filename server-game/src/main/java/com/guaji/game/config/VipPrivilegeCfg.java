package com.guaji.game.config;

import org.guaji.config.ConfigBase;
import org.guaji.config.ConfigManager;

import com.guaji.game.item.AwardItems;

@ConfigManager.XmlResource(file = "xml/vipPrivilege.xml", struct = "map")
public class VipPrivilegeCfg extends ConfigBase {
	/**
	 * 最大等级
	 */
	private static int maxVipLevel = 0;

	/**
	 * 等级
	 */
	@Id
	protected final int vipLevel;

	/**
	 * 所需充值钻石
	 */
	protected final int needRechargeGold;

	/**
	 * 所需充值货币
	 */
	protected final int needPayMoney;

	/**
	 * 神秘商店显示商品数
	 */
	protected final int shopItemCount;

	/**
	 * 购买金币数目
	 */
	protected final int buyCoinTimes;

	/**
	 * 快速战斗次数
	 */
	protected final int fastFightTimes;

	/**
	 * 可购买快速战斗次数
	 */
	protected final int buyFastFightTimes;

	/**
	 * boss战免费次数
	 */
	protected final int bossBattleTimes;

	/**
	 * 可购买BOSS挑战次数
	 */
	protected final int buyBossBattleTimes;

	/**
	 * 击杀boss的额外奖励
	 */
	protected final String killBossExtAward;

	/**
	 * 最高佣兵培养
	 */
	protected final int mercenaryMaxFoster;

	/**
	 * 精英副本次数
	 */
	private final int eliteMapTimes;

	/**
	 * 购买精英副本次数
	 */
	private final int buyEliteMapTimes;

	/**
	 * 佣兵魂石数量
	 */
	private final int starStoneTimes;

	/**
	 * 多人副本购买次数
	 */
	private final int multiEliteBuyTimes;

	/**
	 * 远征派遣最大次数
	 */
	private final int expeditionTaskCount;

	/**
	 * 新宝石购买最大数量限制<该字段暂时不用.>
	 */
	private final int gemShopLimitBuy;

	/**
	 * 可购买跨服战竞技次数
	 */
	private final int crossBattleTimes;

	/**
	 * 可签到次数
	 */
	private final int complementsign;

	/**
	 * 可掛機時長
	 */
	private final int IdleTimes;

	/**
	 * 掛機收益倍率
	 */
	private final double IdleRatio;

	/**
	 * 可派遣次数
	 */
	private final int BountyTimes;

	/**
	 * boss击杀的额外奖励
	 */
	private AwardItems bossExtAward;
	/**
	 * 增加秘密信條最大體力
	 */
	private final int MaxPower;
	/**
	 * 增加秘密信條回體力直
	 */
	private final int RecoverPower;

	public VipPrivilegeCfg() {
		vipLevel = 0;
		needRechargeGold = 0;
		needPayMoney = 0;
		shopItemCount = 0;
		buyCoinTimes = 0;
		fastFightTimes = 0;
		buyFastFightTimes = 0;
		bossBattleTimes = 0;
		buyBossBattleTimes = 0;
		killBossExtAward = null;
		mercenaryMaxFoster = 0;
		eliteMapTimes = 0;
		buyEliteMapTimes = 0;
		starStoneTimes = 0;
		multiEliteBuyTimes = 0;
		expeditionTaskCount = 0;
		gemShopLimitBuy = 0;
		crossBattleTimes = 0;
		complementsign = 0;
		IdleTimes = 0;
		IdleRatio = 0.0;
		BountyTimes = 0;
		MaxPower = 0;
		RecoverPower = 0;
	}

	/**
	 * 获取配置的最大等级
	 * 
	 * @return
	 */
	public static int getMaxVipLevel() {
		return maxVipLevel;
	}

	public int getVipLevel() {
		return vipLevel;
	}

	public int getNeedRechargeGold() {
		return needRechargeGold;
	}

	public int getNeedPayMoney() {
		return needPayMoney;
	}

	public int getShopItemCount() {
		return shopItemCount;
	}

	public int getBuyCoinTimes() {
		return buyCoinTimes;
	}

	public int getFastFightTimes() {
		return fastFightTimes;
	}

	public int getBuyFastFightTimes() {
		return buyFastFightTimes;
	}

	public int getBossBattleTimes() {
		return bossBattleTimes;
	}

	public int getBuyBossBattleTimes() {
		return buyBossBattleTimes;
	}

	public String getKillBossExtAward() {
		return killBossExtAward;
	}

	public int getMercenaryMaxFoster() {
		return mercenaryMaxFoster;
	}

	public int getEliteMapTimes() {
		return eliteMapTimes;
	}

	public int getBuyEliteMapTimes() {
		return buyEliteMapTimes;
	}

	public int getStarStoneTimes() {
		return starStoneTimes;
	}

	public int getMultiEliteBuyTimes() {
		return multiEliteBuyTimes;
	}

	public int getExpeditionTaskCount() {
		return expeditionTaskCount;
	}

	public int getGemShopLimitBuy() {
		return gemShopLimitBuy;
	}

	public int getCrossBattleTimes() {
		return crossBattleTimes;
	}

	public AwardItems getBossExtAward() {
		return bossExtAward;
	}

	public int getComplementsign() {
		return complementsign;
	}

	public int getIdleTimes() {
		return IdleTimes;
	}

	public double getIdleRatio() {
		return IdleRatio;
	}

	public int getBountyTimes() {
		return BountyTimes;
	}

	public int getMaxPower() {
		return MaxPower;
	}

	public int getRecoverPower() {
		return RecoverPower;
	}

	@Override
	protected boolean assemble() {
		if (maxVipLevel < vipLevel) {
			maxVipLevel = vipLevel;
		}
		if (killBossExtAward != null && killBossExtAward.length() > 0) {
			bossExtAward = AwardItems.valueOf(killBossExtAward);
		}
		return true;
	}

	@Override
	protected boolean checkValid() {
		return true;
	}
}
