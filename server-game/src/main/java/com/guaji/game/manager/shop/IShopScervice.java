package com.guaji.game.manager.shop;

import java.util.List;

import com.guaji.game.player.Player;
import com.guaji.game.protocol.Const.ShopType;
import com.guaji.game.protocol.Shop.BuyShopItemsRequest;
import com.guaji.game.protocol.Shop.ShopItemInfoRequest;

/**
 * 商店服务接口类<T>
 * @author zdz
 */
public interface IShopScervice<T> {

	/**
	 * 构建商店数据包
	 * @param player
	 */
	public void sendShopBuilder(Player player);
	
	/**
	 * 构建商店数据包
	 * @param player
	 */
	public void shopRefreshLogic(Player player,ShopItemInfoRequest request);
		
	/**
	 * 商店物品购买逻辑处理
	 * @param player
	 * @param request
	 */
	public void shopBuyLogic(Player player, BuyShopItemsRequest request);
	
	/**
	 * 获取总权重值
	 * @param itemList
	 * @return
	 */
	public List<Integer> getTotalWeight(List<T> itemList);
	
	/**
	 * 获取随机物品集
	 * @param itemList
	 * @param randNumber
	 * @return
	 */
	public List<T> getItemList(List<T> itemList, int randNumber);
	
	/**
	 * 获取商店类型
	 * @return
	 */
	public ShopType getShopType();
	
}
