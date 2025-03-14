package com.guaji.game.entity;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;

import org.guaji.os.GuaJiTime;

import com.guaji.game.config.SysBasicCfg;
import com.guaji.game.manager.SnapShotManager;
import com.guaji.game.player.Player;
import com.guaji.game.protocol.Const;
import com.guaji.game.protocol.MultiElite.MultiEliteBattleState;
import com.guaji.game.protocol.MultiElite.RoomItem;
import com.guaji.game.protocol.MultiElite.RoomMember;
import com.guaji.game.protocol.MultiElite.RoomMemberInfo;
import com.guaji.game.protocol.Player.RoleInfo;
import com.guaji.game.protocol.Snapshot.PlayerSnapshotInfo;
import com.guaji.game.util.GsConst;
import com.guaji.game.util.PlayerUtil;
import com.guaji.game.util.Tuple2;

public class MultiEliteRoomInfo {
	/**
	 * 服务器房间编号
	 */
	private int serverRoomId;

	/**
	 * 对应的副本Id
	 */
	private int multiEliteId;

	/**
	 * 房间成员列表<playerId,Tuple2<状态, 加入时间>>
	 */
	private Map<Integer, Tuple2<Integer, Long>> memberPlayerIds;

	/**
	 * 最小战力
	 */
	private int minFightValue;

	/**
	 * 房间名字
	 */
	private String roomName;

	/**
	 * 房间密码
	 */
	private String passWord;

	/**
	 * 玩家创建房间时间
	 */
	private long playerCreateTime;

	/**
	 * 战斗开打时间
	 */
	private int startBattleTime;

	/**
	 * 是否进入战斗中
	 */
	private boolean isOnBattle;

	/**
	 * 房间实体
	 * 
	 * @param multiEliteId 副本ID
	 * @param roomNum      房间号
	 */
	public MultiEliteRoomInfo(int multiEliteId, int roomNum) {
		memberPlayerIds = new ConcurrentHashMap<Integer, Tuple2<Integer, Long>>();
		this.multiEliteId = multiEliteId;
		this.serverRoomId = roomNum;
		minFightValue = 0;
		playerCreateTime = 0;
		startBattleTime = -1;
		isOnBattle = false;
	}

	public int getServerRoomId() {
		return serverRoomId;
	}

	public void setServerRoomId(int serverRoomId) {
		this.serverRoomId = serverRoomId;
	}

	public int getMultiEliteId() {
		return multiEliteId;
	}

	public void setMultiEliteId(int multiEliteId) {
		this.multiEliteId = multiEliteId;
	}

	public Map<Integer, Tuple2<Integer, Long>> getMemberPlayerIds() {
		return memberPlayerIds;
	}

	public int getMinFightValue() {
		return minFightValue;
	}

	public void setMinFightValue(int minFightValue) {
		this.minFightValue = minFightValue;
	}

	public long getPlayerCreateTime() {
		return playerCreateTime;
	}

	public void setPlayerCreateTime(long playerCreateTime) {
		this.playerCreateTime = playerCreateTime;
	}

	public int getStartBattleTime() {
		return startBattleTime;
	}

	public void setStartBattleTime(int startBattleTime) {
		this.startBattleTime = startBattleTime;
	}

	public boolean isOnBattle() {
		return isOnBattle;
	}

	public void setOnBattle(boolean isOnBattle) {
		this.isOnBattle = isOnBattle;
	}

	public String getRoomName() {
		return roomName;
	}

	public void setRoomName(String roomName) {
		this.roomName = roomName;
	}

	/**
	 * 是否是空房间
	 * 
	 * @return
	 */
	public boolean isEmpty() {
		return memberPlayerIds.size() == 0;
	}

	/**
	 * 房间是否满员
	 * 
	 * @return
	 */
	public boolean isFull() {
		return memberPlayerIds.size() >= SysBasicCfg.getInstance().getMultiEliteMaxCapacity();
	}

	/**
	 * 获取房主id
	 * 
	 * @return
	 */
	public int getLandlordPlayerId() {
		for (Entry<Integer, Tuple2<Integer, Long>> state : memberPlayerIds.entrySet()) {
			if (Const.MultiEliteState.IS_HOUSE_OWNER_VALUE == state.getValue().first) {
				return state.getKey();
			}
		}
		return 0;
	}

	/**
	 * 加入房间
	 * 
	 * @param playerId
	 * @param          isHouseOwner(是否房主)
	 */
	public synchronized int joinRoom(int playerId, boolean isHouseOwner) {
		if (!isFull()) {
			if (!memberPlayerIds.containsKey(playerId)) {
				if (isHouseOwner) {
					Tuple2<Integer, Long> playerJoin = new Tuple2<Integer, Long>(
							Const.MultiEliteState.IS_HOUSE_OWNER_VALUE, GuaJiTime.getMillisecond());
					memberPlayerIds.put(playerId, playerJoin);
					playerCreateTime = GuaJiTime.getMillisecond();
				} else {
					Tuple2<Integer, Long> playerJoin = new Tuple2<Integer, Long>(Const.MultiEliteState.IS_AWAIT_VALUE,
							GuaJiTime.getMillisecond());
					memberPlayerIds.put(playerId, playerJoin);
				}
			}
			return memberPlayerIds.size();
		}
		return 0;
	}

	/**
	 * 退出房间
	 */
	public synchronized void exitRoom(int playerId) {
		if (memberPlayerIds.containsKey(playerId)) {
			if (getLandlordPlayerId() != playerId) {
				memberPlayerIds.remove(playerId);
			}
		}
	}

	/**
	 * 踢出玩家
	 * 
	 * @param playerId
	 */
	public synchronized void kickMember(int playerId) {
		int landlordPlayerId = getLandlordPlayerId();
		if (memberPlayerIds.containsKey(playerId) && playerId != landlordPlayerId) {
			memberPlayerIds.remove(playerId);
		}
	}

	/**
	 * 获取房间状态
	 */
	public int getBattleState() {
		if (startBattleTime > 0) {
			if (startBattleTime - GuaJiTime.getSeconds() > 0) {
				return MultiEliteBattleState.COUNTDOWN_VALUE;
			} else {
				return MultiEliteBattleState.FIGHTING_ING_VALUE;
			}
		}
		return MultiEliteBattleState.UNSTART_VALUE;
	}

	/**
	 * 生成对应的协议对象
	 */
	public RoomItem.Builder genRoomItemBuilder() {
		RoomItem.Builder builder = RoomItem.newBuilder();
		builder.setServerRoomId(serverRoomId);

		for (Integer playerId : memberPlayerIds.keySet()) {
			Player player = PlayerUtil.queryPlayer(playerId);
			if (player != null) {
				RoomMemberInfo.Builder roomMemberBuilder = RoomMemberInfo.newBuilder();
				roomMemberBuilder.setPlayerId(playerId);
				roomMemberBuilder.setRoleItemId(player.getPlayerData().getMainRole().getItemId());
				roomMemberBuilder.setFightValue(player.getFightValue());
				roomMemberBuilder.setName(player.getName());
				roomMemberBuilder.setLevel(player.getLevel());
				roomMemberBuilder.setHeadIcon(player.getPlayerData().getPlayerEntity().getHeadIcon());
				if (getLandlordPlayerId() == playerId) {
					builder.addRoomMemberInfo(0, roomMemberBuilder);
				} else {
					builder.addRoomMemberInfo(roomMemberBuilder);
				}
				roomMemberBuilder.setAvatarId(player.getPlayerData().getUsedAvatarId());
			}
		}
		builder.setCreateTime(playerCreateTime);
		builder.setMinFightValue(minFightValue);
		builder.setLandlordName(roomName);
		builder.setIsPassWord(!passWord.equals("A"));
		return builder;
	}

	/**
	 * 生成房间成员信息列表
	 * 
	 * @return
	 */
	public List<RoomMember.Builder> genRoomMemberBuilders() {
		List<RoomMember.Builder> memberBuilders = new ArrayList<RoomMember.Builder>();
		for (Entry<Integer, Tuple2<Integer, Long>> memberS : memberPlayerIds.entrySet()) {
			RoomMember.Builder builder = genRoomMemberBuilder(memberS.getKey());
			builder.setTeamState(Const.MultiEliteState.valueOf(memberS.getValue().first));
			builder.setJoinTime(memberS.getValue().second);
			memberBuilders.add(builder);
		}
		return memberBuilders;
	}

	public static RoomMember.Builder genRoomMemberBuilder(int playerId) {
		RoomMember.Builder builder = RoomMember.newBuilder();
		PlayerSnapshotInfo.Builder snapshotInfoBuilder = SnapShotManager.getInstance().getPlayerSnapShot(playerId)
				.clone();
		if (snapshotInfoBuilder != null) {
			RoleInfo playerMainRoleInfo = snapshotInfoBuilder.getMainRoleInfo();
			builder.setPlayerId(snapshotInfoBuilder.getPlayerId());
			builder.setHeadIcon(snapshotInfoBuilder.getPlayerInfo().getHeadIcon());
			// 添加主角
			builder.addRoleItemId(playerMainRoleInfo.getItemId());

			List<RoleInfo.Builder> shotList = snapshotInfoBuilder.getMercenaryInfoBuilderList();
			if (shotList != null && shotList.size() > 0) {
				for (RoleInfo.Builder mercenaryInfo : shotList) {

					if (mercenaryInfo.getActiviteState() == Const.RoleActiviteState.IS_ACTIVITE_VALUE
							&& snapshotInfoBuilder.getFightingRoleIdList().contains(mercenaryInfo.getItemId())) {
						builder.addRoleItemId(mercenaryInfo.getItemId());
					}
				}
			}

			builder.setLevel(playerMainRoleInfo.getLevel());
			builder.setName(playerMainRoleInfo.getName());
			builder.setProf(playerMainRoleInfo.getProf());
			builder.setRebirthStage(playerMainRoleInfo.getRebirthStage());
			builder.setFightValue(playerMainRoleInfo.getMarsterFight());
			builder.setRebirthStage(playerMainRoleInfo.getRebirthStage());
			builder.setAvatarId(playerMainRoleInfo.getAvatarId());
		}
		return builder;
	}

	/**
	 * 计算服务器房间ID
	 */
	public static int calcServerRoomId(int multiEliteId, int roomNum) {
		return multiEliteId * GsConst.ITEM_TYPE_BASE + roomNum;
	}

	/**
	 * 根据服务器房间Id计算对应副本
	 */
	public static int calcMultiEliteId(int serverRoomId) {
		return serverRoomId / GsConst.ITEM_TYPE_BASE;
	}

	/**
	 * 计算客户端房间Id
	 * 
	 * @return
	 */
	public static int calcClientRoomId(int serverRoomId) {
		return serverRoomId % GsConst.ITEM_TYPE_BASE;
	}

	/**
	 * 该房间是否包含该玩家
	 * 
	 * @param playerId
	 * @return
	 */
	public boolean isPlayerIn(int playerId) {
		return memberPlayerIds.containsKey(playerId);
	}

	/**
	 * 获取该房间是否全员准备
	 * 
	 * @return
	 */
	public boolean getIsSetout() {
		for (Tuple2<Integer, Long> state : memberPlayerIds.values()) {
			if (state.first != Const.MultiEliteState.IS_SETOUT_VALUE
					&& state.first != Const.MultiEliteState.IS_HOUSE_OWNER_VALUE) {
				return false;
			}
		}
		return true;
	}

	public String getPassWord() {
		return passWord;
	}

	public void setPassWord(String passWord) {
		this.passWord = passWord;
	}

}
