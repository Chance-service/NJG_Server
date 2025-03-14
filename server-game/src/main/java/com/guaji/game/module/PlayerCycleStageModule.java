package com.guaji.game.module;


import java.util.ArrayList;
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
import com.guaji.game.config.ActivityStage191Cfg;
import com.guaji.game.config.ActivityStage196Cfg;
import com.guaji.game.config.ActivityTimeCfg;
import com.guaji.game.config.SysBasicCfg;
import com.guaji.game.item.AwardItems;
import com.guaji.game.item.ConsumeItems;
import com.guaji.game.item.DropItems;
import com.guaji.game.item.ItemInfo;
import com.guaji.game.log.BehaviorLogger;
import com.guaji.game.log.BehaviorLogger.Action;
import com.guaji.game.log.BehaviorLogger.Params;
import com.guaji.game.log.BehaviorLogger.Source;
import com.guaji.game.module.activity.activity191.Activity191Status;
import com.guaji.game.module.activity.activity196.Activity196Status;
import com.guaji.game.player.Player;
import com.guaji.game.player.PlayerModule;
import com.guaji.game.protocol.Battle.NewBattleFormation;
import com.guaji.game.protocol.Battle.NewBattleLog;
import com.guaji.game.protocol.Battle.NewBattleLog.NewBattleLogInfo;
import com.guaji.game.protocol.Const;
import com.guaji.game.protocol.Dungeon.HPCycleStageInfo;
import com.guaji.game.protocol.Dungeon.HPCycleStageOneKeyRes;
import com.guaji.game.protocol.Dungeon.HPCycleStageOneKeyRet;
import com.guaji.game.protocol.HP;
import com.guaji.game.protocol.Player.CLIcheckInfo;
import com.guaji.game.protocol.Player.RoleInfo;
import com.guaji.game.protocol.Status;
import com.guaji.game.util.ActivityUtil;
import com.guaji.game.util.BuilderUtil;
import com.guaji.game.util.GsConst;
import com.guaji.game.util.PlayerUtil;
import com.guaji.game.util.TapDBUtil.TapDBSource;

/**
 * 循環活動戰場
 */
public class PlayerCycleStageModule extends PlayerModule {
	/**
	 * 戰鬥場所
	 */
	private Battlefield battlefield;
	
	public PlayerCycleStageModule(Player player) {
		super(player);
		//戰場列隊
		listenProto(HP.code.BATTLE_FORMATION_C);
		listenProto(HP.code.CYCLE_BATTLE_LOG_C);
		listenProto(HP.code.CYCLE_LIST_INFO_C);
		
		listenProto(HP.code.CYCLE_ONEKEY_CLEARANCE_C);
	}
	
	@Override
	public boolean onProtocol(Protocol protocol) {
		
		if (protocol.checkType(HP.code.BATTLE_FORMATION_C)) {
			onChallengeCycle(protocol,protocol.parseProtocol(NewBattleFormation.getDefaultInstance()));
			return true;
		
		} else if (protocol.checkType(HP.code.CYCLE_BATTLE_LOG_C)) {
			CycleStageCheckLog(protocol,protocol.parseProtocol(NewBattleLog.getDefaultInstance()));
			return true;
		} else if (protocol.checkType(HP.code.CYCLE_LIST_INFO_C)) {
			onCycleInfo(protocol);
			return true;
		 } else if (protocol.checkType(HP.code.CYCLE_ONEKEY_CLEARANCE_C)) {
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
		return super.onPlayerLogin();
	}

	/**
	 * 同步循環活動
	 */
	private void CycleInfoSync(ActivityTimeCfg timeConfig,boolean original) {
		
		HPCycleStageInfo.Builder ret = HPCycleStageInfo.newBuilder();
		int activityId = timeConfig.getActivityId();
		int stageId = timeConfig.getStageId();
		
		if (original) {
			Activity191Status status = ActivityUtil.getActivityStatus(player.getPlayerData(), activityId, stageId, Activity191Status.class);
			ret.setPassId(status.getPassStage());
			ret.setItem(SysBasicCfg.getInstance().getCycleStageItemCost());

		} else {
			Activity196Status status = ActivityUtil.getActivityStatus(player.getPlayerData(), activityId, stageId, Activity196Status.class);
			ret.setPassId(status.getPassStage());
			ret.setItem(SysBasicCfg.getInstance().getCycleStageItemCost2());
		}
		ret.setStarTime(timeConfig.getlStartTime());
		ret.setLeftTime(timeConfig.calcActivitySurplusTime());

		player.sendProtocol(Protocol.valueOf(HP.code.CYCLE_LIST_INFO_S, ret));
	}
	
	/**
	 * 掃蕩按鈕
	 */
	private void oneKeyClearance(Protocol protocol) {
		
		int activityId = Const.ActivityId.ACTIVITY191_CycleStage_VALUE;
		ActivityTimeCfg timeConfig = ActivityUtil.getCurActivityTimeCfg(activityId);
		boolean original = false;
		if (timeConfig == null) {
			// 活动已关闭
			ActivityUtil.CycleStageClearItem(player);
			
			activityId = Const.ActivityId.ACTIVITY191_CycleStage_VALUE;
			timeConfig = ActivityUtil.getCurActivityTimeCfg(activityId);
			original = false;
			if (timeConfig == null) {
				ActivityUtil.CycleStageClearItem2(player);
				player.sendError(protocol.getType(), Status.error.ACTIVITY_CLOSE);
				return ;
			}

		} else {
			original = true;
		}
		
		HPCycleStageOneKeyRet requst = protocol.parseProtocol(HPCycleStageOneKeyRet.getDefaultInstance());
		
		int mapId = requst.getMapId();
		int count = requst.getCount();
		
		if (count <= 0) {
			player.sendError(protocol.getType(), Status.error.PARAMS_INVALID_VALUE);
			return;
		}
		
		AwardItems awardItems = new AwardItems();
		Action cycle_action = Action.NULL;
		if (original) {
			ActivityStage191Cfg activityCfg = ConfigManager.getInstance().getConfigByKey(ActivityStage191Cfg.class, mapId);
			if (activityCfg == null) {
				player.sendError(protocol.getType(), Status.error.CONFIG_ERROR_VALUE);
				return;
			}
			
			if (activityCfg.getReplay() != 1) {
				// 非可掃蕩地圖
				player.sendError(protocol.getType(), Status.error.PARAMS_INVALID_VALUE);
				return;
			}
			
			int stageId = timeConfig.getStageId();
			Activity191Status status = ActivityUtil.getActivityStatus(player.getPlayerData(), activityId, stageId, Activity191Status.class);
			int passId = status.getPassStage();
			
			if (mapId > passId) {
				// 還未挑戰通過該地圖
				player.sendError(protocol.getType(), Status.error.PARAMS_INVALID_VALUE);
				return;
			}
			
			for (int i = 1 ; i <= count ; i++) {
				List<DropItems.Item> dropitems = activityCfg.getItems().calcDrop();
				for (DropItems.Item item : dropitems) {
					awardItems.addItem(item);
				}
			}
			cycle_action = Action.ACTIVITY191_CycleStage_WIPE;
		} else {
			ActivityStage196Cfg activityCfg = ConfigManager.getInstance().getConfigByKey(ActivityStage196Cfg.class, mapId);
			if (activityCfg == null) {
				player.sendError(protocol.getType(), Status.error.CONFIG_ERROR_VALUE);
				return;
			}
			
			if (activityCfg.getReplay() != 1) {
				// 非可掃蕩地圖
				player.sendError(protocol.getType(), Status.error.PARAMS_INVALID_VALUE);
				return;
			}
			
			int stageId = timeConfig.getStageId();
			Activity196Status status = ActivityUtil.getActivityStatus(player.getPlayerData(), activityId, stageId, Activity196Status.class);
			int passId = status.getPassStage();
			
			if (mapId > passId) {
				// 還未挑戰通過該地圖
				player.sendError(protocol.getType(), Status.error.PARAMS_INVALID_VALUE);
				return;
			}
			for (int i = 1 ; i <= count ; i++) {
				List<DropItems.Item> dropitems = activityCfg.getItems().calcDrop();
				for (DropItems.Item item : dropitems) {
					awardItems.addItem(item);
				}
			}
			cycle_action = Action.ACTIVITY196_CycleStage_WIPE;
		}
						
		ConsumeItems consumeItems = ConsumeItems.valueOf();
		ItemInfo itemInfo = null;
		if (original) {
			itemInfo = ItemInfo.valueOf(SysBasicCfg.getInstance().getCycleStageItemCost());
		} else {
			itemInfo = ItemInfo.valueOf(SysBasicCfg.getInstance().getCycleStageItemCost2());
		}
		itemInfo.setQuantity(count);
		List<ItemInfo> itemList = new ArrayList<ItemInfo>();
		itemList.add(itemInfo);
		boolean isAdd = false ;
		isAdd = consumeItems.addConsumeInfo(player.getPlayerData(),itemList);
		
		if (isAdd && consumeItems.checkConsume(player)) {
			if(!consumeItems.consumeTakeAffect(player, cycle_action)) {
				player.sendError(protocol.getType(),Status.error.ITEM_NOT_ENOUGH_VALUE); 
				return; 
			} 
		} else {
			player.sendError(protocol.getType(),Status.error.ITEM_NOT_ENOUGH_VALUE); 
			return; 
		}
		
			
		awardItems.rewardTakeAffectAndPush(player, cycle_action, 1,TapDBSource.Cycle_Stage,
				Params.valueOf("mapId",mapId),
				Params.valueOf("count",count)
				);
		
		Msg hawkMsg = Msg.valueOf(GsConst.MsgType.DailyQuestMsg.CYCLE_STAGE_JOINTIMES,
				GuaJiXID.valueOf(GsConst.ObjType.PLAYER, player.getId()));
		hawkMsg.pushParam(1);
		GsApp.getInstance().postMsg(hawkMsg);
		
		HPCycleStageOneKeyRes.Builder ret = HPCycleStageOneKeyRes.newBuilder();
		ret.setReward(awardItems.toString());

		
		player.sendProtocol(Protocol.valueOf(HP.code.CYCLE_ONEKEY_CLEARANCE_S, ret));
	}
	
	/**
	 * 循環活動資訊
	 * 
	 * @param protocol
	 */
	public void onCycleInfo(Protocol protocol) {
		// 检测活动是否开放
		int activityId = Const.ActivityId.ACTIVITY191_CycleStage_VALUE;
		ActivityTimeCfg timeConfig = ActivityUtil.getCurActivityTimeCfg(activityId);
		boolean original = false;
		if (timeConfig == null) {
			ActivityUtil.CycleStageClearItem(player);
			
			activityId = Const.ActivityId.ACTIVITY196_CycleStage_Part2_VALUE;
			timeConfig = ActivityUtil.getCurActivityTimeCfg(activityId);
			
			if (timeConfig == null) {
				ActivityUtil.CycleStageClearItem2(player);
				player.sendError(protocol.getType(), Status.error.ACTIVITY_CLOSE);
				return ;
			}
			
			original = false;
			int stageId = timeConfig.getStageId();
			Activity196Status status = ActivityUtil.getActivityStatus(player.getPlayerData(), activityId, stageId, Activity196Status.class);
			
			if (status == null) {
				player.sendError(protocol.getType(), Status.error.DATA_NOT_FOUND_VALUE);
				return ;
			}
			
			ActivityUtil.CycleStageInitItem2(player, timeConfig, status);

		} else {
			original = true;
			int stageId = timeConfig.getStageId();
			Activity191Status status = ActivityUtil.getActivityStatus(player.getPlayerData(), activityId, stageId, Activity191Status.class);
			
			if (status == null) {
				player.sendError(protocol.getType(), Status.error.DATA_NOT_FOUND_VALUE);
				return ;
			}
			
			ActivityUtil.CycleStageInitItem(player, timeConfig, status);
		
		}
		
		CycleInfoSync(timeConfig,original);
	}

	/**
	 *	循環活動地下城進入編隊
	 * @param oneprotocol
	 */
	public void onChallengeCycle(Protocol protocol,NewBattleFormation Battleprotocol) {
		if (Battleprotocol.getBattleType() != GsConst.BattleType.CycleActivity) {
			return;
		}
		
		// 检测活动是否开放
		int passId = 0;
		boolean original = false;
		int activityId = Const.ActivityId.ACTIVITY191_CycleStage_VALUE;
		ActivityTimeCfg timeConfig = ActivityUtil.getCurActivityTimeCfg(activityId);
		if (timeConfig == null) {
			// 活动已关闭
			if (timeConfig == null) {
				ActivityUtil.CycleStageClearItem(player);
			}
			
			activityId = Const.ActivityId.ACTIVITY196_CycleStage_Part2_VALUE;
			timeConfig = ActivityUtil.getCurActivityTimeCfg(activityId);
			if (timeConfig == null) {
				ActivityUtil.CycleStageClearItem2(player);
				player.sendError(protocol.getType(), Status.error.ACTIVITY_CLOSE);
				return ;
			}
			original = false;
			int stageId = timeConfig.getStageId();
			Activity196Status status = ActivityUtil.getActivityStatus(player.getPlayerData(), activityId, stageId, Activity196Status.class);
			passId = status.getPassStage();
		} else {
			original = true;
			int stageId = timeConfig.getStageId();
			Activity191Status status = ActivityUtil.getActivityStatus(player.getPlayerData(), activityId, stageId, Activity191Status.class);
			
			if (status == null) {
				player.sendError(protocol.getType(), Status.error.DATA_NOT_FOUND_VALUE);
				return ;
			}
			passId = status.getPassStage();
		}
		
		if (Battleprotocol.getType() == 0) { //0.請求敵方編隊資訊
			String mapStr = Battleprotocol.getMapId();
			int mapId = mapStr.isEmpty()? 0: Integer.valueOf(mapStr);
			
			if ((passId == 0)&&(mapId != 1)) {
				// 都沒有打只能打第一關
				player.sendError(protocol.getType(), Status.error.PARAMS_INVALID_VALUE);
				return;
			}
			
			List<Integer> MonsterList = new ArrayList<>();
			
			if (original) {
				ActivityStage191Cfg activityCfg = ConfigManager.getInstance().getConfigByKey(ActivityStage191Cfg.class, mapId);
				if (activityCfg == null) {
					// 副本不存在
					player.sendError(protocol.getType(), Status.error.CONFIG_NOT_FOUND_VALUE);
					return;
				}
				
				MonsterList = activityCfg.getMonsterIdList();
				
				long currentTime = GuaJiTime.getMillisecond();
				
				long limitTime = timeConfig.getlStartTime() + (activityCfg.getUnlockTime() * 3600*1000);
				
				if (currentTime < limitTime) {
					player.sendError(protocol.getType(), Status.error.CONFIG_NOT_FOUND_VALUE);
					return;
				}
				
				if (passId > 0) {
					ActivityStage191Cfg passCfg = ConfigManager.getInstance().getConfigByKey(ActivityStage191Cfg.class, passId);
					if (passCfg == null) {
						// 副本不存在
						player.sendError(protocol.getType(), Status.error.CONFIG_NOT_FOUND_VALUE);
						return;
					}
					
					if (passCfg.getNextid() == 0) {
						// 已打到最後一關 , 不能往下打
						if (mapId > passId) {
							player.sendError(protocol.getType(), Status.error.PARAMS_INVALID_VALUE);
							return;
						}
					}
				}
				
				if (mapId <= passId) {
					// 非可再次挑戰地圖
					if (activityCfg.getReplay() != 1) {
						player.sendError(protocol.getType(), Status.error.PARAMS_INVALID_VALUE);
						return;
					}
				}
			} else {
				ActivityStage196Cfg activityCfg = ConfigManager.getInstance().getConfigByKey(ActivityStage196Cfg.class, mapId);
				if (activityCfg == null) {
					// 副本不存在
					player.sendError(protocol.getType(), Status.error.CONFIG_NOT_FOUND_VALUE);
					return;
				}
				
				MonsterList = activityCfg.getMonsterIdList();
				
				long currentTime = GuaJiTime.getMillisecond();
				
				long limitTime = timeConfig.getlStartTime() + (activityCfg.getUnlockTime() * 3600*1000);
				
				if (currentTime < limitTime) {
					player.sendError(protocol.getType(), Status.error.CONFIG_NOT_FOUND_VALUE);
					return;
				}
				
				if (passId > 0) {
					ActivityStage196Cfg passCfg = ConfigManager.getInstance().getConfigByKey(ActivityStage196Cfg.class, passId);
					if (passCfg == null) {
						// 副本不存在
						player.sendError(protocol.getType(), Status.error.CONFIG_NOT_FOUND_VALUE);
						return;
					}
					
					if (passCfg.getNextid() == 0) {
						// 已打到最後一關 , 不能往下打
						if (mapId > passId) {
							player.sendError(protocol.getType(), Status.error.PARAMS_INVALID_VALUE);
							return;
						}
					}
				}
				
				if (mapId <= passId) {
					// 非可再次挑戰地圖
					if (activityCfg.getReplay() != 1) {
						player.sendError(protocol.getType(), Status.error.PARAMS_INVALID_VALUE);
						return;
					}
				}
			}
			
			ConsumeItems consumeItems = ConsumeItems.valueOf();
			ItemInfo itemInfo = null;
			if (original) {
				itemInfo = ItemInfo.valueOf(SysBasicCfg.getInstance().getCycleStageItemCost());			
			} else {
				itemInfo = ItemInfo.valueOf(SysBasicCfg.getInstance().getCycleStageItemCost2());
			}
			List<ItemInfo> itemList = new ArrayList<ItemInfo>();
			itemList.add(itemInfo);
			boolean isAdd = false ;
			isAdd = consumeItems.addConsumeInfo(player.getPlayerData(),itemList);
			
			if (isAdd && consumeItems.checkConsume(player)) {
				// 先檢查有沒有物品
			} else {
				player.sendError(protocol.getType(),Status.error.ITEM_NOT_ENOUGH_VALUE); 
				return; 
			}
						
			int battleId = GuaJiTime.getSeconds();
			//currLevel = changeLv;
			Map<Integer,NewBattleRole> monsterRoles = new HashMap<Integer,NewBattleRole>();
			List<Integer> monsterIds =  MonsterList;
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
								String.format("multimap monster config not exist, map: %d, monster: %d", mapId, monsterId));
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
				this.battlefield.setMapId(mapId);
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
			int mapId = mapStr.isEmpty()? 0: Integer.valueOf(mapStr);
			if ((this.battlefield == null) ||(checkid != this.battlefield.getBattleId()) ||(mapId != this.battlefield.getMapId()) ){
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
				
				if (original) {
					ActivityStage191Cfg activityCfg = ConfigManager.getInstance().getConfigByKey(ActivityStage191Cfg.class, mapId);
					if (activityCfg == null) {
						// 副本不存在
						player.sendError(protocol.getType(), Status.error.MULTIELITE_CFG_NULL);
						return;
					}
				} else {
					ActivityStage196Cfg activityCfg = ConfigManager.getInstance().getConfigByKey(ActivityStage196Cfg.class, mapId);
					if (activityCfg == null) {
						// 副本不存在
						player.sendError(protocol.getType(), Status.error.MULTIELITE_CFG_NULL);
						return;
					}
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
	//@ProtocolHandlerAnno(code = HP.code.MULTI_BATTLE_LOG_C_VALUE)
	private void CycleStageCheckLog(Protocol protocol,NewBattleLog Battleprotocol) {
		//NewBattleLog protocol = oneprotocol.parseProtocol(NewBattleLog.getDefaultInstance());
		int resultid = Battleprotocol.getResault();
		NewBattleLog.Builder builder = NewBattleLog.newBuilder();
		int battleId = Battleprotocol.getBattleId();
		
		// 检测活动是否开放
		int activityId = Const.ActivityId.ACTIVITY191_CycleStage_VALUE;
		ActivityTimeCfg timeConfig = ActivityUtil.getCurActivityTimeCfg(activityId);
		boolean original = false;
		if (timeConfig == null) {
			// 活动已关闭
			ActivityUtil.CycleStageClearItem(player);
			
			activityId = Const.ActivityId.ACTIVITY196_CycleStage_Part2_VALUE;
			timeConfig = ActivityUtil.getCurActivityTimeCfg(activityId);
			if (timeConfig == null) {
				ActivityUtil.CycleStageClearItem2(player);
				player.sendError(protocol.getType(), Status.error.ACTIVITY_CLOSE);
				return ;
			}
			original = false;
			int stageId = timeConfig.getStageId();
			Activity196Status status = ActivityUtil.getActivityStatus(player.getPlayerData(), activityId, stageId, Activity196Status.class);
			
			if (status == null) {
				player.sendError(protocol.getType(), Status.error.DATA_NOT_FOUND_VALUE);
				return ;
			}

		} else {
			original = true;
			int stageId = timeConfig.getStageId();
			Activity191Status status = ActivityUtil.getActivityStatus(player.getPlayerData(), activityId, stageId, Activity191Status.class);
			
			if (status == null) {
				player.sendError(protocol.getType(), Status.error.DATA_NOT_FOUND_VALUE);
				return ;
			}
		
		}
		
		if ((this.battlefield == null) || (this.battlefield.getBattleId() != battleId) || (!this.battlefield.getBattleStandby())) {
			sendError(protocol.getType(), Status.error.PARAMS_INVALID);
			return;
		}
		
		if (this.battlefield.getBattleType() != GsConst.BattleType.CycleActivity) {
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
								CycleStageWinning(protocol,timeConfig,original);
								Log.debugInfo("CycleStageWinning()_check");
							}
						} else {
							CycleStageWinning(protocol,timeConfig,original);
							Log.debugInfo("CycleStageWinning()_No_check");
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
		sendProtocol(Protocol.valueOf(HP.code.CYCLE_BATTLE_LOG_S, builder));
	}
	
	/**
	 * 對循環活動挑戰勝利
	 */	
	private void CycleStageWinning(Protocol protocol,ActivityTimeCfg timeConfig,boolean original) {
		if (battlefield.getBattleFinish()) {
			return;
		}
		
		int curMapId = battlefield.getMapId();
		List<DropItems.Item> dropitems = null;
		int activityId = timeConfig.getActivityId();
		int stageId = timeConfig.getStageId();
		int passStage = 0;
		int newPass = 0;
		Action cycle_action = Action.NULL;
		if (original) {
			ActivityStage191Cfg mapCfg = ConfigManager.getInstance().getConfigByKey(ActivityStage191Cfg.class, curMapId);
			
			Activity191Status status = ActivityUtil.getActivityStatus(player.getPlayerData(), activityId, stageId, Activity191Status.class);
			
			if((mapCfg == null)||(status == null)) {
				sendError(protocol.getType(), Status.error.PARAMS_INVALID);
				return;
			}
			cycle_action = Action.ACTIVITY191_CycleStage;
			passStage = status.getPassStage();
			newPass = status.getPassStage();
			dropitems = mapCfg.getItems().calcDrop();
		} else {
			ActivityStage196Cfg mapCfg = ConfigManager.getInstance().getConfigByKey(ActivityStage196Cfg.class, curMapId);
			
			Activity196Status status = ActivityUtil.getActivityStatus(player.getPlayerData(), activityId, stageId, Activity196Status.class);
			
			if((mapCfg == null)||(status == null)) {
				sendError(protocol.getType(), Status.error.PARAMS_INVALID);
				return;
			}
			cycle_action = Action.ACTIVITY196_CycleStage;
			passStage = status.getPassStage();
			newPass = status.getPassStage();
			dropitems = mapCfg.getItems().calcDrop();
		}
		
		AwardItems awardItems = new AwardItems();

		 for (DropItems.Item item : dropitems) {
			 awardItems.addItem(item);
		 }
		 
		ConsumeItems consumeItems = ConsumeItems.valueOf();
		ItemInfo itemInfo = null;
		if (original) {
			itemInfo = ItemInfo.valueOf(SysBasicCfg.getInstance().getCycleStageItemCost());
		} else {
			itemInfo = ItemInfo.valueOf(SysBasicCfg.getInstance().getCycleStageItemCost2());
		}			
		List<ItemInfo> itemList = new ArrayList<ItemInfo>();
		itemList.add(itemInfo);
		boolean isAdd = false ;
		isAdd = consumeItems.addConsumeInfo(player.getPlayerData(),itemList);
		
		if (isAdd && consumeItems.checkConsume(player)) {
			if(!consumeItems.consumeTakeAffect(player, cycle_action)) {
				player.sendError(protocol.getType(),Status.error.ITEM_NOT_ENOUGH_VALUE); 
				return; 
			} 
		} else {
			player.sendError(protocol.getType(),Status.error.ITEM_NOT_ENOUGH_VALUE); 
			return; 
		}
		
		if (curMapId > passStage) {
			if (original) {
				Activity191Status status = ActivityUtil.getActivityStatus(player.getPlayerData(), activityId, stageId, Activity191Status.class);
				status.setPassStage(curMapId);
				newPass = status.getPassStage();
			} else {
				Activity196Status status = ActivityUtil.getActivityStatus(player.getPlayerData(), activityId, stageId, Activity196Status.class);
				status.setPassStage(curMapId);
				newPass = status.getPassStage();
			}
		}
		
		player.getPlayerData().updateActivity(activityId,stageId);

		
		awardItems.rewardTakeAffectAndPush(player, cycle_action, 1,TapDBSource.Cycle_Stage,
				Params.valueOf("curMapId",curMapId),
				Params.valueOf("PassId",newPass)
//				Params.valueOf("DungeonType",mapCfg.getType()),
//				Params.valueOf("DungeonMax",stateEntity.getDungeonMax(mapCfg.getType())),
//				Params.valueOf("DungeonStar",stateEntity.getDungeonStar(mapCfg.getType()))
				);
		
		BehaviorLogger.log4Service(player, Source.USER_OPERATION, Action.CYCLESTAGE_WIPE,//Params.valueOf("type", mapCfg.getType()),
				Params.valueOf("curMapId", curMapId),
				Params.valueOf("PassId",newPass),
				Params.valueOf("reward",awardItems.toString()));
		
		//QuestEventBus.fireQuestEventOneTime(QuestEventType.DUO_REN_FU_BEN, player.getXid());
		
		Msg hawkMsg = Msg.valueOf(GsConst.MsgType.DailyQuestMsg.CYCLE_STAGE_JOINTIMES,
				GuaJiXID.valueOf(GsConst.ObjType.PLAYER, player.getId()));
		hawkMsg.pushParam(1);
		GsApp.getInstance().postMsg(hawkMsg);
		
		battlefield.setBattleFinish(true);

		player.getPlayerData().syncStateInfo();		
	}
}
