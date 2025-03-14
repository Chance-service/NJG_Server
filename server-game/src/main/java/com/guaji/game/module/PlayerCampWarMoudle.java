package com.guaji.game.module;

import java.util.List;

import org.guaji.msg.Msg;
import org.guaji.net.protocol.Protocol;

import com.guaji.game.protocol.CampWar.CampInfo;
import com.guaji.game.protocol.CampWar.CampMainInfo;
import com.guaji.game.protocol.CampWar.CampReportInfo;
import com.guaji.game.protocol.CampWar.CampStateType;
import com.guaji.game.protocol.CampWar.HPCampWarInfoSyncS;
import com.guaji.game.protocol.CampWar.HPCampWarStateSyncS;
import com.guaji.game.protocol.CampWar.HPEnterBattleFieldRet;
import com.guaji.game.protocol.CampWar.HPInspireCampWar;
import com.guaji.game.protocol.CampWar.HPLastCampWarRankInfoSyncS;
import com.guaji.game.protocol.CampWar.MultiKillRankInfo;
import com.guaji.game.protocol.CampWar.PersonalCampWarInfo;
import com.guaji.game.protocol.Const.QuestEventType;
import com.guaji.game.protocol.Const.changeType;
import com.guaji.game.config.SysBasicCfg;
import com.guaji.game.item.ConsumeItems;
import com.guaji.game.log.BehaviorLogger;
import com.guaji.game.log.BehaviorLogger.Action;
import com.guaji.game.log.BehaviorLogger.Params;
import com.guaji.game.log.BehaviorLogger.Source;
import com.guaji.game.manager.CampWarManager;
import com.guaji.game.module.quest.QuestEventBus;
import com.guaji.game.player.Player;
import com.guaji.game.player.PlayerData;
import com.guaji.game.player.PlayerModule;
import com.guaji.game.util.GsConst;
import com.guaji.game.util.PlayerUtil;
import com.guaji.game.protocol.HP;
import com.guaji.game.protocol.Status;
/**
 * 阵营战
 */
public class PlayerCampWarMoudle  extends PlayerModule {

	public PlayerCampWarMoudle(Player player) {
		super(player);
		
		// 注册监听协议
		listenProto(HP.code.UPDATE_AUTO_CAMPWAR_STATE_C);
		listenProto(HP.code.CAMPWAR_INFO_C);
		listenProto(HP.code.CAMPWAR_INSPIRE_C);
		listenProto(HP.code.ENTER_BATTLEFIELD_C_VALUE);
		listenProto(HP.code.GET_CAMPWAR_STATE_C);
		
		// 注册监听消息
		listenMsg(GsConst.MsgType.CAMPWAR_STATUS_CHANGE);
		listenMsg(GsConst.MsgType.CAMPWAR_BATTLE_ROUND_CHANGE);
		listenMsg(GsConst.MsgType.CAMPWAR_END);
		listenMsg(GsConst.MsgType.CAMPWAR_CLOSE);
	}
	
	/**
	 * 玩家组装完成, 主要用来后期数据同步
	 * @return
	 */
	protected boolean onPlayerAssemble() {
		// 同步阵营战状态
		campWarStateSync();
		// 同步上次阵营战排行
		lastCampWarRankInfoSync();
		return true;
	} 
	
	/**
	 * 消息处理
	 */
	@Override
	public boolean onMessage(Msg msg) {
		if(msg.getMsg() == GsConst.MsgType.CAMPWAR_STATUS_CHANGE){
			campWarStateSync();
			return true;
		}else if(msg.getMsg() == GsConst.MsgType.CAMPWAR_BATTLE_ROUND_CHANGE){
			campWarBattlefieldSync();
			return true;
		} else if (msg.getMsg() == GsConst.MsgType.CAMPWAR_END){
			campWarStateSync();
			return true;
		}else if(msg.getMsg() == GsConst.MsgType.CAMPWAR_CLOSE){
			campWarStateSync();
			lastCampWarRankInfoSync();
			return true;
		}
		return super.onMessage(msg);
	}
	
	
	/**
	 * 协议响应
	 * 
	 * @param protocol
	 * @return
	 */
	@Override
	public boolean onProtocol(Protocol protocol) {
		// 模块内协议预判
		if(isListenProto(protocol.getType())){
			int fightValue = PlayerUtil.calcAllFightValue(player.getPlayerData());
			if(fightValue < SysBasicCfg.getInstance().getCampWarMinFightValue()){
				// 战斗力不足，不能进入阵营战
				sendError(protocol.getType(), Status.error.FIGHT_VALUE_NOT_ENOUGH_VALUE);
				return true;
			}
		}
		
		if(protocol.checkType(HP.code.UPDATE_AUTO_CAMPWAR_STATE_C)){
			onUpdateAutoCampWarState();
			return true;
		} else if(protocol.checkType(HP.code.ENTER_BATTLEFIELD_C)){
			onEnterBattlefield();
			campWarStateSync();
			return true;
		} else if(protocol.checkType(HP.code.CAMPWAR_INFO_C)){
			campWarBattlefieldSync();
			return true;
		} else if(protocol.checkType(HP.code.CAMPWAR_INSPIRE_C)){
			onInspireCampWar(protocol.parseProtocol(HPInspireCampWar.getDefaultInstance()));
			return true;
		} else if(protocol.checkType(HP.code.GET_CAMPWAR_STATE_C)){
			campWarStateSync();
			return true;
		}
		return super.onProtocol(protocol);
	}
	
	/**
	 * 阵营战状态同步
	 */
	private void campWarStateSync(){
		CampMainInfo.Builder mainInfo = CampMainInfo.newBuilder();
		CampWarManager instance = CampWarManager.getInstance();
		mainInfo.setLeftTime(instance.calcCurStatuesLeftTime());
		mainInfo.setState(instance.getStatus());
		
		boolean isJoin = CampWarManager.getInstance().isJoinBattlefield(player.getId());
		mainInfo.setIsJoin(isJoin ? GsConst.CampWar.ALREADY_JOINED : GsConst.CampWar.NOT_JOIN);
		
		int isAuto =  instance.isAutoCampWar(player.getId()) ? GsConst.CampWar.AUTO_JOIN : GsConst.CampWar.NOT_AUTO_JOIN;
		mainInfo.setIsAuto(isAuto);
		
		HPCampWarStateSyncS.Builder ret = HPCampWarStateSyncS.newBuilder();
		ret.setMainInfo(mainInfo);
		sendProtocol(Protocol.valueOf(HP.code.CAMPWAR_STATE_SYNC_S, ret));
	}
	
	/**
	 * 上次阵营战连杀排行数据
	 */
	private void lastCampWarRankInfoSync(){
		HPLastCampWarRankInfoSyncS.Builder ret = HPLastCampWarRankInfoSyncS.newBuilder();
		List<MultiKillRankInfo.Builder> lastRankList = CampWarManager.getInstance().genLastCampWarRankList();
		for(MultiKillRankInfo.Builder lastCampWarRankInfo : lastRankList){
			ret.addRankInfo(lastCampWarRankInfo);
		}
		ret.setHangGold(CampWarManager.getInstance().getLastAvgCoins());
		ret.setHangReputation(CampWarManager.getInstance().getLastAvgReputation());
		ret.setWinCampId(CampWarManager.getInstance().getLastWinCampId());
		sendProtocol(Protocol.valueOf(HP.code.LAST_CAMPWAR_RANK_SYNC_S, ret));
	}
	
	/**
	 * 阵营战战场信息（二级页面）同步
	 */
	private void campWarBattlefieldSync() {
		HPCampWarInfoSyncS.Builder ret = HPCampWarInfoSyncS.newBuilder();
		CampWarManager instance = CampWarManager.getInstance();
		
		// 个人信息
		PersonalCampWarInfo.Builder pInfo = instance.getPersonalCampWarInfo(player.getId());
		if(pInfo == null){
			return;
		}
		ret.setPersonalState(pInfo);
		
		// 阵营信息
		List<CampInfo.Builder> campInfoList = instance.getAllCampInfo();
		for(CampInfo.Builder campInfo : campInfoList){
			ret.addCampInfo(campInfo);
		}
		
		// 今日击杀排行信息  
		List<MultiKillRankInfo.Builder> curRankList = instance.getCurCampWarRankList();
		for(MultiKillRankInfo.Builder curCampWarRankInfo : curRankList){
			ret.addRankInfo(curCampWarRankInfo);
		}
		
		// 战报信息增量 
		List<CampReportInfo.Builder> newCampReportList = instance.getNewCampReportInfoList(player.getId());
		for(CampReportInfo.Builder campReport : newCampReportList){
			ret.addCampReportInfo(campReport);
		}
			
		ret.setCampRemainTime(CampWarManager.getInstance().calcCurStatuesLeftTime());
		sendProtocol(Protocol.valueOf(HP.code.CAMPWAR_INFO_SYNC_S, ret));
	}
	
	/**
	 * 进入阵营战战场
	 */
	private void onEnterBattlefield() {
		CampWarManager campWarMan = CampWarManager.getInstance();
		int campWarStatus = campWarMan.getStatus();
		boolean joinSucc = true;
		if(campWarMan.isAutoCampWar(player.getId())){
			// 已经投资战场不能再加入战斗
			joinSucc = false;
			sendError(HP.code.ENTER_BATTLEFIELD_S_VALUE,  Status.error.ENTER_BATTLE_FIELD_FAIL_VALUE);
			return;
		}
		
		if(campWarStatus != CampStateType.CAMP_WAR_PREPARE_VALUE && campWarStatus != CampStateType.CAMP_WAR_FIGHT_VALUE){
			// 战场未开启
			joinSucc = false;
			sendError(HP.code.ENTER_BATTLEFIELD_S_VALUE, Status.error.CAMPWAR_NOT_OPEN_VALUE);
			return;
		}
		
		boolean isJoin = CampWarManager.getInstance().isJoinBattlefield(player.getId());
		if (isJoin) {
			// 您已经在战场中
			joinSucc = false;
			sendError(HP.code.ENTER_BATTLEFIELD_S_VALUE,  Status.error.ALREADY_JOIN_CAMPWAR_VALUE);
			return;
		}
		
		CampWarManager.getInstance().enterCampWarBattlefield(player);
		HPEnterBattleFieldRet.Builder ret = HPEnterBattleFieldRet.newBuilder();
		ret.setIsEnterSucc(joinSucc);
		// 推送参加激流谷任务
		QuestEventBus.fireQuestEventOneTime(QuestEventType.JI_LIU_GU, player.getXid());
		sendProtocol(Protocol.valueOf(HP.code.ENTER_BATTLEFIELD_S_VALUE, ret));
		
		BehaviorLogger.log4Platform(player, Action.JOIN_CAMPWAR, Params.valueOf("curCampWarStageId", campWarMan.getCurWarStageId()));
		
		BehaviorLogger.log4Service(player,Source.USER_OPERATION, Action.JOIN_CAMPWAR, Params.valueOf("curCampWarStageId", campWarMan.getCurWarStageId()));
	}
	
	/**
	 * 更新 自动报名阵营战 状态
	 */
	private void onUpdateAutoCampWarState() {
		if(player.getVipLevel() < SysBasicCfg.getInstance().getAutoCampWarVipLevel()){
			// Vip 等级不足
			sendError(HP.code.UPDATE_AUTO_CAMPWAR_STATE_C_VALUE, Status.error.VIP_NOT_ENOUGH_VALUE);
			return;
		}
		
		if(CampWarManager.getInstance().isJoinBattlefield(player.getId())){
			// 您已经在战场中，不能再进行投资
			sendError(HP.code.ENTER_BATTLEFIELD_S_VALUE,  Status.error.ALREADY_JOIN_CAMPWAR_VALUE);
			return;
		}
		
		CampWarManager campWarMan = CampWarManager.getInstance();
		if(campWarMan.getStatus() == CampStateType.CAMP_WAR_FIGHT_VALUE){
			// 本次阵营战已打响
			sendError(HP.code.ENTER_BATTLEFIELD_S_VALUE, 0);
			return;
		}
		
		if(campWarMan.isAutoCampWar(player.getId())){
			campWarMan.cancelAutoCampWar(player.getId());
		}else{
			campWarMan.addAutoCampWar(player.getId());
		}
		
		// 更新成功, 同步阵营战状态
		campWarStateSync();
	}
	
	/**
	 * 阵营战鼓舞
	 * @param parseProtocol
	 */
	private void onInspireCampWar(HPInspireCampWar parseProtocol) {
		PersonalCampWarInfo.Builder pInfo = CampWarManager.getInstance().getPersonalCampWarInfo(player.getId());
		if(pInfo == null){
			// 请先加入阵营战
			sendError(HP.code.CAMPWAR_INSPIRE_C_VALUE, Status.error.NOT_JOIN_CAMPWAR_VALUE);
			return;
		}
		
		CampWarManager campWarMan = CampWarManager.getInstance();
		if(campWarMan.getStatus() != CampStateType.CAMP_WAR_PREPARE_VALUE
				&& campWarMan.getStatus() != CampStateType.CAMP_WAR_FIGHT_VALUE){
			// 非战斗期间不能鼓舞
			return;
		}
		
		if(pInfo.getInspireTimes() >= SysBasicCfg.getInstance().getCampWarMaxInspireTimes()){
			// 鼓舞已达最大次数上限
			sendError(HP.code.CAMPWAR_INSPIRE_C_VALUE, Status.error.MAX_CAMPWAR_INSPIRE_VALUE);
			return;
		}
		
		PlayerData playerData = player.getPlayerData();
		int inspirePrice = SysBasicCfg.getInstance().getCampWarInspirePrice();
		if(playerData.getPlayerEntity().getTotalGold() < inspirePrice){
			// 钻石不足
			sendError(HP.code.CAMPWAR_INSPIRE_C_VALUE,  Status.error.GOLD_NOT_ENOUGH_VALUE);
			return;
		}
		
		CampWarManager.getInstance().inspire(player.getId());
		if(CampWarManager.getInstance().getStatus() == CampStateType.CAMP_WAR_PREPARE_VALUE){
			campWarBattlefieldSync();
		}
		
		// 扣除钻石并推送消耗
		player.consumeGold(inspirePrice, Action.CAMPWAR_INSPIRE);
		ConsumeItems.valueOf(changeType.CHANGE_GOLD, inspirePrice).pushChange(player);
	}
}
