package com.guaji.game.config;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.guaji.config.ConfigBase;
import org.guaji.config.ConfigManager;

import com.guaji.game.entity.DailyShopItem;
import com.guaji.game.item.ItemInfo;
import com.guaji.game.util.ItemUtil;


@ConfigManager.XmlResource(file = "xml/arenaShop.xml", struct = "list")
public class ArenaShopCfg extends ConfigBase {

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
	 * 權重
	 */
	private final int weight;

	/**
	 * 折扣
	 */
	private final int discount;

	/**
	 * 价格1
	 */
	private final String price;
	/**
	 * 購買等級限制
	 */
	private final int minlv;
	/**
	 * 加上minlv = random等級區間
	 */
	private final int maxlv;

	/**
	 * random 群組
	 */
	private final int group;
	/**
	 * 各群組比重
	 */
	private static Map<Integer,List<Integer>> groupbyWeight;
	/**
	 * 各群組cfg
	 */
	private static Map<Integer,List<ArenaShopCfg>> groupbyCfg;
	
	
	public ArenaShopCfg() {
		id= 0;
		discount = 0;
		item = null;
		count = 0;
		weight = 0;
		price = null;
		minlv = 0 ;
		maxlv = 0 ;
		group = 0;
		groupbyWeight = new HashMap<>();
		groupbyCfg = new HashMap<>();
	}

	@Override
	protected boolean assemble() {
		if (groupbyCfg.containsKey(this.getGroup()))
		{
			groupbyCfg.get(this.getGroup()).add(this);
			groupbyWeight.get(this.getGroup()).add(this.getWeight());
		} else {
			List<ArenaShopCfg> clist = new ArrayList<>();
			clist.add(this);
			List<Integer> wlist = new ArrayList<>();
			wlist.add(this.getWeight());
			groupbyCfg.put(this.getGroup(),clist);
			groupbyWeight.put(this.getGroup(),wlist);
		}
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
	
	public int getWeight() {
		return weight;
	}

	public int getDiscount() {
		return this.discount;
	}
	
	public String getPrice() {
		return price;
	}

	public int getMinLv() {
		return minlv;
	}
	
	public int getMaxlv() {
		return maxlv;
	}

	public int getGroup() {
		return group;
	}

	/**
	 * 组装商品对象
	 * 
	 * @param cfg
	 * @param id
	 * @return
	 */
	public static DailyShopItem getShopInfo(ArenaShopCfg cfg, int id) {
		String[] es = cfg.item.split("_");
		ItemInfo info = ItemInfo.valueOf(cfg.price);// 返回折扣信息，使用itemInfo对象
		DailyShopItem shopItemInfo = new DailyShopItem();
		shopItemInfo.setId(id);
		shopItemInfo.setItemType(Integer.valueOf(es[0]));
		shopItemInfo.setItemId(Integer.valueOf(es[1]));
		shopItemInfo.setItemCount(Integer.valueOf(es[2]));
		shopItemInfo.setCostType(ItemUtil.getChangeType(info));// 货币类型
		shopItemInfo.setCostCount(info.getItemId());// 钱数
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
	public static List<ArenaShopCfg> getShopCfgs(){
		List<ArenaShopCfg> aList = ConfigManager.getInstance().getConfigList(ArenaShopCfg.class);
		return aList ;
	}
	
	public static ArenaShopCfg getCfgbyIndex(int idx) {
		 List<ArenaShopCfg> alist = getShopCfgs();
		 for (ArenaShopCfg acfg : alist) {
			 if (idx == acfg.getId()) {
				 return acfg;
			 }
		 }
		 return null;
	}
	
	public static Map<Integer,List<Integer>> getGroupbyWeight(){
		return groupbyWeight;
	}
	
	public static Map<Integer,List<ArenaShopCfg>> getGroupbyCfg(){
		return groupbyCfg;
	}
	
	
}
