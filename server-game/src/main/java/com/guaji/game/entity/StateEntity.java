package com.guaji.game.entity;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.guaji.db.DBEntity;
import org.guaji.msg.Msg;
import org.guaji.os.GuaJiTime;

import com.google.gson.reflect.TypeToken;
import com.guaji.game.GsApp;
import com.guaji.game.config.SysBasicCfg;
import com.guaji.game.item.ItemInfo;
import com.guaji.game.manager.RecordFirstManager;
import com.guaji.game.manager.TapDBManager;
import com.guaji.game.protocol.Battle.BattleInfo;
import com.guaji.game.util.BuilderUtil;
import com.guaji.game.util.GsConst;
import com.guaji.game.util.GsonUtil;

/**
 * 状态实体对象
 */
@Entity
@Table(name = "status")
@SuppressWarnings("serial")
public class StateEntity extends DBEntity {
	@Id
	@Column(name = "playerId")
	private int playerId = 0;

	@Column(name = "platformData")
	private String platformData = "";

	/**
	 * 自动分解装备
	 */
	@Column(name = "autoDecoElement")
	protected int autoDecoElement = 0;

	/**
	 * 自动售出白色装备(mask)
	 */
	@Column(name = "autoSellEquip")
	protected int autoSellEquip = 0;

	/**
	 * 聊天关闭
	 */
	@Column(name = "chatClose")
	protected int chatClose = 0;

	/**
	 * 快速战斗次数
	 */
	@Column(name = "fastFightTimes")
	protected int fastFightTimes = 0;

	/**
	 * 快速战斗已购买次数
	 */
	@Column(name = "fastFightBuyTimes")
	protected int fastFightBuyTimes = 0;

	/**
	 * 剩余boss战斗次数
	 */
	@Column(name = "bossFightTimes")
	protected int bossFightTimes = 0;

	/**
	 * boss战斗已购买次数
	 */
	@Column(name = "bossFightBuyTimes")
	protected int bossFightBuyTimes = 0;

	@Column(name = "eliteMapTimes")
	protected int eliteMapTimes = 0;

	@Column(name = "eliteMapBuyTimes")
	protected int eliteMapBuyTimes = 0;

	/**
	 * 下次战斗开始时间
	 */
	@Column(name = "nextBattleTime")
	protected int nextBattleTime = 0;

	/**
	 * 当前挂机地图
	 */
	@Column(name = "currMapId")
	protected int curBattleMap = 0;

	/**
	 * 已通过地图
	 */
	@Column(name = "passMapId")
	protected int passMapId = 0;

	/**
	 * 已通过地图
	 */
	@Column(name = "passEliteMapId")
	protected int passEliteMapId = 0;

	/**
	 * 装备熔炼打造的当前的装备JsonInfo
	 */
	@Column(name = "equipSmeltCreate")
	private String equipSmeltCreate = "";

	/**
	 * 装备熔炼打造的当前的道具JsonInfo
	 */
	@Column(name = "itemSmeltCreate")
	private String itemSmeltCreate = "";

	/**
	 * 装备熔炼打造的当前的装备今天可刷新次数
	 */
	@Column(name = "equipSmeltRefesh")
	private int equipSmeltRefesh;

	/**
	 * 竞技场今日已购买次数
	 */
	@Column(name = "arenaBuyTimes")
	private int arenaBuyTimes;
	/**
	 * 竞技场上次购买时间
	 */
	@Column(name = "arenaLastBuyTime")
	private int arenaLastBuyTime;
	/**
	 * 竞技场今日剩余挑战次数
	 */
	@Column(name = "surplusChallengeTimes")
	private int surplusChallengeTimes;
	/**
	 * 装备背包空间
	 */
	@Column(name = "equipBagSize")
	private int equipBagSize;
	/**
	 * 装备背包扩展次数
	 */
	@Column(name = "equipBagExtendTimes")
	private int equipBagExtendTimes;

	/**
	 * 已使用cdk类型
	 */
	@Column(name = "cdkeyType")
	private String cdkeyType;

	/**
	 * 礼物领取状态
	 */
	@Column(name = "giftStatus")
	private int giftStatus;

	/**
	 * boss扫荡状态
	 */
	@Column(name = "wipeBoss")
	private int wipeBoss;

	/**
	 * 公测字集齐的日期
	 */
	@Column(name = "gongceWordDay")
	private Date gongceWordDay = null;

	/**
	 * 音乐开关
	 */
	@Column(name = "music")
	private int music = 5;

	/**
	 * 字体大小
	 */
	@Column(name = "fontSize")
	private int fontSize;

	/**
	 * 音效开关
	 */
	@Column(name = "sound")
	private int sound = 10;

	/**
	 * 显示地区
	 */
	@Column(name = "showArea")
	private boolean showArea = true;

	/**
	 * 聊天是否只发送文字
	 */
	@Column(name = "onlyText")
	private boolean onlyText = false;

	/**
	 * 今日新服礼包
	 */
	@Column(name = "newSerGiftRewardCount")
	private int newSerGiftRewardCount = 0;

	/**
	 * 宝箱幸运值
	 */
	@Column(name = "itemLuck")
	private String itemLuck = "{}";

	/**
	 * 上一场战斗类型
	 */
	@Column(name = "latestBattleType")
	private int latestBattleType = 0;

	/**
	 * 星级升级石使用次数
	 */
	@Column(name = "starStoneTimes")
	private int starStoneTimes = 0;

	/**
	 * 通关最大的多人副本Id
	 */
	@Column(name = "passMaxMultiEliteId")
	private int passMaxMultiEliteId = 0;

	/**
	 * 多人副本剩余次数
	 */
	@Column(name = "multiEliteTimes")
	private int multiEliteTimes = 0;
	
	/**
	 * 目前挑戰星等
	 */
	@Column(name = "challengeStarStr" ,columnDefinition = "varchar(1024) not null")
	private String challengeStarStr = "{}";
	@Transient
	private Map<Integer, Integer> challengeStarMap;
	
	/**
	 * 目前地下城挑戰星等
	 */
	@Column(name = "dungeonStarStr" ,columnDefinition = "varchar(1024) not null")
	private String dungeonStarStr = "{}";
	@Transient
	private Map<Integer, Integer> dungeonStarMap;
	
	/**
	 * 地下城最大星等(關卡)
	 */
	@Column(name = "dungeonMaxStr" ,columnDefinition = "varchar(1024) not null")
	private String dungeonMaxStr = "{}";
	@Transient
	private Map<Integer, Integer> dungeonMaxMap;
	
	/**
	 * 地下城每日已挑戰次數
	 */
	@Column(name = "dungeonTimesStr" ,columnDefinition = "varchar(1024) not null")
	private String dungeonTimesStr = "{}";
	@Transient
	private Map<Integer, Integer> dungeonTimesMap;

	/**
	 * 地下城一鍵通關使用
	 */
	@Column(name = "dungeonOneKeyStr" ,columnDefinition = "varchar(1024) not null")
	private String dungeonOneKeyStr = "{}";
	@Transient
	private Map<Integer, Integer> dungeonOneKeyMap;

	/**
	
	/** 
	 * 多人副本首次胜利次数
	 */
	@Column(name = "multiFirstBattle")
	private int multiFirstBattle = 0;

	/**
	 * 多人副本挑战记录
	 */
	@Column(name = "multiBattleInfoStr")
	private String multiBattleInfoStr;

	@Transient
	private Map<Integer, Integer> multiBattleInfoMap;
	
	/**
	 * 多人副本領獎记录
	 */
	@Column(name = "multiGiftInfoStr")
	private String multiGiftInfoStr;

	@Transient
	private Map<Integer, Integer> multiGiftInfoMap;

	/**
	 * 今天购买多人副本次数
	 */
	@Column(name = "todayBuyMultiEliteTimes")
	private int todayBuyMultiEliteTimes = 0;

	/**
	 * 上次刷新多人副本次数时间
	 */
	@Column(name = "lastRefreshMultiEliteTime")
	private long lastRefreshMultiEliteTime = 0;

	/**
	 * 多人副本积分
	 */
	@Column(name = "multiEliteScore")
	private int multiEliteScore = 0;

	/**
	 * 多人副本历史积分
	 */
	@Column(name = "multiEliteHistoryScore")
	private int multiEliteHistoryScore = 0;

	/**
	 * 多人副本上次未展示结果战报Id
	 */
	@Column(name = "lastShowMultiEliteResultId")
	private int lastMultiEliteBattleResultId = 0;

	/**
	 * 元素背包大小
	 */
	@Column(name = "elementBagSize")
	private int elementBagSize = 0;// 基础类型不能不赋值。避免数据库为NUll
	/**
	 * 儲值首抽幸運機率
	 */
	@Column(name = "rechargelucky", columnDefinition = "int default 0")
	private int rechargeluckey = 0;
	
	/**
	 * 儲值首抽幸運的時間
	 */
	@Column(name = "luckyTime")
	private Date luckyTime = null;

	@Transient
	private Map<Integer, Integer> todayHireStatusMap;

	@Transient
	private List<Integer> lastAllHireRoomMembers;

	/**
	 * 多人副本今日刷新雇佣列表次数 (每日刷新)
	 */
	@Column(name = "todayRefreshHireListTimes")
	private int todayRefreshHireListTimes = 0;

	/**
	 * 世界boss自动加入状态
	 */
	@Column(name = "worldBossAutoState")
	private int worldBossAutoState = 0;

	/**
	 * 世界boss挑戰次數
	 */
	@Column(name = "worldBossBuffFreeTimes")
	private int worldBossBuffFreeTimes = 0;

	/**
	 * 玩家帐号绑定状态
	 */
	@Column(name = "accountBoundStatus")
	private int accountBoundStatus = 0;
	/**
	 * 玩家友情點數
	 */
	@Column(name = "friendship")
	private int friendship = 0;
	/**
	 * 玩家VIP點數
	 */
	@Column(name = "vipPoint")
	private int vipPoint = 0;
	/**
	 * 玩家累積登入次數
	 */
	@Column(name = "fristLoginTimes")
	private int firstLoginTimes = 0;
	/**
	 * 玩家累積鑽石消耗量
	 */
	@Column(name = "accConsumeGold")
	private int accConsumeGold = 0;
	/**
	 * 玩家競技場勝利次數
	 */
	@Column(name = "arenaWinTimes")
	private int arenaWinTimes = 0;
	/**
	 * 試煉塔樓層數
	 */
	@Column(name = "ordealFloor")
	private int ordealFloor = 0;
	/**
	 * 挖礦層數
	 */
	@Column(name = "miningLevel")
	private int miningLevel = 0;
//	/**
//	 * 英雄秘密訊息次數
//	 */
//	@Column(name = "secretMsg",columnDefinition = "varchar(2048) not null")
//	private String secretMsg;
//	
//	@Transient
//	private List<Integer> secretMsgList;
//	/**
//	 * 英雄秘密訊息次數
//	 */
//	@Column(name = "lastMsgTime")
//	private int lastMsgTime = 0;
	/**
	 * 秘密信條體力
	 */
	@Column(name = "secretPower",columnDefinition = "int default 100")
	private int secretPower = 100;
	/**
	 * 最後回體力時間
	 */
	@Column(name = "lastRecoverTime",columnDefinition = "int default 0")
	private int lastRecoverTime = 0;
	/**
	 * 首次領取戰鬥獎勵送30分鐘
	 */
	@Column(name = "firstFightAward",columnDefinition = "int default 0")
	private int firstFightAward = 0;
	/**
	 * 新手九級任務開啟時間
	 */	
	@Column(name = "newbieDate")
	private Date newbieDate = null;

	@Column(name = "createTime", nullable = false)
	private Date createTime = null;

	@Column(name = "updateTime")
	private Date updateTime;
	
	@Column(name = "invalid")
	private boolean invalid;

	/**
	 * 装备熔炼打造的当前的道具
	 */
	@Transient
	private ItemInfo smeltItemInfo;

//	@Column(name = "roleBaptizeAttr")
//	private String roleBaptizeAttr = "";
//
//	@Column(name = "roleBaptizeAttrTransferTime")
//	private String roleBaptizeAttrTransferTime = "";

	/**
	 * 上次阅读邮件的时间
	 */
	@Transient
	private Date lastReadMailTime = new Date();

	/**
	 * 装备熔炼打造的当前的装备Entity
	 */
	@Transient
	private EquipEntity equipSmeltCreateEntity;

	@Column(name = "resetTimeMapStr")
	protected String resetTimeMapStr = "";

	/**
	 * 分享次数 20150108
	 */
	@Column(name = "largessGoldTime")
	private int FBShareTime = 0;
	/**
	 * 上一次分享时间
	 */
	@Column(name = "lastlargessDay")
	private Date LastFBShareDate = null;
	/**
	 * 新手引导状态,默认值是2
	 */
	@Column(name = "newGuideState")
	private int newGuideState = 2;

	/**
	 * 评价获得奖励状态
	 */
	@Column(name = "evaluateRewardsState")
	private int evaluateRewardsState = 0;

	/**
	 * 新手送月卡状态
	 */
	@Column(name = "newbieRewardMonthCard")
	private int newbieRewardMonthCard = 0;

	@Column(name = "maxArenaRecordId")
	private int maxArenaRecordId = 0;

	/**
	 * 每一项单独刷新时间
	 */
	@Transient
	protected Map<Integer, Date> resetTimeMap;

	/**
	 * 装备洗练属性
	 */
//	@Transient
//	private Map<Integer, Attribute> roleBaptizeAttrMap;

	@Transient
	private Map<Integer, Integer> itemLuckMap;

	/**
	 * 兑换金豆数量
	 */
	@Column(name = "exchangeCount")
	private int exchangeCount;

	/**
	 * 上一次兑换金豆时间
	 */
	@Column(name = "lastExchangeBeanTime")
	private long lastExchangeBeanTime;

	/**
	 * 上一次重置兑换金豆数量时间
	 */
	@Column(name = "lastResetExchangeBeanTime")
	private long lastResetExchangeBeanTime;

	/**
	 * 新手引导
	 */
	@Column(name = "guideStr")
	private String guideStr = "{}";

	/**
	 * 新手引导挑战第一次失败
	 */
	@Column(name = "firstFalse")
	private int firstFalse;

	/**
	 * 新手引导挑战第一次成功
	 */
	@Column(name = "firstSuccess")
	private int firstSuccess;

	/**
	 * 新手引导第一次战斗
	 */
	@Column(name = "firstBattle")
	private int firstBattle;

	/**
	 * 累计快速战斗次数
	 */
	@Column(name = "totalFastFightCount")
	private int totalFastFightCount;

	/**
	 * 新手引导
	 */
	@Transient
	private Map<Integer, Integer> guideMap;

	/**
	 * 新手引导第一次快速战斗
	 */
	@Column(name = "firstFastBattle")
	private int firstFastBattle;

	/**
	 * 角色第一次快速战斗
	 */
	@Column(name = "roleFirstFastBattle")
	private int roleFirstFastBattle;

	/**
	 * Facebook好友索取
	 */
	@Column(name = "askTickIds")
	private String askTickIds;// 已索取的好友id（tzy）

	@Column(name = "hourCardUseCountOneDay")
	private int hourCardUseCountOneDay;

	/**
	 * 宝石商店每日购买数量 <因需求变化，这列暂时不用了>
	 */
	@Column(name = "gemShopBuyCount")
	private int gemShopBuyCount;

	/**
	 * ios玩家专属礼包领取状态
	 */
	@Column(name = "iosGetState")
	private int iosGetState;

	/**
	 * 上次战斗数据快照
	 */
	@Column(name = "lastSnapBattle")
	private byte[] lastSnapBattle;

	@Transient
	private List<String> askTickIdList;

	@Column(name = "exchangeStr")
	private String exchangeStr = "{}";

	@Column(name = "todayLoginCount", columnDefinition = "int default 0")
	private int todayLoginCount = 0;

	/**
	 * 徽章背包数量
	 */
	@Column(name = "badgeBagSize")
	private int badgeBagSize;

	/**
	 * 徽章背包扩展次数
	 */
	@Column(name = "badgeBagExtendTimes",columnDefinition = "int default 0")
	private int badgeBagExtendTimes;
	
	/**
	 * 上一次領取戰鬥掛機獎勵時間
	 */
	@Column(name = "lastTakeBattleAwardTime",columnDefinition = "int default 0")
	private int lastTakeBattleAwardTime = 0;

	/**
	 * 進入關卡時間
	 */
	@Column(name = "intoLevelTime",columnDefinition = "int default 0")
	private int intoLevelTime = 0;
	
	/**
	 * 是否播放新手故事影片
	 */
	@Column(name = "playstory",columnDefinition = "int default 0")
	private int playstory = 0;


	@Transient
	private Map<String, Integer> exchangeMap;



	public StateEntity() {
		this.createTime = GuaJiTime.getCalendar().getTime();
		this.itemLuckMap = new HashMap<Integer, Integer>();
		this.equipSmeltRefesh = GsConst.Equip.INIT_EQUIP_SMELT_REFRESH;
		this.resetTimeMap = new HashMap<>();
		this.resetTimeMapStr = GsonUtil.getJsonInstance().toJson(this.resetTimeMap);
		this.askTickIdList = new ArrayList<String>();
		this.todayHireStatusMap = new HashMap<Integer, Integer>();
		this.lastAllHireRoomMembers = new ArrayList<Integer>();
		this.guideMap = new HashMap<Integer, Integer>();
		this.guideStr = GsonUtil.getJsonInstance().toJson(this.guideMap);
		this.challengeStarMap = new HashMap<Integer, Integer>();
		this.challengeStarStr = GsonUtil.getJsonInstance().toJson(this.challengeStarMap);
		this.multiBattleInfoMap = new HashMap<Integer, Integer>();
		this.multiBattleInfoStr = GsonUtil.getJsonInstance().toJson(this.multiBattleInfoMap);
		this.multiGiftInfoMap = new HashMap<Integer, Integer>();
		this.multiGiftInfoStr = GsonUtil.getJsonInstance().toJson(this.multiGiftInfoMap);
		this.exchangeMap = new HashMap<String, Integer>();
		this.LastFBShareDate = GuaJiTime.getCalendar().getTime();
//		this.secretMsgList = new ArrayList<>();
//		this.secretMsg =  GsonUtil.getJsonInstance().toJson(this.secretMsgList);
		this.dungeonStarMap = new HashMap<Integer, Integer>();
		this.dungeonStarStr = GsonUtil.getJsonInstance().toJson(this.dungeonStarMap);
		this.dungeonMaxMap = new HashMap<Integer, Integer>();
		this.dungeonMaxStr = GsonUtil.getJsonInstance().toJson(this.dungeonMaxMap);
		this.dungeonTimesMap = new HashMap<Integer, Integer>();
		this.dungeonTimesStr = GsonUtil.getJsonInstance().toJson(this.dungeonTimesMap);
		this.dungeonOneKeyMap = new HashMap<Integer, Integer>();
		this.dungeonOneKeyStr = GsonUtil.getJsonInstance().toJson(this.dungeonOneKeyMap);
		
	}

	public int getPlayerId() {
		return playerId;
	}

	public void setPlayerId(int playerId) {
		this.playerId = playerId;
	}

	public int getAutoSellEquip() {
		return autoSellEquip;
	}

	public void setAutoSellEquip(int autoSellEquip) {
		this.autoSellEquip = autoSellEquip;
	}

	public int getChatClose() {
		return chatClose;
	}

	public void setChatClose(int chatClose) {
		this.chatClose = chatClose;
	}

	public int getFastFightTimes() {
		return fastFightTimes;
	}

	public void setFastFightTimes(int fastFightTimes) {
		this.fastFightTimes = fastFightTimes;
	}
	
	public void incFastFightTimes() {
		this.fastFightTimes = this.fastFightTimes+1;
	}

	public int getFastFightBuyTimes() {
		return fastFightBuyTimes;
	}

	public int getTotalFastFightCount() {
		return totalFastFightCount;
	}

	public void setTotalFastFightCount(int count) {
		totalFastFightCount = count;
		return;
	}

	public void setFastFightBuyTimes(int fastFightBuyTimes) {
		this.fastFightBuyTimes = fastFightBuyTimes;
	}

	public int getBossFightTimes() {
		return bossFightTimes;
	}

	public void setBossFightTimes(int bossFightTimes) {
		this.bossFightTimes = bossFightTimes;
	}

	public int getBossFightBuyTimes() {
		return bossFightBuyTimes;
	}

	public void setBossFightBuyTimes(int bossFightBuyTimes) {
		this.bossFightBuyTimes = bossFightBuyTimes;
	}

	public int getNextBattleTime() {
		return nextBattleTime;
	}

	public void setNextBattleTime(int nextBattleTime) {
		this.nextBattleTime = nextBattleTime;
	}

	public int getCurBattleMap() {
		return curBattleMap;
	}

	public void setCurBattleMap(int curBattleMap) {
		this.curBattleMap = curBattleMap;
		postSvrChangeMsg(GsConst.SvrMissionType.Player_Current_Map,this.getPlayerId(),curBattleMap);
	}
	/**
	 * 用於檢查是否領過挑戰Boss獎勵
	 */
	public int getPassMapId() {
		return passMapId;
	}
	/**
	 * 過關紀錄用於檢查是否領過挑戰Boss獎勵
	 */
	public void setPassMapId(int passMapId) {
		int oldPassMapId = getPassMapId();
		if (oldPassMapId != passMapId) {
			TapDBManager.getInstance().tapdbUser(this.playerId,GsConst.tapDBPropertyMotion.update, GsConst.tapDBPropertyName.pass_mapid, passMapId);
		}
		this.passMapId = passMapId;
	}

	public int getPassEliteMapId() {
		return passEliteMapId;
	}

	public void setPassEliteMapId(int passEliteMapId) {
		this.passEliteMapId = passEliteMapId;
	}

	public String getEquipSmeltCreate() {
		return equipSmeltCreate;
	}

	public void setFirstFalse(int firstFalse) {
		this.firstFalse = firstFalse;
	}

	public void setFirstSuccess(int firstSuccess) {
		this.firstSuccess = firstSuccess;
	}

	/**
	 * 获取第一次挑战boss状态：0为旧用户1为新用户未失败过2为新用户已经失败过
	 * 
	 * @return
	 */
	public int getFirstFalse() {
		return this.firstFalse;
	}

	public int getFirstSuccess() {
		return this.firstSuccess;
	}

	public void setFirstBattle(int firstBattle) {
		this.firstBattle = firstBattle;
	}

	public void setFirstFastBattle(int firstFastBattle) {
		this.firstFastBattle = firstFastBattle;
	}

	public int getFirstFastBattle() {
		return this.firstFastBattle;
	}

	/**
	 * 获取第一次战斗状态：0为旧用户1为新用户未战斗过2为新用户已经战斗过
	 * 
	 * @return
	 */
	public int getFirstBattle() {
		return this.firstBattle;
	}

	public void setEquipSmeltCreate(String equipSmeltCreate) {
		this.equipSmeltCreate = equipSmeltCreate;
		if (equipSmeltCreate != null && equipSmeltCreate.length() > 0) {
			equipSmeltCreateEntity = GsonUtil.getJsonInstance().fromJson(equipSmeltCreate, EquipEntity.class);
		}
	}

	public void convertData() {
		if (this.equipSmeltCreate != null && equipSmeltCreate.length() > 0) {
			equipSmeltCreateEntity = GsonUtil.getJsonInstance().fromJson(equipSmeltCreate, EquipEntity.class);
		}

		if (itemSmeltCreate != null && itemSmeltCreate.length() > 0) {
			smeltItemInfo = GsonUtil.getJsonInstance().fromJson(itemSmeltCreate, ItemInfo.class);
		}

		if (itemLuck != null && !"".equals(itemLuck) && !"null".equals(itemLuck)) {
			itemLuckMap = GsonUtil.getJsonInstance().fromJson(itemLuck, new TypeToken<HashMap<Integer, Integer>>() {
			}.getType());
		} else {
			itemLuckMap = new HashMap<Integer, Integer>();
		}

		if (this.resetTimeMapStr != null && !"".equals(this.resetTimeMapStr)) {
			this.resetTimeMap = GsonUtil.getJsonInstance().fromJson(this.resetTimeMapStr,
					new TypeToken<HashMap<Integer, Date>>() {
					}.getType());
		}

		if (this.guideStr != null && !"".equals(this.guideStr)) {
			this.guideMap = GsonUtil.getJsonInstance().fromJson(this.guideStr,
					new TypeToken<HashMap<Integer, Integer>>() {
					}.getType());
		}
		
		if (this.challengeStarStr != null && !"".equals(this.challengeStarStr)) {
			this.challengeStarMap = GsonUtil.getJsonInstance().fromJson(this.challengeStarStr,
					new TypeToken<HashMap<Integer, Integer>>() {
					}.getType());
		} else {
			this.challengeStarMap = new HashMap<Integer, Integer>();
		}

		if (this.multiBattleInfoStr != null && !"".equals(this.multiBattleInfoStr)) {
			this.multiBattleInfoMap = GsonUtil.getJsonInstance().fromJson(this.multiBattleInfoStr,
					new TypeToken<HashMap<Integer, Integer>>() {
					}.getType());
		}
		
		if (this.multiGiftInfoStr != null && !"".equals(this.multiGiftInfoStr)) {
			this.multiGiftInfoMap = GsonUtil.getJsonInstance().fromJson(this.multiGiftInfoStr,
					new TypeToken<HashMap<Integer, Integer>>() {
					}.getType());
		}

		// 初始化索取集合（tzy）
		if (askTickIds != null && askTickIds.length() > 0) {
			askTickIdList = GsonUtil.getJsonInstance().fromJson(askTickIds, new TypeToken<ArrayList<String>>() {
			}.getType());
		} else {
			askTickIdList = new ArrayList<String>();
		}
		
		// 初始化索取集合（tzy）
//		if (secretMsg != null && secretMsg.length() > 0) {
//			secretMsgList = GsonUtil.getJsonInstance().fromJson(secretMsg, new TypeToken<ArrayList<Integer>>() {
//			}.getType());
//		} else {
//			secretMsgList = new ArrayList<Integer>();
//		}

		// 兑换所副将数量（无需重置）
		if (exchangeStr != null && !"".equals(exchangeStr) && !"null".equals(exchangeStr)) {
			exchangeMap = GsonUtil.getJsonInstance().fromJson(exchangeStr, new TypeToken<HashMap<String, Integer>>() {
			}.getType());
		} else {
			exchangeMap = new HashMap<String, Integer>();
		}
		
		if (this.dungeonStarStr != null && !"".equals(this.dungeonStarStr)) {
			this.dungeonStarMap = GsonUtil.getJsonInstance().fromJson(this.dungeonStarStr,
					new TypeToken<HashMap<Integer, Integer>>() {
					}.getType());
		} else {
			this.dungeonStarMap = new HashMap<Integer, Integer>();
		}
		
		if (this.dungeonMaxStr != null && !"".equals(this.dungeonMaxStr)) {
			this.dungeonMaxMap = GsonUtil.getJsonInstance().fromJson(this.dungeonMaxStr,
					new TypeToken<HashMap<Integer, Integer>>() {
					}.getType());
		} else {
			this.dungeonMaxMap = new HashMap<Integer, Integer>();
		}
		
		if (this.dungeonTimesStr != null && !"".equals(this.dungeonTimesStr)) {
			this.dungeonTimesMap = GsonUtil.getJsonInstance().fromJson(this.dungeonTimesStr,
					new TypeToken<HashMap<Integer, Integer>>() {
					}.getType());
		} else {
			this.dungeonTimesMap = new HashMap<Integer, Integer>();
		}
		
		if (this.dungeonOneKeyStr != null && !"".equals(this.dungeonOneKeyStr)) {
			this.dungeonOneKeyMap = GsonUtil.getJsonInstance().fromJson(this.dungeonOneKeyStr,
					new TypeToken<HashMap<Integer, Integer>>() {
					}.getType());
		} else {
			this.dungeonOneKeyMap = new HashMap<Integer, Integer>();
		}

	}

	public int getFontSize() {
		return fontSize;
	}

	public String getItemLuckStr() {
		return itemLuck = GsonUtil.getJsonInstance().toJson(itemLuckMap);
	}

	public void setItemLuckStr(String itemLuck) {
		this.itemLuck = itemLuck;
	}

	public Map<Integer, Integer> getItemLuckMap() {
		return itemLuckMap;
	}

	public void setItemLuckMap(HashMap<Integer, Integer> itemLuckMap) {
		this.itemLuckMap = itemLuckMap;
	}

	public String getExchangeStr() {
		return exchangeStr = GsonUtil.getJsonInstance().toJson(exchangeMap);
	}

	public void setExchangeStr(String exchangeStr) {
		this.exchangeStr = exchangeStr;
	}

	public Map<String, Integer> getExchangeMap() {
		return exchangeMap;
	}

	public int getExchangeCountById(String itemId) {
		if (this.exchangeMap.containsKey(itemId)) {
			return exchangeMap.get(itemId);
		} else {
			return 0;
		}
	}

	public void addExchange(String itemId, Integer count) {
		this.exchangeMap.put(itemId, count);
		this.exchangeStr = GsonUtil.getJsonInstance().toJson(exchangeMap);
	}

	public void setExchangeMap(Map<String, Integer> exchangeMap) {
		this.exchangeMap = exchangeMap;
	}

	public EquipEntity getEquipSmeltCreateEntity() {
		return equipSmeltCreateEntity;
	}

	public int getEquipSmeltRefesh() {
		return equipSmeltRefesh;
	}

	public void setEquipSmeltRefesh(int equipSmeltRefesh) {
		this.equipSmeltRefesh = equipSmeltRefesh;
	}

	public int getArenaBuyTimes() {
		return arenaBuyTimes;
	}

	public void setArenaBuyTimes(int arenaBuyTimes) {
		this.arenaBuyTimes = arenaBuyTimes;
	}

	public int getArenaLastBuyTime() {
		return arenaLastBuyTime;
	}

	public void setArenaLastBuyTime(int arenaLastBuyTime) {
		this.arenaLastBuyTime = arenaLastBuyTime;
	}

	public int getSurplusChallengeTimes() {
		return surplusChallengeTimes;
	}

	public void setSurplusChallengeTimes(int surplusChallengeTimes) {
		this.surplusChallengeTimes = surplusChallengeTimes;
	}

	public int getEquipBagSize() {
		return equipBagSize;
	}

	public void setEquipBagSize(int equipBagSize) {
		this.equipBagSize = equipBagSize;
	}

	public String getCdkeyType() {
		return cdkeyType;
	}

	public void setCdkeyType(String cdkeyType) {
		this.cdkeyType = cdkeyType;
	}

	public Date getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}

	public Date getUpdateTime() {
		return updateTime;
	}

	public void setUpdateTime(Date updateTime) {
		this.updateTime = updateTime;
	}

	/***
	 * 分享数据 访问接口
	 */
	public int getFBShareTime() {
		return FBShareTime;
	}

	public void setFBShareTime(int time) {
		this.FBShareTime = time;
	}

	public Date getLastShareDate() {
		return LastFBShareDate;
	}

	public void setLastShareDate(Date updateTime) {
		this.LastFBShareDate = updateTime;
	}

	public boolean isInvalid() {
		return invalid;
	}

	public void setInvalid(boolean invalid) {
		this.invalid = invalid;
	}

	public void setEquipSmeltCreateEntity(EquipEntity equipSmeltCreateEntity) {
		this.equipSmeltCreateEntity = equipSmeltCreateEntity;
		this.equipSmeltCreate = GsonUtil.getJsonInstance().toJson(equipSmeltCreateEntity);
	}

	public int getEquipBagExtendTimes() {
		return equipBagExtendTimes;
	}

	public void setEquipBagExtendTimes(int equipBagExtendTimes) {
		this.equipBagExtendTimes = equipBagExtendTimes;
	}

	public int getLeftEquipBagExtendTimes() {
		return SysBasicCfg.getInstance().getEquipExtendTimes() - equipBagExtendTimes;
	}

	public ItemInfo getSmeltItemInfo() {
		return smeltItemInfo;
	}

	public void setSmeltItemInfo(ItemInfo smeltItemInfo) {
		this.smeltItemInfo = smeltItemInfo;
		this.itemSmeltCreate = GsonUtil.getJsonInstance().toJson(smeltItemInfo);
	}

	public Date getLastReadMailTime() {
		return lastReadMailTime;
	}

	public void setLastReadMailTime(Date lastReadMailTime) {
		this.lastReadMailTime = lastReadMailTime;
	}

	public int getGiftStatus() {
		return giftStatus;
	}

	public void setGiftStatus(int giftStatus) {
		this.giftStatus = giftStatus;
	}

	public int getWipeBoss() {
		return wipeBoss;
	}

	public void setWipeBoss(int wipeBoss) {
		this.wipeBoss = wipeBoss;
	}

	public Date getGongceWordDay() {
		return gongceWordDay;
	}

	public void setGongceWordDay(Date gongceWordDay) {
		this.gongceWordDay = gongceWordDay;
	}

	public int getMusic() {
		return music;
	}

	public void setMusic(int music) {
		this.music = Math.max(0,Math.min(music,10));
	}

	public void setFontSize(int fontSize) {
		this.fontSize = fontSize;
	}

	public int getSound() {
		return sound;
	}

	public void setSound(int sound) {
		this.sound = Math.max(0,Math.min(sound,10));
	}

	public boolean isShowArea() {
		return showArea;
	}

	public void setShowArea(boolean showArea) {
		this.showArea = showArea;
	}

	public int getEliteMapTimes() {
		return eliteMapTimes;
	}

	public void setEliteMapTimes(int eliteMapTimes) {
		this.eliteMapTimes = eliteMapTimes;
	}

	public int getEliteMapBuyTimes() {
		return eliteMapBuyTimes;
	}

	public void setEliteMapBuyTimes(int eliteMapBuyTimes) {
		this.eliteMapBuyTimes = eliteMapBuyTimes;
	}

	public int getNewSerGiftRewardCount() {
		return newSerGiftRewardCount;
	}

	public void setNewSerGiftRewardCount(int newSerGiftRewardCount) {
		this.newSerGiftRewardCount = newSerGiftRewardCount;
	}

	public String getPlatformData() {
		return platformData;
	}

	public void setPlatformData(String platformData) {
		this.platformData = platformData;
	}

	public int getLatestBattleType() {
		return latestBattleType;
	}

	public void setLatestBattleType(int latestBattleType) {
		this.latestBattleType = latestBattleType;
	}

	public Date getLastResetTime(int type) {
		return resetTimeMap.get(type);
	}

	public void putResetTime(int type, Date time) {
		this.resetTimeMap.put(type, time);
		this.resetTimeMapStr = GsonUtil.getJsonInstance().toJson(this.resetTimeMap);
	}

	public int getStarStoneTimes() {
		return starStoneTimes;
	}

	public void setStarStoneTimes(int starStoneTimes) {
		this.starStoneTimes = starStoneTimes;
	}

	public int getNewGuideState() {
		return newGuideState;
	}

	public Map<Integer, Integer> getGuideMap() {
		return guideMap;
	}

	public void addGuideMap(int key, int value) {
		this.guideMap.put(key, value);
		this.guideStr = GsonUtil.getJsonInstance().toJson(this.guideMap);
	}
	
	public void clearGuideMap() {
		this.guideMap.clear();
		this.guideStr = GsonUtil.getJsonInstance().toJson(this.guideMap);
	}

	public void setNewGuideState(int newGuideState) {
		this.newGuideState = newGuideState;
	}

	public int getEvaluateRewardsState() {
		return evaluateRewardsState;
	}

	public int getNewbieRewardMonthCard() {
		return newbieRewardMonthCard;
	}

	public void setNewbieRewardMonthCard(int newbieRewardMonthCard) {
		this.newbieRewardMonthCard = newbieRewardMonthCard;
	}

	public void setEvaluateRewardsState(int evaluateRewardsState) {
		this.evaluateRewardsState = evaluateRewardsState;
	}

	@Override
	public void notifyUpdate(boolean async) {
		getItemLuckStr();
		getExchangeStr();
		convertStr();
		super.notifyUpdate(async);
	}

	public boolean isOnlyText() {
		return onlyText;
	}

	public void setOnlyText(boolean onlyText) {
		this.onlyText = onlyText;
	}

	public int getPassMaxMultiEliteId() {
		return passMaxMultiEliteId;
	}

	public void setPassMaxMultiEliteId(int passMaxMultiEliteId) {
		this.passMaxMultiEliteId = passMaxMultiEliteId;
	}

	public int getTodayBuyMultiEliteTimes() {
		refreshMultiElite();
		return todayBuyMultiEliteTimes;
	}

	public void setTodayBuyMultiEliteTimes(int todayBuyMultiEliteTimes) {
		this.todayBuyMultiEliteTimes = todayBuyMultiEliteTimes;
	}

	private void refreshMultiElite() {
		if (lastRefreshMultiEliteTime < GuaJiTime.getAM0Date().getTime()) {
			lastRefreshMultiEliteTime = GuaJiTime.getMillisecond();
			multiEliteTimes = SysBasicCfg.getInstance().getMultiEliteDayFreeTimes();
			todayBuyMultiEliteTimes = 0;
		}
	}

	public int getMultiEliteTimes() {
		return multiEliteTimes;
	}

	public void setMultiEliteTimes(int multiEliteTimes) {
		this.multiEliteTimes = multiEliteTimes;
	}
	
//	public int getChallengeStar() {
//		return challengeStar;
//	}
//	
//	public void setChallengeStar(int challengeStar) {
//		this.challengeStar = challengeStar;
//	}

	public int getMultiEliteScore() {
		return multiEliteScore;
	}

	public void setMultiEliteScore(int multiEliteScore) {
		this.multiEliteScore = multiEliteScore;
	}

	public int getMultiEliteHistoryScore() {
		return multiEliteHistoryScore;
	}

	public void setMultiEliteHistoryScore(int multiEliteHistoryScore) {
		this.multiEliteHistoryScore = multiEliteHistoryScore;
	}

	public int getLastMultiEliteBattleResultId() {
		return lastMultiEliteBattleResultId;
	}

	public void setLastMultiEliteBattleResultId(int lastMultiEliteBattleResultId) {
		this.lastMultiEliteBattleResultId = lastMultiEliteBattleResultId;
	}

	public int getWorldBossAutoState() {
		return worldBossAutoState;
	}

	public void setWorldBossAutoState(int worldBossAutoState) {
		this.worldBossAutoState = worldBossAutoState;
	}

	public List<String> getAskTickIdList() {
		return askTickIdList;
	}

	public void setAskTickIdList(List<String> askTickIdList) {
		this.askTickIdList = askTickIdList;
	}

	public void addAskTickId(String id) {
		this.askTickIdList.add(id);
		this.askTickIds = GsonUtil.getJsonInstance().toJson(this.askTickIdList);
	}

	public void clearAskTickIds() {
		this.askTickIdList.clear();
		this.askTickIds = GsonUtil.getJsonInstance().toJson(this.askTickIdList);
	}

	public Map<Integer, Integer> getTodayHireStatusMap() {
		return todayHireStatusMap;
	}

	public void setTodayHireStatusMap(Map<Integer, Integer> todayHireStatusMap) {
		this.todayHireStatusMap = todayHireStatusMap;
	}

	public int getTodayRefreshHireListTimes() {
		return todayRefreshHireListTimes;
	}

	public void setTodayRefreshHireListTimes(int todayRefreshHireListTimes) {
		this.todayRefreshHireListTimes = todayRefreshHireListTimes;
	}

	public int getElementBagSize() {
		return elementBagSize;
	}

	public void setElementBagSize(int elementBagSize) {
		this.elementBagSize = elementBagSize;
	}

	public int getMaxArenaRecordId() {
		return maxArenaRecordId;
	}

	public void setMaxArenaRecordId(int maxArenaRecordId) {
		this.maxArenaRecordId = maxArenaRecordId;
	}

	public int getAccountBoundStatus() {
		return accountBoundStatus;
	}

	public void setAccountBoundStatus(int accountBoundStatus) {
		this.accountBoundStatus = accountBoundStatus;
	}
	
	public int getFriendship() {
		return friendship;
	}

	public void setFriendship(int friendship) {
		this.friendship = friendship;
	}
	
	public int getVipPoint() {
		return vipPoint;
	}

	public void setVipPoint(int vipPoint) {
		this.vipPoint = vipPoint;
	}

	public List<Integer> getLastAllHireRoomMembers() {
		return lastAllHireRoomMembers;
	}

	public int getAutoDecoElement() {
		return autoDecoElement;
	}

	public void setAutoDecoElement(int autoDecoElement) {
		this.autoDecoElement = autoDecoElement;
	}

	public int getExchangeCount() {
		return exchangeCount;
	}

	public void setExchangeCount(int exchangeCount) {
		this.exchangeCount = exchangeCount;
	}

	public long getLastResetExchangeBeanTime() {
		return lastResetExchangeBeanTime;
	}

	public void setLastResetExchangeBeanTime(long lastResetExchangeBeanTime) {
		this.lastResetExchangeBeanTime = lastResetExchangeBeanTime;
	}

	public long getLastExchangeBeanTime() {
		return lastExchangeBeanTime;
	}

	public void setLastExchangeBeanTime(long lastExchangeBeanTime) {
		this.lastExchangeBeanTime = lastExchangeBeanTime;
	}

	public int getHourCardUseCountOneDay() {
		return hourCardUseCountOneDay;
	}

	public void setHourCardUseCountOneDay(int count) {
		hourCardUseCountOneDay = count;
		return;
	}

	public int getGemShopBuyCount() {
		return gemShopBuyCount;
	}

	public void setGemShopBuyCount(int gemShopBuyCount) {
		this.gemShopBuyCount = gemShopBuyCount;
	}

	public int getIosGetState() {
		return iosGetState;
	}

	public void setIosGetState(int iosGetState) {
		this.iosGetState = iosGetState;
	}

	public void convertStr() {

		if (multiBattleInfoMap != null) {
			multiBattleInfoStr = GsonUtil.getJsonInstance().toJson(multiBattleInfoMap);
		}
		
		if (multiGiftInfoMap != null) {
			multiGiftInfoStr = GsonUtil.getJsonInstance().toJson(multiGiftInfoMap);
		}
		
		if (challengeStarMap != null) {
			challengeStarStr = GsonUtil.getJsonInstance().toJson(challengeStarMap);
		}
		
		if (dungeonStarMap != null) {
			dungeonStarStr = GsonUtil.getJsonInstance().toJson(dungeonStarMap);
		}
		
		if (dungeonMaxMap != null) {
			dungeonMaxStr = GsonUtil.getJsonInstance().toJson(dungeonMaxMap);
		}
		
		if (dungeonTimesMap != null) {
			dungeonTimesStr = GsonUtil.getJsonInstance().toJson(dungeonTimesMap);
		}
		
		if (dungeonOneKeyMap != null) {
			dungeonOneKeyStr = GsonUtil.getJsonInstance().toJson(dungeonOneKeyMap);
		}
	}

	public int getMultiFirstBattle() {
		return multiFirstBattle;
	}

	public void setMultiFirstBattle(int multiFirstBattle) {
		this.multiFirstBattle = multiFirstBattle;
	}

	public String getMultiBattleInfoStr() {
		return multiBattleInfoStr;
	}

	public void setMultiBattleInfoStr(String multiBattleInfoStr) {
		this.multiBattleInfoStr = multiBattleInfoStr;
	}
	
	public String getMultiGiftInfoStr() {
		return multiGiftInfoStr;
	}

	public void setMultiGiftInfoStr(String Str) {
		this.multiGiftInfoStr = Str;
	}
	
	public int getChallengeStar(int type) {
		if (challengeStarMap.containsKey(type)) {
			return challengeStarMap.get(type);
		}
		return 1; // 初始為一等
	}
	
	public int getDungeonStar(int type) {
		if (dungeonStarMap.containsKey(type)) {
			return dungeonStarMap.get(type);
		}
		return 1; // 初始為一等
	}
	
	public int getDungeonMax(int type) {
		if (dungeonMaxMap.containsKey(type)) {
			return dungeonMaxMap.get(type);
		}
		return 0; // 初始為0等
	}
	
	public Map<Integer, Integer> getMultiBattleInfoMap() {
		return multiBattleInfoMap;
	}

	public void setMultiBattleInfoMap(Map<Integer, Integer> multiBattleInfoMap) {
		this.multiBattleInfoMap = multiBattleInfoMap;
	}
	
//	public Map<Integer, Integer> getMultiGiftInfoMap() {
//		return multiGiftInfoMap;
//	}
//
//	public void setMultiGiftInfoMap(Map<Integer, Integer> multiGiftInfoMap) {
//		this.multiGiftInfoMap = multiGiftInfoMap;
//	}
	
	public int getMultiGiftTimesByType(int type) {
		if (multiGiftInfoMap.containsKey(type)) {
			return multiGiftInfoMap.get(type);
		}
		return 0;
	}
	
	public int getDungeonTimes(int type) {
		if (dungeonTimesMap.containsKey(type)) {
			return dungeonTimesMap.get(type);
		}
		return 0;
	}
	
	public int getDungeonOneKey(int type) {
		if (dungeonOneKeyMap.containsKey(type)) {
			return dungeonOneKeyMap.get(type);
		}
		return 0;
	}
	
	
	
	/**
	 * 取世界boss挑戰次數
	 * @return
	 */
	public int getWorldBossBuffFreeTimes() {
		return worldBossBuffFreeTimes;
	}
	/**
	 * 設世界boss挑戰次數
	 * @return
	 */
	public void setWorldBossBuffFreeTimes(int worldBossBuffFreeTimes) {
		this.worldBossBuffFreeTimes = worldBossBuffFreeTimes;
	}

	/**
	 * 添加战斗信息
	 * 
	 * @param mapId
	 */
	public void addMultiBattleInfoMap(int mapId) {
		if (this.multiBattleInfoMap.containsKey(mapId)) {
			this.multiBattleInfoMap.put(mapId, this.multiBattleInfoMap.get(mapId) + 1);
		} else {
			this.multiBattleInfoMap.put(mapId, 1);
		}
	}
	
	/**
	 * 清除信息
	 */
	public void clearMultiBattleInfoMap() {
		if (this.multiBattleInfoMap != null || this.multiBattleInfoMap.size() > 0) {
			multiBattleInfoMap.clear();
		}
	}
	
	/**
	 * 增加領取該類型獎勵次數
	 * 
	 * @param type
	 */
	public void addMultiGiftInfoMap(int type) {
		if (this.multiGiftInfoMap.containsKey(type)) {
			this.multiGiftInfoMap.put(type, this.multiGiftInfoMap.get(type) + 1);
		} else {
			this.multiGiftInfoMap.put(type, 1);
		}
	}
	
	/**
	 * 增加每日地下城已挑戰次數
	 * @param type
	 */
	public void addDungeonOneKey(int type) {
		if (this.dungeonOneKeyMap.containsKey(type)) {
			this.dungeonOneKeyMap.put(type, this.dungeonOneKeyMap.get(type) + 1);
		} else {
			this.dungeonOneKeyMap.put(type, 1);
		}
	}
	
	/**
	 * 增加每日地下城已挑戰次數
	 * @param type
	 */
	public void addDungeonTimesMap(int type) {
		if (this.dungeonTimesMap.containsKey(type)) {
			this.dungeonTimesMap.put(type, this.dungeonTimesMap.get(type) + 1);
		} else {
			this.dungeonTimesMap.put(type, 1);
		}
	}
	/**
	 * 扣除每日地下城已挑戰次數
	 * @param type
	 */
	public void decDungeonTimesMap(int type) {
		if (this.dungeonTimesMap.containsKey(type)) {
			this.dungeonTimesMap.put(type, Math.max(this.dungeonTimesMap.get(type) - 1,0));
		} else {
			this.dungeonTimesMap.put(type, 0);
		}
	}

	/**
	 * 清除領獎紀錄
	 */
	public void clearMultiGiftInfoMap() {
		if (this.multiGiftInfoMap != null || this.multiGiftInfoMap.size() > 0) {
			multiGiftInfoMap.clear();
		}
	}
	
	/**
	 * 清除每日地下城挑戰次數
	 */
	public void clearDungeonTimesMap() {
		if (this.dungeonTimesMap != null || this.dungeonTimesMap.size() > 0) {
			dungeonTimesMap.clear();
		}
	}
	
	/**
	 * 清除每日地下城一鍵闖關
	 */
	public void clearDungeonOneKey() {
		if (this.dungeonOneKeyMap != null || this.dungeonOneKeyMap.size() > 0) {
			dungeonOneKeyMap.clear();
		}
	}
	
	/**
	 * 增加領取該類型挑戰星數
	 * 
	 * @param type
	 */
	public void addChallengeStarMap(int type) {
		this.challengeStarMap.put(type, this.getChallengeStar(type) + 1);
	}
	
	public void clearDungeonStar() {
		this.dungeonStarMap.clear();
	}
	
	public void addDungeonStar(int type) {
		this.dungeonStarMap.put(type, this.getDungeonStar(type) + 1);
	}
	
	public void setDungeonStar(int type,int star) {
		this.dungeonStarMap.put(type,star);
	}
	
	/**
	 * 設定地下城挑戰的最大關卡
	 * @param type
	 * @param star
	 */
	public void setDungeonMax(int type,int star) {
		this.dungeonMaxMap.put(type,star);
	}


	public int getRoleFirstFastBattle() {
		return roleFirstFastBattle;
	}

	public void setRoleFirstFastBattle(int roleFirstFastBattle) {
		this.roleFirstFastBattle = roleFirstFastBattle;
	}

	public byte[] getLastSnapBattle() {
		return lastSnapBattle;
	}

	public void setLastSnapBattle(BattleInfo.Builder snapBattle) {
		if (snapBattle != null) {
			this.lastSnapBattle = snapBattle.build().toByteArray();
		}

	}

	public BattleInfo.Builder getSnapBattleInfo() {
		return convertSnapshot();
	}

	public BattleInfo.Builder convertSnapshot() {
		if (this.lastSnapBattle.length == 0 || this.lastSnapBattle == null) {
			return null;
		}
		return BuilderUtil.convertSnapBattleInfo(this.lastSnapBattle);
	}

	public int getTodayLoginCount() {
		return todayLoginCount;
	}

	public void setTodayLoginCount(int todayLoginCount) {
		this.todayLoginCount = todayLoginCount;
	}

	public int getBadgeBagSize() {
		return badgeBagSize;
	}

	public void setBadgeBagSize(int badgeBagSize) {
		this.badgeBagSize = badgeBagSize;
	}

	public int getBadgeBagExtendTimes() {
		return badgeBagExtendTimes;
	}

	public void setBadgeBagExtendTimes(int badgeBagExtendTimes) {
		this.badgeBagExtendTimes = badgeBagExtendTimes;
	}

	public int getLeftBadgeBagExtendTimes() {
		return SysBasicCfg.getInstance().getBadgeExtendTimes() - badgeBagExtendTimes;
	}
	
	public int getLastTakeBattleAwardTime() {
		return lastTakeBattleAwardTime;
	}

	public void setLastTakeBattleAwardTime(int lastTakeBattleAwardTime) {
		this.lastTakeBattleAwardTime = lastTakeBattleAwardTime;
	}

	public int getintoLevelTime() {
		return intoLevelTime;
	}

	public void setintoLevelTime(int intoLevelTime) {
		this.intoLevelTime = intoLevelTime;
	}
	
	public int getplaystory() {
		return playstory;
	}

	public void setplaystory(int playstory) {
		this.playstory = playstory;
	}
	
	public Date getNewbieDate() {
		return this.newbieDate;
	}
	
	public void setNewbieDate(Date aDate) {
		this.newbieDate = aDate;
	}
	
	/**
	 * 计算新手活动剩余时间
	 * 
	 * @return
	 */
	public int calcNewbieSurplusTime() {
		if (newbieDate != null) {
			long currentTime = GuaJiTime.getMillisecond();
			long endTime = GuaJiTime.getAM0Date(this.newbieDate).getTime()
					+ SysBasicCfg.getInstance().getNewbieDays() * 86400000;
			int surplusTime = (int) ((endTime - currentTime) / 1000);
			return Math.max(surplusTime, 0);
		}
		return -1;
	}
	
	/**
	 * 玩家通關關卡改變回報伺服器成就管理
	 * @param type
	 * @param playerId
	 * @param playerScore
	 */
	private void postSvrChangeMsg(int type,int playerId, int playerScore) {
		Msg questMsg = Msg.valueOf(GsConst.MsgType.ON_SERVER_MISSION_CHANGE);
		questMsg.pushParam(type);
		questMsg.pushParam(playerId);
		questMsg.pushParam(playerScore);
		GsApp.getInstance().postMsg(RecordFirstManager.getInstance().getXid(), questMsg);
	}
	
	public void incFirstLoginTimes() {
		this.firstLoginTimes = this.firstLoginTimes +1;
	}
	
	public int getFirstLoginTimes() {
		return this.firstLoginTimes;
	}
	
	public void addAccConsumeGold(int gold) {
		this.accConsumeGold = this.accConsumeGold+gold;
	}
	
	public int getAccConsumeGold() {
		return this.accConsumeGold;
	}
	
	public void incArenaWinTimes() {
		this.arenaWinTimes = this.arenaWinTimes +1;
	}
	
	public int getArenaWinTimes() {
		return this.arenaWinTimes;
	}
	
	public void setOrdealFloor(int floor) {
		this.ordealFloor = floor;
	}
	
	public int getOrdealFloor() {
		return this.ordealFloor;
	}
	
	public void setMiningLevel(int lv) {
		this.miningLevel = lv;
	}
	
	public int getMiningLevel() {
		return this.miningLevel;
	}
	
//	public List<Integer> getSecretMsgList() {
//		return secretMsgList;
//	}
//
//	public void setSecretMsgList(List<Integer> secretMsgList) {
//		this.secretMsgList = secretMsgList;
//		this.secretMsg = GsonUtil.getJsonInstance().toJson(this.secretMsgList);
//	}
//
//	public void addSecretMsgId(int id) {
//		if (!this.secretMsgList.contains(id)) {
//			this.secretMsgList.add(id);
//			this.secretMsg = GsonUtil.getJsonInstance().toJson(this.secretMsgList);
//		}
//	}
//	
//	public void delSecretMsgId(int id) {
//		if (this.secretMsgList.contains(id)) {
//			this.secretMsgList.remove(this.secretMsgList.indexOf(id));
//			this.secretMsg = GsonUtil.getJsonInstance().toJson(this.secretMsgList);
//		}
//	}
//	
//	public void clearSecretMsg() {
//		this.secretMsgList.clear();
//		this.secretMsg = GsonUtil.getJsonInstance().toJson(this.secretMsgList);
//	}
//	
//	public int getLastMsgTime() {
//		return lastMsgTime;
//	}
//	
//	public void setLastMsgTime(int atime) {
//		this.lastMsgTime = atime;
//	}
	
	public int getSecretPower() {
		return secretPower;
	}

	public void setSecretPower(int secretPower) {
		this.secretPower = secretPower;
	}

	public int getLastRecoverTime() {
		return lastRecoverTime;
	}

	public void setLastRecoverTime(int lastRecoverTime) {
		this.lastRecoverTime = lastRecoverTime;
	}

	public int getRechargeluckey() {
		return rechargeluckey;
	}

	public Date getLuckyTime() {
		return luckyTime;
	}

	public void setRechargeluckey(int rechargeluckey) {
		this.rechargeluckey = rechargeluckey;
	}

	public void setLuckyTime(Date luckyTime) {
		this.luckyTime = luckyTime;
	}

	public int getFirstFightAward() {
		return firstFightAward;
	}

	public void setFirstFightAward(int firstFightAward) {
		this.firstFightAward = firstFightAward;
	}
	
}
