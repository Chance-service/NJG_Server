package com.guaji.game.module;

import java.util.ArrayList;

import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.guaji.config.ConfigManager;
import org.guaji.log.Log;
import org.guaji.msg.Msg;
import org.guaji.net.protocol.Protocol;
import org.guaji.os.GuaJiTime;
import org.guaji.xid.GuaJiXID;


import com.guaji.game.protocol.Arena.ArenaItemInfo;

import com.guaji.game.protocol.Arena.HPArenaDefenderListSyncS;
import com.guaji.game.protocol.Arena.HPArenaRankingList;
import com.guaji.game.protocol.Arena.HPArenaRankingListRet;

import com.guaji.game.protocol.Arena.HPBuyChallengeTimes;
import com.guaji.game.protocol.Arena.HPBuyChallengeTimesRet;

import com.guaji.game.protocol.Arena.HPChallengeDefenderRet;

import com.guaji.game.protocol.Arena.HPReplaceDefenderList;
import com.guaji.game.protocol.Arena.HPReplaceDefenderListRet;

import com.guaji.game.protocol.Battle.NewBattleFormation;
import com.guaji.game.protocol.Battle.NewBattleLog;
import com.guaji.game.protocol.Battle.NewBattleLog.NewBattleLogInfo;
import com.guaji.game.protocol.Const.QuestEventType;
import com.guaji.game.protocol.Const.SevenDayEventType;
import com.guaji.game.protocol.Const.changeType;
import com.guaji.game.protocol.Player.CLIcheckInfo;

import com.guaji.game.protocol.Player.RoleInfo;
import com.guaji.game.protocol.Player.RoleInfo.Builder;
import com.guaji.game.GsApp;

import com.guaji.game.battle.Battlefield;
import com.guaji.game.battle.NewBattleRole;
import com.guaji.game.config.ArenaBuyTimesCfg;
import com.guaji.game.config.CheatCheckCfg;
import com.guaji.game.config.FunctionUnlockCfg;
import com.guaji.game.config.RankNpcCfg;
import com.guaji.game.config.SysBasicCfg;
import com.guaji.game.entity.ArenaEntity;
import com.guaji.game.entity.FormationEntity;
import com.guaji.game.entity.StateEntity;
import com.guaji.game.item.AwardItems;
import com.guaji.game.item.ConsumeItems;
import com.guaji.game.log.BehaviorLogger;
import com.guaji.game.log.BehaviorLogger.Action;
import com.guaji.game.log.BehaviorLogger.Params;
import com.guaji.game.log.BehaviorLogger.Source;
import com.guaji.game.manager.AllianceManager;
import com.guaji.game.manager.ArenaManager;
import com.guaji.game.manager.SnapShotManager;
import com.guaji.game.manager.TapDBManager;
import com.guaji.game.module.quest.QuestEventBus;
import com.guaji.game.module.sevendaylogin.SevenDayQuestEventBus;
import com.guaji.game.player.Player;
import com.guaji.game.player.PlayerData;
import com.guaji.game.player.PlayerModule;
import com.guaji.game.util.ArenaUtil;
import com.guaji.game.util.BuilderUtil;
import com.guaji.game.util.GsConst;
import com.guaji.game.util.PlayerUtil;
import com.guaji.game.util.ProtoUtil;
import com.guaji.game.util.TapDBUtil;
import com.guaji.game.util.TapDBUtil.TapDBSource;

import net.sf.json.JSONObject;

import com.guaji.game.protocol.Const;
import com.guaji.game.protocol.HP;
import com.guaji.game.protocol.Snapshot.PlayerSnapshotInfo;
import com.guaji.game.protocol.Status;

/**
 * 竞技场模块
 */
public class PlayerArenaModule extends PlayerModule {
	/**
	 * 戰鬥場所
	 */
	private Battlefield battlefield;
	/**
	 * 当前玩家的可挑战排名列表
	 */
	private List<Integer> curChallengeRankList = null;

	/**
	 * 延迟消息队列
	 */
	private List<Msg> delayMsgs;

	public PlayerArenaModule(Player player) {
		super(player);
		curChallengeRankList = new ArrayList<Integer>();
		delayMsgs = new LinkedList<Msg>();

		// 注册监听协议
		listenProto(HP.code.ARENA_DEFENDER_LIST_C);
		listenProto(HP.code.REPLACE_DEFENDER_LIST_C);
		listenProto(HP.code.BUY_CHALLENGE_TIMES_C);
		listenProto(HP.code.ARENA_RANKING_LIST_C);
		listenProto(HP.code.BATTLE_FORMATION_C);
		listenProto(HP.code.PVP_BATTLE_LOG_C);
		//listenProto(HP.code.CHALLENGE_DEFENDER_C);
		//listenProto(HP.code.ARENA_PASS_FIGHT_C);

		// 注册监听消息
		listenMsg(GsConst.MsgType.CHALLENGE_FINISHED);
		listenMsg(GsConst.MsgType.SYNC_ARENA_INFO);
	}

	/**
	 * 更新
	 * 
	 * @return
	 */
	@Override
	public boolean onTick() {
		dealwithDelayOperation(false);
		return super.onTick();
	}

	@Override
	protected boolean onPlayerLogout() {
		dealwithDelayOperation(true);
		return true;
	}

	/**
	 * 消息响应
	 * 
	 * @param msg
	 * @return
	 */
	@Override
	public boolean onMessage(Msg msg) {
		if (msg.getMsg() == GsConst.MsgType.CHALLENGE_FINISHED) {
			onChallengeFinish(msg);
			return true;
		} else if (msg.getMsg() == GsConst.MsgType.SYNC_ARENA_INFO) {
			syncArenaDefenderListS();
			return true;
		}
		return super.onMessage(msg);
	}

	public void onChallengeFinish(Msg msg) {

		// 同步竞技场信息
		syncArenaDefenderListS();

		// 增加公会Boss元气
		int battleResult = msg.getParam(2);
		if (battleResult == GsConst.Arena.CHALLENGE_SUCC) {
			int addVitality = SysBasicCfg.getInstance().getArenaWinAddVitality();
			AllianceManager.getInstance().addAllianceBossVitality(player.getId(), addVitality, Action.ARENA_CHALLENGE);
		}
	}

	/**
	 * 发放竞技场奖励
	 *
	 * @param challengeRank
	 * @param fightResult
	 */
	protected void deliverArenaAward(int challengeRank, int fightResult) {
		if (fightResult == GsConst.Arena.CHALLENGE_SUCC) {
			if (SysBasicCfg.getInstance().getArenaWinAward().length() > 0) {
				AwardItems awardItems = AwardItems.valueOf(SysBasicCfg.getInstance().getArenaWinAward());
				if (awardItems != null) {
					awardItems.rewardTakeAffectAndPush(player, Action.ARENA_FIGHTING, 0,TapDBSource.ELITE_Map,
							Params.valueOf("CHALLENGE_SUCC",fightResult));
				}
			}
		} else {
			if (SysBasicCfg.getInstance().getArenaLoseAward().length() > 0) {
				AwardItems awardItems = AwardItems.valueOf(SysBasicCfg.getInstance().getArenaLoseAward());
				if (awardItems != null) {
					awardItems.rewardTakeAffectAndPush(player, Action.ARENA_FIGHTING, 0,TapDBSource.ELITE_Map,
							Params.valueOf("CHALLENGE_SUCC",fightResult));
				}
			}
		}
	}

	private void dealwithDelayOperation(boolean forceAll) {
		int currSeconds = GuaJiTime.getSeconds();
		// 延迟消息
		Iterator<Msg> it_msg = delayMsgs.iterator();
		while (it_msg.hasNext()) {
			Msg msg = it_msg.next();
			int delayMsgTime = (int) msg.getUserData();
			if (forceAll || currSeconds >= delayMsgTime) {
				if (msg.getMsg() == GsConst.MsgType.CHALLENGE_FINISHED) {
					deliverArenaAward((Integer) msg.getParam(1), (Integer) msg.getParam(2));
				}
				GsApp.getInstance().postMsg(msg);
				it_msg.remove();
			}
		}
	}

	/**
	 * 同步竞技场基本信息
	 * 
	 * @param
	 */
	private void syncArenaDefenderListS() {
		// 获取竞技场信息
		ArenaManager arenaMan = ArenaManager.getInstance();
		PlayerData playerData = player.getPlayerData();
		if (playerData == null || playerData.getStateEntity() == null) {
			return;
		}
		ArenaItemInfo.Builder self = arenaMan.getSelfArenaInfo(player);
		if (self == null) {
			return;
		}
		self.setSurplusChallengeTimes(playerData.getStateEntity().getSurplusChallengeTimes());
		int buyTimes = playerData.getStateEntity().getArenaBuyTimes();
		self.setAlreadyBuyTimes(buyTimes);
		self.setNextBuyPrice(ArenaUtil.getPriceCfgByBuyTimes(buyTimes + 1).getPrice());

		curChallengeRankList = ArenaUtil.calcChallengeRank(self.getRank());
		List<ArenaItemInfo.Builder> defenders = arenaMan.getDefenderList(curChallengeRankList);
		self.setHeadIcon(playerData.getPlayerEntity().getHeadIcon());
		// 构造回复协议
		HPArenaDefenderListSyncS.Builder ret = HPArenaDefenderListSyncS.newBuilder();
		ret.setSelf(self);
		for (ArenaItemInfo.Builder defender : defenders) {
			ret.addDefender(defender);
		}
		ret.setLeftTime(ArenaUtil.calArenaSeasonSec());
		

		sendProtocol(Protocol.valueOf(HP.code.ARENA_DEFENDER_LIST_SYNC_S, ret));
	}

	/*******************************************
	 * 以下为协议响应
	 ********************************************/

	@Override
	public boolean onProtocol(Protocol protocol) {
		// 模块内协议预判 ,HP.code.BATTLE_FORMATION_C進去函式再判斷
		if (isListenProto(protocol.getType())&&(!protocol.checkType(HP.code.BATTLE_FORMATION_C))) {
			if (!FunctionUnlockCfg.checkUnlock(player,null,Const.FunctionType.PVPARENA_Unlock)){
				sendError(protocol.getType(), Status.error.CONDITION_NOT_ENOUGH);
				return true;
			}
		}

		if (protocol.checkType(HP.code.ARENA_DEFENDER_LIST_C)) {
			syncArenaDefenderListS();
			return true;
		} else if (protocol.checkType(HP.code.REPLACE_DEFENDER_LIST_C)) {
			onReplaceDefenderList(protocol.parseProtocol(HPReplaceDefenderList.getDefaultInstance()));
			return true;
		} else if (protocol.checkType(HP.code.BUY_CHALLENGE_TIMES_C)) {
			onBuyChallengeTimes(protocol.parseProtocol(HPBuyChallengeTimes.getDefaultInstance()));
			return true;
		} else if (protocol.checkType(HP.code.ARENA_RANKING_LIST_C)) {
			onArenaRankingList(protocol.parseProtocol(HPArenaRankingList.getDefaultInstance()));
			return true;
		} else if (protocol.checkType(HP.code.BATTLE_FORMATION_C)) {
			onChallengeDefender(protocol.parseProtocol(NewBattleFormation.getDefaultInstance()));
			return true;
		} else if (protocol.checkType(HP.code.PVP_BATTLE_LOG_C_VALUE)) {
			onChallengeLog(protocol.parseProtocol(NewBattleLog.getDefaultInstance()));
			return true;
		}

		return super.onProtocol(protocol);
	}

	/**
	 * 挑战对手
	 * 
	 * @param parseProtocol
	 */
	private void onChallengeDefender(NewBattleFormation protocol) {
				
		if (protocol.getBattleType() != GsConst.BattleType.ARENA) {
			return;
		}
		
		if (!FunctionUnlockCfg.checkUnlock(player,null,Const.FunctionType.PVPARENA_Unlock)){
			sendError(protocol.getType(), Status.error.CONDITION_NOT_ENOUGH);
			return;
		}
		
		StateEntity stateEntity = player.getPlayerData().getStateEntity();
		// 获取要挑战的排名
		int defenderRank = protocol.getDefenderRank();
		
		if (stateEntity != null) {
			if (protocol.getType() == 0) { //0.請求敵方編隊資訊
				int surplusTimes = stateEntity.getSurplusChallengeTimes();
				
				if (surplusTimes <= 0) {
					// 当前挑战次数为0
					sendError(HP.code.BATTLE_FORMATION_S_VALUE, Status.error.NO_CHALLENGE_TIMES_VALUE);
					return;
				}
				
				if (defenderRank <= 0) {
					sendError(HP.code.BATTLE_FORMATION_S_VALUE, Status.error.PARAMS_INVALID_VALUE);
					return;
				}
				
				// 是Robot则为monsterId，否则为0，以便打的时候是同一个monster
				//int monsterId = 0;//protocol.getMonsterId();

				//ArenaItemInfo.Builder selfInfo = ArenaManager.getInstance().getSelfArenaInfo(player);
				
				ArenaEntity arenaEntity = ArenaManager.getInstance().getPlayerIdByEntity(defenderRank);
				
				int playerId = 0;
				int arenaRank = 0;
				if (arenaEntity != null) {
					playerId = arenaEntity.getPlayerId();
					arenaRank = arenaEntity.getRank();
					
					if (defenderRank != arenaRank) {
						sendError(HP.code.BATTLE_FORMATION_S_VALUE, Status.error.PARAMS_INVALID_VALUE);
						return;
					}
				}
				
				if (playerId == player.getId()) {
					sendError(HP.code.BATTLE_FORMATION_S_VALUE, Status.error.PARAMS_INVALID_VALUE);
					return;
				}
				
				int battleId = GuaJiTime.getSeconds();
				int defenPower = 0;
				if (playerId <= 0) {
					//现在是NPC
					
					Map<Integer,NewBattleRole> monsterRoles = new HashMap<Integer,NewBattleRole>();
					RankNpcCfg npcCfg = ArenaUtil.getNpcCfgByRank(defenderRank);
					List<Integer> monsterIds =  npcCfg.getMonsterIdList();
					NewBattleFormation.Builder builder = NewBattleFormation.newBuilder();
					
					int pos = 11; // client log 佔位位置
					
					for (int monsterId : monsterIds) {
						RoleInfo.Builder monsterRole = BuilderUtil.genMonsterRoleInfoBuilder(monsterId, false);
						CLIcheckInfo.Builder checkInfo = BuilderUtil.getRoleInfoCKInfoBuilder(monsterRole,0);
						if ((monsterRole != null)&&(checkInfo != null)) {
							monsterRoles.put(pos,new NewBattleRole(monsterRole,pos));
							checkInfo.setPosId(pos);
							builder.addResultInfo(checkInfo);
							defenPower = defenPower + monsterRole.getFight();
						} else {
							if (monsterId > 0){
								Log.errPrintln(
										String.format("Arena monster config not exist, NpcbyRank: %d, monster: %d", npcCfg.getId(), monsterId));
							}
						}
						pos++;
					}
					
					// 开启monster战场
					if (monsterRoles.size() > 0) {
						this.battlefield = new Battlefield();
						this.battlefield.setMapId(defenderRank);
						this.battlefield.setBattleId(battleId);
						this.battlefield.setm_Defenders(monsterRoles);
						this.battlefield.setBattleType(protocol.getBattleType());
						this.battlefield.setDefendId(npcCfg.getId());
						this.battlefield.setMonster(true);
						this.battlefield.setDefenders(monsterIds);
						this.battlefield.setDefenPower(defenPower);
						
						//..................for client.................
						builder.setType(protocol.getType());
						builder.setBattleId(battleId);
						builder.setBattleType(protocol.getBattleType());
						builder.setDefenderRank(defenderRank);
						sendProtocol(Protocol.valueOf(HP.code.BATTLE_FORMATION_S_VALUE, builder));
					} else {
						sendError(HP.code.BATTLE_FORMATION_S_VALUE, Status.error.PARAMS_INVALID);
					}
				} else {
					PlayerSnapshotInfo.Builder snapshot = SnapShotManager.getInstance().getPlayerSnapShot(playerId);
					if (snapshot != null) {
						Map<Integer,NewBattleRole> DefHeroes = new HashMap<>();
						//RoleInfo.Builder snapshotRole = snapshot.getMainRoleInfoBuilder();
						//DefHeroes.put(10,new NewBattleRole(playerId, snapshotRole,10));
						PlayerSnapshotInfo.Builder playerSnapshot = SnapShotManager.getInstance().getPlayerSnapShot(playerId).clone();
						List<Integer> roleIdCloneList = null;
						if (playerSnapshot != null) {
							roleIdCloneList = new ArrayList<>(playerSnapshot.getFightingRoleIdList());
						}
						List<Builder> mercenarySnapshotRole = SnapShotManager.getInstance().getFightMercenaryInfo(playerId);
						if (mercenarySnapshotRole != null) {
							int pos = 11; // 防守 佔位位置
							for (RoleInfo.Builder aRoleInfo :mercenarySnapshotRole) {
								if (aRoleInfo != null) {
									DefHeroes.put(pos,new NewBattleRole(playerId, aRoleInfo,pos));
									defenPower = defenPower + aRoleInfo.getFight();
								}
								pos++;
							}
						}
						NewBattleFormation.Builder builder = NewBattleFormation.newBuilder();
						for ( Integer posid :DefHeroes.keySet()) {
							CLIcheckInfo.Builder checkInfo = BuilderUtil.getRoleInfoCKInfoBuilder(DefHeroes.get(posid).getRoleInfo(),0);
							checkInfo.setPosId(posid);
							builder.addResultInfo(checkInfo);
						}
						if ((DefHeroes.size() > 0)) {
							this.battlefield = new Battlefield();
							this.battlefield.setMapId(defenderRank);
							this.battlefield.setBattleId(battleId);
							this.battlefield.setm_Defenders(DefHeroes);
							this.battlefield.setBattleType(protocol.getBattleType());
							this.battlefield.setDefendId(playerId);
							this.battlefield.setDefenders(roleIdCloneList);
							this.battlefield.setDefenPower(defenPower);
							//.....................for client....................
							builder.setType(protocol.getType());
							builder.setBattleId(battleId);
							builder.setBattleType(protocol.getBattleType());
							builder.setDefenderRank(defenderRank);
						    sendProtocol(Protocol.valueOf(HP.code.BATTLE_FORMATION_S_VALUE, builder));
						} else {
							sendError(HP.code.BATTLE_FORMATION_S_VALUE, Status.error.PARAMS_INVALID);
						}
					}
				}			
			} else if (protocol.getType() == 1) { //1.傳送我方編隊資訊
				int checkid = protocol.getBattleId();
				//int mapId = Integer.valueOf(protocol.getMapId());
				if ((this.battlefield == null) ||(checkid != this.battlefield.getBattleId())||(defenderRank != this.battlefield.getMapId())){
					sendError(HP.code.BATTLE_FORMATION_S_VALUE, Status.error.PARAMS_INVALID);
					return;
				}
				List<String> role_posList = protocol.getRolePosList();
				Map<Integer,Integer> rolePosMap = new HashMap<>();
				
				// 有傳入攜帶英雄
				if (!PlayerUtil.checkRolePos(player,rolePosMap,role_posList)) {
					return;
				}
				
				PlayerUtil.transFormation(player,rolePosMap,GsConst.FormationType.FormationBegin);
				
				NewBattleFormation.Builder builder = NewBattleFormation.newBuilder();
				Map<Integer,NewBattleRole> Heroes = player.getPlayerData().getNewBattleHeroes(rolePosMap);
				int attackPower = 0;
				for ( Integer posid :Heroes.keySet()) {
					CLIcheckInfo.Builder checkInfo = BuilderUtil.getRoleInfoCKInfoBuilder(Heroes.get(posid).getRoleInfo(),0);
					checkInfo.setPosId(posid);
					builder.addResultInfo(checkInfo);
					attackPower = attackPower + Heroes.get(posid).getRoleInfo().getFight();
				}
				if ((Heroes.size() > 0)&&(this.battlefield.getm_Defenders() != null)&&(this.battlefield.getm_Defenders().size() > 0)) {
					this.battlefield.setm_Attackers(Heroes);
					this.battlefield.setBattleStandby(true);
					this.battlefield.setAttackers(player.getPlayerData().getFormationByType(GsConst.FormationType.FormationBegin).getFightingArray());
					this.battlefield.setAttackPower(attackPower);
					// 扣除挑战次数
					if (!this.battlefield.isCost()) {
						int surplusTimes = Math.max(0, stateEntity.getSurplusChallengeTimes() - 1);
						stateEntity.setSurplusChallengeTimes(surplusTimes);
						stateEntity.notifyUpdate(true);
						player.getPlayerData().syncStateInfo();
						this.battlefield.setCost(true);
					}
					
					//.....................for client....................
					builder.setType(protocol.getType());
					builder.setBattleId(checkid);
					builder.setBattleType(protocol.getBattleType());
					//builder.setDefenderRank(defenderRank);
				    sendProtocol(Protocol.valueOf(HP.code.BATTLE_FORMATION_S_VALUE, builder));
				} else {
					sendError(HP.code.BATTLE_FORMATION_S_VALUE, Status.error.PARAMS_INVALID);
				}
			}
		}		
	}
	
	/**
	 * 發送戰鬥紀錄給server檢查
	 */
	private void onChallengeLog(NewBattleLog protocol) {
		
		int resultid = protocol.getResault();
		NewBattleLog.Builder builder = NewBattleLog.newBuilder();
		int battleId = protocol.getBattleId();
		int Number =  protocol.getLogId(); // 第幾份Id
		
		ArenaItemInfo.Builder selfInfo = ArenaManager.getInstance().getSelfArenaInfo(player);
				
		if (Number == 1) {
			//RetestBattleLog();
		}
		
		if (selfInfo == null) {
			sendError(HP.code.PVP_BATTLE_LOG_S_VALUE, Status.error.PARAMS_INVALID);
			return;
		}
		
		if ((this.battlefield == null) || (this.battlefield.getBattleId() != battleId) || (!this.battlefield.getBattleStandby())) {
			sendError(HP.code.PVP_BATTLE_LOG_S_VALUE, Status.error.PARAMS_INVALID);
			return;
		}
		
		if (this.battlefield.getBattleType() != GsConst.BattleType.ARENA) {
			sendError(HP.code.PVP_BATTLE_LOG_S_VALUE, Status.error.PARAMS_INVALID);
			return;
		}
		
		int total = protocol.getTotleLogId(); //有幾份log
		
		if (total <= 0) {
			sendError(HP.code.PVP_BATTLE_LOG_S_VALUE, Status.error.PARAMS_INVALID);
			return;
		}
		
		if (Number <= 0) {
			sendError(HP.code.PVP_BATTLE_LOG_S_VALUE, Status.error.PARAMS_INVALID);
			return;
		}

		if (Number == 1) { //送一份紀錄後續有幾份log
			this.battlefield.settotalLogId(total);
			this.battlefield.setclientRet(resultid); // 紀錄client闖關結果 0.成功 1.失敗,只會在第一份Log傳過來
		}
		int defenderRank = this.battlefield.getMapId();
		ArenaEntity arenaEntity = ArenaManager.getInstance().getPlayerIdByEntity(defenderRank);
		int nowid = 0;
		int checkret = 0;
		int clientRet = this.battlefield.getclientRet();
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
				}
				
			}
			if (checkret == 0) {
				if (Number == battlefield.gettotalLogId()) {
					boolean isWin = false;
					HPChallengeDefenderRet.Builder abuilder = HPChallengeDefenderRet.newBuilder();
					abuilder.setPreOfChallengeRank(selfInfo.getRank());
					if ((clientRet == 0)) {
						if (SysBasicCfg.getInstance().IsBattleCheck()) {
							if (battlefield.checkDefenderSacrifice()) {
								isWin = true;
								//BattleWinning();
								Log.debugInfo("BattleWinning()_check");
							}
						} else {
							isWin = true;
							//BattleWinning();
							Log.debugInfo("BattleWinning()_No_check");
						}
					} else {
						Log.debugInfo("BattleLose()");
					}
					if (isWin) {
						abuilder.setChallengeResult(GsConst.Arena.CHALLENGE_SUCC);
						if (selfInfo.getRank() < defenderRank) {
							abuilder.setAfterOfChallengeRank(selfInfo.getRank());
						} else {
							abuilder.setAfterOfChallengeRank(defenderRank);
						}
						
						CheatCheckCfg ccCfg = CheatCheckCfg.getCheckCfg(battlefield.getAttackPower());
						if (battlefield.getDefenPower() >= (battlefield.getAttackPower()*ccCfg.getCheckrange())) {
							battlefield.RecordCheatWin(player.getId());
						}
						
					} else {
						abuilder.setChallengeResult(GsConst.Arena.CHALLENGE_FAIL);
						abuilder.setAfterOfChallengeRank(selfInfo.getRank());
					}
					int FightResult = isWin ? GsConst.Arena.CHALLENGE_SUCC:GsConst.Arena.CHALLENGE_FAIL;
					deliverArenaAward(defenderRank,FightResult);
					Msg msg = Msg.valueOf(GsConst.MsgType.CHALLENGE_FINISHED);
					msg.setTarget(GuaJiXID.valueOf(GsConst.ObjType.MANAGER, GsConst.ObjId.ARENA));
					msg.pushParam(player.getId());
					msg.pushParam(defenderRank);
					if (isWin) {
						msg.pushParam(GsConst.Arena.CHALLENGE_SUCC);
						// 推送竞技场战斗任务
						QuestEventBus.fireQuestEventOneTime(QuestEventType.ARENA_BATTLE, player.getXid());
					} else {
						msg.pushParam(GsConst.Arena.CHALLENGE_FAIL);
					}

					msg.pushParam(0);
					msg.pushParam(arenaEntity);

					// 设置延时
					int delayMsgTime = GuaJiTime.getSeconds()+ SysBasicCfg.getInstance().getFightDelayTime(); //currSeconds + battleBuilder.getBattleTime()
							//
					msg.setUserData(delayMsgTime);
					// delayMsgs.add(msg);

					GsApp.getInstance().postMsg(msg);
					
					// 同步竞技场信息
					//syncArenaDefenderListS();

					Msg hawkMsg = Msg.valueOf(GsConst.MsgType.DailyQuestMsg.JING_JI_CHANG_FIGHT,
							GuaJiXID.valueOf(GsConst.ObjType.PLAYER, player.getId()));
					hawkMsg.pushParam(1);
					GsApp.getInstance().postMsg(hawkMsg);

					QuestEventBus.fireQuestEventOneTime(QuestEventType.JING_JI_CHANG_TIMES, player.getXid());

					// 7日之诗 竞技场
					SevenDayQuestEventBus.fireQuestEventOneTime(SevenDayEventType.PVPARENA, player.getXid());

					abuilder.setPreOfChallengeRank(selfInfo.getRank());
					//abuilder.setBattleInfo(null);
					
					BehaviorLogger.log4Platform(player, Action.ARENA_CHALLENGE, Params.valueOf("myRank", selfInfo.getRank()),
							Params.valueOf("defenderRank", defenderRank));

					BehaviorLogger.log4Service(player, Source.USER_OPERATION, Action.ARENA_CHALLENGE,
							Params.valueOf("myRank", selfInfo.getRank()), Params.valueOf("defenderRank", defenderRank));

					Protocol aprotocol = Protocol.valueOf(HP.code.CHALLENGE_DEFENDER_S, abuilder);
					player.sendProtocol(ProtoUtil.compressProtocol(aprotocol));
					resultid = clientRet; // 確認client給資訊是正確的,塞回闖關結果
					
					String resultStr = "";
					if (protocol.hasTapdbjstr()) {
						resultStr = protocol.getTapdbjstr();
					}
					
					TapDBUtil.Event_Arena_PVP(player,battlefield.getTapDBBattleTeam(true),battlefield.getTapDBBattleTeam(false),resultStr);
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
		sendProtocol(Protocol.valueOf(HP.code.PVP_BATTLE_LOG_S, builder));
	}
	
	/**
	 * 请求排行榜信息
	 * 
	 * @param parseProtocol
	 */
	private void onArenaRankingList(HPArenaRankingList parseProtocol) {
		// 获取竞技场排行信息
		ArenaManager arenaMan = ArenaManager.getInstance();
		// 构造回复协议
		HPArenaRankingListRet.Builder ret = HPArenaRankingListRet.newBuilder();
		ArenaItemInfo.Builder self = arenaMan.getSelfArenaInfo(player);

		PlayerData playerData = player.getPlayerData();
		self.setSurplusChallengeTimes(playerData.getStateEntity().getSurplusChallengeTimes());
		int buyTimes = playerData.getStateEntity().getArenaBuyTimes() + 1;
		self.setNextBuyPrice(ArenaUtil.getPriceCfgByBuyTimes(buyTimes).getPrice());

		List<ArenaItemInfo.Builder> rankingList = arenaMan.getRankingList();
		ret.setSelf(self);
		for (ArenaItemInfo.Builder rankInfo : rankingList) {
			ret.addRankInfo(rankInfo);
		}
		sendProtocol(Protocol.valueOf(HP.code.ARENA_RANKING_LIST_S, ret));
	}

	/**
	 * 购买竞技场次数
	 * 
	 * @param parseProtocol
	 */
	private void onBuyChallengeTimes(HPBuyChallengeTimes parseProtocol) {
		PlayerData playerData = player.getPlayerData();
		StateEntity states = playerData.getStateEntity();

		int totalPrice = 0;
		int totalAddTimes = 0;
		int buyTimes = parseProtocol.getTimes();
		if (buyTimes <= 0 || buyTimes >= 1000) {
			player.sendError(HP.code.BUY_CHALLENGE_TIMES_C_VALUE, Status.error.PARAMS_INVALID);
			return;
		}
		for (int i = 1; i <= buyTimes; i++) {
			int curTimes = states.getArenaBuyTimes() + i;
			ArenaBuyTimesCfg cfg = ArenaUtil.getPriceCfgByBuyTimes(curTimes);
			totalPrice += cfg.getPrice();
			totalAddTimes += cfg.getAddChallengeTimes();
		}

		if (playerData.getPlayerEntity().getTotalGold() < totalPrice) {
			// 钻石不足
			sendError(HP.code.BUY_CHALLENGE_TIMES_C_VALUE, Status.error.GOLD_NOT_ENOUGH_VALUE);
			return;
		}

		// 扣费加次
		player.consumeGold(totalPrice, Action.BUY_ARENA_CHALLENGE_TIMES);
		int surplusChallengeTimes = states.getSurplusChallengeTimes() + totalAddTimes;
		states.setSurplusChallengeTimes(surplusChallengeTimes);
		states.setArenaBuyTimes(states.getArenaBuyTimes() + totalAddTimes);
		states.setArenaLastBuyTime(GuaJiTime.getSeconds());
		states.notifyUpdate(true);

		// 推送消耗
		ConsumeItems.valueOf(changeType.CHANGE_GOLD, totalPrice).pushChange(player);

		// 回包
		HPBuyChallengeTimesRet.Builder ret = HPBuyChallengeTimesRet.newBuilder();
		ret.setAlreadyBuyTimes(states.getArenaBuyTimes());
		ret.setNextBuyPrice(ArenaUtil.getPriceCfgByBuyTimes(states.getArenaBuyTimes() + 1).getPrice());
		ret.setSurplusChallengeTimes(states.getSurplusChallengeTimes());
		sendProtocol(Protocol.valueOf(HP.code.BUY_CHALLENGE_TIMES_S, ret));
	}
	/**
	 * 换一批挑战对手
	 * 
	 * @param parseProtocol
	 */
	private void onReplaceDefenderList(HPReplaceDefenderList parseProtocol) {
		ArenaManager arenaMan = ArenaManager.getInstance();
		ArenaItemInfo.Builder self = arenaMan.getSelfArenaInfo(player);
		curChallengeRankList = ArenaUtil.calcChallengeRank(self.getRank());
		List<ArenaItemInfo.Builder> defenders = arenaMan.getDefenderList(curChallengeRankList);

		// 构造回复协议
		HPReplaceDefenderListRet.Builder ret = HPReplaceDefenderListRet.newBuilder();
		for (ArenaItemInfo.Builder defender : defenders) {
			ret.addDefender(defender);
		}
		sendProtocol(Protocol.valueOf(HP.code.REPLACE_DEFENDER_LIST_S, ret));
		BehaviorLogger.log4Platform(player, Action.ARENA_REFRESH_OPPONENT_LIST);
	}

}
