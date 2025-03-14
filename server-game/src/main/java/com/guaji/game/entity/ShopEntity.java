package com.guaji.game.entity;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.guaji.db.DBEntity;
import org.guaji.os.GuaJiTime;

import com.guaji.game.manager.shop.PlayerShopItem;

import net.sf.json.JSONArray;

/**
 * 商店实体对象
 */
@Entity
@Table(name = "shop")
@SuppressWarnings("serial")
public class ShopEntity extends DBEntity {
	
	/**
	 * 1000000万为幸运值最大值
	 */
	private static final int MAX_LUCKEY_VALUE = 1000000;

	@Id
	@Column(name = "playerId")
	private int playerId = 0;

	/**
	 * 商城幸运值
	 */
	@Column(name = "shopLuckValue")
	private int shopLuckValue = 0;

	/**
	 * 购买金币次数
	 */
	@Column(name = "buyCoinCount")
	private int buyCoinCount = 0;

	/**
	 * 商城物品数据
	 */
	@Column(name = "shopItems")
	private String shopItems = "";

	/**
	 * 刷新时间
	 */
	@Column(name = "refreshDate")
	private Date refreshDate;

	/**
	 * 每天刷新次数
	 */
	@Column(name = "refreshTimes")
	private int refreshTimes = 0;

	@Column(name = "createTime", nullable = false)
	protected Date createTime = null;
	
	@Column(name = "updateTime")
	protected Date updateTime;
	
	@Column(name = "invalid")
	protected boolean invalid;
	
	@Transient
	private List<PlayerShopItem> shopItemList = null;

	public ShopEntity() {
		createTime = GuaJiTime.getCalendar().getTime();
	}

	public int getPlayerId() {
		return playerId;
	}

	public void setPlayerId(int playerId) {
		this.playerId = playerId;
	}

	public int getShopLuckValue() {
		return shopLuckValue;
	}

	public void setShopLuckValue(int shopLuckValue) {
		this.shopLuckValue = shopLuckValue;
		if (shopLuckValue >= MAX_LUCKEY_VALUE) {
			shopLuckValue = MAX_LUCKEY_VALUE;
		}
	}

	public String getShopItems() {
		return shopItems;
	}

	public void setShopItems(String shopItems) {
		this.shopItems = shopItems;
	}

	public Date getRefreshDate() {
		return refreshDate;
	}

	public void setRefreshDate(Date refreshDate) {
		this.refreshDate = refreshDate;
	}

	public int getRefreshTimes() {
		return refreshTimes;
	}

	public void setRefreshTimes(int refreshTimes) {
		this.refreshTimes = refreshTimes;
	}

	public int getBuyCoinCount() {
		return buyCoinCount;
	}

	public void setBuyCoinCount(int buyCoinCount) {
		this.buyCoinCount = buyCoinCount;
	}

	public Date getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}

	public Date getUpdateTime() {
		return updateTime;
	}

	public void setUpdateTime(Date updateTime) {
		this.updateTime = updateTime;
	}

	public boolean isInvalid() {
		return invalid;
	}

	public void setInvalid(boolean invalid) {
		this.invalid = invalid;
	}
	
	/**
	 * 数据清除
	 */
	public void clearData() {
		
		this.shopLuckValue = 0;
		this.buyCoinCount = 0;
		this.refreshTimes = 0;
		this.shopItems = "";
		this.refreshDate = new Date();
		this.createTime = GuaJiTime.getCalendar().getTime();
	}

	/**
	 * 获取用户商品
	 * return
	 */
	@SuppressWarnings("unchecked")
	public List<PlayerShopItem> getShopItemList() {
		
		if (this.shopItemList != null) {
			return shopItemList;
		} else {
			shopItemList = new ArrayList<PlayerShopItem>();
			JSONArray array = null;
			if (shopItems != null && shopItems.length() > 0) {
				array = JSONArray.fromObject(shopItems);
			} else {
				array = new JSONArray();
			}
			shopItemList = ((List<PlayerShopItem>) JSONArray.toCollection(array, PlayerShopItem.class));
		}
		return shopItemList;
	}

	/**
	 * 赋值商品
	 * @param list
	 */
	public void shopItemToString(List<PlayerShopItem> list) {
		
		this.shopItemList = null;
		JSONArray array = JSONArray.fromObject(list);
		this.shopItems = array.toString();
	}

	/** 增加幸运值 */
	public void addShopLuckValue(int value) {
		this.shopLuckValue += value;
		if (shopLuckValue >= MAX_LUCKEY_VALUE) {
			shopLuckValue = MAX_LUCKEY_VALUE;
		}
	}

	/**
	 * 购买金币次数
	 */
	public void addBuyCoinCount() {
		this.buyCoinCount++;
	}

	/**
	 * 每天刷新次数
	 */
	public void addRefreshTimes() {
		this.refreshTimes++;
	}

	/**
	 * 移除商品
	 * @param removeItemList
	 */
	public void removeShopItem(List<PlayerShopItem> removeItemList) {
		
		this.getShopItemList();
		this.shopItemList.removeAll(removeItemList);
		shopItemToString(this.shopItemList);
	}
	
}
