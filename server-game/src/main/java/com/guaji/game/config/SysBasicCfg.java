package com.guaji.game.config;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import org.guaji.config.ConfigBase;
import org.guaji.config.ConfigManager;
import org.guaji.os.MyException;

import com.guaji.game.item.ItemInfo;
import com.guaji.game.protocol.Const.equipPart;
import com.guaji.game.util.GameUtil;
import com.guaji.game.util.GsConst;
import com.guaji.game.util.WeightUtil;
import com.guaji.game.util.WeightUtil.WeightItem;

@ConfigManager.KVResource(file = "xml/sysBasic.cfg")
public class SysBasicCfg extends ConfigBase {
	
	public static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd_HH:mm:ss");
	/**
	 * 系统版本号
	 */
	protected final String sysVersion;

	private int sysVersionId;

	/**
	 * 神器锻造播报ID
	 */
	private final int forgingBroadcastId;

	/**
	 * 每日贡献值
	 */
	private final int everydayContribution;

	/**
	 * 引导获得的羽毛个数
	 */
	private final String wingLeadGet;

	/**
	 * 翅膀最大等级
	 */
	private final int maxWingLevel;

	/**
	 * 职业元素穿戴等级限制
	 */
	private final int profElementLevelLimit;

	/**
	 * 玩家清空真气属性消耗钻石
	 */
	private final int clearTalentPay;

	/**
	 * 玩家转生成功初始真气点数
	 */
	private final int rebirthTalentNum;

	/**
	 * 玩家升级时获得到的真气点数
	 */
	private final int levelupTalentNum;

	/**
	 * 真气属性每一阶的等级上限
	 */
	private final int talentLevelLimit;

	/**
	 * 帐号绑定是否开启
	 */
	private final boolean accountBoundOpen;

	/**
	 * 帐号绑定领取钻石
	 */
	private final int accountBoundReward;

	/**
	 * 终身卡每日能够领取钻石
	 */
	private final int dailyGoldAward;

	/**
	 * 终身卡玩家获得激活资格的充值数额
	 */
	private final int foreverCardCanActivateNeedGold;

	/**
	 * 激活终身卡需要消耗钻石
	 */
	private final int activateForeverCardGold;

	/**
	 * 排行献礼做展示的人数
	 */
	private final int rankGiftShowNum;

	/**
	 * 排行献礼排行的奖励人数（需要与rankGift.xml中数值一致）
	 */
	private final int rankGiftPlayerNum;

	/**
	 * 发红包奖励固定钻石
	 */
	private final String giveRredEnvelopeAwards;

	/**
	 * 红包钻石区间
	 */
	private final String redEnvelopeGoldInterval;
	/**
	 * 每次发放红包数量
	 */
	private final int giveRedEnvelopeAmount;
	/**
	 * 每日抢红包数量限制
	 */
	private final int everyDayGrabRedEnvelope;
	/**
	 * 充值红包兑换比率
	 */
	private final int redEnvelopeExchangeRate;
	/**
	 * 多人副本最大回合数
	 */
	private final int multiEliteMaxTurn;
	/**
	 * 多人副本进入战斗倒计时长
	 */
	private final int multiEliteCountDown;
	/**
	 * # 每个多人副本最大房间数
	 */
	private final int multiEliteMaxRoomSize;
	/**
	 * 多人副本每日免费次数
	 */
	private final int multiEliteDayFreeTimes;
	/**
	 * 购买多人副本价格信息
	 */
	private final String buymultiElitePrice;
	/**
	 * 多人副本房间人数上限
	 */
	private final int multiEliteMaxCapacity;
	/**
	 * 夺宝奇兵单次寻宝触发宝箱次数
	 */
	private final String raiderSingleSpecialTimes;
	private Set<Integer> raiderSpecialSingleTimes;

	/**
	 * 夺宝奇兵单次宝箱价格
	 */
	private final int treasureRaiderSinglePrice;

	/**
	 * 夺宝奇兵十次宝箱价格
	 */
	private final int treasureRaiderTenPrice;

	/**
	 * 雪地Cell上限
	 */
	private final int snowfieldCellMaxSize;
	/**
	 * 雪地探宝每次购买体力增加值
	 */
	private final int snowfieldBuyAddPhyc;
	/**
	 * 雪地探宝连翻模式到期时间（s）
	 */
	private final int snowfieldContinuePeriod;
	/**
	 * 雪地探宝体力回复周期（s）
	 */
	private final int snowfieldPhycRecoverPeriod;
	/**
	 * 雪地探宝最大体力
	 */
	private final int snowfieldMaxRecoverPhyc;
	/**
	 * 雪地探宝兑换所得物品
	 */
	private final String snowfieldExchangeAwards;
	/**
	 * 雪地探宝兑换所需物品
	 */
	private final String snowfieldExchangeNeeds;
	/**
	 * 台湾大车队乘车券兑换Key
	 */
	private final String taxiCodeKey;
	/**
	 * 台湾大车队活动限制等级
	 */
	private final int taxiLimitLevel;
	/**
	 * 装备高级洗练的价格信息
	 */
	private final String superEquipBaptizePrice;
	/**
	 * 最高的技能等级
	 */
	private final int maxSkillLevel;

	/**
	 * 刷新公会商店价格信息;
	 */
	private final String refreshAllianceShopPrice;
	/**
	 * 好友邀请兑换奖励
	 */
	private final String friendInviteExchangeAwards;
	/**
	 * 好友邀请兑换等级限制
	 */
	private final int friendInviteLevelLimit;

	/**
	 * 是否开启远端CMem快照缓存服务
	 */
	protected final int cmcStatus;

	/**
	 * CMem快照缓存有效时间（单位：小时）
	 */
	protected final int cmcObjInvalidHours;

	/**
	 * CMem快照服务读写超时时间（单位：毫秒）
	 */
	protected final int cmcGetsTimeOut;

	/**
	 * 聊天信息最大长度
	 */
	protected final int chatMsgMaxLen;

	/**
	 * 最大名字长度
	 */
	protected final int roleNameMaxLen;

	/**
	 * 聊天的冷却时间
	 */
	protected final int chatElapse;

	/**
	 * 玩家对象缓存时间
	 */
	protected final int playerCacheTime;

	/**
	 * 地图统计战斗时间
	 */
	protected final String statisticsFightTime;

	/**
	 * 地图战斗时间
	 */
	protected final String battlefieldTime;

	/**
	 * 战斗最小冷却
	 */
	protected final int fightMinCool;

	/**
	 * 战斗前后端延时
	 */
	protected final int fightDelayTime;

	/**
	 * 最大战斗回合
	 */
	protected final int fightMaxTurn;

	/**
	 * 竞技场最大回合
	 */
	protected final int arenaMaxTurn;

	/**
	 * boss最大回合
	 */
	protected final int bossMaxTurn;

	/**
	 * 竞技场-玩家最小初始排行
	 */
	protected final int playerMinInitRank;

	/**
	 * 竞技场-每页可挑战的玩家的数量
	 */
	protected final int pageDefenderQty;

	/**
	 * 竞技场-每天玩家免费挑战次数
	 */
	protected final int freeChallengeTimes;

	/**
	 * 竞技场-排行榜中显示的数量
	 */
	protected final int rankingListPlayerQty;

	/**
	 * 全服挂机掉率率
	 */
	protected final float globalAwardRatio;

	/**
	 * 竞技场-每日奖励时间
	 */
	protected final String arenaRewardTimeStr;
	/**
	 * 竞技场奖励时间
	 */
	protected int arenaRewardTime;

	/**
	 * 竞技场广播世界聊天名次
	 */
	protected final int broadcastWorldMsgRank;

	/**
	 * 竞技场-竞技记录数量上限
	 */
	protected final int maxArenaRankRecord;

	/**
	 * 留言-单个玩家之间显示的最大留言数
	 */
	protected final int maxMsgQty;

	/**
	 * 快照-服务器玩家快照数量上限
	 */
	protected final int maxPlayerSnapShotQty;

	/**
	 * 快照-当前服务器玩家快照版本
	 */
	protected final int playerSnapShotVersion;

	/**
	 * 公会排行榜
	 */
	protected final int rankingAllianceQty;

	/**
	 * 公会鼓舞次数
	 */
	protected final int allianceBossAddPorpNumber;

	/**
	 * 装备背包默认空间上限
	 */
	private final int equipBagSize;

	/**
	 * 装备背包扩展上限
	 */
	private final int equipExtendTimes;

	/**
	 * 装备背包扩展一次增加多少
	 */
	private final int equipExtendSize;

	/**
	 * 装备背包扩展一次多少钻石
	 */
	private final String equipExtendGoldCostStr;

	private List<Integer> equipExtendGoldCostList;

	/**
	 * 最大离线挂机时间
	 */
	private final int maxOfflineTime;

	/**
	 * 快速战斗时间
	 */
	private final int fastFightTime;

	/**
	 * 购买快速战斗的价格信息
	 */
	private final String buyFastFightPrice;

	/**
	 * 购买boss挑战价格信息
	 */
	private final String buyBossFightPrice;

	/**
	 * 购买精英副本价格信息
	 */
	private final String buyEliteMapPrice;

	/**
	 * 开启技能专精消耗的物品信息
	 */
	private final String openSkillEnhanceCostItems;

	/**
	 * 刷新次数和钻石对应表
	 */
	private final String refashCostGoldNums;

	/**
	 * 刷新次数和钻石对应表
	 */
	private int[] refreshCostGoldNums;

	/**
	 * 技能格子开放数等级限制信息
	 */
	private final String skillSlotLevelLimit;
	private final String mercenarySkillSlotLevelLimit;

	/**
	 * 团战-参加团战的最小战力值
	 */
	private final int joinTeamBattleMinFight;

	/**
	 * 团战-每队人数上限
	 */
	private final int teamBattleMaxPlayerQty;

	/**
	 * 团战-每日团战开始时间字符串
	 */
	private final String teamBattleStartTimeStr;

	/**
	 * 团战-每日团战开始时间
	 */
	private final List<Integer> teamBattleStartTime;

	/**
	 * 团战-上一次团战结束到下一次团战开始之间的休战时间
	 */
	private final int teamBattleEndSwitchTime;

	/**
	 * 团战-一次团战中每轮战斗的间隔时间
	 */
	private final int teamBattleRoundIntervalTime;

	/**
	 * 团战-队长奖励金币系数（等级*金币系数）
	 */
	private final int captainGoldRatio;

	/**
	 * 团战-队员奖励金币系数（等级*金币系数）
	 */
	private final int memberGoldRatio;

	/**
	 * 团战-每轮奖励金币系数（等级*金币系数）
	 */
	private final int roundGoldRatio;

	/**
	 * 团战-队长额外奖励物品（每回合发放）
	 */
	private final String captainExtraAward;

	/**
	 * 团战-全员额外奖励物品
	 */
	private final String extraAward;

	/**
	 * 团战-冠军队长额外奖励物品
	 */
	private final String winTeamCaptainExtraAward;

	/**
	 * 团战-冠军队员额外奖励物品
	 */
	private final String winTeamMemberExtraAward;

	/**
	 * 团战-创建队伍消耗钻石
	 */
	private final int createTeamPrice;

	/**
	 * 留言-最大字数限制
	 */
	private final int msgMaxLength;
	/**
	 * 购买快速战斗的价格信息
	 */
	private List<Integer> fastFightBuyPrice;

	/**
	 * 高级装备洗练的价格信息
	 */
	private List<Integer> superEquipBaptizePrices;

	/**
	 * 刷新公会商店价格
	 */
	private List<Integer> refreshAllianceShopPrices;

	/**
	 * 购买boss挑战价格信息
	 */
	private List<Integer> bossFightBuyPrice;
	/**
	 * 购买买多人副本价格信息
	 */
	private List<Integer> multiEliteBuyPrice;

	/**
	 * 购买精英副本价格
	 */
	private List<Integer> eliteMapBuyPrice;

	/**
	 * 技能格子数的等级限制信息
	 */
	private List<String> skillSlotLevels;
	private List<Integer> mercenarySkillSlotLevels;

	/**
	 * 地图统计战斗时间
	 */
	private List<Integer> statisticsFightTimeCost;

	/**
	 * 地图战斗最大时间
	 */
	private List<Integer> battlefieldTimeCost;

	/**
	 * 装备部位 和 神器属性id 对应关系
	 */
	private final String equipPartGodlyAttr;

	/**
	 * 装备部位 和 神器属性id 对应关系
	 */
	private final String equipPartGodlyAttr2;

	/**
	 * 职业装备洗炼偏向性
	 */
	private final String equipBaptize;

	/**
	 * 公会个人exp奖励
	 */
	private final int allianceReprotExp;

	/**
	 * 公会公会exp奖励
	 */
	private final int allianceReprotSysCoin;

	/**
	 * 公会创建等级
	 */
	private final int allianceCreateLevel;
	/**
	 * 公会创建vip等级限制
	 */
	private final int allianceCreateVipLevel;

	/**
	 * 公会列表，元气增量数值
	 */
	private final int allianceListVitality;

	/**
	 * 特殊类型cdk
	 */
	private final String specialCdks;

	/**
	 * 初始钻石
	 */
	private final int initGold;

	/**
	 * 初始金币
	 */
	private final int initCoin;
	/**
	 * 初始化vip等级
	 */
	private final int initVipLevel;
	/**
	 * 装备出孔概率
	 */
	protected final String punchWeight;
	protected final String equipSwallowPartRatio;

	/**
	 * 创建公会需要钻石
	 */
	protected final int allianceCreateGold;

	/**
	 * 免费开启
	 */
	protected final int allianceBossNumber;

	/**
	 * 礼包配置
	 */
	protected final String giftInfos;

	/**
	 * 职业排行奖励发放的间隔天数
	 */
	private final int profRankRewardDays;

	/**
	 * 职业排行奖励发放的时间
	 */
	private final String profRankReawrdTimeStr;

	/**
	 * 职业排行奖励发放的时间
	 */
	protected int profRankReawrdTime;

	/**
	 * 每日新服好礼领取限制
	 */
	private final int dailyNewSerGiftRewardLimit;

	/**
	 * 公会战对大伦次
	 */
	private final int allianceBattleMaxTurn;

	/**
	 * 装备部位神器属性id表
	 */
	private Map<Integer, Integer> equipPartGodlyAttrIdMap;

	/**
	 * 装备部位神器属性id表2
	 */
	private Map<Integer, Integer> equipPartGodlyAttrIdMap2;

	private List<WeightItem<Integer>> punchWeightItems;

	private Map<Integer, Integer> equipSwallowPartRatioMap;

	private Map<Integer, List<WeightItem<Integer>>> equipBaptizeAttrAllocMap;

	private Map<String, String> specialCdkMap;

	private List<ItemInfo> giftAwardItems;

	private List<Integer> warriorNotCritSkillIdList;

	/**
	 * 自动加入boss战斗的vip限制
	 */
	private final int autoBossJoinVipLimit;

	/**
	 * 公会Boss自动参战扣费
	 */
	protected final int allianceAutoFightCostGold;

	/**
	 * 帮派踢人消耗经验值
	 */
	private final int kickAlliamceMemberExp;

	/**
	 * 好友数量限制
	 */
	private final int friendCountLimit;

	/**
	 * 申请好友数量限制
	 */
	private final int applyFriendCountLimit;

	/**
	 * 荣誉商店刷新消耗字符串
	 */
	private final String honorShopRefreshCostStr;

	/**
	 * 水晶商店刷新消耗字符串
	 */
	private final String crystalShopRefreshCostStr;

	private List<Integer> honorShopRefreshCost;

	private List<Integer> crystalShopRefreshCost;

	/**
	 * 最大屏蔽个数
	 */
	private final int maxShieldSize;

	/**
	 * 职业排行最大排行个数
	 */
	private final int maxProfRankNum;

	/**
	 * 职业排行最大奖励名次
	 */
	private final int maxProfRankRewardNum;

	/**
	 * 职业排行最大显示名次
	 */
	private final int maxProfRankShowNum;

	/**
	 * 神器合成消耗的道具
	 */
	private final int equipCompoundItemId;

	/**
	 * 阵营战-主界面连杀排行榜人数上限
	 */
	private final int lastCampWarWinStreakMaxRank;

	/**
	 * 阵营战-战场内连杀排行上限
	 */
	private final int curCampWarWinStreakMaxRank;

	/**
	 * 阵营战-最大鼓舞次数
	 */
	private final int campWarMaxInspireTimes;

	/**
	 * 阵营战-每次鼓舞价格（钻石）
	 */
	private final int campWarInspirePrice;

	/**
	 * 阵营战-每次鼓舞加成百分比
	 */
	private final int campWarInspireBonuses;

	/**
	 * 阵营战-每日准备时间
	 */
	private final String campWarPrepareTimeStr;
	private int campWarPrepareTime;

	/**
	 * 阵营战-每日战斗开始时间
	 */
	private final String campWarBattleStartTimeStr;
	private int campWarBattleStartTime;

	/**
	 * 阵营战-每日战斗结束时间
	 */
	private final String campWarBattleStopTimeStr;
	private int campWarBattleStopTime;

	/**
	 * 阵营战-每日展示结束时间
	 */
	private final String campWarShowStopTimeStr;
	private int campWarShowStopTime;

	/**
	 * 阵营战-战斗频率(单位：s)
	 */
	private final int campWarBattlePeriod;

	/**
	 * 阵营战-参加所需最小战斗力
	 */
	private final int campWarMinFightValue;

	/**
	 * 阵营战-单轮战斗胜利奖励金币系数
	 */
	private final int campWarRoundWinGoldRatio;

	/**
	 * 阵营战-单轮战斗胜利奖励声望
	 */
	private final int campWarRoundWinReputation;

	/**
	 * 阵营战-胜者所在阵营增加积分
	 */
	private final int campWarWinnerAddScore;

	/**
	 * 阵营战-败者所在阵营增加积分
	 */
	private final int campWarLoserAddScore;

	/**
	 * 阵营战-每轮连胜战报显示数量
	 */
	private final int campWarMaxWinStreakReport;

	/**
	 * 阵营战-每轮终结战报显示数量
	 */
	private final int campWarMaxEndStreakReport;

	/**
	 * 阵营战-自动阵营战VIP等级
	 */
	private final int autoCampWarVipLevel;

	/**
	 * 阵营战-自动阵营战扣除钻
	 */
	private final int autoCampWarGold;
	/**
	 * 阵营战-获胜阵营奖励倍率
	 */
	private final int winCampExtraReputation;

	/**
	 * 阵营战-战场投资（自动加入）奖励系数
	 */
	private final double autoCampWarAwardRatio;

	/**
	 * 手动战斗冷却周期
	 */
	private final int manualBattleCd;

	/**
	 * 活动横条
	 */
	private final int bannerActivityId;

	/**
	 * 神器融合消耗的声望值
	 */
	private final int equipCompoumdCostRep;

	/**
	 * 充值返利活动-返利所需最小充值单位
	 */
	private final int rechargeMinUnitGold;

	/**
	 * 充值返利活动-每日返利最小单位
	 */
	private final int rebateMinUnitGold;

	/**
	 * 竞技场胜利奖励
	 */
	private final String arenaWinAward;

	/**
	 * 竞技场胜利奖励
	 */
	private final String arenaLoseAward;

	/**
	 * 充值返利活动-每日返利最大钻石
	 */
	private final int everydayRebateMaxGold;

	/**
	 * 猎豹移动主机地址
	 */
	private final String cmHost;

	/**
	 * 猎豹移动访问超时时间
	 */
	private final int cmTimeout;

	/**
	 * 公会Boss-快速战斗一次元气值
	 */
	private final int quickBattleAddVitality;

	/**
	 * 公会Boss-战胜世界BOSS元气值
	 */
	private final int worldBossAddVitality;

	/**
	 * 公会Boss-参加多人团战元气值（战斗结束时发放）
	 */
	private final int teamBattleAddVitality;

	/**
	 * 公会Boss-参加阵营战元气值（战斗结束时发放）
	 */
	private final int campWarAddVitality;

	/**
	 * 公会Boss-竞技场挑战成功元气值
	 */
	private final int arenaWinAddVitality;

	/**
	 * 公会Boss-竞技场挑战失败元气值
	 */
	private final int arenaFailAddVitality;

	/**
	 * 远征活动排行榜人数
	 */
	private final int expeditionArmoryRankingSize;

	/**
	 * 工会战每场战斗时间 单位s
	 */
	private final int allianceBattleTime;
	/**
	 * 远征活动每次系统自动增加的积分量
	 */
	private final int expeditionArmorySysAddExp;

	/**
	 * 是否开启远征物资自动增加积分
	 */
	private final int openExpeditionArmoryAutoAddExp;

	/**
	 * 远征活动自动增加积分时间间隔（单位：s）
	 */
	private final int expeditionArmoryAutoAddExpTime;

	/**
	 * 开启远征物资自动增加积分阶段
	 */
	private final int expeditionArmoryAutoAddExpStage;
	/**
	 * 邮件过期删除天数
	 */
	private final int emailDelDay;

	/**
	 * 转1次疯狂转盘所需充值的数额
	 */
	private final int crazyRouletteRechargeUnit;

	/**
	 * 疯狂转盘每日次数上限
	 */
	private final int crazyRouletteDayMaxTimes;

	/**
	 * 疯狂转盘每次增加积分
	 */
	private final int crazyRouletteAddCredits;

	/**
	 * 开关：分享后是否赠送金币 0：不赠送 1:赠送
	 */
	private final int nLargessGoldofShare;
	/**
	 * /** 分享后赠送金币数量
	 */
	private final int nLargessGoldNum;
	/**
	 * 团战踢人钻石消耗递增值
	 */
	private final int teamBattleKickMemberCostAdd;
	/**
	 * 团战踢人钻石消耗最大值
	 */
	private final int teamBattleKickMemberCostMax;

	/**
	 * 熔炼值初始
	 */
	private final int equipSmeltNum;

	/**
	 * 评价送钻石
	 */
	private final int evaluateRewardGold;
	/**
	 * 初始音乐是否开启:1 开启，0 关闭
	 */
	private final int initMusicOn;

	/**
	 * # 新手送月卡活动是否开启：1 开启, 0 关闭
	 */
	private final int jpNewbieRewardMonthCard;

	/**
	 * 公会战场BUFF
	 */
	protected final String allianceBattleBuff;

	/**
	 * 下线清理无效数据
	 */
	protected final boolean deleteInvalid;
	/**
	 * ipcache
	 */
	protected final boolean ipCacheEnable;
	/**
	 * 战斗Action时间系数
	 */
	private final float fightActionTimeRatio;
	/**
	 * 周几不能开启争霸战
	 */
	private final int allowAllianceBattleOpenWeekDay;

	/**
	 * app大版本更新提醒
	 */
	private final String appVersionNotice;
	/**
	 * 跨服战奖励是否可以领取
	 */
	private final Boolean csReward;

	// 光环升级所需佣兵星级
	private final int ringLvlUpNeedStarLvl;
	// 光环升级次数
	private final int ringLvlUpTimes;

	/**
	 * 改名字消耗的钻石
	 */
	private final int changeNameCost;
	/**
	 * 公会战场BUFF
	 */
	private Map<Integer, List<Integer>> allianceBattleBuffMap;

	private final int maxElementSize;
	/**
	 * 默认元素背包大小
	 */
	private final int elementBagDefault;

	/**
	 * 扩展元素背包消耗
	 */
	private final int elementBagExtendCost;

	/**
	 * 扩展元素背包增加的格子数
	 */
	private final int elementBagExtendNum;
	/**
	 * 元素吞噬提供的经验的品质系数
	 */
	private final String elementSwallowExp;

	private Map<Integer, Float> elementSwallowExpRatio;
	/**
	 * 高级重铸消耗的钻石
	 */
	private final int recastGoldCost;
	/**
	 * YY钻石兑换豆芽比例
	 */
	private final int yyExchangeRadio;
	/**
	 * Facebook好友每日索取最大次数（tzy）
	 */
	private final int askTicketCount;
	/**
	 * Facebook索取所得物品
	 */
	private final String askTickAwards;

	/**
	 * 黑市手动刷新次数
	 */
	private final String manuelRefreshDefaultCount;
	/**
	 * 黑市手动刷新价格
	 */
	private final String manuelRefreshPrize;

	/**
	 * 黑市手动刷新次数和钻石对应表
	 */
	private int[] refreshMysteryCostGoldNums;

	/**
	 * 公会长每天发邮件次数
	 */
	private final int sendEmailNum;

	/**
	 * 全局静态对象
	 */
	private static SysBasicCfg instance = null;

	/**
	 * 全局服务器是否开启
	 */
	private final boolean globalServerOpen;

	/**
	 * YY兑换排行最大人数
	 */
	private final int yyExchangeRankTopNum;

	/**
	 * 英雄令任务每日最大个数
	 */
	private final int heroTokenTaskLimit;

	/**
	 * 终身卡用户增加英雄完成次数
	 */
	private final int heroTokenTaskUpgrade;

	/**
	 * YY兑换排行领奖最小充值数量
	 */
	private final int yyExchangeRankAwardLimit;

	/**
	 * 排名献礼tick时间
	 */
	private final long rankGiftTickTime;

	private List<String> platformBlackList;

	private final String platformForeverCard;

	/**
	 * 合服低等级玩家邮件福利
	 */
	private final String mergeServerRewards;

	/**
	 * 合服福利玩家等级
	 */
	private final int mergePlayerLevel;

	/**
	 * 合服福利时间
	 */
	private final String mergeTime;

	/**
	 * facebook分享奖励
	 */
	private final String faceBookShareRewards;

	/**
	 * 装备打造刷新等级浮动
	 */
	private final String refreshEquipOffsetLevel;

	/**
	 * 装备打造刷新等级浮动权重
	 */
	private final String refreshEquipWeight;

	private List<Integer> refreshEquipOffsetLevelList;

	private List<Integer> refreshEquipWeightList;

	private final int hourCardUseCount;

	/**
	 * 账号绑定奖励
	 */
	private final String playerBindRewards;

	/**
	 * 远征刷新消耗字符串
	 */
	private final String expeditionTaskRefreshCostStr;

	private List<Integer> expeditionTaskRefreshCost;

	private final int applyAddAllianceMaxCount;

	private final int refreshApplyAddAllianceTime;

	private final int vitalityNoChangeDayTime;

	// 气枪打靶活动刷新奖池时间
	private final int shootRefreshTime;
	// 活动关闭时间
	private final int shootCloseTime;
	// 聊天缓存
	private final int chat_msg_chunk;
	// ios专属礼包
	private final String ios_git_reward;

	/**
	 * 佣兵培养开启等级
	 */
	private final int roleBaptizeLevel;
	/**
	 * 神器融合等级
	 */
	private final int equipCompoumdLevel;
	/**
	 * 技能升级开启等级
	 */
	private final int skillUpgradeLevel;
	/**
	 * 可申请加入公会数量最大上限值
	 */
	private final int applyAddAllianceMaxVal;

	/**
	 * 装备打造开启等级
	 */
	private final int equipSpecialLevel;
	/**
	 * boos挑战卷ID
	 */
	private final int bossChallengeItemId;

	/**
	 * 佣兵训练开启等级
	 */
	private final int roleTrainOpenLevel;

	/**
	 * 第一次快速战斗添加固定经验值
	 */
	private final int firstFastFightExp;

	/**
	 * 成长基金购买vip等级限制
	 */
	private final int growthVipLevel;
	/**
	 * 成长基金购买所需钻石
	 */
	private final int growthNeedGold;

	/**
	 * 开佣兵格子
	 */
	private final String openFightingBoxStr;

	/**
	 * 开发佣兵格子等级
	 */
	private Map<Integer, Integer> openFightingLevelMap;

	/**
	 * 开放应援格子
	 */
	private final String openAssistanceBoxStr;

	/**
	 * 开放应援格子
	 */
	private Map<Integer, Integer> openAssistanceLevelMap;
	/**
	 * 新夺宝奇兵活动(UR抽奖)必定获得奖励次数
	 */
	private final String newTreasureRaiderLimitTimesStr;
	private Set<Integer> newTreasureRaiderLimitTimes;
	/**
	 * 新神将投放必定获得奖励次数
	 */
	private final String releaseURLimitTimesStr;
	private Set<Integer> releaseURLimitTimes;
	/**
	 * 少女的保佑累计充值活动金钱和鲜花的比例
	 */
	private final int goldFlowerRatio;
	/**
	 * 少女的邂逅活动免费互动次数
	 */
	private final int freeInteractTimes;
	/**
	 * 少女的邂逅活动免费刷新次数
	 */
	private final int freeRefreshTimes;

	/**
	 * 新手UR活动必定获得奖励次数
	 */
	private final String newURLimitTimesStr;
	private Set<Integer> newURLimitTimes;

	/**
	 * 获取改名卡物品ID
	 */
	private final String changeNameCard;

	/**
	 * vip档位修改起始等级
	 */
	private final int checkCurVipLevel;

	/**
	 * 副将品质减少搜索敌人时间配置 副将品质_单个减少时间 多个用逗号隔离
	 */
	private final String decrPVESearchTime;

	private Map<Integer, Integer> decrPVESearchTimeMap;

	/***
	 * 聊天同样的信息重发时间间隔
	 */
	private final int chatSameMsgInterval;
	/**
	 * 荣誉商店(竞技场商店)显示数目
	 */
	private final int honorShopShowCount;
	/**
	 * 联盟商店显示数目
	 */
	private final int allianceShopShowCount;
	/***
	 * 每日充值返利功能开关
	 */
	private final int dailyChargeRebateEnable;
	/**
	 * 每日充值返利发放奖励时间
	 */
	private final String dailyChargeRebateTime;
	/***
	 * cdk统一码
	 */
	private final String cdkUniteCode;

	/***
	 * UR领奖重置需要消耗钻石
	 */
	private final int releaseUrResetCost;

	/***
	 * UR抽奖需要消耗幸运值
	 */
	private final int releaseUrLotteryCost;

	/***
	 * 抽一次送多少积分
	 */
	private final int releaseUrGiveLucky;

	/***
	 * UR领奖重置需要消耗钻石 121 活动
	 */
	private final int releaseUrResetCost3;

	/***
	 * UR抽奖需要消耗幸运值 121 活动
	 */
	private final int releaseUrLotteryCost3;

	/***
	 * 抽一次送多少积分 121 活动
	 */
	private final int releaseUrGiveLucky3;

	private final int giftExchangeSwitch;

	private final String screenedMailId;
	/**
	 * 屏蔽的邮件列表
	 */
	private List<Integer> screenedMailIdList;

	/**
	 * Ur复刻版 抽奖消耗幸运值
	 */
	private final int releaseUrCostLucky2;

	/***
	 * UR 抽卡一次所送积分
	 */
	private final int releaseUrGiveLucky2;

	/**
	 * 活跃度达标 周期性天数
	 */
	private final int activeComplianceCycleDays;

	/**
	 * 活跃度达标目标点数
	 */
	private final int activeCompliancePoint;

	/**
	 * 活跃度达标可参与领奖天数
	 */
	private final int activeCanAwardDays;

	/**
	 * 束缚彼女 积分抽奖消耗
	 */
	private final int activity123UrCostLucky;

	/**
	 * 束缚彼女 一次抽卡所给积分
	 */
	private final int activity123UrGiveLucky;

	/**
	 * 武器召唤副本
	 */
	private final int activity127UrGiveLucky;

	/**
	 * 128抽奖排行活动积分奖励
	 */
	private final int activity128UrGiveLucky;

	/**
	 * 128抽奖排行活动最大参与排名数量
	 */
	private final int activity128UrRankMaxNum;

	/**
	 * 128抽奖排行活动每天结算时间
	 */
	private final String activity128UrRankCalcTime;

	/***
	 * 特殊装备熔炼
	 */
	// private List<Long> specialSmeltEquipList;

	/**
	 * 装备高级洗练的价格信息
	 */
	// private final String specialSmeltEquipStr;

	/**
	 * 宝石单次最大兑换个数
	 */
	private final int shopGemBuyMaxOnce;

	/**
	 * 高速育成开启等级
	 */
	private final int autoHighSpeedBreedOpenVip;

	/**
	 * 123 连点成线概率
	 */
	private final int act123EvenDotsRate;

	/**
	 * 116 连点成线概率
	 */
	private final int act116EvenDotsRate;

	/**
	 * 132 活动老用户界定时间
	 */
	private final String activity132OldUserTime;

	/**
	 * 132 活动老用户界定等级
	 */
	private final int activity132OldUserLevel;

	/**
	 * 18路诸侯最大关卡数
	 */
	private final int eighteenPrincesMaxLayerNum;

	/**
	 * 18路诸侯开启等级
	 */
	private final int eighteenPrincesOpenLevel;

	/**
	 * 18路诸侯开服后多少天
	 */
	private final int eighteenPrincesOpenServerDays;

	/**
	 * 18 路诸侯协战吴江使用最多次数
	 */
	private final int eighteenMaxUseTimes;

	/**
	 * 18 路诸侯协战武将免费次数
	 */
	private final int eighteenFreeUseTimes;
	/**
	 * 18 路诸侯协战武将使用消耗钻石数
	 */
	private final int eighteenUseSpend;

	/**
	 * 137 老虎机充值返利活动开启的登录次数
	 */
	private final String activity137OpenLoginCount;

	/**
	 * # 137 老虎机充值返利活动每次活动时长（分钟）
	 */
	private final int activity137OpenTime;

	/**
	 * 默认徽章背包上限
	 */
	private final int badgeBagSize;

	/**
	 * 徽章背包扩展次数
	 */
	private final int badgeExtendTimes;

	/**
	 * badgeExtendSize
	 */
	private final int badgeExtendSize;

	/**
	 * 装备背包扩展一次多少钻石
	 */
	private final String badgeExtendGoldCostStr;

	/**
	 * 装备背包扩展一次多少钻石
	 */
	private final List<Integer> badgeExtendGoldCostList;

	/**
	 * 140 活动持续时间 (单位分钟)
	 */
	private final int activity140Continuetime;

	/**
	 * 单次转动轮盘钻石消耗
	 */
	private final int activity140LotteryCost;
	
	
	/**
	 * 140  活动持续时间 登陆次数
	 */
	private final String activity140OpenLoginCount;

	/**
	 * 解鎖奧義所需物品數量
	 */
	private final String buyAlbumItemCountStr;

	/**
	 * 解鎖奧義所需物品數量
	 */
	private final List<Integer> buyAlbumItemCountList;
	
	/**
	 * 解鎖奧義所需物品Id
	 */
	private final int buyAlbumItemId;
	
	/**
	 * 使用物品數量上限鎖號
	 */
	private final int useItemMaxcount;

	/**
	 * 大富翁擲骰所需物品
	 */
	private final String RichManUseItem;
	
	/**
	 * 大富翁擲骰位置種類
	 */
	private final String RichManStpeTypeStr;

	/**
	 * 大富翁擲骰位置種類
	 */
	private final List<Integer> RichManStpeTypeList;
	
	/**
	 * 海盜寶箱開箱所需物品
	 */
	private final String PirateUseItem;
	/**
	 *修學環境適應力道具
	 */
	private final int AdaptationType;

	/**
	 *HOH Server戰鬥檢查
	 */
	private final int BattleCheck;
	/**
	 *HOH裝備強化等級上限
	 */
	private final int EquipLimitLevel;
	/**
	 * 神秘商店刷新消耗
	 */
	private final String MysteryRefreshCost;
	
	private LinkedList<Integer>  MysteryRefreshList;
	/**
	 * 神秘商店最大免費刷新
	 */
	private final int MysteryFreeRefresh;
	/**
	 * 神秘商店最大收費刷新
	 */
	private final int MysteryMaxRefresh;
	/**
	 * 神秘商店免費次數新增間隔
	 */
	private final int MysteryRefreshTime;
	/**
	 * 英靈召喚單抽消費
	 */
	private final int ChosenOneSingleCost;
	/**
	 * 英靈召喚十抽消費
	 */
	private final int ChosenOneTenCost;
	/**
	 *英靈召喚保底抽數
	 */
	private final int ChosenOneGuarant;
	/**
	 * 英靈召喚十抽道具消費
	 */
	private final String ChosenOneItemCost;
	/**
	 * 英靈召喚十抽道具消費
	 */
	private final String ChosenOneTenItemCost;
	/**
	 * PickUp單抽道具消費
	 */
	private final String PickUpItemCost;
	/**
	 * PickUp十抽道具消費
	 */
	private final String PickUpTenItemCost;
	
	/**
	 * 許願輪消耗道具
	 */
	private final String WishingCostItem;
	
	/**
	 * 装备背包扩展一次多少钻石
	 */
	private final List<String> WishingCostList;
	/**
	 * 許願輪單次抽獎累積幸運
	 */
	private final int WishingIncLucky;
	/**
	 * 許願輪免費刷新間隔(秒)
	 */
	private final int WishingRefreshTime;
	/**
	 * 許願輪(星)免費抽數
	 */
	private final int WishingFreeDraw;
	/**
	 * 許願輪付費刷新消耗
	 */
	private final String WishingRefreshCost;
	
	/**
	 * 小瑪莉消耗道具
	 */
	private final String MarryCostItem;
	/**
	 * 小瑪莉各個抽獎消耗數量
	 */
	private final String MarryCostStr;
	/**
	 * 小瑪莉各個抽獎消耗數量
	 */
	private final List<Integer> MarryCostCountList;
	/**
	 * 新手系列活動開啟天數
	 */
	private final int NewbieDays;
	/**
	 * 一次可贈送友情點數
	 */
	private final int FriendshipPoint;
	/**
	 * 英雄劇情禮物
	 */
	private final String HeroDramaGift;
	/**
	 * 重置英雄消耗鑽石
	 */
	private final String ResetHero;
	/**
	 * 重置英雄消耗道具
	 */
	private final String ResetHeroCostItem;
	/**
	 * 精靈召喚單抽消耗道具
	 */
	private final String SpriteSingleCost;
	/**
	 * 精靈召喚十抽消耗道具
	 */
	private final String SpriteTenCost;
	/**
	 * 累積登入領取獎勵物品
	 */
	private final String LoginTenDrawAward;
	/**
	 * 累積登入領取總天數
	 */
	private final int LoginTenDrawDays;
	/**
	 * 種族召喚消耗物品
	 */
	private final String RaceSummon;
	/**
	 * 種族召喚每抽積分
	 */
	private final int RacePoint;
	
	/**
	 * 收費打卡消耗道具
	 */
	private final String SupportCalendarCost;
	
	/**
	 * 購買打卡表消耗道具列表
	 */
	private final List<String> SupportCalendarCostList;
	/**
	 * 挖礦活動最大排行榜人數
	 */
	
	private final int activity165RankMaxNum;
	
	/**
	 * 挖礦活動消耗物品
	 */
	private final String GoldMineCost;
	
	/**
	 * 重置公會魔典消耗物品
	 */
	private final String ReSetGuildSoulItem;
	
	/**
	 * 重置裝備強化
	 */
	private final String ResetEquip;
	/**
	 * 購買金幣兩倍機率
	 */
	private final int BuyCoinCriRate;
	/**
	 * 友情召喚單抽消費
	 */
	private final int CallOfFriendshipSingleCost;
	/**
	 * 友情召喚十抽消費
	 */
	private final int CallOfFriendshipTenCost;
	/**
	 * 快速領取經驗藥水
	 */
	private final String HeroEXPItem;
	/**
	 * 快速領取突破石
	 */
	private final String HeroStoneItem;
	/**
	 * 首儲首抽幸運度
	 */
	private final String ChargeGuaranteed;

		
	private List<Integer> RechargeLuckyList;
	
	private final int RechargeLuckyCD;
		
	/**
	 * 每抽種族召喚額外獎勵
	 */
	private final String CallOfRaceAward;
	
	/**
	 * 175壁尻排行活动每天遊玩时间
	 */
	private final String activity175GameTime;
	
	/***
	 * 175壁尻排行活动每天遊玩时间
	 */
	
	private List<String> act175GameTimeList;
	
	/**
	 * 175壁活动團隊獎勵
	 */	
	private final String activity175TeamAward;
	
	/**
	 * 175壁活动團隊獎勵
	 */
	private List<String> act175TeamAwardList;
	
	/**
	 * 壁尻免費爽次數
	 */
	private final int GloryHoleFree;
	/**
	 * 壁尻一局遊戲時長(秒)
	 */
	private final int GloryHoleGameTime;
	/**
	 * 壁尻消耗物品
	 */
	private final String GloryHoleCost;
	/**
	 * 壁尻消耗物品列表(對應使用次數,每次使用消耗數不一樣)
	 */
	private List<String> GloryHoleCostList;
	/**
	 * 壁尻最大付費遊玩次數
	 */
	private final int GloryHoleMaxCost;
	/**
	 * 壁尻道具
	 */
	private final String GloryHoleGameItem;
	 /**
	 * 壁尻道具列表
	 */
	private List<String> GloryHoleItemList;
	/**
	 * 壁尻最大道具槽
	 */
	private final int GloryHoleMaxUseItem;
	/**
	 * 壁尻每週幾開放
	 */
	private final String GloryHoleOpenDay;
	
	private List<Integer> GloryHoleOpenDayList;
	/**
	 * 信件最大限制
	 */
	private final int MaxMailLimit;
	/**
	 * 首抽必中物品
	 */
	private final String Firstgacha;
	/**
	*神秘商店商品數量
	*/
	private final String MysteryItemCount;
	
	private List<Integer> MysteryItemList;
	/**
	*每日商店商品數量
	*/
	private final String  DailyItemCount;
	
	private List<Integer> DailyItemList ;
	/**
	*種族商店商品數量
	*/
	private final String  RaceItemCount;
	
	private List<Integer> RaceItemList ;
	/**
	 * 競技商店商品數量
	 */
	private final String ArenaItemCount;
	
	private List<Integer> ArenaItemList ;
	/**
	 * 地下城每日免費攻略次數
	 */
	private final int dungeonDayFreeTimes;
	/**
	 * 地下城一鍵闖關VIP限制
	 */
	private final int dungeonOneKeyVIPLV;
	/**
	 * 地下城一鍵闖關扣除關卡
	 */
	private final int dungeonOneKeyDecStar;
	
	/**
	 * 競技場開始日期(用於推算季結算)
	 */
	private final String ArenaStartStr;
	private Date ArenaStartDate;
	/**
	 * 競技場季循環間隔日期
	 */
	private final int ArenaCycleDay;
	
	/**
	 * 失敗禮包觸發次數
	 */
	private final int FailedGiftCount;
	/**
	 * 秘密信條最大體力
	 */
	private final int SecretMaxPower;
	/**
	 * 秘密信條單次回體力量
	 */
	private final int SecretRecoverPower;
	/**
	 * 秘密信條回體力時間(秒)
	 */
	private final int SecretRecoverTime;
	/**
	 * 秘密信條單次消耗體力
	 */
	private final int SecretDecPower;
	/**
	 * 專武召喚單抽消費
	 */
	private final int CallEquipSingleCost;
	/**
	 * 專武召喚十抽消費
	 */
	private final int CallEquipTenCost;
	/**
	 * 專武召喚單抽道具消費
	 */
	private final String CallEquipItemCost; 
	/**
	 * 專武召喚十抽道具消費
	 */
	private final String CallEquipTenItemCost;
	/**
	 * 循環地下城活動消費道具
	 */
	private final String CycleStageItemCost; 
	/**
	 * 循環地下城活動代幣
	 */
	private final String CycleStageCoin;
	/**
	 * 循環地下城回復數量
	 */
	private final int CycleStageRecover;
	/**
	 * 循環地下城(復刻)活動消費道具
	 */
	private final String CycleStageItemCost2; 
	/**
	 * 循環地下城(復刻)活動代幣
	 */
	private final String CycleStageCoin2;
	/**
	 * 循環地下城(復刻)回復數量
	 */
	private final int CycleStageRecover2;
	
	/**
	 * 符文洗鍊石
	 */
	private final String RefineStone;
	/**
	 * 符文洗鍊石消費(隨鎖定孔量上升0-4)
	 */
	private final String RefineStoneCost;
	
	private List<Integer> RefineStoneList ;
	/**
	 * 符文特殊洗鍊石
	 */
	private final String RefineStoneMax;
	/**
	 * 符石密鎖
	 */
	private final String RefineLock ;
	
	private List<Integer> RefineLockList ;
	/**
	 * 符石密鎖消費數(隨鎖定孔量上升1-4)
	 */
	private final String RefineLockCost;
	/**
	 * 單人強敵活動挑戰次數
	 */
	private final int SingleBossChanllengeTime;
	/**
	 * 單人強敵活動動代幣
	 */
	private final String SingleBossCoin;
	/**
	 * 賽季爬塔樓層控制
	 */
	private final int SeasonTowerFloorContrl;
	/**
	 * 173.轉蛋抽池保底數
	 */
	private final int NewRoleGuarant;
	/**
	 * 获取全局静态对象
	 *
	 * @return
	 */
	public static SysBasicCfg getInstance() {
		return instance;
	}

	public SysBasicCfg() {
		instance = this;
		roleBaptizeLevel = 0;
		roleTrainOpenLevel = 0;
		bossChallengeItemId = 0;
		skillUpgradeLevel = 0;
		equipCompoumdLevel = 0;
		equipSpecialLevel = 0;
		sysVersion = "0.0.1";

		firstFastFightExp = 0;
		forgingBroadcastId = 0;
		everydayContribution = 2500;
		wingLeadGet = "";
		maxWingLevel = 50;
		profElementLevelLimit = 0;
		clearTalentPay = 0;
		rebirthTalentNum = 0;
		levelupTalentNum = 0;
		talentLevelLimit = 0;
		accountBoundOpen = false;
		accountBoundReward = 100;
		dailyGoldAward = 10000;
		foreverCardCanActivateNeedGold = 10000;
		activateForeverCardGold = 1000;
		rankGiftShowNum = 3;
		rankGiftPlayerNum = 50;
		giveRredEnvelopeAwards = "10000_1001_10";
		redEnvelopeGoldInterval = "50,100";
		giveRedEnvelopeAmount = 10;
		everyDayGrabRedEnvelope = 2;
		redEnvelopeExchangeRate = 100;
		multiEliteMaxTurn = 0;
		multiEliteCountDown = 0;
		multiEliteMaxRoomSize = 0;
		multiEliteDayFreeTimes = 0;
		multiEliteMaxCapacity = 0;
		buymultiElitePrice = null;
		raiderSpecialSingleTimes = new HashSet<Integer>();
		raiderSingleSpecialTimes = "";
		treasureRaiderSinglePrice = 20;
		treasureRaiderTenPrice = 180;
		snowfieldCellMaxSize = 12;
		snowfieldExchangeNeeds = "10000_1001_80,10000_1002_100000,30000_50001_2";
		snowfieldExchangeAwards = "10000_1001_80,10000_1002_100000,30000_50001_2";
		snowfieldBuyAddPhyc = 10;
		snowfieldContinuePeriod = 3600;
		snowfieldPhycRecoverPeriod = 60;
		snowfieldMaxRecoverPhyc = 15;
		taxiLimitLevel = 5;
		taxiCodeKey = "qmgj";
		friendInviteExchangeAwards = "10000_1001_100,30000_30001_1,30000_41001_3";
		friendInviteLevelLimit = 10;
		maxSkillLevel = 0;
		cmcStatus = 0;
		cmcObjInvalidHours = 72;
		cmcGetsTimeOut = 200;
		chatMsgMaxLen = 64;
		roleNameMaxLen = 16;
		chatElapse = 0;
		playerCacheTime = 600000;
		statisticsFightTime = null;
		battlefieldTime = null;
		fightMinCool = 3;
		fightMaxTurn = 30;
		arenaMaxTurn = 30;
		bossMaxTurn = 50;
		playerMinInitRank = 3001;
		pageDefenderQty = 3;
		freeChallengeTimes = 5;
		rankingListPlayerQty = 20;
		rankingAllianceQty = 20;
		arenaRewardTimeStr = "21:00:00";
		arenaRewardTime = 75600;
		maxPlayerSnapShotQty = 3000;
		playerSnapShotVersion = 1;
		fightDelayTime = 2;
		globalAwardRatio = 1.0f;
		initGold = 0;
		initCoin = 0;
		initVipLevel = 0;
		friendCountLimit = 0;
		applyFriendCountLimit = 0;
		kickAlliamceMemberExp = 0;
		equipCompoundItemId = 0;
		dailyNewSerGiftRewardLimit = 0;
		manualBattleCd = 0;
		emailDelDay = 3;
		allianceBossAddPorpNumber = 10;
		equipBagSize = 40;
		equipExtendTimes = 0;
		equipExtendSize = 0;
		equipExtendGoldCostStr = null;
		equipExtendGoldCostList = new LinkedList<Integer>();
		maxOfflineTime = 0;
		fastFightTime = 0;
		buyFastFightPrice = null;
		superEquipBaptizePrice = null;
		openSkillEnhanceCostItems = null;
		refreshAllianceShopPrice = null;
		buyBossFightPrice = null;
		buyEliteMapPrice = null;
		skillSlotLevelLimit = null;
		mercenarySkillSlotLevelLimit = null;
		equipPartGodlyAttr = null;
		equipPartGodlyAttr2 = null;
		cmHost = "";
		cmTimeout = 0;
		joinTeamBattleMinFight = 1500;
		teamBattleMaxPlayerQty = 10;
		teamBattleStartTimeStr = "14:00:00,20:00:00";
		teamBattleEndSwitchTime = 600;
		teamBattleRoundIntervalTime = 300;
		captainGoldRatio = 3000;
		memberGoldRatio = 1000;
		roundGoldRatio = 1000;
		captainExtraAward = "30000_11151_3";// 宝石袋
		extraAward = "30000_11151_5";// 能量核
		winTeamCaptainExtraAward = "30000_11151_10";// 高级宝石袋
		winTeamMemberExtraAward = "30000_11151_10";// 宝石袋
		arenaWinAward = "";
		arenaLoseAward = "";
		createTeamPrice = 50;
		msgMaxLength = 40;
		punchWeight = null;
		refashCostGoldNums = "";
		equipSwallowPartRatio = null;
		allianceReprotExp = 0;
		allianceReprotSysCoin = 0;
		equipBaptize = null;
		allianceCreateLevel = 0;
		allianceCreateVipLevel = 0;;
		allianceCreateGold = 0;
		allianceBossNumber = 0;
		specialCdks = "";
		giftInfos = "";
		broadcastWorldMsgRank = 3;
		maxArenaRankRecord = 20;
		maxMsgQty = 50;
		autoBossJoinVipLimit = 3;
		allianceAutoFightCostGold = 20;
		honorShopRefreshCostStr = null;
		crystalShopRefreshCostStr = null;
		maxShieldSize = 0;
		profRankRewardDays = 100000;
		profRankReawrdTimeStr = "00:00:00";
		profRankReawrdTime = -1;
		fastFightBuyPrice = new LinkedList<Integer>();
		superEquipBaptizePrices = new LinkedList<Integer>();
		refreshAllianceShopPrices = new LinkedList<Integer>();
		bossFightBuyPrice = new LinkedList<Integer>();
		multiEliteBuyPrice = new LinkedList<Integer>();
		eliteMapBuyPrice = new LinkedList<Integer>();
		skillSlotLevels = new LinkedList<String>();
		mercenarySkillSlotLevels = new LinkedList<Integer>();
		statisticsFightTimeCost = new LinkedList<Integer>();
		battlefieldTimeCost = new LinkedList<Integer>();
		equipPartGodlyAttrIdMap = new HashMap<Integer, Integer>();
		equipPartGodlyAttrIdMap2 = new HashMap<Integer, Integer>();
		equipSwallowPartRatioMap = new HashMap<>();
		teamBattleStartTime = new ArrayList<Integer>();
		equipBaptizeAttrAllocMap = new HashMap<>();
		specialCdkMap = new HashMap<String, String>();
		giftAwardItems = new ArrayList<>();
		honorShopRefreshCost = new LinkedList<>();
		crystalShopRefreshCost = new LinkedList<>();
		maxProfRankNum = 0;
		maxProfRankRewardNum = 0;
		maxProfRankShowNum = 0;
		lastCampWarWinStreakMaxRank = 10;
		curCampWarWinStreakMaxRank = 3;
		campWarMaxInspireTimes = 10;
		campWarInspirePrice = 20;
		campWarInspireBonuses = 10;
		campWarPrepareTimeStr = "19:50:00";
		campWarBattleStartTimeStr = "20:00:00";
		campWarBattleStopTimeStr = "20:30:00";
		campWarShowStopTimeStr = "20:40:00";
		campWarPrepareTime = 0;
		campWarBattleStartTime = 0;
		campWarBattleStopTime = 0;
		campWarShowStopTime = 0;
		campWarBattlePeriod = 30;
		campWarMinFightValue = 2000;
		campWarRoundWinGoldRatio = 30;
		campWarRoundWinReputation = 10;
		campWarWinnerAddScore = 3;
		campWarLoserAddScore = 1;
		campWarMaxWinStreakReport = 3;
		campWarMaxEndStreakReport = 2;
		autoCampWarVipLevel = 4;
		autoCampWarGold = 30;
		bannerActivityId = 0;
		equipCompoumdCostRep = 8000;
		winCampExtraReputation = 100;
		autoCampWarAwardRatio = 0.7;
		rechargeMinUnitGold = 1000;
		rebateMinUnitGold = 33;
		everydayRebateMaxGold = 330;
		quickBattleAddVitality = 20;
		worldBossAddVitality = 10;
		teamBattleAddVitality = 10;
		campWarAddVitality = 20;
		arenaWinAddVitality = 5;
		arenaFailAddVitality = 0;
		expeditionArmoryRankingSize = 50;
		expeditionArmorySysAddExp = 10000;
		expeditionArmoryAutoAddExpTime = 3600;
		expeditionArmoryAutoAddExpStage = 1;
		openExpeditionArmoryAutoAddExp = 1;
		crazyRouletteRechargeUnit = 100;
		crazyRouletteDayMaxTimes = 20;
		crazyRouletteAddCredits = 1;
		nLargessGoldofShare = 0;
		nLargessGoldNum = 150000;
		teamBattleKickMemberCostAdd = 10;
		teamBattleKickMemberCostMax = 100;
		evaluateRewardGold = 0;
		initMusicOn = 0;
		jpNewbieRewardMonthCard = 0;
		allianceBattleMaxTurn = 0;
		allianceBattleTime = 0;
		allianceBattleBuff = null;
		allianceBattleBuffMap = new HashMap<Integer, List<Integer>>();
		deleteInvalid = true;
		ipCacheEnable = true;
		fightActionTimeRatio = 0.0f;
		allowAllianceBattleOpenWeekDay = 0;
		appVersionNotice = "";
		csReward = false;
		ringLvlUpNeedStarLvl = 0;
		ringLvlUpTimes = 0;
		changeNameCost = 300;
		askTicketCount = 4;
		askTickAwards = "30000_990001_1";
		manuelRefreshDefaultCount = "";
		manuelRefreshPrize = "";
		sendEmailNum = 0;
		this.maxElementSize = 9;
		this.elementBagExtendCost = 100;
		this.elementBagExtendNum = 10;
		this.elementBagDefault = 40;
		this.elementSwallowExp = "";
		this.elementSwallowExpRatio = new HashMap<>();
		this.recastGoldCost = 10;
		globalServerOpen = false;
		yyExchangeRadio = 10;
		this.yyExchangeRankTopNum = 10;
		this.yyExchangeRankAwardLimit = 5000;
		heroTokenTaskLimit = 5;
		heroTokenTaskUpgrade = 2;
		rankGiftTickTime = 1000 * 60;
		platformBlackList = new ArrayList<String>();
		platformForeverCard = null;
		mergeServerRewards = null;
		mergePlayerLevel = 0;
		mergeTime = null;
		MysteryRefreshCost = "";
		MysteryRefreshList = new LinkedList<Integer>();
		MysteryFreeRefresh = 0;
		MysteryMaxRefresh = 0;
		MysteryRefreshTime = 0;
		faceBookShareRewards = null;
		refreshEquipOffsetLevel = null;
		refreshEquipWeight = null;
		refreshEquipOffsetLevelList = new LinkedList<Integer>();
		refreshEquipWeightList = new LinkedList<Integer>();
		allianceListVitality = 0;
		hourCardUseCount = 0;

		equipSmeltNum = 1000;

		playerBindRewards = null;

		expeditionTaskRefreshCostStr = null;

		expeditionTaskRefreshCost = new LinkedList<>();

		applyAddAllianceMaxCount = 5;
		refreshApplyAddAllianceTime = 24;
		vitalityNoChangeDayTime = 3;

		// 打靶
		shootRefreshTime = 0;
		shootCloseTime = 0;
		// 聊天
		chat_msg_chunk = 0;
		// 可申请加入公会数量最大上限值
		applyAddAllianceMaxVal = 3;
		// ios专属礼包
		ios_git_reward = null;
		// 成长基金购买vip等级限制
		growthVipLevel = 0;
		// 成长基金购买所需元宝
		growthNeedGold = 0;
		openFightingBoxStr = null;
		openFightingLevelMap = new HashMap<Integer, Integer>();
		openAssistanceBoxStr = null;
		openAssistanceLevelMap = new HashMap<Integer, Integer>();

		newTreasureRaiderLimitTimesStr = null;
		newTreasureRaiderLimitTimes = new TreeSet<Integer>();

		releaseURLimitTimesStr = null;
		releaseURLimitTimes = new TreeSet<Integer>();

		goldFlowerRatio = 0;
		freeInteractTimes = 0;
		freeRefreshTimes = 0;

		newURLimitTimesStr = null;
		newURLimitTimes = new TreeSet<Integer>();

		changeNameCard = null;
		checkCurVipLevel = 0;
		decrPVESearchTime = "";
		decrPVESearchTimeMap = new HashMap<Integer, Integer>();
		chatSameMsgInterval = 0;
		honorShopShowCount = 6;
		allianceShopShowCount = 6;
		dailyChargeRebateEnable = 0;
		dailyChargeRebateTime = "12:00";
		cdkUniteCode = "";
		releaseUrResetCost = 0;
		releaseUrLotteryCost = 0;
		releaseUrGiveLucky = 0;
		giftExchangeSwitch = 0;
		screenedMailIdList = new ArrayList<Integer>();
		screenedMailId = "";
		releaseUrCostLucky2 = 0;

		releaseUrGiveLucky2 = 0;

		releaseUrResetCost3 = 0;
		releaseUrLotteryCost3 = 0;
		releaseUrGiveLucky3 = 0;

		activeComplianceCycleDays = 0;
		activeCompliancePoint = 0;
		activeCanAwardDays = 0;
		activity123UrCostLucky = 0;
		activity123UrGiveLucky = 0;
		shopGemBuyMaxOnce = 0;
		autoHighSpeedBreedOpenVip = 0;
		activity127UrGiveLucky = 0;
		activity128UrGiveLucky = 0;
		activity128UrRankMaxNum = 0;
		activity128UrRankCalcTime = "10:00";
		act123EvenDotsRate = 0;
		act116EvenDotsRate = 0;
		activity132OldUserTime = "";
		activity132OldUserLevel = 0;
		eighteenPrincesMaxLayerNum = 0;
		eighteenPrincesOpenLevel = 0;
		this.eighteenMaxUseTimes = 0;
		this.eighteenFreeUseTimes = 0;
		this.eighteenUseSpend = 0;
		activity137OpenLoginCount = "";
		activity137OpenTime = 0;
		badgeBagSize = 10;
		badgeExtendTimes = 0;
		badgeExtendSize = 0;
		badgeExtendGoldCostStr = null;
		badgeExtendGoldCostList = new LinkedList<>();
		eighteenPrincesOpenServerDays = 0;
		activity140Continuetime = 0;
		activity140LotteryCost = 0;
		activity140OpenLoginCount="";
		buyAlbumItemId = 0;
		buyAlbumItemCountStr = null;
		buyAlbumItemCountList = new LinkedList<>();
		useItemMaxcount = 10;
		RichManUseItem = null;
		RichManStpeTypeStr = null;
		RichManStpeTypeList = new LinkedList<>();
		PirateUseItem = null;
		AdaptationType = 0;
		BattleCheck = 1;
		EquipLimitLevel = 15;
		ChosenOneSingleCost = 0;
		ChosenOneTenCost = 0;
		ChosenOneGuarant = 0;
		WishingCostItem = null;
		WishingCostList = new ArrayList<String>();
		WishingIncLucky = 5;
		WishingRefreshTime = 10800;
		WishingFreeDraw = 3;
		WishingRefreshCost = null;
		MarryCostItem = "10000_1001_1";
		MarryCostStr = "";
		MarryCostCountList = new ArrayList<Integer>();
		NewbieDays = 10;
		FriendshipPoint = 10;
		HeroDramaGift = "10000_1001_100";
		ResetHero = "10000_1001_50";
		ResetHeroCostItem ="";
		SpriteSingleCost = "10000_1001_220";
		SpriteTenCost = "10000_1001_2000";
		LoginTenDrawAward = "30000_106102_10";
		LoginTenDrawDays = 100;
		RaceSummon = "30000_106102_1";
		RacePoint = 20;
		SupportCalendarCost = null;
		SupportCalendarCostList = new ArrayList<>();
		activity165RankMaxNum = 0;
		GoldMineCost = "10000_1001_1";
		ReSetGuildSoulItem = null;
		ResetEquip = "10000_1001_1000";
		BuyCoinCriRate = 0;
		CallOfFriendshipSingleCost = 0;
		CallOfFriendshipTenCost = 0;
		ChosenOneItemCost = "30000_6004_1";
		ChosenOneTenItemCost = "30000_6004_10";
		// PickUp單抽道具消費
		PickUpItemCost = "";
		// PickUp十抽道具消費 
		PickUpTenItemCost ="";
	
		
		HeroEXPItem="";
		HeroStoneItem ="";
		ChargeGuaranteed ="";
		RechargeLuckyList = new ArrayList<>(); 
		RechargeLuckyCD =0;
		CallOfRaceAward="";
		activity175GameTime = "";
		act175GameTimeList = new ArrayList<>();
		
		activity175TeamAward = "";
		act175TeamAwardList = new ArrayList<>();
		
		GloryHoleFree = 0;
		
		GloryHoleGameTime = 0;
		
		GloryHoleCost = "";
		GloryHoleCostList = new ArrayList<>();
		
		GloryHoleMaxCost = 0;
		
		GloryHoleGameItem = "";
		GloryHoleItemList = new ArrayList<>();
		
		GloryHoleMaxUseItem = 0;
		
		
		// 壁尻每週幾開放
		GloryHoleOpenDay = "";
		GloryHoleOpenDayList = new ArrayList<>();
		
		MaxMailLimit = 0;
		Firstgacha = "";
		
		//神秘商店商品數量
		MysteryItemCount = null;
		MysteryItemList = new ArrayList<>();
		//每日商店商品數量
		DailyItemCount = null;
		DailyItemList = new ArrayList<>();
		//每日商店商品數量
		RaceItemCount = null;
		RaceItemList = new ArrayList<>();
		
		//競技商店商品數量
		ArenaItemCount = null;
		ArenaItemList = new ArrayList<>();
		
		//地下城每日免費攻略次數
		dungeonDayFreeTimes = 0;
		//地下城一鍵闖關VIPLV
		dungeonOneKeyVIPLV = 0;
		//地下城一鍵闖關扣除關卡
		dungeonOneKeyDecStar = 0;
		//競技場開始計算日期
		ArenaStartStr = "";
		ArenaStartDate = null;
		//競技場季循環天數
		ArenaCycleDay = 0;
		// 失敗禮包最大計數次
		FailedGiftCount = 0;
		//秘密信條最大體力
		SecretMaxPower = 0;
		//秘密信條單次回體力量
		SecretRecoverPower = 0;
		//秘密信條回體力時間(秒)
		SecretRecoverTime = 0;
		//秘密信條單次消耗體力
		SecretDecPower = 0;
		// 專武召喚單抽消費
		CallEquipSingleCost = 0;
		// 專武召喚十抽消費
		CallEquipTenCost = 0;
		// 專武召喚單抽道具消費
		CallEquipItemCost = "";
		// 專武召喚十抽道具消費
		CallEquipTenItemCost = "";
		// 循環地下城活動消費道具
		CycleStageItemCost = "";
		// 循環地下城活動代幣
		CycleStageCoin = "";
		// 循環地下城回復道具限制
		CycleStageRecover = 0;
		
		// 循環地下城(復刻)活動消費道具
		CycleStageItemCost2 = "";
		// 循環地下城(復刻)活動代幣
		CycleStageCoin2 = "";
		// 循環地下城(復刻)回復道具限制
		CycleStageRecover2 = 0;
		
		//符文洗鍊石
		RefineStone = "";
		//符文洗鍊石消費(隨鎖定孔量上升0-4)
		RefineStoneCost = "";
		RefineStoneList = new ArrayList<>();
		//符文特殊洗鍊石
		RefineStoneMax = "";
		//符石密鎖
		RefineLock = "";
		//符石密鎖消費數(隨鎖定孔量上升1-4)
		RefineLockCost = "";
		RefineLockList = new ArrayList<>();
		// 單人強敵活動挑戰次數
		SingleBossChanllengeTime = 0;
		// 單人強敵活動動代幣 
		SingleBossCoin = "";
		// 賽季爬塔初始樓層控制
		SeasonTowerFloorContrl = 0;
		// 173.轉蛋抽池保底數
		NewRoleGuarant = 0;
	}

	public int getEverydayContribution() {
		return everydayContribution;
	}

	public String getPlatformForeverCard() {
		return platformForeverCard;
	}

	public String getWingLeadGet() {
		return wingLeadGet;
	}

	public String getChangeNameCard() {
		return changeNameCard;
	}

	public int getHeroTokenTaskLimit() {
		return heroTokenTaskLimit;
	}

	public int getEquipSmeltNum() {
		return equipSmeltNum;
	}

	public int getHourCardUseMaxCount() {
		return hourCardUseCount;
	}

	public int getHeroTokenTaskUpgrade() {
		return heroTokenTaskUpgrade;
	}

	public int getYyExchangeRankAwardLimit() {
		return yyExchangeRankAwardLimit;
	}

	public boolean isGlobalServerOpen() {
		return globalServerOpen;
	}

	public int getYyExchangeRankTopNum() {
		return yyExchangeRankTopNum;
	}

	public int getRankGiftShowNum() {
		return rankGiftShowNum;
	}

	public int getRankGiftPlayerNum() {
		return rankGiftPlayerNum;
	}

	public String getGiveRredEnvelopeAwards() {
		return giveRredEnvelopeAwards;
	}

	public String getRedEnvelopeGoldInterval() {
		return redEnvelopeGoldInterval;
	}

	public String getGiveRedEnvelopeTxtFmt() {
		return "@giveRedEnvelopeTxtFmt";
	}

	public int getGiveRedEnvelopeAmount() {
		return giveRedEnvelopeAmount;
	}

	public int getEveryDayGrabRedEnvelope() {
		return everyDayGrabRedEnvelope;
	}

	public int getRedEnvelopeExchangeRate() {
		return redEnvelopeExchangeRate;
	}

	public int getJpNewbieRewardMonthCard() {
		return jpNewbieRewardMonthCard;
	}

	public int getMultiEliteMaxTurn() {
		return multiEliteMaxTurn;
	}

	public int getMultiEliteCountDown() {
		return multiEliteCountDown;
	}

	public int getMultiEliteMaxRoomSize() {
		return multiEliteMaxRoomSize;
	}

	public int getMultiEliteDayFreeTimes() {
		return multiEliteDayFreeTimes;
	}

	public int getMultiEliteMaxCapacity() {
		return multiEliteMaxCapacity;
	}

	public Set<Integer> getRaiderSpecialSingleTimes() {
		return raiderSpecialSingleTimes;
	}

	public String getRaiderSingleSpecialTimes() {
		return raiderSingleSpecialTimes;
	}

	public int getTreasureRaiderSinglePrice() {
		return treasureRaiderSinglePrice;
	}

	public int getTreasureRaiderTenPrice() {
		return treasureRaiderTenPrice;
	}

	public int getSnowfieldCellMaxSize() {
		return snowfieldCellMaxSize;
	}

	public int getSnowfieldBuyAddPhyc() {
		return snowfieldBuyAddPhyc;
	}

	public int getSnowfieldContinuePeriod() {
		return snowfieldContinuePeriod;
	}

	public int getSnowfieldPhycRecoverPeriod() {
		return snowfieldPhycRecoverPeriod;
	}

	public int getSnowfieldMaxRecoverPhyc() {
		return snowfieldMaxRecoverPhyc;
	}

	public String getSnowfieldExchangeNeeds() {
		return snowfieldExchangeNeeds;
	}

	public String getSnowfieldExchangeAwards() {
		return snowfieldExchangeAwards;
	}

	public String getTaxiCodeKey() {
		return taxiCodeKey;
	}

	public int getTaxiLimitLevel() {
		return taxiLimitLevel;
	}

	public int getMaxSkillLevel() {
		return maxSkillLevel;
	}

	public String getFriendInviteExchangeAwards() {
		return friendInviteExchangeAwards;
	}

	public int getFriendInviteLevelLimit() {
		return friendInviteLevelLimit;
	}

	public int getCmcStatus() {
		return cmcStatus;
	}

	public int getCmcObjInvalidHours() {
		return cmcObjInvalidHours;
	}

	public int getCmcGetsTimeOut() {
		return cmcGetsTimeOut;
	}

	public int getChatMsgMaxLen() {
		return chatMsgMaxLen;
	}

	public int getRoleNameMaxLen() {
		return roleNameMaxLen;
	}

	public int getChatElapse() {
		return chatElapse * 1000;
	}

	public int getPlayerCacheTime() {
		return playerCacheTime;
	}

	public int getFightMinCool() {
		return fightMinCool;
	}

	public int getFightDelayTime() {
		return fightDelayTime;
	}

	public int getFightMaxTurn() {
		return fightMaxTurn;
	}

	public int getArenaMaxTurn() {
		return arenaMaxTurn;
	}

	public int getBossMaxTurn() {
		return bossMaxTurn;
	}

	public int getPlayerMinInitRank() {
		return playerMinInitRank;
	}

	public int getPageDefenderQty() {
		return pageDefenderQty;
	}

	public int getFreeChallengeTimes() {
		return freeChallengeTimes;
	}

	public int getRankingListPlayerQty() {
		return rankingListPlayerQty;
	}

	public int getRankingAllianceQty() {
		return rankingAllianceQty;
	}

	public int getMaxPlayerSnapShotQty() {
		return maxPlayerSnapShotQty;
	}

	public int getPlayerSnapShotVersion() {
		return playerSnapShotVersion;
	}

	public int getAllianceBossAddPorpNumber() {
		return allianceBossAddPorpNumber;
	}

	public int getArenaRewardTime() {
		return arenaRewardTime;
	}

	public int getEquipBagSize() {
		return equipBagSize;
	}

	public int getMaxOfflineTime() {
		return maxOfflineTime;
	}

	public int getFastFightTime() {
		return fastFightTime;
	}

	public int getEquipExtendTimes() {
		return equipExtendTimes;
	}

	public int getEquipExtendSize() {
		return equipExtendSize;
	}
	
	public float getGlobalAwardRatio() {
		return globalAwardRatio;
	}

	public int getEquipExtendGoldCost(int times) {
		if (times >= equipExtendGoldCostList.size()) {
			return equipExtendGoldCostList.get(equipExtendGoldCostList.size() - 1);
		}

		return equipExtendGoldCostList.get(times);
	}

	public List<String> getSkillSlotLevels() {
		return skillSlotLevels;
	}

	public List<Integer> getMercenarySkillSlotLevels() {
		return mercenarySkillSlotLevels;
	}

	public String getSpecialCdkInfo(String cdk) {
		return specialCdkMap.get(cdk);
	}

	public int getBuyFastFightPrice(int hasBuyTimes) {
		if (hasBuyTimes >= fastFightBuyPrice.size()) {
			return fastFightBuyPrice.get(fastFightBuyPrice.size() - 1);
		}
		return fastFightBuyPrice.get(hasBuyTimes);
	}

	public int getBuyBossFightPrice(int hasBuyTimes) {
		if (hasBuyTimes >= bossFightBuyPrice.size()) {
			return bossFightBuyPrice.get(bossFightBuyPrice.size() - 1);
		}
		return bossFightBuyPrice.get(hasBuyTimes);
	}
	
	public int getBuymultiElitePrice(int hasBuyTimes) {
		if (hasBuyTimes >= multiEliteBuyPrice.size()) {
			return multiEliteBuyPrice.get(multiEliteBuyPrice.size() - 1);
		}
		return multiEliteBuyPrice.get(hasBuyTimes);
	}

	public List<Integer> getRefreshEquipOffsetLevelList() {
		return refreshEquipOffsetLevelList;
	}

	public List<Integer> getRefreshEquipWeightList() {
		return refreshEquipWeightList;
	}

	public int getBuyEliteMapPrice(int hasBuyTimes) {
		if (hasBuyTimes >= eliteMapBuyPrice.size()) {
			return eliteMapBuyPrice.get(eliteMapBuyPrice.size() - 1);
		}
		return eliteMapBuyPrice.get(hasBuyTimes);
	}

	public int getStatisticsFightTime(int enemyCount) {
		if (enemyCount > statisticsFightTimeCost.size()) {
			return statisticsFightTimeCost.get(battlefieldTimeCost.size() - 1);
		}
		return statisticsFightTimeCost.get(enemyCount - 1);
	}

	public int getBattlefieldTime(int enemyCount) {
		if (enemyCount > battlefieldTimeCost.size()) {
			return battlefieldTimeCost.get(battlefieldTimeCost.size() - 1);
		}
		return battlefieldTimeCost.get(enemyCount - 1);
	}

	public int getJoinTeamBattleMinFight() {
		return joinTeamBattleMinFight;
	}

	public int[] getRefreshCostGoldNums() {
		return refreshCostGoldNums;
	}

	public int getTeamBattleMaxPlayerQty() {
		return teamBattleMaxPlayerQty;
	}

	public List<Integer> getTeamBattleStartTime() {
		return teamBattleStartTime;
	}

	public int getTeamBattleEndSwitchTime() {
		return teamBattleEndSwitchTime;
	}

	public int getTeamBattleRoundIntervalTime() {
		return teamBattleRoundIntervalTime;
	}

	public int getCreateTeamPrice() {
		return createTeamPrice;
	}

	public int getCaptainGoldRatio() {
		return captainGoldRatio;
	}

	public int getMemberGoldRatio() {
		return memberGoldRatio;
	}

	public int getRoundGoldRatio() {
		return roundGoldRatio;
	}

	public String getCaptainExtraAward() {
		return captainExtraAward;
	}

	public String getExtraAward() {
		return extraAward;
	}

	public String getWinTeamCaptainExtraAward() {
		return winTeamCaptainExtraAward;
	}

	public String getWinTeamMemberExtraAward() {
		return winTeamMemberExtraAward;
	}

	public int getMsgMaxLength() {
		return msgMaxLength;
	}

	public String getEquipBaptize() {
		return equipBaptize;
	}

	public int getInitGold() {
		return initGold;
	}

	public int getInitCoin() {
		return initCoin;
	}

	public int getInitVipLevel() {
		return initVipLevel;
	}

	public int getAllianceBossNumber() {
		return allianceBossNumber;
	}

	public int getEmailDelDay() {
		return emailDelDay;
	}

	public int getAskTicketCount() {
		return askTicketCount;
	}

	public String getAskTickAwards() {
		return askTickAwards;
	}

	@Override
	protected boolean checkValid() {
		return true;
	}

	public int getApplyAddAllianceMaxVal() {
		return applyAddAllianceMaxVal;
	}

	public Set<Integer> getNewTreasureRaiderLimitTimes() {
		return newTreasureRaiderLimitTimes;
	}

	public Set<Integer> getReleaseURLimitTimes() {
		return releaseURLimitTimes;
	}

	public void setReleaseURLimitTimes(Set<Integer> releaseURLimitTimes) {
		this.releaseURLimitTimes = releaseURLimitTimes;
	}

	public int getGoldFlowerRatio() {
		return goldFlowerRatio;
	}

	public int getFreeInteractTimes() {
		return freeInteractTimes;
	}

	public int getFreeRefreshTimes() {
		return freeRefreshTimes;
	}

	public Set<Integer> getNewURLimitTimes() {
		return newURLimitTimes;
	}

	public void setNewURLimitTimes(Set<Integer> newURLimitTimes) {
		this.newURLimitTimes = newURLimitTimes;
	}

	public String getNewURLimitTimesStr() {
		return newURLimitTimesStr;
	}

	@Override
	protected boolean assemble() {

		fastFightBuyPrice.clear();
		superEquipBaptizePrices.clear();
		refreshAllianceShopPrices.clear();
		bossFightBuyPrice.clear();
		multiEliteBuyPrice.clear();
		eliteMapBuyPrice.clear();
		skillSlotLevels.clear();
		mercenarySkillSlotLevels.clear();
		statisticsFightTimeCost.clear();
		battlefieldTimeCost.clear();
		specialCdkMap.clear();
		teamBattleStartTime.clear();
		equipBaptizeAttrAllocMap.clear();
		equipSwallowPartRatioMap.clear();
		equipPartGodlyAttrIdMap.clear();
		equipPartGodlyAttrIdMap2.clear();
		giftAwardItems.clear();
		raiderSpecialSingleTimes.clear();
		refreshEquipOffsetLevelList.clear();
		refreshEquipWeightList.clear();
		newTreasureRaiderLimitTimes.clear();
		releaseURLimitTimes.clear();
		newURLimitTimes.clear();
		act175GameTimeList.clear();
		act175TeamAwardList.clear();
		GloryHoleItemList.clear();
		DailyItemList.clear();
		RaceItemList.clear();
		ArenaItemList.clear();
		GloryHoleOpenDayList.clear();

		if (superEquipBaptizePrice != null && superEquipBaptizePrice.length() > 0) {
			String[] items = superEquipBaptizePrice.split(",");
			for (String item : items) {
				superEquipBaptizePrices.add(Integer.valueOf(item.trim()));
			}
		}

		if (refreshAllianceShopPrice != null && refreshAllianceShopPrice.length() > 0) {
			String[] items = refreshAllianceShopPrice.split(",");
			for (String item : items) {
				refreshAllianceShopPrices.add(Integer.valueOf(item.trim()));
			}
		}

		if (buyFastFightPrice != null && buyFastFightPrice.length() > 0) {
			String[] items = buyFastFightPrice.split(",");
			for (String item : items) {
				fastFightBuyPrice.add(Integer.valueOf(item.trim()));
			}
		}

		if (equipExtendGoldCostStr != null && equipExtendGoldCostStr.length() > 0) {
			String[] items = equipExtendGoldCostStr.split(",");
			for (String item : items) {
				equipExtendGoldCostList.add(Integer.valueOf(item.trim()));
			}
		}

		if (buyBossFightPrice != null && buyBossFightPrice.length() > 0) {
			String[] items = buyBossFightPrice.split(",");
			for (String item : items) {
				bossFightBuyPrice.add(Integer.valueOf(item.trim()));
			}
		}
		
		if (buymultiElitePrice != null && buymultiElitePrice.length() > 0) {
			String[] items = buymultiElitePrice.split(",");
			for (String item : items) {
				multiEliteBuyPrice.add(Integer.valueOf(item.trim()));
			}
		}

		if (buyEliteMapPrice != null && buyEliteMapPrice.length() > 0) {
			String[] items = buyEliteMapPrice.split(",");
			for (String item : items) {
				eliteMapBuyPrice.add(Integer.valueOf(item.trim()));
			}
		}

		if (refashCostGoldNums != null && refashCostGoldNums.length() > 0) {
			String[] golds = refashCostGoldNums.split(",");
			refreshCostGoldNums = new int[golds.length];
			for (int i = 0; i < golds.length; i++) {
				refreshCostGoldNums[i] = Integer.parseInt(golds[i]);
			}
		}

		if (manuelRefreshDefaultCount != null) {
			if (manuelRefreshPrize != null && manuelRefreshPrize.length() > 0) {
				String[] golds = manuelRefreshPrize.split(",");
				int refreshNum = Integer.parseInt(manuelRefreshDefaultCount);
				refreshMysteryCostGoldNums = new int[refreshNum];
				for (int i = 0; i < refreshNum; i++) {
					refreshMysteryCostGoldNums[i] = Integer.parseInt(golds[i]);
				}
			}
		}

		if (refreshEquipOffsetLevel != null && refreshEquipOffsetLevel.length() > 0) {
			String[] items = refreshEquipOffsetLevel.split(",");
			for (String item : items) {
				refreshEquipOffsetLevelList.add(Integer.valueOf(item.trim()));
			}
		}

		if (refreshEquipWeight != null && refreshEquipWeight.length() > 0) {
			String[] items = refreshEquipWeight.split(",");
			for (String item : items) {
				refreshEquipWeightList.add(Integer.valueOf(item.trim()));
			}
		}

		if (skillSlotLevelLimit != null && skillSlotLevelLimit.length() > 0) {
			String[] items = skillSlotLevelLimit.split(",");
			for (String item : items) {
				skillSlotLevels.add(item.trim());
			}
		}

		if (mercenarySkillSlotLevelLimit != null && mercenarySkillSlotLevelLimit.length() > 0) {
			String[] items = mercenarySkillSlotLevelLimit.split(",");
			for (String item : items) {
				mercenarySkillSlotLevels.add(Integer.valueOf(item.trim()));
			}
		}

		if (statisticsFightTime != null && statisticsFightTime.length() > 0) {
			String[] items = statisticsFightTime.split(",");
			for (String item : items) {
				statisticsFightTimeCost.add(Integer.valueOf(item.trim()));
			}
		}

		if (battlefieldTime != null && battlefieldTime.length() > 0) {
			String[] items = battlefieldTime.split(",");
			for (String item : items) {
				battlefieldTimeCost.add(Integer.valueOf(item.trim()));
			}
		}

		if (specialCdks != null && specialCdks.length() > 0) {
			String[] items = specialCdks.split(";");
			for (String item : items) {
				String[] kv = item.split(":");
				if (kv.length != 2) {
					return false;
				}
				specialCdkMap.put(kv[0].trim().toLowerCase(), kv[1].trim());
			}
		}

		if (equipPartGodlyAttr != null) {
			String[] ss = equipPartGodlyAttr.split(",");
			for (String s : ss) {
				String[] sv = s.split("_");
				equipPartGodlyAttrIdMap.put(Integer.valueOf(sv[0]), Integer.valueOf(sv[1]));
			}
		}

		if (equipPartGodlyAttr2 != null) {
			String[] ss = equipPartGodlyAttr2.split(",");
			for (String s : ss) {
				String[] sv = s.split("_");
				equipPartGodlyAttrIdMap2.put(Integer.valueOf(sv[0]), Integer.valueOf(sv[1]));
			}
		}

		if (equipSwallowPartRatio != null) {
			String[] ss = equipSwallowPartRatio.split(",");
			for (String s : ss) {
				String[] sv = s.split("_");
				equipSwallowPartRatioMap.put(Integer.valueOf(sv[0]), Integer.valueOf(sv[1]));
			}
		}

		if (punchWeight != null && punchWeight.length() > 0) {
			punchWeightItems = WeightUtil.convertToList(punchWeight);
		}

		if (equipBaptize != null) {
			String[] ss = equipBaptize.split("\\|");
			int index = 0;
			for (String s : ss) {
				equipBaptizeAttrAllocMap.put(++index, WeightUtil.convertToList(s));
			}
		}

		if (arenaRewardTimeStr != null && arenaRewardTimeStr.trim().length() > 0) {
			String[] timeStrs = arenaRewardTimeStr.trim().split(":");
			if (timeStrs.length != 3) {
				throw new RuntimeException("sysCfg arenaRewardTimeStr is Error.");
			}
			arenaRewardTime = timeStrToInt(timeStrs);
		}

		if (teamBattleStartTimeStr != null && teamBattleStartTimeStr.trim().length() > 0) {
			String[] strs = teamBattleStartTimeStr.trim().split(",");
			for (String str : strs) {
				String[] timeStrs = str.trim().split(":");
				if (timeStrs.length != 3) {
					throw new RuntimeException("sysCfg teamBattleStartTimeStr is Error.");
				}
				int startTime = timeStrToInt(timeStrs);
				teamBattleStartTime.add(startTime);
			}
		}

		if (giftInfos != null && giftInfos.length() > 0) {
			String[] items = giftInfos.split(",");
			for (String item : items) {
				ItemInfo itemInfo = ItemInfo.valueOf(item);
				if (itemInfo != null) {
					giftAwardItems.add(itemInfo);
				}
			}
		}

		if (honorShopRefreshCostStr != null && honorShopRefreshCostStr.length() > 0) {
			String[] hsrcs = honorShopRefreshCostStr.split(",");
			for (String hsrc : hsrcs) {
				honorShopRefreshCost.add(Integer.valueOf(hsrc));
			}
		}

		if (crystalShopRefreshCostStr != null && crystalShopRefreshCostStr.length() > 0) {
			String[] hsrcs = crystalShopRefreshCostStr.split(",");
			for (String hsrc : hsrcs) {
				crystalShopRefreshCost.add(Integer.valueOf(hsrc));
			}
		}

		if (expeditionTaskRefreshCostStr != null && expeditionTaskRefreshCostStr.length() > 0) {
			String[] hsrcs = expeditionTaskRefreshCostStr.split(",");
			for (String hsrc : hsrcs) {
				expeditionTaskRefreshCost.add(Integer.valueOf(hsrc));
			}
		}

		if (profRankReawrdTimeStr != null && profRankReawrdTimeStr.trim().length() > 0) {
			String[] timeStrs = profRankReawrdTimeStr.trim().split(":");
			if (timeStrs.length != 3) {
				throw new RuntimeException("sysCfg profRankReawrdTimeStr is Error.");
			}
			profRankReawrdTime = timeStrToInt(timeStrs);
		}

		if (campWarPrepareTimeStr != null && campWarPrepareTimeStr.trim().length() > 0) {
			String[] timeStrs = campWarPrepareTimeStr.trim().split(":");
			if (timeStrs.length != 3) {
				throw new RuntimeException("sysCfg campWarPrepareTimeStr is Error.");
			}
			campWarPrepareTime = timeStrToInt(timeStrs);
		}

		if (campWarBattleStartTimeStr != null && campWarBattleStartTimeStr.trim().length() > 0) {
			String[] timeStrs = campWarBattleStartTimeStr.trim().split(":");
			if (timeStrs.length != 3) {
				throw new RuntimeException("sysCfg arenaRewardTimeStr is Error.");
			}
			campWarBattleStartTime = timeStrToInt(timeStrs);
		}

		if (campWarBattleStopTimeStr != null && campWarBattleStopTimeStr.trim().length() > 0) {
			String[] timeStrs = campWarBattleStopTimeStr.trim().split(":");
			if (timeStrs.length != 3) {
				throw new RuntimeException("sysCfg arenaRewardTimeStr is Error.");
			}
			campWarBattleStopTime = timeStrToInt(timeStrs);
		}

		if (campWarShowStopTimeStr != null && campWarShowStopTimeStr.trim().length() > 0) {
			String[] timeStrs = campWarShowStopTimeStr.trim().split(":");
			if (timeStrs.length != 3) {
				throw new RuntimeException("sysCfg arenaRewardTimeStr is Error.");
			}
			campWarShowStopTime = timeStrToInt(timeStrs);
		}

		if (allianceBattleBuff != null && !"".equals(allianceBattleBuff)) {
			String[] allianceBattleStrs = allianceBattleBuff.split(",");
			int index = 0;
			for (String allianceBattleStr : allianceBattleStrs) {
				String[] allianceBattles = allianceBattleStr.split("_");
				List<Integer> list = new ArrayList<>(2);
				list.add(Integer.valueOf(allianceBattles[0]));
				list.add(Integer.valueOf(allianceBattles[1]));

				this.allianceBattleBuffMap.put(++index, list);
			}
		}

		if (sysVersion != null) {
			String[] sysVers = sysVersion.split("\\.");
			if (sysVers.length == 3) {
				try {
					sysVersionId = Integer.valueOf(sysVers[2]);
				} catch (Exception e) {
					MyException.catchException(e);
				}
			}
		}

		if (!raiderSingleSpecialTimes.equals("")) {
			for (String specialTimes : raiderSingleSpecialTimes.split(",")) {
				raiderSpecialSingleTimes.add(Integer.valueOf(specialTimes));
			}
		}

		if (this.elementSwallowExp != null && this.elementSwallowExp.length() > 0) {
			String[] ss = this.elementSwallowExp.split(",");
			for (String s : ss) {
				String[] vs = s.split("_");
				if (vs.length == 2) {
					this.elementSwallowExpRatio.put(Integer.valueOf(vs[0]), Float.valueOf(vs[1]));
				} else {
					throw new RuntimeException("Element Level Exp Ratio Config Error");
				}
			}
		}

		if (this.openFightingBoxStr != null && this.openFightingBoxStr.length() > 0) {

			String[] openList = this.openFightingBoxStr.split(",");

			for (String hsrc : openList) {

				String[] openList2 = hsrc.split(":");

				if (openList2.length != 2) {
					throw new RuntimeException("openFightingBoxStr Config Error");
				}

				this.openFightingLevelMap.put(Integer.valueOf(openList2[0]), Integer.valueOf(openList2[1]));

			}
		}

		if (this.openAssistanceBoxStr != null && this.openAssistanceBoxStr.length() > 0) {
			String[] openList = this.openAssistanceBoxStr.split(",");

			for (String item : openList) {
				String[] subOpenList = item.split(":");
				if (subOpenList.length != 2) {
					throw new RuntimeException("openAssistanceBoxStr Config Error");
				}
				this.openAssistanceLevelMap.put(Integer.valueOf(subOpenList[0]), Integer.valueOf(subOpenList[1]));
			}
		}

		if (this.newTreasureRaiderLimitTimesStr != null && newTreasureRaiderLimitTimesStr.trim().length() > 0) {
			String[] timesArray = this.newTreasureRaiderLimitTimesStr.split(",");
			for (String times : timesArray) {
				this.newTreasureRaiderLimitTimes.add(Integer.parseInt(times));
			}
		}

		if (this.releaseURLimitTimesStr != null && releaseURLimitTimesStr.trim().length() > 0) {
			String[] timesArray = this.releaseURLimitTimesStr.split(",");
			for (String times : timesArray) {
				this.releaseURLimitTimes.add(Integer.parseInt(times));
			}
		}

		if (this.newURLimitTimesStr != null && newURLimitTimesStr.trim().length() > 0) {
			String[] timesArray = this.newURLimitTimesStr.split(",");
			for (String times : timesArray) {
				this.newURLimitTimes.add(Integer.parseInt(times));
			}
		}
		if (this.decrPVESearchTime != null && decrPVESearchTime.trim().length() > 0) {
			if (decrPVESearchTime.contains("_")) {
				String[] timesArray = this.decrPVESearchTime.split(",");
				for (String times : timesArray) {
					String[] array = times.split("_");
					this.decrPVESearchTimeMap.put(Integer.valueOf(array[0]), Integer.valueOf(array[1]));
				}
			}
		}

		if (this.screenedMailId != null && screenedMailId.trim().length() > 0) {

			String[] mailIdsArray = this.screenedMailId.split(",");
			for (String mailId : mailIdsArray) {
				this.screenedMailIdList.add(Integer.valueOf(mailId));
			}
		}

		if (superEquipBaptizePrice != null && superEquipBaptizePrice.length() > 0) {
			String[] items = superEquipBaptizePrice.split(",");
			for (String item : items) {
				superEquipBaptizePrices.add(Integer.valueOf(item.trim()));
			}
		}

		if (badgeExtendGoldCostStr != null && badgeExtendGoldCostStr.length() > 0) {
			String[] items = badgeExtendGoldCostStr.split(",");
			for (String item : items) {
				badgeExtendGoldCostList.add(Integer.valueOf(item.trim()));
			}
		}
		
		if (buyAlbumItemCountStr != null && buyAlbumItemCountStr.length() > 0) {
			String[] items = buyAlbumItemCountStr.split(",");
			for (String item : items) {
				buyAlbumItemCountList.add(Integer.valueOf(item.trim()));
			}
		}
		
		if (RichManStpeTypeStr != null && RichManStpeTypeStr.length() > 0) {
			String[] items = RichManStpeTypeStr.split(",");
			for (String item : items) {
				RichManStpeTypeList.add(Integer.valueOf(item.trim()));
			}
		}
		
		if (WishingCostItem != null && WishingCostItem.length() > 0) {
			String[] items = WishingCostItem.split(",");
			for (String item : items) {
				WishingCostList.add(item.trim());
			}
		}
		
		if (MarryCostStr != null && MarryCostStr.length() > 0) {
			String[] items = MarryCostStr.split(",");
			for (String item : items) {
				MarryCostCountList.add(Integer.valueOf(item.trim()));
			}
		}
		
		if (SupportCalendarCost != null && SupportCalendarCost.length() > 0) {
			String[] items = SupportCalendarCost.split(",");
			for (String item : items) {
				SupportCalendarCostList.add(item.trim());
			}
		}
		
		if (ChargeGuaranteed != null && ChargeGuaranteed.length() > 0) {
			String[] items = ChargeGuaranteed.split(",");
			for (String item : items) {
				RechargeLuckyList.add(Integer.valueOf(item.trim()));
			}
		}
		
		if (activity175GameTime != null && activity175GameTime.length() > 0) {
			String[] items = activity175GameTime.split(",");
			for (String item : items) {
				act175GameTimeList.add(item);
			}
		}
		
		if (activity175GameTime != null && activity175GameTime.length() > 0) {
			String[] items = activity175GameTime.split(",");
			for (String item : items) {
				act175GameTimeList.add(item);
			}
		}
		
		if (activity175TeamAward != null && activity175TeamAward.length() > 0) {
			String[] items = activity175TeamAward.split(";");
			for (String item : items) {
				act175TeamAwardList.add(item);
			}
		}
		
		if (GloryHoleCost != null && GloryHoleCost.length() > 0) {
			String[] items = GloryHoleCost.split(",");
			for (String item : items) {
				GloryHoleCostList.add(item);
			}
		}
		
		if (GloryHoleGameItem != null && GloryHoleGameItem.length() > 0) {
			String[] items = GloryHoleGameItem.split(",");
			for (String item : items) {
				GloryHoleItemList.add(item);
			}
		}
		
		if (GloryHoleGameItem != null && GloryHoleGameItem.length() > 0) {
			String[] items = GloryHoleGameItem.split(",");
			for (String item : items) {
				GloryHoleItemList.add(item);
			}
		}
		
		// 壁尻星期幾開放
		GloryHoleOpenDayList = GameUtil.StringToIntList(GloryHoleOpenDay);
		
		if (ArenaStartStr != null && ArenaStartStr.length() > 0) {
			try {
				ArenaStartDate = DATE_FORMAT.parse(ArenaStartStr);
			} catch (ParseException e) {
				MyException.catchException(e);
			}
			
		}
		
		if (DailyItemCount != null && DailyItemCount.length() > 0) {
			String[] Strcount = DailyItemCount.split(",");
			for (String conut : Strcount) {
				DailyItemList.add(Integer.valueOf(conut));
			}
		}
		
		if (RaceItemCount != null && RaceItemCount.length() > 0) {
			String[] Strcount = RaceItemCount.split(",");
			for (String conut : Strcount) {
				RaceItemList.add(Integer.valueOf(conut));
			}
		}
		
		if (ArenaItemCount != null && ArenaItemCount.length() > 0) {
			String[] Strcount = ArenaItemCount.split(",");
			for (String conut : Strcount) {
				ArenaItemList.add(Integer.valueOf(conut));
			}
		}
		
		if (MysteryItemCount != null && MysteryItemCount.length() > 0) {
			String[] Strcount = MysteryItemCount.split(",");
			for (String conut : Strcount) {
				MysteryItemList.add(Integer.valueOf(conut));
			}
		}
		
		if (MysteryRefreshCost != null && MysteryRefreshCost.length() > 0) {
			String[] Strcount = MysteryRefreshCost.split(",");
			for (String conut : Strcount) {
				MysteryRefreshList.add(Integer.valueOf(conut));
			}
		}
								
		if (RefineStoneCost != null && RefineStoneCost.length() > 0) {
			String[] Strcount = RefineStoneCost.split(",");
			for (String conut : Strcount) {
				RefineStoneList.add(Integer.valueOf(conut));
			}
		}
		
		if (RefineLockCost != null && RefineLockCost.length() > 0) {
			String[] Strcount = RefineLockCost.split(",");
			for (String conut : Strcount) {
				RefineLockList.add(Integer.valueOf(conut));
			}
		}
		
		return true;
	}

	public int getSkillSlot(int level, int rebirthStage) {
		final int REBIRTH_ZERO = 0;
		final int REBIRTH_ONE = 1;

		List<String> skillSlotLevels = SysBasicCfg.getInstance().getSkillSlotLevels();
		List<Integer> skillList = new ArrayList<Integer>();
		List<Integer> rebirthSkill = new ArrayList<Integer>();
		for (String eacheSkillSlot : skillSlotLevels) {
			String[] skillSlot = eacheSkillSlot.split("_");
			if (Integer.parseInt(skillSlot[0]) == REBIRTH_ZERO) {
				skillList.add(Integer.parseInt(skillSlot[1]));
			} else if (Integer.parseInt(skillSlot[0]) == REBIRTH_ONE) {
				rebirthSkill.add(Integer.parseInt(skillSlot[1]));
			}
		}
		switch (rebirthStage) {
		case REBIRTH_ZERO:
			for (int i = skillList.size() - 1; i >= 0; i--) {
				if (skillList.get(i) <= level) {
					return Math.min(i + 1, GsConst.MAX_SKILL_COUNT);
				}
			}
			break;
		default:
			return GsConst.MAX_SKILL_COUNT - 1;
		}
		return 0;
	}

	/**
	 * 时间字符传转Int
	 *
	 * @param timeStrs
	 * @return
	 */
	public static int timeStrToInt(String[] timeStrs) {
		int hour = Integer.valueOf(timeStrs[0]);
		int min = Integer.valueOf(timeStrs[1]);
		int sec = Integer.valueOf(timeStrs[2]);
		return hour * GsConst.ONE_HOUR_SEC + min * GsConst.ONE_MIN_SEC + sec;
	}

	/**
	 * 获取开服礼包
	 *
	 * @return
	 */
	public List<ItemInfo> getGiftAward() {
		return giftAwardItems;
	}

	public List<WeightItem<Integer>> getEquipBaptizeWeightItems(int prof) {
		return equipBaptizeAttrAllocMap.get(prof);
	}

	public int randomPunchCount() {
		return WeightUtil.random(punchWeightItems);
	}

	/**
	 * 获得装备部位对应神器属性
	 *
	 * @param equipPart
	 * @return
	 */
	public int getGodlyAttrId(int equipPart) {
		return equipPartGodlyAttrIdMap.get(equipPart);
	}

	/**
	 * 获得装备部位对应神器属性2
	 *
	 * @param equipPart
	 * @return
	 */
	public int getGodlyAttrId2(int equipPart) {
		return equipPartGodlyAttrIdMap2.get(equipPart);
	}

	/**
	 * 获得装备部位神器吞噬消耗金币系数
	 *
	 * @param equipPart
	 * @return
	 */
	public int getEquipSwallowPartRatio(int equipPart) {
		return equipSwallowPartRatioMap.get(equipPart);
	}

	public int getAllianceCreateGold() {
		return allianceCreateGold;
	}

	/**
	 * 获得装备部位神器吞噬消耗金币系数
	 *
	 * @param equipPart
	 * @return
	 */
	public int getEquipSwallowPartRatio(equipPart equipPart) {
		return equipSwallowPartRatioMap.get(equipPart.getNumber());
	}

	public int getAllianceReprotExp() {
		return allianceReprotExp;
	}

	public int getAllianceReprotSysCoin() {
		return allianceReprotSysCoin;
	}

	public int getAllianceCreateLevel() {
		return allianceCreateLevel;
	}

	public int getAllianceCreateVipLevel() {
		return allianceCreateVipLevel;
	}

	public String getArenaRankBroadcast() {
		return "@arenaRankBroadcast";
	}

	public String getArenaRankItemMsg() {
		return "@arenaRankItemMsg";
	}

	public String getArenaRankWorldMsg() {
		return "@arenaRankWorldMsg";
	}

	public String getArenaRankChangeWorldMsg() {
		return "@arenaRankChangeWorldMsg";
	}

	public String getArenaRankChangeBroadcast() {
		return "@arenaRankChangeBroadcast";
	}

	public String getAuctionWinnerBroadCast() {
		return "@AuctionWinnerBroadCast";
	}

	public String getAuctionFailBroadCast() {
		return "@AuctionFailBroadCast";
	}

	public int getBroadcastWorldMsgRank() {
		return broadcastWorldMsgRank;
	}

	public int getMaxArenaRankRecord() {
		return maxArenaRankRecord;
	}

	public int getMaxMsgQty() {
		return maxMsgQty;
	}

	public int getAutoBossJoinVipLimit() {
		return autoBossJoinVipLimit;
	}

	public int getAllianceAutoFightCostGold() {
		return allianceAutoFightCostGold;
	}

	public int getFriendCountLimit() {
		return friendCountLimit;
	}

	public int getKickAlliamceMemberExp() {
		return kickAlliamceMemberExp;
	}

	public int getRefreshCost(int refreshCount) {
		if (refreshCount >= honorShopRefreshCost.size()) {
			return honorShopRefreshCost.get(honorShopRefreshCost.size() - 1);
		} else if (refreshCount < 1) {
			return honorShopRefreshCost.get(0);
		}
		return honorShopRefreshCost.get(refreshCount);
	}

	public int getCrystalRefreshCost(int refreshCount) {
		if (refreshCount >= crystalShopRefreshCost.size()) {
			return crystalShopRefreshCost.get(crystalShopRefreshCost.size() - 1);
		} else if (refreshCount < 1) {
			return crystalShopRefreshCost.get(0);
		}
		return crystalShopRefreshCost.get(refreshCount);
	}

	public int getExpeditionTaskRefreshCost(int refreshCount) {
		if (refreshCount >= expeditionTaskRefreshCost.size()) {
			return expeditionTaskRefreshCost.get(expeditionTaskRefreshCost.size() - 1);
		} else if (refreshCount < 1) {
			return expeditionTaskRefreshCost.get(0);
		}
		return expeditionTaskRefreshCost.get(refreshCount);
	}

	public int getProfRankRewardDays() {
		return profRankRewardDays;
	}

	public int getProfRankReawrdTime() {
		return profRankReawrdTime;
	}

	public int getMaxShieldSize() {
		return maxShieldSize;
	}

	public int getMaxProfRankNum() {
		return maxProfRankNum;
	}

	public int getMaxProfRankRewardNum() {
		return maxProfRankRewardNum;
	}

	public int getMaxProfRankShowNum() {
		return maxProfRankShowNum;
	}

	public String getAddFriendNotice() {
		return "@addFriendNotice";
	}

	public String getEquipPartGodlyAttr2() {
		return equipPartGodlyAttr2;
	}

	public int getLastCampWarWinStreakMaxRank() {
		return lastCampWarWinStreakMaxRank;
	}

	public int getCurCampWarWinStreakMaxRank() {
		return curCampWarWinStreakMaxRank;
	}

	public int getEquipCompoundItemId() {
		return equipCompoundItemId;
	}

	public int getCampWarMaxInspireTimes() {
		return campWarMaxInspireTimes;
	}

	public int getCampWarInspirePrice() {
		return campWarInspirePrice;
	}

	public int getCampWarInspireBonuses() {
		return campWarInspireBonuses;
	}

	public String getCampWarPrepareTimeStr() {
		return campWarPrepareTimeStr;
	}

	public int getCampWarPrepareTime() {
		return campWarPrepareTime;
	}

	public String getCampWarBattleStartTimeStr() {
		return campWarBattleStartTimeStr;
	}

	public int getCampWarBattleStartTime() {
		return campWarBattleStartTime;
	}

	public String getCampWarBattleStopTimeStr() {
		return campWarBattleStopTimeStr;
	}

	public int getCampWarBattleStopTime() {
		return campWarBattleStopTime;
	}

	public String getCampWarShowStopTimeStr() {
		return campWarShowStopTimeStr;
	}

	public int getCampWarShowStopTime() {
		return campWarShowStopTime;
	}

	public int getCampWarBattlePeriod() {
		return campWarBattlePeriod;
	}

	public int getCampWarMinFightValue() {
		return campWarMinFightValue;
	}

	public int getCampWarRoundWinGoldRatio() {
		return campWarRoundWinGoldRatio;
	}

	public int getCampWarRoundWinReputation() {
		return campWarRoundWinReputation;
	}

	public int getCampWarWinnerAddScore() {
		return campWarWinnerAddScore;
	}

	public int getCampWarLoserAddScore() {
		return campWarLoserAddScore;
	}

	public int getDailyNewSerGiftRewardLimit() {
		return dailyNewSerGiftRewardLimit;
	}

	public int getCampWarMaxWinStreakReport() {
		return campWarMaxWinStreakReport;
	}

	public int getCampWarMaxEndStreakReport() {
		return campWarMaxEndStreakReport;
	}

	public double getAutoCampWarAwardRatio() {
		return autoCampWarAwardRatio;
	}

	public int getManualBattleCd() {
		return manualBattleCd;
	}

	public int getAutoCampWarVipLevel() {
		return autoCampWarVipLevel;
	}

	public int getAutoCampWarGold() {
		return autoCampWarGold;
	}

	public int getBannerActivityId() {
		return bannerActivityId;
	}

	public String getCampWarStartWorldMsg() {
		return "@campWarStartWorldMsg";
	}

	public String getCampWarStartChatMsg() {
		return "@campWarStartChatMsg";
	}

	public String getCampWarStopWorldMsg() {
		return "@campWarStopWorldMsg";
	}

	public String getCampWarStopChatMsg() {
		return "@campWarStopChatMsg";
	}

	public int getWinCampExtraReputation() {
		return winCampExtraReputation;
	}

	public int getEquipCompoumdCostRep() {
		return equipCompoumdCostRep;
	}

	public int getRechargeMinUnitGold() {
		return rechargeMinUnitGold;
	}

	public int getRebateMinUnitGold() {
		return rebateMinUnitGold;
	}

	public String getArenaWinAward() {
		return arenaWinAward;
	}

	public String getArenaLoseAward() {
		return arenaLoseAward;
	}

	public int getEverydayRebateMaxGold() {
		return everydayRebateMaxGold;
	}

	public String getCmHost() {
		return cmHost;
	}

	public int getCmTimeout() {
		return cmTimeout;
	}

	public int getQuickBattleAddVitality() {
		return quickBattleAddVitality;
	}

	public int getWorldBossAddVitality() {
		return worldBossAddVitality;
	}

	public int getTeamBattleAddVitality() {
		return teamBattleAddVitality;
	}

	public int getCampWarAddVitality() {
		return campWarAddVitality;
	}

	public int getArenaWinAddVitality() {
		return arenaWinAddVitality;
	}

	public int getArenaFailAddVitality() {
		return arenaFailAddVitality;
	}

	public String getAutoJoinAllianceChat() {
		return "@autojoinAllianceChat";
	}

	public String getManualJoinAllianceChat() {
		return "@manualjoinAllianceChat";
	}

	public String getExitAllianceChat() {
		return "@exitAllianceChat";
	}

	public String getKICKAllianceChat() {
		return "@kickAllianceChat";
	}

	public int getExpeditionArmoryRankingSize() {
		return expeditionArmoryRankingSize;
	}

	public String getExpeditionArmoryStageChangeMsg() {
		return "@expeditionArmoryStageChangeMsg";
	}

	public int getExpeditionArmorySysAddExp() {
		return expeditionArmorySysAddExp;
	}

	public int getExpeditionArmoryAutoAddExpTime() {
		return expeditionArmoryAutoAddExpTime;
	}

	public int getOpenExpeditionArmoryAutoAddExp() {
		return openExpeditionArmoryAutoAddExp;
	}

	public int getExpeditionArmoryAutoAddExpStage() {
		return expeditionArmoryAutoAddExpStage;
	}

	public int getCrazyRouletteRechargeUnit() {
		return crazyRouletteRechargeUnit;
	}

	public int getCrazyRouletteDayMaxTimes() {
		return crazyRouletteDayMaxTimes;
	}

	public int getCrazyRouletteAddCredits() {
		return crazyRouletteAddCredits;
	}

	public int getSwitchOfLargessGold() {
		return nLargessGoldofShare;
	}

	public int getLargessGoldNum() {
		return nLargessGoldNum;
	}

	public int getTeamBattleKickMemberCostAdd() {
		return teamBattleKickMemberCostAdd;
	}

	public int getTeamBattleKickMemberCostMax() {
		return teamBattleKickMemberCostMax;
	}

	public int getEvaluateRewardGold() {
		return evaluateRewardGold;
	}

	public int getInitMusicOn() {
		return initMusicOn;
	}

	public int getAllianceBattleMaxTurn() {
		return allianceBattleMaxTurn;
	}

	public int getAllianceBattleTime() {
		return allianceBattleTime;
	}

	public List<Integer> getAllianceBattleTeamBuff(int teamIndex) {
		return this.allianceBattleBuffMap.get(teamIndex);
	}

	public boolean isDeleteInvalid() {
		return deleteInvalid;
	}

	public boolean isIpCacheEnable() {
		return ipCacheEnable;
	}

	public float getFightActionTimeRatio() {
		return fightActionTimeRatio;
	}

	public int getSysVersion() {
		return sysVersionId;
	}

	public int getAllowAllianceBattleOpenWeekDay() {
		return allowAllianceBattleOpenWeekDay;
	}

	public int getEquipSuperBaptizePrice(int lockAttributeTypesCount) {
		if (lockAttributeTypesCount >= superEquipBaptizePrices.size()) {
			return superEquipBaptizePrices.get(superEquipBaptizePrices.size() - 1);
		}
		return superEquipBaptizePrices.get(lockAttributeTypesCount);
	}

	public int getRefreshAllianceShopPrice(int refreshShopCount) {
		if (refreshShopCount >= refreshAllianceShopPrices.size()) {
			return refreshAllianceShopPrices.get(refreshAllianceShopPrices.size() - 1);
		}
		return refreshAllianceShopPrices.get(refreshShopCount);
	}

	public String getAppVersionNotice() {
		return appVersionNotice;
	}

	/**
	 * 清理相关静态数据
	 */
	protected void clearStaticData() {
	}

	public Boolean getCsReward() {
		return csReward;
	}

	public int getRingLvlUpNeedStarLvl() {
		return ringLvlUpNeedStarLvl;
	}

	public int getRingLvlUpTimes() {
		return ringLvlUpTimes;
	}

	public int getChangeNameCost() {
		return this.changeNameCost;
	}

	public String getChangeNameNotice() {
		return "@changeNameNotice";
	}

	public String getChangeAllianceNameNotice() {
		return "@changeAllianceNameNotice";
	}

	public String getManuelRefreshDefaultCount() {
		return manuelRefreshDefaultCount;
	}

	public String getManuelRefreshPrize() {
		return manuelRefreshPrize;
	}

	public int[] getRefreshMysteryCostGoldNums() {
		return refreshMysteryCostGoldNums;
	}

	public int getSendEmailNum() {
		return sendEmailNum;
	}

	public int getForeverCardCanActivateNeedGold() {
		return foreverCardCanActivateNeedGold;
	}

	public int getActivateForeverCardGold() {
		return activateForeverCardGold;
	}

	public int getDailyGoldAward() {
		return dailyGoldAward;
	}

	public String getForeverDailyGoldAwardWorldChat() {
		return "@foreverDailyGoldAwardWorldChat";
	}

	public int getMaxElementSize() {
		return maxElementSize;
	}

	public int getElementBagDefault() {
		return elementBagDefault;
	}

	public int getElementBagExtendCost() {
		return elementBagExtendCost;
	}

	public int getElementBagExtendNum() {
		return elementBagExtendNum;
	}

	public Float getElementSwallowRatio(int quality) {
		return this.elementSwallowExpRatio.get(quality);
	}

	public int getRecastGoldCost() {
		return recastGoldCost;
	}

	public int getAccountBoundReward() {
		return accountBoundReward;
	}

	public boolean isAccountBoundOpen() {
		return accountBoundOpen;
	}

	public int getTalentLevelLimit() {
		return talentLevelLimit;
	}

	public int getLevelupTalentNum() {
		return levelupTalentNum;
	}

	public int getRebirthTalentNum() {
		return rebirthTalentNum;
	}

	public String getLocalWOTK() {
		return "@LocalWOTK";
	}

	public String getCrossWOTK() {
		return "@CrossWOTK";
	}

	public int getClearTalentPay() {
		return clearTalentPay;
	}

	public int getProfElementLevelLimit() {
		return profElementLevelLimit;
	}

	public int getYyExchangeRadio() {
		return yyExchangeRadio;
	}

	public int getMaxWingLevel() {
		return maxWingLevel;
	}

	public String getWingTenStarBroadcast() {
		return "@wingTenStarBroadcast";
	}

	public long getRankGiftTickTime() {
		return rankGiftTickTime;
	}

	public List<String> getPlatformBlackList() {
		return platformBlackList;
	}

	public String getMergeServerRewards() {
		return mergeServerRewards;
	}

	public int getMergePlayerLevel() {
		return mergePlayerLevel;
	}

	public String getMergeTime() {
		return mergeTime;
	}

	public String getFaceBookShareRewards() {
		return faceBookShareRewards;
	}

	public String getPlayerBindRewards() {
		return playerBindRewards;
	}

	public int getApplyAddAllianceMaxCount() {
		return applyAddAllianceMaxCount;
	}

	public int getRefreshApplyAddAllianceTime() {
		return refreshApplyAddAllianceTime;
	}

	public int getVitalityNoChangeDayTime() {
		return vitalityNoChangeDayTime;
	}

	public int getForgingBroadcastId() {
		return forgingBroadcastId;
	}

	public int getShootRefreshTime() {
		return shootRefreshTime;
	}

	public int getShootCloseTime() {
		return shootCloseTime;
	}

	public int getChat_msg_chunk() {
		return chat_msg_chunk;
	}

	public String getIos_git_reward() {
		return ios_git_reward;
	}

	public int getRoleBaptizeLevel() {
		return roleBaptizeLevel;
	}

	public int getEquipCompoumdLevel() {
		return equipCompoumdLevel;
	}

	public int getSkillUpgradeLevel() {
		return skillUpgradeLevel;
	}

	public int getEquipSpecialLevel() {
		return equipSpecialLevel;
	}

	public String getOpenSkillEnhanceCostItems() {
		return openSkillEnhanceCostItems;
	}

	public int getBossChallengeItemId() {
		return bossChallengeItemId;
	}

	public int getRoleTrainOpenLevel() {
		return roleTrainOpenLevel;
	}

	public List<Integer> getWarriorNotCritSkillIdList() {
		return warriorNotCritSkillIdList;
	}

	public int getFirstFastFightExp() {
		return firstFastFightExp;
	}

	public int getGrowthVipLevel() {
		return growthVipLevel;
	}

	public int getGrowthNeedGold() {
		return growthNeedGold;
	}

	/**
	 * 等级对应的格子数
	 *
	 * @param level
	 * @return
	 */
	public int getLevelOpenCount(int level) {
		if (this.openFightingLevelMap.containsKey(level)) {
			return this.openFightingLevelMap.get(level);
		}

		return 0;
	}

	/**
	 * 最大格子数
	 *
	 * @return
	 */
	public int getMaxOpenCount() {
		int maxCount = 0;
		for (int i : this.openFightingLevelMap.values()) {
			if (i > maxCount) {
				maxCount = i;
			}
		}
		return maxCount;
	}

	/**
	 * 等级对应的应援格子数
	 *
	 * @param level
	 * @return
	 */
	public int getLevelAssistanceOpenCount(int level) {
		if (this.openAssistanceLevelMap.containsKey(level)) {
			return this.openAssistanceLevelMap.get(level);
		}
		return 0;
	}

	public Map<Integer, Integer> getOpenAssistanceLevelMap() {
		return this.openAssistanceLevelMap;
	}

	public int getApplyFriendCountLimit() {
		return applyFriendCountLimit;
	}

	public int getAllianceListVitality() {
		return allianceListVitality;
	}

	public int getCheckCurVipLevel() {
		return checkCurVipLevel;
	}

	/**
	 * 副将数量减少搜索敌人时间配置
	 *
	 * @return
	 */
	public Map<Integer, Integer> getDecrPVESearchTimeMap() {
		return decrPVESearchTimeMap;
	}

	/**
	 * 聊天同样的消息间隔时间
	 *
	 * @return
	 */
	public int getChatSameMsgInterval() {
		return chatSameMsgInterval * 1000;
	}

	/**
	 * 荣誉商店（竞技场商店）显示数目
	 *
	 * @return
	 */
	public int getHonorShopShowCount() {
		return honorShopShowCount;
	}

	/**
	 * 联盟商店显示数目
	 *
	 * @return
	 */
	public int getAllianceShopShowCount() {
		return allianceShopShowCount;
	}
	
	/**
	 * 每日充值返利功能开关
	 *
	 * @return
	 */
	public int getDailyChargeRebateEnable() {
		return dailyChargeRebateEnable;
	}

	/**
	 * 每日充值返利发放奖励时间
	 *
	 * @return
	 */
	public String getDailyChargeRebateTime() {
		return dailyChargeRebateTime;
	}
	/**
	 * cdk统一码
	 *
	 * @return
	 */
	public String getCdkUniteCode() {
		return cdkUniteCode;
	}

	public int getReleaseUrResetCost() {
		return releaseUrResetCost;
	}

	public int getReleaseUrLotteryCost() {
		return releaseUrLotteryCost;
	}

	public int getReleaseUrGiveLucky() {
		return releaseUrGiveLucky;
	}

	public int getGiftExchangeSwitch() {
		return giftExchangeSwitch;
	}

	public List<Integer> getScreenedMailIdList() {
		return screenedMailIdList;
	}

	public int getReleaseUrCostLucky2() {
		return releaseUrCostLucky2;
	}

	public int getReleaseUrGiveLucky2() {
		return releaseUrGiveLucky2;
	}

	public int getReleaseUrResetCost3() {
		return releaseUrResetCost3;
	}

	public int getReleaseUrLotteryCost3() {
		return releaseUrLotteryCost3;
	}

	public int getReleaseUrGiveLucky3() {
		return releaseUrGiveLucky3;
	}

	public int getActiveComplianceCycleDays() {
		return activeComplianceCycleDays;
	}

	public int getActiveCompliancePoint() {
		return activeCompliancePoint;
	}

	public int getActiveCanAwardDays() {
		return activeCanAwardDays;
	}

	public int getActivity123UrCostLucky() {
		return activity123UrCostLucky;
	}

	public int getActivity123UrGiveLucky() {
		return activity123UrGiveLucky;
	}

	public int getShopGemBuyMaxOnce() {
		return shopGemBuyMaxOnce;
	}

	public int getAutoHighSpeedBreedOpenVip() {
		return autoHighSpeedBreedOpenVip;
	}

	public int getActivity127UrGiveLucky() {
		return activity127UrGiveLucky;
	}

	public int getActivity128UrGiveLucky() {
		return activity128UrGiveLucky;
	}

	public int getActivity128UrRankMaxNum() {
		return activity128UrRankMaxNum;
	}

	public String getActivity128UrRankCalcTime() {
		return activity128UrRankCalcTime;
	}

	public int getAct123EvenDotsRate() {
		return act123EvenDotsRate;
	}

	public int getAct116EvenDotsRate() {
		return act116EvenDotsRate;
	}

	public String getActivity132OldUserTime() {
		return activity132OldUserTime;
	}

	public int getActivity132OldUserLevel() {
		return activity132OldUserLevel;
	}

	public int getEighteenPrincesMaxLayerNum() {
		return eighteenPrincesMaxLayerNum;
	}

	public int getEighteenPrincesOpenLevel() {
		return eighteenPrincesOpenLevel;
	}

	public int getEighteenMaxUseTimes() {
		return eighteenMaxUseTimes;
	}

	public int getEighteenFreeUseTimes() {
		return eighteenFreeUseTimes;
	}

	public int getEighteenUseSpend() {
		return eighteenUseSpend;
	}

	public String getActivity137OpenLoginCount() {
		return activity137OpenLoginCount;
	}

	public int getActivity137OpenTime() {
		return activity137OpenTime;
	}

	public int getBadgeBagSize() {
		return badgeBagSize;
	}

	public int getBadgeExtendTimes() {
		return badgeExtendTimes;
	}

	public int getBadgeExtendSize() {
		return badgeExtendSize;
	}

	public int getEighteenPrincesOpenServerDays() {
		return eighteenPrincesOpenServerDays;
	}

	public int getBadgeExtendGoldCost(int times) {
		if (times >= badgeExtendGoldCostList.size()) {
			return badgeExtendGoldCostList.get(badgeExtendGoldCostList.size() - 1);
		}

		return badgeExtendGoldCostList.get(times);
	}

	public int getActivity140Continuetime() {
		return activity140Continuetime;
	}

	public int getActivity140LotteryCost() {
		return activity140LotteryCost;
	}

	public String getActivity140OpenLoginCount() {
		return activity140OpenLoginCount;
	}
	
	public int getbuyAlbumItemId() {
		return buyAlbumItemId;
	}
	
	public int getbuyAlbumItemCount(int times) {
		if (times >= buyAlbumItemCountList.size()) {
			return buyAlbumItemCountList.get(buyAlbumItemCountList.size() - 1);
		}

		return buyAlbumItemCountList.get(times);
	}
	
	public int getuseItemMaxcount() {
		return useItemMaxcount;
	}
	
	public String getRichManUseItem() {
		return RichManUseItem;
	}
	
	public int getRichManStpeType(int step) {
		if (step >= RichManStpeTypeList.size()) {
			return RichManStpeTypeList.get(RichManStpeTypeList.size() - 1);
		}

		return RichManStpeTypeList.get(step);
	}
	
	public String getPirateUseItem() {
		return PirateUseItem;
	}
	
	public int getAdaptationType() {
		return AdaptationType;
	}
	
	public boolean IsBattleCheck(){
		return (BattleCheck == 1);
	}
	
	public int getEquipLimitLevel() {
		return EquipLimitLevel;
	}
		
	public int getMysteryMaxFreeRefresh() {
		return MysteryFreeRefresh;
	}
	
	public int getMysteryMaxRefresh() {
		return MysteryMaxRefresh;
	}
	
	public int getMysteryRefreshTime() {
		return MysteryRefreshTime;
	}
	
	public int getChosenOneSingleCost() {
		return ChosenOneSingleCost ;
	}
	
	public int getChosenOneTenCost() {
		return ChosenOneTenCost;
	}
	public int getChosenOneGuarant() {
		return ChosenOneGuarant;
	}
	
	public String getWishingCostItem(int type) {
		if (type >= WishingCostList.size()) {
			return WishingCostList.get(WishingCostList.size() - 1);
		}
		return WishingCostList.get(type);
	}
	
	public int getWishingIncLucky() {
		return WishingIncLucky;
	}
	
	public int getWishingRefreshTime() {
		return WishingRefreshTime;
	}
	
	public String getWishingRefreshCost() {
		return WishingRefreshCost;
	}
	
	public String getMarryCostItem() {
		return MarryCostItem;
	}
	
	public int getMarryCostCount(int idx) {
		
		if ((idx >= 0) && (idx < MarryCostCountList.size())) {
			return MarryCostCountList.get(idx);
		}
		return -1;
	}
		
	public int getNewbieDays() {
		return NewbieDays;
	}
	
	public int getFriendship() {
		return FriendshipPoint;
	}
	
	public int getWishingFreeDraw() {
		return WishingFreeDraw;
	}
	
	public String getHeroDramaGift() {
		return HeroDramaGift;
	}
	
	public String getResetHero() {
		return ResetHero;
	}
	
	
	public String getResetHeroCostItem() {
		return ResetHeroCostItem;
	}

	public String getSpriteSingleCost() {
		return SpriteSingleCost;
	}
	
	public String getSpriteTenCost() {
		return SpriteTenCost;
	}
	
	public String getLoginTenDrawAward() {
		return LoginTenDrawAward;
	}
	
	public int getLoginTenDrawDays() {
		return LoginTenDrawDays;
	}
	
	public String getRaceSummon() {
		return RaceSummon;
	}

	public int getRacePoint() {
		return RacePoint;
	}
	
	public String getSupportCalendarCost(int type) {
		int idx = type -1;
		if (idx >= SupportCalendarCostList.size()) {
			return SupportCalendarCostList.get(SupportCalendarCostList.size() - 1);
		}
		return SupportCalendarCostList.get(idx);
	}
	
	public int getActivity165RankMaxNum() {
		return activity165RankMaxNum;
	}
	
	public String getGoldMineCost() {
		return GoldMineCost;
	}
	
	public String getReSetGuildSoulItem() {
		return ReSetGuildSoulItem;
	}
	
	public String getResetEquip() {
		return ResetEquip;
	}
	
	public int getBuyCoinCriRate() {
		return BuyCoinCriRate;
	}

	public int getCallOfFriendshipSingleCost() {
		return CallOfFriendshipSingleCost;
	}

	public int getCallOfFriendshipTenCost() {
		return CallOfFriendshipTenCost;
	}

	public String getChosenOneItemCost() {
		return ChosenOneItemCost;
	}

	public String getChosenOneTenItemCost() {
		return ChosenOneTenItemCost;
	}
	
	public String getHeroEXPItem() {
		return HeroEXPItem;
	}
	

	public String getHeroStoneItem() {
		return HeroStoneItem;
	}

	public int getReChargeLucky(int index) {
		if(index < RechargeLuckyList.size() && index >= 0){
			return RechargeLuckyList.get(index);
		}
		return 0;
	}
	
	public int getRechargeLuckyCD() {
		return RechargeLuckyCD;
	}
	
	public String getCallOfRaceAward() {
		return CallOfRaceAward;
	}
		
	public String getAct175GameTime(int index) {
		if(index < act175GameTimeList.size() && index >= 0){
			return act175GameTimeList.get(index);
		}
		return "";
	}
	
	public String getAct175TeamAward(int index) {
		if(index < act175TeamAwardList.size() && index >= 0) {
			return act175TeamAwardList.get(index);
		}
		return "";
	}

	public int getGloryHoleFree() {
		return GloryHoleFree;
	}

	public int getGloryHoleGameTime() {
		return GloryHoleGameTime;
	}

	public String getGloryHoleCost(int index) {
		if(index < GloryHoleCostList.size() && index >= 0) {
			return GloryHoleCostList.get(index);
		}
		return "";
	}
	
	public int getGloryHoleMaxCost() {
		return GloryHoleMaxCost;
	}

	public String getGloryHoleGameItem(int index) {
		if(index < GloryHoleItemList.size() && index >= 0) {
			return GloryHoleItemList.get(index);
		}
		return "";
	}
	
	public int getGloryHoleMaxUseItem() {
		return GloryHoleMaxUseItem;
	}

	public int getMaxMailLimit() {
		return MaxMailLimit;
	}

	public String getFirstgacha() {
		return Firstgacha;
	}

	public List<Integer> getMysteryItemList() {
		return MysteryItemList;
	}

//	public int getDailyItemCount() {
//		int total = 0;
//		for (Integer count : DailyItemList) {
//			total = total + count;
//		}
//		return total;
//	}
	
	public List<Integer> getDailyItemList() {
		return DailyItemList;
	}
	
	public List<Integer> getRaceItemList() {
		return RaceItemList;
	}
	
	public List<Integer> getArenaItemList() {
		return ArenaItemList;
	}
	
	public int getDungeonDayFreeTimes() {
		return dungeonDayFreeTimes;
	}

	public int getDungeonOneKeyVIPLV() {
		return dungeonOneKeyVIPLV;
	}

	public int getDungeonOneKeyDecStar() {
		return dungeonOneKeyDecStar;
	}

	public Date getArenaStartDate() {
		return ArenaStartDate;
	}

	public void setArenaStartDate(Date arenaStartDate) {
		ArenaStartDate = arenaStartDate;
	}

	public int getArenaCycleDay() {
		return ArenaCycleDay;
	}

	public int getFailedGiftCount() {
		return FailedGiftCount;
	}

	public int getSecretMaxPower() {
		return SecretMaxPower;
	}

	public int getSecretRecoverPower() {
		return SecretRecoverPower;
	}

	public int getSecretRecoverTime() {
		return SecretRecoverTime;
	}

	public int getSecretDecPower() {
		return SecretDecPower;
	}

	public int getCallEquipSingleCost() {
		return CallEquipSingleCost;
	}

	public int getCallEquipTenCost() {
		return CallEquipTenCost;
	}

	public String getCallEquipItemCost() {
		return CallEquipItemCost;
	}

	public String getCallEquipTenItemCost() {
		return CallEquipTenItemCost;
	}

	public String getCycleStageItemCost() {
		return CycleStageItemCost;
	}

	public String getCycleStageCoin() {
		return CycleStageCoin;
	}

	public int getCycleStageRecover() {
		return CycleStageRecover;
	}
	
	public String getCycleStageItemCost2() {
		return CycleStageItemCost2;
	}

	public String getCycleStageCoin2() {
		return CycleStageCoin2;
	}

	public int getCycleStageRecover2() {
		return CycleStageRecover2;
	}
	
	public int getMysteryRefreshList(int index) {
		if(index < MysteryRefreshList.size() && index >= 0) {
			return MysteryRefreshList.get(index);
		}		
		return MysteryRefreshList.get(MysteryRefreshList.size()-1);
	}

	public String getRefineStone() {
		return RefineStone;
	}

	public String getRefineStoneMax() {
		return RefineStoneMax;
	}

	public String getRefineLock() {
		return RefineLock;
	}
	
	public int getRefineStoneList(int index) {
		if(index < RefineStoneList.size() && index >= 0) {
			return RefineStoneList.get(index);
		}
		return RefineStoneList.get(RefineStoneList.size()-1);
	}
	
	public int getRefineLockList(int index) {
		if(index < RefineLockList.size() && index >= 0) {
			return RefineLockList.get(index);
		}
		return RefineLockList.get(RefineLockList.size()-1);
	}

	public int getSingleBossChanllengeTime() {
		return SingleBossChanllengeTime;
	}

	public String getSingleBossCoin() {
		return SingleBossCoin;
	}

	public int getSeasonTowerFloorContrl() {
		return SeasonTowerFloorContrl;
	}

	public int getNewRoleGuarant() {
		return NewRoleGuarant;
	}
	
	public List<Integer> getGloryHoleOpenDayList() {
		return GloryHoleOpenDayList;
	}

	public String getPickUpItemCost() {
		return PickUpItemCost;
	}

	public String getPickUpTenItemCost() {
		return PickUpTenItemCost;
	}
	
}