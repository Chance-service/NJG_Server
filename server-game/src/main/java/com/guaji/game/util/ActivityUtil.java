package com.guaji.game.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

import org.guaji.config.ConfigManager;
import org.guaji.db.DBManager;
import org.guaji.msg.Msg;
import org.guaji.net.protocol.Protocol;
import org.guaji.os.GuaJiRand;
import org.guaji.os.GuaJiTime;
import org.guaji.os.MyException;
import org.guaji.xid.GuaJiXID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.guaji.game.GsApp;
import com.guaji.game.GsConfig;
import com.guaji.game.ServerData;
import com.guaji.game.battle.MapReward;
import com.guaji.game.config.AccConsumeItemCfg;
import com.guaji.game.config.Activity140DishWheelRadioCfg;
import com.guaji.game.config.ActivityCfg;
import com.guaji.game.config.ActivityCfg.ActivityItem;
import com.guaji.game.config.ActivityGiftAward169Cfg;
import com.guaji.game.config.ActivityGiftAward170Cfg;
import com.guaji.game.config.ActivityGiftAward181Cfg;
import com.guaji.game.config.ActivityGiftAward182Cfg;
import com.guaji.game.config.ActivityGiftAward183Cfg;
import com.guaji.game.config.ActivityGiftAward184Cfg;
import com.guaji.game.config.ActivityGiftAward185Cfg;
import com.guaji.game.config.ActivityGiftAward186Cfg;
import com.guaji.game.config.ActivityTimeCfg;
import com.guaji.game.config.ChatSkinCfg;
import com.guaji.game.config.ConsumeWeekCardCfg;
import com.guaji.game.config.ContinueRechargeCfg;
import com.guaji.game.config.DailyQuestPointCfg;
import com.guaji.game.config.DoubleRechargeCfg;
import com.guaji.game.config.ExpeditionArmoryRankingCfg;
import com.guaji.game.config.FailedGift177Cfg;
import com.guaji.game.config.GachaListCfg;
import com.guaji.game.config.GloryHoleDailyCfg;
import com.guaji.game.config.LevelGiftAward132Cfg;
import com.guaji.game.config.LuckyTreasureDropsCfg;
import com.guaji.game.config.PackBoxCfg;
import com.guaji.game.config.RechargeBounceCfg;
import com.guaji.game.config.RechargeRatioCfg;
import com.guaji.game.config.RechargeReturnLotteryCfg;
import com.guaji.game.config.ReleaseStepGiftCfg;
import com.guaji.game.config.SeasonTowerTimeCfg;
import com.guaji.game.config.SignNewCfg;
import com.guaji.game.config.SingleRechargeCfg;
import com.guaji.game.config.StageGiftAward151Cfg;
import com.guaji.game.config.SubScriptionCfg;
import com.guaji.game.config.SysBasicCfg;
import com.guaji.game.config.TimeGiftCfg;
import com.guaji.game.config.TimeLimitCfg;
import com.guaji.game.config.VipWelfareCfg;
import com.guaji.game.config.WeekCardCfg;
import com.guaji.game.entity.ActivityEntity;
import com.guaji.game.entity.ChatSkinEntity;
import com.guaji.game.entity.ChatSkinItem;
import com.guaji.game.entity.CycleStageShopEntity;
import com.guaji.game.entity.DailyQuestItem;
import com.guaji.game.entity.GodSeaShopEntity;
import com.guaji.game.entity.HaremActivityEntity;
import com.guaji.game.entity.ItemEntity;
import com.guaji.game.entity.ServerTimeLimitEntity;
import com.guaji.game.item.AwardItems;
import com.guaji.game.item.ConsumeItems;
import com.guaji.game.item.DropItems.Item;
import com.guaji.game.item.ItemInfo;
import com.guaji.game.log.BehaviorLogger;
import com.guaji.game.log.BehaviorLogger.Action;
import com.guaji.game.log.BehaviorLogger.Params;
import com.guaji.game.log.BehaviorLogger.Source;
import com.guaji.game.manager.ActivityManager;
import com.guaji.game.manager.MailManager;
import com.guaji.game.manager.WealthClubManager;
import com.guaji.game.manager.shop.strategy.CycleStageMarket;
import com.guaji.game.manager.shop.strategy.GodSeaMarket;
import com.guaji.game.module.activity.ActiveCompliance.ActiveStatus;
import com.guaji.game.module.activity.activity124.Activity124Status;
import com.guaji.game.module.activity.activity132.Activity132Status;
import com.guaji.game.module.activity.activity137.Activity137Status;
import com.guaji.game.module.activity.activity140.Activity140Status;
import com.guaji.game.module.activity.activity151.Activity151Status;
import com.guaji.game.module.activity.activity159.Activity159Status;
import com.guaji.game.module.activity.activity160.Activity160Status;
import com.guaji.game.module.activity.activity161.Activity161Status;
import com.guaji.game.module.activity.activity162.Activity162Status;
import com.guaji.game.module.activity.activity163.Activity163Status;
import com.guaji.game.module.activity.activity164.Activity164Status;
import com.guaji.game.module.activity.activity168.Activity168Status;
import com.guaji.game.module.activity.activity169.Activity169Status;
import com.guaji.game.module.activity.activity170.Activity170Status;
import com.guaji.game.module.activity.activity175.Activity175Status;
import com.guaji.game.module.activity.activity176.Activity176ExchangeStatus;
import com.guaji.game.module.activity.activity177.Activity177Handler;
import com.guaji.game.module.activity.activity177.Activity177Status;
import com.guaji.game.module.activity.activity179.Activity179Handler;
import com.guaji.game.module.activity.activity179.Activity179Status;
import com.guaji.game.module.activity.activity181.Activity181Status;
import com.guaji.game.module.activity.activity182.Activity182Status;
import com.guaji.game.module.activity.activity183.Activity183Status;
import com.guaji.game.module.activity.activity184.Activity184Status;
import com.guaji.game.module.activity.activity185.Activity185Status;
import com.guaji.game.module.activity.activity186.Activity186Status;
import com.guaji.game.module.activity.activity187.Activity187Handler;
import com.guaji.game.module.activity.activity187.Activity187Status;
import com.guaji.game.module.activity.activity190.Activity190Status;
import com.guaji.game.module.activity.activity191.Activity191Status;
import com.guaji.game.module.activity.activity192.Activity192Status;
import com.guaji.game.module.activity.activity193.Activity193Status;
import com.guaji.game.module.activity.activity194.Activity194Status;
import com.guaji.game.module.activity.activity196.Activity196Status;
import com.guaji.game.module.activity.commendationTribe.CommendationTribeStatus;
import com.guaji.game.module.activity.consumMonthCard.ConMonthCardStatus;
import com.guaji.game.module.activity.consumWeekCard.ConWeekCardStatus;
import com.guaji.game.module.activity.consume.AccConsumeStatus;
import com.guaji.game.module.activity.consumeitem.AccConItemStatus;
import com.guaji.game.module.activity.discountGift.DiscountGiftData;
import com.guaji.game.module.activity.exchange.ExchangeStatus;
import com.guaji.game.module.activity.firstgiftpack.FirstGiftPackStatus;
import com.guaji.game.module.activity.foreverCard.ForeverCardStatus;
import com.guaji.game.module.activity.fortune.FortuneStatus;
import com.guaji.game.module.activity.grabRedEnvelope.RedEnvelopeServerStatus;
import com.guaji.game.module.activity.grabRedEnvelope.RedEnvelopeStatus;
import com.guaji.game.module.activity.growthFund.GrowthFundStatus;
import com.guaji.game.module.activity.harem.HaremManager;
import com.guaji.game.module.activity.loginsigned.AccLoginSignedStatus;
import com.guaji.game.module.activity.monthcard.MonthCardStatus;
import com.guaji.game.module.activity.newUR.NewURManager;
import com.guaji.game.module.activity.newUR.NewURStatus;
import com.guaji.game.module.activity.newWeekCard.NewWeekCardStatus;
import com.guaji.game.module.activity.recharge.AccRechargeStatus;
import com.guaji.game.module.activity.recharge.ContinueMoneyRechargeStatus;
import com.guaji.game.module.activity.recharge.ContinueRechargeDays131Status;
import com.guaji.game.module.activity.recharge.ContinueRechargeStatus;
import com.guaji.game.module.activity.recharge.DoubleRechargeStatus;
import com.guaji.game.module.activity.recharge.FirstRechargeStatus;
import com.guaji.game.module.activity.recharge.RechargeRebateStatus;
import com.guaji.game.module.activity.recharge.SingleRechargeStatus;
import com.guaji.game.module.activity.roulette.RouletteStatus;
import com.guaji.game.module.activity.salePacket.SalePacketStatus;
import com.guaji.game.module.activity.shoot.ShootActivityInfo;
import com.guaji.game.module.activity.shoot.ShootActivityManager;
import com.guaji.game.module.activity.timeLimit.PersonalTimeLimitStatus;
import com.guaji.game.module.activity.timeLimit.TimeLimitManager;
import com.guaji.game.module.activity.timeLimit.TimeLimitPurchaseInfoHandler;
import com.guaji.game.module.activity.vipPackage.VipPackageStatus;
import com.guaji.game.module.activity.wealthClub.WealthClubStatus;
import com.guaji.game.module.activity.weekCard.WeekCardStatus;
import com.guaji.game.module.activity.welfareReward.WelfareRewardStatus;
import com.guaji.game.module.activity.welfareRewardByRegDate.WelfareRewardStatusByRegDate;
import com.guaji.game.player.Player;
import com.guaji.game.player.PlayerData;
import com.guaji.game.protocol.Activity2.HPChatSkinBuy;
import com.guaji.game.protocol.Activity2.HPChatSkinInfo;
import com.guaji.game.protocol.Activity2.HPChatSkinInfo.ChatSkinInfo;
import com.guaji.game.protocol.Activity2.HPDiscountBuySuccRet;
import com.guaji.game.protocol.Activity2.HPForeverCardRet;
import com.guaji.game.protocol.Activity3;
import com.guaji.game.protocol.Activity4;
import com.guaji.game.protocol.Activity4.Activity132LevelGiftBuyRes;
import com.guaji.game.protocol.Activity4.LevelGiftInfo;
import com.guaji.game.protocol.Activity5.SubScriptionResp;
import com.guaji.game.protocol.Activity5.SupportCalendarRep;
import com.guaji.game.protocol.Battle;
import com.guaji.game.protocol.Battle.DropAward;
import com.guaji.game.protocol.Const;
import com.guaji.game.protocol.Const.ActivityId;
import com.guaji.game.protocol.HP;
import com.guaji.game.protocol.Mail;
import com.guaji.game.protocol.Mail.MailType;
import com.guaji.game.protocol.Status;
import com.guaji.game.recharge.SyncSubResponse;
import com.guaji.game.recharge.SyncSubscriptionService;
import com.guaji.game.util.GsConst.ActivityTimeType;
import com.guaji.game.util.TapDBUtil.TapDBSource;

/**
 * 活动数据帮助类
 */
public class ActivityUtil {

	/**
	 * 时间配置按活动Id分类
	 */
	private static HashMap<Integer, TreeSet<ActivityTimeCfg>> activityTimeCfgs = new HashMap<Integer, TreeSet<ActivityTimeCfg>>();

	/**
	 * 将活动时间配置按ActivityId归类
	 */
	public static void activityTimeCfgsClassify() {

		List<ActivityTimeCfg> timeCfgs = ConfigManager.getInstance().getConfigList(ActivityTimeCfg.class);

		for (ActivityTimeCfg cfg : timeCfgs) {
			int activityId = cfg.getActivityId();
			if (activityTimeCfgs.containsKey(activityId)) {
				activityTimeCfgs.get(activityId).add(cfg);
			} else {
				TreeSet<ActivityTimeCfg> cfgSet = new TreeSet<ActivityTimeCfg>();
				cfgSet.add(cfg);
				activityTimeCfgs.put(activityId, cfgSet);
			}
		}

		Logger logger = LoggerFactory.getLogger("");
		logger.info("activityTimeCfgs has been classified.");
	}

	/**
	 * 个人活动过滤 - 是否在玩家的前端显示
	 *
	 * @return true 即使活动开放也不在个人前端显示
	 */
	public static boolean isCancelShowInClient(int activityId, int stageId, PlayerData playerData) {
		
		if (activityId == Const.ActivityId.ACTIVECOMPLIANCE_VALUE) {
			ActiveStatus activeStatus = ActivityUtil.getActiveComplianceStatus(playerData);
			if (activeStatus != null) {
		        if (activeStatus.calcActivitySurplusTime() <= 0){
					return true;
				}
			}
		}
		
		if ((activityId == Const.ActivityId.ACTIVITY148_MARRY_GAME_VALUE)||
				(activityId == Const.ActivityId.ACTIVITY149_THREE_DAY_VALUE)||
				(activityId == Const.ActivityId.ACTIVITY150_LIMIT_GIFT_VALUE)||
				(activityId== Const.ActivityId.ACCUMULATIVE_LOGIN_SEVEN_VALUE)) {
						
			int SurplusTime = playerData.getStateEntity().calcNewbieSurplusTime();
	        if (SurplusTime == 0){
				return true;
			}
		}
		
		if (activityId == Const.ActivityId.ACTIVITY194_SeasonTower_VALUE) {
			
			//進階控制開關
			int timeIdx = SeasonTowerTimeCfg.getValidTimeIdx();
			
			if (timeIdx == -1) {
				return true;
			}
		}
		
		// 折扣礼包活动
		if (activityId == Const.ActivityId.SALE_PACKET_VALUE) {
			return false;
		}
		// 日常签到活动
		if (activityId == Const.ActivityId.DAILY_QUEST_VALUE) {
			return false;
		}
		// 英雄令商店打折活动不在列表中显示
		if (activityId == Const.ActivityId.HERO_TOKEN_SHOP_VALUE) {
			return true;
		}
		// 首冲
		if (activityId == Const.ActivityId.FIRST_GIFTPACK_VALUE) {
			FirstGiftPackStatus status = ActivityUtil.getActivityStatus(playerData,
					Const.ActivityId.FIRST_GIFTPACK_VALUE, 0, FirstGiftPackStatus.class);

			if (status.getLastAwareTime() != null) {
				return true;
			}
		}
		// 新手8天活动时补单独开放此活动
		if (activityId == Const.ActivityId.QUICK_COST_RATIO_VALUE) {
			Date registerDate = playerData.getPlayerEntity().getCreateTime();
			if (getRegisterCycleActivityId(registerDate) > 0) {
				return true;
			}
		}
		// 连续充值的奖励被全部领取后不再显示
		if (activityId == Const.ActivityId.CONTINUE_RECHARGE_VALUE) {
			ContinueRechargeStatus status = getActivityStatus(playerData, activityId, stageId,
					ContinueRechargeStatus.class);
			int awardCfgSize = ConfigManager.getInstance().getConfigMap(ContinueRechargeCfg.class).size();
			return (status.getGotAwardCfgIds().size() >= awardCfgSize);
		}
		// 充值返利活动截止时间前，累计充值额度小于指定额度不再返利
		if (activityId == Const.ActivityId.RECHARGE_REBATE_VALUE) {
			Date registerDate = playerData.getPlayerEntity().getCreateTime();
			RechargeRebateStatus status = getActivityStatus(playerData, activityId, stageId,
					RechargeRebateStatus.class);
			return (calcRechargeRebateActivityStatus(registerDate) > GsConst.RechargeRebateActivity.STATUS_RECHARGE
					&& status.getAccRechargeAmount() < SysBasicCfg.getInstance().getRechargeMinUnitGold());
		}
		// 充值返利活动截止时间前，累计充值额度小于指定额度不再返利
		if (activityId == Const.ActivityId.RECHARGE_REBATE2_VALUE) {
			ActivityTimeCfg timeCfg = ActivityUtil.getCurActivityTimeCfg(activityId);
			RechargeRebateStatus rebateStatus = ActivityUtil.getActivityStatus(playerData, activityId,
					timeCfg.getStageId(), RechargeRebateStatus.class);
			int accAmount = rebateStatus.getAccRechargeAmount();
			SysBasicCfg sysCfg = SysBasicCfg.getInstance();
			int returnGold = Math.min(sysCfg.getEverydayRebateMaxGold(),
					accAmount / sysCfg.getRechargeMinUnitGold() * sysCfg.getRebateMinUnitGold());
			if (timeCfg.isEnd() && returnGold <= 0) {
				return true;
			}
		}
		// 如果周卡截止时间前未购买，截止时间后不再显示 || 周卡奖励领取完了也提前关闭
		if (activityId == Const.ActivityId.WEEK_CARD_VALUE) {
			ActivityItem activityItem = ActivityCfg.getActivityItem(Const.ActivityId.WEEK_CARD_VALUE);
			if (activityItem == null || activityItem.getActivityTimeType() == ActivityTimeType.CLOSED) {
				return true;
			}
			ActivityTimeCfg activityTimeCfg = ActivityUtil.getCurActivityTimeCfg(activityId);
			if (activityTimeCfg == null) {
				return true;
			}
			WeekCardStatus status = getActivityStatus(playerData, activityId, activityTimeCfg.getStageId(),
					WeekCardStatus.class);
			return (activityTimeCfg.isEnd() && status.getLeftDays() <= 0);
		}

		if (activityId == Const.ActivityId.SHOOT_ACTIVITY_VALUE) {
			return !ShootActivityManager.getInstance().isShow();
		}

		// 天降元宝活动，如果玩家所有阶段已经完成则不显示该入口
		if (activityId == Const.ActivityId.WELFARE_REWARD_VALUE) {
			WelfareRewardStatus status = ActivityUtil.getActivityStatus(playerData,
					Const.ActivityId.WELFARE_REWARD_VALUE, stageId, WelfareRewardStatus.class);
			if (null != status && !status.canPlay()) {
				return true;
			}
		}

		// 新手UR活动
		if (activityId == Const.ActivityId.NEW_UR_VALUE) {
			NewURStatus status = ActivityUtil.getNewURStatus(playerData);
			if (status != null) {
				if (!NewURManager.canPlay(playerData.getPlayerEntity().getLevel(), status)) {
					return true;
				}
			}
		}

		// 新手扭蛋活动
		if (activityId == Const.ActivityId.NEW_ND_VALUE) {
			if (!HaremManager.isNewStrictOpen(playerData.getHaremActivityEntity()))
				return true;
		}
		// 7日之诗活动
//		if (activityId == Const.ActivityId.ACCUMULATIVE_LOGIN_SEVEN_VALUE) {
//			if (clacSevenDaySurplusTime(playerData.getPlayerEntity().getCreateTime()) <= 0)
//				return true;
//		}
		
		// 活動156成就
		if (activityId == Const.ActivityId.ACTIVITY156_ACHIEVE_FIGHTVALUE_VALUE) {
			if (clacQuestSurplusTime(playerData.getPlayerEntity().getCreateTime()) <= 0)
				return true;
		}

		// 控制前端不显示消耗型周卡
		if (activityId == Const.ActivityId.CONSUME_MONTH_CARD_VALUE) {
			ActivityItem activityItem = ActivityCfg.getActivityItem(Const.ActivityId.CONSUME_MONTH_CARD_VALUE);
			if (activityItem == null || activityItem.getActivityTimeType() == ActivityTimeType.CLOSED) {
				return true;
			}
		}
		// 控制不显示消耗型月卡
		if (activityId == Const.ActivityId.CONSUME_WEEK_CARD_VALUE) {
			ActivityItem activityItem = ActivityCfg.getActivityItem(Const.ActivityId.CONSUME_WEEK_CARD_VALUE);
			if (activityItem == null || activityItem.getActivityTimeType() == ActivityTimeType.CLOSED) {
				return true;
			}
		}
		/* 需要控制个人前端显示的活动添加 return true 的分支 */
		return false;
	}

	/**
	 * 获取指定活动当前开放的时间配置
	 *
	 * @param activityId
	 * @return 返回null说明活动没有开放
	 */
	public static ActivityTimeCfg getCurActivityTimeCfg(int activityId) {
		if (activityTimeCfgs.containsKey(activityId)) {

			TreeSet<ActivityTimeCfg> timeCfgs = activityTimeCfgs.get(activityId);
			for (ActivityTimeCfg cfg : timeCfgs) {
				if (cfg.isActive()) {
					return cfg;
				}
			}
		}
		return null;
	}

	/**
	 * 获取指定活动当前开放的时间配置（到end非close）
	 *
	 * @param activityId
	 * @return 返回null说明活动没有开放
	 */
	public static ActivityTimeCfg getCurActivityTimeToEndCfg(int activityId) {
		if (activityTimeCfgs.containsKey(activityId)) {
			TreeSet<ActivityTimeCfg> timeCfgs = activityTimeCfgs.get(activityId);
			for (ActivityTimeCfg cfg : timeCfgs) {
				if (cfg.isActiveToEnd()) {
					return cfg;
				}
			}
		}
		return null;
	}

	/**
	 * 获得活动状态信息
	 *
	 * @param playerData
	 * @param activityId
	 * @param stageId
	 * @param statusClazz
	 * @return
	 */
	public static <T> T getActivityStatus(PlayerData playerData, int activityId, int stageId, Class<T> statusClazz) {
		@SuppressWarnings("unchecked")
		ActivityEntity<T> activityEntity = (ActivityEntity<T>) playerData.getActivityEntity(activityId, stageId);
		if (activityEntity == null) {
			activityEntity = new ActivityEntity<T>(playerData.getId(), activityId, stageId);
			try {
				activityEntity.setActivityStatus(statusClazz.newInstance());
			} catch (InstantiationException | IllegalAccessException e) {
				MyException.catchException(e);
				return null;
			}
			playerData.createActivity(activityEntity);
			ActivityCreateDo(playerData,activityId);
		}
		return activityEntity.getActivityStatus(statusClazz);
	}

	/**
	 * 判断活动是否开放
	 *
	 * @param activityId
	 * @return
	 */
	public static boolean isActivityOpen(int activityId) {
		ActivityItem activityItem = ActivityCfg.getActivityItem(activityId);
		if (activityItem != null && activityItem.getActivityTimeType() != ActivityTimeType.CLOSED) {
			ActivityTimeCfg timeCfg = ActivityUtil.getCurActivityTimeCfg(activityId);
			if (timeCfg != null && !timeCfg.isEnd()) {
				return true;
			}
		}
		return false;
	}

	/**********************************************************************/

	public static Integer getCoinsMapRatio(Date registerDate) {
		int activityId = getRegisterCycleActivityId(registerDate);
		if (activityId > 0) {
			if (activityId == Const.ActivityId.COINS_MAP_DROP_RATIO_VALUE) {
				ActivityItem activityItem = ActivityCfg.getActivityItem(Const.ActivityId.COINS_MAP_DROP_RATIO_VALUE);
				if (activityItem != null) {
					return activityItem.getParam("ratio");
				}
			}
		} else {
			ActivityItem activityItem = ActivityManager.getInstance()
					.getActiveActivityItem(Const.ActivityId.COINS_MAP_DROP_RATIO_VALUE);
			if (activityItem != null) {
				return activityItem.getParam("ratio");
			}
		}

		return null;
	}

	public static float getCoinsMapRatio(int vipLevel) {
		float ratio = 1.0f;
		ActivityTimeCfg vipWalfare = getCurActivityTimeCfg(Const.ActivityId.VIP_WELFARE_VALUE);
		if (vipWalfare != null) {
			VipWelfareCfg cfg = ConfigManager.getInstance().getConfigByKey(VipWelfareCfg.class, vipLevel);
			if (cfg != null) {
				ratio = cfg.getRatioCoin();
			}
		}
		return ratio;
	}

	public static Integer getExpMapRatio(Date registerDate) {
		int activityId = getRegisterCycleActivityId(registerDate);
		if (activityId > 0) {
			if (activityId == Const.ActivityId.EXP_MAP_DROP_RATIO_VALUE) {
				ActivityItem activityItem = ActivityCfg.getActivityItem(Const.ActivityId.EXP_MAP_DROP_RATIO_VALUE);
				if (activityItem != null) {
					return activityItem.getParam("ratio");
				}
			}
		} else {
			ActivityItem activityItem = ActivityManager.getInstance()
					.getActiveActivityItem(Const.ActivityId.EXP_MAP_DROP_RATIO_VALUE);
			if (activityItem != null) {
				return activityItem.getParam("ratio");
			}
		}
		return null;
	}

	public static float getExpMapRatio(int vipLevel) {
		float ratio = 1.0f;
		ActivityTimeCfg vipWalfare = getCurActivityTimeCfg(Const.ActivityId.VIP_WELFARE_VALUE);
		if (vipWalfare != null) {
			VipWelfareCfg cfg = ConfigManager.getInstance().getConfigByKey(VipWelfareCfg.class, vipLevel);
			if (cfg != null) {
				ratio = cfg.getRatioExp();
			}
		}
		return ratio;
	}

	public static Integer getEquipDropActivity(Date registerDate) {
		int activityId = getRegisterCycleActivityId(registerDate);
		if (activityId > 0) {
			if (activityId == Const.ActivityId.EQUIP_MAP_DROP_RATIO_VALUE) {
				ActivityItem activityItem = ActivityCfg.getActivityItem(Const.ActivityId.EQUIP_MAP_DROP_RATIO_VALUE);
				if (activityItem != null) {
					return activityItem.getParam("ratio");
				}
			}
		} else {
			ActivityItem activityItem = ActivityManager.getInstance()
					.getActiveActivityItem(Const.ActivityId.EQUIP_MAP_DROP_RATIO_VALUE);
			if (activityItem != null) {
				return activityItem.getParam("ratio");
			}
		}
		return null;
	}

	public static Integer getQuickRewardActivity(Date registerDate) {
		int activityId = getRegisterCycleActivityId(registerDate);
		if (activityId > 0) {
			if (activityId == Const.ActivityId.QUICK_REWARD_RATIO_VALUE) {
				ActivityItem activityItem = ActivityCfg.getActivityItem(Const.ActivityId.QUICK_REWARD_RATIO_VALUE);
				if (activityItem != null) {
					return activityItem.getParam("ratio");
				}
			}
		} else {
			ActivityItem activityItem = ActivityManager.getInstance()
					.getActiveActivityItem(Const.ActivityId.QUICK_REWARD_RATIO_VALUE);
			if (activityItem != null) {
				return activityItem.getParam("ratio");
			}
		}
		return null;
	}

	public static Float getQuickCostActivity(Date registerDate) {
		int activityId = getRegisterCycleActivityId(registerDate);
		if (activityId > 0) {
			if (activityId == Const.ActivityId.QUICK_COST_RATIO_VALUE) {
				ActivityItem activityItem = ActivityCfg.getActivityItem(Const.ActivityId.QUICK_COST_RATIO_VALUE);
				if (activityItem != null) {
					return activityItem.getParam("ratio");
				}
			}
		} else {
			ActivityItem activityItem = ActivityManager.getInstance()
					.getActiveActivityItem(Const.ActivityId.QUICK_COST_RATIO_VALUE);
			if (activityItem != null) {
				return activityItem.getParam("ratio");
			}
		}
		return null;
	}

	public static Integer getSmeltValueActivity(Date registerDate) {
		int activityId = getRegisterCycleActivityId(registerDate);
		if (activityId > 0) {
			if (activityId == Const.ActivityId.SMELT_VALUE_RATIO_VALUE) {
				ActivityItem activityItem = ActivityCfg.getActivityItem(Const.ActivityId.SMELT_VALUE_RATIO_VALUE);
				if (activityItem != null) {
					return activityItem.getParam("ratio");
				}
			}
		} else {
			ActivityItem activityItem = ActivityManager.getInstance()
					.getActiveActivityItem(Const.ActivityId.SMELT_VALUE_RATIO_VALUE);
			if (activityItem != null) {
				return activityItem.getParam("ratio");
			}
		}
		return null;
	}

	public static Integer getEquipSellActivity(Date registerDate) {
		int activityId = getRegisterCycleActivityId(registerDate);
		if (activityId > 0) {
			if (activityId == Const.ActivityId.EQUIP_SELL_RATIO_VALUE) {
				ActivityItem activityItem = ActivityCfg.getActivityItem(Const.ActivityId.EQUIP_SELL_RATIO_VALUE);
				if (activityItem != null) {
					return activityItem.getParam("ratio");
				}
			}
		} else {
			ActivityItem activityItem = ActivityManager.getInstance()
					.getActiveActivityItem(Const.ActivityId.EQUIP_SELL_RATIO_VALUE);
			if (activityItem != null) {
				return activityItem.getParam("ratio");
			}
		}
		return null;
	}

	/**
	 * 获取月卡信息
	 *
	 * @param playerData
	 * @return
	 */
	public static MonthCardStatus getMonthCardStatus(PlayerData playerData) {
		@SuppressWarnings("unchecked")
		ActivityEntity<MonthCardStatus> activityEntity = (ActivityEntity<MonthCardStatus>) playerData
				.getActivityEntity(Const.ActivityId.MONTH_CARD_VALUE, 0);
		// 之前没有月卡信息
		if (activityEntity == null) {
			activityEntity = new ActivityEntity<MonthCardStatus>(playerData.getId(), Const.ActivityId.MONTH_CARD_VALUE,
					0);
			activityEntity.setActivityStatus(new MonthCardStatus());
			playerData.createActivity(activityEntity);
		}
		MonthCardStatus monthCardStatus = activityEntity.getActivityStatus(MonthCardStatus.class);
		return monthCardStatus;
	}

	/**
	 * 获取消耗型月卡信息
	 *
	 * @param playerData
	 * @return
	 */
	public static ConMonthCardStatus getConMonthCardStatus(PlayerData playerData) {
		@SuppressWarnings("unchecked")
		ActivityEntity<ConMonthCardStatus> activityEntity = (ActivityEntity<ConMonthCardStatus>) playerData
				.getActivityEntity(Const.ActivityId.CONSUME_MONTH_CARD_VALUE, 0);
		// 之前没有月卡信息
		if (activityEntity == null) {
			activityEntity = new ActivityEntity<ConMonthCardStatus>(playerData.getId(),
					Const.ActivityId.CONSUME_MONTH_CARD_VALUE, 0);
			activityEntity.setActivityStatus(new ConMonthCardStatus());
			playerData.createActivity(activityEntity);
		}
		ConMonthCardStatus monthCardStatus = activityEntity.getActivityStatus(ConMonthCardStatus.class);
		return monthCardStatus;
	}

	/**
	 * 获取成长基金信息
	 *
	 * @param playerData
	 * @return
	 */
	public static GrowthFundStatus getGrowthFundStatus(PlayerData playerData) {
		@SuppressWarnings("unchecked")
		ActivityEntity<GrowthFundStatus> activityEntity = (ActivityEntity<GrowthFundStatus>) playerData
				.getActivityEntity(Const.ActivityId.GROWTH_FUND_VALUE, 0);
		// 之前没有成长基金信息
		if (activityEntity == null) {
			activityEntity = new ActivityEntity<GrowthFundStatus>(playerData.getId(),
					Const.ActivityId.GROWTH_FUND_VALUE, 0);
			activityEntity.setActivityStatus(new GrowthFundStatus());
			playerData.createActivity(activityEntity);
		}
		GrowthFundStatus status = activityEntity.getActivityStatus(GrowthFundStatus.class);
		return status;
	}

	/**
	 * 获取周卡信息
	 *
	 * @param playerData
	 * @return
	 */
	public static NewWeekCardStatus getNewWeekCardStatus(PlayerData playerData) {
		@SuppressWarnings("unchecked")
		ActivityEntity<NewWeekCardStatus> activityEntity = (ActivityEntity<NewWeekCardStatus>) playerData
				.getActivityEntity(Const.ActivityId.NEW_WEEK_CARD_VALUE, 0);
		if (activityEntity == null) {
			activityEntity = new ActivityEntity<NewWeekCardStatus>(playerData.getId(),
					Const.ActivityId.NEW_WEEK_CARD_VALUE, 0);
			activityEntity.setActivityStatus(new NewWeekCardStatus());
			playerData.createActivity(activityEntity);
		}
		NewWeekCardStatus newWeekCardStatus = activityEntity.getActivityStatus(NewWeekCardStatus.class);
		return newWeekCardStatus;
	}

	/**
	 * 30天签到
	 *
	 * @param playerData
	 * @return
	 */
	public static AccLoginSignedStatus getAccLoginSignedStatus(PlayerData playerData) {
		@SuppressWarnings("unchecked")
		ActivityEntity<AccLoginSignedStatus> activityEntity = (ActivityEntity<AccLoginSignedStatus>) playerData
				.getActivityEntity(Const.ActivityId.ACCUMULATIVE_LOGIN_SIGNED_VALUE, -1);
		if (activityEntity == null) {
			activityEntity = new ActivityEntity<AccLoginSignedStatus>(playerData.getId(),
					Const.ActivityId.ACCUMULATIVE_LOGIN_SIGNED_VALUE, -1);
			activityEntity.setActivityStatus(new AccLoginSignedStatus());
			playerData.createActivity(activityEntity);
		}
		AccLoginSignedStatus accLoginSignedStatus = activityEntity.getActivityStatus(AccLoginSignedStatus.class);
		return accLoginSignedStatus;
	}

	/**
	 * 打折商品信息获取
	 */
	public static SalePacketStatus getSalePacketStatus(PlayerData playerData) {
		@SuppressWarnings("unchecked")
		ActivityEntity<SalePacketStatus> activityEntity = (ActivityEntity<SalePacketStatus>) playerData
				.getActivityEntity(Const.ActivityId.SALE_PACKET_VALUE, 0);
		// 生成折扣活动存储信息
		if (activityEntity == null) {
			activityEntity = new ActivityEntity<SalePacketStatus>(playerData.getId(),
					Const.ActivityId.SALE_PACKET_VALUE, 0);
			activityEntity.setActivityStatus(new SalePacketStatus());
			playerData.createActivity(activityEntity);
		}
		SalePacketStatus salePacketStatus = activityEntity.getActivityStatus(SalePacketStatus.class);
		return salePacketStatus;
	}

	/**
	 * 获取vip活动的数据库信息
	 */
	public static VipPackageStatus getVipPackageStatus(PlayerData playerData) {
		@SuppressWarnings("unchecked")
		ActivityEntity<VipPackageStatus> activityEntity = (ActivityEntity<VipPackageStatus>) playerData
				.getActivityEntity(Const.ActivityId.VIP_PACKAGE_VALUE, 0);
		// 生成折扣活动存储信息
		if (activityEntity == null) {
			activityEntity = new ActivityEntity<VipPackageStatus>(playerData.getId(),
					Const.ActivityId.VIP_PACKAGE_VALUE, 0);
			activityEntity.setActivityStatus(new VipPackageStatus());
			playerData.createActivity(activityEntity);
		}
		VipPackageStatus vipPacketStatus = activityEntity.getActivityStatus(VipPackageStatus.class);
		return vipPacketStatus;
	}

	/**
	 * 获取七夕兑换信息
	 *
	 * @param playerData
	 * @return
	 */
	public static ExchangeStatus getExchangeStatus(PlayerData playerData) {
		@SuppressWarnings("unchecked")
		ActivityEntity<ExchangeStatus> activityEntity = (ActivityEntity<ExchangeStatus>) playerData
				.getActivityEntity(Const.ActivityId.EXCHANGE_DOUBLE_SEVEN_VALUE, 0);
		// 生成折扣活动存储信息
		if (activityEntity == null) {
			activityEntity = new ActivityEntity<ExchangeStatus>(playerData.getId(),
					Const.ActivityId.EXCHANGE_DOUBLE_SEVEN_VALUE, 0);
			activityEntity.setActivityStatus(new ExchangeStatus());
			playerData.createActivity(activityEntity);
		}
		ExchangeStatus exchangeStatus = activityEntity.getActivityStatus(ExchangeStatus.class);
		return exchangeStatus;
	}

	/**
	 * 获取首冲翻倍信息
	 *
	 * @param playerData
	 * @return
	 */
	public static FirstRechargeStatus getFirstRechargeStatus(PlayerData playerData) {

		ActivityItem item = ActivityCfg.getActivityItem(Const.ActivityId.RECHARGE_RATIO_VALUE);
		if (item == null || item.getActivityTimeType() == ActivityTimeType.CLOSED) {
			return null;
		}
		int curStageId = 0;
		Map<String, Object> map = item.getParamsMap();
		Object activeStageId = map.get("activeStageId");
		if (null != activeStageId) {
			curStageId = (int) activeStageId;
		}
		@SuppressWarnings("unchecked")
		ActivityEntity<FirstRechargeStatus> activityEntity = (ActivityEntity<FirstRechargeStatus>) playerData
				.getActivityEntity(Const.ActivityId.RECHARGE_RATIO_VALUE, curStageId);
		// 之前没有首冲翻倍信息
		if (activityEntity == null) {
			activityEntity = new ActivityEntity<FirstRechargeStatus>(playerData.getId(),
					Const.ActivityId.RECHARGE_RATIO_VALUE, curStageId);
			activityEntity.setActivityStatus(new FirstRechargeStatus());
			playerData.createActivity(activityEntity);
		}
		FirstRechargeStatus firstRechargeStatus = activityEntity.getActivityStatus(FirstRechargeStatus.class);
		return firstRechargeStatus;
	}

	/**
	 * 计算开服活动剩余时间
	 *
	 * @param activityId
	 * @return
	 */
	public static int calcOpenServerActivitySurplusTime(int activityId) {
		ActivityItem activityItem = ActivityCfg.getActivityItem(activityId);
		if (activityItem == null || activityItem.getActivityTimeType() != GsConst.ActivityTimeType.SERVER_OPEN_DELYS) {
			return 0;
		}

		// 开服时间
		String serviceDate = GsConfig.getInstance().getServiceDate();
		// 活动持续时间
		long openTime = ((int) activityItem.getParam("openHours")) * 3600 * 1000;
		// 当前时间
		long curTime = GsApp.getInstance().getCurrentTime();
		Date openServerDate = GuaJiTime.DATE_FORMATOR_DAYNUM(serviceDate);
		long openServerTime = openServerDate.getTime();
		long activityCloseTime = openServerTime + openTime;
		int surplusTime = (int) ((activityCloseTime - curTime) / 1000);
		return Math.max(surplusTime, 0);
	}

	/**
	 * 计算注册时间内，新手周期活动的活动id
	 *
	 * @param registerDate
	 * @return
	 */
	public static int getRegisterCycleActivityId(Date registerDate) {
		ActivityItem activityItem = ActivityCfg.getActivityItem(Const.ActivityId.REGISTER_CYCLE_VALUE);
		int registerDays = calcReisterDates(registerDate);
		if (activityItem != null && registerDays > 0
				&& activityItem.getActivityTimeType() == GsConst.ActivityTimeType.REGISTER_CYCLE) {
			Integer activityId = activityItem.getParam("day" + registerDays);
			if (activityId != null) {
				return activityId;
			}
		}
		return 0;
	}

	/**
	 * 计算充值返利活动状态
	 *
	 * @param registerDate
	 * @return
	 */
	public static int calcRechargeRebateActivityStatus(Date registerDate) {
		ActivityItem activityItem = ActivityCfg.getActivityItem(Const.ActivityId.RECHARGE_REBATE_VALUE);
		// 计算玩家角色注册天数
		int registerDays = calcReisterDates(registerDate);
		if (registerDays > 0 && activityItem != null
				&& activityItem.getActivityTimeType() == GsConst.ActivityTimeType.REGISTER_CYCLE) {
			Integer delayDays = activityItem.getParam("delayDays");
			Integer rechargeDays = activityItem.getParam("rechargeDays");
			Integer rebateDays = activityItem.getParam("rebateDays");
			if (delayDays != null && rechargeDays != null && rebateDays != null) {
				rechargeDays += delayDays;
				rebateDays += rechargeDays;
				if (registerDays <= delayDays) {
					return GsConst.RechargeRebateActivity.STATUS_DELAY;
				}
				if (registerDays <= rechargeDays) {
					return GsConst.RechargeRebateActivity.STATUS_RECHARGE;
				}
				if (registerDays <= rebateDays) {
					return GsConst.RechargeRebateActivity.STATUS_REBATE;
				}
			}
		}
		return GsConst.RechargeRebateActivity.STATUS_CLOSE;
	}

	/**
	 * 打折礼包活动状态
	 */
	public static int calcSalePacketActivityStatus(Date registerDate, int mergeTime) {
		ActivityItem activityItem = ActivityCfg.getActivityItem(Const.ActivityId.SALE_PACKET_VALUE);

		Date curDate = GuaJiTime.getCalendar().getTime();
		long lMergeTime = (long) mergeTime * 1000;
		if (lMergeTime > registerDate.getTime()) {
			registerDate = new Date(lMergeTime);
		}

		if (registerDate.getTime() > curDate.getTime()) {
			return GsConst.SalePacketActivity.STATUS_CLOSE;
		}

		int registerDays = GuaJiTime.calcBetweenDays(registerDate, curDate) + 1;

		if (registerDays > 0 && activityItem != null
				&& activityItem.getActivityTimeType() == GsConst.ActivityTimeType.REGISTER_CYCLE) {
			Integer keepDays = activityItem.getParam("keepDays");

			if (keepDays != null) {
				if (registerDays <= keepDays) {
					return GsConst.SalePacketActivity.STATUS_OPEN;
				}
			}
		}

		return GsConst.SalePacketActivity.STATUS_CLOSE;

	}

	/**
	 * 连续充值活动状态
	 */
	public static int calcContinueRechargeActivityStatus(Date registerDate, int mergeTime) {
		ActivityItem activityItem = ActivityCfg.getActivityItem(Const.ActivityId.CONTINUE_RECHARGE_VALUE);

		Date curDate = GuaJiTime.getCalendar().getTime();
		long lMergeTime = (long) mergeTime * 1000;
		if (lMergeTime > registerDate.getTime()) {
			registerDate = new Date(lMergeTime);
		}

		if (registerDate.getTime() > curDate.getTime()) {
			return GsConst.ContinueRechargeActivity.STATUS_CLOSE;
		}

		int registerDays = GuaJiTime.calcBetweenDays(registerDate, curDate) + 1;

		if (registerDays > 0 && activityItem != null
				&& activityItem.getActivityTimeType() == GsConst.ActivityTimeType.REGISTER_CYCLE) {
			Integer keepDays = activityItem.getParam("keepDays");

			if (keepDays != null) {
				if (registerDays <= keepDays) {
					return GsConst.ContinueRechargeActivity.STATUS_OPEN;
				}
			}
		}

		return GsConst.ContinueRechargeActivity.STATUS_CLOSE;

	}

	/**
	 * 计算打折礼包活动剩余时间
	 *
	 * @param registerDate
	 * @return
	 */
	public static int clacSalePacketSurplusTime(Date registerDate, int mergeTime) {
		ActivityItem activityItem = ActivityCfg.getActivityItem(Const.ActivityId.SALE_PACKET_VALUE);

		Date curDate = GuaJiTime.getCalendar().getTime();

		long lMergeTime = (long) mergeTime * 1000;
		if (lMergeTime > registerDate.getTime()) {
			registerDate = new Date(lMergeTime);
		}

		if (registerDate.getTime() > curDate.getTime()) {
			return 0;
		}

		int registerDays = GuaJiTime.calcBetweenDays(registerDate, curDate) + 1;

		if (registerDays > 0 && activityItem != null) {

			Integer delayDays = activityItem.getParam("keepDays");

			if (delayDays != null) {

				Calendar calendar = GuaJiTime.getCalendar();
				calendar.setTime(registerDate);
				calendar.add(Calendar.DATE, delayDays);
				Date stopDate = calendar.getTime();
				int leftTime = (int) ((GuaJiTime.getAM0Date(stopDate).getTime() - GuaJiTime.getMillisecond()) / 1000);
				return Math.max(leftTime, 0);
			}
		}
		return 0;

	}

	/**
	 * 新手扭蛋到期时间
	 *
	 * @param registerDate
	 * @return
	 */
	public static int clacStrictEndTime(Date registerDate, int mergeTime) {
		ActivityItem activityItem = ActivityCfg.getActivityItem(Const.ActivityId.NEW_ND_VALUE);

		Date curDate = GuaJiTime.getCalendar().getTime();
		long lMergeTime = (long) mergeTime * 1000;
		if (lMergeTime > registerDate.getTime()) {
			registerDate = new Date(lMergeTime);
		}

		if (registerDate.getTime() > curDate.getTime()) {
			return 0;
		}

		int registerDays = GuaJiTime.calcBetweenDays(registerDate, curDate) + 1;

		if (registerDays > 0 && activityItem != null) {

			Integer delayDays = activityItem.getParam("keepDays");

			if (delayDays != null) {

				Calendar calendar = GuaJiTime.getCalendar();
				calendar.setTime(registerDate);
				calendar.add(Calendar.DATE, delayDays);
				Date stopDate = calendar.getTime();
				int leftTime = (int) (GuaJiTime.getAM0Date(stopDate).getTime() / 1000);
				return Math.max(leftTime, 0);
			}
		}
		return 0;

	}

	/**
	 * 计算打折礼包活动剩余时间
	 *
	 * @param registerDate
	 * @return
	 */
	public static int clacSevenDaySurplusTime(Date registerDate) {
		ActivityItem activityItem = ActivityCfg.getActivityItem(Const.ActivityId.ACCUMULATIVE_LOGIN_SEVEN_VALUE);

		Date curDate = GuaJiTime.getCalendar().getTime();

		if (registerDate.getTime() > curDate.getTime()) {
			return 0;
		}

		int registerDays = GuaJiTime.calcBetweenDays(registerDate, curDate) + 1;

		if (registerDays > 0 && activityItem != null) {

			Integer delayDays = SysBasicCfg.getInstance().getNewbieDays();

			if (delayDays != null) {

				Calendar calendar = GuaJiTime.getCalendar();
				calendar.setTime(registerDate);
				calendar.add(Calendar.DATE, delayDays);
				Date stopDate = calendar.getTime();
				int leftTime = (int) ((GuaJiTime.getAM0Date(stopDate).getTime() - GuaJiTime.getMillisecond()) / 1000);
				return Math.max(leftTime, 0);
			}
		}
		return 0;

	}
	
	/**
	 * 計算活動成就剩餘時間
	 *
	 * @param registerDate
	 * @return
	 */
	public static int clacQuestSurplusTime(Date registerDate) {
		ActivityItem activityItem = ActivityCfg.getActivityItem(Const.ActivityId.ACTIVITY156_ACHIEVE_FIGHTVALUE_VALUE);

		Date curDate = GuaJiTime.getCalendar().getTime();

		if (registerDate.getTime() > curDate.getTime()) {
			return 0;
		}

		int registerDays = GuaJiTime.calcBetweenDays(registerDate, curDate) + 1;

		if (registerDays > 0 && activityItem != null) {

			Integer delayDays = activityItem.getParam("keepDays");

			if (delayDays != null) {

				Calendar calendar = GuaJiTime.getCalendar();
				calendar.setTime(registerDate);
				calendar.add(Calendar.DATE, delayDays);
				Date stopDate = calendar.getTime();
				int leftTime = (int) ((GuaJiTime.getAM0Date(stopDate).getTime() - GuaJiTime.getMillisecond()) / 1000);
				return Math.max(leftTime, 0);
			}
		}
		return 0;

	}

	/**
	 * 计算连续充值（注册天数内）活动剩余时间 注：注册天数内的活动以后统一整理
	 *
	 * @param registerDate
	 * @return
	 */
	public static int clacContinueRechargeSurplusTime(Date registerDate, int mergeTime) {
		ActivityItem activityItem = ActivityCfg.getActivityItem(Const.ActivityId.CONTINUE_RECHARGE_VALUE);

		Date curDate = GuaJiTime.getCalendar().getTime();

		long lMergeTime = (long) mergeTime * 1000;
		if (lMergeTime > registerDate.getTime()) {
			registerDate = new Date(lMergeTime);
		}

		if (registerDate.getTime() > curDate.getTime()) {
			return 0;
		}

		int registerDays = GuaJiTime.calcBetweenDays(registerDate, curDate) + 1;

		if (registerDays > 0 && activityItem != null) {

			Integer delayDays = activityItem.getParam("keepDays");

			if (delayDays != null) {

				Calendar calendar = GuaJiTime.getCalendar();
				calendar.setTime(registerDate);
				calendar.add(Calendar.DATE, delayDays);
				Date stopDate = calendar.getTime();
				int leftTime = (int) ((GuaJiTime.getAM0Date(stopDate).getTime() - GuaJiTime.getMillisecond()) / 1000);
				return Math.max(leftTime, 0);
			}
		}
		return 0;

	}

	public static int clacContinueRechargeMoneySurplusTime() {
		int activityId = Const.ActivityId.CONTINUE_RECHARGE_MONEY_VALUE;
		ActivityItem activityItem = ActivityCfg.getActivityItem(activityId);
		if (activityItem == null)
			return 0;
		// 检测活动是否开放
		ActivityTimeCfg timeCfg = ActivityUtil.getCurActivityTimeCfg(activityId);
		if (timeCfg == null)
			return 0;

		Date curDate = GuaJiTime.getCalendar().getTime();
		if (curDate.getTime() > timeCfg.getlCloseTime())
			return 0;

		int leftTime = (int) ((timeCfg.getlCloseTime() - curDate.getTime()) / 1000);

		return leftTime;
	}

	/**
	 * 计算充值返利活动-充值状态剩余时间
	 */
	public static int clacRechargeSurplusTime(Date registerDate) {
		ActivityItem activityItem = ActivityCfg.getActivityItem(Const.ActivityId.RECHARGE_REBATE_VALUE);
		// 计算玩家角色注册天数
		int registerDays = calcReisterDates(registerDate);
		if (registerDays > 0 && activityItem != null) {
			Integer delayDays = activityItem.getParam("delayDays");
			Integer rechargeDays = activityItem.getParam("rechargeDays");
			Integer rebateDays = activityItem.getParam("rebateDays");

			if (delayDays != null && rechargeDays != null && rebateDays != null) {
				rechargeDays += delayDays;
				rebateDays += rechargeDays;
				if (registerDays > delayDays && registerDays <= rechargeDays) {
					Calendar calendar = GuaJiTime.getCalendar();
					calendar.setTime(registerDate);
					calendar.add(Calendar.DAY_OF_MONTH, rechargeDays);
					Date stopDate = calendar.getTime();
					int leftTime = (int) ((GuaJiTime.getAM0Date(stopDate).getTime() - GuaJiTime.getMillisecond())
							/ 1000);
					return Math.max(leftTime, 0);
				}
			}
		}
		return 0;
	}

	/**
	 * 计算充值返利活动-返利状态剩余天数
	 */
	public static int calcRebateLeftDays(Date registerDate) {
		ActivityItem activityItem = ActivityCfg.getActivityItem(Const.ActivityId.RECHARGE_REBATE_VALUE);
		// 计算玩家角色注册天数
		int registerDays = calcReisterDates(registerDate);
		if (registerDays > 0 && activityItem != null) {
			Integer delayDays = activityItem.getParam("delayDays");
			Integer rechargeDays = activityItem.getParam("rechargeDays");
			Integer rebateDays = activityItem.getParam("rebateDays");

			if (delayDays != null && rechargeDays != null && rebateDays != null) {
				rechargeDays += delayDays;
				rebateDays += rechargeDays;
				if (registerDays > rechargeDays && registerDays <= rebateDays) {
					return rebateDays - registerDays;
				}
			}
		}
		return 0;
	}

	/**
	 * 计算玩家角色注册天数
	 *
	 * @param registerDate
	 * @return
	 */
	public static int calcReisterDates(Date registerDate) {
		ActivityItem activityItem = ActivityCfg.getActivityItem(Const.ActivityId.REGISTER_CYCLE_VALUE);
		if (activityItem != null && activityItem.getActivityTimeType() == GsConst.ActivityTimeType.REGISTER_CYCLE) {
			Date curDate = GuaJiTime.getCalendar().getTime();
			if (registerDate.getTime() > curDate.getTime()) {
				return 0;
			}
			return GuaJiTime.calcBetweenDays(registerDate, curDate) + 1;
		}
		return 0;
	}

	/**
	 * 计算新用户许愿活动
	 *
	 * @param registerDate
	 * @return
	 */
	public static boolean calcWelfareRewardDates(Player player) {
		int activityId = ActivityId.WELFAREBYREGDATE_REWARD_VALUE;
		ActivityItem activityItem = ActivityCfg.getActivityItem(activityId);
		ActivityTimeCfg timeCfg = ActivityUtil.getCurActivityTimeCfg(activityId);
		if (activityItem != null && timeCfg != null
				&& activityItem.getActivityTimeType() == GsConst.ActivityTimeType.REGISTER_CYCLE) {
			Date registerDate = player.getPlayerData().getPlayerEntity().getCreateTime();
			Integer keepDays = activityItem.getParam("keepDays");
			WelfareRewardStatusByRegDate status = ActivityUtil.getActivityStatus(player.getPlayerData(), activityId,
					timeCfg.getStageId(), WelfareRewardStatusByRegDate.class);
			return status.isCanPlayByDate(registerDate.getTime(), timeCfg.getlStartTime(), keepDays,
					player.getPlayerData().getPlayerEntity().getMergeTime());
		}
		return false;
	}
	/**
	 * 檢查檢查階段禮包是不是領完
	 */
	public static boolean checkStepGifeOver(Player player){
		int activityId = ActivityId.ACTIVITY179_Step_Gift_VALUE;
		ActivityItem activityItem = ActivityCfg.getActivityItem(activityId);
		ActivityTimeCfg timeCfg = ActivityUtil.getCurActivityTimeCfg(activityId);
		if (activityItem != null && timeCfg != null) {
			Activity179Status status = ActivityUtil.getActivityStatus(player.getPlayerData(), activityId, timeCfg.getStageId(), Activity179Status.class);
			if (status != null) {
				if (status.getTakeGoodsId() < ReleaseStepGiftCfg.getMaxGoodsId()) {
					return false;
				}
			}
		} 
		return true;
	}

	/**
	 * 计算新玩家及老玩家活动剩余时间
	 *
	 * @param registerTime
	 * @param activityStartTime
	 * @param activityDays
	 * @return
	 */
	public static int getLeftTimeByRegister(long registerTime, long activityStartTime, int activityDays,
			int mergeTime) {
		int leftTime = 0;
		long currentTime = System.currentTimeMillis();

		long lMergeTime = (long) mergeTime * 1000;
		if (lMergeTime > registerTime) {
			registerTime = lMergeTime;
		}

		if (registerTime < activityStartTime) {
			long playerActivityEndTime = activityStartTime + 1000 * 60 * 60 * 24 * 3;
			leftTime = playerActivityEndTime > currentTime ? (int) ((playerActivityEndTime - currentTime) / 1000) : 0;
		} else {
			Integer interval = 1000 * 60 * 60 * 24 * activityDays;
			long playerActivityEndTime = registerTime + interval;
			leftTime = playerActivityEndTime > currentTime ? (int) ((playerActivityEndTime - currentTime) / 1000) : 0;
		}
		return leftTime;
	}

	/**
	 * 计算玩家角色新手扭蛋活动
	 *
	 * @param registerDate
	 * @return
	 */
	public static boolean calcNewNdReisterDates(Date registerDate, int mergeTime) {
		long lMergeTime = (long) mergeTime * 1000;
		if (lMergeTime > registerDate.getTime()) {
			registerDate = new Date(lMergeTime);
		}

		ActivityItem activityItem = ActivityCfg.getActivityItem(Const.ActivityId.NEW_ND_VALUE);
		if (activityItem != null && activityItem.getActivityTimeType() == GsConst.ActivityTimeType.REGISTER_CYCLE) {
			Date curDate = GuaJiTime.getCalendar().getTime();
			int registerDays = GuaJiTime.calcBetweenDays(registerDate, curDate) + 1;

			Integer keepDays = activityItem.getParam("keepDays");

			if (keepDays != null) {
				if (registerDays <= keepDays) {
					return true;
				}
			}

		}
		return false;
	}

	/**
	 * 通过排名获取远征物资排行榜奖励配置
	 *
	 * @return
	 */
	public static ExpeditionArmoryRankingCfg getExpeditionArmoryRankingCfgByRank(int rank) {
		TreeMap<Object, ExpeditionArmoryRankingCfg> cfgMap = (TreeMap<Object, ExpeditionArmoryRankingCfg>) ConfigManager
				.getInstance().getConfigMap(ExpeditionArmoryRankingCfg.class);
		for (Map.Entry<Object, ExpeditionArmoryRankingCfg> entry : cfgMap.entrySet()) {
			ExpeditionArmoryRankingCfg cfg = entry.getValue();
			if (rank <= cfg.getMinRank())
				return cfg;
		}
		return null;
	}

	/********************************************************************************************/

	/**
	 * 检查是否触发月卡,暂时关闭by callan
	 *
	 * @return
	 */

	public static boolean triggerMonthCard(PlayerData playerData, int goodsId, float costMoney) {
		/*
		 * MonthCardStatus monthCardStatus = getMonthCardStatus(playerData);
		 * if(monthCardStatus == null) { return false; } MonthCardCfg monthCardCfg =
		 * MonthCardCfg.getMonthCardCfgByGoodsId(goodsId); boolean isBuyMonthCard =
		 * false; if(monthCardCfg != null) { //是月卡配置,直接加月卡 isBuyMonthCard = true; } else
		 * if(monthCardStatus.getPrepareBuyTime() != null) {
		 * if(GuaJiTime.getMillisecond() - monthCardStatus.getPrepareBuyTime().getTime()
		 * <= 60000) { //直接转化 monthCardCfg =
		 * MonthCardCfg.getMonthCardCfgByMoney(costMoney); if(monthCardCfg != null) {
		 * isBuyMonthCard = true; monthCardStatus.setPrepareBuyTime(null); } } }
		 *
		 * if(isBuyMonthCard) { monthCardStatus.refresh(); Date lastEndDate =
		 * monthCardStatus.getLastEndTime();
		 * monthCardStatus.addMonthCard(monthCardCfg.getId()); //更新活动信息
		 * playerData.updateActivity(ActivityId.MONTH_CARD_VALUE,0); AwardItems
		 * awardItems = AwardItems.valueOf(monthCardCfg.getReward()); Calendar calendar
		 * = GuaJiTime.getCalendar(); if(lastEndDate != null) {
		 * calendar.setTime(lastEndDate); } calendar.set(Calendar.HOUR_OF_DAY,0);
		 * calendar.set(Calendar.MINUTE,0); calendar.set(Calendar.SECOND,0);
		 * calendar.set(Calendar.MILLISECOND,0); //造奖励邮件 for(int
		 * i=0;i<monthCardCfg.getDays();i++) { calendar.add(Calendar.DAY_OF_YEAR, i);
		 * MailManager.createMail(playerData.getId(), Mail.MailType.Reward_VALUE,
		 * GsConst.MailId.MONTH_CARD_REWARD, calendar.getTime(), "", awardItems,
		 * GsConst.EmailClassification.COMMON, String.valueOf(monthCardCfg.getDays() - i
		 * -1)); calendar.add(Calendar.DAY_OF_YEAR, 0 - i); }
		 *
		 * Msg noticeMonthcardMsg = Msg.valueOf(GsConst.MsgType.MONTH_CARD_SUC,
		 * GuaJiXID.valueOf(GsConst.ObjType.PLAYER, playerData.getId()));
		 * noticeMonthcardMsg.pushParam(monthCardStatus);
		 * GsApp.getInstance().postMsg(noticeMonthcardMsg); return true; }
		 */
		return false;
	}

	/**
	 * 增加玩家每日累计充值
	 *
	 * @param playerData
	 * @param amount
	 * @return
	 */
	public static boolean addPlayerAccRecharge(int amount, PlayerData playerData) {
		ActivityTimeCfg accRechargeTimeCfg = getCurActivityTimeCfg(Const.ActivityId.ACCUMULATIVE_RECHARGE_VALUE);
		if (accRechargeTimeCfg != null && (!accRechargeTimeCfg.isEnd())) {
			int stageId = accRechargeTimeCfg.getStageId();
			int activityId = accRechargeTimeCfg.getActivityId();
			AccRechargeStatus status = ActivityUtil.getActivityStatus(playerData, activityId, stageId,
					AccRechargeStatus.class);
			status.addAccRechargeAmount(amount);
			playerData.updateActivity(Const.ActivityId.ACCUMULATIVE_RECHARGE_VALUE, stageId, true);
		}

		ActivityTimeCfg fortuneTimeCfg = getCurActivityTimeCfg(Const.ActivityId.FORTUNE_VALUE);
		if (fortuneTimeCfg != null && !fortuneTimeCfg.isEnd()) {
			int stageId = fortuneTimeCfg.getStageId();
			int activityId = fortuneTimeCfg.getActivityId();
			FortuneStatus status = ActivityUtil.getActivityStatus(playerData, activityId, stageId, FortuneStatus.class);
			status.addRecharge(amount);
			playerData.updateActivity(Const.ActivityId.FORTUNE_VALUE, stageId, true);
		}
		return true;
	}

	public static boolean addPlayerAccRecharge(float payMoney, PlayerData playerData) {
		ActivityTimeCfg accRechargeTimeCfg = getCurActivityTimeCfg(Const.ActivityId.ACCUMULATIVE_RECHARGE_VALUE);
		if (accRechargeTimeCfg != null && (!accRechargeTimeCfg.isEnd())) {
			int stageId = accRechargeTimeCfg.getStageId();
			int activityId = accRechargeTimeCfg.getActivityId();
			AccRechargeStatus status = ActivityUtil.getActivityStatus(playerData, activityId, stageId,
					AccRechargeStatus.class);
			status.addAccRechargeAmount((int) payMoney);
			playerData.updateActivity(Const.ActivityId.ACCUMULATIVE_RECHARGE_VALUE, stageId, true);
		}
		return true;
	}

	/**
	 * 检查并触发首冲翻倍
	 *
	 * @return addGold
	 */
	public static int triggerFirstRecharge(PlayerData playerData, int goodsId, int amout) {
		if (playerData == null) {
			return 0;
		}
		FirstRechargeStatus firstRechargeStatus = getFirstRechargeStatus(playerData);
		if (firstRechargeStatus != null) {
			if (firstRechargeStatus.getRecharge(goodsId) > 0) {
				// 已经充值
				return 0;
			} else {
				RechargeRatioCfg rechargeRatioCfg = ConfigManager.getInstance().getConfigByKey(RechargeRatioCfg.class,
						goodsId);
				if (rechargeRatioCfg == null) {
					return 0;
				} else {

					int curStageId = 0;
					firstRechargeStatus.setRecharge(goodsId, amout);
					ActivityItem item = ActivityCfg.getActivityItem(Const.ActivityId.RECHARGE_RATIO_VALUE);
					if (item != null && item.getActivityTimeType() != ActivityTimeType.CLOSED) {
						Map<String, Object> map = item.getParamsMap();
						Object activeStageId = map.get("activeStageId");
						if (null != activeStageId) {
							curStageId = (int) activeStageId;
						}
					}

					playerData.updateActivity(Const.ActivityId.RECHARGE_RATIO_VALUE, curStageId, true);
					return amout * rechargeRatioCfg.getRatio(playerData);
				}
			}
		}
		return 0;
	}
	/**
	 * 是否有首除兩倍資格
	 * @param playerData
	 * @param goodsId
	 * @return
	 */
	public static boolean isFirstRecharge(PlayerData playerData, int goodsId) {
		if (playerData == null) {
			return false;
		}
		
		FirstRechargeStatus firstRechargeStatus = getFirstRechargeStatus(playerData);
		if (firstRechargeStatus != null) {
			if (firstRechargeStatus.getRecharge(goodsId) > 0) {
				// 已经充值
				return false;
			} else {
				RechargeRatioCfg rechargeRatioCfg = ConfigManager.getInstance().getConfigByKey(RechargeRatioCfg.class,
						goodsId);
				if (rechargeRatioCfg == null) {
					return false;
				} else {
					return true;
				}
			}
		}
		return false;
	}

	public static int calcActivity124Return(PlayerData playerData, int GoldCount) {
		int returnGold = 0;
		try {
			if (playerData == null) {
				return returnGold;
			}
			// 检测活动是否开放
			int activityId = Const.ActivityId.ACTIVITY124_RECHARGE_RETURN_VALUE;
			ActivityTimeCfg activityTimeCfg = ActivityUtil.getCurActivityTimeCfg(activityId);
			if (activityTimeCfg == null) {
				return returnGold;
			}
			// 剩余时间
			int leftTime = activityTimeCfg.calcActivitySurplusTime();
			if (leftTime <= 0) {
				// 活动已关闭
				return returnGold;
			}
			// 是否已经抽奖过
			Activity124Status status = ActivityUtil.getActivityStatus(playerData, activityId,
					activityTimeCfg.getStageId(), Activity124Status.class);
			if (status.getGotTicket() && !status.getUsed()) {
				int ticketId = status.getTicketId();
				RechargeReturnLotteryCfg cfg = null;
				Map<Object, RechargeReturnLotteryCfg> configMap = ConfigManager.getInstance()
						.getConfigMap(RechargeReturnLotteryCfg.class);
				for (RechargeReturnLotteryCfg item : configMap.values()) {
					if (ticketId == item.getId()) {
						cfg = item;
					}
				}
				if (cfg != null) {
					returnGold = (int) Math.ceil(GoldCount * cfg.getAwards() * 0.01);
					status.setUsed(true);
					// 保存玩家数据
					playerData.updateActivity(activityId, activityTimeCfg.getStageId(), true);
					return returnGold;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return returnGold;
	}

	/**
	 * 获取充值返利抽奖活动彩票使用状态
	 */
	public static void notifyActivity124Info(Player player) {
		try {
			if (player == null || player.getPlayerData() == null) {
				return;
			}
			// 检测活动是否开放
			int activityId = Const.ActivityId.ACTIVITY124_RECHARGE_RETURN_VALUE;
			ActivityTimeCfg activityTimeCfg = ActivityUtil.getCurActivityTimeCfg(activityId);
			if (activityTimeCfg == null) {
				return;
			}
			// 剩余时间
			int leftTime = activityTimeCfg.calcActivitySurplusTime();
			if (leftTime <= 0) {
				// 活动已关闭
				return;
			}
			// 是否已经抽奖过
			Activity124Status status = ActivityUtil.getActivityStatus(player.getPlayerData(), activityId,
					activityTimeCfg.getStageId(), Activity124Status.class);
			Activity3.Activity124InfoRep.Builder builder = Activity3.Activity124InfoRep.newBuilder();
			builder.setLefttime(leftTime);
			builder.setCount(status.getCount());
			if (status.getGotTicket()) {
				builder.setType(status.getTicketId());
				builder.setIsUsed(status.getUsed());
			}
			player.sendProtocol(Protocol.valueOf(HP.code.ACTIVITY124_RECHARGE_RETURN_INFO_S_VALUE, builder));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 检查并触发充值双倍活动
	 *
	 * @return addGold
	 */
	public static int triggerDoubleRecharge(PlayerData playerData, int goodsId, int amout) {
		if (playerData == null) {
			return 0;
		}

		// 检测活动是否开放
		int activityId = Const.ActivityId.DOUBLE_RECHARGE_VALUE;
		ActivityTimeCfg timeCfg = ActivityUtil.getCurActivityTimeCfg(activityId);
		if (timeCfg == null || timeCfg.isEnd()) {
			return 0;
		}

		DoubleRechargeStatus doubleRechargeStatus = getActivityStatus(playerData, activityId, timeCfg.getStageId(),
				DoubleRechargeStatus.class);
		if (doubleRechargeStatus != null) {
			if (!doubleRechargeStatus.canTrigger(goodsId)) {
				// 已经被触发过
				doubleRechargeStatus.setRecharge(goodsId, amout);
				playerData.updateActivity(Const.ActivityId.DOUBLE_RECHARGE_VALUE, timeCfg.getStageId(), true);
				return 0;
			} else {
				DoubleRechargeCfg rechargeRatioCfg = ConfigManager.getInstance().getConfigByKey(DoubleRechargeCfg.class,
						goodsId);
				if (rechargeRatioCfg == null) {
					return 0;
				} else {
					doubleRechargeStatus.setRecharge(goodsId, amout);
					playerData.updateActivity(Const.ActivityId.DOUBLE_RECHARGE_VALUE, timeCfg.getStageId(), true);
					return amout * rechargeRatioCfg.getRatio();
				}
			}
		}
		return 0;
	}
	
	/**
	 * 是否有充值双倍資格
	 * @param playerData
	 * @param goodsId
	 * @return
	 */
	
	public static boolean isDoubleRecharge(PlayerData playerData, int goodsId) {
		if (playerData == null) {
			return false;
		}

		// 检测活动是否开放
		int activityId = Const.ActivityId.DOUBLE_RECHARGE_VALUE;
		ActivityTimeCfg timeCfg = ActivityUtil.getCurActivityTimeCfg(activityId);
		if (timeCfg == null || timeCfg.isEnd()) {
			return false;
		}
		
		DoubleRechargeStatus doubleRechargeStatus = getActivityStatus(playerData, activityId, timeCfg.getStageId(),
				DoubleRechargeStatus.class);
		if (doubleRechargeStatus != null) {
			if (!doubleRechargeStatus.canTrigger(goodsId)) {
				// 已经被触发过
				return false;
			} else {
				DoubleRechargeCfg rechargeRatioCfg = ConfigManager.getInstance().getConfigByKey(DoubleRechargeCfg.class,
						goodsId);
				if (rechargeRatioCfg == null) {
					return false;
				} else {
					return true;
				}
			}
		}

		return false;
	}

	/**
	 * 增加连续充值天数
	 *
	 * @param playerData
	 */
	public static void addPlayerRechargeDays(PlayerData playerData) {
		ActivityTimeCfg continueRechargeTimeCfg = getCurActivityTimeCfg(Const.ActivityId.CONTINUE_RECHARGE_VALUE);
		if (continueRechargeTimeCfg != null && (!continueRechargeTimeCfg.isEnd())) {
			int stageId = continueRechargeTimeCfg.getStageId();
			int activityId = continueRechargeTimeCfg.getActivityId();
			ContinueRechargeStatus status = getActivityStatus(playerData, activityId, stageId,
					ContinueRechargeStatus.class);
			if (status.addContinueRechargeDays()) {
				GuaJiXID targetXID = GuaJiXID.valueOf(GsConst.ObjType.PLAYER, playerData.getId());
				Msg msg = Msg.valueOf(GsConst.MsgType.RECHARGE_DAYS_INCREASE, targetXID);
				GsApp.getInstance().postMsg(msg);
			}
			playerData.updateActivity(Const.ActivityId.CONTINUE_RECHARGE_VALUE, stageId, true);
		}

		// 非新手累积充值
		ActivityTimeCfg continueRecharge131TimeCfg = getCurActivityTimeCfg(Const.ActivityId.CONTINUE_RECHARGE131_VALUE);
		if (continueRecharge131TimeCfg != null && (!continueRecharge131TimeCfg.isEnd())) {
			int stageId = continueRecharge131TimeCfg.getStageId();
			int activityId = continueRecharge131TimeCfg.getActivityId();
			ContinueRechargeDays131Status status = getActivityStatus(playerData, activityId, stageId,
					ContinueRechargeDays131Status.class);
			if (status != null) {
				if (status.addContinueRechargeDays()) {
					playerData.updateActivity(activityId, stageId, true);
				}
			}
		}
	}

	/**
	 * @param playerData
	 * @param nMoney     充值金额
	 */
	public static void addPalyerRechargeMoney(PlayerData playerData, float nMoney) {
		int activityId = Const.ActivityId.CONTINUE_RECHARGE_MONEY_VALUE;
		ActivityTimeCfg continueRechargeTimeCfg = getCurActivityTimeCfg(activityId);
		if (continueRechargeTimeCfg != null && (!continueRechargeTimeCfg.isEnd())) {
			int stageId = continueRechargeTimeCfg.getStageId();
			ContinueMoneyRechargeStatus status = getActivityStatus(playerData, activityId, stageId,
					ContinueMoneyRechargeStatus.class);
			if (status != null) {
				status.addContinueRechargeMoney((int) nMoney);
				playerData.updateActivity(activityId, stageId, true);
			}

		}
		addRechargeBounce192(GsConst.RechargeBounceType.Deposit,(int)nMoney,playerData);
	}
	
	/**
	 * 累積活動VIP點數
	 * @param playerData
	 * @param point
	 */
	public static void addPalyerVipPoint(PlayerData playerData, int point) {
		int activityId = Const.ActivityId.ACTIVITY159_VIP_POINT_VALUE;
		ActivityTimeCfg TimeCfg = getCurActivityTimeCfg(activityId);
		if (TimeCfg != null && (!TimeCfg.isEnd())) {
			int stageId = TimeCfg.getStageId();
			Activity159Status status = getActivityStatus(playerData, activityId, stageId,
					Activity159Status.class);
			if (status != null) {
				status.addVIPPoint(point);
				playerData.updateActivity(activityId, stageId, true);
			}
		}
		
		activityId = Const.ActivityId.ACTIVITY160_NP_CONTINUE_RECHARGE_VALUE;
		TimeCfg = getCurActivityTimeCfg(activityId);
		if (TimeCfg != null && (!TimeCfg.isEnd())) {
			int stageId = TimeCfg.getStageId();
			Activity160Status status = getActivityStatus(playerData, activityId, stageId,
					Activity160Status.class);
			if (status != null) {
				status.addContinueRechargeMoney(point);
				playerData.updateActivity(activityId, stageId, true);
			}
		}
	}

	/**
	 * 增加累计消费数额
	 *
	 * @param playerData
	 * @param amount
	 */
	public static void addAccConsumeGold(PlayerData playerData, int amount) {
		ActivityTimeCfg accConsumeTimeCfg = getCurActivityTimeCfg(Const.ActivityId.ACCUMULATIVE_CONSUME_VALUE);
		if (accConsumeTimeCfg != null && (!accConsumeTimeCfg.isEnd())) {
			int stageId = accConsumeTimeCfg.getStageId();
			int activityId = accConsumeTimeCfg.getActivityId();
			AccConsumeStatus status = getActivityStatus(playerData, activityId, stageId, AccConsumeStatus.class);
			status.addAccConsumeAmount(amount);

			GuaJiXID targetXID = GuaJiXID.valueOf(GsConst.ObjType.PLAYER, playerData.getId());
			Msg msg = Msg.valueOf(GsConst.MsgType.ACC_CONSUME_INCREASE, targetXID);
			GsApp.getInstance().postMsg(msg);

			playerData.updateActivity(Const.ActivityId.ACCUMULATIVE_CONSUME_VALUE, stageId);
		}

		ActivityTimeCfg activityTimeCfg = getCurActivityTimeCfg(Const.ActivityId.COMMENDATION_TRIBE_VALUE);
		if (activityTimeCfg != null && (!activityTimeCfg.isEnd())) {
			int stageId = activityTimeCfg.getStageId();
			int activityId = activityTimeCfg.getActivityId();
			CommendationTribeStatus status = getActivityStatus(playerData, activityId, stageId,
					CommendationTribeStatus.class);
			status.addGoldCost(amount);
			playerData.updateActivity(Const.ActivityId.COMMENDATION_TRIBE_VALUE, stageId, true);
		}
		addRechargeBounce192(GsConst.RechargeBounceType.consums,(int)amount,playerData);
	}
	/**
	 * 	增加累计消费次數
	 * @param playerData
	 */
	public static void addAccConsume(PlayerData playerData) {
		ActivityTimeCfg accConsumeTimeCfg = getCurActivityTimeCfg(Const.ActivityId.ACTIVITY190_StepSummom_VALUE);
		if (accConsumeTimeCfg != null && (!accConsumeTimeCfg.isEnd())) {
			int stageId = accConsumeTimeCfg.getStageId();
			int activityId = accConsumeTimeCfg.getActivityId();
			Activity190Status status = getActivityStatus(playerData, activityId, stageId, Activity190Status.class);
			status.addCount();
			playerData.updateActivity(activityId, stageId);
		}
	}

	/**
	 * @param playerData 玩家数据
	 * @param activityId 活动编号
	 * @param nTimes     购买时间
	 */
	public static void addAccConsumeItemTimes(PlayerData playerData, int activityId, int nTimes) {
		ActivityTimeCfg accConsumeTimeCfg = getCurActivityTimeCfg(activityId);
		if (accConsumeTimeCfg != null && (!accConsumeTimeCfg.isEnd())) {
			int stageId = accConsumeTimeCfg.getStageId();
			int actId = accConsumeTimeCfg.getActivityId();
			//AccConItemStatus status = getActivityStatus(playerData, actId, stageId, AccConItemStatus.class);
			// status.addAccConsumeTimes(nTimes);
			GuaJiXID targetXID = GuaJiXID.valueOf(GsConst.ObjType.PLAYER, playerData.getId());
			Msg msg = Msg.valueOf(GsConst.MsgType.ACC_CONSUME_INCREASE, targetXID);
			GsApp.getInstance().postMsg(msg);

			playerData.updateActivity(actId, stageId);
		}

	}

	/**
	 * 触发每日单笔充值活动
	 *
	 * @param playerData
	 */
	public static boolean addPlayerSingleRecharge(PlayerData playerData, int rechargeAmout) {

		ActivityTimeCfg singleRechargeTimeCfg = getCurActivityTimeCfg(Const.ActivityId.SINGLE_RECHARGE_VALUE);
		if (null == singleRechargeTimeCfg)
			return false;
		if (singleRechargeTimeCfg != null && (!singleRechargeTimeCfg.isEnd())) {
			int stageId = singleRechargeTimeCfg.getStageId();
			int activityId = singleRechargeTimeCfg.getActivityId();
			SingleRechargeStatus status = ActivityUtil.getActivityStatus(playerData, activityId, stageId,
					SingleRechargeStatus.class);

			int cfgId = 0;
			Map<Object, SingleRechargeCfg> cfgs = ConfigManager.getInstance().getConfigMap(SingleRechargeCfg.class);
			for (Map.Entry<Object, SingleRechargeCfg> entry : cfgs.entrySet()) {
				SingleRechargeCfg cfg = entry.getValue();
				if (cfg.getSingleRecharge() == rechargeAmout) {
					cfgId = cfg.getId();
					break;
				}
			}

			// 只是添加了可以领奖的
			if (cfgId != 0) {
				int date = Integer.valueOf(GuaJiTime.DATE_FORMATOR_DAYNUM(GuaJiTime.getAM0Date()));
				status.addAwardData(cfgId, date);
				// 将可以领奖的更新到status
				playerData.updateActivity(Const.ActivityId.SINGLE_RECHARGE_VALUE, stageId, true);
			}
		}
		return true;
	}

	/**
	 * 充值返利活动
	 *
	 * @param playerData
	 */
	public static boolean addRechargeRebateAmount(PlayerData playerData, int rechargeAmout) {
		Date registerDate = playerData.getPlayerEntity().getCreateTime();
		if (calcRechargeRebateActivityStatus(registerDate) == GsConst.RechargeRebateActivity.STATUS_RECHARGE) {
			RechargeRebateStatus status = ActivityUtil.getActivityStatus(playerData,
					Const.ActivityId.RECHARGE_REBATE_VALUE, -1, RechargeRebateStatus.class);
			status.addAccRechargeAmount(rechargeAmout);
			playerData.updateActivity(Const.ActivityId.RECHARGE_REBATE_VALUE, -1);
		}

		ActivityTimeCfg timeCfg = ActivityUtil.getCurActivityTimeCfg(Const.ActivityId.RECHARGE_REBATE2_VALUE);
		if (timeCfg != null && !timeCfg.isEnd()) {
			RechargeRebateStatus status = ActivityUtil.getActivityStatus(playerData,
					Const.ActivityId.RECHARGE_REBATE2_VALUE, timeCfg.getStageId(), RechargeRebateStatus.class);
			status.addAccRechargeAmount(rechargeAmout);
			playerData.updateActivity(Const.ActivityId.RECHARGE_REBATE2_VALUE, timeCfg.getStageId(), true);
		}
		return true;
	}

	/**
	 * 检查并触发周卡
	 *
	 * @param playerData
	 * @param goodsId
	 * @param costMoney
	 * @return
	 */
	public static boolean triggerWeekCard(PlayerData playerData, int goodsId, float costMoney) {
		int activityId = Const.ActivityId.WEEK_CARD_VALUE;
		ActivityItem activityItem = ActivityManager.getInstance().getActiveActivityItem(activityId);
		if (activityItem == null) {
			return false;
		}
		ActivityTimeCfg weekActivityTimeCfg = ActivityUtil.getCurActivityTimeCfg(activityId);
		if (weekActivityTimeCfg == null || weekActivityTimeCfg.isEnd()) {
			return false;
		}

		WeekCardStatus weekCardStatus = getActivityStatus(playerData, activityId, weekActivityTimeCfg.getStageId(),
				WeekCardStatus.class);
		if (weekCardStatus == null) {
			return false;
		}

		WeekCardCfg weekCardCfg = WeekCardCfg.getWeekCardCfgByGoodsId(goodsId);
		if (weekCardCfg == null) {
			return false;
		}

		boolean isLevelUp = weekCardStatus.canLevelUp(goodsId);
		if (weekCardStatus.isBetweenReward() && !isLevelUp) {
			// 已有周卡并且升级失败
			return false;
		}

		if (isLevelUp) {
			boolean isRewardToday = weekCardStatus.isRewardToday();
			// 今天领过奖励后, 周卡即使升级也不能再领
			weekCardStatus.resetlevelUpData();
			if (isRewardToday) {
				weekCardStatus.putRewardToday();
			}
		}

		GuaJiXID targetXID = GuaJiXID.valueOf(GsConst.ObjType.PLAYER, playerData.getId());
		Msg noticeWeekcardMsg = Msg.valueOf(GsConst.MsgType.WEEK_CARD_SUC, targetXID);
		noticeWeekcardMsg.pushParam(weekCardStatus);
		GsApp.getInstance().postMsg(noticeWeekcardMsg);
		return true;
	}

	/**
	 * 检查并触发周卡
	 *
	 * @param playerData
	 * @param goodsId
	 * @param costMoney
	 * @return
	 */
	public static boolean triggerConsumeWeekCard(PlayerData playerData, int goodsId, float costMoney) {
		int activityId = Const.ActivityId.CONSUME_WEEK_CARD_VALUE;
		ActivityItem activityItem = ActivityManager.getInstance().getActiveActivityItem(activityId);
		if (activityItem == null) {
			return false;
		}
		ActivityTimeCfg weekActivityTimeCfg = ActivityUtil.getCurActivityTimeCfg(activityId);
		if (weekActivityTimeCfg == null || weekActivityTimeCfg.isEnd()) {
			return false;
		}

		ConWeekCardStatus weekCardStatus = getActivityStatus(playerData, activityId, weekActivityTimeCfg.getStageId(),
				ConWeekCardStatus.class);
		if (weekCardStatus == null) {
			return false;
		}

		ConsumeWeekCardCfg weekCardCfg = ConsumeWeekCardCfg.getWeekCardCfgByGoodsId(goodsId);
		if (weekCardCfg == null) {
			return false;
		}

		// 更新活动信息
		weekCardStatus.setCurrentActiveCfgId(weekCardCfg.getId());
		weekCardStatus.setStartDate(GuaJiTime.getCalendar().getTime());

		weekCardStatus.clearRewardMap();
	
		// 購買禮
		if ( playerData.getPlayer() != null) { 
			String info = weekCardCfg.getBuyReward();
			AwardItems awardItems = AwardItems.valueOf(info);
			awardItems.rewardTakeAffectAndPush(playerData.getPlayer(), Action.CONSUME_WEEK_CARD_DAILY_REWARD,2,TapDBSource.Recharge,Params.valueOf("goodsIs",goodsId));
			playerData.setLastRecharage(info);
		}
		
		playerData.updateActivity(ActivityId.CONSUME_WEEK_CARD_VALUE, weekActivityTimeCfg.getStageId(), true);

		MailManager.createSysMail(playerData.getId(), Mail.MailType.Normal_VALUE, GsConst.MailId.CONSUME_WEEK_CARD_MAIL,
				"", null, GuaJiTime.getTimeString());

		GuaJiXID targetXID = GuaJiXID.valueOf(GsConst.ObjType.PLAYER, playerData.getId());
		Msg noticeWeekcardMsg = Msg.valueOf(GsConst.MsgType.CONSUME_WEEK_CARD_SUC, targetXID);
		noticeWeekcardMsg.pushParam(weekCardStatus);
		GsApp.getInstance().postMsg(noticeWeekcardMsg);
		return true;
	}

	/**
	 * 检查并触发周卡
	 *
	 * @param playerData
	 * @param goodsId
	 * @param costMoney
	 * @return
	 */
	public static boolean triggerWeekCard(PlayerData playerData, int goodsId, float costMoney, long subExprie) {

		int activityId = Const.ActivityId.WEEK_CARD_VALUE;
		ActivityItem activityItem = ActivityManager.getInstance().getActiveActivityItem(activityId);
		if (activityItem == null) {
			return false;
		}
		ActivityTimeCfg weekActivityTimeCfg = ActivityUtil.getCurActivityTimeCfg(activityId);
		if (weekActivityTimeCfg == null || weekActivityTimeCfg.isEnd()) {
			return false;
		}

		WeekCardStatus weekCardStatus = getActivityStatus(playerData, activityId, weekActivityTimeCfg.getStageId(),
				WeekCardStatus.class);
		if (weekCardStatus == null) {
			return false;
		}

		WeekCardCfg weekCardCfg = WeekCardCfg.getWeekCardCfgByGoodsId(goodsId);
		if (weekCardCfg == null) {
			return false;
		}

		// 没有业务场景 直接去掉
		if (subExprie == 0) {
			boolean isLevelUp = weekCardStatus.canLevelUp(goodsId);
			if (weekCardStatus.isBetweenReward() && !isLevelUp) {
				// 已有周卡并且升级失败
				return false;
			}

			if (isLevelUp) {
				boolean isRewardToday = weekCardStatus.isRewardToday();
				// 今天领过奖励后, 周卡即使升级也不能再领
				// weekCardStatus.resetlevelUpData();
				weekCardStatus.clearRewardMap();
				if (isRewardToday) {
					weekCardStatus.putRewardToday();
				}
			}
			weekCardStatus.setStartDate(GuaJiTime.getCalendar().getTime());

		} else {

			if (subExprie == weekCardStatus.getExpireTime())
				return false;
			// 为了兼容之前规则计算开始时间
			long beginTime = subExprie - 86400000 * weekCardCfg.getDays();
			Date beginDate = new Date(beginTime);
			weekCardStatus.setStartDate(beginDate);
			boolean isRewardToday = weekCardStatus.isRewardToday();
			weekCardStatus.clearRewardMap();
			if (isRewardToday)
				weekCardStatus.putRewardToday();
		}

		// 更新活动信息
		weekCardStatus.setCurrentActiveCfgId(weekCardCfg.getId());
		weekCardStatus.setExpireTime(subExprie);
		playerData.updateActivity(ActivityId.WEEK_CARD_VALUE, weekActivityTimeCfg.getStageId(), true);

		MailManager.createSysMail(playerData.getId(), Mail.MailType.Normal_VALUE, GsConst.MailId.WEEK_CARD_MAIL, "",
				null, GuaJiTime.getTimeString());

		GuaJiXID targetXID = GuaJiXID.valueOf(GsConst.ObjType.PLAYER, playerData.getId());
		Msg noticeWeekcardMsg = Msg.valueOf(GsConst.MsgType.WEEK_CARD_SUC, targetXID);
		noticeWeekcardMsg.pushParam(weekCardStatus);
		GsApp.getInstance().postMsg(noticeWeekcardMsg);
		return true;
	}

	/**
	 * @param playerData 用户数据
	 * @return 获取当前周卡状态
	 */
	public static WeekCardStatus GetWeekCardStatus(PlayerData playerData) {

		int activityId = Const.ActivityId.WEEK_CARD_VALUE;
		ActivityItem activityItem = ActivityManager.getInstance().getActiveActivityItem(activityId);
		if (activityItem == null) {
			return null;
		}
		ActivityTimeCfg weekActivityTimeCfg = ActivityUtil.getCurActivityTimeCfg(activityId);
		if (weekActivityTimeCfg == null || weekActivityTimeCfg.isEnd()) {
			return null;
		}

		WeekCardStatus weekCardStatus = getActivityStatus(playerData, activityId, weekActivityTimeCfg.getStageId(),
				WeekCardStatus.class);
		if (weekCardStatus == null) {
			return null;
		}

		return weekCardStatus;
	}

	/**
	 * 检查并触发疯狂转轮活动
	 *
	 * @param playerData
	 * @param goodsId
	 * @param amout
	 * @return
	 */
	public static int triggerCrazyRoulette(PlayerData playerData, int amout) {
		if (playerData == null) {
			return 0;
		}

		// 检测活动是否开放
		int activityId = Const.ActivityId.CRAZY_ROULETTE_VALUE;
		ActivityTimeCfg timeCfg = ActivityUtil.getCurActivityTimeCfg(activityId);
		if (timeCfg == null || timeCfg.isEnd()) {
			return 0;
		}

		RouletteStatus status = getActivityStatus(playerData, activityId, timeCfg.getStageId(), RouletteStatus.class);
		if (status != null) {
			status.addRechargeNum(amout);
			playerData.updateActivity(activityId, timeCfg.getStageId(), true);
		}
		return 0;
	}

	/**
	 * 检查并触发幸运宝箱活动
	 *
	 * @param playerData
	 * @return
	 */
	public static void triggerLuckyTreasure(MapReward mapReward, DropAward.Treasure.Builder treasureBuilder,
			int treasureId) {
		// 检测活动是否开放
		int activityId = Const.ActivityId.LUCK_BOX_VALUE;
		ActivityTimeCfg timeCfg = ActivityUtil.getCurActivityTimeCfg(activityId);
		if (timeCfg == null || timeCfg.isEnd()) {
			return;
		}

		// 设置额外掉落
		treasureBuilder.setState(Battle.treasureState.TREASURE_LUCKY_OPEN_VALUE);
		LuckyTreasureDropsCfg dropsCfg = ConfigManager.getInstance().getConfigByKey(LuckyTreasureDropsCfg.class,
				treasureId);
		if (dropsCfg != null) {
			ItemInfo extraDropItemInfo = GuaJiRand
					.randonWeightObject(dropsCfg.getItemInfos(), dropsCfg.getItemWeights()).clone();
			if (extraDropItemInfo != null) {
				// 添加到奖励信息
				mapReward.getAwardItems().addItem(extraDropItemInfo);
				// 添加额外掉落信息
				DropAward.Item.Builder itemBuilder = DropAward.Item.newBuilder();
				itemBuilder.setItemType(extraDropItemInfo.getType());
				itemBuilder.setItemId(extraDropItemInfo.getItemId());
				itemBuilder.setItemCount((int)extraDropItemInfo.getQuantity());
				treasureBuilder.addLuckItem(itemBuilder);
			}
		}
	}

	/**
	 * 获得商店刷新价格
	 *
	 * @return
	 */
	public static Float getShopPriceRatio() {
		ActivityItem activityItem = ActivityManager.getInstance()
				.getActiveActivityItem(Const.ActivityId.SHOP_REFRESH_PRICE_RATIO_VALUE);
		if (activityItem != null) {
			return activityItem.getParam("ratio");
		}
		return null;
	}

	/**
	 * 触发财富俱乐部活动
	 *
	 * @param playerData
	 * @param recharge
	 */
	public static void triggerGoldClub(PlayerData playerData, int recharge) {

		// 检测活动是否开放
		int activityId = Const.ActivityId.GOLD_CLUB_VALUE;
		ActivityItem activityItem = ActivityManager.getInstance()
				.getActiveActivityItem(Const.ActivityId.GOLD_CLUB_VALUE);
		if (activityItem == null) {
			return;
		}
		ActivityTimeCfg timeCfg = ActivityUtil.getCurActivityTimeCfg(activityId);
		if (timeCfg == null || timeCfg.isEnd()) {
			return;
		}
		// // 结算时间判断
		// WealthClubCfg config =
		// ConfigManager.getInstance().getConfigByIndex(WealthClubCfg.class,
		// 0);
		// if (GuaJiTime.getMillisecond() > config.getSettleTimeValue()) {
		// if (GuaJiTime.getMillisecond() > config.getSettleTimeValue() + 5 * 60
		// * 1000) {
		// // 5分钟--跳到的第二天结算时间数据更新
		// config.updateSettleTimeValue();
		// } else {
		// return;
		// }
		// }

		if (WealthClubManager.getStageStatus() == 1)// 活动中，且属于结算阶段
		{
			return;
		}
		// 取财富俱乐部活动数据
		WealthClubStatus status = getActivityStatus(playerData, activityId, timeCfg.getStageId(),
				WealthClubStatus.class);
		if (status != null) {
			if (status.isFirstRecharge()) {
				// 充值人数增加
				WealthClubManager.getInstance().addTotalNumber();
			}
			status.addRecharge(recharge);
			playerData.updateActivity(activityId, timeCfg.getStageId());
			// 添加缓存
			WealthClubManager.getInstance().addRecharge(playerData.getId(), recharge);
		}
		// 财富聚乐部数据更新
		WealthClubManager.getInstance().rechargePlayerNumber();
		WealthClubManager.getInstance().getEntity().notifyUpdate(true);
	}

	/**
	 * 触发抢红包活动
	 */
	public static void triggerRedEnvelope(PlayerData playerData, int rechargeNum) {
		// 检测活动是否开放
		int activityId = Const.ActivityId.GRAB_RED_ENVELOPE_VALUE;
		ActivityTimeCfg timeCfg = ActivityUtil.getCurActivityTimeCfg(activityId);
		if (timeCfg == null || timeCfg.isEnd()) {
			return;
		}

		RedEnvelopeStatus status = getActivityStatus(playerData, activityId, timeCfg.getStageId(),
				RedEnvelopeStatus.class);
		if (status != null) {
			int lastLeftRecharge = status.getTotalRechargeAmount()
					% SysBasicCfg.getInstance().getRedEnvelopeExchangeRate();
			int redEnvelopeAmount = (rechargeNum + lastLeftRecharge)
					/ SysBasicCfg.getInstance().getRedEnvelopeExchangeRate();
			status.addRechargeAmount(rechargeNum);
			status.addMyRedEnvelope(redEnvelopeAmount);
			playerData.updateActivity(activityId, timeCfg.getStageId());
		}

		RedEnvelopeServerStatus serverStatus = ServerData.getInstance()
				.getServerStatus(GsConst.ServerStatusId.RED_ENVELOPE, RedEnvelopeServerStatus.class);
		serverStatus.addServerRechargeNum(rechargeNum);
		ServerData.getInstance().updateServerData(GsConst.ServerStatusId.RED_ENVELOPE);
	}

	/**
	 * 终身卡,累计每日充值数额
	 *
	 * @param playerData
	 * @param rechargeAmout
	 * @return
	 */
	public static boolean addPlayerTodayRecharge(PlayerData playerData, int rechargeAmout) {
		ActivityTimeCfg foreverCard = getCurActivityTimeCfg(Const.ActivityId.FOREVER_CARD_VALUE);
		if (foreverCard != null && (!foreverCard.isEnd())) {
			int stageId = foreverCard.getStageId();
			int activityId = foreverCard.getActivityId();
			ForeverCardStatus foreverCardStatus = getActivityStatus(playerData, activityId, stageId,
					ForeverCardStatus.class);
			foreverCardStatus.addTodayRecharge(rechargeAmout);

			playerData.updateActivity(Const.ActivityId.FOREVER_CARD_VALUE, foreverCard.getStageId(), true);
		}
		return true;
	}

	/**
	 * 终身卡,累计每日消费数额
	 *
	 * @param playerData
	 * @param rechargeAmout
	 * @return
	 */
	public static boolean addPlayerTodayConsume(PlayerData playerData, int consumeAmout) {
		ActivityTimeCfg foreverCard = getCurActivityTimeCfg(Const.ActivityId.FOREVER_CARD_VALUE);
		if (foreverCard != null && (!foreverCard.isEnd())) {
			int stageId = foreverCard.getStageId();
			int activityId = foreverCard.getActivityId();
			ForeverCardStatus foreverCardStatus = getActivityStatus(playerData, activityId, stageId,
					ForeverCardStatus.class);
			foreverCardStatus.addAccConsumeGold(consumeAmout);
			playerData.updateActivity(Const.ActivityId.FOREVER_CARD_VALUE, foreverCard.getStageId(), true);
		}
		return true;
	}

	/**
	 * 激活终身卡
	 *
	 * @param player
	 * @return r2game
	 */
	public static boolean foreverCardAvtivateHandler(PlayerData playerData) {
		ActivityTimeCfg foreverCardCfg = ActivityUtil.getCurActivityTimeCfg(Const.ActivityId.FOREVER_CARD_VALUE);
		if (foreverCardCfg == null) {
			return false;
		}
		int activityId = foreverCardCfg.getActivityId();
		int stageId = foreverCardCfg.getStageId();
		// 获得终身卡状态
		ForeverCardStatus foreverCardStatus = ActivityUtil.getActivityStatus(playerData, activityId, stageId,
				ForeverCardStatus.class);

		if (!foreverCardStatus.canActivate()) {
			return false;
		}

		// 同步玩家终身卡状态
		foreverCardStatus.setCardStatus(GsConst.ForeverStatus.OPEN_UNDRAW);
		playerData.updateActivity(activityId, foreverCardCfg.getStageId());
		// 构造回复协议
		HPForeverCardRet.Builder builder = HPForeverCardRet.newBuilder();
		builder.setCardStatus(foreverCardStatus.getCardStatus());
		// 日志记录
		BehaviorLogger.log4Platform(playerData.getPlayerEntity(), Action.ACTIVATE_FOREVER_CARD);
		return true;
	}

	public static void tiggerSalePacket(PlayerData playerData, int goodsId) {

		SalePacketStatus salePacketStatus = ActivityUtil.getSalePacketStatus(playerData);

		if (salePacketStatus == null) {
			return;
		}

		if (!salePacketStatus.addPacket(goodsId)) {
			return;
		}
		// 活动结束时间判断
		Date registerDate = playerData.getPlayerEntity().getCreateTime();
		int lastTime = ActivityUtil.clacSalePacketSurplusTime(registerDate,
				playerData.getPlayerEntity().getMergeTime());
		Player player = PlayerUtil.queryPlayer(playerData.getId());
		if (player == null) {
			return;
		}
		salePacketStatus.getInfo().get(goodsId).AddBuyTime();
		salePacketStatus.getInfo().get(goodsId).setState(1);
		// 发送服务器礼包协议
		player.sendProtocol(Protocol.valueOf(HP.code.SALE_PACKET_INFO_S,
				BuilderUtil.genSalePacketStatus(lastTime, salePacketStatus)));

		return;
	}

	public static boolean tiggerLimitRecharge(PlayerData playerData, int goodsId) {

		Player player = PlayerUtil.queryPlayer(playerData.getId());
		// 检测活动是否开放
		int activityId = Const.ActivityId.TIME_LIMIT_PURCHASE_VALUE;
		ActivityTimeCfg timeCfg = ActivityUtil.getCurActivityTimeCfg(activityId);

		if (timeCfg == null) {
			// 活动已关闭
			player.sendError(HP.code.TIME_LIMIT_BUY_S_VALUE, Status.error.ACTIVITY_CLOSE);
			return false;
		}

		PersonalTimeLimitStatus personalTimeLimitStatus = ActivityUtil.getActivityStatus(player.getPlayerData(),
				activityId, timeCfg.getStageId(), PersonalTimeLimitStatus.class);

		if (personalTimeLimitStatus == null) {
			player.sendError(HP.code.TIME_LIMIT_BUY_S_VALUE, Status.error.DATA_NOT_FOUND_VALUE);

			return false;
		}

		ServerTimeLimitEntity timeLimitEntity = TimeLimitManager.getInstance().getCurTimeLimitEntity();

		if (timeLimitEntity == null) {
			player.sendError(HP.code.TIME_LIMIT_BUY_S_VALUE, Status.error.DATA_NOT_FOUND_VALUE);
			return false;
		}

		if (timeLimitEntity.getStageId() != timeCfg.getStageId()) {
			player.sendError(HP.code.TIME_LIMIT_BUY_S_VALUE, Status.error.DATA_NOT_FOUND_VALUE);

			TimeLimitManager.getInstance().refresh();
			return false;
		}

		TimeLimitCfg timeLimitCfg = ConfigManager.getInstance().getConfigByKey(TimeLimitCfg.class, goodsId);
		if (timeLimitCfg == null) {
			player.sendError(HP.code.TIME_LIMIT_BUY_S_VALUE, Status.error.CONFIG_NOT_FOUND_VALUE);

			return false;
		}

		if (player.getVipLevel() < timeLimitCfg.getVipLimit()) {
			player.sendError(HP.code.TIME_LIMIT_BUY_S_VALUE, Status.error.VIP_NOT_ENOUGH_VALUE);

			return false;
		}

		if (player.getLevel() < timeLimitCfg.getLevelLimit()) {
			player.sendError(HP.code.TIME_LIMIT_BUY_S_VALUE, Status.error.LEVEL_NOT_LIMIT_VALUE);
			return false;
		}

		// 有限制
		if (timeLimitCfg.getPersonalLimitType() == 2) {
			if (personalTimeLimitStatus.getTodayBuyTimes(goodsId) >= timeLimitCfg.getPersonalLimit()) {
				// 个人领取限制
				player.sendError(HP.code.TIME_LIMIT_BUY_S_VALUE, Status.error.TIME_LIMIT_TODAY_BUY_TIMES_LIMIT);
				return false;
			}
		}

		if (timeLimitCfg.getPersonalLimitType() == 1) {
			if (personalTimeLimitStatus.getTotalBuyTimes(goodsId) >= timeLimitCfg.getPersonalLimit()) {
				// 个人领取限制
				player.sendError(HP.code.TIME_LIMIT_BUY_S_VALUE, Status.error.TIME_LIMIT_ALL_BUY_TIMES_LIMIT);
				return false;
			}
		}

		if (timeLimitCfg.getServerLimitType() == 2) {
			if (timeLimitEntity.getTodayBuyTimes(goodsId) >= timeLimitCfg.getServerLimit()) {
				// 全服领取限制
				player.sendError(HP.code.TIME_LIMIT_BUY_S_VALUE, Status.error.SERVER_TIME_LIMIT_TODAY_BUY_TIMES_LIMIT);
				return false;
			}
		}

		if (timeLimitCfg.getServerLimitType() == 1) {
			if (timeLimitEntity.getTotalBuyTimes(goodsId) >= timeLimitCfg.getServerLimit()) {
				// 全服领取限制
				player.sendError(HP.code.TIME_LIMIT_BUY_S_VALUE, Status.error.SERVER_TIME_LIMIT_ALL_BUY_TIMES_LIMIT);
				return false;
			}
		}

		AwardItems awardItems = AwardItems.valueOf(timeLimitCfg.getItems());
		awardItems.rewardTakeAffectAndPush(player, Action.TIME_LIMIT_BUY, 2);

		personalTimeLimitStatus.addTodayBuyTime(goodsId, 1);

		player.getPlayerData().updateActivity(Const.ActivityId.TIME_LIMIT_PURCHASE_VALUE, timeCfg.getStageId());

		timeLimitEntity.addTodayBuyTime(goodsId, 1);

		timeLimitEntity.notifyUpdate(false);

		TimeLimitPurchaseInfoHandler.pushTimeLimitInfo(player, personalTimeLimitStatus, timeLimitEntity, timeCfg);

		return true;
	}

	/**
	 * 检查打靶奖池是否达到替换时间
	 */
	public static boolean checkShootRewardTime(long lastRefreshSecond) {

		ActivityTimeCfg timeCfg = ActivityUtil.getCurActivityTimeCfg(Const.ActivityId.SHOOT_ACTIVITY_VALUE);
		if (timeCfg == null) {
			return false;
		}

		String startTime = timeCfg.getStartTime().replace("_", " ");
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		long startTimeMillis = 0;
		try {
			startTimeMillis = sdf.parse(startTime).getTime();
		} catch (ParseException e) {
			e.printStackTrace();
		}
		if (lastRefreshSecond == 0) {
			return true;
		}
		// 上次刷新时间(分钟)
		int lastRefreshMinute = (int) (lastRefreshSecond / 60);
		// 活动开始（分钟）
		int activityTime = (int) (startTimeMillis / 1000 / 60);
		// 当前时间（分钟）
		int curMinute = (int) (GuaJiTime.getSeconds() / 60);
		// 分钟差
		int difference = curMinute - activityTime;
		if (difference <= 0) {
			return false;
		}
		// 间隔刷新（分钟）
		int refreshTime = SysBasicCfg.getInstance().getShootRefreshTime() * 24 * 60;
		if ((curMinute - lastRefreshMinute) > refreshTime) {
			return true;
		}
		return difference % refreshTime == 0;
	}

	/**
	 * 周期清理活动数据
	 *
	 * @param player
	 * @param stageId
	 */
	public static void clearShootCount(Player player, int stageId) {
		ShootActivityInfo shootInfo = ActivityUtil.getActivityStatus(player.getPlayerData(),
				Const.ActivityId.SHOOT_ACTIVITY_VALUE, stageId, ShootActivityInfo.class);
		shootInfo.clearShootInfo();
		player.getPlayerData().updateActivity(Const.ActivityId.SHOOT_ACTIVITY_VALUE, stageId);
	}

	/**
	 * 购买聊天皮肤
	 */
	public static boolean triggerBuyChatSkin(PlayerData playerData, int goodsId, float costMoney) {
		if (goodsId == 0) {
			return false;
		}

		Player player = PlayerUtil.queryPlayer(playerData.getId());
		// 检测活动是否开放
		int activityId = Const.ActivityId.CHAT_SKIN_VALUE;
		ActivityTimeCfg timeCfg = ActivityUtil.getCurActivityTimeCfg(activityId);
		if (timeCfg == null) {
			// 活动已关闭
			player.sendError(HP.code.CHAT_SKIN_BUY_S_VALUE, Status.error.ACTIVITY_CLOSE);
			return false;
		}

		// 通过goodsId获取皮肤ID
		Map<Object, ChatSkinCfg> cfgs = ConfigManager.getInstance().getConfigMap(ChatSkinCfg.class);
		ChatSkinCfg curCfg = null;
		for (Entry<Object, ChatSkinCfg> e : cfgs.entrySet()) {
			ChatSkinCfg cfg = e.getValue();
			if (cfg.getGoodsId() == goodsId) {
				curCfg = cfg;
				break;
			}
		}
		if (curCfg == null) {
			return false;
		}
		int skinId = curCfg.getSkinId();
		ChatSkinEntity entity = playerData.getChatSkinEntity();
		// 不能购买
		if (!entity.canBuy(skinId, timeCfg.getStageId())) {
			return false;
		}

		// 购买
		entity.buy(skinId, timeCfg.getStageId());

		// 更新DB
		entity.reConvert();
		entity.notifyUpdate();

		HPChatSkinBuy.Builder ret = HPChatSkinBuy.newBuilder();
		ret.setSkinId(skinId);
		player.sendProtocol(Protocol.valueOf(HP.code.CHAT_SKIN_BUY_S_VALUE, ret));

		// 发送邮件给玩家
		SimpleDateFormat df = new SimpleDateFormat("HH:mm");
		MailManager.createSysMail(player.getId(), Mail.MailType.Normal_VALUE, GsConst.MailId.BUY_CHAT_SKIN_SUCC, "",
				null, df.format(new Date()), String.valueOf(costMoney), String.valueOf(curCfg.getDays()));

		return true;
	}

	/**
	 * 折扣礼包
	 *
	 * @param playerData
	 * @param goodsId
	 * @return
	 */
	public static boolean tiggerDiscountGift(PlayerData playerData, int goodsId) {
		DiscountGiftData data = ActivityUtil.getDiscountGiftData(playerData);
		// 购买礼包
		boolean succ = data.buyGift(playerData,goodsId);
		if (!succ) {
			return false;
		}
		// 向客户端推送消息
		Player player = PlayerUtil.queryPlayer(playerData.getId());
		if (player != null) {
			HPDiscountBuySuccRet.Builder builder = HPDiscountBuySuccRet.newBuilder();
			builder.setGoodsId(goodsId);
			player.sendProtocol(Protocol.valueOf(HP.code.DISCOUNT_GIFT_BUY_SUCC_S, builder));
		}
		return true;
	}
	
	/**
	 * 成長通行證觸發
	 */
	
	public static boolean tiggerGrowthPass(PlayerData playerData, int type,int goodsId) {
		int activityId = 0 ;
		ActivityTimeCfg timeCfg = null;
		int stageId = 0;
		if (type == 12) {
			activityId = Const.ActivityId.ACTIVITY162_GROWTH_LV_VALUE;
			timeCfg = ActivityUtil.getCurActivityTimeCfg(activityId);
			if (timeCfg == null) {
				return false;
			}
			stageId = timeCfg.getStageId();
			Activity162Status status = ActivityUtil.getActivityStatus(playerData, activityId, stageId, Activity162Status.class);
			if (status == null || status.getCostFlag()) {
				return false;
			}
			status.setCostFlag(true);
		} else if ((type == 13)||(type ==22)) {
			activityId = Const.ActivityId.ACTIVITY163_GROWTH_CH_VALUE;
			timeCfg = ActivityUtil.getCurActivityTimeCfg(activityId);
			if (timeCfg == null) {
				return false;
			}
			stageId = timeCfg.getStageId();
			Activity163Status status = ActivityUtil.getActivityStatus(playerData, activityId, stageId, Activity163Status.class);
			if (status == null) {
				return false;
			}
			int costtype = (type == 13) ? 1 : 2;
			if (status.getCostFlag().contains(costtype)) {
				return false;
			}	
			status.setCostFlag(costtype);
		} else if (type == 14) {
			activityId = Const.ActivityId.ACTIVITY164_GROWTH_TW_VALUE;
			timeCfg = ActivityUtil.getCurActivityTimeCfg(activityId);
			if (timeCfg == null) {
				return false;
			}
			stageId = timeCfg.getStageId();
			Activity164Status status = ActivityUtil.getActivityStatus(playerData, activityId, stageId, Activity164Status.class);
			if (status == null || status.getCostFlag()) {
				return false;
			}
			status.setCostFlag(true);
		} else {
			return false;
		}
		playerData.updateActivity(activityId, timeCfg.getStageId(), true);
		// 向客户端推送消息
		Player player = PlayerUtil.queryPlayer(playerData.getId());
		if (player != null) {
			HPDiscountBuySuccRet.Builder builder = HPDiscountBuySuccRet.newBuilder();
			builder.setGoodsId(goodsId);
			player.sendProtocol(Protocol.valueOf(HP.code.GROWTH_PASS_BUY_SUCC_S, builder));
		}
		return true;
	}
	
	/**
	 * 標記功能禮包
	 */
	public static boolean tiggerSignGoods(PlayerData playerData, int type,int goodsId) {
		SignNewCfg signCfg = ConfigManager.getInstance().getConfigByKey(SignNewCfg.class, goodsId);
		if (signCfg == null) {
			return false;
		}
		int SignId = signCfg.getSignid();
		boolean ret = PlayerUtil.modifyPlayerSign(playerData.getPlayer(),SignId, true,true);
		if (ret) {
			playerData.setLastRecharage(signCfg.getItemid());
		}
		return ret;
	}
	
	/**
	 * 儲值觸發特權
	 */
	public static boolean tiggerSubScription(PlayerData playerData,int goodsId) {
		int activityId = Const.ActivityId.ACTIVITY168_SubScription_VALUE;
		int action = 0;
		ActivityTimeCfg timeCfg = ActivityUtil.getCurActivityTimeCfg(activityId);
		timeCfg = ActivityUtil.getCurActivityTimeCfg(activityId);
		if (timeCfg == null) {
			return false;
		}
		
		SubScriptionCfg cfg = ConfigManager.getInstance().getConfigByKey(SubScriptionCfg.class, goodsId);
		if (cfg == null) {
			return false;
		}
		
		int stageId = timeCfg.getStageId();
		Activity168Status status = ActivityUtil.getActivityStatus(playerData, activityId, stageId, Activity168Status.class);
		if (status == null || status.isActivate(goodsId)) {
			return false;
		}
		
		status.setActivateId(goodsId,1);
		
		playerData.updateActivity(activityId, timeCfg.getStageId());
		
		// 下发奖励
		int MailId = GsConst.MailId.SUBSCRIPTION_GIFT_MAIL;
		String rewar = cfg.getBuyReward()+","+cfg.getDayReward();
		AwardItems awardItems = AwardItems.valueOf(rewar);
		MailManager.createMail(playerData.getId(), MailType.Reward_VALUE, MailId, "", awardItems);
		
		BehaviorLogger.log4Service(playerData.getPlayerEntity(), Source.USER_OPERATION, Action.ACTIVITY168_SUBSCRIPTION,Params.valueOf("action", action),
				Params.valueOf("goodsId", goodsId));
		
		playerData.setLastRecharage("@Subscription168_info");
		
		// 向客户端推送消息
		Player player = PlayerUtil.queryPlayer(playerData.getId());
		if (player != null) {
			// 返回包
			SubScriptionResp.Builder response = SubScriptionResp.newBuilder();
			
			response.setAction(action);
			for(Map.Entry<Integer,Integer> entry : status.getActivateId().entrySet() ) {
				response.addActivateId(entry.getKey());
				response.addTimes(entry.getValue());
			} 
			player.sendProtocol(Protocol.valueOf(HP.code.ACTIVITY168_SUBSCRIPTION_S, response));
		}
		return true;
	}
	
	/**
	 * 儲值彈跳等級禮包132
	 */
	public static boolean tiggerLevelGift(PlayerData playerData,int goodsId) {
		
		Player player = PlayerUtil.queryPlayer(playerData.getId());
		if (player == null) {
			return false;
		}
		
		int activityId = Const.ActivityId.ACTIVITY132_LEVEL_GIFT_VALUE;
		ActivityTimeCfg timeCfg = ActivityUtil.getCurActivityTimeCfg(activityId);
		timeCfg = ActivityUtil.getCurActivityTimeCfg(activityId);
		if (timeCfg == null) {
			return false;
		}
		
		LevelGiftAward132Cfg cfg = LevelGiftAward132Cfg.getCfgByGoodsId(goodsId);
		
		if (cfg == null) {
			return false;
		}
		
		int stageId = timeCfg.getStageId();
		Activity132Status status = ActivityUtil.getActivityStatus(playerData, activityId, stageId, Activity132Status.class);
		if (status == null || status.isAlreadyGot(cfg.getId())) {
			return false;
		}
		
		// 发放奖励
		AwardItems awards = AwardItems.valueOf(cfg.getAwards());
		awards.rewardTakeAffectAndPush(player, BehaviorLogger.Action.ACTIVITY132_LEVEL_GIFT, 2,TapDBSource.Recharge,Params.valueOf("goodsId", goodsId));
		// 更新status
		status.addGiftIds(cfg.getId());
		player.getPlayerData().updateActivity(activityId, timeCfg.getStageId(), true);
		
		BehaviorLogger.log4Service(player,Source.USER_OPERATION, Action.ACTIVITY132_LEVEL_GIFT, 
				Params.valueOf("CfgId", cfg.getId()),
				Params.valueOf("Level", player.getLevel()),
				Params.valueOf("goodsId", goodsId),
		        Params.valueOf("awards", cfg.getAwards()));
		
		player.getPlayerData().setLastRecharage(cfg.getAwards());
		
		// 同步领取状态
		//int hours = 0;
		Activity132LevelGiftBuyRes.Builder builder = Activity132LevelGiftBuyRes.newBuilder();
		Map<Object, LevelGiftAward132Cfg> cfgList = ConfigManager.getInstance().getConfigMap(LevelGiftAward132Cfg.class);
		for (LevelGiftAward132Cfg cfgItem : cfgList.values()) {
			LevelGiftInfo.Builder info = LevelGiftInfo.newBuilder();
			info.setCfgId(cfgItem.getId());
//            if ((player.getLevel() >= cfgItem.getMinLevel()) && (player.getLevel() <= cfgItem.getMaxLevel())) {
//            	hours = cfgItem.getHours();
//            }

		    info.setIsGot(status.isAlreadyGot(cfgItem.getId()));
		    
		    builder.addInfo(info);
		}
		//builder.setLimitDate(status.calcGiftSurplusTime(hours));
		player.sendProtocol(Protocol.valueOf(HP.code.ACTIVITY132_LEVEL_GIFT_BUY_S_VALUE, builder));
		return true;
	}
	
	/**
	 * 儲值彈跳關卡禮包151
	 */
	public static boolean tiggerStageGift(PlayerData playerData,int goodsId) {
		
		Player player = PlayerUtil.queryPlayer(playerData.getId());
		if (player == null) {
			return false;
		}
		
		int activityId = Const.ActivityId.ACTIVITY151_STAGE_GIFT_VALUE;
		ActivityTimeCfg timeCfg = ActivityUtil.getCurActivityTimeCfg(activityId);
		timeCfg = ActivityUtil.getCurActivityTimeCfg(activityId);
		if (timeCfg == null) {
			return false;
		}
		
		StageGiftAward151Cfg cfg = StageGiftAward151Cfg.getCfgByGoodsId(goodsId);
		
		if (cfg == null) {
			return false;
		}
		
		int stageId = timeCfg.getStageId();
		Activity151Status status = ActivityUtil.getActivityStatus(playerData, activityId, stageId, Activity151Status.class);
		if (status == null || status.isAlreadyGot(cfg.getId())) {
			return false;
		}
		
		// 发放奖励
		AwardItems awards = AwardItems.valueOf(cfg.getAwards());
		awards.rewardTakeAffectAndPush(player, BehaviorLogger.Action.ACTIVITY151_STAGE_GIFT, 2,TapDBSource.Recharge,Params.valueOf("goodsId", goodsId));
		// 更新status
		status.addGiftIds(cfg.getId());
		player.getPlayerData().updateActivity(activityId, timeCfg.getStageId(), true);
		
		// BI 日志 ()
		BehaviorLogger.log4Service(player,Source.USER_OPERATION, Action.ACTIVITY151_STAGE_GIFT, 
				Params.valueOf("CfgId", cfg.getId()),
				Params.valueOf("PassMapId", player.getPassMapId()),
				Params.valueOf("goodsId", goodsId),
		        Params.valueOf("awards", cfg.getAwards()));
		
		player.getPlayerData().setLastRecharage(cfg.getAwards());
		
		// 同步领取状态
		Activity132LevelGiftBuyRes.Builder builder = Activity132LevelGiftBuyRes.newBuilder();
		Map<Object, StageGiftAward151Cfg> cfgList = ConfigManager.getInstance().getConfigMap(StageGiftAward151Cfg.class);
		for (StageGiftAward151Cfg cfgItem : cfgList.values()) {
		    LevelGiftInfo.Builder info = LevelGiftInfo.newBuilder();     
		    info.setCfgId(cfgItem.getId());
		      
		    info.setIsGot(status.isAlreadyGot(cfgItem.getId()));
		
		    builder.addInfo(info);
		}
		
		player.sendProtocol(Protocol.valueOf(HP.code.ACTIVITY151_STAGE_GIFT_BUY_S_VALUE, builder));
		return true;
	}
	
	/**
	 * 關卡失敗禮包177
	 */
	public static boolean tiggerFailedGift(PlayerData playerData,int goodsId) {
		
		Player player = PlayerUtil.queryPlayer(playerData.getId());
		if (player == null) {
			return false;
		}
		
		int activityId = Const.ActivityId.ACTIVITY177_Failed_Gift_VALUE;
		ActivityTimeCfg timeCfg = ActivityUtil.getCurActivityTimeCfg(activityId);
		timeCfg = ActivityUtil.getCurActivityTimeCfg(activityId);
		if (timeCfg == null) {
			return false;
		}
		
		FailedGift177Cfg cfg = FailedGift177Cfg.getCfgByGoodsId(goodsId);
		
		if (cfg == null) {
			return false;
		}
		
		int stageId = timeCfg.getStageId();
		Activity177Status status = ActivityUtil.getActivityStatus(playerData, activityId, stageId, Activity177Status.class);
		if (status == null || status.Isbuy()) {
			return false;
		}
		
		// 发放奖励
		AwardItems awards = AwardItems.valueOf(cfg.getAwards());
		awards.rewardTakeAffectAndPush(player, BehaviorLogger.Action.ACTIVITY177_STAGE_FAILED, 2,TapDBSource.Recharge,Params.valueOf("goodsId", goodsId));
		// 更新status
		status.setIsbuy(true);
		player.getPlayerData().updateActivity(activityId, timeCfg.getStageId(), true);
		
		// BI 日志 ()
		BehaviorLogger.log4Service(player,Source.USER_OPERATION, Action.ACTIVITY177_STAGE_FAILED, 
				Params.valueOf("CfgId", cfg.getId()),
				Params.valueOf("CurMapId", player.getCurMapId()),
				Params.valueOf("goodsId", goodsId),
		        Params.valueOf("awards", cfg.getAwards()));
		
		player.getPlayerData().setLastRecharage(cfg.getAwards());
		
		// 同步领取状态
		Activity177Handler.SyncInfo(timeCfg,1,player,status);
		return true;
	}
	
	/**
	 * 階段禮包179
	 */
	public static boolean tiggerStepGift(PlayerData playerData,int goodsId) {
		
		Player player = PlayerUtil.queryPlayer(playerData.getId());
		if (player == null) {
			return false;
		}
		
		int activityId = Const.ActivityId.ACTIVITY179_Step_Gift_VALUE;
		ActivityTimeCfg timeCfg = ActivityUtil.getCurActivityTimeCfg(activityId);
		timeCfg = ActivityUtil.getCurActivityTimeCfg(activityId);
		if (timeCfg == null) {
			return false;
		}
		
		ReleaseStepGiftCfg giftCfg = ConfigManager.getInstance().getConfigByKey(ReleaseStepGiftCfg.class,goodsId);
		
		if (giftCfg == null) {
			return false;
		}
		
		int stageId = timeCfg.getStageId();
		Activity179Status status = ActivityUtil.getActivityStatus(playerData, activityId, stageId, Activity179Status.class);
		if (status == null || status.getTakeGoodsId() >= ReleaseStepGiftCfg.getMaxGoodsId()) {
			return false;
		}
		
		// 更新status
		status.setTakeGoodsId(goodsId);
		player.getPlayerData().updateActivity(activityId, timeCfg.getStageId(), true);
		
		// 发放奖励
		AwardItems awards = AwardItems.valueOf(giftCfg.getAwards());
		awards.rewardTakeAffectAndPush(player, BehaviorLogger.Action.ACTIVITY179_Step_GIFT, 2,TapDBSource.Recharge
				,Params.valueOf("goodsId", goodsId));

		
		// BI 日志 ()
		BehaviorLogger.log4Service(player,Source.USER_OPERATION, Action.ACTIVITY179_Step_GIFT, 
				Params.valueOf("goodsId", goodsId),
		        Params.valueOf("awards", giftCfg.getAwards()));
		
		player.getPlayerData().setLastRecharage(giftCfg.getAwards());
		
		// 同步领取状态
		Activity179Handler.SyncInfo(0,player,status);
		return true;
	}

	/**
	 * 儲值彈跳活動禮包169
	 */
	public static boolean tiggerActivityGift(PlayerData playerData,int goodsId) {
		
		Player player = PlayerUtil.queryPlayer(playerData.getId());
		if (player == null) {
			return false;
		}
		
		int activityId = Const.ActivityId.ACTIVITY169_JumpGift_VALUE;
		ActivityTimeCfg timeCfg = ActivityUtil.getCurActivityTimeCfg(activityId);
		timeCfg = ActivityUtil.getCurActivityTimeCfg(activityId);
		if (timeCfg == null) {
			return false;
		}
		
		ActivityGiftAward169Cfg cfg = ConfigManager.getInstance().getConfigByKey(ActivityGiftAward169Cfg.class,goodsId);
		
		if (cfg == null) {
			return false;
		}
		
		int stageId = timeCfg.getStageId();
		Activity169Status status = ActivityUtil.getActivityStatus(playerData, activityId, stageId, Activity169Status.class);
		if (status == null || status.isAlreadyGot(cfg.getId())) {
			return false;
		}
		
		// 发放奖励
		AwardItems awards = AwardItems.valueOf(cfg.getAwards());
		awards.rewardTakeAffectAndPush(player, BehaviorLogger.Action.ACTIVITY169_JUMP_GIFT, 2,TapDBSource.Recharge,Params.valueOf("goodsId", goodsId));
		// 更新status
		status.addGiftIds(cfg.getId());
		player.getPlayerData().updateActivity(activityId, timeCfg.getStageId(), true);
		
		// BI 日志 ()
		BehaviorLogger.log4Service(player,Source.USER_OPERATION, Action.ACTIVITY169_JUMP_GIFT, 
				Params.valueOf("CfgId", cfg.getId()),
				Params.valueOf("goodsId", goodsId),
		        Params.valueOf("awards", cfg.getAwards()));
		
		player.getPlayerData().setLastRecharage(cfg.getAwards());
		
		// 同步领取状态
		Activity132LevelGiftBuyRes.Builder builder = Activity132LevelGiftBuyRes.newBuilder();
		Map<Object, ActivityGiftAward169Cfg> cfgList = ConfigManager.getInstance().getConfigMap(ActivityGiftAward169Cfg.class);
		for (ActivityGiftAward169Cfg cfgItem : cfgList.values()) {
		    LevelGiftInfo.Builder info = LevelGiftInfo.newBuilder();     
		    info.setCfgId(cfgItem.getId());
		      
		    info.setIsGot(status.isAlreadyGot(cfgItem.getId()));
		
		    builder.addInfo(info);
		}
		
		player.sendProtocol(Protocol.valueOf(HP.code.ACTIVITY169_ACTIVITY_GIFT_VERIFY_S, builder));
		return true;
	}
	
	/**
	 * 儲值彈跳活動禮包170
	 */
	public static boolean tiggerJumpGift(PlayerData playerData,int goodsId) {
		
		Player player = PlayerUtil.queryPlayer(playerData.getId());
		if (player == null) {
			return false;
		}
		
		int activityId = Const.ActivityId.ACTIVITY170_JumpGift_VALUE;
		ActivityTimeCfg timeCfg = ActivityUtil.getCurActivityTimeCfg(activityId);
		timeCfg = ActivityUtil.getCurActivityTimeCfg(activityId);
		if (timeCfg == null) {
			return false;
		}
		
		ActivityGiftAward170Cfg cfg = ConfigManager.getInstance().getConfigByKey(ActivityGiftAward170Cfg.class,goodsId);
		
		if (cfg == null) {
			return false;
		}
		
		int stageId = timeCfg.getStageId();
		Activity170Status status = ActivityUtil.getActivityStatus(playerData, activityId, stageId, Activity170Status.class);
		if (status == null || status.isAlreadyGot(cfg.getId())) {
			return false;
		}
		
		// 发放奖励
		AwardItems awards = AwardItems.valueOf(cfg.getAwards());
		awards.rewardTakeAffectAndPush(player, BehaviorLogger.Action.ACTIVITY170_JUMP_GIFT, 2,TapDBSource.Recharge,Params.valueOf("goodsId", goodsId));
		// 更新status
		status.addGiftIds(cfg.getId());
		player.getPlayerData().updateActivity(activityId, timeCfg.getStageId(), true);
		
		// BI 日志 ()
		BehaviorLogger.log4Service(player,Source.USER_OPERATION, Action.ACTIVITY170_JUMP_GIFT, 
				Params.valueOf("CfgId", cfg.getId()),
				Params.valueOf("goodsId", goodsId),
		        Params.valueOf("awards", cfg.getAwards()));
		
		player.getPlayerData().setLastRecharage(cfg.getAwards());
		
		// 同步领取状态
		Activity132LevelGiftBuyRes.Builder builder = Activity132LevelGiftBuyRes.newBuilder();
		Map<Object, ActivityGiftAward170Cfg> cfgList = ConfigManager.getInstance().getConfigMap(ActivityGiftAward170Cfg.class);
		for (ActivityGiftAward170Cfg cfgItem : cfgList.values()) {
		    LevelGiftInfo.Builder info = LevelGiftInfo.newBuilder();     
		    info.setCfgId(cfgItem.getId());
		      
		    info.setIsGot(status.isAlreadyGot(cfgItem.getId()));
		
		    builder.addInfo(info);
		}
		
		player.sendProtocol(Protocol.valueOf(HP.code.ACTIVITY170_ACTIVITY_GIFT_VERIFY_S, builder));
		return true;
	}

	/**
	 * 儲值彈跳活動禮包181
	 */
	public static boolean tiggerJumpGift181(PlayerData playerData,int goodsId) {
		
		Player player = PlayerUtil.queryPlayer(playerData.getId());
		if (player == null) {
			return false;
		}
		
		int activityId = Const.ActivityId.ACTIVITY181_JumpGift_VALUE;
		ActivityTimeCfg timeCfg = ActivityUtil.getCurActivityTimeCfg(activityId);
		timeCfg = ActivityUtil.getCurActivityTimeCfg(activityId);
		if (timeCfg == null) {
			return false;
		}
		
		ActivityGiftAward181Cfg cfg = ConfigManager.getInstance().getConfigByKey(ActivityGiftAward181Cfg.class,goodsId);
		
		if (cfg == null) {
			return false;
		}
		
		int stageId = timeCfg.getStageId();
		Activity181Status status = ActivityUtil.getActivityStatus(playerData, activityId, stageId, Activity181Status.class);
		if (status == null || status.isAlreadyGot(cfg.getId())) {
			return false;
		}
		
		// 发放奖励
		AwardItems awards = AwardItems.valueOf(cfg.getAwards());
		awards.rewardTakeAffectAndPush(player, BehaviorLogger.Action.ACTIVITY181_JUMP_GIFT, 2,TapDBSource.Recharge,Params.valueOf("goodsId", goodsId));
		// 更新status
		status.addGiftIds(cfg.getId());
		player.getPlayerData().updateActivity(activityId, timeCfg.getStageId(), true);
		
		// BI 日志 ()
		BehaviorLogger.log4Service(player,Source.USER_OPERATION, Action.ACTIVITY181_JUMP_GIFT, 
				Params.valueOf("CfgId", cfg.getId()),
				Params.valueOf("goodsId", goodsId),
		        Params.valueOf("awards", cfg.getAwards()));
		
		player.getPlayerData().setLastRecharage(cfg.getAwards());
		
		// 同步领取状态
		Activity132LevelGiftBuyRes.Builder builder = Activity132LevelGiftBuyRes.newBuilder();
		Map<Object, ActivityGiftAward181Cfg> cfgList = ConfigManager.getInstance().getConfigMap(ActivityGiftAward181Cfg.class);
		for (ActivityGiftAward181Cfg cfgItem : cfgList.values()) {
		    LevelGiftInfo.Builder info = LevelGiftInfo.newBuilder();     
		    info.setCfgId(cfgItem.getId());
		      
		    info.setIsGot(status.isAlreadyGot(cfgItem.getId()));
		
		    builder.addInfo(info);
		}
		
		player.sendProtocol(Protocol.valueOf(HP.code.ACTIVITY181_ACTIVITY_GIFT_VERIFY_S, builder));
		return true;
	}
	
	/**
	 * 儲值彈跳活動禮包182
	 */
	public static boolean tiggerJumpGift182(PlayerData playerData,int goodsId) {
		
		Player player = PlayerUtil.queryPlayer(playerData.getId());
		if (player == null) {
			return false;
		}
		
		int activityId = Const.ActivityId.ACTIVITY182_JumpGift_VALUE;
		ActivityTimeCfg timeCfg = ActivityUtil.getCurActivityTimeCfg(activityId);
		timeCfg = ActivityUtil.getCurActivityTimeCfg(activityId);
		if (timeCfg == null) {
			return false;
		}
		
		ActivityGiftAward182Cfg cfg = ConfigManager.getInstance().getConfigByKey(ActivityGiftAward182Cfg.class,goodsId);
		
		if (cfg == null) {
			return false;
		}
		
		int stageId = timeCfg.getStageId();
		Activity182Status status = ActivityUtil.getActivityStatus(playerData, activityId, stageId, Activity182Status.class);
		if (status == null || status.isAlreadyGot(cfg.getId())) {
			return false;
		}
		
		// 发放奖励
		AwardItems awards = AwardItems.valueOf(cfg.getAwards());
		awards.rewardTakeAffectAndPush(player, BehaviorLogger.Action.ACTIVITY182_JUMP_GIFT, 2,TapDBSource.Recharge,Params.valueOf("goodsId", goodsId));
		// 更新status
		status.addGiftIds(cfg.getId());
		player.getPlayerData().updateActivity(activityId, timeCfg.getStageId(), true);
		
		// BI 日志 ()
		BehaviorLogger.log4Service(player,Source.USER_OPERATION, Action.ACTIVITY182_JUMP_GIFT, 
				Params.valueOf("CfgId", cfg.getId()),
				Params.valueOf("goodsId", goodsId),
		        Params.valueOf("awards", cfg.getAwards()));
		
		player.getPlayerData().setLastRecharage(cfg.getAwards());
		
		// 同步领取状态
		Activity132LevelGiftBuyRes.Builder builder = Activity132LevelGiftBuyRes.newBuilder();
		Map<Object, ActivityGiftAward182Cfg> cfgList = ConfigManager.getInstance().getConfigMap(ActivityGiftAward182Cfg.class);
		for (ActivityGiftAward182Cfg cfgItem : cfgList.values()) {
		    LevelGiftInfo.Builder info = LevelGiftInfo.newBuilder();     
		    info.setCfgId(cfgItem.getId());
		      
		    info.setIsGot(status.isAlreadyGot(cfgItem.getId()));
		
		    builder.addInfo(info);
		}
		
		player.sendProtocol(Protocol.valueOf(HP.code.ACTIVITY182_ACTIVITY_GIFT_VERIFY_S, builder));
		return true;
	}
	
	/**
	 * 儲值彈跳活動禮包183
	 */
	public static boolean tiggerJumpGift183(PlayerData playerData,int goodsId) {
		
		Player player = PlayerUtil.queryPlayer(playerData.getId());
		if (player == null) {
			return false;
		}
		
		int activityId = Const.ActivityId.ACTIVITY183_JumpGift_VALUE;
		ActivityTimeCfg timeCfg = ActivityUtil.getCurActivityTimeCfg(activityId);
		timeCfg = ActivityUtil.getCurActivityTimeCfg(activityId);
		if (timeCfg == null) {
			return false;
		}
		
		ActivityGiftAward183Cfg cfg = ConfigManager.getInstance().getConfigByKey(ActivityGiftAward183Cfg.class,goodsId);
		
		if (cfg == null) {
			return false;
		}
		
		int stageId = timeCfg.getStageId();
		Activity183Status status = ActivityUtil.getActivityStatus(playerData, activityId, stageId, Activity183Status.class);
		if (status == null || status.isAlreadyGot(cfg.getId())) {
			return false;
		}
		
		// 发放奖励
		AwardItems awards = AwardItems.valueOf(cfg.getAwards());
		awards.rewardTakeAffectAndPush(player, BehaviorLogger.Action.ACTIVITY183_JUMP_GIFT, 2,TapDBSource.Recharge,Params.valueOf("goodsId", goodsId));
		// 更新status
		status.addGiftIds(cfg.getId());
		player.getPlayerData().updateActivity(activityId, timeCfg.getStageId(), true);
		
		// BI 日志 ()
		BehaviorLogger.log4Service(player,Source.USER_OPERATION, Action.ACTIVITY183_JUMP_GIFT, 
				Params.valueOf("CfgId", cfg.getId()),
				Params.valueOf("goodsId", goodsId),
		        Params.valueOf("awards", cfg.getAwards()));
		
		player.getPlayerData().setLastRecharage(cfg.getAwards());
		
		// 同步领取状态
		Activity132LevelGiftBuyRes.Builder builder = Activity132LevelGiftBuyRes.newBuilder();
		Map<Object, ActivityGiftAward183Cfg> cfgList = ConfigManager.getInstance().getConfigMap(ActivityGiftAward183Cfg.class);
		for (ActivityGiftAward183Cfg cfgItem : cfgList.values()) {
		    LevelGiftInfo.Builder info = LevelGiftInfo.newBuilder();     
		    info.setCfgId(cfgItem.getId());
		      
		    info.setIsGot(status.isAlreadyGot(cfgItem.getId()));
		
		    builder.addInfo(info);
		}
		
		player.sendProtocol(Protocol.valueOf(HP.code.ACTIVITY183_ACTIVITY_GIFT_VERIFY_S, builder));
		return true;
	}
	
	/**
	 * 儲值彈跳活動禮包184
	 */
	public static boolean tiggerJumpGift184(PlayerData playerData,int goodsId) {
		
		Player player = PlayerUtil.queryPlayer(playerData.getId());
		if (player == null) {
			return false;
		}
		
		int activityId = Const.ActivityId.ACTIVITY184_JumpGift_VALUE;
		ActivityTimeCfg timeCfg = ActivityUtil.getCurActivityTimeCfg(activityId);
		timeCfg = ActivityUtil.getCurActivityTimeCfg(activityId);
		if (timeCfg == null) {
			return false;
		}
		
		ActivityGiftAward184Cfg cfg = ConfigManager.getInstance().getConfigByKey(ActivityGiftAward184Cfg.class,goodsId);
		
		if (cfg == null) {
			return false;
		}
		
		int stageId = timeCfg.getStageId();
		Activity184Status status = ActivityUtil.getActivityStatus(playerData, activityId, stageId, Activity184Status.class);
		if (status == null || status.isAlreadyGot(cfg.getId())) {
			return false;
		}
		
		// 发放奖励
		AwardItems awards = AwardItems.valueOf(cfg.getAwards());
		awards.rewardTakeAffectAndPush(player, BehaviorLogger.Action.ACTIVITY184_JUMP_GIFT, 2,TapDBSource.Recharge,Params.valueOf("goodsId", goodsId));
		// 更新status
		status.addGiftIds(cfg.getId());
		player.getPlayerData().updateActivity(activityId, timeCfg.getStageId(), true);
		
		// BI 日志 ()
		BehaviorLogger.log4Service(player,Source.USER_OPERATION, Action.ACTIVITY184_JUMP_GIFT, 
				Params.valueOf("CfgId", cfg.getId()),
				Params.valueOf("goodsId", goodsId),
		        Params.valueOf("awards", cfg.getAwards()));
		
		player.getPlayerData().setLastRecharage(cfg.getAwards());
		
		// 同步领取状态
		Activity132LevelGiftBuyRes.Builder builder = Activity132LevelGiftBuyRes.newBuilder();
		Map<Object, ActivityGiftAward184Cfg> cfgList = ConfigManager.getInstance().getConfigMap(ActivityGiftAward184Cfg.class);
		for (ActivityGiftAward184Cfg cfgItem : cfgList.values()) {
		    LevelGiftInfo.Builder info = LevelGiftInfo.newBuilder();     
		    info.setCfgId(cfgItem.getId());
		      
		    info.setIsGot(status.isAlreadyGot(cfgItem.getId()));
		
		    builder.addInfo(info);
		}
		
		player.sendProtocol(Protocol.valueOf(HP.code.ACTIVITY184_ACTIVITY_GIFT_VERIFY_S, builder));
		return true;
	}
	
	/**
	 * 儲值彈跳活動禮包185
	 */
	public static boolean tiggerJumpGift185(PlayerData playerData,int goodsId) {
		
		Player player = PlayerUtil.queryPlayer(playerData.getId());
		if (player == null) {
			return false;
		}
		
		int activityId = Const.ActivityId.ACTIVITY185_JumpGift_VALUE;
		ActivityTimeCfg timeCfg = ActivityUtil.getCurActivityTimeCfg(activityId);
		timeCfg = ActivityUtil.getCurActivityTimeCfg(activityId);
		if (timeCfg == null) {
			return false;
		}
		
		ActivityGiftAward185Cfg cfg = ConfigManager.getInstance().getConfigByKey(ActivityGiftAward185Cfg.class,goodsId);
		
		if (cfg == null) {
			return false;
		}
		
		int stageId = timeCfg.getStageId();
		Activity185Status status = ActivityUtil.getActivityStatus(playerData, activityId, stageId, Activity185Status.class);
		if (status == null || status.isAlreadyGot(cfg.getId())) {
			return false;
		}
		
		// 发放奖励
		AwardItems awards = AwardItems.valueOf(cfg.getAwards());
		awards.rewardTakeAffectAndPush(player, BehaviorLogger.Action.ACTIVITY185_JUMP_GIFT, 2,TapDBSource.Recharge,Params.valueOf("goodsId", goodsId));
		// 更新status
		status.addGiftIds(cfg.getId());
		player.getPlayerData().updateActivity(activityId, timeCfg.getStageId(), true);
		
		// BI 日志 ()
		BehaviorLogger.log4Service(player,Source.USER_OPERATION, Action.ACTIVITY185_JUMP_GIFT, 
				Params.valueOf("CfgId", cfg.getId()),
				Params.valueOf("goodsId", goodsId),
		        Params.valueOf("awards", cfg.getAwards()));
		
		player.getPlayerData().setLastRecharage(cfg.getAwards());
		
		// 同步领取状态
		Activity132LevelGiftBuyRes.Builder builder = Activity132LevelGiftBuyRes.newBuilder();
		Map<Object, ActivityGiftAward185Cfg> cfgList = ConfigManager.getInstance().getConfigMap(ActivityGiftAward185Cfg.class);
		for (ActivityGiftAward185Cfg cfgItem : cfgList.values()) {
		    LevelGiftInfo.Builder info = LevelGiftInfo.newBuilder();     
		    info.setCfgId(cfgItem.getId());
		      
		    info.setIsGot(status.isAlreadyGot(cfgItem.getId()));
		
		    builder.addInfo(info);
		}
		
		player.sendProtocol(Protocol.valueOf(HP.code.ACTIVITY185_ACTIVITY_GIFT_VERIFY_S, builder));
		return true;
	}
		
	/**
	 * 儲值彈跳活動禮包186
	 */
	public static boolean tiggerJumpGift186(PlayerData playerData,int goodsId) {
		
		Player player = PlayerUtil.queryPlayer(playerData.getId());
		if (player == null) {
			return false;
		}
		
		int activityId = Const.ActivityId.ACTIVITY186_JumpGift_VALUE;
		ActivityTimeCfg timeCfg = ActivityUtil.getCurActivityTimeCfg(activityId);
		timeCfg = ActivityUtil.getCurActivityTimeCfg(activityId);
		if (timeCfg == null) {
			return false;
		}
		
		ActivityGiftAward186Cfg cfg = ConfigManager.getInstance().getConfigByKey(ActivityGiftAward186Cfg.class,goodsId);
		
		if (cfg == null) {
			return false;
		}
		
		int stageId = timeCfg.getStageId();
		Activity186Status status = ActivityUtil.getActivityStatus(playerData, activityId, stageId, Activity186Status.class);
		if (status == null || status.isAlreadyGot(cfg.getId())) {
			return false;
		}
		
		// 发放奖励
		AwardItems awards = AwardItems.valueOf(cfg.getAwards());
		awards.rewardTakeAffectAndPush(player, BehaviorLogger.Action.ACTIVITY186_JUMP_GIFT, 2,TapDBSource.Recharge,Params.valueOf("goodsId", goodsId));
		// 更新status
		status.addGiftIds(cfg.getId());
		player.getPlayerData().updateActivity(activityId, timeCfg.getStageId(), true);
		
		// BI 日志 ()
		BehaviorLogger.log4Service(player,Source.USER_OPERATION, Action.ACTIVITY186_JUMP_GIFT, 
				Params.valueOf("CfgId", cfg.getId()),
				Params.valueOf("goodsId", goodsId),
		        Params.valueOf("awards", cfg.getAwards()));
		
		player.getPlayerData().setLastRecharage(cfg.getAwards());
		
		// 同步领取状态
		Activity132LevelGiftBuyRes.Builder builder = Activity132LevelGiftBuyRes.newBuilder();
		Map<Object, ActivityGiftAward186Cfg> cfgList = ConfigManager.getInstance().getConfigMap(ActivityGiftAward186Cfg.class);
		for (ActivityGiftAward186Cfg cfgItem : cfgList.values()) {
		    LevelGiftInfo.Builder info = LevelGiftInfo.newBuilder();     
		    info.setCfgId(cfgItem.getId());
		      
		    info.setIsGot(status.isAlreadyGot(cfgItem.getId()));
		
		    builder.addInfo(info);
		}
		
		player.sendProtocol(Protocol.valueOf(HP.code.ACTIVITY186_ACTIVITY_GIFT_VERIFY_S, builder));
		return true;
	}
	
	/**
	 *  觸發加強彈跳活動禮包187
	 */
	public static boolean tiggerMaxJump(PlayerData playerData,int goodsId) {
		
		Player player = PlayerUtil.queryPlayer(playerData.getId());
		if (player == null) {
			return false;
		}
		
		int activityId = Const.ActivityId.ACTIVITY187_MaxJump_VALUE;
		ActivityTimeCfg timeCfg = ActivityUtil.getCurActivityTimeCfg(activityId);
		timeCfg = ActivityUtil.getCurActivityTimeCfg(activityId);
		if (timeCfg == null) {
			return false;
		}
		
		TimeGiftCfg cfg = ConfigManager.getInstance().getConfigByKey(TimeGiftCfg.class,goodsId);
		
		if (cfg == null) {
			return false;
		}
		
		int stageId = timeCfg.getStageId();
		Activity187Status status = ActivityUtil.getActivityStatus(playerData, activityId, stageId, Activity187Status.class);
		if (status == null) {
			return false;
		}
		
		// 发放奖励
		AwardItems awards = AwardItems.valueOf(cfg.getAwards());
		awards.rewardTakeAffectAndPush(player, BehaviorLogger.Action.ACTIVITY187_MAX_JUMP, 2,TapDBSource.Recharge,Params.valueOf("goodsId", goodsId));
		// 更新status
		if (cfg.getCount() != 0) { // 有限量需紀錄購買數及購買期數,方便清除
			status.addGiftCount(goodsId);
			status.setGiftUseTime(goodsId,cfg.getUseTime());
			player.getPlayerData().updateActivity(activityId, timeCfg.getStageId(), true);
		}
		
		int SignId = cfg.getSignid();
		if (cfg.getSignid() != 0) {
			PlayerUtil.modifyPlayerSign(playerData.getPlayer(),SignId, true,true);
		}
				
		player.getPlayerData().setLastRecharage(cfg.getAwards());
		
		Activity187Handler.SyncInfo(0, player, status);
		
		// BI 日志 ()
		BehaviorLogger.log4Service(player,Source.USER_OPERATION, Action.ACTIVITY187_MAX_JUMP, 
				Params.valueOf("goodsId", goodsId),
				Params.valueOf("giftCount", status.getGiftCount(goodsId)),
				Params.valueOf("useTime", cfg.getUseTime()), // 第幾期禮包
				Params.valueOf("SignId", SignId), // 標記
		        Params.valueOf("awards", cfg.getAwards()));
		
		// 同步领取状态
//		Activity132LevelGiftBuyRes.Builder builder = Activity132LevelGiftBuyRes.newBuilder();
//		Map<Object, ActivityGiftAward170Cfg> cfgList = ConfigManager.getInstance().getConfigMap(ActivityGiftAward170Cfg.class);
//		for (ActivityGiftAward170Cfg cfgItem : cfgList.values()) {
//		    LevelGiftInfo.Builder info = LevelGiftInfo.newBuilder();     
//		    info.setCfgId(cfgItem.getId());
//		      
//		    info.setIsGot(status.isAlreadyGot(cfgItem.getId()));
//		
//		    builder.addInfo(info);
//		}
		
//		player.sendProtocol(Protocol.valueOf(HP.code.ACTIVITY187_MAXJUMP_GIFT_S, builder));
		return true;
	}
		
	/**
	 * 發放每日特權禮品
	 */
	@SuppressWarnings("unchecked")
	public static void getSubScriptionReward(PlayerData playerData) {
		int activityId = Const.ActivityId.ACTIVITY168_SubScription_VALUE;
		ActivityTimeCfg timeCfg = ActivityUtil.getCurActivityTimeCfg(activityId);
		timeCfg = ActivityUtil.getCurActivityTimeCfg(activityId);
		if (timeCfg == null) {
			return ;
		}
		
		ActivityEntity<Activity168Status> activityEntity = (ActivityEntity<Activity168Status>) playerData.getActivityEntity(activityId, timeCfg.getStageId());
		if (activityEntity == null) { // 不主動建存檔資料
			return;
		}
		
		Activity168Status status = activityEntity.getActivityStatus(Activity168Status.class);
		String reward = "";
		List<Integer> idList = new ArrayList<>();
		List<Integer> oldtimes = new ArrayList<>(status.getActivateId().values());
		for (Map.Entry<Integer, Integer> entry : status.getActivateId().entrySet()) {
			if (status.isActivate(entry.getKey())){
				idList.add(entry.getKey());
				status.setActivateId(entry.getKey(),entry.getValue()+1);
				SubScriptionCfg cfg = ConfigManager.getInstance().getConfigByKey(SubScriptionCfg.class, entry.getKey());
				if (cfg != null) {
					if (reward.isEmpty()) {
						reward = cfg.getDayReward();
					} else {
						reward = reward+","+cfg.getDayReward();
					}
				}
			}
		}
		playerData.updateActivity(activityId, timeCfg.getStageId());
		
		if (!reward.isEmpty()){
			int MailId = GsConst.MailId.SUBSCRIPTION_GIFT_MAIL;
			AwardItems awardItems = AwardItems.valueOf(reward);
			MailManager.createMail(playerData.getId(), MailType.Reward_VALUE, MailId, "", awardItems);
			BehaviorLogger.log4Service(playerData.getPlayerEntity(), Source.EMAIL_ADD, Action.ACTIVITY168_SUBSCRIPTION,Params.valueOf("goodsIds", idList),
					Params.valueOf("oldtimes", oldtimes),
					Params.valueOf("complate", status.getActivateId().values()),
					Params.valueOf("reward", reward));
		}
	}
	
	/**
	 * 儲值觸發收費打卡 
	 */
	public static boolean tiggerSupportCalender(PlayerData playerData,int costtype,int goodsId) {
		int activityId = 0 ;
		int action = 1;
		ActivityTimeCfg timeCfg = null;
		int stageId = 0;
		activityId = Const.ActivityId.ACTIVITY161_SUPPORT_CALENDER_VALUE;
		timeCfg = ActivityUtil.getCurActivityTimeCfg(activityId);
		if (timeCfg == null) {
			return false;
		}
		
		stageId = timeCfg.getStageId();
		Activity161Status status = ActivityUtil.getActivityStatus(playerData, activityId, stageId, Activity161Status.class);
		if (status == null || status.isBuy(costtype)) {
			return false;
		}
		
		status.reSet(costtype);
		
		status.setBuy(costtype,1);
		

		playerData.updateActivity(activityId, timeCfg.getStageId());
		
		BehaviorLogger.log4Service(playerData.getPlayerEntity(), Source.USER_OPERATION, Action.ACTIVITY161_SUPPORT_CALENDAR,Params.valueOf("action", action),
				Params.valueOf("goodsId", goodsId),
				Params.valueOf("costtype", costtype));
		
		// 向客户端推送消息
		Player player = PlayerUtil.queryPlayer(playerData.getId());
		if (player != null) {
			// 返回包
			SupportCalendarRep.Builder response = SupportCalendarRep.newBuilder();
			
			response.setAction(action);
			response.setBuy(status.isBuy(costtype));
			response.setType(costtype);
			response.setCurMonth(status.getCurMonth());
			response.addAllSignedDays(status.getSignedDays(costtype));
			
			player.sendProtocol(Protocol.valueOf(HP.code.SUPPORT_CALENDAR_ACTION_S_VALUE, response));
		}
		return true;
	}

	/**
	 * 获取折扣礼包数据
	 *
	 * @param playerData
	 */
	public static DiscountGiftData getDiscountGiftData(PlayerData playerData) {
		ActivityTimeCfg timeCfg = ActivityUtil.getCurActivityTimeCfg(Const.ActivityId.DISCOUNT_GIFT_VALUE);
		@SuppressWarnings("unchecked")
		ActivityEntity<DiscountGiftData> activityEntity = (ActivityEntity<DiscountGiftData>) playerData
				.getActivityEntity(Const.ActivityId.DISCOUNT_GIFT_VALUE, timeCfg.getStageId());
		// 生成折扣活动存储信息
		DiscountGiftData data = null;
		if (activityEntity == null) {
			activityEntity = new ActivityEntity<DiscountGiftData>(playerData.getId(),
					Const.ActivityId.DISCOUNT_GIFT_VALUE, timeCfg.getStageId());
			data = new DiscountGiftData(playerData);
			activityEntity.setActivityStatus(data);
			playerData.createActivity(activityEntity);
		} else {
			data = activityEntity.getActivityStatus(DiscountGiftData.class);
			data.refresh(playerData,playerData.getPlayerEntity().getLevel());
		}

		return data;
	}

	/**
	 * 修改聊天皮肤
	 *
	 * @param type 1 : 增加，2：删除
	 */
	public static void changeChatSkin(int playerId, int skinId, int type) {

		try {
			if (type < 1 || type > 2) {
				return;
			}
			ChatSkinEntity chatSkinEntity;
			Player player = PlayerUtil.queryPlayer(playerId);
			if (player != null) {
				// 在线
				chatSkinEntity = player.getPlayerData().loadChatSkinEntity();
			} else {
				// 离线
				chatSkinEntity = DBManager.getInstance().fetch(ChatSkinEntity.class,
						"from ChatSkinEntity where playerId = ? and invalid = 0", playerId);
				if (chatSkinEntity == null) {
					chatSkinEntity = new ChatSkinEntity();
					chatSkinEntity.setPlayerId(playerId);
					DBManager.getInstance().create(chatSkinEntity);
				}
				chatSkinEntity.convert();
			}
			// 修改皮肤
			boolean succ = false;
			if (type == 1) {
				succ = chatSkinEntity.reward(skinId);
			} else if (type == 2) {
				succ = chatSkinEntity.remove(skinId);
			}
			if (succ) {
				chatSkinEntity.reConvert();
				if (player != null) {
					chatSkinEntity.notifyUpdate();

					// 刷新当前拥有的皮肤
					HPChatSkinInfo.Builder ret = HPChatSkinInfo.newBuilder();
					// 可用的皮肤
					List<ChatSkinItem> availableSkins = chatSkinEntity.availableSkins();
					ret.setCurSkinId(chatSkinEntity.getCurSkinId());
					for (ChatSkinItem item : availableSkins) {
						ChatSkinInfo.Builder b = ChatSkinInfo.newBuilder();
						b.setSkinId(item.getSkinId());
						b.setRemainTime(chatSkinEntity.getSkinRemainDays(item.getSkinId()));
						ret.addSkins(b);
					}
					player.sendProtocol(Protocol.valueOf(HP.code.CHAT_SKIN_OWNED_INFO_S_VALUE, ret));

				} else {
					chatSkinEntity.notifyUpdate(false);
				}
			}
		} catch (Exception e) {
			MyException.catchException(e);
		}
	}

	/**
	 * 当到达一定等级之后做的事情
	 */
	public static void onPlayerLevelUp(PlayerData playerData, int level) {
		try {
			// 当玩家等级离百花美人新手限时活动等级还有5级的时候，发邮件通知活动即将开启
			// HaremManager.sendNoticeMail(playerData.getId(), level);
			// 如果玩家等级到达了需要初始化新手活动
			HaremActivityEntity haremActivityEntity = playerData.loadHaremEntity();
			HaremManager.initNewStrict(level, haremActivityEntity, playerData.getPlayerEntity().getCreateTime(),
					playerData.getPlayerEntity().getMergeTime());
			// 当玩家新手限时活动等级还有5级的时候，发邮件通知活动即将开启
			// NewURManager.sendNoticeMail(playerData.getId(), level);
			// 到达等级初始化新UR活动结束时间
			NewURManager.initActivityEndTime(playerData, level);
		} catch (Exception e) {
			MyException.catchException(e);
		}
	}

	/**
	 * 获取新手UR活动信息
	 */
	public static NewURStatus getNewURStatus(PlayerData playerData) {
		@SuppressWarnings("unchecked")
		ActivityEntity<NewURStatus> activityEntity = (ActivityEntity<NewURStatus>) playerData
				.getActivityEntity(Const.ActivityId.NEW_UR_VALUE, 0);
		if (activityEntity == null) {
			activityEntity = new ActivityEntity<NewURStatus>(playerData.getId(), Const.ActivityId.NEW_UR_VALUE, 0);
			NewURStatus status = new NewURStatus();
			// 从来没有初始化过结束时间、等级到达才初始化结束时间
			if (playerData.getPlayerEntity().getLevel() >= NewURManager.getLimitLevel()
					&& status.getActivityEndTime() == 0) {
				status.setActivityEndTime(NewURManager.getInitActivityEndTime());
			}
			activityEntity.setActivityStatus(status);
			playerData.createActivity(activityEntity);
		}
		NewURStatus status = activityEntity.getActivityStatus(NewURStatus.class);
		return status;
	}

	/**
	 * 高速战斗券每日使用次数上限翻倍活动
	 *
	 * @author melvin.mao
	 */
	public static Integer getQuickBattleCardUpperLimitRatio() {
		ActivityItem activityItem = ActivityManager.getInstance()
				.getActiveActivityItem(Const.ActivityId.QUICK_BATTLE_DOUBLE_VALUE);
		if (activityItem != null) {
			Integer ratio = activityItem.getParam("ratio");
			if (ratio != null)
				return ratio;
		}
		return 1;
	}

	/**
	 * @param playerData 游戏玩家数据
	 * @return
	 */
	public static AccConItemStatus getAccConItemStatus(PlayerData playerData) {

		Player player = PlayerUtil.queryPlayer(playerData.getId());
		if (player == null) {
			return null;
		}
		int activityId = Const.ActivityId.ACCUMULATIVE_CONSUMEITEM_VALUE;
		ActivityTimeCfg timeCfg = ActivityUtil.getCurActivityTimeCfg(activityId);
		if (timeCfg == null) {
			// 活动已关闭
			player.sendError(HP.code.ACC_CONSUMEITEM_INFO_S_VALUE, Status.error.ACTIVITY_CLOSE);
			return null;
		}
		AccConItemStatus status = getActivityStatus(playerData, activityId, timeCfg.getStageId(),
				AccConItemStatus.class);
		if (status != null) {
			List<AccConsumeItemCfg> listItems = AccConsumeItemCfg.getConsumeItems(timeCfg.getStageId());
			boolean isChange = false;
			for (AccConsumeItemCfg itemCfg : listItems) {
				if (!status.getConsumeItems().containsKey(itemCfg.getId())) {
					status.addGoodBuyTime(itemCfg.getId(), 0);
					isChange = true;
				}
			}
			if (isChange)
				playerData.updateActivity(activityId, timeCfg.getStageId());

		}
		return status;
	}

	/**
	 * 累计消耗购买道具
	 *
	 * @param playerData 用户数据
	 * @param itemType   道具类型
	 * @param nTimes     购买次数
	 */
	public static void triggerConsumeItem(PlayerData playerData, int itemType, int nTimes) {
		if (playerData == null) {
			return;
		}

		Player player = PlayerUtil.queryPlayer(playerData.getId());
		if (player == null) {
			return;
		}

		int activityId = Const.ActivityId.ACCUMULATIVE_CONSUMEITEM_VALUE;
		ActivityTimeCfg timeCfg = ActivityUtil.getCurActivityTimeCfg(activityId);
		if (timeCfg == null) {
			// 活动已关闭
			return;
		}

		AccConsumeItemCfg itemCfg = AccConsumeItemCfg.getConsumeItemsByItemType(timeCfg.getStageId(), itemType);
		if (itemCfg == null)
			return;

		AccConItemStatus consumeStatus = getAccConItemStatus(playerData);
		if (consumeStatus != null) {
			consumeStatus.addGoodBuyTime(itemCfg.getId(), nTimes);
			playerData.updateActivity(activityId, timeCfg.getStageId());
		}

	}

	/**
	 * @param playerData
	 */
	public static void syncWeekMonthStatus(PlayerData playerData) {
		WeekCardStatus weekStatus = ActivityUtil.GetWeekCardStatus(playerData);
		MonthCardStatus monthStatus = ActivityUtil.getMonthCardStatus(playerData);

		// 周卡月卡状态
		List<SyncSubResponse> syncList = SyncSubscriptionService.getInstance()
				.getSubscriptionStatus(playerData.getPlayerEntity().getPuid(), playerData.getPlayerEntity().getId());

		if (syncList == null) {
			return;
		}

		for (SyncSubResponse syncSubResponse : syncList) {
			// 1 周卡 2月卡
			if (syncSubResponse.getType() == 1) {
				if (weekStatus != null && syncSubResponse.getExpireTime() != weekStatus.getExpireTime()) {
					ActivityUtil.triggerWeekCard(playerData, 71, 0, syncSubResponse.getExpireTime());
				}
			} else if (syncSubResponse.getType() == 2) {
				if (monthStatus != null && syncSubResponse.getExpireTime() != monthStatus.getExpireTime()) {
					Player player = PlayerUtil.queryPlayer(playerData.getPlayerEntity().getId());
					ActivityUtil.getMonthCardStatus(playerData).activateMonthCard2(30, player,
							syncSubResponse.getExpireTime());
					playerData.updateActivity(Const.ActivityId.MONTH_CARD_VALUE, 0, true);
				}
			}
		}
		return;
	}

	/**
	 * @param playerData 账号数据
	 * @return 获取活跃度达标活动状态
	 */
	public static ActiveStatus getActiveComplianceStatus(PlayerData playerData) {
		int activityId = Const.ActivityId.ACTIVECOMPLIANCE_VALUE;
		ActivityTimeCfg timeCfg = ActivityUtil.getCurActivityTimeCfg(activityId);
		if (timeCfg == null) {
			return null;
		}
		@SuppressWarnings("unchecked")
		ActivityEntity<ActiveStatus> activityEntity = (ActivityEntity<ActiveStatus>) playerData
				.getActivityEntity(activityId, timeCfg.getStageId());
		if (activityEntity == null) {
			activityEntity = new ActivityEntity<ActiveStatus>(playerData.getId(),
					Const.ActivityId.ACTIVECOMPLIANCE_VALUE, timeCfg.getStageId());
			activityEntity.setActivityStatus(new ActiveStatus());
			playerData.createActivity(activityEntity);
		}
		ActiveStatus activeComplianceStatus = activityEntity.getActivityStatus(ActiveStatus.class);
		return activeComplianceStatus;
	}

	public static void restActivity137Status(PlayerData playerData) {
		restActivity137Status(playerData, false);
	}
	
	/**
	 * 191.循環地城活動
	 * @param player
	 */
	public static void restCycleStageDailyStatus(Player player) {

		// 检测活动是否开放
		int activityId = Const.ActivityId.ACTIVITY191_CycleStage_VALUE;
		ActivityTimeCfg activityTimeCfg = ActivityUtil.getCurActivityTimeCfg(activityId);
		if (activityTimeCfg == null) {
			ActivityUtil.CycleStageClearItem(player);
			activityId = Const.ActivityId.ACTIVITY196_CycleStage_Part2_VALUE;
			activityTimeCfg = ActivityUtil.getCurActivityTimeCfg(activityId);
			if (activityTimeCfg == null) {
				ActivityUtil.CycleStageClearItem2(player);
				return;
			}
			Activity196Status status = ActivityUtil.getActivityStatus(player.getPlayerData(), activityId, activityTimeCfg.getStageId(),
					Activity196Status.class);
			
			if (status == null) {
				return;
			}
							
			status.Dailyreset();
			
			ActivityUtil.CycleStageInitItem2(player, activityTimeCfg, status);
			
			ActivityUtil.CycleStageDailyReset2(player);
			
			player.getPlayerData().updateActivity(activityId, activityTimeCfg.getStageId());
		} else {
			Activity191Status status = ActivityUtil.getActivityStatus(player.getPlayerData(), activityId, activityTimeCfg.getStageId(),
					Activity191Status.class);
			
			if (status == null) {
				return;
			}
							
			status.Dailyreset();
			
			ActivityUtil.CycleStageInitItem(player, activityTimeCfg, status);
			
			ActivityUtil.CycleStageDailyReset(player);
			
			player.getPlayerData().updateActivity(activityId, activityTimeCfg.getStageId());
		}
		
	}
	/**
	 * 193.單人強敵活動每日重置
	 * @param player
	 */
	public static void restSingleBossDailyStatus(Player player) {

		// 检测活动是否开放
		int activityId = Const.ActivityId.ACTIVITY193_SingleBoss_VALUE;
		ActivityTimeCfg activityTimeCfg = ActivityUtil.getCurActivityTimeCfg(activityId);
		if (activityTimeCfg == null) {
			return;
		}

		Activity193Status status = ActivityUtil.getActivityStatus(player.getPlayerData(), activityId, activityTimeCfg.getStageId(),
				Activity193Status.class);
						
		status.Dailyreset();
		
		ActivityUtil.SingleBossInitItem(player, activityTimeCfg, status);
				
		player.getPlayerData().updateActivity(activityId, activityTimeCfg.getStageId());
		
	}

	public static void restActivity137Status(PlayerData playerData, boolean isReLogin) {

		// 检测活动是否开放
		int activityId = Const.ActivityId.ACTIVITY137_RECHARGE_RETURN_VALUE;
		ActivityTimeCfg activityTimeCfg = ActivityUtil.getCurActivityTimeCfg(activityId);
		if (activityTimeCfg == null) {
			return;
		}
		// 剩余时间
		int leftTime = activityTimeCfg.calcActivitySurplusTime();
		if (leftTime <= 0) {
			// 活动已关闭
			return;
		}

		Activity137Status status = ActivityUtil.getActivityStatus(playerData, activityId, activityTimeCfg.getStageId(),
				Activity137Status.class);
		long currentTime = System.currentTimeMillis();
		if (GuaJiTime.isSameDay(status.getActivityTime(), currentTime)) {
			// 断线重连不需处理
			if (isReLogin) {
				return;
			}

			playerData.loadStateEntity();
			int todayLoginCount = playerData.getStateEntity().getTodayLoginCount() + 1;
			playerData.getStateEntity().setTodayLoginCount(todayLoginCount);
			playerData.getStateEntity().notifyUpdate();

			// 第二次活动重置
			// 先關閉
			/*boolean inLoginCountRule = SysBasicCfg.getInstance().getActivity137OpenLoginCount()
					.contains(String.valueOf(todayLoginCount));
			if (inLoginCountRule && todayLoginCount > status.getLoginTimes()) {
				long thisEndTime = status.getActivityTime()
						+ SysBasicCfg.getInstance().getActivity137OpenTime() * 1000 * 60;
				if (currentTime > thisEndTime || status.isUsed()) {
					status.init(todayLoginCount);
					playerData.updateActivity(activityId, activityTimeCfg.getStageId());
				}
			}*/
		} else {
			// 隔天重置
			status.init();
			playerData.loadStateEntity();
			playerData.updateActivity(activityId, activityTimeCfg.getStageId());
			playerData.getStateEntity().setTodayLoginCount(1);
			playerData.getStateEntity().notifyUpdate();
		}
	}

	public static int calcActivity137Return(PlayerData playerData, int GoldCount) {
		int returnGold = 0;
		try {
			if (playerData == null) {
				return returnGold;
			}
			// 检测活动是否开放
			int activityId = Const.ActivityId.ACTIVITY137_RECHARGE_RETURN_VALUE;
			ActivityTimeCfg activityTimeCfg = ActivityUtil.getCurActivityTimeCfg(activityId);
			if (activityTimeCfg == null) {
				return returnGold;
			}

			// 活动是否开启
			Activity137Status status = ActivityUtil.getActivityStatus(playerData, activityId,
					activityTimeCfg.getStageId(), Activity137Status.class);
			long thisEndTime = status.getActivityTime()
					+ SysBasicCfg.getInstance().getActivity137OpenTime() * 1000 * 60;
			long currentTime = System.currentTimeMillis();
			long activityEndTime = activityTimeCfg.getlEndTime();
			// 如果活动结束未过期延长活动时间
			if (thisEndTime > activityEndTime) {
				activityEndTime = thisEndTime;
			}
			if (activityEndTime < currentTime) {
				// 活动已关闭
				return returnGold;
			}

			// 是否已经抽奖过
			if (!status.isUsed() && thisEndTime > currentTime) {
				returnGold = (int) Math.ceil(GoldCount * status.calcRate() * 0.01);
				status.setUsed(true);
				// 保存玩家数据
				playerData.updateActivity(activityId, activityTimeCfg.getStageId(), true);
				return returnGold;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return returnGold;
	}

	public static void notifyActivity137Info(Player player) {
		notifyActivity137Info(player, false);
	}

	/**
	 * 获取充值返利抽奖活动彩票使用状态
	 */
	public static void notifyActivity137Info(Player player, boolean isRecharge) {
		try {
			if (player == null || player.getPlayerData() == null) {
				return;
			}
			// 检测活动是否开放
			int activityId = ActivityId.ACTIVITY137_RECHARGE_RETURN_VALUE;
			ActivityTimeCfg activityTimeCfg = ActivityUtil.getCurActivityTimeCfg(activityId);
			if (activityTimeCfg == null) {
				return;
			}
			Activity137Status status = ActivityUtil.getActivityStatus(player.getPlayerData(), activityId,
					activityTimeCfg.getStageId(), Activity137Status.class);
			long thisEndTime = status.getActivityTime()
					+ SysBasicCfg.getInstance().getActivity137OpenTime() * 1000 * 60;
			long currentTime = System.currentTimeMillis();
			long activityEndTime = activityTimeCfg.getlEndTime();
			// 如果活动结束未过期延长活动时间
			if (thisEndTime > activityEndTime) {
				activityEndTime = thisEndTime;
			}
			if (activityEndTime < currentTime) {
				// 活动已关闭
				return;
			}

			// 在有效期内使用
			boolean unUsed = thisEndTime > currentTime && !status.isUsed();
			if (unUsed || isRecharge) {

				Activity4.Activity137InfoRep.Builder builder = Activity4.Activity137InfoRep.newBuilder();
				builder.setLoginCount(player.getPlayerData().getStateEntity().getTodayLoginCount());
				builder.setLefttime(thisEndTime > currentTime ? (int) (thisEndTime - currentTime) / 1000 : 0);
				builder.setCount(status.getLotteryCount());
				builder.setFirstLine(status.getFirstLine());
				builder.setSecondLine(status.getSecondLine());
				builder.setThirdLine(status.getThirdLine());
				builder.setIsUsed(status.isUsed());
				builder.setLoginTimes(status.getLoginTimes());
				player.sendProtocol(Protocol.valueOf(HP.code.ACTIVITY137_SLOT_RETURN_INFO_S_VALUE, builder));
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * @param playerData
	 * @param GoldCount
	 * @return 140活动 充值返利
	 */
	public static int calcActivity140Return(PlayerData playerData, int GoldCount) {

		int returnGold = 0;
		if (playerData == null) {
			return returnGold;
		}
		// 检测活动是否开放
		int activityId = Const.ActivityId.RUSSIADISHWHEEL_VALUE;
		ActivityTimeCfg activityTimeCfg = ActivityUtil.getCurActivityTimeCfg(activityId);
		if (activityTimeCfg == null) {
			return returnGold;
		}

		// 活动是否开启
		Activity140Status status = ActivityUtil.getActivityStatus(playerData, activityId, activityTimeCfg.getStageId(),
				Activity140Status.class);

		List<String> takeConditions = Arrays
				.asList(SysBasicCfg.getInstance().getActivity140OpenLoginCount().split(","));

		if (!takeConditions.contains(String.valueOf(status.getLoginTimes()))) {
			return returnGold;
		}

		long thisEndTime = status.getActivityTime() + 30 * 1000 * 60;
		long currentTime = System.currentTimeMillis();
		long activityEndTime = activityTimeCfg.getlEndTime();
		// 如果活动结束未过期延长活动时间
		if (thisEndTime > activityEndTime) {
			activityEndTime = thisEndTime;
		}
		if (activityEndTime < currentTime) {
			// 活动已关闭
			return returnGold;
		}

		// 是否已经抽奖过
		if (!status.isUsed() && thisEndTime > currentTime) {

			Activity140DishWheelRadioCfg inCfg = Activity140DishWheelRadioCfg.getDishWheelRadioCfg(1,
					status.getInIndex());
			Activity140DishWheelRadioCfg outCfg = Activity140DishWheelRadioCfg.getDishWheelRadioCfg(2,
					status.getOutIndex());
			if (inCfg == null || outCfg == null) {
				return returnGold;
			}
			returnGold = (int) Math.ceil(GoldCount * (inCfg.getRatio() / 10000.0f) * (outCfg.getRatio() / 10000.0f));
			status.setUsed(true);
			// 保存玩家数据
			playerData.updateActivity(activityId, activityTimeCfg.getStageId(), true);
			return returnGold;
		}
		return 0;
	}

	public static void notifyActivity140Info(Player player) {
		notifyActivity140Info(player, false);
	}

	/**
	 * 获取充值返利抽奖活动彩票使用状态
	 */
	public static void notifyActivity140Info(Player player, boolean isRecharge) {

		if (player == null || player.getPlayerData() == null) {
			return;
		}
		// 检测活动是否开放
		int activityId = ActivityId.RUSSIADISHWHEEL_VALUE;
		ActivityTimeCfg activityTimeCfg = ActivityUtil.getCurActivityTimeCfg(activityId);
		if (activityTimeCfg == null) {
			return;
		}
		Activity140Status status = ActivityUtil.getActivityStatus(player.getPlayerData(), activityId,
				activityTimeCfg.getStageId(), Activity140Status.class);

		List<String> takeConditions = Arrays
				.asList(SysBasicCfg.getInstance().getActivity140OpenLoginCount().split(","));

		if (!takeConditions.contains(String.valueOf(status.getLoginTimes()))) {
			return;
		}

		long thisEndTime = status.getActivityTime()
				+ SysBasicCfg.getInstance().getActivity140Continuetime() * 1000 * 60;
		long currentTime = System.currentTimeMillis();
		long activityEndTime = activityTimeCfg.getlEndTime();
		// 如果活动结束未过期延长活动时间
		if (thisEndTime > activityEndTime) {
			activityEndTime = thisEndTime;
		}
		if (activityEndTime < currentTime) {
			// 活动已关闭
			return;
		}

		// 在有效期内使用
		boolean unUsed = thisEndTime > currentTime && !status.isUsed();
		if (unUsed || isRecharge) {

			Activity3.Activity140InfoRep.Builder builder = Activity3.Activity140InfoRep.newBuilder();
			builder.setLoginTimes(status.getLoginTimes());
			builder.setLeftTime(thisEndTime > currentTime ? (int) (thisEndTime - currentTime) / 1000 : 0);
			builder.setIsUsed(status.isUsed());
			builder.setInIndex(status.getInIndex() == 0 ? 1 : status.getInIndex());
			builder.setOutIndex(status.getOutIndex() == 0 ? 1 : status.getOutIndex());
			builder.setLotteryTimes(status.getLotteryCount());
			builder.setTodayLoginCount(status.getTodayLoginCount());
			player.sendProtocol(Protocol.valueOf(HP.code.ACTIVITY140_DISHWHEEL_INFO_S, builder));
		}

	}

	/**
	 * @param playerData
	 * @param isReLogin  重置活动状态
	 */
	public static void restActivity140Status(PlayerData playerData, boolean isReLogin) {

		// 数据有效检测
		if (playerData == null) {
			return;
		}
		// 检测活动是否开放
		int activityId = Const.ActivityId.RUSSIADISHWHEEL_VALUE;
		ActivityTimeCfg activityTimeCfg = ActivityUtil.getCurActivityTimeCfg(activityId);
		if (activityTimeCfg == null) {
			return;
		}

		// 剩余时间
		long activityEndTime = activityTimeCfg.getlEndTime();

		Activity140Status status = ActivityUtil.getActivityStatus(playerData, activityId, activityTimeCfg.getStageId(),
				Activity140Status.class);

		long thisEndTime = status.getActivityTime()
				+ SysBasicCfg.getInstance().getActivity140Continuetime() * 1000 * 60;

		// 更新活动截止时间
		if (thisEndTime > activityEndTime) {
			activityEndTime = thisEndTime;
		}

		long currentTime = System.currentTimeMillis();
		// 活动结束
		if (currentTime > activityEndTime) {
			return;
		}

		// 断线重连不需处理
		if (isReLogin) {
			return;
		}

		if (GuaJiTime.isSameDay(status.getDateTime().getTime(), currentTime)) {
			if (currentTime > thisEndTime) {
				List<String> loginRule = Arrays
						.asList(SysBasicCfg.getInstance().getActivity140OpenLoginCount().split(","));
				int todayLoginCount = status.getLoginTimes() + 1;
				int totalLoginCount = status.getTodayLoginCount() + 1;
				status.setTodayLoginCount(totalLoginCount);
				// 第二次活动重置
				boolean inLoginCountRule = loginRule.contains(String.valueOf(todayLoginCount));
				if (inLoginCountRule) {
					status.init(todayLoginCount);
					playerData.updateActivity(activityId, activityTimeCfg.getStageId());
				} else {
					status.setActivityTime(0);
					status.setLoginTimes(todayLoginCount);
					playerData.updateActivity(activityId, activityTimeCfg.getStageId());
				}
			}else {
				int totalLoginCount = status.getTodayLoginCount() + 1;
				status.setTodayLoginCount(totalLoginCount);
				playerData.updateActivity(activityId, activityTimeCfg.getStageId());
			}

		} else {

			if (activityEndTime == activityTimeCfg.getlEndTime()) {
				status.init();
				status.setLoginTimes(1);
				status.setDateTime(GuaJiTime.getAM0Date());
				playerData.updateActivity(activityId, activityTimeCfg.getStageId());
			}

		}
	}
	/**
	 * 重置159活動每日累計VIP點數贈禮
	 * @param playerData
	 */
	public static void restActivity159Status(PlayerData playerData) {
		// 数据有效检测
		if (playerData == null) {
			return;
		}
		// 检测活动是否开放
		int activityId = Const.ActivityId.ACTIVITY159_VIP_POINT_VALUE;
		ActivityTimeCfg activityTimeCfg = ActivityUtil.getCurActivityTimeCfg(activityId);
		if (activityTimeCfg == null) {
			return;
		}
		
		Activity159Status status = ActivityUtil.getActivityStatus(playerData, activityId, activityTimeCfg.getStageId(),
				Activity159Status.class);
		status.resetGotAwardCfgId();
		playerData.updateActivity(activityId, activityTimeCfg.getStageId());
		
	}
	
	/**
	 * 計算下次日刷新剩餘秒數
	 * 
	 * @return
	 */
	public static int calcDayRefreshTime() {
		long currentTime = GuaJiTime.getMillisecond();
		long reTime = GuaJiTime.getNextAM0Date();
		int surplusTime = (int) ((reTime - currentTime) / 1000);
		return Math.max(surplusTime, 0);
	}
	
	/**
	 * 計算下次周刷新剩餘秒數
	 * 
	 * @return
	 */
	public static int calcWeekRefreshTime() {
		long currentTime = GuaJiTime.getMillisecond();
		long reTime = GuaJiTime.getNextWeekAM0Date();
		int surplusTime = (int) ((reTime - currentTime) / 1000);
		return Math.max(surplusTime, 0);
	}
	
	/**
	 * 計算下次月刷新剩餘秒數
	 * 
	 * @return
	 */
	public static int calcMonthRefreshTime() {
		long currentTime = GuaJiTime.getMillisecond();
		long reTime = GuaJiTime.getNextMonthAM0Date();
		int surplusTime = (int) ((reTime - currentTime) / 1000);
		return Math.max(surplusTime, 0);
	}
	/**
	 * 計算離目標時間剩餘秒數
	 * @param reTime
	 * @return
	 */
	public static int calTargetTime(long reTime) {
		long currentTime = GuaJiTime.getMillisecond();
		int surplusTime = (int) ((reTime - currentTime) / 1000);
		return Math.max(surplusTime, 0);
	}

	/**
	 * gacha測試
	 */
	public static List<String> testGacha(int activityId,int drawtime) {
		List<String> awardsList = new ArrayList<>();
		GachaListCfg gachaCfg = ConfigManager.getInstance().getConfigByKey(GachaListCfg.class, activityId);
		if (gachaCfg == null) {
			awardsList.add("GachaListCfg CONFIG_NOT_FOUND");
			return awardsList;
		}
		
		if (drawtime <= 0) {
			awardsList.add("drawtime drawtime <=0");
			return awardsList;
		}
		
		// 执行抽奖逻辑
		int TIMES_TYPE_TEN = 10;
		int boxId = 0;
		int searchTimes = drawtime;
		
		for (int i = 1; i <= searchTimes; i++) {
			
			//AwardItems awards = new AwardItems();
			// 总次数
			//int totalTimes = status.getTotalTimes();
			
//			if(!status.isFirstgacha()) { // 沒首抽過
//				boxId = -1;
//				status.setFirstgacha(true);
//			} else {
			
			if ((i%TIMES_TYPE_TEN) == 0) { // 進入十抽池
				boxId = gachaCfg.getRandomBoxId(GachaListCfg.BOX_TYPE_TEN);
			} else {
				boxId = gachaCfg.getRandomBoxId(GachaListCfg.BOX_TYPE_SINGLE);
			}
			
//			}
			
			PackBoxCfg dropCfg = null;
			if (boxId == -1) {
				ItemInfo firstItem = ItemInfo.valueOf(SysBasicCfg.getInstance().getFirstgacha());
//				awards.addItem(firstItem);
				awardsList.add(firstItem.toString());
//				recordId.add(-1);
			} else {
				dropCfg = ConfigManager.getInstance().getConfigByKey(PackBoxCfg.class, boxId);
				
				if (dropCfg == null) {
					awardsList.add("CONFIG_NOT_FOUND");
					//player.sendError(protocol.getType(), Status.error.CONFIG_NOT_FOUND);
					return awardsList;
			    }
				// 掉落物品
				Item boxItem = dropCfg.getBoxDropItems().calcDropByOnly();
				
				//awards.addItem(boxItem);
				awardsList.add(boxItem.toString());
				//recordId.add(dropCfg.getId());
			}
		}
		return awardsList;
	}
	/**
	 * 當相關活動關閉,活動兌換清除相關紀錄
	 */
	public static void ActivityExchageClear(PlayerData playerData,int actId) {
		// 检测活动是否开放
		int activityId = Const.ActivityId.ACTIVITY176_Activity_Exchange_VALUE;
		ActivityTimeCfg timeConfig = ActivityUtil.getCurActivityTimeCfg(activityId);
		if (timeConfig == null || playerData == null || (!timeConfig.isActiveToEnd())) {
			return;
		}
		Activity176ExchangeStatus status = ActivityUtil.getActivityStatus(playerData, activityId, timeConfig.getStageId(),
				Activity176ExchangeStatus.class);
		
		Set<Integer> keySet = new HashSet <>(status.getExchangeInfo().keySet());
		
		for(Integer keyId : keySet) {
			if (!isActivityOpen(keyId)) { // 順便清沒有開啟的活動
				status.getExchangeInfo().remove(keyId);
			}
			if (keyId == actId) {
				status.getExchangeInfo().remove(keyId);
			}
		}
		
		playerData.updateActivity(activityId, timeConfig.getStageId());
	}
	
	public static void CycleStageInitItem(Player player,ActivityTimeCfg timeConfig,Activity191Status status) {
		if (!status.isInitItem()) {
			// 另外一場活動重置上一場活動設定
			ItemInfo item = ItemInfo.valueOf(SysBasicCfg.getInstance().getCycleStageItemCost());
			ItemEntity itemEntity = player.getPlayerData().getItemByItemId(item.getItemId());
			if (itemEntity == null) {
				item.setQuantity(SysBasicCfg.getInstance().getCycleStageRecover());
				AwardItems awardItmes = AwardItems.valueOf(item.toString());
				awardItmes.rewardTakeAffect(player,Action.ACTIVITY191_CycleStage_ItemInit);
			} else {
				if (itemEntity.getItemCount() > SysBasicCfg.getInstance().getCycleStageRecover())  {					
					long count = itemEntity.getItemCount() - SysBasicCfg.getInstance().getCycleStageRecover();
					ConsumeItems consumeItems = ConsumeItems.valueOf();
					item.setQuantity(count);
					consumeItems.addConsumeInfo(player.getPlayerData(), item);
					consumeItems.consumeTakeAffect(player, Action.ACTIVITY191_CycleStage_ItemInit);
				} else if (itemEntity.getItemCount() < SysBasicCfg.getInstance().getCycleStageRecover()) {
					long count = SysBasicCfg.getInstance().getCycleStageRecover() - itemEntity.getItemCount();
					item.setQuantity(count);
					AwardItems awardItmes = AwardItems.valueOf(item.toString());
					awardItmes.rewardTakeAffect(player,Action.ACTIVITY191_CycleStage_ItemInit);
				}
			}
			
			// 刪除交易幣(確保活動沒有舊的交易幣)
			item = ItemInfo.valueOf(SysBasicCfg.getInstance().getCycleStageCoin());
			itemEntity = player.getPlayerData().getItemByItemId(item.getItemId());
			if (itemEntity != null) {
				long count = itemEntity.getItemCount();
				ConsumeItems consumeItems = ConsumeItems.valueOf();
				item.setQuantity(count);
				consumeItems.addConsumeInfo(player.getPlayerData(), item);
				consumeItems.consumeTakeAffect(player, Action.ACTIVITY191_CycleStage_ItemInit);
			}
			
			status.setInitItem(true);
			player.getPlayerData().updateActivity(timeConfig.getActivityId(), timeConfig.getStageId());
		}
	}
	
	public static void CycleStageInitItem2(Player player,ActivityTimeCfg timeConfig,Activity196Status status) {
		if (!status.isInitItem()) {
			// 另外一場活動重置上一場活動設定
			ItemInfo item = ItemInfo.valueOf(SysBasicCfg.getInstance().getCycleStageItemCost2());
			ItemEntity itemEntity = player.getPlayerData().getItemByItemId(item.getItemId());
			if (itemEntity == null) {
				item.setQuantity(SysBasicCfg.getInstance().getCycleStageRecover2());
				AwardItems awardItmes = AwardItems.valueOf(item.toString());
				awardItmes.rewardTakeAffect(player,Action.ACTIVITY196_CycleStage_ItemInit);
			} else {
				if (itemEntity.getItemCount() > SysBasicCfg.getInstance().getCycleStageRecover2())  {					
					long count = itemEntity.getItemCount() - SysBasicCfg.getInstance().getCycleStageRecover2();
					ConsumeItems consumeItems = ConsumeItems.valueOf();
					item.setQuantity(count);
					consumeItems.addConsumeInfo(player.getPlayerData(), item);
					consumeItems.consumeTakeAffect(player, Action.ACTIVITY196_CycleStage_ItemInit);
				} else if (itemEntity.getItemCount() < SysBasicCfg.getInstance().getCycleStageRecover2()) {
					long count = SysBasicCfg.getInstance().getCycleStageRecover2() - itemEntity.getItemCount();
					item.setQuantity(count);
					AwardItems awardItmes = AwardItems.valueOf(item.toString());
					awardItmes.rewardTakeAffect(player,Action.ACTIVITY196_CycleStage_ItemInit);
				}
			}
			
			// 刪除交易幣(確保活動沒有舊的交易幣)
			item = ItemInfo.valueOf(SysBasicCfg.getInstance().getCycleStageCoin2());
			itemEntity = player.getPlayerData().getItemByItemId(item.getItemId());
			if (itemEntity != null) {
				long count = itemEntity.getItemCount();
				ConsumeItems consumeItems = ConsumeItems.valueOf();
				item.setQuantity(count);
				consumeItems.addConsumeInfo(player.getPlayerData(), item);
				consumeItems.consumeTakeAffect(player, Action.ACTIVITY196_CycleStage_ItemInit);
			}
			
			status.setInitItem(true);
			player.getPlayerData().updateActivity(timeConfig.getActivityId(), timeConfig.getStageId());
		}
	}
	
	public static void SingleBossInitItem(Player player,ActivityTimeCfg timeConfig,Activity193Status status) {
		if (!status.isInitItem()) {			
			// 刪除交易幣(確保活動沒有舊的交易幣)
			ItemInfo item = ItemInfo.valueOf(SysBasicCfg.getInstance().getSingleBossCoin());
			ItemEntity itemEntity = player.getPlayerData().getItemByItemId(item.getItemId());
			if (itemEntity != null) {
				long count = itemEntity.getItemCount();
				if (count > 0) {
					ConsumeItems consumeItems = ConsumeItems.valueOf();
					item.setQuantity(count);
					consumeItems.addConsumeInfo(player.getPlayerData(), item);
					consumeItems.consumeTakeAffect(player, Action.ACTIVITY193_Single_Boss_InitItem);
				}
			}
			status.setPlayerId(player.getId());
			status.setName(player.getName());
			status.setInitItem(true);
			player.getPlayerData().updateActivity(timeConfig.getActivityId(), timeConfig.getStageId());
		}
	}
	
	/**
	 * 每天補上活動coin
	 * @param player
	 */
	public static void CycleStageDailyReset(Player player) {
		// 隔天重置
		ItemInfo item = ItemInfo.valueOf(SysBasicCfg.getInstance().getCycleStageItemCost());
		ItemEntity itemEntity = player.getPlayerData().getItemByItemId(item.getItemId());
		if (itemEntity == null) {
			item.setQuantity(SysBasicCfg.getInstance().getCycleStageRecover());
			AwardItems awardItmes = AwardItems.valueOf(item.toString());
			awardItmes.rewardTakeAffect(player,Action.ACTIVITY191_CycleStage_DailyRest);
		} else {
			if (itemEntity.getItemCount() < SysBasicCfg.getInstance().getCycleStageRecover()) {
				long count = SysBasicCfg.getInstance().getCycleStageRecover() - itemEntity.getItemCount();
				item.setQuantity(count);
				AwardItems awardItmes = AwardItems.valueOf(item.toString());
				awardItmes.rewardTakeAffect(player,Action.ACTIVITY191_CycleStage_DailyRest);
			}
		}
	}
	
	public static void CycleStageClearItem(Player player) {
		ItemInfo item = ItemInfo.valueOf(SysBasicCfg.getInstance().getCycleStageItemCost());
		ItemEntity itemEntity = player.getPlayerData().getItemByItemId(item.getItemId());
		if (itemEntity != null) {
			if (itemEntity.getItemCount() > 0) {
				long count = itemEntity.getItemCount();
				ConsumeItems consumeItems = ConsumeItems.valueOf();
				item.setQuantity(count);
				consumeItems.addConsumeInfo(player.getPlayerData(), item);
				consumeItems.consumeTakeAffect(player, Action.ACTIVITY191_CycleStage_ItemClear);
			}
		}
		item = ItemInfo.valueOf(SysBasicCfg.getInstance().getCycleStageCoin());
		itemEntity = player.getPlayerData().getItemByItemId(item.getItemId());
		if (itemEntity != null) {
			if (itemEntity.getItemCount() > 0) {
				long count = itemEntity.getItemCount();
				ConsumeItems consumeItems = ConsumeItems.valueOf();
				item.setQuantity(count);
				consumeItems.addConsumeInfo(player.getPlayerData(), item);
				consumeItems.consumeTakeAffect(player, Action.ACTIVITY191_CycleStage_ItemClear);
			}
		}
	}
	
	/**
	 * 每天補上活動coin
	 * @param player
	 */
	public static void CycleStageDailyReset2(Player player) {
		// 隔天重置
		ItemInfo item = ItemInfo.valueOf(SysBasicCfg.getInstance().getCycleStageItemCost2());
		ItemEntity itemEntity = player.getPlayerData().getItemByItemId(item.getItemId());
		if (itemEntity == null) {
			item.setQuantity(SysBasicCfg.getInstance().getCycleStageRecover2());
			AwardItems awardItmes = AwardItems.valueOf(item.toString());
			awardItmes.rewardTakeAffect(player,Action.ACTIVITY196_CycleStage_DailyRest);
		} else {
			if (itemEntity.getItemCount() < SysBasicCfg.getInstance().getCycleStageRecover2()) {
				long count = SysBasicCfg.getInstance().getCycleStageRecover2() - itemEntity.getItemCount();
				item.setQuantity(count);
				AwardItems awardItmes = AwardItems.valueOf(item.toString());
				awardItmes.rewardTakeAffect(player,Action.ACTIVITY196_CycleStage_DailyRest);
			}
		}
	}
	
	public static void CycleStageClearItem2(Player player) {
		ItemInfo item = ItemInfo.valueOf(SysBasicCfg.getInstance().getCycleStageItemCost2());
		ItemEntity itemEntity = player.getPlayerData().getItemByItemId(item.getItemId());
		if (itemEntity != null) {
			if (itemEntity.getItemCount() > 0) {
				long count = itemEntity.getItemCount();
				ConsumeItems consumeItems = ConsumeItems.valueOf();
				item.setQuantity(count);
				consumeItems.addConsumeInfo(player.getPlayerData(), item);
				consumeItems.consumeTakeAffect(player, Action.ACTIVITY196_CycleStage_ItemClear);
			}
		}
		item = ItemInfo.valueOf(SysBasicCfg.getInstance().getCycleStageCoin2());
		itemEntity = player.getPlayerData().getItemByItemId(item.getItemId());
		if (itemEntity != null) {
			if (itemEntity.getItemCount() > 0) {
				long count = itemEntity.getItemCount();
				ConsumeItems consumeItems = ConsumeItems.valueOf();
				item.setQuantity(count);
				consumeItems.addConsumeInfo(player.getPlayerData(), item);
				consumeItems.consumeTakeAffect(player, Action.ACTIVITY196_CycleStage_ItemClear);
			}
		}
	}
	
	public static boolean addRechargeBounce192(int type, int term, PlayerData playerData) {
		ActivityTimeCfg accRechargeTimeCfg = getCurActivityTimeCfg(Const.ActivityId.ACTIVITY192_RechargeBounce_VALUE);
		if (accRechargeTimeCfg != null && (!accRechargeTimeCfg.isEnd())) {
			int stageId = accRechargeTimeCfg.getStageId();
			int activityId = accRechargeTimeCfg.getActivityId();
			Activity192Status status = ActivityUtil.getActivityStatus(playerData, activityId, stageId,
					Activity192Status.class);
			Player player = playerData.getPlayer();
			
			// check timeindx			
			boolean save = status.checkTimeIndex(player.getPlatformId());
			
			if (type == GsConst.RechargeBounceType.Deposit) {
				// GsConst.RechargeBounceType.Single(計算累儲順便比對單筆消費是否符合)
				if ((player != null)&&(status.getTimeIndex(type) > 0)) {
					status.addDeposit(term);
					if (status.getTimeIndex(GsConst.RechargeBounceType.Single) > 0) {
						Map<Integer,RechargeBounceCfg> aMap = RechargeBounceCfg.getSingleMark(player.getPlatformId(),status.getTimeIndex(GsConst.RechargeBounceType.Single));
						// 需要單筆整筆吻合才可以達成
//						if (aMap.containsKey(term)) {
//							status.addSingleCount(aMap.get(term).getId());
//						}
						// 只需消費單筆大於等於就可以達成
						for (Map.Entry<Integer,RechargeBounceCfg> entry : aMap.entrySet()) {
							if (term >=entry.getKey()) {
								status.addSingleCount(entry.getValue().getId());
							}
						}
					}
					save = true;
				}
				
			} else if (type == GsConst.RechargeBounceType.consums) {
				if (status.getTimeIndex(type) > 0) {
					status.addConsume(term);
					save = true;
				}
			}
			if (save) {
				playerData.updateActivity(Const.ActivityId.ACTIVITY192_RechargeBounce_VALUE, stageId, true);
			}
		}
		return true;
	}
	
	/**
	 * 活動存檔創建時做的動作 
	 */
	public static void ActivityCreateDo(PlayerData playerData,int activityId) {
		if (activityId == Const.ActivityId.ACTIVITY191_CycleStage_VALUE) {
			GodSeaShopEntity godseaShopEntity = playerData.loadGodSeaShopEntity();
			if (godseaShopEntity != null) {
				// 刷新神海商店
				GodSeaMarket.getInstance().shopItemInfo(playerData.getPlayer(), godseaShopEntity,false);
			}
		}
		
		if (activityId == Const.ActivityId.ACTIVITY196_CycleStage_Part2_VALUE) {
			CycleStageShopEntity cyclestageShopEntity = playerData.loadCycleStageShopEntity();
			if (cyclestageShopEntity != null) {
				// 刷新循環商店
				CycleStageMarket.getInstance().shopItemInfo(playerData.getPlayer(), cyclestageShopEntity,false);
			}
		}
	}
	
	/**
	 * 
	 * @param player
	 * @param timeConfig
	 * @param status
	 */
	
	public static void SeaSonTowerInit(Player player,ActivityTimeCfg timeConfig,Activity194Status status,int timeIdx ) {
		if ((timeIdx != 0)&&(status.getTimeIndex() != timeIdx)) {			
			if (status.getTimeIndex() != 0) {
				
				int decFloor = SysBasicCfg.getInstance().getSeasonTowerFloorContrl();
				int newFloor = (status.getRank() > decFloor) ? (status.getRank() - decFloor) : 0;
				
				status.setRank(newFloor);
				status.setNowfloor(newFloor+1);
				status.setDoneTime(0);
				status.clearAwardrecord();
				//status.setChooseFloor(true);
			}
			status.setPlayerId(player.getId());
			status.setName(player.getName());
			status.setTimeIndex(timeIdx);
			player.getPlayerData().updateActivity(timeConfig.getActivityId(), timeConfig.getStageId());
		}
	}
}
