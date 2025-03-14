package com.guaji.game.manager.shop.strategy;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.guaji.net.protocol.Protocol;
import org.guaji.os.GuaJiRand;

import com.guaji.game.config.RaceShopCfg;
import com.guaji.game.config.SysBasicCfg;
import com.guaji.game.entity.DailyShopItem;
import com.guaji.game.entity.RaceShopEntity;
import com.guaji.game.item.AwardItems;
import com.guaji.game.item.ConsumeItems;
import com.guaji.game.item.ItemInfo;
import com.guaji.game.log.BehaviorLogger;
import com.guaji.game.log.BehaviorLogger.Action;
import com.guaji.game.log.BehaviorLogger.Params;
import com.guaji.game.manager.shop.ShopScervice;
import com.guaji.game.module.quest.QuestEventBus;
import com.guaji.game.player.Player;
import com.guaji.game.protocol.Const;
import com.guaji.game.protocol.Const.DataType;
import com.guaji.game.protocol.Const.QuestEventType;
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
 * 種族商店
 * 
 * @author lin
 *
 */
public class RaceMarket extends ShopScervice<RaceShopCfg> {

	private static final RaceMarket instance = new RaceMarket();

	public static RaceMarket getInstance() {
		return instance;
	}
	
	/**
	 * 商店物品初始化
	 * 
	 * @param player
	 * @param entity
	 * @param luckyScore
	 */
	public List<RaceShopCfg> shopItemInfo(Player player, RaceShopEntity entity,boolean isCreate) {

		entity.getRaceShopMap().clear();
		// vip等级对应物品数量
		//int showItemCount = this.getMaxShopItemCount();
		// 等级对应物品集
		//List<DailyShopCfg> dailyShopList = DailyShopCfg.getDailyShopCfgs();
		// 商品集合clone
		//List<DailyShopCfg> DailyShopCfgsClone = new ArrayList<DailyShopCfg>(dailyShopList);

		// 商城显示集合
		List<RaceShopCfg> ItemList = new ArrayList<>();
		ItemList = this.getItemList();

		for (int i = 0; i < ItemList.size(); i++) {
			DailyShopItem dailyShopItem = RaceShopCfg.getShopInfo(ItemList.get(i), i + 1);
			entity.addShopItem(dailyShopItem);
		}

		entity.setShopItemMapStr(GsonUtil.getJsonInstance().toJson(entity.getRaceShopMap()));
		if (!isCreate) {
			entity.notifyUpdate();
		}

		return ItemList;
	}

	@Override
	public void sendShopBuilder(Player player) {

		RaceShopEntity entity = player.getPlayerData().loadRaceShopEntity();

		// 商城构建
		Map<Integer, DailyShopItem> RaceMap = entity.getRaceShopMap();
		int refreshPrice = 0;

		ShopItemInfoResponse.Builder builder = this.createResponse(refreshPrice);
		
		
		// 商城物品构建
		for (DailyShopItem item : RaceMap.values()) {
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

	@Override
	public void shopBuyLogic(Player player, BuyShopItemsRequest request) {

		int buyShopId = request.getId();
		
		ShopType shoptype = request.getShopType();
		
		if (shoptype != getShopType()) {
			player.sendError(HP.code.SHOP_BUY_S_VALUE, Status.error.CONFIG_NOT_FOUND_VALUE);
			return;
		}

		RaceShopEntity ShopEntity = player.getPlayerData().loadRaceShopEntity();
		

		DailyShopItem shopItem = ShopEntity.getRaceShopMap().get(buyShopId);
		
		if (shopItem == null) {
			player.sendError(HP.code.SHOP_BUY_S_VALUE, Status.error.CONFIG_NOT_FOUND_VALUE);
			return;
		}
		
		int amount = request.getAmount();
				
		// 商城物品是否有存貨
		if ((shopItem.getLeft() == 0) || (amount > shopItem.getLeft()) ||  (amount <= 0))  {
			player.sendError(HP.code.SHOP_BUY_C_VALUE, Status.error.CONFIG_NOT_FOUND_VALUE);
			return;
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
		shopItem.setLeft(shopItem.getLeft()-amount);
		
		ShopEntity.SaveShopItem();

		ShopEntity.notifyUpdate(true);
		

		player.getPlayerData().syncPlayerInfo();

		// 构建返回数据包
		BuyShopItemsResponse.Builder builder = this.createResponse();
		for (DailyShopItem item : ShopEntity.getRaceShopMap().values()) {
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
		
		player.sendProtocol(Protocol.valueOf(HP.code.SHOP_BUY_S_VALUE, builder));

		QuestEventBus.fireQuestEventOneTime(QuestEventType.COMMON_SHOP_BUY_TIMES, player.getXid());
	}
	
	public List<RaceShopCfg> getItemList() {
		List<RaceShopCfg> ShopCfgs = new ArrayList<RaceShopCfg>();
		if (SysBasicCfg.getInstance().getRaceItemList().size() != RaceShopCfg.getGroupbyCfg().size()) {
			throw new RuntimeException("SysBasicCfg's RaceItemList size not match RaceShopCfg's random gourp");
			//return ShopCfgs;
		}
		int group = 0;
		for (Integer randNumber :SysBasicCfg.getInstance().getRaceItemList()) {
			ShopCfgs.addAll(GuaJiRand.randonWeightObject(RaceShopCfg.getGroupbyCfg().get(group), RaceShopCfg.getGroupbyWeight().get(group), randNumber));
			group++;
		}
		return ShopCfgs;
	}

	@Override
	public ShopType getShopType() {
		return Const.ShopType.RACE_MARKET;
	}
		
}
