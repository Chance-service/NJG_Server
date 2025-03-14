package com.guaji.game.manager.shop;

import java.util.List;

import com.guaji.game.player.Player;
import com.guaji.game.protocol.Const.DataType;
import com.guaji.game.protocol.Const.ShopType;
import com.guaji.game.protocol.Const.changeType;
import com.guaji.game.protocol.Shop.BuyShopItemsRequest;
import com.guaji.game.protocol.Shop.BuyShopItemsResponse;
import com.guaji.game.protocol.Shop.DisplayData;
import com.guaji.game.protocol.Shop.ShopItemInfoInit;
import com.guaji.game.protocol.Shop.ShopItemInfoRequest;
import com.guaji.game.protocol.Shop.ShopItemInfoResponse;

/**
 * 商店服务接口类<T>
 * @author zdz
 * @param <T>
 */
public abstract class ShopScervice<T> implements IShopScervice<T> {

	@Override
	public void sendShopBuilder(Player player) {
		
	}
	
	@Override
	public void shopRefreshLogic(Player player,ShopItemInfoRequest request) {
		
	}
			
	@Override
	public void shopBuyLogic(Player player, BuyShopItemsRequest request) {
		
	}
	
	@Override
	public List<Integer> getTotalWeight(List<T> itemList) {
		return null;
	}
	
	public List<T> getItemList(List<T> itemList, int randNumber) {
		return null;
	}
	
	@Override
	public ShopType getShopType() {
		return null;
	}
	
	/**
	 * 构建商店初始化数据包
	 * @param refreshPrice 刷新消耗
	 * @return
	 */
	public ShopItemInfoResponse.Builder createResponse(int refreshPrice) {
		ShopItemInfoResponse.Builder builder = ShopItemInfoResponse.newBuilder();
		builder.setShopType(this.getShopType());
		builder.setRefreshPrice(refreshPrice);
		return builder;
	}

	/**
	 * 构建商店显示数据
	 * @param buyType
	 * @param amount
	 * @return
	 */
	public DisplayData.Builder createDisplayData(DataType dataType, long amount) {
		DisplayData.Builder builder = DisplayData.newBuilder();
		builder.setDataType(dataType);
		builder.setAmount(amount);
		return builder;
	}
	
	/**
	 * 构建商店物品
	 * @param id 商城唯一ID
	 * @param itemId 物品ID
	 * @param itemType 物品类型
	 * @param count 物品数量
	 * @param buyType 购买消耗类型
	 * @param price 折扣后的购买的价格
	 * @param discont 购买折扣
	 * @param isPurple 是否显示紫色
	 * @return
	 */
	public ShopItemInfoInit.Builder createItemInfo(int id, int itemId, int itemType, int count, changeType type, int price, int discont, boolean isPurple) {
		ShopItemInfoInit.Builder builder = ShopItemInfoInit.newBuilder();
		builder.setId(id);
		builder.setItemId(itemId);
		builder.setItemType(itemType);
		builder.setCount(count);
		builder.setDataType(type);
		builder.setPrice(price);
		builder.setDiscont(discont);
//		builder.setIsPurple(isPurple);
		return builder;
	}
	
	/**
	 * 构建购买返回数据包
	 * @return
	 */
	public BuyShopItemsResponse.Builder createResponse() {
		BuyShopItemsResponse.Builder builder = BuyShopItemsResponse.newBuilder();
		builder.setShopType(this.getShopType());
		return builder;
	}
	
}
