package com.guaji.game.module;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeSet;

import org.guaji.msg.Msg;
import org.guaji.net.protocol.Protocol;
import org.guaji.os.GuaJiTime;
import org.guaji.xid.GuaJiXID;

import com.guaji.game.GsApp;
import com.guaji.game.config.SysBasicCfg;
import com.guaji.game.entity.MsgEntity;
import com.guaji.game.entity.StateEntity;
import com.guaji.game.manager.SnapShotManager;
import com.guaji.game.player.Player;
import com.guaji.game.player.PlayerData;
import com.guaji.game.player.PlayerModule;
import com.guaji.game.util.BuilderUtil;
import com.guaji.game.util.GsConst;
import com.guaji.game.protocol.Const;
import com.guaji.game.protocol.HP;
import com.guaji.game.protocol.Msg.HPDeleteAllMsg;
import com.guaji.game.protocol.Msg.HPDeleteOnePlayerMsg;
import com.guaji.game.protocol.Msg.HPMsgPlayerList;
import com.guaji.game.protocol.Msg.HPMsgPlayerListRet;
import com.guaji.game.protocol.Msg.HPNewMsgSyncS;
import com.guaji.game.protocol.Msg.HPSeePlayerMsg;
import com.guaji.game.protocol.Msg.HPSeePlayerMsgRet;
import com.guaji.game.protocol.Msg.HPSendMsgToOthers;
import com.guaji.game.protocol.Msg.MsgInfo;
import com.guaji.game.protocol.Msg.MsgPlayerItemInfo;
import com.guaji.game.protocol.Notice.HPNotice;
import com.guaji.game.protocol.Notice.NoticeItem;
import com.guaji.game.protocol.Player.RoleInfo;
import com.guaji.game.protocol.Snapshot.PlayerSnapshotInfo;
import com.guaji.game.protocol.Status;

/**
 * 留言模块
 */
public class PlayerMsgModule extends PlayerModule {

	public PlayerMsgModule(Player player) {
		super(player);
		
		// 监听协议注册
		listenProto(HP.code.MSG_PLAYER_LIST_C);
		listenProto(HP.code.SEE_PLAYER_MSG_C);
		listenProto(HP.code.SEND_PLAYER_MSG_C);
		listenProto(HP.code.DEL_ONE_PLAYER_MSGS_C);
		listenProto(HP.code.DEL_ALL_PLAYER_MSGS_C);
		
		// 监听消息注册
		listenMsg(GsConst.MsgType.MSG_TO_ME);
	}

	/**
	 * 玩家上线处理
	 * 
	 * @return
	 */
	protected boolean onPlayerLogin() {
		player.getPlayerData().loadPlayerMsg();
		
		// 新留言提示
		if(player.getPlayerData().hasNewMsg()){
			MsgEntity noticeMsg = new MsgEntity(player.getId(), 0, "", 0);
			syncOneMsgS(noticeMsg);
		}
		return true;
	}

	/**
	 * 前端推送新留言提醒
	 */
	private void pushNewMsgNotice(){
		if(player.getPlayerData().hasNewMsg()){
			HPNotice.Builder ret = HPNotice.newBuilder();
			NoticeItem.Builder notice = NoticeItem.newBuilder();
			notice.setNoticeType(Const.NoticeType.NEW_MSG);
			ret.addNotices(notice);
			sendProtocol(Protocol.valueOf(HP.code.NOTICE_PUSH, ret));
		}
	}
	
	/**
	 * 消息处理
	 */
	public boolean onMessage(Msg msg) {
		if(msg.getMsg() == GsConst.MsgType.MSG_TO_ME){
			onRecvToMeMsg(msg);
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
		if (protocol.checkType(HP.code.MSG_PLAYER_LIST_C)) {
			onMsgPlayerList(protocol.parseProtocol(HPMsgPlayerList.getDefaultInstance()));
			return true;
		} else if (protocol.checkType(HP.code.SEE_PLAYER_MSG_C)) {
			onSeePlayerMsg(protocol.parseProtocol(HPSeePlayerMsg.getDefaultInstance()));
			return true;
		} else if (protocol.checkType(HP.code.SEND_PLAYER_MSG_C)) {
			onSendMsgToOthers(protocol.parseProtocol(HPSendMsgToOthers.getDefaultInstance()));
			return true;
		} else if(protocol.checkType(HP.code.DEL_ONE_PLAYER_MSGS_C)){
			onDelOnePlayerMsg(protocol.parseProtocol(HPDeleteOnePlayerMsg.getDefaultInstance()));
			return true;
		} else if(protocol.checkType(HP.code.DEL_ALL_PLAYER_MSGS_C)){
			onDelAllPlayerMsg(protocol.parseProtocol(HPDeleteAllMsg.getDefaultInstance()));
			return true;
		}
		
		return super.onProtocol(protocol);
	}

	/**
	 * 接收发给我的留言，然后推送给前端
	 */
	public void onRecvToMeMsg(Msg msg){
		PlayerData playerData = player.getPlayerData();
		MsgEntity msgEntity = (MsgEntity)msg.getParam(0);
		playerData.addOneToMeMsg(msgEntity);
		
		// 同步单条留言
		syncOneMsgS(msgEntity);
		
		// 新留言提醒
		pushNewMsgNotice();
	}
	
	/**
	 * 同步单条留言信息
	 * @param msgEntity
	 */
	private void syncOneMsgS(MsgEntity msgEntity){
		HPNewMsgSyncS.Builder msgSync = HPNewMsgSyncS.newBuilder();
		int sendPlayerId = msgEntity.getSenderId();
		
		int senderRoleCfgId = 0;
		if(sendPlayerId == player.getId()){
			senderRoleCfgId = player.getPlayerData().getMainRole().getItemId();
		} else {
			PlayerSnapshotInfo.Builder snapshot = SnapShotManager.getInstance().getPlayerSnapShot(sendPlayerId);
			senderRoleCfgId = snapshot.getMainRoleInfo().getItemId();
		}
		
		MsgInfo.Builder msgInfo = BuilderUtil.genMsgInfoBuilder(msgEntity, senderRoleCfgId);
		msgSync.setOneMsg(msgInfo);
		sendProtocol(Protocol.valueOf(HP.code.NEW_MSG_SYNC_S, msgSync));
	}

	/**
	 * 查看留言玩家列表
	 * @param parseProtocol
	 */
	private void onMsgPlayerList(HPMsgPlayerList parseProtocol) {
		PlayerData playerData = player.getPlayerData();
		Map<Integer, TreeSet<MsgEntity>> myAllMsg = playerData.getPlayerMsgs();
		
		HPMsgPlayerListRet.Builder ret = HPMsgPlayerListRet.newBuilder();
		for(Map.Entry<Integer, TreeSet<MsgEntity>> entry : myAllMsg.entrySet()){
			MsgPlayerItemInfo.Builder playerItemInfo = MsgPlayerItemInfo.newBuilder();
			playerItemInfo.setPlayerId(entry.getKey());
			
			PlayerSnapshotInfo.Builder snapshotInfo = SnapShotManager.getInstance().getPlayerSnapShot(playerItemInfo.getPlayerId());
			RoleInfo mainRoleInfo = snapshotInfo.getMainRoleInfo();
			playerItemInfo.setRoleId(mainRoleInfo.getItemId());
			playerItemInfo.setPlayerName(mainRoleInfo.getName());
			
			TreeSet<MsgEntity> msgs = entry.getValue();
			Iterator<MsgEntity> iter = msgs.iterator();
			MsgEntity lastMsg = null;
			while(iter.hasNext()) { // 遍历所有留言，显示最新的留言
				MsgEntity msg = iter.next();
				if(msg.getMsgType() == Const.FriendChatMsgType.LEAVE_MSG_VALUE) {
					lastMsg = msg;
					playerItemInfo.setLastMsg(lastMsg.getContent());
					playerItemInfo.setLastMsgTime(lastMsg.getCreateSysTime());
					int hasNewMsg = (lastMsg.getLastReadTime() > lastMsg.getCreateSysTime()) ? GsConst.Msg.HAS_NEW_MSG: GsConst.Msg.NO_NEW_MSG;
					playerItemInfo.setHasNewMsg(hasNewMsg);
				}
			}
			
			if(lastMsg != null){
				ret.addItemInfo(playerItemInfo);
			}
		}
		ret.setVersion(1);// 防止空包，前端接到空包会报错
		sendProtocol(Protocol.valueOf(HP.code.MSG_PLAYER_LIST_S, ret));
	}
	
	/**
	 * 查看详细的留言记录
	 * @param parseProtocol
	 */
	private void onSeePlayerMsg(HPSeePlayerMsg parseProtocol) {
		int sendPlayerId = parseProtocol.getPlayerId();
		PlayerSnapshotInfo.Builder snapshot = SnapShotManager.getInstance().getPlayerSnapShot(sendPlayerId);
		int senderRoleCfgId = snapshot.getMainRoleInfo().getItemId();
		
		PlayerData playerData = player.getPlayerData();
		Map<Integer, TreeSet<MsgEntity>> myAllMsg = playerData.getPlayerMsgs();
		HPSeePlayerMsgRet.Builder ret = HPSeePlayerMsgRet.newBuilder();
		if(myAllMsg.containsKey(sendPlayerId)){
			TreeSet<MsgEntity> msgs = myAllMsg.get(sendPlayerId);
			
			// 过滤聊天entity，只保留留言entity
			List<MsgEntity> msgList = new ArrayList<MsgEntity>();
			for(MsgEntity msgEntity : msgs){
				if(msgEntity.getMsgType() != Const.FriendChatMsgType.LEAVE_MSG_VALUE) {
					continue;
				}
				msgList.add(msgEntity);
			}
			
			// 超出系统限定留言数时间，删除最老的
			int curMsgQty = msgList.size();
			if(curMsgQty > SysBasicCfg.getInstance().getMaxMsgQty()){
				int overQty = curMsgQty - SysBasicCfg.getInstance().getMaxMsgQty();
				for(int i=0; i<overQty; i++){
					msgList.get(0).delete();
					msgList.remove(0);
				}
			}
			
			// 给协议填充留言
			for(MsgEntity msgEntity : msgList){
				int sId = 0;
				if(msgEntity.getSenderId() == player.getId()){
					// 我的头像
					sId = playerData.getMainRole().getItemId();
				}else{
					// 对方头像
					sId = senderRoleCfgId;
				}
				MsgInfo.Builder msgInfo = BuilderUtil.genMsgInfoBuilder(msgEntity, sId);
				ret.addMsg(msgInfo);
			}
			
			// 更新查看时间
			msgs.last().setLastReadTime(GuaJiTime.getSeconds());
			msgs.last().notifyUpdate(true);
		}
		ret.setVersion(1);// 防止空包，前端接到空包会报错
		sendProtocol(Protocol.valueOf(HP.code.SEE_PLAYER_MSG_S, ret));
	}
	
	/**
	 * 留言给别人
	 * @param parseProtocol 
	 */
	public void onSendMsgToOthers(HPSendMsgToOthers parseProtocol){
		int targetPlayerId = parseProtocol.getRecvPlayerId();
		int sourceModuleId = parseProtocol.getModuleId();
		String content = parseProtocol.getContent();
		
		if(targetPlayerId == player.getId()){
			// 请不要给自己留言
			sendError(HP.code.SEND_PLAYER_MSG_C_VALUE, Status.error.DONT_MSG_TO_SELF_VALUE);
			return;
		}
		
		// 字符超长则尾部截取
		if(content.length() > SysBasicCfg.getInstance().getMsgMaxLength()){
			content = content.substring(0, SysBasicCfg.getInstance().getMsgMaxLength());
		}
		
		// 如果竞技场挑战次数为0 则无法留言
		StateEntity state = player.getPlayerData().getStateEntity();
		if(state.getSurplusChallengeTimes() <= 0){
			sendError(HP.code.SEND_PLAYER_MSG_C_VALUE, Status.error.CAN_NOT_MSG_VALUE);
			return;
		}
		
		int skinId = player.getPlayerData().loadChatSkinEntity().getCurSkinId();
		MsgEntity msgEntity = player.getPlayerData().createPlayerMsg(targetPlayerId, skinId,content, sourceModuleId);
		if(msgEntity != null){
			GuaJiXID targetXID = GuaJiXID.valueOf(GsConst.ObjType.PLAYER, targetPlayerId);
			Msg msg = Msg.valueOf(GsConst.MsgType.MSG_TO_ME, targetXID, player.getXid());
			msg.pushParam(msgEntity);
			GsApp.getInstance().postMsg(msg);
		}
	}

	/**
	 * 删除某一玩家的留言
	 * @param hpDeleteOnePlayerMsg 
	 */
	public void onDelOnePlayerMsg(HPDeleteOnePlayerMsg hpDeleteOnePlayerMsg){
		int sendPlayerId = hpDeleteOnePlayerMsg.getPlayerId();
		PlayerData playerData = player.getPlayerData();
		Map<Integer, TreeSet<MsgEntity>> myAllMsg = playerData.getPlayerMsgs();
		if(myAllMsg.containsKey(sendPlayerId)){
			Iterator<MsgEntity> iter = myAllMsg.get(sendPlayerId).iterator();
			while(iter.hasNext()) {
				MsgEntity msg = iter.next();
				if(msg.getMsgType() == Const.FriendChatMsgType.LEAVE_MSG_VALUE) {
					msg.delete();
					iter.remove();
				}
			}
		}
	}
	
	/**
	 * 删除所有玩家的留言
	 * @param hpDeleteAllMsg 
	 */
	public void onDelAllPlayerMsg(HPDeleteAllMsg hpDeleteAllMsg){
		PlayerData playerData = player.getPlayerData();
		Map<Integer, TreeSet<MsgEntity>> myAllMsg = playerData.getPlayerMsgs();
		for(Map.Entry<Integer, TreeSet<MsgEntity>> entry : myAllMsg.entrySet()){
			TreeSet<MsgEntity> msgs = entry.getValue();
			Iterator<MsgEntity> iter = msgs.iterator();
			while(iter.hasNext()) {
				MsgEntity msg = iter.next();
				if(msg.getMsgType() == Const.FriendChatMsgType.LEAVE_MSG_VALUE) {
					msg.delete();
					iter.remove();
				}
			}
		}
	}
}
