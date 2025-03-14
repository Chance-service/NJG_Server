package com.guaji.game.config;

import org.guaji.config.ConfigBase;
import org.guaji.config.ConfigManager;

import com.guaji.game.item.AwardItems;


@ConfigManager.XmlResource(file = "xml/BPHShop.xml", struct = "list")
public class BPHShopCfg extends ConfigBase {

	/**
	 * 数据配置表ID
	 */
	private final int id;
	
	/**
	 * 物品信息
	 */
	private final String item;
	
	/**
	 * 可购买次数
	 */
	private final int count;
	
	/**
	 * 没开启折扣活动的价格
	 */
	private final int price;
	
	/**
	 * 开启折扣活动后的价格
	 */
	private final int activityPrice;
	
	/**
	 * 物品数据转换
	 */
	private AwardItems awardItems;
	
	public BPHShopCfg () {
		
		id = 0;
		item = "";
		count = 0;
		price = 0;
		activityPrice = 0;
	}
	
	@Override
	protected boolean assemble() {

		if (this.item != null && this.item.length() > 0) {
			this.setAwardItems(AwardItems.valueOf(this.item));
		}
		return true;
	}
	
	@Override
	protected boolean checkValid() {
		return true;
	}
	
	public int getId() {
		return id;
	}
	
	public String getItem() {
		return item;
	}
	
	public int getCount() {
		return count;
	}
	
	public int getPrice() {
		return price;
	}
	
	public int getActivityPrice() {
		return activityPrice;
	}

	public AwardItems getAwardItems() {
		return awardItems;
	}

	public void setAwardItems(AwardItems awardItems) {
		this.awardItems = awardItems;
	}
	
}
