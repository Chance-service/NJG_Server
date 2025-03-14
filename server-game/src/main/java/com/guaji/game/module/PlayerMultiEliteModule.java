package com.guaji.game.module;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.guaji.annotation.ProtocolHandlerAnno;
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
import com.guaji.game.config.MultiMapCfg;
import com.guaji.game.config.SysBasicCfg;
import com.guaji.game.entity.StateEntity;
import com.guaji.game.item.AwardItems;
import com.guaji.game.item.DropItems;
import com.guaji.game.log.BehaviorLogger;
import com.guaji.game.log.BehaviorLogger.Action;
import com.guaji.game.log.BehaviorLogger.Params;
import com.guaji.game.log.BehaviorLogger.Source;
import com.guaji.game.module.quest.QuestEventBus;
import com.guaji.game.player.Player;
import com.guaji.game.player.PlayerModule;
import com.guaji.game.protocol.Battle.NewBattleFormation;
import com.guaji.game.protocol.Battle.NewBattleLog;
import com.guaji.game.protocol.Battle.NewBattleLog.NewBattleLogInfo;
import com.guaji.game.protocol.Const.QuestEventType;
import com.guaji.game.protocol.Const;
import com.guaji.game.protocol.HP;
import com.guaji.game.protocol.MultiElite.HPMultiEliteGetAwardReq;
import com.guaji.game.protocol.MultiElite.HPMultiEliteGetAwardRes;
import com.guaji.game.protocol.MultiElite.HPMultiEliteListInfoRet;
import com.guaji.game.protocol.MultiElite.HPMultiEliteStatePush;
import com.guaji.game.protocol.MultiElite.HPMultiElteMapInfo;
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
public class PlayerMultiEliteModule extends PlayerModule {
	/**
	 * 戰鬥場所
	 */
	private Battlefield battlefield;
	
	public PlayerMultiEliteModule(Player player) {
		super(player);
		//戰場列隊
		listenProto(HP.code.BATTLE_FORMATION_C);
		listenProto(HP.code.MULTI_BATTLE_LOG_C);
		listenProto(HP.code.MULTIELITE_LIST_INFO_C);
		
		listenProto(HP.code.MULTI_BATTLE_AWARD_C);
	}
	
	@Override
	public boolean onProtocol(Protocol protocol) {
		
		if (protocol.checkType(HP.code.BATTLE_FORMATION_C)) {
			onChallengeMultiElite(protocol.parseProtocol(NewBattleFormation.getDefaultInstance()));
			return true;
		} else if (protocol.checkType(HP.code.MULTI_BATTLE_LOG_C)) {
			MultiEliteCheckLog(protocol.parseProtocol(NewBattleLog.getDefaultInstance()));
			return true;
		} else if (protocol.checkType(HP.code.MULTIELITE_LIST_INFO_C)) {
			onMultiEliteListInfo(protocol);
			return true;
		 } else if (protocol.checkType(HP.code.MULTI_BATTLE_AWARD_C)) {
			 onMultiEliteGetAward(protocol);
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
		HPMultiEliteStatePush.Builder stateBuilder = HPMultiEliteStatePush.newBuilder();
//		if (timeCfg != null) {
//			// 开启红点
//			stateBuilder.setState(1);
//		} else {
			stateBuilder.setState(0);
//		}
		// 推送所有玩家
		player.sendProtocol(Protocol.valueOf(HP.code.MULIELTIE_PUSH_STATE_S_VALUE, stateBuilder));
		return super.onPlayerLogin();
	}

	/**
	 * 同步多人副本列表
	 */
	private void multiEliteListInfoSync() {

		StateEntity stateEntity = player.getPlayerData().getStateEntity();
		HPMultiEliteListInfoRet.Builder ret = HPMultiEliteListInfoRet.newBuilder();
		int days = GuaJiTime.getDaysOfWeek();
		// 当天副本ID
		for (int type : MultiMapCfg.getAllType()) {
			MultiMapCfg MapCfg = MultiMapCfg.getMagCfgByStar(type, stateEntity.getChallengeStar(type));
			if (MultiMapCfg.getWeekdayOfCfgId(days).contains(MapCfg.getId())) {
				HPMultiElteMapInfo.Builder builder = HPMultiElteMapInfo.newBuilder();
				builder.setMultiEliteMapId(MapCfg.getId());
				builder.setMultiEliteType(type);
				builder.setLeftTimes(SysBasicCfg.getInstance().getMultiEliteDayFreeTimes()-stateEntity.getMultiGiftTimesByType(type));
				builder.setStar(stateEntity.getChallengeStar(type));
				ret.addMultiEliteInfo(builder);
			}
		}
	
		
		player.sendProtocol(Protocol.valueOf(HP.code.MULTIELITE_LIST_INFO_S_VALUE, ret));
	}
	
	/**
	 * 領取副本獎勵
	 */
	private void onMultiEliteGetAward(Protocol protocol) {
		HPMultiEliteGetAwardReq req = protocol.parseProtocol(HPMultiEliteGetAwardReq.getDefaultInstance());
		int type = req.getType();
		if (!MultiMapCfg.getAllType().contains(type)) {
			player.sendError(protocol.getType(), Status.error.PARAMS_INVALID_VALUE);
			return;
		}
		StateEntity stateEntity = player.getPlayerData().getStateEntity();
		if (stateEntity == null) {
			player.sendError(protocol.getType(), Status.error.DATA_NOT_FOUND_VALUE);
			return;
		}
		
		int times = stateEntity.getMultiGiftTimesByType(type);
		
		if (times >= SysBasicCfg.getInstance().getMultiEliteDayFreeTimes()) {
			player.sendError(protocol.getType(), Status.error.ALREADY_GOT_LIMIT_ERROR);
			return;
		}
		
		if (stateEntity.getChallengeStar(type) == 1) {
			player.sendError(protocol.getType(), Status.error.PARAMS_INVALID_VALUE);
			return;
		}
		
		MultiMapCfg mapCfg = MultiMapCfg.getMagCfgByStar(type, stateEntity.getChallengeStar(type));
		
		AwardItems awardItems = new AwardItems();
		
		if (mapCfg == null){
			player.sendError(protocol.getType(), Status.error.PARAMS_INVALID);
			return;
		}
		
		List<DropItems.Item> dropitems = mapCfg.getItems().calcDrop();
		 for (DropItems.Item item : dropitems) {
			 awardItems.addItem(item);
		 }
		 
		stateEntity.addMultiGiftInfoMap(type);;
		
		stateEntity.notifyUpdate(true);
		awardItems.rewardTakeAffectAndPush(player, Action.ELITE_MAP_WIPE, 1 ,TapDBSource.ELITE_Map,
				Params.valueOf("MultiMap", mapCfg.getId()));
		
		Msg hawkMsg = Msg.valueOf(GsConst.MsgType.DailyQuestMsg.ELITE_MISSION_WIN, GuaJiXID.valueOf(GsConst.ObjType.PLAYER, player.getId()));
		hawkMsg.pushParam(1);
		GsApp.getInstance().postMsg(hawkMsg);
		
		BehaviorLogger.log4Service(player, Source.USER_OPERATION, Action.ELITE_MAP_WIPE,Params.valueOf("ChallengeStar", stateEntity.getChallengeStar(type)),
				Params.valueOf("mapCfg", mapCfg.getId()),
				Params.valueOf("times", times),
				Params.valueOf("reward",awardItems.toString()));
		
		HPMultiEliteGetAwardRes.Builder ret = HPMultiEliteGetAwardRes.newBuilder();
		ret.setType(type);
		ret.setReward(awardItems.toString());
		
		player.sendProtocol(Protocol.valueOf(HP.code.MULTI_BATTLE_AWARD_S, ret));
	}
	
	

	/**
	 * 同步房间成员消息响应
	 */
//	@MessageHandlerAnno(code = GsConst.MsgType.SYNC_ROOM_MEMBER_LIST)


	/**
	 * 被踢出消息响应
	 * 
	 * @param msg
	 */
//	@MessageHandlerAnno(code = GsConst.MsgType.KICK_OUT_IN_ROOM_PLAYER)


	/**
	 * 战斗状态同步
	 */
//	@MessageHandlerAnno(code = GsConst.MsgType.BATTLE_STATE_CHANGE)


	/**
	 * 战斗开始通知队友 == 队员准备完毕通知队长
	 */
//	@MessageHandlerAnno(code = GsConst.MsgType.BATTLE_START_NOTICE)


	/**
	 * 战斗结算消息响应
	 */
//	@MessageHandlerAnno(code = GsConst.MsgType.MULTI_ELITE_BATTLE_SETTLE)

	
	/**
	 * 购买多人副本次数
	 * 
	 * @param protocol
	 */
//	@ProtocolHandlerAnno(code = HP.code.BUY_MULTIELITE_TIMES_C_VALUE)
	
	/**
	 * 多人副本列表
	 * 
	 * @param protocol
	 */
//	@ProtocolHandlerAnno(code = HP.code.MULTIELITE_LIST_INFO_C_VALUE)
	public void onMultiEliteListInfo(Protocol protocol) {
		
		if (!FunctionUnlockCfg.checkUnlock(player,null,Const.FunctionType.multiMap_Unlock)){
			sendError(protocol.getType(), Status.error.CONDITION_NOT_ENOUGH);
			return;
		}
		
		multiEliteListInfoSync();
	}

	/**
	 * 单个副本房间列表
	 * 
	 * @param protocol
	 */
//	@ProtocolHandlerAnno(code = HP.code.ELITE_ROOM_LIST_INFO_C_VALUE)

	/**
	 * 创建房间
	 * 
	 * @param protocol
	 */
//	@ProtocolHandlerAnno(code = HP.code.CREATE_ELITE_ROOM_C_VALUE)

	/**
	 * 加入/进入 房间
	 * 
	 * @param protocol
	 */
//	@ProtocolHandlerAnno(code = HP.code.JOIN_MULTIELITE_ROOM_C_VALUE)
	
	/**
	 * 准备
	 * 
	 * @param protocol
	 */
//	@ProtocolHandlerAnno(code = HP.code.MULTIELTIE_SETOUT_C_VALUE)

	/**
	 * 同步房间队员信息
	 * 
	 * @param protocol
	 */
//	@ProtocolHandlerAnno(code = HP.code.MULIELTIE_ROOM_INFO_C_VALUE)

	/**
	 * 修改队伍名称
	 * 
	 * @param protocol
	 */
//	@ProtocolHandlerAnno(code = HP.code.MULIELTIE_CHANGE_NAME_C_VALUE)

	/**
	 * 修改房间战力
	 * 
	 * @param protocol
	 */
//	@ProtocolHandlerAnno(code = HP.code.MULIELTIE_CHANGE_FIGHT_VALUE_C_VALUE)

	/**
	 * 踢出玩家
	 * 
	 * @param protocol
	 */
//	@ProtocolHandlerAnno(code = HP.code.KICK_MULTIELITE_ROOM_MEMBER_C_VALUE)

	/**
	 * 退出房间
	 * 
	 * @param protocol
	 */
//	@ProtocolHandlerAnno(code = HP.code.EXIT_MULTIELITE_ROOM_C_VALUE)

	/**
	 * 开启战斗倒计时 / 取消开始
	 * 
	 * @param protocol
	 */
//	@ProtocolHandlerAnno(code = HP.code.SET_MULTIELITE_BATTLE_STATE_C_VALUE)

	/**
	 * 重置上次多人副本战报id
	 * 
	 * @param protocol
	 */
//	@ProtocolHandlerAnno(code = HP.code.BATTLE_END_RESULT_CONFIRM_C_VALUE)


	/**
	 * 邮件查看多人副本战报
	 * 
	 * @param protocol
	 */
//	@ProtocolHandlerAnno(code = HP.code.MAIL_SEE_MULTIELITE_BATTLE_REPORT_C_VALUE)

	/**
	 * 返回在线好友列表
	 * 
	 * @param protocol
	 */
//	@ProtocolHandlerAnno(code = HP.code.MULTIELITE_HIRE_LIST_C_VALUE)

	/**
	 * 邀请好友
	 * 
	 * @param protocol
	 */
//	@ProtocolHandlerAnno(code = HP.code.MULTIELITE_INVATE_C_VALUE)

	/**
	 * 多人副本状态信息
	 * 
	 * @param protocol
	 */
//	@ProtocolHandlerAnno(code = HP.code.MULTI_ELITE_OPEN_TIME_C_VALUE)
	
	/**
	 * 多人副本進入編隊
	 * @param oneprotocol
	 */
	public void onChallengeMultiElite(NewBattleFormation protocol) {
		if (protocol.getBattleType() != GsConst.BattleType.EVENT) {
			return;
		}
		if (protocol.getType() == 0) { //0.請求敵方編隊資訊
			String mapStr = protocol.getMapId();
			int mapId = mapStr.isEmpty()? 0: Integer.valueOf(mapStr);
			
			MultiMapCfg eliteCfg = ConfigManager.getInstance().getConfigByKey(MultiMapCfg.class, mapId);
			if (eliteCfg == null) {
				// 副本不存在
				player.sendError(protocol.getType(), Status.error.MULTIELITE_CFG_NULL);
				return;
			}
			StateEntity stateEntity = player.getPlayerData().getStateEntity();
			
			if (MultiMapCfg.getMaxStarByType(eliteCfg.getType()) == stateEntity.getChallengeStar(eliteCfg.getType())) {
				player.sendError(protocol.getType(), Status.error.MULTIELITE_CFG_NULL);
				return;
			}
			
			List<Integer> curDayMultiList = new ArrayList<>();
			int days = GuaJiTime.getDaysOfWeek();
			for (int type : MultiMapCfg.getAllType()) {
				MultiMapCfg MapCfg = MultiMapCfg.getMagCfgByStar(type, stateEntity.getChallengeStar(type));
				if (MultiMapCfg.getWeekdayOfCfgId(days).contains(MapCfg.getId())) {
					curDayMultiList.add(MapCfg.getId());
				}
			}
			
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
			
			if (eliteCfg.getLimitStage() > player.getPassMapId() ) {
				// 闖關條件不足
				player.sendError(protocol.getType(), Status.error.PASS_MAP_LIMIT_ERROR);
				return;
			}

			
//			int leftMultiEliteTimes = stateEntity.getMultiEliteTimes();
//			if (leftMultiEliteTimes <= 0) {
//				// 剩余副本次数不足
//				player.sendError(protocol.getType(), Status.error.NO_MULTIELITE_TIMES);
//				return;
//			}

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
				builder.setMapId(protocol.getMapId());
			    sendProtocol(Protocol.valueOf(HP.code.BATTLE_FORMATION_S_VALUE, builder));
			} else {
				sendError(HP.code.BATTLE_FORMATION_S_VALUE, Status.error.PARAMS_INVALID);
			}
		}
	}
	
	/**
	 * 發送戰鬥紀錄給server檢查(多人副本)
	 */
	@ProtocolHandlerAnno(code = HP.code.MULTI_BATTLE_LOG_C_VALUE)
	private void MultiEliteCheckLog(NewBattleLog protocol) {
		//NewBattleLog protocol = oneprotocol.parseProtocol(NewBattleLog.getDefaultInstance());
		int resultid = protocol.getResault();
		NewBattleLog.Builder builder = NewBattleLog.newBuilder();
		int battleId = protocol.getBattleId();
		
		if ((this.battlefield == null) || (this.battlefield.getBattleId() != battleId) || (!this.battlefield.getBattleStandby())) {
			sendError(HP.code.MULTI_BATTLE_LOG_S_VALUE, Status.error.PARAMS_INVALID);
			return;
		}
		
		if (this.battlefield.getBattleType() != GsConst.BattleType.EVENT) {
			sendError(HP.code.MULTI_BATTLE_LOG_S_VALUE, Status.error.PARAMS_INVALID);
			return;
		}
		
		int total = protocol.getTotleLogId(); //有幾份log
		
		if (total <= 0) {
			sendError(HP.code.MULTI_BATTLE_LOG_S_VALUE, Status.error.PARAMS_INVALID);
			return;
		}
		
		int Number =  protocol.getLogId(); // 第幾份Id
		
		if (Number <= 0) {
			sendError(HP.code.MULTI_BATTLE_LOG_S_VALUE, Status.error.PARAMS_INVALID);
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
								MultiMapWinning();
								Log.debugInfo("MultiMapWinning()_check");
							}
						} else {
							MultiMapWinning();
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
		sendProtocol(Protocol.valueOf(HP.code.MULTI_BATTLE_LOG_S, builder));
	}
	
	/**
	 * 對多人地圖挑戰勝利
	 */	
	private void MultiMapWinning() {
		if (battlefield.getBattleFinish()) {
			return;
		}
		int curMapId = battlefield.getMapId();
		MultiMapCfg mapCfg = ConfigManager.getInstance().getConfigByKey(MultiMapCfg.class, curMapId); ;
		StateEntity stateEntity = player.getPlayerData().getStateEntity();
//		AwardItems awardItems = new AwardItems();
//		if((mapCfg == null)||(stateEntity == null)) {
//			sendError(HP.code.MULTI_BATTLE_LOG_S_VALUE, Status.error.PARAMS_INVALID);
//			return;
//		}
//		
//		List<DropItems.Item> dropitems = mapCfg.getItems().calcDrop();
//		 for (DropItems.Item item : dropitems) {
//			 awardItems.addItem(item);
//		 }
//		int leftMultiEliteTimes = stateEntity.getMultiEliteTimes();
//		// 扣除次数
//		leftMultiEliteTimes -= 1;
//		stateEntity.setMultiEliteTimes(leftMultiEliteTimes);
		
		stateEntity.addChallengeStarMap(mapCfg.getType());
		
		stateEntity.notifyUpdate(true);
		//awardItems.rewardTakeAffectAndPush(player, Action.ELITE_MAP_WIPE, 1);
		
		QuestEventBus.fireQuestEventOneTime(QuestEventType.DUO_REN_FU_BEN, player.getXid());
		
		battlefield.setBattleFinish(true);

		player.getPlayerData().syncStateInfo();		
	}
}
