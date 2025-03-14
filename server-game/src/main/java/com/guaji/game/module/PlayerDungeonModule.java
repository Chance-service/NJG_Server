package com.guaji.game.module;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.guaji.config.ConfigManager;
import org.guaji.log.Log;
import org.guaji.net.protocol.Protocol;
import org.guaji.os.GuaJiTime;

import com.guaji.game.battle.Battlefield;
import com.guaji.game.battle.NewBattleRole;
import com.guaji.game.config.DungeonCfg;
import com.guaji.game.config.FunctionUnlockCfg;
import com.guaji.game.config.SysBasicCfg;
import com.guaji.game.entity.StateEntity;
import com.guaji.game.item.AwardItems;
import com.guaji.game.item.DropItems;
import com.guaji.game.log.BehaviorLogger;
import com.guaji.game.log.BehaviorLogger.Action;
import com.guaji.game.log.BehaviorLogger.Params;
import com.guaji.game.log.BehaviorLogger.Source;
import com.guaji.game.player.Player;
import com.guaji.game.player.PlayerModule;
import com.guaji.game.protocol.Battle.NewBattleFormation;
import com.guaji.game.protocol.Battle.NewBattleLog;
import com.guaji.game.protocol.Battle.NewBattleLog.NewBattleLogInfo;
import com.guaji.game.protocol.Const;
import com.guaji.game.protocol.Dungeon.HPDungeonListInfoRet;
import com.guaji.game.protocol.Dungeon.HPDungeonMapInfo;
import com.guaji.game.protocol.Dungeon.HPDungeonOneKeyRes;
import com.guaji.game.protocol.Dungeon.HPDungeonOneKeyRet;
import com.guaji.game.protocol.HP;
import com.guaji.game.protocol.Player.CLIcheckInfo;
import com.guaji.game.protocol.Player.RoleInfo;
import com.guaji.game.protocol.Status;
import com.guaji.game.util.BuilderUtil;
import com.guaji.game.util.GsConst;
import com.guaji.game.util.PlayerUtil;
import com.guaji.game.util.TapDBUtil.TapDBSource;

/**
 * 多人副本模块
 */
public class PlayerDungeonModule extends PlayerModule {
	/**
	 * 戰鬥場所
	 */
	private Battlefield battlefield;
	
	public PlayerDungeonModule(Player player) {
		super(player);
		//戰場列隊
		listenProto(HP.code.BATTLE_FORMATION_C);
		listenProto(HP.code.DUNGEON_BATTLE_LOG_C);
		listenProto(HP.code.DUNGEON_LIST_INFO_C);
		
		listenProto(HP.code.DUNGEON_ONEKEY_CLEARANCE_C);
	}
	
	@Override
	public boolean onProtocol(Protocol protocol) {
		
		if (protocol.checkType(HP.code.BATTLE_FORMATION_C)) {
			onChallengeDungeon(protocol.parseProtocol(NewBattleFormation.getDefaultInstance()));
			return true;
		} else if (protocol.checkType(HP.code.DUNGEON_BATTLE_LOG_C)) {
			DungeonCheckLog(protocol.parseProtocol(NewBattleLog.getDefaultInstance()));
			return true;
		} else if (protocol.checkType(HP.code.DUNGEON_LIST_INFO_C)) {
			onDungeonListInfo(protocol);
			return true;
		 } else if (protocol.checkType(HP.code.DUNGEON_ONEKEY_CLEARANCE_C)) {
			 oneKeyClearance(protocol);
			return true;
		}
		
		return super.onProtocol(protocol);
	}
	


	/**
	 * 登录处理
	 */
	@Override
	protected boolean onPlayerLogin() {
		// 玩家登录，活动开启中，推送红点
		//MultiEliteTimeCfg timeCfg = MultiEliteTimeCfg.getCurMultiEliteTimeCfg();
//		HPMultiEliteStatePush.Builder stateBuilder = HPMultiEliteStatePush.newBuilder();
//		if (timeCfg != null) {
//			// 开启红点
//			stateBuilder.setState(1);
//		} else {
//			stateBuilder.setState(0);
//		}
		// 推送所有玩家
//		player.sendProtocol(Protocol.valueOf(HP.code.MULIELTIE_PUSH_STATE_S_VALUE, stateBuilder));
		return super.onPlayerLogin();
	}

	/**
	 * 同步地下城列表
	 */
	private void dungeonListInfoSync() {

		StateEntity stateEntity = player.getPlayerData().getStateEntity();
		HPDungeonListInfoRet.Builder ret = HPDungeonListInfoRet.newBuilder();
		int days = GuaJiTime.getDaysOfWeek();
		// 当天副本ID
		for (int type : DungeonCfg.getAllType()) {
			DungeonCfg MapCfg = DungeonCfg.getMagCfgByStar(type, stateEntity.getDungeonStar(type));
			if (MapCfg == null) {
				continue;
			}
			if (DungeonCfg.getWeekdayOfCfgId(days).contains(MapCfg.getId())) {
				HPDungeonMapInfo.Builder builder = HPDungeonMapInfo.newBuilder();
				builder.setDungeonMapId(MapCfg.getId());
				builder.setDungeonType(type);
				builder.setLeftTimes(SysBasicCfg.getInstance().getDungeonDayFreeTimes()-stateEntity.getDungeonTimes(type));
				builder.setStar(stateEntity.getDungeonStar(type));
				builder.setMaxstar(stateEntity.getDungeonMax(type));
				builder.setOnekey(stateEntity.getDungeonOneKey(type));
				ret.addDungeonInfo(builder);
			}
		}
	
		player.sendProtocol(Protocol.valueOf(HP.code.DUNGEON_LIST_INFO_S_VALUE, ret));
	}
	
	/**
	 * 地下城一鍵通關
	 */
	private void oneKeyClearance(Protocol protocol) {
		
		if (player.getVipLevel() < SysBasicCfg.getInstance().getDungeonOneKeyVIPLV() ) {
			player.sendError(protocol.getType(), Status.error.VIP_NOT_ENOUGH_VALUE);
			return;
		}
		
		HPDungeonOneKeyRet req = protocol.parseProtocol(HPDungeonOneKeyRet.getDefaultInstance());
		int type = req.getType();
		if (!DungeonCfg.getAllType().contains(type)) {
			player.sendError(protocol.getType(), Status.error.PARAMS_INVALID_VALUE);
			return;
		}
		
		StateEntity stateEntity = player.getPlayerData().getStateEntity();
		if (stateEntity == null) {
			player.sendError(protocol.getType(), Status.error.DATA_NOT_FOUND_VALUE);
			return;
		}
		
		int times = stateEntity.getDungeonOneKey(type);
		
		if (times > 0) {
			player.sendError(protocol.getType(), Status.error.ALREADY_GOT_LIMIT_ERROR);
			return;
		}
		
		if (stateEntity.getDungeonStar(type) > DungeonCfg.getMaxStarByType(type)) {
			player.sendError(protocol.getType(), Status.error.ALREADY_GOT_LIMIT_ERROR);
			return;
		}
		
		
		int clearance = stateEntity.getDungeonMax(type) - SysBasicCfg.getInstance().getDungeonOneKeyDecStar();
		
		if ((clearance <= 0) || (stateEntity.getDungeonStar(type) > clearance)) {
			player.sendError(protocol.getType(), Status.error.ALREADY_GOT_LIMIT_ERROR);
			return;
		}
		
		AwardItems awardItems = new AwardItems();
		int days = GuaJiTime.getDaysOfWeek();
		List<Integer> cfgList = new ArrayList<>();
		for (int star = stateEntity.getDungeonStar(type) ; star <= clearance ; star++) {
		
			DungeonCfg mapCfg =  DungeonCfg.getMagCfgByStar(type,star);
		
			if (mapCfg == null){
				player.sendError(protocol.getType(), Status.error.PARAMS_INVALID);
				return;
			}
			
			if (!DungeonCfg.getWeekdayOfCfgId(days).contains(mapCfg.getId())) {
				player.sendError(protocol.getType(), Status.error.PARAMS_INVALID);
				return;
			}
			
			List<DropItems.Item> dropitems = mapCfg.getItems().calcDrop();
			 for (DropItems.Item item : dropitems) {
				 awardItems.addItem(item);
			 }
			 
			 cfgList.add(mapCfg.getId());
		}
		stateEntity.setDungeonStar(type,clearance+1);
		stateEntity.addDungeonOneKey(type);
		stateEntity.notifyUpdate(true);
		awardItems.rewardTakeAffectAndPush(player, Action.DUNGEON_WIPE, 1,TapDBSource.Dungeon_Map,
				Params.valueOf("cfgList",cfgList),
				Params.valueOf("DungeonType",type),
				Params.valueOf("DungeonMax",stateEntity.getDungeonMax(type)),
				Params.valueOf("DungeonStar",stateEntity.getDungeonStar(type))
				);
		
		HPDungeonOneKeyRes.Builder ret = HPDungeonOneKeyRes.newBuilder();
		ret.setType(type);
		ret.setStar(stateEntity.getDungeonStar(type));
		ret.setReward(awardItems.toString());
		ret.setOnekey(stateEntity.getDungeonOneKey(type));
		
		player.sendProtocol(Protocol.valueOf(HP.code.DUNGEON_ONEKEY_CLEARANCE_S, ret));
	}
	
	/**
	 * 地下城列表
	 * 
	 * @param protocol
	 */
	public void onDungeonListInfo(Protocol protocol) {
		
		if (!FunctionUnlockCfg.checkUnlock(player,null,Const.FunctionType.dungeon_Unlock)){
			sendError(protocol.getType(), Status.error.CONDITION_NOT_ENOUGH);
			return;
		}
		
		dungeonListInfoSync();
	}

	/**
	 *地下城進入編隊
	 * @param oneprotocol
	 */
	public void onChallengeDungeon(NewBattleFormation protocol) {
		if (protocol.getBattleType() != GsConst.BattleType.Dungeon) {
			return;
		}
		if (protocol.getType() == 0) { //0.請求敵方編隊資訊
			String mapStr = protocol.getMapId();
			int mapId = mapStr.isEmpty()? 0: Integer.valueOf(mapStr);
			
			DungeonCfg eliteCfg = ConfigManager.getInstance().getConfigByKey(DungeonCfg.class, mapId);
			if (eliteCfg == null) {
				// 副本不存在
				player.sendError(protocol.getType(), Status.error.MULTIELITE_CFG_NULL);
				return;
			}
			StateEntity stateEntity = player.getPlayerData().getStateEntity();
			
			if (eliteCfg.getStar() != stateEntity.getDungeonStar(eliteCfg.getType())) {
				player.sendError(protocol.getType(), Status.error.MULTIELITE_CFG_NULL);
				return;
			}
			
			int days = GuaJiTime.getDaysOfWeek();
			List<Integer> curDayMultiList = DungeonCfg.getWeekdayOfCfgId(days);
						
			if (!curDayMultiList.contains(mapId)) {
				player.sendError(protocol.getType(), Status.error.MULTIELITE_CFG_NULL);
				return;
			}
			
			if (eliteCfg.getBP() > player.getFightValue() ) {
				player.sendError(protocol.getType(), Status.error.FIGHT_VALUE_NOT_ENOUGH);
				return;
			}

			if (eliteCfg.getLimitLevel() > player.getLevel()) {
				// 等级不足
				player.sendError(protocol.getType(), Status.error.MULTI_ELITE_LEVEL_NOT_LIMIT);
				return;
			}

			
			int leftMultiEliteTimes = SysBasicCfg.getInstance().getDungeonDayFreeTimes()-stateEntity.getDungeonTimes(eliteCfg.getType());
			if (leftMultiEliteTimes <= 0) {
				// 剩余副本次数不足
				player.sendError(protocol.getType(), Status.error.NO_MULTIELITE_TIMES);
				return;
			}

			int battleId = GuaJiTime.getSeconds();
			//currLevel = changeLv;
			Map<Integer,NewBattleRole> monsterRoles = new HashMap<Integer,NewBattleRole>();
			List<Integer> monsterIds =  eliteCfg.getMonsterIdList();
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
								String.format("multimap monster config not exist, map: %d, monster: %d", eliteCfg.getId(), monsterId));
					}
				}
				pos++;
			}
			// 开启monster战场
			if (monsterRoles.size() > 0) {
				this.battlefield = new Battlefield();
				this.battlefield.setBattleId(battleId);
				this.battlefield.setm_Defenders(monsterRoles);
				this.battlefield.setBattleType(protocol.getBattleType());
				this.battlefield.setMapId(mapId);
				//..................for client.................
				builder.setType(protocol.getType());
				builder.setBattleId(battleId);
				builder.setBattleType(protocol.getBattleType());
				builder.setMapId(protocol.getMapId());
				sendProtocol(Protocol.valueOf(HP.code.BATTLE_FORMATION_S_VALUE, builder));
			} else {
				sendError(HP.code.BATTLE_FORMATION_S_VALUE, Status.error.PARAMS_INVALID);
			}

		} else if (protocol.getType() == 1) { //1.傳送我方編隊資訊
			int checkid = protocol.getBattleId();
			String mapStr = protocol.getMapId();
			int mapId = mapStr.isEmpty()? 0: Integer.valueOf(mapStr);
			if ((this.battlefield == null) ||(checkid != this.battlefield.getBattleId()) ||(mapId != this.battlefield.getMapId()) ){
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
				builder.setMapId(protocol.getMapId());
				StateEntity stateEntity = player.getPlayerData().getStateEntity();
				DungeonCfg eliteCfg = ConfigManager.getInstance().getConfigByKey(DungeonCfg.class, mapId);
				if (eliteCfg == null) {
					// 副本不存在
					player.sendError(protocol.getType(), Status.error.MULTIELITE_CFG_NULL);
					return;
				}
				if (!this.battlefield.isCost()) {
					stateEntity.addDungeonTimesMap(eliteCfg.getType());
					stateEntity.notifyUpdate(true);
					this.battlefield.setCost(true);
				}
			    sendProtocol(Protocol.valueOf(HP.code.BATTLE_FORMATION_S_VALUE, builder));
			} else {
				sendError(HP.code.BATTLE_FORMATION_S_VALUE, Status.error.PARAMS_INVALID);
			}
		}
	}
	
	/**
	 * 發送戰鬥紀錄給server檢查(多人副本)
	 */
	//@ProtocolHandlerAnno(code = HP.code.MULTI_BATTLE_LOG_C_VALUE)
	private void DungeonCheckLog(NewBattleLog protocol) {
		//NewBattleLog protocol = oneprotocol.parseProtocol(NewBattleLog.getDefaultInstance());
		int resultid = protocol.getResault();
		NewBattleLog.Builder builder = NewBattleLog.newBuilder();
		int battleId = protocol.getBattleId();
		
		if ((this.battlefield == null) || (this.battlefield.getBattleId() != battleId) || (!this.battlefield.getBattleStandby())) {
			sendError(HP.code.DUNGEON_BATTLE_LOG_S_VALUE, Status.error.PARAMS_INVALID);
			return;
		}
		
		if (this.battlefield.getBattleType() != GsConst.BattleType.Dungeon) {
			sendError(HP.code.DUNGEON_BATTLE_LOG_S_VALUE, Status.error.PARAMS_INVALID);
			return;
		}
		
		int total = protocol.getTotleLogId(); //有幾份log
		
		if (total <= 0) {
			sendError(HP.code.DUNGEON_BATTLE_LOG_S_VALUE, Status.error.PARAMS_INVALID);
			return;
		}
		
		int Number =  protocol.getLogId(); // 第幾份Id
		
		if (Number <= 0) {
			sendError(HP.code.DUNGEON_BATTLE_LOG_S_VALUE, Status.error.PARAMS_INVALID);
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
			List <NewBattleLogInfo> battlelogList = protocol.getLogList();
			
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
								DungeonMapWinning();
								Log.debugInfo("MultiMapWinning()_check");
							}
						} else {
							DungeonMapWinning();
							Log.debugInfo("MultiMapWinning()_No_check");
						}
						
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
		sendProtocol(Protocol.valueOf(HP.code.DUNGEON_BATTLE_LOG_S, builder));
	}
	
	/**
	 * 對地下城挑戰勝利
	 */	
	private void DungeonMapWinning() {
		if (battlefield.getBattleFinish()) {
			return;
		}
		int curMapId = battlefield.getMapId();
		DungeonCfg mapCfg = ConfigManager.getInstance().getConfigByKey(DungeonCfg.class, curMapId); ;
		StateEntity stateEntity = player.getPlayerData().getStateEntity();
		AwardItems awardItems = new AwardItems();
		if((mapCfg == null)||(stateEntity == null)) {
			sendError(HP.code.DUNGEON_BATTLE_LOG_C_VALUE, Status.error.PARAMS_INVALID);
			return;
		}
		
		List<DropItems.Item> dropitems = mapCfg.getItems().calcDrop();
		 for (DropItems.Item item : dropitems) {
			 awardItems.addItem(item);
		 }
		
		 //勝利歸還挑戰次數
		stateEntity.decDungeonTimesMap(mapCfg.getType());
		
		// 存儲闖過的最高關卡
		stateEntity.setDungeonMax(mapCfg.getType(),Math.max(stateEntity.getDungeonStar(mapCfg.getType()),stateEntity.getDungeonMax(mapCfg.getType())));
		
		// 勝利推進關卡
		stateEntity.addDungeonStar(mapCfg.getType());
		
		
		
		stateEntity.notifyUpdate(true);
		awardItems.rewardTakeAffectAndPush(player, Action.DUNGEON_WIPE, 1,TapDBSource.Dungeon_Map,
				Params.valueOf("curMapId",curMapId),
				Params.valueOf("DungeonType",mapCfg.getType()),
				Params.valueOf("DungeonMax",stateEntity.getDungeonMax(mapCfg.getType())),
				Params.valueOf("DungeonStar",stateEntity.getDungeonStar(mapCfg.getType()))
				);
		
		BehaviorLogger.log4Service(player, Source.USER_OPERATION, Action.DUNGEON_WIPE,Params.valueOf("type", mapCfg.getType()),
				Params.valueOf("DungeonStar", stateEntity.getDungeonStar(mapCfg.getType())),
				Params.valueOf("DungeonMax", stateEntity.getDungeonMax(mapCfg.getType())),
				Params.valueOf("curMapId", curMapId),
				Params.valueOf("reward",awardItems.toString()));
		
		//QuestEventBus.fireQuestEventOneTime(QuestEventType.DUO_REN_FU_BEN, player.getXid());
		
//		Msg hawkMsg = Msg.valueOf(GsConst.MsgType.DailyQuestMsg.ELITE_MISSION_WIN, GuaJiXID.valueOf(GsConst.ObjType.PLAYER, player.getId()));
//		hawkMsg.pushParam(1);
//		GsApp.getInstance().postMsg(hawkMsg);
		
		battlefield.setBattleFinish(true);

		player.getPlayerData().syncStateInfo();		
	}
}
