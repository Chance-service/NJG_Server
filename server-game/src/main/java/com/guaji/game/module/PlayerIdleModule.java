package com.guaji.game.module;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.guaji.annotation.ProtocolHandlerAnno;
import org.guaji.app.App;
import org.guaji.db.DBManager;
import org.guaji.log.Log;
import org.guaji.msg.Msg;
import org.guaji.net.protocol.Protocol;
import org.guaji.thread.GuaJiTask;
import org.guaji.xid.GuaJiXID;

import com.guaji.game.GsApp;
import com.guaji.game.ServerData;
import com.guaji.game.config.SysBasicCfg;
import com.guaji.game.entity.FriendEntity;
import com.guaji.game.entity.IpAddrEntity;
import com.guaji.game.manager.ChatManager;
import com.guaji.game.manager.IpAddrManager;
import com.guaji.game.manager.SnapShotManager;
import com.guaji.game.player.Player;
import com.guaji.game.player.PlayerModule;
import com.guaji.game.util.BuilderUtil;
import com.guaji.game.util.GameUtil;
import com.guaji.game.util.GsConst;
import com.guaji.game.util.ProtoUtil;
import com.guaji.game.protocol.Const;
import com.guaji.game.protocol.Equip.EquipInfo;
import com.guaji.game.protocol.HP;
import com.guaji.game.protocol.Player.HPAssembleFinish;
import com.guaji.game.protocol.Player.RoleEquip;
import com.guaji.game.protocol.Player.RoleInfo;
import com.guaji.game.protocol.Snapshot.HPSeeMercenaryInfo;
import com.guaji.game.protocol.Snapshot.HPSeeMercenaryInfoRet;
import com.guaji.game.protocol.Snapshot.HPSeeOtherPlayerInfo;
import com.guaji.game.protocol.Snapshot.HPSeeOtherPlayerInfoRet;
import com.guaji.game.protocol.Snapshot.PlayerSnapshotInfo;
import com.guaji.game.protocol.Status;

/**
 * 空闲模块, 所有模块最后操作
 * 
 * @author hawk
 */
public class PlayerIdleModule extends PlayerModule {
	/**
	 * 构造
	 * 
	 * @param player
	 */
	public PlayerIdleModule(Player player) {
		super(player);
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

	/**
	 * 协议响应
	 * 
	 * @param protocol
	 * @return
	 */
	@Override
	public boolean onProtocol(Protocol protocol) {
		return super.onProtocol(protocol);
	}

	/**
	 * 玩家上线处理（派发完成模块加载）
	 * 
	 * @return
	 */
	@Override
	protected boolean onPlayerLogin() {
		// editby: tzy 组装流程错误。需要组装完成后再发消息
		// 最后通知组装完成
		sendProtocol(Protocol.valueOf(HP.code.ASSEMBLE_FINISH_S, HPAssembleFinish.newBuilder().setPlayerId(player.getPlayerData().getId())));

		// 通知玩家组装完成
		Msg msg = Msg.valueOf(GsConst.MsgType.PLAYER_ASSEMBLE, player.getXid());//派发模块组装完成事件
		App.getInstance().postMsg(msg);

		// 推送公会聊天状态
		GameUtil.sendAllianceChatTag(player);
		
		if(player.getPlayerData().getStateEntity().getElementBagSize() == 0) {
			player.getPlayerData().getStateEntity().setElementBagSize(SysBasicCfg.getInstance().getElementBagDefault());
			player.getPlayerData().getStateEntity().notifyUpdate(true);
		}

		return true;
	}

	/**
	 * 组装完成
	 */
	@Override
	protected boolean onPlayerAssemble() {
		// 设置组装状态
		player.setAssembleFinish(true);
//		// 最后通知进入游戏
		sendProtocol(Protocol.valueOf(HP.code.GAMEING_STATE_S, HPAssembleFinish.newBuilder().setPlayerId(player.getPlayerData().getId())));

		// 添加在线信息
		ServerData.getInstance().addOnlinePlayerId(player.getId());

		// 通知快照管理器删除离线快照数据
		if (ServerData.getInstance().isPlayerOnline(player.getId())) {
			GuaJiXID targetXId = GuaJiXID.valueOf(GsConst.ObjType.MANAGER, GsConst.ObjId.SNAPSHOT);
			Msg msg = Msg.valueOf(GsConst.MsgType.ONLINE_REMOVE_OFFLINE_SNAPSHOT, targetXId);
			msg.pushParam(player.getId());
			GsApp.getInstance().postMsg(msg);
		}

		// 发送客户端配置信息
		sendProtocol(Protocol.valueOf(HP.code.CLIENT_SETTING_PUSH_VALUE, BuilderUtil.genClientSetting()));

		// 捞取IP信息
		if (player.getSession() != null) {	
			Log.logPrintln("login address: " + player.getSession().getIpAddr());
			
			int ipInt = GameUtil.convertIP2Int(player.getSession().getIpAddr());
			List<IpAddrEntity> ipAddrEntities = DBManager.getInstance().query("from IpAddrEntity where ? between beginIpInt and endIpInt ", ipInt);
			IpAddrEntity ipAddrEntity = null;
			if(ipAddrEntities.size() > 0) {
				ipAddrEntity = ipAddrEntities.get(0);
				if(ipAddrEntity != null) {
					Log.logPrintln("login address: " + ipAddrEntity.getPosition());
				}
				player.getPlayerData().setIpAddrEntity(ipAddrEntity);
			}
			IpAddrManager.getInstance().add(ipAddrEntity, player.getId());
		}
		
		// 登录验证vip等级
		int oldVipLevel = player.getVipLevel();
		if (oldVipLevel == SysBasicCfg.getInstance().getCheckCurVipLevel()) {
			int newVipLevel = GameUtil.getVipLevelByRecharge(player.getEntity().getRecharge(),player.getPlatform());
			if (newVipLevel > oldVipLevel) {
				player.getEntity().setVipLevel(newVipLevel);
				player.getEntity().notifyUpdate(true);
			}
			player.getPlayerData().syncPlayerInfo();
		}
		
		return true;
	}

	/**
	 * 玩家下线处理
	 * 
	 * @return
	 */
	@Override
	protected boolean onPlayerLogout() {
		if (player.getPlayerData().getMainRole() != null) {
			// 保存玩家数据快照
			SnapShotManager.getInstance().cacheSnapshot(player.getId(), player.getPlayerData().getOnlinePlayerSnapshot());
			// 删除在线好友推荐信息
			IpAddrManager.getInstance().remove(player.getPlayerData());
		}
		// 会话关闭
		ChatManager.getInstance().removeSession(player.getSession());
		// 移除玩家在线id
		ServerData.getInstance().removeOnlinePlayerId(player.getId());
		// 情况玩家会话
		player.setSession(null);
		// 设置组装状态
		player.setAssembleFinish(false);

		GsApp.getInstance().savePlayerData(player.getPlayerData());

		// 清理玩家无效数据
		if (SysBasicCfg.getInstance().isDeleteInvalid()) {
			GsApp.getInstance().postCommonTask(new GuaJiTask() {
				@Override
				protected int run() {
					DBManager.getInstance().executeUpdate("delete from email where playerid = " + player.getId() + " and invalid = 1");
					DBManager.getInstance().executeUpdate("delete from equip where playerid = " + player.getId() + " and invalid = 1");
					return 0;
				}    
			});
		}
		return true;
	}

	/**
	 * 查看阵容信息
	 * 
	 * @param parseProtocol
	 */
	@ProtocolHandlerAnno(code=HP.code.SEE_OTHER_PLAYER_INFO_C_VALUE)
	private void onSeeOtherPlayerInfo(Protocol protocol) {
		HPSeeOtherPlayerInfo parseProtocol = protocol.parseProtocol(HPSeeOtherPlayerInfo.getDefaultInstance());
		
		PlayerSnapshotInfo.Builder snapshotInfo = null;
		int type = parseProtocol.getType();
		//通过名字或者id来获取其他玩家快照
		if(type == 1) {
			int otherPlayerId = parseProtocol.getPlayerId();
			snapshotInfo = SnapShotManager.getInstance().getPlayerSnapShot(otherPlayerId);
		} else if(type == 2) {
			String otherPlayerName = parseProtocol.getPlayerName();
			snapshotInfo = SnapShotManager.getInstance().getPlayerSnapShot(otherPlayerName);
		}
		
		if(snapshotInfo == null) {
			// 查看玩家信息失败,玩家不存在
			sendError(HP.code.SEE_OTHER_PLAYER_INFO_C_VALUE, Status.error.PLAYER_NOT_FOUND_VALUE);
			return;
		}
		
		// 过滤未出战佣兵
		List<RoleInfo.Builder> roleInfoClon = new ArrayList<>(snapshotInfo.getMercenaryInfoBuilderList());
		snapshotInfo.clearMercenaryInfo();
		Iterator<RoleInfo.Builder> it = roleInfoClon.iterator();
		while (it.hasNext()) {
			RoleInfo.Builder rInfo = it.next();
			if (rInfo.getStatus() != Const.RoleStatus.FIGHTING_VALUE && rInfo.getStatus() != Const.RoleStatus.MIXTASK_VALUE) {
				it.remove();
			} else {
				snapshotInfo.addMercenaryInfo(rInfo);
			}
		}
		
		HPSeeOtherPlayerInfoRet.Builder ret = HPSeeOtherPlayerInfoRet.newBuilder();
		ret.setPlayerInfo(snapshotInfo);
		FriendEntity friendEntity = player.getPlayerData().getFriendEntity();
		if (friendEntity != null) {
			ret.setIsFriend(friendEntity.isFriend(snapshotInfo.getPlayerId()));
			ret.setIsShield(friendEntity.isShield(snapshotInfo.getPlayerId()));
			ret.setIsSendAllow(friendEntity.isDailySendMsgAllow(snapshotInfo.getPlayerId()));
		} else {
			ret.setIsFriend(false);
			ret.setIsShield(false);
			ret.setIsSendAllow(false);
		}

		Protocol retProtocol = Protocol.valueOf(HP.code.SEE_OTHER_PLAYER_INFO_S, ret);
		sendProtocol(ProtoUtil.compressProtocol(retProtocol));
	}

	@ProtocolHandlerAnno(code=HP.code.SEE_MERCENARY_INFO_C_VALUE)
	private void OnSeeMercenaryInfo(Protocol protocol) {
		HPSeeMercenaryInfo parseProtocol = protocol.parseProtocol(HPSeeMercenaryInfo.getDefaultInstance());
		int otherPlayerId = parseProtocol.getPlayerId();
		int mercenaryId = parseProtocol.getMercenaryId();
		PlayerSnapshotInfo.Builder snapshotInfo = SnapShotManager.getInstance().getPlayerSnapShot(otherPlayerId);
		if (snapshotInfo == null) {
			// 玩家佣兵信息不存在
			sendError(HP.code.SEE_MERCENARY_INFO_C_VALUE, Status.error.MERCENARY_NOT_FOUND_VALUE);
			return;
		}

		// 佣兵信息
		HPSeeMercenaryInfoRet.Builder ret = HPSeeMercenaryInfoRet.newBuilder();
		List<RoleInfo> mercenaryInfoList = snapshotInfo.getMercenaryInfoList();
		RoleInfo curMercenaryInfo = null;
		for (RoleInfo mercenaryInfo : mercenaryInfoList) {
			if (mercenaryId == mercenaryInfo.getRoleId()) {
				curMercenaryInfo = mercenaryInfo;
				break;
			}
		}
		if (curMercenaryInfo == null) {
			// 玩家佣兵信息不存在
			sendError(HP.code.SEE_MERCENARY_INFO_C_VALUE, Status.error.MERCENARY_NOT_FOUND_VALUE);
			return;
		}
		ret.setMercenaryInfo(curMercenaryInfo);

		// 佣兵装备信息
		List<RoleEquip> mercenaryEquipList = curMercenaryInfo.getEquipsList();
		Map<Long, Integer> tmpEquipsMap = new HashMap<Long, Integer>();
		for (RoleEquip equip : mercenaryEquipList) {
			tmpEquipsMap.put(equip.getEquipId(), equip.getPart());
		}

		List<EquipInfo> allEquipInfoList = snapshotInfo.getEquipInfoList();
		for (EquipInfo equipInfo : allEquipInfoList) {
			if (tmpEquipsMap.containsKey(equipInfo.getId())) {
				ret.addEquipInfo(equipInfo);
			}
		}

		// 光环信息
		ret.addAllRingInfos(snapshotInfo.getRingInfosList());

		Protocol retProtocol = Protocol.valueOf(HP.code.SEE_MERCENARY_INFO_S_VALUE, ret);
		sendProtocol(ProtoUtil.compressProtocol(retProtocol));
	}
}
