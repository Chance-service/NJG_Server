package com.guaji.game.module.activity.activity193;

import java.util.List;

import org.guaji.app.AppObj;
import org.guaji.config.ConfigManager;
import org.guaji.net.protocol.IProtocolHandler;
import org.guaji.net.protocol.Protocol;

import com.guaji.game.config.ActivityTimeCfg;
import com.guaji.game.config.SingleBossQuestCfg;
import com.guaji.game.config.SysBasicCfg;
import com.guaji.game.item.AwardItems;
import com.guaji.game.log.BehaviorLogger.Action;
import com.guaji.game.manager.SingleBossRankManager;
import com.guaji.game.player.Player;
import com.guaji.game.protocol.Activity5.SingleBossData;
import com.guaji.game.protocol.Activity5.SingleBossQuest;
import com.guaji.game.protocol.Activity5.SingleBossRankMessage;
import com.guaji.game.protocol.Activity5.SingleBossRanking;
import com.guaji.game.protocol.Activity5.SingleBossReq;
import com.guaji.game.protocol.Activity5.SingleBossResp;
import com.guaji.game.protocol.Const;
import com.guaji.game.protocol.HP;
import com.guaji.game.protocol.Status;
import com.guaji.game.util.ActivityUtil;
import com.guaji.game.util.GsConst;

/**
 * 單人強敵領取協定
 */
public class Activity193Handler implements IProtocolHandler {
	static final int singleBoss_questInfo = 0;
	static final int singleBoss_take_quest_award = 1;
	static final int singleBoss_rankSync = 2;
	@Override
	public boolean onProtocol(AppObj appObj, Protocol protocol) {
		// TODO Auto-generated method stub
		
		Player player = (Player) appObj;
		
		// 检测活动是否开放
		int activityId = Const.ActivityId.ACTIVITY193_SingleBoss_VALUE;
		ActivityTimeCfg timeConfig = ActivityUtil.getCurActivityTimeCfg(activityId);
		if (timeConfig == null || player == null) {
			// 活动已关闭
			player.sendError(protocol.getType(), Status.error.ACTIVITY_CLOSE);
			return true;
		}

		// 解析接受到的数据
		SingleBossReq request = protocol.parseProtocol(SingleBossReq.getDefaultInstance());

		int action = request.getAction();
				
				
		int stageId = timeConfig.getStageId();
		Activity193Status status = ActivityUtil.getActivityStatus(player.getPlayerData(), activityId, stageId, Activity193Status.class);
		
		if (status == null) {
			player.sendError(protocol.getType(), Status.error.DATA_NOT_FOUND_VALUE);
			return true;
		}
		
		ActivityUtil.SingleBossInitItem(player, timeConfig, status);
						
		// 业务分支处理
		switch (action) {
		case singleBoss_questInfo:
		case singleBoss_rankSync:
			SyncInfo(timeConfig,action,player,status);
			break;
		case  singleBoss_take_quest_award:
			onTakeQuestAward(protocol,timeConfig,player,action,status);
			break;
		default:
			player.sendError(protocol.getType(), Status.error.PARAMS_INVALID_VALUE);
			break;
		}
		return true;
	}
	
	protected boolean onTakeQuestAward(Protocol protocol,ActivityTimeCfg timeConfig,Player player,int action,Activity193Status status) {
		SingleBossReq request = protocol.parseProtocol(SingleBossReq.getDefaultInstance());
		
		int id = request.getChoose();
		
		SingleBossQuestCfg questCfg = ConfigManager.getInstance().getConfigByKey(SingleBossQuestCfg.class, id);
		
		if (questCfg == null) {
			player.sendError(protocol.getType(), Status.error.PARAMS_INVALID_VALUE);
			return false;
		}
		
		if (status.getAwardrecord().contains(id)){
			player.sendError(protocol.getType(), Status.error.ALREADY_GIFT_VALUE);
			return false;
		}
		
		int type = questCfg.getType();
		
//		if ((type < GsConst.SingleBossQuestType.PassLv) || (type > GsConst.SingleBossQuestType.ChanllengeTime)) {
//			player.sendError(protocol.getType(), Status.error.PARAMS_INVALID_VALUE);
//			return false;
//		}
		
		if (status.getQuestRecord(type) < questCfg.getTarget()) {
			player.sendError(protocol.getType(), Status.error.PARAMS_INVALID_VALUE);
			return false;
		} 
	
		String award = questCfg.getAwards();

		status.getAwardrecord().add(id);
		player.getPlayerData().updateActivity(timeConfig.getActivityId(),timeConfig.getStageId());
		
		// 奖励
		AwardItems awardItems = AwardItems.valueOf(award);
		awardItems.rewardTakeAffectAndPush(player, Action.Single_Boss_Quest_AWARD, 2);// 记录领取日志
		
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
	private static void SyncInfo(ActivityTimeCfg timeConfig,int action,Player player, Activity193Status status) {
		SingleBossResp.Builder builder = getBuilder(timeConfig,action,player,status);
		player.sendProtocol(Protocol.valueOf(HP.code.ACTIVITY193_SINGLE_BOSS_S, builder));
	}
	
	private static SingleBossResp.Builder getBuilder(ActivityTimeCfg timeConfig,int action,Player player,Activity193Status status) {
		// 返回包
		SingleBossResp.Builder response = SingleBossResp.newBuilder();
		
		response.setAction(action);

		if ((action == singleBoss_questInfo) || (action == singleBoss_take_quest_award)) {
			
			for (SingleBossQuestCfg sbqCfg : ConfigManager.getInstance().getConfigMap(SingleBossQuestCfg.class).values()) {
				SingleBossQuest.Builder questInfo =  SingleBossQuest.newBuilder();
				questInfo.setId(sbqCfg.getId());
				questInfo.setCount(status.getQuestRecord(sbqCfg.getType()));
				if (status.getAwardrecord().contains(sbqCfg.getId())) {
					questInfo.setIsGot(1);
				} else {
					questInfo.setIsGot(0);
				}
				response.addQuestInfo(questInfo);
			}
					
			SingleBossData.Builder BassInfo = SingleBossData.newBuilder();
			BassInfo.setMaxClearStage(status.getNowLv());
			BassInfo.setMaxScore(status.getScore());
			BassInfo.setEndTime(timeConfig.getlEndTime());
			BassInfo.setCount(Math.max(SysBasicCfg.getInstance().getSingleBossChanllengeTime()-status.getCount(),0));
			
			response.setBaseInfo(BassInfo);
		}
		
		if (action == singleBoss_rankSync) {
			
			// 刷新排行数据
			SingleBossRankManager.getInstance().refreshRank();
			
			SingleBossRanking.Builder rankInfo = SingleBossRanking.newBuilder();
			
			SingleBossRankMessage.Builder ownRankItem = SingleBossRankMessage.newBuilder();
			
	        int Rank = SingleBossRankManager.getInstance().getPlayerRank(player.getId());
	        if (Rank != 0) {
		        ownRankItem.setScore(status.getScore());
	        } else {
	        	 ownRankItem.setScore(0);
	        }
	        ownRankItem.setRank(Rank);
	        ownRankItem.setName(player.getName());
	        ownRankItem.setHeadIcon(player.getPlayerData().getPlayerEntity().getHeadIcon());
	        rankInfo.setSelfRankItem(ownRankItem);
	        List<SingleBossRankMessage.Builder> rankItemList = SingleBossRankManager.getInstance().getRankTop(100);
	        for (SingleBossRankMessage.Builder item : rankItemList) {
	        	rankInfo.addOtherRankItem(item);
	        }
	        
	        response.setRankingInfo(rankInfo);
		}
		
		return response;
	}
	
}
