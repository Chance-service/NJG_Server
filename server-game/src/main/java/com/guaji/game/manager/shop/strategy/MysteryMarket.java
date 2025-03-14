package com.guaji.game.manager.shop.strategy;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.guaji.config.ConfigManager;
import org.guaji.net.protocol.Protocol;
import org.guaji.os.GuaJiRand;
import org.guaji.os.GuaJiTime;

import com.guaji.game.config.MysteryShopCfg;
import com.guaji.game.config.ShopDiscountCfg;
import com.guaji.game.config.SysBasicCfg;
import com.guaji.game.entity.DailyShopItem;
import com.guaji.game.entity.MysteryShopEntity;
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
import com.guaji.game.protocol.Shop.ShopItemInfoRequest;
import com.guaji.game.protocol.Shop.ShopItemInfoResponse;
import com.guaji.game.protocol.Status;
import com.guaji.game.util.BuilderUtil;
import com.guaji.game.util.GsonUtil;
import com.guaji.game.util.ItemUtil;
import com.guaji.game.util.TapDBUtil.TapDBSource;

/**
 * 神秘商店
 * 
 * @author zdz
 *
 */
public class MysteryMarket extends ShopScervice<MysteryShopCfg> {

	private static final MysteryMarket instance = new MysteryMarket();

	public static MysteryMarket getInstance() {
		return instance;
	}

	/**
	 * 商店物品初始化
	 * 
	 * @param player
	 * @param entity
	 * @param luckyScore
	 */
	public List<MysteryShopCfg> shopItemInfo(Player player, MysteryShopEntity entity,boolean isCreate) {

		entity.getShopMap().clear();
		// vip等级对应物品数量
//		int showItemCount = this.getMaxShopItemCount();
//		// 等级对应物品集
//		List<MysteryShopCfg> mysteryShopList = MysteryShopCfg.getShopCfgs();
//		// 商品集合clone
//		List<MysteryShopCfg> MysteryShopCfgsClone = new ArrayList<MysteryShopCfg>(mysteryShopList);

		// 商城显示集合
		List<MysteryShopCfg> mysteryItemList = new ArrayList<>();
		mysteryItemList = this.getItemList();

		for (int i = 0; i < mysteryItemList.size(); i++) {
			DailyShopItem mysteryShopItem = MysteryShopCfg.getShopInfo(mysteryItemList.get(i), i + 1);
			entity.addShopItem(mysteryShopItem);
		}

		entity.setShopItemMapStr(GsonUtil.getJsonInstance().toJson(entity.getShopMap()));
		if (!isCreate) {
			entity.notifyUpdate();
		}

		return mysteryItemList;
	}

	@Override
	public void sendShopBuilder(Player player) {

		MysteryShopEntity entity = player.getPlayerData().loadMysteryShopEntity();

		// 商城构建
		Map<Integer, DailyShopItem> mysteryMap = entity.getShopMap();
		
		int times = SysBasicCfg.getInstance().getMysteryMaxRefresh()-entity.getCostRefresh();
				
		int refreshPrice = SysBasicCfg.getInstance().getMysteryRefreshList(times);

		ShopItemInfoResponse.Builder builder = this.createResponse(refreshPrice);
		builder.setFreeRefresh(entity.getFreeRefresh());
		builder.setCostRefresh(entity.getCostRefresh());
		builder.setRefreshTime(entity.getRefreshTime());

		// 商城物品构建
		for (DailyShopItem item : mysteryMap.values()) {
			MysteryShopCfg aCfg = MysteryShopCfg.getShopCfgById(item.getCfgIndex());
			ShopItemInfoInit.Builder iBuilder = this.TransDiscountInfo(player,item,aCfg);
			builder.addItemInfo(iBuilder);
			builder.addData(this.createDisplayData(DataType.ITEM_LEFT,item.getLeft()));
		}
		player.sendProtocol(Protocol.valueOf(HP.code.SHOP_ITEM_S_VALUE, builder));
	}

	@Override
	public void shopRefreshLogic(Player player,ShopItemInfoRequest request) {
		
		//reType 0.免費刷新 1.收費刷新
		int reType = request.getRefreshType();
		
		MysteryShopEntity MysteryShopEntity = player.getPlayerData().loadMysteryShopEntity();
		int freeTime = MysteryShopEntity.getFreeRefresh();
		int costTime = MysteryShopEntity.getCostRefresh();
		
		if ((reType == 0) && (freeTime <= 0)) {
			return;
		}
		
		if ((reType == 1) && (freeTime > 0)) {
			return;
		}
		
		if (freeTime > 0) {
			int currSeconds = GuaJiTime.getSeconds();
			MysteryShopEntity.decFreeRefresh();
			if (MysteryShopEntity.getFreeRefresh() == 0) {
				BuilderUtil.sendShopRedPoint(player,ShopType.MYSTERY_MARKET,false);
			}
			if (MysteryShopEntity.getRefreshTime() == 0) {
				MysteryShopEntity.setRefreshTime(currSeconds);
			}
		} else if (costTime > 0) {
			
			int times = SysBasicCfg.getInstance().getMysteryMaxRefresh()- MysteryShopEntity.getCostRefresh();
			
			int refreshPrice = SysBasicCfg.getInstance().getMysteryRefreshList(times);

			ConsumeItems consumeItems = ConsumeItems.valueOf(changeType.CHANGE_GOLD, refreshPrice);
			if (!consumeItems.checkConsume(player, HP.code.CRYSTAL_SHOP_REFRESH_C_VALUE)) {
				return;
			}
			
			MysteryShopEntity.decCostRefresh();
			consumeItems.consumeTakeAffect(player, Action.CRYSTAL_SHOP_REFRESH);
			BehaviorLogger.log4Platform(player, Action.CRYSTAL_SHOP_REFRESH, Params.valueOf("refreshPrice", refreshPrice));
		} else {
			return;
		}	

		this.shopItemInfo(player, MysteryShopEntity,false);

		this.sendShopBuilder(player);

		MysteryShopEntity.notifyUpdate();

	}

	@Override
	public void shopBuyLogic(Player player, BuyShopItemsRequest request) {

		int buyShopId = request.getId();
		//int type = request.getType(); 只有單買
		
		ShopType shoptype = request.getShopType();
		
		if (shoptype != getShopType()) {
			player.sendError(HP.code.SHOP_BUY_S_VALUE, Status.error.CONFIG_NOT_FOUND_VALUE);
			return;
		}

		MysteryShopEntity mysteryShopEntity = player.getPlayerData().loadMysteryShopEntity();


		DailyShopItem shopItem = mysteryShopEntity.getShopMap().get(buyShopId);
		if (shopItem == null) {
			player.sendError(HP.code.SHOP_BUY_S_VALUE, Status.error.CONFIG_NOT_FOUND_VALUE);
			return;
		}
		
		MysteryShopCfg shopCfg = MysteryShopCfg.getShopCfgById(shopItem.getCfgIndex());
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

		awardItems.rewardTakeAffectAndPush(player, Action.SHOP_BUY_TOOLS, 1,TapDBSource.Mystery_Shop,Params.valueOf("id",shopCfg.getId()));

		BehaviorLogger.log4Platform(player, Action.SHOP_BUY_TOOLS, Params.valueOf("itemId", shopItem.getItemId()),
				Params.valueOf("itemCount", shopItem.getItemCount()), Params.valueOf("itemType", shopItem.getItemType()),
				Params.valueOf("PirecStr", shopItem.getPriceStr()),
				Params.valueOf("Amount", amount));

		// 扣除庫存
		shopItem.setLeft(shopItem.getLeft()-amount);
		
		mysteryShopEntity.SaveShopItem();

		mysteryShopEntity.notifyUpdate(true);
		

		player.getPlayerData().syncPlayerInfo();

		// 构建返回数据包
		BuyShopItemsResponse.Builder builder = this.createResponse();
		for (DailyShopItem item : mysteryShopEntity.getShopMap().values()) {
			MysteryShopCfg aCfg = MysteryShopCfg.getShopCfgById(item.getCfgIndex());
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
//	public List<Integer> getTotalWeight(List<MysteryShopCfg> itemList) {
//		List<Integer> list = new ArrayList<>();
//		for (MysteryShopCfg cfg : itemList) {
//			list.add(cfg.getWeight());
//		}
//		return list;
//	}
//
//	@Override
//	public List<MysteryShopCfg> getItemList(List<MysteryShopCfg> itemList, int randNumber) {
//
//		List<MysteryShopCfg> ShopCfgs = new ArrayList<MysteryShopCfg>();
//		// 权重集合
//		List<Integer> totalWeight = this.getTotalWeight(itemList);
//
//		ShopCfgs.addAll(GuaJiRand.randonWeightObject(itemList, totalWeight, randNumber));
//		return ShopCfgs;
//	}

	/**
	 * 取得根据vip等级取得角色商店物品数量
	 * 
	 * @param vipLievel
	 * @return
	 */
//	private int getMaxShopItemCount() {
//		return SysBasicCfg.getInstance().getMysteryItemCount();
//	}
	
	public List<MysteryShopCfg> getItemList() {
		List<MysteryShopCfg> ShopCfgs = new ArrayList<MysteryShopCfg>();
		if (SysBasicCfg.getInstance().getMysteryItemList().size() != MysteryShopCfg.getGroupbyCfg().size()) {
			throw new RuntimeException("SysBasicCfg's MysteryItemList size not match MysteryShopCfg's random gourp");
			//return ShopCfgs;
		}
		int group = 0;
		for (Integer randNumber :SysBasicCfg.getInstance().getMysteryItemList()) {
			ShopCfgs.addAll(GuaJiRand.randonWeightObject(MysteryShopCfg.getGroupbyCfg().get(group), MysteryShopCfg.getGroupbyWeight().get(group), randNumber));
			group++;
		}
		return ShopCfgs;
	}

	@Override
	public ShopType getShopType() {
		return Const.ShopType.MYSTERY_MARKET;
	}
	
	/**
	 * 登陆的时候計算新增免費次數
	 */
	public void onPlayerLogin(Player player) {
		MysteryShopEntity mysteryShopEntity = player.getPlayerData().loadMysteryShopEntity();
		if (null != mysteryShopEntity) {
			
			int refreshTime = mysteryShopEntity.getRefreshTime();
			int currSeconds = GuaJiTime.getSeconds();
			if ((refreshTime > 0) && (currSeconds >= refreshTime)) {
				if (mysteryShopEntity.getFreeRefresh() < SysBasicCfg.getInstance().getMysteryMaxFreeRefresh()) {
					int aTime = currSeconds - refreshTime;
					if (aTime >= SysBasicCfg.getInstance().getMysteryRefreshTime()) {
						int inc = aTime / SysBasicCfg.getInstance().getMysteryRefreshTime();
						mysteryShopEntity.incFreeRefresh(inc);
						if (mysteryShopEntity.getFreeRefresh() >= SysBasicCfg.getInstance().getMysteryMaxFreeRefresh()) {
							mysteryShopEntity.setRefreshTime(0);
						} else {
							int lefttime = aTime % SysBasicCfg.getInstance().getMysteryRefreshTime();
							int newtime = currSeconds-lefttime; //減回經過但沒超過兩小時的時間
							mysteryShopEntity.setRefreshTime(newtime);
						} 
					}
				}
			}
			if ((mysteryShopEntity.getFreeRefresh() > 0)) {
				BuilderUtil.sendShopRedPoint(player,ShopType.MYSTERY_MARKET,true);
			}
		}
	}
	
	public ShopItemInfoInit.Builder TransDiscountInfo(Player player,DailyShopItem item,MysteryShopCfg cfg) {
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
			itemBuider.setVip(cfg.getVip());
			
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
