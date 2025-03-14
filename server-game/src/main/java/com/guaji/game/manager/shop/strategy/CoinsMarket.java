package com.guaji.game.manager.shop.strategy;

import org.guaji.config.ConfigManager;
import org.guaji.msg.Msg;
import org.guaji.net.protocol.Protocol;
import org.guaji.os.GuaJiRand;
import org.guaji.xid.GuaJiXID;

import com.guaji.game.GsApp;
import com.guaji.game.config.ShopBuyCoinCfg;
import com.guaji.game.config.ShopBuyCoinLevelCfg;
import com.guaji.game.config.SysBasicCfg;
import com.guaji.game.config.VipPrivilegeCfg;
import com.guaji.game.entity.ShopEntity;
import com.guaji.game.item.AwardItems;
import com.guaji.game.item.ConsumeItems;
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
import com.guaji.game.protocol.Shop.ShopItemInfoRequest;
import com.guaji.game.protocol.Shop.ShopItemInfoResponse;
import com.guaji.game.util.GsConst;
import com.guaji.game.util.TapDBUtil.TapDBSource;
import com.guaji.game.protocol.Status;

/**
 * 购买金币商店
 */
public class CoinsMarket extends ShopScervice<ShopItemInfoRequest> {

	/**
	 * 购买金币商店单例
	 */
	private static final CoinsMarket instance = new CoinsMarket();

	public static CoinsMarket getInstance() {
		return instance;
	}

	@Override
	public void sendShopBuilder(Player player) {

		// 查找VIP配置数据
		VipPrivilegeCfg vipCfg = ConfigManager.getInstance().getConfigByKey(VipPrivilegeCfg.class, player.getVipLevel());
		int buyCount = vipCfg.getBuyCoinTimes();
		if (buyCount == 0) {
			player.sendError(HP.code.SHOP_ITEM_S_VALUE, Status.error.DATA_NOT_FOUND_VALUE);
			return;
		}
		// 查找与等级相关配置
		ShopEntity entity = player.getPlayerData().getShopEntity();
		ShopBuyCoinLevelCfg levelCfg = ConfigManager.getInstance().getConfigByKey(ShopBuyCoinLevelCfg.class, player.getLevel());
		if (levelCfg == null) {
			player.sendError(HP.code.SHOP_ITEM_S_VALUE, Status.error.DATA_NOT_FOUND_VALUE);
			return;
		}
		// 查找与等级相关配置
		int key = entity.getBuyCoinCount() + 1 > buyCount ? buyCount : entity.getBuyCoinCount() + 1;
		ShopBuyCoinCfg shopCfg = ConfigManager.getInstance().getConfigByKey(ShopBuyCoinCfg.class, key);
		if (shopCfg == null) {
			player.sendError(HP.code.SHOP_ITEM_S_VALUE, Status.error.DATA_NOT_FOUND_VALUE);
			return;
		}
		// 商城构建
		ShopItemInfoResponse.Builder builder = this.createResponse(0);
		// 当前商店需显示的数据--钻石
		int canBuyTimes = buyCount - entity.getBuyCoinCount();
		builder.addData(this.createDisplayData(DataType.CAN_BUY_TIMES, canBuyTimes));
		// 商城物品构建
		int itemCount = shopCfg.getCoinFactor() * levelCfg.getGoldRatio();
		ShopItemInfoInit.Builder iBuilder = this.createItemInfo(1, 1002, 10000, itemCount, changeType.CHANGE_GOLD, shopCfg.getNeedGold(), 0, false);
		builder.addItemInfo(iBuilder);

		player.sendProtocol(Protocol.valueOf(HP.code.SHOP_ITEM_S_VALUE, builder));
	}

	@Override
	public void shopBuyLogic(Player player, BuyShopItemsRequest request) {

		ShopEntity entity = player.getPlayerData().getShopEntity();
		// 获取可购买次数
		VipPrivilegeCfg vipCfg = ConfigManager.getInstance().getConfigByKey(VipPrivilegeCfg.class, player.getVipLevel());
		int canBuyTimes = vipCfg.getBuyCoinTimes();
		if (canBuyTimes == 0) {
			player.sendError(HP.code.SHOP_BUY_S_VALUE, Status.error.DATA_NOT_FOUND_VALUE);
			return;
		}
		// 判断次数是否够
		if (entity.getBuyCoinCount() >= canBuyTimes) {
			player.sendError(HP.code.SHOP_BUY_S_VALUE, Status.error.SHOP_BUY_COIN_NUMS_LESS_VALUE);
			return;
		}
		// 购买金币和等级相关配置表
		ShopBuyCoinLevelCfg levelCfg = ConfigManager.getInstance().getConfigByKey(ShopBuyCoinLevelCfg.class, player.getLevel());
		if (levelCfg == null) {
			player.sendError(HP.code.SHOP_BUY_S_VALUE, Status.error.DATA_NOT_FOUND_VALUE);
			return;
		}
		// 购买次数
		int amount = 1;
		if (request.getType() == 2) {
			amount = canBuyTimes - entity.getBuyCoinCount();
		}
		// 消耗钻石和获取金币统计
		int totalGold = 0;
		int totalCoin = 0;
		// 购买次数
		int buyCount = entity.getBuyCoinCount();
		for (; amount > 0; amount--) {
			// 基础数据没有找到
			ShopBuyCoinCfg shopCfg = ConfigManager.getInstance().getConfigByKey(ShopBuyCoinCfg.class, buyCount + 1);
			if (null == shopCfg) {
				player.sendError(HP.code.SHOP_BUY_S_VALUE, Status.error.DATA_NOT_FOUND_VALUE);
				return;
			}
			totalGold += shopCfg.getNeedGold();
			totalCoin += shopCfg.getCoinFactor() * levelCfg.getGoldRatio();
			buyCount += 1;

		}
		// 扣除消耗构建---钻石
		ConsumeItems consumeItems = ConsumeItems.valueOf(changeType.CHANGE_GOLD, totalGold);
		// 消耗数据校验
		if (!consumeItems.checkConsume(player, HP.code.SHOP_BUY_S_VALUE)) {
			return;
		}

		// 扣除消耗数据
		consumeItems.consumeTakeAffect(player, Action.SHOP_BUY_COIN);
		BehaviorLogger.log4Platform(player, Action.SHOP_BUY_COIN, Params.valueOf("buyPrice", totalGold), Params.valueOf("buyCoins", totalCoin));
		// 增加购买次数
		entity.setBuyCoinCount(buyCount);
		entity.notifyUpdate(true);
		
		// 下发购买的物品
		// 使否觸發兩倍
		boolean isDouble = GuaJiRand.randPercentRate(SysBasicCfg.getInstance().getBuyCoinCriRate());
		totalCoin = isDouble? totalCoin*2 : totalCoin;
		
		AwardItems awardItems = new AwardItems();
		awardItems.addCoin(totalCoin);
		awardItems.rewardTakeAffectAndPush(player, Action.SHOP_BUY_COIN,1,TapDBSource.Shop_Buy_Coin
				,Params.valueOf("isDouble", isDouble)
				,Params.valueOf("addCoin", totalCoin));
		
		Msg hawkMsg = Msg.valueOf(GsConst.MsgType.DailyQuestMsg.MONEY_COLLETION,
				GuaJiXID.valueOf(GsConst.ObjType.PLAYER, player.getId()));
		hawkMsg.pushParam(1);
		GsApp.getInstance().postMsg(hawkMsg);
		
		// 构建返回数据包
		BuyShopItemsResponse.Builder builder = this.createResponse();
		canBuyTimes -= entity.getBuyCoinCount();
		builder.addData(this.createDisplayData(DataType.CAN_BUY_TIMES, canBuyTimes));
		builder.addIsDouble(isDouble);
		// 商城物品构建
		ShopBuyCoinCfg buyCoinCfg = ConfigManager.getInstance().getConfigByKey(ShopBuyCoinCfg.class, entity.getBuyCoinCount());
		if (buyCoinCfg != null) {
			int itemCount = buyCoinCfg.getCoinFactor() * levelCfg.getGoldRatio();
			ShopItemInfoInit.Builder iBuilder = this.createItemInfo(1, 1002, 10000, itemCount, changeType.CHANGE_GOLD, buyCoinCfg.getNeedGold(), 0,
					false);
			builder.addItemInfo(iBuilder);
		}

		player.sendProtocol(Protocol.valueOf(HP.code.SHOP_BUY_S_VALUE, builder));
	}

	@Override
	public ShopType getShopType() {
		return Const.ShopType.COINS_MARKET;
	}

}
