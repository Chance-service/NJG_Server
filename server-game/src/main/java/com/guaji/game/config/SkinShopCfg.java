package com.guaji.game.config;


import java.util.List;

import org.guaji.config.ConfigBase;
import org.guaji.config.ConfigManager;

import com.guaji.game.entity.DailyShopItem;
import com.guaji.game.item.ItemInfo;
import com.guaji.game.util.ItemUtil;

@ConfigManager.XmlResource(file = "xml/skinShop.xml", struct = "list")
public class SkinShopCfg extends ConfigBase {

	/**
	 * 商品编号
	 */
	private final int id;

	/**
	 * 商品信息
	 */
	private final String item;
	
	/**
	 * 可販賣數量
	 */
	private final int count;

	/**
	 * 折扣
	 */
	private final int discount;

	/**
	 * 价格1
	 */
	private final String price;

	
	
	public SkinShopCfg() {
		id= 0;
		discount = 0;
		item = null;
		count = 0;
		price = null;
	}

	@Override
	protected boolean assemble() {
		return super.assemble();
	}

	@Override
	protected boolean checkValid() {
		return super.checkValid();
	}

	public int getId() {
		return this.id;
	}

	public String getItem() {
		return this.item;
	}
	
	public int getCount() {
		return this.count;
	}

	public int getDiscount() {
		return this.discount;
	}
	
	public String getPrice() {
		return price;
	}

	/**
	 * 组装商品对象
	 * 
	 * @param cfg
	 * @param id
	 * @return
	 */
	public static DailyShopItem getDailyShopInfo(SkinShopCfg cfg, int id) {
		String[] es = cfg.item.split("_");
		ItemInfo info = ItemInfo.valueOf(cfg.price);// 返回折扣信息，使用itemInfo对象
		DailyShopItem shopItemInfo = new DailyShopItem();
		shopItemInfo.setId(id);
		shopItemInfo.setItemType(Integer.valueOf(es[0]));
		shopItemInfo.setItemId(Integer.valueOf(es[1]));
		shopItemInfo.setItemCount(Integer.valueOf(es[2]));
		shopItemInfo.setCostType(ItemUtil.getChangeType(info));// 货币类型
		shopItemInfo.setCostCount((int)info.getQuantity());// 钱数
		shopItemInfo.setInitCount(cfg.getCount()); // 初始販賣數量
		shopItemInfo.setLeft(cfg.getCount());
		shopItemInfo.setDiscount(cfg.getDiscount());
		shopItemInfo.setPriceStr(cfg.price);
		shopItemInfo.setCfgIndex(cfg.getId());
		return shopItemInfo;
	}

	/**
	 * 清理相关静态数据
	 */
	protected void clearStaticData() {
	}
	
	/**
	 * 取得所有商品
	 */
	public static List<SkinShopCfg> getShopCfgs(){
		List<SkinShopCfg> aList = ConfigManager.getInstance().getConfigList(SkinShopCfg.class);
		return aList ;
	}
}
