package com.guaji.game.config;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import org.guaji.config.ConfigBase;
import org.guaji.config.ConfigManager;
import org.guaji.os.GuaJiRand;

import com.guaji.game.entity.CrystalShopItem;
import com.guaji.game.item.ItemInfo;
import com.guaji.game.util.WeightUtil.WeightItem;

@ConfigManager.XmlResource(file = "xml/crystalShop.xml", struct = "list")
public class CrystalShopCfg extends ConfigBase {

	/**
	 * 最高等级奖池
	 */
	private static int MAX_LEVEL_ITEM = 0;
	/**
	 * 商品编号
	 */
	private final int shopID;

	/**
	 * 活动ID
	 */
	private final int activityId;

	/**
	 * 商品信息
	 */
	private final String commodity;

	/**
	 * 折扣
	 */
	private final int discount;

	/**
	 * 商品权重
	 */
	private final int weight;

	/**
	 * 等级
	 */
	private final int level;

	/**
	 * 价格1
	 */
	private final String price1;

	/**
	 * 权重1
	 */
	private final int priceWeight1;

	/**
	 * 价格2
	 */
	private final String price2;

	/**
	 * 权重2
	 */
	private final int priceWeight2;

	/**
	 * 价格3
	 */
	private final String price3;

	/**
	 * 权重3
	 */
	private final int priceWeight3;

	/**
	 * 价格4
	 */
	private final String price4;

	/**
	 * 权重4
	 */
	private final int priceWeight4;

	/**
	 * 等级相关奖励权重池
	 */
	static private Map<Integer, List<WeightItem<CrystalShopItem>>> levelWeightItems;

	static private Map<Integer, List<CrystalShopCfg>> levelWeightMap;

	public CrystalShopCfg() {
		shopID = 0;
		discount = 0;
		commodity = null;
		weight = 0;
		level = 0;
		price1 = null;
		activityId = 0;
		priceWeight1 = 0;
		price2 = null;
		priceWeight2 = 0;
		price3 = null;
		priceWeight3 = 0;
		price4 = null;
		priceWeight4 = 0;
		levelWeightItems = new TreeMap<>();
		levelWeightMap = new TreeMap<Integer, List<CrystalShopCfg>>();
	}

	@Override
	protected boolean assemble() {
		// 商品权重池
		List<WeightItem<CrystalShopItem>> weightItems;

		String[] es = commodity.split("_");
		CrystalShopItem shopItemInfo = new CrystalShopItem();
		shopItemInfo.setItemType(Integer.valueOf(es[0]));
		shopItemInfo.setItemId(Integer.valueOf(es[1]));
		shopItemInfo.setItemCount(Integer.valueOf(es[2]));
		// shopItemInfo.setCostType(Integer.valueOf(es[3]));
		// shopItemInfo.setCostCount(Integer.valueOf(es[4]));
		shopItemInfo.setLevel(level);
		// shopItemInfo.setDiscount(discount);

		if (!levelWeightItems.containsKey(level)) {
			weightItems = new LinkedList<>();
			WeightItem<CrystalShopItem> weightItem = WeightItem.valueOf(shopItemInfo, Integer.valueOf(getWeight()));
			weightItems.add(weightItem);
			levelWeightItems.put(level, weightItems);
			if (level > MAX_LEVEL_ITEM) {
				MAX_LEVEL_ITEM = level; 
			}
		} else {
			WeightItem<CrystalShopItem> weightItem = WeightItem.valueOf(shopItemInfo, Integer.valueOf(getWeight()));
			levelWeightItems.get(level).add(weightItem);
		}

		if (levelWeightMap.containsKey(level)) {
			levelWeightMap.get(level).add(this);
		} else {
			List<CrystalShopCfg> list = new ArrayList<>();
			list.add(this);
			levelWeightMap.put(level, list);
		}

		return super.assemble();
	}

	@Override
	protected boolean checkValid() {
		return super.checkValid();
	}

	public int getWeight() {
		return this.weight;
	}

	public String getCommodity() {
		return this.commodity;
	}

	public int getDiscount() {
		return discount;
	}

//	public static List<CrystalShopItem> random(int level, int count) {
//		List<CrystalShopItem> listCrystalShopItem = new ArrayList<>();
//		List<WeightItem<CrystalShopItem>> list = levelWeightItems.get(getItemLevel(level));
//		listCrystalShopItem = WeightUtil.randomList(list, count);
//		return listCrystalShopItem;
//	}

	/**
	 * 获取匹配等级奖池
	 * 
	 * @param totalTimes
	 * @return
	 */
//	private static int getItemLevel(int level) {
//		Set<Integer> set = levelWeightItems.keySet();
//		Iterator<Integer> it = set.iterator();
//		while (it.hasNext()) {
//			Integer key = it.next();
//			if (level < key) {
//				return key;
//			}
//		}
//		// 最高奖池等级
//		return MAX_LEVEL_ITEM;
//	}

	/**
	 * 根据等级获取奖池
	 * 
	 * @param level
	 * @return
	 */
	public static List<CrystalShopCfg> getCrystalShopCfgs(int level) {
		Set<Integer> set = levelWeightMap.keySet();
		Iterator<Integer> it = set.iterator();
		while (it.hasNext()) {
			Integer key = it.next();
			if (level < key) {
				return levelWeightMap.get(key);
			}
		}
		return levelWeightMap.get(MAX_LEVEL_ITEM);
	}

	/**
	 * 组装商品对象
	 * 
	 * @param cfg
	 * @param id
	 * @return
	 */
	public static CrystalShopItem getCrystalShopInfo(CrystalShopCfg cfg, int id) {
		String[] es = cfg.commodity.split("_");
		ItemInfo info = getItemPriceInfo(cfg);// 返回折扣信息，使用itemInfo对象
		CrystalShopItem shopItemInfo = new CrystalShopItem();
		shopItemInfo.setItemType(Integer.valueOf(es[0]));
		shopItemInfo.setItemId(Integer.valueOf(es[1]));
		shopItemInfo.setItemCount(Integer.valueOf(es[2]));
		shopItemInfo.setCostType(info.getType());// 货币类型
		shopItemInfo.setCostCount(info.getItemId());// 钱数
		shopItemInfo.setLevel(cfg.level);
		shopItemInfo.setId(id);
		shopItemInfo.setDiscount((int)info.getQuantity());
		return shopItemInfo;
	}

	/**
	 * 获取价格信息
	 * 
	 * @param cfg
	 * @return
	 */
	private static ItemInfo getItemPriceInfo(CrystalShopCfg cfg) {
		ItemInfo info = GuaJiRand.randonWeightObject(getPriceItem(cfg), getPriceWeight(cfg));
		return info;
	}

	/**
	 * 获取当前配置价格集合
	 * 
	 * @param cfg
	 * @return
	 */
	private static List<ItemInfo> getPriceItem(CrystalShopCfg cfg) {
		List<ItemInfo> listItem = new ArrayList<>();
		listItem.add(ItemInfo.valueOf(cfg.price1));
		listItem.add(ItemInfo.valueOf(cfg.price2));
		listItem.add(ItemInfo.valueOf(cfg.price3));
		listItem.add(ItemInfo.valueOf(cfg.price4));
		return listItem;
	}

	/**
	 * 获取当前配置价格权重
	 * 
	 * @param cfg
	 * @return
	 */
	private static List<Integer> getPriceWeight(CrystalShopCfg cfg) {
		List<Integer> listPriceWeight = new ArrayList<>();
		listPriceWeight.add(cfg.priceWeight1);
		listPriceWeight.add(cfg.priceWeight2);
		listPriceWeight.add(cfg.priceWeight3);
		listPriceWeight.add(cfg.priceWeight4);
		return listPriceWeight;
	}

	/**
	 * 清理相关静态数据
	 */
	protected void clearStaticData() {
	}

	public static Map<Integer, List<WeightItem<CrystalShopItem>>> getLevelWeightItems() {
		return levelWeightItems;
	}

	public static void setLevelWeightItems(Map<Integer, List<WeightItem<CrystalShopItem>>> levelWeightItems) {
		CrystalShopCfg.levelWeightItems = levelWeightItems;
	}

	public static Map<Integer, List<CrystalShopCfg>> getLevelWeightMap() {
		return levelWeightMap;
	}

	public static void setLevelWeightMap(Map<Integer, List<CrystalShopCfg>> levelWeightMap) {
		CrystalShopCfg.levelWeightMap = levelWeightMap;
	}

	public static int getMaxLevelItem() {
		return MAX_LEVEL_ITEM;
	}

	public int getShopID() {
		return shopID;
	}

	public int getActivityId() {
		return activityId;
	}

	public int getLevel() {
		return level;
	}

	public String getPrice1() {
		return price1;
	}

	public int getPriceWeight1() {
		return priceWeight1;
	}

	public String getPrice2() {
		return price2;
	}

	public int getPriceWeight2() {
		return priceWeight2;
	}

	public String getPrice3() {
		return price3;
	}

	public int getPriceWeight3() {
		return priceWeight3;
	}

	public String getPrice4() {
		return price4;
	}

	public int getPriceWeight4() {
		return priceWeight4;
	}

}
