package com.guaji.game.config;

import org.guaji.config.ConfigBase;
import org.guaji.config.ConfigManager;

import com.guaji.game.item.ItemInfo;

@ConfigManager.XmlResource(file = "xml/gemshop.xml", struct = "map")
public class GemShopCfg extends ConfigBase  {
	// 宝石商店Id
	@Id
	private final int shopId;
	//宝石Id
	private final int gemId;
	// 钻石购买
	private final int goldPrice;
	// 宝石卷购买
	private final String gemVolumePrice;
	//vip可见宝石
	private final int VIPVisible;
	//购买类型 0 不容许购买 1只能钻石 2只能宝石券 3一可以宝石券也可以钻石
	private final int gemVolumeBuy;

	private final String coinCost;

	/**
	 * 消耗的材料
	 */
	private ItemInfo consumeMaterialInfo;

	private ItemInfo consumeCoinInfo;

	public GemShopCfg() {
		shopId = 0;
		gemId = 0;
		goldPrice = 0;
		gemVolumePrice= null;
		VIPVisible = 0;
		gemVolumeBuy = 0;
		coinCost=null;
	}

	public int getShopId() {
		return shopId;
	}

	public int getGemId() {
		return gemId;
	}

	public int getGoldPrice() {
		return goldPrice;
	}


	public String getGemVolumePrice() {
		return gemVolumePrice;
	}

	@Override
	protected boolean assemble() {
		if (this.gemVolumePrice != null && this.gemVolumePrice.length() > 0 ) {
			this.consumeMaterialInfo = new ItemInfo(gemVolumePrice);
		}
		if (this.coinCost!=null&&this.coinCost.length()>0){
		    this.consumeCoinInfo = new ItemInfo(coinCost);
        }
		return true;
	}

	public ItemInfo getConsumeMaterialInfo() {
		return consumeMaterialInfo;
	}

	public void setConsumeMaterialInfo(ItemInfo consumeMaterialInfo) {
		this.consumeMaterialInfo = consumeMaterialInfo;
	}

	public int getVIPVisible() {
		return VIPVisible;
	}

	public int getGemVolumeBuy() {
		return gemVolumeBuy;
	}

    public String getCoinCost() {
        return coinCost;
    }

    public ItemInfo getConsumeCoinInfo() {
        return consumeCoinInfo;
    }

    public void setConsumeCoinInfo(ItemInfo consumeCoinInfo) {
        this.consumeCoinInfo = consumeCoinInfo;
    }

    /**
	 * 清理相关静态数据
	 */
	protected void clearStaticData() {
	}
}
