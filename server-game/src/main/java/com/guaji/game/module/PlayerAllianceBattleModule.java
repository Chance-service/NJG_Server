package com.guaji.game.module;

import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.guaji.config.ConfigManager;
import org.guaji.msg.Msg;
import org.guaji.net.protocol.Protocol;

import com.guaji.game.protocol.Alliance.AllianceInfo;
import com.guaji.game.protocol.AllianceBattle.AllianceBattleState;
import com.guaji.game.protocol.AllianceBattle.FightGroup;
import com.guaji.game.protocol.AllianceBattle.HPAllianceTeamDetail;
import com.guaji.game.protocol.AllianceBattle.HPAllianceTeamDetailRet;
import com.guaji.game.protocol.AllianceBattle.HPAllianceTeamEnterRet;
import com.guaji.game.protocol.AllianceBattle.HPAllianceTeamFight;
import com.guaji.game.protocol.AllianceBattle.HPAllianceTeamFightReport;
import com.guaji.game.protocol.AllianceBattle.HPAllianceTeamFightReportRet;
import com.guaji.game.protocol.AllianceBattle.HPAllianceTeamJoin;
import com.guaji.game.protocol.AllianceBattle.HPAllianceTeamSave;
import com.guaji.game.protocol.AllianceBattle.HPAllianceTeamUnit;
import com.guaji.game.protocol.AllianceBattle.HPInspireRet;
import com.guaji.game.protocol.AllianceBattle.HPInvest;
import com.guaji.game.protocol.AllianceBattle.HPInvestRet;
import com.guaji.game.protocol.AllianceBattle.HPLastBattleFightInfoRet;
import com.guaji.game.protocol.Battle.BattleInfo;
import com.guaji.game.protocol.Const.changeType;
import com.guaji.game.config.AllianceBattleInspireCfg;
import com.guaji.game.config.InvestRewardCfg;
import com.guaji.game.entity.AllianceBattleItem;
import com.guaji.game.entity.AllianceEntity;
import com.guaji.game.entity.AllianceFightUnit;
import com.guaji.game.entity.AllianceFightVersus;
import com.guaji.game.entity.PlayerAllianceEntity;
import com.guaji.game.item.ConsumeItems;
import com.guaji.game.log.BehaviorLogger.Action;
import com.guaji.game.manager.AllianceBattleAgainstInfo;
import com.guaji.game.manager.AllianceBattleManager;
import com.guaji.game.manager.AllianceManager;
import com.guaji.game.manager.SnapShotManager;
import com.guaji.game.player.Player;
import com.guaji.game.player.PlayerModule;
import com.guaji.game.util.BuilderUtil;
import com.guaji.game.util.GsConst;
import com.guaji.game.util.ProtoUtil;
import com.guaji.game.protocol.HP;
import com.guaji.game.protocol.Snapshot.PlayerSnapshotInfo;
import com.guaji.game.protocol.Status;

public class PlayerAllianceBattleModule extends PlayerModule {

	public PlayerAllianceBattleModule(Player player) {
		super(player);
		
		listenProto(HP.code.ALLIANCE_BATTLE_ENTER_C_VALUE);
		listenProto(HP.code.ALLIANCE_TEAM_BASIC_INFO_C);
		listenProto(HP.code.ALLIANCE_TEAM_JOIN_INFO_C_VALUE);
		listenProto(HP.code.ALLIANCE_TEAM_DETAIL_INFO_C_VALUE);
		listenProto(HP.code.ALLIANCE_BATTLE_FIGHT_BET_C_VALUE);
		listenProto(HP.code.ALLIANCE_TEAM_SAVE_C_VALUE);
		listenProto(HP.code.ALLIANCE_BATTLE_TEAM_FIGHT_INFO_C);
		listenProto(HP.code.ALLIANCE_BATTLE_FIGHT_REPORT_C);
		listenProto(HP.code.ALLIANCE_BATTLE_LAST_STAGE_FIGHT_INFO_C);
		listenProto(HP.code.ALLIANCE_BATTLE_INSPIRE_C);
		listenProto(HP.code.ALLIANCE_BATTLE_DRAW_C);
	}
	
	/**
	 * 更新
	 * 
	 * @return
	 */
	@Override
	public boolean onTick() {
		return super.onTick();
	}

	/**
	 * 消息响应
	 * 
	 * @param msg
	 * @return
	 */
	@Override
	public boolean onMessage(Msg msg) {
		return super.onMessage(msg);
	}
	
	@Override
	protected boolean onPlayerLogin() {
		return super.onPlayerLogin();
	}

	/*******************************************
	*    以下为协议响应
	********************************************/

	@Override
	public boolean onProtocol(Protocol protocol) {
		if(protocol.checkType(HP.code.ALLIANCE_BATTLE_ENTER_C)) {
			onAllianceBattleEnter();
			return true;
		}else if(protocol.checkType(HP.code.ALLIANCE_TEAM_BASIC_INFO_C)){
			onAllianceBasicInfo();
			return true;
		}else if(protocol.checkType(HP.code.ALLIANCE_TEAM_JOIN_INFO_C_VALUE)){
			HPAllianceTeamJoin teamJoin = protocol.parseProtocol(HPAllianceTeamJoin.getDefaultInstance());
			onAllianceJoin(teamJoin);
			return true;
		}else if(protocol.checkType(HP.code.ALLIANCE_TEAM_DETAIL_INFO_C_VALUE)) {
			HPAllianceTeamDetail param = protocol.parseProtocol(HPAllianceTeamDetail.getDefaultInstance());
			onAllianceTeamDetail(param);
			return true;
		}else if(protocol.checkType(HP.code.ALLIANCE_TEAM_SAVE_C_VALUE)) {
			HPAllianceTeamSave teamSave = protocol.parseProtocol(HPAllianceTeamSave.getDefaultInstance());
			onAllianceSave(teamSave);
			return true;
		}else if(protocol.checkType(HP.code.ALLIANCE_BATTLE_FIGHT_BET_C)) {
			HPInvest investParam = protocol.parseProtocol(HPInvest.getDefaultInstance());
			onInvest(investParam);
			return true;
		}else if(protocol.checkType(HP.code.ALLIANCE_BATTLE_TEAM_FIGHT_INFO_C)) {
			HPAllianceTeamFight param = protocol.parseProtocol(HPAllianceTeamFight.getDefaultInstance());
			onAllianceBattleFightInfo(param);
			return true;
		}else if(protocol.checkType(HP.code.ALLIANCE_BATTLE_FIGHT_REPORT_C)) {
			HPAllianceTeamFightReport param = protocol.parseProtocol(HPAllianceTeamFightReport.getDefaultInstance());
			onAllianceBattleFightReport(param);
			return true;
		} else if(protocol.checkType(HP.code.ALLIANCE_BATTLE_LAST_STAGE_FIGHT_INFO_C)) {
			onAllianceBattleLastStage();
			return true;
		} else if(protocol.checkType(HP.code.ALLIANCE_BATTLE_INSPIRE_C)) {
			onAllianceBattleInspire();
			return true;
		} else if(protocol.checkType(HP.code.ALLIANCE_BATTLE_DRAW_C)) {
			onAllianceBattleDraw();
			return true;
		}
		return false;
	}
	
	
	/**
	 * 工会战抽签
	 */
	private void onAllianceBattleDraw() {
		int allianceId = 0;
		PlayerAllianceEntity playerAllianceEntity = player.getPlayerData().getPlayerAllianceEntity();
		if(playerAllianceEntity != null) {
			allianceId = playerAllianceEntity.getAllianceId();
		}
		if(allianceId <= 0) {
			sendError(HP.code.ALLIANCE_BATTLE_DRAW_C_VALUE, Status.error.ALLIANCE_NONEXISTENT_VALUE);
			return ;
		}
		
		if(playerAllianceEntity != null) {
			//抽取工会
			AllianceBattleManager.getInstance().drawAllianceBattleValue(player, playerAllianceEntity);
		}
	}

	/**
	 * 帮会战鼓舞
	 */
	private void onAllianceBattleInspire() {
		int allianceId = 0;
		PlayerAllianceEntity playerAllianceEntity = player.getPlayerData().getPlayerAllianceEntity();
		if(playerAllianceEntity != null) {
			allianceId = playerAllianceEntity.getAllianceId();
		}
		if(allianceId <= 0) {
			sendError(HP.code.ALLIANCE_BATTLE_INSPIRE_C_VALUE, Status.error.ALLIANCE_NONEXISTENT_VALUE);
			return ;
		}
		
		AllianceBattleState allianceBattleState = AllianceBattleManager.getInstance().getCurBattleState();
		if(allianceBattleState == null || allianceBattleState == AllianceBattleState.PREPARE || allianceBattleState == AllianceBattleState.SHOW_TIME
				|| allianceBattleState == AllianceBattleState.FS32_16_FIGHTING || allianceBattleState == AllianceBattleState.FS16_8_FIGHTING
				|| allianceBattleState == AllianceBattleState.FS8_4_FIGHTING || allianceBattleState == AllianceBattleState.FS4_2_FIGHTING
				|| allianceBattleState == AllianceBattleState.FS2_1_FIGHTING) {
			sendError(HP.code.ALLIANCE_BATTLE_INSPIRE_C_VALUE, Status.error.BATTLE_STATE_NOT_ALLOW_INSPIRE);
			return ;
		}
		
		AllianceBattleItem allianceBattleItem = AllianceBattleManager.getInstance().getBattleItem(allianceId);
		int inspireTimes = allianceBattleItem.getInspireTimes(player.getId(), allianceBattleState);
		int nextInspireTimes = inspireTimes + 1;
		AllianceBattleInspireCfg inspireCfg = AllianceBattleInspireCfg.getInspireCfg(AllianceBattleManager.GROUP_REGISTER_MAP.get(allianceBattleState).getNumber(), nextInspireTimes);
		if(inspireCfg == null) {
			// 鼓舞次数已满
			sendError(HP.code.ALLIANCE_BATTLE_INSPIRE_C_VALUE, Status.error.ALLIANCE_BATTLE_INSPIRE_FULL);
			return ;
		}
		
		ConsumeItems consumeItems = ConsumeItems.valueOf(changeType.CHANGE_GOLD, inspireCfg.getCostGold());
		
		if(!consumeItems.checkConsume(player, HP.code.ALLIANCE_BATTLE_INSPIRE_C_VALUE)) {
			return ;
		}
		
		consumeItems.consumeTakeAffect(player, Action.ALLIANCE_BATTLE_INSPIRE);
		
		allianceBattleItem.inpsire(player.getId(), AllianceBattleManager.GROUP_REGISTER_MAP.get(allianceBattleState).getNumber());
		allianceBattleItem.notifyUpdate(true);
		
		HPInspireRet.Builder inspireBuilder = HPInspireRet.newBuilder();
		inspireBuilder.setInspireTimes(allianceBattleItem.getInspireTimes(player.getId(), AllianceBattleManager.GROUP_REGISTER_MAP.get(allianceBattleState).getNumber()));
		inspireBuilder.setTotalInspireTimes(allianceBattleItem.getTotalInspireTimes(player.getId()));
		sendProtocol(Protocol.valueOf(HP.code.ALLIANCE_BATTLE_INSPIRE_S_VALUE,inspireBuilder));
	}

	/**
	 * 查看上期战报
	 */
	private void onAllianceBattleLastStage() {
		int allianceId = 0;
		PlayerAllianceEntity playerAllianceEntity = player.getPlayerData().getPlayerAllianceEntity();
		if(playerAllianceEntity != null) {
			allianceId = playerAllianceEntity.getAllianceId();
		}
		AllianceBattleAgainstInfo lastAgainstInfo = AllianceBattleManager.getInstance().getLastBattleAgainstInfo();

		if(lastAgainstInfo == null || lastAgainstInfo.isDataEmpty()) {
			sendError(HP.code.ALLIANCE_BATTLE_LAST_STAGE_FIGHT_INFO_C_VALUE, Status.error.NO_LAST_STAGE_INFO_VALUE);
			return ;
		}
		
		HPLastBattleFightInfoRet.Builder infoBuilder = HPLastBattleFightInfoRet.newBuilder();
		infoBuilder.setFightList(BuilderUtil.genAllianceFightList(player, allianceId, lastAgainstInfo, true));
		sendProtocol(Protocol.valueOf(HP.code.ALLIANCE_BATTLE_LAST_STAGE_FIGHT_INFO_S, infoBuilder));
	}

	private void onAllianceBattleFightReport(HPAllianceTeamFightReport param) {
		int battleId = param.getBattleId();
		if(battleId <= 0) {
			sendError(HP.code.ALLIANCE_BATTLE_FIGHT_REPORT_C_VALUE, Status.error.PARAMS_INVALID_VALUE);
			return ;
		}
		
		AllianceFightUnit allianceFightUnit = AllianceBattleManager.getInstance().getAllianceFightUnit(battleId);
		if(allianceFightUnit == null) {
			sendError(HP.code.ALLIANCE_BATTLE_FIGHT_REPORT_C_VALUE, Status.error.DATA_NOT_FOUND_VALUE);
			return ;
		}
		
		HPAllianceTeamFightReportRet.Builder reportBuilder = HPAllianceTeamFightReportRet.newBuilder();
		BattleInfo battleInfo = allianceFightUnit.getFightReportBattle();
		if(battleInfo == null) {
			sendError(HP.code.ALLIANCE_BATTLE_FIGHT_REPORT_C_VALUE, Status.error.FIGHT_REPORT_NOT_EXIST);
			return ;
		}
		reportBuilder.setBattleInfo(battleInfo);
		Protocol protocol = Protocol.valueOf(HP.code.ALLIANCE_BATTLE_FIGHT_REPORT_S,reportBuilder);
		sendProtocol(ProtoUtil.compressProtocol(protocol));
	}

	/**
	 * 工会战战报查看
	 * @param param
	 */
	private void onAllianceBattleFightInfo(HPAllianceTeamFight param) {
		int versusId = param.getUnitId();
		if(versusId < 0) {
			sendError(HP.code.ALLIANCE_BATTLE_TEAM_FIGHT_INFO_C_VALUE,Status.error.PARAMS_INVALID_VALUE);
			return;
		}
		int allianceId = 0;
		AllianceFightVersus versus ;
		if(param.getIsLastSession()){
			//查看上期战报
			if(versusId == 0) {
				sendError(HP.code.ALLIANCE_BATTLE_TEAM_FIGHT_INFO_C_VALUE,Status.error.PARAMS_INVALID_VALUE);
				return;
			}
			AllianceBattleAgainstInfo lastAgainstInfo = AllianceBattleManager.getInstance().getLastBattleAgainstInfo();
			if(lastAgainstInfo == null) {
				sendError(HP.code.ALLIANCE_BATTLE_TEAM_FIGHT_INFO_C_VALUE,Status.error.NO_LAST_STAGE_INFO_VALUE);
				return ;
			}
			versus = lastAgainstInfo.getAllianceFightVersus(versusId);
		}else{
			AllianceBattleAgainstInfo againstInfo = AllianceBattleManager.getInstance().getBattleAgainstInfo();
			if(againstInfo == null) {
				sendError(HP.code.ALLIANCE_BATTLE_TEAM_FIGHT_INFO_C_VALUE,Status.error.NO_LAST_STAGE_INFO_VALUE);
				return ;
			}
			if(versusId == 0) {
				// 查看自己的公会在当前阶段的战报或者对阵信息
				PlayerAllianceEntity playerAllianceEntity = player.getPlayerData().getPlayerAllianceEntity();
				if(playerAllianceEntity == null) {
					sendError(HP.code.ALLIANCE_BATTLE_TEAM_FIGHT_INFO_C_VALUE,Status.error.PARAMS_INVALID_VALUE);
					return;
				}
				
				FightGroup fightGroup = AllianceBattleManager.getInstance().getCurFightGroup();
				if(fightGroup == null) {
					sendError(HP.code.ALLIANCE_BATTLE_TEAM_FIGHT_INFO_C_VALUE,Status.error.PARAMS_INVALID_VALUE);
					return;
				}
				allianceId = playerAllianceEntity.getAllianceId();
				versus = againstInfo.getAllianceFightVersus(fightGroup.getNumber(),allianceId);
			}else{
				versus = againstInfo.getAllianceFightVersus(versusId);
			}
		}
		
		if(versus == null) {
			sendError(HP.code.ALLIANCE_BATTLE_TEAM_FIGHT_INFO_C_VALUE,Status.error.DATA_NOT_FOUND_VALUE);
			return;
		}
		sendProtocol(Protocol.valueOf(HP.code.ALLIANCE_BATTLE_TEAM_FIGHT_INFO_S_VALUE,BuilderUtil.genAllianceFightVersus(allianceId,versus)));
	}

	/**
	 * 工会战投资
	 * @param investParam
	 */
	private void onInvest(HPInvest investParam) {
		int versusId = investParam.getVersusId();
		int allianceId = investParam.getAllianceId();
		
		AllianceFightVersus fightVersus = AllianceBattleManager.getInstance().getBattleAgainstInfo().getAllianceFightVersus(versusId);
		if(fightVersus == null) {
			sendError(HP.code.ALLIANCE_BATTLE_FIGHT_BET_C_VALUE, Status.error.PARAMS_INVALID_VALUE);
			return ;
		}
		
		int fightGroupValue = fightVersus.getFightGroup();
		List<AllianceFightVersus> versusList = AllianceBattleManager.getInstance().getBattleAgainstInfo().getAllianceFightVersusByGroup(fightGroupValue);
		if(versusList == null) {
			sendError(HP.code.ALLIANCE_BATTLE_FIGHT_BET_C_VALUE, Status.error.PARAMS_INVALID_VALUE);
			return ;
		}
		
		for(AllianceFightVersus versus : versusList) {
			if(versus.containsPlayer(player.getId())) {
				sendError(HP.code.ALLIANCE_BATTLE_FIGHT_BET_C_VALUE, Status.error.ALREADY_INVESTED);
				return ;
			}
		}
		
		InvestRewardCfg investRewardCfg = ConfigManager.getInstance().getConfigByKey(InvestRewardCfg.class, fightVersus.getFightGroup());
		
		if(investRewardCfg == null) {
			sendError(HP.code.ALLIANCE_BATTLE_FIGHT_BET_C_VALUE, Status.error.CONFIG_NOT_FOUND_VALUE);
			return ;
		}
		
		int costGold = investRewardCfg.getCostGold();
		int costCoins = investRewardCfg.getCostCoins();
		
		if(costGold > 0) {
			ConsumeItems consumeItems = ConsumeItems.valueOf(changeType.CHANGE_GOLD,investRewardCfg.getCostGold());
			if(!consumeItems.checkConsume(player,HP.code.ALLIANCE_BATTLE_FIGHT_BET_C_VALUE)) {
				return;
			}
			
			consumeItems.consumeTakeAffect(player, Action.ALLIANCE_BATTLE_INVEST);
		}else if(costCoins > 0) {
			ConsumeItems consumeItems = ConsumeItems.valueOf(changeType.CHANGE_GOLD,investRewardCfg.getCostGold());
			if(!consumeItems.checkConsume(player,HP.code.ALLIANCE_BATTLE_FIGHT_BET_C_VALUE)) {
				return;
			}
			
			consumeItems.consumeTakeAffect(player, Action.ALLIANCE_BATTLE_INVEST);
		}else{
			sendError(HP.code.ALLIANCE_BATTLE_FIGHT_BET_C_VALUE, Status.error.CONFIG_NOT_FOUND_VALUE);
			return ;
		}
		
		fightVersus.addInvest(player.getId(), allianceId);
		fightVersus.notifyUpdate(true);
		
		HPInvestRet.Builder builder = HPInvestRet.newBuilder();
		builder.setAllianceId(allianceId);
		builder.setVersusId(versusId);
		sendProtocol(Protocol.valueOf(HP.code.ALLIANCE_BATTLE_FIGHT_BET_S_VALUE,builder));
		
	}

	/**
	 * 更换位置之后保存
	 * @param teamSave
	 */
	private void onAllianceSave(HPAllianceTeamSave teamSave) {
		List<Integer> idList = teamSave.getIdListList();
		List<Integer> indexList = teamSave.getIndexListList();
		PlayerAllianceEntity playerAllianceEntity = player.getPlayerData().getPlayerAllianceEntity();
		if(playerAllianceEntity == null || playerAllianceEntity.getAllianceId() == 0) {
			sendError(HP.code.ALLIANCE_TEAM_SAVE_C_VALUE, Status.error.ALLIANCE_NONEXISTENT);
			return ;
		}
		
		AllianceBattleItem battleItem = AllianceBattleManager.getInstance().getBattleItem(playerAllianceEntity.getAllianceId());
		
		if(playerAllianceEntity.getPostion() != GsConst.Alliance.ALLIANCE_POS_MAIN && 
				playerAllianceEntity.getPostion() != GsConst.Alliance.ALLIANCE_POS_COPYMAIN) {
			sendError(HP.code.ALLIANCE_TEAM_SAVE_C_VALUE, Status.error.ALLIANCE_NO_MAIN);
			return ;
		}
		
		Map<Integer, List<Integer>> teamMap = new HashMap<Integer, List<Integer>>();
		for(int teamIndex : GsConst.AllianceBattle.ALL_TEAM) {
			teamMap.put(teamIndex, new LinkedList<Integer>());	
		}
		for(int i=0;i < indexList.size();i++ ) {
			List<Integer> memList = teamMap.get(indexList.get(i));
			if(memList == null) {
				sendError(HP.code.ALLIANCE_TEAM_SAVE_C_VALUE, Status.error.PARAMS_INVALID_VALUE);
				return;
			}
			
			memList.add(idList.get(i));
		}
		//检测是否有空的~
		for(List<Integer> memList : teamMap.values()) {
			Iterator<Integer> iterator = memList.iterator();
			while(iterator.hasNext()) {
				Integer id = iterator.next();
				if(id == null) {
					sendError(HP.code.ALLIANCE_TEAM_SAVE_C_VALUE, Status.error.PARAMS_INVALID_VALUE);
					return;
				}
				
				if(!battleItem.getMemberList().contains(id)) {
					iterator.remove();
				}
			}
		}
		
		battleItem.resetTeamMap(teamMap);
		battleItem.notifyUpdate(true);
		
		// 再推送一遍基础信息
		onAllianceBasicInfo();
	}

	/**
	 * 推送公会战场具体信息
	 * @param allianceEntity
	 */
	private void pushTeamDetail(AllianceBattleItem battleItem) {
		HPAllianceTeamDetailRet.Builder teamDetailBuilder = HPAllianceTeamDetailRet.newBuilder();
		AllianceBattleState curBattleState = AllianceBattleManager.getInstance().getCurBattleState();
		int fightGroup = 0;
		if(curBattleState != null) {
			if(curBattleState == AllianceBattleState.Publicity_WAIT || curBattleState == AllianceBattleState.FS32_16_FIGHTING) {
				fightGroup = FightGroup.GROUP_32_VALUE;
			} else if(curBattleState == AllianceBattleState.FS16_8_WAIT || curBattleState == AllianceBattleState.FS16_8_FIGHTING) {
				fightGroup = FightGroup.GROUP_16_VALUE;
			} else if(curBattleState == AllianceBattleState.FS8_4_WAIT || curBattleState == AllianceBattleState.FS8_4_FIGHTING) {
				fightGroup = FightGroup.GROUP_8_VALUE;
			} else if(curBattleState == AllianceBattleState.FS4_2_WAIT || curBattleState == AllianceBattleState.FS4_2_FIGHTING) {
				fightGroup = FightGroup.GROUP_4_VALUE;
			} else if(curBattleState == AllianceBattleState.FS2_1_WAIT || curBattleState == AllianceBattleState.FS2_1_FIGHTING) {
				fightGroup = FightGroup.GROUP_2_VALUE;
			}
		}
		for(int teamIndex : GsConst.AllianceBattle.ALL_TEAM) {
			List<Integer> memList = battleItem.getTeamMemberIds(teamIndex);
			int index = 0;
			if(memList == null) {
				continue;
			}
			for(Integer playerId : memList) {
				index++;
				HPAllianceTeamUnit.Builder teamUnitBuilder = HPAllianceTeamUnit.newBuilder();
				PlayerSnapshotInfo.Builder playerSnapshotInfo = SnapShotManager.getInstance().getPlayerSnapShot(playerId);
				teamUnitBuilder.setId(playerId);
				teamUnitBuilder.setFightValue(playerSnapshotInfo.getMainRoleInfo().getMarsterFight());
				teamUnitBuilder.setLevel(playerSnapshotInfo.getMainRoleInfo().getLevel());
				teamUnitBuilder.setName(playerSnapshotInfo.getMainRoleInfo().getName());
				teamUnitBuilder.setItemId(playerSnapshotInfo.getMainRoleInfo().getItemId());
				teamUnitBuilder.setIndex(teamIndex);
				teamUnitBuilder.setPos(index);
				teamUnitBuilder.setRebirthStage(playerSnapshotInfo.getMainRoleInfo().getRebirthStage());
				AllianceInfo allianceInfo = playerSnapshotInfo.getAllianceInfo();
				if(allianceInfo != null) {
					teamUnitBuilder.setFlag(allianceInfo.getPostion());
				}
				
				if(fightGroup > 0) {
					teamUnitBuilder.setInspireNum(battleItem.getInspireTimes(playerId, fightGroup));
					teamUnitBuilder.setTotalInspireNum(battleItem.getTotalInspireTimes(playerId));
				}
				
				teamDetailBuilder.addTeamList(teamUnitBuilder);
			}
		}
		
		teamDetailBuilder.setAllianceItemInfo(BuilderUtil.genAllianceBattleItem(battleItem));
		
		sendProtocol(Protocol.valueOf(HP.code.ALLIANCE_TEAM_DETAIL_INFO_S,teamDetailBuilder));
	}
	
	/**
	 * 查询具体的战场信息
	 * @param param 
	 */
	private void onAllianceTeamDetail(HPAllianceTeamDetail param) {
		int allianceId = 0;
		if(param.hasAllianceId()) {
			allianceId = param.getAllianceId();
		}
		
		if(allianceId == 0) {
			PlayerAllianceEntity playerAllianceEntity = player.getPlayerData().getPlayerAllianceEntity();
			if(playerAllianceEntity == null || playerAllianceEntity.getAllianceId() == 0) {
				sendError(HP.code.ALLIANCE_TEAM_DETAIL_INFO_C_VALUE, Status.error.ALLIANCE_NONEXISTENT);
				return ;
			}
			
			allianceId = playerAllianceEntity.getAllianceId();
		}
		
		AllianceBattleItem battleItem = AllianceBattleManager.getInstance().getBattleItem(allianceId);
		if(battleItem != null) {
			pushTeamDetail(battleItem);
		}else{
			sendError(HP.code.ALLIANCE_TEAM_DETAIL_INFO_C_VALUE, Status.error.ALLIANCE_NOT_IN_BATTLE);
			return ;
		}
	}

	/**
	 * 加入team
	 * @param teamJoin
	 */
	private void onAllianceJoin(HPAllianceTeamJoin teamJoin) {
		int teamIndex = teamJoin.getTeamIndex();
		if(teamIndex < 1 || teamIndex > 3) {
			sendError(HP.code.ALLIANCE_TEAM_JOIN_INFO_C_VALUE, Status.error.PARAMS_INVALID_VALUE);
			return ;
		}
		
		PlayerAllianceEntity playerAllianceEntity = player.getPlayerData().getPlayerAllianceEntity();
		if(playerAllianceEntity == null || playerAllianceEntity.getAllianceId() == 0) {
			sendError(HP.code.ALLIANCE_TEAM_BASIC_INFO_C_VALUE, Status.error.ALLIANCE_NONEXISTENT);
			return ;
		}
		
		AllianceBattleItem battleItem = AllianceBattleManager.getInstance().getBattleItem(playerAllianceEntity.getAllianceId());
		
		if(battleItem == null) {
			sendError(HP.code.ALLIANCE_TEAM_BASIC_INFO_C_VALUE, Status.error.ALLIANCE_NOT_IN_BATTLE);
			return ;
		}
		
		if(battleItem.getTeamMemberSize(teamIndex) >= battleItem.getTeamMemberMaxSize()) {
			sendError(HP.code.ALLIANCE_TEAM_BASIC_INFO_C_VALUE, Status.error.ALLIANCE_TEAM_FULL);
			return ;
		}
		
		
		int existTeamIndex = battleItem.getMemberTeamIndex(player.getId());
		if(existTeamIndex > 0) {
			if(existTeamIndex == teamIndex) {
				sendError(HP.code.ALLIANCE_TEAM_BASIC_INFO_C_VALUE, Status.error.ALLIANCE_TEAM_EXIST);
				return ;
			}
			//先移除吧
			battleItem.removeMem(existTeamIndex,player.getId());
		}
		
		battleItem.addTeamMem(player.getId(), teamIndex);
		
		battleItem.notifyUpdate(true);
		
		HPAllianceTeamEnterRet.Builder teamListBuilder = HPAllianceTeamEnterRet.newBuilder();
		for(int tIndex : GsConst.AllianceBattle.ALL_TEAM) {
			teamListBuilder.addTeamSize(battleItem.getTeamMemberSize(tIndex));
			teamListBuilder.setSelfTeamIndex(battleItem.getMemberTeamIndex(player.getId()));
		}
		sendProtocol(Protocol.valueOf(HP.code.ALLIANCE_TEAM_BASIC_INFO_S_VALUE,teamListBuilder));
	}

	/**
	 * 查询公会内部的三个team基础信息
	 */
	private void onAllianceBasicInfo() {
		PlayerAllianceEntity playerAllianceEntity = player.getPlayerData().getPlayerAllianceEntity();
		if(playerAllianceEntity == null || playerAllianceEntity.getAllianceId() == 0) {
			sendError(HP.code.ALLIANCE_TEAM_BASIC_INFO_C_VALUE, Status.error.ALLIANCE_NONEXISTENT);
			return ;
		}
		AllianceBattleItem battleItem = AllianceBattleManager.getInstance().getBattleItem(playerAllianceEntity.getAllianceId());
		if(battleItem == null) {
			AllianceEntity allianceEntity = AllianceManager.getInstance().getAlliance(playerAllianceEntity.getAllianceId());
			if(allianceEntity == null) {
				sendError(HP.code.ALLIANCE_TEAM_BASIC_INFO_C_VALUE, Status.error.ALLIANCE_NONEXISTENT_VALUE);
				return ;
			}
			battleItem = AllianceBattleManager.getInstance().createTempBattleItem(allianceEntity);
			if(battleItem == null) {
				sendError(HP.code.ALLIANCE_TEAM_BASIC_INFO_C_VALUE, Status.error.ALLIANCE_NONEXISTENT_VALUE);
				return ;
			}
		}
		
		HPAllianceTeamEnterRet.Builder teamListBuilder = HPAllianceTeamEnterRet.newBuilder();
		for(int teamIndex : GsConst.AllianceBattle.ALL_TEAM) {
			teamListBuilder.addTeamSize(battleItem.getTeamMemberSize(teamIndex));
			teamListBuilder.setSelfTeamIndex(battleItem.getMemberTeamIndex(player.getId()));
		}
		
		sendProtocol(Protocol.valueOf(HP.code.ALLIANCE_TEAM_BASIC_INFO_S_VALUE,teamListBuilder));
	}

	/**
	 * 进入帮会争霸主页面
	 */
	private void onAllianceBattleEnter() {
		if(!AllianceBattleManager.getInstance().isAllowOpen()) {
			sendError(HP.code.ALLIANCE_BATTLE_ENTER_C_VALUE, Status.error.ALLIANCE_BATTLE_NEXT_OPEN_VALUE);
			return ;
		}
		
		PlayerAllianceEntity allianceEntity = player.getPlayerData().loadPlayerAlliance();
		if(allianceEntity != null) {
			AllianceBattleManager.getInstance().getSyncBattleInfo(player, allianceEntity.getAllianceId());
		}else{
			AllianceBattleManager.getInstance().getSyncBattleInfo(player, 0);
		}
	}

}
