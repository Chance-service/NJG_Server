package com.guaji.game.config;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.guaji.config.ConfigBase;

import com.guaji.game.util.ConfigUtil;

//@ConfigManager.XmlResource(file = "xml/blackMarketItemCfg.xml", struct = "list")
public class BlackMarketItemCfg extends ConfigBase {

	/**
	 * 商品配置唯一ID
	 */
	private final int id;
	
	/**
	 * 分组ID
	 */
	private final int listId;
	
	/**
	 * 物品类型
	 */
	private final int itemType;

	/**
	 * 物品id
	 */
	private final int itemId;

	/**
	 * 物品数量
	 */
	private final int itemCount;

	/**
	 * 金币售价
	 */
	private final int coin;

	/**
	 * 金币折扣概率
	 */
	private final String coinDiscount;

	/**
	 * 刷出金币售价权重
	 */
	private final int refreshCoinRate;

	/**
	 * 钻石售价
	 */
	private final int gold;

	/**
	 * 钻石折扣概率
	 */
	private final String goldDiscount;

	/**
	 * 刷出钻石售价权重
	 */
	private final int refreshGoldRate;

	/**
	 * 出现该物品权重
	 */
	private final int weight;

	/**
	 * 金币折扣概率配置
	 */
	private List<ShopDiscount> coinDiscounts;

	/**
	 * 钻石折扣概率配置
	 */
	private List<ShopDiscount> goldDiscounts;
	
	/**
	 * 物品分类<key:listId value:集合>
	 */
	public static Map<Integer, List<BlackMarketItemCfg>> itemMap = new HashMap<Integer, List<BlackMarketItemCfg>>();
	

	public BlackMarketItemCfg() {
		
		this.id = 0;
		this.listId = 0;
		this.itemType = 0;
		this.itemId = 0;
		this.coin = 0;
		this.coinDiscount = null;
		this.refreshCoinRate = 0;
		this.gold = 0;
		this.goldDiscount = null;
		this.refreshGoldRate = 0;
		this.weight = 0;
		this.itemCount = 0;

		this.coinDiscounts = new LinkedList<ShopDiscount>();
		this.goldDiscounts = new LinkedList<ShopDiscount>();
	}

	public int getId() {
		return id;
	}

	public int getListId() {
		return listId;
	}

	public int getItemType() {
		return itemType;
	}

	public int getItemId() {
		return itemId;
	}

	public int getCoin() {
		return coin;
	}

	public int getRefreshCoinRate() {
		return refreshCoinRate;
	}

	public int getGold() {
		return gold;
	}

	public int getRefreshGoldRate() {
		return refreshGoldRate;
	}

	public int getWeight() {
		return weight;
	}

	public int getItemCount() {
		return itemCount;
	}

	public String getCoinDiscount() {
		return coinDiscount;
	}

	public String getGoldDiscount() {
		return goldDiscount;
	}

	@Override
	protected boolean assemble() {
		
		if (!this.praseRate(coinDiscount, coinDiscounts)) {
			return false;
		}
		if (!this.praseRate(goldDiscount, goldDiscounts)) {
			return false;
		}
		
		List<BlackMarketItemCfg> itemList = itemMap.get(this.listId);
		if (null == itemList) {
			itemList = new ArrayList<BlackMarketItemCfg>();
			itemList.add(this);
			itemMap.put(this.listId, itemList);
		} else {
			itemList.add(this);
		}
		
		return true;
	}

//	/**
//	 * 根据权重取得购买类型
//	 * @return
//	 */
//	public DataType getBuyTypeByRate() {
//		
//		try {
//			int sumWeight = refreshCoinRate + refreshGoldRate;
//			int weight = GuaJiRand.randInt(1, sumWeight);
//			if (weight <= refreshCoinRate) {
//				return DataType.COIN_TYPE;
//			}
//		} catch (Exception e) {
//			MyException.catchException(e);
//		}
//		return DataType.GOLD_TYPE;
//	}

//	/**
//	 * 取得价格
//	 * @param type
//	 * @return
//	 */
//	public int getBuyPriceByType(DataType type) {
//		
//		if (type == DataType.COIN_TYPE) {
//			return getCoin();
//		}
//		return getGold();
//	}

//	/**
//	 * 打折率
//	 * @param buyType
//	 * @return
//	 */
//	public int getDiscountByBuyType(DataType type) {
//		
//		if (type == DataType.COIN_TYPE) {
//			return this.getDiscount(coinDiscounts);
//		} else if (type == DataType.GOLD_TYPE) {
//			return this.getDiscount(goldDiscounts);
//		}
//		return 100;
//	}

	/**
	 * 根据权重表取得折扣率
	 * @param list
	 * @return
	 */
//	private int getDiscount(List<ShopDiscount> list) {
//		
//		try {
//			int rate = GuaJiRand.randInt(1, 100);
//			int baseRate = 0;
//			for (ShopDiscount discount : list) {
//				baseRate = baseRate + discount.getDiscountRate();
//				if (rate <= baseRate) {
//					return discount.getDicountKey();
//				}
//			}
//		} catch (Exception e) {
//			MyException.catchException(e);
//		}
//		return 100;
//	}

	/**
	 * 解析概率
	 * @param rate
	 * @param shoDiscounts
	 */
	private boolean praseRate(String rate, List<ShopDiscount> shopDiscounts) {
		
		if (rate == null || rate.length() <= 0 || shopDiscounts == null) {
			return false;
		}

		String[] coins = rate.split(",");
		for (String coin : coins) {
			String[] coinRate = coin.split("_");
			if (coinRate.length != 2) {
				return false;
			}
			if (coinRate[0].equals("0") || coinRate[1].equals("0")) {
				continue;
			}
			ShopDiscount discount = new ShopDiscount(Integer.parseInt(coinRate[0]), Integer.parseInt(coinRate[1]));
			shopDiscounts.add(discount);
		}
		if (shopDiscounts.size() <= 0) {
			return false;
		}
		return true;
	}

	@Override
	protected boolean checkValid() {
		
		if (!ConfigUtil.check(getItemType(), getItemId())) {
			return false;
		}
		return true;
	}
	
}