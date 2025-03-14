package com.guaji.game.module.activity.activity148;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.guaji.app.AppObj;
import org.guaji.config.ConfigManager;
import org.guaji.net.protocol.IProtocolHandler;
import org.guaji.net.protocol.Protocol;
import org.guaji.os.GuaJiRand;
import org.guaji.os.GuaJiTime;

import com.guaji.game.config.ActivityTimeCfg;
import com.guaji.game.config.FunctionUnlockCfg;
import com.guaji.game.config.NewMarryCfg;
import com.guaji.game.config.SysBasicCfg;
import com.guaji.game.entity.StateEntity;
import com.guaji.game.item.AwardItems;
import com.guaji.game.item.ConsumeItems;
import com.guaji.game.item.ItemInfo;
import com.guaji.game.log.BehaviorLogger;
import com.guaji.game.log.BehaviorLogger.Action;
import com.guaji.game.log.BehaviorLogger.Params;
import com.guaji.game.log.BehaviorLogger.Source;
import com.guaji.game.player.Player;
import com.guaji.game.protocol.Activity4.MarrayRequest;
import com.guaji.game.protocol.Activity4.MarrayResponse;
import com.guaji.game.protocol.Const;
import com.guaji.game.protocol.HP;
import com.guaji.game.protocol.Status;
import com.guaji.game.util.ActivityUtil;

/*** @author 作者 Ting Lin
* @version 创建时间：2023年5月8日
* 类说明
*/
public class Activity148Handler implements IProtocolHandler{
	static final int Marry_Sync = 0;
	static final int Marry_Draw = 1;

	@Override // RELEASE_UR_INFO_C
	public boolean onProtocol(AppObj appObj, Protocol protocol) {
		Player player = (Player) appObj;
				
		if (!FunctionUnlockCfg.checkUnlock(player,null,Const.FunctionType.Newbie_Unlock)){
			player.sendError(protocol.getType(), Status.error.CONDITION_NOT_ENOUGH);
			return false;
		}
		
		// 检测活动是否开放
		int activityId = Const.ActivityId.ACTIVITY148_MARRY_GAME_VALUE;
		ActivityTimeCfg timeCfg = ActivityUtil.getCurActivityTimeCfg(activityId);
		if (timeCfg == null) {
			// 活动已关闭
			player.sendError(protocol.getType(), Status.error.ACTIVITY_CLOSE);
			return true;
		}

		MarrayRequest req = protocol.parseProtocol(MarrayRequest.getDefaultInstance());
		int action = req.getAction();
		
		if ((action != Marry_Sync) && (action != Marry_Draw)) {
			// 行為參數錯誤
			player.sendError(protocol.getType(), Status.error.PARAMS_INVALID);
			return true;
		}
		
		StateEntity stateEntity = player.getPlayerData().getStateEntity();
		
		if (stateEntity == null) {
			player.sendError(protocol.getType(), Status.error.DATA_NOT_FOUND);
			return true;
		}
		
		int SurplusTime = stateEntity.calcNewbieSurplusTime();
		
		if (SurplusTime <= 0) {
			if (SurplusTime < 0) {
				stateEntity.setNewbieDate(GuaJiTime.getCalendar().getTime());
			}
			if (SurplusTime == 0) {
				player.sendError(protocol.getType(), Status.error.ACTIVITY_CLOSE);
				return true;
			}
		}

		Activity148Status status = ActivityUtil.getActivityStatus(player.getPlayerData(), activityId, timeCfg.getStageId(), Activity148Status.class);
		
		if (status == null) {
			player.sendError(protocol.getType(), Status.error.DATA_NOT_FOUND);
			return true;
		}
		
		Map<Object,NewMarryCfg> marrayCfgMap = ConfigManager.getInstance().getConfigMap(NewMarryCfg.class);
		
		if (marrayCfgMap == null) {
			player.sendError(protocol.getType(), Status.error.CONFIG_NOT_FOUND);
			return true;
		}
				
		// 业务分支处理
		switch (action) {
		case Marry_Sync:
			SyncInfo(action,player,status);
			break;
		case Marry_Draw:
			DrawAction(action,player,timeCfg,status);
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
	private static void SyncInfo(int action,Player player, Activity148Status status) {
		MarrayResponse.Builder builder = getBuilder(player,action,status);
		player.sendProtocol(Protocol.valueOf(HP.code.ACTIVITY148_S_VALUE, builder));
	}
	/**
	 * 抽獎動作
	 * @param action
	 * @param player
	 * @param timeCfg
	 * @param status
	 */
	private static void DrawAction(int action,Player player,ActivityTimeCfg timeCfg,Activity148Status status) {
		
		Set<Integer> Gotawards =  status.getGotAwards();
		
		int drawCount = Gotawards.size(); // 已抽幾次
		
		Map<Object,NewMarryCfg> marrayCfgMap = ConfigManager.getInstance().getConfigMap(NewMarryCfg.class);
		// 已經抽完所有獎項了
		if (drawCount >= marrayCfgMap.size()) {
			player.sendError(HP.code.ACTIVITY148_C_VALUE,Status.error.ACTIVITY_CLOSE);
			return;
		}
		
		ItemInfo costItem = ItemInfo.valueOf(SysBasicCfg.getInstance().getMarryCostItem());
		
		int count = SysBasicCfg.getInstance().getMarryCostCount(drawCount);
		
		if (count < 0) {
			player.sendError(HP.code.ACTIVITY148_C_VALUE,Status.error.ITEM_NOT_FOUND_VALUE); 
			return ;
		}
		
		if (count > 0) { // 消耗0為免費
			ConsumeItems consumeItems = ConsumeItems.valueOf();
			costItem.setQuantity(costItem.getQuantity()*count);
			List<ItemInfo> itemList = new ArrayList<ItemInfo>();
			itemList.add(costItem);
			boolean isAdd = false ;
			
			isAdd = consumeItems.addConsumeInfo(player.getPlayerData(),itemList);
			
			if (isAdd && consumeItems.checkConsume(player)) {
				if(!consumeItems.consumeTakeAffect(player, Action.ACTIVITY148_MARRY_GAME)) {
					player.sendError(HP.code.ACTIVITY148_C_VALUE,Status.error.ITEM_NOT_ENOUGH_VALUE); 
					return; 
				} 
			} else {
				player.sendError(HP.code.ACTIVITY148_C_VALUE,Status.error.ITEM_NOT_ENOUGH_VALUE); 
				return; 
			}
		}
		
		List<Integer> IndexList = new ArrayList<Integer>();
		List<Integer> WeightList = new ArrayList<Integer>();
		
		int weight = 0;
		int idx = 0;
		for (Map.Entry<Object,NewMarryCfg> entry : marrayCfgMap.entrySet()) {
			if (Gotawards.contains(entry.getValue().getId())){
				continue;
			}
			weight = entry.getValue().getWeight();
			idx = entry.getValue().getId();
			WeightList.add(weight);
			IndexList.add(idx);
		}
		
		AwardItems nowawardItems = new AwardItems();
		int gotIndex = 0;
		String awradStr = "";
		if (IndexList.size() == 2) {  // 只剩兩個,兩個一起給
			for (Integer index : IndexList) {
				status.addGotAwards(index);
				NewMarryCfg marrycfg = ConfigManager.getInstance().getConfigByKey(NewMarryCfg.class,index);
				awradStr = marrycfg.getItem();
				ItemInfo nowitemInfos = ItemInfo.valueOf(awradStr);
				nowawardItems.addItem(nowitemInfos);
				// BI 日志 ()
				BehaviorLogger.log4Service(player,Source.USER_OPERATION, Action.ACTIVITY148_MARRY_GAME, Params.valueOf("drawCount", drawCount),
				        Params.valueOf("count", count), Params.valueOf("gotIndex", index), Params.valueOf("awradStr", awradStr),
				        Params.valueOf("Gotawards", status.getGotAwards()));
			}
			awradStr = nowawardItems.toString();
			nowawardItems.rewardTakeAffectAndPush(player, Action.ACTIVITY148_MARRY_GAME, 0);
		} else {
			gotIndex = GuaJiRand.randonWeightObject(IndexList, WeightList);
			status.addGotAwards(gotIndex);
			NewMarryCfg marrycfg = ConfigManager.getInstance().getConfigByKey(NewMarryCfg.class,gotIndex);
			awradStr = marrycfg.getItem();
			ItemInfo nowitemInfos = ItemInfo.valueOf(awradStr);
			nowawardItems.addItem(nowitemInfos);
			nowawardItems.rewardTakeAffectAndPush(player, Action.ACTIVITY148_MARRY_GAME, 0);
			// BI 日志 ()
			BehaviorLogger.log4Service(player,Source.USER_OPERATION, Action.ACTIVITY148_MARRY_GAME, Params.valueOf("drawCount", drawCount),
			        Params.valueOf("count", count), Params.valueOf("gotIndex", gotIndex), Params.valueOf("awradStr", awradStr),
			        Params.valueOf("Gotawards", status.getGotAwards()));
		}
		
		int activityId = timeCfg.getActivityId();
		player.getPlayerData().updateActivity(activityId, timeCfg.getStageId());
				
		MarrayResponse.Builder builder = getBuilder(player,action,status);
		builder.setReward(awradStr);
		
		player.sendProtocol(Protocol.valueOf(HP.code.ACTIVITY148_S_VALUE, builder));
	}
	
	private static MarrayResponse.Builder getBuilder(Player player,int action ,Activity148Status status) {
		// 返回包
		MarrayResponse.Builder response = MarrayResponse.newBuilder();
		Map<Object,NewMarryCfg> marrayCfgMap = ConfigManager.getInstance().getConfigMap(NewMarryCfg.class);

		response.setAction(action);
		if (marrayCfgMap != null) {
			for (Map.Entry<Object,NewMarryCfg> entry : marrayCfgMap.entrySet()) {
				String aItem = entry.getValue().getItem();
				response.addRewards(aItem);
			}
		}
		response.setLeftTime(player.getPlayerData().getStateEntity().calcNewbieSurplusTime());
		
		List<Integer>aList = new ArrayList<Integer>(status.getGotAwards());
		
		int draw = aList.size(); // 取得已抽次數
		
		ItemInfo costItem = ItemInfo.valueOf(SysBasicCfg.getInstance().getMarryCostItem());
		
		int count = SysBasicCfg.getInstance().getMarryCostCount(draw);
		
		if (count >= 0) {
			costItem.setQuantity(costItem.getQuantity()*count);
			response.setCostItem(costItem.toString());
		} else { // 錯誤情況
			costItem.setQuantity(-1);
			response.setCostItem(costItem.toString());
		}
		
		response.addAllGotIndex(aList);
		
		return response;
	}
}
