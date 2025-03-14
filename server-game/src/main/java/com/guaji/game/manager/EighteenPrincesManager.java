package com.guaji.game.manager;

import java.util.Calendar;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

import org.guaji.app.AppObj;
import org.guaji.config.ConfigManager;
import org.guaji.db.DBManager;
import org.guaji.msg.Msg;
import org.guaji.net.protocol.Protocol;
import org.guaji.os.GuaJiTime;
import org.guaji.xid.GuaJiXID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.guaji.game.GsApp;
import com.guaji.game.GsConfig;
import com.guaji.game.ServerData;
import com.guaji.game.battle.BattleRole;
import com.guaji.game.config.EighteenPrincesCfg;
import com.guaji.game.config.RoleCfg;
import com.guaji.game.config.SysBasicCfg;
import com.guaji.game.entity.AllianceBattleInfo;
import com.guaji.game.entity.ArenaSnapShotEntity;
import com.guaji.game.entity.EighteenPrincesEntity;
import com.guaji.game.entity.EighteenPrincesHelpHistoryEntity;
import com.guaji.game.entity.RoleEntity;
import com.guaji.game.item.ConsumeItems;
import com.guaji.game.item.HelpMercenaryInfo;
import com.guaji.game.log.BehaviorLogger.Action;
import com.guaji.game.player.Player;
import com.guaji.game.protocol.Const;
import com.guaji.game.protocol.EighteenPrinces.HPEighteenPrincesLayerInfoRet;
import com.guaji.game.protocol.HP;
import com.guaji.game.protocol.Status;
import com.guaji.game.protocol.Const.changeType;
import com.guaji.game.protocol.Player.RoleInfo;
import com.guaji.game.protocol.Snapshot.PlayerSnapshotInfo;
import com.guaji.game.util.BuilderUtil;
import com.guaji.game.util.GsConst;
import com.guaji.game.util.PlayerUtil;

/**
 * 十八路诸侯关卡挑战
 */

public class EighteenPrincesManager extends AppObj {

	/**
	 * 日志记录器
	 */
	private static Logger logger = LoggerFactory.getLogger("Server");

	/**
	 * 全局对象, 便于访问
	 */
	private static EighteenPrincesManager instance = null;

	/**
	 * 系统类型
	 */
	private final int systype = 1;

	// 模块Tick周期
	private int tickIndex = 0;

	/**
	 * 18路诸侯协战副将记录
	 */
	private Map<Integer, EighteenPrincesEntity> playerIdEighteenPrincesMap;

	public EighteenPrincesManager(GuaJiXID xid) {
		super(xid);
		playerIdEighteenPrincesMap = new ConcurrentHashMap<>();
		if (instance == null) {
			instance = this;
		}
	}

	public boolean init() {
		// 加载所有协战武将数据
		List<EighteenPrincesEntity> eighteenPrincesEntitys = DBManager.getInstance()
				.query("from EighteenPrincesEntity where invalid = 0 order by playerId asc");
		if (eighteenPrincesEntitys == null) {
			return false;
		}
		// 查询数据库放入内存
		for (EighteenPrincesEntity eighteenPrinceInfo : eighteenPrincesEntitys) {
			eighteenPrinceInfo.convert();
			playerIdEighteenPrincesMap.put(eighteenPrinceInfo.getPlayerId(), eighteenPrinceInfo);
		}

		// 加载
		if (!ArenaManager.getInstance().loadArenaSnap()) {
			logger.info("ArenaManager loadArenaSnap failed");
		}

		// 获取所有符合目标的账户
		String sqlFormat = String.format(
				"SELECT id FROM player P LEFT JOIN eighteenprinces E ON P.id=E.playerId WHERE P.level>=%s AND P.invalid=0 AND E.playerId IS NULL",
				SysBasicCfg.getInstance().getEighteenPrincesOpenLevel());
		List<Object> playerIds = DBManager.getInstance().executeQuery(sqlFormat);
		for (Object rowInfo : playerIds) {
			Integer playerId = (Integer) rowInfo;
			PlayerSnapshotInfo.Builder snapBuilder = SnapShotManager.getInstance().getPlayerSnapShot(playerId);
			if (snapBuilder != null) {
				Map<Integer, Integer> fightMap = new TreeMap<>();
				for (RoleInfo roleInfo : snapBuilder.getMercenaryInfoList()) {

					if (roleInfo.getActiviteState() == Const.RoleActiviteState.IS_ACTIVITE_VALUE) {
						fightMap.put(roleInfo.getItemId(), roleInfo.getFight());
					}

				}

				List<Map.Entry<Integer, Integer>> fightList = fightMap.entrySet().stream()
						.sorted((entry1, entry2) -> entry1.getValue().compareTo(entry2.getValue()))
						.collect(Collectors.toList());
				if (!fightList.isEmpty()) {

					// 战力最高的协战武将
					int itemId = fightList.get(fightList.size() - 1).getKey();
					List<Integer> fightIds = new LinkedList<>();
					fightIds.add(snapBuilder.getMainRoleInfo().getItemId());
					for (Integer item : snapBuilder.getFightingRoleIdList()) {
						if (item != 0) {
							fightIds.add(item);
						}
					}

					// 玩家初始排行
					EighteenPrincesEntity eighteenPrince = new EighteenPrincesEntity(playerId, itemId, fightIds);
					if (DBManager.getInstance().create(eighteenPrince)) {
						eighteenPrince.convert();
						playerIdEighteenPrincesMap.put(eighteenPrince.getPlayerId(), eighteenPrince);
					}
				}

			}

		}

		return true;

	}

	/**
	 * 获取全局实例对象
	 */
	public static EighteenPrincesManager getInstance() {
		return instance;
	}

	public EighteenPrincesEntity createEighteenPrincesaEntity(Player player) {

		if (player == null) {
			return null;
		}
		// 若副将数量为0
		if (player.getPlayerData().getMercenary().isEmpty()) {
			return null;
		}

		Map<Integer, Integer> fightMap = new TreeMap<>();
		List<RoleEntity> roles = player.getPlayerData().getActiviceMercenary();
		for (RoleEntity roleEntity : roles) {
			int fightValue = PlayerUtil.calcFightValue(roleEntity);
			fightMap.put(roleEntity.getItemId(), fightValue);
		}

		List<Map.Entry<Integer, Integer>> fightList = fightMap.entrySet().stream()
				.sorted((entry1, entry2) -> entry1.getValue().compareTo(entry2.getValue()))
				.collect(Collectors.toList());

		if (!fightList.isEmpty()) {
			int itemId = fightList.get(fightList.size() - 1).getKey();
			List<Integer> fightIds = new LinkedList<>();
			// fightIds.add(player.getPlayerData().getMainRole().getItemId());
			for (BattleRole battleRole : player.getPlayerData().getBattleRoles(true)) {
				fightIds.add(battleRole.getRoleInfo().getItemId());
			}
			for (BattleRole battleRole : player.getPlayerData().getBattleAssistanceRoles()) {
				fightIds.add(battleRole.getRoleInfo().getItemId());
			}
			// 玩家初始排行
			EighteenPrincesEntity eighteenPrince = new EighteenPrincesEntity(player.getId(), itemId, fightIds);
			if (!DBManager.getInstance().create(eighteenPrince)) {
				return null;
			}

			playerIdEighteenPrincesMap.put(player.getId(), eighteenPrince);

			if (isOpen()) {
				PlayerUtil.syncEighteenPrinces(player, eighteenPrince);
			}
			return eighteenPrince;
		}
		return null;

	}

	/**
	 * @param playerId
	 * @return
	 */
	public EighteenPrincesEntity queryEighteenPrincesaEntity(int playerId) {
		if (this.playerIdEighteenPrincesMap.containsKey(playerId)) {
			return this.playerIdEighteenPrincesMap.get(playerId);
		}
		return null;
	}

	/**
	 * @param 十八路诸侯 层数
	 * @return
	 */
	public ArenaSnapShotEntity getArenaSnapShotEntityByLayerId(int layerId) {

		EighteenPrincesCfg eighteenPrincesCfg = ConfigManager.getInstance().getConfigByKey(EighteenPrincesCfg.class,
				layerId);
		if (eighteenPrincesCfg == null) {
			return null;
		}
		Calendar nowCal = GuaJiTime.getCalendar();
		nowCal.add(Calendar.DATE, -2);
		String mapKey = GuaJiTime.DATE_FORMATOR_DAYNUM(GuaJiTime.getAM0Date(nowCal.getTime())) + "_"
				+ String.valueOf(systype);

		List<ArenaSnapShotEntity> arenaSnapShotList = ArenaManager.getInstance().getArenaSnapShotListByKey(mapKey);
		if (arenaSnapShotList != null) {
			return arenaSnapShotList.get(eighteenPrincesCfg.getChallengeArenaRank() - 1);
		}
		return null;
	}

	@Override
	public boolean onTick() {
		// 注册
		if (++tickIndex % 100 == 0) {
			EighteenPrincesEntity eighteenPrinc = null;
			for (Integer key : playerIdEighteenPrincesMap.keySet()) {
				eighteenPrinc = playerIdEighteenPrincesMap.get(key);
				if (eighteenPrinc != null) {
					if (GuaJiTime.getSeconds() > eighteenPrinc.getResetTime()) {
						reSetEighteenPrinc(eighteenPrinc);
					}
				}

			}
		}
		return super.onTick();
	}

	/**
	 * 重置 18路诸侯数据
	 * 
	 * @param entrty
	 */
	private boolean reSetEighteenPrinc(EighteenPrincesEntity entrty) {
		if (entrty == null) {
			return false;
		}
		entrty.setEighteenPrinceCount(0);
		entrty.setResetTime((int) GuaJiTime.getNextAM0Date() / 1000);
		PlayerSnapshotInfo.Builder snapShotBuilder = SnapShotManager.getInstance()
				.getPlayerSnapShot(entrty.getPlayerId());

		Map<Integer, Integer> fightMap = new TreeMap<>();
		for (RoleInfo roleInfo : snapShotBuilder.getMercenaryInfoList()) {
			if (roleInfo.getActiviteState() == Const.RoleActiviteState.IS_ACTIVITE_VALUE) {
				fightMap.put(roleInfo.getItemId(), roleInfo.getFight());
			}

		}

		List<Map.Entry<Integer, Integer>> fightList = fightMap.entrySet().stream()
				.sorted((entry1, entry2) -> entry1.getValue().compareTo(entry2.getValue()))
				.collect(Collectors.toList());
		if (fightList != null && !fightList.isEmpty()) {
			int itemId = fightList.get(fightList.size() - 1).getKey();
			entrty.setEighteenPrinceHelpRoleItemId(itemId);
			entrty.setResetTime((int) (GuaJiTime.getNextAM0Date() / 1000));
			entrty.setBigMedicalKit(0);
			entrty.setMidleMedicalKit(0);
			entrty.setSmallMedicalKit(0);
			entrty.setEighteenPrinceCount(0);
			entrty.setLayerId(0);
			entrty.setFirstChallenge(1);
			entrty.getHelpIds().clear();
			entrty.getHelpHistoryMap().clear();
			entrty.getEnemyFormationMap().clear();
			entrty.clearFormationIds();
			entrty.getFormationHistoryMap().clear();
			/*
			 * for (Integer mercenaryKey : entrty.getFormationHistoryMap().keySet()) {
			 * entrty.getFormationHistoryMap().get(mercenaryKey).setHp(100);
			 * entrty.getFormationHistoryMap().get(mercenaryKey).setMp(100); }
			 */
			entrty.notifyUpdate();

			if (ServerData.getInstance().isPlayerOnline(entrty.getPlayerId())) {

				Player player = PlayerUtil.queryPlayer(entrty.getPlayerId());
				if (player == null) {
					return true;
				}
				HPEighteenPrincesLayerInfoRet.Builder builder = HPEighteenPrincesLayerInfoRet.newBuilder();
				builder.setLayerId(entrty.getLayerId());
				if (entrty.getLayerId() == SysBasicCfg.getInstance().getEighteenPrincesMaxLayerNum()) {
					if (entrty.isFormationAlive()) {
						builder.setLayerStatus(Const.EighteenPrincesLayerStatus.ALL_PASS_VALUE);
					}
				} else {
					builder.setLayerStatus(Const.EighteenPrincesLayerStatus.CAN_CHANLLENGE_VALUE);
				}
				if (entrty.isFirstChallenge()) {
					builder.setIsFirstBattle(0);
				} else {
					builder.setIsFirstBattle(1);
				}
				int totalLayer = SysBasicCfg.getInstance().getEighteenPrincesMaxLayerNum();
				for (int layerId = 1; layerId <= totalLayer; layerId++) {
					ArenaSnapShotEntity arenaSnapShotEntity = EighteenPrincesManager.getInstance()
							.getArenaSnapShotEntityByLayerId(layerId);
					if (arenaSnapShotEntity != null) {
						if (arenaSnapShotEntity.getType() == 1) {
							PlayerSnapshotInfo.Builder snapshot = arenaSnapShotEntity.getSnapshotInfo();
							if (snapshot != null) {
								builder.addFightvalue(snapshot.getMainRoleInfo().getMarsterFight());
							}

						} else {
							RoleInfo.Builder monsterRole = null;
							monsterRole = BuilderUtil.genMonsterRoleInfoBuilder(arenaSnapShotEntity.getPlayerId(),
									true);

							if (monsterRole != null) {
								builder.addFightvalue(monsterRole.getFight());
							}
						}
					}

				}
				if (isOpen()) {
					PlayerUtil.syncEighteenPrinces(player, entrty);
				}

				player.sendProtocol(Protocol.valueOf(HP.code.EIGHTEENPRINCES_LAYER_INFO_S_VALUE, builder));

			}

		}

		return true;
	}

	@Override
	public boolean onMessage(Msg msg) {
		if (msg.getMsg() == GsConst.MsgType.EIGHTEENPRINCESD_USE_MERCENARYINFO) {
			UseMercenary(msg);
			return true;
		}
		return super.onMessage(msg);
	}

	private void UseMercenary(Msg msg) {
		// 发起挑战的玩家Id
		int playerId = msg.getParam(0);
		// 使用好友
		int frendId = msg.getParam(1);

		EighteenPrincesEntity friendEighteenPrincesEntity = EighteenPrincesManager.getInstance()
				.queryEighteenPrincesaEntity(frendId);
		if (friendEighteenPrincesEntity == null) {
			return;
		}

		EighteenPrincesEntity eighteenPrincesEntity = EighteenPrincesManager.getInstance()
				.queryEighteenPrincesaEntity(playerId);
		if (eighteenPrincesEntity == null) {
			return;
		}

		int helpPlayerId = friendEighteenPrincesEntity.getPlayerId();
		Map<Integer, HelpMercenaryInfo> helpHistoryMap = eighteenPrincesEntity.getHelpHistoryMap();

		boolean isConsumeTimes = false;
		if (!helpHistoryMap.containsKey(helpPlayerId) || helpHistoryMap.get(helpPlayerId).getHp() <= 0) {
			isConsumeTimes = true;
		}
		// 判断次数
		if (isConsumeTimes) {
			int usedCount = friendEighteenPrincesEntity.getEighteenPrinceCount() + 1;
			// 协战武将被使用的最大次数
			int maxCount = SysBasicCfg.getInstance().getEighteenMaxUseTimes();
			if (usedCount > maxCount) {
				// 协战武将超过最大次数 多线程问题
				GuaJiXID xid = GuaJiXID.valueOf(GsConst.ObjType.PLAYER, helpPlayerId);
				Msg defenderMsg = Msg.valueOf(GsConst.MsgType.EIGHTEENPRINCESD_USE_MERCENARYERROR, xid);
				GsApp.getInstance().postMsg(defenderMsg);
				return;
			}
			//超过免费次数
			if (usedCount > SysBasicCfg.getInstance().getEighteenFreeUseTimes()) {
				int price = SysBasicCfg.getInstance().getEighteenUseSpend();
				Player player = PlayerUtil.queryPlayer(playerId);
				if (player != null) {
					if (player.getGold() >= price) {
						// 在线玩家扣费
						player.consumeGold(price, Action.EIGHTEENPRINCESD_USE_MERCENARY);
						ConsumeItems.valueOf(changeType.CHANGE_GOLD, price).pushChange(player);
					} else {
						// 协战武将超过最大次数 多线程问题
						GuaJiXID xid = GuaJiXID.valueOf(GsConst.ObjType.PLAYER, helpPlayerId);
						Msg defenderMsg = Msg.valueOf(GsConst.MsgType.EIGHTEENPRINCESD_USE_MERCENARYERROR, xid);
						GsApp.getInstance().postMsg(defenderMsg);
						return;
					}
				}

			}

			if (!helpHistoryMap.containsKey(helpPlayerId)) {
				HelpMercenaryInfo helpMercenaryInfo = new HelpMercenaryInfo(100, 100, helpPlayerId);
				helpHistoryMap.put(helpPlayerId, helpMercenaryInfo);
			} else {
				helpHistoryMap.get(helpPlayerId).setHp(100);
				helpHistoryMap.get(helpPlayerId).setMp(100);
			}
			friendEighteenPrincesEntity
					.setEighteenPrinceCount(friendEighteenPrincesEntity.getEighteenPrinceCount() + 1);
			PlayerSnapshotInfo.Builder friendSnapshot = SnapShotManager.getInstance().getPlayerSnapShot(helpPlayerId);
			if (friendSnapshot != null) {
				EighteenPrincesHelpHistoryEntity historyEntity = new EighteenPrincesHelpHistoryEntity();
				historyEntity.setFriendPlayerId(playerId);
				historyEntity.setPlayerId(friendSnapshot.getPlayerId());
				historyEntity.setHelpCount(friendEighteenPrincesEntity.getEighteenPrinceCount());
				if (DBManager.getInstance().create(historyEntity)) {
					if (ServerData.getInstance().isPlayerOnline(helpPlayerId)) {
						// 刷新好友协战武将记录
						GuaJiXID xid = GuaJiXID.valueOf(GsConst.ObjType.PLAYER, helpPlayerId);
						Msg helpSynMsg = Msg.valueOf(GsConst.MsgType.EIGHTEENPRINCESD_HELPHISTORY_SYNC, xid);
						helpSynMsg.pushParam(historyEntity);
						GsApp.getInstance().postMsg(helpSynMsg);
					}
				}
			}
			// 更新次数
			friendEighteenPrincesEntity.notifyUpdate();
		}
		eighteenPrincesEntity.clearHelpIds();
		eighteenPrincesEntity.addHelp(helpPlayerId);
		eighteenPrincesEntity.notifyUpdate();

		if (ServerData.getInstance().isPlayerOnline(playerId)) {
			// 刷新被挑战者的排名
			Msg useMsg = Msg.valueOf(GsConst.MsgType.EIGHTEENPRINCESD_USE_FINISH,
					GuaJiXID.valueOf(GsConst.ObjType.PLAYER, playerId));
			useMsg.pushParam(helpPlayerId);
			GsApp.getInstance().postMsg(useMsg);
		}

	}

	/**
	 * @return true 已开启 false 未开启
	 */
	public boolean isOpen() {

		String serviceDate = GsConfig.getInstance().getServiceDate();

		long openTime = GuaJiTime.DATE_FORMATOR_DAYNUM(serviceDate).getTime()
				+ SysBasicCfg.getInstance().getEighteenPrincesOpenServerDays() * 24 * 60 * 60 * 1000;

		if (GuaJiTime.getCalendar().getTime().getTime() <= openTime) {
			return false;
		}
		Calendar nowCal = GuaJiTime.getCalendar();
		nowCal.add(Calendar.DATE, -2);
		String mapKey = GuaJiTime.DATE_FORMATOR_DAYNUM(GuaJiTime.getAM0Date(nowCal.getTime())) + "_"
				+ String.valueOf(systype);
		List<ArenaSnapShotEntity> arenaSnapShotList = ArenaManager.getInstance().getArenaSnapShotListByKey(mapKey);
		if (arenaSnapShotList == null || arenaSnapShotList.isEmpty()) {
			return false;
		}
		return true;
	}

}
