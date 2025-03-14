package com.guaji.game.module.quest;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.guaji.annotation.MessageHandlerAnno;
import org.guaji.annotation.ProtocolHandlerAnno;
import org.guaji.config.ConfigManager;
import org.guaji.msg.Msg;
import org.guaji.net.protocol.Protocol;
import org.guaji.os.GuaJiTime;
import org.guaji.xid.GuaJiXID;

import com.guaji.game.protocol.Const.QuestEventType;
import com.guaji.game.protocol.Const.QuestState;
import com.guaji.game.config.ActivityTimeCfg;
import com.guaji.game.config.FunctionUnlockCfg;
import com.guaji.game.config.QuestCfg;
import com.guaji.game.config.SysBasicCfg;
import com.guaji.game.entity.DailyQuestEntity;
import com.guaji.game.entity.DailyQuestItem;
import com.guaji.game.entity.QuestEntity;
import com.guaji.game.entity.QuestItem;
import com.guaji.game.entity.WeeklyQuestEntity;
import com.guaji.game.entity.WeeklyQuestItem;
import com.guaji.game.item.AwardItems;
import com.guaji.game.log.BehaviorLogger;
import com.guaji.game.log.BehaviorLogger.Action;
import com.guaji.game.log.BehaviorLogger.Params;
import com.guaji.game.log.BehaviorLogger.Source;
import com.guaji.game.player.Player;
import com.guaji.game.player.PlayerModule;
import com.guaji.game.util.ActivityUtil;
import com.guaji.game.util.GsConst;
import com.guaji.game.util.TapDBUtil.TapDBSource;
import com.guaji.game.protocol.Const;
import com.guaji.game.protocol.HP;
import com.guaji.game.protocol.Status;
import com.guaji.game.protocol.Quest.HPGetQuestList;
import com.guaji.game.protocol.Quest.HPGetQuestListRet;
import com.guaji.game.protocol.Quest.HPGetQuestRedPointStatusRet;
import com.guaji.game.protocol.Quest.HPGetSingeQuestReward;
import com.guaji.game.protocol.Quest.HPQuestUpdate;
import com.guaji.game.protocol.Quest.QuestInfo;

/**
 * 玩家任务模块;
 */
public class PlayerQuestModule extends PlayerModule {

	/**
	 * 构造函数
	 * 
	 * @param player
	 */
	public PlayerQuestModule(Player player) {
		super(player);
	}

	@Override
	protected boolean onPlayerLogin() {
		player.getPlayerData().loadQuestEntity();
		player.getPlayerData().syncStateInfo();
		return true;
	}
	
	/**
	 * 协议响应
	 * 
	 * @param protocol
	 * @return
	 */
	@Override
	public boolean onProtocol(Protocol protocol) {
//		if (isListenProto(protocol.getType())) {
//
//		}
		return super.onProtocol(protocol);
	}

	/**
	 * 获取任务列表;
	 * 
	 * @param hawkProtocol
	 * @return
	 */
	@ProtocolHandlerAnno(code = HP.code.QUEST_GET_QUEST_LIST_C_VALUE)
	private boolean onGetQuestList(Protocol hawkProtocol) {

		QuestEntity qusetEntity = player.getPlayerData().getQuestEntity();

		List<QuestItem> questList = new ArrayList<QuestItem>();
		questList.addAll(qusetEntity.getQuestItemMap().values());
		if (questList == null || questList.size() == 0) {
			HPGetQuestListRet.Builder questListBuilder = HPGetQuestListRet.newBuilder();
			player.sendProtocol(Protocol.valueOf(HP.code.QUEST_GET_QUEST_LIST_S, questListBuilder));
			// 空了
			return true;
		}
		List<Integer> finishedQuestList = player.getPlayerData().getFinishedQuestList();

		sendQuestList(questList, finishedQuestList);
		return true;
	}

	/**
	 * 任务红点
	 * 
	 * @param Protocol
	 * @return
	 */
	@ProtocolHandlerAnno(code = HP.code.QUEST_GET_QUEST_REDPOINT_C_VALUE)
	private boolean onQuestRedpoint(Protocol protocol) {
		HPGetQuestRedPointStatusRet.Builder builder = HPGetQuestRedPointStatusRet.newBuilder();

		QuestEntity qusetEntity = player.getPlayerData().getQuestEntity();
		DailyQuestEntity dailyQuestEntity = player.getPlayerData().loadDailyQuestEntity();
		WeeklyQuestEntity weeklyQuestEntity = player.getPlayerData().loadWeeklyQuestEntity();
		
		// 每日紅點
		builder.setDailyQuestStatus(0);
		boolean havePoint = false;
		for (DailyQuestItem dailyItem : dailyQuestEntity.getDailyQuestMap().values()) {
			if (dailyItem.getQuestStatus() == 1 && dailyItem.getTakeStatus() == 0) {
				builder.setDailyQuestStatus(1);
				havePoint = true;
				break;
			}
		}
		if (!havePoint) {
			for(Integer State : dailyQuestEntity.getDailyPointState().values()) {
				if (State == 1) {
					builder.setDailyQuestStatus(1);
					break;
				}
			}
		}
		
		
		// 周任務紅點
		builder.setMainQuestStatus(0); // 主線任務改放周任務紅點
		havePoint = false;
		for (WeeklyQuestItem weeklyItem : weeklyQuestEntity.getQuestMap().values()) {
			if (weeklyItem.getQuestStatus() == 1 && weeklyItem.getTakeStatus() == 0) {
				builder.setMainQuestStatus(1);
				havePoint = true;
				break;
			}
		}
		
		if (!havePoint) {
			for(Integer State : weeklyQuestEntity.getPointState().values()) {
				if (State == 1) {
					builder.setMainQuestStatus(1);
					break;
				}
			}
		}
		
		// 成就任務紅點
		builder.setAchievementQuestStatus(0);

		for (QuestItem item : qusetEntity.getQuestItemMap().values()) {
			QuestCfg config = ConfigManager.getInstance().getConfigByKey(QuestCfg.class, item.getItemId());

			if (config == null) {
				continue;
			}

			if (item.getState() == QuestState.FINISHED_VALUE) {
				// 任务等级未达到不发送红点
				if (config.getOpenLevel() <= player.getLevel()) {
//					if (config.getQuestType() == 1)// 主线改為周任務
//					{
//						builder.setMainQuestStatus(1);
//					}

					if (config.getQuestType() == 2)// 成就
					{
						builder.setAchievementQuestStatus(1);
						break;
					}
				}

			}
		}

		player.sendProtocol(Protocol.valueOf(HP.code.QUEST_GET_QUEST_REDPOINT_S, builder));

		return true;
	}

	/**
	 * Send the quest list;
	 * 
	 * @param questList
	 * @param finishedQuestList
	 */
	private void sendQuestList(List<QuestItem> questList, List<Integer> finishedQuestList) {
		HPGetQuestListRet.Builder questListBuilder = HPGetQuestListRet.newBuilder();

		for (QuestItem eachItem : questList) {
			// do not send the quest which state == unactive
			if (eachItem.getState() == QuestState.UNACTIVE_VALUE || eachItem.getState() == QuestState.REWARD_VALUE) {
				continue;
			}

			QuestCfg config = ConfigManager.getInstance().getConfigByKey(QuestCfg.class, eachItem.getItemId());

			if (config == null) {
				return;
			}

			if (config.getQuestType() != 1 || config.getOpenLevel() > player.getLevel()) {
				continue;
			}

			QuestInfo.Builder quest = QuestInfo.newBuilder();
			quest.setFinishedCount(eachItem.getFinishedCount());
			quest.setId(eachItem.getItemId());
			quest.setQuestState(eachItem.getState());
			quest.setTaskRewards(config.getReward());
			questListBuilder.addQuestList(quest);
		}

		questListBuilder.addAllFinishedQuestList(finishedQuestList);
		// send quest list
		player.sendProtocol(Protocol.valueOf(HP.code.QUEST_GET_QUEST_LIST_S, questListBuilder));
	}

	/**
	 * 获取任务列表;
	 * 
	 * @param protocol
	 * @return
	 */
	@ProtocolHandlerAnno(code = HP.code.QUEST_GET_ACHIVIMENT_LIST_C_VALUE)
	private boolean onGetAchivimentList(Protocol protocol) {

		QuestEntity qusetEntity = player.getPlayerData().getQuestEntity();

		List<QuestItem> questList = new ArrayList<QuestItem>();
		questList.addAll(qusetEntity.getQuestItemMap().values());

		if (questList == null || questList.size() == 0) {
			// 空了
			return true;
		}
		List<Integer> finishedQuestList = player.getPlayerData().getFinishedQuestList();

		sendAchivimentList(questList, finishedQuestList);
		return true;
	}

	/**
	 * Send the quest list;
	 * 
	 * @param questList
	 * @param finishedQuestList
	 */
	private void sendAchivimentList(List<QuestItem> questList, List<Integer> finishedQuestList) {
		HPGetQuestListRet.Builder questListBuilder = HPGetQuestListRet.newBuilder();

		for (QuestItem eachItem : questList) {
			// do not send the quest which state == unactive
			if (eachItem.getState() == QuestState.UNACTIVE_VALUE || eachItem.getState() == QuestState.REWARD_VALUE) {
				continue;
			}

			QuestCfg config = ConfigManager.getInstance().getConfigByKey(QuestCfg.class, eachItem.getItemId());

			if (config == null) {
				return;
			}

			// 不符合类型或未达到开启等级
			if (config.getQuestType() != 2 || config.getOpenLevel() > player.getLevel()) {
				continue;
			}

			QuestInfo.Builder quest = QuestInfo.newBuilder();
			quest.setFinishedCount(eachItem.getFinishedCount()).setId(eachItem.getItemId())
					.setQuestState(eachItem.getState()).setTaskRewards(config.getReward());
			questListBuilder.addQuestList(quest);
		}
		questListBuilder.addAllFinishedQuestList(finishedQuestList);
		// send quest list
		player.sendProtocol(Protocol.valueOf(HP.code.QUEST_GET_ACHIVIMENT_LIST_S_VALUE, questListBuilder));
	}
	
	/**
	 * 获取任务列表;
	 * 
	 * @param protocol
	 * @return
	 */
	@ProtocolHandlerAnno(code = HP.code.QUEST_GET_NEWBIE_LIST_C_VALUE)
	private boolean onGetNewbieList(Protocol protocol) {
				
		if (!FunctionUnlockCfg.checkUnlock(player,null,Const.FunctionType.Newbie_Unlock)){
			sendError(protocol.getType(), Status.error.CONDITION_NOT_ENOUGH);
			return false;
		}
		
		int surplusTime = player.getPlayerData().getStateEntity().calcNewbieSurplusTime();
		
		if (surplusTime == 0) { // 活動結束
			return false;
		}
		
		if (surplusTime < 0) { // 設定活動開啟			
			player.getPlayerData().getStateEntity().setNewbieDate(GuaJiTime.getCalendar().getTime());
		}
		
		QuestEntity qusetEntity = player.getPlayerData().getQuestEntity();

		List<QuestItem> questList = new ArrayList<QuestItem>();
		questList.addAll(qusetEntity.getQuestItemMap().values());

		if (questList == null || questList.size() == 0) {
			// 空了
			return true;
		}
		List<Integer> finishedQuestList = player.getPlayerData().getFinishedQuestList();

		sendNewbieList(questList, finishedQuestList);
		return true;
	}

	/**
	 * Send the quest list;
	 * 
	 * @param questList
	 * @param finishedQuestList
	 */
	private void sendNewbieList(List<QuestItem> questList, List<Integer> finishedQuestList) {
		HPGetQuestListRet.Builder questListBuilder = HPGetQuestListRet.newBuilder();
		
		int surplusTime = player.getPlayerData().getStateEntity().calcNewbieSurplusTime();

		for (QuestItem eachItem : questList) {
			// do not send the quest which state == unactive
			if (eachItem.getState() == QuestState.UNACTIVE_VALUE) {
				continue;
			}

			QuestCfg config = ConfigManager.getInstance().getConfigByKey(QuestCfg.class, eachItem.getItemId());

			if (config == null) {
				return;
			}

			// 不符合类型或未达到开启等级
			if (config.getQuestType() != 3 || config.getOpenLevel() > player.getLevel()) {
				continue;
			}

			QuestInfo.Builder quest = QuestInfo.newBuilder();
			quest.setFinishedCount(eachItem.getFinishedCount()).setId(eachItem.getItemId())
					.setQuestState(eachItem.getState()).setTaskRewards(config.getReward());
			questListBuilder.addQuestList(quest);
		}
		questListBuilder.setLeftTime(surplusTime);
		questListBuilder.addAllFinishedQuestList(finishedQuestList);
		// send quest list
		player.sendProtocol(Protocol.valueOf(HP.code.QUEST_GET_NEWBIE_LIST_S_VALUE, questListBuilder));
	}

	/**
	 * 获取任务列表;
	 * 
	 * @param protocol
	 * @return
	 */
	@ProtocolHandlerAnno(code = HP.code.QUEST_GET_ACTIVITY_LIST_C_VALUE)
	private boolean onGetActivityList(Protocol protocol) {
		
		HPGetQuestList getReward = protocol.parseProtocol(HPGetQuestList.getDefaultInstance());
		// 检测活动是否开放
		int activityId = getReward.getActivityId();
		
		if ((activityId != Const.ActivityId.ACTIVITY155_ACHIEVE_LEVEL_VALUE) && (activityId != Const.ActivityId.ACTIVITY156_ACHIEVE_FIGHTVALUE_VALUE)){
			player.sendError(protocol.getType(), Status.error.PARAMS_INVALID_VALUE);
			return true;
		}
		
		ActivityTimeCfg timeCfg = ActivityUtil.getCurActivityTimeCfg(activityId);
		if (timeCfg == null) {
			// 活动已关闭
			player.sendError(protocol.getType(), Status.error.ACTIVITY_CLOSE);
			return true;
		}
		
		if	(activityId == Const.ActivityId.ACTIVITY156_ACHIEVE_FIGHTVALUE_VALUE) {      
			int  surplusTime = ActivityUtil.clacQuestSurplusTime(player.getPlayerData().getPlayerEntity().getCreateTime());
	        if (surplusTime <= 0) {
	            return true;
	        }
		}
				
		QuestEntity qusetEntity = player.getPlayerData().getQuestEntity();

		List<QuestItem> questList = new ArrayList<QuestItem>();
		questList.addAll(qusetEntity.getQuestItemMap().values());

		if (questList == null || questList.size() == 0) {
			// 空了
			return true;
		}
		List<Integer> finishedQuestList = player.getPlayerData().getFinishedQuestList();

		sendActivityList(activityId,questList, finishedQuestList);
		return true;
	}

	/**
	 * Send the quest list;
	 * 
	 * @param questList
	 * @param finishedQuestList
	 */
	private void sendActivityList(int activityId,List<QuestItem> questList, List<Integer> finishedQuestList) {
		HPGetQuestListRet.Builder questListBuilder = HPGetQuestListRet.newBuilder();
		
		int surplusTime = -1;
		
		int QuestType = 0;
		if (activityId == Const.ActivityId.ACTIVITY155_ACHIEVE_LEVEL_VALUE) {
			QuestType = 4;
		} else if (activityId == Const.ActivityId.ACTIVITY156_ACHIEVE_FIGHTVALUE_VALUE) {
			QuestType = 5;
	        surplusTime = ActivityUtil
	                .clacQuestSurplusTime(player.getPlayerData().getPlayerEntity().getCreateTime());
		} else {
			return;
		}
		
		for (QuestItem eachItem : questList) {
			// do not send the quest which state == unactive
			if (eachItem.getState() == QuestState.UNACTIVE_VALUE) {
				continue;
			}

			QuestCfg config = ConfigManager.getInstance().getConfigByKey(QuestCfg.class, eachItem.getItemId());

			if (config == null) {
				return;
			}

			// 不符合类型或未达到开启等级
			if (config.getQuestType() != QuestType || config.getOpenLevel() > player.getLevel()) {
				continue;
			}

			QuestInfo.Builder quest = QuestInfo.newBuilder();
			quest.setFinishedCount(eachItem.getFinishedCount()).setId(eachItem.getItemId())
					.setQuestState(eachItem.getState()).setTaskRewards(config.getReward());
			questListBuilder.addQuestList(quest);
		}
		questListBuilder.setLeftTime(surplusTime);
		questListBuilder.setActivityId(activityId);
		questListBuilder.addAllFinishedQuestList(finishedQuestList);
		// send quest list
		player.sendProtocol(Protocol.valueOf(HP.code.QUEST_GET_ACTIVITY_LIST_S, questListBuilder));
	}

	/**
	 * 获取单个任务奖励;
	 * 
	 * @param hawkProtocol
	 * @return
	 */
	@ProtocolHandlerAnno(code = HP.code.QUEST_GET_SINGLE_QUEST_REWARD_C_VALUE)
	protected boolean onGetSingleQuestReward(Protocol protocol) {
		
		QuestEntity qusetEntity = player.getPlayerData().getQuestEntity();

		HPGetSingeQuestReward getReward = protocol.parseProtocol(HPGetSingeQuestReward.getDefaultInstance());
		int questItemId = getReward.getQuestId();
		QuestItem eachItem = player.getPlayerData().getQuestByItemId(questItemId);
		if (eachItem == null) {
			return false;
		}
		// 是否已经完成
		if (eachItem.getState() != QuestState.FINISHED_VALUE) {
			return false;
		}
		// 已经领取完了
		if (eachItem.getState() == QuestState.REWARD_VALUE) {
			return false;
		}
		// send reward
		QuestCfg config = ConfigManager.getInstance().getConfigByKey(QuestCfg.class, questItemId);
		if (config == null) {
			return false;
		}
		
		if ((config.getQuestType() == 1)||(config.getQuestType() == 2)) {
			// 領任務成就獎勵
			if (!FunctionUnlockCfg.checkUnlock(player,null,Const.FunctionType.quest_Unlock)){
				sendError(protocol.getType(), Status.error.CONDITION_NOT_ENOUGH);
				return true;
			}
		}
		
		if (config.getQuestType() == 3) { //新手限時任務
			
			if (!FunctionUnlockCfg.checkUnlock(player,null,Const.FunctionType.Newbie_Unlock)){
				sendError(protocol.getType(), Status.error.CONDITION_NOT_ENOUGH);
				return false;
			}
			
			int surplusTime = player.getPlayerData().getStateEntity().calcNewbieSurplusTime();
			if (surplusTime <= 0) { // 未開啟或時間已過
				return false;
			}
			///新手限時任務完成次數
			QuestEventBus.fireQuestEventOneTime(QuestEventType.NEWBIE_QUEST_FINISHI,
					GuaJiXID.valueOf(GsConst.ObjType.PLAYER, player.getId()));
		}
		
		if (config.getQuestType() == 4) {
			if (!FunctionUnlockCfg.checkUnlock(player,null,Const.FunctionType.quest_Unlock)){
				sendError(protocol.getType(), Status.error.CONDITION_NOT_ENOUGH);
				return false;
			}
		}
		
		if (config.getQuestType() == 5) {
			if (!FunctionUnlockCfg.checkUnlock(player,null,Const.FunctionType.quest_Unlock)){
				sendError(protocol.getType(), Status.error.CONDITION_NOT_ENOUGH);
				return false;
			}
		}
		
		// reward
		AwardItems awardItems = AwardItems.valueOf(config.getReward());
		awardItems.rewardTakeAffectAndPush(player, Action.QUEST_REWARD, 1,TapDBSource.Quest_Reward,Params.valueOf("id", questItemId));
		// persistence
		eachItem.setState(QuestState.REWARD_VALUE);
		qusetEntity.update();
		// update quest state
		HPQuestUpdate.Builder updateQuest = HPQuestUpdate.newBuilder();
		updateQuest.setQuest(QuestInfo.newBuilder().setFinishedCount(eachItem.getFinishedCount()).setId(config.getId())
				.setQuestState(eachItem.getState()).setTaskRewards(config.getReward()));
		player.sendProtocol(Protocol.valueOf(HP.code.QUEST_SINGLE_UPDATE_S, updateQuest));
		
		BehaviorLogger.log4Service(player.getId(), Source.USER_OPERATION, Action.QUEST_REWARD,
				Params.valueOf("Id", questItemId),
				Params.valueOf("award", config.getReward()),
				Params.valueOf("QuestType", config.getQuestType()));
//
//		List<QuestEntity> questList = player.getPlayerData().getQuestEntities();
//		List<Integer> finishedQuestList = player.getPlayerData().getFinishedQuestList();
//		sendQuestList(questList, finishedQuestList);

		return true;
	}

	@MessageHandlerAnno(code = GsConst.MsgType.QUEST_EVENT)
	private void onQuestEvent(Msg msg) {
		QuestEvent event = msg.getParam(0);
		player.getPlayerData().onQuestEvent(event);
	}
}
