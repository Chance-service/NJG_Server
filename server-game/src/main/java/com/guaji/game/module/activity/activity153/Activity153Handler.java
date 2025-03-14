package com.guaji.game.module.activity.activity153;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.guaji.app.AppObj;
import org.guaji.config.ConfigManager;
import org.guaji.net.protocol.IProtocolHandler;
import org.guaji.net.protocol.Protocol;

import com.guaji.game.config.ActivityTimeCfg;
import com.guaji.game.config.RankServerCfg;
import com.guaji.game.entity.FirstEntity;
import com.guaji.game.item.AwardItems;
import com.guaji.game.item.ItemInfo;
import com.guaji.game.log.BehaviorLogger;
import com.guaji.game.log.BehaviorLogger.Action;
import com.guaji.game.log.BehaviorLogger.Params;
import com.guaji.game.log.BehaviorLogger.Source;
import com.guaji.game.manager.RecordFirstManager;
import com.guaji.game.manager.SnapShotManager;
import com.guaji.game.player.Player;
import com.guaji.game.protocol.Activity4.RankGiftCompleteInfo;
import com.guaji.game.protocol.Activity4.RankGiftReq;
import com.guaji.game.protocol.Activity4.RankGiftRes;
import com.guaji.game.protocol.Const;
import com.guaji.game.protocol.HP;
import com.guaji.game.protocol.Snapshot.PlayerSnapshotInfo;
import com.guaji.game.protocol.Status;
import com.guaji.game.util.ActivityUtil;
import com.guaji.game.util.TapDBUtil.TapDBSource;

/*** @author 作者 Ting Lin
* @version 创建时间：2023年6月15日
* 类说明
*/
public class Activity153Handler implements IProtocolHandler{
	static final int Rank_Gift_Sync = 0;
	static final int Rank_Get_Gift = 1;

	@Override // RELEASE_UR_INFO_C
	public boolean onProtocol(AppObj appObj, Protocol protocol) {
		Player player = (Player) appObj;
				
		// 检测活动是否开放
		int activityId = Const.ActivityId.ACTIVITY153_RANK_GIFT_VALUE;
		ActivityTimeCfg timeCfg = ActivityUtil.getCurActivityTimeCfg(activityId);
		if (timeCfg == null) {
			// 活动已关闭
			player.sendError(protocol.getType(), Status.error.ACTIVITY_CLOSE);
			return true;
		}

		RankGiftReq req = protocol.parseProtocol(RankGiftReq.getDefaultInstance());
		int action = req.getAction();
		
		if ((action != Rank_Gift_Sync) && (action != Rank_Get_Gift)) {
			// 行為參數錯誤
			player.sendError(protocol.getType(), Status.error.PARAMS_INVALID);
			return true;
		}
		
		Activity153Status status = ActivityUtil.getActivityStatus(player.getPlayerData(), activityId, timeCfg.getStageId(), Activity153Status.class);
		
		if (status == null) {
			player.sendError(protocol.getType(), Status.error.DATA_NOT_FOUND);
			return true;
		}
						
		// 业务分支处理
		switch (action) {
		case Rank_Gift_Sync:
			SyncInfo(action,player,status);
			break;
		case Rank_Get_Gift:
			Set<Integer> itemSet = new HashSet<>(req.getCfgIdList());
			if (itemSet.isEmpty()) {
				player.sendError(protocol.getType(), Status.error.PARAMS_INVALID);
				return true;
			}
			GetRankGift(action,player,timeCfg,status,itemSet);
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
	private static void SyncInfo(int action,Player player, Activity153Status status) {
		RankGiftRes.Builder builder = getBuilder(action,status);
		player.sendProtocol(Protocol.valueOf(HP.code.ACTIVITY153_S_VALUE, builder));
	}
	/**
	 * 領取
	 * @param action
	 * @param player
	 * @param timeCfg
	 * @param status
	 */
	private static void GetRankGift(int action,Player player,ActivityTimeCfg timeCfg,Activity153Status status, Set<Integer>itemSet) {
		String awradStr = "";
		List<Integer> ItemArray = new ArrayList<>();
		for (Integer itemId : itemSet ) {
			RankServerCfg rankServerCfg = ConfigManager.getInstance().getConfigByKey(RankServerCfg.class, itemId);
			
			if (rankServerCfg == null) {
				player.sendError(HP.code.ACTIVITY153_C_VALUE, Status.error.CONFIG_NOT_FOUND);
				continue;
			}
			
			FirstEntity firstEntity = RecordFirstManager.getInstance().getFirstEntity();
			
			if (firstEntity == null) {
				player.sendError(HP.code.ACTIVITY153_C_VALUE, Status.error.DATA_NOT_FOUND);
				continue;
			}
			
			if (!firstEntity.isAleadyDone(itemId)) {
				// 還沒有玩家完成此成就
				player.sendError(HP.code.ACTIVITY153_C_VALUE, Status.error.DATA_NOT_FOUND);
				continue;
			}
		
			if (status.isAlreadyGot(itemId)) {
				// 已領取過
				player.sendError(HP.code.ACTIVITY153_C_VALUE, Status.error.AWARD_ALREADY_GOT_ERROR);
				continue;
			}
			
			if (awradStr.isEmpty()) {
				awradStr = rankServerCfg.getAward();
			} else {
				awradStr = awradStr +","+rankServerCfg.getAward();
			}
			ItemArray.add(itemId);
			status.addCfgId(itemId);
		}
		
		if (!awradStr.isEmpty()){
			int activityId = timeCfg.getActivityId();
			
			player.getPlayerData().updateActivity(activityId, timeCfg.getStageId());
			
			AwardItems nowawardItems = new AwardItems();
			
			List<ItemInfo> nowitemInfos = ItemInfo.valueListOf(awradStr);
			nowawardItems.addItemInfos(nowitemInfos);
			nowawardItems.rewardTakeAffectAndPush(player, Action.ACTIVITY153_GOT_RANK_GIFT, 0,TapDBSource.Rank_Gift,Params.valueOf("ItemArray",ItemArray));
			
			// BI 日志 ()
			BehaviorLogger.log4Service(player,Source.USER_OPERATION, Action.ACTIVITY153_GOT_RANK_GIFT, 
					Params.valueOf("itemArray", ItemArray),
			        Params.valueOf("awradStr", awradStr),
			        Params.valueOf("GotCfgId", status.getMarkList()));
			
			RankGiftRes.Builder builder = getBuilder(action,status);
			builder.setReward(awradStr);
			player.sendProtocol(Protocol.valueOf(HP.code.ACTIVITY153_S_VALUE, builder));
		}
	}
	
	private static RankGiftRes.Builder getBuilder(int action ,Activity153Status status) {
		// 返回包
		RankGiftRes.Builder response = RankGiftRes.newBuilder();
		
		response.setAction(action);
		List<Integer>aList = new ArrayList<Integer>(status.getMarkList());
		response.addAllGotId(aList);
		List<RankGiftCompleteInfo> clist =  getCompleteInfo();
		if (clist.size() > 0) {
			response.addAllCompleteInfo(clist);
		}
		return response;
	}
	
	private static List<RankGiftCompleteInfo> getCompleteInfo(){
		List<RankGiftCompleteInfo> alist = new ArrayList<>();
		FirstEntity firstEntity = RecordFirstManager.getInstance().getFirstEntity();
		if (firstEntity != null) {
			int playerId = 0;
			for (Map.Entry<Integer, Integer> entry : firstEntity.getCfgMap().entrySet()) {
				playerId = entry.getValue();
				PlayerSnapshotInfo.Builder snapShotBuilder = SnapShotManager.getInstance().getPlayerSnapShot(playerId);
				if (snapShotBuilder == null) {
					continue;
				}
				RankGiftCompleteInfo.Builder completeBuilder = RankGiftCompleteInfo.newBuilder();
				completeBuilder.setCfgId(entry.getKey());
				completeBuilder.setPlayerName(snapShotBuilder.getMainRoleInfo().getName());
				completeBuilder.setHeadIcon(snapShotBuilder.getPlayerInfo().getHeadIcon());
				alist.add(completeBuilder.build());
			}
		}
		return alist;
	}
}
