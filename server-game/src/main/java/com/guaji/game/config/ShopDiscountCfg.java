package com.guaji.game.config;

import java.util.ArrayList;
import java.util.List;

import org.guaji.config.ConfigBase;
import org.guaji.config.ConfigManager;

/**
 * 商店折購商品對應表
 * @author Tinlin_Home
 *
 */

@ConfigManager.XmlResource(file = "xml/shopDiscount.xml", struct = "map")
public class ShopDiscountCfg extends ConfigBase {
	
	/**
	 * 配置id
	 */
	@Id
	private final int id;
	
	/**
	 * 數量計算
	 */
	private final String count;
	/**
	 * 對應數值折購
	 */
	private final String discount;
	/**
	 * 轉換後數量表
	 */
	private List<Integer> countList;
	/**
	 * 轉換後折扣表
	 */
	private List<Float> discountList;
	
	public ShopDiscountCfg() {
		this.id = 0;
		this.count = "";
		this.discount ="";
		this.countList = new ArrayList<>();
		this.discountList = new ArrayList<>();
	}
	
	

	public List<Integer> getCountList() {
		return countList;
	}



	public void setCountList(List<Integer> countList) {
		this.countList = countList;
	}



	public List<Float> getDiscountList() {
		return discountList;
	}



	public void setDiscountList(List<Float> discountList) {
		this.discountList = discountList;
	}



	public int getId() {
		return id;
	}



	public String getCount() {
		return count;
	}



	public String getDiscount() {
		return discount;
	}



	/**
	 * 数据格式转化
	 */
	@Override
	protected boolean assemble() {
		countList.clear();
		if (!count.isEmpty()){
			String[] countAry = count.split(",");
			for (String countStr:countAry) {
				countList.add(Integer.valueOf(countStr));
			}
		}
		discountList.clear();
		if (!discount.isEmpty()){
			String[] discountAry = discount.split(",");
			for (String discountStr:discountAry) {
				discountList.add(Float.valueOf(discountStr));
			}
		}
		if (countList.size() != discountList.size()) {
			return false;
		}
		return true;
	}

	/**
	 * 检测有消息
	 * 
	 * @return
	 */
	@Override
	protected boolean checkValid() {
		return true;
	}

	/**
	 * 清理相关静态数据
	 */
	protected void clearStaticData() {
	}
	
	/**
	 *  依照購買次數獲得折扣 
	 * @param shopcount
	 * @return
	 */
	public float getDiscountByCount(int shopcount) {
		float discount = 1.0f;
		
		int maxIndex = this.getCountList().size()-1;
		
		if  (shopcount >= this.getCountList().get(maxIndex)) {
			discount = this.getDiscountList().get(maxIndex);
		} else {
			for (int i = 0 ; i <= maxIndex ; i++) {
				int acount = this.getCountList().get(i);
				if (shopcount <= acount) {
					return this.getDiscountList().get(i);
				}
			}
		}
		return discount ;
	}
	
}
