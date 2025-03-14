package com.guaji.game.module.activity.mysteryShop;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.guaji.game.shop.PlayerShopItem;

import net.sf.json.JSONArray;

/**
 * 黑市
 * 
 * @author Darcy
 * 
 */
public class MysteryShopStatus{

	/**
	 * 刷新时间
	 */
	private Date refreshDate;

	/**
	 * 每天刷新次数
	 */
	private int daliyRefreshNum = 0;

	/**
	 * 黑市物品数据
	 */
	private String mysteryShopItem = "";

	/**
	 * 商品数量
	 */
	private int currentItemNum = 0;
	
	/**
	 * 竞拍物品价格
	 */
	private int auctionItemPrice = 0;

	private List<PlayerShopItem> playerShopItems = null;


	public Date getRefreshDate() {
		return refreshDate;
	}

	public void setRefreshDate(Date refreshDate) {
		this.refreshDate = refreshDate;
	}

	public int getDaliyRefreshNum() {
		return daliyRefreshNum;
	}

	public void setDaliyRefreshNum(int daliyRefreshNum) {
		this.daliyRefreshNum = daliyRefreshNum;
	}

	public String getMysteryShopItem() {
		return mysteryShopItem;
	}

	public void setMysteryShopItem(String mysteryShopItem) {
		this.mysteryShopItem = mysteryShopItem;
	}

	public int getCurrentItemNum() {
		return currentItemNum;
	}

	public void setCurrentItemNum(int currentItemNum) {
		this.currentItemNum = currentItemNum;
	}
	
	public int getAuctionItemPrice() {
		return auctionItemPrice;
	}

	public void setAuctionItemPrice(int auctionItemPrice) {
		this.auctionItemPrice = auctionItemPrice;
	}
	
	public void clear(){
		this.daliyRefreshNum = 0;
		this.mysteryShopItem = "";
		this.currentItemNum = 0;
		this.refreshDate = new Date();
	}
	
	/** 获取用户商品 **/
	@SuppressWarnings("unchecked")
	public List<PlayerShopItem> getPlayerMysteryShopItemList() {
		if (this.playerShopItems != null) {
			return playerShopItems;
		} else {
			playerShopItems = new ArrayList<>();
			JSONArray array = null;
			if (mysteryShopItem != null && mysteryShopItem.length() > 0) {
				array = JSONArray.fromObject(mysteryShopItem);
			} else {
				array = new JSONArray();
			}
			playerShopItems = ((List<PlayerShopItem>) JSONArray.toCollection(array, PlayerShopItem.class));
			int size = playerShopItems.size() - 6;
			if (size > 0) {
				for (int i = 0; i < size; i++) {
					playerShopItems.get(i).setPurple(true);
				}
			}
		}
		return playerShopItems;
	}
	
	/**
	 * 根据ItemId获取商品
	 * @param itemId
	 * @return
	 */
	public PlayerShopItem getPlayerShopItem(String itemId){
		getPlayerMysteryShopItemList();
		for (PlayerShopItem playerShopItem : playerShopItems) {
			if(itemId.equals(playerShopItem.getId())){
				return playerShopItem;
			}
		}
		return null;
	}
	
	public void removePlayerShopItem(PlayerShopItem... playerShopItem){
		getPlayerMysteryShopItemList();
		for (PlayerShopItem shop : playerShopItem) {
			this.playerShopItems.remove(shop);
		}
		this.currentItemNum = playerShopItems.size();
		shopItemToString(this.playerShopItems);
		
	}
	
	public void removePlayerShopItem(List<PlayerShopItem> playerShopItem){
		getPlayerMysteryShopItemList();
		this.playerShopItems.removeAll(playerShopItem);
		this.currentItemNum = playerShopItems.size();
		this.shopItemToString(playerShopItems);
	}
	
	
	/** 赋值商品 */
	public void shopItemToString(List<PlayerShopItem> list) {
		this.playerShopItems = null;
		JSONArray array = JSONArray.fromObject(list);
		this.mysteryShopItem = array.toString();
	}
	
	public void increaseDaliyRefreshNum() {
		this.daliyRefreshNum++;
	}

}
