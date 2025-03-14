package com.guaji.game.module.activity.activity154;

import java.util.ArrayList;
import java.util.List;

import org.guaji.app.AppObj;
import org.guaji.net.protocol.IProtocolHandler;
import org.guaji.net.protocol.Protocol;
import org.guaji.os.GuaJiTime;

import com.guaji.game.config.ActivityTimeCfg;
import com.guaji.game.config.CallSpriteMilestoneCfg;
import com.guaji.game.config.ReleaseURDropCfg154;
import com.guaji.game.config.SysBasicCfg;
import com.guaji.game.item.AwardItems;
import com.guaji.game.item.ConsumeItems;
import com.guaji.game.item.ItemInfo;
import com.guaji.game.log.BehaviorLogger;
import com.guaji.game.log.BehaviorLogger.Action;
import com.guaji.game.log.BehaviorLogger.Params;
import com.guaji.game.log.BehaviorLogger.Source;
import com.guaji.game.player.Player;
import com.guaji.game.protocol.Activity4.CallOfSpriteRequest;
import com.guaji.game.protocol.Activity4.CallOfSpriteResponse;
import com.guaji.game.protocol.Const;
import com.guaji.game.protocol.HP;
import com.guaji.game.protocol.Status;
import com.guaji.game.util.ActivityUtil;

/*** @author 作者 Ting Lin
* @version 创建时间：2023年5月8日
* 类说明
*/
public class Activity154Handler implements IProtocolHandler{
	static final int SYNC_INFO = 0;
	static final int SINGLE_DRAW = 1;
	static final int TEN_DRAW = 2;
	static final int FREE_DRAW = 3;
	static final int LUCKY_AWARD = 4;
	
	@Override 
	public boolean onProtocol(AppObj appObj, Protocol protocol) {
		Player player = (Player) appObj;
		// 检测活动是否开放
		int activityId = Const.ActivityId.ACTIVITY154_CALL_OF_SPRITE_VALUE;
		ActivityTimeCfg timeCfg = ActivityUtil.getCurActivityTimeCfg(activityId);
		if (timeCfg == null) {
			// 活动已关闭
			player.sendError(protocol.getType(), Status.error.ACTIVITY_CLOSE);
			return true;
		}

		CallOfSpriteRequest req = protocol.parseProtocol(CallOfSpriteRequest.getDefaultInstance());
		int action = req.getAction();
	
		// 获取活动数据
		int stageId = timeCfg.getStageId();
		Activity154Status status = ActivityUtil.getActivityStatus(player.getPlayerData(), activityId, stageId, Activity154Status.class);
		
		if (null == status) {
			player.sendError(protocol.getType(), Status.error.DATA_NOT_FOUND);
			return true;
		}
					
		// 业务分支处理
		switch (action) {
		case SYNC_INFO:
			SyncInfo(action, player, status,timeCfg);
			break;
		case SINGLE_DRAW:
			gameDraw(action,player,status);
			break;
		case TEN_DRAW:
			gameDraw(action,player,status);
			break;
		case FREE_DRAW:
			gameDraw(action,player,status);
			break;
		case LUCKY_AWARD:
			takeLuckyAward(action, player, status);
			break;
		default:
			player.sendError(protocol.getType(), Status.error.PARAMS_INVALID_VALUE);
			break;
		}
		return true;
	}
	
	/**
	 * 同步資訊
	 * @param action
	 * @param player
	 * @param timeCfg
	 * @param status
	 */
	private static void SyncInfo(int action,Player player, Activity154Status status,ActivityTimeCfg timeCfg) {
		CallOfSpriteResponse.Builder builder = generateInfo(action,player,status,timeCfg);
		player.sendProtocol(Protocol.valueOf(HP.code.ACTIVITY154_S_VALUE, builder));
	}
	/**
	 * 抽獎
	 * @param action 1.單抽 2.十抽 3.免費抽
	 * @param type
	 * @param player
	 * @param status
	 */
	static  void gameDraw(int action,Player player,Activity154Status status) {
						
		int count = 1;
		
		if (action == Activity154Handler.TEN_DRAW) {
			count = 10;
		}
		
		boolean freedraw = false;

		if (action == Activity154Handler.FREE_DRAW) { // 免費抽
			long lastFreeTime = status.getLastFreeTime();
			long currentTime = System.currentTimeMillis();
			if (!GuaJiTime.isSameDay(lastFreeTime,currentTime)) {
				freedraw = true;
				status.setLastFreeTime(currentTime);
			} else {
				player.sendError(HP.code.ACTIVITY154_C_VALUE,Status.error.PARAMS_INVALID); 
				return;
			}
		}
		
		if (!freedraw) {
			String costItems = SysBasicCfg.getInstance().getSpriteSingleCost();
			
			if (action == Activity154Handler.TEN_DRAW) {
				costItems = SysBasicCfg.getInstance().getSpriteTenCost();
			}
			
			if (costItems.isEmpty()) {
				player.sendError(HP.code.ACTIVITY154_C_VALUE,Status.error.ITEM_NOT_FOUND_VALUE); 
				return;
			}
			
			ConsumeItems consumeItems = ConsumeItems.valueOf();
			ItemInfo itemInfo = ItemInfo.valueOf(costItems);
			itemInfo.setQuantity(itemInfo.getQuantity());
			
			List<ItemInfo> itemList = new ArrayList<ItemInfo>();
			itemList.add(itemInfo);
			boolean isAdd = false ;
			isAdd = consumeItems.addConsumeInfo(player.getPlayerData(),itemList);
			
			if (isAdd && consumeItems.checkConsume(player)) {
				if(!consumeItems.consumeTakeAffect(player, Action.ACTIVITY154_CALL_OF_SPRITE)) {
					player.sendError(HP.code.ACTIVITY154_C_VALUE,Status.error.ITEM_NOT_ENOUGH_VALUE); 
					return; 
				} 
			} else {
				player.sendError(HP.code.ACTIVITY154_C_VALUE,Status.error.ITEM_NOT_ENOUGH_VALUE); 
				return; 
			}
		}
		
		int activityId = Const.ActivityId.ACTIVITY154_CALL_OF_SPRITE_VALUE;
		ActivityTimeCfg timeCfg = ActivityUtil.getCurActivityTimeCfg(activityId);
		String awradStr = "";
		AwardItems nowawardItems = new AwardItems();
		int oldLucky = status.getLucky();
		for (int i = 0 ; i < count ; i++) {
			ReleaseURDropCfg154 acfg = ReleaseURDropCfg154.RandomReward();
 			if (acfg == null) {
 				player.sendError(HP.code.ACTIVITY154_C_VALUE,Status.error.CONFIG_ERROR);
 				return;
 			}
 			status.incLucky(1);
 			awradStr = acfg.getRewards();
			ItemInfo nowitemInfos = ItemInfo.valueOf(awradStr);
			nowawardItems.addItem(nowitemInfos);
		}
		nowawardItems.rewardTakeAffectAndPush(player, Action.ACTIVITY154_CALL_OF_SPRITE, 0);
		
		// BI 日志 ()
		BehaviorLogger.log4Service(player, Source.USER_OPERATION, Action.ACTIVITY154_CALL_OF_SPRITE, Params.valueOf("action", action),Params.valueOf("count", count),
				Params.valueOf("count", count),
				Params.valueOf("freedraw", freedraw),
				Params.valueOf("oldLucky", oldLucky),
				Params.valueOf("Lucky", status.getLucky()),
				Params.valueOf("awardItems", nowawardItems.toDbString()));
		
		player.getPlayerData().updateActivity(activityId, timeCfg.getStageId());
		
		CallOfSpriteResponse.Builder builder = generateInfo(action,player,status,timeCfg);

		builder.setReward(nowawardItems.toString());
		
		player.sendProtocol(Protocol.valueOf(HP.code.ACTIVITY154_S_VALUE, builder));
		
	}
	
	/**
	 *  領取累積幸運值獎品
	 * @param action
	 * @param type
	 * @param player
	 * @param status
	 */
	static void takeLuckyAward(int action,Player player,Activity154Status status) {
		int activityId = Const.ActivityId.ACTIVITY154_CALL_OF_SPRITE_VALUE;
		ActivityTimeCfg timeCfg = ActivityUtil.getCurActivityTimeCfg(activityId);
		
		CallSpriteMilestoneCfg milestoneCfg = CallSpriteMilestoneCfg.getMileStoneCfg();
		if (milestoneCfg == null) {
				player.sendError(HP.code.ACTIVITY154_C_VALUE,Status.error.CONFIG_ERROR);
				return;
		}
		
		int takeMark = status.getTake(); // 領到第幾個
		int lucky = status.getLucky();
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
				status.incTake(1);
				nowawardItems = new AwardItems();
				nowawardItems.addItem(reward);
				nowawardItems.rewardTakeAffectAndPush(player, Action.ACTIVITY154_CALL_OF_SPRITE, 0);
			} else {
				player.sendError(HP.code.ACTIVITY154_C_VALUE,Status.error.CRYSTAL_NOT_ENOUGH);
				return;
			}
		}
		takeMark = status.getTake(); // 再次檢查領到第幾個,有沒有到最後了
		
		if (takeMark >= PointList.size()){ //沒獎品了,全部領完,扣除領取全部獎品所需的幸運值
			status.setTake(0);
			int pointVaule = PointList.get(PointList.size()-1);
			status.decLucky(pointVaule);
		}
		
		player.getPlayerData().updateActivity(activityId, timeCfg.getStageId());
		
		BehaviorLogger.log4Service(player,Source.USER_OPERATION, Action.ACTIVITY154_CALL_OF_SPRITE, Params.valueOf("action", action),
		        Params.valueOf("takeMark", takeMark),Params.valueOf("lucky", lucky),Params.valueOf("needPoint", needPoint),
		        Params.valueOf("awardItems", nowawardItems.toDbString()));
		
		CallOfSpriteResponse.Builder builder = generateInfo(action,player,status,timeCfg);
		if (nowawardItems != null) {
			builder.setReward(nowawardItems.toString());
		}
		
		player.sendProtocol(Protocol.valueOf(HP.code.ACTIVITY154_S_VALUE, builder));
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
	public static CallOfSpriteResponse.Builder generateInfo(int action,Player player,Activity154Status status,ActivityTimeCfg activityTimeCfg) {
		
		CallOfSpriteResponse.Builder builder = CallOfSpriteResponse.newBuilder();
		
		long currentTime = System.currentTimeMillis();		
		long lastFreeTime = status.getLastFreeTime();
		int freeTime = 0;
		if (!GuaJiTime.isSameDay(lastFreeTime,currentTime)) {
			freeTime = 1;
		}
		int lucky = status.getLucky();
		int take = status.getTake();
		
		builder.setAction(action);
		builder.setSingleItem(SysBasicCfg.getInstance().getSpriteSingleCost());
		builder.setTenItem(SysBasicCfg.getInstance().getSpriteTenCost());
		builder.setLucky(lucky);
		builder.setTake(take);
		builder.setFree(freeTime);
		
		return builder;
	}
}
