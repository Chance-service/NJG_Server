package com.guaji.game.module;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.guaji.annotation.ProtocolHandlerAnno;

import org.guaji.log.Log;
import org.guaji.net.protocol.Protocol;
import org.guaji.os.GuaJiTime;

import com.guaji.game.battle.Battlefield;
import com.guaji.game.battle.NewBattleRole;
import com.guaji.game.config.FunctionUnlockCfg;
import com.guaji.game.config.SysBasicCfg;

import com.guaji.game.config.WorldBossCfg;

import com.guaji.game.entity.PlayerWorldBossEntity;
import com.guaji.game.entity.RoleEntity;
import com.guaji.game.entity.StateEntity;

import com.guaji.game.log.BehaviorLogger;
import com.guaji.game.log.BehaviorLogger.Action;
import com.guaji.game.log.BehaviorLogger.Params;
import com.guaji.game.log.BehaviorLogger.Source;

import com.guaji.game.manager.WorldBossInfo;
import com.guaji.game.manager.WorldBossManager;
import com.guaji.game.player.Player;
import com.guaji.game.player.PlayerModule;
import com.guaji.game.util.BuilderUtil;
import com.guaji.game.util.GsConst;
import com.guaji.game.util.PlayerUtil;
import com.guaji.game.protocol.Player.CLIcheckInfo;
import com.guaji.game.protocol.Player.RoleInfo;
import com.guaji.game.protocol.Const;
import com.guaji.game.protocol.HP;
import com.guaji.game.protocol.Status;
import com.guaji.game.protocol.Battle.NewBattleFormation;
import com.guaji.game.protocol.Battle.NewBattleLog;
import com.guaji.game.protocol.Battle.NewBattleLog.NewBattleLogInfo;
import com.guaji.game.protocol.WorldBoss.BossState;
import com.guaji.game.protocol.WorldBoss.HPBossStatePush;


/**
 * 世界boss
 */
public class PlayerWorldBossModule extends PlayerModule {
	
	/**
	 * 戰鬥場所
	 */
	private Battlefield battlefield;

	public PlayerWorldBossModule(Player player) {
		super(player);
		battlefield = null;
		//WoroldBoss戰專用
		listenProto(HP.code.BATTLE_FORMATION_C);
		listenProto(HP.code.WORLD_BOSS_BATTLE_LOG_C_VALUE);
	}

	@Override
	protected boolean onPlayerAssemble() {
		// 等级限制（12级）
		if (!FunctionUnlockCfg.checkUnlock(player,null,Const.FunctionType.worldBoss_Unlock)){
			return true;
		}
		
		// 判断有没有离线Boss计算奖励
		WorldBossInfo worldBossInfo = WorldBossManager.getInstance().getCurBossInfo();
		if (worldBossInfo != null) {
			HPBossStatePush.Builder stateBuilder = HPBossStatePush.newBuilder();
			if (worldBossInfo.isEnd() || worldBossInfo.isBossDead()) {
				stateBuilder.setState(BossState.LAST_RESULT_SHOW_VALUE);
			} else {
				stateBuilder.setState(BossState.SHOWING_VALUE);
				int bossId = worldBossInfo.getWorldBossEntity().getBossNpcId();
				if (bossId > 0) {
					stateBuilder.setActivityBossId(bossId);
				}
			}

			stateBuilder.setStartTime(worldBossInfo.getStartDate().getTime());
			sendProtocol(Protocol.valueOf(HP.code.WORLD_BOSS_STATE_PUSH, stateBuilder));
		} else {
			HPBossStatePush.Builder stateBuilder = HPBossStatePush.newBuilder();
			stateBuilder.setState(BossState.LAST_RESULT_SHOW_VALUE);
			sendProtocol(Protocol.valueOf(HP.code.WORLD_BOSS_STATE_PUSH, stateBuilder));
		}
		return true;
	}
	
	
	@Override
	public boolean onProtocol(Protocol protocol) {
		if (protocol.checkType(HP.code.BATTLE_FORMATION_C)) {
			onChallengeWorldBoss(protocol.parseProtocol(NewBattleFormation.getDefaultInstance()));
			return true;
		} else if (protocol.checkType(HP.code.WORLD_BOSS_BATTLE_LOG_C_VALUE)) {
			onChallengeLog(protocol.parseProtocol(NewBattleLog.getDefaultInstance()));
			return true;
		}
		return super.onProtocol(protocol);
	}
	/**
	 * 列陣挑戰WorldBoss
	 * @param protocol
	 */
	private void onChallengeWorldBoss(NewBattleFormation protocol) {
		if ((protocol.getBattleType() != GsConst.BattleType.Boss)) {
			return;
		}
			
		if (!FunctionUnlockCfg.checkUnlock(player,null,Const.FunctionType.worldBoss_Unlock)){
			sendError(protocol.getType(), Status.error.CONDITION_NOT_ENOUGH);
			return;
		}
		
		WorldBossInfo worldBossInfo = WorldBossManager.getInstance().getCurBossInfo();
		if (worldBossInfo == null || worldBossInfo.isEmpty() || worldBossInfo.isEnd()) {
			sendError(protocol.getType(), Status.error.BOSS_NOT_OPEN);
			return;
		}
		
		// boss死亡
		if (worldBossInfo.isBossDead()) {
			sendError(protocol.getType(), Status.error.BOSS_ALREADY_DEAD);
			return;
		}
		
		StateEntity stateEntity = player.getPlayerData().getStateEntity();
		if (stateEntity == null) {
			sendError(protocol.getType(), Status.error.DATA_NOT_FOUND_VALUE);
			return;
		}
		
		if (stateEntity.getWorldBossBuffFreeTimes() >= WorldBossCfg.getInstance().getWorldBossFreeTimes()) {
			sendError(HP.code.WORLD_BOSS_BATTLE_LOG_S_VALUE, Status.error.BOSS_OPEN_TIMES);
			return;
		}
		
		if (stateEntity != null) {
			if (protocol.getType() == 0) { //0.請求敵方編隊資訊
				int battleId = GuaJiTime.getSeconds();
				Map<Integer,NewBattleRole> monsterRoles = new HashMap<Integer,NewBattleRole>();
				NewBattleFormation.Builder builder = NewBattleFormation.newBuilder();
				
				int monsterId = worldBossInfo.getWorldBossEntity().getBossNpcId();
								
				int pos = 14; //boss client log 佔位位置
				
				int initHp = (int)worldBossInfo.getWorldBossEntity().getCurrBossHp();

				RoleInfo.Builder monsterRole = BuilderUtil.genMonsterRoleInfoBuilder(monsterId, true);
				CLIcheckInfo.Builder checkInfo = BuilderUtil.getRoleInfoCKInfoBuilder(monsterRole,initHp);
				if ((monsterRole != null)&&(checkInfo != null)) {
					monsterRoles.put(pos,new NewBattleRole(monsterRole,pos,initHp));
					checkInfo.setPosId(pos);
					builder.addResultInfo(checkInfo);
				} else {
					if (monsterId > 0){
						Log.errPrintln(
								String.format("Boss monster config not exist, monster: %d",monsterId));
					}
				}
				
				// 開啟WorldBoss戰場
				if (monsterRoles.size() > 0) {
					this.battlefield = new Battlefield();
					this.battlefield.setMapId(0);
					this.battlefield.setBattleId(battleId);
					this.battlefield.setm_Defenders(monsterRoles);
					this.battlefield.setBattleType(protocol.getBattleType());
					//..................for client.................
					builder.setType(protocol.getType());
					builder.setBattleId(battleId);
					builder.setBattleType(protocol.getBattleType());
					sendProtocol(Protocol.valueOf(HP.code.BATTLE_FORMATION_S_VALUE, builder));
				} else {
					sendError(HP.code.BATTLE_FORMATION_S_VALUE, Status.error.PARAMS_INVALID);
				}
			} else if (protocol.getType() == 1) { //1.傳送我方編隊資訊
				int checkid = protocol.getBattleId();
				//String mapStr = protocol.getMapId();
				//int mapId = mapStr.isEmpty()? 0: Integer.valueOf(mapStr);
				if ((this.battlefield == null) ||(checkid != this.battlefield.getBattleId())){
					sendError(HP.code.BATTLE_FORMATION_S_VALUE, Status.error.PARAMS_INVALID);
					return;
				}
				List<String> role_posList = protocol.getRolePosList();
				Map<Integer,Integer> rolePosMap = new HashMap<>();
				
				// 有傳入攜帶英雄
				if (!PlayerUtil.checkRolePos(player,rolePosMap,role_posList)) {
					return;
				}
				
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
					builder.setType(protocol.getType());
					builder.setBattleId(checkid);
					builder.setBattleType(protocol.getBattleType());
				    sendProtocol(Protocol.valueOf(HP.code.BATTLE_FORMATION_S_VALUE, builder));
				} else {
					sendError(HP.code.BATTLE_FORMATION_S_VALUE, Status.error.PARAMS_INVALID);
				}				
			}
		}
	}
	
	/**
	 * WorldBoss發送戰鬥紀錄給server檢查
	 */
	private void onChallengeLog(NewBattleLog protocol) {
				
		if (!FunctionUnlockCfg.checkUnlock(player,null,Const.FunctionType.worldBoss_Unlock)){
			sendError(HP.code.FETCH_WORLD_BOSS_BANNER_C_VALUE, Status.error.CONDITION_NOT_ENOUGH);
			return;
		}
		
		StateEntity stateEntity = player.getPlayerData().getStateEntity();
		if (stateEntity == null) {
			sendError(HP.code.FETCH_WORLD_BOSS_BANNER_C_VALUE, Status.error.DATA_NOT_FOUND_VALUE);
			return;
		}
		
		WorldBossInfo worldBossInfo = WorldBossManager.getInstance().getCurBossInfo();
		if (worldBossInfo == null || worldBossInfo.isEmpty() || worldBossInfo.isEnd()) {
			sendError(HP.code.FETCH_WORLD_BOSS_BANNER_C_VALUE, Status.error.BOSS_NOT_OPEN);
			return;
		}
		
		// boss死亡
		if (worldBossInfo.isBossDead()) {
			sendError(HP.code.FETCH_WORLD_BOSS_BANNER_C_VALUE, Status.error.BOSS_ALREADY_DEAD);
			return;
		}
		
		int resultid = protocol.getResault();
		NewBattleLog.Builder builder = NewBattleLog.newBuilder();
		int battleId = protocol.getBattleId();
		int Number =  protocol.getLogId(); // 第幾份Id
		
//		if (Number == 1) {
//			if (!this.battlefield.IsRetest()) {
//				BattleDebugManager.getInstance().clearlog(player.getId());
//			}
//			BattleDebugManager.getInstance().RetestBattlefield(player,this.battlefield);
//		}
		
		if ((this.battlefield == null) || (this.battlefield.getBattleId() != battleId) || (!this.battlefield.getBattleStandby())) {
			sendError(HP.code.WORLD_BOSS_BATTLE_LOG_S_VALUE, Status.error.PARAMS_INVALID);
			return;
		}
		
		if ((this.battlefield.getBattleType() != GsConst.BattleType.Boss)) {
			sendError(HP.code.WORLD_BOSS_BATTLE_LOG_S_VALUE, Status.error.PARAMS_INVALID);
			return;
		}
		
		int total = protocol.getTotleLogId(); //有幾份log
		
		if (total <= 0) {
			sendError(HP.code.WORLD_BOSS_BATTLE_LOG_S_VALUE, Status.error.PARAMS_INVALID);
			return;
		}
		
		if (Number <= 0) {
			sendError(HP.code.WORLD_BOSS_BATTLE_LOG_S_VALUE, Status.error.PARAMS_INVALID);
			return;
		}

		if (Number == 1) { //送一份紀錄後續有幾份log
			this.battlefield.settotalLogId(total);
			this.battlefield.setclientRet(resultid); // 紀錄client闖關結果 0.成功 1.失敗,只會在第一份Log傳過來
		}
		
		int nowid = 0;
		int checkret = 0;
		int clientRet = this.battlefield.getclientRet();
		//BattleDebugManager.getInstance().addProto(player.getId(), protocol);
		if ((clientRet == 0) || (clientRet == 1)) {  // 闖關結果 0.成功  1.失敗
			List <NewBattleLogInfo> battlelogList = protocol.getLogList();
			
			for(NewBattleLogInfo logInfo : battlelogList) {
				nowid = logInfo.getId();
				if (SysBasicCfg.getInstance().IsBattleCheck()) {
					checkret = this.battlefield.checkBattlelog(logInfo.getRoleInfo(),logInfo.getTargetRoleInfoList(),nowid,logInfo.getMarkTime());
					if (checkret != 0) {
						resultid = 2;  //回傳檢查異常
						this.battlefield.setRetest(true);
						Log.debugInfo("Battle error = battleId: {},total: {}, Number: {},Handle id: {},checkcode:{},MarkTime:{}",battleId,total,Number,nowid,checkret,logInfo.getMarkTime());
						break;
					}
				} else {
					checkret = this.battlefield.checkBosslog(logInfo.getRoleInfo(),logInfo.getTargetRoleInfoList(),nowid,logInfo.getMarkTime());
					if (checkret != 0) {
						resultid = 2;  //回傳檢查異常
						//this.battlefield.setRetest(true);
						Log.debugInfo("Battle error = battleId: {},total: {}, Number: {},Handle id: {},checkcode:{},MarkTime:{}",battleId,total,Number,nowid,checkret,logInfo.getMarkTime());
						break;
					} 
				}
				
			}
			if (checkret == 0) {
				if (Number == battlefield.gettotalLogId()) {
					PlayerWorldBossEntity playerWorldBossEntity = worldBossInfo.getPlayerWorldBoss(player.getId());
					if (playerWorldBossEntity == null) {
						playerWorldBossEntity = worldBossInfo.addPlayer(player.getId());
					}

					if (playerWorldBossEntity == null) {
						sendError(HP.code.WORLD_BOSS_BATTLE_LOG_S_VALUE, Status.error.DATA_NOT_FOUND_VALUE);
						return;
					}
					
					if (playerWorldBossEntity.getAttack()) {
						sendError(HP.code.WORLD_BOSS_BATTLE_LOG_S_VALUE, Status.error.DATA_NOT_FOUND_VALUE);
						Log.debugInfo("WorldBoss is attacking");
						return;
					}
					
					int BossHurt = 0;
					boolean dead = battlefield.checkDefenderSacrifice();
					
					if (dead) { // Boss死亡
						BossHurt = battlefield.getBossInitHP();
						Log.debugInfo("Boss dead BossHurt: {}",BossHurt);
					} else {
						BossHurt = battlefield.getWorldBossHurt();
						Log.debugInfo("Boss survive BossHurt: {}",BossHurt);
					}
					
					playerWorldBossEntity.setHurt(BossHurt);
															
					playerWorldBossEntity.notifyUpdate();
										
					resultid = clientRet; // 確認client給資訊是正確的,塞回闖關結果
					
					stateEntity.setWorldBossBuffFreeTimes(stateEntity.getWorldBossBuffFreeTimes() + 1);
					
					WorldBossCfg.getInstance().getActionAwardsInfo().rewardTakeAffectAndPush(player,
							Action.WORLD_BOSS_ACTION, 1);
					// 记录LOG
					BehaviorLogger.log4Service(player, Source.USER_OPERATION, Action.WORLD_BOSS_ACTION,
							Params.valueOf("playerId", player.getId()),
							Params.valueOf("dead", dead),
							Params.valueOf("BossHurt", BossHurt));
				} else if (Number < battlefield.gettotalLogId()) {
					resultid = 3;  //要下一筆
				}
			}

			//Log.debugInfo("battleId: {},total: {}, Number: {},clientRet:{},resultid:{},HandleId:{},checkcode:{}",battleId,total,Number,clientRet,resultid,nowid,checkret);
		}
		builder.setResault(resultid);
		builder.setTotleLogId(total);
		builder.setLogId(Number);
		builder.setErrorCode(checkret);
		sendProtocol(Protocol.valueOf(HP.code.WORLD_BOSS_BATTLE_LOG_S, builder));
	}
	

	/**
	 * 查询banner信息
	 * 
	 * @param protocol
	 */
//	@ProtocolHandlerAnno(code = HP.code.FETCH_WORLD_BOSS_BANNER_C_VALUE)

	/**
	 * 请求世界boss详细信息
	 * 
	 * @param protocol
	 */
	@ProtocolHandlerAnno(code = HP.code.FETCH_WORLD_BOSS_INFO_C_VALUE)
	private void onWorldBossDetailInfo(Protocol protocol) {
		// 等级限制（12级）
		if (!FunctionUnlockCfg.checkUnlock(player,null,Const.FunctionType.worldBoss_Unlock)){
			sendError(protocol.getType(), Status.error.CONDITION_NOT_ENOUGH);
			return;
		}
		
		StateEntity stateEntity = player.getPlayerData().getStateEntity();
		if (stateEntity == null) {
			sendError(HP.code.FETCH_WORLD_BOSS_BANNER_C_VALUE, Status.error.DATA_NOT_FOUND_VALUE);
			return;
		}

		// 同步状态
		WorldBossManager.getInstance().syncBossInfo(this.player);
	}

	/**
	 * 设置世界boss自动加入
	 * 
	 * @param protocol
	 */
//	@ProtocolHandlerAnno(code = HP.code.WORLD_BOSS_AUTO_JOIN_C_VALUE)

	/**
	 * 请求世界boss重生
	 * 
	 * @param protocol
	 */
//	@ProtocolHandlerAnno(code = HP.code.WORLD_BOSS_REBIRTH_C_VALUE)

	/**
	 * 请求排行榜信息
	 * 
	 * @param protocol
	 */
//	@ProtocolHandlerAnno(code = HP.code.WORLD_BOSS_RANK_C_VALUE)

	/**
	 * 随机世界bossBuff
	 * 
	 * @param protocol
	 */
//	@ProtocolHandlerAnno(code = HP.code.WORLD_BOSS_RANDOM_C_VALUE)


	/**
	 * 确定随机buff
	 * 
	 * @param protocol
	 */
//	@ProtocolHandlerAnno(code = HP.code.WORLD_BOSS_CONFIRM_BUFF_C_VALUE)


	/**
	 * 查看buff信息
	 * 
	 * @param protocol
	 */
//	@ProtocolHandlerAnno(code = HP.code.WORLD_BOSS_SEARCH_BUFF_C_VALUE)

	/**
	 * 升级世界bossBuff
	 * 
	 * @param protocol
	 */
//	@ProtocolHandlerAnno(code = HP.code.WORLD_BOSS_UPGRADE_C_VALUE)
	/**
	 * 升级世界bossBuff
	 * 
	 * @param protocol
	 */
//	@ProtocolHandlerAnno(code = HP.code.WORLD_BOSS_ATTACK_SINGLE_C_VALUE)


}
