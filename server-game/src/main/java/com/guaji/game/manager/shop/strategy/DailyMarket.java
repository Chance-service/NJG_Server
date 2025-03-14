package com.guaji.game.manager.shop.strategy;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.guaji.config.ConfigManager;
import org.guaji.net.protocol.Protocol;
import org.guaji.os.GuaJiRand;

import com.guaji.game.config.ArenaShopCfg;
import com.guaji.game.config.DailyShopCfg;
import com.guaji.game.config.ShopDiscountCfg;
import com.guaji.game.config.SysBasicCfg;
import com.guaji.game.entity.DailyShopEntity;
import com.guaji.game.entity.DailyShopItem;
import com.guaji.game.entity.ShopDiscountEntity;
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
import com.guaji.game.util.ItemUtil;
import com.guaji.game.util.TapDBUtil.TapDBSource;

/**
 * 每日商店
 * 
 * @author zdz
 *
 */
public class DailyMarket extends ShopScervice<DailyShopCfg> {

	private static final DailyMarket instance = new DailyMarket();

	public static DailyMarket getInstance() {
		return instance;
	}

	/**
	 * 商店物品初始化
	 * 
	 * @param player
	 * @param entity
	 * @param luckyScore
	 */
	public List<DailyShopCfg> shopItemInfo(Player player, DailyShopEntity entity,boolean isCreate) {

		entity.getDailyShopMap().clear();
		// vip等级对应物品数量
		//int showItemCount = this.getMaxShopItemCount();
		// 等级对应物品集
		//List<DailyShopCfg> dailyShopList = DailyShopCfg.getDailyShopCfgs();
		// 商品集合clone
		//List<DailyShopCfg> DailyShopCfgsClone = new ArrayList<DailyShopCfg>(dailyShopList);

		// 商城显示集合
		List<DailyShopCfg> DailyItemList = new ArrayList<>();
		DailyItemList = this.getItemList();

		for (int i = 0; i < DailyItemList.size(); i++) {
			DailyShopItem dailyShopItem = DailyShopCfg.getDailyShopInfo(DailyItemList.get(i), i + 1);
			entity.addShopItem(dailyShopItem);
		}

		entity.setShopItemMapStr(GsonUtil.getJsonInstance().toJson(entity.getDailyShopMap()));
		if (!isCreate) {
			entity.notifyUpdate();
		}

		return DailyItemList;
	}

	@Override
	public void sendShopBuilder(Player player) {

		DailyShopEntity entity = player.getPlayerData().loadDailyShopEntity();

		// 商城构建
		Map<Integer, DailyShopItem> dailyMap = entity.getDailyShopMap();
		int refreshPrice = 0;//entity.getNextRefreshCost();

		ShopItemInfoResponse.Builder builder = this.createResponse(refreshPrice);
		
		
		// 商城物品构建
		for (DailyShopItem item : dailyMap.values()) {
			DailyShopCfg aCfg = DailyShopCfg.getShopCfgById(item.getCfgIndex());
			ShopItemInfoInit.Builder iBuilder = this.TransDiscountInfo(player,item,aCfg);
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

		DailyShopEntity dailyShopEntity = player.getPlayerData().loadDailyShopEntity();


		DailyShopItem shopItem = dailyShopEntity.getDailyShopMap().get(buyShopId);
		
		if (shopItem == null) {
			player.sendError(HP.code.SHOP_BUY_S_VALUE, Status.error.CONFIG_NOT_FOUND_VALUE);
			return;
		}
		
		DailyShopCfg shopCfg = DailyShopCfg.getShopCfgById(shopItem.getCfgIndex());
		if (shopCfg == null) {
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
		
		String PriceStr = "";
		
		for (int i = 0 ; i < amount ; i ++) {
			if (PriceStr.isEmpty()) {
				PriceStr = shopItem.getPriceStr();
			} else {
				PriceStr = PriceStr + "," + shopItem.getPriceStr();
			}
		}
		
		List<ItemInfo> TmpItems = ItemInfo.valueListOf(PriceStr);
		
		ShopDiscountCfg discountCfg = null;
		ShopDiscountEntity discountEntity = null;
		
		int shopcount = -1;
		
		if (shopCfg.getDcgroup() == 0) {
			shopcount = shopCfg.getCount() - shopItem.getLeft();
		} else { // 使用group discount
			discountCfg = ConfigManager.getInstance().getConfigByKey(ShopDiscountCfg.class,shopCfg.getDcgroup());
			
			discountEntity = player.getPlayerData().loadShopDiscountEntity();
			
			if ((discountCfg != null)&&(discountEntity != null)) {
				shopcount = discountEntity.getShopCount(discountCfg.getId()); 
			}
		}
		
		float discount = (shopCfg.getDcgroup() == 0) ? shopCfg.getDiscountByCount(shopcount) : discountCfg.getDiscountByCount(shopcount);
		for (ItemInfo aItem : TmpItems) {
				ItemInfo need = ItemInfo.valueOf(aItem.getType(), aItem.getItemId(),(long)(aItem.getQuantity()*discount));
				needItems.add(need);
				shopcount++;
		}
			
		if (!consumeItems.addConsumeInfo(player.getPlayerData(), needItems)) {
			return;
		}

		// 消耗数据校验
		if (!consumeItems.checkConsume(player, HP.code.SHOP_BUY_C_VALUE)) {
			return;
		}
		
		if ((discountCfg != null)&&(discountEntity != null)&&(shopcount > 0)) {
			 discountEntity.setShopCount(discountCfg.getId(),shopcount);
			 discountEntity.notifyUpdate(true);
		}
		
		// 扣除消耗数据
		consumeItems.consumeTakeAffect(player, Action.SHOP_BUY_TOOLS);

		awardItems.rewardTakeAffectAndPush(player, Action.SHOP_BUY_TOOLS, 1,TapDBSource.Daily_Shop,Params.valueOf("id",shopCfg.getId()));

		BehaviorLogger.log4Platform(player, Action.SHOP_BUY_TOOLS, Params.valueOf("itemId", shopItem.getItemId()),
				Params.valueOf("itemCount", shopItem.getItemCount()), Params.valueOf("itemType", shopItem.getItemType()),
				Params.valueOf("PirecStr", shopItem.getPriceStr()),
				Params.valueOf("Amount", amount));

		// 扣除庫存
		shopItem.setLeft(shopItem.getLeft()-amount);
		
		dailyShopEntity.SaveShopItem();

		dailyShopEntity.notifyUpdate(true);
		

		player.getPlayerData().syncPlayerInfo();

		// 构建返回数据包
		BuyShopItemsResponse.Builder builder = this.createResponse();
		for (DailyShopItem item : dailyShopEntity.getDailyShopMap().values()) {
			DailyShopCfg aCfg = DailyShopCfg.getShopCfgById(item.getCfgIndex());
			if (aCfg != null) {
				ShopItemInfoInit.Builder itemBuider = this.TransDiscountInfo(player,item,aCfg);
				builder.addItemInfo(itemBuider);
				builder.addData(this.createDisplayData(DataType.ITEM_LEFT,item.getLeft()));
			}
		}

		player.sendProtocol(Protocol.valueOf(HP.code.SHOP_BUY_S_VALUE, builder));

		QuestEventBus.fireQuestEventOneTime(QuestEventType.COMMON_SHOP_BUY_TIMES, player.getXid());

	}
	
//	@Override
//	public List<Integer> getTotalWeight(List<DailyShopCfg> itemList) {
//		List<Integer> list = new ArrayList<>();
//		for (DailyShopCfg cfg : itemList) {
//			list.add(cfg.getWeight());
//		}
//		return list;
//	}
	
//	@Override
//	public List<DailyShopCfg> getItemList(List<DailyShopCfg> itemList) {
//
//		List<DailyShopCfg> ShopCfgs = new ArrayList<DailyShopCfg>();
//		// 权重集合
//		List<Integer> totalWeight = this.getTotalWeight(itemList);
//
//		ShopCfgs.addAll(GuaJiRand.randonWeightObject(itemList, totalWeight, randNumber));
//		return ShopCfgs;
//	}
	
	public List<DailyShopCfg> getItemList() {
		List<DailyShopCfg> ShopCfgs = new ArrayList<DailyShopCfg>();
		if (SysBasicCfg.getInstance().getDailyItemList().size() != DailyShopCfg.getGroupbyCfg().size()) {
			throw new RuntimeException("SysBasicCfg's DailyItemList size not match DailyShopCfg random gourp");
			//return ShopCfgs;
		}
		int group = 0;
		for (Integer randNumber :SysBasicCfg.getInstance().getDailyItemList()) {
			ShopCfgs.addAll(GuaJiRand.randonWeightObject(DailyShopCfg.getGroupbyCfg().get(group), DailyShopCfg.getGroupbyWeight().get(group), randNumber));
			group++;
		}
		return ShopCfgs;
	}

	/**
	 * 取得色商店物品数量
	 * 
	 * @param vipLievel
	 * @return
	 */
//	private int getMaxShopItemCount() {
//		return SysBasicCfg.getInstance().getDailyItemCount();
//	}

	@Override
	public ShopType getShopType() {
		return Const.ShopType.DAILY_MARKET;
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
	
	public ShopItemInfoInit.Builder TransDiscountInfo(Player player,DailyShopItem item,DailyShopCfg cfg) {
		ShopItemInfoInit.Builder itemBuider = null;
		if (cfg != null) {
			String[] es = cfg.getItem().split("_");
			ItemInfo info = ItemInfo.valueOf(cfg.getPrice());// 返回折扣信息，使用itemInfo对象
			int itemId = Integer.valueOf(es[1]);
			int itemType = Integer.valueOf(es[0]);
			int count = Integer.valueOf(es[2]);
			int type = ItemUtil.getChangeType(info);
			int price = (int)info.getQuantity();
			int discount = 100;
			itemBuider = this.createItemInfo(item.getId(),itemId,itemType,count,changeType.valueOf(type),price,discount,false);
			itemBuider.setTotalcount(cfg.getCount());
			itemBuider.setPriceStr(cfg.getPrice());
			
			ShopDiscountCfg discountCfg = ConfigManager.getInstance().getConfigByKey(ShopDiscountCfg.class,cfg.getDcgroup());
			ShopDiscountEntity discountEntity = player.getPlayerData().loadShopDiscountEntity();
			
			int shopcount = 0;
			float dis_f = 1.0f;
			int disprice = -1;
			if (cfg.getDcgroup() == 0) {
				shopcount =  cfg.getCount() - item.getLeft();
				dis_f = cfg.getDiscountByCount(shopcount);

			} else {
				shopcount = discountEntity.getShopCount(cfg.getDcgroup());
				dis_f = discountCfg.getDiscountByCount(shopcount);
			}
			
			disprice = (int)(dis_f*100);
			price = (int)(price*dis_f);
		
			itemBuider.setDiscont(disprice);
			info.setQuantity(price);
			itemBuider.setPrice(price);
			itemBuider.setPriceStr(info.toString());
		}
		return itemBuider;
	 }
}
