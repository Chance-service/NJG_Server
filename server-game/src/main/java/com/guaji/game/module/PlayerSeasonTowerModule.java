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
import com.guaji.game.config.SeasonTowerCfg;
import com.guaji.game.config.SeasonTowerTimeCfg;
import com.guaji.game.config.SysBasicCfg;
import com.guaji.game.item.AwardItems;
import com.guaji.game.log.BehaviorLogger;
import com.guaji.game.log.BehaviorLogger.Action;
import com.guaji.game.log.BehaviorLogger.Params;
import com.guaji.game.log.BehaviorLogger.Source;
import com.guaji.game.module.activity.activity194.Activity194Status;
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
import com.guaji.game.util.MonsterItem;
import com.guaji.game.util.PlayerUtil;

/**
 * 循環活動戰場
 */
public class PlayerSeasonTowerModule extends PlayerModule {
	/**
	 * 戰鬥場所
	 */
	private Battlefield battlefield;
	
	public PlayerSeasonTowerModule(Player player) {
		super(player);
		//戰場列隊
		listenProto(HP.code.BATTLE_FORMATION_C);
		listenProto(HP.code.SEASON_TOWER_BATTLE_LOG_C);
		//listenProto(HP.code.SINGLE_BOSS_LIST_INFO_C);
		//listenProto(HP.code.CYCLE_ONEKEY_CLEARANCE_C);
	}
	
	@Override
	public boolean onProtocol(Protocol protocol) {
		
		if (protocol.checkType(HP.code.BATTLE_FORMATION_C)) {
			onChallengeSeasonTower(protocol,protocol.parseProtocol(NewBattleFormation.getDefaultInstance()));
			return true;
		
		} else if (protocol.checkType(HP.code.SEASON_TOWER_BATTLE_LOG_C)) {
			SeasonTowerCheckLog(protocol,protocol.parseProtocol(NewBattleLog.getDefaultInstance()));
			return true;
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

	/**
	 *	賽季爬塔活動進入編隊
	 * @param oneprotocol
	 */
	public void onChallengeSeasonTower(Protocol protocol,NewBattleFormation Battleprotocol) {
		if (Battleprotocol.getBattleType() != GsConst.BattleType.SeasonTower){
			return;
		}
		
		// 检测活动是否开放
		int activityId = Const.ActivityId.ACTIVITY194_SeasonTower_VALUE;
		ActivityTimeCfg timeConfig = ActivityUtil.getCurActivityTimeCfg(activityId);
		int timeIdx = SeasonTowerTimeCfg.getValidTimeIdx();
		
		if (timeConfig == null || player == null || timeIdx == -1) {
			// 活动已关闭
			player.sendError(protocol.getType(), Status.error.ACTIVITY_CLOSE);
			return ;
		}
		
		int stageId = timeConfig.getStageId();
		Activity194Status status = ActivityUtil.getActivityStatus(player.getPlayerData(), activityId, stageId, Activity194Status.class);
		
		if (status == null) {
			player.sendError(protocol.getType(), Status.error.DATA_NOT_FOUND_VALUE);
			return ;
		}
		
		ActivityUtil.SeaSonTowerInit(player, timeConfig, status,timeIdx);
		
//		if (status.isChooseFloor()) {
//			// 選擇樓層中不能進行
//			player.sendError(protocol.getType(), Status.error.DATA_NOT_FOUND_VALUE);
//			return ;
//		}
		
		if (Battleprotocol.getType() == 0) { //0.請求敵方編隊資訊
			String mapStr = Battleprotocol.getMapId();
			int chanllengeLv = mapStr.isEmpty()? 1: Integer.valueOf(mapStr);
			int NowLv = status.getNowfloor();
			
			if (chanllengeLv > NowLv) {
				// 只能挑戰小於等於 NowLv
				player.sendError(protocol.getType(), Status.error.PARAMS_INVALID_VALUE);
				return;
			}
			
			SeasonTowerCfg activityCfg = ConfigManager.getInstance().getConfigByKey(SeasonTowerCfg.class, chanllengeLv);
			if (activityCfg == null) {
				// 副本不存在
				player.sendError(protocol.getType(), Status.error.CONFIG_NOT_FOUND_VALUE);
				return;
			}
												
			int battleId = GuaJiTime.getSeconds();
			//currLevel = changeLv;
			Map<Integer,NewBattleRole> monsterRoles = new HashMap<Integer,NewBattleRole>();
			List<MonsterItem> monsterIds =  activityCfg.getBossIDIdxList();
			NewBattleFormation.Builder builder = NewBattleFormation.newBuilder();
			
			int pos = 10; // client log 佔位位置
			for (MonsterItem mItem : monsterIds) {
				RoleInfo.Builder monsterRole = BuilderUtil.TowerMonsterRoleInfoBuilder(mItem.getMonsterId(), mItem.getLevel(), mItem.getRatio());
				CLIcheckInfo.Builder checkInfo = BuilderUtil.getRoleInfoCKInfoBuilder(monsterRole,0);
				if ((monsterRole != null)&&(checkInfo != null)) {
					monsterRoles.put(pos,new NewBattleRole(monsterRole,pos));
					checkInfo.setPosId(pos);
					builder.addResultInfo(checkInfo);
				} else {
					if (mItem.getMonsterId() > 0){
						Log.errPrintln(
								String.format("seasonTower monster config not exist, map: %d, monster: %d", activityCfg.getId(), mItem.getMonsterId()));
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
				SeasonTowerCfg activityCfg = ConfigManager.getInstance().getConfigByKey(SeasonTowerCfg.class, chanllengeLv);
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
	private void SeasonTowerCheckLog(Protocol protocol,NewBattleLog Battleprotocol) {
		//NewBattleLog protocol = oneprotocol.parseProtocol(NewBattleLog.getDefaultInstance());
		int resultid = Battleprotocol.getResault();
		NewBattleLog.Builder builder = NewBattleLog.newBuilder();
		int battleId = Battleprotocol.getBattleId();
		
		// 检测活动是否开放
		int activityId = Const.ActivityId.ACTIVITY194_SeasonTower_VALUE;
		ActivityTimeCfg timeConfig = ActivityUtil.getCurActivityTimeCfg(activityId);
		int timeIdx = SeasonTowerTimeCfg.getValidTimeIdx();
		
		if (timeConfig == null || player == null || timeIdx == -1) {
			// 活动已关闭
			player.sendError(protocol.getType(), Status.error.ACTIVITY_CLOSE);
			return ;
		}
		
		int stageId = timeConfig.getStageId();
		Activity194Status status = ActivityUtil.getActivityStatus(player.getPlayerData(), activityId, stageId, Activity194Status.class);
		
		if (status == null) {
			player.sendError(protocol.getType(), Status.error.DATA_NOT_FOUND_VALUE);
			return ;
		}
		
		if ((this.battlefield == null) || (this.battlefield.getBattleId() != battleId) || (!this.battlefield.getBattleStandby())) {
			sendError(protocol.getType(), Status.error.PARAMS_INVALID);
			return;
		}
		
		if (this.battlefield.getBattleType() != GsConst.BattleType.SeasonTower) {
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
								
								SeasonTowerResult(protocol,timeConfig,status);
								
								Log.debugInfo("SeasonTowerResult()_check");
							}
						} else {
								
							SeasonTowerResult(protocol,timeConfig,status);

							Log.debugInfo("SeasonTowerResult()_No_check");
						}
					}	
//					} else { // 闖關失敗
//						if (Battleprotocol.hasBattleScore()) {
//							long score = Battleprotocol.getBattleScore();
//							SingleBossResult(protocol,timeConfig,status,score,false);
//						}
//						Log.debugInfo("SingleBossResult()_fail");
//					}
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
		sendProtocol(Protocol.valueOf(HP.code.SEASON_TOWER_BATTLE_LOG_S, builder));
	}
	
	/**
	 * 對單人強敵活動挑戰回傳分數
	 */	
	private void SeasonTowerResult(Protocol protocol,ActivityTimeCfg timeConfig,Activity194Status status) {
		if (battlefield.getBattleFinish()) {
			return;
		}
		
		int chanllengeLv = battlefield.getMapId();
		SeasonTowerCfg mapCfg = ConfigManager.getInstance().getConfigByKey(SeasonTowerCfg.class, chanllengeLv);
		
		AwardItems awardItems = new AwardItems();
		
		if((mapCfg == null)||(status == null)) {
			sendError(protocol.getType(), Status.error.PARAMS_INVALID);
			return;
		}
		
		String awardStr = "";
				
		
		
		int NowLv = status.getNowfloor();
		long oldDone = status.getDoneTime();
		
		if (chanllengeLv <= NowLv) {
			for (int floor = 1 ; floor <= chanllengeLv ; floor++) {
				SeasonTowerCfg floorCfg = ConfigManager.getInstance().getConfigByKey(SeasonTowerCfg.class, floor);
				if ((floorCfg!= null)&&(!status.getAwardrecord().contains(floor))){
					status.setAwardrecord(floor); // 設定領獎標記
					status.setDoneTime(GuaJiTime.getMillisecond()); // 領獎就算完成塔樓任務
					if (floor > status.getRank()) {
						status.setRank(floor); // 此rank用記來打過獲得獎勵的最高關卡
					}
					if (awardStr.isEmpty()) {
						awardStr = floorCfg.getReward();
					} else {
						awardStr = awardStr + "," + floorCfg.getReward();
					}
				}
			}
			
			if (!awardStr.isEmpty()) { 
				awardItems = AwardItems.valueOf(awardStr);
				awardItems.rewardTakeAffectAndPush(player, Action.ACTIVITY194_Season_Tower, 1);
			}
			
			if (chanllengeLv == NowLv) {
				SeasonTowerCfg checkCfg = ConfigManager.getInstance().getConfigByKey(SeasonTowerCfg.class, NowLv);
				
				if ((checkCfg != null)&&(checkCfg.getNextstage() != 0)) { // 移動到下一個攻略關卡
					// 有下一關
					status.setNowfloor(checkCfg.getNextstage());
				}
			}
		}
				
		if (oldDone != status.getDoneTime()) {
			// 紀錄任務MaxFloor
			Msg msg = Msg.valueOf(GsConst.MsgType.SEASON_TOWER_ACTIVITY);
			msg.pushParam(status.getDoneFloor());
			msg.pushParam(player);
			msg.pushParam(status.getDoneTime());
			GuaJiXID targetXId = GuaJiXID.valueOf(GsConst.ObjType.MANAGER, GsConst.ObjId.SeasonTower_RANK_MANAGER);
			GsApp.getInstance().postMsg(targetXId, msg);
		}
				
		player.getPlayerData().updateActivity(timeConfig.getActivityId(), timeConfig.getStageId());

		
		BehaviorLogger.log4Service(player, Source.USER_OPERATION, Action.ACTIVITY194_Season_Tower,//Params.valueOf("type", mapCfg.getType()),
				Params.valueOf("chanllengeLv", chanllengeLv),
				Params.valueOf("oldfloor", NowLv),
				Params.valueOf("currfloor", status.getNowfloor()),
				Params.valueOf("reward",awardItems.toString()));
				
		battlefield.setBattleFinish(true);

		player.getPlayerData().syncStateInfo();		
	}
}
