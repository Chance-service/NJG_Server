package com.guaji.game.module.activity.activity147;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.guaji.net.protocol.Protocol;
import org.guaji.os.GuaJiRand;
import org.guaji.os.GuaJiTime;

import com.guaji.game.config.ActivityTimeCfg;
import com.guaji.game.config.SysBasicCfg;
import com.guaji.game.config.WishingMilestoneCfg;
import com.guaji.game.config.WishingWellCfg;
import com.guaji.game.item.AwardItems;
import com.guaji.game.item.ConsumeItems;
import com.guaji.game.item.ItemInfo;
import com.guaji.game.log.BehaviorLogger;
import com.guaji.game.log.BehaviorLogger.Action;
import com.guaji.game.log.BehaviorLogger.Params;
import com.guaji.game.log.BehaviorLogger.Source;
import com.guaji.game.player.Player;
import com.guaji.game.protocol.Activity4.WishingItemInfo;
import com.guaji.game.protocol.Activity4.WishingWellInfo;
import com.guaji.game.protocol.Const;
import com.guaji.game.protocol.HP;
import com.guaji.game.protocol.Status;
import com.guaji.game.util.ActivityUtil;



public class Activity147WishingManager {
	/**
	 * 抽獎
	 * @param action 1.單抽 2.十抽
	 * @param type
	 * @param player
	 * @param status
	 */
	static  void WishingDraw(int action,int type,Player player,Activity147Status status) {
						
		int count = 1;
		
		if (action == Activity147Handler.TEN_DRAW) {
			count = 10;
		}
		
		boolean freedraw = false;
		if (action == Activity147Handler.FREE_DRAW) { // 免費抽
			if (type != Activity147Handler.STAR_WELL) {
				player.sendError(HP.code.ACTIVITY147_WISHING_DRAW_C_VALUE,Status.error.PARAMS_INVALID_VALUE); 
				return;
			}
			if (status.getFreeDraw() <= 0) {
				player.sendError(HP.code.ACTIVITY147_WISHING_DRAW_C_VALUE,Status.error.PARAMS_INVALID_VALUE); 
				return;
			}
			freedraw = true;
		}
		
		if (!freedraw) {
			String costItems = SysBasicCfg.getInstance().getWishingCostItem(type-1);
			
			if (costItems.isEmpty()) {
				player.sendError(HP.code.ACTIVITY147_WISHING_DRAW_C_VALUE,Status.error.ITEM_NOT_FOUND_VALUE); 
				return;
			}
			
			ConsumeItems consumeItems = ConsumeItems.valueOf();
			ItemInfo itemInfo = ItemInfo.valueOf(costItems);
			itemInfo.setQuantity(itemInfo.getQuantity()*count);
			
			List<ItemInfo> itemList = new ArrayList<ItemInfo>();
			itemList.add(itemInfo);
			boolean isAdd = false ;
			isAdd = consumeItems.addConsumeInfo(player.getPlayerData(),itemList);
			
			if (isAdd && consumeItems.checkConsume(player)) {
				if(!consumeItems.consumeTakeAffect(player, Action.ACTIVITY147_WISHING_WELLS)) {
					player.sendError(HP.code.ACTIVITY147_WISHING_DRAW_C_VALUE,Status.error.ITEM_NOT_ENOUGH_VALUE); 
					return; 
				} 
			} else {
				player.sendError(HP.code.ACTIVITY147_WISHING_DRAW_C_VALUE,Status.error.ITEM_NOT_ENOUGH_VALUE); 
				return; 
			}
		} else {
			status.setFreeDraw(status.getFreeDraw()-1);
		}
		
		int activityId = Const.ActivityId.ACTIVITY147_WISHING_WELL_VALUE;
		ActivityTimeCfg timeCfg = ActivityUtil.getCurActivityTimeCfg(activityId);
		String awradStr = "";
		AwardItems nowawardItems = new AwardItems();
		Set<Integer> IdxSet = status.getCfgIdxList(type);
		List<Integer> gotidxList = new ArrayList<Integer>();
		String allaward ="";
		for (int i = 0 ; i < count ; i++) {
			List<Integer> cfgList = new ArrayList<Integer>();
			List<Integer> weightList = new ArrayList<Integer>();
 			for (Integer idx :status.getCfgIdxList(type)) {
				cfgList.add(idx);
				weightList.add(1); // 沒有權重機率平均
			}
 			int cfgIndex = GuaJiRand.randonWeightObject(cfgList, weightList);
 			status.incLucky(type,SysBasicCfg.getInstance().getWishingIncLucky());
 			WishingWellCfg acfg = WishingWellCfg.getWingCfgByKey(cfgIndex);
 			IdxSet.remove(cfgIndex);
 			gotidxList.add(cfgIndex);
 			if (acfg == null) {
 				player.sendError(HP.code.ACTIVITY147_WISHING_DRAW_C_VALUE,Status.error.CONFIG_ERROR);
 				return;
 			}
 			awradStr = acfg.getItem();
 			if (allaward.isEmpty()) {
 				allaward = awradStr;
 			} else {
 				allaward = allaward+","+awradStr;
 			}
 			if ((acfg.getArea() >= 1) && (acfg.getArea() <= 7)) {
 				status.setAwradTake(type, awradStr, true);
 			}
			ItemInfo nowitemInfos = ItemInfo.valueOf(awradStr);
			nowawardItems.addItem(nowitemInfos);

			if (status.gameEnd(type)) {
				Activity147WishingManager.initWishing(type, player, status,activityId,timeCfg.getStageId());
				IdxSet = status.getCfgIdxList(type);
			}
		}
		nowawardItems.rewardTakeAffectAndPush(player, Action.ACTIVITY147_WISHING_WELLS, 0);
		
		// BI 日志 ()
		BehaviorLogger.log4Service(player,Source.USER_OPERATION, Action.ACTIVITY147_WISHING_WELLS, Params.valueOf("action", action),Params.valueOf("count", count),
				Params.valueOf("type", type),Params.valueOf("gotidxList", gotidxList),
				Params.valueOf("freedraw", freedraw),
				Params.valueOf("allaward", allaward));
		
		player.getPlayerData().updateActivity(activityId, timeCfg.getStageId());
		
		WishingWellInfo.Builder builder = Activity147WishingManager.generateInfo(action,player,type,status,timeCfg);

		builder.setReward(allaward);
		
		player.sendProtocol(Protocol.valueOf(HP.code.ACTIVITY147_WISHING_DRAW_S_VALUE, builder));
		
	}
	/**
	 *  領取累積幸運值獎品
	 * @param action
	 * @param type
	 * @param player
	 * @param status
	 */
	static void takeLuckyAward(int action,int type,Player player,Activity147Status status) {
		int activityId = Const.ActivityId.ACTIVITY147_WISHING_WELL_VALUE;
		ActivityTimeCfg timeCfg = ActivityUtil.getCurActivityTimeCfg(activityId);
		
		WishingMilestoneCfg milestoneCfg = WishingMilestoneCfg.getWingCfgByKey(type);
		if (milestoneCfg == null) {
				player.sendError(HP.code.ACTIVITY147_WISHING_DRAW_C_VALUE,Status.error.CONFIG_ERROR);
				return;
		}
		
		int takeMark = status.getTake(type); // 領到第幾個
		int lucky = status.getLucky(type);
		int needPoint = 0;
		ItemInfo reward = null;
		List<Integer> PointList = milestoneCfg.getPointList();
		List<ItemInfo> RewardList = milestoneCfg.getRewardList();
		AwardItems nowawardItems = null;
		if ((takeMark >=0) && (takeMark < PointList.size())){
			int targetIdx = takeMark;
			needPoint = PointList.get(targetIdx);
			reward =  RewardList.get(targetIdx);
			if (lucky >= needPoint) {
				status.incTake(type, 1);
				nowawardItems = new AwardItems();
				nowawardItems.addItem(reward);
				nowawardItems.rewardTakeAffectAndPush(player, Action.ACTIVITY147_WISHING_WELLS, 0);
			} else {
				player.sendError(HP.code.ACTIVITY147_WISHING_DRAW_C_VALUE,Status.error.CRYSTAL_NOT_ENOUGH);
				return;
			}
		}
		takeMark = status.getTake(type); // 再次檢查領到第幾個,有沒有到最後了
		
		if (takeMark >= PointList.size()){ //沒獎品了,全部領完,扣除領取全部獎品所需的幸運值
			status.setTake(type, 0);
			int pointVaule = PointList.get(PointList.size()-1);
			status.decLucky(type, pointVaule);
		}
		
		player.getPlayerData().updateActivity(activityId, timeCfg.getStageId());
		
		BehaviorLogger.log4Service(player,Source.USER_OPERATION, Action.ACTIVITY147_WISHING_WELLS, Params.valueOf("action", action),Params.valueOf("type", type),
		        Params.valueOf("takeMark", takeMark),Params.valueOf("lucky", lucky),Params.valueOf("needPoint", needPoint),
		        Params.valueOf("awardItems", nowawardItems.toDbString()));
		
		WishingWellInfo.Builder builder = Activity147WishingManager.generateInfo(action,player,type,status,timeCfg);
		if (nowawardItems != null) {
			builder.setReward(nowawardItems.toString());
		}
		
		player.sendProtocol(Protocol.valueOf(HP.code.ACTIVITY147_WISHING_DRAW_S_VALUE, builder));
	}
	/**
	 *  刷新獎池
	 * @param action 4.免費 5.收費
	 * @param type
	 * @param player
	 * @param status
	 */
	static void RefreshPool(int action,int type,Player player,Activity147Status status) {
		if (action == Activity147Handler.FREE_REFRESH) { // 免費檢查
			int refreshTime = status.getRefreshFreeTime(type);
			int currSeconds = GuaJiTime.getSeconds();
			if ((refreshTime > 0) && (currSeconds >= refreshTime)) {
				int aTime = currSeconds - refreshTime;
				if (aTime < SysBasicCfg.getInstance().getWishingRefreshTime()) {
					player.sendError(HP.code.ACTIVITY147_WISHING_DRAW_C_VALUE,Status.error.ALREADY_ASKTICKET);
					return;
				}
			}
			status.setRefreshFreeTime(type, currSeconds);
		} else if (action == Activity147Handler.COST_REFRESH) {
			ConsumeItems consumeItems = ConsumeItems.valueOf();
			List<ItemInfo> itemList = ItemInfo.valueListOf(SysBasicCfg.getInstance().getWishingRefreshCost());
			boolean isAdd = false ;
			isAdd = consumeItems.addConsumeInfo(player.getPlayerData(),itemList);
			
			if (isAdd && consumeItems.checkConsume(player)) {
				if(!consumeItems.consumeTakeAffect(player, Action.ACTIVITY147_WISHING_WELLS)) {
					player.sendError(HP.code.ACTIVITY147_WISHING_DRAW_C_VALUE,Status.error.ITEM_NOT_ENOUGH_VALUE); 
					return; 
				} 
			} else {
				player.sendError(HP.code.ACTIVITY147_WISHING_DRAW_C_VALUE,Status.error.ITEM_NOT_ENOUGH_VALUE); 
				return; 
			}
			
		} else {
			player.sendError(HP.code.ACTIVITY147_WISHING_DRAW_C_VALUE,Status.error.PARAMS_INVALID_VALUE);
			return;
		}
		
		int activityId = Const.ActivityId.ACTIVITY147_WISHING_WELL_VALUE;
		ActivityTimeCfg timeCfg = ActivityUtil.getCurActivityTimeCfg(activityId);
		
		initWishing(type, player, status, activityId, timeCfg.getStageId());
		
		player.getPlayerData().updateActivity(activityId, timeCfg.getStageId());
		
		BehaviorLogger.log4Service(player,Source.USER_OPERATION, Action.ACTIVITY147_WISHING_WELLS, Params.valueOf("action", action),Params.valueOf("type", type));
		
		WishingWellInfo.Builder builder = Activity147WishingManager.generateInfo(action,player,type,status,timeCfg);
		
		player.sendProtocol(Protocol.valueOf(HP.code.ACTIVITY147_WISHING_DRAW_S_VALUE, builder));
	}
	/**
	 * 初始化獎池
	 * @param kind
	 * @param player
	 * @param status
	 * @param activityId
	 * @param StageId
	 */
	static void initWishing(int kind,Player player,Activity147Status status,int activityId ,int StageId ) {
		status.setCfgIdxList(kind,WishingWellCfg.getWellCfgIdx(kind));
		status.clearAwardTake(kind);
		status.clearAreaAward(kind);
		for(Integer idx :status.getCfgIdxList(kind)) {
			WishingWellCfg aCfg = WishingWellCfg.getWingCfgByKey(idx);
			String awradStr = aCfg.getItem();
			if ((aCfg.getArea() >= 1 )&&(aCfg.getArea() <= 7)) {
				status.setAwradTake(kind, awradStr, false);
				status.setAreaAward(kind, aCfg.getArea(), awradStr);
			}
		}
		player.getPlayerData().updateActivity(activityId,StageId);
	}
	/**
	 * 重置動作
	 * @param player
	 */
    public static void restActivityStatus(Player player) {
        try {
        	
            if (player == null || player.getPlayerData() == null) {
                return;
            }
            
            int activityId = Const.ActivityId.ACTIVITY147_WISHING_WELL_VALUE;

            ActivityTimeCfg activityTimeCfg = ActivityUtil.getCurActivityTimeCfg(activityId);
            if (activityTimeCfg == null) {
                // 活动已关闭
                return;
            }
            
            Activity147Status status = ActivityUtil.getActivityStatus(player.getPlayerData(), activityId, activityTimeCfg.getStageId(), Activity147Status.class);
            for (int type = 1 ; type <= 3 ; type++ ) {
            	initWishing(type, player, status, activityId, activityTimeCfg.getStageId());
            }
            // 重置星輪免費次數
            status.setFreeDraw(SysBasicCfg.getInstance().getWishingFreeDraw());
            
            player.getPlayerData().updateActivity(activityId, activityTimeCfg.getStageId());

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

	/**
	 * 取得獎池協定資訊
	 * @param action
	 * @param player
	 * @param kind
	 * @param status
	 * @param activityTimeCfg
	 * @return
	 */
	public static WishingWellInfo.Builder generateInfo(int action,Player player,int kind,Activity147Status status,ActivityTimeCfg activityTimeCfg) {
		
		WishingWellInfo.Builder builder = WishingWellInfo.newBuilder();
				
		int lucky = status.getLucky(kind);
		int lastFreeTime = status.getRefreshFreeTime(kind);
		int take = status.getTake(kind);
		
		Map<String,Boolean>awradMap = status.getAwradTakeMap(kind);
		Map<Integer,String>areaMap = status.getAreaAwardMap(kind);

		builder.setKind(kind);
		builder.setAction(action);
		builder.setLastFreeTime(lastFreeTime);
		builder.setLucky(lucky);
		builder.setTake(take);
		int maxNum = WishingWellCfg.getWellMaxReward(kind);
		int leftNum = status.getCfgIdxList(kind).size();
		
		builder.setMaxReward(maxNum);
		builder.setNowReward(leftNum);
		
		if (kind == Activity147Handler.STAR_WELL) {
			builder.setFreeDraw(status.getFreeDraw());
		}
		String ItemStr = "";
		if (awradMap != null) {
			for (int area = 1 ; area <= 7 ; area++) {
				if (areaMap.containsKey(area)) {
					ItemStr = areaMap.get(area); 
					if (awradMap.containsKey(ItemStr)) {
						WishingItemInfo.Builder ibuilder = WishingItemInfo.newBuilder();
						ibuilder.setItemStr(ItemStr);
						ibuilder.setGiven(awradMap.get(ItemStr));
						builder.addDisplayItem(ibuilder);
					}
				}
			}
		} else { // fix
//			for(Integer idx :status.getCfgIdxList(kind)) {
//				WishingWellCfg aCfg = WishingWellCfg.getWingCfgByKey(idx);
//				String awradStr = aCfg.getItem();
//				if ((aCfg.getArea() >= 1 )&&(aCfg.getArea() <= 7)) {
//					status.setAwradTake(kind, awradStr, false);
//					status.setAreaAward(kind,aCfg.getArea(),awradStr);
//				}
//			}
//			
//			player.getPlayerData().updateActivity(Const.ActivityId.ACTIVITY147_WISHING_WELL_VALUE, activityTimeCfg.getStageId());
//			
//			for (Map.Entry<String,Boolean> entry : status.getAwradTakeMap(kind).entrySet()) {
//				WishingItemInfo.Builder ibuilder = WishingItemInfo.newBuilder();
//				ibuilder.setItemStr(entry.getKey());
//				ibuilder.setGiven(entry.getValue());
//				builder.addDisplayItem(ibuilder);
//			}
		}
		
		return builder;
	}
}
