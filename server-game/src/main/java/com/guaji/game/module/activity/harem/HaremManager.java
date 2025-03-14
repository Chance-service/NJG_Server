package com.guaji.game.module.activity.harem;

import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.guaji.config.ConfigManager;
import org.guaji.db.DBManager;
import org.guaji.os.GuaJiTime;

import com.guaji.game.protocol.Activity2.HPHaremInfo;
import com.guaji.game.config.ActivityCfg;
import com.guaji.game.config.HaremConstCfg;
import com.guaji.game.config.HaremDropCfg;
import com.guaji.game.config.ActivityCfg.ActivityItem;
import com.guaji.game.entity.HaremActivityEntity;
import com.guaji.game.item.AwardItems;
import com.guaji.game.item.ConsumeItems;
import com.guaji.game.item.ItemInfo;
import com.guaji.game.log.BehaviorLogger.Action;
import com.guaji.game.manager.MailManager;
import com.guaji.game.player.Player;
import com.guaji.game.player.PlayerData;
import com.guaji.game.util.ActivityUtil;
import com.guaji.game.util.GsConst;
import com.guaji.game.protocol.Const;
import com.guaji.game.protocol.Const.changeType;
import com.guaji.game.protocol.Mail;
import com.guaji.game.protocol.Status;

/**
 * 百花美人活动管理器
 */
public class HaremManager {

	static final int TIMES_TYPE_SINGLE = 1;
	static final int TIMES_TYPE_COMBO = 10;

	/**
	 * 限定活动是否开启
	 */
	static boolean isStrictOpen(Player player, HaremConstCfg cfg) {
		// 时间限制
		long start = cfg.getStartDate().getTime();
		long end = cfg.getEndDate().getTime();
		long now = GuaJiTime.getMillisecond();
		if (now > start && now <= end)
			return true;
		return false;
	}

	/**
	 * 限定活动剩余时间
	 */
	static int getStrictLeftTime(Player player, HaremConstCfg cfg) {
		if (isStrictOpen(player, cfg)) {
			int end = (int) (cfg.getEndDate().getTime() / 1000);
			int now = GuaJiTime.getSeconds();
			return end - now;
		}
		return 0;
	}

	/**
	 * 新手限定活动是否开启
	 */
	public static boolean isNewStrictOpen(Player player, HaremActivityEntity entity) {
		// 等级限制
		if (!isNewStrictLevelEnough(player.getLevel()))
			return false;
		// 时间限制
		int end = entity.getNewStrictEndTime();
		int now = GuaJiTime.getSeconds();
		if (end < now)
			return false;
		return true;
	}

	/**
	 * 新手限定活动是否开启
	 */
	public static boolean isNewStrictOpen(HaremActivityEntity entity) {

		// 时间限制
		int end = entity.getNewStrictEndTime();
		int now = GuaJiTime.getSeconds();
		if (end < now)
			return false;
		return true;

	}

	/**
	 * 新手限定活动剩余时间
	 */
	static int getNewStrictLeftTime(Player player, HaremActivityEntity entity) {
		if (isNewStrictOpen(entity)) {
			int end = entity.getNewStrictEndTime();
			int now = GuaJiTime.getSeconds();
			return end - now;
		}
		return 0;
	}

	/**
	 * 抽奖和发放
	 */
	static List<AwardItems> drawAndGive(Player player, HaremActivityEntity entity, int haremType, int drawTimes) {
		int totalTimes = 0;
		int daytotalTimes = 0;
		switch (haremType) {
		case Const.HaremType.HAREM_TYPE_COMMON_VALUE:
			totalTimes = entity.getCommonTotalTimes();
			entity.setCommonTotalTimes(totalTimes + drawTimes);
			daytotalTimes = entity.getCommonDayTotalTimes();
			entity.setCommonDayTotalTimes(daytotalTimes + drawTimes);
			break;
		case Const.HaremType.HAREM_TYPE_ADVANCED_VALUE:
			totalTimes = entity.getAdvancedTotalTimes();
			entity.setAdvancedTotalTimes(totalTimes + drawTimes);
			break;
		case Const.HaremType.HAREM_TYPE_MIDDLE_VALUE:
			totalTimes = entity.getMiddleTotalTimes();
			entity.setMiddleTotalTimes(totalTimes + drawTimes);
			break;
		case Const.HaremType.HAREM_TYPE_STRICT_VALUE:
			totalTimes = entity.getStrictTotalTimes();
			entity.setStrictTotalTimes(totalTimes + drawTimes);
			break;
		case Const.HaremType.HAREM_TYPE_NEW_STRICT_VALUE:
			totalTimes = entity.getNewStrictTotalTimes();
			entity.setNewStrictTotalTimes(totalTimes + drawTimes);
			break;
		case Const.HaremType.HAREM_TYPE_LIMIT_VALUE:
			totalTimes = entity.getLimitTotalTimes();
			entity.setLimitTotalTimes(totalTimes + drawTimes);
			break;
		}
		// 掉落物品
		List<AwardItems> awardItemList = HaremDropCfg.dropReward(haremType, totalTimes, drawTimes);
		// 奖励物品
		for (AwardItems awardItems : awardItemList) {
			if (awardItems != null) {
				awardItems.rewardTakeAffectAndPush(player, Action.HAREM_ACTIVITY, 0);
			}
		}
		return awardItemList;
	}
	
	static void resetExchage(Player player, HaremActivityEntity harem) { 
	
	}

	/**
	 * 检测并消耗，成功返回0，否则返回错误码 如果有免费抽奖机会，直接消耗 先算出抽奖券和元宝的需求
	 */
	static int checkAndConsume(Player player, HaremActivityEntity harem, int haremType, int drawTimes) {
		Map<Object, HaremConstCfg> HaremConstCfgMap = ConfigManager.getInstance().getConfigMap(HaremConstCfg.class);
		HaremConstCfg cfg = HaremConstCfgMap.get(haremType);
		/** 单次抽奖券消耗 **/
		String costItems = cfg.getItemCost();
		ConsumeItems consumeItems = ConsumeItems.valueOf();
		List<ItemInfo> itemInfoList = ItemInfo.valueListOf(costItems);

		int freeCd = cfg.getFreeCd();
		boolean isCost;
		int gold = 0;
		if (drawTimes == TIMES_TYPE_COMBO) {
			/** 抽奖券需要10个 **/
			for (Iterator<ItemInfo> iterator = itemInfoList.iterator(); iterator.hasNext();) {
				ItemInfo itemInfo = iterator.next();
				itemInfo.setQuantity(itemInfo.getQuantity() * TIMES_TYPE_COMBO);
			}
			gold = cfg.getTenTimesCost();
		} else if (drawTimes == TIMES_TYPE_SINGLE) {
			/** 进行免费消耗 **/
			long lastFreeTime = 0;// 上次免费时间
			int freeChance = 0;// 还有几次免费机会
			int now = GuaJiTime.getSeconds();
			switch (haremType) {
			case Const.HaremType.HAREM_TYPE_COMMON_VALUE:
				lastFreeTime = harem.getLastCommonFreeTime();
				freeChance = harem.getCommonFreeChance();

				if (lastFreeTime != 0) {
					lastFreeTime = Math.max(freeCd - (now - lastFreeTime), 0);
				}
				if (lastFreeTime == 0 && freeChance >= 1) {
					harem.setLastCommonFreeTime(now);
					harem.setCommonFreeChance(freeChance - 1);
					return 0;
				}
				break;
			case Const.HaremType.HAREM_TYPE_ADVANCED_VALUE:
				lastFreeTime = harem.getLastAdvancedFreeTime();
				freeChance = harem.getAdvancedFreeChance();
				if (lastFreeTime != 0) {
					lastFreeTime = Math.max(freeCd - (now - lastFreeTime), 0);
				}
				if (lastFreeTime == 0 && freeChance >= 1) {
					harem.setLastAdvancedFreeTime(now);
					harem.setAdvancedFreeChance(freeChance - 1);
					return 0;
				}
				break;
			case Const.HaremType.HAREM_TYPE_MIDDLE_VALUE:
				lastFreeTime = harem.getLastMiddleFreeTime();
				freeChance = harem.getMiddleFreeChance();
				if (lastFreeTime != 0) {
					lastFreeTime = Math.max(freeCd - (now - lastFreeTime), 0);
				}
				if (lastFreeTime == 0 && freeChance >= 1) {
					harem.setLastMiddleFreeTime(now);
					harem.setMiddleFreeChance(freeChance - 1);
					return 0;
				}
				break;
			case Const.HaremType.HAREM_TYPE_STRICT_VALUE:
				lastFreeTime = harem.getLastStrictFreeTime();
				freeChance = harem.getStrictFreeChance();
				if (lastFreeTime != 0) {
					lastFreeTime = Math.max(freeCd - (now - lastFreeTime), 0);
				}
				if (lastFreeTime == 0 && freeChance >= 1) {
					harem.setLastStrictFreeTime(now);
					harem.setStrictFreeChance(freeChance - 1);
					return 0;
				}
				break;
			case Const.HaremType.HAREM_TYPE_NEW_STRICT_VALUE:
				lastFreeTime = harem.getLastNewStrictFreeTime();
				freeChance = harem.getNewStrictFreeChance();
				if (lastFreeTime != 0) {
					lastFreeTime = Math.max(freeCd - (now - lastFreeTime), 0);
				}
				if (lastFreeTime == 0 && freeChance >= 1) {
					harem.setLastNewStrictFreeTime(now);
					harem.setNewStrictFreeChance(freeChance - 1);
					return 0;
				}
				break;
			case Const.HaremType.HAREM_TYPE_LIMIT_VALUE:
				lastFreeTime = harem.getLastLimitFreeTime();
				freeChance = harem.getLimitFreeChance();
				if (lastFreeTime != 0) {
					lastFreeTime = Math.max(freeCd - (now - lastFreeTime), 0);
				}
				if (lastFreeTime == 0 && freeChance >= 1) {
					harem.setLastLimitFreeTime(now);
					harem.setLimitFreeChance(freeChance - 1);
					return 0;
				}
				break;
			}
			/** 钻石消耗 **/
			gold = cfg.getOneTimeCost();
		} else {
			if (haremType != Const.HaremType.HAREM_TYPE_COMMON_VALUE)
				return Status.error.ITEM_NOT_ENOUGH_VALUE;

			if (drawTimes > (cfg.getMaxDayTotalTimes() - harem.getCommonDayTotalTimes())) {
				return Status.error.ITEM_NOT_ENOUGH_VALUE;
			}

			/** HAREM_TYPE_COMMON_VALUE 没有免费次数 **/
			gold = cfg.getOneTimeCost() * drawTimes;
		}

		// 低级不能走消耗卷
		if (haremType != Const.HaremType.HAREM_TYPE_COMMON_VALUE) {
			/** 优先判断抽奖券消耗 **/
			isCost = consumeItems.addConsumeInfo(player.getPlayerData(), itemInfoList);
			if (isCost && consumeItems.checkConsume(player)) {
				if (!consumeItems.consumeTakeAffect(player, Action.HAREM_ACTIVITY)) {
					return Status.error.ITEM_NOT_ENOUGH_VALUE;
				}
				return 0;
			}
		}
		if (haremType == Const.HaremType.HAREM_TYPE_COMMON_VALUE) {
			/** 钱 币消耗 **/
			if (player.getCoin() < gold)
				return Status.error.GOLD_NOT_ENOUGH_VALUE;
			player.consumeCoin(gold, Action.HAREM_ACTIVITY);
			ConsumeItems.valueOf(changeType.CHANGE_COIN, gold).pushChange(player);
		} else {
			/** 钻石消耗 **/
			if (player.getGold() < gold)
				return Status.error.GOLD_NOT_ENOUGH_VALUE;
			player.consumeGold(gold, Action.HAREM_ACTIVITY);
			ConsumeItems.valueOf(changeType.CHANGE_GOLD, gold).pushChange(player);
		}

		return 0;
	}

	/**
	 * 获取活动信息
	 */
	public static HPHaremInfo.Builder getHaremInfo(Player player, int haremType) {
		HaremActivityEntity entity = player.getPlayerData().getHaremActivityEntity();
		Map<Object, HaremConstCfg> HaremConstCfgMap = ConfigManager.getInstance().getConfigMap(HaremConstCfg.class);
		HaremConstCfg cfg = HaremConstCfgMap.get(haremType);
		long freeCd = cfg.getFreeCd();// 免费抽奖CD时间
		long lastFreeTime = 0;// 上次免费抽奖时间
		int freeChance = 0;// 免费抽奖机会
		int totalTimes = 0;// 分支活动抽奖总次数
		int now = GuaJiTime.getSeconds();
		switch (haremType) {
		case Const.HaremType.HAREM_TYPE_COMMON_VALUE:
			lastFreeTime = entity.getLastCommonFreeTime();
			freeChance = entity.getCommonFreeChance();
			totalTimes = entity.getCommonTotalTimes();
			break;
		case Const.HaremType.HAREM_TYPE_ADVANCED_VALUE:
			lastFreeTime = entity.getLastAdvancedFreeTime();
			freeChance = entity.getAdvancedFreeChance();
			totalTimes = entity.getAdvancedTotalTimes();
			break;
		case Const.HaremType.HAREM_TYPE_MIDDLE_VALUE: {
			lastFreeTime = entity.getLastMiddleFreeTime();
			freeChance = entity.getMiddleFreeChance();
			totalTimes = entity.getMiddleTotalTimes();
		}
			break;
		case Const.HaremType.HAREM_TYPE_STRICT_VALUE:
			if (HaremManager.isStrictOpen(player, cfg)) {
				lastFreeTime = entity.getLastStrictFreeTime();
				freeChance = entity.getStrictFreeChance();
				totalTimes = entity.getStrictTotalTimes();
			}
			break;
		case Const.HaremType.HAREM_TYPE_NEW_STRICT_VALUE:
			if (HaremManager.isNewStrictOpen(entity)) {
				lastFreeTime = entity.getLastNewStrictFreeTime();
				freeChance = entity.getNewStrictFreeChance();
				totalTimes = entity.getNewStrictTotalTimes();
			}
			break;
		case Const.HaremType.HAREM_TYPE_LIMIT_VALUE:
			if (HaremManager.isStrictOpen(player, cfg)) {
				lastFreeTime = entity.getLastLimitFreeTime();
				freeChance = entity.getLimitFreeChance();
				totalTimes = entity.getLimitTotalTimes();
			}
			break;

		}

		HPHaremInfo.Builder builder = HPHaremInfo.newBuilder();
		// 免费CD时间
		if (lastFreeTime == 0) {
			builder.setFreeCd(0);
		} else {
			freeCd = Math.max(freeCd - (now - lastFreeTime), 0);
			builder.setFreeCd(freeCd);
		}
		builder.setHaremType(haremType);
		// 分支活动剩余时间
		int leftTime = 0;
		switch (haremType) {
		case Const.HaremType.HAREM_TYPE_STRICT_VALUE:
			// 活动开启
			leftTime = HaremManager.getStrictLeftTime(player, cfg);
			break;
		case Const.HaremType.HAREM_TYPE_LIMIT_VALUE:
			// 活动开启
			leftTime = HaremManager.getStrictLeftTime(player, cfg);
			break;
		case Const.HaremType.HAREM_TYPE_NEW_STRICT_VALUE:
			// 新手限定活动
			// leftTime = HaremManager.getStrictLeftTime(player, cfg);
			leftTime = HaremManager.getNewStrictLeftTime(player, entity);
			break;
		default:
			Date endDate = cfg.getEndDate();
			leftTime = (int) (endDate.getTime() / 1000) - now;
			break;
		}
		builder.setLeftTime(leftTime);
		// builder.setDayLeftTimes(value)
		builder.setFreeChance(freeChance);
		builder.setDayLeftTimes(cfg.getMaxDayTotalTimes() - entity.getCommonDayTotalTimes());
		int luckyTime = totalTimes % 10;
		/** 小于10的整数倍是幸运次数那就是10 **/
		if (totalTimes < HaremDropCfg.WILL_HIT_FACTOR) {
			luckyTime = HaremDropCfg.WILL_HIT_FACTOR - totalTimes;
		} else {
			luckyTime = HaremDropCfg.WILL_HIT_FACTOR - totalTimes % 10;
		}
		// 如果总数小于10
		builder.setLuckyTime(luckyTime);
		return builder;
	}

	/**
	 * 发送邮件提前通知活动即将开启
	 */
	public static void sendNoticeMail(int playerId, int level) {
		int limitLevel = getNewStrictLimitLevel();
		int noticeLevel = limitLevel - 5;
		if (level == noticeLevel) {
			MailManager.createMail(playerId, Mail.MailType.Normal_VALUE, GsConst.MailId.HAREM_NEW_STRICT_MAIL, "",
					null);
		}
	}

	/**
	 * 等级是否足够
	 */
	public static boolean isNewStrictLevelEnough(int level) {
		int limitLevel = getNewStrictLimitLevel();
		if (level >= limitLevel)
			return true;
		return false;
	}

	/**
	 * 获取新手限定活动限制等级(默认20)
	 */
	private static int getNewStrictLimitLevel() {
		ActivityItem item = ActivityCfg.getActivityItem(Const.ActivityId.HAREM_VALUE);
		Map<String, Object> map = item.getParamsMap();
		int limitLevel = 20;
		Object newStrictLimitLevel = map.get("newStrictLimitLevel");
		if (null != newStrictLimitLevel) {
			limitLevel = (int) newStrictLimitLevel;
		}
		return limitLevel;
	}

	/**
	 * 获取新手限定活动持续时间(单位：秒)
	 */
	private static int getNewStrictPeriod() {
		ActivityItem item = ActivityCfg.getActivityItem(Const.ActivityId.NEW_ND_VALUE);
		Map<String, Object> map = item.getParamsMap();
		Object newStrictPeriod = map.get("keepDays");
		if (null != newStrictPeriod) {
			return (int) newStrictPeriod * 60 * 60 * 24;
		}
		return 0;
	}

	/**
	 * 初始化新手限定活动结束时间
	 */
	public static int initNewStrictEndTime() {
		
		/*
		Calendar calendar = GuaJiTime.getCalendar();
		calendar.setTime(registerDate);
		calendar.add(Calendar.DATE, delayDays);
		Date stopDate = calendar.getTime();
		int leftTime = (int) ((GuaJiTime.getAM0Date(stopDate).getTime() - GuaJiTime.getMillisecond()) / 1000);
		return Math.max(leftTime, 0);
		 */	
		
		
		return GuaJiTime.getSeconds() + getNewStrictPeriod();
	}

	/**
	 * 初始化新手限定活动
	 */
	public static void initNewStrict(int level, HaremActivityEntity entity,Date regDate,int mergeTime) {
		Map<Object, HaremConstCfg> HaremConstCfgMap = ConfigManager.getInstance().getConfigMap(HaremConstCfg.class);
		HaremConstCfg constCfg = HaremConstCfgMap.get(Const.HaremType.HAREM_TYPE_NEW_STRICT_VALUE);
		if (null != constCfg) {
			if (entity.getNewStrictEndTime() == 0) {
				if (HaremManager.isNewStrictLevelEnough(level)) {
					entity.setNewStrictFreeChance(constCfg.getFreeChance());
					entity.setNewStrictEndTime(ActivityUtil.clacStrictEndTime(regDate,mergeTime));
					entity.notifyUpdate(false);
				}
			}
		}
	}

	/**
	 * 整个活动初始化
	 */
	public static HaremActivityEntity initHaremEntity(Player player) {
		Map<Object, HaremConstCfg> HaremConstCfgMap = ConfigManager.getInstance().getConfigMap(HaremConstCfg.class);
		HaremActivityEntity entity = new HaremActivityEntity(player.getId());
		HaremConstCfg constCfg = HaremConstCfgMap.get(Const.HaremType.HAREM_TYPE_COMMON_VALUE);
		if (constCfg != null) {
			entity.setCommonFreeChance(constCfg.getFreeChance());
		}
		constCfg = HaremConstCfgMap.get(Const.HaremType.HAREM_TYPE_ADVANCED_VALUE);
		if (constCfg != null) {
			entity.setAdvancedFreeChance(constCfg.getFreeChance());
		}
		constCfg = HaremConstCfgMap.get(Const.HaremType.HAREM_TYPE_MIDDLE_VALUE);
		if (constCfg != null) {
			entity.setMiddleFreeChance(constCfg.getFreeChance());
		}

		constCfg = HaremConstCfgMap.get(Const.HaremType.HAREM_TYPE_STRICT_VALUE);
		if (constCfg != null) {
			entity.setStrictFreeChance(constCfg.getFreeChance());
		}
		constCfg = HaremConstCfgMap.get(Const.HaremType.HAREM_TYPE_NEW_STRICT_VALUE);

//		if (constCfg != null && HaremManager.isNewStrictLevelEnough(player.getLevel())) {
		if (constCfg != null) {
			entity.setNewStrictFreeChance(constCfg.getFreeChance());
			entity.setNewStrictEndTime(ActivityUtil.clacStrictEndTime(player.getPlayerData().getPlayerEntity().getCreateTime(),player.getPlayerData().getPlayerEntity().getMergeTime()));
		}
		constCfg = HaremConstCfgMap.get(Const.HaremType.HAREM_TYPE_LIMIT_VALUE);
		if (constCfg != null) {
			entity.setLimitFreeChance(constCfg.getFreeChance());
		}
		DBManager.getInstance().create(entity);
		entity.notifyUpdate(false);
		return entity;
	}
}
