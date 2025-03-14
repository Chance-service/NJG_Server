package com.guaji.game.module.activity.activity175;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.guaji.app.AppObj;
import org.guaji.config.ConfigManager;
import org.guaji.msg.Msg;
import org.guaji.net.protocol.IProtocolHandler;
import org.guaji.net.protocol.Protocol;
import org.guaji.os.GuaJiTime;
import org.guaji.xid.GuaJiXID;

import com.guaji.game.GsApp;
import com.guaji.game.config.ActivityTimeCfg;
import com.guaji.game.config.GloryHoleDailyCfg;
import com.guaji.game.config.GloryHoleDailyPointCfg;
import com.guaji.game.config.GloryHoleMissionCfg;
import com.guaji.game.config.GloryHoleRewardCfg;
import com.guaji.game.config.SysBasicCfg;
import com.guaji.game.entity.DailyQuestItem;
import com.guaji.game.item.AwardItems;
import com.guaji.game.item.ConsumeItems;
import com.guaji.game.item.ItemInfo;
import com.guaji.game.log.BehaviorLogger;
import com.guaji.game.log.BehaviorLogger.Action;
import com.guaji.game.log.BehaviorLogger.Params;
import com.guaji.game.log.BehaviorLogger.Source;
import com.guaji.game.manager.GloryHoleActivityManager;
import com.guaji.game.player.Player;
import com.guaji.game.protocol.Activity5.Activity175RankItem;
import com.guaji.game.protocol.Activity5.Activity175RankRes;
import com.guaji.game.protocol.Activity5.Activity175SelfItem;
import com.guaji.game.protocol.Activity5.Activity175TeamItem;
import com.guaji.game.protocol.Activity5.GloryHoleActInfo;
import com.guaji.game.protocol.Activity5.GloryHoleGameInfo;
import com.guaji.game.protocol.Activity5.GloryHoleGameStatus;
import com.guaji.game.protocol.Activity5.GloryHoleMissionInfo;
import com.guaji.game.protocol.Activity5.GloryHoleMissionItem;
import com.guaji.game.protocol.Activity5.GloryHoleReq;
import com.guaji.game.protocol.Activity5.GloryHoleResp;
import com.guaji.game.protocol.Activity5.GloryHoleUseItemInfo;
import com.guaji.game.protocol.Const;
import com.guaji.game.protocol.DailyQuest.DailyPointCore;
import com.guaji.game.protocol.DailyQuest.HPDailyQuestInfoRet;
import com.guaji.game.protocol.DailyQuest.QuestItem;
import com.guaji.game.protocol.HP;
import com.guaji.game.protocol.Status;
import com.guaji.game.util.ActivityUtil;
import com.guaji.game.util.GsConst;
import com.guaji.game.util.GsConst.GloryHoleGameTime;
import com.guaji.game.util.GsConst.GloryHoleRankType;
import com.guaji.game.util.TapDBUtil;

/**
 * 壁尻活動協定
 */
public class Activity175Handler implements IProtocolHandler {
	static final int gloryHole_Sync = 0;
	static final int gloryHole_rankSync = 1;
	static final int gloryHole_choose = 2;
	static final int gloryHole_start = 3;
	static final int gloryHole_update = 4;
	static final int gloryHole_practice = 5;
	static final int gloryHole_missionInfo = 6;
	static final int gloryHole_take_mission_award = 7;
	static final int gloryHole_dailyInfo = 8;
	static final int gloryHole_take_daily_award = 9;
	static final int gloryHole_take_point_award = 10;
	//static final int Get_Rward = 1;

	@Override
	public boolean onProtocol(AppObj appObj, Protocol protocol) {
		// TODO Auto-generated method stub

		Player player = (Player) appObj;
		// 检测活动是否开放
		int activityId = Const.ActivityId.ACTIVITY175_Glory_Hole_VALUE;
		ActivityTimeCfg timeConfig = ActivityUtil.getCurActivityTimeCfg(activityId);
		if (timeConfig == null || player == null || (!timeConfig.isActiveToEnd())) {
			// 活动已关闭
			player.sendError(protocol.getType(), Status.error.ACTIVITY_CLOSE);
			return true;
		}
		
		int days = GuaJiTime.getDaysOfWeek();
		
		if (!SysBasicCfg.getInstance().getGloryHoleOpenDayList().contains(days)) {
			// 活动已关闭
			player.sendError(protocol.getType(), Status.error.ACTIVITY_CLOSE);
			return true;
		}

		// 解析接受到的数据
		GloryHoleReq request = protocol.parseProtocol(GloryHoleReq.getDefaultInstance());

		int action = request.getAction();
								
		int stageId = timeConfig.getStageId();
		Activity175Status status = ActivityUtil.getActivityStatus(player.getPlayerData(), activityId, stageId, Activity175Status.class);
		
		if (status == null) {
			player.sendError(protocol.getType(), Status.error.DATA_NOT_FOUND_VALUE);
			return true;
		}
		
		
//		static final int gloryHole_Sync = 0;
//		static final int gloryHole_rankSync = 1;
//		static final int gloryHole_choose = 2;
//		static final int gloryHole_start = 3;
//		static final int gloryHole_update = 4;
		
		// 业务分支处理
		switch (action) {
		case gloryHole_Sync: 
		case gloryHole_rankSync:
		case gloryHole_missionInfo:
		case gloryHole_dailyInfo:
			SyncInfo(timeConfig,action,player,status);
			break;
		case gloryHole_choose:
			chooseTeamId(protocol, timeConfig, player,action, status);
			break;
		case gloryHole_start:
			GloryHoleStart(protocol, timeConfig, player,action, status);
			break;
		case gloryHole_update: 
			GloryHoleUpdateScore(protocol,timeConfig, player,action,status);
			break;
		case gloryHole_practice:
			GloryHoleActivityManager.getInstance().updateJoinCount();
			SyncInfo(timeConfig,action,player,status); // 給回應而已
			break;
		case gloryHole_take_mission_award:
			onTakeMissionAward(protocol, timeConfig, player,action, status);
			break;
		case gloryHole_take_daily_award:
			onTakeDailyAward(protocol, timeConfig, player,action, status);
			break;
		case gloryHole_take_point_award:
			onTakeDailyPointAward(protocol, timeConfig, player,action, status);
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
	private static void SyncInfo(ActivityTimeCfg timeConfig,int action,Player player, Activity175Status status) {
		GloryHoleResp.Builder builder = getBuilder(timeConfig,player,action,status);
		player.sendProtocol(Protocol.valueOf(HP.code.ACTIVITY175_GLORY_HOLE_S, builder));
	}
	
	private static GloryHoleResp.Builder getBuilder(ActivityTimeCfg timeConfig,Player player,int action,Activity175Status status) {
		// 返回包
		GloryHoleResp.Builder response = GloryHoleResp.newBuilder();
		
		response.setAction(action);
		
		if ((action == gloryHole_Sync) || (action == gloryHole_choose)) {
			GloryHoleActInfo.Builder resInfo = GloryHoleActInfo.newBuilder();
			int TeamId = GuaJiTime.isSameDay(status.getJoinTime(),GloryHoleActivityManager.getInstance().getGloryHoleStartTime())? status.getTeam() : 0;
			if (TeamId == 0) {
				ActivityUtil.ActivityExchageClear(player.getPlayerData(),timeConfig.getActivityId());
			}
			resInfo.setTeamId(TeamId) ;
			resInfo.setChallengeTime(SysBasicCfg.getInstance().getGloryHoleFree()-status.getUseFree()) ;
			
			resInfo.setActLeftTime(0) ; // 沒使用了
			//int days = GuaJiTime.getDaysOfWeek();
			//long nextTime = GuaJiTime.getTimeHourMinute(SysBasicCfg.getInstance().getAct175GameTime(GloryHoleGameTime.START));
//			if ((days == 1) ||  (days == 3) || (days == 5)) {
			long endTime = GuaJiTime.getTimeHourMinute(SysBasicCfg.getInstance().getAct175GameTime(GloryHoleGameTime.END));
			resInfo.setDailyLeftTime(ActivityUtil.calTargetTime(endTime)) ;
//			} else if ((days == 0) || (days == 2) || (days == 4))  {
//				nextTime += 24 * 60 * 60 * 1000;
//				resInfo.setDailyLeftTime(ActivityUtil.calTargetTime(nextTime)) ;
//			} else if (days == 6) {
//				nextTime += 48 * 60 * 60 * 1000;
//				resInfo.setDailyLeftTime(ActivityUtil.calTargetTime(nextTime)) ;
//			}
			resInfo.setParticipants(GloryHoleActivityManager.getInstance().getJoinCount()) ;
			resInfo.setMaxScore(status.getScore());
			resInfo.setUsePay(SysBasicCfg.getInstance().getGloryHoleMaxCost()-status.getUsePay());
			resInfo.setNowPayNum(0); // 預設
			if (status.getUsePay() < SysBasicCfg.getInstance().getGloryHoleMaxCost()) {
				String costItems = SysBasicCfg.getInstance().getGloryHoleCost(status.getUsePay());
				if (!costItems.isEmpty()) {
					ItemInfo aItem = ItemInfo.valueOf(costItems);
					resInfo.setNowPayNum((int)aItem.getQuantity());
				}
			}
			
			for (Map.Entry<Integer, Integer> entry :GloryHoleActivityManager.getInstance().getTeamMaxMap().entrySet()) {
				Activity175TeamItem.Builder teamItem = Activity175TeamItem.newBuilder();
				teamItem.setTeamId(entry.getKey());
				teamItem.setScore(entry.getValue());
				resInfo.addTeamitem(teamItem);
			}
			response.setActInfo(resInfo);
		} else if  ((action == gloryHole_start) || (action == gloryHole_update))  {
			GloryHoleGameInfo.Builder gameInfo = GloryHoleGameInfo.newBuilder();
			gameInfo.setMaxScore(status.getScore());
			GloryHoleUseItemInfo.Builder useInfo = GloryHoleUseItemInfo.newBuilder();
			useInfo.setAddTime(status.isAddTime());
			useInfo.setAddbar(status.isAddbar());
			useInfo.setOffset(status.isOffset());
			useInfo.setAddGain(status.isAddGain());
			gameInfo.setUseItem(useInfo);
			response.setGameInfo(gameInfo);
		} else if (action == gloryHole_rankSync) {
			
			Activity175RankRes.Builder builder = Activity175RankRes.newBuilder();
	        // 獲取排名
	        
	        Activity175SelfItem.Builder ownRankItem = Activity175SelfItem.newBuilder();
	        int dailyRank = GloryHoleActivityManager.getInstance().getPlayerRank(player.getId(),GloryHoleRankType.DAILY);
	        if (dailyRank != 0) {
		        ownRankItem.setScore(status.getScore());
	        } else {
	        	 ownRankItem.setScore(0);
	        }
	        ownRankItem.setRank(dailyRank);
	        ownRankItem.setMaxrank(0);
	        ownRankItem.setMaxscore(0);
	        ownRankItem.setPlayerId(player.getId());
	        ownRankItem.setName(player.getName());
	        ownRankItem.setTeamId(status.getTeam());
	        ownRankItem.setHeaderId(player.getPlayerData().getPlayerEntity().getHeadIcon());
	        builder.setOwnItem(ownRankItem);
	        List<Activity175RankItem.Builder> rankItemList = GloryHoleActivityManager.getInstance().getRankTop(GloryHoleRankType.DAILY);
	        for (Activity175RankItem.Builder item : rankItemList) {
	            builder.addDailyitem(item);
	        }
	        
//	        List<Activity175RankItem.Builder> MaxItemList = GloryHoleActivityManager.getInstance().getRankTop(GloryHoleRankType.TOTALMAX);
//	        for (Activity175RankItem.Builder item : MaxItemList) {
//	            builder.addMaxitem(item);
//	        }
	        
			for (Map.Entry<Integer, Integer> entry :GloryHoleActivityManager.getInstance().getTeamMaxMap().entrySet()) {
				Activity175TeamItem.Builder teamItem = Activity175TeamItem.newBuilder();
				teamItem.setTeamId(entry.getKey());
				teamItem.setScore(entry.getValue());
				builder.addTeamitem(teamItem);
			}
			response.setRankInfo(builder);
		} else if ((action == gloryHole_missionInfo) || (action == gloryHole_take_mission_award)) {
			GloryHoleMissionInfo.Builder missionInfo =  GloryHoleMissionInfo.newBuilder();
			
			//Map<Object, GloryHoleMissionCfg> MissionMap = ConfigManager.getInstance().getConfigMap(GloryHoleMissionCfg.class);
			
			Map<Integer,Integer> counter = status.getMissionCounter();

			for (Integer mtype:GloryHoleMissionCfg.getAllType()) {
				GloryHoleMissionItem.Builder missionItem = GloryHoleMissionItem.newBuilder();
				missionItem.setMissionType(mtype);
				if (counter.containsKey(mtype)) {
					missionItem.setCount(counter.get(mtype));
				} else {
					missionItem.setCount(0);
				}
				if (status.getAwardrecord().containsKey(mtype)) {
					missionItem.addAllTook(status.getAwardrecord().get(mtype));
				}
				missionInfo.addMissionItem(missionItem);
			}
			response.setMissionInfo(missionInfo);
		} else if ((action == gloryHole_dailyInfo)||(action == gloryHole_take_daily_award)||(action == gloryHole_take_point_award)) {
			
			ChkDailyQuest(timeConfig,player,status);
			
			HPDailyQuestInfoRet.Builder dailyQuestInfoRet = HPDailyQuestInfoRet.newBuilder();
			Map<Integer, DailyQuestItem> map = status.getDailyQuestMap();
			
			
			for (Map.Entry<Integer, DailyQuestItem> entry : map.entrySet()) {
				QuestItem.Builder questItem = QuestItem.newBuilder();

				GloryHoleDailyCfg dailyQuestCfg = ConfigManager.getInstance().getConfigByKey(GloryHoleDailyCfg.class,
						entry.getValue().getId());
				if (dailyQuestCfg == null) {
					continue;
				}

				questItem.setQuestId(entry.getValue().getId());
				questItem.setQuestStatus(entry.getValue().getQuestStatus());
				questItem.setTakeStatus(entry.getValue().getTakeStatus());
				questItem.setQuestCompleteCount(entry.getValue().getCompleteCount());
				questItem.setTaskRewards(dailyQuestCfg.getAward());

				dailyQuestInfoRet.addAllDailyQuest(questItem);
			}

			for (int pointNumber : status.getDailyPoint()) {
				DailyPointCore.Builder dailyPointCore = DailyPointCore.newBuilder();
				dailyPointCore.setDailyPointNumber(pointNumber);
				dailyPointCore.setState(1);
				dailyQuestInfoRet.addDailyPointCore(dailyPointCore);
			}

			dailyQuestInfoRet.setDailyPoint(status.getPoint());
			
			response.setDailyInfo(dailyQuestInfoRet);
		}
		
		return response;
	}
	/**
	 * 選擇隊伍
	 * 
	 * @param protocol
	 * @param timeConfig
	 * @param player
	 * @param action
	 * @param status
	 */
	private void chooseTeamId(Protocol protocol,ActivityTimeCfg timeConfig,Player player,int action,Activity175Status status) {
		
		GloryHoleReq request = protocol.parseProtocol(GloryHoleReq.getDefaultInstance());
		
		int TeamId = request.getTeamId();
		
		if ((TeamId < 1) || (TeamId > 2)) {
			player.sendError(protocol.getType(), Status.error.PARAMS_INVALID_VALUE);
			return;
		}
		
		if (!GuaJiTime.isToday(GloryHoleActivityManager.getInstance().getGloryHoleStartTime())){
			player.sendError(protocol.getType(), Status.error.PARAMS_INVALID_VALUE);
			return;
		}
		
		if (status.getTeam() != 0) {
			if (GuaJiTime.isSameDay(status.getJoinTime(),GloryHoleActivityManager.getInstance().getGloryHoleStartTime())) {
				// 已選過團隊
				player.sendError(protocol.getType(), Status.error.PARAMS_INVALID_VALUE);
				return;
			}
		}
		status.setTeam(TeamId);
		// 順便重置一些東西
		status.setScore(0);
		status.setScoreTime(GuaJiTime.getMillisecond());
		status.getHistoryList().clear();
		status.setJoinTime(GloryHoleActivityManager.getInstance().getGloryHoleStartTime());
		//-- 重置任務相關--//
		//每日
		//status.getDailyQuestMap().clear();
		//status.getDailyPoint().clear();
		//status.setPoint(0);
		//任務成就不重置
		//status.getMissionCounter().clear();
		//status.getAwardrecord().clear();
		player.getPlayerData().updateActivity(timeConfig.getActivityId(),timeConfig.getStageId());
		
		SyncInfo(timeConfig,action,player,status);
	}
	/**
	 * 開始活動
	 * @param protocol
	 * @param timeConfig
	 * @param player
	 * @param action
	 * @param status
	 */
	private void GloryHoleStart(Protocol protocol,ActivityTimeCfg timeConfig,Player player,int action,Activity175Status status) {
		
		if (!GuaJiTime.isToday(GloryHoleActivityManager.getInstance().getGloryHoleStartTime())){
			player.sendError(protocol.getType(), Status.error.PARAMS_INVALID_VALUE);
			return;
		}
		
		long endTime = GuaJiTime.getTimeHourMinute(SysBasicCfg.getInstance().getAct175GameTime(GloryHoleGameTime.END));//今天晚上23點00分
		
		if (GuaJiTime.getMillisecond() >= endTime) {
			player.sendError(protocol.getType(), Status.error.PARAMS_INVALID_VALUE);
			return;
		} 
		
		if (status.getTeam() == 0) {
			// 未選隊伍
			player.sendError(protocol.getType(), Status.error.PARAMS_INVALID_VALUE);
			return;
		} else {
			// 此次活動未選隊伍
			if (!GuaJiTime.isSameDay(status.getJoinTime(),GloryHoleActivityManager.getInstance().getGloryHoleStartTime())) {
				player.sendError(protocol.getType(), Status.error.PARAMS_INVALID_VALUE);
				return;
			}
		}
		
//		if (status.getGameEndTime() > 0) {
//			if (GuaJiTime.getMillisecond() < status.getGameEndTime()) {
//				// 上一場還未結束不能重複開始
//				player.sendError(protocol.getType(), Status.error.ACTIVITY_NOT_ENDING_VALUE);
//				return;
//			}
//		}
		
		GloryHoleReq request = protocol.parseProtocol(GloryHoleReq.getDefaultInstance());
		
		boolean costItem = request.getCostItem();
		GloryHoleUseItemInfo UseItemInfo = request.getUseItem();
		boolean addTime  =  false; // 關閉 UseItemInfo.getAddTime(); // 是否使用增加時間
		boolean addbar  = UseItemInfo.getAddbar();  // 是否增加Feverbar
		boolean offset  = UseItemInfo.getOffset();  // 是否使用抵銷
		boolean addGain  = UseItemInfo.getAddGain(); // 是否使用增益
		Map<String,Boolean> useitemMap = new HashMap<>();
		useitemMap.put("addTime",addTime);
		useitemMap.put("addbar",addbar);
		useitemMap.put("offset",offset);
		useitemMap.put("addGain",addGain);
		
		ConsumeItems consumeItems = ConsumeItems.valueOf();
		List<ItemInfo> itemList = new ArrayList<ItemInfo>();
		String costItems = "";
		
		boolean isCost = false; // 是否需要消耗物品
		int countItem = 0;
		if ((addTime)||(addbar)||(offset)||(addGain)) { // 使用道具
			isCost = true;
			for (int itemType = 0 ; itemType < GsConst.GloryHoleItemType.ItemCount ;itemType++) {
				
				costItems = SysBasicCfg.getInstance().getGloryHoleGameItem(itemType);
				if (costItems.isEmpty()) {
					player.sendError(protocol.getType(),Status.error.ITEM_NOT_FOUND_VALUE); 
					return;
				}
				ItemInfo itemInfo = null;
				if (addTime && (itemType == GsConst.GloryHoleItemType.ADDTIME)) {
					itemInfo = ItemInfo.valueOf(costItems);
					countItem++;
				}
				if (addbar && (itemType == GsConst.GloryHoleItemType.ADDBAR)) {
					itemInfo = ItemInfo.valueOf(costItems);
					countItem++;
				}
				if (offset && (itemType == GsConst.GloryHoleItemType.OFFSET)) {
					itemInfo = ItemInfo.valueOf(costItems);
					countItem++;
				}
				if (addGain && (itemType == GsConst.GloryHoleItemType.ADDGAIN)) {
					itemInfo = ItemInfo.valueOf(costItems);
					countItem++;
				}
				
				if (itemInfo != null) {
					itemList.add(itemInfo);
				}
			}
		}
		
//		if (countItem == 2) {
//			if (player.getVipLevel() < 5) {
//				player.sendError(protocol.getType(),Status.error.VIP_NOT_ENOUGH); 
//				return;
//			}
//		}
		
		// 物品使用超過
		if (countItem > SysBasicCfg.getInstance().getGloryHoleMaxUseItem()) {
			player.sendError(protocol.getType(),Status.error.PARAMS_INVALID_VALUE); 
			return;
		}
		
		if (costItem) { // 使用消耗鑽石
			
			if (status.getUsePay() >= SysBasicCfg.getInstance().getGloryHoleMaxCost()) {
				player.sendError(protocol.getType(),Status.error.TIME_LIMIT_TODAY_BUY_TIMES_LIMIT_VALUE); 
				return;
			}
			
			isCost = true;
			costItems = SysBasicCfg.getInstance().getGloryHoleCost(status.getUsePay());
			
			if (costItems.isEmpty()) {
				player.sendError(protocol.getType(),Status.error.ITEM_NOT_FOUND_VALUE); 
				return;
			}
			
			ItemInfo itemInfo = ItemInfo.valueOf(costItems);			
			itemList.add(itemInfo);
			status.incUsePay();
		} else {
			if (status.getUseFree() >= SysBasicCfg.getInstance().getGloryHoleFree()) {
				player.sendError(protocol.getType(), Status.error.PARAMS_INVALID_VALUE);
				return;
			}
			status.incUseFree();
		}
		
		if (isCost) {
			boolean isAdd = false ;
			isAdd = consumeItems.addConsumeInfo(player.getPlayerData(),itemList);
			
			if (isAdd && consumeItems.checkConsume(player)) {
				if(!consumeItems.consumeTakeAffect(player, Action.ACTIVITY175_GLORY_HOLE)) {
					player.sendError(protocol.getType(),Status.error.ITEM_NOT_ENOUGH_VALUE); 
					return; 
				} 
			} else {
				player.sendError(protocol.getType(),Status.error.ITEM_NOT_ENOUGH_VALUE); 
				return; 
			}
		}
		
		long gameEndTime = GuaJiTime.getMillisecond()+ SysBasicCfg.getInstance().getGloryHoleGameTime()*1000;
		
		gameEndTime = addTime ? (gameEndTime + 30*1000) : gameEndTime;
		
		status.setGameEndTime(gameEndTime);
		
		status.setAddTime(addTime);
		
		status.setAddbar(addbar);
		
		status.setOffset(offset);
		
		status.setAddGain(addGain);
		
		player.getPlayerData().updateActivity(timeConfig.getActivityId(),timeConfig.getStageId());
		
		Msg hawkMsg = Msg.valueOf(GsConst.MsgType.DailyQuestMsg.GLORYHOLE_JOINTIMES,
				GuaJiXID.valueOf(GsConst.ObjType.PLAYER, player.getId()));
		hawkMsg.pushParam(1);
		GsApp.getInstance().postMsg(hawkMsg);
		//  參與活動計數
		Integer count = status.getMissionCounter().get(GsConst.GloryHoleMissionType.JOIN_ACTIVITY);
		
		status.getMissionCounter().put(GsConst.GloryHoleMissionType.JOIN_ACTIVITY, count == null? 1: ++count);
		
		// 道具使用計數
		Integer ItemUse = status.getMissionCounter().get(GsConst.GloryHoleMissionType.USE_ITEM);
		
		status.getMissionCounter().put(GsConst.GloryHoleMissionType.USE_ITEM, ItemUse == null? countItem: (countItem+ItemUse));
		
		SyncInfo(timeConfig,action,player,status);
		
		GloryHoleActivityManager.getInstance().updateJoinCount();
		
		int tapdbteam = status.getTeam();
		int playtimes = 0;
		if (!costItem) {
			playtimes = status.getUseFree();
		} else {
			playtimes = 100 + status.getUsePay();
		}
		String usetitem = TapDBUtil.TapDBString(useitemMap.toString());
		TapDBUtil.Event_GloryHole_Start(player,player.getTapDBUId(),playtimes, tapdbteam, usetitem);
	}
	/**
	 * 更新分數
	 * @param protocol
	 * @param timeConfig
	 * @param player
	 * @param action
	 * @param status
	 */
	
	private void GloryHoleUpdateScore(Protocol protocol,ActivityTimeCfg timeConfig,Player player,int action,Activity175Status status) {
		
		if (!GuaJiTime.isToday(GloryHoleActivityManager.getInstance().getGloryHoleStartTime())){
			player.sendError(protocol.getType(), Status.error.PARAMS_INVALID_VALUE);
			return;
		}
		
		long endTime = GuaJiTime.getTimeHourMinute(SysBasicCfg.getInstance().getAct175GameTime(GloryHoleGameTime.END));//今天晚上23點00分
		
		if (GuaJiTime.getMillisecond() >= endTime) {
			player.sendError(protocol.getType(), Status.error.PARAMS_INVALID_VALUE);
			return;
		} 
		
		if (status.getTeam() == 0) {
			// 未選隊伍
			player.sendError(protocol.getType(), Status.error.PARAMS_INVALID_VALUE);
			return;
		} else {
			// 此次活動未選隊伍
			if (!GuaJiTime.isSameDay(status.getJoinTime(),GloryHoleActivityManager.getInstance().getGloryHoleStartTime())) {
				player.sendError(protocol.getType(), Status.error.PARAMS_INVALID_VALUE);
				return;
			}
		}
		
		if ((status.getGameEndTime() == 0) || (GuaJiTime.getMillisecond() > status.getGameEndTime())) {
			//未參加活動或逾時
			player.sendError(protocol.getType(), Status.error.PARAMS_INVALID_VALUE);
			return;
		}
		
		GloryHoleReq request = protocol.parseProtocol(GloryHoleReq.getDefaultInstance());
		
		GloryHoleGameStatus gameStatus = request.getGameStatus();
		
		int fanatic_cli = gameStatus.getFanatic();
		int good_cli = gameStatus.getGood();
		
		if ((fanatic_cli < 0)||(good_cli < 0)) {
			player.sendError(protocol.getType(), Status.error.PARAMS_INVALID_VALUE);
			return;
		}
		
		int Newscore = request.getNewScore();
		
		if (Newscore <= 0) {
			player.sendError(protocol.getType(), Status.error.PARAMS_INVALID_VALUE);
			return;
		}
		
		Integer count = status.getMissionCounter().get(GsConst.GloryHoleMissionType.ACCUMULATE_SCROES);
		
		status.getMissionCounter().put(GsConst.GloryHoleMissionType.ACCUMULATE_SCROES, count == null? Newscore: (Newscore+count));
		

		int checkScore = GuaJiTime.isSameDay(status.getScoreTime(),GuaJiTime.getMillisecond())? status.getScore() : 0;
		
		status.addHistory(Newscore);
		
		if (Newscore > checkScore) {
			status.setScore(Newscore);
			status.setScoreTime(GuaJiTime.getMillisecond());
		}
		
		String reward = GloryHoleRewardCfg.getRewardByCfg(Newscore);
		if (!reward.isEmpty()){
			AwardItems awards = AwardItems.valueOf(reward);
			awards.rewardTakeAffectAndPush(player, Action.ACTIVITY175_GLORY_HOLE, 2);
			
			BehaviorLogger.log4Service(player, Source.USER_OPERATION, Action.ACTIVITY175_GLORY_HOLE,Params.valueOf("score", Newscore),
					Params.valueOf("reward", reward));
		}
		
		if (Newscore > status.getMissionCountByType(GsConst.GloryHoleMissionType.HIGH_SCROE)){
			status.getMissionCounter().put(GsConst.GloryHoleMissionType.HIGH_SCROE,Newscore);
		}

		//  狂熱值計數
		Integer fanatic = status.getMissionCounter().get(GsConst.GloryHoleMissionType.FANATIC);
		
		status.getMissionCounter().put(GsConst.GloryHoleMissionType.FANATIC, fanatic == null? fanatic_cli: fanatic_cli+fanatic);
		
		// 良好計數
		Integer good = status.getMissionCounter().get(GsConst.GloryHoleMissionType.GOOD_JOB);
		
		status.getMissionCounter().put(GsConst.GloryHoleMissionType.GOOD_JOB, good == null? good_cli:(good_cli+good));
		
		status.setGameEndTime(0); // 遊戲結束
		
		player.getPlayerData().updateActivity(timeConfig.getActivityId(),timeConfig.getStageId());
		
		SyncInfo(timeConfig,action,player,status);
		
		int tapdbteam = status.getTeam();
		int playtimes = 0;
		if (status.getUsePay() == 0) { // 表示使用免費
			playtimes = status.getUseFree();
		} else {
			playtimes = 100 + status.getUsePay();
		}
		Map<String,Boolean> useitemMap = new HashMap<>();
		useitemMap.put("addTime",status.isAddTime());
		useitemMap.put("addbar",status.isAddbar());
		useitemMap.put("offset",status.isOffset());
		useitemMap.put("addGain",status.isAddGain());
		String usetitem = TapDBUtil.TapDBString(useitemMap.toString());
		TapDBUtil.Event_GloryHole_Result(player,player.getTapDBUId(),playtimes, tapdbteam, usetitem,Newscore);
	}
	
	private static void ChkDailyQuest(ActivityTimeCfg timeConfig,Player player,Activity175Status status) {
		if (status == null) {
			return;
		}
		int count=0;
		for (GloryHoleDailyCfg dailyQuestCfg : ConfigManager.getInstance().getConfigMap(GloryHoleDailyCfg.class).values()) {
			DailyQuestItem item = new DailyQuestItem();
			if (status.getDailyQuestMap().containsKey(dailyQuestCfg.getId())) {
				continue;
			}
			if (dailyQuestCfg.getType() == GsConst.DailyQuestType.LOGIN) {
				item.setQuestStatus(1);
				item.setCompleteCount(1);

			} else {
				item.setQuestStatus(0);
				item.setCompleteCount(0);

			}
			item.setId(dailyQuestCfg.getId());
			item.setTakeStatus(0);
			status.addDailyQuest(dailyQuestCfg.getId(), item);
			count++;

		}
		if(count>0) {
			player.getPlayerData().updateActivity(timeConfig.getActivityId(),timeConfig.getStageId());
		}

	}
	
	protected boolean onTakeMissionAward(Protocol protocol,ActivityTimeCfg timeConfig,Player player,int action,Activity175Status status) {
		GloryHoleReq request = protocol.parseProtocol(GloryHoleReq.getDefaultInstance());
		
		int mtype = request.getTeamId();
		
		int count = request.getNewScore();
		
		String award = GloryHoleMissionCfg.getAwardbyTypeCount(mtype, count);
		
		if (!GloryHoleMissionCfg.getAllType().contains(mtype)||(count <= 0)||(award.isEmpty())) {
			// type錯誤
			player.sendError(protocol.getType(), Status.error.PARAMS_INVALID_VALUE);
			return false;
		}
		
		if (status.getMissionCountByType(mtype) < count) {
			player.sendError(protocol.getType(), Status.error.ASKTICKET_COUNT_LIMIT_VALUE);
			return false;
		}
		
		if (status.isAlreadyGot(mtype, count)) {
			player.sendError(protocol.getType(), Status.error.ALREADY_GIFT_VALUE);
			return false;
		}
		
		status.addAwardrecord(mtype,count);
		
		player.getPlayerData().updateActivity(timeConfig.getActivityId(),timeConfig.getStageId());
		
		// 奖励
		AwardItems awardItems = AwardItems.valueOf(award);
		awardItems.rewardTakeAffectAndPush(player, Action.TAKE_MISSION_AWARD, 2);// 记录领取日志
		
		SyncInfo(timeConfig,action,player,status);
		
		return true;
	}
	
	private void onTakeDailyPointAward(Protocol protocol,ActivityTimeCfg timeConfig,Player player,int action,Activity175Status status) {
		
		if (!GuaJiTime.isToday(GloryHoleActivityManager.getInstance().getGloryHoleStartTime())){
			player.sendError(protocol.getType(), Status.error.PARAMS_INVALID_VALUE);
			return;
		}
		
		GloryHoleReq request = protocol.parseProtocol(GloryHoleReq.getDefaultInstance());

		int count = request.getNewScore();

		GloryHoleDailyPointCfg dailyQuestPointCfg = ConfigManager.getInstance().getConfigByKey(GloryHoleDailyPointCfg.class,
				count);
		if (dailyQuestPointCfg == null) {
			player.sendError(protocol.getType(), Status.error.CONFIG_ERROR);
			return ;
		}

		if ((status.getPoint() < count)) //未達領取標準
		{
			player.sendError(protocol.getType(), Status.error.POINT_NOI_ENOUGH);
			return;
		}
		
		if (status.getDailyPoint().contains(count))// 已領取
		{
			player.sendError(protocol.getType(), Status.error.AWARD_ALREADY_GOT_ERROR);
			return;
		}
		
		status.getDailyPoint().add(count);// 已领取
		
		player.getPlayerData().updateActivity(timeConfig.getActivityId(),timeConfig.getStageId());

		String award = dailyQuestPointCfg.getAward();

		// 奖励
		AwardItems awardItems = AwardItems.valueOf(award);
		awardItems.rewardTakeAffectAndPush(player, Action.TAKE_DAILY_QUEST_AWARD, 2);// 记录领取日志
		
		SyncInfo(timeConfig,action,player,status);
	}
	
	protected boolean onTakeDailyAward(Protocol protocol,ActivityTimeCfg timeConfig,Player player,int action,Activity175Status status) {
		
		if (!GuaJiTime.isToday(GloryHoleActivityManager.getInstance().getGloryHoleStartTime())){
			player.sendError(protocol.getType(), Status.error.PARAMS_INVALID_VALUE);
			return false;
		}
		
		GloryHoleReq request = protocol.parseProtocol(GloryHoleReq.getDefaultInstance());

		int id = request.getTeamId();

		DailyQuestItem quest = status.getDailyQuest(id);

		if (quest == null) {
			player.sendError(protocol.getType(), Status.error.DATA_NOT_FOUND_VALUE);
			return true;
		}

		if (quest.getQuestStatus() != 1) {
			player.sendError(protocol.getType(), Status.error.PARAMS_INVALID_VALUE);
			return true;
		}

		if (quest.getTakeStatus() != 0) {
			player.sendError(protocol.getType(), Status.error.PARAMS_INVALID_VALUE);
			return true;
		}

		GloryHoleDailyCfg dailyQuestCfg = ConfigManager.getInstance().getConfigByKey(GloryHoleDailyCfg.class, id);

		if (dailyQuestCfg == null) {
			player.sendError(protocol.getType(), Status.error.CONFIG_NOT_FOUND_VALUE);
			return true;
		}

		String award = dailyQuestCfg.getAward();

		// 奖励
		AwardItems awardItems = AwardItems.valueOf(award);
		awardItems.rewardTakeAffectAndPush(player, Action.GLORYHOLE_DAILY_QUEST_AWARD, 2);// 记录领取日志

		quest.setTakeStatus(1);

		int point = dailyQuestCfg.getPoint();

		status.setPoint(point + status.getPoint());

		player.getPlayerData().updateActivity(timeConfig.getActivityId(),timeConfig.getStageId());

		SyncInfo(timeConfig,action,player,status);
		
		BehaviorLogger.log4Service(player,Source.USER_OPERATION, Action.GLORYHOLE_DAILY_QUEST_AWARD, 
				Params.valueOf("id", id),
				Params.valueOf("getPoint", point), 
				Params.valueOf("award", award));
//		if (point > 0) {
//			Msg hawkMsg = Msg.valueOf(GsConst.MsgType.DailyQuestMsg.DAILY_POINT,
//					GuaJiXID.valueOf(GsConst.ObjType.PLAYER, player.getId()));
//			hawkMsg.pushParam(point);
//			onDailyQuestCount(hawkMsg);
//		}

		// sendDailyQuestInfo(id,award);
		//sendAllDailyQuestInfo(HP.code.TAKE_DAILY_QUEST_AWARD_S_VALUE);
		return true;
	}
		
}
