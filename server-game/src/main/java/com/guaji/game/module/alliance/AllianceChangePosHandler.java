package com.guaji.game.module.alliance;

import java.util.TreeMap;

import org.guaji.app.AppObj;
import org.guaji.log.Log;
import org.guaji.msg.Msg;
import org.guaji.net.protocol.Protocol;
import org.guaji.net.protocol.IProtocolHandler;
import org.guaji.obj.ObjBase;
import org.guaji.os.GuaJiTime;
import org.guaji.xid.GuaJiXID;

import com.guaji.game.protocol.Alliance.HPAllianceEnterS;
import com.guaji.game.protocol.Alliance.HPAllianceOperC;
import com.guaji.game.protocol.AllianceBattle.AllianceBattleState;
import com.guaji.game.GsApp;
import com.guaji.game.config.GvgCfg;
import com.guaji.game.config.SysBasicCfg;
import com.guaji.game.entity.AllianceBattleItem;
import com.guaji.game.entity.AllianceEntity;
import com.guaji.game.entity.GvgCityEntity;
import com.guaji.game.entity.PlayerAllianceEntity;
import com.guaji.game.manager.AllianceBattleManager;
import com.guaji.game.manager.AllianceManager;
import com.guaji.game.manager.ChatManager;
import com.guaji.game.manager.SnapShotManager;
import com.guaji.game.manager.gvg.GvgService;
import com.guaji.game.player.Player;
import com.guaji.game.util.ActivityUtil;
import com.guaji.game.util.GameUtil;
import com.guaji.game.util.GsConst;
import com.guaji.game.protocol.Const;
import com.guaji.game.protocol.Const.AllianCheckButton;
import com.guaji.game.protocol.Const.ApplyAddAllianceUpEmail;
import com.guaji.game.protocol.HP;
import com.guaji.game.protocol.Status;

/**
 * 改变公会成员职位
 */
public class AllianceChangePosHandler implements IProtocolHandler {
	
	/**
	 * 协议操作子编号
	 */
	private final int CHANGE_MAIN = 1;		// 更换会长
	private final int CHANGE_COPYMAIN = 2;// 提升副会长
	private final int CHANGE_JOIN = 3;		// 加入公会
	private final int CHANGE_EXIT = 4;		// 退出
	private final int CHANGE_COMMAND = 5;	// 转成普通成员
	private final int CHANGE_KICK = 6;		// 踢人
	// 退出后允许再加入的时间差
	private final long EXITTIME = (60 * 60000) * 24;
	
	@Override
	public boolean onProtocol(AppObj appObj, Protocol protocol) {
		Player player = (Player) appObj;
		PlayerAllianceEntity myPlayerAllianceEntity = player.getPlayerData().getPlayerAllianceEntity();
		int allianceId = myPlayerAllianceEntity.getAllianceId(); 
		AllianceEntity allianceEntity = AllianceManager.getInstance().getAlliance(allianceId);
		
		HPAllianceOperC par = protocol.parseProtocol(HPAllianceOperC.getDefaultInstance());
		int operation = par.getOperType();
		if(allianceEntity == null && operation != CHANGE_JOIN){
			// 公会不存在
			player.sendError(protocol.getType(), Status.error.ALLIANCE_NONEXISTENT);
			return true;
		}
		
		if(operation != CHANGE_JOIN && operation != CHANGE_EXIT){
			if(player.getId() != allianceEntity.getPlayerId() || player.getId() == par.getTargetId()){
				// 权限不足
				player.sendError(protocol.getType(), Status.error.ALLIANCE_NO_MAIN);
				return true;
			}
		}
		
		// 调试日志
		Log.logPrintln(String.format("ConfigManager operationType: %d, targetId: %d", par.getOperType(), par.getTargetId()));
		
		if(operation == CHANGE_MAIN){
			changeMain(player, allianceEntity, myPlayerAllianceEntity, par.getTargetId());
		} else if (operation == CHANGE_COPYMAIN){
			changeCopyMain(player, allianceEntity, par.getTargetId());
		} else if (operation == CHANGE_JOIN){
			joinAlliance(player, protocol, myPlayerAllianceEntity, par.getTargetId());
			return true;
		} else if (operation == CHANGE_EXIT){
			exitAlliance(player, protocol, allianceEntity, myPlayerAllianceEntity);
			return true;
		} else if (operation == CHANGE_COMMAND) { //解除副会长
			changeCommon(player, allianceEntity, par.getTargetId());
		} else if (operation == CHANGE_KICK){ //踢人
			changeKick(player, allianceEntity, par.getTargetId());
		}
		player.sendProtocol(Protocol.valueOf(HP.code.ALLIANCE_MEMBER_S, AllianceManager.getInstance().getAllianceMember(allianceEntity)));
		return true;
	}
	
	/**
	 * 更换会长
	 * @param player 操作玩家对象
	 * @param allianceEntity 所在AllianceEntity
	 * @param myPlayerAllianceEntity 老会长PlayerAllianceEntity
	 * @param targetPlayerId 新会长playerId
	 */
	private void changeMain(Player player, AllianceEntity allianceEntity, PlayerAllianceEntity myPlayerAllianceEntity, int targetPlayerId){
		AllianceManager.getInstance().changeMain(allianceEntity, myPlayerAllianceEntity, targetPlayerId);
		AllianceManager.getInstance().sendSelfData(HPAllianceEnterS.newBuilder(), player, allianceEntity);
	}
	/**
	 * 提升副会长
	 * @param player 操作玩家对象
	 * @param allianceEntity 所在AllianceEntity
	 * @param targetPlayerId 新会长playerId
	 */
	private void changeCopyMain(Player player, AllianceEntity allianceEntity, int targetPlayerId){
		GuaJiXID targetXid = GuaJiXID.valueOf(GsConst.ObjType.PLAYER, targetPlayerId);
		ObjBase<GuaJiXID, AppObj> objBase = GsApp.getInstance().lockObject(targetXid);
		try {
			if (objBase != null && objBase.isObjValid()) {
				Player targetPlayer = (Player) objBase.getImpl();
				Log.logPrintln(String.format("ConfigManager::changeCopyMain 1、playerId %d targetId %d", player.getId(), targetPlayerId));
				PlayerAllianceEntity targetPlayerAllianceEntity = targetPlayer.getPlayerData().getPlayerAllianceEntity();
				targetPlayerAllianceEntity.setPostion(GsConst.Alliance.ALLIANCE_POS_COPYMAIN);
				targetPlayerAllianceEntity.notifyUpdate(false);
				// 更新快照数据
				SnapShotManager.getInstance().getCurrentStrategy().onAllianceDataChanged(targetPlayerAllianceEntity);
				// 同步玩家公会数据
				if (targetPlayer.isOnline()) {
					AllianceManager.getInstance().sendSelfData(HPAllianceEnterS.newBuilder(), targetPlayer, allianceEntity);
				}
			} else {
				// 修改target玩家快照工会数据
				Log.logPrintln(String.format("ConfigManager::changeCopyMain 2、playerId %d targetId %d", player.getId(), targetPlayerId));
				AllianceManager.getInstance().updateOfflineAllianceData(targetPlayerId, GsConst.Alliance.ALLIANCE_POS_COPYMAIN, allianceEntity.getId());
			}
		} finally {
			if (objBase != null) {
				objBase.unlockObj();
			}
		}
	}
	
	/**
	 * 副会长转成普通成员
	 * @param player 操作玩家对象
	 * @param allianceEntity 所在AllianceEntity
	 * @param targetPlayerId 副会长playerId
	 */
	private void changeCommon(Player player, AllianceEntity allianceEntity, int targetPlayerId){
		GuaJiXID targetXid = GuaJiXID.valueOf(GsConst.ObjType.PLAYER, targetPlayerId);
		ObjBase<GuaJiXID, AppObj> objBase = GsApp.getInstance().lockObject(targetXid);
		try {
			if (objBase != null && objBase.isObjValid()) {
				Player targetPlayer = (Player) objBase.getImpl();
				Log.logPrintln(String.format("ConfigManager::changeCommon 1、playerId %d targetId %d", player.getId(), targetPlayerId));
				PlayerAllianceEntity targetPlayerAllianceEntity = targetPlayer.getPlayerData().getPlayerAllianceEntity();
				targetPlayerAllianceEntity.setPostion(GsConst.Alliance.ALLIANCE_POS_COMMON);
				targetPlayerAllianceEntity.notifyUpdate(false);
				// 更新快照数据
				SnapShotManager.getInstance().getCurrentStrategy().onAllianceDataChanged(targetPlayerAllianceEntity);
				// 同步玩家公会数据
				if (targetPlayer.isOnline()) {
					AllianceManager.getInstance().sendSelfData(HPAllianceEnterS.newBuilder(), targetPlayer, allianceEntity);
				}
			} else {
				Log.logPrintln(String.format("ConfigManager::changeCommon 2、playerId %d targetId %d", player.getId(), targetPlayerId));
				AllianceManager.getInstance().updateOfflineAllianceData(targetPlayerId, GsConst.Alliance.ALLIANCE_POS_COMMON, allianceEntity.getId());
			}
		} finally {
			if (objBase != null) {
				objBase.unlockObj();
			}
		}
	}
	
	/**
	 * 公会踢人
	 * @param player 操作玩家对象
	 * @param allianceEntity 所在AllianceEntity
	 * @param targetPlayerId 成员playerId
	 */
	private void changeKick(Player player, AllianceEntity allianceEntity, int targetPlayerId){
		GuaJiXID targetXid = GuaJiXID.valueOf(GsConst.ObjType.PLAYER, targetPlayerId);
		ObjBase<GuaJiXID, AppObj> objBase = GsApp.getInstance().lockObject(targetXid);
		try {
			if (objBase != null && objBase.isObjValid()) {
				Player targetPlayer = (Player)objBase.getImpl();
				allianceEntity.getMemberList().remove((Integer)targetPlayer.getId());
				allianceEntity.getBossJoinMap().remove((Integer)targetPlayer.getId());
				removeFromAllianceBattle(allianceEntity.getId(), targetPlayerId);
				//发送广播通知离开公会
				ChatManager.getInstance().addAllianceSession(player.getSession(), player.getPlayerData().getPlayerAllianceEntity().getAllianceId(), player.getId());
				String msg = SysBasicCfg.getInstance().getKICKAllianceChat();
				ChatManager.getInstance().postChat(player, ChatManager.getMsgJson(msg, targetPlayer.getName()) , Const.chatType.CHAT_ALLIANCE_SYSTEM_VALUE, 1);
				
				PlayerAllianceEntity targetPlayerAllianceEntity = targetPlayer.getPlayerData().getPlayerAllianceEntity();
				targetPlayerAllianceEntity.setAllianceId(0);
				targetPlayerAllianceEntity.setExitTime(System.currentTimeMillis() + EXITTIME );
				targetPlayerAllianceEntity.setPostion(GsConst.Alliance.ALLIANCE_POS_COMMON);
				targetPlayerAllianceEntity.notifyUpdate(false);
				AllianceManager.getInstance().sendSelfData(HPAllianceEnterS.newBuilder(), targetPlayer, allianceEntity);
				
				GameUtil.sendAllianceChatTag(targetPlayer);
				//发邮件通知踢出的会员和全体公会成员
				AllianceManager.getInstance().sendMailNotice(targetPlayer, allianceEntity, targetPlayerAllianceEntity, Const.AddOrExitAlliance.EXIT_ALLIANCE_TYPE_2_VALUE, Const.ExitAllianceState.MAIN_KICK_TYPE_2_VALUE);
			}else{
				allianceEntity.getMemberList().remove((Integer)targetPlayerId);
				AllianceManager.getInstance().updateOfflineAllianceData(targetPlayerId, GsConst.Alliance.ALLIANCE_POS_COMMON, 0);
			}
			// 移除聊天框皮肤
			ActivityUtil.changeChatSkin(targetPlayerId, GvgCfg.getInstance().getSkinId(), 2);
		} finally {
			if (objBase != null) {
				objBase.unlockObj();
			}
		}
		
		//客户端推送
		player.sendProtocol(Protocol.valueOf(HP.code.ALLIANCE_CREATE_S_VALUE, AllianceManager.getInstance().getAllianceInfo(allianceEntity, player.getId(), player.getGold())));
		
		// 踢人 扣除 30点经验
		int exp = allianceEntity.getExp();
		int subExp = SysBasicCfg.getInstance().getKickAlliamceMemberExp();
		allianceEntity.setExp(exp >= subExp ? exp - subExp : 0);
		allianceEntity.getBossJoinMap().remove(targetPlayerId);
		allianceEntity.notifyUpdate(true);
		AllianceManager.getInstance().sendSelfData(HPAllianceEnterS.newBuilder(), player, allianceEntity);
		
		GsApp.getInstance().postMsg(GuaJiXID.valueOf(GsConst.ObjType.MANAGER,GsConst.ObjId.ALLIANCE_BATTLE), 
				Msg.valueOf(GsConst.MsgType.ALLIANCE_MEMBER_REMOVE).pushParam(allianceEntity.getId(), targetPlayerId));
	}
	/**
	 * 加入公会
	 * @param player
	 * @param protocol
	 * @param myPlayerAllianceEntity 个人公会信息
	 * @param targetAllianceId 目标公会id
	 */
	private void joinAlliance(Player player, Protocol protocol, PlayerAllianceEntity myPlayerAllianceEntity, int targetAllianceId){
		AllianceEntity targetAllianceEntity = AllianceManager.getInstance().getAlliance(targetAllianceId);
		if(targetAllianceEntity.getHasCheckLeaderMail() == AllianCheckButton.CHECK_BUTTON_TYPE_1_VALUE) {
			int result = AllianceManager.getInstance().checkAddAlliance(player, targetAllianceEntity, player, protocol.getType(), myPlayerAllianceEntity);
			if(result < 0) {
				return;
			}
			AllianceManager.getInstance().joinAlliance(player, myPlayerAllianceEntity, targetAllianceEntity, protocol.getType(), true, Const.AddAllianceState.INITIATIVE_ADD_TYPE_1_VALUE);
			myPlayerAllianceEntity.notifyUpdate(false);
		}else {
			//不可以直接加入公会
			player.sendError(protocol.getType(), Status.error.CHECK_BUTTON_ALLIANCE_FAIL);
			return;
		}
		// 添加聊天框皮肤
		TreeMap<Integer, GvgCityEntity> cities = GvgService.getInstance().getCities();
		for (GvgCityEntity cityEntity : cities.values()) {
			if (targetAllianceId != cityEntity.getHolderId()) {
				continue;
			}
			if (cityEntity.getCityId() == GvgCfg.getInstance().getCityId()) {
				ActivityUtil.changeChatSkin(player.getId(), GvgCfg.getInstance().getSkinId(), 1);
				return;
			}
		}
	}
	
	/**
	 * 退出公会
	 * @param player
	 * @param protocol
	 * @param allianceEntity
	 * @param myPlayerAllianceEntity
	 */
	private void exitAlliance(Player player, Protocol protocol, AllianceEntity allianceEntity, PlayerAllianceEntity myPlayerAllianceEntity){	
		if (allianceEntity.getMemberList().size() > 1) {
			if (player.getId() == allianceEntity.getPlayerId()) {
				// 公会有多个成员会长不能退出
				player.sendError(protocol.getType(), Status.error.ALLIANCE_EXIT_ERROR);
				return;
			}
			// 将这个玩家从公会成员中删除
			allianceEntity.getMemberList().remove((Integer) player.getId());
			// 这个操作没用，因为BossJoinMap从头至尾没有被使用
			allianceEntity.getBossJoinMap().remove((Integer) player.getId());
			// 将如果这个玩家在公会战准备阶段之前不在公会里，则将这个人的信息从公会战中也清除掉
			removeFromAllianceBattle(allianceEntity.getId(), player.getId());
			GsApp.getInstance().postMsg(GuaJiXID.valueOf(GsConst.ObjType.MANAGER,GsConst.ObjId.ALLIANCE_BATTLE),Msg.valueOf(GsConst.MsgType.ALLIANCE_MEMBER_REMOVE).pushParam(allianceEntity.getId(), player.getId()));
		} else {
			// 如果当前会长还存在未确认的申请加入邮件，则进行删除
			AllianceManager.getInstance().deleteApplyAddAllianceMsg(player, allianceEntity.getPlayerId(), allianceEntity, ApplyAddAllianceUpEmail.UPEMAIL_TYPE_2_VALUE);
			// 解除公会
			allianceEntity.delete();
			allianceEntity.getMemberList().clear();
			allianceEntity.notifyUpdate(true);
			
			GsApp.getInstance().postMsg(GuaJiXID.valueOf(GsConst.ObjType.MANAGER,GsConst.ObjId.ALLIANCE_BATTLE),Msg.valueOf(GsConst.MsgType.ALLIANCE_REMOVE).pushParam(allianceEntity.getId()));
			
			AllianceManager.getInstance().getAllianceMap().remove(allianceEntity.getId());
		}
		// 移除聊天框皮肤
		ActivityUtil.changeChatSkin(player.getId(), GvgCfg.getInstance().getSkinId(), 2);
		//发送广播通知离开公会
		ChatManager.getInstance().removeAllaiceSession(allianceEntity.getId(), player.getId());
		String msg = SysBasicCfg.getInstance().getExitAllianceChat();
		ChatManager.getInstance().postChat(player, ChatManager.getMsgJson(msg, player.getName()) , Const.chatType.CHAT_ALLIANCE_SYSTEM_VALUE, 1);
		
		myPlayerAllianceEntity.setPostion(GsConst.Alliance.ALLIANCE_POS_COMMON);
		myPlayerAllianceEntity.setAllianceId(0);
		myPlayerAllianceEntity.setContribution(0);
		myPlayerAllianceEntity.setJoinTime(0);
		myPlayerAllianceEntity.setExitTime(GuaJiTime.getMillisecond() + EXITTIME );
		myPlayerAllianceEntity.notifyUpdate(true);
		
		// 刷新快照
		player.getPlayerData().refreshOnlinePlayerSnapshot();
		
		GameUtil.sendAllianceChatTag(player);
		
		//发邮件通知加入的会员和全体公会成员
		AllianceManager.getInstance().sendMailNotice(player, allianceEntity, myPlayerAllianceEntity, Const.AddOrExitAlliance.EXIT_ALLIANCE_TYPE_2_VALUE, Const.ExitAllianceState.INITIATIVE_TYPE_1_VALUE);
	}
	
	/**
	 * 将如果这个玩家在公会战准备阶段之前不在公会里，则将这个人的信息从公会战中也清除掉
	 */
	private void removeFromAllianceBattle(int allianaceId, int playerId) {
		AllianceBattleState curBattleState = AllianceBattleManager.getInstance().getCurBattleState();
		if (null != curBattleState && (curBattleState == AllianceBattleState.PREPARE || curBattleState == AllianceBattleState.Draw_Lots_WAIT)) {
			AllianceBattleItem battleItem = AllianceBattleManager.getInstance().getBattleItem(allianaceId);
			if (battleItem != null) {
				battleItem.removeMem(playerId);
			}
		}
	}
}
