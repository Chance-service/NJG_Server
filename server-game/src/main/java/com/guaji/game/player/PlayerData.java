package com.guaji.game.player;

import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

import org.apache.commons.beanutils.BeanUtils;
import org.guaji.config.ConfigManager;
import org.guaji.db.DBManager;
import org.guaji.net.protocol.Protocol;
import org.guaji.os.GuaJiTime;
import org.guaji.os.MyException;

import com.guaji.game.ServerData;
import com.guaji.game.attribute.EliteMapAttr;
import com.guaji.game.battle.BattleRole;
import com.guaji.game.battle.NewBattleRole;
import com.guaji.game.bean.HeroTokenShopBean;
import com.guaji.game.bean.HeroTokenTaskBean;
import com.guaji.game.config.ActivityCfg;
//import com.guaji.game.cmreport.CmReportManager;
import com.guaji.game.config.ActivityCfg.ActivityItem;
import com.guaji.game.config.ActivityTimeCfg;
import com.guaji.game.config.AvatarCfg;
import com.guaji.game.config.BPHShopCfg;
import com.guaji.game.config.BadgeCfg;
import com.guaji.game.config.EquipCfg;
import com.guaji.game.config.Hero_NGListCfg;
import com.guaji.game.config.ItemCfg;
import com.guaji.game.config.MottoCfg;
import com.guaji.game.config.NewMapCfg;
import com.guaji.game.config.NewSkillCfg;
import com.guaji.game.config.NewWeekCardCfg;
import com.guaji.game.config.QuestCfg;
import com.guaji.game.config.RoleRelatedCfg;
import com.guaji.game.config.SecretMsgCfg;
import com.guaji.game.config.SevenDayQuestCfg;
import com.guaji.game.config.SysBasicCfg;
import com.guaji.game.entity.ActivityEntity;
import com.guaji.game.entity.AllianceEntity;
import com.guaji.game.entity.ArchiveEntity;
import com.guaji.game.entity.ArenaShopEntity;
import com.guaji.game.entity.AvatarEntity;
import com.guaji.game.entity.BadgeEntity;
import com.guaji.game.entity.ChatSkinEntity;
import com.guaji.game.entity.CrossShopEntity;
import com.guaji.game.entity.CrystalShopEntity;
import com.guaji.game.entity.CycleStageShopEntity;
import com.guaji.game.entity.DailyQuestEntity;
import com.guaji.game.entity.DailyShopEntity;
import com.guaji.game.entity.EighteenPrincesEntity;
import com.guaji.game.entity.EighteenPrincesHelpHistoryEntity;
import com.guaji.game.entity.ElementEntity;
import com.guaji.game.entity.EmailEntity;
import com.guaji.game.entity.EquipEntity;
import com.guaji.game.entity.FacebookShareEntity;
import com.guaji.game.entity.FormationEntity;
import com.guaji.game.entity.FriendEntity;
import com.guaji.game.entity.GodSeaShopEntity;
import com.guaji.game.entity.GuildBuffEntity;
import com.guaji.game.entity.GuildShopEntity;
import com.guaji.game.entity.GvgRewardEntity;
import com.guaji.game.entity.HaremActivityEntity;
import com.guaji.game.entity.HeroTokenTaskEntity;
import com.guaji.game.entity.HonorShopEntity;
import com.guaji.game.entity.IpAddrEntity;
import com.guaji.game.entity.ItemEntity;
import com.guaji.game.entity.LoginEntity;
import com.guaji.game.entity.MaidenEncounterEntity;
import com.guaji.game.entity.MapEntity;
import com.guaji.game.entity.MapStatisticsEntity;
import com.guaji.game.entity.MercenaryExpeditionEntity;
import com.guaji.game.entity.MottoEntity;
import com.guaji.game.entity.MsgEntity;
import com.guaji.game.entity.MutualEntity;
import com.guaji.game.entity.MysteryShopEntity;
import com.guaji.game.entity.PlayerAllianceEntity;
import com.guaji.game.entity.PlayerEntity;
import com.guaji.game.entity.PlayerPrinceDevilsEntity;
import com.guaji.game.entity.PlayerStarSoulEntity;
import com.guaji.game.entity.PlayerTalentEntity;
import com.guaji.game.entity.PlayerWingsEntity;
import com.guaji.game.entity.QuestEntity;
import com.guaji.game.entity.QuestItem;
import com.guaji.game.entity.RaceShopEntity;
import com.guaji.game.entity.RoleEntity;
import com.guaji.game.entity.RoleRingEntity;
import com.guaji.game.entity.RoleSkinEntity;
import com.guaji.game.entity.SecretMsgEntity;
import com.guaji.game.entity.SevenDayQuestEntity;
import com.guaji.game.entity.SevenDayQuestItem;
import com.guaji.game.entity.ShopDiscountEntity;
import com.guaji.game.entity.ShopEntity;
import com.guaji.game.entity.SignEntity;
import com.guaji.game.entity.SkillEntity;
import com.guaji.game.entity.SkinShopEntity;
import com.guaji.game.entity.StateEntity;
import com.guaji.game.entity.TempleShopEntity;
import com.guaji.game.entity.TitleEntity;
import com.guaji.game.entity.WeeklyQuestEntity;
import com.guaji.game.entity.WorshipEntity;
import com.guaji.game.item.AwardItems;
import com.guaji.game.item.ConsumeItems;
import com.guaji.game.item.ItemInfo;
import com.guaji.game.log.BehaviorLogger;
import com.guaji.game.log.BehaviorLogger.Action;
import com.guaji.game.log.BehaviorLogger.Params;
import com.guaji.game.log.BehaviorLogger.Source;
import com.guaji.game.manager.ActivityManager;
import com.guaji.game.manager.AllianceManager;
import com.guaji.game.module.activity.foreverCard.ForeverCardStatus;
import com.guaji.game.module.activity.harem.HaremManager;
import com.guaji.game.module.activity.monthcard.MonthCardStatus;
import com.guaji.game.module.activity.newWeekCard.NewWeekCardStatus;
import com.guaji.game.module.quest.QuestEvent;
import com.guaji.game.module.quest.QuestEventBus;
import com.guaji.game.module.sevendaylogin.SevenDayEvent;
import com.guaji.game.protocol.Badge;
import com.guaji.game.protocol.Battle.HPMapStatisticsSync;
import com.guaji.game.protocol.Const;
import com.guaji.game.protocol.Const.ApplyAddAllianceUpEmail;
import com.guaji.game.protocol.Const.QuestEventType;
import com.guaji.game.protocol.Const.QuestState;
import com.guaji.game.protocol.Const.QuestType;
import com.guaji.game.protocol.Const.SevenDayEventType;
import com.guaji.game.protocol.Const.itemType;
import com.guaji.game.protocol.Consume.ConsumeItem;
import com.guaji.game.protocol.Element.HPElementInfoSync;
import com.guaji.game.protocol.Equip.HPEquipInfoSync;
import com.guaji.game.protocol.Guide.GuideInfoBean;
import com.guaji.game.protocol.Guide.HPGuideInfoSync;
import com.guaji.game.protocol.Guide.HPPlayStorySync;
import com.guaji.game.protocol.HP;
import com.guaji.game.protocol.HeroToken.HPHeroTokenTaskInfoRet;
import com.guaji.game.protocol.HeroToken.ShopStatusBean;
import com.guaji.game.protocol.HeroToken.TaskStatusBean;
import com.guaji.game.protocol.Item.HPItemInfoSync;
import com.guaji.game.protocol.Mail;
import com.guaji.game.protocol.Motto;
import com.guaji.game.protocol.Player.EliteMapInfo;
import com.guaji.game.protocol.Player.HPEliteMapInfoSync;
import com.guaji.game.protocol.Player.HPPlayerInfoSync;
import com.guaji.game.protocol.Player.HPRoleInfoSync;
import com.guaji.game.protocol.Player.HPRoleRingInfoSync;
import com.guaji.game.protocol.Player.RoleInfo;
import com.guaji.game.protocol.PlayerTitle.HPTitleInfoSyncS;
import com.guaji.game.protocol.PlayerTitle.TitleInfo;
import com.guaji.game.protocol.RoleOpr.HPRoleInfo;
import com.guaji.game.protocol.SecretMsg.syncSecretMsg;
import com.guaji.game.protocol.SevenDayQuest.QuestItemInfo;
import com.guaji.game.protocol.SevenDayQuest.SyncQuestItemInfo;
import com.guaji.game.protocol.Skill.HPSkillInfoSync;
import com.guaji.game.protocol.SkillEnhance.HPSkillEnhanceOpenState;
import com.guaji.game.protocol.Snapshot.PlayerSnapshotInfo;
import com.guaji.game.util.ActivityUtil;
import com.guaji.game.util.AdjustEventUtil;
import com.guaji.game.util.BuilderUtil;
import com.guaji.game.util.ElementUtil;
import com.guaji.game.util.EquipUtil;
import com.guaji.game.util.GsConst;
import com.guaji.game.util.GsConst.ActivityTimeType;
import com.guaji.game.util.GsConst.GuideType;
import com.guaji.game.util.PlayerUtil;
import com.guaji.game.util.ProtoUtil;
import com.guaji.game.util.QuickPhotoUtil;

import net.sf.json.JSONObject;

/**
 * 管理所有玩家数据集合
 */
public class PlayerData implements Serializable {
	/**
	 *
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * 玩家对象
	 */
	private transient Player player = null;

	/**
	 * 玩家基础数据
	 */
	private PlayerEntity playerEntity = null;

	/**
	 * 玩家登陆信息
	 */
	private LoginEntity loginEntity = null;

	/**
	 * 玩家状态存储
	 */
	private StateEntity stateEntity = null;
	/**
	 * 玩家標記信息
	 */
	private SignEntity signEntity = null;

	/**
	 * 战斗分析存储
	 */
	private MapStatisticsEntity mapStatisticsEntity = null;

	/**
	 * 角色商城数据
	 */
	private ShopEntity shopEntity = null;

	/**
	 * 公会基本信息
	 */
	private PlayerAllianceEntity playerAllianceEntity = null;

	/**
	 * 地图数据
	 */
	private MapEntity mapEntity = null;

	/**
	 * 好友数据
	 */
	private FriendEntity friendEntity = null;

	/**
	 * 荣誉商店信息
	 */
	private HonorShopEntity honorShopEntity = null;

	/**
	 * 水晶商店信息
	 */
	private CrystalShopEntity crystalShopEntity = null;
	
	/**
	 * 商店購買折扣記數信息
	 */
	private ShopDiscountEntity shopDiscountEntity = null;
	/**
	 * 每日商店信息
	 */
	private DailyShopEntity dailyShopEntity = null;
	/**
	 * 種族(屬性)商店信息
	 */
	private RaceShopEntity raceShopEntity = null;
	/**
	 * 新公會商店信息
	 */
	private GuildShopEntity guildShopEntity = null;
	/**
	 * 神秘商店信息
	 */
	private MysteryShopEntity mysteryShopEntity = null;
	
	/**
	 * 角鬥商店信息
	 */
	private ArenaShopEntity arenaShopEntity = null;
	/**
	 * 跨服商店信息
	 */
	private CrossShopEntity crossShopEntity = null;
	
	/**
	 * 殿堂商店信息
	 */
	private TempleShopEntity templeShopEntity = null;
	/**
	 * 神海商店信息
	 */
	private GodSeaShopEntity godseaShopEntity = null;
	/**
	 * 循環商店信息
	 */
	private CycleStageShopEntity cyclestageShopEntity = null;
	/**
	 * 皮膚商店信息
	 */
	private SkinShopEntity skinShopEntity = null;
	/**
	 * 公會魔典信息
	 */
	private GuildBuffEntity guildBuffEntity = null;

	/**
	 * 佣兵远征对象
	 */
	private MercenaryExpeditionEntity mercenaryExpeditionEntity = null;

	/**
	 * ip地址对象
	 */
	private IpAddrEntity ipAddrEntity;

	/**
	 * 称号对象
	 */
	private TitleEntity titleEntity;

	/**
	 * 真气对象
	 */
	protected PlayerTalentEntity playerTalentEntity;

	/**
	 * 翅膀对象
	 */
	private PlayerWingsEntity playerWingsEntity;

	/**
	 * 英雄令任务对象
	 */
	private HeroTokenTaskEntity heroTokenTaskEntity;

	/**
	 * 日常任务数据对象
	 */
	private DailyQuestEntity dailyQuestEntity;
	
	/**
	 * 每周任务数据对象
	 */
	private WeeklyQuestEntity weeklyQuestEntity;

	/**
	 * 7日登陆任务
	 */
	private SevenDayQuestEntity sevenDayQuestEntity;

	/**
	 * 玩家数据快照
	 */
	private PlayerSnapshotInfo.Builder onlinePlayerSnapshot = null;

	/**
	 * 角色列表
	 */
	protected List<RoleEntity> roleEntities = null;
	
	/**
	 * 箴言列表
	 */
	protected List<MottoEntity> mottoEntities = null;
	/**
	 * 秘密留言列表
	 */
	protected List<SecretMsgEntity> secretMsgEntities = null;

	/**
	 * 物品列表
	 */
	private List<ItemEntity> itemEntities = null;

	/**
	 * 装备列表
	 */
	protected List<EquipEntity> equipEntities = null;

	/**
	 * 徽章列表
	 */
	protected List<BadgeEntity> badgeEntities = null;

	/**
	 * 技能列表
	 */
	protected List<SkillEntity> skillEntities = null;

	/**
	 * 任务成就数据;
	 */
	private QuestEntity questEntity = null;
	/**
	 * 光环列表
	 */
	protected List<RoleRingEntity> roleRingEntities = null;

	/**
	 * 邮件列表
	 */
	private Map<Integer, EmailEntity> emailEntities = null;

	/**
	 * 元素列表
	 */
	private List<ElementEntity> elementEntities = null;

	/**
	 * 阵型列表
	 */
	private List<FormationEntity> formationEntities = null;

	/**
	 * 玩家所有消息 <senderId, (send + recv) Msg>
	 */
	private Map<Integer, TreeSet<MsgEntity>> playerMsgs = null;

	/**
	 * 活动数据存储
	 */
	private Set<ActivityEntity<?>> activitySet = null;

	private WorshipEntity worship;

	/**
	 * 魔王的宝藏对象
	 */
	private PlayerPrinceDevilsEntity playerPrinceDevilsEntity;

	/**
	 * 星魂对象
	 */
	private PlayerStarSoulEntity playerStarSoulEntity;

	/**
	 * 王的后宫数据
	 */
	private HaremActivityEntity haremActivityEntity;

	/**
	 * 聊天皮肤数据
	 */
	private ChatSkinEntity chatSkinEntity;

	/**
	 * GVG领奖数据
	 */
	private GvgRewardEntity gvgRewardEntity;

	/**
	 * 少女的邂逅数据
	 */
	private MaidenEncounterEntity maidenEncounterEntity;

	/**
	 * 图鉴数据
	 */
	protected ArchiveEntity archiveEntity = null;
	
	/**
	 * 武器相生數據
	 */
	protected MutualEntity mutualEntity = null;
	
	/**
	 * 皮膚數據
	 */
	protected RoleSkinEntity roleSkinEntity = null;

	/**
	 * faceBook分享数据
	 */
	private Map<String, FacebookShareEntity> faceBookShareMap = null;

	/**
	 * Avatar
	 */
	private List<AvatarEntity> avatarEntities = null;

	/**
	 * 搜索敌人的时间
	 */
	private transient int decreasePVESearchTime = 0;
	/**
	 * 最後充值物品
	 */
	private String lastRecharage = "";
	/**
	 * syncTime,同步時間(秒)
	 */
	private transient int syncTime = 0;

	private EighteenPrincesEntity eighteenPrincesEntity = null;

	private List<EighteenPrincesHelpHistoryEntity> eighteenPrincesHelpHistoryEntities = null;

	/**
	 * 构造函数
	 *
	 * @param player
	 */
	public PlayerData(Player player) {
		setPlayer(player);
	}

	public void setPlayer(Player player) {
		this.player = player;
	}
	
	public Player getPlayer() {
		return this.player;
	}

	/**********************************************************************************************************
	 * 数据db操作区
	 **********************************************************************************************************/
	/**
	 * 加载玩家信息
	 *
	 * @return
	 */
	public PlayerEntity loadPlayer(String puid, int serverId) {
		if (playerEntity == null) {
			List<PlayerEntity> playerEntitys = DBManager.getInstance()
					.query("from PlayerEntity where puid = ? and serverId = ? and invalid = 0", puid, serverId);
			if (playerEntitys != null && playerEntitys.size() > 0) {
				playerEntity = playerEntitys.get(0);
				try {
					if (playerEntity.getSilentTime() != null
							&& playerEntity.getSilentTime().getTime() > GuaJiTime.getMillisecond()) {
						ServerData.getInstance().addSilentPhone(playerEntity.getPuid());
					}
				} catch (Exception e) {
				}
				playerEntity.convertData();
			}
		}
		return playerEntity;
	}

	/**
	 * 初始化加载faceBook分享
	 *
	 * @param puid
	 * @return
	 */
	public Map<String, FacebookShareEntity> loadFaceBookShare() {
		if (faceBookShareMap == null) {
			faceBookShareMap = new HashMap<String, FacebookShareEntity>();
			List<FacebookShareEntity> facebookList = new ArrayList<>();
			facebookList = DBManager.getInstance().query(
					"from FacebookShareEntity where puid = ? and serverId = ? and invalid = 0 and DATE(createTime) = CURDATE()",
					player.getPuid(), player.getServerId());
			for (FacebookShareEntity facebookShareEntity : facebookList) {
				faceBookShareMap.put(GuaJiTime.getDateString(facebookShareEntity.getCreateTime()), facebookShareEntity);
			}
		}
		return faceBookShareMap;
	}

	/**
	 * 加载玩家登陆信息
	 *
	 * @param puid
	 * @return
	 */
	public LoginEntity loadLogin(String puid, int serverId) {
		String date = GuaJiTime.getDateString();
		if (loginEntity == null || !loginEntity.getDate().equals(date)) {
			List<LoginEntity> loginEntitys = DBManager.getInstance()
					.query("from LoginEntity where puid = ? and serverId = ? and date = ?", puid, serverId, date);
			if (loginEntitys != null && loginEntitys.size() > 0) {
				loginEntity = loginEntitys.get(0);
			} else {
				loginEntitys = DBManager.getInstance()
						.query("from LoginEntity where puid = ? and serverId = ? and date = ?", puid, 0, date);
				if (loginEntitys != null && loginEntitys.size() > 0) {
					loginEntity = loginEntitys.get(0);
					loginEntity.setServerId(serverId);
					loginEntity.notifyUpdate(true);
				} else {
					loginEntity = new LoginEntity();
					loginEntity.setPlayerId(player.getId());
					loginEntity.setPlayerName(player.getName());
					loginEntity.setPuid(puid);
					loginEntity.setServerId(serverId);
					loginEntity.setDate(date);
					DBManager.getInstance().create(loginEntity);
				}

			}
		}
		return loginEntity;
	}

	/**
	 * 加载公会个人信息
	 *
	 * @return
	 */
	public PlayerAllianceEntity loadPlayerAlliance() {
		if (playerAllianceEntity == null) {
			List<PlayerAllianceEntity> playerEntitys = DBManager.getInstance()
					.query("from PlayerAllianceEntity where playerId = ? and invalid = 0", playerEntity.getId());
			if (playerEntitys != null && playerEntitys.size() > 0) {
				playerAllianceEntity = playerEntitys.get(0);
			}
			if (playerAllianceEntity == null) {
				playerAllianceEntity = new PlayerAllianceEntity();
				playerAllianceEntity.setPlayerId(playerEntity.getId());
				DBManager.getInstance().create(playerAllianceEntity);
			}
		}
		playerAllianceEntity.init();
		return playerAllianceEntity;
	}

	/**
	 * 加载角色商城
	 *
	 * @param puid
	 */
	public ShopEntity loadShopData() {
		if (this.shopEntity == null) {
			List<ShopEntity> shopEntities = DBManager.getInstance()
					.query("from ShopEntity where playerId = ? and invalid = 0", playerEntity.getId());
			if (shopEntities != null && shopEntities.size() > 0) {
				this.shopEntity = shopEntities.get(0);
			} else {
				shopEntity = new ShopEntity();
				shopEntity.setPlayerId(playerEntity.getId());
				DBManager.getInstance().create(shopEntity);
			}
		}
		return this.shopEntity;
	}

	/**
	 * 加载玩家状态信息
	 *
	 * @param playerId
	 */
	public StateEntity loadStateEntity() {
		if (stateEntity == null) {
			List<StateEntity> stateEntitys = DBManager.getInstance()
					.query("from StateEntity where playerId = ? and invalid = 0", playerEntity.getId());
			if (stateEntitys != null && stateEntitys.size() > 0) {
				stateEntity = stateEntitys.get(0);
				// 转换字符串到对象
				stateEntity.convertData();

				// 新号上报数据
				//if (stateEntity.getPlatformData() != null && stateEntity.getPlatformData().indexOf("65535") > 0) {
					//CmReportManager.getInstance().reportCmActivePlayer(player, false);
				//}
			} else {
				stateEntity = new StateEntity();
				stateEntity.setPlayerId(playerEntity.getId());
				stateEntity.setCurBattleMap(NewMapCfg.getMinMapId());
				stateEntity.setPassMapId(0);
				stateEntity.setintoLevelTime(GuaJiTime.getSeconds());
				stateEntity.setLastTakeBattleAwardTime(GuaJiTime.getSeconds());
				stateEntity.setEquipBagSize(SysBasicCfg.getInstance().getEquipBagSize());
				stateEntity.setBadgeBagSize(SysBasicCfg.getInstance().getBadgeBagSize());
				stateEntity.setSurplusChallengeTimes(SysBasicCfg.getInstance().getFreeChallengeTimes());
				stateEntity.setFirstFalse(1);
				stateEntity.setFirstSuccess(1);
				stateEntity.setFirstBattle(1);
				stateEntity.setFirstFastBattle(1);
				stateEntity.setRoleFirstFastBattle(1);
				DBManager.getInstance().create(stateEntity);

				// 新号上报数据
				// GsApp.getInstance().reportCmActivePlayer(player);
			}
		}
		return stateEntity;
	}
	
	/**
	 * 加载玩家標記信息
	 *
	 * @param playerId
	 */
	public SignEntity loadSignEntity() {
		if (signEntity == null) {
			List<SignEntity> signEntitys = DBManager.getInstance()
					.query("from SignEntity where playerId = ? and invalid = 0", playerEntity.getId());
			if (signEntitys != null && signEntitys.size() > 0) {
				signEntity = signEntitys.get(0);
				// 转换字符串到对象
				signEntity.convert();

			} else {
				signEntity = SignEntity.valueOf(player);
				DBManager.getInstance().create(signEntity);
			}
		}
		return signEntity;
	}
	
	/**
	 * 加载玩家公會魔典
	 *
	 * @param playerId
	 */
	public GuildBuffEntity loadGuildBuffEntity() {
		if (guildBuffEntity == null) {
			List<GuildBuffEntity> guildbuffEntitys = DBManager.getInstance()
					.query("from GuildBuffEntity where playerId = ? and invalid = 0", playerEntity.getId());
			if (guildbuffEntitys != null && guildbuffEntitys.size() > 0) {
				guildBuffEntity = guildbuffEntitys.get(0);
				// 转换字符串到对象
				guildBuffEntity.convertData();
			} else {
				guildBuffEntity = new GuildBuffEntity();
				guildBuffEntity.setPlayerId(playerEntity.getId());
				DBManager.getInstance().create(guildBuffEntity);
			}
		}
		return guildBuffEntity;
	}

	/**
	 * 加载战斗统计信息
	 *
	 * @return
	 */
	public MapStatisticsEntity loadMapStatistics() {
		if (mapStatisticsEntity == null) {
			List<MapStatisticsEntity> mapStatisticsEntities = DBManager.getInstance()
					.query("from MapStatisticsEntity where playerId = ? and invalid = 0", playerEntity.getId());
			if (mapStatisticsEntities != null && mapStatisticsEntities.size() > 0) {
				mapStatisticsEntity = mapStatisticsEntities.get(0);
			} else {
				mapStatisticsEntity = new MapStatisticsEntity();
				// 策划的需求
				mapStatisticsEntity.setWinRate(81);
				mapStatisticsEntity.setPlayerId(playerEntity.getId());
				mapStatisticsEntity.setMapId(NewMapCfg.getMinMapId());
				DBManager.getInstance().create(mapStatisticsEntity);
			}
		}
		return mapStatisticsEntity;
	}

	/**
	 * 加载战斗统计信息
	 *
	 * @return
	 */
	public MapEntity loadMapEntity() {
		if (mapEntity == null) {
			List<MapEntity> mapEntities = DBManager.getInstance()
					.query("from MapEntity where playerId = ? and invalid = 0", playerEntity.getId());
			if (mapEntities != null && mapEntities.size() > 0) {
				mapEntity = mapEntities.get(0);
				mapEntity.convertMapAttr();
				// //给老玩家发放宝石奖励
				// List<MapAttr> mapAttrList = mapEntity.getMapAttrs();
//				if (!mapEntity.isHasGemPrice()) {
//					StateEntity stateEntity = player.getPlayerData().getStateEntity();
//					if (stateEntity != null && stateEntity.getPassMapId() > 0) {
//						// 给老玩家发放奖励邮件 (经过策划多次确认，这块的奖励就这么愉快的写死了.)
//						AwardItems awardItems = AwardItems.valueOf("30000_11201_" + stateEntity.getPassMapId());
//						MailManager.createSysMail(player.getEntity().getId(), Mail.MailType.Reward_VALUE,
//								GsConst.MailId.PLAYER_LOGIN_GEM_PRICE, "老玩家登录宝石的奖励", awardItems);
//					}
//				}
			} else {
				mapEntity = new MapEntity();
				mapEntity.setPlayerId(playerEntity.getId());
				DBManager.getInstance().create(mapEntity);
			}
			// 设置领取状态
			mapEntity.setHasGemPrice(true);
			mapEntity.notifyUpdate(true);
		}
		return mapEntity;
	}

	/**
	 * 加载角色列表
	 *
	 * @return
	 */
	public List<RoleEntity> loadRoleEntities() {
		if (roleEntities == null) {
			roleEntities = DBManager.getInstance()
					.query("from RoleEntity where playerId = ? and invalid = 0 order by id asc ", playerEntity.getId());
			for (RoleEntity role : roleEntities) {
				role.convertRing();
			}
		}
		return roleEntities;
	}
	
	/**
	 * 加载箴言列表
	 *
	 * @return
	 */
	public List<MottoEntity> loadMottoEntities() {
		if (mottoEntities == null) {
			mottoEntities = DBManager.getInstance()
					.query("from MottoEntity where playerId = ? and invalid = 0 order by id asc ", playerEntity.getId());
			if (mottoEntities == null) {
				mottoEntities = new ArrayList<>();
			}
		}
		return mottoEntities;
	}
	
	/**
	 * 加载秘密留言列表
	 *
	 * @return
	 */
	public List<SecretMsgEntity> loadSecretMsgEntities() {
		if (secretMsgEntities == null) {
			secretMsgEntities = DBManager.getInstance()
					.query("from SecretMsgEntity where playerId = ? and invalid = 0 order by id asc ", playerEntity.getId());
			if (secretMsgEntities == null) {
				secretMsgEntities = new ArrayList<>();
			} else {
				for (SecretMsgEntity aMsgEntity:secretMsgEntities) {
					aMsgEntity.convert();
				}
			}
		}
		return secretMsgEntities;
	}

	/**
	 * 加载物品信息
	 *
	 * @return
	 */
	public List<ItemEntity> loadItemEntities() {
		if (itemEntities == null) {
			itemEntities = DBManager.getInstance()
					.query("from ItemEntity where playerId = ? and invalid = 0 order by id asc", playerEntity.getId());
		}
		return itemEntities;
	}

	/**
	 * 加载装备信息
	 *
	 * @return
	 */
	public List<EquipEntity> loadEquipEntities() {
		if (equipEntities == null) {
			equipEntities = DBManager.getInstance()
					.query("from EquipEntity where playerId = ? and invalid = 0 order by id asc", playerEntity.getId());
		}

		return equipEntities;
	}

	/**
	 * 加载技能信息
	 *
	 * @return
	 */
	public List<BadgeEntity> loadBadgeEntities() {
		if (badgeEntities == null) {
			badgeEntities = DBManager.getInstance()
					.query("from BadgeEntity where playerId = ? and invalid = 0 order by id asc", playerEntity.getId());
			if (badgeEntities == null) {
				badgeEntities = new ArrayList<>();
			} else {
				for (BadgeEntity badgeEntity :badgeEntities) {
					badgeEntity.convertData();
				}
			}
		}
		return badgeEntities;
	}

	public BadgeEntity getBadgeById(long id) {
		if (badgeEntities != null) {
			return badgeEntities.stream().filter(entity -> entity.getId() == id).findFirst().orElse(null);
		}
		return null;
	}

	/**
	 * 加载技能信息
	 *
	 * @return
	 */
	public List<SkillEntity> loadSkillEntities() {
		if (skillEntities == null) {
			skillEntities = DBManager.getInstance()
					.query("from SkillEntity where playerId = ? and invalid = 0 order by id asc", playerEntity.getId());
		}
		return skillEntities;
	}

	/**
	 * 加载光环信息
	 *
	 * @return
	 */
	public List<RoleRingEntity> loadRoleRingEntities() {
		if (roleRingEntities == null) {
			roleRingEntities = DBManager.getInstance().query(
					"from RoleRingEntity where playerId = ? and invalid = 0 order by id asc", playerEntity.getId());
		}
		return roleRingEntities;
	}

	/**
	 * 加载邮件信息
	 *
	 * @return
	 */
	public Map<Integer, EmailEntity> loadEmailEntities() {

		Calendar calendar = GuaJiTime.getCalendar();
		calendar.add(Calendar.DAY_OF_YEAR, -3);
		long time = calendar.getTime().getTime();
		if (emailEntities == null) {
			emailEntities = new TreeMap<Integer, EmailEntity>();
			String hql = "from EmailEntity where playerId = ? and invalid = 0 and effectTime <= ? order by id asc";
			List<EmailEntity> emailList = DBManager.getInstance().query(hql, playerEntity.getId(),
					GuaJiTime.getCalendar().getTime());
			for (EmailEntity emailEntity : emailList) {
				if (emailEntity.getType() == Mail.MailType.Normal_VALUE
						&& emailEntity.getCreateTime().getTime() < time) {
					if (emailEntity.getMailId() == GsConst.MailId.APPLY_ADD_ALLIANCE) {
						// 玩家公会数据
						PlayerAllianceEntity playerAlliance = player.getPlayerData().getPlayerAllianceEntity();
						if (playerAlliance != null) {
							// 公会数据
							AllianceEntity entity = AllianceManager.getInstance()
									.getAlliance(playerAlliance.getAllianceId());
							// 如果当前会长还存在未确认的申请加入邮件，则进行删除
							AllianceManager.getInstance().deleteApplyAddAllianceMsg(player, player.getId(), entity,
									ApplyAddAllianceUpEmail.UPEMAIL_TYPE_1_VALUE, emailEntity);
						}
					}
					// 删除3天之前的纯文字邮件
					emailEntity.delete(false);
					continue;
				}
				emailEntity.convertData();
				// 补丁，竞技场邮件优化，防止参数不一致 TODO
				if (emailEntity.getType() == Mail.MailType.ARENA_VALUE) {
					if (emailEntity.getParamsList().size() != 6) {
						emailEntity.addParams(String.valueOf(0));
					}
				}
				if (emailEntity.getType() == Mail.MailType.ARENA_ALL_VALUE) {
					switch (emailEntity.getMailId()) {
					case GsConst.MailId.ARENA_RANK_UP:
						while (emailEntity.getParamsList().size() < 11) {
							emailEntity.addParams(String.valueOf(0));
						}
						break;
					case GsConst.MailId.ARENA_CHALLANGE_SELF_FAIL:
						while (emailEntity.getParamsList().size() < 8) {
							emailEntity.addParams(String.valueOf(0));
						}
						break;
					case GsConst.MailId.ARENA_CHALLANGE_OTHER_FAIL:
						while (emailEntity.getParamsList().size() < 8) {
							emailEntity.addParams(String.valueOf(0));
						}
						break;
					case GsConst.MailId.ARENA_RANK_DROP_RECORD:
						while (emailEntity.getParamsList().size() < 10) {
							emailEntity.addParams(String.valueOf(0));
						}
						break;
					}
				}
				emailEntities.put(emailEntity.getId(), emailEntity);
			}
		}
		return emailEntities;
	}

	/**
	 * 加载消息信息
	 */
	public Map<Integer, TreeSet<MsgEntity>> loadPlayerMsg() {
		if (playerMsgs == null) {
			playerMsgs = new HashMap<Integer, TreeSet<MsgEntity>>();
			// 加载所有发给我和我回复的消息
			int playerId = playerEntity.getId();
			List<MsgEntity> msgs = DBManager.getInstance()
					.query("from MsgEntity where senderId = ? or recverId = ? and invalid = 0", playerId, playerId);
			for (MsgEntity msg : msgs) {
				int sendPlayerId = msg.getSenderId();
				if (sendPlayerId != playerId) {
					// 把所有发给我的消息按sendPlayerId归类
					if (playerMsgs.containsKey(sendPlayerId)) {
						playerMsgs.get(sendPlayerId).add(msg);
					} else {
						TreeSet<MsgEntity> msgSet = new TreeSet<MsgEntity>();
						msgSet.add(msg);
						playerMsgs.put(sendPlayerId, msgSet);
					}
				} else {
					// 把所有我发送的消息按recvPlayerId归类
					int recvPlayerId = msg.getRecverId();
					if (playerMsgs.containsKey(recvPlayerId)) {
						playerMsgs.get(recvPlayerId).add(msg);
					} else {
						TreeSet<MsgEntity> msgSet = new TreeSet<MsgEntity>();
						msgSet.add(msg);
						playerMsgs.put(recvPlayerId, msgSet);
					}
				}
			}
		}
		return playerMsgs;
	}

	/**
	 * 加载好友列表
	 *
	 * @return
	 */
	public FriendEntity loadFriendEntity() {
		if (getFriendEntity() == null) {
			friendEntity = DBManager.getInstance().fetch(FriendEntity.class,
					"from FriendEntity where playerId = ? and invalid = 0", playerEntity.getId());
			if (friendEntity == null) {
				createFriendEntity();
			}
			friendEntity.convert();
			friendEntity.refresh();
		}
		return friendEntity;
	}

	/**
	 * 获得荣誉商店信息
	 */
	public HonorShopEntity loadHonorShopEntity() {
		if (honorShopEntity == null) {
			honorShopEntity = DBManager.getInstance().fetch(HonorShopEntity.class,
					"from HonorShopEntity where playerId = ? and invalid = 0", playerEntity.getId());
			if (honorShopEntity != null) {
				honorShopEntity.convert();
			} else {
				// 创建新的荣誉商店信息
				honorShopEntity = HonorShopEntity.valueOf(playerEntity.getId());

				DBManager.getInstance().create(honorShopEntity);
			}
		}
		return honorShopEntity;
	}

	/**
	 * 获取商店信息（水晶商店与普通商店合并）
	 */
	public CrystalShopEntity loadCrystalShopEntity() {
		if (crystalShopEntity == null) {
			crystalShopEntity = DBManager.getInstance().fetch(CrystalShopEntity.class,
					"from CrystalShopEntity where playerId = ? and invalid = 0", playerEntity.getId());
			if (crystalShopEntity != null) {
				crystalShopEntity.convert();
			} else {
				// 创建新的荣誉商店信息
				crystalShopEntity = CrystalShopEntity.valueOf(player);
				DBManager.getInstance().create(crystalShopEntity);
			}
		}
		return crystalShopEntity;
	}
	
	/**
	 * 獲取商店折扣信息
	 */
	public ShopDiscountEntity loadShopDiscountEntity() {
		if (shopDiscountEntity == null) {
			shopDiscountEntity = DBManager.getInstance().fetch(ShopDiscountEntity.class,
					"from ShopDiscountEntity where playerId = ? and invalid = 0", playerEntity.getId());
			if (shopDiscountEntity != null) {
				shopDiscountEntity.convert();
			} else {
				// 创建新的荣誉商店信息
				shopDiscountEntity = ShopDiscountEntity.valueOf(player);
				DBManager.getInstance().create(shopDiscountEntity);
			}
		}
		return shopDiscountEntity;
	}
	
	/**
	 * 获取每日商店信息
	 */
	public DailyShopEntity loadDailyShopEntity() {
		if (dailyShopEntity == null) {
			dailyShopEntity = DBManager.getInstance().fetch(DailyShopEntity.class,
					"from DailyShopEntity where playerId = ? and invalid = 0", playerEntity.getId());
			if (dailyShopEntity != null) {
				dailyShopEntity.convert();
			} else {
				// 创建新的荣誉商店信息
				dailyShopEntity = DailyShopEntity.valueOf(player);
				DBManager.getInstance().create(dailyShopEntity);
			}
		}
		return dailyShopEntity;
	}
	
	/**
	 * 获取種族商店信息
	 */
	public RaceShopEntity loadRaceShopEntity() {
		if (raceShopEntity == null) {
			raceShopEntity = DBManager.getInstance().fetch(RaceShopEntity.class,
					"from RaceShopEntity where playerId = ? and invalid = 0", playerEntity.getId());
			if (raceShopEntity != null) {
				raceShopEntity.convert();
			} else {
				// 创建新的荣誉商店信息
				raceShopEntity = RaceShopEntity.valueOf(player);
				DBManager.getInstance().create(raceShopEntity);
			}
		}
		return raceShopEntity;
	}
	
	/**
	 * 获取神海商店信息
	 */
	public GodSeaShopEntity loadGodSeaShopEntity() {
		if (godseaShopEntity == null) {
			godseaShopEntity = DBManager.getInstance().fetch(GodSeaShopEntity.class,
					"from GodSeaShopEntity where playerId = ? and invalid = 0", playerEntity.getId());
			if (godseaShopEntity != null) {
				godseaShopEntity.convert();
			} else {
				// 创建新的荣誉商店信息
				godseaShopEntity = GodSeaShopEntity.valueOf(player);
				DBManager.getInstance().create(godseaShopEntity);
			}
		}
		return godseaShopEntity;
	}
	
	/**
	 * 获取循環活動商店信息
	 */
	public CycleStageShopEntity loadCycleStageShopEntity() {
		if (cyclestageShopEntity == null) {
			cyclestageShopEntity = DBManager.getInstance().fetch(CycleStageShopEntity.class,
					"from CycleStageShopEntity where playerId = ? and invalid = 0", playerEntity.getId());
			if (cyclestageShopEntity != null) {
				cyclestageShopEntity.convert();
			} else {
				// 创建新的循環活動商店信息
				cyclestageShopEntity = CycleStageShopEntity.valueOf(player);
				DBManager.getInstance().create(cyclestageShopEntity);
			}
		}
		return cyclestageShopEntity;
	}
	/**
	 * 獲取皮膚商店信息
	 * @return
	 */
	public SkinShopEntity loadSkinShopEntity() {
		if (skinShopEntity == null) {
			skinShopEntity = DBManager.getInstance().fetch(SkinShopEntity.class,
					"from SkinShopEntity where playerId = ? and invalid = 0", playerEntity.getId());
			if (skinShopEntity != null) {
				skinShopEntity.convert();
			} else {
				// 创建新的荣誉商店信息
				skinShopEntity = SkinShopEntity.valueOf(player);
				DBManager.getInstance().create(skinShopEntity);
			}
		}
		return skinShopEntity;
	}
	/**
	 * 获取新公會商店信息
	 */
	public GuildShopEntity loadGuildShopEntity() {
		if (guildShopEntity == null) {
			guildShopEntity = DBManager.getInstance().fetch(GuildShopEntity.class,
					"from GuildShopEntity where playerId = ? and invalid = 0", playerEntity.getId());
			if (guildShopEntity != null) {
				guildShopEntity.convert();
			} else {
				// 创建新的荣誉商店信息
				guildShopEntity = GuildShopEntity.valueOf(player);
				DBManager.getInstance().create(guildShopEntity);
			}
		}
		return guildShopEntity;
	}

	/**
	 * 獲取神秘商店信息
	 */
	public MysteryShopEntity loadMysteryShopEntity() {
		if (mysteryShopEntity == null) {
			mysteryShopEntity = DBManager.getInstance().fetch(MysteryShopEntity.class,
					"from MysteryShopEntity where playerId = ? and invalid = 0", playerEntity.getId());
			if (mysteryShopEntity != null) {
				mysteryShopEntity.convert();
			} else {
				// 创建新的荣誉商店信息
				mysteryShopEntity = MysteryShopEntity.valueOf(player);
				DBManager.getInstance().create(mysteryShopEntity);
			}
		}
		return mysteryShopEntity;		
	}
	
	/**
	 * 獲取神秘商店信息
	 */
	public ArenaShopEntity loadArenaShopEntity() {
		if (arenaShopEntity == null) {
			arenaShopEntity = DBManager.getInstance().fetch(ArenaShopEntity.class,
					"from ArenaShopEntity where playerId = ? and invalid = 0", playerEntity.getId());
			if (arenaShopEntity != null) {
				arenaShopEntity.convert();
			} else {
				// 创建新的荣誉商店信息
				arenaShopEntity = ArenaShopEntity.valueOf(player);
				DBManager.getInstance().create(arenaShopEntity);
			}
		}
		return arenaShopEntity;		
	}
	
	/**
	 * 獲取跨服商店信息
	 */
	public CrossShopEntity loadCrossShopEntity() {
		if (crossShopEntity == null) {
			crossShopEntity = DBManager.getInstance().fetch(CrossShopEntity.class,
					"from CrossShopEntity where playerId = ? and invalid = 0", playerEntity.getId());
			if (crossShopEntity != null) {
				crossShopEntity.convert();
			} else {
				// 创建新的荣誉商店信息
				crossShopEntity = CrossShopEntity.valueOf(player);
				DBManager.getInstance().create(crossShopEntity);
			}
		}
		return crossShopEntity;		
	}
	
	/**
	 * 獲取殿堂商店信息
	 */
	public TempleShopEntity loadTempleShopEntity() {
		if (templeShopEntity == null) {
			templeShopEntity = DBManager.getInstance().fetch(TempleShopEntity.class,
					"from TempleShopEntity where playerId = ? and invalid = 0", playerEntity.getId());
			if (templeShopEntity != null) {
				templeShopEntity.convert();
			} else {
				// 创建新的荣誉商店信息
				templeShopEntity = TempleShopEntity.valueOf(player);
				DBManager.getInstance().create(templeShopEntity);
			}
		}
		return templeShopEntity;		
	}
	
	/**
	 * 加载佣兵远征数据并获得实体
	 */
	public MercenaryExpeditionEntity loadMercenaryExpeditionEntity() {
		if (mercenaryExpeditionEntity == null) {
			mercenaryExpeditionEntity = DBManager.getInstance().fetch(MercenaryExpeditionEntity.class,
					"from MercenaryExpeditionEntity where playerId = ? and invalid = 0", playerEntity.getId());
			if (mercenaryExpeditionEntity != null) {
				mercenaryExpeditionEntity.convert();
			} else {
				// 创建数据条目
				mercenaryExpeditionEntity = MercenaryExpeditionEntity.valueOf(playerEntity.getId());
				DBManager.getInstance().create(mercenaryExpeditionEntity);
			}
		}
		return mercenaryExpeditionEntity;
	}

	/**
	 * 获得佣兵远征数据对象对象
	 */

	/**
	 * 加载称号信息
	 *
	 * @return
	 */
	public TitleEntity loadTitleEntity() {
		if (titleEntity == null) {
			titleEntity = DBManager.getInstance().fetch(TitleEntity.class,
					"from TitleEntity where playerId = ? and invalid = 0", playerEntity.getId());
			if (titleEntity != null) {
				titleEntity.convert();
			} else {
				titleEntity = TitleEntity.valueOf(playerEntity.getId());
				DBManager.getInstance().create(titleEntity);
			}
		}
		return titleEntity;
	}

	/**
	 * 加载魔王的宝藏活动信息
	 */
	public PlayerPrinceDevilsEntity loadPrinceDevilsEntity() {
		if (playerPrinceDevilsEntity == null) {
			playerPrinceDevilsEntity = DBManager.getInstance().fetch(PlayerPrinceDevilsEntity.class,
					"from PlayerPrinceDevilsEntity where playerId = ? and invalid = 0", playerEntity.getId());
			if (playerPrinceDevilsEntity == null) {
				playerPrinceDevilsEntity = new PlayerPrinceDevilsEntity();
				playerPrinceDevilsEntity.setPlayerId(player.getId());
				DBManager.getInstance().create(playerPrinceDevilsEntity);
			}
		}
		playerPrinceDevilsEntity.convert();
		return playerPrinceDevilsEntity;
	}

	/**
	 * 加载真气实体
	 *
	 * @return
	 */
	public PlayerTalentEntity loadPlayerTalentEntity() {
		if (playerTalentEntity == null) {
			playerTalentEntity = DBManager.getInstance().fetch(PlayerTalentEntity.class,
					"from PlayerTalentEntity where playerId = ? and invalid = 0", playerEntity.getId());
			if (playerTalentEntity != null) {
				playerTalentEntity.convertData();
			} else {
				playerTalentEntity = new PlayerTalentEntity(playerEntity.getId());
				playerTalentEntity.initElementTalent();
				DBManager.getInstance().create(playerTalentEntity);
			}
		}
		return playerTalentEntity;
	}
	
	/**
	 * 加载翅膀实体
	 *
	 * @return
	 */
	public PlayerWingsEntity loadPlayerWingsEntity() {
		if (playerWingsEntity == null) {
			playerWingsEntity = DBManager.getInstance().fetch(PlayerWingsEntity.class,
					"from PlayerWingsEntity where playerId = ? and invalid = 0", playerEntity.getId());
			if (playerWingsEntity == null) {
				playerWingsEntity = new PlayerWingsEntity(playerEntity.getId());
				DBManager.getInstance().create(playerWingsEntity);
			}
		}
		return playerWingsEntity;
	}

	public PlayerWingsEntity getPlayerWingsEntity() {
		if (playerWingsEntity != null) {
			return playerWingsEntity;
		}
		return null;
	}

	/**
	 * 加载英雄令任务信息
	 *
	 * @return
	 */
	public HeroTokenTaskEntity loadHeroTokenTaskEntity() {
		if (heroTokenTaskEntity == null) {
			heroTokenTaskEntity = DBManager.getInstance().fetch(HeroTokenTaskEntity.class,
					"from HeroTokenTaskEntity where playerId = ? and invalid = 0", playerEntity.getId());
			if (heroTokenTaskEntity != null)
				heroTokenTaskEntity.convert();
			else {
				heroTokenTaskEntity = HeroTokenTaskEntity.valueOf(playerEntity.getId());
				DBManager.getInstance().create(heroTokenTaskEntity);
			}
		}
		return heroTokenTaskEntity;
	}

	/**
	 * 創建主角色
	 *
	 * @param roleItemId
	 * @return
	 */
	public RoleEntity createMainRole(int roleItemId, String roleName) {
//		RoleCfg roleCfg = ConfigManager.getInstance().getConfigByKey(RoleCfg.class, roleItemId);
//		if (roleCfg == null) {
//			return null;
//		}

		// 创建实体对象
		RoleEntity roleEntity = new RoleEntity();
		roleEntity.setPlayerId(playerEntity.getId());
		roleEntity.setType(GsConst.RoleType.MAIN_ROLE);
		roleEntity.setItemId(roleItemId);
		roleEntity.setName(roleName);
		roleEntity.setAttr(0);
		playerEntity.setName(roleName);

		// 注意等级修复
		long exp = 0;

		roleEntity.setExp(exp);
		roleEntity.setLevel(1);
		roleEntity.setStarLevel(1);
		// 创建主角色，默认给装备
//		List<EquipEntity> needSyncDbList = new LinkedList<EquipEntity>();
//
//		EquipEntity equipEntity = EquipUtil.generateEquip(player, Integer.parseInt(roleCfg.getEquip5()), 10000);
//
//		if (equipEntity != null) {
//			needSyncDbList.add(equipEntity);
//		}
//
//		// db创建装备
//		EquipUtil.createEquipsSync(needSyncDbList);

		// 同步更改玩家等级信息
		playerEntity.setExp(exp);
		playerEntity.setLevel(1);

		// 从db创建
		if (!DBManager.getInstance().create(roleEntity)) {
			return null;
		}

		// 添加到角色列表
		roleEntities.add(roleEntity);

		// 刷新属性
		PlayerUtil.refreshOnlineAttribute(this, roleEntity);

		return roleEntity;
	}

	/**
	 * 创建隨從对象
	 *
	 * @param itemId
	 * @return
	 */
	public void createMercenary(int itemId) {
		Map<Object, Hero_NGListCfg> roleCfgMap = ConfigManager.getInstance().getConfigMap(Hero_NGListCfg.class);
		if (roleCfgMap == null || roleCfgMap.size() <= 0) {
			return;
		}
		if (itemId == 0) {
			for (Hero_NGListCfg cfg : roleCfgMap.values()) {
				if (cfg.getType() != 1) { // 1.主角
					createRole(cfg);
				}
			}
			player.getPlayerData().syncRoleInfo(0);
		} else {
			Hero_NGListCfg roleCfg = ConfigManager.getInstance().getConfigByKey(Hero_NGListCfg.class, itemId);
			if (roleCfg == null) {
				return;
			}
			RoleEntity roleEntity = createRole(roleCfg);
			player.getPlayerData().syncRoleInfo(roleEntity.getId());
		}
	}

	/**
	 * 創建隨從實體
	 *
	 * @param cfg
	 * @return
	 */
	private RoleEntity createRole(Hero_NGListCfg cfg) {
		// 当前等级对应的属性
		RoleEntity roleEntity = new RoleEntity();
		roleEntity.setPlayerId(playerEntity.getId());
		roleEntity.setType(cfg.getType());
		roleEntity.setItemId(cfg.getId());
		roleEntity.setName("");
		roleEntity.setLevel(1);
		roleEntity.setAttr(cfg.getattr());
		roleEntity.setStarLevel(cfg.getStar());
		roleEntity.setSoulCount(0);
		if (Hero_NGListCfg.isFreeIdx(cfg.getId())) {
			roleEntity.setRoleState(Const.RoleActiviteState.IS_ACTIVITE_VALUE);
			RoleRelatedCfg RRcfg = ConfigManager.getInstance().getConfigByKey(RoleRelatedCfg.class,roleEntity.getItemId());
			if (RRcfg != null) {
				roleEntity.setSoulCount(RRcfg.getLimitCount());
			}
			player.getPlayerData().createSecretMsg(roleEntity.getItemId());
		} else {
			roleEntity.setRoleState(Const.RoleActiviteState.NOT_ACTIVITE_VALUE);
		}
//		if (cfg.getType() == Const.roleType.MAIN_ROLE_VALUE) {
//			roleEntity.setStatus(Const.RoleStatus.FIGHTING_VALUE);
//		} else {
//		roleEntity.setStatus(Const.RoleStatus.RESTTING_VALUE);
//		}
		roleEntity.setExp(0);
		roleEntity.setStageLevel(1);
//		roleEntity.setmetaStr(cfg.toString());
		// 刷新属性
		PlayerUtil.refreshOnlineAttribute(this, roleEntity);
		roleEntity.convert();
		// 从db创建
		roleEntity.notifyCreate();
		// 添加到角色列表
		roleEntities.add(roleEntity);
		return roleEntity;
	}
	
	/**
	 * 創建箴言
	 *
	 * @param itemId
	 * @return
	 */
	public void createMotto(int itemId) {
		Map<Object, MottoCfg> mottoCfgMap = ConfigManager.getInstance().getConfigMap(MottoCfg.class);
		if (mottoCfgMap == null || mottoCfgMap.size() <= 0) {
			return;
		}
		if (itemId == 0) {
			for (MottoCfg cfg : mottoCfgMap.values()) {
				createSingleMotto(cfg);
			}
			//player.getPlayerData().syncRoleInfo(0);
		} else {
			MottoCfg mottoCfg = ConfigManager.getInstance().getConfigByKey(MottoCfg.class, itemId);
			if (mottoCfg == null) {
				return;
			}
			createSingleMotto(mottoCfg);
			//player.getPlayerData().syncRoleInfo(roleEntity.getId());
		}
	}
	
	/**
	 * 創建一筆箴言實體
	 *
	 * @param cfg
	 * @return
	 */
	private MottoEntity createSingleMotto(MottoCfg cfg) {
		// 当前等级对应的属性
		MottoEntity mottoEntity = new MottoEntity();
		mottoEntity.setPlayerId(playerEntity.getId());
		mottoEntity.setItemId(cfg.getId());
		mottoEntity.setStar(0);

		// 从db创建
		mottoEntity.notifyCreate();
		// 添加到角色列表
		mottoEntities.add(mottoEntity);
		return mottoEntity;
	}
	
	/**
	 * 創建箴言
	 *
	 * @param itemId
	 * @return
	 */
	public void createSecretMsg(int itemId) {
		// 当前等级对应的属性
		SecretMsgEntity SMsgEntity = new SecretMsgEntity();
		SMsgEntity.setPlayerId(playerEntity.getId());
		SMsgEntity.setItemId(itemId);
		SMsgEntity.setChoiceMsgMap(SecretMsgCfg.initMsg(itemId));
		// 从db创建
		SMsgEntity.notifyCreate();
		// 添加到留言列表
		secretMsgEntities.add(SMsgEntity);
	}
	
	/**
	 * 创建技能对象
	 *
	 * @param skillId
	 * @return
	 */
	public SkillEntity createSkill(RoleEntity roleEntity, int itemId) {
		NewSkillCfg skillCfg = ConfigManager.getInstance().getConfigByKey(NewSkillCfg.class, itemId);
		if (skillCfg == null) {
			return null;
		}

		// 创建实体对象
		SkillEntity skillEntity = new SkillEntity();
		skillEntity.setPlayerId(playerEntity.getId());
		skillEntity.setItemId(itemId);
		skillEntity.setSkillLevel(1);
		// 推送技能专精等级事件
		QuestEventBus.fireQuestEvent(QuestEventType.SKILL_ENHANCE_LEVEL, getSpecializeLevel(), player.getXid());
		skillEntity.setRoleId(roleEntity.getId());

		// 从db创建
		if (!DBManager.getInstance().create(skillEntity)) {
			return null;
		}

		// 添加到角色列表
		if (skillEntities == null) {
			skillEntities = new ArrayList<>();
		} 
		skillEntities.add(skillEntity);
		return skillEntity;
	}

	/**
	 * 加载日常任务数据并获得实体
	 */
	public DailyQuestEntity loadDailyQuestEntity() {
		if (dailyQuestEntity == null) {

			dailyQuestEntity = DBManager.getInstance().fetch(DailyQuestEntity.class,
					"from DailyQuestEntity where playerId = ? and invalid = 0", playerEntity.getId());
			if (dailyQuestEntity != null) {
				dailyQuestEntity.convert();
			} else {
				dailyQuestEntity = DailyQuestEntity.valueOf(playerEntity.getId());
				DBManager.getInstance().create(dailyQuestEntity);
			}
		}

		return dailyQuestEntity;
	}
	
	/**
	 * 加载周任务数据并获得实体
	 */
	public WeeklyQuestEntity loadWeeklyQuestEntity() {
		if (weeklyQuestEntity == null) {

			weeklyQuestEntity = DBManager.getInstance().fetch(WeeklyQuestEntity.class,
					"from WeeklyQuestEntity where playerId = ? and invalid = 0", playerEntity.getId());
			if (weeklyQuestEntity != null) {
				weeklyQuestEntity.convert();
			} else {
				weeklyQuestEntity = WeeklyQuestEntity.valueOf(playerEntity.getId());
				DBManager.getInstance().create(weeklyQuestEntity);
			}
		}

		return weeklyQuestEntity;
	}

	/**
	 * 加载7日登陆任务
	 */
	public SevenDayQuestEntity loadSevenDayQuestEntity() {

		if (sevenDayQuestEntity == null) {

			sevenDayQuestEntity = DBManager.getInstance().fetch(SevenDayQuestEntity.class,
					"from SevenDayQuestEntity where playerId = ? and invalid = 0", playerEntity.getId());
			if (sevenDayQuestEntity != null) {
				sevenDayQuestEntity.loadQuest();
			} else {
				sevenDayQuestEntity = SevenDayQuestEntity.valueOf(playerEntity.getId());
				DBManager.getInstance().create(sevenDayQuestEntity);
			}
		}

		return sevenDayQuestEntity;
	}

	/**
	 * 创建技能对象
	 *
	 * @param skillId
	 * @return
	 */
	public RoleRingEntity createRoleRing(RoleEntity roleEntity, int ringItemId) {
		// 创建实体对象
		RoleRingEntity roleRingEntity = new RoleRingEntity();
		roleRingEntity.setPlayerId(playerEntity.getId());
		roleRingEntity.setRoleId(roleEntity.getId());
		roleRingEntity.setItemId(ringItemId);

		// 从db创建
		if (!DBManager.getInstance().create(roleRingEntity)) {
			return null;
		}

		// 添加到角色列表
		roleRingEntities.add(roleRingEntity);
		return roleRingEntity;
	}

	/**
	 * 创建消息对象
	 */
	public MsgEntity createPlayerMsg(int targetPlayerId, int senderSkinId, String content, int moduleId) {
		int myPlayerId = getPlayerEntity().getId();
		if (content == null || "".equals(content) || targetPlayerId == myPlayerId)
			return null;

		// 创建消息
		MsgEntity msg = new MsgEntity(myPlayerId, targetPlayerId, content, moduleId);
		DBManager.getInstance().create(msg);
		// 加入内存
		if (playerMsgs.containsKey(targetPlayerId)) {
			playerMsgs.get(targetPlayerId).add(msg);
		} else {
			TreeSet<MsgEntity> msgSet = new TreeSet<MsgEntity>();
			msgSet.add(msg);
			playerMsgs.put(targetPlayerId, msgSet);
		}

		return msg;
	}

	/**
	 * 获取战斗对象
	 *
	 * @return
	 */
	public List<BattleRole> getBattleRoles(boolean includeMercenary) {
		List<BattleRole> battleRoles = new ArrayList<BattleRole>();

//		RoleInfo.Builder roleInfoBuilder = BuilderUtil.genRoleBuilder(this, this.getMainRole(), equipEntities,
//				skillEntities, elementEntities, badgeEntities);
//
//		battleRoles.add(new BattleRole(playerEntity.getId(), roleInfoBuilder));
//
//		if (includeMercenary) {
//			for (int itemId : this.getFormationByType(1).getFightingArray()) {
//				if (itemId == 0) {
//					continue;
//				}
//
//				RoleEntity roleEntity = this.getMercenaryByItemId(itemId);
//
//				if (roleEntity == null) {
//					continue;
//				}
//
//				if (roleEntity.getStatus() != Const.RoleStatus.FIGHTING_VALUE) {
//					continue;
//				}
//				roleInfoBuilder = BuilderUtil.genRoleBuilder(this, roleEntity, equipEntities, skillEntities,
//						elementEntities, badgeEntities);
//
//				battleRoles.add(new BattleRole(playerEntity.getId(), roleInfoBuilder));
//			}
//		}

		return battleRoles;
	}
	
	/**
	 * 按client获取战斗对象資料
	 *
	 * @return
	 */
	public Map<Integer,NewBattleRole> getNewBattleHeroes(Map<Integer,Integer>mapHeroList) {
		Map<Integer,NewBattleRole> battleRoles = new HashMap<Integer,NewBattleRole>();

		RoleInfo.Builder roleInfoBuilder = BuilderUtil.genRoleBuilder(this, this.getMainRole(), equipEntities,
				skillEntities, elementEntities, badgeEntities);

		//battleRoles.put(0,new NewBattleRole(playerEntity.getId(), roleInfoBuilder,0));

		if (mapHeroList.size() > 0) {
			for (int roleId : mapHeroList.keySet()) {
				if (roleId == 0) {
					continue;
				}

				RoleEntity roleEntity = this.getMercenaryById(roleId);

				if (roleEntity == null) {
					continue;
				}

//				if (roleEntity.getStatus() != Const.RoleStatus.FIGHTING_VALUE) {
//					continue;
//				}
				
				roleInfoBuilder = BuilderUtil.genRoleBuilder(this, roleEntity, equipEntities, skillEntities,
						elementEntities, badgeEntities);
				int pos = mapHeroList.get(roleId);
				battleRoles.put(pos,new NewBattleRole(playerEntity.getId(), roleInfoBuilder,pos));
			}
		}

		return battleRoles;
	}

	/**
	 * 捞取活动数据
	 */
	public void loadActivity() {
		if (activitySet == null) {
			activitySet = new TreeSet<>();
			int playerId = playerEntity.getId();
			List<ActivityEntity<?>> activityEntities = DBManager.getInstance()
					.query("from ActivityEntity where playerId = ? and invalid = 0", playerId);
			if (activityEntities != null && activityEntities.size() > 0) {
				Set<Integer> serverActiveActivityIds = ActivityManager.getInstance().getCurServerActiveActivityIds();
				for (ActivityEntity<?> activityEntity : activityEntities) {
					// 修正格式
					if (activityEntity.getActivityId() == Const.ActivityId.MONTH_CARD_VALUE) {
						String status = activityEntity.getStatusStr();
						if (status.indexOf("AM") >= 0 || status.indexOf("PM") >= 0) {
							JSONObject jsonObject = JSONObject.fromObject(status);
							if (jsonObject != null) {
								String startDateStr = (String) jsonObject.get("startDate");
								try {
									SimpleDateFormat sdf = new SimpleDateFormat("MMM d, yyyy K:m:s a", Locale.ENGLISH);
									SimpleDateFormat chineseSdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
									jsonObject.put("startDate", chineseSdf.format(sdf.parse(startDateStr)));
								} catch (Exception e) {
									MyException.catchException(e);
								}
								activityEntity.setStatusStr(jsonObject.toString());
								activityEntity.notifyUpdate(true);
							}
						}
					}

					ActivityCfg.ActivityItem activityItem = ActivityCfg.getActivityItem(activityEntity.getActivityId());
					if (activityItem != null) {
						if (activityItem.getActivityTimeType() == ActivityTimeType.CYCLE_TIME_OPEN) {
							ActivityTimeCfg timeCfg = ActivityUtil
									.getCurActivityTimeCfg(activityEntity.getActivityId());
							if (timeCfg == null 
									|| activityEntity.getCreateTime().getTime() < timeCfg.getlStartTime()) {
								// 删除过期活动数据,增加判斷活動建立時間小於活動開啟時間就刪除 by jackal 20201015
								activityEntity.delete();
								continue;
							}
						} else if (activityItem.getActivityTimeType() == ActivityTimeType.SERVER_OPEN_DELYS) {
							int activityId = activityEntity.getActivityId();
							if (!serverActiveActivityIds.contains(activityId)) {
								// 删除过期活动数据
								activityEntity.delete();
								continue;
							}
						}
						// 加入活动数据
						activitySet.add(activityEntity);
					}
				}
			}
		}
	}

	/**
	 * 获取活动数据
	 *
	 * @return
	 */
	public ActivityEntity<?> getActivityEntity(int activityId, int stageId) {
		if (activitySet == null) {
			loadActivity();
		}

		for (ActivityEntity<?> activityEntity : activitySet) {
			if (activityEntity.getActivityId() == activityId && activityEntity.getStageId() == stageId) {
				return activityEntity;
			}
		}
		return null;
	}

	/**
	 * 活动完成移除活动
	 *
	 * @param activityId
	 * @param stageId
	 */
	public void removeActivityEntity(int activityId, int stageId) {
		Iterator<ActivityEntity<?>> activityEntityIter = activitySet.iterator();
		while (activityEntityIter.hasNext()) {
			ActivityEntity<?> activityEntity = activityEntityIter.next();
			if (activityEntity != null && activityEntity.getActivityId() == activityId
					&& activityEntity.getStageId() == stageId) {
				activityEntityIter.remove();
				activityEntity.delete();
			}
		}
	}

	/**
	 * 添加活动
	 */
	public void createActivity(ActivityEntity<?> activityEntity) {
		activitySet.add(activityEntity);
		DBManager.getInstance().create(activityEntity);
	}

	/**
	 * 更新活动信息
	 *
	 * @param activityId
	 * @param stageId
	 */
	public void updateActivity(int activityId, int stageId, boolean isLandImmediately) {
		ActivityEntity<?> activityEntity = (ActivityEntity<?>) getActivityEntity(activityId, stageId);
		if (activityEntity != null) {
			activityEntity.notifyUpdate(!isLandImmediately);
		}
	}

	/**
	 * 更新活动信息
	 *
	 * @param activityId
	 * @param stageId
	 */
	public void updateActivity(int activityId, int stageId) {
		updateActivity(activityId, stageId, false);
	}

	/**
	 * 创建好友
	 */
	public void createFriendEntity() {
		friendEntity = new FriendEntity();
		friendEntity.setPlayerId(playerEntity.getId());
		DBManager.getInstance().create(friendEntity);
	}

	/**
	 * 同步玩家信息
	 */
	public void syncPlayerInfo() {
		HPPlayerInfoSync.Builder builder = HPPlayerInfoSync.newBuilder();
		builder.addPlayerInfos(BuilderUtil.genPlayerBuilder(playerEntity, this, playerTalentEntity.getTalentNum()));
		player.sendProtocol(Protocol.valueOf(HP.code.PLAYER_INFO_SYNC_S, builder));
	}

	/**
	 * 同步角色信息(0表示同步所有)
	 *
	 * @param roleId
	 */
	public void syncRoleInfo(int id) {
		HPRoleInfoSync.Builder builder = HPRoleInfoSync.newBuilder();
		for (RoleEntity roleEntity : roleEntities) {
			if (roleEntity.getType() == Const.roleType.MAIN_ROLE_VALUE
					|| roleEntity.getRoleState() == Const.RoleActiviteState.IS_ACTIVITE_VALUE) {
				if (id == 0 || id == roleEntity.getId()) {
					builder.addRoleInfos(BuilderUtil.genRoleBuilder(this, roleEntity, equipEntities, skillEntities,
							elementEntities, badgeEntities));
				}

				if (roleEntity.isArmy()
						&& roleEntity.getRoleState() == Const.RoleActiviteState.IS_ACTIVITE_VALUE) {
					builder.addActiviteRoleId(roleEntity.getId());
				}
			}
		}

		if (builder.getRoleInfosCount() > 0) {
			Protocol protocol = Protocol.valueOf(HP.code.ROLE_INFO_SYNC_S, builder);
			player.sendProtocol(ProtoUtil.compressProtocol(protocol));
		}
	}

	/**
	 * @param itemId 副将编号
	 */
	public void syncMercenarySoulInfo(int itemId) {
		RoleEntity role = player.getPlayerData().getMercenaryByItemId(itemId);

		if (role.isArmy()) {
			//RoleRelatedCfg cfgR = ConfigManager.getInstance().getConfigByKey(RoleRelatedCfg.class, role.getItemId());

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
				return;
			}
			if (cfg.getCost() == null || cfg.getCost().isEmpty()) {
				roleInfoBuilder.setCostSoulCount(0);
			} else {
				ItemInfo info = ItemInfo.valueOf(cfg.getCost());
				roleInfoBuilder.setCostSoulCount((int)info.getQuantity());
			}
			player.sendProtocol(Protocol.valueOf(HP.code.SOULCOUNT_INFO_SYNC_S_VALUE, roleInfoBuilder));
		}

	}

	/**
	 * 同步物品信息
	 */
	public void syncItemInfo(int... ids) {
		HPItemInfoSync.Builder builder = HPItemInfoSync.newBuilder();
		for (Integer id : ids) {
			for (ItemEntity itemEntity : itemEntities) {
				if ((id == 0 || id == itemEntity.getId()) && itemEntity.getItemCount() > 0 && !itemEntity.isInvalid()) {
					builder.addItemInfos(BuilderUtil.genItemBuilder(itemEntity));
				}
			}
		}
		Protocol protocol = Protocol.valueOf(HP.code.ITEM_INFO_SYNC_S, builder);
		player.sendProtocol(protocol);
	}
	
	/**
	 * 同步待回覆信條
	 */
	public void syncSecretMsgInfo() {
		syncSecretMsg.Builder builder = BuilderUtil.genSercretMsgInfoBuilders(player);
		Protocol protocol = Protocol.valueOf(HP.code.SECRET_MESSAGE_SYNC_S, builder);
		player.sendProtocol(protocol);
	}
	
	/**
	 * 發出關卡訊息
	 */
//	public void sendPassMapMsg(int passMapId) {
//		int msgId = SecretMsgCfg.getPassMapMsg(passMapId);
//		if (msgId != 0) {
//			player.getPlayerData().getStateEntity().addSecretMsgId(msgId);
//			player.getPlayerData().getStateEntity().notifyUpdate();
//			player.getPlayerData().syncSecretMsgInfo();	
//		}
//	}

	/**
	 * 同步激活的佣兵角色信息
	 */
	public void syncActivitedMercenaryRoleInfo() {
		HPRoleInfoSync.Builder builder = HPRoleInfoSync.newBuilder();
		for (RoleEntity roleEntity : roleEntities) {
			if ((roleEntity.isArmy()) 
					&& (roleEntity.getRoleState() == Const.RoleActiviteState.IS_ACTIVITE_VALUE)) {
				builder.addRoleInfos(BuilderUtil.genRoleBuilder(this, roleEntity, equipEntities, skillEntities,
						elementEntities, badgeEntities));
				builder.addActiviteRoleId(roleEntity.getId());
			}
		}
		Protocol protocol = Protocol.valueOf(HP.code.ROLE_INFO_SYNC_S, builder);
		player.sendProtocol(ProtoUtil.compressProtocol(protocol));
	}

	/**
	 * 同步物品信息
	 */
	public void syncItemInfo(List<Integer> ids) {
		HPItemInfoSync.Builder builder = HPItemInfoSync.newBuilder();
		for (Integer id : ids) {
			for (ItemEntity itemEntity : itemEntities) {
				if ((id == 0 || id == itemEntity.getId()) && itemEntity.getItemCount() > 0 && !itemEntity.isInvalid()) {
					builder.addItemInfos(BuilderUtil.genItemBuilder(itemEntity));
				}
			}
		}
		Protocol protocol = Protocol.valueOf(HP.code.ITEM_INFO_SYNC_S, builder);
		player.sendProtocol(protocol);
	}

	/**
	 * 同步已穿戴物品信息
	 */
	public void syncDressedItemInfo() {
		HPItemInfoSync.Builder builder = HPItemInfoSync.newBuilder();
		for (ItemEntity itemEntity : itemEntities) {
			if (itemEntity.getItemCount() > 0 && !itemEntity.isInvalid()) {
				ItemCfg itemCfg = ConfigManager.getInstance().getConfigByKey(ItemCfg.class, itemEntity.getItemId());
				if (itemCfg == null) {
					continue;
				}

				if (itemCfg.getType() == Const.toolType.GEM_VALUE) {
					builder.addItemInfos(BuilderUtil.genItemBuilder(itemEntity));
				}
			}
		}
		Protocol protocol = Protocol.valueOf(HP.code.ITEM_INFO_SYNC_S, builder);
		player.sendProtocol(protocol);
	}

	/**
	 * 同步已消耗道具（目前仅用于佣兵）
	 *
	 * @param consumeItems
	 */
	public void syncConsumeItemInfo(ConsumeItems consumeItems) {
		List<Integer> itemIds = new ArrayList<>();
		List<ConsumeItem> list = consumeItems.getBuilder().getConsumeItemList();
		for (ConsumeItem item : list) {
			itemIds.add((int) item.getId());
		}
		if (itemIds.size() > 0) {
			syncItemInfo(itemIds);
		}
	}

	/**
	 * 同步非宝石道具信息
	 */
	public void syncUnDressedItemInfo() {
		HPItemInfoSync.Builder builder = HPItemInfoSync.newBuilder();
		for (ItemEntity itemEntity : itemEntities) {
			if (itemEntity.getItemCount() > 0 && !itemEntity.isInvalid()) {
				ItemCfg itemCfg = ConfigManager.getInstance().getConfigByKey(ItemCfg.class, itemEntity.getItemId());
				if (itemCfg == null) {
					continue;
				}

				if (itemCfg.getType() != Const.toolType.GEM_VALUE) {
					builder.addItemInfos(BuilderUtil.genItemBuilder(itemEntity));
				}
			}
		}
		Protocol protocol = Protocol.valueOf(HP.code.ITEM_INFO_SYNC_S, builder);
		player.sendProtocol(protocol);
	}

	/**
	 * 同步装备信息
	 */
	public void syncEquipInfo(Long... ids) {
		HPEquipInfoSync.Builder builder = HPEquipInfoSync.newBuilder();
		for (Long id : ids) {
			for (EquipEntity equipEntity : equipEntities) {
				if ((id == 0 || id == equipEntity.getId()) && !equipEntity.isInvalid()) {
					builder.addEquipInfos(BuilderUtil.genEquipBuilder(equipEntity));
					// 分批发送
					if (builder.getEquipInfosCount() >= 10) {
						Protocol protocol = Protocol.valueOf(HP.code.EQUIP_INFO_SYNC_S, builder);
						if (id == 0) {
							player.sendProtocol(ProtoUtil.compressProtocol(protocol));
						} else {
							player.sendProtocol(protocol);
						}
						builder = HPEquipInfoSync.newBuilder();
					}
				}
			}
		}

		if (builder.getEquipInfosCount() > 0) {
			player.sendProtocol(Protocol.valueOf(HP.code.EQUIP_INFO_SYNC_S, builder));
		}
	}

	public void syncEquipInfo(List<Long> ids) {
		HPEquipInfoSync.Builder builder = HPEquipInfoSync.newBuilder();
		for (Long id : ids) {
			for (EquipEntity equipEntity : equipEntities) {
				if ((id == 0 || id == equipEntity.getId()) && !equipEntity.isInvalid()) {
					builder.addEquipInfos(BuilderUtil.genEquipBuilder(equipEntity));
					// 分批发送
					if (builder.getEquipInfosCount() >= 10) {
						Protocol protocol = Protocol.valueOf(HP.code.EQUIP_INFO_SYNC_S, builder);
						if (id == 0) {
							player.sendProtocol(ProtoUtil.compressProtocol(protocol));
						} else {
							player.sendProtocol(protocol);
						}
						builder = HPEquipInfoSync.newBuilder();
					}
				}
			}
		}

		if (builder.getEquipInfosCount() > 0) {
			player.sendProtocol(Protocol.valueOf(HP.code.EQUIP_INFO_SYNC_S, builder));
		}
	}

	/**
	 * 同步元素信息
	 */
	public void syncElementInfo(Long... ids) {
		HPElementInfoSync.Builder builder = HPElementInfoSync.newBuilder();
		if (ids.length == 0) {
			for (ElementEntity elementEntity : getElementEntities()) {
				if (!elementEntity.isInvalid()) {
					builder.addElements(BuilderUtil.genElementBuilder(elementEntity));
					// 分批发送
					if (builder.getElementsCount() >= 10) {
						Protocol protocol = Protocol.valueOf(HP.code.ELEMENT_INFO_SYNC_S_VALUE, builder);
						player.sendProtocol(ProtoUtil.compressProtocol(protocol));
						builder = HPElementInfoSync.newBuilder();
					}
				}
			}
		}
		for (Long id : ids) {
			for (ElementEntity elementEntity : getElementEntities()) {
				if ((id == 0 || id == elementEntity.getId()) && !elementEntity.isInvalid()) {
					builder.addElements(BuilderUtil.genElementBuilder(elementEntity));
					// 分批发送
					if (builder.getElementsCount() >= 10) {
						Protocol protocol = Protocol.valueOf(HP.code.ELEMENT_INFO_SYNC_S_VALUE, builder);
						if (id == 0) {
							player.sendProtocol(ProtoUtil.compressProtocol(protocol));
						} else {
							player.sendProtocol(protocol);
						}
						builder = HPElementInfoSync.newBuilder();
					}
				}
			}
		}

		if (builder.getElementsCount() > 0) {
			player.sendProtocol(Protocol.valueOf(HP.code.ELEMENT_INFO_SYNC_S_VALUE, builder));
		}
	}

	/**
	 * 同步装备信息,新获得的装备
	 */
	public void syncEquipInfoReward(Long... ids) {
		HPEquipInfoSync.Builder builder = HPEquipInfoSync.newBuilder();
		builder.setIsReward(true);
		for (Long id : ids) {
			for (EquipEntity equipEntity : equipEntities) {
				if ((id == 0 || id == equipEntity.getId()) && !equipEntity.isInvalid()) {
					builder.addEquipInfos(BuilderUtil.genEquipBuilder(equipEntity));
					// 分批发送
					if (builder.getEquipInfosCount() >= 10) {
						Protocol protocol = Protocol.valueOf(HP.code.EQUIP_INFO_SYNC_S, builder);
						if (id == 0) {
							player.sendProtocol(ProtoUtil.compressProtocol(protocol));
						} else {
							player.sendProtocol(protocol);
						}
						builder = HPEquipInfoSync.newBuilder();
					}
				}
			}
		}

		if (builder.getEquipInfosCount() > 0) {
			player.sendProtocol(Protocol.valueOf(HP.code.EQUIP_INFO_SYNC_S, builder));
		}
	}

	/**
	 * 同步已装备的装备信息
	 */
	public void syncDressedEquipInfo() {
		HPEquipInfoSync.Builder builder = HPEquipInfoSync.newBuilder();
		for (EquipEntity equipEntity : equipEntities) {
			if (!equipEntity.isInvalid() && EquipUtil.getEquipDressRole(player, equipEntity) != null) {
				builder.addEquipInfos(BuilderUtil.genEquipBuilder(equipEntity));
				// 分批发送
				if (builder.getEquipInfosCount() >= 10) {
					Protocol protocol = Protocol.valueOf(HP.code.EQUIP_INFO_SYNC_S, builder);
					player.sendProtocol(ProtoUtil.compressProtocol(protocol));
					builder = HPEquipInfoSync.newBuilder();
				}
			}
		}

		if (builder.getEquipInfosCount() > 0) {
			player.sendProtocol(Protocol.valueOf(HP.code.EQUIP_INFO_SYNC_S, builder));
		}
	}

	public void syncBadgeInfo(Long... ids) {
		Badge.HPMysticalDressInfoSync.Builder builder = Badge.HPMysticalDressInfoSync.newBuilder();
		if (ids.length == 0) {
			builder.setType(Badge.DRESS_SYNC_TYPE.All);
			for (BadgeEntity badgeEntity : badgeEntities) {
				if (!badgeEntity.isInvalid()) {
					builder.addDresses(genBadgeInfo(badgeEntity));
				}
			}
		} else {
			builder.setType(Badge.DRESS_SYNC_TYPE.UPDATE);
			for (Long id : ids) {
				for (BadgeEntity badgeEntity : badgeEntities) {
					if ((id == badgeEntity.getId()) && !badgeEntity.isInvalid()) {
						builder.addDresses(genBadgeInfo(badgeEntity));
					}
				}
			}
		}
		player.sendProtocol(Protocol.valueOf(HP.code.BADGE_INFO_SYNC_S_VALUE, builder));
	}
	
	/**
	 * 徽章信息
	 */
	private Badge.MysticalDressInfo.Builder genBadgeInfo(BadgeEntity badgeEntity) {
		Badge.MysticalDressInfo.Builder badgeInfo = Badge.MysticalDressInfo.newBuilder();
		badgeInfo.setId(badgeEntity.getId());
		badgeInfo.setEquipId(badgeEntity.getBadgeId());
		badgeInfo.addAllSkillId(badgeEntity.getSkillList());
		badgeInfo.setAttr(badgeEntity.getAttr());
		badgeInfo.addAllRefineId(badgeEntity.getRefineList());
		badgeInfo.setFusionLock(badgeEntity.getFusionLock());
		RoleEntity roleEntity = getRoleByBadgeId(badgeEntity.getId());
		if (roleEntity != null) {
			badgeInfo.setRoleId(roleEntity.getId());
		}
		//badgeInfo.setExp(badgeEntity.getLevelExp());
		return badgeInfo;
	}
		
	public Motto.MottoInfoSync.Builder genMottoSynInfo(Long... ids) {
		Motto.MottoInfoSync.Builder builder = Motto.MottoInfoSync.newBuilder();
		if (ids.length == 0) {
			builder.setSyncType(2); // all
			for (MottoEntity mottoEntity : mottoEntities) {
				if (!mottoEntity.isInvalid()) {
					builder.addMottos(genMottoInfo(mottoEntity));
				}
			}
		} else {
			builder.setSyncType(1); // update
			for (Long id : ids) {
				for  (MottoEntity mottoEntity : mottoEntities) {
					if ((id == mottoEntity.getId()) && !mottoEntity.isInvalid()) {
						builder.addMottos(genMottoInfo(mottoEntity));
					}
				}
			}
		}
		return builder;
	}
	
	/**
	 * 箴言訊息
	 */
	private Motto.MottoInfo.Builder genMottoInfo(MottoEntity mottoEntity){
		Motto.MottoInfo.Builder mottoInfo = Motto.MottoInfo.newBuilder();
		mottoInfo.setId(mottoEntity.getId());
		mottoInfo.setItemId(mottoEntity.getItemId());
		mottoInfo.setStar(mottoEntity.getStar());
		return mottoInfo;
	}
	
	/**
	 * 同步未装备的装备信息
	 */
	public void syncUnDressedEquipInfo() {
		HPEquipInfoSync.Builder builder = HPEquipInfoSync.newBuilder();
		for (EquipEntity equipEntity : equipEntities) {
			if (!equipEntity.isInvalid() && EquipUtil.getEquipDressRole(player, equipEntity) == null) {
				builder.addEquipInfos(BuilderUtil.genEquipBuilder(equipEntity));
				// 分批发送
				if (builder.getEquipInfosCount() >= 10) {
					Protocol protocol = Protocol.valueOf(HP.code.EQUIP_INFO_SYNC_S, builder);
					player.sendProtocol(ProtoUtil.compressProtocol(protocol));
					builder = HPEquipInfoSync.newBuilder();
				}
			}
		}

		if (builder.getEquipInfosCount() > 0) {
			player.sendProtocol(Protocol.valueOf(HP.code.EQUIP_INFO_SYNC_S, builder));
		}
	}

	/**
	 * 同步未装备的元素信息
	 */
	public void syncUnDressedElementInfo() {
		HPElementInfoSync.Builder builder = HPElementInfoSync.newBuilder();
		for (ElementEntity elementEntity : getElementEntities()) {
			if (!elementEntity.isInvalid() && ElementUtil.getElementDressRole(player, elementEntity) == null) {
				builder.addElements(BuilderUtil.genElementBuilder(elementEntity));
				// 分批发送
				if (builder.getElementsCount() >= 10) {
					Protocol protocol = Protocol.valueOf(HP.code.ELEMENT_INFO_SYNC_S_VALUE, builder);
					player.sendProtocol(ProtoUtil.compressProtocol(protocol));
					builder = HPElementInfoSync.newBuilder();
				}
			}
		}

		if (builder.getElementsCount() > 0) {
			player.sendProtocol(Protocol.valueOf(HP.code.ELEMENT_INFO_SYNC_S_VALUE, builder));
		}
	}

	/**
	 * 同步技能信息
	 */
	public void syncSkillInfo(int... ids) {
		HPSkillInfoSync.Builder builder = HPSkillInfoSync.newBuilder();
		for (Integer id : ids) {
			for (SkillEntity skillEntity : skillEntities) {
				if ((id == 0 || id == skillEntity.getId()) && !skillEntity.isInvalid()) {
					builder.addSkillInfos(BuilderUtil.genSkillBuilder(skillEntity));
				}
			}
		}
		player.sendProtocol(Protocol.valueOf(HP.code.SKILL_INFO_SYNC_S, builder));
		// 同步技能专精信息;
		HPSkillEnhanceOpenState.Builder skillEnhanceState = HPSkillEnhanceOpenState.newBuilder();
		skillEnhanceState.setIsOpen(playerEntity.isSkillEnhanceOpen());
		player.sendProtocol(Protocol.valueOf(HP.code.SKILL_ENHANCE_OPEN_STATE_S, skillEnhanceState));
	}

	/**
	 * 同步光环信息
	 */
	public void syncRingInfo(int... ids) {
		HPRoleRingInfoSync.Builder builder = HPRoleRingInfoSync.newBuilder();
		for (Integer id : ids) {
			for (RoleRingEntity ringEntity : roleRingEntities) {
				if ((id == 0 || id == ringEntity.getId()) && !ringEntity.isInvalid()) {
					builder.addRingInfos(BuilderUtil.genRingInfoBuilder(ringEntity));
				}
			}
		}
		player.sendProtocol(Protocol.valueOf(HP.code.ROLE_RING_INFO_S_VALUE, builder));
	}

	/**
	 * 同步经验副本信息
	 */
	public void syncEliteMapInfo(int... levels) {
		HPEliteMapInfoSync.Builder builder = HPEliteMapInfoSync.newBuilder();
		for (Integer level : levels) {
			for (EliteMapAttr attr : mapEntity.getEliteMapAttr()) {
				if (level == 0 || level == attr.getLevel()) {
					EliteMapInfo.Builder info = EliteMapInfo.newBuilder();
					info.setLevel(attr.getLevel());
					info.setMapId(attr.getMapId());
					builder.addEliteMapInfos(info);
				}
			}
		}
		player.sendProtocol(Protocol.valueOf(HP.code.ELITE_MAP_INFO_SYNC_S_VALUE, builder));
	}

	/**
	 * 同步状态信息
	 */
	public void syncStateInfo() {
		MonthCardStatus monthCardStatus = ActivityUtil.getMonthCardStatus(this);// 月卡本来写好了，又改东西，不想动了，直接在这里加

		player.sendProtocol(Protocol.valueOf(HP.code.STATE_INFO_SYNC_S,
				BuilderUtil.genStateBuilder(stateEntity, monthCardStatus, false)));
	}

	/**
	 * 月卡是否到期
	 */
	public MonthCardStatus isMonthCard() {
		MonthCardStatus monthCardStatus = ActivityUtil.getMonthCardStatus(this);
		if (monthCardStatus.isActivateFlag()) {
			return monthCardStatus;
		}
		return null;
	}

	/**
	 * 周卡是否有属性加成
	 *
	 * @param
	 * @return
	 */
	public int isWeekCardAttr() {
		int attrValue = 0;
		NewWeekCardStatus weekCardStatus = ActivityUtil.getNewWeekCardStatus(this);
		Map<Integer, HashMap<String, Date>> map = weekCardStatus.getCardInfo();
		for (Integer key : map.keySet()) {
			NewWeekCardCfg cfg = ConfigManager.getInstance().getConfigByKey(NewWeekCardCfg.class, key);
			if (cfg.getAddExpBuff() != 0) {
				attrValue += cfg.getAddExpBuff();
			}
		}
		return attrValue;
	}

	/**
	 * 消耗型周卡加成
	 *
	 * @param
	 * @return
	 */
//	public int isConsumeWeekCardAttr() {
//		int attrValue = 0;
//		int activityId = Const.ActivityId.CONSUME_WEEK_CARD_VALUE;
//		ActivityTimeCfg activityTimeCfg = ActivityUtil.getCurActivityTimeCfg(activityId);
//		if (activityTimeCfg != null) {
//			ConWeekCardStatus weekCardStatus = ActivityUtil.getActivityStatus(this, activityId,
//					activityTimeCfg.getStageId(), ConWeekCardStatus.class);
//			if (weekCardStatus != null && weekCardStatus.getLeftDays() > 0) {
//				ConsumeWeekCardCfg weekCardCfg = ConfigManager.getInstance().getConfigByKey(ConsumeWeekCardCfg.class,
//						weekCardStatus.getCurrentActiveCfgId());
//
//				if (weekCardCfg != null) {
//					attrValue = weekCardCfg.getAddExpBuff();
//				}
//			}
//		}
//
//		return attrValue;
//	}

	/**
	 * 同步地图统计信息
	 */
	public void syncMapStatistics() {
		if (mapStatisticsEntity != null) {
			HPMapStatisticsSync.Builder builder = BuilderUtil.genMapStatisticsBuilder(mapStatisticsEntity);

			int coinsRatioVal = 1;
			int expRatioVal = 1;
			int equipRatioVal = 1;

			// vip福利,经验,金币的加成
			float vipCoinRatio = ActivityUtil.getCoinsMapRatio(player.getVipLevel());
			if (vipCoinRatio > 1) {
				coinsRatioVal *= vipCoinRatio;
			}

			float vipExpRatio = ActivityUtil.getExpMapRatio(player.getVipLevel());
			if (vipExpRatio > 1) {
				expRatioVal *= vipExpRatio;
			}

			// 地图双倍掉落活动
			Integer mapCoinsRatio = ActivityUtil.getCoinsMapRatio(playerEntity.getCreateTime());
			if (mapCoinsRatio != null) {
				coinsRatioVal *= mapCoinsRatio;
			}

			Integer mapExpRatio = ActivityUtil.getExpMapRatio(playerEntity.getCreateTime());
			if (mapExpRatio != null) {
				expRatioVal *= mapExpRatio;
			}

			Integer mapEquipRatio = ActivityUtil.getEquipDropActivity(playerEntity.getCreateTime());
			if (mapEquipRatio != null) {
				equipRatioVal *= mapEquipRatio;
			}

			if (coinsRatioVal > 1) {
				builder.setCoinRatio(coinsRatioVal);
			} else {
				builder.setCoinRatio(0);
			}

			if (expRatioVal > 1) {
				builder.setExpRatio(expRatioVal);
			} else {
				builder.setExpRatio(0);
			}

			if (equipRatioVal > 1) {
				builder.setEquipRatio(equipRatioVal);
			} else {
				builder.setEquipRatio(0);
			}

			player.sendProtocol(Protocol.valueOf(HP.code.MAP_STATISTICS_SYNC_S, builder));
		}
	}

	/**
	 * 同步称号信息
	 */
	public void syncTitleInfo() {
		if (titleEntity != null) {
			HPTitleInfoSyncS.Builder builder = HPTitleInfoSyncS.newBuilder();
			TitleInfo.Builder info = TitleInfo.newBuilder();
			for (int titleId : titleEntity.getFinishIdSet()) {
				info.addTitleIds(titleId);
			}
			info.setTitleId(titleEntity.getUseId());
			builder.setTitleInfo(info);
			// 检测玩家称号是否有变化
			if (titleEntity.getIsChange() == 1) {
				builder.setTitleChanged(titleEntity.getIsChange());
				titleEntity.setIsChange(0);
				titleEntity.notifyUpdate(true);
			}
			player.sendProtocol(Protocol.valueOf(HP.code.TITLE_SYNC_S, builder));
		}
	}

	/**
	 * 同步yaya主播信息
	 */
//	public void syncYayaInfo() {
//		HPExchangeGoldBeanRet.Builder ret = HPExchangeGoldBeanRet.newBuilder();
//		ret.setGoldBean(player.getGoldBean());
//		ret.setTodayExchangeCostGold(getPlayerEntity().getExchangeGoldBeanCostRmbGold());
//		ret.setMsgType(Const.YaYaMsgType.PUSH_TYPE_VALUE);
//		player.sendProtocol(Protocol.valueOf(HP.code.YAYA_EXCHANGE_GOLD_BEAN_S_VALUE, ret));
//	}

	/**
	 * 同步 Adjust 战力事件
	 */
	public void syncAdjustScoreEvent() {

		AdjustEventUtil.sentAdjustEventInfo(player, GsConst.AdjustActionType.CE30000, 0);
	}

	/**
	 * 同步新手引导信息
	 *
	 * @return
	 */
	public void syncGuideInfo() {
		if (stateEntity != null) {
			Map<Integer, Integer> guideMap = stateEntity.getGuideMap();
			HPGuideInfoSync.Builder builder = HPGuideInfoSync.newBuilder();
//			if (player.getLevel() > 35) {
//				for (int i = 1; i < 15; i++) {
//					GuideInfoBean.Builder bean = GuideInfoBean.newBuilder();
//					bean.setGuideId(i);
//					bean.setStep(0);
//					builder.addGuideInfoBean(bean);
//				}
//			} else {
			for (Integer key : guideMap.keySet()) {
				int value = guideMap.get(key);
				if (key == GuideType.FIRST_STEP && guideMap.size() > 1 && value != 0) {
					value = 0;
					stateEntity.addGuideMap(key, value);
					stateEntity.notifyUpdate();
				}
				GuideInfoBean.Builder bean = GuideInfoBean.newBuilder();
				bean.setGuideId(key);
				bean.setStep(value);
				builder.addGuideInfoBean(bean);
//				}
			}
			player.sendProtocol(Protocol.valueOf(HP.code.GUIDE_INFO_SYNC_S, builder));
		}
	}
	/**
	 * 同步新手影片是否撥放
	 *
	 * @return
	 */
	public void syncplaystory() {
		HPPlayStorySync.Builder builder = HPPlayStorySync.newBuilder();
		if (stateEntity != null) {
			builder.setIsDone(stateEntity.getplaystory());
		}
		player.sendProtocol(Protocol.valueOf(HP.code.PLAYSTORYDONE_SYNC_S, builder));
	}
	/**
	 * 同步英雄令任务信息
	 *
	 * @return
	 */
	public void syncHeroTokenTaskInfo() {
		if (heroTokenTaskEntity != null) {
			HPHeroTokenTaskInfoRet.Builder response = HPHeroTokenTaskInfoRet.newBuilder();
			List<HeroTokenTaskBean> taskList = heroTokenTaskEntity.getTaskList();
			for (int i = 0; i < taskList.size(); i++) {
				HeroTokenTaskBean bean = taskList.get(i);
				TaskStatusBean.Builder builder = TaskStatusBean.newBuilder();
				builder.setTaskId(bean.getTaskId());
				builder.setStatus(bean.getCount());
				response.addTaskStatusBeanList(builder);
			}
			// 1是推送2是请求返回
			response.setVersion(1);

			int taskCount = SysBasicCfg.getInstance().getHeroTokenTaskLimit();
			// 终身卡的影响
			ForeverCardStatus foreverCardStatus = ActivityUtil.getActivityStatus(player.getPlayerData(),
					Const.ActivityId.FOREVER_CARD_VALUE, -1, ForeverCardStatus.class);
			if (null != foreverCardStatus && foreverCardStatus.isOpen()) {
				taskCount += SysBasicCfg.getInstance().getHeroTokenTaskUpgrade();
			}
			response.setTaskFinishAlltimes(taskCount);
			response.setTaskFinishLefttimes(taskCount - taskList.size());
			// 构建商店物品
			boolean isOpenActivity = false;
			int activityId = Const.ActivityId.HERO_TOKEN_SHOP_VALUE;
			ActivityTimeCfg timeCfg = ActivityUtil.getCurActivityTimeCfg(activityId);
			if (null != timeCfg) {
				isOpenActivity = true;
			}
			List<HeroTokenShopBean> shopList = heroTokenTaskEntity.getShopList();
			List<BPHShopCfg> shopCfgs = ConfigManager.getInstance().getConfigList(BPHShopCfg.class);
			if (null != shopCfgs && shopCfgs.size() > 0) {
				for (BPHShopCfg shopCfg : shopCfgs) {
					if (null != shopCfg.getAwardItems()) {
						AwardItems.Item item = shopCfg.getAwardItems().getAwardItems().get(0);
						int buyTimes = shopCfg.getCount();
						// 剩余购买次数计算
						for (int i = 0; i < shopList.size(); i++) {
							if (shopList.get(i).getItemId() == item.getId()) {
								buyTimes -= shopList.get(i).getCount();
								break;
							}
						}
						buyTimes = buyTimes < 0 ? 0 : buyTimes;
						int price = isOpenActivity ? shopCfg.getActivityPrice() : shopCfg.getPrice();
						ShopStatusBean.Builder builder = BuilderUtil.createShopBuilder(item, price, buyTimes);
						response.addShopStatusBeanList(builder);
					}
				}
			}

			player.sendProtocol(Protocol.valueOf(HP.code.HERO_TOKEN_TASK_INFO_S_VALUE, response));
		}
	}

	/**********************************************************************************************************
	 * 数据查询区
	 **********************************************************************************************************/
	/**
	 * 获取玩家ID
	 *
	 * @return
	 */
	public int getId() {
		return playerEntity.getId();
	}

	/**
	 * 获取玩家基础数据
	 *
	 * @return
	 */
	public PlayerEntity getPlayerEntity() {
		return playerEntity;
	}

	/**
	 * 设置玩家基础数据
	 *
	 * @param playerInfo
	 */
	public void setPlayerEntity(PlayerEntity playerEntity) {
		this.playerEntity = playerEntity;
	}
	
	/**
	 * 獲取秘密信條列表
	 */
	public List<SecretMsgEntity> getSecretMsgEntities(){
		return secretMsgEntities;
	}

	/**
	 * R 获取角色列表
	 *
	 * @return
	 */
	public List<RoleEntity> getRoleEntities() {
		return roleEntities;
	}

	/**
	 * 获取主角信息
	 *
	 * @return
	 */
	public RoleEntity getMainRole() {
		if (roleEntities != null) {
			for (RoleEntity roleEntity : roleEntities) {
				if (roleEntity.getType() == GsConst.RoleType.MAIN_ROLE) {
					return roleEntity;
				}
			}
		}
		return null;
	}

	/**
	 * 获取佣兵列表
	 *
	 * @return
	 */
	public List<RoleEntity> getMercenary() {
		List<RoleEntity> mercenary = new LinkedList<RoleEntity>();
		if (roleEntities != null && roleEntities.size() > 0) {
			for (RoleEntity roleEntity : roleEntities) {
				if (roleEntity.isArmy()) {
					mercenary.add(roleEntity);
				}
			}
		}
		return mercenary;
	}
	
	/**
	 * 获取英雄詳細資料列表
	 *
	 * @return
	 */
	public List<RoleEntity> getHero() {
		List<RoleEntity> Heroes = new LinkedList<RoleEntity>();
		if (roleEntities != null && roleEntities.size() > 0) {
			for (RoleEntity roleEntity : roleEntities) {
				if (roleEntity.isHero()) {
					Heroes.add(roleEntity);
				}
			}
		}
		return Heroes;
	}
	
	/**
	 * 獲取英雄索引Id列表
	 */
	public Set<Integer> getHeroIdList() {
		Set<Integer> aset = new HashSet<>();
		if (roleEntities != null && roleEntities.size() > 0) {
			for (RoleEntity roleEntity : roleEntities) {
				if (roleEntity.isHero()) {
					aset.add(roleEntity.getId());
				}
			}
		}
		return aset;
	}
	
	/**
	 * 獲取英雄Id轉ItemId列表
	 */
	public List<Integer> getHeroIdtoItemId(List<Integer> tlist) {
		List<Integer> aList = new ArrayList<>();
		for (Integer idxId : tlist) {
			if (idxId <= 0) {
				aList.add(0);
			} else {
				RoleEntity roleEntity = getMercenaryById(idxId);
				if (roleEntity != null) {
					aList.add(roleEntity.getItemId());
				} else {
					aList.add(0);
				}
			}
		}
		return aList;
	}
	
	/**
	 * 獲取隊伍(英雄+精靈)Id列表
	 */
	public Set<Integer> getArmyList() {
		Set<Integer> aset = new HashSet<>();
		if (roleEntities != null && roleEntities.size() > 0) {
			for (RoleEntity roleEntity : roleEntities) {
				if (roleEntity.isArmy()) {
					aset.add(roleEntity.getId());
				}
			}
		}
		return aset;
	}
	
	/**
	 * 獲取精靈Id列表
	 */
	public Set<Integer> getSpriteList() {
		Set<Integer> aset = new HashSet<>();
		if (roleEntities != null && roleEntities.size() > 0) {
			for (RoleEntity roleEntity : roleEntities) {
				if (roleEntity.isSprite()) {
					aset.add(roleEntity.getId());
				}
			}
		}
		return aset;
	}

	/**
	 * 获得已激活佣兵
	 *
	 * @return
	 */
	public List<RoleEntity> getActiviceMercenary() {
		List<RoleEntity> mercenary = new LinkedList<RoleEntity>();
		if (roleEntities != null && roleEntities.size() > 0) {
			for (RoleEntity roleEntity : roleEntities) {
				if ((roleEntity.isArmy())
						&& (roleEntity.getRoleState() == Const.RoleActiviteState.IS_ACTIVITE_VALUE)) {
					mercenary.add(roleEntity);
				}
			}
		}
		return mercenary;
	}
	
	/**
	 * 获得已激活英雄
	 *
	 * @return
	 */
	public List<RoleEntity> getActiviceHero() {
		List<RoleEntity> mercenary = new LinkedList<RoleEntity>();
		if (roleEntities != null && roleEntities.size() > 0) {
			for (RoleEntity roleEntity : roleEntities) {
				if ((roleEntity.isHero())
						&& (roleEntity.getRoleState() == Const.RoleActiviteState.IS_ACTIVITE_VALUE)) {
					mercenary.add(roleEntity);
				}
			}
		}
		return mercenary;
	}
	/**
	 * 取出所有已完成回覆的訊息
	 */
//	public List<Integer> getCompleteMsgId(){
//		List<Integer> aList = new ArrayList<Integer>();
//		for (SecretMsgEntity sEntity:getSecretMsgEntities()) {
//			aList.addAll(sEntity.getChoiceMsgMap().keySet());
//		}
//		return aList;
//	}
	/**
	 * 取出可以發出訊息的英雄
	 * @return
	 */
	public List<Integer> getCanSecretMsgHero(){
		List<Integer> aList = new LinkedList<Integer>();
		List<RoleEntity> heroList = getActiviceHero();
		for (RoleEntity heroEntity :heroList) {
			SecretMsgEntity sEntity = getSecretMsgByItem(heroEntity.getItemId());
			if (sEntity != null) {
				if (sEntity.getMsgCount() < SecretMsgCfg.getHeroMaxMsg(heroEntity.getItemId())) {
					aList.add(sEntity.getItemId());
				}
			}
		}
		return aList;
	}
	/**
	 * 利用英雄ItemId找出英雄相關秘密留言
	 */
	public SecretMsgEntity getSecretMsgByItem(int itemId) {
		SecretMsgEntity retEntity = null;
		if (secretMsgEntities != null) {
			for (SecretMsgEntity sEntity : secretMsgEntities) {
				if (sEntity.getItemId() == itemId) {
					retEntity = sEntity;
				}
			}
		} 
		return retEntity;
	}
	/**
	 * 取出該屬性(出生屬性不是外觀屬性)的英雄
	 * @param attr
	 * @return
	 */
	public List<RoleEntity> getHeroByAttr(int attr){
		List<RoleEntity> mercenary = new LinkedList<RoleEntity>();
		List<RoleEntity> HeroEntites = getActiviceHero();
		for (RoleEntity heroEntity : HeroEntites) {
			if (heroEntity.getAttr() == attr) {
				mercenary.add(heroEntity);
			}
		}
		return mercenary;
	}
	/**
	 * 取出該屬性(原始屬性)戰力最強英雄
	 */
	
	public RoleEntity getBestHeroByAttr(int attr) {
		RoleEntity heroEntity = null;
		List<RoleEntity> HeroEntites = getHeroByAttr(attr);
		if (HeroEntites.size() == 1) {
			heroEntity = HeroEntites.get(0);
		} else if (HeroEntites.size() > 1){
			for (RoleEntity aEntity:HeroEntites) {
				if (heroEntity == null) {
					heroEntity = aEntity;
				} else {
					if (heroEntity.getFightValue() < aEntity.getFightValue()) {
						heroEntity = aEntity;
					} else if (heroEntity.getFightValue() == aEntity.getFightValue()) {
						if (heroEntity.getItemId() > aEntity.getItemId()) {
							heroEntity = aEntity;
						}
					}
				}
			}
		}
		return heroEntity;
	}

	/**
	 * 获得已激活佣兵
	 *
	 * @return
	 */
	public boolean continsMercenaryByItemId(int itemId) {
		if (roleEntities != null && roleEntities.size() > 0) {
			for (RoleEntity roleEntity : roleEntities) {
				if (roleEntity.isArmy()
						&& (roleEntity.getRoleState() == Const.RoleActiviteState.IS_ACTIVITE_VALUE)
						&& (roleEntity.getItemId() == itemId)) {
					return true;
				}
			}
		}
		return false;
	}

	/**
	 * 使用表格ItemId獲取英雄及免費英雄
	 *
	 * @return
	 */
	public RoleEntity getMercenaryByItemId(int roleItemId) {
		for (RoleEntity roleEntity : roleEntities) {
			if (roleEntity.isArmy() && roleEntity.getItemId() == roleItemId) {
				return roleEntity;
			}
		}
		return null;
	}

	/**
	 * 获取佣兵
	 *
	 * @return
	 */
	public RoleEntity getMercenaryById(int roleId) {
		for (RoleEntity roleEntity : roleEntities) {
			if (roleEntity.isArmy() && roleEntity.getId() == roleId) {
				return roleEntity;
			}
		}
		return null;
	}

	/**
	 * 加入英雄
	 *
	 */
	public void addHeroEntity(RoleEntity roleEntity) {
		if ((roleEntities != null)&&(roleEntity != null)) {
			roleEntities.add(roleEntity);
		}
	}
	/**
	 * 移除英雄
	 *
	 */
	public void removeHeroEntity(RoleEntity roleEntity) {
		if ((roleEntities != null)&&(roleEntity != null)) {
			roleEntities.remove(roleEntity);
		}
	}
	/**
	 * 获取物品列表
	 *
	 * @return
	 */
	public List<ItemEntity> getItemEntities() {
		return itemEntities;
	}

	/**
	 * 获取物品
	 *
	 * @return
	 */
	public ItemEntity getItemById(int id) {
		for (ItemEntity itemEntity : itemEntities) {
			if (id == itemEntity.getId()) {
				return itemEntity;
			}
		}
		return null;
	}

	/**
	 * 获取物品
	 *
	 * @return
	 */
	public ItemEntity getItemByItemId(int itemId) {
		for (ItemEntity itemEntity : itemEntities) {
			if (itemId == itemEntity.getItemId()) {
				return itemEntity;
			}
		}
		return null;
	}

	/**
	 * 获取物品id数量映射表
	 *
	 * @return
	 */
	public Map<Integer, Long> getItemIdCountMap() {
		Map<Integer, Long> idCountMap = new HashMap<>();
		for (ItemEntity itemEntity : itemEntities) {
			if (itemEntity.getItemCount() > 0) {
				idCountMap.put(itemEntity.getItemId(), itemEntity.getItemCount());
			}
		}
		return idCountMap;
	}

	/**
	 * 增加物品实体
	 *
	 * @return
	 */
	public void addItemEntity(ItemEntity itemEntity) {
		itemEntities.add(itemEntity);
	}

	/**
	 * 获取装备列表
	 *
	 * @return
	 */
	public List<EquipEntity> getEquipEntities() {
		return equipEntities;
	}

	/**
	 * 获取装备
	 *
	 * @return
	 */
	public EquipEntity getEquipById(long id) {
		if (equipEntities != null) {
			for (EquipEntity equipEntity : equipEntities) {
				if (id == equipEntity.getId()) {
					return equipEntity;
				}
			}
		}
		return null;
	}

	/**
	 * 获取角色
	 *
	 * @return
	 */
	public RoleEntity getRoleById(int id) {
		for (RoleEntity roleEntity : roleEntities) {
			if (id == roleEntity.getId()) {
				return roleEntity;
			}
		}
		return null;
	}

	/**
	 * 获取装备列表
	 *
	 * @return
	 */
	public List<EquipEntity> getEquipByEquipId(int equipId) {
		List<EquipEntity> equipEntityList = new LinkedList<EquipEntity>();
		for (EquipEntity equipEntity : equipEntities) {
			if (equipId == equipEntity.getEquipId()) {
				equipEntityList.add(equipEntity);
			}
		}
		return null;
	}
	
	/**
	 * 获取装备列表
	 *
	 * @return
	 */
	public List<EquipEntity> getEquipByEquipId(int equipId,boolean wearPass) {
		List<EquipEntity> equipEntityList = new LinkedList<EquipEntity>();
		for (EquipEntity equipEntity : equipEntities) {
			if (equipId == equipEntity.getEquipId()) {
				if (wearPass) { // 只計算包包內的
					if (getRoleByEquipId(equipEntity.getId()) == null) { //沒人穿才加入列表
						equipEntityList.add(equipEntity);
					}
				} else {
					equipEntityList.add(equipEntity);
				}
			}
		}
		return equipEntityList;
	}

	/**
	 * 獲取當前戰鬥地圖資訊
	 *
	 * @return
	 */
	public NewMapCfg getCurBattleMap() {
		int curMapId = stateEntity.getCurBattleMap();
		if (curMapId <= 0) {
			curMapId = NewMapCfg.getMinMapId();
		}
		
		NewMapCfg mapCfg = ConfigManager.getInstance().getConfigByKey(NewMapCfg.class, curMapId);

		return mapCfg;
	}

	/**
	 * 获取地图
	 *
	 * @return
	 */
	public NewMapCfg getBattleMap(int mapId) {
		NewMapCfg mapCfg = ConfigManager.getInstance().getConfigByKey(NewMapCfg.class, mapId);
//		ActivityTimeCfg timeCfg = ActivityUtil.getCurActivityTimeCfg(Const.ActivityId.MAP_DROP_REWARD_VALUE);
//		if (timeCfg != null && !timeCfg.isEnd()) {
//			mapCfg = ConfigManager.getInstance().getConfigByKey(MapActivityCfg.class, mapId);
//		}
		return mapCfg;
	}

	public IpAddrEntity getIpAddrEntity() {
		return ipAddrEntity;
	}

	public void setIpAddrEntity(IpAddrEntity ipAddrEntity) {
		this.ipAddrEntity = ipAddrEntity;
	}

	/**
	 * 增加装备实体
	 *
	 * @return
	 */
	public void addEquipEntity(EquipEntity equipEntity) {
		equipEntities.add(equipEntity);
	}

	/**
	 * 移除装备数据
	 *
	 * @param equipEntity
	 */
	public void removeEquipEntity(EquipEntity equipEntity) {
		equipEntities.remove(equipEntity);
	}

	/**
	 * 获取技能列表
	 *
	 * @return
	 */
	public List<SkillEntity> getSkillEntities() {
		return skillEntities;
	}

	/**
	 * 获取光环列表
	 *
	 * @return
	 */
	public List<RoleRingEntity> getRingInfoEntities() {
		return roleRingEntities;
	}

	/**
	 * 增加徽章实体
	 *
	 * @return
	 */
	public void addBadgeEntity(BadgeEntity badgeEntity) {
		badgeEntities.add(badgeEntity);
	}

	/**
	 * 移除装备数据
	 *
	 * @param badgeEntity
	 */
	public void removeBadgeEntity(BadgeEntity badgeEntity) {
		badgeEntities.remove(badgeEntity);
	}

	/**
	 * 获取技能列表
	 *
	 * @return
	 */
	public void removeSkillByItemId(int skillId) {
		Iterator<SkillEntity> iterator = skillEntities.iterator();
		while (iterator.hasNext()) {
			SkillEntity skillEntity = iterator.next();
			if (skillEntity.getItemId() == skillId) {
				skillEntity.delete();
				skillEntity.notifyUpdate(true);

				iterator.remove();
			}
		}
	}

	/**
	 * 获取技能
	 *
	 * @return
	 */
	public SkillEntity getSkillById(int id) {
		if (skillEntities != null) {
			for (SkillEntity skillEntity : skillEntities) {
				if (id == skillEntity.getId()) {
					return skillEntity;
				}
			}
		}
		return null;
	}

	/**
	 * 获取技能
	 *
	 * @return
	 */
	public SkillEntity getSkillByItemId(int itemId) {
		if (skillEntities != null) {
			for (SkillEntity skillEntity : skillEntities) {
				if (itemId == skillEntity.getItemId()) {
					return skillEntity;
				}
			}
		}
		return null;
	}

	/**
	 * 添加技能实体
	 *
	 * @param skillEntity
	 */
	public void addSkillEntity(SkillEntity skillEntity) {
		skillEntities.add(skillEntity);
	}

	/**
	 * 获取光环
	 *
	 * @return
	 */
	public RoleRingEntity getRingInfoById(int id) {
		for (RoleRingEntity roleRingEntity : roleRingEntities) {
			if (id == roleRingEntity.getId()) {
				return roleRingEntity;
			}
		}
		return null;
	}

	/**
	 * 获取光环
	 *
	 * @return
	 */
	public RoleRingEntity getRingInfoByItemId(int itemId) {
		for (RoleRingEntity roleRingEntity : roleRingEntities) {
			if (itemId == roleRingEntity.getItemId()) {
				return roleRingEntity;
			}
		}
		return null;
	}

	/**
	 * 获取佣兵拥有的光环升级次数
	 *
	 * @return
	 */
	public int getRoleRingLvlUpTimes(int roleId) {
		int lvlUpTimes = 0;
		for (RoleRingEntity roleRingEntity : roleRingEntities) {
			if (roleId == roleRingEntity.getRoleId()) {
				lvlUpTimes += roleRingEntity.getLvlUpTimes();
			}
		}
		return lvlUpTimes;
	}

	/**
	 * 清空升级次数
	 */
	public void resetRoleRingLvlUpTimes() {
		for (RoleRingEntity roleRingEntity : roleRingEntities) {
			roleRingEntity.setLvlUpTimes(0);
			roleRingEntity.notifyUpdate();
		}
	}

	/**
	 * 添加光环实体
	 *
	 * @param roleRingEntity
	 */
	public void addRingInfoEntity(RoleRingEntity roleRingEntity) {
		roleRingEntities.add(roleRingEntity);
	}

	/**
	 * 获取商店实体对象
	 *
	 * @return
	 */
	public ShopEntity getShopEntity() {
		return shopEntity;
	}

	/**
	 * 获取水晶商店实体
	 */
	public CrystalShopEntity getCrystalShopEntity() {
		return crystalShopEntity;
	}

	/**
	 * 获取玩家公会实体
	 *
	 * @return
	 */
	public PlayerAllianceEntity getPlayerAllianceEntity() {
		return playerAllianceEntity;
	}

	/**
	 * 获取状态实体
	 *
	 * @return
	 */
	public StateEntity getStateEntity() {
		return stateEntity;
	}
	
	/**
	 * 取得永標實體
	 */
	public SignEntity getSignEntity() {
		return signEntity;
	}
	
	/**
	 * 獲取公會魔典實體
	 *
	 * @return
	 */
	public GuildBuffEntity getGuildBuffEntity() {
		return guildBuffEntity;
	}

	/**
	 * 获取战斗统计实体
	 *
	 * @return
	 */
	public MapStatisticsEntity getMapStatisticsEntity() {
		return mapStatisticsEntity;
	}

	/**
	 * 获取地图状态
	 *
	 * @return
	 */
	public MapEntity getMapEntity() {
		return mapEntity;
	}

	/**
	 * 检测道具数量是否足够
	 *
	 * @param tool
	 * @param itemId
	 * @param count
	 * @return
	 */
	public boolean checkItemEnough(itemType tool, int itemId, int count) {
		if (tool == itemType.TOOL) {
			ItemEntity itemEntity = this.getItemByItemId(itemId);
			if (itemEntity != null && itemEntity.getItemCount() >= count) {
				return true;
			}
		}
		return false;
	}

	/**
	 * 检测道具数量是否足够
	 *
	 * @param tool
	 * @param itemId
	 * @param count
	 * @return
	 */
	public boolean checkItemEnough(ItemInfo itemInfo) {
		if (itemInfo.getType() / GsConst.ITEM_TYPE_BASE == itemType.TOOL.getNumber()) {
			ItemEntity itemEntity = this.getItemByItemId(itemInfo.getItemId());
			if (itemEntity != null && itemEntity.getItemCount() >= itemInfo.getQuantity()) {
				return true;
			}
		}
		return false;
	}

	/**
	 * 根据装备Id获得Role
	 *
	 * @param equipId
	 * @return
	 */
	public RoleEntity getRoleByEquipId(long equipId) {
		EquipEntity equipEntity = getEquipById(equipId);
		if (equipEntity == null) {
			return null;
		}
		EquipCfg equipCfg = ConfigManager.getInstance().getConfigByKey(EquipCfg.class, equipEntity.getEquipId());
		if (equipCfg == null) {
			return null;
		}
		for (RoleEntity role : roleEntities) {
			if ((role.getPartEquipId(equipCfg.getPart()) == equipId) || (role.getPartEquipId(Const.equipPart.WEAPON2_VALUE) == equipId))  {
				return role;
			}
		}
		return null;
	}

	/**
	 * 获取所有的留言
	 */
	public Map<Integer, TreeSet<MsgEntity>> getPlayerMsgs() {
		return playerMsgs;
	}

	/**
	 * 根据Id获得Role
	 *
	 * @param badgeId
	 * @return
	 */
	public RoleEntity getRoleByBadgeId(long badgeId) {
		BadgeEntity badgeEntity = getBadgeById(badgeId);
		if (badgeEntity == null) {
			return null;
		}
		BadgeCfg badgeCfg = ConfigManager.getInstance().getConfigByKey(BadgeCfg.class, badgeEntity.getBadgeId());
		if (badgeCfg == null) {
			return null;
		}
		for (RoleEntity role : roleEntities) {
			if (role.getBadgeMap().containsValue(badgeId)) {
				return role;
			}
		}
		return null;
	}

	/**
	 * 获取徽章背包剩余数量
	 */
	public int getLeftBadgeBagCount() {
		int badgeCount = 0;
		for (BadgeEntity badgeEntity : badgeEntities) {
			if (!badgeEntity.isInvalid() && getRoleByBadgeId(badgeEntity.getId()) == null) {
				badgeCount++;
			}
		}
		int leftCount = stateEntity.getBadgeBagSize() > badgeCount ? stateEntity.getBadgeBagSize() - badgeCount : 0;
		return leftCount;
	}

	/**
	 * 根据元素Id获得Role
	 *
	 * @param equipId
	 * @return
	 */
	public RoleEntity getRoleByElementId(long eleId) {
		ElementEntity elementEntity = getElementById(eleId);
		if (elementEntity == null) {
			return null;
		}
		for (RoleEntity role : roleEntities) {
			if (role.checkElementInDress(eleId)) {
				return role;
			}
		}
		return null;
	}

	/**
	 * 是否有未读留言
	 *
	 * @return
	 */
	public boolean hasNewMsg() {
		Map<Integer, TreeSet<MsgEntity>> myAllMsg = getPlayerMsgs();
		for (TreeSet<MsgEntity> onePlayerMsgs : myAllMsg.values()) {
			if (onePlayerMsgs.size() > 0) {
				for (MsgEntity newestMsg : onePlayerMsgs) {
					if (newestMsg.getMsgType() == Const.FriendChatMsgType.LEAVE_MSG_VALUE
							&& newestMsg.getLastReadTime() < newestMsg.getCreateSysTime()) {
						return true;
					}
				}
			}
		}
		return false;
	}

	/**
	 * 将别人发送给我的msg加入到内存
	 */
	public void addOneToMeMsg(MsgEntity msgEntity) {
		if (playerMsgs.containsKey(msgEntity.getSenderId())) {
			TreeSet<MsgEntity> msgs = playerMsgs.get(msgEntity.getSenderId());
			msgs.add(msgEntity);
		} else {
			TreeSet<MsgEntity> msgs = new TreeSet<MsgEntity>();
			msgs.add(msgEntity);
			playerMsgs.put(msgEntity.getSenderId(), msgs);
		}
	}

	/**
	 * 添加新邮件
	 *
	 * @param emailEntity
	 */
	public void addEmaliEntity(EmailEntity emailEntity) {
		emailEntities.put(emailEntity.getId(), emailEntity);
	}

	public Map<Integer, EmailEntity> getEmailEntities() {
		return emailEntities;
	}

	public EmailEntity getEmailById(int mailId) {
		return emailEntities.get(mailId);
	}

	public boolean removeEmailEntity(EmailEntity emailEntity) {
		if (emailEntities.containsKey(emailEntity.getId())) {
			emailEntities.remove(emailEntity.getId());
			emailEntity.delete(false);
			BehaviorLogger.log4Service(player, Source.EMAIL_REMOVE, Action.EMAIL_REMOVE,
					Params.valueOf("mailid", emailEntity.getMailId()),
					Params.valueOf("playerId", emailEntity.getPlayerId()),
					Params.valueOf("effectTime", emailEntity.getEffectTime()),
					Params.valueOf("createTime", emailEntity.getCreateTime()),
					Params.valueOf("type", emailEntity.getType()), Params.valueOf("content", emailEntity.getContent()));
			return true;
		}
		return false;
	}

	/**
	 * 获得公测字的集齐日期
	 *
	 * @return
	 */
	public Date getGongceCompleteDate() {
		List<ItemCfg> itemCfgs = ItemCfg.getItemCfgByType(Const.toolType.WORDS_EXCHANGE_SPECIAL_VALUE);
		ItemEntity completeItemEntity = null;
		for (ItemCfg itemCfg : itemCfgs) {
			ItemEntity itemEntity = getItemByItemId(itemCfg.getId());
			if (itemEntity != null && itemEntity.getItemCount() >= 1) {
				if (completeItemEntity == null
						|| itemEntity.getCreateTime().getTime() >= completeItemEntity.getCreateTime().getTime()) {
					completeItemEntity = itemEntity;
				}
			} else {
				return null;
			}
		}
		return completeItemEntity.getCreateTime();
	}

	public FriendEntity getFriendEntity() {
		return friendEntity;
	}

	public void setFriendEntity(FriendEntity friendEntity) {
		this.friendEntity = friendEntity;
	}

	public TitleEntity getTitleEntity() {
		return titleEntity;
	}

	public HeroTokenTaskEntity getHeroTokenTaskEntity() {
		return heroTokenTaskEntity;
	}

	public MercenaryExpeditionEntity getMercenaryExpeditionEntity() {
		return mercenaryExpeditionEntity;
	}

	/**
	 * 移除消息
	 */
	public void removeMsgs(List<MsgEntity> msgList) {
		for (TreeSet<MsgEntity> msgSet : playerMsgs.values()) {
			Iterator<MsgEntity> iter = msgSet.iterator();
			while (iter.hasNext()) {
				MsgEntity msg = iter.next();
				if (msgList.indexOf(msg) >= 0) {
					iter.remove();
					msg.delete();
				}
			}
		}
	}

	/**
	 * 获取在线玩家快照数据
	 *
	 * @return
	 */
	public PlayerSnapshotInfo.Builder getOnlinePlayerSnapshot() {
		refreshOnlinePlayerSnapshot();
		return onlinePlayerSnapshot;
	}

	/**
	 * 刷新在线玩家快照数据
	 */
	public void refreshOnlinePlayerSnapshot() {
		this.onlinePlayerSnapshot = QuickPhotoUtil.genOnlineQuickPhoto(this);
	}

	public List<EmailEntity> getAllExpactRewardmailEntities(int emailClassify) {
		List<EmailEntity> emails = new LinkedList<>();
		for (EmailEntity emailEntity : this.emailEntities.values()) {
			if (emailEntity.getType() == Mail.MailType.Reward_VALUE) {
				continue;
			}
			if (emailEntity.getClassification() == emailClassify) {
				emails.add(emailEntity);
			}
		}
		return emails;
	}

	/**
	 * 取得所有奖励邮件
	 *
	 * @param emailClassify
	 * @return
	 */
	public List<EmailEntity> getAllRewardEmailEntities(int emailClassify) {
		List<EmailEntity> emails = new LinkedList<>();
		for (EmailEntity emailEntity : this.emailEntities.values()) {
			if (emailEntity.getType() != Mail.MailType.Reward_VALUE) {
				continue;
			}
			if (emailEntity.getClassification() == 0 || emailEntity.getClassification() == emailClassify) {
				emails.add(emailEntity);
			}
		}
		return emails;
	}

	/**
	 * 获得膜拜信息
	 */
	public WorshipEntity getWorship() {
		if (this.worship == null) {
			try {
				List<WorshipEntity> worshipList = DBManager.getInstance()
						.limitQuery("from WorshipEntity where playerId = ? ", 0, 1, this.player.getId());
				if (worshipList.size() > 0) {
					worship = worshipList.get(0);
				}
			} catch (Exception e) {
				MyException.catchException(e);
			}
		}
		if (this.worship == null) {
			this.worship = new WorshipEntity();
			this.worship.setPlayerId(player.getId());
			this.worship.setWorshipStamp(0);
			DBManager.getInstance().create(this.worship);
		}
		return this.worship;
	}

	public void setWorship(WorshipEntity worship) {
		this.worship = worship;
	}

	/**
	 * 加载任务数据;
	 *
	 * @return
	 */
	public QuestEntity loadQuestEntity() {
		if (questEntity == null) {
			questEntity = DBManager.getInstance().fetch(QuestEntity.class,
					"from QuestEntity where playerId = ? and invalid = 0", playerEntity.getId());
			if (questEntity != null) {
				questEntity.loadQuest();
			} else {
				questEntity = QuestEntity.valueOf(playerEntity.getId());
				DBManager.getInstance().create(questEntity);
			}
		}
		return questEntity;
	}

	/**
	 * 获取成就任务实体
	 */
	public QuestEntity getQuestEntity() {
		return questEntity;
	}

	/**
	 * 加载玩家星魂信息
	 */
	public PlayerStarSoulEntity loadPlayerStarSoulEntity() {
		if (playerStarSoulEntity == null) {
			int playerId = playerEntity.getId();
			String hql = "from PlayerStarSoulEntity where playerId = ? and invalid = 0";
			playerStarSoulEntity = DBManager.getInstance().fetch(PlayerStarSoulEntity.class, hql, playerId);
			if (playerStarSoulEntity == null) {
				playerStarSoulEntity = new PlayerStarSoulEntity(playerId);
				DBManager.getInstance().create(playerStarSoulEntity);
			}
		}
		return playerStarSoulEntity;
	}

	/**
	 * 加载百花美人信息,类型是常驻类型,分支活动类型有时间控制
	 */
	public HaremActivityEntity loadHaremEntity() {
		if (haremActivityEntity == null) {
			// 从数据库加载
			String hql = "from HaremActivityEntity where playerId = ? and invalid = 0";
			haremActivityEntity = DBManager.getInstance().fetch(HaremActivityEntity.class, hql, playerEntity.getId());
			if (haremActivityEntity == null) {
				haremActivityEntity = HaremManager.initHaremEntity(player);
			}
		}

		// 如果等级足够并且未被初始化
		HaremManager.initNewStrict(player.getLevel(), haremActivityEntity, this.getPlayerEntity().getCreateTime(),
				this.getPlayerEntity().getMergeTime());
		haremActivityEntity.coverToMap();
		return haremActivityEntity;
	}

	/**
	 * 响应任务事件;
	 *
	 * @param event
	 */
	public void onQuestEvent(QuestEvent event) {
		if (this.questEntity == null) {

			return;
		}
		for (QuestItem eachItem : questEntity.getQuestItemMap().values()) {
			if (eachItem == null) {
				continue;
			}
			eachItem.onQuestEvent(event);
		}
	}

	/**
	 * 响应7日之师活动;
	 *
	 * @param event
	 */
	public void onSevenDayQuestEvent(SevenDayEvent event) {

		if (this.sevenDayQuestEntity == null) {
			return;
		}
		
		int surplusTime = player.getPlayerData().getStateEntity().calcNewbieSurplusTime();
		
		if (surplusTime <= 0) {
			return ;
		}

		// 活動開始天数
		int registerDays = GuaJiTime.calcBetweenDays(player.getPlayerData().getStateEntity().getNewbieDate(),
				GuaJiTime.getCalendar().getTime()) + 1;

		//ActivityItem activityItem = ActivityCfg.getActivityItem(Const.ActivityId.ACCUMULATIVE_LOGIN_SEVEN_VALUE);
		Integer keepDays = SysBasicCfg.getInstance().getNewbieDays();
		if (registerDays > keepDays)
			return;
		
		boolean isChange = false;
		SyncQuestItemInfo.Builder syncQuest = SyncQuestItemInfo.newBuilder();
		for (SevenDayQuestItem eachItem : sevenDayQuestEntity.getQuestMap().values()) {
			if (eachItem == null) {
				continue;
			}

			SevenDayQuestCfg questCfg = ConfigManager.getInstance().getConfigByKey(SevenDayQuestCfg.class,
					eachItem.getId());
			if (questCfg == null) {
				continue;
			}

			// 若未到开启时间不会完成
			if (SevenDayEventType.valueOf(questCfg.getTargetType()) != SevenDayEventType.RECHARGE
					&& questCfg.getType() != QuestType.STATEFUL_VALUE && registerDays < questCfg.getDays()) {
				continue;
			}

			if (event.getType() == SevenDayEventType.valueOf(questCfg.getTargetType())) {

				try {
					SevenDayQuestItem cloneQuest = (SevenDayQuestItem) BeanUtils.cloneBean(eachItem);
					eachItem.onQuestEvent(event);
					if (!cloneQuest.chkqual(eachItem)) {
						isChange = true;
						QuestItemInfo.Builder builer = QuestItemInfo.newBuilder();
						builer.setQuestId(eachItem.getId());
						builer.setState(eachItem.getStatus());
						builer.setFinishCount((int) eachItem.getFinishNum());
						syncQuest.addItems(builer);

					}
				} catch (IllegalAccessException | InstantiationException | InvocationTargetException
						| NoSuchMethodException e) {
					e.printStackTrace();
				}

			}
		}
		// 状态更新同步信息
		if (isChange == true) {
			Protocol protocol = Protocol.valueOf(HP.code.SEVENDAY_QUEST_STATUS_UPDATE, syncQuest);
			player.sendProtocol(protocol);
		}

	}

	/**
	 * 根据模板id获取任务;
	 *
	 * @param questItemId
	 * @return
	 */
	public QuestItem getQuestByItemId(int questItemId) {
		for (QuestItem eachItem : this.questEntity.getQuestItemMap().values()) {
			if (eachItem.getItemId() == questItemId) {
				return eachItem;
			}
		}
		return null;
	}

	/**
	 * 获取已完成的任务列表;
	 *
	 * @return
	 */
	public List<Integer> getFinishedQuestList() {
		List<Integer> result = new ArrayList<Integer>();

		for (QuestItem eachItem : this.questEntity.getQuestItemMap().values()) {
			QuestCfg questConfig = ConfigManager.getInstance().getConfigByKey(QuestCfg.class, eachItem.getItemId());
			if (questConfig == null) {
				continue;
			}

			if (eachItem.getState() == QuestState.REWARD_VALUE) {
				result.add(eachItem.getItemId());
			}

			// 等级添加新任务判定
			if (questConfig.getTargetType() == QuestEventType.MAIN_ROLE_LEVEL_VALUE
					&& eachItem.getState() == QuestState.ING_VALUE
					&& questConfig.getNeedCount() <= this.playerEntity.getLevel()) {
				eachItem.setState(QuestState.FINISHED_VALUE);
				eachItem.setFinishedCount(this.playerEntity.getLevel());
			}
		}
		return result;
	}

	/**
	 * 捞取elements
	 */
	public List<ElementEntity> loadElements() {
		if (this.elementEntities == null) {
			this.elementEntities = DBManager.getInstance().query(
					"from ElementEntity where playerId = ? and invalid = 0 order by id asc", playerEntity.getId());
		}
		for (ElementEntity elementEntity : this.elementEntities) {
			elementEntity.convertData();
		}
		return this.elementEntities;
	}

	/**
	 * 捞取阵型数据
	 *
	 * @param elementEntity
	 */

	public List<FormationEntity> loadFormations() {
		if (this.formationEntities == null) {
			this.formationEntities = DBManager.getInstance().query(
					"from FormationEntity where playerId = ? and invalid = 0 order by id asc", playerEntity.getId());
		}

		if (this.formationEntities != null) {

			for (FormationEntity formationEntity : this.formationEntities) {
				formationEntity.convertData();
			}
		}

		if (formationEntities == null || formationEntities.size() == 0) {
			formationEntities = new ArrayList<FormationEntity>();

			// 共8套阵型,1为默认使用阵型，其余8套为client编队阵型
			for (int i = GsConst.FormationType.FormationBegin; i <= GsConst.FormationType.FormationEnd; i++) {
				FormationEntity formationEntity = FormationEntity.valueOf(this.getId(), i);
				DBManager.getInstance().create(formationEntity);

				this.addFormation(formationEntity);
			}
		}

		return this.formationEntities;
	}

	/**
	 * 获取某个类型阵型的数据实体
	 *
	 * @param elementEntity
	 */
	public FormationEntity getFormationByType(int type) {
		if (formationEntities != null && formationEntities.size() > 0) {
			for (FormationEntity formationEntity : this.formationEntities) {
				if (formationEntity.getType() == type) {
					return formationEntity;
				}
			}
		}

		return null;
	}

	/**
	 * 获取所有类型的阵容实体
	 */
	public List<FormationEntity> getFromationEntities() {
		return this.formationEntities;
	}

	/**
	 * 根据阵型ID获取整型数据实体
	 *
	 * @param elementEntity
	 */
	public FormationEntity getFormationById(int id) {
		for (FormationEntity formationEntity : this.formationEntities) {
			if (formationEntity.getId() == id) {
				return formationEntity;
			}
		}

		return null;
	}

	/**
	 * 获取所有阵型entity
	 */
	public List<FormationEntity> getFormationEntities() {
		return this.formationEntities;
	}

	/**
	 * 添加一个阵型
	 *
	 * @param elementEntity
	 */
	public void addFormation(FormationEntity formationEntity) {
		this.formationEntities.add(formationEntity);
	}

	public void setFormationEntities(List<FormationEntity> list) {
		this.formationEntities = list;
	}

	public void addElementEntity(ElementEntity elementEntity) {
		this.getElementEntities().add(elementEntity);
	}

	public ElementEntity getElementById(long eleId) {
		for (ElementEntity ele : getElementEntities()) {
			if (ele.getId() == eleId) {
				return ele;
			}
		}
		return null;
	}

	/**
	 * 移除元素的内存对象
	 *
	 * @param elementEntity
	 */
	public void removeElementEntity(ElementEntity elementEntity) {
		this.getElementEntities().remove(elementEntity);
	}

	public List<ElementEntity> getElementEntities() {
		return elementEntities;
	}

	public void setElementEntities(List<ElementEntity> elementEntities) {
		this.elementEntities = elementEntities;
	}

	public PlayerTalentEntity getPlayerTalentEntity() {
		return playerTalentEntity;
	}

	@SuppressWarnings("unused")
	private final Comparator<EquipEntity> EQUIP_ENHANCE_SORT = new Comparator<EquipEntity>() {

		@Override
		public int compare(EquipEntity o1, EquipEntity o2) {
			if (o2.getStrength() > o1.getStrength()) {
				return -1;
			} else {
				if (o2.getStrength() == o1.getStrength()) {
					return 0;
				}

				return 1;
			}
		}
	};

	/**
	 * 全身神器最高等级
	 *
	 * @param godlyAttrInfo(1=普通神器，2=声望神器)
	 * @return
	 */
	public int getEquipsMaxLevel(int godlyAttrInfo) {
		int level = 0;
		List<Integer> list = new ArrayList<>();
		for (int part = Const.equipPart.HELMET_VALUE; part <= Const.equipPart.NECKLACE_VALUE; part++) {
			long equipId = getMainRole().getPartEquipId(part);
			if (equipId <= 0) {
				level = 0;
				break;
			}

			EquipEntity equipEntity = getEquipById(equipId);
			if (equipEntity != null) {
				if (godlyAttrInfo == 1) {
					list.add(equipEntity.getStarLevel());
				} else {
					list.add(equipEntity.getStarLevel2());
				}
			}
		}

		Collections.sort(list);
		if (list.size() >= Const.equipPart.NECKLACE_VALUE) {
			level = list.get(0);
		}
		return level;
	}

	/**
	 * 获取全身装备中最小强化等级
	 *
	 * @return
	 */
	public int getEquipsEnhanceMinLevel() {
		int level = 0;
		List<Integer> list = new ArrayList<>();
		for (int part = Const.equipPart.HELMET_VALUE; part <= Const.equipPart.NECKLACE_VALUE; part++) {
			long equipId = getMainRole().getPartEquipId(part);
			if (equipId <= 0) {
				level = 0;
				break;
			}

			EquipEntity equipEntity = getEquipById(equipId);
			if (equipEntity != null) {
				list.add(equipEntity.getStrength());
			}
		}

		if (list.size() >= Const.equipPart.NECKLACE_VALUE) {
			Collections.sort(list);
			level = list.get(0);
		} else {
			level = 0;
		}
		return level;
	}

	/**
	 * 获取全身装备套装数量 套装类型(0普通装备;1=R,2=SR,3=SSSR,4=UR)
	 *
	 * @return
	 */
	public int getEquipSuitQualityCount(int suitType) {
		int count = 0;
		for (int part = Const.equipPart.HELMET_VALUE; part <= Const.equipPart.NECKLACE_VALUE; part++) {
			int equipId = (int) getMainRole().getPartEquipId(part);
			if (equipId <= 0) {
				continue;
			}
			EquipEntity entity = player.getPlayerData().getEquipById(equipId);
			if (entity == null) {
				continue;
			}
			EquipCfg equipCfg = ConfigManager.getInstance().getConfigByKey(EquipCfg.class, entity.getEquipId());
			if (equipCfg == null) {
				continue;
			}
			if (equipCfg.getSuitQuality() == suitType) {
				count++;
			}
		}
		return count;
	}

	/**
	 * 获取全身神器
	 *
	 * @param isGodly true 普通神器，false 声望神器
	 * @return
	 */
	@Deprecated
	public int getEquipsGodlyMinLevel(boolean isGodly) {
		int level = 0;
		List<Integer> list = new ArrayList<>();
		for (int part = Const.equipPart.HELMET_VALUE; part <= Const.equipPart.NECKLACE_VALUE; part++) {
			long equipId = getMainRole().getPartEquipId(part);
			if (equipId <= 0) {
				level = 0;
				break;
			}

			EquipEntity equipEntity = getEquipById(equipId);
			if (equipEntity != null) {
				if (isGodly) {
					if (equipEntity.getGodlyAttrId() > 0) {
						list.add(equipEntity.getStarLevel());
					}
				} else {
					if (equipEntity.getGodlyAttrId2() > 0) {
						list.add(equipEntity.getStarLevel2());
					}
				}
			}
		}

		if (list.size() >= Const.equipPart.NECKLACE_VALUE) {
			Collections.sort(list);
			level = list.get(0);
		} else {
			level = 0;
		}
		return level;
	}

	private final Comparator<EquipEntity> EQUIP_COMMON_GOD_LEVEL_SORT = new Comparator<EquipEntity>() {

		@Override
		public int compare(EquipEntity o1, EquipEntity o2) {
			if (o2.getStarLevel() > o1.getStarLevel()) {
				return -1;
			} else {
				if (o2.getStarLevel() == o1.getStarLevel()) {
					return 0;
				}

				return 1;
			}
		}
	};

	/**
	 * 获取装备的所有装备中普通神器星级最小的（必须装备的都是神器）
	 */
	public int getEquipsCommonGodStarMinLevel() {

		for (EquipEntity obj : this.equipEntities) {
			if (obj.getGodlyAttrId() == 0 && obj.getGodlyAttrId2() == 0) {
				return 0;
			}
		}

		int level = 0;

		List<EquipEntity> list = new ArrayList<EquipEntity>();

		list.addAll(this.equipEntities);

		Collections.sort(list, EQUIP_COMMON_GOD_LEVEL_SORT);

		level = list.get(0).getStarLevel();

		return level;
	}

	private final Comparator<EquipEntity> EQUIP_HONOUR_GOD_LEVEL_SORT = new Comparator<EquipEntity>() {

		@Override
		public int compare(EquipEntity o1, EquipEntity o2) {
			if (o2.getStarLevel2() > o1.getStarLevel2()) {
				return -1;
			} else {
				if (o2.getStarLevel2() == o1.getStarLevel2()) {
					return 0;
				}

				return 1;
			}
		}
	};

	/**
	 * 获取装备的所有装备中声望神器星级最小的（必须装备的都是神器）
	 */
	public int getEquipsHonourGodStarMinLevel() {
		for (EquipEntity obj : this.equipEntities) {
			if (obj.getGodlyAttrId() == 0 && obj.getGodlyAttrId2() == 0) {
				return 0;
			}
		}

		int level = 0;

		List<EquipEntity> list = new ArrayList<EquipEntity>();

		list.addAll(this.equipEntities);

		Collections.sort(list, EQUIP_HONOUR_GOD_LEVEL_SORT);

		level = list.get(0).getStarLevel2();

		return level;
	}

	public boolean checkSuitRInDress() {
		int levelUpHunderCount = 0;
		int levelDownHunderCount = 0;

		for (EquipEntity obj : this.equipEntities) {
			EquipCfg equipCfg = ConfigManager.getInstance().getConfigByKey(EquipCfg.class, obj.getEquipId());
			if (equipCfg == null) {
				return false;
			}
			if (equipCfg.getSuitId() > 0) {
				if (equipCfg.getQuality() == 5) {
					if (equipCfg.getLevel() < 100) {
						levelDownHunderCount++;
					} else {
						levelUpHunderCount++;
					}
				}
			}

		}

		if (levelDownHunderCount >= 4) {
			return true;
		}

		if (levelUpHunderCount >= 10) {
			return true;
		}
		return false;
	}

	public boolean checkSuitSRInDress() {
		int levelUpHunderCount = 0;
		int levelDownHunderCount = 0;

		for (EquipEntity obj : this.equipEntities) {
			EquipCfg equipCfg = ConfigManager.getInstance().getConfigByKey(EquipCfg.class, obj.getEquipId());
			if (equipCfg == null) {
				return false;
			}
			if (equipCfg.getSuitId() > 0) {
				if (equipCfg.getQuality() == 8) {
					if (equipCfg.getLevel() < 100) {
						levelDownHunderCount++;
					} else {
						levelUpHunderCount++;
					}
				}
			}

		}

		if (levelDownHunderCount >= 6) {
			return true;
		}

		if (levelUpHunderCount >= 10) {
			return true;
		}
		return false;
	}

	public boolean checkSuitSSRInDress() {
		int levelUpHunderCount = 0;
		int levelDownHunderCount = 0;

		for (EquipEntity obj : this.equipEntities) {
			EquipCfg equipCfg = ConfigManager.getInstance().getConfigByKey(EquipCfg.class, obj.getEquipId());
			if (equipCfg == null) {
				return false;
			}
			if (equipCfg.getSuitId() > 0) {
				if (equipCfg.getQuality() == 9) {
					if (equipCfg.getLevel() < 100) {
						levelDownHunderCount++;
					} else {
						levelUpHunderCount++;
					}
				}
			}

		}

		if (levelDownHunderCount >= 8) {
			return true;
		}

		if (levelUpHunderCount >= 10) {
			return true;
		}
		return false;
	}

	public boolean checkSuitURInDress() {
		int levelUpHunderCount = 0;
		int levelDownHunderCount = 0;

		for (EquipEntity obj : this.equipEntities) {
			EquipCfg equipCfg = ConfigManager.getInstance().getConfigByKey(EquipCfg.class, obj.getEquipId());
			if (equipCfg == null) {
				return false;
			}
			if (equipCfg.getSuitId() > 0) {
				if (equipCfg.getQuality() == 10) {
					if (equipCfg.getLevel() < 100) {
						levelDownHunderCount++;
					} else {
						levelUpHunderCount++;
					}
				}
			}

		}

		if (levelDownHunderCount >= 10) {
			return true;
		}

		if (levelUpHunderCount >= 10) {
			return true;
		}
		return false;
	}

	/**
	 * 是否组装完成
	 *
	 * @return
	 */
	public boolean isAssembleFinish() {
		return player.isAssembleFinish();
	}

	public void setPlayerAllianceEntity(PlayerAllianceEntity playerAllianceEntity) {
		this.playerAllianceEntity = playerAllianceEntity;
	}

	/**
	 * 获取玩家技能专精等级;
	 *
	 * @return 返回的是玩家当前所有技能的等级之和;
	 */
	public int getSpecializeLevel() {
		int level = 1;
		if (!playerEntity.isSkillEnhanceOpen()) {
			return 0;
		}
		if (getMainRole() == null || getSkillEntities() == null) {
			return 0;
		}
		for (SkillEntity eachSkill : this.getSkillEntities()) {
			if (getMainRole().getId() == eachSkill.getRoleId()) {
				level += (eachSkill.getSkillLevel() - 1);
			}
		}
		return level;
	}

	/**
	 * 获取技能
	 *
	 * @return
	 */
	public SkillEntity getSkillEntityInfo(int itemId, RoleEntity entity) {
		List<SkillEntity> skillEntities = getSkillEntities();
		for (SkillEntity skillEntity : skillEntities) {
			if (itemId == skillEntity.getItemId()) {
				if (entity.getId() == skillEntity.getRoleId()) {
					return skillEntity;
				} else {
					continue;
				}
			}
		}
		return null;
	}

	/**
	 * 获取星魂
	 */
	public PlayerStarSoulEntity getPlayerStarSoulEntity() {
		if (playerStarSoulEntity == null) {
			// 客户端有登录请求，防止，数据未加载，客户端发来请求
			loadPlayerStarSoulEntity();
		}
		return playerStarSoulEntity;
	}

	public HaremActivityEntity getHaremActivityEntity() {
		return haremActivityEntity;
	}

	/**
	 * 加载聊天皮肤信息
	 *
	 * @return
	 */
	public ChatSkinEntity loadChatSkinEntity() {
		if (getChatSkinEntity() == null) {
			chatSkinEntity = DBManager.getInstance().fetch(ChatSkinEntity.class,
					"from ChatSkinEntity where playerId = ? and invalid = 0", playerEntity.getId());
			if (chatSkinEntity == null) {
				createChatSkinEntity();
			}
			chatSkinEntity.convert();
		}
		return chatSkinEntity;
	}

	public ChatSkinEntity getChatSkinEntity() {
		return chatSkinEntity;
	}

	/**
	 * 创建聊天皮肤数据
	 */
	public void createChatSkinEntity() {
		chatSkinEntity = new ChatSkinEntity();
		chatSkinEntity.setPlayerId(playerEntity.getId());
		DBManager.getInstance().create(chatSkinEntity);
	}

	/**
	 * 加载GVG领奖数据
	 *
	 * @return
	 */
	public GvgRewardEntity loadGvgRewardEntity() {
		if (gvgRewardEntity == null) {
			gvgRewardEntity = DBManager.getInstance().fetch(GvgRewardEntity.class,
					"from GvgRewardEntity where playerId = ? and invalid = 0", playerEntity.getId());
		}
		if (gvgRewardEntity == null) {
			gvgRewardEntity = GvgRewardEntity.createEntity(playerEntity.getId());
		} else {
			gvgRewardEntity.init();
		}
		return gvgRewardEntity;
	}

	/**
	 * 获取GVG领奖数据
	 *
	 * @return
	 */
	public GvgRewardEntity getGvgRewardEntity() {
		return gvgRewardEntity;
	}

	/**
	 * 获取少女的邂逅信息
	 */
	public MaidenEncounterEntity getMaidenEncounterEntity() {
		return maidenEncounterEntity;
	}

	/**
	 * 加载少女的邂逅活动实例
	 */
	public MaidenEncounterEntity loadMaidenEncounterEntity() {
		int playerId = playerEntity.getId();
		int activityId = Const.ActivityId.MAIDEN_ENCOUNTER_VALUE;
		ActivityTimeCfg cfg = ActivityUtil.getCurActivityTimeCfg(activityId);
		if (null != cfg) {
			int stageId = cfg.getStageId();
			if (maidenEncounterEntity == null) {
				String hql = "from MaidenEncounterEntity where playerId = ? and stageId = ? and invalid = 0";
				maidenEncounterEntity = DBManager.getInstance().fetch(MaidenEncounterEntity.class, hql, playerId,
						stageId);
				if (maidenEncounterEntity == null) {
					maidenEncounterEntity = new MaidenEncounterEntity(playerId, stageId);
					DBManager.getInstance().create(maidenEncounterEntity);
				}
			} else {
				if (stageId != maidenEncounterEntity.getStageId()) {
					maidenEncounterEntity.setInvalid(true);
					maidenEncounterEntity.notifyUpdate(false);
					maidenEncounterEntity = new MaidenEncounterEntity(playerId, stageId);
					DBManager.getInstance().create(maidenEncounterEntity);
				}
			}
		}
		return maidenEncounterEntity;
	}

	public ArchiveEntity getArchiveEntity() {
		return archiveEntity;
	}
	
	public MutualEntity getMutualEntity() {
		return mutualEntity;
	}

	/**
	 * 创建图鉴数据
	 */
	public void createArchiveEntity() {
		archiveEntity = new ArchiveEntity();
		archiveEntity.setPlayerId(playerEntity.getId());
		DBManager.getInstance().create(archiveEntity);
	}
	
	/**
	 * 創建武器相生數據
	 */
	public void createMutualEntity() {
		mutualEntity = new MutualEntity();
		mutualEntity.setPlayerId(playerEntity.getId());
		DBManager.getInstance().create(mutualEntity);
	}

	/**
	 * 加载图鉴数据
	 */
	public ArchiveEntity loadArchiveEntity() {
		if (getArchiveEntity() == null) {
			archiveEntity = DBManager.getInstance().fetch(ArchiveEntity.class,
					"from ArchiveEntity where playerId = ? and invalid = 0", playerEntity.getId());
			if (archiveEntity == null) {
				createArchiveEntity();
			}
			archiveEntity.convert();
		}
		return archiveEntity;
	}
	/**
	 * 加載武器相生數據
	 * @return
	 */
	public MutualEntity loadMutualEntity() {
		if (getMutualEntity() == null) {
			mutualEntity = DBManager.getInstance().fetch(MutualEntity.class,
					"from MutualEntity where playerId = ? and invalid = 0", playerEntity.getId());
			if (mutualEntity == null) {
				createMutualEntity();
			}
			mutualEntity.convert();
		}
		return mutualEntity;
	}
	
	/**
	 * 建立角色皮膚數據
	 */
	public void createRoleSkinEntity() {
		roleSkinEntity = new RoleSkinEntity(playerEntity.getId());
		DBManager.getInstance().create(roleSkinEntity);
	}

	/**
	 * 加载图鉴数据
	 */
	public RoleSkinEntity loadRoleSkinEntity() {
		if (roleSkinEntity == null) {
			roleSkinEntity = DBManager.getInstance().fetch(RoleSkinEntity.class,
					"from RoleSkinEntity where playerId = ? and invalid = 0", playerEntity.getId());
			if (roleSkinEntity == null) {
				createRoleSkinEntity();
			}
			roleSkinEntity.convert();
		}
		return roleSkinEntity;
	}

	public Map<String, FacebookShareEntity> getFaceBookShareMap() {
		return faceBookShareMap;
	}

	public void setFaceBookShareMap(Map<String, FacebookShareEntity> faceBookShareMap) {
		this.faceBookShareMap = faceBookShareMap;
	}

	/**
	 * 加载Avatar列表
	 */
	public List<AvatarEntity> loadAvatarEntities() {
		if (avatarEntities == null) {
			avatarEntities = DBManager.getInstance().query(
					"from AvatarEntity where playerId = ? and invalid = 0 order by id asc ", playerEntity.getId());
			if (avatarEntities == null) {
				avatarEntities = new ArrayList<AvatarEntity>();
			}
		}
		return avatarEntities;
	}

	/**
	 * 添加一个Avatar
	 */
	public boolean addAvatar(int avatarId) {
		AvatarCfg cfg = ConfigManager.getInstance().getConfigByKey(AvatarCfg.class, avatarId);
		if (cfg == null) {
			return false;
		}
		List<AvatarEntity> avatars = loadAvatarEntities();
		for (AvatarEntity e : avatars) {
			if (e.getAvatarId() == avatarId) {
				// 已有，更新结束时间
				if (cfg.getDays() > 0) {
					e.setEndTime(Math.max(GuaJiTime.getMillisecond(), e.getEndTime())
							+ cfg.getDays() * 24L * 60L * 60L * 1000L);
				}
				e.notifyUpdate();
				return true;
			}
		}
		// 未找到，创建
		AvatarEntity avatar = new AvatarEntity();
		avatar.setPlayerId(player.getId());
		avatar.setAvatarId(avatarId);
		if (cfg.getDays() > 0) {
			avatar.setEndTime(GuaJiTime.getMillisecond() + cfg.getDays() * 24L * 60L * 60L * 1000L);
		}
		boolean succ = avatar.notifyCreate();
		if (succ) {
			avatars.add(avatar);
		}
		return true;
	}

	/**
	 *
	 */
	public AvatarEntity getUsedAvatar() {
		List<AvatarEntity> avatars = loadAvatarEntities();
		for (AvatarEntity e : avatars) {
			if (e.getId() == playerEntity.getAvatarId() && !e.isOverdue()) {
				return e;
			}
		}
		return null;
	}

	/**
	 *
	 */
	public int getUsedAvatarId() {
		AvatarEntity usedAvatar = getUsedAvatar();
		return usedAvatar == null ? 0 : usedAvatar.getAvatarId();
	}

	/**
	 *
	 */
	public AvatarEntity getAvatarEntity(int id) {
		List<AvatarEntity> avatars = loadAvatarEntities();
		for (AvatarEntity e : avatars) {
			if (e.getId() == id) {
				return e;
			}
		}
		return null;
	}

	/**
	 * 获取应援对象
	 *
	 * @return
	 */
	public List<BattleRole> getBattleAssistanceRoles() {
		List<BattleRole> battleRoles = new ArrayList<BattleRole>();

//		RoleInfo.Builder roleInfoBuilder = null;
//
//		for (int itemId : this.getFormationByType(1).getAssistanceArrayList()) {
//			if (itemId == 0) {
//				continue;
//			}
//
//			RoleEntity roleEntity = this.getMercenaryByItemId(itemId);
//
//			if (roleEntity == null) {
//				continue;
//			}
//
//			if (roleEntity.getStatus() != Const.RoleStatus.FIGHTING_VALUE) {
//				continue;
//			}
//
//			roleInfoBuilder = BuilderUtil.genRoleBuilder(this, roleEntity, equipEntities, skillEntities,
//					elementEntities, badgeEntities);
//
//			battleRoles.add(new BattleRole(playerEntity.getId(), roleInfoBuilder));
//		}
		return battleRoles;
	}

	/**
	 * 副将出阵个数，品质减少的搜索敌人时间
	 *
	 * @return
	 */
	public int getDecreasePVESearchTime() {
		return decreasePVESearchTime;
	}

	/**
	 * 副将出阵个数，品质减少的搜索敌人时间
	 *
	 * @param decreasePVESearchTime
	 */
	public void setDecreasePVESearchTime(int decreasePVESearchTime) {
		this.decreasePVESearchTime = decreasePVESearchTime;
	}

	/**
	 * 取出已同步時間(秒)
	 *
	 * @return
	 */
	public int getsyncTime() {
		return syncTime;
	}

	/**
	 * 設定已同步時間(秒)
	 *
	 * @param syncTime
	 */
	public void setsyncTime(int syncTime) {
		this.syncTime = syncTime;
	}
	
	public EighteenPrincesEntity loadEighteenPrincesEntity() {
		// 39级才开启
//		if (eighteenPrincesEntity == null
//				&& this.playerEntity.getLevel() >= SysBasicCfg.getInstance().getEighteenPrincesOpenLevel()) {
//			eighteenPrincesEntity = EighteenPrincesManager.getInstance().queryEighteenPrincesaEntity(this.getId());
//			if (eighteenPrincesEntity == null) {
//				eighteenPrincesEntity = EighteenPrincesManager.getInstance().createEighteenPrincesaEntity(this.player);
//			}
//			if (eighteenPrincesEntity != null) {
//				eighteenPrincesEntity.convertFormation();
//				eighteenPrincesEntity.convertHelp();
//
//				// 检测数据
//				boolean isUpdate=false;
//				for (int itemId : this.eighteenPrincesEntity.getFormationIds()) {
//					if (!this.eighteenPrincesEntity.getFormationHistoryMap().containsKey(itemId)) {
//						HelpMercenaryInfo helpMercenaryInfo = new HelpMercenaryInfo(100, 100, player.getId());
//						this.eighteenPrincesEntity.addFormationMercenaryInfo(itemId, helpMercenaryInfo);
//						isUpdate=true;
//					}
//				}
//				if(isUpdate) {
//					this.eighteenPrincesEntity.notifyUpdate();
//				}
//			
//			}
//		}

		return eighteenPrincesEntity;
	}

	public List<EighteenPrincesHelpHistoryEntity> loadEighteenPrincesHelpHistoryEntities() {
		if (eighteenPrincesHelpHistoryEntities == null) {
			Calendar calendar = GuaJiTime.getCalendar();
			calendar.add(Calendar.DAY_OF_YEAR, -2);
			eighteenPrincesHelpHistoryEntities = DBManager.getInstance().query(
					"from EighteenPrincesHelpHistoryEntity where playerId = ? and invalid = 0 and createTime > ? order by createTime desc",
					playerEntity.getId(), calendar.getTime());

			if (eighteenPrincesHelpHistoryEntities == null) {
				eighteenPrincesHelpHistoryEntities = new ArrayList<>();
			}
		}
		return eighteenPrincesHelpHistoryEntities;
	}

	public List<EighteenPrincesHelpHistoryEntity> getEighteenPrincesHelpHistoryEntities() {
		return eighteenPrincesHelpHistoryEntities;
	}

	public EighteenPrincesEntity getEighteenPrincesEntity() {
		return eighteenPrincesEntity;
	}

	public void setEighteenPrincesEntity(EighteenPrincesEntity eighteenPrincesEntity) {
		this.eighteenPrincesEntity = eighteenPrincesEntity;
	}

	public List<BadgeEntity> getBadgeEntities() {
		return badgeEntities;
	}
	/**
	 * 转换角色基础属性,且修正武器
	 */
	public void converRolesBaseAttr() {
		List<RoleEntity> roleEntities = getRoleEntities();
		for (RoleEntity roleEntity : roleEntities) {
			roleEntity.convert();
			PlayerUtil.refreshOnlineAttribute(player.getPlayerData(), roleEntity);
			// 验证装备存在性
			for (int i = Const.equipPart.HELMET_VALUE; i <= Const.equipPart.NECKLACE_VALUE; i++) {
				if (roleEntity.getPartEquipId(i) > 0) {
					EquipEntity equipEntity = player.getPlayerData().getEquipById(roleEntity.getPartEquipId(i));
					if (equipEntity == null) {
						roleEntity.setPartEquipId(i, 0);
						roleEntity.notifyUpdate(true);
					}
				}
			}
		}
	}
	
	/**
	 *取出箴言(包含未激活) 
	 */
	public MottoEntity getMottoById(long Id) {
		MottoEntity retEntity = null;
		for (MottoEntity mEntity :mottoEntities) {
			if (mEntity.getId() == Id) {
				retEntity = mEntity;
				break;
			}
		}
		return retEntity;
	}
	/**
	 * 取出已激活箴言
	 * 
	 */
	public List<MottoEntity> getActMotto(){
		List<MottoEntity> aList = new ArrayList<>();
		if (mottoEntities != null) {
			for (MottoEntity mEntity :mottoEntities) {
				if (mEntity.getStar() > 0) {
					aList.add(mEntity);
				}
			}
		}
		return aList;
	}
	/**
	 * 是否加入公會
	 */
	public boolean isJoinGuild() {
		// 公会校验
		PlayerAllianceEntity Guildentity = getPlayerAllianceEntity();
		
		if (Guildentity == null) {
			return false;
		} 			
		
		int allianceId = Guildentity.getAllianceId();
		if (allianceId == 0) {
			return false;
		}
		
		AllianceEntity a_entity = AllianceManager.getInstance().getAlliance(allianceId);
		if (a_entity == null) {
			return false;
		}
		
		return true;
	}

	public String getLastRecharage() {
		return lastRecharage;
	}

	public void setLastRecharage(String lastRecharage) {
		this.lastRecharage = lastRecharage;
	}
		
}
