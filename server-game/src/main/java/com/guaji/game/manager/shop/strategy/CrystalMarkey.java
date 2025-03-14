package com.guaji.game.manager.shop.strategy;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.guaji.config.ConfigManager;
import org.guaji.net.protocol.Protocol;
import org.guaji.os.GuaJiRand;

import com.guaji.game.config.CrystalShopCfg;
import com.guaji.game.config.VipPrivilegeCfg;
import com.guaji.game.entity.CrystalShopEntity;
import com.guaji.game.entity.CrystalShopItem;
import com.guaji.game.item.AwardItems;
import com.guaji.game.item.ConsumeItems;
import com.guaji.game.log.BehaviorLogger;
import com.guaji.game.log.BehaviorLogger.Action;
import com.guaji.game.log.BehaviorLogger.Params;
import com.guaji.game.manager.shop.ShopScervice;
import com.guaji.game.module.activity.monthcard.MonthCardStatus;
import com.guaji.game.module.quest.QuestEventBus;
import com.guaji.game.player.Player;
import com.guaji.game.util.ActivityUtil;
import com.guaji.game.util.BuilderUtil;
import com.guaji.game.util.GsonUtil;
import com.guaji.game.protocol.Const;
import com.guaji.game.protocol.Const.DataType;
import com.guaji.game.protocol.Const.QuestEventType;
import com.guaji.game.protocol.Const.ShopType;
import com.guaji.game.protocol.Const.changeType;
import com.guaji.game.protocol.HP;
import com.guaji.game.protocol.Shop.BuyShopItemsRequest;
import com.guaji.game.protocol.Shop.BuyShopItemsResponse;
import com.guaji.game.protocol.Shop.PushShopRedPoint;
import com.guaji.game.protocol.Shop.ShopItemInfoInit;
import com.guaji.game.protocol.Shop.ShopItemInfoRequest;
import com.guaji.game.protocol.Shop.ShopItemInfoResponse;
import com.guaji.game.protocol.Status;

/**
 * 水晶与普通商店合并
 * 
 * @author zdz
 *
 */
public class CrystalMarkey extends ShopScervice<CrystalShopCfg> {

	private static final CrystalMarkey instance = new CrystalMarkey();

	public static CrystalMarkey getInstance() {
		return instance;
	}

	/**
	 * 商店物品初始化
	 * 
	 * @param player
	 * @param entity
	 * @param luckyScore
	 */
	public List<CrystalShopCfg> shopItemInfo(Player player, CrystalShopEntity entity,boolean isCreate) {

		entity.getCrystalShopMap().clear();
		// vip等级对应物品数量
		int showItemCount = this.getMaxShopItemCount(player.getVipLevel());
		// 等级对应物品集
		List<CrystalShopCfg> crystalShopList = CrystalShopCfg.getCrystalShopCfgs(player.getLevel());
		// 商品集合clone
		List<CrystalShopCfg> crystalShopCfgsClone = new ArrayList<CrystalShopCfg>(crystalShopList);
		// 活动商品添加
		Iterator<CrystalShopCfg> it = crystalShopCfgsClone.iterator();
		while (it.hasNext()) {
			CrystalShopCfg cfg = it.next();
			if (cfg.getActivityId() != 0) {
				if (!ActivityUtil.isActivityOpen(cfg.getActivityId())) {
					it.remove();
				}
			}
		}

		// 商城显示集合
		List<CrystalShopCfg> crystalItemList = this.getItemList(crystalShopCfgsClone, showItemCount);

		for (int i = 0; i < crystalItemList.size(); i++) {
			CrystalShopItem crystalShopItem = CrystalShopCfg.getCrystalShopInfo(crystalItemList.get(i), i + 1);
			entity.addShopItem(crystalShopItem);
		}

		entity.setShopItemMapStr(GsonUtil.getJsonInstance().toJson(entity.getCrystalShopMap()));
		if (!isCreate) {
			entity.notifyUpdate();
		}

		return crystalItemList;
	}

	@Override
	public void sendShopBuilder(Player player) {

		CrystalShopEntity entity = player.getPlayerData().loadCrystalShopEntity();

		// 商城构建
		Map<Integer, CrystalShopItem> crystalMap = entity.getCrystalShopMap();
		int refreshPrice = entity.getNextRefreshCost();

		ShopItemInfoResponse.Builder builder = this.createResponse(refreshPrice);

		int lucky = entity.getLucky();
		builder.addData(this.createDisplayData(DataType.CRYSTAL_CHANGE, lucky));
		// 商城物品构建
		for (CrystalShopItem item : crystalMap.values()) {
			int price = item.getCostCount();
			int discount = item.getDiscount();
			if (discount != 0) {
				price = (price * discount) / 100;
			}
			ShopItemInfoInit.Builder iBuilder = this.createItemInfo(item.getId(), item.getItemId(), item.getItemType(), item.getItemCount(),
					changeType.valueOf(item.getCostType()), price, item.getDiscount(), false);
			builder.addItemInfo(iBuilder);
		}
		player.sendProtocol(Protocol.valueOf(HP.code.SHOP_ITEM_S_VALUE, builder));
	}

	@Override
	public void shopRefreshLogic(Player player,ShopItemInfoRequest request) {
		//水晶shop刷新移除
		// 是否记录刷新次数
		boolean isFlag = true;
		CrystalShopEntity crystalShopEntity = player.getPlayerData().loadCrystalShopEntity();
		int refreshCost = crystalShopEntity.getNextRefreshCost();
		// 每天有一次免费次数
		boolean canCostMonthCardFree = true;
		if (refreshCost == 0) {
			isFlag = false;
			crystalShopEntity.increaseRefreshCount();
			canCostMonthCardFree = false;
		}
		if (canCostMonthCardFree) {
			MonthCardStatus monthCardStatus = ActivityUtil.getMonthCardStatus(player.getPlayerData());
			if (monthCardStatus != null) {
				if (monthCardStatus.getLeftFreeRefreshShopTimes() > 0) {
					if (monthCardStatus.expendLeftFreeRefreshShopTimes(1)) {
						isFlag = false;
						player.getPlayerData().syncStateInfo();
						player.getPlayerData().updateActivity(Const.ActivityId.MONTH_CARD_VALUE, 0, true);
					}
				}
			}
		}

		if (isFlag) {
			ConsumeItems consumeItems = ConsumeItems.valueOf(changeType.CHANGE_GOLD, refreshCost);
			if (!consumeItems.checkConsume(player, HP.code.CRYSTAL_SHOP_REFRESH_C_VALUE)) {
				return;
			}
			consumeItems.consumeTakeAffect(player, Action.CRYSTAL_SHOP_REFRESH);
		}

		this.shopItemInfo(player, crystalShopEntity,false);

		crystalShopEntity.increaseLuckyValue(refreshCost);

		if (isFlag) {
			crystalShopEntity.increaseRefreshCount();
		}

		this.sendShopBuilder(player);

		crystalShopEntity.notifyUpdate();

		BehaviorLogger.log4Platform(player, Action.CRYSTAL_SHOP_REFRESH, Params.valueOf("refreshCost", refreshCost));

	}

	@Override
	public void shopBuyLogic(Player player, BuyShopItemsRequest request) {

		int buyShopId = request.getId();
		int type = request.getType();

		CrystalShopEntity crystalShopEntity = player.getPlayerData().loadCrystalShopEntity();

		// 筛选要购买的物品数据
		Map<Integer, CrystalShopItem> removeMap = new HashMap<>();
		if (type == 2) {
			removeMap = crystalShopEntity.getCrystalShopMap();
		} else {
			CrystalShopItem shop = crystalShopEntity.getCrystalShopMap().get(buyShopId);
			if (shop == null) {
				player.sendError(HP.code.SHOP_BUY_S_VALUE, Status.error.CONFIG_NOT_FOUND_VALUE);
				return;
			}

			removeMap.put(shop.getId(), shop);
		}

		// 商城物品是否存在
		if (removeMap.size() == 0) {
			player.sendError(HP.code.SHOP_BUY_C_VALUE, Status.error.CONFIG_NOT_FOUND_VALUE);
			return;
		}
		// 消耗总货币
		int totalGold = 0;
		int totalCoin = 0;
		int totalCrystal = 0;
		int price = 0;
		AwardItems awardItems = new AwardItems();

		for (CrystalShopItem shopItem : removeMap.values()) {
			// 扣除统计
			price = shopItem.getCostCount() * shopItem.getDiscount() / 100;
			if (shopItem.getCostType() == changeType.CHANGE_COIN_VALUE) {
				totalCoin += price;// 不能直接用原始价格，costCount，要用折扣价格，表里配置的为原始价格和打折数
			} else if (shopItem.getCostType() == changeType.CHANGE_GOLD_VALUE) {
				totalGold += price;
			} else {
				totalCrystal += price;
			}
			// 购买的物品
			awardItems.addItem(shopItem.getItemType(), shopItem.getItemId(), shopItem.getItemCount());
		}
		// 扣除购买消耗---金币 钻石 水晶
		ConsumeItems consumeItems = new ConsumeItems();
		if (totalCoin > 0) {
			consumeItems.addChangeInfo(changeType.CHANGE_COIN, totalCoin);
		}
		if (totalGold > 0) {
			consumeItems.addChangeInfo(changeType.CHANGE_GOLD, totalGold);
		}
		if (totalCrystal > 0) {
			consumeItems.addChangeInfo(changeType.CHANGE_CRYSTAL, totalCrystal);
		}
		// 消耗数据校验
		if (!consumeItems.checkConsume(player, HP.code.SHOP_BUY_C_VALUE)) {
			return;
		}
		// 扣除消耗数据
		consumeItems.consumeTakeAffect(player, Action.SHOP_BUY_TOOLS);

		awardItems.rewardTakeAffectAndPush(player, Action.SHOP_BUY_TOOLS, 1);

		for (CrystalShopItem shopItem : removeMap.values()) {
			BehaviorLogger.log4Platform(player, Action.SHOP_BUY_TOOLS, Params.valueOf("itemId", shopItem.getItemId()),
					Params.valueOf("itemCount", shopItem.getItemCount()), Params.valueOf("itemType", shopItem.getItemType()),
					Params.valueOf("buyType", shopItem.getCostType()),
					Params.valueOf("buyPrice", shopItem.getCostCount() * shopItem.getDiscount() / 100));
		}

		// 清理购买缓存
		crystalShopEntity.removeShopItem(removeMap);

		if (crystalShopEntity.getCrystalShopMap().size() == 0) {
			this.shopItemInfo(player, crystalShopEntity,false);
		} else {
			crystalShopEntity.notifyUpdate(true);
		}

		player.getPlayerData().syncPlayerInfo();

		// 构建返回数据包
		BuyShopItemsResponse.Builder builder = this.createResponse();
		for (CrystalShopItem item : crystalShopEntity.getCrystalShopMap().values()) {
			ShopItemInfoInit.Builder itemBuider = this.createItemInfo(item.getId(), item.getItemId(), item.getItemType(), item.getItemCount(),
					changeType.valueOf(item.getCostType()), item.getCostCount() * item.getDiscount() / 100, item.getDiscount(), false);
			builder.addItemInfo(itemBuider);
		}

		player.sendProtocol(Protocol.valueOf(HP.code.SHOP_BUY_S_VALUE, builder));

		QuestEventBus.fireQuestEventOneTime(QuestEventType.COMMON_SHOP_BUY_TIMES, player.getXid());

	}

	@Override
	public List<Integer> getTotalWeight(List<CrystalShopCfg> itemList) {
		List<Integer> list = new ArrayList<>();
		for (CrystalShopCfg cfg : itemList) {
			list.add(cfg.getWeight());
		}
		return list;
	}

	@Override
	public List<CrystalShopCfg> getItemList(List<CrystalShopCfg> itemList, int randNumber) {

		List<CrystalShopCfg> allianceShopCfgs = new ArrayList<CrystalShopCfg>();
		// 权重集合
		List<Integer> totalWeight = this.getTotalWeight(itemList);

		allianceShopCfgs.addAll(GuaJiRand.randonWeightObject(itemList, totalWeight, randNumber));
		return allianceShopCfgs;
	}

	/**
	 * 取得根据vip等级取得角色商店物品数量
	 * 
	 * @param vipLievel
	 * @return
	 */
	private int getMaxShopItemCount(int vipLievel) {
		VipPrivilegeCfg vipCfg = ConfigManager.getInstance().getConfigByKey(VipPrivilegeCfg.class, vipLievel);
		if (vipCfg == null) {
			return 0;
		}
		return vipCfg.getShopItemCount();
	}

	@Override
	public ShopType getShopType() {
		return Const.ShopType.CRYSTAL_MARKET;
	}
	
	/**
	 * 登陆的时候推送红点
	 */
	public void onPlayerLogin(Player player) {
		CrystalShopEntity crystalShopEntity = player.getPlayerData().loadCrystalShopEntity();
		if (null != crystalShopEntity) {
			int refreshCost = crystalShopEntity.getNextRefreshCost();
			// 推送红点
			if (refreshCost == 0) {
//				BuilderUtil.sendShopRedPoint(player,ShopType.CRYSTAL_MARKET,true);
			}
		}
	}
	
}
