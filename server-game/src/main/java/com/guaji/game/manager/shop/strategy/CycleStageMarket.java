package com.guaji.game.manager.shop.strategy;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.guaji.net.protocol.Protocol;

import com.guaji.game.config.CycleStageShopCfg;
import com.guaji.game.entity.CycleStageShopEntity;
import com.guaji.game.entity.DailyShopItem;
import com.guaji.game.item.AwardItems;
import com.guaji.game.item.ConsumeItems;
import com.guaji.game.item.ItemInfo;
import com.guaji.game.log.BehaviorLogger;
import com.guaji.game.log.BehaviorLogger.Action;
import com.guaji.game.log.BehaviorLogger.Params;
import com.guaji.game.manager.shop.ShopScervice;
import com.guaji.game.player.Player;
import com.guaji.game.protocol.Const;
import com.guaji.game.protocol.Const.DataType;
import com.guaji.game.protocol.Const.ShopType;
import com.guaji.game.protocol.Const.changeType;
import com.guaji.game.protocol.HP;
import com.guaji.game.protocol.Shop.BuyShopItemsRequest;
import com.guaji.game.protocol.Shop.BuyShopItemsResponse;
import com.guaji.game.protocol.Shop.ShopItemInfoInit;
import com.guaji.game.protocol.Shop.ShopItemInfoResponse;
import com.guaji.game.protocol.Status;
import com.guaji.game.util.GsonUtil;

/**
 * 水晶与普通商店合并
 * 
 * @author zdz
 *
 */
public class CycleStageMarket extends ShopScervice<CycleStageShopCfg> {

	private static final CycleStageMarket instance = new CycleStageMarket();

	public static CycleStageMarket getInstance() {
		return instance;
	}

	/**
	 * 商店物品初始化
	 * 
	 * @param player
	 * @param entity
	 * @param luckyScore
	 */
	public List<CycleStageShopCfg> shopItemInfo(Player player, CycleStageShopEntity entity,boolean isCreate) {

		entity.getShopMap().clear();
		// vip等级对应物品数量
		//int showItemCount = this.getMaxShopItemCount(player.getVipLevel());
		// 等级对应物品集
		List<CycleStageShopCfg> ShopList = CycleStageShopCfg.getShopCfgs();
		// 商品集合clone
		List<CycleStageShopCfg> ShopCfgsClone = new ArrayList<CycleStageShopCfg>(ShopList);

		// 商城显示集合
		//List<CycleStageShopCfg> crystalItemList = this.getItemList(DailyShopCfgsClone, showItemCount);

		for (int i = 0; i < ShopCfgsClone.size(); i++) {
			DailyShopItem dailyShopItem = CycleStageShopCfg.getDailyShopInfo(ShopCfgsClone.get(i), i + 1);
			entity.addShopItem(dailyShopItem);
		}

		entity.setShopItemMapStr(GsonUtil.getJsonInstance().toJson(entity.getShopMap()));
		if (!isCreate) {
			entity.notifyUpdate();
		}

		return ShopCfgsClone;
	}

	@Override
	public void sendShopBuilder(Player player) {

		CycleStageShopEntity entity = player.getPlayerData().loadCycleStageShopEntity();

		// 商城构建
		Map<Integer, DailyShopItem> ShopMap = entity.getShopMap();
		int refreshPrice = 0;//entity.getNextRefreshCost();

		ShopItemInfoResponse.Builder builder = this.createResponse(refreshPrice);
		
		
		// 商城物品构建
		for (DailyShopItem item : ShopMap.values()) {
			int price = item.getCostCount();
			int discount = item.getDiscount();
			int total = item.getInitCount();
			
			ShopItemInfoInit.Builder iBuilder = this.createItemInfo(item.getId(), item.getItemId(), item.getItemType(), item.getItemCount(),
					changeType.valueOf(item.getCostType()), price,discount, false);
			iBuilder.setTotalcount(total);
			iBuilder.setPriceStr(item.getPriceStr());
			builder.addItemInfo(iBuilder);
			builder.addData(this.createDisplayData(DataType.ITEM_LEFT,item.getLeft()));
		}
		player.sendProtocol(Protocol.valueOf(HP.code.SHOP_ITEM_S_VALUE, builder));
	}

//	@Override
//	public void shopRefreshLogic(Player player,ShopItemInfoRequest request) {
//		
//	}

	@Override
	public void shopBuyLogic(Player player, BuyShopItemsRequest request) {

		int buyShopId = request.getId();
		//int type = request.getType(); 只有單買
		
		ShopType shoptype = request.getShopType();
		
		if (shoptype != getShopType()) {
			player.sendError(HP.code.SHOP_BUY_S_VALUE, Status.error.CONFIG_NOT_FOUND_VALUE);
			return;
		}

		CycleStageShopEntity ShopEntity = player.getPlayerData().loadCycleStageShopEntity();


		DailyShopItem shopItem = ShopEntity.getShopMap().get(buyShopId);
		
		if (shopItem == null) {
			player.sendError(HP.code.SHOP_BUY_S_VALUE, Status.error.CONFIG_NOT_FOUND_VALUE);
			return;
		}
		
		int amount = request.getAmount();
				
		if (shopItem.getInitCount() != 0) { // 有限量物品
			// 商城物品是否有存貨
			if ((shopItem.getLeft() == 0) || (amount > shopItem.getLeft()) ||  (amount <= 0))  {
				player.sendError(HP.code.SHOP_BUY_C_VALUE, Status.error.CONFIG_NOT_FOUND_VALUE);
				return;
			}
		}
		
		AwardItems awardItems = new AwardItems();
		
		awardItems.addItem(shopItem.getItemType(), shopItem.getItemId(), shopItem.getItemCount());

		// 扣除购买消耗---金币 钻石 水晶
		ConsumeItems consumeItems = ConsumeItems.valueOf();
		
		List<ItemInfo> needItems = new ArrayList<ItemInfo>();
		
		List<ItemInfo> TmpItems = ItemInfo.valueListOf(shopItem.getPriceStr());
		
		for (ItemInfo aItem : TmpItems) {
			ItemInfo need = ItemInfo.valueOf(aItem.getType(), aItem.getItemId(),aItem.getQuantity()*amount);
			needItems.add(need);
		}
						
		if (!consumeItems.addConsumeInfo(player.getPlayerData(), needItems)) {
			return;
		}

		// 消耗数据校验
		if (!consumeItems.checkConsume(player, HP.code.SHOP_BUY_C_VALUE)) {
			return;
		}
		// 扣除消耗数据
		consumeItems.consumeTakeAffect(player, Action.SHOP_BUY_TOOLS);

		awardItems.rewardTakeAffectAndPush(player, Action.SHOP_BUY_TOOLS, 1);

		BehaviorLogger.log4Platform(player, Action.SHOP_BUY_TOOLS, Params.valueOf("itemId", shopItem.getItemId()),
				Params.valueOf("itemCount", shopItem.getItemCount()), Params.valueOf("itemType", shopItem.getItemType()),
				Params.valueOf("PirecStr", shopItem.getPriceStr()),
				Params.valueOf("Amount", amount));

		// 扣除庫存
		if (shopItem.getInitCount() != 0) { // 有限量物品
			shopItem.setLeft(shopItem.getLeft()-amount);
			
			ShopEntity.SaveShopItem();
	
			ShopEntity.notifyUpdate(true);
		}

		player.getPlayerData().syncPlayerInfo();

		// 构建返回数据包
		BuyShopItemsResponse.Builder builder = this.createResponse();
		for (DailyShopItem item : ShopEntity.getShopMap().values()) {
			ShopItemInfoInit.Builder itemBuider = this.createItemInfo(item.getId(), item.getItemId(), item.getItemType(), item.getItemCount(),
					changeType.valueOf(item.getCostType()), item.getCostCount(),item.getDiscount(), false);
			itemBuider.setTotalcount(item.getInitCount());
			itemBuider.setPriceStr(item.getPriceStr());
			builder.addItemInfo(itemBuider);
			builder.addData(this.createDisplayData(DataType.ITEM_LEFT,item.getLeft()));
		}

		player.sendProtocol(Protocol.valueOf(HP.code.SHOP_BUY_S_VALUE, builder));

		//QuestEventBus.fireQuestEventOneTime(QuestEventType.COMMON_SHOP_BUY_TIMES, player.getXid());

	}

	@Override
	public ShopType getShopType() {
		return Const.ShopType.CYCLESTAGE_MARKET;
	}
	
	/**
	 * 登陆的时候推送红点
	 */
//	public void onPlayerLogin(Player player) {
//		CrystalShopEntity crystalShopEntity = player.getPlayerData().loadCrystalShopEntity();
//		if (null != crystalShopEntity) {
//			int refreshCost = crystalShopEntity.getNextRefreshCost();
//			// 推送红点
//			if (refreshCost == 0) {
//				PushShopRedPoint.Builder response = PushShopRedPoint.newBuilder();
//				response.setShopType(ShopType.CRYSTAL_MARKET);
//				response.setShowRedPoint(true);
//				player.sendProtocol(Protocol.valueOf(HP.code.SHOP_RED_POINT_S_VALUE, response));
//			}
//		}
//	}
	
}
