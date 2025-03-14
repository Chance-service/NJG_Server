package com.guaji.game.module;

public class PlayerMysteryShopModule
{
}


//package com.guaji.game.module;
//
//import java.text.DateFormat;
//import java.text.ParseException;
//import java.text.SimpleDateFormat;
//import java.util.ArrayList;
//import java.util.Calendar;
//import java.util.Date;
//import java.util.List;
//
//import org.hawk.log.HawkLog;
//import org.hawk.net.protocol.HawkProtocol;
//
//import com.guaji.game.ServerData;
//import com.guaji.game.config.ActivityTimeCfg;
//import com.guaji.game.config.SysBasicCfg;
//import com.guaji.game.item.AwardItems;
//import com.guaji.game.item.AwardItems.Item;
//import com.guaji.game.item.ConsumeItems;
//import com.guaji.game.log.BehaviorLogger;
//import com.guaji.game.log.BehaviorLogger.Action;
//import com.guaji.game.log.BehaviorLogger.Params;
//import com.guaji.game.manager.ChatManager;
//import com.guaji.game.manager.ChatMsg;
//import com.guaji.game.manager.MailManager;
//import com.guaji.game.manager.ShopManager;
//import com.guaji.game.module.activity.mysteryShop.MysteryShopAuctionStatus;
//import com.guaji.game.module.activity.mysteryShop.MysteryShopStatus;
//import com.guaji.game.player.Player;
//import com.guaji.game.player.PlayerModule;
//import com.guaji.game.protocol.Const;
//import com.guaji.game.protocol.Const.changeType;
//import com.guaji.game.protocol.HP;
//import com.guaji.game.protocol.Mail;
//import com.guaji.game.protocol.Shop.AuctionDrop;
//import com.guaji.game.protocol.Shop.AuctionInfoMsg;
//import com.guaji.game.protocol.Shop.AuctionInfoMsgRet;
//import com.guaji.game.protocol.Shop.ShopItemInfo;
//import com.guaji.game.protocol.Shop.SteriousShopInfoMsg;
//import com.guaji.game.protocol.Shop.SteriousShopInfoMsgRet;
//import com.guaji.game.protocol.Status;
//import com.guaji.game.shop.PlayerShopItem;
//import com.guaji.game.shop.ShopResultVo;
//import com.guaji.game.util.ActivityUtil;
//import com.guaji.game.util.GsConst;
//import com.guaji.game.util.PlayerUtil;
//
///**
// * 黑市模块
// * @author Darcy
// */
//public class PlayerMysteryShopModule extends PlayerModule {
//
//	/**
//	 * 1.黑市商城初始化 2.黑市商城单个购买id 3.黑市商城全部购买,4,黑市商城刷新,
//	 */
//	private static final int TYPE_SHOP_INIT = 1, TYPE_SHOP_BUY = 2, TYPE_SHOP_BUY_ALL = 3, TYPE_SHOP_REFASH = 4;
//	
//	/**
//	 * 1.竞拍初始化 2.竞价 3.刷新
//	 */
//	private static final int TYPE_AUCTION_INIT = 1, TYPE_AUCTION_BIDDING = 2,TYPE_AUCTION_REFRESH = 3;
//	
//	public PlayerMysteryShopModule(Player player) {
//		super(player);
//		listenProto(HP.code.SHOP_STERIOUS_C_VALUE);
//		listenProto(HP.code.SHOP_AUCTION_C_VALUE);
//	}
//
//	@Override
//	protected boolean onPlayerLogin() {
//		return super.onPlayerLogin();
//	}
//
//	@Override
//	protected boolean onPlayerLogout() {
//		return super.onPlayerLogout();
//	}
//
//	@Override
//	public boolean onTick() {
//		clearAuctionInfo();
//		return super.onTick();
//	}
//	
//	public void clearAuctionInfo(){
//		
//		ActivityTimeCfg timeCfg = ActivityUtil.getCurActivityTimeCfg( Const.ActivityId.MYSTERY_SHOP_VALUE);
//		if (timeCfg == null) {
//			MysteryShopAuctionStatus auctionItemInfo = ServerData.getInstance().getServerStatus(GsConst.ServerStatusId.MYSTERY_SHOP_AUCTION, MysteryShopAuctionStatus.class);
//			if(auctionItemInfo.getIsAward() == 1){
//				auctionItemInfo.clear();
//				auctionItemInfo.setIsAward(0);
//				ServerData.getInstance().updateServerData(GsConst.ServerStatusId.MYSTERY_SHOP_AUCTION);
//			}
//		}
//	}
//	
//	@Override
//	public boolean onProtocol(Protocol protocol) {
//		ActivityTimeCfg timeCfg = ActivityUtil.getCurActivityTimeCfg( Const.ActivityId.MYSTERY_SHOP_VALUE);
//		if (timeCfg == null) {
//			// 活动已关闭
//			sendError(protocol.getType(), Status.error.ACTIVITY_CLOSE);
//			SteriousShopInfoMsgRet.Builder builder = getCloseSteriousShopInfoMsgRetBuilder(TYPE_SHOP_INIT);
//			sendProtocol(Protocol.valueOf(HP.code.SHOP_STERIOUS_S_VALUE,builder));
//			return true;
//		}
//		
//		//黑市商品
//		if (protocol.checkType(HP.code.SHOP_STERIOUS_C_VALUE)) {
//			SteriousShopInfoMsg shopInfo = protocol.parseProtocol(SteriousShopInfoMsg.getDefaultInstance());
//			if(timeCfg.isEnd()){
//				// 活动已关闭
//				sendError(protocol.getType(), Status.error.ACTIVITY_CLOSE);
//				SteriousShopInfoMsgRet.Builder builder = getCloseSteriousShopInfoMsgRetBuilder(TYPE_SHOP_INIT);
//				sendProtocol(Protocol.valueOf(HP.code.SHOP_STERIOUS_S_VALUE,builder));
//				return true;
//			}
//			dealWithShopProcess(shopInfo, protocol.getType(), timeCfg);
//		}
//		//黑市竞拍
//		else if(protocol.checkType(HP.code.SHOP_AUCTION_C_VALUE)){
//			AuctionInfoMsg auctioInfo = protocol.parseProtocol(AuctionInfoMsg.getDefaultInstance());
//			if(timeCfg.isEnd()){
//				int type = auctioInfo.getType();
//				if(type == TYPE_AUCTION_INIT){
//					sendAuctionAwardItem(timeCfg);
//					// 活动已关闭
//					sendError(protocol.getType(), Status.error.ACTIVITY_CLOSE);
//					return true;
//				}
//				// 活动已关闭
//				sendError(protocol.getType(), Status.error.ACTIVITY_CLOSE);
//				SteriousShopInfoMsgRet.Builder builder = getCloseSteriousShopInfoMsgRetBuilder(TYPE_SHOP_INIT);
//				sendProtocol(Protocol.valueOf(HP.code.SHOP_STERIOUS_S_VALUE,builder));
//				return true;
//			}
//			dealWithAuctionProcess(auctioInfo, protocol.getType(), timeCfg);
//		}
//		return super.onProtocol(protocol);
//	}
//	
//	/**
//	 * 黑市物品
//	 * @param shopInfo
//	 * @param protType
//	 * @param timeCfg
//	 */
//	public boolean dealWithShopProcess(SteriousShopInfoMsg shopInfo, int protType,
//			ActivityTimeCfg timeCfg) {
//		MysteryShopStatus mysteryShopStatus = ActivityUtil.getActivityStatus(player.getPlayerData(),  Const.ActivityId.MYSTERY_SHOP_VALUE,timeCfg.getStageId(), MysteryShopStatus.class);
//		int type = shopInfo.getType();
//		/** 初始化 */
//		if (type == TYPE_SHOP_INIT) {
//			List<PlayerShopItem> shopList = mysteryShopStatus.getPlayerMysteryShopItemList();
//			
////			if (shopList.size() == 0) {
////				shopList.clear();
////				shopList = ShopManager.getNewMysteryPlayerShopItems(player,true,timeCfg.getStageId(),mysteryShopStatus);
////			}
//			Date preRefreshDate = mysteryShopStatus.getRefreshDate();
//			if(!isSameDay(preRefreshDate)){
//				shopList.clear();
//				shopList = ShopManager.getNewMysteryPlayerShopItems(player,true,timeCfg.getStageId(),mysteryShopStatus);
//				mysteryShopStatus.clear();
//			}
//			mysteryShopStatus.shopItemToString(shopList);
//			mysteryShopStatus.setCurrentItemNum(shopList.size());
//			player.getPlayerData().updateActivity( Const.ActivityId.MYSTERY_SHOP_VALUE, timeCfg.getStageId());
//			SteriousShopInfoMsgRet.Builder builder = getSteriousShopInfoMsgRetBuilder(type, mysteryShopStatus.getPlayerMysteryShopItemList(),mysteryShopStatus.getDaliyRefreshNum());
//			builder.setActivitylefttimes(timeCfg.calcActivitySurplusTime());
//			sendProtocol(Protocol.valueOf(HP.code.SHOP_STERIOUS_S_VALUE,builder));
//		}
//		/** 单个购买 */
//		else if (type == TYPE_SHOP_BUY) {
//			ShopResultVo<AwardItems> resultVo= playBuyMysteryShopItem(shopInfo.getItemId(),mysteryShopStatus);
//			
//			AwardItems awardItems = resultVo.getT();
//			int errorCode = resultVo.getErrorCode();
//			if (awardItems == null) {
//				sendError(protType, errorCode);
//				return true;
//			}
//			mysteryShopStatus.removePlayerShopItem(resultVo.getRomveShopItem());
//			player.getPlayerData().updateActivity( Const.ActivityId.MYSTERY_SHOP_VALUE, timeCfg.getStageId());
//			
//			awardItems.rewardTakeAffectAndPush(player, Action.MYSTERY_SHOP_BUY_TOOLS);
//			SteriousShopInfoMsgRet.Builder builder = getSteriousShopInfoMsgRetBuilder(type, mysteryShopStatus.getPlayerMysteryShopItemList(),mysteryShopStatus.getDaliyRefreshNum());
//			builder.setActivitylefttimes(timeCfg.calcActivitySurplusTime());
//			sendProtocol(Protocol.valueOf(HP.code.SHOP_STERIOUS_S_VALUE,builder));
//		}
//		/** 购买全部 */
//		else if (type == TYPE_SHOP_BUY_ALL) {
//			if(mysteryShopStatus.getPlayerMysteryShopItemList() == null || mysteryShopStatus.getPlayerMysteryShopItemList().size() == 0){
//				sendError(protType, Status.error.DATA_NOT_FOUND_VALUE);
//				return true;
//			}
//			int sumCost = 0;
//			for (PlayerShopItem playerShopItem : mysteryShopStatus.getPlayerMysteryShopItemList()) {
//				sumCost += playerShopItem.getBuyPrice();
//			}
//			if(player.getGold() < sumCost){
//				sendError(protType, Status.error.GOLD_NOT_ENOUGH_VALUE);
//				return true;
//			}
//			
//			AwardItems awardItemList = new AwardItems();
//			List<PlayerShopItem> removeShopItemList = new ArrayList<>();
//			int errorCode = 0;
//			for (PlayerShopItem playerShopItem : mysteryShopStatus.getPlayerMysteryShopItemList()) {
//				ShopResultVo<AwardItems> resultVo = playBuyMysteryShopItem(playerShopItem.getId(),mysteryShopStatus);
//				AwardItems awardItem = resultVo.getT();
//				errorCode = resultVo.getErrorCode();
//				if(awardItem == null){
//					continue;
//				}
//				if (errorCode == -1) {
//					break;
//				}
//				List<Item> itemList = awardItem.getAwardItems();
//				for (Item item : itemList) {
//					awardItemList.addItem(item);
//				}
//				removeShopItemList.add(resultVo.getRomveShopItem());
//			}
//			if (errorCode > 0) {
//				sendError(protType, errorCode);
//				return true;
//			}
//			if(awardItemList.getAwardItemInfos().size() != 0){
//				mysteryShopStatus.removePlayerShopItem(removeShopItemList);
//				player.getPlayerData().updateActivity( Const.ActivityId.MYSTERY_SHOP_VALUE, timeCfg.getStageId());
//
//				awardItemList.rewardTakeAffectAndPush(player, Action.MYSTERY_SHOP_BUY_TOOLS);
//				SteriousShopInfoMsgRet.Builder builder = getSteriousShopInfoMsgRetBuilder(type, mysteryShopStatus.getPlayerMysteryShopItemList(),mysteryShopStatus.getDaliyRefreshNum());
//				builder.setActivitylefttimes(timeCfg.calcActivitySurplusTime());
//				sendProtocol(Protocol.valueOf(HP.code.SHOP_STERIOUS_S_VALUE,builder));
//			}
//		}
//		/** 刷新 */
//		else if (type == TYPE_SHOP_REFASH) {
//			
//			//当玩家处于黑市商品界面，但活动正好结束
//			if(!timeCfg.isActive()){
//				// 活动已关闭
//				sendError(protType, Status.error.ACTIVITY_CLOSE);
//				return true;
//			}
//			List<PlayerShopItem> shopList = mysteryShopStatus.getPlayerMysteryShopItemList();
//			if(shopList.size()>0){
//				int totalRefreshNum = SysBasicCfg.getInstance().getRefreshMysteryCostGoldNums().length;
//				int daliyRefreshNum = mysteryShopStatus.getDaliyRefreshNum();
//				if(daliyRefreshNum >= totalRefreshNum){
//					sendError(protType, Status.error.DATA_NOT_FOUND_VALUE);
//					return true;
//				}
//				
//				int refreshGold = 0;
//				if(daliyRefreshNum >= totalRefreshNum){
//					refreshGold = ShopManager.getRefreshMysteryShopGoldNum(totalRefreshNum-1);
//				}else{
//					refreshGold = ShopManager.getRefreshMysteryShopGoldNum(daliyRefreshNum);
//				}
//				
//				if (refreshGold == 0) {
//					sendError(protType, Status.error.DATA_NOT_FOUND_VALUE);
//					return true;
//				}
//				if (refreshGold > player.getCoin()) {
//					sendError(protType, Status.error.COINS_NOT_ENOUGH_VALUE);
//					return true;
//				}
//				ConsumeItems.valueOf(changeType.CHANGE_COIN, refreshGold).consumeTakeAffect(player, Action.MYSTERY_SHOP_REFRESH);
//				List<PlayerShopItem> list = ShopManager.getNewMysteryPlayerShopItems(player, false,timeCfg.getStageId(),mysteryShopStatus);
//				mysteryShopStatus.setCurrentItemNum(list.size());
//				mysteryShopStatus.shopItemToString(list);
//				mysteryShopStatus.increaseDaliyRefreshNum();
//				mysteryShopStatus.setRefreshDate(new Date());
//				player.getPlayerData().updateActivity( Const.ActivityId.MYSTERY_SHOP_VALUE, timeCfg.getStageId());
//
//				SteriousShopInfoMsgRet.Builder builder = SteriousShopInfoMsgRet.newBuilder();
//				
//				for (PlayerShopItem playerShopItem : mysteryShopStatus.getPlayerMysteryShopItemList()) {
//					builder.addShopItems(ShopManager.getShopItemInforBuilder(playerShopItem));
//				}
//				builder.setType(type);
//				builder.setRefreshCount(totalRefreshNum-mysteryShopStatus.getDaliyRefreshNum());
//				int nextRefreshGold= 0;
//				daliyRefreshNum = mysteryShopStatus.getDaliyRefreshNum();
//				if(daliyRefreshNum >= totalRefreshNum){
//					nextRefreshGold = ShopManager.getRefreshMysteryShopGoldNum(totalRefreshNum-1);
//				}else{
//					nextRefreshGold = ShopManager.getRefreshMysteryShopGoldNum(daliyRefreshNum);
//				}
//				builder.setRefreshPrice(nextRefreshGold);
//				builder.setLefttimes(diffTimeMillis());
//				builder.setActivitylefttimes(timeCfg.calcActivitySurplusTime());
//				sendProtocol(Protocol.valueOf(HP.code.SHOP_STERIOUS_S_VALUE,builder));
//			}else{
//				sendError(protType, Status.error.DATA_NOT_FOUND_VALUE);
//				return true;
//			}
//		}
//		return true;
//	}
//
//	/**
//	 * 获取玩家黑市数据(用于返回给客户端)
//	 * 
//	 * @param type
//	 * @param shopList
//	 * @param daliyRefreshNum
//	 * @return
//	 */
//	public SteriousShopInfoMsgRet.Builder getSteriousShopInfoMsgRetBuilder(
//			int type, List<PlayerShopItem> shopList, int daliyRefreshNum) {
//		SteriousShopInfoMsgRet.Builder builder = SteriousShopInfoMsgRet.newBuilder();
//		builder.setType(type);
//		for (PlayerShopItem playerShopItem : shopList) {
//			if (playerShopItem != null) {
//				builder.addShopItems(ShopManager.getShopItemInforBuilder(playerShopItem));
//			}
//		}
//		int totalRefreshNum = SysBasicCfg.getInstance().getRefreshMysteryCostGoldNums().length;
//		if(daliyRefreshNum == totalRefreshNum){
//			int refreshGold = ShopManager.getRefreshMysteryShopGoldNum(totalRefreshNum-1);
//			builder.setRefreshPrice(refreshGold);
//		}else{
//			int refreshGold = ShopManager.getRefreshMysteryShopGoldNum(daliyRefreshNum);
//			builder.setRefreshPrice(refreshGold);
//		}
//		builder.setRefreshCount(totalRefreshNum - daliyRefreshNum);
//		builder.setLefttimes(diffTimeMillis());
//		return builder;
//	}
//	
//	/**
//	 * 活动结束返回黑市商人数据
//	 * @param type
//	 * @return
//	 */
//	public SteriousShopInfoMsgRet.Builder getCloseSteriousShopInfoMsgRetBuilder(int type){
//		SteriousShopInfoMsgRet.Builder builder = SteriousShopInfoMsgRet.newBuilder();
//		ShopItemInfo.Builder itemBuilder = ShopItemInfo.newBuilder();
//		itemBuilder.setId("");
//		itemBuilder.setItemId(0);
//		itemBuilder.setBuyType(0);
//		itemBuilder.setBuyPrice(0);
//		itemBuilder.setBuyDiscont(0);
//		itemBuilder.setItemCount(0);
//		itemBuilder.setItemType(0);
//		itemBuilder.setLevel(1);
//		itemBuilder.setIsAdd(false);
//		builder.setType(type);
//		builder.addShopItems(itemBuilder);
//		builder.setRefreshCount(0);
//		builder.setLefttimes(0);
//		builder.setActivitylefttimes(0);
//		builder.setRefreshPrice(0);
//		return builder;
//	}
//	
//	/**
//	 * 黑市购买物品
//	 * @param itemId
//	 * @param mysteryShopStatus
//	 * @return
//	 */
//	public ShopResultVo<AwardItems> playBuyMysteryShopItem(String itemId,MysteryShopStatus mysteryShopStatus){
//		ShopResultVo<AwardItems> resultVo = new ShopResultVo<AwardItems>();
//		if(mysteryShopStatus == null){
//			resultVo.setErrorCode(Status.error.DATA_NOT_FOUND_VALUE);
//			return resultVo;
//		}
//		PlayerShopItem shopItem = mysteryShopStatus.getPlayerShopItem(itemId);
//		if(shopItem == null){
//			resultVo.setErrorCode(Status.error.SHOP_ITEM_NOT_FOUND_VALUE);
//			return resultVo;
//		}
//		int buyPrice = shopItem.getBuyPrice();
//		
//		if(player.getGold() < buyPrice){
//			resultVo.setErrorCode(Status.error.GOLD_NOT_ENOUGH_VALUE);
//			return resultVo;
//		}
//		//扣除钻石
//		ConsumeItems.valueOf(changeType.CHANGE_GOLD, buyPrice).consumeTakeAffect(player, Action.MYSTERY_SHOP_BUY_TOOLS);
//		// 添加道具
//		AwardItems awardItems = new AwardItems();
//		awardItems.addItem(shopItem.getItemType(), shopItem.getItemId(), shopItem.getItemCount());
//		
//		BehaviorLogger.log4Platform(player, Action.MYSTERY_SHOP_BUY_TOOLS, Params.valueOf("itemId", shopItem.getItemId()),
//				Params.valueOf("itemCount", shopItem.getItemCount()),
//				Params.valueOf("itemType", shopItem.getItemType()),
//				Params.valueOf("buyType", shopItem.getBuyType()),
//				Params.valueOf("buyPrice", shopItem.getBuyPrice()));
//
//		resultVo.setT(awardItems);
//		resultVo.setRomveShopItem(shopItem);
//		return resultVo;
//	}
//	
//	/**
//	 * 黑市竞拍
//	 * @param auctioInfo
//	 * @param protType
//	 * @param timeCfg
//	 */
//	public boolean dealWithAuctionProcess(AuctionInfoMsg playerAuctionInfo,int protType,ActivityTimeCfg timeCfg){
//		MysteryShopAuctionStatus serverAuctionItemInfo = ServerData.getInstance().getServerStatus(GsConst.ServerStatusId.MYSTERY_SHOP_AUCTION, MysteryShopAuctionStatus.class);
//		
//		int type = playerAuctionInfo.getType();
//		MysteryShopStatus mysteryShopStatus = ActivityUtil.getActivityStatus(player.getPlayerData(),  Const.ActivityId.MYSTERY_SHOP_VALUE,timeCfg.getStageId(), MysteryShopStatus.class);
//		/**初始化  */
//		if(type == TYPE_AUCTION_INIT){
//			if(serverAuctionItemInfo.getItemId() == 0){
//				
//				serverAuctionItemInfo = ShopManager.getNewMysteryAuctionItem(player,serverAuctionItemInfo);
//				serverAuctionItemInfo.setActivityId(timeCfg.getActivityId());
//				serverAuctionItemInfo.setStageId(timeCfg.getStageId());
//				serverAuctionItemInfo.setIsAward(0);
//				ServerData.getInstance().updateServerData(GsConst.ServerStatusId.MYSTERY_SHOP_AUCTION);
//			}
//			
//			AuctionInfoMsgRet.Builder builder = getInitAuctionInfoMsgRetBuilder(serverAuctionItemInfo);
//			builder.setType(type);
//			builder.setMyprices(mysteryShopStatus.getAuctionItemPrice());
//			builder.setLefttimes(timeCfg.calcActivitySurplusTime());
//			sendProtocol(Protocol.valueOf(HP.code.SHOP_AUCTION_S_VALUE,builder));
//		}
//		/**竞价 */
//		else if(type == TYPE_AUCTION_BIDDING){
//			int myAuctionPrice = playerAuctionInfo.getAuctionprices();
//			int currentHighestPrice = serverAuctionItemInfo.getAuctionPrice();
//			if(myAuctionPrice <= 0){
//				sendError(protType,Status.error.AUCTION_PRICE_ERROR_VALUE);
//				AuctionInfoMsgRet.Builder builder = getErrorAuctionInfoMsgBuilder(type, myAuctionPrice, currentHighestPrice,timeCfg);
//				sendProtocol(Protocol.valueOf(HP.code.SHOP_AUCTION_S_VALUE,builder));
//				return true;
//			}
//			
//			if(myAuctionPrice > player.getGold()){
//				sendError(protType,Status.error.AUCTION_GOLD_NOT_ENOUGH_VALUE);
//				AuctionInfoMsgRet.Builder builder = getErrorAuctionInfoMsgBuilder(type, myAuctionPrice, currentHighestPrice,timeCfg);
//				sendProtocol(Protocol.valueOf(HP.code.SHOP_AUCTION_S_VALUE,builder));
//				return true;
//			}
//			
//			if(myAuctionPrice <= currentHighestPrice){
//				sendError(protType,Status.error.AUCTION_NEED_MORE_COIN_VALUE);
//				AuctionInfoMsgRet.Builder builder = getErrorAuctionInfoMsgBuilder(type, myAuctionPrice, currentHighestPrice,timeCfg);
//				sendProtocol(Protocol.valueOf(HP.code.SHOP_AUCTION_S_VALUE,builder));
//				return true;
//			}
//			
//			//物品不是第一次被竞拍
//			if(currentHighestPrice != 0){
//				//扣除钻石
//				ConsumeItems.valueOf(changeType.CHANGE_GOLD, myAuctionPrice).consumeTakeAffect(player, Action.MYSTERY_SHOP_AUCTION);
//				
//				String awardInfo = "10000_1001_"+serverAuctionItemInfo.getAuctionPrice();
//				//返还上次竞价最高玩家的钻石
//				AwardItems awardItems = AwardItems.valueOf(awardInfo);
//				MailManager.createMail(serverAuctionItemInfo.getPlayerId(), Mail.MailType.Reward_VALUE, GsConst.MailId.AUCTION, "", awardItems);
//				Log.logPrintln("send auction gold ,playerId :" + serverAuctionItemInfo.getPlayerId());
//			}
//			
//			currentHighestPrice = myAuctionPrice;
//			serverAuctionItemInfo.setAuctionPrice(currentHighestPrice);
//			serverAuctionItemInfo.setPlayerId(player.getId());
//			//更新竞拍物品最高竞拍价格及出价最高的玩家id
//			ServerData.getInstance().updateServerData(GsConst.ServerStatusId.MYSTERY_SHOP_AUCTION);
//			
//			mysteryShopStatus.setAuctionItemPrice(myAuctionPrice);
//			//更新玩家竞拍信息
//			player.getPlayerData().updateActivity( Const.ActivityId.MYSTERY_SHOP_VALUE, timeCfg.getStageId());
//			
//			BehaviorLogger.log4Platform(player, Action.MYSTERY_SHOP_AUCTION, Params.valueOf("itemId", serverAuctionItemInfo.getItemId()),
//					Params.valueOf("itemCount", serverAuctionItemInfo.getItemCount()),
//					Params.valueOf("itemType", serverAuctionItemInfo.getItemType()),
//					Params.valueOf("auctionPrice", serverAuctionItemInfo.getAuctionPrice()));
//			
//			serverAuctionItemInfo = ServerData.getInstance().getServerStatus(GsConst.ServerStatusId.MYSTERY_SHOP_AUCTION, MysteryShopAuctionStatus.class);
//			mysteryShopStatus = ActivityUtil.getActivityStatus(player.getPlayerData(),  Const.ActivityId.MYSTERY_SHOP_VALUE,timeCfg.getStageId(), MysteryShopStatus.class);
//			
//			AuctionInfoMsgRet.Builder builder  = AuctionInfoMsgRet.newBuilder();
//			builder.setType(type);
//			builder.setLefttimes(timeCfg.calcActivitySurplusTime());
//			builder.setCurprices(currentHighestPrice);
//			builder.setMyprices(myAuctionPrice);
//			builder.setIssucceed(1);
//			sendProtocol(Protocol.valueOf(HP.code.SHOP_AUCTION_S_VALUE,builder));
//		}
//		//刷新价格
//		else if(type == TYPE_AUCTION_REFRESH){
//			//当玩家处于黑市商品界面，但活动正好结束
//			if(timeCfg.isEnd()){
//				// 活动已关闭
//				sendError(protType, Status.error.ACTIVITY_CLOSE);
//				return true;
//			}
//			serverAuctionItemInfo = ServerData.getInstance().getServerStatus(GsConst.ServerStatusId.MYSTERY_SHOP_AUCTION, MysteryShopAuctionStatus.class);
//			AuctionInfoMsgRet.Builder builder  = AuctionInfoMsgRet.newBuilder();
//			builder.setType(type);
//			builder.setCurprices(serverAuctionItemInfo.getAuctionPrice());
//			builder.setMyprices(mysteryShopStatus.getAuctionItemPrice());
//			sendProtocol(Protocol.valueOf(HP.code.SHOP_AUCTION_S_VALUE,builder));
//		}
//		return true;
//	}
//	
//	/**
//	 * 竞拍结束给竞价最高的玩家发放当期竞拍物品
//	 */
//	public void sendAuctionAwardItem(ActivityTimeCfg timeCfg){
//		MysteryShopAuctionStatus serverAuctionItemInfo = ServerData.getInstance().getServerStatus(GsConst.ServerStatusId.MYSTERY_SHOP_AUCTION, MysteryShopAuctionStatus.class);
//		int isAward = serverAuctionItemInfo.getIsAward();
//		if(isAward == 0){
//			ChatMsg chatMsg = new ChatMsg();
//			//玩家参与竞拍
//			if(serverAuctionItemInfo.getAuctionPrice() != 0){
//				// 添加道具
//				AwardItems awardItems = new AwardItems();
//				Player awardPlayer = PlayerUtil.queryPlayer(serverAuctionItemInfo.getPlayerId());
//				awardItems.addItem(serverAuctionItemInfo.getItemType(), serverAuctionItemInfo.getItemId(), serverAuctionItemInfo.getItemCount());
//				MailManager.createMail(serverAuctionItemInfo.getPlayerId(), Mail.MailType.Reward_VALUE, GsConst.MailId.AUCTION_SUCCESS, "", awardItems);
//				BehaviorLogger.log4Platform(awardPlayer, Action.MYSTERY_SHOP_AUCTION_AWARD, 
//						Params.valueOf("itemId", serverAuctionItemInfo.getItemId()),
//						Params.valueOf("itemCount", serverAuctionItemInfo.getItemCount()),
//						Params.valueOf("itemType", serverAuctionItemInfo.getItemType()),
//						Params.valueOf("auctionPrice", serverAuctionItemInfo.getAuctionPrice()));
//				AuctionInfoMsgRet.Builder builder = getInitAuctionInfoMsgRetBuilder(serverAuctionItemInfo);
//				builder.setType(TYPE_AUCTION_INIT);
//				builder.setMyprices(serverAuctionItemInfo.getAuctionPrice());
//				builder.setOwner(awardPlayer.getName());
//				builder.setLefttimes(timeCfg.calcActivitySurplusTime());
//				sendProtocol(Protocol.valueOf(HP.code.SHOP_AUCTION_S_VALUE,builder));
//				
//				serverAuctionItemInfo.setIsAward(1);//已发放
//				ServerData.getInstance().updateServerData(GsConst.ServerStatusId.MYSTERY_SHOP_AUCTION);
//				
//				String notice = ChatManager.getMsgJson(SysBasicCfg.getInstance().getAuctionWinnerBroadCast(),awardPlayer.getName());
//				chatMsg.setType(Const.chatType.WORLD_BROADCAST_VALUE);
//				chatMsg.setChatMsg(notice);
//				ChatManager.getInstance().postBroadcast(chatMsg);
//				
//			}else{
//				AuctionInfoMsgRet.Builder builder = getInitAuctionInfoMsgRetBuilder(serverAuctionItemInfo);
//				builder.setType(2);
//				builder.setMyprices(serverAuctionItemInfo.getAuctionPrice());
//				builder.setOwner("");
//				sendProtocol(Protocol.valueOf(HP.code.SHOP_AUCTION_S_VALUE,builder));
//				
//				String notice = ChatManager.getMsgJson(SysBasicCfg.getInstance().getAuctionFailBroadCast());
//				chatMsg.setType(Const.chatType.WORLD_BROADCAST_VALUE);
//				chatMsg.setChatMsg(notice);
//				ChatManager.getInstance().postBroadcast(chatMsg);
//			}
//		}else{
//			AuctionInfoMsgRet.Builder builder = getInitAuctionInfoMsgRetBuilder(serverAuctionItemInfo);
//			builder.setType(TYPE_AUCTION_INIT);
//			MysteryShopStatus mysteryShopStatus = ActivityUtil.getActivityStatus(player.getPlayerData(),  Const.ActivityId.MYSTERY_SHOP_VALUE,timeCfg.getStageId(), MysteryShopStatus.class);
//			builder.setMyprices(mysteryShopStatus.getAuctionItemPrice());
//			if(serverAuctionItemInfo.getAuctionPrice() == 0){
//				builder.setOwner("");
//			}else{
//				Player awardPlayer = PlayerUtil.queryPlayer(serverAuctionItemInfo.getPlayerId());
//				builder.setOwner(awardPlayer.getName());
//			}
//			
//			builder.setLefttimes(timeCfg.calcActivitySurplusTime());
//			sendProtocol(Protocol.valueOf(HP.code.SHOP_AUCTION_S_VALUE,builder));
//		}
//	}
//
//	/**
//	 * 竞拍失败返回信息
//	 * @param type
//	 * @param myAuctionPrice
//	 * @param currentHighestPrice
//	 * @return
//	 */
//	private AuctionInfoMsgRet.Builder getErrorAuctionInfoMsgBuilder(int type,
//			int myAuctionPrice, int currentHighestPrice,ActivityTimeCfg timeCfg) {
//		AuctionInfoMsgRet.Builder builder = AuctionInfoMsgRet.newBuilder();
//		builder.setType(type);
//		builder.setLefttimes(timeCfg.calcActivitySurplusTime());
//		builder.setCurprices(currentHighestPrice);
//		builder.setMyprices(myAuctionPrice);
//		builder.setIssucceed(0);
//		return builder;
//	}
//	
//	/**
//	 * 获取竞拍初始化信息
//	 * @param auctionItemInfo
//	 * @return
//	 */
//	public AuctionInfoMsgRet.Builder getInitAuctionInfoMsgRetBuilder(MysteryShopAuctionStatus auctionItemInfo){
//		
//		AuctionInfoMsgRet.Builder auctionInfobuilder = AuctionInfoMsgRet.newBuilder();
//		AuctionDrop.Builder dropBuilder = AuctionDrop.newBuilder();
//		
//		dropBuilder.setItemId(auctionItemInfo.getItemId());
//		dropBuilder.setItemType(auctionItemInfo.getItemType());
//		dropBuilder.setItemCount(auctionItemInfo.getItemCount());
//		dropBuilder.setLevel(0);
//		auctionInfobuilder.setCurprices(auctionItemInfo.getAuctionPrice());
//		auctionInfobuilder.setDrop(dropBuilder);
//		return auctionInfobuilder;
//	}
//
//	/**
//	 * 获取活动结束时间与当前时间差
//	 * @param timeCfg
//	 * @return
//	 */
//	public static int diffTimeMillis(ActivityTimeCfg timeCfg){
//		DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//		String endTime = timeCfg.getEndTime().replace("_", " ");
//		try {
//			Date d1 = df.parse(endTime);
//			long diff = d1.getTime() - System.currentTimeMillis();
//			return (int)(diff/1000);
//		} catch (ParseException e) {
//			e.printStackTrace();
//		}
//		return 0;
//	}
//	
//	public static boolean isSameDay(Date date1){
//		if(date1 == null){
//			return false;
//		}
//		Calendar cal1 = Calendar.getInstance();
//        cal1.setTime(date1);
//        Calendar cal2 = Calendar.getInstance();
//        cal2.setTime(new Date());
//        boolean isSameDate = cal1.get(Calendar.DAY_OF_MONTH) == cal2
//                        .get(Calendar.DAY_OF_MONTH);
//        return isSameDate;
//	}
//	
//	/**
//	 * 获取当天24点与当前时间差
//	 * @return
//	 */
//	public static int diffTimeMillis(){
//		
//		Calendar calendar = Calendar.getInstance();
//	    calendar.set(Calendar.HOUR_OF_DAY, 23);
//	    calendar.set(Calendar.MINUTE, 59);
//	    calendar.set(Calendar.SECOND, 59);
//	    calendar.set(Calendar.MILLISECOND, 999);
//	    Date date=calendar.getTime();
//	    long diff = date.getTime() - System.currentTimeMillis();
//	    return (int)(diff/1000);
//	}
//	/**
//	 * 获得当天24点时间
//	 * @return
//	 */
//	public static Date getTimesnight() {
//		Calendar cal = Calendar.getInstance();
//		cal.set(Calendar.HOUR_OF_DAY, 24);
//		cal.set(Calendar.SECOND, 0);
//		cal.set(Calendar.MINUTE, 0);
//		cal.set(Calendar.MILLISECOND, 0);
//		return cal.getTime();
//	}
//	
//
//	/**
//	 * 是否在活动有效期内
//	 * @param timeCfg
//	 * @return
//	 */
//	public boolean isTimeValidate(ActivityTimeCfg timeCfg){
//		String startTime = timeCfg.getStartTime().replace("_", " ");
//		String endTime = timeCfg.getEndTime().replace("_", " ");
//		Long currentTimeMullis = System.currentTimeMillis();
//		SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//		try {
//			long startTimeMillis=sdf.parse(startTime).getTime();
//			long endTimeMillis=sdf.parse(endTime).getTime();
//			if((currentTimeMullis>startTimeMillis)&&(currentTimeMullis<endTimeMillis)){
//				return true;
//			}
//		} catch (ParseException e) {
//			e.printStackTrace();
//		}
//		return false;
//	}
//}
