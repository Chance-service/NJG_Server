package com.guaji.game.module;

import java.util.Base64;
import java.util.Collection;
import java.util.Deque;
import java.util.LinkedList;
import java.util.Map;
import java.util.UUID;

import org.apache.http.HttpEntity;
import org.apache.http.HttpHeaders;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.guaji.app.App;
import org.guaji.config.ConfigManager;
import org.guaji.cryption.Md5;
import org.guaji.db.DBManager;
import org.guaji.log.Log;
import org.guaji.net.protocol.Protocol;
import org.guaji.os.GuaJiTime;
import org.guaji.os.MyException;

import com.guaji.game.config.ActivityTimeCfg;
import com.guaji.game.config.FunctionUnlockCfg;
import com.guaji.game.config.RechargeConfig;
import com.guaji.game.config.RechargeConfig.RechargeItem;
import com.guaji.game.config.RechargeRatioCfg;
import com.guaji.game.config.SysBasicCfg;
import com.guaji.game.entity.MysteryShopEntity;
import com.guaji.game.entity.RechargeEntity;
import com.guaji.game.entity.ShopEntity;
import com.guaji.game.entity.TempleShopEntity;
import com.guaji.game.manager.shop.ShopFactory;
import com.guaji.game.manager.shop.ShopScervice;
import com.guaji.game.manager.shop.strategy.CrystalMarkey;
import com.guaji.game.manager.shop.strategy.MysteryMarket;
import com.guaji.game.module.activity.activity191.Activity191Status;
import com.guaji.game.module.activity.recharge.FirstRechargeStatus;
import com.guaji.game.player.Player;
import com.guaji.game.player.PlayerModule;
import com.guaji.game.protocol.Const;
import com.guaji.game.protocol.Const.ShopType;
import com.guaji.game.protocol.HP;
import com.guaji.game.protocol.Recharge.HPFetchShopList;
import com.guaji.game.protocol.Recharge.HPShopListSync;
import com.guaji.game.protocol.Recharge.LastGoodsItem;
import com.guaji.game.protocol.Recharge.ShopItem;
import com.guaji.game.protocol.Shop.BuyShopItemsRequest;
import com.guaji.game.protocol.Shop.HoneyPBuyRequest;
import com.guaji.game.protocol.Shop.HoneyPBuyResponse;
import com.guaji.game.protocol.Shop.HoneyPRequest;
import com.guaji.game.protocol.Shop.HoneyPResponse;
import com.guaji.game.protocol.Shop.ShopItemInfoRequest;
import com.guaji.game.protocol.Shop.SixNineCoinTakeRequest;
import com.guaji.game.protocol.Shop.SixNineTakeResponse;
import com.guaji.game.protocol.Status;
import com.guaji.game.recharge.RechargeManager;
import com.guaji.game.recharge.RechargeParam;
import com.guaji.game.util.ActivityUtil;
import com.guaji.game.util.BuilderUtil;
import com.guaji.game.util.GJLocal;
import com.guaji.game.util.GsConst;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

/**
 * 商城模块
 */
public class PlayerShopModule extends PlayerModule {

	/**
	 * 构造函数
	 */
	public PlayerShopModule(Player player) {
		super(player);
		// 一般商店相關
		listenProto(HP.code.SHOP_ITEM_C_VALUE);
		listenProto(HP.code.SHOP_BUY_C_VALUE);
		// 儲值商城相關
		listenProto(HP.code.FETCH_SHOP_LIST_C_VALUE);
		listenProto(HP.code.LAST_SHOP_ITEM_C_VALUE);
		listenProto(HP.code.SHOP_HONEYP_C_VALUE);
		listenProto(HP.code.SHOP_HONEYP_BUY_C_VALUE);
		listenProto(HP.code.SHOP_69COIN_TAKE_C_VALUE);
	}

	@Override
	protected boolean onPlayerLogin() {
		// 登录捞取数据
		player.getPlayerData().loadShopData();
		player.getPlayerData().loadHonorShopEntity();
		// 杂货铺商店推送红点(如果有其他商店红点需求可以在Service接口层统一加登陆操作)
		CrystalMarkey crystalMarket = (CrystalMarkey) ShopFactory.getInstance().getShopHandler(ShopType.CRYSTAL_MARKET);
		crystalMarket.onPlayerLogin(player);
		MysteryMarket mysteryMarket = (MysteryMarket) ShopFactory.getInstance().getShopHandler(ShopType.MYSTERY_MARKET);
		mysteryMarket.onPlayerLogin(player);
		return super.onPlayerLogin();
	}

	@Override
	protected boolean onPlayerLogout() {
		// 退出保存数据
		ShopEntity shopEntity = player.getPlayerData().getShopEntity();
		shopEntity.notifyUpdate(true);
		return super.onPlayerLogout();
	}

	@Override
	public boolean onTick() {
		
		// 神秘商店每兩小時回復
		MysteryShopEntity mysteryShopEntity = player.getPlayerData().loadMysteryShopEntity();
		if (mysteryShopEntity != null)
		{	
			int refreshTime = mysteryShopEntity.getRefreshTime();
			int currSeconds = GuaJiTime.getSeconds();
			if (refreshTime > 0){
				int countTime = currSeconds - refreshTime;
				int oldRefresh = mysteryShopEntity.getFreeRefresh();
				if (countTime >= SysBasicCfg.getInstance().getMysteryRefreshTime()) {
					mysteryShopEntity.incFreeRefresh(1);
					if (mysteryShopEntity.getFreeRefresh() >= SysBasicCfg.getInstance().getMysteryMaxFreeRefresh()) {
						mysteryShopEntity.setRefreshTime(0);
					} else {
						mysteryShopEntity.setRefreshTime(currSeconds);
					}
				}
				if ((oldRefresh == 0) && (mysteryShopEntity.getFreeRefresh() > 0)) {
					BuilderUtil.sendShopRedPoint(player,ShopType.MYSTERY_MARKET,true);
				}
				
			}
		}
		return super.onTick();
	}

	@Override
	public boolean onProtocol(Protocol protocol) {
		
		if (protocol.checkType(HP.code.SHOP_ITEM_C_VALUE)||protocol.checkType(HP.code.SHOP_BUY_C_VALUE)) { // 只檢查商店,略過商城資訊
			if (!FunctionUnlockCfg.checkUnlock(player,null,Const.FunctionType.shop_Unlock)){
				player.sendError(protocol.getType(), Status.error.CONDITION_NOT_ENOUGH);
				return true;
			}
		}
		
		if (protocol.checkType(HP.code.SHOP_ITEM_C_VALUE)) {
			// 商城商品
			ShopItemInfoRequest request = protocol.parseProtocol(ShopItemInfoRequest.getDefaultInstance());
			ShopScervice<?> scervice = ShopFactory.getInstance().getShopHandler(request.getShopType());
			
			if (request.getShopType() == ShopType.SKIN_MARKET) {
				if (!FunctionUnlockCfg.checkUnlock(player,null,Const.FunctionType.skinshop_Unlock)){
					player.sendError(protocol.getType(), Status.error.NOT_LEVEL_OVER_LIMIT);
					return true;
				}
			}
			
			if (request.getShopType() == ShopType.DAILY_MARKET) {
				if (!FunctionUnlockCfg.checkUnlock(player,null,Const.FunctionType.dailyShop_Unlock)){
					player.sendError(protocol.getType(), Status.error.NOT_LEVEL_OVER_LIMIT);
					return true;
				}
			}
						
			if (request.getShopType() == ShopType.ARENA_MARKET) {
				if (!FunctionUnlockCfg.checkUnlock(player,null,Const.FunctionType.arenaShop_Unlock)){
					player.sendError(protocol.getType(), Status.error.NOT_LEVEL_OVER_LIMIT);
					return true;
				}
			}
			
			if (request.getShopType() == ShopType.MYSTERY_MARKET) {
				if (!FunctionUnlockCfg.checkUnlock(player,null,Const.FunctionType.mysteryShop_Unlock)){
					player.sendError(protocol.getType(), Status.error.NOT_LEVEL_OVER_LIMIT);
					return true;
				}
			}
			
			if (request.getShopType() == ShopType.RACE_MARKET) {
				if (!FunctionUnlockCfg.checkUnlock(player,null,Const.FunctionType.raceShop_Unlock)){
					player.sendError(protocol.getType(), Status.error.NOT_LEVEL_OVER_LIMIT);
					return true;
				}
			}
			
			if (request.getShopType() == ShopType.GODSEA_MARKET) {
				
				if (!FunctionUnlockCfg.checkUnlock(player,null,Const.FunctionType.godseaShop_Unlock)){
					player.sendError(protocol.getType(), Status.error.NOT_LEVEL_OVER_LIMIT);
					return true;
				}
				int activityId = Const.ActivityId.ACTIVITY191_CycleStage_VALUE;
				ActivityTimeCfg activityTimeCfg = ActivityUtil.getCurActivityTimeCfg(activityId);
				if (activityTimeCfg == null) {
					ActivityUtil.CycleStageClearItem(player);
					// 活动已关闭
					player.sendError(protocol.getType(), Status.error.ACTIVITY_CLOSE);
					return true;
				}
				int stageId = activityTimeCfg.getStageId();
				// 呼叫建立活動資訊,刷新活動
				ActivityUtil.getActivityStatus(player.getPlayerData(), activityId, stageId, Activity191Status.class);
			}
			
			if (request.getShopType() == ShopType.CYCLESTAGE_MARKET) {
				
				if (!FunctionUnlockCfg.checkUnlock(player,null,Const.FunctionType.godseaShop_Unlock)){
					player.sendError(protocol.getType(), Status.error.NOT_LEVEL_OVER_LIMIT);
					return true;
				}
				
				int activityId = Const.ActivityId.ACTIVITY196_CycleStage_Part2_VALUE;
				ActivityTimeCfg activityTimeCfg = ActivityUtil.getCurActivityTimeCfg(activityId);
				if (activityTimeCfg == null) {
					ActivityUtil.CycleStageClearItem2(player);
					// 活动已关闭
					player.sendError(protocol.getType(), Status.error.ACTIVITY_CLOSE);
					return true;
				}
				int stageId = activityTimeCfg.getStageId();
				// 呼叫建立活動資訊,刷新活動
				ActivityUtil.getActivityStatus(player.getPlayerData(), activityId, stageId, Activity191Status.class);
			}
			
			if (null != scervice) { 
				if (request.getType() == Const.ShopOperationType.INIT_TYPE_VALUE) {
					scervice.sendShopBuilder(player);
				} else if (request.getType() == Const.ShopOperationType.REFRESH_TYPE_VALUE) {
					scervice.shopRefreshLogic(player,request);
				}
			}
			return true;
		} else if (protocol.checkType(HP.code.SHOP_BUY_C_VALUE)) {
			// 商品购买
			BuyShopItemsRequest request = protocol.parseProtocol(BuyShopItemsRequest.getDefaultInstance());
			ShopScervice<?> scervice = ShopFactory.getInstance().getShopHandler(request.getShopType());
			
			if (request.getShopType() == ShopType.SKIN_MARKET) {
				if (!FunctionUnlockCfg.checkUnlock(player,null,Const.FunctionType.skinshop_Unlock)){
					player.sendError(protocol.getType(), Status.error.NOT_LEVEL_OVER_LIMIT);
					return true;
				}
			}
			
			if (request.getShopType() == ShopType.DAILY_MARKET) {
				if (!FunctionUnlockCfg.checkUnlock(player,null,Const.FunctionType.dailyShop_Unlock)){
					player.sendError(protocol.getType(), Status.error.NOT_LEVEL_OVER_LIMIT);
					return true;
				}
			}
			
			if (request.getShopType() == ShopType.DAILY_MARKET) {
				if (!FunctionUnlockCfg.checkUnlock(player,null,Const.FunctionType.dailyShop_Unlock)){
					player.sendError(protocol.getType(), Status.error.NOT_LEVEL_OVER_LIMIT);
					return true;
				}
			}
			
			if (request.getShopType() == ShopType.ARENA_MARKET) {
				if (!FunctionUnlockCfg.checkUnlock(player,null,Const.FunctionType.arenaShop_Unlock)){
					player.sendError(protocol.getType(), Status.error.NOT_LEVEL_OVER_LIMIT);
					return true;
				}
			}
			
			if (request.getShopType() == ShopType.MYSTERY_MARKET) {
				if (!FunctionUnlockCfg.checkUnlock(player,null,Const.FunctionType.mysteryShop_Unlock)){
					player.sendError(protocol.getType(), Status.error.NOT_LEVEL_OVER_LIMIT);
					return true;
				}
			}
			
			if (request.getShopType() == ShopType.RACE_MARKET) {
				if (!FunctionUnlockCfg.checkUnlock(player,null,Const.FunctionType.raceShop_Unlock)){
					player.sendError(protocol.getType(), Status.error.NOT_LEVEL_OVER_LIMIT);
					return true;
				}
			}
			
			if (request.getShopType() == ShopType.GODSEA_MARKET) {
				
				if (!FunctionUnlockCfg.checkUnlock(player,null,Const.FunctionType.godseaShop_Unlock)){
					player.sendError(protocol.getType(), Status.error.NOT_LEVEL_OVER_LIMIT);
					return true;
				}
				
				int activityId = Const.ActivityId.ACTIVITY191_CycleStage_VALUE;
				ActivityTimeCfg activityTimeCfg = ActivityUtil.getCurActivityTimeCfg(activityId);
				if (activityTimeCfg == null) {
					ActivityUtil.CycleStageClearItem(player);
					// 活动已关闭
					player.sendError(protocol.getType(), Status.error.ACTIVITY_CLOSE);
					return true;
				}
			}
			
			if (request.getShopType() == ShopType.CYCLESTAGE_MARKET) {
				
				if (!FunctionUnlockCfg.checkUnlock(player,null,Const.FunctionType.godseaShop_Unlock)){
					player.sendError(protocol.getType(), Status.error.NOT_LEVEL_OVER_LIMIT);
					return true;
				}
				
				int activityId = Const.ActivityId.ACTIVITY196_CycleStage_Part2_VALUE;
				ActivityTimeCfg activityTimeCfg = ActivityUtil.getCurActivityTimeCfg(activityId);
				if (activityTimeCfg == null) {
					ActivityUtil.CycleStageClearItem2(player);
					// 活动已关闭
					player.sendError(protocol.getType(), Status.error.ACTIVITY_CLOSE);
					return true;
				}
			}
			
			if (null != scervice) {
				scervice.shopBuyLogic(player, request);
			}
			return true;
		} else if (protocol.checkType(HP.code.FETCH_SHOP_LIST_C)) {
			// 充值列表
			onFetchRechargeList(protocol.parseProtocol(HPFetchShopList.getDefaultInstance()));
			return true;
		} else if (protocol.checkType(HP.code.LAST_SHOP_ITEM_C)) {
			onLastRechargeItem(protocol);
			return true;
		} else if (protocol.checkType(HP.code.SHOP_HONEYP_C_VALUE)) {
			
			if (player.getPlatformId() != GsConst.PlatformById.R18) {
				  player.sendError(protocol.getType(), Status.error.PARAMS_INVALID);
				return false;
			}  
			
			HoneyPRequest request = protocol.parseProtocol(HoneyPRequest.getDefaultInstance());
			String token = request.getToken();
			String resultStr = getHoneyP(token);//chkH54647(player.getPuid(),token,1,player.getPlayerData().getId(),0);
			JSONObject result = JSONObject.fromObject(resultStr);
			int coins = result.getInt("coins");
			HoneyPResponse.Builder builder = HoneyPResponse.newBuilder();
			builder.setCoins(coins);
			sendProtocol(Protocol.valueOf(HP.code.SHOP_HONEYP_S_VALUE, builder));
			return true;
		} else if (protocol.checkType(HP.code.SHOP_HONEYP_BUY_C_VALUE)) {
			
			if (player.getPlatformId() != GsConst.PlatformById.R18) {
				  player.sendError(protocol.getType(), Status.error.PARAMS_INVALID);
				return false;
			} 
			HoneyPBuyRequest request = protocol.parseProtocol(HoneyPBuyRequest.getDefaultInstance());
			String token = request.getToken();
			int goodsId = request.getPid(); // goodsId
			
	        String platform = player.getPlatform(); //使用自身平台
			RechargeConfig rechargeConfig = RechargeConfig.getRechargeConfig(platform);

			if (rechargeConfig == null) {
			    //logger.info("recharge config cannot found, platform: {}", new Object[] { platform });
				throw new RuntimeException("recharge config cannot found");
			}
			
			
			Map<Integer,RechargeItem> allRechargeItem =rechargeConfig.getAllrechargeCfg();
			
			if (allRechargeItem == null || !allRechargeItem.containsKey(goodsId)) {
	            player.sendError(protocol.getType(), Status.error.CONFIG_NOT_FOUND);
	            return true;
			}
			
			int costCoin =  (int)allRechargeItem.get(goodsId).getCostMoney();//cny轉honeyp
			
			String orderId = UUID.randomUUID().toString().replace("-", "");
			
			JSONObject rechargeJson = new JSONObject();
			
			rechargeJson.put("puid",player.getPuid());
			rechargeJson.put("orderSerial",orderId);
			rechargeJson.put("platform","android_r18");
			rechargeJson.put("payMoney",String.valueOf(allRechargeItem.get(goodsId).getCostMoney()));
			rechargeJson.put("goodsId",allRechargeItem.get(goodsId).getName()); // 此處指的是商品名稱(要特別注意)
			rechargeJson.put("goodsCount",1);
			rechargeJson.put("serverId",player.getServerId());
			rechargeJson.put("test","false");
			
			String resultStr = ExchangeByHoneyP(token,orderId,costCoin);//chkH54647(player.getPuid(),token,2,player.getPlayerData().getId(),pid);
			JSONObject result = JSONObject.fromObject(resultStr);
			String code = result.getString("result");
			Log.logPrintln("dohutuoapi code: " + code + ",orderId" + orderId);
			HoneyPBuyResponse.Builder builder = HoneyPBuyResponse.newBuilder();
			builder.setResult(code.contains("0000")?1:0);
			builder.setRefno(orderId);
			builder.setCostmoney(costCoin);
			sendProtocol(Protocol.valueOf(HP.code.SHOP_HONEYP_BUY_S_VALUE, builder));
			if (code.contains("0000")) {
				RechargeParam rechargeParam = RechargeParam.valueOf(rechargeJson.toString());
				if (rechargeParam != null) {
					JSONObject ret = RechargeManager.getInstance().handleRecharge(rechargeParam);
					int errno = ret.getInt("errno");
					if(errno==0){
						double CNYD = (Double.parseDouble(rechargeParam.getPayMoney()) / 10);
						String productName = ret.getString("productName");	
						RechargeManager.getInstance().dohtapdb(player.getTabDBAppId(), rechargeParam.getOrderSerial(),rechargeParam.getPuid(),"CNY",CNYD,rechargeParam.getGoodsId(),productName);
					}
					Log.logPrintln("doH54647Tapdb result: " + errno);
				}
			}
			return true;
		} else if (protocol.checkType(HP.code.SHOP_69COIN_TAKE_C_VALUE)) {
			
			if (player.getPlatformId() != GsConst.PlatformById.KUSO) {
				  player.sendError(protocol.getType(), Status.error.PARAMS_INVALID);
				return false;
			} 
			
			SixNineCoinTakeRequest request = protocol.parseProtocol(SixNineCoinTakeRequest.getDefaultInstance());
			String token = request.getToken();
			String orderId = request.getOrderid(); // orderId
			String nonce = request.getNonce();
			int goodsId = request.getGoodsId(); // goodsId
			
	        String platform = player.getPlatform(); //使用自身平台
			RechargeConfig rechargeConfig = RechargeConfig.getRechargeConfig(platform);

			if (rechargeConfig == null) {
			    //logger.info("recharge config cannot found, platform: {}", new Object[] { platform });
				throw new RuntimeException("recharge config cannot found");
			}
			
			
			Map<Integer,RechargeItem> allRechargeItem =rechargeConfig.getAllrechargeCfg();
			
			if (allRechargeItem == null || !allRechargeItem.containsKey(goodsId)) {
	            player.sendError(protocol.getType(), Status.error.CONFIG_NOT_FOUND);
	            return true;
			}
			
			int points =  (int)allRechargeItem.get(goodsId).getCostMoney();
			
			RechargeEntity rechargeEntity = DBManager.getInstance().fetch(RechargeEntity.class,
					"from RechargeEntity where orderSerial = ? and invalid = 0", nonce);
			
			if (rechargeEntity != null) {
				// check repeat nonce
	            player.sendError(protocol.getType(), Status.error.CONFIG_NOT_FOUND);
	            return true;
			}
			
			boolean successs =  Kuso69QueryOrderId(orderId,nonce,token,points);
			
			if (successs) {
				JSONObject rechargeJson = new JSONObject();
				
				rechargeJson.put("puid",player.getPuid());
				rechargeJson.put("orderSerial",nonce);
				rechargeJson.put("platform","android_kuso");
				rechargeJson.put("payMoney",String.valueOf(allRechargeItem.get(goodsId).getCostMoney()));
				rechargeJson.put("goodsId",allRechargeItem.get(goodsId).getName()); // 此處指的是商品名稱(要特別注意)
				rechargeJson.put("goodsCount",1);
				rechargeJson.put("serverId",player.getServerId());
				rechargeJson.put("test","false");
				
				RechargeParam rechargeParam = RechargeParam.valueOf(rechargeJson.toString());
				if (rechargeParam != null) {
					JSONObject ret = RechargeManager.getInstance().handleRecharge(rechargeParam);
					int errno = ret.getInt("errno");
					if(errno==0){
						double CNYD = (Double.parseDouble(rechargeParam.getPayMoney()) / 10);
						String productName = ret.getString("productName");	
						RechargeManager.getInstance().dohtapdb(player.getTabDBAppId(), rechargeParam.getOrderSerial(),rechargeParam.getPuid(),"CNY",CNYD,rechargeParam.getGoodsId(),productName);
					}
					Log.logPrintln("do_KusoTapdb result: " + errno);
				}

			}
			
			SixNineTakeResponse.Builder builder = SixNineTakeResponse.newBuilder();
			if (successs) {
				builder.setResult(1);
			} else {
				builder.setResult(0);
			}
			sendProtocol(Protocol.valueOf(HP.code.SHOP_69COIN_TAKE_S_VALUE, builder));
			
		}
		return super.onProtocol(protocol);
	}
	/**
	 * 充值列表
	 * 
	 * @param protocol
	 */
	private void onFetchRechargeList(HPFetchShopList protocol) {

		String platform = player.getPlatform(); //使用自身平台
		
		if (platform.contains("win32")) {
			platform = "";
		}
		
		RechargeConfig rechargeConfig = null;
		int goodsId = 0;
		
//		if (protocol.hasPlatform()) {
//			platform = protocol.getPlatform().trim().toLowerCase();
//		}
				
		if (protocol.hasGoodsId()) {
			goodsId = protocol.getGoodsId();
		}
		
		try {
			rechargeConfig = RechargeConfig.getRechargeConfig(platform);
		} catch (Exception e) {
			MyException.catchException(e);
			return;
		}
		
		
		if (rechargeConfig == null) {
			player.sendError(HP.code.FETCH_SHOP_LIST_C_VALUE, Status.error.FETCH_RECHARGE_LIST_FAILED_VALUE);
			return;
		}
		int rate = 1;
//		if (platform.contains("r18")) {	//H54647幣值轉換成honeyp
//			rate = 10;
//		} else if (platform.contains("kuso")) {
//			rate = 10; //kuso人民幣值轉換成69coin
//		}

		HPShopListSync.Builder builder = HPShopListSync.newBuilder();
		Collection<RechargeConfig.RechargeItem> rechargeItems = rechargeConfig.getAllRechargeItems();
		Deque<ShopItem.Builder> shopItemQueue = new LinkedList<ShopItem.Builder>();
		for (RechargeConfig.RechargeItem rechargeItem : rechargeItems) {
			if ((goodsId != 0) && (rechargeItem.getId() != goodsId)) {
				continue;
			}
			if (rechargeItem.isValid()) {
				ShopItem.Builder shopItemBuilder = ShopItem.newBuilder();
				int itemId = rechargeItem.getId();
				shopItemBuilder.setProductId(String.valueOf(rechargeItem.getId()));//配置id
				shopItemBuilder.setProductPrice(rechargeItem.getCostMoney()*rate);// 价值货币
				shopItemBuilder.setGold(rechargeItem.getAmount());
				shopItemBuilder.setPresentGold(rechargeItem.getAddNum());
				shopItemBuilder.setProductType(rechargeItem.getType());
				shopItemBuilder.setName(rechargeItem.getName());//商品id
				shopItemBuilder.setProductName(rechargeItem.getProductName());
				shopItemBuilder.setShowAddGold(rechargeItem.getAddNum());
				shopItemBuilder.setDescription(rechargeItem.getDesc());
				// 首充翻倍活动
				FirstRechargeStatus firstRechargeStatus = ActivityUtil.getFirstRechargeStatus(player.getPlayerData());
				if (firstRechargeStatus != null && rechargeItem.getType() == 0) {
					if (firstRechargeStatus.getRecharge(itemId) <= 0) {
						RechargeRatioCfg rechargeRatioCfg = ConfigManager.getInstance().getConfigByKey(RechargeRatioCfg.class, itemId);
						if (rechargeRatioCfg != null) {
							shopItemBuilder.setRatio(rechargeRatioCfg.getRatio(player.getPlayerData()) + 1);
						}
						if (GJLocal.isLocal(GJLocal.R2)) {
							if (rechargeRatioCfg.isCountDown(player.getPlayerData())) {
								builder.setCountdown(rechargeRatioCfg.getCountDownTime(player.getPlayerData()));
							} else {
								builder.setCountdown(0);
							}
						}
					}
				}

				if (rechargeItem.getType() > 0) {
					shopItemQueue.addFirst(shopItemBuilder);
				} else {
					shopItemQueue.addLast(shopItemBuilder);
				}
			}
		}

		for (ShopItem.Builder itemBuilder : shopItemQueue) {
			builder.addShopItems(itemBuilder);
		}

		sendProtocol(Protocol.valueOf(HP.code.FETCH_SHOP_LIST_S, builder));
	
	}
	
	private void onLastRechargeItem(Protocol protocol) {
		// goods Type 18 -23 user
		LastGoodsItem.Builder builder = LastGoodsItem.newBuilder();
		builder.setItems(player.getPlayerData().getLastRecharage());
		player.getPlayerData().setLastRecharage("");
		sendProtocol(Protocol.valueOf(HP.code.LAST_SHOP_ITEM_S, builder));
	};
	
	
	public static String getHoneyP(String token) {
		String url = "https://gen.exchangcall.com/api/getUserCoins";
		HttpClient httpClient = null;
		HttpPost httpPost = null;
		HttpResponse response = null;
		JSONObject jsonobj = new JSONObject();
		jsonobj.put("result","0001");
		jsonobj.put("coins",-1);
		try {
			
			httpClient = HttpClients.custom().build();
			httpPost = new HttpPost(url);
			RequestConfig reqConfig = RequestConfig.custom().setSocketTimeout(2000).setConnectTimeout(2000).build();
			httpPost.setConfig(reqConfig);
			httpPost.setHeader(HttpHeaders.CONNECTION, "close");
			httpPost.setHeader("Authorization", token);
			
			StringEntity entity = new StringEntity("");
			entity.setContentEncoding("utf-8");
			entity.setContentType("application/x-www-form-urlencoded");
			httpPost.setEntity(entity);
			
			response = httpClient.execute(httpPost);
			int statusCode = response.getStatusLine().getStatusCode();
			HttpEntity httpEntity = response.getEntity();
			String resultStr = EntityUtils.toString(httpEntity);
			Log.logPrintln("H54647 GetHoneyP result: " + resultStr + "code: " + statusCode);
			JSONObject resjos = JSONObject.fromObject(resultStr);
			
			String rusult = jsonobj.toString();
			
			if (resjos.has("result")) {
				String errorcode = resjos.getString("result");
				if (errorcode.equals("0000")) {
					rusult = resultStr;
				}
			}
			httpPost.releaseConnection();
			return rusult;
		} catch (Exception e) {
			MyException.catchException(e);
			return jsonobj.toString();
		}
	}

	public static String ExchangeByHoneyP(String token,String orderId,int costCoin) {
		String url = "https://gen.exchangcall.com/api/exchangeCoins";
		int gameId = 128;
		String Md5Key = "63130443A94845DD8062FD751591C9B2";
		HttpClient httpClient = null;
		HttpPost httpPost = null;
		HttpResponse response = null;
		
		JSONObject jsonobj = new JSONObject();
		jsonobj.put("result","0001");
	
		try {
			
			JSONObject req_json = new JSONObject();
			req_json.put("transaction_id",orderId);
			req_json.put("exchange_coins",costCoin);
			
			String text = req_json.toString();
			byte[] textByte = text.getBytes("UTF-8");
			
			Base64.Encoder encoder = Base64.getEncoder();
			String encodedText = encoder.encodeToString(textByte);
			
	        StringBuffer signbuff = new StringBuffer();
	        signbuff.append("game_id:");
	        signbuff.append(String.valueOf(gameId));
	        signbuff.append("&request:");
	        signbuff.append(encodedText);
	        signbuff.append("&key:");
	        signbuff.append(Md5Key);
	        
	        String signbufStr = signbuff.toString();
	        
	        String signMd5 = Md5.makeMD5(signbufStr);
	        signMd5 = signMd5.toUpperCase();
	        
	        StringBuffer reqBuffer = new StringBuffer();
	        reqBuffer.append("game_id=");
	        reqBuffer.append(String.valueOf(gameId));
	        reqBuffer.append("&signMsg=");
	        reqBuffer.append(signMd5);
	        reqBuffer.append("&request=");
	        reqBuffer.append(encodedText);
	        reqBuffer.append("&platform=");
	        reqBuffer.append("R18");

			httpClient = HttpClients.custom().build();
			httpPost = new HttpPost(url);
			RequestConfig reqConfig = RequestConfig.custom().setSocketTimeout(2000).setConnectTimeout(2000).build();
			httpPost.setConfig(reqConfig);
			httpPost.setHeader(HttpHeaders.CONNECTION, "close");
			httpPost.setHeader("Content-type", "application/x-www-form-urlencoded");
			httpPost.setHeader("Accept", "application/json");
			httpPost.setHeader("Authorization", token);
			
			StringEntity entity = new StringEntity(reqBuffer.toString());
			entity.setContentEncoding("utf-8");
			entity.setContentType("application/x-www-form-urlencoded");
			httpPost.setEntity(entity);
			
			response = httpClient.execute(httpPost);
			int statusCode = response.getStatusLine().getStatusCode();
			HttpEntity httpEntity = response.getEntity();
			String resultStr = EntityUtils.toString(httpEntity);
			Log.logPrintln("H54647 ExchangeByHoneyP result: " + resultStr + "code: " + statusCode);
			JSONObject resjos = JSONObject.fromObject(resultStr);
			
			String rusult = jsonobj.toString();
			
			if (resjos.has("result")) {
				String errorcode = resjos.getString("result");
				if (errorcode.equals("0000")) {
					rusult = resultStr;
				}
			}
			httpPost.releaseConnection();
			return rusult;
		} catch (Exception e) {
			MyException.catchException(e);
			return jsonobj.toString();
		}
	}
	
	public static String ExchangeBy69Coin(String token,String orderId,int costCoin,JSONObject items) {
		String SanBox_BASE_URL = "https://dev-api.69game.xyz/accounts/spend";
		String Production_BASE_URL = "https://api.69game.xyz/accounts/spend";
		String BASE_URL = App.getInstance().getAppCfg().isDebug() ? SanBox_BASE_URL : Production_BASE_URL;

		HttpClient httpClient = null;
		HttpPost httpPost = null;
		HttpResponse response = null;
		
		JSONObject jsonobj = new JSONObject();
		jsonobj.put("result","0001");
	
		try {
				        
//	        StringBuffer reqBuffer = new StringBuffer();
//	        reqBuffer.append("appId	=");
//	        reqBuffer.append("");
//	        reqBuffer.append("&nonce=");
//	        reqBuffer.append(orderId);
//	        reqBuffer.append("&points=");
//	        reqBuffer.append(String.valueOf(costCoin));
//	        reqBuffer.append("&description=");
//	        reqBuffer.append("test");
//	        reqBuffer.append("&items=");
//	        reqBuffer.append("");
//	        reqBuffer.append("&subId=");
//	        reqBuffer.append("");
			
			JSONObject param = new JSONObject();
			param.put("appId", "APPncbR1hdPgUIjSKt");
			param.put("nonce", orderId);
			param.put("points", String.valueOf(costCoin));
			param.put("description", "test 123");
			param.put("items", items);
			param.put("subId", "NG24");

			httpClient = HttpClients.custom().build();
			httpPost = new HttpPost(BASE_URL);
			RequestConfig reqConfig = RequestConfig.custom().setSocketTimeout(2000).setConnectTimeout(2000).build();
			httpPost.setConfig(reqConfig);
			httpPost.setHeader(HttpHeaders.CONNECTION, "close");
			httpPost.addHeader("Content-type", "application/x-www-form-urlencoded");
			httpPost.addHeader("Content-type", "application/json");
			httpPost.setHeader("Referer", "com.kusoplay.sample");
			httpPost.setHeader("Authorization", token);
			
			StringEntity entity = new StringEntity(param.toString());
			entity.setContentEncoding("utf-8");
			entity.setContentType(ContentType.APPLICATION_JSON.toString());
			httpPost.setEntity(entity);
			
			response = httpClient.execute(httpPost);
			int statusCode = response.getStatusLine().getStatusCode();
			HttpEntity httpEntity = response.getEntity();
			String resultStr = EntityUtils.toString(httpEntity);
			Log.logPrintln("KUSO ExchangeBy69Coin result: " + resultStr + "code: " + statusCode);
			JSONObject resjos = JSONObject.fromObject(resultStr);
			
			String rusult = jsonobj.toString();
			
			if (resjos.has("result")) {
				String errorcode = resjos.getString("result");
				if (errorcode.equals("0000")) {
					rusult = resultStr;
				}
			}
			httpPost.releaseConnection();
			return rusult;
		} catch (Exception e) {
			MyException.catchException(e);
			return jsonobj.toString();
		}
	}
	
	
//	public static String chkH54647(String puid, String token, int type, int mid, int pid) {
//		//String chkhutuoURL = "http://devgoc.bigwin-tech.com/idlepaycenter/chkregistered";
//		String chkhutuoURL = "https://recharge.idleparadise.com/idlepaycenter/chkH54647";
//		int sid = GsConfig.getInstance().getServerId();
//		if (sid == 1)
//		{
//			chkhutuoURL = "http://devgoc.bigwin-tech.com/idlepaycenter/chkH54647";//內部測試用
//			sid = 6;
//		}
//			
//		HttpClient httpClient = null;
//		HttpPost httpPost = null;
//		HttpResponse response = null;
//		try {		
//			JSONObject obj = new JSONObject();
//			obj.put("sid", sid);
//			obj.put("mid", (int)mid);
//			obj.put("puid", puid);
//			obj.put("token", token);
//			obj.put("pid", (int)pid);
//			obj.put("type", (int)type);
//			Log.logPrintln("chkH54647 : " + chkhutuoURL + "?" + obj.toString());
//			
//			String postRequest = URLEncoder.encode(obj.toString(), "UTF-8");
//			Log.logPrintln("chkH54647 : " + chkhutuoURL + "?" + postRequest);
//			//String postRequest = "puid="+puid+"&pwd="+pwd+"&type="+type;
//			
//			httpClient = HttpClients.custom().build();
//
//			httpPost = new HttpPost(chkhutuoURL);
//			RequestConfig reqConfig = RequestConfig.custom().setSocketTimeout(10000).setConnectTimeout(10000).build();
//			httpPost.setConfig(reqConfig);
//			httpPost.setHeader(HttpHeaders.CONNECTION, "close");
//
//			StringEntity entity = new StringEntity(postRequest);
//			entity.setContentEncoding("utf-8");
//			entity.setContentType("application/json");
//			httpPost.setEntity(entity);
//			response = httpClient.execute(httpPost);
//
//			HttpEntity httpEntity = response.getEntity();
//			String resultStr = EntityUtils.toString(httpEntity);
//			Log.logPrintln("chkH54647 result: " + resultStr);
//			resultStr = resultStr.trim().substring(resultStr.indexOf("{"));//有一個特殊的符號要去掉
//			// consume an optional byte order mark (BOM) if it exists
////			JSONObject result = JSONObject.fromObject(resultStr);
//			httpPost.releaseConnection();
//			return resultStr;
//		
//		} catch (Exception e) {
//			MyException.catchException(e);
//		}
//		return "{\"result\":\"0301\",\"coin\":0,\"refno\":\"0\"}";
//	}
//	
//	public static String OrderH54647(boolean Isupdate,String renf) {
//		String chkhutuoURL = "";
//		String reStr= "error";
//		if (Isupdate)
//			chkhutuoURL ="http://devgoc.bigwin-tech.com/idlepaycenter/updateOrderH54647";
//		else
//			chkhutuoURL = "https://recharge.idleparadise.com/idlepaycenter/getorderH54647";
//		
//		int sid = GsConfig.getInstance().getServerId();
//		if (sid == 1)
//		{
//			if (Isupdate)
//				chkhutuoURL = "http://devgoc.bigwin-tech.com/idlepaycenter/updateOrderH54647";//內部測試用
//			else
//				chkhutuoURL = "http://devgoc.bigwin-tech.com/idlepaycenter/getorderH54647";//內部測試用
//			sid = 6;
//		}
//		HttpClient httpClient = null;
//		HttpGet httptget = null;
//		HttpResponse response = null;
//		try {
//			chkhutuoURL = chkhutuoURL+ "?"+"refno="+renf;
//
//			httpClient = HttpClients.custom().build();
//
//			httptget = new HttpGet(chkhutuoURL);
//			RequestConfig reqConfig = RequestConfig.custom().setSocketTimeout(10000).setConnectTimeout(10000).build();
//			httptget.setConfig(reqConfig);
//			httptget.setHeader(HttpHeaders.CONNECTION, "close");
//			response = httpClient.execute(httptget);
//
//			HttpEntity httpEntity = response.getEntity();
//			reStr = EntityUtils.toString(httpEntity);
//			if (Isupdate)
//				Log.logPrintln("updateOrderH54647 result: " + reStr);
//			else
//				Log.logPrintln("getorderH54647 result: " + reStr);
//			httptget.releaseConnection();
//			return reStr;
//		} catch (Exception e) {
//			MyException.catchException(e);
//			return reStr;
//		}
//	}
	
	public boolean Kuso69QueryOrderId(String orderId,String nonce,String token,int points) {
		String SanBox_BASE_URL = "https://dev-api.69game.xyz/accounts/activities/";
		String Production_BASE_URL = "https://api.69game.xyz/accounts/activities/";
		String appId = "APPncbR1hdPgUIjSKt";
		String subId = "NG24";
		
		String SanBox_Package = "chance.debug.ninja.girl.kuso.cn";
		String Production_Package = "chance.release.ninja.girl";

		
		String BASE_URL = App.getInstance().getAppCfg().isDebug() ? SanBox_BASE_URL : Production_BASE_URL;
		
		String PackageName = App.getInstance().getAppCfg().isDebug() ? SanBox_Package : Production_Package;
		
		try {
						
			StringBuffer reqBuffer = new StringBuffer();
			reqBuffer.append(BASE_URL);
			reqBuffer.append("?orderID=");
			reqBuffer.append(orderId);
			reqBuffer.append("&appId=");
			reqBuffer.append(appId);
			reqBuffer.append("&subId=");
			reqBuffer.append(subId);
			
			String reqUrl = reqBuffer.toString();
			
			HttpClient httpClient = HttpClients.custom().build();
			HttpGet httpGet = new HttpGet(reqUrl);
			RequestConfig reqConfig = RequestConfig.custom().setSocketTimeout(10000).setConnectTimeout(10000).build();
			httpGet.setConfig(reqConfig);
			httpGet.addHeader("Content-type", "application/x-www-form-urlencoded");
			httpGet.addHeader("Content-type", "application/json");
			httpGet.setHeader("Referer", PackageName);
			httpGet.setHeader("Authorization", token);
			HttpResponse response = null;
			
			response = httpClient.execute(httpGet);
			
			HttpEntity httpEntity = response.getEntity();
			String reStr = EntityUtils.toString(httpEntity);
			
			Log.logPrintln("Kuso69QueryOrderId reStr: " + reStr);
			
			int statusCode = response.getStatusLine().getStatusCode();
			if (statusCode == HttpStatus.SC_OK) {
				JSONArray jsonArray = JSONArray.fromObject(reStr);
		        for(int i=0;i<jsonArray.size();i++){ 
		        	JSONObject obj = jsonArray.getJSONObject(i);
		        	String Id = obj.getString("id");
		        	String nonceId = obj.getString("nonce");
		        	int checkPoint = obj.getInt("points");
		        	// 如果後來新增資料需要檢查再取,不然舊資料沒有這個key會跳掉
		        	int freePoints = obj.containsKey("freePoints") ? obj.getInt("freePoints") : 0;
		        	
		        	if ((orderId.equals(Id))&&(nonce.equals(nonceId))&&((checkPoint+freePoints) == points)){
		        		return true;
		        	}
		        } 
				return false;
			}
			return false;
		} catch (Exception e) {
			// TODO: handle exception
			MyException.catchException(e);
			return false;
		}
		
	}
	
}
