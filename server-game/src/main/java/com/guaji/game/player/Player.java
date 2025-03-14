package com.guaji.game.player;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.guaji.app.AppObj;
import org.guaji.app.ObjModule;
import org.guaji.config.ConfigManager;
import org.guaji.db.DBManager;
import org.guaji.log.Log;
import org.guaji.msg.Msg;
import org.guaji.net.protocol.Protocol;
import org.guaji.os.GuaJiTime;
import org.guaji.os.MyException;
import org.guaji.service.ServiceProxy;
//import org.guaji.util.services.ReportService;
import org.guaji.xid.GuaJiXID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.protobuf.ProtocolMessageEnum;
import com.guaji.game.GsApp;
import com.guaji.game.GsConfig;
import com.guaji.game.config.ActivityCfg;
import com.guaji.game.config.ActivityTimeCfg;
import com.guaji.game.config.DailyQuestPointCfg;
import com.guaji.game.config.DiamondVIPCfg;
import com.guaji.game.config.EquipCfg;
import com.guaji.game.config.FailedGift177Cfg;
import com.guaji.game.config.FunctionUnlockCfg;
import com.guaji.game.config.GloryHoleDailyCfg;
import com.guaji.game.config.HaremExchangeCfg;
import com.guaji.game.config.ItemCfg;
import com.guaji.game.config.LevelExpCfg;
import com.guaji.game.config.LevelGiftAward132Cfg;
import com.guaji.game.config.RebirthStageCfg;
import com.guaji.game.config.ResetTimeCfg;
import com.guaji.game.config.RoleRelatedCfg;
import com.guaji.game.config.RoleSkinCfg;
import com.guaji.game.config.StageGiftAward151Cfg;
import com.guaji.game.config.SysBasicCfg;
import com.guaji.game.config.VipPrivilegeCfg;
import com.guaji.game.entity.ActivityEntity;
import com.guaji.game.entity.AllianceEntity;
import com.guaji.game.entity.ArenaShopEntity;
import com.guaji.game.entity.BadgeEntity;
import com.guaji.game.entity.CrossShopEntity;
import com.guaji.game.entity.CrystalShopEntity;
import com.guaji.game.entity.DailyQuestEntity;
import com.guaji.game.entity.DailyQuestItem;
import com.guaji.game.entity.DailyShopEntity;
import com.guaji.game.entity.EighteenPrincesEntity;
import com.guaji.game.entity.ElementEntity;
import com.guaji.game.entity.EquipEntity;
import com.guaji.game.entity.FriendEntity;
import com.guaji.game.entity.GodSeaShopEntity;
import com.guaji.game.entity.GuildShopEntity;
import com.guaji.game.entity.HaremActivityEntity;
import com.guaji.game.entity.HeroTokenTaskEntity;
import com.guaji.game.entity.HonorShopEntity;
import com.guaji.game.entity.ItemEntity;
import com.guaji.game.entity.MaidenEncounterEntity;
import com.guaji.game.entity.MercenaryExpeditionEntity;
import com.guaji.game.entity.MutualEntity;
import com.guaji.game.entity.MysteryShopEntity;
import com.guaji.game.entity.PlayerAllianceEntity;
import com.guaji.game.entity.PlayerEntity;
import com.guaji.game.entity.RaceShopEntity;
import com.guaji.game.entity.RoleEntity;
import com.guaji.game.entity.RoleSkinEntity;
import com.guaji.game.entity.ShopDiscountEntity;
import com.guaji.game.entity.ShopEntity;
import com.guaji.game.entity.StateEntity;
import com.guaji.game.entity.TempleShopEntity;
import com.guaji.game.entity.WeeklyQuestEntity;
import com.guaji.game.item.AwardItems;
import com.guaji.game.item.AwardItems.Item;
import com.guaji.game.log.BehaviorLogger;
import com.guaji.game.log.BehaviorLogger.Action;
import com.guaji.game.log.BehaviorLogger.Params;
import com.guaji.game.log.BehaviorLogger.Source;
import com.guaji.game.manager.AllianceManager;
import com.guaji.game.manager.MailManager;
import com.guaji.game.manager.shop.strategy.ArenaMarket;
import com.guaji.game.manager.shop.strategy.CrossMarket;
//import com.guaji.game.manager.EighteenPrincesManager;
import com.guaji.game.manager.shop.strategy.CrystalMarkey;
import com.guaji.game.manager.shop.strategy.DailyMarket;
import com.guaji.game.manager.shop.strategy.GodSeaMarket;
import com.guaji.game.manager.shop.strategy.GuildMarket;
import com.guaji.game.manager.shop.strategy.RaceMarket;
import com.guaji.game.manager.shop.strategy.TempleMarket;
import com.guaji.game.module.PlayerAccountBoundModule;
import com.guaji.game.module.PlayerActivityModule;
import com.guaji.game.module.PlayerAllianceBattleModule;
import com.guaji.game.module.PlayerAllianceModule;
import com.guaji.game.module.PlayerArchiveModule;
import com.guaji.game.module.PlayerArenaModule;
import com.guaji.game.module.PlayerBadgeModule;
import com.guaji.game.module.PlayerBattleModule;
import com.guaji.game.module.PlayerBindPriceModule;
import com.guaji.game.module.PlayerBulletinModule;
import com.guaji.game.module.PlayerChatModule;
import com.guaji.game.module.PlayerCrossModule;
import com.guaji.game.module.PlayerCycleStageModule;
import com.guaji.game.module.PlayerDailyQuestModule;
import com.guaji.game.module.PlayerDungeonModule;
import com.guaji.game.module.PlayerEighteenPrincesModule;
import com.guaji.game.module.PlayerElementModule;
import com.guaji.game.module.PlayerEquipModule;
//import com.guaji.game.module.PlayerFaceBookShareModule;
import com.guaji.game.module.PlayerFormationModule;
import com.guaji.game.module.PlayerFriendModule;
import com.guaji.game.module.PlayerGSMatchModule;
import com.guaji.game.module.PlayerGuideModule;
import com.guaji.game.module.PlayerGuildBuffModule;
import com.guaji.game.module.PlayerGvgModule;
import com.guaji.game.module.PlayerHeroTokenTaskModule;
import com.guaji.game.module.PlayerIdleModule;
import com.guaji.game.module.PlayerItemModule;
import com.guaji.game.module.PlayerLoginModule;
import com.guaji.game.module.PlayerMailModule;
import com.guaji.game.module.PlayerMercenaryExpeditionModule;
import com.guaji.game.module.PlayerMottoModule;
import com.guaji.game.module.PlayerMsgModule;
import com.guaji.game.module.PlayerMultiEliteModule;
import com.guaji.game.module.PlayerProfRankModule;
import com.guaji.game.module.PlayerRebirthModule;
import com.guaji.game.module.PlayerRewardModule;
import com.guaji.game.module.PlayerRoleModule;
import com.guaji.game.module.PlayerSeasonTowerModule;
import com.guaji.game.module.PlayerShopModule;
import com.guaji.game.module.PlayerSingleBossModule;
import com.guaji.game.module.PlayerSkillModule;
import com.guaji.game.module.PlayerStarSoulModule;
import com.guaji.game.module.PlayerStateModule;
import com.guaji.game.module.PlayerTitleModule;
import com.guaji.game.module.PlayerWeeklyQuestModule;
import com.guaji.game.module.PlayerWingModule;
import com.guaji.game.module.PlayerWorldBossModule;
import com.guaji.game.module.activity.ActiveCompliance.ActiveStatus;
import com.guaji.game.module.activity.activity132.Activity132Status;
import com.guaji.game.module.activity.activity147.Activity147WishingManager;
import com.guaji.game.module.activity.activity151.Activity151Status;
import com.guaji.game.module.activity.activity157.Activity157Handler;
import com.guaji.game.module.activity.activity175.Activity175Status;
import com.guaji.game.module.activity.activity177.Activity177Status;
import com.guaji.game.module.activity.discountGift.DiscountGiftData;
import com.guaji.game.module.activity.monthcard.MonthCardStatus;
import com.guaji.game.module.activity.timeLimit.PersonalTimeLimitStatus;
import com.guaji.game.module.activity.turntable.TurntableManager;
import com.guaji.game.module.activity.turntable.TurntableStatus;
import com.guaji.game.module.activity.vipwelfare.VipWelfareAwardHandler;
import com.guaji.game.module.activity.vipwelfare.VipWelfareStatus;
import com.guaji.game.module.quest.PlayerQuestModule;
import com.guaji.game.module.quest.QuestEventBus;
import com.guaji.game.module.sevendaylogin.PlayerSevenDayModule;
import com.guaji.game.module.sevendaylogin.SevenDayQuestEventBus;
import com.guaji.game.module.yaya.PlayerYYModule;
import com.guaji.game.protocol.Activity.HPRegisterCycleRet;
import com.guaji.game.protocol.Activity2.HPHaremScorePanelRes;
import com.guaji.game.protocol.Attribute.AttrInfo;
import com.guaji.game.protocol.Const;
import com.guaji.game.protocol.Const.QuestEventType;
import com.guaji.game.protocol.Const.SevenDayEventType;
import com.guaji.game.protocol.Const.playerAttr;
import com.guaji.game.protocol.Const.toolType;
import com.guaji.game.protocol.EighteenPrinces.HPSyncMedicalKitInfoRet;
import com.guaji.game.protocol.HP;
import com.guaji.game.protocol.Mail;
import com.guaji.game.protocol.Player.HPCommentMsgRet;
import com.guaji.game.protocol.Player.HPPlayerKickout;
import com.guaji.game.protocol.Player.HPPlayerRegisterDay;
import com.guaji.game.protocol.SysProtocol.HPErrorCode;
import com.guaji.game.util.ActivityUtil;
import com.guaji.game.util.AdjustEventUtil;
import com.guaji.game.util.BuilderUtil;
import com.guaji.game.util.ConfigUtil;
import com.guaji.game.util.ElementUtil;
import com.guaji.game.util.EquipUtil;
import com.guaji.game.util.FriendUtil;
import com.guaji.game.util.GJLocal;
import com.guaji.game.util.GameUtil;
import com.guaji.game.util.GsConst;
import com.guaji.game.util.PlayerUtil;
import com.guaji.game.util.TapDBUtil;
import com.guaji.game.util.TapDBUtil.TapDBSource;

/**
 * 玩家对象
 */
public class Player extends AppObj {
	/**
	 * 协议日志记录器
	 */
	private static final Logger logger = LoggerFactory.getLogger("Protocol");

	/**
	 * 挂载玩家数据管理集合
	 */
	private PlayerData playerData;

	/**
	 * 组装状态
	 */
	private boolean assembleFinish;
	/**
	 * 设备mac
	 */
	private String deviceMac = "020000000000";
	/**
	 * 设备路由信息
	 */
	private String routeInfo = "";
	/**
	 * 设备名
	 */
	private String deviceName = "iphone";

	/**
	 * 今天的充值额度
	 */
	private int todayRechargeNum = 0;
	
	/**
	 * 平台Id
	 */
	private int platformId = 0;

	/**
	 * 玩家接口处理成功
	 */
	public static final int PLAYER_INF_SUCCESS = 1;

	/**
	 * 玩家接口处理失败
	 */
	public static final int PLAYER_INF_FAIL = 0;

	/**
	 * 经验排行更新时间
	 */
	protected long tickTime = GuaJiTime.getMillisecond();

	/**
	 * 构造函数
	 *
	 * @param xid
	 */
	public Player(GuaJiXID xid) {
		super(xid);

		initModules();

		playerData = new PlayerData(this);
	}

	/**
	 * 初始化模块
	 */
	public void initModules() {
		registerModule(GsConst.ModuleType.STATE_MODULE, new PlayerStateModule(this));// 状态计时器先行，走今日首次登录检测
		registerModule(GsConst.ModuleType.LOGIN_MODULE, new PlayerLoginModule(this));
		// 任务模块放在最前头, 因为其他模块要抛事件给它
		registerModule(GsConst.ModuleType.QUEST_MODULE, new PlayerQuestModule(this));
		registerModule(GsConst.ModuleType.ITEM_MODULE, new PlayerItemModule(this));
		registerModule(GsConst.ModuleType.SKILL_MODULE, new PlayerSkillModule(this));
		registerModule(GsConst.ModuleType.EQUIP_MODULE, new PlayerEquipModule(this));
		registerModule(GsConst.ModuleType.BADGE_MODULE,new PlayerBadgeModule(this));
		registerModule(GsConst.ModuleType.ELEMENT, new PlayerElementModule(this));
		registerModule(GsConst.ModuleType.REBIRTH, new PlayerRebirthModule(this));
		registerModule(GsConst.ModuleType.WINGS, new PlayerWingModule(this));
		registerModule(GsConst.ModuleType.FORMATION, new PlayerFormationModule(this));
		registerModule(GsConst.ModuleType.STAR_SOUL_MODULE, new PlayerStarSoulModule(this));
		registerModule(GsConst.ModuleType.ARCHIVE_MODULE, new PlayerArchiveModule(this));
		registerModule(GsConst.ModuleType.ROLE_MODULE, new PlayerRoleModule(this));
		registerModule(GsConst.ModuleType.CHAT_MODULE, new PlayerChatModule(this));
		registerModule(GsConst.ModuleType.ALLIANCE_MODULE, new PlayerAllianceModule(this));
		registerModule(GsConst.ModuleType.ARENA_MODULE, new PlayerArenaModule(this));
		registerModule(GsConst.ModuleType.BATTLE_MODULE, new PlayerBattleModule(this));
		registerModule(GsConst.ModuleType.MSG_MODULE, new PlayerMsgModule(this));
		registerModule(GsConst.ModuleType.MAIL_MODULE, new PlayerMailModule(this));
		registerModule(GsConst.ModuleType.REWARD_MODULE, new PlayerRewardModule(this));
		registerModule(GsConst.ModuleType.ACTIVITY_MODULE, new PlayerActivityModule(this));
		registerModule(GsConst.ModuleType.FRIEND_MODULE, new PlayerFriendModule(this));
		registerModule(GsConst.ModuleType.SHOP_MODULE, new PlayerShopModule(this));
		registerModule(GsConst.ModuleType.PROFRANK_MODULE, new PlayerProfRankModule(this));
		registerModule(GsConst.ModuleType.TITLE_MODULE, new PlayerTitleModule(this));
		registerModule(GsConst.ModuleType.ALLIANCE_BATTLE_MODULE, new PlayerAllianceBattleModule(this));
		registerModule(GsConst.ModuleType.MULTI_ELITE_MODULE, new PlayerMultiEliteModule(this));
		registerModule(GsConst.ModuleType.DUNGEON_MODULE, new PlayerDungeonModule(this));
		registerModule(GsConst.ModuleType.WORLD_BOSS, new PlayerWorldBossModule(this));
		registerModule(GsConst.ModuleType.GLOBAL_SERVER, new PlayerGSMatchModule(this));
		registerModule(GsConst.ModuleType.ACCOUNT_BOUND, new PlayerAccountBoundModule(this));
		registerModule(GsConst.ModuleType.YAYASHOP, new PlayerYYModule(this));
		registerModule(GsConst.ModuleType.HERO_TOKEN_TASK, new PlayerHeroTokenTaskModule(this));
		registerModule(GsConst.ModuleType.GUIDE, new PlayerGuideModule(this));
		registerModule(GsConst.ModuleType.EXPEDITION_TASK, new PlayerMercenaryExpeditionModule(this));
		registerModule(GsConst.ModuleType.DAILY_QUEST, new PlayerDailyQuestModule(this));
		registerModule(GsConst.ModuleType.WEEKLY_QUEST, new PlayerWeeklyQuestModule(this));
//		registerModule(GsConst.ModuleType.FACEBOOK_SHARE_EVENT, new PlayerFaceBookShareModule(this));
		registerModule(GsConst.ModuleType.PLAYER_BIND_PRICE, new PlayerBindPriceModule(this));
		registerModule(GsConst.ModuleType.GVG_MODULE, new PlayerGvgModule(this));
		registerModule(GsConst.ModuleType.CROSS_SERVER, new PlayerCrossModule(this));
		registerModule(GsConst.ModuleType.BULLETIN_MODULE, new PlayerBulletinModule(this));

		registerModule(GsConst.ModuleType.ACC_LOGIN_SEVENDAY, new PlayerSevenDayModule(this));
		registerModule(GsConst.ModuleType.EIGHTEENPRINCES_MODULE, new PlayerEighteenPrincesModule(this));
		registerModule(GsConst.ModuleType.MOTTO_MODULE, new PlayerMottoModule(this));
		registerModule(GsConst.ModuleType.GUILDBUFF_MODULE, new PlayerGuildBuffModule(this));
		registerModule(GsConst.ModuleType.CYCLESTAGE_MODULE, new PlayerCycleStageModule(this));
		registerModule(GsConst.ModuleType.SINGLEBOSS_MODULE, new PlayerSingleBossModule(this));
		registerModule(GsConst.ModuleType.SEASONTOWER_MODULE, new PlayerSeasonTowerModule(this));
		// 最后注册空闲模块, 用来消息收尾处理（没问题，这里用来派发所有模块加载完成，link）
		registerModule(GsConst.ModuleType.IDLE_MODULE, new PlayerIdleModule(this));

	}

	/**
	 * 获取玩家数据
	 *
	 * @return
	 */
	public PlayerData getPlayerData() {
		return playerData;
	}

	/**
	 * 获取玩家实体
	 *
	 * @return
	 */
	public PlayerEntity getEntity() {
		return playerData.getPlayerEntity();
	}

	/**
	 * 是否组装完成
	 *
	 * @return
	 */
	public boolean isAssembleFinish() {
		return assembleFinish;
	}

	/**
	 * 设置组装完成状态
	 *
	 * @param assembleFinish
	 */
	public void setAssembleFinish(boolean assembleFinish) {
		this.assembleFinish = assembleFinish;
	}

	public String getDeviceMac() {
		return deviceMac;
	}

	public void setDeviceMac(String deviceMac) {
		this.deviceMac = deviceMac;
	}

	public String getRouteInfo() {
		return routeInfo;
	}

	public void setRouteInfo(String routeInfo) {
		this.routeInfo = routeInfo;
	}

	public String getDeviceName() {
		return deviceName;
	}

	public void setDeviceName(String deviceName) {
		this.deviceName = deviceName;
	}
	
	public int getPlatformId() {
		return platformId;
	}

	public void setPlatformId(int platformId) {
		this.platformId = platformId;
	}
	
	/**
	 * 是否首儲
	 * @return
	 */
	public boolean isFirstRecharge() {
		return getEntity().getRecharge() == 0 ? true : false;
	}

	/**
	 * 通知错误码
	 *
	 * @param errCode
	 */
	public void sendError(int hpCode, int errCode) {
		HPErrorCode.Builder builder = HPErrorCode.newBuilder();
		builder.setHpCode(hpCode);
		builder.setErrCode(errCode);
		sendProtocol(Protocol.valueOf(HP.sys.ERROR_CODE, builder));
	}

	/**
	 * 推送状态
	 *
	 * @param hpCode
	 * @param status
	 */
	public void sendStatus(int hpCode, int status) {
		HPErrorCode.Builder builder = HPErrorCode.newBuilder();
		builder.setHpCode(hpCode);
		builder.setErrCode(status);
		// 表示文字提示
		builder.setErrFlag(2);
		sendProtocol(Protocol.valueOf(HP.sys.ERROR_CODE, builder));
	}

	/**
	 * 推送状态文本
	 *
	 * @param hpCode
	 * @param status
	 */
	public void sendStatus(int hpCode, String msg) {
		HPErrorCode.Builder builder = HPErrorCode.newBuilder();
		builder.setHpCode(hpCode);
		builder.setErrMsg(msg);
		// 表示文字提示
		builder.setErrFlag(1);
		sendProtocol(Protocol.valueOf(HP.sys.ERROR_CODE, builder));
	}

	/**
	 * 通知错误码
	 *
	 * @param errCode
	 */
	public void sendError(int hpCode, int errCode, int errFlag) {
		HPErrorCode.Builder builder = HPErrorCode.newBuilder();
		builder.setHpCode(hpCode);
		builder.setErrCode(errCode);
		builder.setErrFlag(errFlag);
		sendProtocol(Protocol.valueOf(HP.sys.ERROR_CODE, builder));
	}

	/**
	 * 通知错误码
	 *
	 * @param errCode
	 */
	public void sendError(int hpCode, ProtocolMessageEnum errCode) {
		HPErrorCode.Builder builder = HPErrorCode.newBuilder();
		builder.setHpCode(hpCode);
		builder.setErrCode(errCode.getNumber());
		sendProtocol(Protocol.valueOf(HP.sys.ERROR_CODE, builder));
	}

	/**
	 * 发送协议
	 *
	 * @param protocol
	 * @return
	 */
	@Override
	public boolean sendProtocol(Protocol protocol) {
		if (protocol.getSize() >= 2048) {
			logger.info("send protocol size overflow, protocol: {}, size: {}",
					new Object[] { protocol.getType(), protocol.getSize() });
		}
		return super.sendProtocol(protocol);
	}

	/**
	 * 踢出玩家
	 *
	 * @param reason
	 */
	public void kickout(int reason) {
		HPPlayerKickout.Builder builder = HPPlayerKickout.newBuilder();
		builder.setReason(reason);
		sendProtocol(Protocol.valueOf(HP.code.PLAYER_KICKOUT_S, builder));
		if (session != null) {
			Log.logPrintln(String.format("player kickout setAppObject nul ip", session.getIpAddr()));
			session.setAppObject(null);
			session = null;
		}
	}

	/**
	 * 玩家消息预处理（响应登录，模块加载完成，关闭session消息）
	 *
	 * @param msg
	 * @return
	 */
	private boolean onPlayerMessage(Msg msg) {
		// 优先服务拦截
		if (ServiceProxy.onMessage(this, msg)) {
			return true;
		}

		// 系统级消息, 所有模块都进行处理的消息
		if (msg.getMsg() == GsConst.MsgType.PLAYER_LOGIN) {
			for (Entry<Integer, ObjModule> entry : objModules.entrySet()) {
				try {
					PlayerModule playerModule = (PlayerModule) entry.getValue();
					playerModule.onPlayerLogin();
				} catch (Exception e) {
					MyException.catchException(e);
				}

			}
			// 在线玩家快照刷新
			playerData.refreshOnlinePlayerSnapshot();
			return true;
		} else if (msg.getMsg() == GsConst.MsgType.PLAYER_ASSEMBLE) {
			for (Entry<Integer, ObjModule> entry : objModules.entrySet()) {
				try {
					PlayerModule playerModule = (PlayerModule) entry.getValue();
					playerModule.onPlayerAssemble();
				} catch (Exception e) {

					MyException.catchException(e);
				}

			}
			return true;
		} else if (msg.getMsg() == GsConst.MsgType.SESSION_CLOSED) {
			if (isAssembleFinish()) {
				for (Entry<Integer, ObjModule> entry : objModules.entrySet()) {
					try {
						PlayerModule playerModule = (PlayerModule) entry.getValue();
						playerModule.onPlayerLogout();
					} catch (Exception e) {
						MyException.catchException(e);
					}

				}
			}
			return true;
		}

		return false;
	}

	/**
	 * 玩家协议预处理
	 *
	 * @param protocol
	 * @return
	 */
	private boolean onPlayerProtocol(Protocol protocol) {
		// 优先服务拦截
		if (ServiceProxy.onProtocol(this, protocol)) {
			return true;
		}

		// 玩家不在线而且不是登陆协议(非法协议时机)
		if (!isOnline() && !protocol.checkType(HP.code.LOGIN_C)) {
			Log.errPrintln(String.format("player is offline, session: %s, protocol: %d",
					protocol.getSession().getIpAddr(), protocol.getType()));
			return true;
		}

		/**************** 注意，这块的注释是为了给测试使用.正式上线这块是需要打开的 ****************/
		// 玩家未组装完成
		if (!isAssembleFinish() && !protocol.checkType(HP.code.LOGIN_C) && !protocol.checkType(HP.code.ROLE_CREATE_C)) {
			Log.errPrintln(String.format("player assemble unfinish, session: %s, protocol: %d",
					protocol.getSession().getIpAddr(), protocol.getType()));
			return true;
		}

		return false;
	}

	/**
	 * 帧更新
	 */
	@Override
	public boolean onTick() {

		// 玩家离线或者未组装完成直接不走时钟tick机制
		if (!isOnline() || !isAssembleFinish()) {

			if (this.getXid().getId() == 22149)
				Log.errPrintln(String.format("player%d isOnline=%s isAssembleFinish=%s", this.getXid().getId(),
						String.valueOf(isOnline()), String.valueOf(isAssembleFinish())));

			return true;
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
		if (onPlayerMessage(msg)) {// player对象先对消息的响应（指的是登录登出消息）
			return true;
		}
		return super.onMessage(msg);// 其他消息响应
	}

	/**
	 * 协议响应
	 *
	 * @param protocol
	 * @return
	 */
	@Override
	public boolean onProtocol(Protocol protocol) {
		if (onPlayerProtocol(protocol)) {// player对象先处理的协议
			return true;
		}
		return super.onProtocol(protocol);
	}

	/**
	 * 获取玩家id
	 *
	 * @return
	 */
	public int getId() {
		return playerData.getPlayerEntity().getId();
	}

	/**
	 * 获取puid
	 *
	 * @return
	 */
	public String getPuid() {
		return playerData.getPlayerEntity().getPuid();
	}

	/**
	 * 获取serverId
	 *
	 * @return
	 */
	public int getServerId() {
		return playerData.getPlayerEntity().getServerId();
	}
	
	/**
	 * 獲取TapDB使用ID
	 * @return
	 */
	public String getTapDBUId() {
		long uid = (getServerId()*GsConst.TAPUID_BASE) + getId();
		return String.valueOf(uid);
	}

	/**
	 * 获取设备
	 *
	 * @return
	 */
	public String getDevice() {
		return playerData.getPlayerEntity().getDevice();
	}

	/**
	 * 获取平台
	 *
	 * @return
	 */
	public String getPlatform() {
		return playerData.getPlayerEntity().getPlatform();
	}

	/**
	 * 获取手机信息
	 *
	 * @return
	 */
	public String getPhoneInfo() {
		return playerData.getPlayerEntity().getPhoneInfo();
	}

	/**
	 * 获取钻石
	 *
	 * @return
	 */
	public int getGold() {
		return playerData.getPlayerEntity().getTotalGold();
	}

	/**
	 * 获取金币
	 *
	 * @return
	 */
	public long getCoin() {
		return playerData.getPlayerEntity().getCoin();
	}

	/**
	 * 获取金豆
	 *
	 * @return
	 */
	public int getGoldBean() {
		return playerData.getPlayerEntity().getGoldBean();
	}

	/**
	 * 获取玩家职业
	 *
	 * @return
	 */
	public int getProf() {
		try {
			return playerData.getMainRole().getProfession();
		} catch (Exception e) {
			MyException.catchException(e);
		}
		return 0;
	}

	/**
	 * 计算玩家战斗力
	 *
	 * @return
	 */
	public int calcFightValue() {
		try {
			return PlayerUtil.calcAllFightValue(this.getPlayerData());
		} catch (Exception e) {
			MyException.catchException(e);
		}
		return 0;
	}

	/**
	 * 获取玩家战斗力
	 *
	 * @return
	 */
	public int getFightValue() {
		return playerData.getPlayerEntity().getFightValue();
	}

	/**
	 * 获取玩家vip等级
	 *
	 * @return
	 */
	public int getVipLevel() {
		return playerData.getPlayerEntity().getVipLevel();
	}

	/**
	 * 获取玩家装备熔炼值
	 *
	 * @return
	 */
	public int getSmeltValue() {
		return playerData.getPlayerEntity().getSmeltValue();
	}

	/**
	 * 获取公会贡献
	 *
	 * @return
	 */
	public int getAllianceContribution() {
		return playerData.getPlayerAllianceEntity().getContribution();
	}

	/**
	 * boss挑战次数
	 *
	 * @return
	 */
	public int getBossFightTimes() {
		return playerData.getStateEntity().getBossFightTimes();
	}
	
	/**
	 * 多人副本挑战次数
	 *
	 * @return
	 */
	public int getMultiEliteTimes() {
		return playerData.getStateEntity().getMultiEliteTimes();
	}
	
	/**
	 * 玩家目前已通關關卡
	 *
	 * @return
	 */
	public int getPassMapId() {
		return playerData.getStateEntity().getPassMapId();
	}
	/**
	 * 玩家目前關卡
	 *
	 * @return
	 */
	public int getCurMapId() {
		return playerData.getStateEntity().getCurBattleMap();
	}

	/**
	 * 获取玩家名字
	 *
	 * @return
	 */
	public String getName() {
		return playerData.getPlayerEntity().getName();
	}

	/**
	 * 获取玩家等级
	 *
	 * @return
	 */
	public int getLevel() {
		if (playerData.getMainRole() != null) {
			return playerData.getMainRole().getLevel();
		}
		return 0;
	}

	/**
	 * 获取经验
	 *
	 * @return
	 */
	public long getExp() {
		return playerData.getPlayerEntity().getExp();
	}
	
	/**
	 * 获取塔樓層
	 *
	 * @return
	 */
	public int getTower() {
		return 0;
	}
	/**
	 * 獲取友情點數
	 * @return
	 */
	public int getFriendship() {
		return playerData.getStateEntity().getFriendship();
	}

	/**
	 * 获取转生阶段
	 *
	 * @return
	 */
	public int getRebirthStage() {
		return playerData.getPlayerEntity().getRebirthStage();
	}
	/**
	 * 取出秘密信條最大體力
	 * @return
	 */
	public int getSecretMaxPower() {
		VipPrivilegeCfg vipcfg = ConfigManager.getInstance().getConfigByKey(VipPrivilegeCfg.class,getVipLevel());
		if (vipcfg == null) {
			return SysBasicCfg.getInstance().getSecretMaxPower();
		}
		return Math.max(SysBasicCfg.getInstance().getSecretMaxPower(),vipcfg.getMaxPower());
	}
	
	/**
	 * 取出秘密信條回復體力
	 * @return
	 */
	public int getRecoverPower() {
		VipPrivilegeCfg vipcfg = ConfigManager.getInstance().getConfigByKey(VipPrivilegeCfg.class,getVipLevel());
		if (vipcfg == null) {
			return SysBasicCfg.getInstance().getSecretRecoverPower();
		}
		return (SysBasicCfg.getInstance().getSecretRecoverPower() + vipcfg.getRecoverPower());
	}

	/**
	 * 获取会话ip地址
	 *
	 * @return
	 */
	public String getIp() {
		if (session != null) {
			return session.getIpAddr();
		}
		return null;
	}

	/**
	 * 等级提升操作, 开启佣兵和所有角色技能
	 */
	public void notifyLevelUp(int oldLevel, int curLevel) {
		if (curLevel <= oldLevel) {
			return;
		}

//		List<FormationEntity> formationEntities = playerData.getFromationEntities();

//		SysBasicCfg sysCfg = SysBasicCfg.getInstance();

		// 解锁出战格子
		for (int i = oldLevel + 1; i <= curLevel; i++) {
			ActivityUtil.onPlayerLevelUp(playerData, i);
			
//			if (sysCfg.getLevelOpenCount(i) == 0) {
//				continue;
//			}
//
//			int extendCount = sysCfg.getLevelOpenCount(i) - playerData.getFormationByType(1).getFightingArrayBoxCount();
//
//			if (extendCount > 0) {
//				for (FormationEntity formationEntity : formationEntities) {
//					formationEntity.addFinghtingArrayBox(extendCount);
//					formationEntity.notifyUpdate();
//				}
//				Msg hawkMsg = Msg.valueOf(GsConst.MsgType.FORMATION_MODIFY,
//						GuaJiXID.valueOf(GsConst.ObjType.PLAYER, this.getId()));
//				hawkMsg.pushParam(1);
//				GsApp.getInstance().postMsg(hawkMsg);
//				continue;
//			}
		}

		// 如果出战达到最大值解锁应援格子
//		if (playerData.getFormationByType(1).getFightingArrayBoxCount() == sysCfg.getMaxOpenCount()) {
//			for (int i = oldLevel + 1; i <= curLevel; i++) {
//				if (sysCfg.getLevelAssistanceOpenCount(i) == 0) {
//					continue;
//				}
//
//				int extendCount = sysCfg.getLevelAssistanceOpenCount(i)
//						- playerData.getFormationByType(1).getAssistanceArrayBoxCount();
//
//				if (extendCount > 0) {
//					for (FormationEntity formationEntity : formationEntities) {
//						formationEntity.addAssistanceArrayBox(extendCount);
//						formationEntity.notifyUpdate();
//					}
//					Msg hawkMsg = Msg.valueOf(GsConst.MsgType.FORMATION_MODIFY,
//							GuaJiXID.valueOf(GsConst.ObjType.PLAYER, this.getId()));
//					hawkMsg.pushParam(1);
//					GsApp.getInstance().postMsg(hawkMsg);
//					continue;
//				}
//			}
//		}

		// 职业躶体等级属性提升(属性更新操作必须放在开启新佣兵之前只需)
		List<RoleEntity> roleEntities = playerData.getRoleEntities();
		for (RoleEntity roleEntity : roleEntities) {
			// 属性角色整体属性
			PlayerUtil.refreshOnlineAttribute(this.playerData, roleEntity);
			// 通知db更新
			roleEntity.notifyUpdate(true);
			// 同步角色信息
			playerData.syncRoleInfo(roleEntity.getId());
		}

		// 激活新佣兵
//		Map<Object, RoleRelatedCfg> cfg = ConfigManager.getInstance().getConfigMap(RoleRelatedCfg.class);
//		if (cfg != null) {
//			for (RoleRelatedCfg relatedCfg : cfg.values()) {
//				if (relatedCfg.getCostType() == GsConst.RoleSoulExchangeType.ROLE_LEVEL) {
//					RoleEntity roleEntity = playerData.getMercenaryByItemId(relatedCfg.getId());
//					if (roleEntity != null) {
//						if (curLevel >= relatedCfg.getCostLevel()
//								&& roleEntity.getRoleState() == Const.RoleActiviteState.NOT_ACTIVITE_VALUE) {
//							Msg hawkMsg = Msg.valueOf(GsConst.MsgType.AUTO_EMPLOY_ROLE,
//									GuaJiXID.valueOf(GsConst.ObjType.PLAYER, this.getId()));
//							hawkMsg.pushParam(roleEntity.getItemId());
//							GsApp.getInstance().postMsg(hawkMsg);
//						}
//					}
//				}
//			}
//		}

		// 解锁新技能
//		roleEntities = playerData.getRoleEntities();
//		for (RoleEntity roleEntity : roleEntities) {
//			if (roleEntity.getType() == Const.roleType.MAIN_ROLE_VALUE) {
//				//RoleCfg roleCfg = roleEntity.getRoleCfg();
//				List<Integer> skillIds = roleEntity.getRoleCfg().getSkillList();
//				for (Integer skillId : skillIds) {
//					// 已学
//					if (playerData.getSkillByItemId(skillId) != null) {
//						continue;
//					}
//
//					// 等级条件达到
//					SkillCfg skillCfg = ConfigManager.getInstance().getConfigByKey(SkillCfg.class, skillId);
//					// 职业和等级限制满足要求
//					if (skillCfg != null && skillCfg.getLevelLimit() <= GameUtil.getRoleSkillLimitLevel(roleEntity)){
//						SkillEntity skillEntity = playerData.createSkill(roleEntity, skillId);
//						if (skillEntity != null) {
//							// 同步技能信息
//							playerData.syncSkillInfo(skillEntity.getId());
//							// 同步技能套装
//							SkillUtil.pushSkillInfo(this, roleEntity);
//						}
//					}
//				}
//			}
//		}

		// 通知等级升级任务
		QuestEventBus.fireQuestEvent(QuestEventType.MAIN_ROLE_LEVEL, curLevel, this.getXid());

		// 7日之诗提示等级提升
		SevenDayQuestEventBus.fireQuestEvent(SevenDayEventType.LEVELUP, curLevel, this.getXid());

		if (GJLocal.isLocal(GJLocal.R2)) {
			// 更新redis（tzy）
			FriendUtil.refreshFBFriendInfo(this);
		}
		if (oldLevel < 3 && curLevel >= 3) {
			// 新号上报数据
			//CmReportManager.getInstance().reportCmActivePlayer(this, false);
		}

		// 发送等级礼包红点
		triggerLevelGiftStart(); // 觸發禮包時間
		PlayerActivityModule activityModule = (PlayerActivityModule) this.getModule(GsConst.ModuleType.ACTIVITY_MODULE);
		if (activityModule != null) {
			activityModule.pushClientShowRedPointByID(Const.ActivityId.ACTIVITY132_LEVEL_GIFT_VALUE);
		}
		
	}
	
	/**
	 * 觸發等級禮包開始時間
	 */
	public void triggerLevelGiftStart() {
		
        int activityId = Const.ActivityId.ACTIVITY132_LEVEL_GIFT_VALUE;
        ActivityTimeCfg timeCfg = ActivityUtil.getCurActivityTimeCfg(activityId);
        if (timeCfg == null) {
            // 活动已关闭
            return;
        }
        ActivityCfg.ActivityItem activityItem = ActivityCfg.getActivityItem(activityId);
        if (activityItem == null) {
            // 活动已关闭
            return;
        }
        Activity132Status status = ActivityUtil.getActivityStatus(getPlayerData(), activityId,
                timeCfg.getStageId(), Activity132Status.class);
        if (status == null) {
            return;
        }
		Map<Object, LevelGiftAward132Cfg> cfgList = ConfigManager.getInstance().getConfigMap(LevelGiftAward132Cfg.class);
		for (LevelGiftAward132Cfg cfg : cfgList.values()) {
			if (getLevel() >= cfg.getMinLevel()&& getLevel()<= cfg.getMaxLevel()) {
				 if (status.getNowCfgId() != cfg.getId()) {
					 status.setNowCfgId(cfg.getId());
					 status.setLimitDate(GuaJiTime.getCalendar().getTime());
					 getPlayerData().updateActivity(activityId, timeCfg.getStageId());
				 }
			}
		}
	}
	
	/**
	 * 觸發關卡禮包開始時間
	 */
	public void triggerStageGiftStart() {
		
        int activityId = Const.ActivityId.ACTIVITY151_STAGE_GIFT_VALUE;
        ActivityTimeCfg timeCfg = ActivityUtil.getCurActivityTimeCfg(activityId);
        if (timeCfg == null) {
            // 活动已关闭
            return;
        }
        ActivityCfg.ActivityItem activityItem = ActivityCfg.getActivityItem(activityId);
        if (activityItem == null) {
            // 活动已关闭
            return;
        }
        Activity151Status status = ActivityUtil.getActivityStatus(getPlayerData(), activityId,
                timeCfg.getStageId(), Activity151Status.class);
        if (status == null) {
            return;
        }
		Map<Object, StageGiftAward151Cfg> cfgList = ConfigManager.getInstance().getConfigMap(StageGiftAward151Cfg.class);
		for (StageGiftAward151Cfg cfg : cfgList.values()) {
			if (getPassMapId() == cfg.getMinLevel()) {
				 status.setStartDate(GuaJiTime.getCalendar().getTime());
				 getPlayerData().updateActivity(activityId, timeCfg.getStageId());
			}
		}
	}
	
	/**
	 * 觸發失敗禮包開始時間
	 */
	public void triggerFailedGiftStart() {
		
        int activityId = Const.ActivityId.ACTIVITY177_Failed_Gift_VALUE;
        ActivityTimeCfg timeCfg = ActivityUtil.getCurActivityTimeCfg(activityId);
        if (timeCfg == null) {
            // 活动已关闭
            return;
        }
        ActivityCfg.ActivityItem activityItem = ActivityCfg.getActivityItem(activityId);
        if (activityItem == null) {
            // 活动已关闭
            return;
        }
        Activity177Status status = ActivityUtil.getActivityStatus(getPlayerData(), activityId,
                timeCfg.getStageId(), Activity177Status.class);
        if (status == null) {
            return;
        }
        
        boolean needSave = false;
        needSave = status.checkRestGift(); // 檢查並重置已觸發的禮包
        
        if (status.getCount() < SysBasicCfg.getInstance().getFailedGiftCount()) {
	        status.addCount();
	        needSave = true;
	        if (status.getCount() == SysBasicCfg.getInstance().getFailedGiftCount()) {
	        	FailedGift177Cfg triggerCfg=  FailedGift177Cfg.getCfgByCurMapId(getCurMapId());
	        	if (triggerCfg != null) {
	        		status.setTriggerDate(GuaJiTime.getCalendar().getTime());
	        		status.setTriggerCfgId(triggerCfg.getId());
	        		status.setIsbuy(false);
	        	}
	        }    
        }
        if (needSave) {
        	getPlayerData().updateActivity(activityId, timeCfg.getStageId());
        }
	}
		
	/**
	 * 通關, 觸發事件
	 */
	public void notifyMapPass(int oldMap, int curMap) {
		if (curMap <= oldMap) {
			return;
		}
		triggerStageGiftStart();
		PlayerActivityModule activityModule = (PlayerActivityModule) this.getModule(GsConst.ModuleType.ACTIVITY_MODULE);
		if (activityModule != null) {
			activityModule.pushClientShowRedPointByID(Const.ActivityId.ACTIVITY151_STAGE_GIFT_VALUE);
		}
		
		
		Integer checkMapId = FunctionUnlockCfg.getTypeValue(Const.FunctionType.Newbie_Unlock.getNumber()).get(1);
		
		if (checkMapId != null) {
			if (checkMapId == curMap) {
				playerData.getStateEntity().setNewbieDate(GuaJiTime.getCalendar().getTime());
				Msg msg = Msg.valueOf(GsConst.MsgType.ACTIVITY_LIST_CHANGE);
				GsApp.getInstance().postMsg(this.getXid(), msg);
			}
		}
	}
	/**
	 * 增加充值钻石
	 *
	 * @param gold
	 * @param action
	 */
	public void increaseRmbGold(int gold, Action action ,int goodsId) {
		if (gold < 0) {
			throw new RuntimeException("increaseRmbGold");
		}

		playerData.getPlayerEntity().setRmbGold(playerData.getPlayerEntity().getRmbGold() + gold);
		playerData.getPlayerEntity().notifyUpdate(true);
				
		BehaviorLogger.log4Service(this, Source.PLAYER_ATTR_CHANGE, action,
				Params.valueOf("playerAttr", Const.playerAttr.GOLD_VALUE), Params.valueOf("add", gold),
				Params.valueOf("after", getGold()));

		BehaviorLogger.log4Platform(this, action, Params.valueOf("playerAttr", Const.playerAttr.GOLD_VALUE),
				Params.valueOf("add", gold), Params.valueOf("after", getGold()));

		// 统一接口,便于统计
		BehaviorLogger.log4Platform(this, Action.GOLD_ADD, Params.valueOf("playerAttr", Const.playerAttr.GOLD_VALUE),
				Params.valueOf("add", gold), Params.valueOf("after", getGold()),
				Params.valueOf("source", action.name()));
		
		TapDBUtil.Event_GetItem(this, this.getTapDBUId(), action, TapDBSource.Recharge,(Const.itemType.PLAYER_ATTR_VALUE*GsConst.ITEM_TYPE_BASE), playerAttr.GOLD_VALUE,gold,
				Params.valueOf("goodsId", goodsId));
	}

	/**
	 * 增加钻石
	 *
	 * @param gold
	 * @param action
	 */
	public void increaseGold(int gold, Action action) {
		if (gold <= 0) {
			throw new RuntimeException("increaseGold");
		}

		playerData.getPlayerEntity().setSysGold(playerData.getPlayerEntity().getSysGold() + gold);
		playerData.getPlayerEntity().notifyUpdate(true);

		BehaviorLogger.log4Service(this, Source.PLAYER_ATTR_CHANGE, action,
				Params.valueOf("playerAttr", Const.playerAttr.GOLD_VALUE), Params.valueOf("add", gold),
				Params.valueOf("after", getGold()));

		BehaviorLogger.log4Platform(this, action, Params.valueOf("playerAttr", Const.playerAttr.GOLD_VALUE),
				Params.valueOf("add", gold), Params.valueOf("after", getGold()));

		// 统一接口,便于统计
		BehaviorLogger.log4Platform(this, Action.GOLD_ADD, Params.valueOf("playerAttr", Const.playerAttr.GOLD_VALUE),
				Params.valueOf("add", gold), Params.valueOf("after", getGold()),
				Params.valueOf("source", action.name()));
	}

	/**
	 * 消耗钻石
	 *
	 * @param gold
	 * @param action
	 */
	public void consumeGold(int gold, Action action) {
		if (gold <= 0 || gold > getGold()) {
			throw new RuntimeException("consumeGold");
		}

		PlayerEntity playerEntity = playerData.getPlayerEntity();
		StateEntity stateEntity = playerData.getStateEntity();
		int rmbGold = playerEntity.getRmbGold();
		if (gold <= rmbGold) {
			playerEntity.setRmbGold(rmbGold - gold);

			BehaviorLogger.log4Platform(this, Action.FINANCE_GOLD_COST, Params.valueOf("money", gold),
					Params.valueOf("wpnum", 1), Params.valueOf("price", gold), Params.valueOf("wpid", 0),
					Params.valueOf("wptype", action.name()));
		} else {
			playerEntity.setSysGold(playerEntity.getSysGold() - (gold - playerEntity.getRmbGold()));
			playerEntity.setRmbGold(0);

			BehaviorLogger.log4Platform(this, Action.FINANCE_GOLD_COST, Params.valueOf("money", rmbGold),
					Params.valueOf("wpnum", 1), Params.valueOf("price", gold), Params.valueOf("wpid", 0),
					Params.valueOf("wptype", action.name()));

			BehaviorLogger.log4Platform(this, Action.GOLD_COST, Params.valueOf("money", gold - rmbGold),
					Params.valueOf("wpnum", 1), Params.valueOf("price", gold), Params.valueOf("wpid", 0),
					Params.valueOf("wptype", action.name()));
		}

		playerData.getPlayerEntity().notifyUpdate(true);

		// 计入累计消费
		stateEntity.addAccConsumeGold(gold);
		stateEntity.notifyUpdate(true);
		ActivityUtil.addAccConsumeGold(playerData, gold);
		// 终身卡每日累计消费平台验证
		if (GsConfig.getInstance().getPlatform().equals(SysBasicCfg.getInstance().getPlatformForeverCard().trim())) {
			ActivityUtil.addPlayerTodayConsume(playerData, gold);
			ActivityUtil.foreverCardAvtivateHandler(playerData);
		}
		// 给客户端通知累计消费活动小红点
		PlayerActivityModule activityModule = (PlayerActivityModule) getModule(GsConst.ModuleType.ACTIVITY_MODULE);
		if (activityModule != null) {
			activityModule.pushClientShowRedPointByID(Const.ActivityId.ACCUMULATIVE_CONSUME_VALUE);
		}
		BehaviorLogger.log4Service(this, Source.PLAYER_ATTR_CHANGE, action,
				Params.valueOf("playerAttr", Const.playerAttr.GOLD_VALUE), Params.valueOf("sub", gold),
				Params.valueOf("after", getGold()));
	}

	/**
	 * 增加金币
	 *
	 * @param coin
	 * @param action
	 */
	public void increaseCoin(int coin, Action action) {
		if (coin <= 0) {
			throw new RuntimeException("increaseCoin");
		}

		playerData.getPlayerEntity().setCoin(playerData.getPlayerEntity().getCoin() + coin);
		playerData.getPlayerEntity().notifyUpdate(true);

		QuestEventBus.fireQuestEvent(QuestEventType.TOTAL_GAIN_COIN_COUNT, playerData.getPlayerEntity().getCoin(),
				this.getXid());

		BehaviorLogger.log4Service(this, Source.PLAYER_ATTR_CHANGE, action,
				Params.valueOf("playerAttr", Const.playerAttr.COIN_VALUE), Params.valueOf("add", coin),
				Params.valueOf("after", getCoin()));
	}

	/**
	 * 增加金豆
	 *
	 * @param goldBean
	 * @param action
	 */
	public void increaseGoldBean(int goldBean, Action action) {
		if (goldBean <= 0) {
			throw new RuntimeException("increaseGoldBean");
		}

		playerData.getPlayerEntity().setGoldBean(getGoldBean() + goldBean);
		playerData.getPlayerEntity().notifyUpdate(true);

		BehaviorLogger.log4Service(this, Source.PLAYER_ATTR_CHANGE, action,
				Params.valueOf("playerAttr", Const.playerAttr.GOLD_BEAN_VALUE), Params.valueOf("add", goldBean),
				Params.valueOf("after", getGoldBean()));

		BehaviorLogger.log4Platform(this, action, Params.valueOf("playerAttr", Const.playerAttr.GOLD_BEAN_VALUE),
				Params.valueOf("add", goldBean), Params.valueOf("after", getGoldBean()));
	}

	/**
	 * 消费金豆
	 *
	 * @param goldBean
	 * @param action
	 */
	public void consumeGoldBean(int goldBean, Action action) {
		if (goldBean <= 0 || goldBean > getGoldBean()) {
			throw new RuntimeException("consumeGoldBean");
		}

		playerData.getPlayerEntity().setGoldBean(getGoldBean() - goldBean);
		playerData.getPlayerEntity().notifyUpdate(true);

		BehaviorLogger.log4Service(this, Source.PLAYER_ATTR_CHANGE, action,
				Params.valueOf("playerAttr", Const.playerAttr.GOLD_BEAN_VALUE), Params.valueOf("sub", goldBean),
				Params.valueOf("after", getGoldBean()));

		BehaviorLogger.log4Platform(this, Action.GOLD_BEAN_COST, Params.valueOf("goldBean", goldBean),
				Params.valueOf("wpnum", 1), Params.valueOf("price", goldBean), Params.valueOf("wpid", 0),
				Params.valueOf("wptype", action.name()));
	}

	/**
	 * 增加公会贡献值
	 *
	 * @param value
	 * @param action
	 */
	public void increaseAllianceContribution(int value, Action action) {
		if (value <= 0) {
			throw new RuntimeException("increaseAllianceContribution");
		}

		if (getPlayerData().getPlayerAllianceEntity() != null) {
			getPlayerData().getPlayerAllianceEntity()
					.setContribution(getPlayerData().getPlayerAllianceEntity().getContribution() + value);
			playerData.getPlayerAllianceEntity().notifyUpdate(true);
		}
		BehaviorLogger.log4Service(this, Source.PLAYER_ATTR_CHANGE, action,
				Params.valueOf("playerAttr", Const.playerAttr.CONTRIBUTION), Params.valueOf("add", value),
				Params.valueOf("after", getPlayerData().getPlayerAllianceEntity().getContribution()));
	}

	/**
	 * 增加公会经验
	 *
	 * @param value
	 * @param action
	 */
	public void increaseAllianceExp(int value, Action action) {
		if (value <= 0) {
			throw new RuntimeException("increaseAllianceExp:" + value + ",action=" + action);
		}

		if (getPlayerData().getPlayerAllianceEntity() != null) {

			AllianceEntity allianceEntity = AllianceManager.getInstance()
					.getAlliance(getPlayerData().getPlayerAllianceEntity().getAllianceId());
			if (allianceEntity != null) {
				allianceEntity.setExp(allianceEntity.getExp() + value);
				AllianceManager.getInstance().checkAllianceLevelUp(allianceEntity);
				allianceEntity.notifyUpdate(false);
				BehaviorLogger.log4Service(this, Source.PLAYER_ATTR_CHANGE, action,
						Params.valueOf("playerAttr", Const.playerAttr.ALLIANCE_EXP), Params.valueOf("add", value),
						Params.valueOf("after", allianceEntity.getExp()));
			}
		}
	}

	/**
	 * 增加充值经验(用来提示vip等级)modify by callan for paymoney vip require
	 *
	 * @param coin
	 * @param playMoney
	 * @param action
	 */
	public void increaseRechargeGold(int goodsId, int gold,int addGold, float payMoney, Action action) {
		if (gold < 0 ) {
			throw new RuntimeException("increaseRechargeGold");
		}

		playerData.getPlayerEntity().setRecharge(playerData.getPlayerEntity().getRecharge() + gold);
		playerData.getPlayerEntity().setRechargeSoul(playerData.getPlayerEntity().getRechargeSoul() + gold);
		playerData.getPlayerEntity().setPayMoney(playerData.getPlayerEntity().getPayMoney() + payMoney);
		
		ActivityUtil.addAccConsume(playerData);
		
		// 儲值累加順便增加VIP點數
		//DiamondVIPCfg dcfg = ConfigManager.getInstance().getConfigByKey(DiamondVIPCfg.class, goodsId);
		//if (dcfg != null) {
		if (addGold > 0) {
			String awardStr = String.format("10000_1001_%d",addGold);
			//increaseVipPoint(dcfg.getGainVIP(),action);
			//awardStr = awardStr + "," + String.format("10000_1026_%d",dcfg.getGainVIP());
			playerData.setLastRecharage(awardStr);
		}
		
		BehaviorLogger.log4Service(this, Source.PLAYER_ATTR_CHANGE, action,
				Params.valueOf("goodsId",goodsId),
				Params.valueOf("playerAttr", Const.playerAttr.VIPLEVEL_VALUE),
				Params.valueOf("add", gold),
				Params.valueOf("after", playerData.getPlayerEntity().getRecharge()));
	}

	/**
	 * 消费金币
	 *
	 * @param coin
	 * @param action
	 */
	public void consumeCoin(long coin, Action action) {
		if (coin <= 0 || coin > getCoin()) {
			throw new RuntimeException("consumeCoin");
		}

		playerData.getPlayerEntity().setCoin(playerData.getPlayerEntity().getCoin() - coin);
		playerData.getPlayerEntity().notifyUpdate(true);

		BehaviorLogger.log4Service(this, Source.PLAYER_ATTR_CHANGE, action,
				Params.valueOf("playerAttr", Const.playerAttr.COIN_VALUE), Params.valueOf("sub", coin),
				Params.valueOf("after", getCoin()));

		BehaviorLogger.log4Platform(this, Action.COIN_COST, Params.valueOf("money", coin), Params.valueOf("wpnum", 1),
				Params.valueOf("price", coin), Params.valueOf("wpid", 0), Params.valueOf("wptype", action.name()));
	}

	/**
	 * 消耗熔炼值
	 *
	 * @param coin
	 * @param action
	 */
	public void consumeSmeltValue(int smeltValue, Action action) {
		if (smeltValue <= 0 || smeltValue > getSmeltValue()) {
			throw new RuntimeException("consumeSmeltValue");
		}

		playerData.getPlayerEntity().setSmeltValue(playerData.getPlayerEntity().getSmeltValue() - smeltValue);
		playerData.getPlayerEntity().notifyUpdate(true);

		BehaviorLogger.log4Service(this, Source.PLAYER_ATTR_CHANGE, action,
				Params.valueOf("playerAttr", Const.playerAttr.SMELT_VALUE), Params.valueOf("sub", smeltValue),
				Params.valueOf("after", getSmeltValue()));
	}

	/**
	 * 消耗贡献
	 *
	 * @param value
	 * @param action
	 */
	public void consumeContribution(int value, Action action) {
		if (value <= 0 || value > getCoin()) {
			throw new RuntimeException("contribution");
		}

		playerData.getPlayerAllianceEntity()
				.setContribution(playerData.getPlayerAllianceEntity().getContribution() - value);
		playerData.getPlayerAllianceEntity().notifyUpdate(true);

		BehaviorLogger.log4Service(this, Source.PLAYER_ATTR_CHANGE, action,
				Params.valueOf("playerAttr", Const.playerAttr.CONTRIBUTION_VALUE), Params.valueOf("sub", value),
				Params.valueOf("after", playerData.getPlayerAllianceEntity().getContribution()));
	}

	/**
	 * 增加等级
	 *
	 * @param level
	 */
	public void increaseLevel(int level, Action action) {
		if (level <= 0) {
			throw new RuntimeException("increaseLevel");
		}

		// 增加经验, 并判断是否升级
		RoleEntity mainRoleEntity = playerData.getMainRole();
		if (mainRoleEntity != null) {
			int oldLevel = mainRoleEntity.getLevel();
			int newLevel = mainRoleEntity.getLevel() + level;
			if (newLevel > LevelExpCfg.getMaxLevel()) {
				newLevel = LevelExpCfg.getMaxLevel();
			}

			// 转生数据
			int rebirthStage = this.getRebirthStage() + 1;
			RebirthStageCfg config = ConfigManager.getInstance().getConfigByKey(RebirthStageCfg.class, rebirthStage);
			// 新等级大于转生级且玩家未转生
			if (config != null && newLevel > config.getLevelLimit()) {
				newLevel = config.getLevelLimit();
			}

			mainRoleEntity.setLevel(newLevel);
			mainRoleEntity.notifyUpdate(true);

			playerData.getPlayerEntity().setLevel(newLevel);

			// Adjust 角色升级
			if (newLevel == 15 || newLevel == 30 || newLevel == 45 || newLevel == 60)
				AdjustEventUtil.sentAdjustEventInfo(this, String.format("level%d", newLevel), 0);

			// 升级后自动激活
			// 成长基金活动红点
			PlayerActivityModule.pushGrowthFundPoint(this);

			playerData.getPlayerEntity().notifyUpdate(true);

			notifyLevelUp(oldLevel, newLevel);

			BehaviorLogger.log4Service(this, Source.PLAYER_ATTR_CHANGE, action,
					Params.valueOf("playerAttr", Const.playerAttr.LEVEL_VALUE), Params.valueOf("add", level),
					Params.valueOf("after", newLevel));
		}
	}

	/**
	 * 增加vip等级
	 *
	 * @param level
	 */
	public void setVipLevel(int level, Action action) {
		if (level > VipPrivilegeCfg.getMaxVipLevel()) {
			level = VipPrivilegeCfg.getMaxVipLevel();
		}

		if (level < 0) {
			int newVipLevel = Math.max(0, playerData.getPlayerEntity().getVipLevel() + level);
			int oldVipLevel = playerData.getPlayerEntity().getVipLevel();
			playerData.getPlayerEntity().setVipLevel(newVipLevel);
			playerData.getPlayerEntity().notifyUpdate(true);
			// 成长基金活动红点
			if (oldVipLevel < 2 && playerData.getPlayerEntity().getVipLevel() >= 2) {
				PlayerActivityModule.pushGrowthFundPoint(this);
			}

			BehaviorLogger.log4Service(this, Source.PLAYER_ATTR_CHANGE, action,
					Params.valueOf("playerAttr", Const.playerAttr.VIPLEVEL_VALUE), Params.valueOf("sub", level),
					Params.valueOf("after", newVipLevel));

		} else if (level > 0) {
			playerData.getPlayerEntity().setVipLevel(level);
			playerData.getPlayerEntity().notifyUpdate(true);
			BehaviorLogger.log4Service(this, Source.PLAYER_ATTR_CHANGE, action,
					Params.valueOf("playerAttr", Const.playerAttr.VIPLEVEL_VALUE), Params.valueOf("add", level),
					Params.valueOf("after", level));
		}
	}

	/**
	 * 增加经验
	 *
	 * @param exp
	 */
	public void increaseExp(long exp, Action action) {

		if (exp <= 0) {
			throw new RuntimeException("increaseExp");
		}
		// 增加经验, 并判断是否升级
		RoleEntity mainRoleEntity = playerData.getMainRole();
		if (mainRoleEntity != null) {
			int oldLevel = Math.max(1, mainRoleEntity.getLevel());
			long newExp = mainRoleEntity.getExp() + exp;
			int level = oldLevel;
			// 转生数据
			int rebirthStage = this.getRebirthStage() + 1;
			RebirthStageCfg config = ConfigManager.getInstance().getConfigByKey(RebirthStageCfg.class, rebirthStage);
			// 经验不够升级
			for (; level < LevelExpCfg.getMaxLevel(); level++) {
				LevelExpCfg levelExpCfg = ConfigManager.getInstance().getConfigByKey(LevelExpCfg.class, level);
				if (levelExpCfg == null) {
					Log.errPrintln("cannot find level exp config, level: " + level);
					break;
				}
				// 达到转生前等级上限
				if (config != null && level >= config.getLevelLimit()) {
					break;
				}
				// 消耗经验
				long levelUpNeedExp = ConfigManager.getInstance().getConfigByKey(LevelExpCfg.class, level).getExp();
				if (newExp < levelUpNeedExp) {
					break;
				}
				newExp -= levelUpNeedExp;
			}

			mainRoleEntity.setExp(newExp);
			mainRoleEntity.setLevel(level);
			mainRoleEntity.notifyUpdate(true);

			playerData.getPlayerEntity().setExp(newExp);
			playerData.getPlayerEntity().setLevel(level);

			// 成长基金活动红点
			if (playerData.getPlayerEntity().getLevel() > oldLevel) {
				PlayerActivityModule.pushGrowthFundPoint(this);
			}
			playerData.getPlayerEntity().notifyUpdate(true);

			// 佣兵等级随主角改变
			if (level > oldLevel) {
				notifyLevelUp(oldLevel, level);
			}
			sendExpData();

			BehaviorLogger.log4Service(this, Source.PLAYER_ATTR_CHANGE, action,
					Params.valueOf("playerAttr", Const.playerAttr.EXP_VALUE), Params.valueOf("add", exp),
					Params.valueOf("after", newExp), Params.valueOf("oldLevel", oldLevel),
					Params.valueOf("newLevel", mainRoleEntity.getLevel()));
		}
	}

	/**
	 * 增加熔炼值
	 */
	public void increaseSmeltValue(int smeltValue, Action action) {
		if (smeltValue < 0) {
			throw new RuntimeException("increaseSmeltValue");
		}

		playerData.getPlayerEntity().setSmeltValue(playerData.getPlayerEntity().getSmeltValue() + smeltValue);
		playerData.getPlayerEntity().notifyUpdate(true);

		BehaviorLogger.log4Service(this, Source.PLAYER_ATTR_CHANGE, action,
				Params.valueOf("playerAttr", Const.playerAttr.SMELT_VALUE), Params.valueOf("add", smeltValue),
				Params.valueOf("after", getSmeltValue()));
	}

	/**
	 * 增加boss挑战次数
	 */
	public void increaseBossFightTimes(int bossFightTimes, Action action) {
		if (bossFightTimes < 0) {
			throw new RuntimeException("increaseBossFightTimes");
		}

		StateEntity stateEntity = playerData.getStateEntity();
		stateEntity.setBossFightTimes(stateEntity.getBossFightTimes() + bossFightTimes);
		stateEntity.notifyUpdate(true);

		BehaviorLogger.log4Service(this, Source.PLAYER_ATTR_CHANGE, action,
				Params.valueOf("playerAttr", Const.playerAttr.BOSS_TIMES), Params.valueOf("add", bossFightTimes),
				Params.valueOf("after", stateEntity.getBossFightTimes()));
	}
	
	/**
	 * 增加多人副本挑战次数
	 */
	public void increaseMultiEliteTimes(int MultiEliteTimes, Action action) {
		if (MultiEliteTimes < 0) {
			throw new RuntimeException("increaseMultiEliteTimes");
		}

		StateEntity stateEntity = playerData.getStateEntity();
		stateEntity.setMultiEliteTimes(stateEntity.getMultiEliteTimes() + MultiEliteTimes);
		stateEntity.notifyUpdate(true);

		BehaviorLogger.log4Service(this, Source.PLAYER_ATTR_CHANGE, action,
				Params.valueOf("playerAttr", Const.playerAttr.MULTI_ELITE_TIMES), Params.valueOf("add", MultiEliteTimes),
				Params.valueOf("after", stateEntity.getMultiEliteTimes()));
	}
	
	/**
	 * 增加友情點數
	 */
	public void increaseFriendship(int point, Action action) {
		if (point < 0) {
			throw new RuntimeException("increaseFriendship");
		}

		StateEntity stateEntity = playerData.getStateEntity();
		stateEntity.setFriendship(stateEntity.getFriendship() + point);
		stateEntity.notifyUpdate(true);
				
		BehaviorLogger.log4Service(this, Source.PLAYER_ATTR_CHANGE, action,
				Params.valueOf("playerAttr", Const.playerAttr.FRIENDSHIP_VALUE_VALUE), Params.valueOf("add", point),
				Params.valueOf("after", stateEntity.getFriendship()));
	}
	
	/**
	 * 增加VIP點數(NJG聲望)
	 */
	public void increaseVipPoint(int point, Action action) {
		if (point < 0) {
			throw new RuntimeException("increaseVipPoint");
		}

		StateEntity stateEntity = playerData.getStateEntity();
		stateEntity.setVipPoint(stateEntity.getVipPoint() + point);
		stateEntity.notifyUpdate(true);
		
		int oldVipLevel = playerData.getPlayerEntity().getVipLevel();
		playerData.getPlayerEntity()
		.setVipLevel(GameUtil.getVipLevelByRecharge(stateEntity.getVipPoint(),playerData.getPlayerEntity().getPlatform()));
		playerData.getPlayerEntity().notifyUpdate(true);
		
		ActivityUtil.addPalyerVipPoint(playerData,point); // 每日活動統計
		
		// 成长基金活动红点
		if (oldVipLevel < 2 && playerData.getPlayerEntity().getVipLevel() >= 2) {
			PlayerActivityModule.pushGrowthFundPoint(this);
		}
		
		playerData.syncStateInfo();
		
		if (oldVipLevel != playerData.getPlayerEntity().getVipLevel()){
			playerData.syncPlayerInfo();
		}
		
		QuestEventBus.fireQuestEvent(QuestEventType.VIP_LEVEL_UP, playerData.getPlayerEntity().getVipLevel(),
				this.getXid());

		BehaviorLogger.log4Service(this, Source.PLAYER_ATTR_CHANGE, action,
				Params.valueOf("playerAttr", Const.playerAttr.VIPPOINT_VALUE), Params.valueOf("add", point),
				Params.valueOf("after", stateEntity.getVipPoint()),
				Params.valueOf("oldVipLevel", oldVipLevel),
				Params.valueOf("vipLevel", playerData.getPlayerEntity().getVipLevel()));
	}

	/**
	 * 增加boss挑战次数
	 */
	public void increaseEliteMapFightTimes(int eliteMapTimes, Action action) {
		if (eliteMapTimes < 0) {
			throw new RuntimeException("eliteMapTimes");
		}

		StateEntity stateEntity = playerData.getStateEntity();
		stateEntity.setEliteMapTimes(stateEntity.getEliteMapTimes() + eliteMapTimes);
		stateEntity.notifyUpdate(true);

		BehaviorLogger.log4Service(this, Source.PLAYER_ATTR_CHANGE, action,
				Params.valueOf("playerAttr", Const.playerAttr.ELITE_MAP_TIMES), Params.valueOf("add", eliteMapTimes),
				Params.valueOf("after", stateEntity.getEliteMapTimes()));
	}

	/**
	 * 增加多人副本积分
	 *
	 * @param eliteMapTimes
	 * @param action
	 */
	public void increaseMultiEliteScore(int score, Action action) {
		if (score < 0) {
			throw new RuntimeException("multiEliteScore");
		}

		StateEntity stateEntity = playerData.getStateEntity();
		stateEntity.setMultiEliteScore(stateEntity.getMultiEliteScore() + score);
		stateEntity.setMultiEliteHistoryScore(stateEntity.getMultiEliteHistoryScore() + score);
		stateEntity.notifyUpdate(true);

		BehaviorLogger.log4Service(this, Source.PLAYER_ATTR_CHANGE, action,
				Params.valueOf("playerAttr", Const.playerAttr.MULTI_ELITE_SCORE), Params.valueOf("add", score),
				Params.valueOf("after", stateEntity.getMultiEliteScore()));
	}

	/**
	 * 增加多人副本积分
	 *
	 * @param eliteMapTimes
	 * @param action
	 */
	public void increaseEighteenPrincesMedicalKit(int type, int count, Action action) {
		if (count < 0) {
			throw new RuntimeException("increaseEighteenprincesMedicalKit");
		}
		EighteenPrincesEntity eighteenPrincesEntity = playerData.getEighteenPrincesEntity();
		if (eighteenPrincesEntity == null) {
			return;
		}
		switch (type) {
		case Const.playerAttr.BIG_MEDICALKIT_VALUE: {
			eighteenPrincesEntity.setBigMedicalKit(eighteenPrincesEntity.getBigMedicalKit() + count);
			BehaviorLogger.log4Service(this, Source.PLAYER_ATTR_CHANGE, action,
					Params.valueOf("playerAttr", Const.playerAttr.BIG_MEDICALKIT), Params.valueOf("add", count),
					Params.valueOf("after", eighteenPrincesEntity.getBigMedicalKit()));
		}
			break;
		case Const.playerAttr.MIDLE_MEDICALKIT_VALUE: {
			eighteenPrincesEntity.setMidleMedicalKit(eighteenPrincesEntity.getMidleMedicalKit() + count);
			BehaviorLogger.log4Service(this, Source.PLAYER_ATTR_CHANGE, action,
					Params.valueOf("playerAttr", Const.playerAttr.MIDLE_MEDICALKIT), Params.valueOf("add", count),
					Params.valueOf("after", eighteenPrincesEntity.getMidleMedicalKit()));
		}
			break;
		case Const.playerAttr.SMALL_MEDICALKIT_VALUE: {
			eighteenPrincesEntity.setSmallMedicalKit(eighteenPrincesEntity.getSmallMedicalKit() + count);
			BehaviorLogger.log4Service(this, Source.PLAYER_ATTR_CHANGE, action,
					Params.valueOf("playerAttr", Const.playerAttr.SMALL_MEDICALKIT), Params.valueOf("add", count),
					Params.valueOf("after", eighteenPrincesEntity.getSmallMedicalKit()));
		}
			break;
		default: {
			return;
		}

		}
		eighteenPrincesEntity.notifyUpdate(true);

		HPSyncMedicalKitInfoRet.Builder builder = BuilderUtil.genSyncMedicalKitInfoRetBuilders(eighteenPrincesEntity);
		if (builder != null) {

			this.sendProtocol(Protocol.valueOf(HP.code.EIGHTEENPRINCES_MEDICALKIT_S_VALUE, builder));
		}

	}
	
	/**
	 * 增加英雄皮膚
	 *
	 * @param score
	 * @param action
	 */
	public void increaseRoleSkin(Item item, Action action) {
		RoleSkinEntity roleSkinEntity = playerData.loadRoleSkinEntity();
		if ((roleSkinEntity != null)&&!roleSkinEntity.haveSkin(item.getId())) {
			RoleSkinCfg cfg = ConfigManager.getInstance().getConfigByKey(RoleSkinCfg.class,item.getId());
			if (cfg != null) {
				roleSkinEntity.setSkinSet(item.getId());
				roleSkinEntity.notifyUpdate();
				List<RoleEntity> herolist = playerData.getRoleEntities();
				for (RoleEntity ahero : herolist) {
					PlayerUtil.refreshOnlineAttribute(playerData, ahero);
				}
				playerData.syncRoleInfo(0);
				BehaviorLogger.log4Service(this, Source.SKIN_ADD, action,
						Params.valueOf("skinId", item.getId()));
			}
		}
	}

	/**
	 * 增加佣兵魂魄
	 *
	 * @param score
	 * @param action
	 */
	@Deprecated
	public void increaseRoleSoul(List<Item> list, Item item, Action action) {
		// ID命名为佣兵ID
		RoleEntity entity = playerData.getMercenaryByItemId(item.getId());
		if (entity != null) {
			entity.setSoulCount((int)(entity.getSoulCount() + item.getCount()));
			RoleRelatedCfg cfg = ConfigManager.getInstance().getConfigByKey(RoleRelatedCfg.class, entity.getItemId());
			if (cfg != null) {
				// 超出魂魄，兑换成材料
				if (cfg.getCostType() == GsConst.RoleSoulExchangeType.ROLE_SOUL) {
					if (entity.getSoulCount() > cfg.getLimitCount()) {
						if (cfg.getExchange() != null || !cfg.getExchange().isEmpty()) {
							int sendCount = entity.getSoulCount() - cfg.getLimitCount();
							while (sendCount > 0) {
								sendCount--;
							}

							this.playerData.syncPlayerInfo();
							entity.setSoulCount(cfg.getLimitCount());
						}
					}
				}
			}
			entity.notifyUpdate();
		}
	}

	/**
	 * 消耗多人副本积分
	 *
	 * @param eliteMapTimes
	 * @param action
	 */
	public void consumeMultiEliteScore(int score, Action action) {
		if (score < 0) {
			throw new RuntimeException("multiEliteScore");
		}

		StateEntity stateEntity = playerData.getStateEntity();
		stateEntity.setMultiEliteScore(stateEntity.getMultiEliteScore() - score);
		stateEntity.notifyUpdate(true);

		BehaviorLogger.log4Service(this, Source.PLAYER_ATTR_CHANGE, action,
				Params.valueOf("playerAttr", Const.playerAttr.MULTI_ELITE_SCORE), Params.valueOf("sub", score),
				Params.valueOf("after", stateEntity.getMultiEliteScore()));
	}

	/**
	 * 增加物品
	 */
	public ItemEntity increaseTools(int itemId, long itemCount, Action action) {
		if (itemCount < 0) {
			throw new RuntimeException("consumeTools");
		}

		if (!ConfigUtil.check(Const.itemType.TOOL_VALUE, itemId)) {
			return null;
		}
		
		ItemCfg itCfg = ConfigManager.getInstance().getConfigByKey(ItemCfg.class, itemId);
		
		if (itCfg.getType() == Const.toolType.FAKE_ITEMS_VALUE) {
			// 假物品不用加
			return null;
		}
		
		ItemEntity itemEntity = playerData.getItemByItemId(itemId);
		if (itemEntity == null) {
			itemEntity = new ItemEntity();
			itemEntity.setItemId(itemId);
			itemEntity.setItemCount(itemCount);
			itemEntity.setPlayerId(getId());
			if (DBManager.getInstance().create(itemEntity)) {
				playerData.addItemEntity(itemEntity);
			}
		} else {
			itemEntity.setItemCount(itemEntity.getItemCount() + itemCount);
			itemEntity.notifyUpdate(true);
		}

		if (itemEntity.getId() > 0) {
			// 公测字获得的时间统计
			ItemCfg itemCfg = ConfigManager.getInstance().getConfigByKey(ItemCfg.class, itemId);
			if (itemCfg != null && itemCfg.getType() == toolType.WORDS_EXCHANGE_SPECIAL_VALUE) {
//				StateEntity stateEntity = playerData.getStateEntity();
//				if (stateEntity != null && stateEntity.getGongceWordDay() == null) {
//					Date completeDate = playerData.getGongceCompleteDate();
//					if (completeDate != null) {
//						stateEntity.setGongceWordDay(completeDate);
//						stateEntity.notifyUpdate(true);
//						playerData.syncStateInfo();
//					}
//				}
			}

			BehaviorLogger.log4Service(this, Source.TOOLS_ADD, action, Params.valueOf("itemId", itemId),
					Params.valueOf("id", itemEntity.getId()), Params.valueOf("add", itemCount),
					Params.valueOf("after", itemEntity.getItemCount()));

			return itemEntity;
		}
		return null;
	}

	/**
	 * 消耗物品
	 */
	public ItemEntity consumeTools(int itemId, long itemCount, Action action) {
		if (itemCount < 0) {
			throw new RuntimeException("consumeTools");
		}

		ItemEntity itemEntity = playerData.getItemByItemId(itemId);
		if (itemEntity != null && itemEntity.getItemCount() >= itemCount) {
			long beforeCount = itemEntity.getItemCount();
			itemEntity.setItemCount(itemEntity.getItemCount() - itemCount);
			itemEntity.notifyUpdate(true);

			BehaviorLogger.log4Service(this, Source.TOOLS_REMOVE, action, Params.valueOf("itemId", itemId),
					Params.valueOf("id", itemEntity.getId()), Params.valueOf("sub", itemCount),
					Params.valueOf("after", itemEntity.getItemCount()), Params.valueOf("before", beforeCount));

			BehaviorLogger.log4Platform(this, action, Params.valueOf("itemId", itemId),
					Params.valueOf("id", itemEntity.getId()), Params.valueOf("sub", itemCount),
					Params.valueOf("after", itemEntity.getItemCount()), Params.valueOf("before", beforeCount));

			return itemEntity;
		}
		return null;
	}

	/**
	 * 增加装备
	 */
	public EquipEntity increaseEquip(int equipId, int godlyRate, Action action) {
		return increaseEquip(equipId, godlyRate, false, 0, false, action);
	}

	/**
	 * 增加装备
	 */
	public EquipEntity increaseEquip(int equipId, boolean isGodly1, boolean isGodly2, Action action) {
		return increaseEquip(equipId, isGodly1 ? 10000 : 0, isGodly2, 0, false, action);
	}

	/**
	 * 增加装备
	 */
	public EquipEntity increaseEquip(int equipId, int godlyRatio, boolean isSecondGodly, int punchSize,
			boolean isFullAttr, Action action) {
		if (!ConfigUtil.check(Const.itemType.EQUIP_VALUE, equipId)) {
			return null;
		}

		EquipEntity equipEntity = EquipUtil.generateEquip(this, equipId, godlyRatio, isSecondGodly, punchSize,
				isFullAttr);
		EquipUtil.refreshAttribute(equipEntity, this.getPlayerData());
		if (equipEntity != null) {
			if (DBManager.getInstance().create(equipEntity)) {
				
				playerData.addEquipEntity(equipEntity);
				
				EquipCfg equipCfg = ConfigManager.getInstance().getConfigByKey(EquipCfg.class, equipId);
				if (equipCfg.getSeries() != 0) { // 套裝生成紀錄星數
					MutualEntity mutualEntity = getPlayerData().getMutualEntity();
					mutualEntity.addStarMap(equipCfg.getSeries(), equipCfg.getStar());
					mutualEntity.notifyUpdate(true);
				}

				BehaviorLogger.log4Service(this, Source.EQUIP_ADD, action, Params.valueOf("equipId", equipId),
						Params.valueOf("id", equipEntity.getId()),
						Params.valueOf("attr", equipEntity.getAttribute().toString()),
						Params.valueOf("godlyAttrId", equipEntity.getGodlyAttrId()),
						Params.valueOf("godlyAttrId2", equipEntity.getGodlyAttrId2()));
				return equipEntity;
			}
		}
		return null;
	}

	/**
	 * 消耗装备
	 */
	public boolean consumeEquip(long id, Action action) {
		EquipEntity equipEntity = playerData.getEquipById(id);
		if (equipEntity != null) {
			playerData.removeEquipEntity(equipEntity);
			equipEntity.delete();

			BehaviorLogger.log4Service(this, Source.EQUIP_REMOVE, action,
					Params.valueOf("equipId", equipEntity.getEquipId()), Params.valueOf("id", equipEntity.getId()),
					Params.valueOf("attr", equipEntity.getAttribute().toString()));

			return true;
		}
		return false;
	}

	/**
	 * 消耗徽章
	 */
	public boolean consumeBadge(long id, Action action) {
		BadgeEntity badgeEntity = playerData.getBadgeById(id);
		if (badgeEntity != null) {
			playerData.removeBadgeEntity(badgeEntity);
			badgeEntity.delete();

			BehaviorLogger.log4Service(this, Source.BADGE_REMOVE, action,
					Params.valueOf("badgeId", badgeEntity.getBadgeId()), Params.valueOf("id", badgeEntity.getId()),
					Params.valueOf("attr", badgeEntity.getAttribute().toString()),
					Params.valueOf("skill", badgeEntity.getSkill()));

			return true;
		}
		return false;
	}

	/**
	 * 批量消耗装备
	 *
	 * @return 消耗失败的装备Id
	 */
	public List<Integer> consumeEquip(List<Integer> ids, Action action) {
		List<Integer> removeFailEquipIds = new LinkedList<>();
		for (Integer id : ids) {
			if (!consumeEquip(id, action)) {
				removeFailEquipIds.add(id);
			}
		}
		return removeFailEquipIds;
	}

	public void increaseHonorValue(int honor, Action action) {
		if (honor < 0) {
			throw new RuntimeException("increaseHonor");
		}

		playerData.getPlayerEntity().setHonorValue(playerData.getPlayerEntity().getHonorValue() + honor);
		playerData.getPlayerEntity().notifyUpdate(true);

		BehaviorLogger.log4Service(this, Source.PLAYER_ATTR_CHANGE, action,
				Params.valueOf("playerAttr", Const.playerAttr.HONOR_VALUE_VALUE), Params.valueOf("add", honor),
				Params.valueOf("after", playerData.getPlayerEntity().getHonorValue()));
	}

	public void consumeHonor(int honor, Action action) {
		if (honor <= 0 || honor > playerData.getPlayerEntity().getHonorValue()) {
			throw new RuntimeException("consumeHonor");
		}

		playerData.getPlayerEntity().setHonorValue(playerData.getPlayerEntity().getHonorValue() - honor);
		playerData.getPlayerEntity().notifyUpdate(true);

		BehaviorLogger.log4Service(this, Source.PLAYER_ATTR_CHANGE, action,
				Params.valueOf("playerAttr", Const.playerAttr.HONOR_VALUE_VALUE), Params.valueOf("sub", honor),
				Params.valueOf("after", playerData.getPlayerEntity().getHonorValue()));
	}
	/**
	 * 增加聲望
	 * @param reputation
	 * @param action
	 */
	public void increaseReputationValue(int reputation, Action action) {
		if (reputation < 0) {
			throw new RuntimeException("increaseReputation");
		}

		playerData.getPlayerEntity().setReputationValue(playerData.getPlayerEntity().getReputationValue() + reputation);
		playerData.getPlayerEntity().notifyUpdate(true);

		BehaviorLogger.log4Service(this, Source.PLAYER_ATTR_CHANGE, action,
				Params.valueOf("playerAttr", Const.playerAttr.REPUTATION_VALUE_VALUE),
				Params.valueOf("add", reputation),
				Params.valueOf("after", playerData.getPlayerEntity().getReputationValue()));
	}
	/**
	 * 消耗聲望
	 * @param reputation
	 * @param action
	 */
	public void consumeReputationValue(int reputation, Action action) {
		if (reputation <= 0 || reputation > playerData.getPlayerEntity().getReputationValue()) {
			throw new RuntimeException("consumeReputation");
		}

		playerData.getPlayerEntity().setReputationValue(playerData.getPlayerEntity().getReputationValue() - reputation);
		playerData.getPlayerEntity().notifyUpdate(true);

		BehaviorLogger.log4Service(this, Source.PLAYER_ATTR_CHANGE, action,
				Params.valueOf("playerAttr", Const.playerAttr.REPUTATION_VALUE_VALUE),
				Params.valueOf("sub", reputation),
				Params.valueOf("after", playerData.getPlayerEntity().getReputationValue()));
	}

	public void increaseCrystalValue(int crystal, Action action) {
		if (crystal < 0) {
			throw new RuntimeException("increaseCrystal");
		}

		playerData.getPlayerEntity().setCrystalValue(playerData.getPlayerEntity().getCrystalValue() + crystal);
		playerData.getPlayerEntity().notifyUpdate(true);

		BehaviorLogger.log4Service(this, Source.PLAYER_ATTR_CHANGE, action,
				Params.valueOf("playerAttr", Const.playerAttr.REPUTATION_VALUE_VALUE), Params.valueOf("add", crystal),
				Params.valueOf("after", playerData.getPlayerEntity().getCrystalValue()));
	}

	public void consumeCrystalValue(int crystal, Action action) {
		if (crystal <= 0 || crystal > playerData.getPlayerEntity().getCrystalValue()) {
			throw new RuntimeException("consumeCrystal");
		}

		playerData.getPlayerEntity().setCrystalValue(playerData.getPlayerEntity().getCrystalValue() - crystal);
		playerData.getPlayerEntity().notifyUpdate(true);

		BehaviorLogger.log4Service(this, Source.PLAYER_ATTR_CHANGE, action,
				Params.valueOf("playerAttr", Const.playerAttr.CRYSTAL_VALUE_VALUE), Params.valueOf("sub", crystal),
				Params.valueOf("after", playerData.getPlayerEntity().getCrystalValue()));
	}
	
	public void consumeFriendship(int value, Action action) {
		if (value <= 0 || value > playerData.getStateEntity().getFriendship()) {
			throw new RuntimeException("consumeCrystal");
		}

		playerData.getStateEntity().setFriendship(playerData.getStateEntity().getFriendship() - value);
		playerData.getStateEntity().notifyUpdate(true);

		BehaviorLogger.log4Service(this, Source.PLAYER_ATTR_CHANGE, action,
				Params.valueOf("playerAttr", Const.playerAttr.FRIENDSHIP_VALUE_VALUE), Params.valueOf("sub", value),
				Params.valueOf("after", playerData.getStateEntity().getFriendship()));
	}

	/**
	 * 	每日首次登陆
	 * @param sync (玩家線上掛跨日須同步)
	 */
	public void handleDailyFirstLogin(boolean sync) {
		pushRegisterCycle();
		StateEntity stateEntity = playerData.getStateEntity();
		
		stateEntity.incFirstLoginTimes();

		MonthCardStatus monthCardStatus = ActivityUtil.getMonthCardStatus(playerData);// 月卡本来写好了，又改东西，不想动了，直接在这里加

		HonorShopEntity hornorShopEntity = playerData.loadHonorShopEntity();
		if (hornorShopEntity != null) {
			hornorShopEntity.reset();
			hornorShopEntity.notifyUpdate(true);
		}

		CrystalShopEntity crystalShopEntity = playerData.loadCrystalShopEntity();
		if (crystalShopEntity != null) {
			crystalShopEntity.reset();
			crystalShopEntity.notifyUpdate(true);
		}
		
		DailyRefreshShop();
		
		WishingRefreshPool(); //activity147
		
		LoginTenDrawResetGot(); // activity157
		
		WeekRefreshShop(playerData.getPlayerEntity().getResetTime());
		
		ShopDiscountEntity shopDiscountEntity = playerData.loadShopDiscountEntity();
		if (shopDiscountEntity != null) {
			shopDiscountEntity.reset();
			shopDiscountEntity.notifyUpdate(true);
		}
		
		MysteryShopEntity mysteryShopEntity = playerData.loadMysteryShopEntity();
		if (mysteryShopEntity != null) {
			mysteryShopEntity.reset();
			mysteryShopEntity.notifyUpdate(true);
		}

		MercenaryExpeditionEntity mercenaryExpeditionEntity = playerData.loadMercenaryExpeditionEntity();
		if (mercenaryExpeditionEntity != null) {
			mercenaryExpeditionEntity.reset();
			mercenaryExpeditionEntity.notifyUpdate(true);
		}

		FriendEntity friendEntity = playerData.loadFriendEntity();
		if (friendEntity != null) {
			friendEntity.reset();
			friendEntity.notifyUpdate(true);
		}

		// 每日重置英雄令任务
		HeroTokenTaskEntity heroTokenTaskEntity = playerData.loadHeroTokenTaskEntity();
		if (heroTokenTaskEntity != null) {
			heroTokenTaskEntity.reset();
			heroTokenTaskEntity.notifyUpdate(true);
		}

		DailyQuestEntity dailyQuestEntity = playerData.loadDailyQuestEntity();
		if (dailyQuestEntity != null) {
			dailyQuestEntity.reset();
			dailyQuestEntity.reConvert();
			dailyQuestEntity.notifyUpdate(true);
		}
		
		// 週任務重置
		WeeklyQuestReset(playerData.getPlayerEntity().getResetTime());
		
		//給周任務登入一次
//		Msg hawkMsg = Msg.valueOf(GsConst.MsgType.DailyQuestMsg.LOGIN_DAY,
//				GuaJiXID.valueOf(GsConst.ObjType.PLAYER,getId()));
//		hawkMsg.pushParam(1);
//		GsApp.getInstance().postMsg(hawkMsg);
		
		// 新手八天送禮
		ActiveStatus activeStatus = ActivityUtil.getActiveComplianceStatus(playerData);
		if (activeStatus != null) {
			int activityId = Const.ActivityId.ACTIVECOMPLIANCE_VALUE;
			ActivityTimeCfg activityTimeCfg = ActivityUtil.getCurActivityTimeCfg(activityId);
			if (activityTimeCfg != null) {
				if (activeStatus.calcActivitySurplusTime() > 0) { 
					if (activeStatus.isIsfirst()) {
						activeStatus.setDays(activeStatus.getDays() + 1);
						activeStatus.setIsfirst(false);
						playerData.updateActivity(activityId, activityTimeCfg.getStageId());
					}
				}
			}
		}
		
		ActivityUtil.restActivity159Status(playerData);
		
		// 特權發放禮物
		ActivityUtil.getSubScriptionReward(playerData);
		
		// 重置壁尻每日任務
		//ActivityUtil.restGloryHoleDailyStatus(playerData);
		
		// 重置循環關卡每日任務
		ActivityUtil.restCycleStageDailyStatus(this);
		// 單人強敵重置
		ActivityUtil.restSingleBossDailyStatus(this);

		// 新服礼包每日领取次数
		stateEntity.setNewSerGiftRewardCount(0);

		Date curDate = GuaJiTime.getCalendar().getTime();
		HPPlayerRegisterDay.Builder regbuilder = HPPlayerRegisterDay.newBuilder();
		if (getPlayerData().getPlayerEntity().getCreateTime().getTime() > curDate.getTime()) {
			regbuilder.setRegisterDay(0);
		} else {
			regbuilder.setRegisterDay(
					GuaJiTime.calcBetweenDays(getPlayerData().getPlayerEntity().getCreateTime(), curDate) + 1);
		}

		this.sendProtocol(Protocol.valueOf(HP.code.PLAYER_REGISTERDAY_S, regbuilder));

		// YAYA兑换金豆消耗钻石数重置
		playerData.getPlayerEntity().setExchangeGoldBeanCostRmbGold(0);
		playerData.getPlayerEntity().setTodayRechargeNum(0);

		// 小时卡使用次数
		playerData.getStateEntity().setHourCardUseCountOneDay(0);

		// 每日宝石商店购买次数重置<需求变更，该字段不在使用>
		QuestEventBus.fireQuestEventOneTime(QuestEventType.TOTAL_LOGIN_TIMES, getXid());

		// 重置打折礼包购买次数
		ActivityTimeCfg activityTimeCfg = ActivityUtil.getCurActivityTimeCfg(Const.ActivityId.DISCOUNT_GIFT_VALUE);
		if (activityTimeCfg != null) {
			DiscountGiftData discountGift = ActivityUtil.getDiscountGiftData(playerData);
			discountGift.resetBuyTimes(playerData.getPlayerEntity().getResetTime());
			discountGift.setShouldShowPoint(true);
			playerData.updateActivity(Const.ActivityId.DISCOUNT_GIFT_VALUE, activityTimeCfg.getStageId());
		}

		// 重置限定特典(限时抢购)的数据
		ActivityTimeCfg timeCfg = ActivityUtil.getCurActivityTimeCfg(Const.ActivityId.TIME_LIMIT_PURCHASE_VALUE);
		if (null != timeCfg) {
			PersonalTimeLimitStatus personalTimeLimitStatus = ActivityUtil.getActivityStatus(playerData,
					Const.ActivityId.TIME_LIMIT_PURCHASE_VALUE, timeCfg.getStageId(), PersonalTimeLimitStatus.class);
			if (null != personalTimeLimitStatus) {
				personalTimeLimitStatus.setShouldShowPoint(true);
				playerData.updateActivity(Const.ActivityId.TIME_LIMIT_PURCHASE_VALUE, timeCfg.getStageId(), false);
			}
		}

		// 保存重置时间
		playerData.getPlayerEntity().setResetTime(GuaJiTime.getCalendar().getTime());
		playerData.getPlayerEntity().notifyUpdate(true);

		// 王的后宫(百花美人)活动每日数据重置
		HaremActivityEntity haremActivityEntity = playerData.loadHaremEntity();
		if (null != haremActivityEntity) {
			// 抽卡数据重置
			haremActivityEntity.clearDailyData();
			// 兑换次数重置
			// haremActivityEntity.getExchangeMap().clear();
			haremActivityEntity.notifyUpdate();
		}

		// 少女的邂逅活动每日数据重置
		ActivityTimeCfg maidenEncounterCfg = ActivityUtil
				.getCurActivityTimeCfg(Const.ActivityId.MAIDEN_ENCOUNTER_VALUE);
		if (null != maidenEncounterCfg) {
			MaidenEncounterEntity maidenEncounterEntity = playerData.getMaidenEncounterEntity();
			if (maidenEncounterEntity == null) {
				maidenEncounterEntity = playerData.loadMaidenEncounterEntity();
			}
			if (null != maidenEncounterEntity) {
				maidenEncounterEntity.clearDailyData();
				maidenEncounterEntity.notifyUpdate();
			}
		}

		// 大转盘活动每日兑换记录重置
		ActivityTimeCfg turntableTimeCfg = ActivityUtil.getCurActivityTimeCfg(Const.ActivityId.TURNTABLE_VALUE);
		if (null != turntableTimeCfg) {
			TurntableStatus status = ActivityUtil.getActivityStatus(playerData, Const.ActivityId.TURNTABLE_VALUE,
					turntableTimeCfg.getStageId(), TurntableStatus.class);
			if (null != status) {
				TurntableManager.initExchangeMap(status);
				playerData.updateActivity(Const.ActivityId.TURNTABLE_VALUE, turntableTimeCfg.getStageId(), false);
			}
		}

		// 万圣节活动每日兑换记录重置
		/* 20201028 暫不重置
		ActivityTimeCfg halloweenTimeCfg = ActivityUtil.getCurActivityTimeCfg(Const.ActivityId.HALLOWEEN_VALUE);
		if (null != halloweenTimeCfg) {
			HalloweenStatus status = ActivityUtil.getActivityStatus(playerData, Const.ActivityId.HALLOWEEN_VALUE,
					halloweenTimeCfg.getStageId(), HalloweenStatus.class);
			if (null != status) {
				HalloweenManager.initExchangeMap(status);
				playerData.updateActivity(Const.ActivityId.HALLOWEEN_VALUE, halloweenTimeCfg.getStageId(), false);
			}
		}*/

		// TTT
		BehaviorLogger.log4Service(this, Source.SYS_OPERATION, Action.DAILY_RESET,
				Params.valueOf("fastFightBuyTimes", stateEntity.getFastFightBuyTimes()),
//				Params.valueOf("fastFightTimes", stateEntity.getFastFightTimes()),
				Params.valueOf("bossFightBuyTimes", stateEntity.getBossFightBuyTimes()),
				Params.valueOf("bossFightTimes", stateEntity.getBossFightTimes()),
				Params.valueOf("arenaBuyTimes", stateEntity.getArenaBuyTimes()),
				Params.valueOf("surplusChallengeTimes", stateEntity.getSurplusChallengeTimes()),
				Params.valueOf("equipSmeltRefesh", stateEntity.getEquipSmeltRefesh()),
				Params.valueOf("gemShopBuyCount", stateEntity.getGemShopBuyCount()));

		// 同步
		if (sync) {
			// 今天首次登录更新
			sendProtocol(Protocol.valueOf(HP.code.STATE_INFO_SYNC_S,
					BuilderUtil.genStateBuilder(stateEntity, monthCardStatus, true)));
			// 首次登录红点
			// 协议体不处理
			HPCommentMsgRet.Builder builder = HPCommentMsgRet.newBuilder();
			builder.setNumber(1);
			sendProtocol(Protocol.valueOf(HP.code.FIRST_LOGIN_POINT_PUSH_S, builder));
			// 同步英雄令任务
			playerData.syncHeroTokenTaskInfo();
		}
	}

	public void pushRegisterCycle() {
		HPRegisterCycleRet.Builder builder = HPRegisterCycleRet.newBuilder();
		int registerDays = ActivityUtil.calcReisterDates(this.getPlayerData().getPlayerEntity().getCreateTime());
		builder.setRegisterSpaceDays(registerDays);
		if (ActivityUtil.getRegisterCycleActivityId(
				this.getPlayerData().getPlayerEntity().getCreateTime()) == Const.ActivityId.QUICK_COST_RATIO_VALUE) {
			builder.setRatio(1);
		}
		this.sendProtocol(Protocol.valueOf(HP.code.REGISTER_CYCLE_INFO_S_VALUE, builder));
	}

	public void handleReset() {
		Date curDate = GuaJiTime.getCalendar().getTime();
		StateEntity stateEntity = playerData.getStateEntity();
		if (stateEntity == null) {
			return;
		}
		// TTT
		VipPrivilegeCfg vipPrivilegeCfg = ConfigManager.getInstance().getConfigByKey(VipPrivilegeCfg.class,
				getVipLevel());

		List<ResetTimeCfg> resetList = ConfigManager.getInstance().getConfigList(ResetTimeCfg.class);
		if (null != resetList && resetList.size() > 0) {
			for (ResetTimeCfg resetTimeCfg : resetList) {
				try {
					boolean isNeedReset = false;
					Date lastResetTime = stateEntity.getLastResetTime(resetTimeCfg.getId());

					if (lastResetTime == null) {
						Calendar calendar = GuaJiTime.getCalendar();

						if ((resetTimeCfg.getResetHour() <= calendar.get(Calendar.HOUR_OF_DAY)) 
								&& (resetTimeCfg.getResetMin() <= calendar.get(Calendar.MINUTE)))  {
					
								isNeedReset = true;

								calendar.set(Calendar.HOUR_OF_DAY, resetTimeCfg.getResetHour());
								calendar.set(Calendar.MINUTE, resetTimeCfg.getResetMin());
								calendar.set(Calendar.SECOND, 0);
								calendar.set(Calendar.MILLISECOND, 0);
								stateEntity.putResetTime(resetTimeCfg.getId(), calendar.getTime());
							
						} else {
							calendar.add(Calendar.DATE, -1);  // 1 day ago
							calendar.set(Calendar.HOUR_OF_DAY, resetTimeCfg.getResetHour());
							calendar.set(Calendar.MINUTE, resetTimeCfg.getResetMin());
							calendar.set(Calendar.SECOND, 0);
							calendar.set(Calendar.MILLISECOND, 0);
							stateEntity.putResetTime(resetTimeCfg.getId(), calendar.getTime());
						}
					} else {
						// 每个活动时间点24小时内只能刷新一次,且当前当天时间大于活动点时间
						// ,遍历每个reset时间点，保证配置表顺序by callan
						if (Math.abs(lastResetTime.getTime() - curDate.getTime()) >= 24 * 3600 * 1000) {
							Calendar calendar = GuaJiTime.getCalendar();

							isNeedReset = true;
							calendar.add(Calendar.DATE, -1);
							calendar.set(Calendar.HOUR_OF_DAY, resetTimeCfg.getResetHour());
							calendar.set(Calendar.MINUTE, resetTimeCfg.getResetMin());
							calendar.set(Calendar.SECOND, 0);
							calendar.set(Calendar.MILLISECOND, 0);
							stateEntity.putResetTime(resetTimeCfg.getId(), calendar.getTime());

							if (GuaJiTime.setTimeHourMinute(resetTimeCfg.getResetHour(),
									resetTimeCfg.getResetMin()) <= GuaJiTime.getCalendar().getTimeInMillis()) {
								calendar.add(Calendar.DATE, 1);
								calendar.set(Calendar.HOUR_OF_DAY, resetTimeCfg.getResetHour());
								calendar.set(Calendar.MINUTE, resetTimeCfg.getResetMin());
								calendar.set(Calendar.SECOND, 0);
								calendar.set(Calendar.MILLISECOND, 0);
								stateEntity.putResetTime(resetTimeCfg.getId(), calendar.getTime());
							}
						}
					}

					if (isNeedReset) {
						MercenaryExpeditionEntity mercenaryExpeditionEntity = null;
						if (resetTimeCfg.getId() >= Const.dailyRefreshType.MERCENARY_EXPEDTION_ITEM_REFRESH_1_VALUE
								&& resetTimeCfg
										.getId() <= Const.dailyRefreshType.MERCENARY_EXPEDTION_ITEM_REFRESH_12_VALUE) {
							mercenaryExpeditionEntity = this.getPlayerData().loadMercenaryExpeditionEntity();
						}
						switch (resetTimeCfg.getId()) {
						// 普通商店和水晶商店合并
						case Const.dailyRefreshType.SHOP_ITEM_REFRESH_VALUE:
							ShopEntity shopEntity = this.getPlayerData().getShopEntity();
							shopEntity.clearData();
							shopEntity.notifyUpdate(false);
						// 这个定时刷新的逻辑判断事件类型地方以后优化吧，配置文件变个数据结构
						//派遣 115-126
						case Const.dailyRefreshType.MERCENARY_EXPEDTION_ITEM_REFRESH_1_VALUE:
						case Const.dailyRefreshType.MERCENARY_EXPEDTION_ITEM_REFRESH_2_VALUE:
						case Const.dailyRefreshType.MERCENARY_EXPEDTION_ITEM_REFRESH_3_VALUE:
						case Const.dailyRefreshType.MERCENARY_EXPEDTION_ITEM_REFRESH_4_VALUE:
						case Const.dailyRefreshType.MERCENARY_EXPEDTION_ITEM_REFRESH_5_VALUE:
						case Const.dailyRefreshType.MERCENARY_EXPEDTION_ITEM_REFRESH_6_VALUE:
						case Const.dailyRefreshType.MERCENARY_EXPEDTION_ITEM_REFRESH_7_VALUE:
						case Const.dailyRefreshType.MERCENARY_EXPEDTION_ITEM_REFRESH_8_VALUE:
						case Const.dailyRefreshType.MERCENARY_EXPEDTION_ITEM_REFRESH_9_VALUE:
						case Const.dailyRefreshType.MERCENARY_EXPEDTION_ITEM_REFRESH_10_VALUE:
						case Const.dailyRefreshType.MERCENARY_EXPEDTION_ITEM_REFRESH_11_VALUE:
						case Const.dailyRefreshType.MERCENARY_EXPEDTION_ITEM_REFRESH_12_VALUE:
							if (mercenaryExpeditionEntity != null) {
								mercenaryExpeditionEntity.fixedReset();
							}
							break;
						case Const.dailyRefreshType.ALLIANCE_SIGN_VALUE:
							PlayerAllianceEntity playerAllianceEntity = this.getPlayerData().getPlayerAllianceEntity();
							if (playerAllianceEntity != null) {
								playerAllianceEntity.setReportTime(curDate.getTime());
							}
							break;

						case Const.dailyRefreshType.EQUIP_CREATE_REFRESH_VALUE:
							stateEntity.setEquipSmeltRefesh(GsConst.Equip.INIT_EQUIP_SMELT_REFRESH);
							break;

						case Const.dailyRefreshType.ARENA_CHALLENGE_VALUE:
							stateEntity.setArenaBuyTimes(0);
							if (vipPrivilegeCfg != null) {
								// 重置竞技场日数据
								stateEntity.setSurplusChallengeTimes(SysBasicCfg.getInstance().getFreeChallengeTimes());
							}
							GuaJiXID xid = GuaJiXID.valueOf(GsConst.ObjType.PLAYER, getId());
							Msg defenderMsg = Msg.valueOf(GsConst.MsgType.SYNC_ARENA_INFO, xid);
							GsApp.getInstance().postMsg(defenderMsg);
							break;

						case Const.dailyRefreshType.FAST_FIGHT_VALUE:
							stateEntity.setFastFightBuyTimes(0);
							break;

						case Const.dailyRefreshType.BOSS_FIGHT_VALUE:
							stateEntity.setBossFightBuyTimes(0);
							if (vipPrivilegeCfg != null) {
								stateEntity.setBossFightTimes(vipPrivilegeCfg.getBossBattleTimes());
							}
							break;

						case Const.dailyRefreshType.ELITE_MAP_VALUE:
							stateEntity.setEliteMapBuyTimes(0);
							if (vipPrivilegeCfg != null) {
								stateEntity.setEliteMapTimes(vipPrivilegeCfg.getEliteMapTimes());
							}
							break;

						case Const.dailyRefreshType.STAR_STONE_VALUE:
							stateEntity.setStarStoneTimes(0);
							break;

						case Const.dailyRefreshType.FB_FRIEND_ASKTICK_VALUE:
							stateEntity.clearAskTickIds();
							break;
						case Const.dailyRefreshType.VIP_WELFARE_AWARD_VALUE:
							VipWelfareStatus status = ActivityUtil.getActivityStatus(this.getPlayerData(),
									Const.ActivityId.VIP_WELFARE_VALUE, 0, VipWelfareStatus.class);
							status.setAwareStatus(VipWelfareAwardHandler.NOTGET);
							this.getPlayerData().updateActivity(Const.ActivityId.VIP_WELFARE_VALUE, 0);
							break;
						case Const.dailyRefreshType.MYSTERY_SHOP_ITEM_REFRESH_VALUE:
							break;
						case Const.dailyRefreshType.CRYSTAL_SHOP_ITEM_REFRESH_VALUE:
						case Const.dailyRefreshType.CRYSTAL_SHOP_ITEM_REFRESH_2_VALUE:
						case Const.dailyRefreshType.CRYSTAL_SHOP_ITEM_REFRESH_3_VALUE:
						case Const.dailyRefreshType.CRYSTAL_SHOP_ITEM_REFRESH_4_VALUE:
							refreshCrystalShop();
							break;
						case Const.dailyRefreshType.MULTIELITE_INIT_BATTLE_COUNT_1_VALUE:
						case Const.dailyRefreshType.MULTIELITE_INIT_BATTLE_COUNT_2_VALUE:
						case Const.dailyRefreshType.MULTIELITE_INIT_BATTLE_COUNT_3_VALUE:
						case Const.dailyRefreshType.MULTIELITE_INIT_BATTLE_COUNT_4_VALUE:
							// 多人副本剩余次数
							stateEntity.setMultiEliteTimes(SysBasicCfg.getInstance().getMultiEliteDayFreeTimes());
							// 多人副本首次胜利次数
							stateEntity.setMultiFirstBattle(0);
							// 清除多人副本战斗信息
							stateEntity.clearMultiBattleInfoMap();
							// 清除多人副本領獎信息
							stateEntity.clearMultiGiftInfoMap();
							break;
						case Const.dailyRefreshType.WORLD_BOSS_BUFF_FREE_TIMES_1_VALUE:
						case Const.dailyRefreshType.WORLD_BOSS_BUFF_FREE_TIMES_2_VALUE:
							stateEntity.setWorldBossBuffFreeTimes(0);
							break;
						case Const.dailyRefreshType.DUNGEON_REERESH_TIME_VALUE:
							stateEntity.clearDungeonStar();
							stateEntity.clearDungeonTimesMap();
							stateEntity.clearDungeonOneKey();
							break;
						case Const.dailyRefreshType.GLORYHOLE_REERESH_TIME_VALUE:
							restGloryHoleDailyStatus();
							break;
						default:
							break;
						}

						stateEntity.notifyUpdate(true);
						this.playerData.syncStateInfo();// 在线跨天更新

						BehaviorLogger.log4Service(this, Source.SYS_OPERATION, Action.DAILY_RESET,
								Params.valueOf("fastFightBuyTimes", stateEntity.getFastFightBuyTimes()),
//								Params.valueOf("fastFightTimes", stateEntity.getFastFightTimes()),
								Params.valueOf("bossFightBuyTimes", stateEntity.getBossFightBuyTimes()),
								Params.valueOf("bossFightTimes", stateEntity.getBossFightTimes()),
								Params.valueOf("arenaBuyTimes", stateEntity.getArenaBuyTimes()),
								Params.valueOf("surplusChallengeTimes", stateEntity.getSurplusChallengeTimes()),
								Params.valueOf("equipSmeltRefesh", stateEntity.getEquipSmeltRefesh()),
								Params.valueOf("resetId", resetTimeCfg.getId()));
					}
				} catch (Exception e) {
					MyException.catchException(e);
				}
			}
		}

	}

	/**
	 * 每月重置
	 */
	public void handleHaremReset() {
		HaremActivityEntity entity = playerData.getHaremActivityEntity();
		if (entity == null)
			return;

		// 重置 解决之前的数据问题 添加 GuaJiTime.isSameDay
		if (GuaJiTime.getMillisecond() > entity.getExchangeNextResetTime()
				|| GuaJiTime.isSameDay(GuaJiTime.getMillisecond(), entity.getExchangeNextResetTime())) {
			Map<Object, HaremExchangeCfg> cfgMap = ConfigManager.getInstance().getConfigMap(HaremExchangeCfg.class);
			if (cfgMap == null || cfgMap.size() <= 0) {
				return;
			}
			for (Object key : cfgMap.keySet()) {
				HaremExchangeCfg cfg = cfgMap.get(key);
				if (cfg == null)
					continue;
				if (cfg.getIsReset() == 1 && entity.getExchangeMap().containsKey(cfg.getId())) {
					entity.getExchangeMap().put(cfg.getId(), 0);
				}
			}
			Calendar curCalendar = GuaJiTime.getCalendar();
			curCalendar.add(Calendar.MONTH, 1);
			curCalendar.set(Calendar.DAY_OF_MONTH, 1);
			curCalendar.set(Calendar.HOUR, 0);
			curCalendar.set(Calendar.MINUTE, 0);
			curCalendar.set(Calendar.SECOND, 0);

			entity.setExchangeNextResetTime(curCalendar.getTime().getTime());
			entity.notifyUpdate();
			// 组装协议
			HPHaremScorePanelRes.Builder builder = BuilderUtil.getHaremExchangeBuilders(entity, cfgMap, 0);
			sendProtocol(Protocol.valueOf(HP.code.HAREM_EXCHANGE_S_VALUE, builder));
		}

	}

	/**
	 * 每周重置呀呀兑换金豆数量
	 */
	public void handleYaYaRankReset() {
		StateEntity stateEntity = playerData.getStateEntity();
		if (stateEntity == null) {
			return;
		}
		// 是否首次重置
		long lastResetExchangeBeanTime = stateEntity.getLastResetExchangeBeanTime();
		if (lastResetExchangeBeanTime <= 0) {
			resetStateEntityExchangeBean(stateEntity);
			return;
		}
		// 是否满足重置条件;
		long currentTime = GuaJiTime.getMillisecond();
		if (currentTime > lastResetExchangeBeanTime
				&& (currentTime - lastResetExchangeBeanTime) >= GsConst.RESET_YAYA_RANK_INTERVAL) {
			resetStateEntityExchangeBean(stateEntity);
		}
	}

	/**
	 * 重置兑换金豆数量
	 *
	 * @param stateEntity
	 */
	private void resetStateEntityExchangeBean(StateEntity stateEntity) {
		stateEntity.setLastResetExchangeBeanTime(GuaJiTime.getFirstDayCalendarOfCurWeek().getTimeInMillis());
		stateEntity.setExchangeCount(0);
		stateEntity.setLastExchangeBeanTime(0);
		stateEntity.notifyUpdate(true);
	}

	public void refreshCrystalShop() {
		CrystalShopEntity crystalShopEntity = this.getPlayerData().loadCrystalShopEntity();

		if (crystalShopEntity != null) {
			CrystalMarkey.getInstance().shopItemInfo(this, crystalShopEntity,false);
		}
	}
	
	public void DailyRefreshShop() {
		
		DailyShopEntity dailyShopEntity = this.getPlayerData().loadDailyShopEntity();
		if ((dailyShopEntity != null)) {
			DailyMarket.getInstance().shopItemInfo(this, dailyShopEntity,false);
		}
		
		RaceShopEntity raceShopEntity = this.getPlayerData().loadRaceShopEntity();
		if ((raceShopEntity != null)) {
			RaceMarket.getInstance().shopItemInfo(this, raceShopEntity,false);
		}
		
		TempleShopEntity templeShopEntity = this.getPlayerData().loadTempleShopEntity();
		if ((templeShopEntity != null)){
			TempleMarket.getInstance().shopItemInfo(this, templeShopEntity,false);
		}
		
		ArenaShopEntity arenaShopEntity = this.getPlayerData().loadArenaShopEntity();
		if (arenaShopEntity != null) {
			ArenaMarket.getInstance().shopItemInfo(this, arenaShopEntity,false);
		}
	}
		
	public void WishingRefreshPool() {
		Activity147WishingManager.restActivityStatus(this);
	}
	
	public void LoginTenDrawResetGot() {
		Activity157Handler.restGot(this);
	}
		
	/*
	 * 一周重置商店
	 */
	public void WeekRefreshShop(Date lastDate) {
		GuildShopEntity guildShopEntity = this.getPlayerData().loadGuildShopEntity();
		CrossShopEntity crossShopEntity = this.getPlayerData().loadCrossShopEntity();
//		GodSeaShopEntity godseaShopEntity = this.getPlayerData().loadGodSeaShopEntity();
		// 和上次重置时间是否是同一周
		boolean sameWeek = false;
		if (lastDate != null) {
			Calendar lastCalendar = Calendar.getInstance();
			lastCalendar.setTime(lastDate);
			sameWeek = GuaJiTime.getFirstDayOfWeek(lastCalendar).equals(GuaJiTime.getFirstDayOfWeek(Calendar.getInstance())) ? true : false;
		}
		if (!sameWeek) {
			if (guildShopEntity != null) {
				GuildMarket.getInstance().shopItemInfo(this, guildShopEntity,false);
			}
			if ((crossShopEntity != null)) {
				CrossMarket.getInstance().shopItemInfo(this, crossShopEntity,false);
			}
//			if ((godseaShopEntity != null)){
//				GodSeaMarket.getInstance().shopItemInfo(this, godseaShopEntity,false);
//			}
		}
	}
	
	/**
	 *   重置週任務 
	 * @param lastDate
	 */
	public void WeeklyQuestReset(Date lastDate) {
		WeeklyQuestEntity QuestEntity = playerData.loadWeeklyQuestEntity();
		// 和上次重置时间是否是同一周
		boolean sameWeek = false;
		if (lastDate != null) {
			Calendar lastCalendar = Calendar.getInstance();
			lastCalendar.setTime(lastDate);
			sameWeek = GuaJiTime.getFirstDayOfWeek(lastCalendar).equals(GuaJiTime.getFirstDayOfWeek(Calendar.getInstance())) ? true : false;
		}
		if (!sameWeek) {
			if (QuestEntity != null) {
				QuestEntity.reset();
				QuestEntity.reConvert();
				QuestEntity.notifyUpdate(true);
			}
		}
	}
	
	/**
	 * 判断黑市活动时间是否正确
	 *
	 * @param timeCfg
	 * @return
	 */
	public boolean isTimeValidate(ActivityTimeCfg timeCfg) {
		String startTime = timeCfg.getStartTime().replace("_", " ");
		String endTime = timeCfg.getEndTime().replace("_", " ");
		Long currentTimeMullis = System.currentTimeMillis();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		try {
			long startTimeMillis = sdf.parse(startTime).getTime();
			long endTimeMillis = sdf.parse(endTime).getTime();
			if ((currentTimeMullis > startTimeMillis) && (currentTimeMullis < endTimeMillis)) {
				return true;
			}
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return false;
	}

	/**
	 * 增加元素
	 */
	public ElementEntity increaseElement(int elementItemId, Action action, AttrInfo.Builder attr) {
		// if(!ConfigUtil.check(Const.itemType.ELEMENT_VALUE, elementItemId)) {
		// return null;
		// }

		ElementEntity elementEntity = ElementUtil.generateElement(this, elementItemId, attr);

		if (elementEntity != null) {
			if (DBManager.getInstance().create(elementEntity)) {
				playerData.addElementEntity(elementEntity);

				BehaviorLogger.log4Service(this, Source.ELEMENT_ADD, action, Params.valueOf("eleItemId", elementItemId),
						Params.valueOf("id", elementEntity.getId()),
						Params.valueOf("attr", elementEntity.getAttribute().toString()));
				return elementEntity;
			}
		}
		return null;
	}

	public ElementEntity increaseElement(int elementItemId, Action action) {
		return this.increaseElement(elementItemId, action, null);
	}

	/**
	 * 增加boss挑战次数
	 */
//	public void increaseFastFightTimes(int fastFightTimes, Action action) {
//		if (fastFightTimes < 0) {
//			throw new RuntimeException("fastFightTimes");
//		}
//
//		StateEntity stateEntity = playerData.getStateEntity();
//		stateEntity.setFastFightTimes(stateEntity.getFastFightTimes() + fastFightTimes);
//		stateEntity.notifyUpdate(true);
//
//		BehaviorLogger.log4Service(this, Source.PLAYER_ATTR_CHANGE, action,
//				Params.valueOf("playerAttr", Const.playerAttr.FASTFIGHT_TIMES_VALUE),
//				Params.valueOf("add", fastFightTimes), Params.valueOf("after", stateEntity.getEliteMapTimes()));
//	}

	/**
	 * 通知排行献礼管理器刷新经验
	 */
	public void sendExpData() {
		// editby: crazjohn fuck this fucking codes
		// if(true){
		// return;
		// }
		// if(!RankGiftManager.getInstance().isActivityNotEnd() ){
		//
		// return;
		// }

		// if (!RankGiftManager.getInstance().compareWithLastExpRank(getLevel(),
		// getExp())){
		//
		// return;
		// }
		// 通知排行献礼管理器刷新经验
		// GsApp.getInstance().postMsg(RankGiftManager.getInstance().getXid(),
		// Msg.valueOf(GsConst.MsgType.RANK_GIFT_RESET_EXP_RANK).pushParam(getId(),
		// getName(), getLevel(), getExp()));
	}

	public boolean consumElement(long id, Action action) {
		ElementEntity elementEntity = playerData.getElementById(id);
		if (elementEntity != null) {
			playerData.removeElementEntity(elementEntity);
			elementEntity.delete();

			BehaviorLogger.log4Service(this, Source.ELEMENT_REMOVE, action,
					Params.valueOf("eleItemId", elementEntity.getItemId()), Params.valueOf("id", elementEntity.getId()),
					Params.valueOf("attr", elementEntity.getAttribute().toString()));

			return true;
		}
		return false;
	}

	// /**
	// * 返回当日的分享次数
	// * @param puid
	// * @return
	// */
	// public int facebookShareCount(String puid) {
	// Map<String, Integer> mapFaceBook = playerData.loadFaceBookShare(puid);
	// return mapFaceBook.get(GuaJiTime.getDateString());
	// }

	public void increasetodayRecharge(int gold) {
		this.todayRechargeNum += gold;
	}

	public void setTodayRechargeNum(int todayRechargeNum) {
		this.todayRechargeNum = todayRechargeNum;
	}

	public int getTodayRechargeNum() {
		return this.todayRechargeNum;
	}
	
	/**
	 * 取得玩家 tabdb appid
	 * @return
	 */
	public String getTabDBAppId() {
		return GameUtil.getTabDBId(getPlatformId());
	}
	
	/**
	 * 玩家擁有標記
	 * @param id
	 * @return
	 */
	public boolean isSigned(int id ) {
		return getPlayerData().getSignEntity().isSign(id);
	}
	
	/**
	 * 175.壁尻重置daily任務,並發送任務未領禮物
	 */
	public void restGloryHoleDailyStatus() {
		
		// 检测活动是否开放
		int activityId = Const.ActivityId.ACTIVITY175_Glory_Hole_VALUE;
		ActivityTimeCfg activityTimeCfg = ActivityUtil.getCurActivityTimeCfg(activityId);
		if (activityTimeCfg == null) {
			return;
		}
		
		//活動数据加载
		int stageId = activityTimeCfg.getStageId();
		
		Activity175Status status = ActivityUtil.getActivityStatus(getPlayerData(), activityId, stageId, Activity175Status.class);
		
		if (status == null) {
			return;
		}
		
		String awardStr = "";
		int mailId = GsConst.MailId.GH_DAILY_QUEST_MAIL;
		
		for (Integer id :status.getDailyQuestMap().keySet()) {
			DailyQuestItem quest = status.getDailyQuestMap().get(id);
			GloryHoleDailyCfg dailyQuestCfg = ConfigManager.getInstance().getConfigByKey(GloryHoleDailyCfg.class, id);
			if ((quest.getQuestStatus() == 1) && (quest.getTakeStatus() == 0)&& (dailyQuestCfg != null)) {
				int point = dailyQuestCfg.getPoint();
				status.setPoint(point + status.getPoint());
				if (awardStr.isEmpty()) {
					awardStr = dailyQuestCfg.getAward();
				} else {
					awardStr = awardStr+","+dailyQuestCfg.getAward();
				}
			}
		}
		
		Map<Object,DailyQuestPointCfg> pointMap = ConfigManager.getInstance().getConfigMap(DailyQuestPointCfg.class);
		for (Map.Entry<Object,DailyQuestPointCfg> entry : pointMap.entrySet()) {
			int count = (int)entry.getKey();
			if ((status.getPoint() >= count) && (!status.getDailyPoint().contains(count))) {
				if (awardStr.isEmpty()) {
					awardStr = entry.getValue().getAward();
				} else {
					awardStr = awardStr+","+entry.getValue().getAward();
				}
			}
		}
		
		status.Dailyreset();
		
		getPlayerData().updateActivity(activityId, stageId);
		
		if (!awardStr.isEmpty()) {
			AwardItems everydayAward = AwardItems.valueOf(awardStr);
			// 发送邮件时间
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
			String date = sdf.format(GuaJiTime.getAM0Date());
			MailManager.createMail(this.getId(), Mail.MailType.Reward_VALUE, mailId, "",
					everydayAward, date);
		}
	}
}
