package com.guaji.game.module.alliance;

import java.util.Calendar;

import org.guaji.app.AppObj;
import org.guaji.msg.Msg;
import org.guaji.net.protocol.Protocol;
import org.guaji.net.protocol.IProtocolHandler;
import org.guaji.os.MyException;
import org.guaji.os.GuaJiTime;
import org.guaji.xid.GuaJiXID;

import com.guaji.game.protocol.Alliance.HPAllianceBossFunOpenC;
import com.guaji.game.protocol.Const.changeType;
import com.guaji.game.GsApp;
import com.guaji.game.config.AllianceCfg;
import com.guaji.game.config.SysBasicCfg;
import com.guaji.game.entity.AllianceEntity;
import com.guaji.game.entity.PlayerAllianceEntity;
import com.guaji.game.item.ConsumeItems;
import com.guaji.game.log.BehaviorLogger;
import com.guaji.game.log.BehaviorLogger.Action;
import com.guaji.game.log.BehaviorLogger.Params;
import com.guaji.game.log.BehaviorLogger.Source;
import com.guaji.game.manager.AllianceManager;
import com.guaji.game.manager.MailManager;
import com.guaji.game.manager.SnapShotManager;
import com.guaji.game.player.Player;
import com.guaji.game.util.AllianceUtil;
import com.guaji.game.util.GsConst;
import com.guaji.game.util.PlayerUtil;
import com.guaji.game.protocol.HP;
import com.guaji.game.protocol.Mail.MailType;
import com.guaji.game.protocol.Snapshot.PlayerSnapshotInfo;
import com.guaji.game.protocol.Status;

/**
 * 开启Boss功能
 */
public class AllianceBossFunOpenHandler implements IProtocolHandler {
	
	/**
	 * 开启BOSS战
	 */
	private final int FUN_OPEN = 1;
	
	/**
	 * 加入Boss战
	 */
	private final int FUN_JOIN = 2;
	
	/**
	 * Boss战鼓舞
	 */
	private final int FUN_ADDPROP = 3;
	
	/**
	 * 单次鼓舞价格
	 */
	private final int GOLD = 20;

	@Override
	public boolean onProtocol(AppObj appObj, Protocol protocol) {
		
		Player player = (Player) appObj;
		// 玩家公会数据
		PlayerAllianceEntity playerAlliance = player.getPlayerData().getPlayerAllianceEntity();
		int allianceId = playerAlliance.getAllianceId();
		// 公会数据
		AllianceEntity entity = AllianceManager.getInstance().getAlliance(allianceId);
		if (entity == null) {
			player.sendError(protocol.getType(), Status.error.ALLIANCE_NONEXISTENT);
			return true;
		}
		// 公会等级配置
		AllianceCfg allianceCfg = AllianceUtil.getAllianceCfg(entity.getLevel(), entity.getExp());
		if (allianceCfg == null) {
			player.sendError(protocol.getType(), Status.error.DATA_NOT_FOUND);
			return true;
		}
		Calendar today = Calendar.getInstance();
		int todayWeek = today.get(Calendar.DAY_OF_WEEK);
		HPAllianceBossFunOpenC par = protocol.parseProtocol(HPAllianceBossFunOpenC.getDefaultInstance());
		if (par.getOperType() == FUN_OPEN) {
			if (player.getPlayerData().getId() != entity.getPlayerId() && playerAlliance.getPostion() != GsConst.Alliance.ALLIANCE_POS_COPYMAIN) {
				// 没有权限开启公会BOSS
				player.sendError(protocol.getType(), Status.error.ALLIANCE_NO_MAIN);
				return true;
			}
			// BOSS正在开启中
			if (entity.isBossOpen()) {
				player.sendError(protocol.getType(), Status.error.ALLIANCE_BOSS_OPEN_ERROR);
				return true;
			}
			// 公会Boss元气值不足
			if (entity.getBossVitality() < allianceCfg.getOpenBossVitality()) {
				player.sendError(protocol.getType(), Status.error.BOSS_VITALITY_LACK);
				return true;
			}
			// 公会Boss开启次数不足
			if (entity.getEverydayBossOpenTimes() + 1 > entity.getLevel() / 5 + 2) {
				player.sendError(protocol.getType(), Status.error.BOSS_OPEN_TIMES);
				return true;
			}
			if(!allianceCfg.getDayOfWeekSet().contains(todayWeek)){
				//boss开启的日期不对
				player.sendError(protocol.getType(), Status.error.BOSS_OPEN_TIMES);
				return true;
			}
			// 更新数据
			entity.deductBossVitality(allianceCfg.getOpenBossVitality());
			entity.setBossOpenSizeAdd();
			entity.setBossOpen(true);
			entity.setBossId(allianceCfg.getBossId());
			entity.setBossHp(allianceCfg.getBossHp());
			entity.setEverydayBossOpenTimes(entity.getEverydayBossOpenTimes() + 1);
			// 分钟为单位
			entity.setBossMaxTime(allianceCfg.getTime() * 60000);
			entity.setBossAttTime(0l);
			entity.notifyUpdate(true);
//			sendAllianceMemberNotice(allianceId);
			//add by weiyong
			BehaviorLogger.log4Service(player, Source.USER_OPERATION, Action.ALLIANCE_OPEN_BOSS, Params.valueOf("bossId", allianceCfg.getBossId()), Params.valueOf("bossHP", allianceCfg.getBossHp()));
			int postion = playerAlliance.getPostion();
			//自动将vip3的人加入战斗
			for(int memberId : entity.getMemberList()) {
				if(memberId > 0 && !entity.getBossJoinMap().containsKey(memberId)) {
					PlayerSnapshotInfo.Builder snapShotInfo = SnapShotManager.getInstance().getPlayerSnapShot(memberId);
					if(snapShotInfo != null) {
						// 手动解禁BOSS成功通知邮件
						MailManager.createSysMail(memberId, MailType.Normal_VALUE, GsConst.MailId.ALLIANCE_LIFT_BOSS, "手动解禁公会BOSS", null, GuaJiTime.getTimeString(), "" + String.format("@GuildPosition%d", postion), player.getName());
						if (snapShotInfo.getAllianceInfo().getAutoFight() > 0) {
							try {
								if (PlayerUtil.deductAutoAllianceBossGold(snapShotInfo, SysBasicCfg.getInstance().getAllianceAutoFightCostGold(), allianceId)){
									entity.getBossJoinMap().put(memberId, 0);
									//加入boss战 先砍一刀
									Msg msg = Msg.valueOf(GsConst.MsgType.SINGLE_ATTACK_ALLIANCE_BOSS,GuaJiXID.valueOf(GsConst.ObjType.MANAGER, GsConst.ObjId.ALLIANCE));
									msg.pushParam(entity);
									msg.pushParam(memberId);
									GsApp.getInstance().postMsg(msg);
									Player target = PlayerUtil.queryPlayer(memberId);
									if(target != null && target.isOnline()) {
										target.sendProtocol(Protocol.valueOf(HP.code.ALLIANCE_CREATE_S_VALUE, AllianceManager.getInstance().getAllianceInfo(entity, target.getId(), target.getGold())));
									}
								}
							} catch (Exception e) {
								MyException.catchException(e);
							}
						}
						
					}
				}
			}
			
			BehaviorLogger.log4Platform(player, Action.ALLIANCE_OPEN_BOSS, Params.valueOf("alliacneId", allianceId), Params.valueOf("costGold", 0), Params.valueOf("costBossVitality", allianceCfg.getOpenBossVitality()));
			
		} else if (par.getOperType() == FUN_JOIN) {
			if(entity.isBossOpen()) {
				if (!entity.getBossJoinMap().containsKey(player.getId())) {
					entity.getBossJoinMap().put(player.getId(), 0);
					//加入boss战 先砍一刀
					Msg hawkMsg = Msg.valueOf(GsConst.MsgType.SINGLE_ATTACK_ALLIANCE_BOSS,GuaJiXID.valueOf(GsConst.ObjType.MANAGER, GsConst.ObjId.ALLIANCE));
					hawkMsg.pushParam(entity);
					hawkMsg.pushParam(player.getId());
					GsApp.getInstance().postMsg(hawkMsg);
				}
				BehaviorLogger.log4Platform(player, Action.JOIN_ALLIANCE_BOSS, Params.valueOf("allianceId", entity.getId()), Params.valueOf("isAuto", "false"));
				BehaviorLogger.log4Service(player, Source.USER_OPERATION, Action.JOIN_ALLIANCE_BOSS, Params.valueOf("allianceId", entity.getId()), Params.valueOf("isAuto", "false"));
			}
		} else if (par.getOperType() == FUN_ADDPROP) {
			if (player.getGold() < GOLD) {
				player.sendError(protocol.getType(), Status.error.GOLD_NOT_ENOUGH);
				return true;
			}
			if (entity.getPlayerAddMap(player.getId()) >= SysBasicCfg.getInstance().getAllianceBossAddPorpNumber()) {
				player.sendError(protocol.getType(), Status.error.ALLIANCE_BOSS_ADDPROP);
				return true;
			}
			ConsumeItems.valueOf(changeType.CHANGE_GOLD, GOLD).consumeTakeAffect(player, Action.ALLIANCE_CONSUME_ADD_PROP);
			entity.putPlayerAddMap(player.getId());
			
			player.sendStatus(HP.code.ALLIANCE_CREATE_S_VALUE, Status.error.ALLIANCE_BOSS_HARM_ADD_SUC_VALUE);
		}
		entity.notifyUpdate(true);
		player.sendProtocol(Protocol.valueOf(HP.code.ALLIANCE_CREATE_S_VALUE, AllianceManager.getInstance().getAllianceInfo(entity, player.getId(), player.getGold())));
		
		return true;
	}

//	private void sendAllianceMemberNotice(int allianceId) {
//		Map<Integer, GuaJiSession> m = ChatManager.getInstance().getAllianceSession(allianceId);
//		for (GuaJiSession session : m.values()) {
//			if (session != null)
//				MissionUtil.sendNotice(session, NoticeType.ALLIANCE_BOSS_OPEN);
//		}
//	}
}
