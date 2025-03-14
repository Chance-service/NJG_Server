package com.guaji.game.module;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.lang.StringUtils;
import org.guaji.annotation.MessageHandlerAnno;
import org.guaji.app.App;
import org.guaji.config.ConfigManager;
import org.guaji.log.Log;
import org.guaji.msg.Msg;
import org.guaji.net.protocol.Protocol;
import org.guaji.os.GuaJiTime;
import org.guaji.os.MyException;
import org.guaji.util.services.ReportService;
import org.guaji.xid.GuaJiXID;

import com.guaji.game.GsApp;
import com.guaji.game.ServerData;
import com.guaji.game.attribute.Attribute;
import com.guaji.game.config.ActivityTimeCfg;
import com.guaji.game.config.FetterCfg;
import com.guaji.game.config.FunctionUnlockCfg;
import com.guaji.game.config.GrowthLVCfg;
import com.guaji.game.config.HeroAwakeCfg;
import com.guaji.game.config.HeroStarCfg;
import com.guaji.game.config.HeroUpLevelCfg;
import com.guaji.game.config.Hero_NGListCfg;
import com.guaji.game.config.PlatInitRewardConfig;
import com.guaji.game.config.RoleRelatedCfg;
import com.guaji.game.config.RoleSkinCfg;
import com.guaji.game.config.SecretAlbumCfg;
import com.guaji.game.config.SecretMsgCfg;
import com.guaji.game.config.SysBasicCfg;
import com.guaji.game.entity.AllianceBattleItem;
import com.guaji.game.entity.AllianceEntity;
import com.guaji.game.entity.ArchiveEntity;
import com.guaji.game.entity.PlayerAllianceEntity;
import com.guaji.game.entity.PlayerEntity;
import com.guaji.game.entity.RoleEntity;
import com.guaji.game.entity.RoleSkinEntity;
import com.guaji.game.entity.SecretMsgEntity;
import com.guaji.game.entity.SignEntity;
import com.guaji.game.entity.StateEntity;
import com.guaji.game.item.AwardItems;
import com.guaji.game.item.ConsumeItems;
import com.guaji.game.item.ItemInfo;
import com.guaji.game.log.BehaviorLogger;
import com.guaji.game.log.BehaviorLogger.Action;
import com.guaji.game.log.BehaviorLogger.Params;
import com.guaji.game.log.BehaviorLogger.Source;
import com.guaji.game.manager.AllianceBattleManager;
import com.guaji.game.manager.AllianceManager;
import com.guaji.game.manager.ChatManager;
import com.guaji.game.manager.MailManager;
import com.guaji.game.manager.RankGiftManager;
import com.guaji.game.manager.SnapShotManager;
import com.guaji.game.module.quest.QuestEventBus;
import com.guaji.game.module.sevendaylogin.SevenDayQuestEventBus;
import com.guaji.game.player.Player;
import com.guaji.game.player.PlayerData;
import com.guaji.game.player.PlayerModule;
import com.guaji.game.protocol.Const;
import com.guaji.game.protocol.Const.QuestEventType;
import com.guaji.game.protocol.Const.SevenDayEventType;
import com.guaji.game.protocol.Const.changeType;
import com.guaji.game.protocol.HP;
import com.guaji.game.protocol.HeadIcon.PlayerHeadIconChangeRep;
import com.guaji.game.protocol.HeadIcon.PlayerHeadIconChangeReq;
import com.guaji.game.protocol.HeadIcon.PlayerHeadIconInfoRet;
import com.guaji.game.protocol.Mail;
import com.guaji.game.protocol.Mail.MailType;
import com.guaji.game.protocol.Player.HPChangeRoleName;
import com.guaji.game.protocol.Player.HPChangeRoleNameRet;
import com.guaji.game.protocol.Player.HPChangeSignature;
import com.guaji.game.protocol.Player.HPChangeSignatureRet;
import com.guaji.game.protocol.Player.HPEquipSyncFinish;
import com.guaji.game.protocol.Player.HPRoleAwake;
import com.guaji.game.protocol.Player.HPRoleCreate;
import com.guaji.game.protocol.Player.HPRoleCreateRet;
import com.guaji.game.protocol.Player.HPRoleUPLevel;
import com.guaji.game.protocol.Player.HPRoleUpStar;
import com.guaji.game.protocol.RoleOpr.HPChangeMercenarySkinReq;
import com.guaji.game.protocol.RoleOpr.HPChangeMercenarySkinRes;
import com.guaji.game.protocol.RoleOpr.HPHeroLevelResetReq;
import com.guaji.game.protocol.RoleOpr.HPHeroLevelResetRes;
import com.guaji.game.protocol.RoleOpr.HPNewGuideStep;
import com.guaji.game.protocol.RoleOpr.HPRoleAwakeRes;
import com.guaji.game.protocol.RoleOpr.HPRoleEmploy;
import com.guaji.game.protocol.RoleOpr.HPRoleInfo;
import com.guaji.game.protocol.RoleOpr.HPRoleInfoRes;
import com.guaji.game.protocol.RoleOpr.HPRoleUPLevelRes;
import com.guaji.game.protocol.RoleOpr.HPRoleUPStarRes;
import com.guaji.game.protocol.SecretMsg.secretMsgRequest;
import com.guaji.game.protocol.SecretMsg.secretMsgResponse;
import com.guaji.game.protocol.Snapshot.PlayerSnapshotInfo;
import com.guaji.game.protocol.Status;
import com.guaji.game.util.ActivityUtil;
import com.guaji.game.util.AdjustEventUtil;
import com.guaji.game.util.BuilderUtil;
import com.guaji.game.util.GJLocal;
import com.guaji.game.util.GameUtil;
import com.guaji.game.util.GsConst;
import com.guaji.game.util.GsonUtil;
import com.guaji.game.util.PlayerUtil;
import com.guaji.game.util.ProtoUtil;
import com.guaji.game.util.TapDBUtil;
import com.guaji.game.util.TapDBUtil.TapDBSource;

/**
 * 玩家角色模块
 */
public class PlayerRoleModule extends PlayerModule {
    // 模块Tick周期
    private int tickIndex;
	/**
	 * 构造函数
	 *
	 * @param player
	 */
	public PlayerRoleModule(Player player) {
		super(player);

		listenProto(HP.code.ROLE_CREATE_C);
		//listenProto(HP.code.ROLE_FIGHT_C);
		//listenProto(HP.code.ROLE_BAPTIZE_C);
		//listenProto(HP.code.ROLE_REPLACE_C);
		// 高级育成
		//listenProto(HP.code.ROLE_SENIORBAPTIZE_C);

		listenProto(HP.code.ROLE_CHANGE_SIGNATURE_C);
		// 英雄升等
		listenProto(HP.code.ROLE_UP_LEVEL_C);
		listenProto(HP.code.ROLE_LEVEL_MAX_C);
		listenProto(HP.code.ROLE_CHANGE_NAME_C);
		listenProto(HP.code.NEW_GUIDE_STEP_C);
		//listenProto(HP.code.EVALUATE_REWARDS_C);
		//listenProto(HP.code.ROLE_GAME_COMMENT_C);
		listenProto(HP.code.ROLE_PANEL_INFOS_C);
		listenProto(HP.code.ROLE_EMPLOY_C);
		listenProto(HP.code.ROLE_UPGRADE_STAR_C);
		//listenProto(HP.code.ROLE_UPGRADE_STAGE2_C);
		//listenProto(HP.code.ROLE_BAPTIZE_MAX_C);
		listenProto(HP.code.ROLE_CHANGE_SKIN_C);
		//listenProto(HP.code.ROLE_AVATAR_INFO_C);
		//listenProto(HP.code.ROLE_CHECK_AVATAR_C);
		//listenProto(HP.code.ROLE_CHANGE_AVATAR_C);

		// 购买角色头像
		listenProto(HP.code.MAINROLE_ICON_CHANGE_C);
		listenProto(HP.code.MAINROLE_ICON_INFO_C);
		listenProto(HP.code.MAINROLE_ICON_BUY_C);
		// 重置角色等級
		listenProto(HP.code.ROLE_LEVEL_RESET_C);
		
		//秘密信條
		listenProto(HP.code.SECRET_MESSAGE_ACTION_C);
		
		//英雄覺醒
		listenProto(HP.code.ROLE_AWAKE_C);
		
		//listenProto(HP.code.SEND_MARKETPLACE_SYNC_C);

		// 监听推送
//		listenMsg(GsConst.MsgType.TEST_ROLE_SURMOUNT);
//		listenMsg(GsConst.MsgType.NOTIFY_ROLE_PANEL);
				
	}

	/**
	 * 更新
	 *
	 * @return
	 */
	@Override
	public boolean onTick() {
		if (++tickIndex % 100 == 0) {
			//sendSecretMsg();
//			RecoverScretPower();
			tickIndex = 0;
		}
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
	 * 响应新任务;
	 *
	 * @param msg
	 */
	@MessageHandlerAnno(code = GsConst.MsgType.NEW_QUEST_EVENT)
	private void onNewQuestEvent(Msg msg) {
		QuestEventType eventType = msg.getParam(0);
		List<RoleEntity> roleEntities = player.getPlayerData().getRoleEntities();
		// 佣兵升星
		if (eventType == QuestEventType.HERO_LEVEL) {
			for (RoleEntity roleEntity : roleEntities) {
				if (roleEntity.isHero()) {
					Log.debugPrintln("sync role: " + roleEntity.getId());
					player.getPlayerData().syncRoleInfo(roleEntity.getId());
					// 推送佣兵升星任务事件
					QuestEventBus.fireQuestEvent(QuestEventType.HERO_LEVEL, roleEntity.getStarLevel(), player.getXid());
				}
			}
		}
	}

	/**
	 * 协议响应
	 *
	 * @param protocol
	 * @return
	 */
	@Override
	public boolean onProtocol(Protocol protocol) {
		if (protocol.checkType(HP.code.ROLE_CREATE_C)) {
			onMainRoleCreate(protocol.getType(), protocol.parseProtocol(HPRoleCreate.getDefaultInstance()));
			return true;
		} else if (protocol.checkType(HP.code.ROLE_FIGHT_C)) {
			// 佣兵出战
			//onRoleFight(protocol.getType(), protocol.parseProtocol(HPRoleFight.getDefaultInstance()));
			return true;
		} else if (protocol.checkType(HP.code.ROLE_BAPTIZE_C)) {
			// 佣兵培养(育成)
			//onRoleBaptize(protocol.getType(), protocol.parseProtocol(HPRoleBaptize.getDefaultInstance()));
			return true;
		} else if (protocol.checkType(HP.code.ROLE_SENIORBAPTIZE_C)) {
			// 佣兵培养(育成)
			//onRoleSeniorBaptize(protocol.getType(), protocol.parseProtocol(HPRoleBaptize.getDefaultInstance()));
			return true;
		} else if (protocol.checkType(HP.code.ROLE_REPLACE_C)) {
			//onRoleReplace(protocol.getType(), protocol.parseProtocol(HPRoleAttrReplace.getDefaultInstance()));
			return true;
		} else if (protocol.checkType(HP.code.ROLE_CHANGE_SIGNATURE_C)) {
			onRoleChangeSignature(protocol);
			return true;
		} else if (protocol.checkType(HP.code.ROLE_UP_LEVEL_C)) {
			// 英雄升級（训练）
			onRoleUpgradeLevel(protocol);
			return true;
		} else if (protocol.checkType(HP.code.ROLE_LEVEL_MAX_C)) {
			// 英雄升級一鍵（训练)
			onRoleUpLevelByOneKey(protocol);
			return true;
		} else if (protocol.checkType(HP.code.ROLE_CHANGE_NAME_C)) {
			onRoleChangeName(protocol);
			return true;
		} else if (protocol.checkType(HP.code.NEW_GUIDE_STEP_C)) {
			// 记录新手引导步骤
			onNewGuideStep(protocol.parseProtocol(HPNewGuideStep.getDefaultInstance()));
		} else if (protocol.checkType(HP.code.EVALUATE_REWARDS_C)) {
			// 评价奖励
			//onEvaluateReward(protocol.getType());
		} else if (protocol.checkType(HP.code.ROLE_GAME_COMMENT_C)) {
			if (GJLocal.isLocal(GJLocal.R2)) {
				// R2游戏评价
				//onRoleComment(protocol);
			}
		} else if (protocol.checkType(HP.code.ROLE_EMPLOY_C)) {
			// 英雄隨從(雇佣)
			onRoleEmploy(protocol);
		} else if (protocol.checkType(HP.code.ROLE_UPGRADE_STAR_C)) {
			// 英雄突破星等
			onRoleUpgradeStar(protocol);
		} else if (protocol.checkType(HP.code.ROLE_UPGRADE_STAGE2_C)) {
			// 佣兵升阶
			//onRoleUpgradeStage2(protocol);
		} else if (protocol.checkType(HP.code.ROLE_PANEL_INFOS_C)) {
			// 佣兵面板列表
			onRolePanelInfo();
		} else if (protocol.checkType(HP.code.ROLE_BAPTIZE_MAX_C)) {
			// 获取培养最大属性
			//onRoleBatizeMaxAttr(protocol);
		} else if (protocol.checkType(HP.code.ROLE_CHANGE_SKIN_C)) {
			// 切换佣兵皮肤
			onChangeMercenarySkin(protocol);
		} else if (protocol.checkType(HP.code.ROLE_AVATAR_INFO_C)) {
			// 主角当前拥有的Avatar
			//onMainRoleAvatarInfo(protocol);
		} else if (protocol.checkType(HP.code.ROLE_CHECK_AVATAR_C)) {
			// 查看某个avatar
			//this.onCheckMainRoleAvatar(protocol);
		} else if (protocol.checkType(HP.code.ROLE_CHANGE_AVATAR_C)) {
			// 切换主角Avatar
			//onChangeMainRoleAvatar(protocol);
		} else if (protocol.checkType(HP.code.MAINROLE_ICON_INFO_C)) {
			// 用户当前头像信息
			//onMainRoleHeadIconInfo(protocol);

		} else if (protocol.checkType(HP.code.MAINROLE_ICON_CHANGE_C)) {
			// 用户头像修改
			onMainRoleHeadIconChange(protocol);

		} else if (protocol.checkType(HP.code.MAINROLE_ICON_BUY_C)) {
			// 用户头像购买
			//onMainRoleHeadIconBuy(protocol);
		}else if (protocol.checkType(HP.code.SEND_MARKETPLACE_SYNC_C)) {
			//client 請求同步MarketPlaceHero資料
			//onSyncMarketHeroInfo();
		}else if (protocol.checkType(HP.code.ROLE_LEVEL_RESET_C)) {
			onHeroResetLevel(protocol);
		}else if (protocol.checkType(HP.code.SECRET_MESSAGE_ACTION_C)){
			onHandleSecretMsg(protocol);
		}else if (protocol.checkType(HP.code.ROLE_AWAKE_C)) {
			onHandleHeroAwake(protocol);
		}

		return super.onProtocol(protocol);
	}

	/**
	 * 请求佣兵培养最大属性
	 *
	 * @param protocol
	 * @return
	 */
//	private boolean onRoleBatizeMaxAttr(Protocol protocol) {
//		HPRoleMaxAttribute roleMaxAttribute = protocol.parseProtocol(HPRoleMaxAttribute.getDefaultInstance());
//		int roleId = roleMaxAttribute.getRoleId();
//
//		if (roleId <= 0) {
//			sendError(HP.code.ROLE_EMPLOY_C_VALUE, Status.error.PARAMS_INVALID);
//			return false;
//		}
//
//		RoleEntity roleEntity = player.getPlayerData().getMercenaryById(roleId);
//		if (roleEntity == null) {
//			sendError(HP.code.ROLE_EMPLOY_C_VALUE, Status.error.PARAMS_INVALID);
//			return false;
//		}
//
//		RoleBaptizeCfg roleBaptizeCfg = ConfigManager.getInstance().getConfigByKey(RoleBaptizeCfg.class,
//				roleEntity.getLevel());
//		if (roleBaptizeCfg == null) {
//			sendError(HP.code.ROLE_BAPTIZE_MAX_C_VALUE, Status.error.CONFIG_NOT_FOUND);
//			return false;
//		}
//
//		RoleCfg cfg = ConfigManager.getInstance().getConfigByKey(RoleCfg.class, roleEntity.getItemId());
//		if (cfg == null) {
//			sendError(HP.code.ROLE_EMPLOY_C_VALUE, Status.error.PARAMS_INVALID);
//			return false;
//		}
//
//		HPRoleMaxAttributeRet.Builder roleBaptizeBuilder = HPRoleMaxAttributeRet.newBuilder();
//		for (Const.attr attr : GsConst.Role.BAPTIZE_ATTRS) {
//			int fullValue = (int) (roleBaptizeCfg.getFullValue(cfg.getProfession(), attr.getNumber())
//					* cfg.getTrainRatio());
//			roleBaptizeBuilder.addValues(fullValue);
//		}
//
//		player.sendProtocol(Protocol.valueOf(HP.code.ROLE_BAPTIZE_MAX_S, roleBaptizeBuilder));
//		return true;
//	}

	/**
	 * 佣兵激活(雇佣)
	 *
	 * @param protocol
	 * @return
	 * @throws MyException 
	 */
	private boolean onRoleEmploy(Protocol protocol) {
		HPRoleEmploy roleEmploy = protocol.parseProtocol(HPRoleEmploy.getDefaultInstance());
		int roleId = roleEmploy.getRoleId();

		// 佣兵id错误
		if (roleId < 0) {
			sendError(HP.code.ROLE_EMPLOY_C_VALUE, Status.error.PARAMS_INVALID);
			return false;
		}

		RoleEntity roleEntity = player.getPlayerData().getMercenaryById(roleId);
		if (roleEntity == null) {
			sendError(HP.code.ROLE_EMPLOY_C_VALUE, Status.error.PARAMS_INVALID_VALUE);
			return false;
		}
		
		Hero_NGListCfg roleCfg = ConfigManager.getInstance().getConfigByKey(Hero_NGListCfg.class, roleEntity.getItemId());
		
		if (roleCfg == null) {
			sendError(HP.code.ROLE_EMPLOY_C_VALUE, Status.error.PARAMS_INVALID);
			return false;
		}
		
		ItemInfo info = ItemInfo.valueOf(roleCfg.getCost());
		RoleRelatedCfg cfgR = ConfigManager.getInstance().getConfigByKey(RoleRelatedCfg.class, roleEntity.getItemId());
		if (cfgR != null) {
			if (roleEntity.getSoulCount() < info.getQuantity()
					&& cfgR.getCostType() == GsConst.RoleSoulExchangeType.ROLE_SOUL) {
				sendError(HP.code.ROLE_EMPLOY_C_VALUE, Status.error.ITEM_NOT_ENOUGH_VALUE);
				return false;
			}
		}


		roleEmploy(roleEntity);

		HPRoleEmploy.Builder builder = HPRoleEmploy.newBuilder();
		builder.setRoleId(roleId);
		player.sendProtocol(Protocol.valueOf(HP.code.ROLE_EMPLOY_S_VALUE, builder));
		return true;
	}

	/**
	 * 满足条件自动激活;
	 *
	 * @param msg
	 */
	@MessageHandlerAnno(code = GsConst.MsgType.AUTO_EMPLOY_ROLE)
	private void onAutoEmployRole(Msg msg) {
		int roleId = msg.getParam(0);
		RoleEntity roleEntity = player.getPlayerData().getMercenaryByItemId(roleId);
		if (roleEntity.isArmy()) {
			roleEmploy(roleEntity);
			player.getPlayerData().syncMercenarySoulInfo(roleEntity.getItemId());
		}
	}

	/**
	 * 请求面板信息
	 *
	 * @return
	 */
	private boolean onRolePanelInfo() {
 		List<RoleEntity> roleList = player.getPlayerData().getRoleEntities();
		if (roleList == null || roleList.size() == 0) {
			sendError(HP.code.ROLE_EMPLOY_C_VALUE, Status.error.PARAMS_INVALID_VALUE);
			return false;
		}

		HPRoleInfoRes.Builder builder = HPRoleInfoRes.newBuilder();
		for (RoleEntity role : roleList) {
			if (role.isArmy()) {
				RoleRelatedCfg cfgR = ConfigManager.getInstance().getConfigByKey(RoleRelatedCfg.class,
						role.getItemId());

				if (cfgR != null && cfgR.getCostType() == GsConst.RoleSoulExchangeType.ROLE_SOUL
						&& role.getRoleState() != Const.RoleActiviteState.IS_ACTIVITE_VALUE) {
					if (role.getSoulCount() >= cfgR.getLimitCount()) {
						role.setRoleState(Const.RoleActiviteState.CAN_ACTIVITE_VALUE);
						BehaviorLogger.log4Platform(player, Action.ACTIVITE_MERCENARY_CAN_ACTIVITE, Params.valueOf("itemId", role.getItemId()));
					}
				}
				HPRoleInfo.Builder roleInfoBuilder = HPRoleInfo.newBuilder();
				roleInfoBuilder.setRoleId(role.getId());
				roleInfoBuilder.setType(role.getType());
				roleInfoBuilder.setSoulCount(role.getSoulCount());
				roleInfoBuilder.setRoleStage(Const.RoleActiviteState.valueOf(role.getRoleState()));
				roleInfoBuilder.setItemId(role.getItemId());
				roleInfoBuilder.setStatus(role.getStatus());
				roleInfoBuilder.setFight(PlayerUtil.calcFightValue(role));
				roleInfoBuilder.setSkinId(role.getSkinId());

				Hero_NGListCfg cfg = ConfigManager.getInstance().getConfigByKey(Hero_NGListCfg.class, role.getItemId());
				if (cfg == null) {
					continue;
				}
				if (cfg.getCost() == null || cfg.getCost().isEmpty()) {
					roleInfoBuilder.setCostSoulCount(0);
				} else {
					ItemInfo info = ItemInfo.valueOf(cfg.getCost());
					roleInfoBuilder.setCostSoulCount((int)info.getQuantity());
				}
				builder.addRoleInfos(roleInfoBuilder);
			}
		}
		builder.setRoleCount(builder.getRoleInfosCount());

		Protocol protocol = Protocol.valueOf(HP.code.ROLE_PANEL_INFOS_S_VALUE, builder);
		sendProtocol(ProtoUtil.compressProtocol(protocol));
		return true;

	}

	/**
	 * 激活佣兵
	 *
	 * @param roleEntity
	 * @return
	 */
	private boolean roleEmploy(RoleEntity roleEntity) {
		// 已激活不需要激活
		if (roleEntity.getRoleState() == Const.RoleActiviteState.IS_ACTIVITE_VALUE) {
			sendError(HP.code.ROLE_EMPLOY_C_VALUE, Status.error.PARAMS_INVALID_VALUE);
			return false;
		}
		// 是皮肤，并且基础佣兵还没有激活
//		RoleCfg cfg = ConfigManager.getInstance().getConfigByKey(RoleCfg.class, roleEntity.getItemId());
//		if (cfg.getBaseId() > 0) {
//			RoleEntity baseRoleEntity = player.getPlayerData().getMercenaryByItemId(cfg.getBaseId());
//			if (baseRoleEntity == null || baseRoleEntity.getRoleState() != Const.RoleActiviteState.IS_ACTIVITE_VALUE) {
//				return false;
//			}
//		}
		roleEntity.setRoleState(Const.RoleActiviteState.IS_ACTIVITE_VALUE);
		
		player.getPlayerData().createSecretMsg(roleEntity.getItemId());
		// 皮肤初始为隐藏状态
//		if (cfg.getBaseId() != 0) {
//			roleEntity.setHide(true);
//		}
		// 账号转生->佣兵转生
		roleEntity.setRebirthStage(player.getRebirthStage());

		// 检查是否新技能
		//activiceSkill(roleEntity);
		// 检查是否激活光环
		//activiceRing(roleEntity);

		roleEntity.notifyUpdate(false);
		// 佣兵刷新属性并通知
		PlayerUtil.refreshOnlineAttribute(player.getPlayerData(), roleEntity);
		PlayerUtil.refreshOnlineAttribute(player.getPlayerData(), player.getPlayerData().getMainRole());

		player.getPlayerData().syncRoleInfo(roleEntity.getId());

		player.getPlayerData().syncRoleInfo(player.getPlayerData().getMainRole().getId());

		// RoleCfg cfg =
		// ConfigManager.getInstance().getConfigByKey(RoleCfg.class,
		// roleEntity.getItemId());
		QuestEventBus.fireQuestEvent(QuestEventType.HERO_COUNT, player.getPlayerData().getActiviceHero().size(),
				GuaJiXID.valueOf(GsConst.ObjType.PLAYER, this.player.getId()));

		if (roleEntity.getRoleCfg().getStar() == 1) { // SR
			QuestEventBus.fireQuestEventOneTime(QuestEventType.PURPLE_HERO_COUNT,
					GuaJiXID.valueOf(GsConst.ObjType.PLAYER, this.player.getId()));
		}

		if (roleEntity.getRoleCfg().getStar() == 6) { //SSR
			QuestEventBus.fireQuestEventOneTime(QuestEventType.GOLD_HERO_COUNT,
					GuaJiXID.valueOf(GsConst.ObjType.PLAYER, this.player.getId()));
		}
		
		if (roleEntity.getAttr() == GsConst.HeroAttrType.Light || roleEntity.getAttr() == GsConst.HeroAttrType.Dark) {
			QuestEventBus.fireQuestEventOneTime(QuestEventType.GET_LIGHT_DARK_HERO,
					GuaJiXID.valueOf(GsConst.ObjType.PLAYER, this.player.getId()));
		}

		// 7日之诗 副将人数
		SevenDayQuestEventBus.fireQuestEvent(SevenDayEventType.DEPUTYNUM,
				player.getPlayerData().getActiviceHero().size(), this.player.getXid());

		BehaviorLogger.log4Service(player, Source.USER_OPERATION, Action.ROLE_EMPOLY,
				Params.valueOf("roleId", roleEntity.getId()), Params.valueOf("roleItemId", roleEntity.getItemId()),
				Params.valueOf("employ", roleEntity.getId()), Params.valueOf("soulCount", roleEntity.getSoulCount()));

//		BehaviorLogger.log4Platform(player, Action.ROLE_EMPOLY, Params.valueOf("roleId", roleEntity.getId()),
//				Params.valueOf("roleItemId", roleEntity.getItemId()), Params.valueOf("employ", roleEntity.getId()),
//				Params.valueOf("soulCount", roleEntity.getSoulCount()));
		return true;
	}

	// 发送评价奖励
//	private void onEvaluateReward(int hpCode) {
//
//		if (player.getLevel() < 20) {
//			sendError(hpCode, Status.error.LEVEL_NOT_LIMIT_VALUE);
//			return;
//		}
//
//		StateEntity entity = player.getPlayerData().getStateEntity();
//		if (entity.getEvaluateRewardsState() == 1) {
//			sendError(hpCode, Status.error.ACTIVITY_AWARDS_HAS_GOT);
//			return;
//		}
//		// 发奖
//		AwardItems award = new AwardItems();
//		award.addGold(SysBasicCfg.getInstance().getEvaluateRewardGold());
//		award.rewardTakeAffectAndPush(player, Action.EVALUATE_REWARDS, 1);
//		entity.setEvaluateRewardsState(1);
//		entity.notifyUpdate(true);
//	}

	// 记录新手引导步骤
	private void onNewGuideStep(HPNewGuideStep protocol) {
		int stepId = protocol.getStepId();
		StateEntity state = player.getPlayerData().getStateEntity();
		if (state != null) {
			state.setNewGuideState(stepId);
			state.notifyUpdate(true);
		}
	}
	/**
	 * 重置英雄等級
	 * @param protocol
	 */
	public void onHeroResetLevel(Protocol protocol) {
		HPHeroLevelResetReq req = protocol.parseProtocol(HPHeroLevelResetReq.getDefaultInstance());
		int HeroId = req.getId();
		// 检查参数
		if (HeroId <= 0) {
			sendError(protocol.getType(), Status.error.PARAMS_INVALID_VALUE);
			return;
		}
		RoleEntity heroEntity = player.getPlayerData().getMercenaryById(HeroId);
		
		if (heroEntity == null ) {
			// 参数错误
			sendError(protocol.getType(), Status.error.PARAMS_INVALID_VALUE);
			return;
		}
		
		if (!heroEntity.isHero()) {
			// 此功能英雄獨佔
			sendError(protocol.getType(), Status.error.PARAMS_INVALID_VALUE);
			return;
		}
		
		if (heroEntity.getRoleState() == Const.RoleActiviteState.NOT_ACTIVITE_VALUE) {
			// 佣兵未激活
			sendError(protocol.getType(), Status.error.MERCENARY_NOT_FOUND_VALUE);
			return;
		}
		
		if (heroEntity.isEquipment()) {
			// 身上有裝備
			sendError(protocol.getType(), Status.error.EQUIP_HAS_DRESSED);
			return;
		}
		
		if (heroEntity.getLevel() <= 1) {
			///等級太低
			sendError(protocol.getType(), Status.error.PARAMS_INVALID_VALUE);
			return;
		}
		
		if (heroEntity.getLevel() > 100) { // 大於100級重置收費
			ConsumeItems consumeItems = ConsumeItems.valueOf();
			List<ItemInfo> itemList = ItemInfo.valueListOf(SysBasicCfg.getInstance().getResetHero());
			boolean isAdd = consumeItems.addConsumeInfo(player.getPlayerData(),itemList);
			if (isAdd && consumeItems.checkConsume(player)) {
				if(!consumeItems.consumeTakeAffect(player, Action.Role_ReSet_Level)) {
					player.sendError(protocol.getType(),Status.error.ITEM_NOT_ENOUGH_VALUE); 
					return; 
				} 
			} else {
				player.sendError(protocol.getType(),Status.error.ITEM_NOT_ENOUGH_VALUE); 
				return; 
			}
		}
			
		AwardItems awardItems = new AwardItems();
		for (int level = 1 ; level <= heroEntity.getLevel()-1; level++ ) {
			HeroUpLevelCfg upLevelcfg = HeroUpLevelCfg.getRoleUpLevelCfg(level);
			awardItems.addItemInfos(upLevelcfg.getItemList());
		}
		
		int oldLevel = heroEntity.getLevel();
		heroEntity.setLevel(1);
		
		awardItems.rewardTakeAffectAndPush(player, Action.Role_ReSet_Level, 0,TapDBSource.Hero_Reset_Lv,
				Params.valueOf("HeroId", HeroId),
				Params.valueOf("oldLevel", oldLevel)
				);
		
		// 刷新属性
		Attribute oldAttr = heroEntity.getAttribute().clone();
		PlayerUtil.refreshOnlineAttribute(player.getPlayerData(), heroEntity);
		player.getPlayerData().syncRoleInfo(heroEntity.getId());
		//player.getPlayerData().syncRoleInfo(player.getPlayerData().getMainRole().getId());
		Attribute newAttr = heroEntity.getAttribute();
		PlayerUtil.pushAttrChange(player, oldAttr, newAttr);
		heroEntity.notifyUpdate();

		BehaviorLogger.log4Service(player, Source.USER_OPERATION, Action.Role_ReSet_Level,
				Params.valueOf("roleId", heroEntity.getId()), Params.valueOf("roleItemId", heroEntity.getItemId()),
				Params.valueOf("oldLevel", oldLevel),
				Params.valueOf("newLevel", heroEntity.getLevel()));
		
		HPHeroLevelResetRes.Builder builder = HPHeroLevelResetRes.newBuilder();
		builder.setId(heroEntity.getId());
		player.sendProtocol(Protocol.valueOf(HP.code.ROLE_LEVEL_RESET_S, builder));
		
	}
	/**
	 * 英雄升等
	 *
	 * @param protocol
	 */
	private boolean onRoleUpgradeLevel(Protocol protocol) {
		HPRoleUPLevel upLevel = protocol.parseProtocol(HPRoleUPLevel.getDefaultInstance());
		int roleId = upLevel.getRoleId();
				
		// 获取佣兵
		RoleEntity roleEntity = player.getPlayerData().getMercenaryById(roleId);
		if (roleEntity == null || !roleEntity.isHero()) {
			// 此功能英雄獨佔
			sendError(HP.code.ROLE_UP_LEVEL_C_VALUE, Status.error.ROLE_NOT_FOUND_VALUE);
			return false;
		}
				
		// 佣兵未激活
		if (roleEntity.getRoleState() == Const.RoleActiviteState.NOT_ACTIVITE_VALUE) {
			sendError(HP.code.ROLE_UP_LEVEL_C_VALUE, Status.error.PARAMS_INVALID);
			return false;
		}
		
		// 當前系統等級限制
		int Level = roleEntity.getLevel();
				
		if (Level >= HeroUpLevelCfg.getMaxLevel() || Level >= roleEntity.getLimitLevel()) {
			// 已最大等级
			sendError(HP.code.ROLE_UP_LEVEL_C_VALUE, Status.error.ROLE_MAX_STAR);
			return false;
		}
		
		HeroUpLevelCfg upLevelcfg = HeroUpLevelCfg.getRoleUpLevelCfg(Level);
		
		if (upLevelcfg == null) {
			sendError(HP.code.ROLE_UP_LEVEL_C_VALUE, Status.error.CONFIG_NOT_FOUND_VALUE);
			return false;
		}
		
		ConsumeItems consumeItems = ConsumeItems.valueOf();
		
		boolean isAdd = consumeItems.addConsumeInfo(player.getPlayerData(), upLevelcfg.getItemList());
			
		if (!isAdd || !consumeItems.checkConsume(player) || !consumeItems.consumeTakeAffect(player, Action.HERO_UP_LEVEL)) {
			sendError(HP.code.ROLE_UP_LEVEL_C_VALUE, Status.error.STAR_STONE_NOT_ENOUGH_VALUE);
			return false;
		}
		
		int oldLevel = roleEntity.getLevel();
		roleEntity.setLevel(roleEntity.getLevel() + 1);
		
		// 培养每日
		Msg hawkMsg = Msg.valueOf(GsConst.MsgType.DailyQuestMsg.ROLE_UPGRADE_LEVEL,
				GuaJiXID.valueOf(GsConst.ObjType.PLAYER, player.getId()));
		hawkMsg.pushParam(1);
		GsApp.getInstance().postMsg(hawkMsg);
		
		SevenDayQuestEventBus.fireQuestEventOneTime(SevenDayEventType.DEPUTYDEVELOP, this.player.getXid());
					
		// 刷新属性
		Attribute oldAttr = roleEntity.getAttribute().clone();
		PlayerUtil.refreshOnlineAttribute(player.getPlayerData(), roleEntity);
		player.getPlayerData().syncRoleInfo(roleEntity.getId());
		player.getPlayerData().syncRoleInfo(player.getPlayerData().getMainRole().getId());
		Attribute newAttr = roleEntity.getAttribute();
		PlayerUtil.pushAttrChange(player, oldAttr, newAttr);
		roleEntity.notifyUpdate();

		BehaviorLogger.log4Service(player, Source.USER_OPERATION, Action.HERO_UP_LEVEL,
				Params.valueOf("roleId", roleEntity.getId()), Params.valueOf("roleItemId", roleEntity.getItemId()),
				Params.valueOf("oldLevel", oldLevel),
				Params.valueOf("newLevel", roleEntity.getLevel()));
		
		HPRoleUPLevelRes.Builder builder = HPRoleUPLevelRes.newBuilder();
		builder.setRoleId(roleEntity.getId());
		builder.setNewLv(roleEntity.getLevel());
		player.sendProtocol(Protocol.valueOf(HP.code.ROLE_UP_LEVEL_S, builder));

		return true;
	}
	
	/**
	 * 英雄一鍵升等(到目前可到等級)
	 */
	private boolean onRoleUpLevelByOneKey(Protocol protocol) 
	{
		HPRoleUPLevel upLevel = protocol.parseProtocol(HPRoleUPLevel.getDefaultInstance());
		int roleId = upLevel.getRoleId();
				
		// 获取佣兵
		RoleEntity roleEntity = player.getPlayerData().getMercenaryById(roleId);
		if (roleEntity == null || !roleEntity.isHero()) {
			// 此功能英雄獨佔
			sendError(HP.code.ROLE_UP_LEVEL_C_VALUE, Status.error.ROLE_NOT_FOUND_VALUE);
			return false;
		}
				
		// 佣兵未激活
		if (roleEntity.getRoleState() == Const.RoleActiviteState.NOT_ACTIVITE_VALUE) {
			sendError(HP.code.ROLE_UP_LEVEL_C_VALUE, Status.error.PARAMS_INVALID);
			return false;
		}
		
		// 當前系統等級限制
		int oldLevel = roleEntity.getLevel();
					
		if (oldLevel >= HeroUpLevelCfg.getMaxLevel() || oldLevel >= roleEntity.getLimitLevel()) {
			// 已最大等级
			sendError(HP.code.ROLE_UP_LEVEL_C_VALUE, Status.error.ROLE_MAX_STAR);
			return false;
		}
		
		HeroUpLevelCfg upLevelcfg = HeroUpLevelCfg.getRoleUpLevelCfg(oldLevel);
		
		if (upLevelcfg == null) {
			sendError(HP.code.ROLE_UP_LEVEL_C_VALUE, Status.error.CONFIG_NOT_FOUND_VALUE);
			return false;
		}
		boolean haveUp = false;
		for (int i = oldLevel ; i <= HeroUpLevelCfg.getMaxLevel() ; i++) {
			
			int Level = roleEntity.getLevel();
			
			if (Level >= HeroUpLevelCfg.getMaxLevel() || Level >= roleEntity.getLimitLevel()) {
				// 已最大等级
				break;
			}
			
			HeroUpLevelCfg Levelcfg = HeroUpLevelCfg.getRoleUpLevelCfg(Level);
			
			if (Levelcfg == null) {
				break;
			}
			
			if (Levelcfg.getItemList().size() > 2) { // 需使用突破石停止
				break;
			}
			
			ConsumeItems consumeItems = ConsumeItems.valueOf();
		
			boolean isAdd = consumeItems.addConsumeInfo(player.getPlayerData(), Levelcfg.getItemList());
			
			if (!isAdd || !consumeItems.checkConsume(player) || !consumeItems.consumeTakeAffect(player, Action.HERO_UP_LEVEL_BYONE)) {
				break;
			}
			roleEntity.setLevel(roleEntity.getLevel() + 1);
			// 培养每日
			
			Msg hawkMsg = Msg.valueOf(GsConst.MsgType.DailyQuestMsg.ROLE_UPGRADE_LEVEL,
					GuaJiXID.valueOf(GsConst.ObjType.PLAYER, player.getId()));
			hawkMsg.pushParam(1);
			
			GsApp.getInstance().postMsg(hawkMsg);
			haveUp = true;
		}
		
		if (haveUp) {				
			// 刷新属性
			Attribute oldAttr = roleEntity.getAttribute().clone();
			PlayerUtil.refreshOnlineAttribute(player.getPlayerData(), roleEntity);
			player.getPlayerData().syncRoleInfo(roleEntity.getId());
			player.getPlayerData().syncRoleInfo(player.getPlayerData().getMainRole().getId());
			Attribute newAttr = roleEntity.getAttribute();
			PlayerUtil.pushAttrChange(player, oldAttr, newAttr);
			roleEntity.notifyUpdate();
	
			BehaviorLogger.log4Service(player, Source.USER_OPERATION, Action.HERO_UP_LEVEL_BYONE,
					Params.valueOf("roleId", roleEntity.getId()), Params.valueOf("roleItemId", roleEntity.getItemId()),
					Params.valueOf("oldLevel", oldLevel),
					Params.valueOf("newLevel", roleEntity.getLevel()));
		}
		
		HPRoleUPLevelRes.Builder builder = HPRoleUPLevelRes.newBuilder();
		builder.setRoleId(roleEntity.getId());
		builder.setNewLv(roleEntity.getLevel());
		player.sendProtocol(Protocol.valueOf(HP.code.ROLE_UP_LEVEL_S, builder));
		return true;
	}

	/**
	 * 英雄突破(升星)
	 *
	 * @param protocol
	 */
	private boolean onRoleUpgradeStar(Protocol protocol) {

		HPRoleUpStar upStage = protocol.parseProtocol(HPRoleUpStar.getDefaultInstance());
		int roleId = upStage.getRoleId();


		// 獲取欲突破的英雄
		RoleEntity roleEntity = player.getPlayerData().getMercenaryById(roleId);
		if (roleEntity == null) {
			sendError(HP.code.ROLE_UPGRADE_STAR_C_VALUE, Status.error.ROLE_NOT_FOUND_VALUE);
			return false;
		}
		
		// 佣兵未激活
		if (roleEntity.getRoleState() == Const.RoleActiviteState.NOT_ACTIVITE_VALUE) {
			sendError(HP.code.ROLE_UPGRADE_STAR_C_VALUE, Status.error.PARAMS_INVALID);
			return false;
		}
					
		if (roleEntity.getStarLevel() >= HeroStarCfg.getHeroMaxStar(roleEntity.getItemId())) {
			// 已经到突破最高级了
			sendError(HP.code.ROLE_UPGRADE_STAR_C_VALUE, Status.error.ROLE_MAX_RANK);
			return false;
		}
		
		HeroStarCfg cfg = HeroStarCfg.getHeroStarCfg(roleEntity.getItemId(), roleEntity.getStarLevel());
		
		if (cfg == null) {
			//找不到配置表
			sendError(HP.code.ROLE_UPGRADE_STAR_C_VALUE, Status.error.CONFIG_NOT_FOUND_VALUE);
			return false;
		}
		
//		if (cfg.getCost() <= 0) {
//			sendError(HP.code.ROLE_UPGRADE_STAR_C_VALUE, Status.error.CONFIG_ERROR_VALUE);
//			return false;
//		}
		
		if (cfg.getCostList().size() > 0) {
			ConsumeItems consumeItems = ConsumeItems.valueOf();
			boolean isAdd = consumeItems.addConsumeInfo(player.getPlayerData(), cfg.getCostList());
			if (!isAdd || !consumeItems.checkConsume(player) || !consumeItems.consumeTakeAffect(player, Action.HERO_UP_STAR)) {
				sendError(HP.code.ROLE_UPGRADE_STAR_C_VALUE, Status.error.STAR_STONE_NOT_ENOUGH_VALUE);
				return false;
			}
		}
			
		int oldStarLv = roleEntity.getStarLevel();
		roleEntity.setStarLevel(oldStarLv+ 1);
		
		String Award = cfg.getAwards();
		
		
		AwardItems awards = AwardItems.valueOf(Award);
		
		if (awards != null) {
			awards.rewardTakeAffectAndPush(player, Action.HERO_UP_STAR, 0);
		}
		
		// 刷新佣兵属性
		Attribute oldAttr = roleEntity.getAttribute().clone();
		PlayerUtil.refreshOnlineAttribute(player.getPlayerData(), roleEntity);

		PlayerUtil.refreshOnlineAttribute(player.getPlayerData(), player.getPlayerData().getMainRole());
		Attribute newAttr = roleEntity.getAttribute().clone();
		PlayerUtil.pushAttrChange(player, oldAttr, newAttr);
		roleEntity.notifyUpdate();
		
		//player.getPlayerData().syncRoleInfo(roleEntity.getId());
		//player.getPlayerData().syncRoleInfo(player.getPlayerData().getMainRole().getId());
		// 羈絆重整
		ArchiveEntity archiveEntity = player.getPlayerData().getArchiveEntity();
		if(archiveEntity != null && archiveEntity.getOpenFetters() != null){
			for(int fetterId : archiveEntity.getOpenFetters()){
				FetterCfg fetterCfg = ConfigManager.getInstance().getConfigByKey(FetterCfg.class, fetterId);
				if (fetterCfg != null) {
					if (fetterCfg.getArchiveIds().contains(roleEntity.getItemId())) {
						int minStar = fetterCfg.getGroupMinStar(player.getPlayerData());
						if (roleEntity.getStarLevel() == minStar) { // 是否為羈絆的最小星數
							for (int itemId :fetterCfg.getArchiveIds()) { // 更新其他人羈絆能力
								if (itemId != roleEntity.getItemId()) {
									RoleEntity heroEntity = player.getPlayerData().getMercenaryByItemId(itemId);
									PlayerUtil.refreshOnlineAttribute(player.getPlayerData(), heroEntity);
								}
							}
						}
					}
				}
			}
		}

		player.getPlayerData().syncRoleInfo(0);
		
		HPRoleUPStarRes.Builder builder = HPRoleUPStarRes.newBuilder();
		builder.setRoleId(roleEntity.getId());
		builder.setNewStar(roleEntity.getStarLevel());

		player.sendProtocol(
				Protocol.valueOf(HP.code.ROLE_UPGRADE_STAR_S, builder));
		// 推送佣兵训练升星任务 重複去除 59
//		QuestEventBus.fireQuestEvent(QuestEventType.ROLE_UPGRADE_QUEST, roleEntity.getStarLevel(),
//				GuaJiXID.valueOf(GsConst.ObjType.PLAYER, this.player.getId()));
		
        // 推送佣兵升星任务事件
        QuestEventBus.fireQuestEvent(QuestEventType.HERO_LEVEL, roleEntity.getStarLevel(), GuaJiXID.valueOf(GsConst.ObjType.PLAYER, this.player.getId()));

		// 7日之诗 副将训练
		SevenDayQuestEventBus.fireQuestEventOneTime(SevenDayEventType.DEPUTYTRAIN, this.player.getXid());

		// 培养每日
		Msg hawkMsg = Msg.valueOf(GsConst.MsgType.DailyQuestMsg.ROLE_UPGRADE_STAR,
				GuaJiXID.valueOf(GsConst.ObjType.PLAYER, player.getId()));
		hawkMsg.pushParam(1);
		GsApp.getInstance().postMsg(hawkMsg);

		BehaviorLogger.log4Service(player, Source.USER_OPERATION, Action.HERO_UP_STAR,
				Params.valueOf("roleId", roleEntity.getId()), Params.valueOf("roleItemId", roleEntity.getItemId()),
				Params.valueOf("Award", Award),
				Params.valueOf("oldStarLv", oldStarLv),
				Params.valueOf("newStarLv", roleEntity.getStarLevel()));
		return true;
	}
	
	/**
	 * 角色修改签名
	 *
	 * @param protocol
	 * @return
	 */
	private boolean onRoleChangeSignature(Protocol protocol) {
		HPChangeSignature changeSignature = protocol.parseProtocol(HPChangeSignature.getDefaultInstance());
		String signature = changeSignature.getSignature();
		if (signature.length() >= 50) {
			return false;
		}
		signature = GameUtil.filterString(signature);
		player.getPlayerData().getPlayerEntity().setSignature(signature);
		player.getPlayerData().syncPlayerInfo();
		player.getPlayerData().getPlayerEntity().notifyUpdate(true);
		HPChangeSignatureRet.Builder builder = HPChangeSignatureRet.newBuilder();
		builder.setVersion(GuaJiTime.getSeconds());
		// 推送修改签名任务
		QuestEventBus.fireQuestEventOneTime(QuestEventType.MODIFY_SIN, player.getXid());
		sendProtocol(Protocol.valueOf(HP.code.ROLE_CHANGE_SIGNATURE_S, builder));
		player.getPlayerData().syncPlayerInfo();
		return true;
	}

	/**
	 * @param protocol
	 */
	public void onMainRoleHeadIconInfo(Protocol protocol) {

		PlayerHeadIconInfoRet.Builder headIconBuilder = PlayerHeadIconInfoRet.newBuilder();
		//int curHeadIcon = player.getPlayerData().getPlayerEntity().getHeadIcon();
		headIconBuilder.setHeadIcon(player.getPlayerData().getPlayerEntity().getHeroDataString());
		List<String> buyIconList = player.getPlayerData().getPlayerEntity().getIconList();
		for (String string : buyIconList) {

			headIconBuilder.addBuyIconList(Integer.parseInt(string));
		}
		sendProtocol(Protocol.valueOf(HP.code.MAINROLE_ICON_INFO_S_VALUE, headIconBuilder));

	}

	/**
	 * @param protocol
	 */
	public void onMainRoleHeadIconChange(Protocol protocol) {

		PlayerHeadIconChangeReq changeIconReq = protocol.parseProtocol(PlayerHeadIconChangeReq.getDefaultInstance());
		int changeIconId = changeIconReq.getHeadIconId();

		if (changeIconId < 0) {
			sendError(protocol.getType(), Status.error.PARAMS_INVALID);
			return;
		}
		
		if ((changeIconId != 0)&&(changeIconId < GsConst.HEADICON_BASE)) {
			sendError(protocol.getType(), Status.error.PARAMS_INVALID);
			return;
		}
		
		RoleEntity aRole = null;
		if (changeIconId > 0) {
			int itemId = changeIconId / GsConst.HEADICON_BASE;
			int skinId = changeIconId % GsConst.HEADICON_BASE;
			aRole = player.getPlayerData().getMercenaryByItemId(itemId);
			if ((aRole == null)) {
				sendError(protocol.getType(), Status.error.PARAMS_INVALID);
				return;
			}
			if (!aRole.isHero()) {
				sendError(protocol.getType(), Status.error.PARAMS_INVALID);
				return;
			}
			
			RoleSkinEntity roleSkinEntity = player.getPlayerData().loadRoleSkinEntity();
			
			if (roleSkinEntity == null) {
				sendError(protocol.getType(), Status.error.DATA_NOT_FOUND);
				return;
			}
			
			// 檢查skinId
			if ((skinId != 0)) {
				if (!roleSkinEntity.haveSkin(skinId)) {
					sendError(protocol.getType(), Status.error.PARAMS_INVALID);
					return;					
				}
				if (!aRole.isMySkin(skinId)) {
					sendError(protocol.getType(), Status.error.PARAMS_INVALID);
					return;
				}
			}
		}

		int curHeadIcon = player.getPlayerData().getPlayerEntity().getHeadIcon();

		if (curHeadIcon == changeIconId) {
			sendError(protocol.getType(), Status.error.PARAMS_INVALID);
			return;
		}
				
		player.getPlayerData().getPlayerEntity().setHeadIcon(changeIconId);

		player.getPlayerData().getPlayerEntity().notifyUpdate();

		PlayerHeadIconChangeRep.Builder repBuilder = PlayerHeadIconChangeRep.newBuilder();
		repBuilder.setChangeIcon(changeIconId);
		repBuilder.setStatus(1);
		sendProtocol(Protocol.valueOf(HP.code.MAINROLE_ICON_CHANGE_S_VALUE, repBuilder));

	}
	


	private int calcuDecreasSearchTime() {
		int decreaseTime = 0;
//		if (player == null) {
//			return decreaseTime;
//		}
//		if (player.getPlayerData() == null) {
//			return decreaseTime;
//		}
//		List<RoleEntity> roleList = player.getPlayerData().getRoleEntities();
//		if (roleList == null) {
//			return decreaseTime;
//		}
//		Map<Integer, Integer> maps = SysBasicCfg.getInstance().getDecrPVESearchTimeMap();
//		int size = roleList.size();
//		for (int i = 0; i < size; i++) {
//			RoleEntity role = roleList.get(i);
//			if (role.getStatus() != Const.RoleStatus.FIGHTING_VALUE) {
//				continue;
//			}
//			RoleCfg roleCfg = ConfigManager.getInstance().getConfigByKey(RoleCfg.class, role.getItemId());
//			if (roleCfg == null) {
//				continue;
//			}
//			if (!maps.containsKey(roleCfg.getQuality())) {
//				continue;
//			}
//			decreaseTime += maps.get(roleCfg.getQuality());
//		}
		return decreaseTime / 1000;
	}

	/**
	 * 玩家登陆处理(数据同步)
	 */
	@Override
	protected boolean onPlayerLogin() {
		this.tickIndex = 0;
		// 激活新佣兵
		Map<Object, RoleRelatedCfg> cfg = ConfigManager.getInstance().getConfigMap(RoleRelatedCfg.class);
		if (cfg != null) {
			int newVipLevel = player.getVipLevel();
			for (RoleRelatedCfg relatedCfg : cfg.values()) {
				if (relatedCfg.getCostType() == GsConst.RoleSoulExchangeType.ROLE_VIP) {
					RoleEntity roleEntity = player.getPlayerData().getMercenaryByItemId(relatedCfg.getId());
					if (roleEntity != null) {
						if (newVipLevel >= relatedCfg.getCostLevel()
								&& roleEntity.getRoleState() == Const.RoleActiviteState.NOT_ACTIVITE_VALUE) {
							roleEmploy(roleEntity);
							// 同步收集的碎片数量
							player.getPlayerData().syncMercenarySoulInfo(roleEntity.getItemId());
						}
					}
				}
			}
		}

		// 檢查並增加DB傭兵數量
		List<RoleEntity> roleEntities = player.getPlayerData().getRoleEntities();
		Map<Integer, Hero_NGListCfg> roleCfgMap = Hero_NGListCfg.getRoleInfoMap();
		// 临时缓存
		Map<Integer, Integer> roleCacheMap = new HashMap<>();
		if (roleEntities.size() != roleCfgMap.size()) {
			for (RoleEntity entity : roleEntities) {
				if (entity.getType() != Const.roleType.MAIN_ROLE_VALUE) {
					roleCacheMap.put(entity.getItemId(), entity.getItemId());
				}
			}
			
			// 对比是否配置有新添加佣兵
			for (Entry<Integer, Hero_NGListCfg> c : roleCfgMap.entrySet()) {
				if (Hero_NGListCfg.getPlayerCfgIdx() == c.getKey()) {
					continue;
				}
				if (!roleCacheMap.containsKey(c.getKey())) {
					player.getPlayerData().createMercenary(c.getKey());
				}
			}
		}
		
		// 創建激活英雄的秘密留言資料
		List<RoleEntity> HeroEntities = player.getPlayerData().getActiviceHero();
		for (RoleEntity aEntity :HeroEntities) {
			int itemId = aEntity.getItemId();
			SecretMsgEntity sEntity =  player.getPlayerData().getSecretMsgByItem(itemId);
			if (sEntity == null) {
				player.getPlayerData().createSecretMsg(itemId);
			} else {
				// 檢查是否有企劃新增訊息
				List<Integer> allMsg = SecretMsgCfg.getHeroAllMsg(itemId);
				if (allMsg != null) {
					boolean needsave = false;
					for (Integer MsgId :allMsg) {
						if (!sEntity.getChoiceMsgMap().containsKey(MsgId)) {
							sEntity.addChoiceMsg(MsgId, -1);
							needsave = true;
						}
					}
					if (needsave) {
						sEntity.notifyUpdate();
					}
				}
				// fix album reward
				if (sEntity.getAblumMaxId() != 0) {
					int AlbumMaxId = sEntity.getAblumMaxId();
					List<SecretAlbumCfg> albumCfgList = SecretAlbumCfg.getAlbumListByHeroId(itemId);
					for (SecretAlbumCfg scfg :albumCfgList) {
						if (scfg.getId() <= AlbumMaxId) {
							sEntity.addFreeCfgId(scfg.getId());
							sEntity.addUnlockCfgId(scfg.getId());
						}
					}
					sEntity.setAblumMaxId(0);
					sEntity.notifyUpdate();
				}
			}
		}

		// 转换角色基础属性
		player.getPlayerData().converRolesBaseAttr();

		// 同步主角信息
		player.getPlayerData().syncRoleInfo(player.getPlayerData().getMainRole().getId());

		//fixFormationByRoleStatus();
		// 同步周卡月卡数据
		ActivityUtil.syncWeekMonthStatus(player.getPlayerData());
		int decreasePVESearchTime = calcuDecreasSearchTime();
		player.getPlayerData().setDecreasePVESearchTime(decreasePVESearchTime);
		
		player.getPlayerData().loadRoleSkinEntity();
		
		player.getPlayerData().syncSecretMsgInfo();

		return true;
	}

	@Override
	protected boolean onPlayerAssemble() {
		// 同步佣兵信息
		List<RoleEntity> roleEntities = player.getPlayerData().getRoleEntities();
		for (RoleEntity roleEntity : roleEntities) {
			if (roleEntity.isArmy()) {

				player.getPlayerData().syncRoleInfo(roleEntity.getId());
				// 推送佣兵升星任务事件
				//QuestEventBus.fireQuestEvent(QuestEventType.HERO_LEVEL, roleEntity.getStarLevel(), player.getXid());
			}
		}

		sendProtocol(Protocol.valueOf(HP.code.EQUIP_SYNC_FINISH_S_VALUE,
				HPEquipSyncFinish.newBuilder().setPlayerId(player.getId())));
		return true;
	}

	/**
	 * 玩家角色创建()
	 *
	 * @param protocol
	 */
	private boolean onMainRoleCreate(int hpCode, HPRoleCreate protocol) {
		HPRoleCreateRet.Builder builder = HPRoleCreateRet.newBuilder();
		builder.setStatus(0);
		PlayerData playerData = player.getPlayerData();

		RoleEntity mainRoleEntity = playerData.getMainRole();
		if (mainRoleEntity != null) {
			builder.setStatus(Status.error.MAIN_ROLE_EXIST_VALUE);
			sendProtocol(Protocol.valueOf(HP.code.ROLE_CREATE_S, builder));
			return false;
		}

		// 获取角色名
		String roleName = protocol.getRoleName();
		roleName = roleName.trim().replaceAll(" ", "").replaceAll("　", "");
		// 角色名存在
		if (ServerData.getInstance().isExistName(roleName)) {
			builder.setStatus(Status.error.ROLE_NAME_EXIST_VALUE);
			sendProtocol(Protocol.valueOf(HP.code.ROLE_CREATE_S, builder));
			return false;
		}
		
		// 角色名稱不符合規則
		if (PlayerUtil.hasDirtyKey(roleName)) {
			builder.setStatus(Status.error.ROLE_NAME_EXIST_VALUE);
			sendProtocol(Protocol.valueOf(HP.code.ROLE_CREATE_S, builder));
			return false;
		}

		// 角色名长度不对
		if (roleName.length() <= 0 || roleName.length() > SysBasicCfg.getInstance().getRoleNameMaxLen()) {
			builder.setStatus(Status.error.ROLE_NAME_EXIST_VALUE);
			sendProtocol(Protocol.valueOf(HP.code.ROLE_CREATE_S, builder));
			return false;
		}

		RoleEntity roleEntity = playerData.createMainRole(Hero_NGListCfg.getPlayerCfgIdx(), roleName);
		if (roleEntity == null) {
			builder.setStatus(Status.error.CREATE_ROLE_FAILED_VALUE);
			sendProtocol(Protocol.valueOf(HP.code.ROLE_CREATE_S, builder));
			return false;
		}
	
		// 初始化玩家钻石和金币

		playerData.getPlayerEntity().setCoin(SysBasicCfg.getInstance().getInitCoin());
		playerData.getPlayerEntity().setSysGold(SysBasicCfg.getInstance().getInitGold());


		builder.setRoleInfo(BuilderUtil.genRoleBuilder(playerData, roleEntity, playerData.getEquipEntities(),
				playerData.getSkillEntities(), playerData.getElementEntities(), playerData.getBadgeEntities()));
		player.sendProtocol(Protocol.valueOf(HP.code.ROLE_CREATE_S, builder));
				
		playerData.getPlayerEntity().setProf(roleEntity.getProfession());
		playerData.getPlayerEntity().setVipLevel(SysBasicCfg.getInstance().getInitVipLevel());

		playerData.getPlayerEntity().notifyUpdate(true);
		playerData.syncPlayerInfo();

		// 通知玩家其他模块玩家登陆成功
		App.getInstance().postMsg(Msg.valueOf(GsConst.MsgType.PLAYER_LOGIN, player.getXid()));

		// 添加聊天玩家
		ChatManager.getInstance().addSession(player.getSession(), true);

		// 添加存在名字到缓存
		ServerData.getInstance().addIfNameAbsent(roleName, player.getId());

		// Adjust 创建角色
		AdjustEventUtil.sentAdjustEventInfo(player, GsConst.AdjustActionType.CREATORSUCCESS, 0);

		int serverId = player.getPlayerData().getPlayerEntity().getServerId();
		// 注册信息上报
		ReportService.RegisterData registerData = new ReportService.RegisterData(serverId, player.getPuid(),
				player.getDevice(), player.getPhoneInfo(), player.getId(), GuaJiTime.getTimeString());
		ReportService.getInstance().report(registerData);

		// 发送注册奖励
		playerRegisterReward(player.getId(), playerData.getPlayerEntity().getPlatform());

		// 創建英雄
		playerData.createMercenary(0);
		// 創建箴言
		playerData.createMotto(0);
				
		return true;

	}

	/**
	 * 注册奖励
	 *
	 * @param playerId
	 * @param platform
	 */
	private void playerRegisterReward(int playerId, String platform) {
		if (platform == null || platform.isEmpty()) {
			return;
		}
		List<PlatInitRewardConfig> configList = ConfigManager.getInstance().getConfigList(PlatInitRewardConfig.class);
		PlatInitRewardConfig platInitRewardConfig = null;
		for (PlatInitRewardConfig config : configList) {
			if (config.getPlatform() != null && config.getPlatform().equals(platform)) {
				platInitRewardConfig = config;
				break;
			}
		}

		if (platInitRewardConfig == null || !platInitRewardConfig.isValid()) {
			return;
		}

		// 判断时间是否有效
		boolean isReward = false;
		if (platInitRewardConfig.getRewardEndTime() == 0) {
			isReward = true;
		} else {
			long nowTime = System.currentTimeMillis();
			if (nowTime >= platInitRewardConfig.getRewardStartTime()
					&& nowTime <= platInitRewardConfig.getRewardEndTime()) {
				isReward = true;
			}
		}

		// 发放邮件奖励
		if (isReward) {
			AwardItems awardItems = null;
			if (!platInitRewardConfig.getRewardInfo().isEmpty()) {
				awardItems = AwardItems.valueOf(platInitRewardConfig.getRewardInfo());
			}
			// MailManager.createMail(playerId, Mail.MailType.Normal_VALUE, 0,
			// platInitRewardConfig.getRewardMsg(),
			// awardItems);
			MailManager.createSysMail(playerId, Mail.MailType.Reward_VALUE, 0, platInitRewardConfig.getRewardMsg(),
					awardItems);
		}
	}

	/**
	 * 修改角色名字
	 *
	 * @param protocol
	 */
	private void onRoleChangeName(Protocol protocol) {
		HPChangeRoleName params = protocol.parseProtocol(HPChangeRoleName.getDefaultInstance());
		// 参数校验
		String newName = params.getName();
		if (StringUtils.isBlank(newName)) {
			sendError(protocol.getType(), Status.error.PARAMS_INVALID_VALUE);
			return;
		}
		
		// 角色名稱不符合規則
		if (PlayerUtil.hasDirtyKey(newName)) {
			sendError(protocol.getType(), Status.error.ROLE_NAME_EXIST_VALUE);
			return;
		}
		
		// 角色名存在
		if (ServerData.getInstance().isExistName(newName)) {
			sendError(protocol.getType(), Status.error.ROLE_NAME_EXIST_VALUE);
			return;
		}

		// 角色名长度不对
		if (newName.length() <= 0 || newName.length() > SysBasicCfg.getInstance().getRoleNameMaxLen()) {
			sendError(protocol.getType(), Status.error.ROLE_NAME_EXIST_VALUE);
			return;
		}

		boolean consumed = false;
		// 先消耗改名卡
		String changeNameCardStr = SysBasicCfg.getInstance().getChangeNameCard();
		if (StringUtils.isBlank(changeNameCardStr)) {
			sendError(protocol.getType(), Status.error.PARAMS_INVALID_VALUE);
			return;
		}
		List<ItemInfo> itemInfoList = ItemInfo.valueListOf(changeNameCardStr);
		ConsumeItems consumeItems = ConsumeItems.valueOf();
		if (consumeItems.addConsumeInfo(player.getPlayerData(), itemInfoList)) {
			if (consumeItems.checkConsume(player)) {
				if (consumeItems.consumeTakeAffect(player, Action.CHANGE_NAME)) {
					consumed = true;
				}
			}
		}

		// 没有改名卡消耗钻石
		if (!consumed) {
			consumeItems = ConsumeItems.valueOf(changeType.CHANGE_GOLD, SysBasicCfg.getInstance().getChangeNameCost());
			if (!consumeItems.checkConsume(player, protocol.getType())) {
				return;
			}
			consumeItems.consumeTakeAffect(player, Action.CHANGE_NAME);
		}

		String oldName = player.getPlayerData().getPlayerEntity().getName();
		// player
		PlayerEntity playerEntity = player.getPlayerData().getPlayerEntity();
		playerEntity.setName(newName);
		playerEntity.notifyUpdate(true);
		// role
		RoleEntity roleEntity = player.getPlayerData().getMainRole();
		roleEntity.setName(newName);
		roleEntity.notifyUpdate(true);

		// 删除之前的名字缓存
		ServerData.getInstance().removeNameAndPlayerId(oldName, player.getId());
		// 添加存在名字到缓存
		ServerData.getInstance().addIfNameAbsent(newName, player.getId());

		// 帮主姓名修改
		PlayerAllianceEntity playerAllianceEntity = player.getPlayerData().getPlayerAllianceEntity();
		if (playerAllianceEntity != null && playerAllianceEntity.getAllianceId() > 0) {
			AllianceEntity allianceEntity = AllianceManager.getInstance()
					.getAlliance(playerAllianceEntity.getAllianceId());
			if (allianceEntity != null && allianceEntity.getPlayerId() == player.getId()) {
				allianceEntity.setPlayerName(newName);
				AllianceBattleItem allianceItem = AllianceBattleManager.getInstance()
						.getBattleItem(allianceEntity.getId());
				if (allianceItem != null) {
					allianceItem.setCaptainName(newName);
					allianceItem.notifyUpdate();
				}
				allianceEntity.notifyUpdate();
			}
		}

		PlayerSnapshotInfo.Builder playerInfo = SnapShotManager.getInstance().getPlayerSnapShot(player.getId());
		if (playerInfo.getMainRoleInfoBuilder() != null) {
			playerInfo.getMainRoleInfoBuilder().setName(newName);
		}
		String notice = ChatManager.getMsgJson(SysBasicCfg.getInstance().getChangeNameNotice(), oldName, newName);
		// 发送世界聊天
		GsApp.getInstance().broadcastChatWorldMsg(notice, notice);
		// 发送公会聊天
		ChatManager.getInstance().postChat(player, notice, Const.chatType.CHAT_ALLIANCE_SYSTEM_VALUE, 1);

		Msg hawkMsg = Msg.valueOf(GsConst.MsgType.CHANGE_NAME,
				GuaJiXID.valueOf(GsConst.ObjType.PLAYER, player.getId()));
		hawkMsg.pushParam(notice);
		GsApp.getInstance().postMsg(hawkMsg);

		player.getPlayerData().syncRoleInfo(player.getPlayerData().getMainRole().getId());
		sendExpData(newName);

		sendProtocol(Protocol.valueOf(HP.code.ROLE_CHANGE_NAME_S, HPChangeRoleNameRet.newBuilder().setName(newName)));
	}

	public void sendExpData(String newName) {
		// 通知排行献礼管理器更改玩家昵称
		if (RankGiftManager.getInstance().isActivityNotClose()) {
			GsApp.getInstance().postMsg(RankGiftManager.getInstance().getXid(),
					Msg.valueOf(GsConst.MsgType.PLAYER_NAME_CHANGE).pushParam(player.getId(), newName));
		}
	}

	/**
	 * 激活光环
	 *
	 * @param roleEntity
	 */
//	private void activiceRing(RoleEntity roleEntity) {
		// 获取升阶后配置
//		RoleUpStageCfg nextStageCfg = RoleUpStageCfg.getRoleUpStageCfg(roleEntity.getItemId(),
//				roleEntity.getStageLevel());
//		if (nextStageCfg != null) {
//			// 解锁光环
//			int ringId = nextStageCfg.getRingId();
//			if (ringId != 0) {
//				// 解锁光环
//				RingCfg ringCfg = ConfigManager.getInstance().getConfigByKey(RingCfg.class, ringId);
//				// 配置没找到
//				if (ringCfg != null) {
//					if (!roleEntity.getRingList().contains(ringId) && roleEntity.getRingList().size() < 2) {
//						roleEntity.addRing(ringId);
//					}
//				}
//
//				roleEntity.notifyUpdate();
//			}
//		}
//	}

	/**
	 * 激活技能
	 *
	 * @param skillId
	 * @param roleEntity
	 * @return
	 */
//	private boolean activiceSkill(RoleEntity roleEntity) {
//		RoleUpStageCfg roleUpStageCfg = RoleUpStageCfg.getRoleUpStageCfg(roleEntity.getItemId(),
//				roleEntity.getStageLevel());
//		if (roleUpStageCfg == null) {
//			return false;
//		}
//
//		int skillId = roleUpStageCfg.getActiveSkill();
//		if (skillId == 0) {
//			return false;
//		}
//
//		// 已学
//		if (player.getPlayerData().getSkillEntityInfo(skillId, roleEntity) != null) {
//			return false;
//		}
//		// 等级条件达到
//		NewSkillCfg skillCfg = ConfigManager.getInstance().getConfigByKey(NewSkillCfg.class, skillId);
//		if (skillCfg != null) {
//			SkillEntity skillEntity = player.getPlayerData().createSkill(roleEntity, skillId);
//			if (skillEntity != null) {
//				roleEntity.addRoleSkill(skillEntity.getId());
//				roleEntity.notifyUpdate(false);
//				player.getPlayerData().syncSkillInfo(skillEntity.getId());
//			}
//		}
//		return true;

//	}

	/**
	 * 更换佣兵皮肤
	 */
	private void onChangeMercenarySkin(Protocol protocol) {
		HPChangeMercenarySkinReq req = protocol.parseProtocol(HPChangeMercenarySkinReq.getDefaultInstance());
		int fromRoleId = req.getFromRoleId();
		int skinId = req.getToRoleId();
		// 检查参数
		if (fromRoleId <= 0 || skinId < 0) {
			sendError(protocol.getType(), Status.error.PARAMS_INVALID_VALUE);
			return;
		}
		RoleEntity fromEntity = player.getPlayerData().getMercenaryById(fromRoleId);
		
		if (fromEntity == null ) {
			// 参数错误
			sendError(protocol.getType(), Status.error.PARAMS_INVALID_VALUE);
			return;
		}
		
		if (!fromEntity.isHero()) {
			// 此功能英雄獨佔
			sendError(protocol.getType(), Status.error.PARAMS_INVALID_VALUE);
			return;
		}
		
		if (fromEntity.getRoleState() == Const.RoleActiviteState.NOT_ACTIVITE_VALUE) {
			// 佣兵未激活
			sendError(protocol.getType(), Status.error.MERCENARY_NOT_FOUND_VALUE);
			return;
		}
		
		// 皮肤关系Check
		if (skinId != 0) {
			RoleSkinCfg skinCfg = ConfigManager.getInstance().getConfigByKey(RoleSkinCfg.class,skinId);
			if (skinCfg == null) {
				sendError(protocol.getType(), Status.error.CONFIG_NOT_FOUND);
				return;
			}
			
			RoleSkinEntity roleSkinEntity = player.getPlayerData().loadRoleSkinEntity();
			
			if (roleSkinEntity == null) {
				sendError(protocol.getType(), Status.error.DATA_NOT_FOUND);
				return;
			}
			if(!roleSkinEntity.haveSkin(skinId)){
				sendError(protocol.getType(), Status.error.DATA_NOT_FOUND);
				return;
			}
		}
		// 是否為該角色皮膚
		if ((skinId != 0) && (!fromEntity.isMySkin(skinId))) {
			sendError(protocol.getType(), Status.error.PARAMS_INVALID_VALUE);
			return;
		}
		
		Attribute oldAttr = PlayerUtil.refreshOnlineAttribute(player.getPlayerData(), fromEntity).clone();
		
		// 更换皮肤
		fromEntity.setSkinId(skinId);
		
//		List<RoleEntity> herolist = player.getPlayerData().getRoleEntities();
//		for (RoleEntity ahero : herolist) {
//			if (ahero.getId() == fromEntity.getId()) {
//				continue;
//			}
//			PlayerUtil.refreshOnlineAttribute(player.getPlayerData(), ahero);
//		}
		// 刷新属性
		Attribute newAttr = PlayerUtil.refreshOnlineAttribute(player.getPlayerData(), fromEntity).clone();

		player.getPlayerData().syncRoleInfo(fromEntity.getId());
		player.getPlayerData().syncMercenarySoulInfo(fromEntity.getItemId());
		
		// 更新DB
		fromEntity.notifyUpdate();
//
//		Msg hawkMsg = Msg.valueOf(GsConst.MsgType.FORMATION_MODIFY,
//				GuaJiXID.valueOf(GsConst.ObjType.PLAYER, player.getId()));
//		hawkMsg.pushParam(1);
//		GsApp.getInstance().postMsg(hawkMsg);

//		GvgService.getInstance().updateOccupy(player.getId(), fromRoleId, toRoleId);

		// 返回更换皮肤成功
		HPChangeMercenarySkinRes.Builder res = HPChangeMercenarySkinRes.newBuilder();
		res.setFromRoleId(fromRoleId);
		res.setToRoleId(skinId);
		
		sendProtocol(Protocol.valueOf(HP.code.ROLE_CHANGE_SKIN_S_VALUE, res));

		PlayerUtil.pushAttrChange(player, oldAttr, newAttr);
	}
	
	private void onHandleSecretMsg(Protocol protocol) {
		
		if (!FunctionUnlockCfg.checkUnlock(player,null,Const.FunctionType.SecretMessage_Unlock)){
			player.sendError(protocol.getType(), Status.error.CONDITION_NOT_ENOUGH);
			return;
		}
		
		secretMsgRequest req = protocol.parseProtocol(secretMsgRequest.getDefaultInstance());
		int action = req.getAction();
		String reward = "";
		if (action == 1) {
			int msgId = req.getMsgId();
			int choice = req.getChoice();
			SecretMsgCfg cfg = ConfigManager.getInstance().getConfigByKey(SecretMsgCfg.class, msgId);
			if (cfg == null) {
				player.sendError(protocol.getType(), Status.error.CONFIG_NOT_FOUND_VALUE);
				return;
			}
//			List<Integer> msgList =  player.getPlayerData().getStateEntity().getSecretMsgList();
//			if ((msgList == null)||(!msgList.contains(msgId))) {
//				player.sendError(protocol.getType(), Status.error.DATA_NOT_FOUND_VALUE);
//				return;
//			}
			if ((choice < 0) || (choice > 1)) {
				player.sendError(protocol.getType(), Status.error.PARAMS_INVALID_VALUE);
				return;
			}
			int itemId = cfg.getHero();
			
			RoleEntity roleEntity =  player.getPlayerData().getMercenaryByItemId(itemId);
			
			if ((!roleEntity.isHero())||(roleEntity.getRoleState()!= Const.RoleActiviteState.IS_ACTIVITE_VALUE)) {
				player.sendError(protocol.getType(), Status.error.CONFIG_ERROR_VALUE);
				return;
			}
			
			SecretMsgEntity smEntity = player.getPlayerData().getSecretMsgByItem(itemId);
			if (smEntity == null) {
				player.sendError(protocol.getType(), Status.error.DATA_NOT_FOUND_VALUE);
				return;
			}
			
			if ((!smEntity.getChoiceMsgMap().containsKey(msgId)) || (smEntity.getChoiceMsgMap().get(msgId) != -1)) {
				player.sendError(protocol.getType(), Status.error.DATA_NOT_FOUND_VALUE);
				return;
			}
			
			int power = player.getPlayerData().getStateEntity().getSecretPower();
			
			if (power < SysBasicCfg.getInstance().getSecretDecPower()) {
				player.sendError(protocol.getType(), Status.error.ALREADY_GOT_LIMIT_ERROR_VALUE);
				return;
			}
			
			int recoverTime = player.getPlayerData().getStateEntity().getLastRecoverTime();
			int currTime = GuaJiTime.getSeconds();
			
			if (recoverTime == 0) {
				player.getPlayerData().getStateEntity().setLastRecoverTime(currTime);
			}
			
			player.getPlayerData().getStateEntity().setSecretPower(Math.min(power-SysBasicCfg.getInstance().getSecretDecPower(),player.getSecretMaxPower()));
			player.getPlayerData().getStateEntity().notifyUpdate(true);
			
			smEntity.addChoiceMsg(msgId,choice);
			smEntity.notifyUpdate(true);
			
			TapDBUtil.Event_SecretMessage(player,player.getTapDBUId(),itemId,msgId);
//			player.getPlayerData().getStateEntity().delSecretMsgId(msgId);
			
			
			
		} else if ((action == 0)||(action == 3)) { // 同步
			
		} else if (action == 2) { // 解鎖相簿
			int albumId = req.getUnlockPic();
			SecretAlbumCfg albumCfg = ConfigManager.getInstance().getConfigByKey(SecretAlbumCfg.class,albumId);
			
			if (albumCfg == null) {
				player.sendError(protocol.getType(), Status.error.CONFIG_ERROR_VALUE);
				return;
			}
			SecretMsgEntity smEntity = player.getPlayerData().getSecretMsgByItem(albumCfg.getItemId());
			
			if (smEntity == null) {
				player.sendError(protocol.getType(), Status.error.DATA_NOT_FOUND_VALUE);
				return;
			}
			
			//int AlbumMaxId = smEntity.getAblumMaxId();
			
			if (smEntity.getUnlockCfgId().contains(albumId)) {
				player.sendError(protocol.getType(), Status.error.PARAMS_INVALID_VALUE);
				return;
			}
			
			Map<Integer, Integer> valueMap = PlayerUtil.calcSecretMsgValue(player.getPlayerData(), albumCfg.getItemId());
			
			int ownScore = valueMap.get(0); // 擁有的好感度
			
			int totalScore = SecretAlbumCfg.getUnlockScore(albumCfg.getItemId(),albumId);
			
			if (ownScore < totalScore) {
				player.sendError(protocol.getType(), Status.error.UNLOCK_ALBUM_LIMIT_ERROR);
				return;
			}
						
			smEntity.addUnlockCfgId(albumId);
			smEntity.notifyUpdate(true);
			
//			reward = albumCfg.getReward();
//			
//			AwardItems awards = AwardItems.valueOf(reward);
//			awards.rewardTakeAffectAndPush(player, Action.SECRET_MESSAGE, 0,TapDBSource.Secret_Message,Params.valueOf("albumId", albumId),
//					Params.valueOf("ownScore", ownScore),
//					Params.valueOf("reward", reward));
			
			
//			BehaviorLogger.log4Service(player, Source.USER_OPERATION, Action.SECRET_MESSAGE,Params.valueOf("albumId", albumId),
//					Params.valueOf("ownScore", ownScore),
//					Params.valueOf("reward", reward));
		} else if (action == 4) { // 領取通行證獎勵
			int cfgId = req.getUnlockPic();
			
			SecretAlbumCfg cfg = ConfigManager.getInstance().getConfigByKey(SecretAlbumCfg.class, cfgId);
			
			if (cfg == null) {
				player.sendError(protocol.getType(), Status.error.CONFIG_NOT_FOUND_VALUE);
				return;
			}
			
			SecretMsgEntity smEntity = player.getPlayerData().getSecretMsgByItem(cfg.getItemId());
			
			if (smEntity == null) {
				player.sendError(protocol.getType(), Status.error.DATA_NOT_FOUND_VALUE);
				return;
			}
			
			if ((smEntity.getFreeCfgId().contains(cfgId))&&(smEntity.getCostCfgId().contains(cfgId))) {
				player.sendError(protocol.getType(), Status.error.PARAMS_INVALID_VALUE);
				return;
			}
			
			if (!smEntity.getUnlockCfgId().contains(cfgId)) {
				// 未解鎖照片
				player.sendError(protocol.getType(), Status.error.PARAMS_INVALID_VALUE);
				return;
			}
			
			SignEntity signEntity = player.getPlayerData().getSignEntity();
			
			if (signEntity  == null) {
				player.sendError(protocol.getType(), Status.error.PARAMS_INVALID_VALUE);
				return;
			}
			
			reward = "";
			boolean free = (!smEntity.getFreeCfgId().contains(cfgId));
			boolean cost = (!smEntity.getCostCfgId().contains(cfgId)) && (signEntity.isSign(GsConst.SignMark.SecretMsgPass));
			
			if (free){
				smEntity.addFreeCfgId(cfgId);
				reward = cfg.getReward();
			}
			
			if (cost){
				smEntity.addCostCfgId(cfgId);
				if (reward.isEmpty()) {
					reward = cfg.getCost_reward();
				} else {
					reward = reward+","+cfg.getCost_reward();
				}
			}
			smEntity.notifyUpdate();		
			
			// 下发奖励
			AwardItems awards = AwardItems.valueOf(reward);
			awards.rewardTakeAffectAndPush(player, Action.SECRET_MESSAGE, 2,TapDBSource.Secret_Message,
					Params.valueOf("cfgId", cfgId),
					Params.valueOf("free", free),
					Params.valueOf("cost", cost));
			
		} else {
			player.sendError(protocol.getType(), Status.error.PARAMS_INVALID_VALUE);
			return;
		}
		
		// 返回訊息
		secretMsgResponse.Builder res = secretMsgResponse.newBuilder();
		RecoverScretPower();
		res.setAction(action);
		if (action == 2) {
			// 解鎖照片
			res.setSyncMsg(BuilderUtil.genSercretMsgInfoBuilders(player));
			//res.setAwards(reward); // 附加解鎖獲得獎勵
		} else if(action == 3) {
			// 僅同步體力
			res.setSyncMsg(BuilderUtil.genSercretMsgPowerBuilders(player));
		} else if(action == 4) {
			// 領通行證
			res.setSyncMsg(BuilderUtil.genSercretMsgInfoBuilders(player));
			res.setAwards(reward); // 附加解鎖獲得獎勵
		} else {
			res.setSyncMsg(BuilderUtil.genSercretMsgInfoBuilders(player));
		}
		sendProtocol(Protocol.valueOf(HP.code.SECRET_MESSAGE_ACTION_S_VALUE, res));
	}
	/**
	 * 發送秘密信條
	 */
//	private void sendSecretMsg() {
//		int lastMsgTime = player.getPlayerData().getStateEntity().getLastMsgTime();
//		VipPrivilegeCfg vipcfg = ConfigManager.getInstance().getConfigByKey(VipPrivilegeCfg.class, player.getVipLevel());
//		if (vipcfg == null) {
//			return;
//		}
//		
//		if (PlayerUtil.getSecretMsgCount(player.getPlayerData().getStateEntity().getSecretMsgList()) 
//				>= vipcfg.getMessageLimit()) {
//			return;
//		}
//		
//		List<Integer> heroList = player.getPlayerData().getCanSecretMsgHero();
//		
//		if (heroList.size() <= 0) {
//			return;
//		}
//		int msgId = 0;
//		if (GuaJiTime.getSeconds() - lastMsgTime >= vipcfg.getMessageCD()) {
//			lastMsgTime = GuaJiTime.getSeconds();
//			player.getPlayerData().getStateEntity().setLastMsgTime(lastMsgTime);
//			List<Integer> WaitMsg = player.getPlayerData().getStateEntity().getSecretMsgList();
//			List<Integer> completeMsgId = player.getPlayerData().getCompleteMsgId();
//			msgId = SecretMsgCfg.RandomSecretMsgId(heroList, WaitMsg, completeMsgId);
//			if (msgId > 0) {
//				player.getPlayerData().getStateEntity().addSecretMsgId(msgId);
//				player.getPlayerData().getStateEntity().notifyUpdate();
//				player.getPlayerData().syncSecretMsgInfo();
//			}
//		}
//	}
	
	/**
	 * 
	 */
	private void RecoverScretPower() {
		int recoverTime = player.getPlayerData().getStateEntity().getLastRecoverTime();
		int power = player.getPlayerData().getStateEntity().getSecretPower();
		
		int currTime = GuaJiTime.getSeconds();
		
		if (recoverTime == 0) {
			return;
		}
		
		int calsec = currTime - recoverTime;
		int incPower = 0;
		
		if (calsec >= SysBasicCfg.getInstance().getSecretRecoverTime()) {
			if (power < player.getSecretMaxPower()) {
				incPower = (calsec / SysBasicCfg.getInstance().getSecretRecoverTime())*player.getRecoverPower();
				int decsec = (calsec % SysBasicCfg.getInstance().getSecretRecoverTime());
				player.getPlayerData().getStateEntity().setLastRecoverTime(currTime-decsec);
				player.getPlayerData().getStateEntity().setSecretPower(Math.min(player.getSecretMaxPower(),power+incPower));
				player.getPlayerData().getStateEntity().notifyUpdate();
			} else {
				// fix max power
				if (power > player.getSecretMaxPower()) {
					player.getPlayerData().getStateEntity().setSecretPower(player.getSecretMaxPower());
				}
				player.getPlayerData().getStateEntity().setLastRecoverTime(0);
			}
			
		}
	}
	/**
	 * 處理英雄覺醒
	 * @param protocol
	 */
	private void onHandleHeroAwake(Protocol protocol) {
		HPRoleAwake upStage = protocol.parseProtocol(HPRoleAwake.getDefaultInstance());
		int roleId = upStage.getRoleId();

		// 獲取欲突破的英雄
		RoleEntity roleEntity = player.getPlayerData().getMercenaryById(roleId);
		if (roleEntity == null) {
			sendError(protocol.getType(), Status.error.ROLE_NOT_FOUND_VALUE);
			return ;
		}
		
		// 佣兵未激活
		if (roleEntity.getRoleState() == Const.RoleActiviteState.NOT_ACTIVITE_VALUE) {
			sendError(protocol.getType(), Status.error.PARAMS_INVALID);
			return ;
		}
		
		if (!roleEntity.isHero()) {
			sendError(protocol.getType(), Status.error.PARAMS_INVALID);
			return ;
		}
					
//		if (roleEntity.getStarLevel() >= HeroStarCfg.getHeroMaxStar(roleEntity.getItemId())) {
//			// 已经到突破最高级了
//			sendError(protocol.getType(), Status.error.ROLE_MAX_RANK);
//			return;
//		}
		
		HeroAwakeCfg cfg = ConfigManager.getInstance().getConfigByKey(HeroAwakeCfg.class,roleEntity.getStageLevel2()+1);
		
		if (cfg == null) {
			//找不到配置表
			sendError(protocol.getType(), Status.error.CONFIG_NOT_FOUND_VALUE);
			return ;
		}
		
//		if (cfg.getCost() <= 0) {
//			sendError(HP.code.ROLE_UPGRADE_STAR_C_VALUE, Status.error.CONFIG_ERROR_VALUE);
//			return false;
//		}
		

		
		ItemInfo needItem = ItemInfo.valueOf(cfg.getCost());
		if (needItem != null) {
			ConsumeItems consumeItems = ConsumeItems.valueOf();
			boolean isAdd = consumeItems.addConsumeInfo(player.getPlayerData(),needItem);
			if (!isAdd || !consumeItems.checkConsume(player) || !consumeItems.consumeTakeAffect(player, Action.HERO_AWAKE)) {
				sendError(protocol.getType(), Status.error.ITEM_NOT_ENOUGH_VALUE);
				return ;
			}
		}
		
		if (cfg.getShard() > 0) {
			RoleRelatedCfg cfgR = ConfigManager.getInstance().getConfigByKey(RoleRelatedCfg.class, roleEntity.getItemId());
			if (cfgR != null) {
				ItemInfo soulItem = ItemInfo.valueOf(cfgR.getExchange());
				ConsumeItems consumeItems = ConsumeItems.valueOf();
				boolean isAdd = consumeItems.addConsumeInfo(player.getPlayerData(),soulItem);
				if (!isAdd || !consumeItems.checkConsume(player) || !consumeItems.consumeTakeAffect(player, Action.HERO_AWAKE)) {
					sendError(protocol.getType(), Status.error.ITEM_NOT_ENOUGH_VALUE);
					return ;
				}
			} else {
				sendError(protocol.getType(), Status.error.CONFIG_NOT_FOUND_VALUE);
			}
		}
			
		int oldAwakeLv = roleEntity.getStageLevel2();
		roleEntity.setStageLevel2(oldAwakeLv+ 1);		

		// 刷新佣兵属性
		Attribute oldAttr = roleEntity.getAttribute().clone();
		PlayerUtil.refreshOnlineAttribute(player.getPlayerData(), roleEntity);

		Attribute newAttr = roleEntity.getAttribute().clone();
		PlayerUtil.pushAttrChange(player, oldAttr, newAttr);
		roleEntity.notifyUpdate();
		
		player.getPlayerData().syncRoleInfo(roleEntity.getId());
		
		HPRoleAwakeRes.Builder builder = HPRoleAwakeRes.newBuilder();
		builder.setRoleId(roleEntity.getId());
		builder.setNewLv(roleEntity.getStageLevel2());

		player.sendProtocol(
				Protocol.valueOf(HP.code.ROLE_AWAKE_S, builder));

		BehaviorLogger.log4Service(player, Source.USER_OPERATION, Action.HERO_AWAKE,
				Params.valueOf("roleId", roleEntity.getId()), Params.valueOf("roleItemId", roleEntity.getItemId()),
				Params.valueOf("oldLv", oldAwakeLv),
				Params.valueOf("newLv", roleEntity.getStageLevel2()));		
	}
}
