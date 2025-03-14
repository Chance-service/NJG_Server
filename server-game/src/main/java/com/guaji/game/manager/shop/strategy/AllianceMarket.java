package com.guaji.game.manager.shop.strategy;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.guaji.config.ConfigManager;
import org.guaji.net.GuaJiNetManager;
import org.guaji.net.protocol.Protocol;
import org.guaji.os.GuaJiTime;

import com.guaji.game.config.AllianceShopCfg;
import com.guaji.game.config.RoleRelatedCfg;
import com.guaji.game.config.SysBasicCfg;
import com.guaji.game.entity.AllianceEntity;
import com.guaji.game.entity.AllianceShopItem;
import com.guaji.game.entity.PlayerAllianceEntity;
import com.guaji.game.entity.PlayerEntity;
import com.guaji.game.entity.RoleEntity;
import com.guaji.game.item.AwardItems;
import com.guaji.game.item.ConsumeItems;
import com.guaji.game.log.BehaviorLogger;
import com.guaji.game.log.BehaviorLogger.Action;
import com.guaji.game.log.BehaviorLogger.Params;
import com.guaji.game.log.BehaviorLogger.Source;
import com.guaji.game.manager.AllianceManager;
import com.guaji.game.manager.ArenaManager;
import com.guaji.game.manager.shop.ShopScervice;
import com.guaji.game.player.Player;
import com.guaji.game.util.GsConst;
import com.guaji.game.util.GsonUtil;
import com.guaji.game.protocol.Const;
import com.guaji.game.protocol.Const.DataType;
import com.guaji.game.protocol.Const.ShopType;
import com.guaji.game.protocol.Const.changeType;
import com.guaji.game.protocol.HP;
import com.guaji.game.protocol.Shop.BuyShopItemsRequest;
import com.guaji.game.protocol.Shop.ShopItemInfoInit;
import com.guaji.game.protocol.Shop.ShopItemInfoRequest;
import com.guaji.game.protocol.Shop.ShopItemInfoResponse;
import com.guaji.game.protocol.Status;

/**
 * 公会商店--每天自动刷新一次
 */
public class AllianceMarket extends ShopScervice<AllianceShopCfg> {

	/**
	 * 公会商店单例
	 */
	private static final AllianceMarket instance = new AllianceMarket();

	public static AllianceMarket getInstance() {
		return instance;
	}

	/**
	 * 商店物品初始化
	 * 
	 * @param player
	 * @param entity
	 * @param luckyScore
	 */
	public List<AllianceShopCfg> shopItemInfo(Player player, PlayerAllianceEntity entity) {
		// 公会商店配置
		List<AllianceShopCfg> listCfg = ConfigManager.getInstance().getConfigList(AllianceShopCfg.class);
		if (listCfg == null) {
			return null;
		}
		if (entity == null) {
			return null;
		}
		int allianceId = entity.getAllianceId();
		if (allianceId == 0) {
			return null;
		}
		// 检查魂魄是否有刷新
		if (checkItemRefresh(player) || checkSoulRefresh(player)) {
			String shopItemsStr = refresh(player);
			entity.setShopItemsStr(shopItemsStr);
			entity.convert();
			entity.notifyUpdate(true);
		}
		Map<Integer, AllianceShopItem> itemMap = entity.getItemMap();
		List<AllianceShopCfg> showList = new ArrayList<AllianceShopCfg>();
		AllianceShopCfg soulCfg = null;
		Set<Integer> keySet = itemMap.keySet();
		for (Integer key : keySet) {
			AllianceShopItem item = itemMap.get(key);
			if (item == null) {
				continue;
			}
			AllianceShopCfg cfg = AllianceShopCfg.getItemInfo(item.getId());
			if (cfg.getItemType() / 10000 == Const.itemType.SOUL_VALUE) {
				soulCfg = cfg;
			} else {
				showList.add(cfg);
			}
		}
		if (soulCfg != null) {
			showList.add(0, soulCfg);
		}
		return showList;
	}

	@Override
	public void sendShopBuilder(Player player) {
		// 公会校验
		PlayerAllianceEntity entity = player.getPlayerData().getPlayerAllianceEntity();
		int allianceId = entity.getAllianceId();
		if (allianceId == 0) {
			player.sendError(HP.code.SHOP_ITEM_S_VALUE, Status.error.ALLIANCE_NO_JOIN);
			return;
		}
		AllianceEntity a_entity = AllianceManager.getInstance().getAlliance(allianceId);
		if (a_entity == null) {
			player.sendError(HP.code.SHOP_ITEM_S_VALUE, Status.error.ALLIANCE_NONEXISTENT);
			return;
		}
		// 商城构建
		int refreshCount = entity.getRefreshShopCount();
		int refreshPrice = SysBasicCfg.getInstance().getRefreshAllianceShopPrice(refreshCount);
		ShopItemInfoResponse.Builder builder = this.createResponse(refreshPrice);

		// 当前商店展示数据构建--贡献值 幸运值
		int contribution = entity.getContribution();
		builder.addData(this.createDisplayData(DataType.CONTRIBUTION_VALUE, contribution));

		// 商城物品构建
		List<AllianceShopCfg> itemList = this.shopItemInfo(player, entity);

		for (AllianceShopCfg cfg : itemList) {
			ShopItemInfoInit.Builder iBuilder = this.createItemInfo(cfg.getId(), cfg.getItemId(), cfg.getItemType(),
					cfg.getItemCount(), changeType.CHANGE_SMELT_VALUE, cfg.getContribution(), 0, false);
			builder.addItemInfo(iBuilder);
		}
		player.sendProtocol(Protocol.valueOf(HP.code.SHOP_ITEM_S_VALUE, builder));
	}

	@Override
	public void shopRefreshLogic(Player player,ShopItemInfoRequest request) {

		// 公会校验
		PlayerAllianceEntity pllianceEntity = player.getPlayerData().getPlayerAllianceEntity();
		int allianceId = pllianceEntity.getAllianceId();
		if (allianceId == 0) {
			player.sendError(HP.code.SHOP_ITEM_S_VALUE, Status.error.ALLIANCE_NO_JOIN);
			return;
		}
		AllianceEntity entity = AllianceManager.getInstance().getAlliance(allianceId);
		if (entity == null) {
			player.sendError(HP.code.SHOP_ITEM_S_VALUE, Status.error.ALLIANCE_NONEXISTENT);
			return;
		}
		// 去扣贡献并且添加幸运值;
		int refreshCount = pllianceEntity.getRefreshShopCount();
		int refreshPrice = SysBasicCfg.getInstance().getRefreshAllianceShopPrice(refreshCount);
		// 刷新消耗数据构建---贡献值
		ConsumeItems consumeItems = ConsumeItems.valueOf(changeType.CHANGE_CONTRIBUTION, refreshPrice);
		// 消耗数据校验
		if (!consumeItems.checkConsume(player, HP.code.SHOP_ITEM_S_VALUE)) {
			return;
		}
		// 扣除消耗数据
		consumeItems.consumeTakeAffect(player, Action.ALLIANCE_CONSUME_CONTRIBUTION);
		// 更新公会幸运值
		int luckyScore = entity.getLuckyScore() + GsConst.Alliance.REFRESH_SHOP_ADD_LUCKY;
		if (luckyScore > GsConst.Alliance.MAX_SHOP_LUCKY) {
			luckyScore = GsConst.Alliance.MAX_SHOP_LUCKY;
		}
		// 持久化公会实体
		entity.setLuckyScore(luckyScore);
		entity.notifyUpdate(true);
		// 持久化自己的公会数据
		pllianceEntity.setRefreshShopCount(pllianceEntity.getRefreshShopCount() + 1);
		// 商店数据初始化
		this.shopItemInfo(player, pllianceEntity);
		// 返回数据包
		this.sendShopBuilder(player);
	}

	@Override
	public void shopBuyLogic(Player player, BuyShopItemsRequest request) {

		PlayerAllianceEntity entity = player.getPlayerData().getPlayerAllianceEntity();
		int allianceId = entity.getAllianceId();
		if (allianceId == 0) {
			// 没有加入工会
			player.sendError(HP.code.SHOP_BUY_S_VALUE, Status.error.ALLIANCE_NO_JOIN);
			return;
		}
		AllianceEntity allianceEntity = AllianceManager.getInstance().getAlliance(allianceId);
		if (allianceEntity == null) {
			// 工会不存在
			player.sendError(HP.code.SHOP_BUY_S_VALUE, Status.error.ALLIANCE_NONEXISTENT);
			return;
		}

		// 获取商品数据
		AllianceShopCfg shopCfg = AllianceShopCfg.getItemInfo(request.getId());
		if (shopCfg == null) {
			player.sendError(HP.code.SHOP_BUY_S_VALUE, Status.error.DATA_NOT_FOUND);
			return;
		}
		//
		if (request.getAmount() <= 0) {
			player.sendError(HP.code.SHOP_BUY_S_VALUE, Status.error.PARAMS_INVALID);
			return;
		}
		int buyCount = request.getAmount();
		int consumeCount =buyCount*shopCfg.getContribution();

		if (buyCount>=100)
		{//外掛送錯誤參數鎖帳號5年
			Calendar calendar = GuaJiTime.getCalendar();
			calendar.add(Calendar.YEAR, 5);
			PlayerEntity playerEntity = null;
			playerEntity = player.getPlayerData().getPlayerEntity();
			playerEntity.setForbidenTime(calendar.getTime());
			playerEntity.notifyUpdate(false);
			// 从竞技场排行榜删除
			ArenaManager.getInstance().removeArenaRank(player.getPlayerData().getId());
			GuaJiNetManager.getInstance().addBlackIp(player.getIp());
			GuaJiNetManager.getInstance().addBlackDevice(player.getDevice());
			// 日志记录
			BehaviorLogger.log4GM(String.valueOf(player.getPlayerData().getId()), Source.GM_OPERATION, Action.GM_FORBIDEN, Params.valueOf("shop_buy_Count", buyCount), Params.valueOf("ip", player.getIp()));
			
			// 踢出玩家
			if (player != null) {
				player.kickout(Const.kickReason.LOGIN_FORBIDEN_VALUE);
			}
			player.sendError(HP.code.SHOP_BUY_S_VALUE, Status.error.PARAMS_INVALID);
			return;
		}
		
		// 购买消耗数据构建(贡献值)
		ConsumeItems consumeItems = ConsumeItems.valueOf(changeType.CHANGE_CONTRIBUTION,consumeCount);

		// 检查消耗
		if (!consumeItems.checkConsume(player, HP.code.SHOP_BUY_S_VALUE)) {
			return;
		}
		// 扣除消耗数据
		consumeItems.consumeTakeAffect(player, Action.ALLIANCE_CONSUME_CONTRIBUTION);

		AwardItems awardItems = new AwardItems();
		awardItems.addItem(shopCfg.getItemType(), shopCfg.getItemId(), shopCfg.getItemCount()*buyCount);
		awardItems.rewardTakeAffectAndPush(player, Action.ALLIANCE_CONSUME_CONTRIBUTION, 1);

		BehaviorLogger.log4Platform(player, Action.ALLIANCE_CONSUME_CONTRIBUTION,
				Params.valueOf("costContribution", consumeCount),
				Params.valueOf("awardItems", awardItems.toString()));

		// 返回数据包
		this.sendShopBuilder(player);
	}

	@Override
	public List<Integer> getTotalWeight(List<AllianceShopCfg> itemList) {
		List<Integer> list = new ArrayList<>();
		for (AllianceShopCfg cfg : itemList) {
			list.add(cfg.getWeight());
		}
		return list;
	}

	@Override
	public List<AllianceShopCfg> getItemList(List<AllianceShopCfg> itemList, int randNumber) {

		// List<AllianceShopCfg> allianceShopCfgs = new
		// ArrayList<AllianceShopCfg>();
		// // 权重集合
		// List<Integer> totalWeight = this.getTotalWeight(itemList);
		//
		// allianceShopCfgs.addAll(GuaJiRand.randonWeightObject(itemList,
		// totalWeight, randNumber));
		return itemList;
	}

	@Override
	public ShopType getShopType() {
		return Const.ShopType.ALLIANCE_MARKET;
	}

	/**
	 * 检测商店是否需要刷新 1.商店数据为空，则刷新新的商品 2.商店中魂魄收集齐，则投放新的商品 3.商店中道具有新的投放,则刷新新的商品
	 * 
	 * @param player
	 * @return
	 */
	private boolean checkSoulRefresh(Player player) {
		PlayerAllianceEntity allianceEntity = player.getPlayerData().getPlayerAllianceEntity();
		if (allianceEntity == null) {
			return false;
		}
		Map<Integer, AllianceShopItem> maps = allianceEntity.getItemMap();
		// 先检查当前魂魄是否收集完毕
		if (maps == null || maps.isEmpty()) {
			return true;
		}
		AllianceShopItem soulItem = null;
		Set<Integer> keys = maps.keySet();
		for (Integer key : keys) {
			AllianceShopItem item = maps.get(key);
			int type = item.getItemType() / 10000;
			if (type == Const.itemType.SOUL_VALUE) {
				soulItem = item;
				break;
			}
		}
		List<RoleEntity> roleEntities = player.getPlayerData().getRoleEntities();
		if (soulItem != null) {
			// 是否收集齐全
			RoleRelatedCfg cfg = ConfigManager.getInstance().getConfigByKey(RoleRelatedCfg.class, soulItem.getItemId());
			int limitCount = cfg.getLimitCount();
			RoleEntity role = null;
			for (RoleEntity entity : roleEntities) {
				if (entity.getItemId() == soulItem.getItemId()) {
					role = entity;
					break;
				}
			}
			// 玩家没有该武将
			if (role == null) {
				return true;
			}
			// 玩家收集齐全了这个武将，刷新商店
			if (role.getSoulCount() == limitCount) {
				return true;
			}
		} else {
			// 检查是否有符合条件的新的魂魄投放
			List<AllianceShopCfg> cfgs = ConfigManager.getInstance().getConfigList(AllianceShopCfg.class);
			if (cfgs == null) {
				return false;
			}
			for (AllianceShopCfg cfg : cfgs) {
				// 玩家没有该武将则表示需要显示该商品，以供玩家收集
				boolean isExist = false;
				// 如果玩家没有收集齐该商品，则表示需要显示该商品，以供玩家收集
				boolean isFull = false;
				int itemId = cfg.getItemId();
				RoleRelatedCfg roleCfg = ConfigManager.getInstance().getConfigByKey(RoleRelatedCfg.class, itemId);
				for (RoleEntity entity : roleEntities) {
					if (itemId == entity.getItemId()) {
						isExist = true;
						isFull = entity.getSoulCount() == roleCfg.getLimitCount();
						break;
					}
				}
				if (!isExist || !isFull) {
					return true;
				}
			}
		}
		return false;
	}

	/**
	 * 检查商品是否需要刷新 如果有新投放的商品，则刷新商店
	 * 
	 * @param player
	 * @return
	 */
	private boolean checkItemRefresh(Player player) {
		PlayerAllianceEntity allianceEntity = player.getPlayerData().getPlayerAllianceEntity();
		if (allianceEntity == null) {
			return false;
		}
		Map<Integer, AllianceShopItem> maps = allianceEntity.getItemMap();
		// 先检查当前魂魄是否收集完毕
		if (maps == null || maps.isEmpty()) {
			return true;
		}
		List<Integer> shopItemIds = new ArrayList<Integer>();
		Set<Integer> keys = maps.keySet();
		for (Integer key : keys) {
			AllianceShopItem item = maps.get(key);
			int type = item.getItemType() / 10000;
			if (type == Const.itemType.SOUL_VALUE) {
				continue;
			}
			shopItemIds.add(item.getItemId());
		}
		List<AllianceShopCfg> allCfgs = ConfigManager.getInstance().getConfigList(AllianceShopCfg.class);
		if (allCfgs.size() == 0) {
			return false;
		}
		List<Integer> cfgItemIds = new ArrayList<Integer>();
		for (AllianceShopCfg cfg : allCfgs) {
			int type = cfg.getItemType() / 10000;
			if (type == Const.itemType.SOUL_VALUE) {
				continue;
			}
			cfgItemIds.add(cfg.getItemId());
		}
		cfgItemIds.removeAll(shopItemIds);
		return cfgItemIds.size() > 0;
	}

	/**
	 * 橱窗陈列物品 1.如果魂魄类的商品满足刷新条件则刷新橱窗商品（a.如果当前魂魄收集齐，b.当前没有正在收集的魂魄）
	 * 2.如果其他类商品有新上商品则刷新橱窗商品
	 * 
	 * @param player
	 * @return
	 */
	private String refresh(Player player) {
		// 先增加所有的配置，然后在删除掉不符合条件的
		PlayerAllianceEntity allianceEntity = player.getPlayerData().getPlayerAllianceEntity();
		if (allianceEntity == null) {
			return "";
		}
		Map<Integer, AllianceShopItem> maps = allianceEntity.getItemMap();
		AllianceShopItem soulItem = null;
		Set<Integer> keys = maps.keySet();
		for (Integer key : keys) {
			AllianceShopItem item = maps.get(key);
			int type = item.getItemType() / 10000;
			if (type == Const.itemType.SOUL_VALUE) {
				soulItem = item;
				break;
			}
		}
		// TODO HA
		maps.clear();
		List<AllianceShopItem> soulItems = new ArrayList<AllianceShopItem>();
		List<AllianceShopCfg> cfgs = ConfigManager.getInstance().getConfigList(AllianceShopCfg.class);
		for (AllianceShopCfg cfg : cfgs) {
			AllianceShopItem item = new AllianceShopItem();
			item.setId(cfg.getId());
			item.setItemType(cfg.getItemType());
			item.setItemId(cfg.getItemId());
			item.setItemCount(cfg.getItemCount());
			item.setPrice(cfg.getContribution());
			if (cfg.getItemType() / 10000 == Const.itemType.SOUL_VALUE) {
				soulItems.add(item);
			}
			maps.put(cfg.getItemId(), item);
		}
		if (soulItems.isEmpty()) {
			return GsonUtil.getJsonInstance().toJson(maps);
		}
		List<RoleEntity> roleEntities = player.getPlayerData().getRoleEntities();
		int curSoulId = -1;
		if (soulItem != null) {
			// 该卡是否收集齐全，如果收集齐全则删除掉橱窗陈列项，如果未收集齐全，则保留该项目，将其他陈列项全部删除掉
			curSoulId = soulItem.getItemId();
		}

		// 已经集齐的必然删除掉
		List<AllianceShopItem> removeItems = new ArrayList<AllianceShopItem>();
		for (AllianceShopItem item : soulItems) {
			// TODO 检查玩家是否收集齐该项目
			int soulId = item.getItemId();
			for (RoleEntity role : roleEntities) {
				if (role.getItemId() != soulId) {
					continue;
				}
				RoleRelatedCfg roleCfg = ConfigManager.getInstance().getConfigByKey(RoleRelatedCfg.class, soulId);
				if (roleCfg.getLimitCount() == role.getSoulCount()) {
					// 如果收集齐，则不在向下执行
					removeItems.add(item);
				}
				break;
			}
		}
		// TODO 第一遍清理，清理的都是已经收集齐的
		for (AllianceShopItem item : removeItems) {
			maps.remove(item.getItemId());
			soulItems.remove(item);
		}
		if (soulItems.isEmpty()) {
			// 没有可收集的魂魄了
			return GsonUtil.getJsonInstance().toJson(maps);
		}
		// TODO 剩下的全是没收集全或者没收集过的魂魄，如果有没收集全的，则将其他的全部删掉
		removeItems = new ArrayList<AllianceShopItem>();
		for (AllianceShopItem item : soulItems) {
			if (item.getItemId() != curSoulId) {
				removeItems.add(item);
			}
		}
		for (AllianceShopItem item : removeItems) {
			maps.remove(item.getItemId());
		}
		if (removeItems.size() == soulItems.size()) {
			// 魂魄数据删除干净了。所以需要增加一个
			AllianceShopItem addItem = soulItems.get(0);
			maps.put(addItem.getItemId(), addItem);
		}
		return GsonUtil.getJsonInstance().toJson(maps);
	}
}
