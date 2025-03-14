package com.guaji.game.module;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.guaji.annotation.ProtocolHandlerAnno;
import org.guaji.config.ConfigManager;
import org.guaji.msg.Msg;
import org.guaji.net.protocol.Protocol;
import org.guaji.os.GuaJiRand;
import org.guaji.os.GuaJiTime;
import org.guaji.xid.GuaJiXID;

import com.guaji.game.GsApp;
import com.guaji.game.battle.BattleRole;
import com.guaji.game.battle.Battlefield;
import com.guaji.game.config.BadgeCfg;
import com.guaji.game.config.EighteenPrincesCfg;
import com.guaji.game.config.EighteenPrincesDoubleBuyCfg;
import com.guaji.game.config.EighteenPrincesHelpAwardCfg;
import com.guaji.game.config.EighteenPrincesMedicalCfg;
import com.guaji.game.config.SysBasicCfg;
import com.guaji.game.entity.ArenaSnapShotEntity;
import com.guaji.game.entity.EighteenPrincesEntity;
import com.guaji.game.entity.EighteenPrincesHelpHistoryEntity;
import com.guaji.game.entity.FriendEntity;
import com.guaji.game.item.AwardItems;
import com.guaji.game.item.ConsumeItems;
import com.guaji.game.item.AwardItems.Item;
import com.guaji.game.item.HelpMercenaryInfo;
import com.guaji.game.item.ItemInfo;
import com.guaji.game.log.BehaviorLogger;
import com.guaji.game.log.BehaviorLogger.Action;
import com.guaji.game.manager.EighteenPrincesManager;
import com.guaji.game.manager.SnapShotManager;
import com.guaji.game.module.quest.QuestEventBus;
import com.guaji.game.player.Player;
import com.guaji.game.player.PlayerModule;
import com.guaji.game.protocol.Battle;
import com.guaji.game.protocol.Battle.BattleInfo;
import com.guaji.game.protocol.Const;
import com.guaji.game.protocol.Const.EighteenPrincesMedicalKitTYPE;
import com.guaji.game.protocol.Const.QuestEventType;
import com.guaji.game.protocol.Const.changeType;
import com.guaji.game.protocol.Const.itemType;
import com.guaji.game.protocol.EighteenPrinces.EighteenPrincesChallengeRet;
import com.guaji.game.protocol.EighteenPrinces.HPEighteenPrincesChangeHelpReq;
import com.guaji.game.protocol.EighteenPrinces.HPEighteenPrincesChangeHelpRet;
import com.guaji.game.protocol.EighteenPrinces.HPEighteenPrincesFormationInfoRet;
import com.guaji.game.protocol.EighteenPrinces.HPEighteenPrincesFormationReq;
import com.guaji.game.protocol.EighteenPrinces.HPEighteenPrincesFormationRet;
import com.guaji.game.protocol.EighteenPrinces.HPEighteenPrincesHelpHistoryRet;
import com.guaji.game.protocol.EighteenPrinces.HPEighteenPrincesHelpListRet;
import com.guaji.game.protocol.EighteenPrinces.HPEighteenPrincesHelpRewardReq;
import com.guaji.game.protocol.EighteenPrinces.HPEighteenPrincesHelpRewardRet;
import com.guaji.game.protocol.EighteenPrinces.HPEighteenPrincesLayerInfoRet;
import com.guaji.game.protocol.EighteenPrinces.HPEighteenPrincesRewardNotice;
import com.guaji.game.protocol.EighteenPrinces.HPEighteenPrincesUseMedicalReq;
import com.guaji.game.protocol.EighteenPrinces.HPEighteenPrincesUseMedicalRet;
import com.guaji.game.protocol.EighteenPrinces.HPSyncMedicalKitInfoRet;
import com.guaji.game.protocol.EighteenPrinces.HelpHistoryItemInfo;
import com.guaji.game.protocol.EighteenPrinces.HelpItemInfo;
import com.guaji.game.protocol.EighteenPrinces.MercenaryInfo;
import com.guaji.game.protocol.HP;
import com.guaji.game.protocol.Player.RoleInfo;
import com.guaji.game.protocol.Reward.RewardInfo;
import com.guaji.game.protocol.Snapshot.PlayerSnapshotInfo;
import com.guaji.game.protocol.Status;
import com.guaji.game.util.AwardUtil;
import com.guaji.game.util.BuilderUtil;
import com.guaji.game.util.GsConst;
import com.guaji.game.util.PlayerUtil;

/**
 * 竞技场模块
 */
public class PlayerEighteenPrincesModule extends PlayerModule {

	public PlayerEighteenPrincesModule(Player player) {
		super(player);

		// 注册监听消息
		listenMsg(GsConst.MsgType.EIGHTEENPRINCESD_USE_FINISH);
		listenMsg(GsConst.MsgType.EIGHTEENPRINCESD_USE_MERCENARYERROR);
		listenMsg(GsConst.MsgType.EIGHTEENPRINCESD_HELPHISTORY_SYNC);
	}

	private EighteenPrincesEntity eighteenPrincesEntity;

	private List<EighteenPrincesHelpHistoryEntity> eighteenPrincesHelpHistoryEntities;

	@Override
	protected boolean onPlayerLogin() {

		eighteenPrincesEntity = player.getPlayerData().loadEighteenPrincesEntity();
		if (eighteenPrincesEntity != null && EighteenPrincesManager.getInstance().isOpen()) {
			PlayerUtil.syncEighteenPrinces(player, eighteenPrincesEntity);
			HPSyncMedicalKitInfoRet.Builder builder = BuilderUtil
					.genSyncMedicalKitInfoRetBuilders(eighteenPrincesEntity);
			if (builder != null) {
				player.sendProtocol(Protocol.valueOf(HP.code.EIGHTEENPRINCES_MEDICALKIT_S_VALUE, builder));
			}
		}
		eighteenPrincesHelpHistoryEntities = player.getPlayerData().loadEighteenPrincesHelpHistoryEntities();
		if (eighteenPrincesHelpHistoryEntities != null && !eighteenPrincesHelpHistoryEntities.isEmpty()
				&& EighteenPrincesManager.getInstance().isOpen()) {
			noticeGetReward(player);
		}
		return true;
	}

	/**
	 * 获取协战历史
	 *
	 * @param protocol
	 */
	@ProtocolHandlerAnno(code = HP.code.EIGHTEENPRINCES_HELP_HISTORY_C_VALUE)
	public void GetHelpHistory(Protocol protocol) {
		if (eighteenPrincesEntity == null) {
			eighteenPrincesEntity = EighteenPrincesManager.getInstance().queryEighteenPrincesaEntity(player.getId());
			if (eighteenPrincesEntity == null) {
				return;
			}
			
		}

		if (!EighteenPrincesManager.getInstance().isOpen()) {
			player.sendError(protocol.getType(), Status.error.EIGHTEENPRINCES_NOTOPEN_VALUE);
			return;
		}

		HPEighteenPrincesHelpHistoryRet.Builder builder = HPEighteenPrincesHelpHistoryRet.newBuilder();

		int todayNum = 0;
		if (eighteenPrincesHelpHistoryEntities != null) {
			Iterator<EighteenPrincesHelpHistoryEntity> item = eighteenPrincesHelpHistoryEntities.iterator();
			while (item.hasNext()) {
				EighteenPrincesHelpHistoryEntity helpHistory = item.next();
				if (helpHistory == null) {
					item.remove();
				} else {
					int spanDays = GuaJiTime.calcBetweenDays(helpHistory.getCreateTime(),
							GuaJiTime.getCalendar().getTime());
					if (spanDays >= 3) {
						helpHistory.setInvalid(true);
						item.remove();
					} else {
						HelpHistoryItemInfo.Builder history = HelpHistoryItemInfo.newBuilder();
						PlayerSnapshotInfo.Builder snapshot = SnapShotManager.getInstance()
								.getPlayerSnapShot(helpHistory.getFriendPlayerId());
						if (snapshot == null) {
							continue;
						}
						if (GuaJiTime.isToday(helpHistory.getCreateTime())) {
							todayNum += 1;
						}
						history.setHistoryId(helpHistory.getId());
						history.setHelpTime((int) (helpHistory.getCreateTime().getTime() / 1000));
						history.setName(snapshot.getMainRoleInfo().getName());
						history.setLevel(snapshot.getMainRoleInfo().getLevel());
						history.setHeadIcon(snapshot.getPlayerInfo().getHeadIcon());
						history.setHelpCount(helpHistory.getHelpCount());
						history.setProf(snapshot.getMainRoleInfo().getProf());
						if (helpHistory.isReward()) {
							history.setIsGet(1);
						} else {
							history.setIsGet(0);
						}

						builder.addHistoryInfos(history);
					}
				}

			}

			builder.setTodayCount(todayNum);
		}
		player.sendProtocol(Protocol.valueOf(HP.code.EIGHTEENPRINCES_HELP_HISTORY_S_VALUE, builder));
	}

	/**
	 * 获取协战奖励
	 *
	 * @param protocol
	 */
	@ProtocolHandlerAnno(code = HP.code.EIGHTEENPRINCES_HELP_REWARD_C_VALUE)
	public void GetHelpReward(Protocol protocol) {

		if (eighteenPrincesEntity == null) {
			eighteenPrincesEntity = EighteenPrincesManager.getInstance().queryEighteenPrincesaEntity(player.getId());
			if (eighteenPrincesEntity == null) {
				return;
			}
			
		}

		if (!EighteenPrincesManager.getInstance().isOpen()) {
			player.sendError(protocol.getType(), Status.error.EIGHTEENPRINCES_NOTOPEN_VALUE);
			return;
		}

		HPEighteenPrincesHelpRewardReq req = protocol
				.parseProtocol(HPEighteenPrincesHelpRewardReq.getDefaultInstance());
		long historyId = req.getHistoryId();
		EighteenPrincesHelpHistoryEntity historyEntity = eighteenPrincesHelpHistoryEntities.stream()
				.filter(item -> item.getId() == historyId).findFirst().orElse(null);
		// 协战记录是否存在
		if (historyEntity == null) {
			player.sendError(protocol.getType(), Status.error.PARAMS_INVALID);
			return;
		}
		// 协战是否已经领取奖励
		if (historyEntity.isReward()) {
			player.sendError(protocol.getType(), Status.error.EIGHTEENPRINCES_HELPREWARD_HASGET_VALUE);

			return;
		}
		// 获取奖励配置
		EighteenPrincesHelpAwardCfg cfg = ConfigManager.getInstance().getConfigByKey(EighteenPrincesHelpAwardCfg.class,
				historyEntity.getHelpCount());
		if (cfg == null) {
			player.sendError(protocol.getType(), Status.error.CONFIG_NOT_FOUND_VALUE);
			return;
		}
		AwardItems awards = AwardItems.valueOf(cfg.getRewards());
		// 发放奖励
		awards.rewardTakeAffectAndPush(player, BehaviorLogger.Action.EIGHTEEN_PRINCES_HELP_REWARD, 2);
		historyEntity.setReward(true);
		historyEntity.notifyUpdate();

		HPEighteenPrincesHelpRewardRet.Builder builder = HPEighteenPrincesHelpRewardRet.newBuilder();
		// builder.setHistoryId(historyEntity.getId());
		builder.addHistoryId(historyEntity.getId());
		builder.setResult(1);
		player.sendProtocol(Protocol.valueOf(HP.code.EIGHTEENPRINCES_HELP_REWARD_S_VALUE, builder));

	}

	@ProtocolHandlerAnno(code = HP.code.EIGHTEENPRINCES_HELP_ONEKEYAWARD_C_VALUE)
	public void GetOneKeyHelpReward(Protocol protocol) {

		if (eighteenPrincesEntity == null) {
			eighteenPrincesEntity = EighteenPrincesManager.getInstance().queryEighteenPrincesaEntity(player.getId());
			if (eighteenPrincesEntity == null) {
				return;
			}
			
		}

		if (!EighteenPrincesManager.getInstance().isOpen()) {
			player.sendError(protocol.getType(), Status.error.EIGHTEENPRINCES_NOTOPEN_VALUE);
			return;
		}

		long canGetCount = eighteenPrincesHelpHistoryEntities.stream().filter(item -> item.isReward() == false).count();

		if (canGetCount <= 0) {
			player.sendError(protocol.getType(), Status.error.EIGHTEENPRINCES_HELPREWARD_NOHAVE_VALUE);
			return;
		}

		AwardItems awards = new AwardItems();
		HPEighteenPrincesHelpRewardRet.Builder builder = HPEighteenPrincesHelpRewardRet.newBuilder();
		eighteenPrincesHelpHistoryEntities.stream().filter(item -> item.isReward() == false).forEach(item -> {
			// 获取奖励配置
			EighteenPrincesHelpAwardCfg cfg = ConfigManager.getInstance()
					.getConfigByKey(EighteenPrincesHelpAwardCfg.class, item.getHelpCount());
			if (cfg != null) {
				awards.appendAward(AwardItems.valueOf(cfg.getRewards()));
				builder.addHistoryId(item.getId());

				item.setReward(true);
				item.notifyUpdate(true);
			}
		});
		awards.rewardTakeAffectAndPush(player, BehaviorLogger.Action.EIGHTEEN_PRINCES_HELP_REWARD, 2);
		builder.setResult(1);
		player.sendProtocol(Protocol.valueOf(HP.code.EIGHTEENPRINCES_HELP_ONEKEYAWARD_S_VALUE, builder));

	}

	/**
	 * 获取协战列表
	 *
	 * @param protocol
	 */
	@ProtocolHandlerAnno(code = HP.code.EIGHTEENPRINCES_HELP_LIST_C_VALUE)
	public void GetHelpList(Protocol protocol) {

		if (eighteenPrincesEntity == null) {
			eighteenPrincesEntity = EighteenPrincesManager.getInstance().queryEighteenPrincesaEntity(player.getId());
			if (eighteenPrincesEntity == null) {
				return;
			}
			
		}

		if (!EighteenPrincesManager.getInstance().isOpen()) {
			player.sendError(protocol.getType(), Status.error.EIGHTEENPRINCES_NOTOPEN_VALUE);
			return;
		}

		HPEighteenPrincesHelpListRet.Builder builder = HPEighteenPrincesHelpListRet.newBuilder();
		FriendEntity friendEntity = player.getPlayerData().getFriendEntity();

		// 当前选中的协战武将
		for (Integer helpId : eighteenPrincesEntity.getHelpIds()) {
			HelpMercenaryInfo mercenaryInfo = eighteenPrincesEntity.getHelpHistoryMap().get(helpId);
			if (mercenaryInfo != null && mercenaryInfo.isAlive()) {
				builder.addPlayerId(helpId);
			}
		}

		if (builder.getPlayerIdList().isEmpty()) {
			builder.addPlayerId(0);
		}

		for (Integer friendId : friendEntity.getFriendIdSet()) {
			PlayerSnapshotInfo.Builder friendSnapshot = SnapShotManager.getInstance().getPlayerSnapShot(friendId);

			if (friendSnapshot == null) {
				continue;
			}

			EighteenPrincesEntity friendEighteenPrincesEntity = EighteenPrincesManager.getInstance()
					.queryEighteenPrincesaEntity(friendId);

			if (friendEighteenPrincesEntity == null) {
				continue;
			}
			HelpItemInfo.Builder helpItemInfo = HelpItemInfo.newBuilder();
			helpItemInfo.setPlayerId(friendId);
			helpItemInfo.setName(friendSnapshot.getMainRoleInfo().getName());
			for (RoleInfo.Builder roleInfo : friendSnapshot.getMercenaryInfoBuilderList()) {
				if (roleInfo.getItemId() == friendEighteenPrincesEntity.getEighteenPrinceHelpRoleItemId()) {
					helpItemInfo.setFightValue(roleInfo.getFight());
					break;
				}
			}

			helpItemInfo.setLevel(friendSnapshot.getMainRoleInfo().getLevel());

			helpItemInfo.setRoleItemId(friendEighteenPrincesEntity.getEighteenPrinceHelpRoleItemId());
			int leftTimes = SysBasicCfg.getInstance().getEighteenMaxUseTimes() > friendEighteenPrincesEntity
					.getEighteenPrinceCount()
							? SysBasicCfg.getInstance().getEighteenMaxUseTimes()
									- friendEighteenPrincesEntity.getEighteenPrinceCount()
							: 0;
			helpItemInfo.setLeftCount(leftTimes);

			int helpPlayerId = friendEighteenPrincesEntity.getPlayerId();
			Map<Integer, HelpMercenaryInfo> helpHistoryMap = eighteenPrincesEntity.getHelpHistoryMap();

			int isCanUse = 1;
			if (!helpHistoryMap.containsKey(helpPlayerId) || helpHistoryMap.get(helpPlayerId).getHp() <= 0) {
				isCanUse = 0;
			}

			helpItemInfo.setIsCanUse(isCanUse);
			builder.addInfos(helpItemInfo);
		}

		player.sendProtocol(Protocol.valueOf(HP.code.EIGHTEENPRINCES_HELP_LIST_S_VALUE, builder));
	}

	/**
	 * 上阵协战副将
	 *
	 * @param protocol
	 */
	@ProtocolHandlerAnno(code = HP.code.EIGHTEENPRINCES_HELP_CHANGE_C_VALUE)
	public void HelpChange(Protocol protocol) {

		if (eighteenPrincesEntity == null) {
			eighteenPrincesEntity = EighteenPrincesManager.getInstance().queryEighteenPrincesaEntity(player.getId());
			if (eighteenPrincesEntity == null) {
				return;
			}
			
		}

		if (!EighteenPrincesManager.getInstance().isOpen()) {
			player.sendError(protocol.getType(), Status.error.EIGHTEENPRINCES_NOTOPEN_VALUE);
			return;
		}

		HPEighteenPrincesChangeHelpReq req = protocol
				.parseProtocol(HPEighteenPrincesChangeHelpReq.getDefaultInstance());
		int friendId = req.getFriendId();
		if (friendId == 0) {
			eighteenPrincesEntity.clearHelpIds();
			eighteenPrincesEntity.notifyUpdate();
			HPEighteenPrincesChangeHelpRet.Builder builder = HPEighteenPrincesChangeHelpRet.newBuilder();
			builder.setFriendId(friendId);
			builder.setResult(1);
			player.sendProtocol(Protocol.valueOf(HP.code.EIGHTEENPRINCES_HELP_CHANGE_S_VALUE, builder));
		} else {
			EighteenPrincesEntity friendEighteenPrincesEntity = EighteenPrincesManager.getInstance()
					.queryEighteenPrincesaEntity(friendId);
			if (friendEighteenPrincesEntity == null) {
				player.sendError(protocol.getType(), Status.error.PARAMS_INVALID);
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
					// 达到最大次数
					player.sendError(protocol.getType(), Status.error.EIGHTEENPRINCES_MERCENARY_CANNOTUSE_VALUE);
					return;
				}
			}
			if (!isConsumeTimes) {
				eighteenPrincesEntity.clearHelpIds();
				eighteenPrincesEntity.addHelp(helpPlayerId);
				eighteenPrincesEntity.notifyUpdate();
				HPEighteenPrincesChangeHelpRet.Builder builder = HPEighteenPrincesChangeHelpRet.newBuilder();
				builder.setFriendId(friendId);
				builder.setResult(1);
				player.sendProtocol(Protocol.valueOf(HP.code.EIGHTEENPRINCES_HELP_CHANGE_S_VALUE, builder));
			} else {
				Msg msg = Msg.valueOf(GsConst.MsgType.EIGHTEENPRINCESD_USE_MERCENARYINFO);
				msg.setTarget(GuaJiXID.valueOf(GsConst.ObjType.MANAGER, GsConst.ObjId.EighteenPrinces));
				msg.pushParam(player.getId());
				msg.pushParam(friendId);
				GsApp.getInstance().postMsg(msg);
			}

		}

	}

	/**
	 * @param protocol
	 * 
	 *                 双倍购买
	 */
	@ProtocolHandlerAnno(code = HP.code.EIGHTEENPRINCES_HELP_DOUBLEBUY_C_VALUE)
	public void doubleBuy(Protocol protocol) {

		if (eighteenPrincesEntity == null) {
			eighteenPrincesEntity = EighteenPrincesManager.getInstance().queryEighteenPrincesaEntity(player.getId());
			if (eighteenPrincesEntity == null) {
				return;
			}
			
		}

		if (!EighteenPrincesManager.getInstance().isOpen()) {
			player.sendError(protocol.getType(), Status.error.EIGHTEENPRINCES_NOTOPEN_VALUE);
			return;
		}

		if (eighteenPrincesEntity.getDropItemId() == 0) {
			player.sendError(protocol.getType(), Status.error.EIGHTEENPRINCES_HELPREWARD_CANNOTDOUBLEBUY_VALUE);
			return;
		}

		BadgeCfg badgeCfg = ConfigManager.getInstance().getConfigByKey(BadgeCfg.class,
				eighteenPrincesEntity.getDropItemId());
		if (badgeCfg == null) {
			player.sendError(protocol.getType(), Status.error.CONFIG_NOT_FOUND_VALUE);
			return;
		}

		EighteenPrincesDoubleBuyCfg doubleBuyCfg = ConfigManager.getInstance()
				.getConfigByKey(EighteenPrincesDoubleBuyCfg.class, badgeCfg.getId());
		if (doubleBuyCfg == null) {
			player.sendError(protocol.getType(), Status.error.CONFIG_NOT_FOUND_VALUE);
			return;
		}
		
		if(player.getGold()<doubleBuyCfg.getCost()) {
			player.sendError(protocol.getType(), Status.error.GOLD_NOT_ENOUGH_VALUE);
			return;	
		}
		
		// 推送消耗
		player.consumeGold(doubleBuyCfg.getCost(), Action.EIGHTEEN_PRINCE_DOUBLEBUY);
		ConsumeItems.valueOf(changeType.CHANGE_GOLD,doubleBuyCfg.getCost()).pushChange(player);
		
	
		// 必掉
		AwardItems buyItem = new AwardItems();

		buyItem.addItem(itemType.BADGE_VALUE * GsConst.ITEM_TYPE_BASE, badgeCfg.getId(), 1);
		buyItem.rewardTakeAffectAndPush(player, Action.EIGHTEEN_PRINCE_DOUBLEBUY, 0);
		eighteenPrincesEntity.setDropItemId(0);
		eighteenPrincesEntity.notifyUpdate();
	}

	/**
	 * 获取关卡数据
	 *
	 * @param protocol
	 */
	@ProtocolHandlerAnno(code = HP.code.EIGHTEENPRINCES_LAYER_INFO_C_VALUE)
	public void GetLayerInfo(Protocol protocol) {
		if (eighteenPrincesEntity == null) {
			eighteenPrincesEntity = EighteenPrincesManager.getInstance().queryEighteenPrincesaEntity(player.getId());
			if (eighteenPrincesEntity == null) {
				return;
			}
			
		}
		if (!EighteenPrincesManager.getInstance().isOpen()) {
			player.sendError(protocol.getType(), Status.error.EIGHTEENPRINCES_NOTOPEN_VALUE);
			return;
		}

		HPEighteenPrincesLayerInfoRet.Builder builder = HPEighteenPrincesLayerInfoRet.newBuilder();
		builder.setLayerId(eighteenPrincesEntity.getLayerId());
		if (eighteenPrincesEntity.getLayerId() == SysBasicCfg.getInstance().getEighteenPrincesMaxLayerNum()) {
			if (eighteenPrincesEntity.isFormationAlive()) {
				builder.setLayerStatus(Const.EighteenPrincesLayerStatus.ALL_PASS_VALUE);
			}
		} else {
			builder.setLayerStatus(Const.EighteenPrincesLayerStatus.CAN_CHANLLENGE_VALUE);
		}

		if (eighteenPrincesEntity.isFirstChallenge()) {
			builder.setIsFirstBattle(0);
		} else {
			builder.setIsFirstBattle(1);
		}

		int totalLayer = SysBasicCfg.getInstance().getEighteenPrincesMaxLayerNum();
		for (int layerId = 1; layerId <= totalLayer; layerId++) {
			ArenaSnapShotEntity arenaSnapShotEntity = EighteenPrincesManager.getInstance()
					.getArenaSnapShotEntityByLayerId(layerId);
			if (arenaSnapShotEntity.getType() == 1) {
				PlayerSnapshotInfo.Builder snapshot = arenaSnapShotEntity.getSnapshotInfo();
				if (snapshot != null) {
					builder.addFightvalue(snapshot.getMainRoleInfo().getMarsterFight());
				}

			} else {
				RoleInfo.Builder monsterRole = null;
				monsterRole = BuilderUtil.genMonsterRoleInfoBuilder(arenaSnapShotEntity.getPlayerId(), true);

				if (monsterRole != null) {
					builder.addFightvalue(monsterRole.getFight());
				}
			}
		}

		player.sendProtocol(Protocol.valueOf(HP.code.EIGHTEENPRINCES_LAYER_INFO_S_VALUE, builder));
	}

	/**
	 * 设置战斗阵型
	 *
	 * @param protocol
	 */
	@ProtocolHandlerAnno(code = HP.code.EIGHTEENPRINCES_FORMATION_C_VALUE)
	public void SetFormation(Protocol protocol) {
		if (eighteenPrincesEntity == null) {
			eighteenPrincesEntity = EighteenPrincesManager.getInstance().queryEighteenPrincesaEntity(player.getId());
			if (eighteenPrincesEntity == null) {
				return;
			}
			
		}
		if (!EighteenPrincesManager.getInstance().isOpen()) {
			player.sendError(protocol.getType(), Status.error.EIGHTEENPRINCES_NOTOPEN_VALUE);
			return;
		}

		HPEighteenPrincesFormationReq req = protocol.parseProtocol(HPEighteenPrincesFormationReq.getDefaultInstance());
		Map<Integer, HelpMercenaryInfo> formationHistoryMap = eighteenPrincesEntity.getFormationHistoryMap();
		if (req.getRoleItemIdList().isEmpty()) {
			// 参数不能为空
			player.sendError(protocol.getType(), Status.error.PARAMS_INVALID);
			return;
		}

		eighteenPrincesEntity.clearFormationIds();
		// 记录
		for (int itemId : req.getRoleItemIdList()) {
			if (!formationHistoryMap.containsKey(itemId)) {
				HelpMercenaryInfo helpMercenaryInfo = new HelpMercenaryInfo(100, 100, player.getId());
				formationHistoryMap.put(itemId, helpMercenaryInfo);
			}
			eighteenPrincesEntity.addFormation(itemId);
		}
		eighteenPrincesEntity.notifyUpdate();
		HPEighteenPrincesFormationRet.Builder builder = HPEighteenPrincesFormationRet.newBuilder();
		builder.setResult(true);
		player.sendProtocol(Protocol.valueOf(HP.code.EIGHTEENPRINCES_FORMATION_S_VALUE, builder));

	}

	/**
	 * 设置战斗阵型
	 *
	 * @param protocol
	 */
	@ProtocolHandlerAnno(code = HP.code.EIGHTEENPRINCES_FORMATIONINFO_C_VALUE)
	public void FormationInfo(Protocol protocol) {

		if (eighteenPrincesEntity == null) {
			eighteenPrincesEntity = EighteenPrincesManager.getInstance().queryEighteenPrincesaEntity(player.getId());
			if (eighteenPrincesEntity == null) {
				return;
			}
			
		}

		if (!EighteenPrincesManager.getInstance().isOpen()) {
			player.sendError(protocol.getType(), Status.error.EIGHTEENPRINCES_NOTOPEN_VALUE);
			return;
		}

		HPEighteenPrincesFormationInfoRet.Builder builder = HPEighteenPrincesFormationInfoRet.newBuilder();

		// 协战武将数据
		for (Integer helpPlayerId : eighteenPrincesEntity.getHelpIds()) {

			HelpMercenaryInfo mercenaryInfo = eighteenPrincesEntity.getHelpHistoryMap().get(helpPlayerId);
			if (mercenaryInfo != null) {
				MercenaryInfo.Builder item = MercenaryInfo.newBuilder();
				item.setHp(mercenaryInfo.getHp());
				item.setMp(mercenaryInfo.getMp());
				item.setPlayerId(mercenaryInfo.getPlayerId());
//			
				EighteenPrincesEntity friendEighteenPrinces = EighteenPrincesManager.getInstance()
						.queryEighteenPrincesaEntity(helpPlayerId);
				if (friendEighteenPrinces != null) {
					item.setItemId(friendEighteenPrinces.getEighteenPrinceHelpRoleItemId());
				} else {
					item.setItemId(0);
				}

				PlayerSnapshotInfo.Builder snap = SnapShotManager.getInstance().getPlayerSnapShot(helpPlayerId);
				if (snap != null) {
					item.setPlayerName(snap.getMainRoleInfo().getName());
				} else {
					item.setPlayerName("");
				}

				item.setLevel(0);
				builder.addHelpItem(item);
			}

		}
		// 协战当前选中武将信息
		for (Integer itemId : eighteenPrincesEntity.getFormationIds()) {
			HelpMercenaryInfo mercenaryInfo = eighteenPrincesEntity.getFormationHistoryMap().get(itemId);
			if (mercenaryInfo != null) {
				MercenaryInfo.Builder item = MercenaryInfo.newBuilder();
				item.setHp(mercenaryInfo.getHp());
				item.setMp(mercenaryInfo.getMp());
				item.setPlayerId(mercenaryInfo.getPlayerId());
				item.setPlayerName(player.getName());
				item.setItemId(itemId);
				item.setLevel(0);
				builder.addRoleItem(item);
			}
		}
		// 历史所选武将
		for (Integer itemId : eighteenPrincesEntity.getFormationHistoryMap().keySet()) {
			if (eighteenPrincesEntity.getFormationIds().contains(itemId)) {
				continue;
			}
			HelpMercenaryInfo mercenaryInfo = eighteenPrincesEntity.getFormationHistoryMap().get(itemId);
			MercenaryInfo.Builder item = MercenaryInfo.newBuilder();
			item.setHp(mercenaryInfo.getHp());
			item.setMp(mercenaryInfo.getMp());
			item.setPlayerId(mercenaryInfo.getPlayerId());
			item.setPlayerName(player.getName());
			item.setItemId(itemId);
			item.setLevel(0);
			item.setPlayerId(mercenaryInfo.getPlayerId());
			builder.addHistoryItem(item);
		}

		// 协战历史武将信息
		for (Integer helpPlayerId : eighteenPrincesEntity.getHelpHistoryMap().keySet()) {
			if (!eighteenPrincesEntity.getHelpHistoryMap().containsKey(helpPlayerId)) {
				continue;
			}
			HelpMercenaryInfo mercenaryInfo = eighteenPrincesEntity.getHelpHistoryMap().get(helpPlayerId);
			MercenaryInfo.Builder item = MercenaryInfo.newBuilder();
			item.setHp(mercenaryInfo.getHp());
			item.setMp(mercenaryInfo.getMp());
			item.setPlayerId(mercenaryInfo.getPlayerId());
			PlayerSnapshotInfo.Builder snap = SnapShotManager.getInstance().getPlayerSnapShot(helpPlayerId);
			if (snap != null) {
				item.setPlayerName(snap.getMainRoleInfo().getName());
			} else {
				item.setPlayerName("");
			}
			item.setItemId(helpPlayerId);
			item.setLevel(0);
			builder.addHistoryHelpItem(item);
		}

		player.sendProtocol(Protocol.valueOf(HP.code.EIGHTEENPRINCES_FORMATIONINFO_S_VALUE, builder));

	}

	/**
	 * 使用医疗箱
	 *
	 * @param protocol
	 */
	@ProtocolHandlerAnno(code = HP.code.EIGHTEENPRINCES_USE_MEDICAL_C_VALUE)
	public void UseMedical(Protocol protocol) {

		if (eighteenPrincesEntity == null) {
			eighteenPrincesEntity = EighteenPrincesManager.getInstance().queryEighteenPrincesaEntity(player.getId());
			if (eighteenPrincesEntity == null) {
				return;
			}
			
		}

		if (!EighteenPrincesManager.getInstance().isOpen()) {
			player.sendError(protocol.getType(), Status.error.EIGHTEENPRINCES_NOTOPEN_VALUE);
			return;
		}

		HPEighteenPrincesUseMedicalReq req = protocol
				.parseProtocol(HPEighteenPrincesUseMedicalReq.getDefaultInstance());
		int medicalType = req.getType();
		int count = req.getCount();

		EighteenPrincesMedicalKitTYPE medicalKitType = EighteenPrincesMedicalKitTYPE.valueOf(medicalType);
		if (medicalKitType == null) {
			// 参数无效
			player.sendError(protocol.getType(), Status.error.PARAMS_INVALID);
			return;
		}
		if (count > eighteenPrincesEntity.getMedicalKitCount(medicalKitType)) {

			player.sendError(protocol.getType(), Status.error.EIGHTEENPRINCES_MEDICALKIT_NOTENOUGH_VALUE);
			return;
		}

		EighteenPrincesMedicalCfg medicalCfg = ConfigManager.getInstance()
				.getConfigByKey(EighteenPrincesMedicalCfg.class, medicalKitType.getNumber());
		if (medicalCfg == null) {
			player.sendError(protocol.getType(), Status.error.CONFIG_NOT_FOUND);
			return;
		}
		int addHpRadio = (medicalCfg.getAddHp() * count) >= 100 ? 100 : medicalCfg.getAddHp() * count;
		for (Integer itemId : eighteenPrincesEntity.getFormationHistoryMap().keySet()) {
			HelpMercenaryInfo mercenaryInfo = eighteenPrincesEntity.getFormationHistoryMap().get(itemId);
			if (mercenaryInfo != null) {
				if ((mercenaryInfo.getHp() + addHpRadio) > 100) {
					mercenaryInfo.setHp(100);
				} else {
					mercenaryInfo.setHp(mercenaryInfo.getHp() + addHpRadio);
				}

				if ((mercenaryInfo.getMp() + addHpRadio) > 100) {
					mercenaryInfo.setMp(100);
				} else {
					mercenaryInfo.setMp(mercenaryInfo.getMp() + addHpRadio);
				}
			}

		}
		//
		for (Integer playerId : eighteenPrincesEntity.getHelpHistoryMap().keySet()) {
			HelpMercenaryInfo mercenaryInfo = eighteenPrincesEntity.getHelpHistoryMap().get(playerId);
			if (mercenaryInfo != null) {
				if ((mercenaryInfo.getHp() + addHpRadio) > 100) {
					mercenaryInfo.setHp(100);
				} else {
					mercenaryInfo.setHp(mercenaryInfo.getHp() + addHpRadio);
				}

				if ((mercenaryInfo.getMp() + addHpRadio) > 100) {
					mercenaryInfo.setMp(100);
				} else {
					mercenaryInfo.setMp(mercenaryInfo.getMp() + addHpRadio);
				}
			}

		}

		eighteenPrincesEntity.useMedicalKit(medicalKitType, count);
		eighteenPrincesEntity.notifyUpdate();
		HPSyncMedicalKitInfoRet.Builder medicalKitBuilder = BuilderUtil
				.genSyncMedicalKitInfoRetBuilders(eighteenPrincesEntity);
		if (medicalKitBuilder != null) {
			player.sendProtocol(Protocol.valueOf(HP.code.EIGHTEENPRINCES_MEDICALKIT_S_VALUE, medicalKitBuilder));
		}

		HPEighteenPrincesUseMedicalRet.Builder builder = HPEighteenPrincesUseMedicalRet.newBuilder();
		builder.setResult(true);

		player.sendProtocol(Protocol.valueOf(HP.code.EIGHTEENPRINCES_USE_MEDICAL_S_VALUE, builder));

	}

	/**
	 * 关卡战斗
	 *
	 * @param protocol
	 */
	@ProtocolHandlerAnno(code = HP.code.EIGHTEENPRINCES_CHALLENGE_C_VALUE)
	public void Challenge(Protocol protocol) {

		if (eighteenPrincesEntity == null) {
			eighteenPrincesEntity = EighteenPrincesManager.getInstance().queryEighteenPrincesaEntity(player.getId());
			if (eighteenPrincesEntity == null) {
				return;
			}
			
		}
		
		if (!EighteenPrincesManager.getInstance().isOpen()) {
			player.sendError(protocol.getType(), Status.error.EIGHTEENPRINCES_NOTOPEN_VALUE);
			return;
		}

		// 当前关卡数
		int curLayerId = eighteenPrincesEntity.getLayerId();
		ArenaSnapShotEntity arenaSnapShotEntity = EighteenPrincesManager.getInstance()
				.getArenaSnapShotEntityByLayerId(curLayerId + 1);

		if (arenaSnapShotEntity == null) {
			return;
		}

		if (eighteenPrincesEntity == null) {
			return;
		}

		if (player.getPlayerData().getLeftBadgeBagCount() <= 0) {
			player.sendError(protocol.getType(), Status.error.BADGE_BAG_GRID_FULL_VALUE);
			return;
		}
		// 第一次挑战
		if (eighteenPrincesEntity.isFirstChallenge()) {
			// 第一次重置挑战阵型
			if (eighteenPrincesEntity.getFormationIds().isEmpty()) {

				for (BattleRole battleRole : player.getPlayerData().getBattleRoles(true)) {
					int itemId = battleRole.getRoleInfo().getItemId();
					eighteenPrincesEntity.addFormation(itemId);
					if (!eighteenPrincesEntity.getFormationHistoryMap().containsKey(itemId)) {
						eighteenPrincesEntity.getFormationHistoryMap().put(itemId,
								new HelpMercenaryInfo(100, 100, player.getId()));
					} else {
						HelpMercenaryInfo helpMercenary = eighteenPrincesEntity.getFormationHistoryMap().get(itemId);
						if (helpMercenary != null) {
							helpMercenary.setHp(100);
							helpMercenary.setMp(100);
						}
					}

				}
				for (BattleRole battleRole : player.getPlayerData().getBattleAssistanceRoles()) {
					int itemId = battleRole.getRoleInfo().getItemId();
					eighteenPrincesEntity.addFormation(itemId);
					if (!eighteenPrincesEntity.getFormationHistoryMap().containsKey(itemId)) {
						eighteenPrincesEntity.getFormationHistoryMap().put(itemId,
								new HelpMercenaryInfo(100, 100, player.getId()));
					} else {
						HelpMercenaryInfo helpMercenary = eighteenPrincesEntity.getFormationHistoryMap().get(itemId);
						if (helpMercenary != null) {
							helpMercenary.setHp(100);
							helpMercenary.setMp(100);
						}
					}
				}
			}
		}

		if (eighteenPrincesEntity.getHelpIds().isEmpty() && eighteenPrincesEntity.getFormationIds().isEmpty()) {

			player.sendError(protocol.getType(), Status.error.EIGHTEENPRINCES_MERCENARY_NOTSET_VALUE);
			return;
		}

		if (!eighteenPrincesEntity.isFormationHaveAlive() && !eighteenPrincesEntity.isHelpHaveAlive()) {

			player.sendError(protocol.getType(), Status.error.EIGHTEENPRINCES_MERCENARY_CANNOTUSE);
			return;
		}

		EighteenPrincesChallengeRet.Builder builder = EighteenPrincesChallengeRet.newBuilder();

		BattleInfo.Builder battleBuilder = calcEighteenPrincesBattle(arenaSnapShotEntity, eighteenPrincesEntity);
		// 战斗出错
		if (battleBuilder == null) {
			player.sendError(protocol.getType(), Status.error.PARAMS_INVALID);
			return;
		}

		QuestEventBus.fireQuestEventOneTime(QuestEventType.EIGHTEENPRINCES_FIRST,
				GuaJiXID.valueOf(GsConst.ObjType.PLAYER, player.getId()));

		// 每日任务触发
		Msg msg = Msg.valueOf(GsConst.MsgType.DailyQuestMsg.EIGHTEENPRINCESCHANGE,
				GuaJiXID.valueOf(GsConst.ObjType.PLAYER, player.getId()));
		GsApp.getInstance().postMsg(msg);

		if (battleBuilder.getFightResult() > 0) {
			EighteenPrincesCfg awardCfg = ConfigManager.getInstance().getConfigByKey(EighteenPrincesCfg.class,
					eighteenPrincesEntity.getLayerId());
			if (awardCfg == null) {
				return;
			}

			// 必掉
			AwardItems mustAward = AwardItems.valueOf(awardCfg.getMustRewards());
			if (mustAward != null) {
				for (Item item : mustAward.getAwardItems()) {
					if (item.id == 0) {
						ItemInfo randItem = AwardUtil.randomDrop(item.getType());
						if (randItem != null) {
							item.type = randItem.getType();
							item.id = randItem.getItemId();
						}
					}
				}
				// 挑战成功的奖励
				RewardInfo.Builder mustRewards = mustAward.rewardTakeAffectAndPush(player,
						Action.EIGHTEEN_PRINCE_CHALLENGE, 0);

				if (mustRewards != null) {
					builder.setMustReward(mustRewards);
				}
			}

			// 随机掉
			AwardItems randomAward = new AwardItems();
			ItemInfo randItem = GuaJiRand.randonWeightObject(awardCfg.getItemInfos(), awardCfg.getItemWeights())
					.clone();

			// 随机组中获取
			if (randItem != null) {
				if (randItem.getItemId() == 0) {
					randItem = AwardUtil.randomDrop(randItem.getType());
					if (randItem != null) {
						randomAward.addItem(randItem);
						RewardInfo.Builder randomRewards = randomAward.rewardTakeAffectAndPush(player,
								Action.EIGHTEEN_PRINCE_CHALLENGE, 0);
						builder.setRandReward(randomRewards);
						eighteenPrincesEntity.setDropItemId(randItem.getItemId());
						eighteenPrincesEntity.notifyUpdate();

					}
				}

			}
		} else {
			if (eighteenPrincesEntity.getDropItemId() != 0) {
				eighteenPrincesEntity.setDropItemId(0);
				eighteenPrincesEntity.notifyUpdate();

			}

		}
		// 协战武将是否为空
		if (!eighteenPrincesEntity.getHelpIds().isEmpty()) {

			PlayerSnapshotInfo.Builder friendSnap = SnapShotManager.getInstance()
					.getPlayerSnapShot(eighteenPrincesEntity.getHelpIds().get(0));
			if (friendSnap != null) {

				// helpItem.s
				EighteenPrincesEntity friendHelp = EighteenPrincesManager.getInstance()
						.queryEighteenPrincesaEntity(eighteenPrincesEntity.getHelpIds().get(0));

				if (friendHelp != null) {
					HelpMercenaryInfo helpMercenaryInfo = eighteenPrincesEntity.getHelpHistoryMap()
							.get(friendHelp.getPlayerId());
					if (helpMercenaryInfo != null && helpMercenaryInfo.isAlive()) {
						HelpItemInfo.Builder helpItem = HelpItemInfo.newBuilder();
						helpItem.setFightValue(friendSnap.getMainRoleInfo().getFight());
						helpItem.setName(friendSnap.getMainRoleInfo().getName());
						helpItem.setLevel(friendSnap.getMainRoleInfo().getLevel());
						helpItem.setPlayerId(friendSnap.getPlayerId());
						helpItem.setLeftCount(friendHelp.getEighteenPrinceCount());
						helpItem.setRoleItemId(friendHelp.getEighteenPrinceHelpRoleItemId());
						builder.setHelpItem(helpItem);
					}

				}
			}

		}
		// 通关时完成任务
		if (eighteenPrincesEntity.getLayerId() == SysBasicCfg.getInstance().getEighteenPrincesMaxLayerNum()) {
			QuestEventBus.fireQuestEventOneTime(QuestEventType.EIGHTEENPRINCES_PASS,
					GuaJiXID.valueOf(GsConst.ObjType.PLAYER, player.getId()));
		}

		builder.setCurLayer(eighteenPrincesEntity.getLayerId());
		builder.setChallengeResult(battleBuilder.getFightResult());
		builder.setBattleInfo(battleBuilder);
		player.sendProtocol(Protocol.valueOf(HP.code.EIGHTEENPRINCES_CHALLENGE_S_VALUE, builder));

	}

	/**
	 * 购买协战武将数据
	 */
	public void buyMercenaryUseTimes() {

	}

	/**
	 * 购买双倍奖励
	 */
	public void buyDoubleAward() {

	}

	/**
	 * @param arenaSnapEntity       pvp 快照
	 * @param eighteenPrincesEntity 十八路块
	 * @return
	 */
	private BattleInfo.Builder calcEighteenPrincesBattle(ArenaSnapShotEntity arenaSnapEntity,
			EighteenPrincesEntity eighteenPrincesEntity) {

		// 出战副将数据（协战武将放最前面）
		List<BattleRole> battleRoles = new LinkedList<>();
		// 应援副将数据（应援武将放最前面）
		List<BattleRole> assistanceBattleRoles = new LinkedList<>();

		// 优先加入协战武将数据
		for (Integer helpPlayerId : eighteenPrincesEntity.getHelpIds()) {
			// 没有协战武将数据或协战武将血量为0
			HelpMercenaryInfo helpMercenaryInfo = eighteenPrincesEntity.getHelpHistoryMap().get(helpPlayerId);
			if (helpMercenaryInfo == null || !helpMercenaryInfo.isAlive()) {
				continue;
			}
			EighteenPrincesEntity friendEighteenPrincesEntity = EighteenPrincesManager.getInstance()
					.queryEighteenPrincesaEntity(helpPlayerId);

			if (friendEighteenPrincesEntity == null) {
				continue;
			}

			RoleInfo.Builder roleInfo = SnapShotManager.getInstance().getActiviteRoleInfo(helpPlayerId,
					friendEighteenPrincesEntity.getEighteenPrinceHelpRoleItemId());
			if (roleInfo != null) {
				BattleRole helpRole = new BattleRole(helpPlayerId, roleInfo, true);
				int maxHp = helpRole.getAttrValue(Const.attr.HP);
				int maxMp = helpRole.getAttrValue(Const.attr.MP);
				helpRole.setHp((int) (maxHp * (helpMercenaryInfo.getHp() / 100.f)));
				helpRole.setMp((int) (maxMp * (helpMercenaryInfo.getMp() / 100.f)));
				battleRoles.add(helpRole);
			}

		}
		PlayerSnapshotInfo.Builder playerShot = SnapShotManager.getInstance().getPlayerSnapShot(player.getId());

		for (Integer itemId : eighteenPrincesEntity.getFormationIds()) {
			RoleInfo.Builder roleInfo = null;
			if (playerShot.getMainRoleInfo().getItemId() == itemId) {
				roleInfo = playerShot.getMainRoleInfoBuilder();
			} else {
				roleInfo = SnapShotManager.getInstance().getActiviteRoleInfo(player.getId(), itemId);
			}
			if (roleInfo != null) {
				HelpMercenaryInfo mercenaryInfo = eighteenPrincesEntity.getFormationHistoryMap().get(itemId);
				if (mercenaryInfo != null && mercenaryInfo.getHp() > 0) {
					BattleRole battleRole = new BattleRole(mercenaryInfo.getPlayerId(), roleInfo);
					int maxHp = battleRole.getAttrValue(Const.attr.HP);
					int maxMp = battleRole.getAttrValue(Const.attr.MP);

					battleRole.setHp((int) (maxHp * (mercenaryInfo.getHp() / 100.f)));
					battleRole.setMp((int) (maxMp * (mercenaryInfo.getMp() / 100.f)));
					// 最大战斗数量
					int maxFightCount = SysBasicCfg.getInstance().getMaxOpenCount();
					if (battleRoles.size() >= (maxFightCount + 1)) {
						assistanceBattleRoles.add(battleRole);
					} else {
						battleRoles.add(battleRole);
					}
				}
			}
		}

		// 玩家
		if (arenaSnapEntity.getType() == 1) {

			PlayerSnapshotInfo.Builder snapshot = arenaSnapEntity.getSnapshotInfo();
			if (snapshot == null) {
				return null;
			}

			List<Integer> roleIdList = new ArrayList<>(snapshot.getFightingRoleIdList());
			List<RoleInfo.Builder> mercenaryList = new ArrayList<>();
			if (roleIdList != null) {
				roleIdList.stream().distinct().forEach(itemId -> {
					for (RoleInfo.Builder roleInfo : snapshot.getMercenaryInfoBuilderList()) {
						if (roleInfo != null && roleInfo.getItemId() == itemId) {

							HelpMercenaryInfo mainInfo = eighteenPrincesEntity.getEnemyFormationMap()
									.get(roleInfo.getItemId());
							if (mainInfo != null) {
								if (mainInfo.isAlive()) {
									mercenaryList.add(roleInfo);
								}
							} else {
								mercenaryList.add(roleInfo);
							}
						}

					}
				});

			}

			// 出战副将数据
			List<BattleRole> enemyBattleRoles = new LinkedList<>();
			// 应援副将数据
			List<BattleRole> enemyAssistanceBattleRoles = new LinkedList<>();
			// 所有参战的副将编号
			List<Integer> allAttackMercenaryIds = new LinkedList<>();

			// 优先选取主将
			if (!eighteenPrincesEntity.getEnemyFormationMap().containsKey(snapshot.getMainRoleInfo().getItemId())) {
				enemyBattleRoles.add(new BattleRole(arenaSnapEntity.getPlayerId(), snapshot.getMainRoleInfoBuilder()));

			} else {
				BattleRole battleRole = new BattleRole(arenaSnapEntity.getPlayerId(),
						snapshot.getMainRoleInfoBuilder());
				HelpMercenaryInfo mainInfo = eighteenPrincesEntity.getEnemyFormationMap()
						.get(snapshot.getMainRoleInfo().getItemId());
				if (mainInfo.isAlive()) {
					battleRole.setHp(mainInfo.getHp());
					battleRole.setMp(mainInfo.getMp());
					enemyBattleRoles.add(battleRole);
				}
			}

			if (mercenaryList != null) {
				int maxFightCount = SysBasicCfg.getInstance().getMaxOpenCount();
				int mercenarySize = mercenaryList.size();
				if (mercenarySize >= maxFightCount) {

					List<RoleInfo.Builder> assistanceList = mercenaryList.subList(maxFightCount, mercenarySize);
					List<RoleInfo.Builder> battleList = mercenaryList.subList(0, maxFightCount);

					for (RoleInfo.Builder roleInfo : battleList) {
						BattleRole battleRole = new BattleRole(arenaSnapEntity.getPlayerId(), roleInfo);
						if (eighteenPrincesEntity.getEnemyFormationMap().containsKey(roleInfo.getItemId())) {
							HelpMercenaryInfo mercenaryInfo = eighteenPrincesEntity.getEnemyFormationMap()
									.get(roleInfo.getItemId());
							if (mercenaryInfo.isAlive()) {

								battleRole.setHp(mercenaryInfo.getHp());
								battleRole.setMp(mercenaryInfo.getMp());
								enemyBattleRoles.add(battleRole);
							}

						} else {
							enemyBattleRoles.add(battleRole);
						}

					}
					for (RoleInfo.Builder roleInfo : assistanceList) {
						BattleRole battleRole = new BattleRole(arenaSnapEntity.getPlayerId(), roleInfo);
						if (eighteenPrincesEntity.getEnemyFormationMap().containsKey(roleInfo.getItemId())) {
							HelpMercenaryInfo mercenaryInfo = eighteenPrincesEntity.getEnemyFormationMap()
									.get(roleInfo.getItemId());
							if (mercenaryInfo.isAlive()) {
								battleRole.setHp(mercenaryInfo.getHp());
								battleRole.setMp(mercenaryInfo.getMp());
								enemyAssistanceBattleRoles.add(battleRole);
							}

						} else {
							enemyAssistanceBattleRoles.add(battleRole);
						}

					}

				} else {

					for (RoleInfo.Builder roleInfo : mercenaryList) {
						BattleRole battleRole = new BattleRole(arenaSnapEntity.getPlayerId(), roleInfo);
						if (eighteenPrincesEntity.getEnemyFormationMap().containsKey(roleInfo.getItemId())) {
							HelpMercenaryInfo mercenaryInfo = eighteenPrincesEntity.getEnemyFormationMap()
									.get(roleInfo.getItemId());
							if (mercenaryInfo.isAlive()) {
								battleRole.setMp(mercenaryInfo.getMp());
								battleRole.setHp(mercenaryInfo.getHp());
								enemyBattleRoles.add(battleRole);
							}

						} else {
							enemyBattleRoles.add(battleRole);
						}

					}

				}
			}

			enemyBattleRoles.forEach(

					item -> {
						allAttackMercenaryIds.add(item.getRoleInfo().getItemId());
					});
			enemyAssistanceBattleRoles.forEach(

					item -> {
						allAttackMercenaryIds.add(item.getRoleInfo().getItemId());
					});

			// 开启战斗数据
			Battlefield battlefield = new Battlefield();

			BattleInfo.Builder battleBuilder = battlefield.eighteenprincesfighting(
					Battle.battleType.BATTLE_PVE_EIGHTEENPRINCES_VALUE, battleRoles, assistanceBattleRoles,
					enemyBattleRoles, enemyAssistanceBattleRoles, null);

			List<BattleRole> atttackList = new ArrayList<>();
			// 协战武将
			atttackList.addAll(battleRoles);
			atttackList.addAll(assistanceBattleRoles);
			for (BattleRole battleRole : atttackList) {
				int hp = (int) ((1.0f * battleRole.getHp()) / (1.0f * battleRole.getAttrValue(Const.attr.HP)) * 100);
				int mp = (int) ((1.0f * battleRole.getMp()) / (1.0f * battleRole.getAttrValue(Const.attr.MP)) * 100);
				if (battleRole.isHelper()) {
					HelpMercenaryInfo mercenaryInfo = eighteenPrincesEntity.getHelpHistoryMap()
							.get(battleRole.getPlayerId());
					if (mercenaryInfo != null) {
						mercenaryInfo.setHp(hp);
						mercenaryInfo.setMp(mp);
					} else {
						mercenaryInfo = new HelpMercenaryInfo(hp, mp, battleRole.getPlayerId());
						eighteenPrincesEntity.getHelpHistoryMap().put(battleRole.getPlayerId(), mercenaryInfo);
					}
				} else {
					HelpMercenaryInfo mercenaryInfo = eighteenPrincesEntity.getFormationHistoryMap()
							.get(battleRole.getRoleInfo().getItemId());
					if (mercenaryInfo != null) {
						mercenaryInfo.setHp(hp);
						mercenaryInfo.setMp(mp);
					} else {

						mercenaryInfo = new HelpMercenaryInfo(hp, mp, eighteenPrincesEntity.getPlayerId());
						eighteenPrincesEntity.getFormationHistoryMap().put(battleRole.getRoleInfo().getItemId(),
								mercenaryInfo);
						eighteenPrincesEntity.addFormationMercenaryInfo(battleRole.getRoleInfo().getItemId(),
								mercenaryInfo);
					}
				}

			}
			// 胜利
			if (battleBuilder.getFightResult() > 0) {

				// 之前副将数据清空
				eighteenPrincesEntity.getEnemyFormationMap().clear();
				eighteenPrincesEntity.setLayerId(eighteenPrincesEntity.getLayerId() + 1);
			} else {

				List<BattleRole> enemyList = new ArrayList<>();
				enemyList.addAll(enemyBattleRoles);
				enemyList.addAll(enemyAssistanceBattleRoles);

				for (BattleRole battleRole : enemyList) {
					int hp = battleRole.getHp();
					int mp = battleRole.getMp();
					HelpMercenaryInfo mercenaryInfo = eighteenPrincesEntity.getEnemyFormationMap()
							.get(battleRole.getRoleInfo().getItemId());

					allAttackMercenaryIds.remove(Integer.valueOf(battleRole.getRoleInfo().getItemId()));

					if (mercenaryInfo != null) {
						mercenaryInfo.setHp(hp);
						mercenaryInfo.setMp(mp);
					} else {
						mercenaryInfo = new HelpMercenaryInfo(hp, mp, arenaSnapEntity.getPlayerId());
						eighteenPrincesEntity.getEnemyFormationMap().put(battleRole.getRoleInfo().getItemId(),
								mercenaryInfo);
					}
				}
				// 记录
				for (Integer mercenaryId : allAttackMercenaryIds) {
					HelpMercenaryInfo mercenaryInfo = eighteenPrincesEntity.getEnemyFormationMap().get(mercenaryId);
					if (mercenaryInfo != null) {
						mercenaryInfo.setHp(0);
						mercenaryInfo.setMp(0);
					} else {
						mercenaryInfo = new HelpMercenaryInfo(0, 0, arenaSnapEntity.getPlayerId());
						eighteenPrincesEntity.getEnemyFormationMap().put(mercenaryId, mercenaryInfo);
					}

				}
			}
			eighteenPrincesEntity.setFirstChallenge(0);
			eighteenPrincesEntity.notifyUpdate();

			return battleBuilder;

		} else {

			RoleInfo.Builder monsterRole = null;
			monsterRole = BuilderUtil.genMonsterRoleInfoBuilder(arenaSnapEntity.getPlayerId(), true);

			// 开启战场
			if (monsterRole == null) {
				return null;
			}

			// 开启战场
			Battlefield battlefield = new Battlefield();
			// 挑战对象
			List<BattleRole> enemyBattleRoles = Arrays.asList(new BattleRole(monsterRole));

			Iterator<BattleRole> enemyIterator = enemyBattleRoles.iterator();
			while (enemyIterator.hasNext()) {
				BattleRole roleInfo = enemyIterator.next();
				if (roleInfo != null) {
					if (eighteenPrincesEntity.getEnemyFormationMap().containsKey(roleInfo.getRoleInfo().getItemId())) {
						HelpMercenaryInfo mercenaryInfo = eighteenPrincesEntity.getEnemyFormationMap()
								.get(roleInfo.getRoleInfo().getItemId());

						if (!mercenaryInfo.isAlive()) {
							enemyIterator.remove();
							continue;
						}
						roleInfo.setHp(mercenaryInfo.getHp());
						roleInfo.setMp(mercenaryInfo.getMp());
					}
				}

			}

			BattleInfo.Builder battleBuilder = battlefield.eighteenprincesfighting(
					Battle.battleType.BATTLE_PVE_EIGHTEENPRINCES_VALUE, battleRoles, assistanceBattleRoles,
					enemyBattleRoles, null, null);

			// 保存出战副将血量百分比
			List<BattleRole> atttackList = new ArrayList<>();
			atttackList.addAll(battleRoles);
			atttackList.addAll(assistanceBattleRoles);
			for (BattleRole battleRole : atttackList) {

				int hp = (int) ((1.0f * battleRole.getHp()) / (1.0f * battleRole.getAttrValue(Const.attr.HP)) * 100);
				int mp = (int) ((1.0f * battleRole.getMp()) / (1.0f * battleRole.getAttrValue(Const.attr.MP)) * 100);
				if (battleRole.isHelper()) {
					HelpMercenaryInfo mercenaryInfo = eighteenPrincesEntity.getHelpHistoryMap()
							.get(battleRole.getRoleInfo().getItemId());
					if (mercenaryInfo != null) {
						mercenaryInfo.setHp(hp);
						mercenaryInfo.setMp(mp);
					} else {
						mercenaryInfo = new HelpMercenaryInfo(hp, mp, battleRole.getPlayerId());
						eighteenPrincesEntity.getHelpHistoryMap().put(battleRole.getPlayerId(), mercenaryInfo);
					}
				} else {
					HelpMercenaryInfo mercenaryInfo = eighteenPrincesEntity.getFormationHistoryMap()
							.get(battleRole.getRoleInfo().getItemId());
					if (mercenaryInfo != null) {
						mercenaryInfo.setHp(hp);
						mercenaryInfo.setMp(mp);
					} else {

						mercenaryInfo = new HelpMercenaryInfo(hp, mp, battleRole.getRoleInfo().getItemId());
						eighteenPrincesEntity.getFormationHistoryMap().put(battleRole.getRoleInfo().getItemId(),
								mercenaryInfo);
					}
				}
			}
			// 胜利
			if (battleBuilder.getFightResult() > 0) {

				// 之前副将数据清空
				eighteenPrincesEntity.getEnemyFormationMap().clear();
				eighteenPrincesEntity.setLayerId(eighteenPrincesEntity.getLayerId() + 1);
			} else {

				for (BattleRole battleRole : enemyBattleRoles) {
					int hp = battleRole.getHp();
					int mp = battleRole.getMp();
					HelpMercenaryInfo mercenaryInfo = eighteenPrincesEntity.getEnemyFormationMap()
							.get(battleRole.getRoleInfo().getItemId());
					if (mercenaryInfo != null) {
						mercenaryInfo.setHp(hp);
						mercenaryInfo.setMp(mp);
					} else {
						mercenaryInfo = new HelpMercenaryInfo(hp, mp, arenaSnapEntity.getPlayerId());
						eighteenPrincesEntity.getEnemyFormationMap().put(battleRole.getRoleInfo().getItemId(),
								mercenaryInfo);

					}
				}
			}
			eighteenPrincesEntity.setFirstChallenge(0);
			eighteenPrincesEntity.notifyUpdate();
			return battleBuilder;
		}

	}

	public void noticeGetReward(Player player) {
		if (player == null)
			return;
		if (player.getPlayerData() == null) {
			return;
		}
		for (EighteenPrincesHelpHistoryEntity item : player.getPlayerData().getEighteenPrincesHelpHistoryEntities()) {
			if (!item.isReward()) {
				HPEighteenPrincesRewardNotice.Builder builder = HPEighteenPrincesRewardNotice.newBuilder();
				builder.setIsShow(1);
				player.sendProtocol(Protocol.valueOf(HP.code.EIGHTEENPRINCES_HELPREWARDNOTICE_S_VALUE, builder));
				break;
			}
		}

	}

	@Override
	public boolean onMessage(Msg msg) {

		if (msg.getMsg() == GsConst.MsgType.EIGHTEENPRINCESD_USE_FINISH) {
			if (!msg.getParams().isEmpty()) {
				HPEighteenPrincesChangeHelpRet.Builder builder = HPEighteenPrincesChangeHelpRet.newBuilder();
				builder.setFriendId(msg.getParam(0));
				builder.setResult(1);
				player.sendProtocol(Protocol.valueOf(HP.code.EIGHTEENPRINCES_HELP_CHANGE_S_VALUE, builder));
			}
			return true;
		} else if (msg.getMsg() == GsConst.MsgType.EIGHTEENPRINCESD_USE_MERCENARYERROR) {
			// 目标协战武将次数已经被用完
			player.sendError(HP.code.EIGHTEENPRINCES_HELP_CHANGE_C_VALUE,
					Status.error.EIGHTEENPRINCES_MERCENARY_CANNOTUSE_VALUE);

			HPEighteenPrincesChangeHelpRet.Builder builder = HPEighteenPrincesChangeHelpRet.newBuilder();
			builder.setFriendId(0);
			builder.setResult(0);
			player.sendProtocol(Protocol.valueOf(HP.code.EIGHTEENPRINCES_HELP_CHANGE_S_VALUE, builder));

			return true;
		} else if (msg.getMsg() == GsConst.MsgType.EIGHTEENPRINCESD_HELPHISTORY_SYNC) {
			// 同步好友协战武将协战记录
			if (!msg.getParams().isEmpty()) {
				EighteenPrincesHelpHistoryEntity helpHistoryEntity = msg.getParam(0);
				if (helpHistoryEntity == null) {
					return true;
				}
				boolean isFound = false;
				for (EighteenPrincesHelpHistoryEntity helpHistoryItem : eighteenPrincesHelpHistoryEntities) {
					if (helpHistoryItem.getId() == helpHistoryEntity.getId()) {
						isFound = true;
					}
				}
				if (!isFound) {
					this.eighteenPrincesHelpHistoryEntities.add(helpHistoryEntity);
					HPEighteenPrincesHelpHistoryRet.Builder historyBuilder = HPEighteenPrincesHelpHistoryRet
							.newBuilder();

					int todayCount = 0;

					Iterator<EighteenPrincesHelpHistoryEntity> item = eighteenPrincesHelpHistoryEntities.iterator();
					while (item.hasNext()) {
						EighteenPrincesHelpHistoryEntity helpHistory = item.next();
						if (helpHistory == null) {
							item.remove();
						} else {
							int spanDays = GuaJiTime.calcBetweenDays(helpHistory.getCreateTime(),
									GuaJiTime.getCalendar().getTime());
							if (spanDays >= 3) {
								helpHistory.setInvalid(true);
								item.remove();
							} else {
								PlayerSnapshotInfo.Builder snapshot = SnapShotManager.getInstance()
										.getPlayerSnapShot(helpHistory.getFriendPlayerId());
								if (snapshot == null) {
									continue;
								}
								if (GuaJiTime.isToday(helpHistory.getCreateTime())) {
									todayCount += 1;
								}
								HelpHistoryItemInfo.Builder history = HelpHistoryItemInfo.newBuilder();
								history.setHistoryId(helpHistory.getId());
								history.setHelpTime((int) (helpHistory.getCreateTime().getTime() / 1000));
								history.setName(snapshot.getMainRoleInfo().getName());
								history.setLevel(snapshot.getMainRoleInfo().getLevel());
								history.setHeadIcon(snapshot.getPlayerInfo().getHeadIcon());
								history.setHelpCount(helpHistory.getHelpCount());
								history.setProf(snapshot.getMainRoleInfo().getProf());
								if (helpHistory.isReward()) {
									history.setIsGet(1);
								} else {
									history.setIsGet(0);
								}

								historyBuilder.addHistoryInfos(history);
							}
						}

					}

					historyBuilder.setTodayCount(todayCount);
					player.sendProtocol(Protocol.valueOf(HP.code.EIGHTEENPRINCES_HELP_HISTORY_S_VALUE, historyBuilder));

					HPEighteenPrincesRewardNotice.Builder builder = HPEighteenPrincesRewardNotice.newBuilder();
					builder.setIsShow(1);
					player.sendProtocol(Protocol.valueOf(HP.code.EIGHTEENPRINCES_HELPREWARDNOTICE_S_VALUE, builder));
				}

			}
			return true;
		}
		return super.onMessage(msg);
	}

}
