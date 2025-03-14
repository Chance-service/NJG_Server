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
import com.guaji.game.config.FunctionUnlockCfg;
import com.guaji.game.config.NewAFKItemCfg;
import com.guaji.game.config.NewMapCfg;
import com.guaji.game.config.SysBasicCfg;
import com.guaji.game.config.VipPrivilegeCfg;
import com.guaji.game.entity.StateEntity;
import com.guaji.game.item.AwardItems;
import com.guaji.game.item.ConsumeItems;
import com.guaji.game.item.DropItems;
import com.guaji.game.log.BehaviorLogger;
import com.guaji.game.log.BehaviorLogger.Action;
import com.guaji.game.log.BehaviorLogger.Params;
import com.guaji.game.manager.BattleDebugManager;
import com.guaji.game.manager.RankManager;
import com.guaji.game.module.quest.QuestEventBus;
import com.guaji.game.module.sevendaylogin.SevenDayQuestEventBus;
import com.guaji.game.player.Player;
import com.guaji.game.player.PlayerModule;
import com.guaji.game.protocol.Battle.HPBuyFastFightTimes;
import com.guaji.game.protocol.Battle.HPBuyFastFightTimesRet;
import com.guaji.game.protocol.Battle.NewBattleAward;
import com.guaji.game.protocol.Battle.NewBattleFormation;
import com.guaji.game.protocol.Battle.NewBattleLevelInfo;
import com.guaji.game.protocol.Battle.NewBattleLog;
import com.guaji.game.protocol.Battle.NewBattleLog.NewBattleLogInfo;
import com.guaji.game.protocol.Const;
import com.guaji.game.protocol.Const.QuestEventType;
import com.guaji.game.protocol.Const.RankType;
import com.guaji.game.protocol.Const.SevenDayEventType;
import com.guaji.game.protocol.HP;
import com.guaji.game.protocol.Player.CLIcheckInfo;
import com.guaji.game.protocol.Player.RoleInfo;
import com.guaji.game.protocol.Status;
import com.guaji.game.util.ActivityUtil;
import com.guaji.game.util.BuilderUtil;
import com.guaji.game.util.GsConst;
import com.guaji.game.util.PlayerUtil;
import com.guaji.game.util.TapDBUtil;
import com.guaji.game.util.TapDBUtil.TapDBSource;


/**
 * 战斗模块 战斗类型（分为普通关卡（快速战斗），普通boss关卡（boss扫荡），和精英boss关卡（boss扫荡））
 */
public class PlayerBattleModule extends PlayerModule {
	/**
	 * 戰鬥場所
	 */
	private Battlefield battlefield;
	/**
	 * 构造函数
	 *
	 * @param player
	 */
	public PlayerBattleModule(Player player) {
		super(player);
		battlefield = null;
		//一般戰鬥領獎Client自己演
		listenProto(HP.code.TAKE_FIGHT_AWARD_C);
		listenProto(HP.code.SYNC_LEVEL_INFO_C);
		listenProto(HP.code.BUY_FAST_FIGHT_TIMES_C);
		listenProto(HP.code.BATTLE_FAST_FIGHT_C);
		//Boss戰專用
		listenProto(HP.code.BATTLE_FORMATION_C);
		listenProto(HP.code.BATTLE_LOG_C);
		
		if (GsApp.getInstance().isDebug()) {
			listenMsg(GsConst.MsgType.BATTLE_REPEAT);
		}

	}
	
	/**
	 * 消息响应
	 * 
	 * @param msg
	 * @return
	 */
	@Override
	public boolean onMessage(Msg msg) {
		if (GsApp.getInstance().isDebug()) {
			if (msg.getMsg() == GsConst.MsgType.BATTLE_REPEAT) {
				repeatBattle();
				return true;
			}
		}
		return super.onMessage(msg);
	}
	
	public void repeatBattle() {
		if (GsApp.getInstance().isDebug()) {
			List<NewBattleLog> protoList = BattleDebugManager.getInstance().getProto(player.getId());
			for (NewBattleLog aproto:protoList) {
				onChallengeLog(aproto);
			}
		}
	}


//	@Override
//	public boolean onTick() {
//		try {
//		} catch (Exception e) {
//			MyException.catchException(e);
//		}
//		
//		return super.onTick();
//	}

	@Override
	public boolean onProtocol(Protocol protocol) {
		if (protocol.checkType(HP.code.TAKE_FIGHT_AWARD_C)) {
			onTakeFightAward(false);	
			return true;
		} else if (protocol.checkType(HP.code.SYNC_LEVEL_INFO_C)) {
			syncLevelInfo();
			return true;
		} else if (protocol.checkType(HP.code.BATTLE_FORMATION_C)) {
			onChallengeBoss(protocol.parseProtocol(NewBattleFormation.getDefaultInstance()));
			return true;
		} else if (protocol.checkType(HP.code.BATTLE_LOG_C_VALUE)) {
			onChallengeLog(protocol.parseProtocol(NewBattleLog.getDefaultInstance()));
			return true;
		} else if (protocol.checkType(HP.code.BATTLE_FAST_FIGHT_C)) {
			onFastBattle();
			return true;
		} else if (protocol.checkType(HP.code.BUY_FAST_FIGHT_TIMES_C)) {
			onBuyFastFightTimes(protocol.parseProtocol(HPBuyFastFightTimes.getDefaultInstance()));
			return true;
		}
//		if (protocol.checkType(HP.code.BATTLE_REQUEST_C)) {
//			onBattleRequest(protocol.parseProtocol(HPBattleRequest.getDefaultInstance()));
//			return true;
//		} else if (protocol.checkType(HP.code.BUY_FAST_FIGHT_TIMES_C)) {
//			onBuyFastFightTimes(protocol.parseProtocol(HPBuyFastFightTimes.getDefaultInstance()));
//			return true;
//		} else if (protocol.checkType(HP.code.BUY_BOSS_FIGHT_TIMES_C)) {
//			onBuyBossFightTimes(protocol.parseProtocol(HPBuyBossFightTimes.getDefaultInstance()));
//			return true;
//		} else if (protocol.checkType(HP.code.BOSS_WIPE_C)) {
//			onWipeBoss(protocol.parseProtocol(HPBossWipe.getDefaultInstance()));
//			return true;
//		} else if (protocol.checkType(HP.code.BUY_ELITE_FIGHT_TIMES_C)) {
//			onBuyEliteMapFightTimes(protocol.parseProtocol(HPBuyEliteFightTimes.getDefaultInstance()));
//			return true;
//		} else if (protocol.checkType(HP.code.FACE_BOOK_SHARE_TIMES_C)) { // 20150108
//			OnFaceBookShareTimes();
//			return true;
//		} else if (protocol.checkType(HP.code.PASS_ARENA_FIGHT_C)) {
//			onArenaBattleSkip();
//			return true;
//		} else if (protocol.checkType(HP.code.PASS_PVE_BOSS_C)) {
//			onPveBossBattleSkip();
//			return true;
//		} else if (protocol.checkType(HP.code.FETCH_LAST_FIGHT_C_VALUE)) {
//			onFetchLastBattleRequest();
//			return true;
//		}
		return super.onProtocol(protocol);
	}
	/**
	 * 對地圖挑戰勝利
	 */	
	private void BattleWinning() {
		if (battlefield.getBattleFinish()) {
			return;
		}
		NewMapCfg mapCfg = player.getPlayerData().getCurBattleMap();
		StateEntity stateEntity = player.getPlayerData().getStateEntity();
		AwardItems awardItems = new AwardItems();
		if((mapCfg == null)||(stateEntity == null)) {
			sendError(HP.code.BATTLE_LOG_S_VALUE, Status.error.PARAMS_INVALID);
			return;
		}
		
		if (mapCfg.getNextMapId() == 0) { // 沒有下一個關了
			sendError(HP.code.BATTLE_LOG_S_VALUE, Status.error.NO_CHALLENGE_TIMES);
			return;
		}
		int currSeconds = GuaJiTime.getSeconds();
		//onTakeFightAward(true);
		List<DropItems.Item> dropitems = mapCfg.getBossDropItems().calcDrop();
		 for (DropItems.Item item : dropitems) {
			 awardItems.addItem(item);
		 }
		if (mapCfg.BossClass()) {
			
			//stateEntity.setLastTakeBattleAwardTime(currSeconds);
			// 推送战胜BOSS任务
			QuestEventBus.fireQuestEventOneTime(QuestEventType.KILL_BOSS, player.getXid());
		}
		int oldMapId = stateEntity.getPassMapId();
		stateEntity.setPassMapId(mapCfg.getId());
		//player.getPlayerData().sendPassMapMsg(mapCfg.getId());
		
		//七日任務
		SevenDayQuestEventBus.fireQuestEvent(SevenDayEventType.PASS_MISSION,stateEntity.getPassMapId(),player.getXid());
		
		QuestEventBus.fireQuestEvent(QuestEventType.CROSS_MISSION_NUMBER,
				stateEntity.getPassMapId(), player.getXid());
		
		stateEntity.setintoLevelTime(currSeconds);
		stateEntity.setCurBattleMap(mapCfg.getNextMapId());
		stateEntity.notifyUpdate(true);
		
		postRankChangeMsg(RankType.CUSTOMPASS_BOSS_RANK,stateEntity.getPlayerId(),stateEntity.getCurBattleMap());
		
		awardItems.rewardTakeAffectAndPush(player, Action.BOSS_WIPE, 1,TapDBSource.Challenge_Boss,Params.valueOf("mapId", mapCfg.getId()));
		
		player.notifyMapPass(oldMapId, mapCfg.getId());
		
		battlefield.setBattleFinish(true);

		player.getPlayerData().syncStateInfo();
	}
	/**
	 * 對地圖Boss發起挑戰(PvE編隊)
	 */
	private void onChallengeBoss(NewBattleFormation protocol) {
		if ((protocol.getBattleType() != GsConst.BattleType.LEVEL)&&(protocol.getBattleType() != GsConst.BattleType.DEBUG)) {
			return;
		}
		StateEntity stateEntity = player.getPlayerData().getStateEntity();
		if (stateEntity != null) {
			if (protocol.getType() == 0) { //0.請求敵方編隊資訊
				
				int battleId = GuaJiTime.getSeconds();
				List<Integer> monsterIds = null;
				Map<Integer,NewBattleRole> monsterRoles = new HashMap<Integer,NewBattleRole>();
				NewBattleFormation.Builder builder = NewBattleFormation.newBuilder();
				int mapId = 0;
				
				if (protocol.getBattleType() == GsConst.BattleType.DEBUG) {
					String MonsterStr = protocol.getMapId();
					Map<Integer,Integer> rolePosMap = new HashMap<>();
					String [] id_pos = MonsterStr.split(",");
					int monId = 0;
					int mPos = 0;
					for (String aStr :id_pos) {
						String [] ss = aStr.split("_");
						monId = Integer.valueOf(ss[0].trim());
						mPos = Integer.valueOf(ss[1].trim());
						rolePosMap.put(mPos, monId);
					}
					int monsterId = 0;
					for (Integer pos:rolePosMap.keySet()) {
						if (!((pos >= 10)&&(pos <= 15))) {
							continue;
						}
						monsterId = rolePosMap.get(pos);
						RoleInfo.Builder monsterRole = BuilderUtil.genMonsterRoleInfoBuilder(monsterId, false);
						CLIcheckInfo.Builder checkInfo = BuilderUtil.getRoleInfoCKInfoBuilder(monsterRole,0);
						if ((monsterRole != null)&&(checkInfo != null)) {
							monsterRoles.put(pos,new NewBattleRole(monsterRole,pos));
							checkInfo.setPosId(pos);
							builder.addResultInfo(checkInfo);
						} else {
							if (monsterId > 0){
								Log.errPrintln(
										String.format("Debug monster config not exist, monster: %d", monsterId));
							}
						}
					}
					
				} else { 
					NewMapCfg mapCfg = player.getPlayerData().getCurBattleMap();
					if((mapCfg == null)||(player.getLevel() < mapCfg.getUnlock())) {
						sendError(HP.code.BATTLE_FORMATION_S_VALUE, Status.error.PARAMS_INVALID);
						return;
					}
					mapId = mapCfg.getId();
					monsterIds =  mapCfg.getBossIDIdxList();
					
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
										String.format("map monster config not exist, map: %d, monster: %d", mapCfg.getId(), monsterId));
							}
						}
						pos++;
					}
				}

				// 开启monster战场
				if (monsterRoles.size() > 0) {
					this.battlefield = new Battlefield();
					this.battlefield.setMapId(mapId);
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
				String mapStr = protocol.getMapId();
				int mapId = mapStr.isEmpty()? 0: Integer.valueOf(mapStr);
				if ((this.battlefield == null) ||(checkid != this.battlefield.getBattleId())||(mapId != this.battlefield.getMapId()) ){
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
	 * 發送戰鬥紀錄給server檢查
	 */
	private void onChallengeLog(NewBattleLog protocol) {
		
		int resultid = protocol.getResault();
		NewBattleLog.Builder builder = NewBattleLog.newBuilder();
		int battleId = protocol.getBattleId();
		int Number =  protocol.getLogId(); // 第幾份Id
		
		if (Number == 1) {
			if (GsApp.getInstance().isDebug()) {
				if (!this.battlefield.IsRetest()) {
					BattleDebugManager.getInstance().clearlog(player.getId());
				}
				BattleDebugManager.getInstance().RetestBattlefield(player,this.battlefield);
			}
		}
		
		if ((this.battlefield == null) || (this.battlefield.getBattleId() != battleId) || (!this.battlefield.getBattleStandby())) {
			sendError(HP.code.BATTLE_LOG_S_VALUE, Status.error.PARAMS_INVALID);
			return;
		}
		
		if ((this.battlefield.getBattleType() != GsConst.BattleType.LEVEL)&&(this.battlefield.getBattleType() != GsConst.BattleType.DEBUG)) {
			sendError(HP.code.BATTLE_LOG_S_VALUE, Status.error.PARAMS_INVALID);
			return;
		}
		
		int total = protocol.getTotleLogId(); //有幾份log
		
		if (total <= 0) {
			sendError(HP.code.BATTLE_LOG_S_VALUE, Status.error.PARAMS_INVALID);
			return;
		}
		
		if (Number <= 0) {
			sendError(HP.code.BATTLE_LOG_S_VALUE, Status.error.PARAMS_INVALID);
			return;
		}

		if (Number == 1) { //送一份紀錄後續有幾份log
			this.battlefield.settotalLogId(total);
			this.battlefield.setclientRet(resultid); // 紀錄client闖關結果 0.成功 1.失敗,只會在第一份Log傳過來
		}
		
		int nowid = 0;
		int checkret = 0;
		int clientRet = this.battlefield.getclientRet();
		if (GsApp.getInstance().isDebug()) {
			BattleDebugManager.getInstance().addProto(player.getId(), protocol);
		}
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
					if ((clientRet == 0)) {
						if (SysBasicCfg.getInstance().IsBattleCheck()) {
							if (battlefield.checkDefenderSacrifice()) {
								BattleWinning();
								Log.debugInfo("BattleWinning()_check");
							}
						} else {
							BattleWinning();
							Log.debugInfo("BattleWinning()_No_check");
						}
						
					} else {
						player.triggerFailedGiftStart();
					}
					
					resultid = clientRet; // 確認client給資訊是正確的,塞回闖關結果
										
					String resultStr = "";
					if (protocol.hasTapdbjstr()) {
						resultStr = protocol.getTapdbjstr();
					}

					TapDBUtil.Event_Battle_PVE(player,battlefield.getMapId(),battlefield.getTapDBBattleTeam(true),resultStr);
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
		sendProtocol(Protocol.valueOf(HP.code.BATTLE_LOG_S, builder));
	}
	
	/**
	 * 購買戰鬥次數
	 *
	 * @param protocol
	 * @return
	 */
	protected boolean onBuyFastFightTimes(HPBuyFastFightTimes protocol) {
		StateEntity stateEntity = player.getPlayerData().getStateEntity();
		VipPrivilegeCfg vipPrivilegeCfg = ConfigManager.getInstance().getConfigByKey(VipPrivilegeCfg.class,
				player.getVipLevel());
		if (vipPrivilegeCfg != null && stateEntity != null) {
			int buyTimes = protocol.getTimes();
			if (buyTimes <= 0) {
				return true;
			}

			if ((stateEntity.getFastFightBuyTimes() + buyTimes) > vipPrivilegeCfg.getBuyFastFightTimes()) {
				sendError(HP.code.BUY_FAST_FIGHT_TIMES_C_VALUE, Status.error.FAST_FIGHT_BUY_TIMES_NOT_ENOUGH_VALUE);
				return false;
			}

			if (stateEntity.getFastFightTimes() >= 99) {
				sendError(HP.code.BUY_FAST_FIGHT_TIMES_C_VALUE, Status.error.BATTLE_FAST_FIGHT_TIME_IS_MAX_VALUE);
				return false;
			}
			int totalPrice = 0;
			for (int i = 0; i < buyTimes; i++) {
				// 钻石不够
				int goldPrice = SysBasicCfg.getInstance().getBuyFastFightPrice(stateEntity.getFastFightBuyTimes() + i);
				totalPrice += goldPrice;
			}

			Float quickCostRatio = ActivityUtil
					.getQuickCostActivity(player.getPlayerData().getPlayerEntity().getCreateTime());
			if (quickCostRatio != null) {
				totalPrice *= quickCostRatio;
			}
			if (player.getGold() < totalPrice) { // 會有不用花錢的時候,移除小於等於零
				sendError(HP.code.BUY_FAST_FIGHT_TIMES_C_VALUE, Status.error.GOLD_NOT_ENOUGH_VALUE);
				return false;
			}

			// 扣除钻石
			if (totalPrice > 0) {
				player.consumeGold(totalPrice, Action.BUY_FAST_FIGHT_TIMES);
				ConsumeItems consumeItems = ConsumeItems.valueOf(Const.changeType.CHANGE_GOLD, player.getGold());
				consumeItems.pushChange(player);
			}

			BehaviorLogger.log4Platform(player, Action.BUY_FAST_FIGHT_TIMES, Params.valueOf("costGold", totalPrice),
					Params.valueOf("buyTimes", buyTimes));

			// 增加公会Boss元气 ( xpf )
//			int addVitality = SysBasicCfg.getInstance().getQuickBattleAddVitality();
//			AllianceManager.getInstance().addAllianceBossVitality(player.getId(), addVitality * buyTimes,
//					Action.FAST_FIGHTING);

			// 设置次数
			stateEntity.setFastFightTimes(stateEntity.getFastFightTimes() + buyTimes);
			stateEntity.setFastFightBuyTimes(stateEntity.getFastFightBuyTimes() + buyTimes);
			stateEntity.notifyUpdate(true);

			HPBuyFastFightTimesRet.Builder builder = HPBuyFastFightTimesRet.newBuilder();
			builder.setFastFightTimes(stateEntity.getFastFightTimes());
			builder.setFastFightBuyTimes(stateEntity.getFastFightBuyTimes());
			sendProtocol(Protocol.valueOf(HP.code.BUY_FAST_FIGHT_TIMES_S, builder));

			// 直接开始快速战斗
			onFastBattle();
			return true;
		}
		return false;
	}
	
	/**
	 * 快速戰鬥
	 */
	private void onFastBattle() {
		//int currSeconds = GuaJiTime.getSeconds();
		
		if (!FunctionUnlockCfg.checkUnlock(player,null,Const.FunctionType.speedpve_Unlock)){
			sendError(HP.code.BATTLE_FAST_FIGHT_C_VALUE, Status.error.CONDITION_NOT_ENOUGH);
			return;
		}
		
		StateEntity stateEntity = player.getPlayerData().getStateEntity();
		
		if (stateEntity == null) {
			sendError(HP.code.BATTLE_FAST_FIGHT_C_VALUE,Status.error.DATA_NOT_FOUND);
			return;
		}
		
		NewMapCfg mapCfg = player.getPlayerData().getCurBattleMap();
		if (mapCfg == null) {
			sendError(HP.code.BATTLE_FAST_FIGHT_C_VALUE,Status.error.PARAMS_INVALID_VALUE);
			return;
		}
		
		if (stateEntity.getFastFightTimes() <= 0) {
			player.sendError(HP.code.BATTLE_FAST_FIGHT_C_VALUE,Status.error.BATTLE_FAST_FIGHT_TIME_NOT_ENOUGH_VALUE);
			return ;
		}
		
		stateEntity.setFastFightTimes(stateEntity.getFastFightTimes() - 1);

		
		stateEntity.setTotalFastFightCount(stateEntity.getTotalFastFightCount() + 1);
		
		if (stateEntity.getRoleFirstFastBattle() == 1) {
			stateEntity.setRoleFirstFastBattle(0);
		}
		
		stateEntity.notifyUpdate(true);
		player.getPlayerData().syncStateInfo();
		
		NewBattleAward.Builder builder = NewBattleAward.newBuilder();
		AwardItems awardItems = new AwardItems();
		VipPrivilegeCfg vipCfg = ConfigManager.getInstance().getConfigByKey(VipPrivilegeCfg.class, player.getVipLevel());
		int addSeconds = SysBasicCfg.getInstance().getFastFightTime(); // 兩小時
		int maxSeconds = addSeconds;
		int addexp = (int)Math.floor((float)maxSeconds * mapCfg.getExp()*vipCfg.getIdleRatio());
		int addcoin = (int)Math.floor((float)maxSeconds * mapCfg.getSkyCoin());
		int addpotion = (int)Math.floor((float)maxSeconds * mapCfg.getPotion());
		builder.setExp(addexp);
		awardItems.addExp(addexp);
		awardItems.addCoin(addcoin);
		String itemStr = String.format(SysBasicCfg.getInstance().getHeroEXPItem(),addpotion);
		AwardItems potionItems = AwardItems.valueOf(itemStr);
		awardItems.appendAward(potionItems);
		
		// 計算掉落物
		List<Integer> DropList = mapCfg.getDropItemIdxList();
		for (Integer idx : DropList) {
			NewAFKItemCfg ItemCfg = ConfigManager.getInstance().getConfigByKey(NewAFKItemCfg.class, idx);
			if (ItemCfg != null) {

				int	acount =  maxSeconds/ItemCfg.getTimes();

				if (acount == 0) { // 預設第一時間未達成,後面更不可能達成
					break;
				} 
				for (int i = 0 ; i < acount ; i++) {
					awardItems.addItem(ItemCfg.getDropItems().calcDropByOnly());
				}
			}
		}
//		List<Integer> DropList = mapCfg.getFastItemIdxList();
//		List<Integer> countList = mapCfg.getFastItemTimeIdxList();
//		int index = 0;
//		int acount = 0;
//		for (Integer idx : DropList) {
//			NewAFKItemCfg ItemCfg = ConfigManager.getInstance().getConfigByKey(NewAFKItemCfg.class, idx);
//			if (countList.get(index) != null) {
//				acount = countList.get(index);
//				for (int i = 0 ; i < acount ; i++) {
//					awardItems.addItem(ItemCfg.getDropItems().calcDropByOnly());
//				}
//			}
//			index++;
//		}
		
		awardItems.rewardTakeAffectAndPush(player, Action.FAST_FIGHTING, 0,TapDBSource.Fast_Fighting,Params.valueOf("TotalCount", stateEntity.getTotalFastFightCount()));
		for (AwardItems.Item item : awardItems.getAwardItems()) {
			NewBattleAward.Item.Builder aItmebuilder = NewBattleAward.Item.newBuilder();
			aItmebuilder.setItemId(item.getId());
			aItmebuilder.setItemType(item.getType());
			aItmebuilder.setItemCount(item.getCount());
			builder.addDrop(aItmebuilder);
		}
		
		QuestEventBus.fireQuestEventOneTime(QuestEventType.QUICK_BATTLE, player.getXid());

		// 7日之诗 快速战斗
		SevenDayQuestEventBus.fireQuestEventOneTime(SevenDayEventType.FASTBATTLE, player.getXid());

		Msg hawkMsg = Msg.valueOf(GsConst.MsgType.DailyQuestMsg.FAST_FIGHT,
				GuaJiXID.valueOf(GsConst.ObjType.PLAYER, player.getId()));
		hawkMsg.pushParam(1);
		GsApp.getInstance().postMsg(hawkMsg);
		
		sendProtocol(Protocol.valueOf(HP.code.BATTLE_FAST_FIGHT_S, builder));
	}
	
	/**
	 * 領取掛機戰鬥獎勵
	 */
	private void onTakeFightAward(boolean challenge) {
		int currSeconds = GuaJiTime.getSeconds();
		StateEntity stateEntity = player.getPlayerData().getStateEntity();
		NewMapCfg mapCfg = player.getPlayerData().getCurBattleMap();
		if (mapCfg == null) {
			sendError(HP.code.TAKE_FIGHT_AWARD_S_VALUE,Status.error.PARAMS_INVALID_VALUE);
			return;
		}
		
//		if (challenge && (!mapCfg.BossClass())) { //挑戰關卡,過下一章節才順便領
//			return;
//		}
		
		if (stateEntity.getFirstFightAward() == 0) {  // 首次領獎贈送30分獎勵
			NewBattleAward.Builder builder = NewBattleAward.newBuilder();
			AwardItems awardItems = new AwardItems();
			int maxSeconds = 1800;
			int addexp = (int)Math.floor((float)maxSeconds * mapCfg.getExp());
			int addcoin = (int)Math.floor((float)maxSeconds * mapCfg.getSkyCoin());
			int addpotion = (int) Math.floor((float) maxSeconds * mapCfg.getPotion());
			builder.setExp(addexp);
			awardItems.addExp(addexp);
			awardItems.addCoin(addcoin);
			String itemStr = String.format(SysBasicCfg.getInstance().getHeroEXPItem(),addpotion);
			AwardItems potionItems = AwardItems.valueOf(itemStr);
			awardItems.appendAward(potionItems);
			List<Integer> DropList = mapCfg.getDropItemIdxList();
			for (Integer idx : DropList) {
				NewAFKItemCfg ItemCfg = ConfigManager.getInstance().getConfigByKey(NewAFKItemCfg.class, idx);
				if (ItemCfg != null) {
	
					int	acount =  maxSeconds/ItemCfg.getTimes();

					if (acount == 0) { // 預設第一時間未達成,後面更不可能達成
						break;
					} 
					for (int i = 0 ; i < acount ; i++) {
						awardItems.addItem(ItemCfg.getDropItems().calcDropByOnly());
					}
				}
			}
			awardItems.rewardTakeAffectAndPush(player, Action.FIRST_BATTLE_REWARD, 0,TapDBSource.Battle_Reward);
			for (AwardItems.Item item : awardItems.getAwardItems()) {
				NewBattleAward.Item.Builder aItmebuilder = NewBattleAward.Item.newBuilder();
				aItmebuilder.setItemId(item.getId());
				aItmebuilder.setItemType(item.getType());
				aItmebuilder.setItemCount(item.getCount());
				builder.addDrop(aItmebuilder);
			}
			stateEntity.setFirstFightAward(1);
			stateEntity.notifyUpdate(true);
			if (!challenge)
				sendProtocol(Protocol.valueOf(HP.code.TAKE_FIGHT_AWARD_S, builder));
			return;
		}
		
		int intoleveltime = stateEntity.getintoLevelTime();
		int takeSeconds = stateEntity.getLastTakeBattleAwardTime();
		int allSeconds = currSeconds - intoleveltime; // 經過時間
		int addSeconds = (takeSeconds > 0) ? (currSeconds - takeSeconds) : allSeconds; // 有效領取時間
		int useSeconds = (takeSeconds > 0) ? (takeSeconds - intoleveltime) : 0; // 已領取時間
		int acount,bcount = 0;
		NewBattleAward.Builder builder = NewBattleAward.newBuilder();
		AwardItems awardItems = new AwardItems();
		VipPrivilegeCfg vipCfg = ConfigManager.getInstance().getConfigByKey(VipPrivilegeCfg.class, player.getVipLevel());
		int BaseAFKMaxTime = vipCfg.getIdleTimes();
		if (addSeconds >= 60)  { // 大於一分鐘再檢查
			int maxSeconds = Math.min(addSeconds,BaseAFKMaxTime);
			int addexp = (int)Math.floor((float)maxSeconds * mapCfg.getExp()*vipCfg.getIdleRatio());
			int addcoin = (int)Math.floor((float)maxSeconds * mapCfg.getSkyCoin());
			int addpotion = (int)Math.floor((float)maxSeconds * mapCfg.getPotion());
			builder.setExp(addexp);
			awardItems.addExp(addexp);
			awardItems.addCoin(addcoin);
			String itemStr = String.format(SysBasicCfg.getInstance().getHeroEXPItem(),addpotion);
			AwardItems potionItems = AwardItems.valueOf(itemStr);
			awardItems.appendAward(potionItems);
			List<Integer> DropList = mapCfg.getDropItemIdxList();
			for (Integer idx : DropList) {
				NewAFKItemCfg ItemCfg = ConfigManager.getInstance().getConfigByKey(NewAFKItemCfg.class, idx);
				if (ItemCfg != null) {
					if (takeSeconds == 0) { // 本關卡第一次領取
						acount =  maxSeconds/ItemCfg.getTimes();
					}else {
						if ((allSeconds-useSeconds) > BaseAFKMaxTime) {
							allSeconds = BaseAFKMaxTime+useSeconds;
						}
						acount =  allSeconds/ItemCfg.getTimes();
						bcount =  useSeconds/ItemCfg.getTimes();
						acount = acount - bcount;
					}
					if (acount == 0) { // 預設第一時間未達成,後面更不可能達成
						break;
					} 
					for (int i = 0 ; i < acount ; i++) {
						awardItems.addItem(ItemCfg.getDropItems().calcDropByOnly());
					}
				}
			}
			stateEntity.setLastTakeBattleAwardTime(currSeconds);
			stateEntity.notifyUpdate(true);
			awardItems.rewardTakeAffectAndPush(player, Action.BATTLE_REWARD, 0,TapDBSource.Battle_Reward);
			for (AwardItems.Item item : awardItems.getAwardItems()) {
				NewBattleAward.Item.Builder aItmebuilder = NewBattleAward.Item.newBuilder();
				aItmebuilder.setItemId(item.getId());
				aItmebuilder.setItemType(item.getType());
				aItmebuilder.setItemCount(item.getCount());
				builder.addDrop(aItmebuilder);
			}
			// 7日之诗 快速战斗
			SevenDayQuestEventBus.fireQuestEventOneTime(SevenDayEventType.TAKE_FIGHT_AWARD, player.getXid());
			// 每日任務
			Msg hawkMsg = Msg.valueOf(GsConst.MsgType.DailyQuestMsg.TAKE_FIGHT_AWARD,
					GuaJiXID.valueOf(GsConst.ObjType.PLAYER, player.getId()));
			hawkMsg.pushParam(1);
			GsApp.getInstance().postMsg(hawkMsg);
			
//			Msg gMsg = Msg.valueOf(GsConst.MsgType.DailyQuestMsg.GLORY_HOLE_TAKE_FIGHT_AWARD,
//					GuaJiXID.valueOf(GsConst.ObjType.PLAYER, player.getId()));
//			gMsg.pushParam(1);
//			GsApp.getInstance().postMsg(gMsg);
			
			if (!challenge)
				sendProtocol(Protocol.valueOf(HP.code.TAKE_FIGHT_AWARD_S, builder));
		} else {
			if (!challenge)
				sendError(HP.code.TAKE_FIGHT_AWARD_S_VALUE,Status.error.GIFT_REWARDED);
		}
	}
	
	/**
	 * 領取掛機相關數據
	 */
	private void syncLevelInfo() {
		StateEntity stateEntity = player.getPlayerData().getStateEntity();
		if (stateEntity != null) {
			int intotime = stateEntity.getintoLevelTime();
			if (intotime == 0) {
				stateEntity.setintoLevelTime(GuaJiTime.getSeconds());
				int levelmap = stateEntity.getCurBattleMap();
				if (levelmap == 0) {
					stateEntity.setCurBattleMap(NewMapCfg.getMinMapId());
				}
				stateEntity.notifyUpdate(true);
			}
			NewBattleLevelInfo.Builder builder = NewBattleLevelInfo.newBuilder();
			builder.setIntoTime(stateEntity.getintoLevelTime());
			builder.setTakeTime(stateEntity.getLastTakeBattleAwardTime());
			sendProtocol(Protocol.valueOf(HP.code.SYNC_LEVEL_INFO_S, builder));
		}
	}

	@Override
	protected boolean onPlayerLogin() {
		// 加载地图状态数据
		player.getPlayerData().loadMapEntity();

		// 加载地图分析数据
		player.getPlayerData().loadMapStatistics();
		player.getPlayerData().syncMapStatistics();
		return true;
	}

	@Override
	protected boolean onPlayerAssemble() {
		// 获取状态数据
//		StateEntity stateEntity = player.getPlayerData().getStateEntity();

		// 手动战斗冷却
//		if (SysBasicCfg.getInstance().getManualBattleCd() > 0) {
//			if (stateEntity.getLatestBattleType() == Battle.battleType.BATTLE_PVE_BOSS_VALUE
//					|| stateEntity.getLatestBattleType() == Battle.battleType.BATTLE_PVP_ARENA_VALUE) {
//				int curSeconds = GuaJiTime.getSeconds();
//				if (stateEntity.getNextBattleTime() > curSeconds + SysBasicCfg.getInstance().getManualBattleCd()) {
//					stateEntity.setNextBattleTime(curSeconds + SysBasicCfg.getInstance().getManualBattleCd());
//				}
//			}
//		}

		// 首次数据延迟处理
//		if (stateEntity.getNextBattleTime() <= 0) {
//			stateEntity.setNextBattleTime(GuaJiTime.getSeconds() + SysBasicCfg.getInstance().getFightMinCool());
//		}

		// 同步状态信息
		player.getPlayerData().syncStateInfo();
		return true;
	}
	
	/**
	 * @param type
	 * @param playerId
	 * @param passMapId
	 */
	private void postRankChangeMsg(RankType type,int playerId,int curMapId) {
		Msg questMsg = Msg.valueOf(GsConst.MsgType.ON_RANK_CHANGE);
		questMsg.pushParam(type);
		questMsg.pushParam(String.format("%s,%s", playerId, curMapId));
		GsApp.getInstance().postMsg(RankManager.getInstance().getXid(), questMsg);
	}

	@Override
	protected boolean onPlayerLogout() {
		//battlefield = null;
		return true;
	}

}
