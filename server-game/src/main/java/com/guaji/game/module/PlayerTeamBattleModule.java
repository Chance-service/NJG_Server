package com.guaji.game.module;

import java.util.List;

import org.guaji.db.DBManager;
import org.guaji.msg.Msg;
import org.guaji.net.protocol.Protocol;
import org.guaji.os.GuaJiTime;

import com.guaji.game.config.SysBasicCfg;
import com.guaji.game.entity.TeamBattleReportEntity;
import com.guaji.game.item.ConsumeItems;
import com.guaji.game.log.BehaviorLogger;
import com.guaji.game.log.BehaviorLogger.Action;
import com.guaji.game.log.BehaviorLogger.Params;
import com.guaji.game.log.BehaviorLogger.Source;
import com.guaji.game.manager.TeamBattleManager;
import com.guaji.game.player.Player;
import com.guaji.game.player.PlayerModule;
import com.guaji.game.util.GJLocal;
import com.guaji.game.util.GsConst;
import com.guaji.game.util.PlayerUtil;
import com.guaji.game.protocol.Const;
import com.guaji.game.protocol.Const.changeType;
import com.guaji.game.protocol.HP;
import com.guaji.game.protocol.Mail.OPMailViewTeam;
import com.guaji.game.protocol.Mail.OPMailViewTeamRet;
import com.guaji.game.protocol.Mail.TbPlayerInfo;
import com.guaji.game.protocol.Mail.TbPlayerVSInfo;
import com.guaji.game.protocol.Mail.TeamRoundInfo;
import com.guaji.game.protocol.Notice.HPNotice;
import com.guaji.game.protocol.Notice.NoticeItem;
import com.guaji.game.protocol.Status;
import com.guaji.game.protocol.TeamBattle.HPCreateTeam;
import com.guaji.game.protocol.TeamBattle.HPJoinTeam;
import com.guaji.game.protocol.TeamBattle.HPKickMember;
import com.guaji.game.protocol.TeamBattle.HPTeamBattleSyncS;
import com.guaji.game.protocol.TeamBattle.HPTeamMemberSyncS;
import com.guaji.game.protocol.TeamBattle.HPUndoSignUp;
import com.guaji.game.protocol.TeamBattle.MemberInfo;

/**
 * 团战模块
 */
public class PlayerTeamBattleModule extends PlayerModule {
	
	/**
	 * 是否推送报名提醒
	 */
	private boolean isPushNotice = false;
	
	public PlayerTeamBattleModule(Player player) {
		super(player);

		// 注册监听协议
		listenProto(HP.code.TEAM_BATTLE_INFO_C);
		listenProto(HP.code.JOIN_TEAM_C);
		listenProto(HP.code.UNDO_SIGN_UP_C);
		listenProto(HP.code.CREATE_TEAM_C);
		listenProto(HP.code.MANAGE_MEMBER_C);
		listenProto(HP.code.REFRESH_MEMBER_C);
		listenProto(HP.code.KICK_MEMBER_C);
		listenProto(HP.code.MAIL_BATTLE_C);

		// 注册监听消息
		listenMsg(GsConst.MsgType.KICK_OUT_ME);
		listenMsg(GsConst.MsgType.TEAM_BATTLE_CHANGE);
	}

	/**
	 * 消息响应
	 * 
	 * @param msg
	 * @return
	 */
	public boolean onMessage(Msg msg) {
		if(msg.getMsg() == GsConst.MsgType.TEAM_BATTLE_CHANGE
				/*|| msg.getMsg() == GsConst.MsgType.KICK_OUT_ME*/ ){
			syncTeamBattleInfoS();
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
	public boolean onProtocol(Protocol protocol) {
		// 模块内协议预判
		if (isListenProto(protocol.getType())) {
			int fightValue = PlayerUtil.calcAllFightValue(player.getPlayerData());
			if (fightValue < SysBasicCfg.getInstance().getJoinTeamBattleMinFight()) {
				// 战斗力不足，不能进入团战
				sendError(protocol.getType(), Status.error.FIGHT_VALUE_NOT_ENOUGH_VALUE);
				return true;
			}
		}
		
		if(protocol.checkType(HP.code.TEAM_BATTLE_INFO_C)){
			syncTeamBattleInfoS();
			return true;
		} else if (protocol.checkType(HP.code.JOIN_TEAM_C)){
			onJoinTeam(protocol.parseProtocol(HPJoinTeam.getDefaultInstance()));
			return true;
		} else if (protocol.checkType(HP.code.UNDO_SIGN_UP_C)){
			onUndoSignUp(protocol.parseProtocol(HPUndoSignUp.getDefaultInstance()));
			return true;
		} else if (protocol.checkType(HP.code.CREATE_TEAM_C)){
			onCreateTeam(protocol.parseProtocol(HPCreateTeam.getDefaultInstance()));
			return true;
		} else if (protocol.checkType(HP.code.MANAGE_MEMBER_C) || protocol.checkType(HP.code.REFRESH_MEMBER_C)){
			syncTeamMemberListS();
			return true;
		} else if (protocol.checkType(HP.code.KICK_MEMBER_C)){
			onKickUpMember(protocol.parseProtocol(HPKickMember.getDefaultInstance()));
			return true;
		} else if (protocol.checkType(HP.code.MAIL_BATTLE_C)){
			onMailViewTeam(protocol.parseProtocol(OPMailViewTeam.getDefaultInstance()));
			return true;
		}

		return super.onProtocol(protocol);
	}
	
	/**
	 * 定时更新
	 */
	@Override
	public boolean onTick() {
		TeamBattleManager teamBattleMan = TeamBattleManager.getInstance();
		int state = teamBattleMan.getCurBattleState();
		if(isPushNotice && state == GsConst.TeamBattle.STATE_PREPARE 
				&& player.getFightValue() >= 1500 && !teamBattleMan.isAlreadySignUp(player)){
			pushTeamBattleSignUpNotice();
		}
		return true;
	}
	
	/**
	 * 玩家上线处理
	 * 
	 * @return
	 */
	@Override
	protected boolean onPlayerLogin() {
		isPushNotice = true;
		return true;
	}

	/**
	 * 向前端推送团战报名提醒
	 */
	private void pushTeamBattleSignUpNotice() {
		HPNotice.Builder ret = HPNotice.newBuilder();
		NoticeItem.Builder notice = NoticeItem.newBuilder();
		notice.setNoticeType(Const.NoticeType.TEAM_BATTLE_SIGNUP);
		ret.addNotices(notice);
		sendProtocol(Protocol.valueOf(HP.code.NOTICE_PUSH, ret));
		isPushNotice = false;
	}
	
	/**
	 * 同步团战页面基本信息
	 */
	private void syncTeamBattleInfoS() {
		TeamBattleManager teamBattleMan = TeamBattleManager.getInstance();
		HPTeamBattleSyncS.Builder ret = HPTeamBattleSyncS.newBuilder();
		ret.setStageId(teamBattleMan.getManStageId());
		ret.setBattleState(teamBattleMan.getCurBattleState());
		ret.setPeriod(Math.max(0, teamBattleMan.getNextStatePeroid() - GuaJiTime.getSeconds()));
		ret.setIsJoin(teamBattleMan.isAlreadySignUp(player) ? GsConst.TeamBattle.IS_JOIN : GsConst.TeamBattle.HAS_NOT_JOIN);
		ret.setIsCaptain(teamBattleMan.getMyTeamMemberInfo(player) != null ? GsConst.TeamBattle.IS_CAPTAIN : GsConst.TeamBattle.IS_NOT_CAPTAIN);
		sendProtocol(Protocol.valueOf(HP.code.TEAM_BATTLE_SYNC_S, ret));
	}

	/**
	 * 同步团队成员列表
	 */
	private void syncTeamMemberListS() {
		TeamBattleManager teamBattleMan = TeamBattleManager.getInstance();
		List<MemberInfo.Builder> memberInfos = teamBattleMan.getMyTeamMemberInfo(player);
		if(memberInfos == null) return;

		HPTeamMemberSyncS.Builder ret = HPTeamMemberSyncS.newBuilder();
		for(MemberInfo.Builder member : memberInfos){
			ret.addMemberInfo(member);
		}
		// 根据踢人次数计算扣费
		int myTeamId = teamBattleMan.getMyTeamId(player);
		int kickTimes = teamBattleMan.getCaptainKickTimes(myTeamId);
		if(kickTimes < 0) return;
		ret.setKickCount(kickTimes);
		sendProtocol(Protocol.valueOf(HP.code.TEAM_MEMBER_SYNC_S, ret));
	}

	/**
	 * 加入团队
	 * @param hpJoinTeam 
	 */
	private void onJoinTeam(HPJoinTeam hpJoinTeam) {
		TeamBattleManager teamBattleMan = TeamBattleManager.getInstance();
		if(teamBattleMan.getCurBattleState() != GsConst.TeamBattle.STATE_PREPARE){
			// 准备时间以过，请等待下次团战
			sendError(HP.code.JOIN_TEAM_C_VALUE, Status.error.TEAM_BATTLE_NOT_IN_PREPARE_VALUE);
			return;
		}
		
		if(!teamBattleMan.signUp(player)){
			// 你已经在队伍中
			sendError(HP.code.JOIN_TEAM_C_VALUE, Status.error.ALREADY_IN_TEAM_VALUE);
			return;
		}
		syncTeamBattleInfoS();
		int stageId = TeamBattleManager.getInstance().getManStageId();
		BehaviorLogger.log4Platform(player, Action.TAKE_PART_IN_TEAM_BATTLE, Params.valueOf("teamBattleStageId", stageId));
		//add by weiyong
		BehaviorLogger.log4Service(player, Source.USER_OPERATION, Action.TAKE_PART_IN_TEAM_BATTLE, Params.valueOf("teamBattleStageId", stageId));
	}

	/**
	 * 创建团队
	 * @param hpCreateTeam 
	 */
	private void onCreateTeam(HPCreateTeam hpCreateTeam) {
		TeamBattleManager teamBattleMan = TeamBattleManager.getInstance();
		if(teamBattleMan.getCurBattleState() != GsConst.TeamBattle.STATE_PREPARE){
			// 准备时间以过，请等待下次团战
			sendError(HP.code.CREATE_TEAM_C_VALUE, Status.error.TEAM_BATTLE_NOT_IN_PREPARE_VALUE);
			return;
		}
		
		int price = SysBasicCfg.getInstance().getCreateTeamPrice();
		if(player.getGold() < price){
			// 钻石不足
			sendError(HP.code.CREATE_TEAM_C_VALUE, Status.error.GOLD_NOT_ENOUGH_VALUE);
			isPushNotice = true;
			return;
		}
		
		if(!teamBattleMan.createTeam(player)){
			// 你已经在队伍中
			sendError(HP.code.CREATE_TEAM_C_VALUE, Status.error.ALREADY_IN_TEAM_VALUE);
			isPushNotice = true;
			return;
		}
		// 推送消耗
		player.consumeGold(price, Action.TEAM_BATTLE_CREATE_TEAM);
		ConsumeItems.valueOf(changeType.CHANGE_GOLD, price).pushChange(player);
		
		syncTeamBattleInfoS();
		
		int stageId = TeamBattleManager.getInstance().getManStageId();
		BehaviorLogger.log4Platform(player, Action.TAKE_PART_IN_TEAM_BATTLE, Params.valueOf("teamBattleStageId", stageId));
	}

	/**
	 * 撤销报名
	 * @param hpUndoSignUp 
	 */
	private void onUndoSignUp(HPUndoSignUp hpUndoSignUp) {
		TeamBattleManager teamBattleMan = TeamBattleManager.getInstance();
		isPushNotice = true;
		if(teamBattleMan.getCurBattleState() != GsConst.TeamBattle.STATE_PREPARE){
			// 本次团战已开始，已无法撤销报名
			sendError(HP.code.UNDO_SIGN_UP_C_VALUE, Status.error.TEAM_BATTLE_STARTED_VALUE);
			return;
		}
		teamBattleMan.undoSignUp(player);
		//add by weiyong
		BehaviorLogger.log4Service(player, Source.USER_OPERATION, Action.CANCEL_TEAM_BATTLE, Params.valueOf("teamBattleStageId", teamBattleMan.getManStageId()));
		syncTeamBattleInfoS();
	}

	/**
	 * 踢出成员
	 * @param hpKickMember 
	 */
	private void onKickUpMember(HPKickMember hpKickMember) {
		TeamBattleManager teamBattleMan = TeamBattleManager.getInstance();
		int memberPlayerId = hpKickMember.getKickPlayerId();
		if(memberPlayerId == player.getId()){
			return;
		}
		
		if(teamBattleMan.getMyTeamMemberInfo(player) == null){
			// 你不是队长无权删除成员
			sendError(HP.code.KICK_MEMBER_C_VALUE, Status.error.ALREADY_IN_TEAM_VALUE);
			return;
		}
		
		// 根据踢人次数计算扣费
		int myTeamId = teamBattleMan.getMyTeamId(player);
		int kickTimes = teamBattleMan.getCaptainKickTimes(myTeamId);
		if(kickTimes < 0) return;
		
		int price = Math.min((kickTimes+1)*SysBasicCfg.getInstance().getTeamBattleKickMemberCostAdd(), SysBasicCfg.getInstance().getTeamBattleKickMemberCostMax());
		if(GJLocal.isLocal(GJLocal.KOREAN)) {
			price = Math.min((kickTimes)*SysBasicCfg.getInstance().getTeamBattleKickMemberCostAdd()+10, SysBasicCfg.getInstance().getTeamBattleKickMemberCostMax());
		}
		if(player.getGold() < price){
			// 钻石不足
			sendError(HP.code.CREATE_TEAM_C_VALUE, Status.error.GOLD_NOT_ENOUGH_VALUE);
			return;
		}
		
		// 推送消耗
		player.consumeGold(price, Action.TEAM_BATTLE_KICK_UP_MEMBER);
		ConsumeItems.valueOf(changeType.CHANGE_GOLD, price).pushChange(player);
		
		teamBattleMan.kickTeamMember(player, memberPlayerId);
		syncTeamMemberListS();
	}
	
	/**
	 * 查看团战战报
	 * @param parseProtocol
	 */
	private void onMailViewTeam(OPMailViewTeam parseProtocol) {
		String myHistoryTeamId = parseProtocol.getTeamId();
		
		OPMailViewTeamRet.Builder ret = OPMailViewTeamRet.newBuilder();
		ret.setVersion(1);// 防止空包
		
		List<TeamBattleReportEntity> reports = DBManager.getInstance().query("from TeamBattleReportEntity where leftTeamId = ?", Integer.valueOf(myHistoryTeamId));
		if(reports.size() > 0){
			for(TeamBattleReportEntity report : reports){
				ret.addRounds(report.convertTeamRoundInfo());
			}
		}
			
		reports = DBManager.getInstance().query("from TeamBattleReportEntity where rightTeamId = ?", Integer.valueOf(myHistoryTeamId));
		if(reports.size() > 0){
			for(TeamBattleReportEntity report : reports){
				// 视角反转
				TeamRoundInfo.Builder builder = report.convertTeamRoundInfo();
				String leftName = builder.getLeftTeamName();
				boolean leftIsWin = builder.getIsWin();
				for(TbPlayerVSInfo.Builder vsInfoBuilder : builder.getVsInfoBuilderList()){
					TbPlayerInfo.Builder left = vsInfoBuilder.getLeftPlayerBuilder();
					vsInfoBuilder.setLeftPlayer(vsInfoBuilder.getRightPlayerBuilder());
					vsInfoBuilder.setRightPlayer(left);
				}
				builder.setLeftTeamName(builder.getRightTeamName());
				builder.setRightTeamName(leftName);
				builder.setIsWin(!leftIsWin);
				ret.addRounds(builder);
			}
		}
		
		sendProtocol(Protocol.valueOf(HP.code.MAIL_BATTLE_S, ret));
	}
}
