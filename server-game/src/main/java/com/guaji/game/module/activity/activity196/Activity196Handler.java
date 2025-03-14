package com.guaji.game.module.activity.activity196;

import java.util.Map;

import org.guaji.app.AppObj;
import org.guaji.config.ConfigManager;
import org.guaji.net.protocol.IProtocolHandler;
import org.guaji.net.protocol.Protocol;

import com.guaji.game.config.ActivityDailyQuest196Cfg;
import com.guaji.game.config.ActivityDailyQuestPoint196Cfg;
import com.guaji.game.config.ActivityQuest196Cfg;
import com.guaji.game.config.ActivityTimeCfg;
import com.guaji.game.entity.DailyQuestItem;
import com.guaji.game.item.AwardItems;
import com.guaji.game.log.BehaviorLogger.Action;
import com.guaji.game.player.Player;
import com.guaji.game.protocol.Activity5.CycleStageQuest;
import com.guaji.game.protocol.Activity5.CycleStageReq;
import com.guaji.game.protocol.Activity5.CycleStageResp;
import com.guaji.game.protocol.Const;
import com.guaji.game.protocol.DailyQuest.DailyPointCore;
import com.guaji.game.protocol.DailyQuest.HPDailyQuestInfoRet;
import com.guaji.game.protocol.DailyQuest.QuestItem;
import com.guaji.game.protocol.HP;
import com.guaji.game.protocol.Status;
import com.guaji.game.util.ActivityUtil;
import com.guaji.game.util.GsConst;

/**
 * 循環關卡協定
 */
public class Activity196Handler implements IProtocolHandler {
	static final int cycleStage_questInfo = 0;
	static final int cycleStage_take_quest_award = 1;
	static final int cycleStage_dailyInfo = 2;
	static final int cycleStage_take_daily_award = 3;
	static final int cycleStage_take_point_award = 4;

	@Override
	public boolean onProtocol(AppObj appObj, Protocol protocol) {
		// TODO Auto-generated method stub
		
		Player player = (Player) appObj;
		
		// 检测活动是否开放
		int activityId = Const.ActivityId.ACTIVITY196_CycleStage_Part2_VALUE;
		ActivityTimeCfg timeConfig = ActivityUtil.getCurActivityTimeCfg(activityId);
		if (timeConfig == null || player == null) {
			// 活动已关闭
			if (timeConfig == null) {
				ActivityUtil.CycleStageClearItem2(player);
			}
			player.sendError(protocol.getType(), Status.error.ACTIVITY_CLOSE);
			return true;
		}

		// 解析接受到的数据
		CycleStageReq request = protocol.parseProtocol(CycleStageReq.getDefaultInstance());

		int action = request.getAction();
				
				
		int stageId = timeConfig.getStageId();
		Activity196Status status = ActivityUtil.getActivityStatus(player.getPlayerData(), activityId, stageId, Activity196Status.class);
		
		if (status == null) {
			player.sendError(protocol.getType(), Status.error.DATA_NOT_FOUND_VALUE);
			return true;
		}
		
		ActivityUtil.CycleStageInitItem2(player, timeConfig, status);
				
		// 业务分支处理
		switch (action) {
		case cycleStage_questInfo:
		case  cycleStage_dailyInfo:
			SyncInfo(timeConfig,action,player,status);
			break;
		case  cycleStage_take_quest_award:
			onTakeQuestAward(protocol,timeConfig,player,action,status);
			break;
		case  cycleStage_take_daily_award:
			onTakeDailyAward(protocol, timeConfig, player,action, status);
			break;
		case  cycleStage_take_point_award:
			onTakeDailyPointAward(protocol, timeConfig, player,action, status);
			break;
		default:
			player.sendError(protocol.getType(), Status.error.PARAMS_INVALID_VALUE);
			break;
		}
		return true;
	}
	
	protected boolean onTakeQuestAward(Protocol protocol,ActivityTimeCfg timeConfig,Player player,int action,Activity196Status status) {
		CycleStageReq request = protocol.parseProtocol(CycleStageReq.getDefaultInstance());
		
		int id = request.getChoose();
		
		ActivityQuest196Cfg questCfg = ConfigManager.getInstance().getConfigByKey(ActivityQuest196Cfg.class, id);
		
		if (questCfg == null) {
			player.sendError(protocol.getType(), Status.error.PARAMS_INVALID_VALUE);
			return false;
		}
		
		if (status.getAwardrecord().contains(id)){
			player.sendError(protocol.getType(), Status.error.ALREADY_GIFT_VALUE);
			return false;
		}
		
		int passId = status.getPassStage();
		
		boolean done = (passId >= questCfg.getTarget());
		
		String award = questCfg.getAwards();
		
		if (!done) {
			player.sendError(protocol.getType(), Status.error.ACTIVITY_NOT_ENDING_VALUE);
			return false;
		}
		
		status.getAwardrecord().add(id);
		player.getPlayerData().updateActivity(timeConfig.getActivityId(),timeConfig.getStageId());
		
		// 奖励
		AwardItems awardItems = AwardItems.valueOf(award);
		awardItems.rewardTakeAffectAndPush(player, Action.TAKE_MISSION_AWARD, 2);// 记录领取日志
		
		SyncInfo(timeConfig,action,player,status);
			
		return true;
	}
		
	/**
	 * 同步資訊
	 * @param action
	 * @param player
	 * @param timeCfg
	 * @param status
	 */
	private static void SyncInfo(ActivityTimeCfg timeConfig,int action,Player player, Activity196Status status) {
		CycleStageResp.Builder builder = getBuilder(timeConfig,action,player,status);
		player.sendProtocol(Protocol.valueOf(HP.code.ACTIVITY196_CYCLE_STAGE_S, builder));
	}
	
	private static CycleStageResp.Builder getBuilder(ActivityTimeCfg timeConfig,int action,Player player,Activity196Status status) {
		// 返回包
		CycleStageResp.Builder response = CycleStageResp.newBuilder();
		
		response.setAction(action);
		
		if ((action == cycleStage_questInfo) || (action == cycleStage_take_quest_award)) {
			CycleStageQuest.Builder questInfo =  CycleStageQuest.newBuilder();
			questInfo.setPassId(status.getPassStage());
			questInfo.addAllTakeId(status.getAwardrecord());
			response.setQuestInfo(questInfo);
		} if ((action == cycleStage_dailyInfo) || (action == cycleStage_take_daily_award) ||(action == cycleStage_take_point_award) ) {
			
				ChkDailyQuest(timeConfig,player,status);
			
				HPDailyQuestInfoRet.Builder dailyQuestInfoRet = HPDailyQuestInfoRet.newBuilder();
				Map<Integer, DailyQuestItem> map = status.getDailyQuestMap();
			
			
			for (Map.Entry<Integer, DailyQuestItem> entry : map.entrySet()) {
				QuestItem.Builder questItem = QuestItem.newBuilder();

				ActivityDailyQuest196Cfg dailyQuestCfg = ConfigManager.getInstance().getConfigByKey(ActivityDailyQuest196Cfg.class,
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
	
	private static void ChkDailyQuest(ActivityTimeCfg timeConfig,Player player,Activity196Status status) {
		if (status == null) {
			return;
		}
		int count=0;
		for (ActivityDailyQuest196Cfg dailyQuestCfg : ConfigManager.getInstance().getConfigMap(ActivityDailyQuest196Cfg.class).values()) {
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
	
	private void onTakeDailyPointAward(Protocol protocol,ActivityTimeCfg timeConfig,Player player,int action,Activity196Status status) {
				
		CycleStageReq request = protocol.parseProtocol(CycleStageReq.getDefaultInstance());

		int count = request.getChoose();

		ActivityDailyQuestPoint196Cfg dailyQuestPointCfg = ConfigManager.getInstance().getConfigByKey(ActivityDailyQuestPoint196Cfg.class,
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
	
	protected boolean onTakeDailyAward(Protocol protocol,ActivityTimeCfg timeConfig,Player player,int action,Activity196Status status) {
				
		CycleStageReq request = protocol.parseProtocol(CycleStageReq.getDefaultInstance());

		int id = request.getChoose();

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

		ActivityDailyQuest196Cfg dailyQuestCfg = ConfigManager.getInstance().getConfigByKey(ActivityDailyQuest196Cfg.class, id);

		if (dailyQuestCfg == null) {
			player.sendError(protocol.getType(), Status.error.CONFIG_NOT_FOUND_VALUE);
			return true;
		}

		String award = dailyQuestCfg.getAward();

		// 奖励
		AwardItems awardItems = AwardItems.valueOf(award);
		awardItems.rewardTakeAffectAndPush(player, Action.TAKE_DAILY_QUEST_AWARD, 2);// 记录领取日志

		quest.setTakeStatus(1);

		int point = dailyQuestCfg.getPoint();

		status.setPoint(point + status.getPoint());

		player.getPlayerData().updateActivity(timeConfig.getActivityId(),timeConfig.getStageId());

		SyncInfo(timeConfig,action,player,status);
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
