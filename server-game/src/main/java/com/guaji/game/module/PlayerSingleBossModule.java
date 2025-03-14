package com.guaji.game.module;


import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.guaji.config.ConfigManager;
import org.guaji.log.Log;
import org.guaji.msg.Msg;
import org.guaji.net.protocol.Protocol;
import org.guaji.os.GuaJiTime;
import org.guaji.xid.GuaJiXID;

import com.guaji.game.GsApp;
import com.guaji.game.battle.Battlefield;
import com.guaji.game.battle.NewBattleRole;
import com.guaji.game.config.ActivityTimeCfg;
import com.guaji.game.config.SingleBossCfg;
import com.guaji.game.config.SysBasicCfg;
import com.guaji.game.item.AwardItems;
import com.guaji.game.log.BehaviorLogger;
import com.guaji.game.log.BehaviorLogger.Action;
import com.guaji.game.log.BehaviorLogger.Params;
import com.guaji.game.log.BehaviorLogger.Source;
import com.guaji.game.module.activity.activity193.Activity193Status;
import com.guaji.game.player.Player;
import com.guaji.game.player.PlayerModule;
import com.guaji.game.protocol.Battle.NewBattleFormation;
import com.guaji.game.protocol.Battle.NewBattleLog;
import com.guaji.game.protocol.Battle.NewBattleLog.NewBattleLogInfo;
import com.guaji.game.protocol.Const;
import com.guaji.game.protocol.HP;
import com.guaji.game.protocol.Player.CLIcheckInfo;
import com.guaji.game.protocol.Player.RoleInfo;
import com.guaji.game.protocol.Status;
import com.guaji.game.util.ActivityUtil;
import com.guaji.game.util.BuilderUtil;
import com.guaji.game.util.GsConst;
import com.guaji.game.util.PlayerUtil;
import com.guaji.game.util.GsConst.SingleBossQuestType;

/**
 * 循環活動戰場
 */
public class PlayerSingleBossModule extends PlayerModule {
	/**
	 * 戰鬥場所
	 */
	private Battlefield battlefield;
	
	public PlayerSingleBossModule(Player player) {
		super(player);
		//戰場列隊
		listenProto(HP.code.BATTLE_FORMATION_C);
		listenProto(HP.code.SINGLE_BOSS_BATTLE_LOG_C);
		//listenProto(HP.code.SINGLE_BOSS_LIST_INFO_C);
		//listenProto(HP.code.CYCLE_ONEKEY_CLEARANCE_C);
	}
	
	@Override
	public boolean onProtocol(Protocol protocol) {
		
		if (protocol.checkType(HP.code.BATTLE_FORMATION_C)) {
			onChallengeSingleBoss(protocol,protocol.parseProtocol(NewBattleFormation.getDefaultInstance()));
			return true;
		
		} else if (protocol.checkType(HP.code.SINGLE_BOSS_BATTLE_LOG_C)) {
			SingleBossCheckLog(protocol,protocol.parseProtocol(NewBattleLog.getDefaultInstance()));
			return true;
//		} else if (protocol.checkType(HP.code.SINGLE_BOSS_LIST_INFO_C)) {
//			onSingleBossInfo(protocol);
//			return true;
		}
		return super.onProtocol(protocol);
	}
	


	/**
	 * 登录处理
	 */
	@Override
	protected boolean onPlayerLogin() {
		return super.onPlayerLogin();
	}

//	/**
//	 * 同步單人強敵活動
//	 */
//	private void SingleBossInfoSync(ActivityTimeCfg timeConfig,Activity193Status status) {
//		
//		HPSingleBossInfo.Builder ret = HPSingleBossInfo.newBuilder();
//		
//		ret.setChanllengeLv(status.getNowLv());
//		ret.setLeftTime(timeConfig.calcActivitySurplusTime());
//		ret.setCount(Math.max(SysBasicCfg.getInstance().getSingleBossChanllengeTime()-status.getCount(),0));
//		
//		player.sendProtocol(Protocol.valueOf(HP.code.SINGLE_BOSS_LIST_INFO_S, ret));
//	}
	
//	/**
//	 * 掃蕩按鈕
//	 */
//	private void oneKeyClearance(Protocol protocol) {
//		
//		int activityId = Const.ActivityId.ACTIVITY191_CycleStage_VALUE;
//		ActivityTimeCfg timeConfig = ActivityUtil.getCurActivityTimeCfg(activityId);
//		if (timeConfig == null || player == null) {
//			// 活动已关闭
//			if (timeConfig == null) {
//				ActivityUtil.CycleStageClearItem(player);
//			}
//			player.sendError(protocol.getType(), Status.error.ACTIVITY_CLOSE);
//			return ;
//		}
//		
//		HPCycleStageOneKeyRet requst = protocol.parseProtocol(HPCycleStageOneKeyRet.getDefaultInstance());
//		
//		int mapId = requst.getMapId();
//		int count = requst.getCount();
//		
//		if (count <= 0) {
//			player.sendError(protocol.getType(), Status.error.PARAMS_INVALID_VALUE);
//			return;
//		}
//		
//		ActivityStage191Cfg activityCfg = ConfigManager.getInstance().getConfigByKey(ActivityStage191Cfg.class, mapId);
//		
//		if (activityCfg == null) {
//			player.sendError(protocol.getType(), Status.error.CONFIG_ERROR_VALUE);
//			return;
//		}
//		
//		if (activityCfg.getReplay() != 1) {
//			// 非可掃蕩地圖
//			player.sendError(protocol.getType(), Status.error.PARAMS_INVALID_VALUE);
//			return;
//		}
//		
//		int stageId = timeConfig.getStageId();
//		Activity191Status status = ActivityUtil.getActivityStatus(player.getPlayerData(), activityId, stageId, Activity191Status.class);
//		int passId = status.getPassStage();
//		
//		if (mapId > passId) {
//			// 還未挑戰通過該地圖
//			player.sendError(protocol.getType(), Status.error.PARAMS_INVALID_VALUE);
//			return;
//		}
//		
//		ConsumeItems consumeItems = ConsumeItems.valueOf();
//		ItemInfo itemInfo = ItemInfo.valueOf(SysBasicCfg.getInstance().getCycleStageItemCost());
//		itemInfo.setQuantity(count);
//		List<ItemInfo> itemList = new ArrayList<ItemInfo>();
//		itemList.add(itemInfo);
//		boolean isAdd = false ;
//		isAdd = consumeItems.addConsumeInfo(player.getPlayerData(),itemList);
//		
//		if (isAdd && consumeItems.checkConsume(player)) {
//			if(!consumeItems.consumeTakeAffect(player, Action.ACTIVITY191_CycleStage_WIPE)) {
//				player.sendError(protocol.getType(),Status.error.ITEM_NOT_ENOUGH_VALUE); 
//				return; 
//			} 
//		} else {
//			player.sendError(protocol.getType(),Status.error.ITEM_NOT_ENOUGH_VALUE); 
//			return; 
//		}
//		
//		AwardItems awardItems = new AwardItems();
//		for (int i = 1 ; i <= count ; i++) {
//			List<DropItems.Item> dropitems = activityCfg.getItems().calcDrop();
//			for (DropItems.Item item : dropitems) {
//				awardItems.addItem(item);
//			}
//		}
//		
//		awardItems.rewardTakeAffectAndPush(player, Action.ACTIVITY191_CycleStage_WIPE, 1,TapDBSource.Cycle_Stage,
//				Params.valueOf("mapId",mapId),
//				Params.valueOf("count",count)
//				);
//		
//		Msg hawkMsg = Msg.valueOf(GsConst.MsgType.DailyQuestMsg.CYCLE_STAGE_JOINTIMES,
//				GuaJiXID.valueOf(GsConst.ObjType.PLAYER, player.getId()));
//		hawkMsg.pushParam(1);
//		GsApp.getInstance().postMsg(hawkMsg);
//		
//		HPCycleStageOneKeyRes.Builder ret = HPCycleStageOneKeyRes.newBuilder();
//		ret.setReward(awardItems.toString());
//
//		
//		player.sendProtocol(Protocol.valueOf(HP.code.CYCLE_ONEKEY_CLEARANCE_S, ret));
//	}
	
	/**
	 * 循環活動資訊
	 * 
	 * @param protocol
	 */
//	public void onSingleBossInfo(Protocol protocol) {
//		// 检测活动是否开放
//		int activityId = Const.ActivityId.ACTIVITY193_SingleBoss_VALUE;
//		ActivityTimeCfg timeConfig = ActivityUtil.getCurActivityTimeCfg(activityId);
//		if (timeConfig == null || player == null) {
//			// 活动已关闭
//			if (timeConfig == null) {
//				//ActivityUtil.CycleStageClearItem(player);
//			}
//			player.sendError(protocol.getType(), Status.error.ACTIVITY_CLOSE);
//			return ;
//		}
//		
//		int stageId = timeConfig.getStageId();
//		Activity193Status status = ActivityUtil.getActivityStatus(player.getPlayerData(), activityId, stageId, Activity193Status.class);
//		
//		if (status == null) {
//			player.sendError(protocol.getType(), Status.error.DATA_NOT_FOUND_VALUE);
//			return ;
//		}
//		
//		ActivityUtil.SingleBossInitItem(player, timeConfig, status);
//		
//		SingleBossInfoSync(timeConfig,status);
//	}

	/**
	 *	循環活動地下城進入編隊
	 * @param oneprotocol
	 */
	public void onChallengeSingleBoss(Protocol protocol,NewBattleFormation Battleprotocol) {
		if ((Battleprotocol.getBattleType() != GsConst.BattleType.SingleBoss)&&
		(Battleprotocol.getBattleType() != GsConst.BattleType.SingleBoss_Mock)){
			return;
		}
		
		// 检测活动是否开放
		int activityId = Const.ActivityId.ACTIVITY193_SingleBoss_VALUE;
		ActivityTimeCfg timeConfig = ActivityUtil.getCurActivityTimeCfg(activityId);
		if (timeConfig == null || player == null) {
			// 活动已关闭
			player.sendError(protocol.getType(), Status.error.ACTIVITY_CLOSE);
			return ;
		}
		
		int stageId = timeConfig.getStageId();
		Activity193Status status = ActivityUtil.getActivityStatus(player.getPlayerData(), activityId, stageId, Activity193Status.class);
		
		if (status == null) {
			player.sendError(protocol.getType(), Status.error.DATA_NOT_FOUND_VALUE);
			return ;
		}
		
		if (Battleprotocol.getType() == 0) { //0.請求敵方編隊資訊
			String mapStr = Battleprotocol.getMapId();
			int chanllengeLv = mapStr.isEmpty()? 1: Integer.valueOf(mapStr);
			int NowLv = status.getNowLv();
			
			if (chanllengeLv > NowLv) {
				// 只能挑戰小於等於 NowLv
				player.sendError(protocol.getType(), Status.error.PARAMS_INVALID_VALUE);
				return;
			}
			
			SingleBossCfg activityCfg = ConfigManager.getInstance().getConfigByKey(SingleBossCfg.class, chanllengeLv);
			if (activityCfg == null) {
				// 副本不存在
				player.sendError(protocol.getType(), Status.error.CONFIG_NOT_FOUND_VALUE);
				return;
			}
			
			if ((Battleprotocol.getBattleType() == GsConst.BattleType.SingleBoss)) {
				if (status.getCount() >= SysBasicCfg.getInstance().getSingleBossChanllengeTime()) {
					player.sendError(protocol.getType(), Status.error.BATTLE_BOSS_FIGHT_TIME_NOT_ENOUGH);
					return;
				} 
			}
									
			int battleId = GuaJiTime.getSeconds();
			//currLevel = changeLv;
			Map<Integer,NewBattleRole> monsterRoles = new HashMap<Integer,NewBattleRole>();
			List<Integer> monsterIds =  activityCfg.getBossIDIdxList();
			NewBattleFormation.Builder builder = NewBattleFormation.newBuilder();
			
			int pos = 10; // client log 佔位位置
			for (int monsterId : monsterIds) {
				RoleInfo.Builder monsterRole = BuilderUtil.genMonsterRoleInfoBuilder(monsterId, false);
				CLIcheckInfo.Builder checkInfo = BuilderUtil.getRoleInfoCKInfoBuilder(monsterRole,0);
				if ((monsterRole != null)&&(checkInfo != null)) {
					monsterRoles.put(pos,new NewBattleRole(monsterRole,pos));
					checkInfo.setPosId(pos);
					builder.addResultInfo(checkInfo);
				} else {
					if (monsterId > 0){
						Log.errPrintln(
								String.format("singleBoss monster config not exist, map: %d, monster: %d", activityCfg.getId(), monsterId));
					}
				}
				pos++;
			}
			// 开启monster战场
			if (monsterRoles.size() > 0) {
				this.battlefield = new Battlefield();
				this.battlefield.setBattleId(battleId);
				this.battlefield.setm_Defenders(monsterRoles);
				this.battlefield.setBattleType(Battleprotocol.getBattleType());
				this.battlefield.setMapId(chanllengeLv);
				//..................for client.................
				builder.setType(Battleprotocol.getType());
				builder.setBattleId(battleId);
				builder.setBattleType(Battleprotocol.getBattleType());
				builder.setMapId(Battleprotocol.getMapId());
				sendProtocol(Protocol.valueOf(HP.code.BATTLE_FORMATION_S_VALUE, builder));
			} else {
				sendError(protocol.getType(), Status.error.PARAMS_INVALID);
			}

		} else if (Battleprotocol.getType() == 1) { //1.傳送我方編隊資訊
			int checkid = Battleprotocol.getBattleId();
			String mapStr = Battleprotocol.getMapId();
			int chanllengeLv = mapStr.isEmpty()? 0: Integer.valueOf(mapStr);
			if ((this.battlefield == null) ||(checkid != this.battlefield.getBattleId()) ||(chanllengeLv != this.battlefield.getMapId()) ){
				sendError(HP.code.BATTLE_FORMATION_S_VALUE, Status.error.PARAMS_INVALID);
				return;
			}
			List<String> role_posList = Battleprotocol.getRolePosList();
			Map<Integer,Integer> rolePosMap = new HashMap<>();
			
			// 有傳入攜帶英雄
			if (!PlayerUtil.checkRolePos(player,rolePosMap,role_posList)) {
				return;
			}
			
			PlayerUtil.transFormation(player,rolePosMap,GsConst.FormationType.FormationBegin);
			
			NewBattleFormation.Builder builder = NewBattleFormation.newBuilder();
			Map<Integer,NewBattleRole> Heroes = player.getPlayerData().getNewBattleHeroes(rolePosMap);
			for ( Integer posid :Heroes.keySet()) {
				CLIcheckInfo.Builder checkInfo = BuilderUtil.getRoleInfoCKInfoBuilder(Heroes.get(posid).getRoleInfo(),0);
				checkInfo.setPosId(posid);
				builder.addResultInfo(checkInfo);
			}
			if ((Heroes.size() > 0)&&(this.battlefield.getm_Defenders() != null)&&(this.battlefield.getm_Defenders().size() > 0)) {
				this.battlefield.setm_Attackers(Heroes);
				this.battlefield.setBattleStandby(true);
				//.....................for client....................
				builder.setType(Battleprotocol.getType());
				builder.setBattleId(checkid);
				builder.setBattleType(Battleprotocol.getBattleType());
				builder.setMapId(Battleprotocol.getMapId());
				//StateEntity stateEntity = player.getPlayerData().getStateEntity();
				SingleBossCfg activityCfg = ConfigManager.getInstance().getConfigByKey(SingleBossCfg.class, chanllengeLv);
				if (activityCfg == null) {
					// 副本不存在
					player.sendError(protocol.getType(), Status.error.MULTIELITE_CFG_NULL);
					return;
				}
			    sendProtocol(Protocol.valueOf(HP.code.BATTLE_FORMATION_S_VALUE, builder));
			} else {
				sendError(protocol.getType(), Status.error.PARAMS_INVALID);
			}
		}
	}
	
	/**
	 * 發送戰鬥紀錄給server檢查(多人副本)
	 */
	private void SingleBossCheckLog(Protocol protocol,NewBattleLog Battleprotocol) {
		//NewBattleLog protocol = oneprotocol.parseProtocol(NewBattleLog.getDefaultInstance());
		int resultid = Battleprotocol.getResault();
		NewBattleLog.Builder builder = NewBattleLog.newBuilder();
		int battleId = Battleprotocol.getBattleId();
		
		// 检测活动是否开放
		int activityId = Const.ActivityId.ACTIVITY193_SingleBoss_VALUE;
		ActivityTimeCfg timeConfig = ActivityUtil.getCurActivityTimeCfg(activityId);
		if (timeConfig == null || player == null) {
			// 活动已关闭
			player.sendError(protocol.getType(), Status.error.ACTIVITY_CLOSE);
			return ;
		}
		
		int stageId = timeConfig.getStageId();
		Activity193Status status = ActivityUtil.getActivityStatus(player.getPlayerData(), activityId, stageId, Activity193Status.class);
		
		if (status == null) {
			player.sendError(protocol.getType(), Status.error.DATA_NOT_FOUND_VALUE);
			return ;
		}
		
		if ((this.battlefield == null) || (this.battlefield.getBattleId() != battleId) || (!this.battlefield.getBattleStandby())) {
			sendError(protocol.getType(), Status.error.PARAMS_INVALID);
			return;
		}
		
		if ((this.battlefield.getBattleType() != GsConst.BattleType.SingleBoss)&&
				(this.battlefield.getBattleType() != GsConst.BattleType.SingleBoss_Mock)){
			sendError(protocol.getType(), Status.error.PARAMS_INVALID);
			return;
		}
		
		int total = Battleprotocol.getTotleLogId(); //有幾份log
		
		if (total <= 0) {
			sendError(protocol.getType(), Status.error.PARAMS_INVALID);
			return;
		}
		
		int Number =  Battleprotocol.getLogId(); // 第幾份Id
		
		if (Number <= 0) {
			sendError(protocol.getType(), Status.error.PARAMS_INVALID);
			return;
		}

		if (Number == 1) { //送一份紀錄後續有幾份log
			this.battlefield.settotalLogId(total);
			this.battlefield.setclientRet(resultid); // 紀錄client闖關結果 0.成功 1.失敗,只會在第一份Log傳過來
		}
		
		int nowid = 0;
		int checkret = 0;
		int clientRet = this.battlefield.getclientRet();
		if ((clientRet == 0) || (clientRet == 1)) {  // 闖關結果 0.成功  1.失敗
			List <NewBattleLogInfo> battlelogList = Battleprotocol.getLogList();
			
			for(NewBattleLogInfo logInfo : battlelogList) {
				nowid = logInfo.getId();
				if (SysBasicCfg.getInstance().IsBattleCheck()) {
					checkret = this.battlefield.checkBattlelog(logInfo.getRoleInfo(),logInfo.getTargetRoleInfoList(),nowid,logInfo.getMarkTime());
					if (checkret != 0) {
						resultid = 2;  //回傳檢查異常
						Log.debugInfo("MultiBattle error = battleId: {},total: {}, Number: {},Handle id: {},checkcode:{},MarkTime:{}",battleId,total,Number,nowid,checkret,logInfo.getMarkTime());
						break;
					}
				}
				
			}
			if (checkret == 0) {
				if (Number == battlefield.gettotalLogId()) {
					if ((clientRet == 0)) {
						if (SysBasicCfg.getInstance().IsBattleCheck()) {
							if (battlefield.checkDefenderSacrifice()) {
								if (Battleprotocol.hasBattleScore()) {
									long score = Battleprotocol.getBattleScore();
									SingleBossResult(protocol,timeConfig,status,score,true);
								}
								Log.debugInfo("SingleBossResult()_check");
							}
						} else {
							if (Battleprotocol.hasBattleScore()) {
								long score = Battleprotocol.getBattleScore();
								SingleBossResult(protocol,timeConfig,status,score,true);
							}
							Log.debugInfo("SingleBossResult()_No_check");
						}
						
					} else { // 闖關失敗
						if (Battleprotocol.hasBattleScore()) {
							long score = Battleprotocol.getBattleScore();
							SingleBossResult(protocol,timeConfig,status,score,false);
						}
						Log.debugInfo("SingleBossResult()_fail");
					}
					resultid = clientRet; // 確認client給資訊是正確的,塞回闖關結果
				} else if (Number < battlefield.gettotalLogId()) {
					resultid = 3;  //要下一筆
				}
			}
			//Log.debugInfo("battleId: {},total: {}, Number: {},clientRet:{},resultid:{},HandleId:{},checkcode:{}",battleId,total,Number,clientRet,resultid,nowid,checkret);
		} else {
			
		}
		builder.setResault(resultid);
		builder.setTotleLogId(total);
		builder.setLogId(Number);
		builder.setErrorCode(checkret);
		sendProtocol(Protocol.valueOf(HP.code.SINGLE_BOSS_BATTLE_LOG_S, builder));
	}
	
	/**
	 * 對單人強敵活動挑戰回傳分數
	 */	
	private void SingleBossResult(Protocol protocol,ActivityTimeCfg timeConfig,Activity193Status status,long score,boolean iswin) {
		if (battlefield.getBattleFinish()) {
			return;
		}
		
		int chanllengeLv = battlefield.getMapId();
		SingleBossCfg mapCfg = ConfigManager.getInstance().getConfigByKey(SingleBossCfg.class, chanllengeLv); ;
		
		AwardItems awardItems = new AwardItems();
		
		if((mapCfg == null)||(status == null)) {
			sendError(protocol.getType(), Status.error.PARAMS_INVALID);
			return;
		}
		
		int rewardIndex = mapCfg.getBloodList().size()-1;
		int stagePoint = 0;
		for (int i = 0 ; i < mapCfg.getBloodList().size()-1;i++) {
			stagePoint = mapCfg.getBloodList().get(i);
			if (score <= stagePoint) {
				rewardIndex = i;
				break;
			}
		}
		
		String awardStr = mapCfg.getRewardList().get(rewardIndex);
		
		awardItems = AwardItems.valueOf(awardStr);
		
		int NowLv = status.getNowLv();
		
		if (iswin) { // pass lv handle
			if (chanllengeLv == NowLv) {
				// 紀錄任務PassLv
				status.setQuestRecord(SingleBossQuestType.PassLv,NowLv);
				chanllengeLv++;
			}
			
			SingleBossCfg checkCfg = ConfigManager.getInstance().getConfigByKey(SingleBossCfg.class, chanllengeLv);
			
			if (checkCfg != null) {
				if (chanllengeLv > NowLv) {
					status.setNowLv(chanllengeLv);
				}
			} else {
				status.setNowLv(999);
			}
		}
		
		if (score > status.getScore()) {
			// 紀錄任務MaxScore
			status.setScore(score);
			Msg msg = Msg.valueOf(GsConst.MsgType.SINGLE_BOSS_ACTIVITY);
			msg.pushParam(score);
			msg.pushParam(player);
			GuaJiXID targetXId = GuaJiXID.valueOf(GsConst.ObjType.MANAGER, GsConst.ObjId.SingleBoss_RANK_MANAGER);
			GsApp.getInstance().postMsg(targetXId, msg);
		}
		
		// 增加任務totalScore
		status.addTotalScore(score);
		
		status.addCount();
		
		// 增加任務挑戰次數
		status.addChanllengeTime();
		
		player.getPlayerData().updateActivity(timeConfig.getActivityId(), timeConfig.getStageId());

		
		awardItems.rewardTakeAffectAndPush(player, Action.ACTIVITY193_Single_Boss, 1);
		
		BehaviorLogger.log4Service(player, Source.USER_OPERATION, Action.SINGLE_BOSS_SCORE,//Params.valueOf("type", mapCfg.getType()),
				Params.valueOf("chanllengeLv", chanllengeLv),
				Params.valueOf("NowLv",status.getNowLv()),
				Params.valueOf("score",score),
				Params.valueOf("Maxscore",status.getScore()),
				Params.valueOf("reward",awardItems.toString()));
				
		battlefield.setBattleFinish(true);

		player.getPlayerData().syncStateInfo();		
	}
}
