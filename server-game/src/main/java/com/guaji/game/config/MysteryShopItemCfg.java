package com.guaji.game.config;

import java.util.LinkedList;
import java.util.List;

import org.guaji.config.ConfigBase;
import org.guaji.config.ConfigManager;
import org.guaji.os.GuaJiRand;
import org.guaji.os.MyException;

@ConfigManager.XmlResource(file = "xml/mysteryShopItemCfg.xml", struct = "list")
public class MysteryShopItemCfg extends ConfigBase {

	/**
	 * 列表ID
	 */
	@Id
	protected final int listId;

	/**
	 * 商品前缀(10000,20000,30000,40000,50000)
	 */
	protected final String typeId;

	/**
	 * 商品id
	 */
	protected final int productId;

	/**
	 * 商品数量
	 */
	protected final int productCount;

	/**
	 * 贩卖钻石价格
	 */
	protected final int productPrice;

	/**
	 * 折扣(int_int)
	 */
	protected final String gemDiscount;

	/**
	 * 权重
	 */
	protected final int weight;

	/**
	 * 刷新出现概率
	 */
	protected final int refreshCoinRate;

	/**
	 * 物品属性（0:只能用于贩卖1：只能用于竞拍 2:贩卖竞拍皆可）
	 */
	protected final int productType;
	
	/**
	 * 钻石折扣概率配置
	 */
	protected List<ShopDiscount> gemDiscounts;

	public MysteryShopItemCfg() {
		listId = 0;
		typeId = null;
		productId = 0;
		productCount = 0;
		productPrice = 0;
		gemDiscount = null;
		weight = 0;
		refreshCoinRate = 0;
		productType = 0;
		gemDiscounts = new LinkedList<ShopDiscount>();
	}
	
	@Override
	protected boolean assemble(){
		gemDiscounts.clear();
		if(!gemDiscount.equals("0")){
			if (!praseRate(gemDiscount, gemDiscounts)) {
				return false;
			}
		}
		return true;
	}

	/**
	 * 解析概率
	 * 
	 * @param rate
	 * @param gemDiscounts
	 */
	private boolean praseRate(String gemDiscount, List<ShopDiscount> gemDiscounts) {
		if (gemDiscount == null || gemDiscount.length() <= 0 || gemDiscounts == null) {
			return false;
		}

		String[] coins = gemDiscount.split(",");
		for (String coin : coins) {
			String[] coinRate = coin.split("_");
			if (coinRate.length != 2) {
				return false;
			}
			ShopDiscount discount = new ShopDiscount(Integer.parseInt(coinRate[0]), Integer.parseInt(coinRate[1]));
			gemDiscounts.add(discount);
		}
		return true;
	}
	
	/**
	 * 打折率
	 * @return
	 */
	public int getDiscountRate() {
		return getDiscount(gemDiscounts);
	}
	
	/**
	 * 根据权重表取得折扣率
	 * 
	 * @param list
	 * @return
	 */
	private int getDiscount(List<ShopDiscount> list) {
		try {
			int rate = GuaJiRand.randInt(1, 100);
			int baseRate = 0;
			for (ShopDiscount discount : list) {
				baseRate = baseRate + discount.getDiscountRate();
				if (rate <= baseRate) {
					return discount.getDicountKey();
				}
			}
		} catch (Exception e) {
			MyException.catchException(e);
		}
		return 100;
	}
	
	
	public int getListId() {
		return listId;
	}

	public String getTypeId() {
		return typeId;
	}

	public int getProductId() {
		return productId;
	}

	public int getProductCount() {
		return productCount;
	}

	public int getProductPrice() {
		return productPrice;
	}

	public String getGemDiscount() {
		return gemDiscount;
	}

	public int getWeight() {
		return weight;
	}

	public int getRefreshCoinRate() {
		return refreshCoinRate;
	}

	public int getProductType() {
		return productType;
	}

	/**
	 * 清理相关静态数据
	 */
	protected void clearStaticData() {
	}
}
