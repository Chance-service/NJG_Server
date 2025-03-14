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

@ConfigManager.XmlResource(file = "xml/mysteryShop.xml", struct = "list")
public class MysteryShopCfg extends ConfigBase {

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
	 * 折扣範圍
	 */
	private final String discount;
	/**
	 * 折扣數量
	 */
	private final String dcCounts;

	/**
	 * 价格1
	 */
	private final String price;
	
	/**
	 * random 群組
	 */
	private final int group;
	/**
	 * vip限定
	 */
	private final int vip;
	
	/**
	 * 各群組比重
	 */
	private static Map<Integer,List<Integer>> groupbyWeight;
	/**
	 * 各群組cfg
	 */
	private static Map<Integer,List<MysteryShopCfg>> groupbyCfg;
	/**
	 * 折扣or倍率表索引對應(shopDiscount.xml)
	 */
	private final int dcgroup;
	/**
	 * 折扣範圍
	 */
	private List<Float> discountList ;
	/**
	 * 折扣數量
	 */
	private List<Integer> dcCountsList ;
	
	public MysteryShopCfg() {
		id= 0;
		discount = null;
		dcCounts = null;
		item = null;
		count = 0;
		weight = 0;
		price = null;
		group = 0;
		vip = 0;
		dcgroup = 0;
		groupbyWeight = new HashMap<>();
		groupbyCfg = new HashMap<>();
		discountList = new ArrayList<>();
		dcCountsList = new ArrayList<>();
	}

	@Override
	protected boolean assemble() {
		
		if (groupbyCfg.containsKey(this.getGroup()))
		{
			groupbyCfg.get(this.getGroup()).add(this);
			groupbyWeight.get(this.getGroup()).add(this.getWeight());
		} else {
			List<MysteryShopCfg> clist = new ArrayList<>();
			clist.add(this);
			List<Integer> wlist = new ArrayList<>();
			wlist.add(this.getWeight());
			groupbyCfg.put(this.getGroup(),clist);
			groupbyWeight.put(this.getGroup(),wlist);
		}
		
		this.discountList.clear();
		if (!discount.isEmpty()){
			String[] ss = discount.split(",");
			for (String astr : ss) {
				discountList.add(Float.valueOf(astr.trim()));
			}
		}
		
		this.dcCountsList.clear();
		if (!dcCounts.isEmpty()){
			String[] ss = dcCounts.split(",");
			for (String astr : ss) {
				dcCountsList.add(Integer.valueOf(astr.trim()));
			}
		}
		
		if (this.discountList.size() != this.dcCountsList.size()) {
			return false;
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
	
	public int getWeight() {
		return this.weight;
	}
	
	public int getCount() {
		return count;
	}

	
	public String getPrice() {
		return price;
	}
	
	
	public int getGroup() {
		return group;
	}
	
	public int getVip() {
		return vip;
	}
	
	public int getDcgroup() {
		return dcgroup;
	}
	
	public List<Float> getDiscountList() {
		return discountList;
	}

	public void setDiscountList(List<Float> discountList) {
		this.discountList = discountList;
	}

	public List<Integer> getDcCountsList() {
		return dcCountsList;
	}

	public void setDcCountsList(List<Integer> dcCountsList) {
		this.dcCountsList = dcCountsList;
	}
	/**
	 * 组装商品对象
	 * 
	 * @param cfg
	 * @param id
	 * @return
	 */
	public static DailyShopItem getShopInfo(MysteryShopCfg cfg, int id) {
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
		shopItemInfo.setDiscount(100);
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
	public static List<MysteryShopCfg> getShopCfgs(){
		List<MysteryShopCfg> aList = ConfigManager.getInstance().getConfigList(MysteryShopCfg.class);
		return aList ;
	}
	
	/**
	 * 取得單一商品byId
	 */
	public static MysteryShopCfg getShopCfgById(int id){
		return MysteryShopCfg.getShopCfgs().stream().filter(cfg -> cfg.getId() == id).findFirst().orElse(null);

	}
	
	public static Map<Integer,List<Integer>> getGroupbyWeight(){
		return groupbyWeight;
	}
	
	public static Map<Integer,List<MysteryShopCfg>> getGroupbyCfg(){
		return groupbyCfg;
	}
	
	/**
	 *  依照購買次數獲得折扣 
	 * @param shopcount
	 * @return
	 */
	public float getDiscountByCount(int shopcount) {
		float discount = 1.0f;
		
		int maxIndex = this.getDcCountsList().size()-1;
		
		if  (shopcount >= this.getDcCountsList().get(maxIndex)) {
			discount = this.getDiscountList().get(maxIndex);
		} else {
			for (int i = 0 ; i <= maxIndex ; i++) {
				int acount = this.getDcCountsList().get(i);
				if (shopcount <= acount) {
					return this.getDiscountList().get(i);
				}
			}
		}
		return discount ;
	}
}
