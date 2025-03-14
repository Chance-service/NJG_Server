package com.guaji.game.config;

/**
 * 商城物品折扣
 * 
 * @author ManGao
 * 
 */
public class ShopDiscount {
	/**
	 * 折扣key 例如3折=30
	 */
	private int dicountKey = 100;
	/**
	 * 折扣概率 例如40=40%
	 */
	private int discountRate = 0;

	public ShopDiscount(int dicountKey, int discountRate) {
		this.dicountKey = dicountKey;
		this.discountRate = discountRate;
	}

	public int getDicountKey() {
		return dicountKey;
	}

	public int getDiscountRate() {
		return discountRate;
	}
	
	/**
	 * 清理相关静态数据
	 */
	protected void clearStaticData() {
	}
}
