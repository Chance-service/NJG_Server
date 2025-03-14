package com.guaji.game.module.activity.activity158;

import java.util.ArrayList;
import java.util.List;

import org.guaji.app.AppObj;
import org.guaji.config.ConfigManager;
import org.guaji.msg.Msg;
import org.guaji.net.protocol.IProtocolHandler;
import org.guaji.net.protocol.Protocol;
import org.guaji.xid.GuaJiXID;

import com.guaji.game.GsApp;
import com.guaji.game.config.ActivityTimeCfg;
import com.guaji.game.config.FunctionUnlockCfg;
import com.guaji.game.config.ReleaseUrDropCfg158;
import com.guaji.game.config.SysBasicCfg;
import com.guaji.game.item.AwardItems;
import com.guaji.game.item.ConsumeItems;
import com.guaji.game.item.ItemInfo;
import com.guaji.game.log.BehaviorLogger;
import com.guaji.game.log.BehaviorLogger.Action;
import com.guaji.game.log.BehaviorLogger.Params;
import com.guaji.game.log.BehaviorLogger.Source;
import com.guaji.game.player.Player;
import com.guaji.game.protocol.Activity5.CallOfRaceReq;
import com.guaji.game.protocol.Activity5.CallOfRaceRes;
import com.guaji.game.protocol.Const;
import com.guaji.game.protocol.HP;
import com.guaji.game.protocol.Status;
import com.guaji.game.util.ActivityUtil;
import com.guaji.game.util.GsConst;
import com.guaji.game.util.TapDBUtil.TapDBSource;

/*** @author 作者 Ting Lin
* @version 创建时间：2023年7月4日
* 类说明
*/
public class Activity158Handler implements IProtocolHandler{
	static final int SYNC_INFO = 0; // 同步
	static final int SINGLE_DRAW = 1; // 單抽
	static final int FIVE_DRAW = 2; // 五抽
	static final int EXCHANGE_GIFT = 3; // 領取隨機兌換
	
	@Override 
	public boolean onProtocol(AppObj appObj, Protocol protocol) {
		Player player = (Player) appObj;
		
		// 检测活动是否开放
		int activityId = Const.ActivityId.ACTIVITY158_CALL_OF_RACE_VALUE;
		ActivityTimeCfg timeCfg = ActivityUtil.getCurActivityTimeCfg(activityId);
		if (timeCfg == null) {
			// 活动已关闭
			player.sendError(protocol.getType(), Status.error.ACTIVITY_CLOSE);
			return true;
		}

		CallOfRaceReq req = protocol.parseProtocol(CallOfRaceReq.getDefaultInstance());
		int action = req.getAction();
		int race = req.getRace();
		
		if (action !=SYNC_INFO) {
			if (!FunctionUnlockCfg.checkUnlock(player,null,Const.FunctionType.racegacha_Unlock)){
				player.sendError(protocol.getType(), Status.error.CONDITION_NOT_ENOUGH);
				return true;
			}
		}
			
		// 获取活动数据
		int stageId = timeCfg.getStageId();
		Activity158Status status = ActivityUtil.getActivityStatus(player.getPlayerData(), activityId, stageId, Activity158Status.class);
		
		if (null == status) {
			player.sendError(protocol.getType(), Status.error.DATA_NOT_FOUND);
			return true;
		}
			
		// 业务分支处理
		switch (action) {
		case SYNC_INFO:
			SyncInfo(action, player, status);
			break;
		case SINGLE_DRAW:
			takeDraw(action,race,player,status,timeCfg);
			break;
		case FIVE_DRAW:
			takeDraw(action,race,player,status,timeCfg);
			break;
		case EXCHANGE_GIFT:
			exchangeGift(action,race,player,status,timeCfg);
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
	private static void SyncInfo(int action,Player player, Activity158Status status) {
		CallOfRaceRes.Builder builder = generateInfo(action,player,status);
		player.sendProtocol(Protocol.valueOf(HP.code.ACTIVITY158_S_VALUE, builder));
	}
	/**
	 * 抽獎
	 * @param action 1.領五抽 
	 * @param type
	 * @param player
	 * @param status
	 */
	static  void takeDraw(int action,int race,Player player,Activity158Status status,ActivityTimeCfg timeCfg) {
		
		if (race < 1 || race > 5) {
			player.sendError(HP.code.ACTIVITY158_C_VALUE, Status.error.PARAMS_INVALID_VALUE);
			return ;
		}
		
		ReleaseUrDropCfg158 rcfg = ConfigManager.getInstance().getConfigByKey(ReleaseUrDropCfg158.class, race);
		
		if (rcfg == null) {
			player.sendError(HP.code.ACTIVITY158_C_VALUE,Status.error.CONFIG_NOT_FOUND_VALUE); 
			return ;
		}
		
		int count = 1;
		
		if (action == Activity158Handler.FIVE_DRAW) {
			count = 5;
		}
		
		String costItems = SysBasicCfg.getInstance().getRaceSummon();
				
		if (costItems.isEmpty()) {
			player.sendError(HP.code.ACTIVITY158_C_VALUE,Status.error.ITEM_NOT_FOUND_VALUE); 
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
			if(!consumeItems.consumeTakeAffect(player, Action.ACTIVITY158_CALL_OF_RACE)) {
				player.sendError(HP.code.ACTIVITY158_C_VALUE,Status.error.ITEM_NOT_ENOUGH_VALUE); 
				return; 
			} 
		} else {
			player.sendError(HP.code.ACTIVITY158_C_VALUE,Status.error.ITEM_NOT_ENOUGH_VALUE); 
			return; 
		}
		
		int activityId = Const.ActivityId.ACTIVITY158_CALL_OF_RACE_VALUE;
		
		
		String awradStr = "";
		AwardItems nowawardItems = new AwardItems();
		AwardItems otherItems = new AwardItems();
		int oldPoint = status.getPoint();
		for (int i = 0 ; i < count ; i++) {
			awradStr = rcfg.randomRewrad();
			status.incPoint(SysBasicCfg.getInstance().getRacePoint());
			ItemInfo nowitemInfos = ItemInfo.valueOf(awradStr);
			nowawardItems.addItem(nowitemInfos);
			//種族召喚額外獎勵
			ItemInfo otherItem = ItemInfo.valueOf(SysBasicCfg.getInstance().getCallOfRaceAward());
			otherItems.addItem(otherItem);
		}
		
		nowawardItems.rewardTakeAffectAndPush(player, Action.ACTIVITY158_CALL_OF_RACE, 0,TapDBSource.Call_Of_Race,Params.valueOf("Draw", 0));
		
		otherItems.rewardTakeAffectAndPush(player, Action.ACTIVITY158_CALL_OF_RACE, 2,TapDBSource.Call_Of_Race,Params.valueOf("Draw", 0));
		
		Msg hawkMsg = Msg.valueOf(GsConst.MsgType.DailyQuestMsg.CALL_HERO,
				GuaJiXID.valueOf(GsConst.ObjType.PLAYER, player.getId()));
		hawkMsg.pushParam(count);
		GsApp.getInstance().postMsg(hawkMsg);
		
		// BI 日志 ()
		BehaviorLogger.log4Service(player,Source.USER_OPERATION,Action.ACTIVITY158_CALL_OF_RACE, Params.valueOf("action", action),
				Params.valueOf("race",race),
				Params.valueOf("count",count),
				Params.valueOf("oldPoint", oldPoint),
				Params.valueOf("Point", status.getPoint()),
				Params.valueOf("awardItems", nowawardItems.toDbString()));
		
		player.getPlayerData().updateActivity(activityId, timeCfg.getStageId());
		
		CallOfRaceRes.Builder builder = generateInfo(action,player,status);
		builder.setRace(race);
		builder.setReward(nowawardItems.toString());
		
		player.sendProtocol(Protocol.valueOf(HP.code.ACTIVITY158_S_VALUE, builder));
		
	}
	
	/**
	 * 兌換隨機禮物
	 */
	 public static void exchangeGift(int action,int race,Player player,Activity158Status status,ActivityTimeCfg timeCfg) {
		 
			if (race < 1 || race > 5) {
				player.sendError(HP.code.ACTIVITY158_C_VALUE, Status.error.PARAMS_INVALID_VALUE);
				return ;
			}
			
			ReleaseUrDropCfg158 rcfg = ConfigManager.getInstance().getConfigByKey(ReleaseUrDropCfg158.class, race);
			
			if (rcfg == null) {
				player.sendError(HP.code.ACTIVITY158_C_VALUE,Status.error.CONFIG_NOT_FOUND_VALUE); 
				return ;
			}
			
			int oldPoint = status.getPoint();
			
			if (rcfg.getConsume() > oldPoint) {
				player.sendError(HP.code.ACTIVITY158_C_VALUE,Status.error.POINT_NOI_ENOUGH); 
				return;
			}
			
			status.decPoint(rcfg.getConsume());
			
			int activityId = Const.ActivityId.ACTIVITY158_CALL_OF_RACE_VALUE;
			String awradStr = rcfg.randomExchange();
			AwardItems nowawardItems = new AwardItems();
			ItemInfo nowitemInfos = ItemInfo.valueOf(awradStr);
			nowawardItems.addItem(nowitemInfos);
			nowawardItems.rewardTakeAffectAndPush(player, Action.ACTIVITY158_CALL_OF_RACE, 0,TapDBSource.Call_Of_Race,Params.valueOf("Draw",1));
			
			// BI 日志 ()
			BehaviorLogger.log4Service(player, Source.USER_OPERATION, Action.ACTIVITY158_CALL_OF_RACE, Params.valueOf("action", action),
					Params.valueOf("race", race),
					Params.valueOf("oldPoint", oldPoint),
					Params.valueOf("Point", status.getPoint()),
					Params.valueOf("awardItems", awradStr));
			
			player.getPlayerData().updateActivity(activityId, timeCfg.getStageId());
			
			CallOfRaceRes.Builder builder = generateInfo(action,player,status);
			builder.setRace(race);
			builder.setReward(nowawardItems.toString());
			
			player.sendProtocol(Protocol.valueOf(HP.code.ACTIVITY158_S, builder));
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
	public static CallOfRaceRes.Builder generateInfo(int action,Player player,Activity158Status status) {
		
		CallOfRaceRes.Builder builder = CallOfRaceRes.newBuilder();
				
		builder.setAction(action);
		builder.setPoint(status.getPoint());
		
		
		return builder;
	}
}
