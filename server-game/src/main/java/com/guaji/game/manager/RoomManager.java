package com.guaji.game.manager;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.guaji.config.ConfigManager;
import org.guaji.db.DBManager;
import org.guaji.log.Log;
import org.guaji.msg.Msg;
import org.guaji.os.GuaJiTime;
import org.guaji.thread.GuaJiTask;

import com.guaji.game.GsApp;
import com.guaji.game.attribute.AttrInfoObj;
import com.guaji.game.attribute.Attribute;
import com.guaji.game.battle.BattleRole;
import com.guaji.game.battle.BattleUtil;
import com.guaji.game.battle.Battlefield;
import com.guaji.game.battle.MultiEliteReward;
import com.guaji.game.config.MultiLuckRoleCfg;
import com.guaji.game.config.MultiMapCfg;
import com.guaji.game.config.SysBasicCfg;
import com.guaji.game.entity.MultiEliteReportEntity;
import com.guaji.game.entity.MultiEliteRoomInfo;
import com.guaji.game.entity.StateEntity;
import com.guaji.game.log.BehaviorLogger.Action;
import com.guaji.game.player.Player;
import com.guaji.game.protocol.Battle;
import com.guaji.game.protocol.Battle.BattleInfo;
import com.guaji.game.protocol.Const;
import com.guaji.game.protocol.MultiElite.MultiBattleAward;
import com.guaji.game.protocol.MultiElite.MultiEliteBattleResult;
import com.guaji.game.protocol.MultiElite.MultiEliteBattleState;
import com.guaji.game.protocol.MultiElite.MultiEliteLuckRoleInfo;
import com.guaji.game.protocol.MultiElite.RoomItem;
import com.guaji.game.protocol.MultiElite.RoomMember;
import com.guaji.game.protocol.Player.RoleInfo;
import com.guaji.game.util.BuilderUtil;
import com.guaji.game.util.GsConst;
import com.guaji.game.util.PlayerUtil;
import com.guaji.game.util.Tuple2;

/**
 * 每个副本的房间管理器
 */
public class RoomManager {

	/**
	 * 多人副本战斗任务Task
	 * 
	 * @author zdz
	 */
	public class MultiEliteFightTask extends GuaJiTask {
		/**
		 * 战斗房间信息
		 */
		private MultiEliteRoomInfo battleEliteRoom;

		public MultiEliteFightTask(MultiEliteRoomInfo battleEliteRoom) {
			super(true);
			this.battleEliteRoom = battleEliteRoom;
		}

		@Override
		protected int run() {
			doFight();

			// 战斗结束，移除对象
			battleEliteRooms.remove(battleEliteRoom);
			return 0;
		}

		/**
		 * 组织双方数据并进行战斗
		 * 
		 * @return
		 */
		public void doFight() {
//
//			List<Entry<Integer, Tuple2<Integer, Long>>> battleList = new ArrayList<>(
//					battleEliteRoom.getMemberPlayerIds().entrySet());
//
//			Collections.sort(battleList, new Comparator<Entry<Integer, Tuple2<Integer, Long>>>() {
//				@Override
//				public int compare(Entry<Integer, Tuple2<Integer, Long>> o1, Entry<Integer, Tuple2<Integer, Long>> o2) {
//					long s1 = o1.getValue().second / 1000;
//					long s2 = o2.getValue().second / 1000;
//					return (int) (s1 - s2);
//				}
//			});
//
//			// 排序后的战斗队伍
//			List<Integer> battlePlayer = new ArrayList<>();
//			for (Entry<Integer, Tuple2<Integer, Long>> battleP : battleList) {
//				battlePlayer.add(battleP.getKey());
//			}
//
//			List<BattleRole> multiLuckRole = new ArrayList<BattleRole>();
//			// 玩家战斗集合
//			List<BattleRole> playerBattleRoleList = BattleUtil.getBattleTeamItems(battlePlayer);
//
//			multiLuckRole.addAll(playerBattleRoleList);
//
//			// 应援战斗对象
//			List<BattleRole> assistanceBattleRoleList = BattleUtil.getBattleAssistanceItems(battlePlayer);
//
//			// 幸运副将属性添加
//			if (assistanceBattleRoleList.size() > 0) {
//				multiLuckRole.addAll(assistanceBattleRoleList);
//			}
//
//			// 幸运副将属性添加
//			onMultiLuckRole(multiLuckRole);
//
//			// 怪物列表
//			MultiMapCfg cfg = ConfigManager.getInstance().getConfigByKey(MultiMapCfg.class,
//					battleEliteRoom.getMultiEliteId());
//			List<BattleRole> monsterRoles = new LinkedList<BattleRole>();
//			for (int monsterId : cfg.getMonsterIdList()) {
//				RoleInfo.Builder monsterRole = BuilderUtil.genMultiEliteMonsterRoleInfoBuilder(monsterId);
//				if (monsterRole == null) {
//					Log.errPrintln(String.format("map monster config not exist, map: %d, monster: %d",
//							battleEliteRoom.getMultiEliteId(), monsterId));
//					continue;
//				}
//				BattleRole monsterBattleRole = new BattleRole(monsterRole);
//				// Boss免疫眩晕、冰冻、沉默、中毒buff标记
//				// monsterBattleRole.setSpecialFlag(GsConst.SkillSpecialFlag.MULTI_ELITE_MAP_BOSS,
//				// true);
//				monsterRoles.add(monsterBattleRole);
//			}
//
//			// 战斗
//			Battlefield battlefield = new Battlefield();
//			// battlefield.fighting(Battle.battleType.BATTLE_PVE_MULTI_ELITE_VALUE,
//			// playerBattleRoleList, monsterRoles, null);
//
//			battlefield.fighting(Battle.battleType.BATTLE_PVE_MULTI_ELITE_VALUE, playerBattleRoleList,
//					assistanceBattleRoleList, monsterRoles, null, null);
//
//			MultiEliteBattleResult.Builder battleResult = MultiEliteBattleResult.newBuilder();
//
//			// 添加福将信息
//			Map<Integer, String> luckMap = MultiEliteManager.getInstance().getMultiElitetInfoEntity()
//					.getMultiLuckRoleMap();
//			if (luckMap != null) {
//				for (Entry<Integer, String> luckInfo : luckMap.entrySet()) {
//					MultiEliteLuckRoleInfo.Builder builderInfo = MultiEliteLuckRoleInfo.newBuilder();
//					builderInfo.setRoleItemId(luckInfo.getKey());
//					MultiLuckRoleCfg roleCfg = ConfigManager.getInstance().getConfigByKey(MultiLuckRoleCfg.class,
//							luckInfo.getValue());
//					if (roleCfg != null) {
//						builderInfo.setTextInfo(roleCfg.getName1());
//					}
//					battleResult.addMultiEliteLuckRoleInfo(builderInfo);
//				}
//			}
//
//			// 战斗信息
//			BattleInfo.Builder battleInfoBuilder = battlefield.getBattleInfo();
//			if (battleInfoBuilder != null) {
//				battleResult.setBattleInfo(battleInfoBuilder);
//			}
//
//			// 成员信息
//			List<RoomMember.Builder> builders = battleEliteRoom.genRoomMemberBuilders();
//			for (RoomMember.Builder builder : builders) {
//				battleResult.addRoomMember(builder);
//			}
//
//			boolean isWin = (battlefield.getBattleResult() > 0);
//			if (isWin) {
//				// 战斗胜利
//				battleResult.setIsWin(true);
//				// 激战回合数
//				int battleRound = battlefield.getBattleRound();
//				battleResult.setBattleRound(battleRound);
//				// 平均剩余血量比率
//				double avgLeftBloodRate = 0;
//				for (BattleRole battleRole : playerBattleRoleList) {
//					double leftBloodRate = (double) battleRole.getHp()
//							/ (double) battleRole.getAttrValue(Const.attr.HP);
//					avgLeftBloodRate += leftBloodRate;
//				}
//				avgLeftBloodRate /= playerBattleRoleList.size();
//				battleResult.setLeftBloodRate((int) (avgLeftBloodRate * 100));
//
//				// 添加延迟发奖队列(保留延迟发奖励接口)
//				int rewardTime = 0; // GuaJiTime.getSeconds() +
//									// battleInfo.getBattleTime();
//				Map<Integer, Tuple2<Integer, Long>> battleS = battleEliteRoom.getMemberPlayerIds();
//
//				for (Integer playerId : battleS.keySet()) {
//					Player player = PlayerUtil.queryPlayer(playerId);
//					if (player == null) {
//						continue;
//					}
//					StateEntity stateEntity = player.getPlayerData().getStateEntity();
//					if (stateEntity != null) {
//						// 添加战斗信息
//						stateEntity.addMultiBattleInfoMap(cfg.getId());
//						// 组装奖励
//						MultiEliteReward eliteReward = new MultiEliteReward(player, rewardTime);
//						// 前端战士奖励
//						MultiBattleAward.Builder battleAward = eliteReward.deliverAward(cfg, battleS.size(),
//								stateEntity);
//						battleAward.setPlayerId(playerId);
//						// 添加胜利次数
//						stateEntity.setMultiFirstBattle(stateEntity.getMultiFirstBattle() + 1);
//						// 修改为直接发奖
//						eliteReward.dropTakeAffect(Action.MULTI_ELITE_BATTLE_WIN);
//						// 添加延迟队列
//						// eliteRewards.add(eliteReward);
//						// 添加前端展示
//						battleResult.addBattleAward(battleAward);
//					}
//				}
//
//			} else {
//				// 战斗失败
//				battleResult.setIsWin(false);
//			}
//
//			// 战报数据写入数据库
//			MultiEliteReportEntity reportEntity = new MultiEliteReportEntity();
//			reportEntity.setBattleResultBuilder(battleResult);
//			DBManager.getInstance().create(reportEntity);
//
//			// 战斗结束邮件通知
//			MultiEliteManager.getInstance().sendRoomMemberAward(isWin, battleEliteRoom, reportEntity);
//
//			// 同步战斗信息
//			Msg msg1 = Msg.valueOf(GsConst.MsgType.BATTLE_STATE_CHANGE);
//			msg1.pushParam(battleEliteRoom.getBattleState(), battleEliteRoom.getStartBattleTime(), battleResult);
//			MultiEliteManager.getInstance().broadcastRoomMemberMsg(msg1,
//					new ArrayList<>(battleEliteRoom.getMemberPlayerIds().keySet()));
//
//			// 战斗结束后结算
//			Msg msg2 = Msg.valueOf(GsConst.MsgType.MULTI_ELITE_BATTLE_SETTLE);
//			msg2.pushParam(battleEliteRoom.getMultiEliteId());
//			msg2.pushParam(reportEntity.getId());
//			msg2.pushParam(isWin);
//			MultiEliteManager.getInstance().broadcastRoomMemberMsg(msg2,
//					new ArrayList<>(battleEliteRoom.getMemberPlayerIds().keySet()));
//
//			// TODO 有任务推送任务
		}

	}

	/**
	 * 福将属性计算
	 * 
	 * @param playerBattleRoleList
	 */
	private void onMultiLuckRole(List<BattleRole> playerBattleRoleList) {
		Map<Integer, String> roleIdMap = MultiEliteManager.getInstance().getMultiElitetInfoEntity()
				.getMultiLuckRoleMap();
		if (roleIdMap != null && roleIdMap.size() > 0) {
			Player player = PlayerUtil.queryPlayer(playerBattleRoleList.get(0).getPlayerId());
			if (player != null) {
				List<Integer> attrAddlist = new ArrayList<Integer>();
				for (Entry<Integer, String> luckCfg : roleIdMap.entrySet()) {
					boolean isContins = player.getPlayerData().continsMercenaryByItemId(luckCfg.getKey());
					BattleRole battleRole = getContinsBattleRole(playerBattleRoleList, luckCfg.getKey());
					if (battleRole != null) {
						if (isContins && !attrAddlist.contains(luckCfg.getKey())) {
							attrAddlist.add(luckCfg.getKey());
							MultiLuckRoleCfg cfg = ConfigManager.getInstance().getConfigByKey(MultiLuckRoleCfg.class,
									luckCfg.getValue());
							if (cfg != null) {
								for (BattleRole _battleRole : playerBattleRoleList) {
									addRoleAttr(_battleRole.getAttribute(), cfg.getAttr1(), battleRole.getRoleInfo());
								}
							}
						}
					}

				}
			}
		}
	}

	/**
	 * 添加属性
	 * 
	 * @param attribute
	 * @param cfg
	 * @param roleInfo
	 */
	private void addRoleAttr(Attribute attribute, String attrStr, RoleInfo.Builder roleInfo) {
		Attribute additionAttrMap = new Attribute();
		List<AttrInfoObj> attrInfoList = AttrInfoObj.valueOfs(attrStr);
		for (AttrInfoObj attr : attrInfoList) {
			if (attr.getAddType() == GsConst.RingValueType.PERCENTAGE_TYPE) {
				additionAttrMap.add(Const.attr.valueOf(attr.getAttrType()), attr.getAttrValue());
			} else if (attr.getAddType() == GsConst.RingValueType.NUMERICAL_TYPE) {
				attribute.add(attr.getAttrType(), attr.getAttrValue());
			} else {
				// 数值 * 等级
				attribute.add(attr.getAttrType(), attr.getAttrValue() * roleInfo.getLevel());
			}
		}
		attribute.additionAttr(additionAttrMap);
	}

	/**
	 * 战斗队列中，是否包含该佣兵
	 * 
	 * @param playerBattleRoleList
	 * @param itemId
	 * @return
	 */
	private BattleRole getContinsBattleRole(List<BattleRole> playerBattleRoleList, int itemId) {
		for (BattleRole battleRole : playerBattleRoleList) {
			if (battleRole.getRoleInfo().getItemId() == itemId) {
				return battleRole;
			}
		}
		return null;
	}

	/**
	 * 对应的多人副本Id
	 */
	private int multiEliteId;
	/**
	 * 管理器房间最大容量
	 */
	private int maxCapacity = SysBasicCfg.getInstance().getMultiEliteMaxRoomSize();
	/**
	 * 等待状态的房间
	 */
	private List<MultiEliteRoomInfo> waitEliteRooms;
	/**
	 * 战斗中的房间
	 */
	private List<MultiEliteRoomInfo> battleEliteRooms;

	/**
	 * 延迟发奖队列
	 */
	// private List<MultiEliteReward> eliteRewards;

	public RoomManager(int multiEliteId) {
		waitEliteRooms = Collections.synchronizedList(new ArrayList<MultiEliteRoomInfo>());
		battleEliteRooms = Collections.synchronizedList(new ArrayList<MultiEliteRoomInfo>());
		// eliteRewards = Collections.synchronizedList(new
		// LinkedList<MultiEliteReward>());
		this.multiEliteId = multiEliteId;
	}

	/**
	 * 定时检查战斗开始的房间
	 */
	public void onTick() {
		// 检测可以进入战斗的房间
		synchronized (battleEliteRooms) {
			Iterator<MultiEliteRoomInfo> iterator1 = battleEliteRooms.iterator();
			while (iterator1.hasNext()) {
				MultiEliteRoomInfo room = iterator1.next();
				if (GuaJiTime.getSeconds() >= room.getStartBattleTime() && !room.isOnBattle()) {
					room.setOnBattle(true);
					// 战斗
					GsApp.getInstance().postCommonTask(new MultiEliteFightTask(room));
					iterator1.remove();
				}
			}
		}

		// 延迟发奖励
		// Iterator<MultiEliteReward> iteratorReward = eliteRewards.iterator();
		// while (iteratorReward.hasNext()) {
		// MultiEliteReward reward = iteratorReward.next();
		// if (!reward.isHasReward() && reward.checkIsReward()) {
		// synchronized (reward.getPlayer()) {
		// try {
		// reward.dropTakeAffect(Action.MULTI_ELITE_BATTLE_WIN);
		// iteratorReward.remove();
		// } catch (Exception e) {
		// Log.errPrintln("MultiEliteReward Error" + e);
		// }
		// }
		// }
		// }

		// 活动结束，清除所有房间
		boolean checkIsMultTime = MultiEliteManager.getInstance().checkIsMultTime();
		if (!checkIsMultTime && waitEliteRooms.size() > 0) {
			waitEliteRooms.clear();
		}
	}

	/**
	 * 对应的多人副本Id
	 * 
	 * @return
	 */
	public int getMultiEliteId() {
		return multiEliteId;
	}

	/**
	 * 玩家创建房间
	 * 
	 * @param playerId
	 * @param isAutoStart
	 * @param minFightValue
	 * @param eliteId
	 * @param roomName
	 * @return
	 */
	public MultiEliteRoomInfo createWaitRoom(int playerId, int eliteId, int minFightValue, String roomName,
			String passWord) {
		// 超过房间数量
		if (this.getWaitEliteRoomSize() >= this.getMaxCapacity()) {
			return null;
		}
		MultiEliteRoomInfo room = new MultiEliteRoomInfo(eliteId, playerId);
		room.joinRoom(playerId, true);
		room.setMinFightValue(minFightValue);
		room.setRoomName(roomName);
		room.setPassWord(passWord);
		waitEliteRooms.add(room);
		return room;
	}

	/**
	 * 根据serverRoomId获取等待玩家进入的房间
	 * 
	 * @param serverRoomId
	 * @return
	 */
	public MultiEliteRoomInfo getWaitEliteRoom(int serverRoomId) {
		synchronized (waitEliteRooms) {
			for (MultiEliteRoomInfo room : waitEliteRooms) {
				if (room.getServerRoomId() == serverRoomId) {
					return room;
				}
			}
		}
		return null;
	}

	/**
	 * 根据serverRoomId获取 读秒或战斗中 的房间
	 * 
	 * @param serverRoomId
	 * @return
	 */
	public MultiEliteRoomInfo getBattleEliteRoom(int serverRoomId) {
		synchronized (battleEliteRooms) {
			for (MultiEliteRoomInfo room : battleEliteRooms) {
				if (room.getServerRoomId() == serverRoomId) {
					return room;
				}
			}
		}
		return null;
	}

	/**
	 * 返回指定id的房间
	 * 
	 * @param serverRoomId
	 * @return
	 */
	public MultiEliteRoomInfo getRoom(int serverRoomId) {
		synchronized (waitEliteRooms) {
			for (MultiEliteRoomInfo room : waitEliteRooms) {
				if (room.getServerRoomId() == serverRoomId) {
					return room;
				}
			}
		}

		synchronized (battleEliteRooms) {
			for (MultiEliteRoomInfo room : battleEliteRooms) {
				if (room.getServerRoomId() == serverRoomId) {
					return room;
				}
			}
		}
		return null;
	}

	/**
	 * 获取等待开始战斗的房间数量
	 * 
	 * @return
	 */
	public int getWaitEliteRoomSize() {
		return this.waitEliteRooms.size();
	}

	/**
	 * 获取副本房间列表
	 * 
	 * @return
	 */
	public List<RoomItem.Builder> getWaitEliteRoomList() {
		List<RoomItem.Builder> roomItemBuilders = new ArrayList<RoomItem.Builder>();
		synchronized (waitEliteRooms) {
			for (MultiEliteRoomInfo room : waitEliteRooms) {
				roomItemBuilders.add(room.genRoomItemBuilder());
			}
		}
		return roomItemBuilders;
	}

	/**
	 * 最佳进入房间排序
	 * 
	 * @param waitEliteRooms
	 */
	private void sortBestInRooms(List<MultiEliteRoomInfo> waitEliteRooms) {
		Collections.sort(waitEliteRooms, new Comparator<MultiEliteRoomInfo>() {
			public int compare(MultiEliteRoomInfo arg0, MultiEliteRoomInfo arg1) {
				int size0 = arg0.getMemberPlayerIds().size();
				int size1 = arg1.getMemberPlayerIds().size();
				if (size0 == size1) {
					long playerCreateTime0 = arg0.getPlayerCreateTime();
					long playerCreateTime1 = arg1.getPlayerCreateTime();
					return (int) (playerCreateTime0 - playerCreateTime1);
				} else {
					return size1 - size0;
				}
			}
		});
	}

	/**
	 * 获取最佳可进入的房间
	 */
	public MultiEliteRoomInfo getBestCanJoinRoom(int fightValue) {
		List<MultiEliteRoomInfo> allCanJoinRooms = new ArrayList<MultiEliteRoomInfo>();
		synchronized (waitEliteRooms) {
			for (MultiEliteRoomInfo room : waitEliteRooms) {
				if (!room.isFull() && room.getMinFightValue() <= fightValue && room.getPassWord().equals("A")) {
					allCanJoinRooms.add(room);
				}
			}
		}

		if (allCanJoinRooms.size() > 0) {
			sortBestInRooms(allCanJoinRooms);
			return allCanJoinRooms.get(0);
		}
		return null;
	}

	/**
	 * 玩家加入房间
	 * 
	 * @param serverRoomId
	 * @param playerId
	 * @param              isHouseOwner(是否是房主)
	 */
	public void jionRoom(int serverRoomId, int playerId, boolean isHouseOwner) {
		if (MultiEliteManager.getInstance().getPlayerRoomId(playerId) != 0) {
			Log.errPrintln("##player already in room :" + MultiEliteManager.getInstance().getPlayerRoomId(playerId));
			Log.errPrintln("##player want to join room :" + serverRoomId);
			return;
		}

		MultiEliteRoomInfo waitRoom = getWaitEliteRoom(serverRoomId);
		waitRoom.joinRoom(playerId, isHouseOwner);
	}

	/**
	 * 玩家退出房间
	 * 
	 * @param playerId
	 */
	public MultiEliteRoomInfo exitRoom(int playerId) {
		int inRoomId = MultiEliteManager.getInstance().getPlayerRoomId(playerId);
		MultiEliteRoomInfo room = getRoom(inRoomId);

		if (room.getBattleState() != MultiEliteBattleState.UNSTART_VALUE) {
			// 战斗状态取消
			cancelStartBattle(inRoomId);
		}

		// 是队长，解散房间
		if (playerId == room.getLandlordPlayerId()) {
			room.getMemberPlayerIds().remove(playerId);
			waitEliteRooms.remove(room);
		} else {
			room.exitRoom(playerId);
		}
		return room;
	}

	/**
	 * 开始战斗倒计时
	 * 
	 * @param myRoom
	 */
	public void startBattleCountDown(int serverRoomId) {
		MultiEliteRoomInfo myRoom = getWaitEliteRoom(serverRoomId);
		int index = waitEliteRooms.indexOf(myRoom);
		if (index >= 0) {
			int battleStartTime = GuaJiTime.getSeconds() + SysBasicCfg.getInstance().getMultiEliteCountDown();
			myRoom.setStartBattleTime(battleStartTime);
			battleEliteRooms.add(myRoom);
			waitEliteRooms.remove(index);
		}
	}

	/**
	 * 取消开始战斗
	 */
	public void cancelStartBattle(int serverRoomId) {
		MultiEliteRoomInfo myRoom = getBattleEliteRoom(serverRoomId);
		int index = battleEliteRooms.indexOf(myRoom);
		myRoom.setStartBattleTime(-1);
		if (index >= 0) {
			waitEliteRooms.add(myRoom);
			battleEliteRooms.remove(index);
		}
	}

	public int getPlayerRoomId(int playerId) {
		synchronized (waitEliteRooms) {
			for (MultiEliteRoomInfo room : waitEliteRooms) {
				if (room.isPlayerIn(playerId)) {
					return room.getServerRoomId();
				}
			}
		}
		return 0;
	}

	public int getMaxCapacity() {
		return maxCapacity;
	}

	public void setMaxCapacity(int maxCapacity) {
		this.maxCapacity = maxCapacity;
	}

}
