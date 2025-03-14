package com.guaji.game.module;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.TreeSet;

import org.guaji.annotation.MessageHandlerAnno;
import org.guaji.config.ConfigManager;
import org.guaji.msg.Msg;
import org.guaji.net.GuaJiNetManager;
import org.guaji.net.protocol.Protocol;
import org.guaji.os.MyException;
import org.guaji.xid.GuaJiXID;
import org.guaji.os.GuaJiTime;

import com.guaji.game.GsApp;
import com.guaji.game.config.ActivityTimeCfg;
import com.guaji.game.config.CompoundCfg;
import com.guaji.game.config.ElementStampCfg;
import com.guaji.game.config.EquipCfg;
import com.guaji.game.config.ItemCfg;
import com.guaji.game.config.NewMapCfg;
import com.guaji.game.config.SysBasicCfg;
import com.guaji.game.config.WordsExchangeSpecialCfg;
import com.guaji.game.entity.ArchiveEntity;
import com.guaji.game.entity.EquipEntity;
import com.guaji.game.entity.HeroTokenTaskEntity;
import com.guaji.game.entity.ItemEntity;
import com.guaji.game.entity.MutualEntity;
import com.guaji.game.entity.PlayerEntity;
import com.guaji.game.entity.StateEntity;
import com.guaji.game.item.AwardItems;
import com.guaji.game.item.ConsumeItems;
import com.guaji.game.item.ItemInfo;
import com.guaji.game.log.BehaviorLogger;
import com.guaji.game.log.BehaviorLogger.Action;
import com.guaji.game.log.BehaviorLogger.Params;
import com.guaji.game.log.BehaviorLogger.Source;
import com.guaji.game.manager.ArenaManager;
import com.guaji.game.module.activity.foreverCard.ForeverCardStatus;
import com.guaji.game.module.quest.QuestEventBus;
import com.guaji.game.module.sevendaylogin.SevenDayQuestEventBus;
import com.guaji.game.player.Player;
import com.guaji.game.player.PlayerModule;
import com.guaji.game.util.ActivityUtil;
import com.guaji.game.util.AwardUtil;
import com.guaji.game.util.BuilderUtil;
import com.guaji.game.util.GsConst;
import com.guaji.game.util.ItemUtil;
import com.guaji.game.util.PlayerUtil;
import com.guaji.game.util.TapDBUtil;
import com.guaji.game.util.TapDBUtil.TapDBSource;
import com.guaji.game.util.WeightUtil;
import com.guaji.game.protocol.Const;
import com.guaji.game.protocol.Const.HeroTokenMsgType;
import com.guaji.game.protocol.Const.QuestEventType;
import com.guaji.game.protocol.Const.SevenDayEventType;
import com.guaji.game.protocol.Const.changeType;
import com.guaji.game.protocol.Const.itemType;
import com.guaji.game.protocol.Const.toolType;
import com.guaji.game.protocol.HP;
import com.guaji.game.protocol.ItemOpr.ExchangeItem;
import com.guaji.game.protocol.ItemOpr.HPItemExchange;
import com.guaji.game.protocol.ItemOpr.HPItemExchangeRet;
import com.guaji.game.protocol.ItemOpr.HPItemSell;
import com.guaji.game.protocol.ItemOpr.HPItemUse;
import com.guaji.game.protocol.ItemOpr.HPItemUseRet;
import com.guaji.game.protocol.Reward.RewardInfo;
import com.guaji.game.protocol.Reward.RewardItem;
import com.guaji.game.protocol.Status;

/**
 * 物品模块
 * 
 * @author hawk
 */
public class PlayerItemModule extends PlayerModule {
	/**
	 * 构造函数
	 * 
	 * @param player
	 */
	public PlayerItemModule(Player player) {
		super(player);

		listenProto(HP.code.ITEM_USE_C_VALUE);
		//listenProto(HP.code.ITEM_SELL_C);
		listenProto(HP.code.ITEM_EXCHANGE_C_VALUE);
	}

	/**
	 * 更新
	 * 
	 * @return
	 */
	@Override
	public boolean onTick() {
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
		// 宝石等级
		if (eventType == QuestEventType.GEM_LEVEL) {
			List<ItemEntity> itemEntities = player.getPlayerData().getItemEntities();
			// 任务宝石等级相关判断
			for (ItemEntity eachEntity : itemEntities) {
				// 是否宝石
				if (ItemUtil.isGem(eachEntity.getItemId())) {
					// 推送宝石升级事件
					QuestEventBus.fireQuestEvent(QuestEventType.GEM_LEVEL, ItemUtil.calcGemLevel(eachEntity.getItemId(), 2), player.getXid());
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
		if (protocol.checkType(HP.code.ITEM_USE_C)) {
			// 使用道具
			onItemUse(protocol.getType(), protocol.parseProtocol(HPItemUse.getDefaultInstance()));
			return true;
		} else if (protocol.checkType(HP.code.ITEM_SELL_C)) {
			// 道具出售
			//onItemSell(protocol.parseProtocol(HPItemSell.getDefaultInstance()));
			return true;
		} else if (protocol.checkType(HP.code.ITEM_EXCHANGE_C_VALUE)) {
			// 道具出售
			onItemExchange(protocol.parseProtocol(HPItemExchange.getDefaultInstance()));
			return true;
		}
		return super.onProtocol(protocol);
	}

	/**
	 * 玩家登陆处理(数据同步)
	 */
	@Override
	protected boolean onPlayerLogin() {
		//player.getPlayerData().loadItemEntities();
		player.getPlayerData().syncDressedItemInfo();

		return true;
	}

	@Override
	protected boolean onPlayerAssemble() {
		int activityId = Const.ActivityId.ACTIVITY191_CycleStage_VALUE;
		ActivityTimeCfg activityTimeCfg = ActivityUtil.getCurActivityTimeCfg(activityId);
		if (activityTimeCfg == null) {
			ActivityUtil.CycleStageClearItem(player);
		}
		
		activityId = Const.ActivityId.ACTIVITY196_CycleStage_Part2_VALUE;
		activityTimeCfg = ActivityUtil.getCurActivityTimeCfg(activityId);
		if (activityTimeCfg == null) {
			ActivityUtil.CycleStageClearItem2(player);
		}
		// 同步物品信息
		player.getPlayerData().syncUnDressedItemInfo();

		// 检测公测字集齐的日期情况
//		StateEntity stateEntity = player.getPlayerData().getStateEntity();
//		if (stateEntity != null && stateEntity.getGongceWordDay() == null) {
//			Date completeDate = player.getPlayerData().getGongceCompleteDate();
//			if (completeDate != null) {
//				stateEntity.setGongceWordDay(completeDate);
//				stateEntity.notifyUpdate(true);
//				player.getPlayerData().syncStateInfo();
//			}
//		}
		return true;
	}

	/**
	 * 使用道具
	 * 
	 * @param hpCode
	 * @param protocol
	 */
	private void onItemUse(int hpCode, HPItemUse protocol) {

		// 数据校验
		int itemId = protocol.getItemId();
		int itemCount = protocol.getItemCount();
		if (itemId <= 0 || itemCount <= 0) {
			sendError(hpCode, Status.error.PARAMS_INVALID);
			return;
		}
		
		ItemEntity itemEntity = player.getPlayerData().getItemByItemId(itemId);
		if (itemEntity == null) {
			sendError(hpCode, Status.error.ITEM_NOT_FOUND);
			return;
		}
		// 静态数据的查找
		ItemCfg itemCfg = ConfigManager.getInstance().getConfigByKey(ItemCfg.class, itemEntity.getItemId());
		if (itemCfg == null) {
			sendError(hpCode, Status.error.CONFIG_NOT_FOUND);
			return;
		}

		if (itemCfg.getType() == toolType.GEM_VALUE) {
			if (itemEntity.getItemCount() < 1) {
				sendError(hpCode, Status.error.ITEM_NOT_ENOUGH);
				return;
			}
		} else {
			if (itemEntity.getItemCount() < itemCount) {
				sendError(hpCode, Status.error.ITEM_NOT_ENOUGH);
				return;
			}
		}
		if (itemCount>SysBasicCfg.getInstance().getuseItemMaxcount())
		{//外掛送錯誤參數鎖帳號5年 
			Calendar calendar = GuaJiTime.getCalendar();
			calendar.add(Calendar.YEAR, 5);
			PlayerEntity playerEntity = null;
			playerEntity = player.getPlayerData().getPlayerEntity();
			playerEntity.setForbidenTime(calendar.getTime());
			playerEntity.notifyUpdate(false);
			// 从竞技场排行榜删除
			ArenaManager.getInstance().removeArenaRank(player.getPlayerData().getId());
			GuaJiNetManager.getInstance().addBlackIp(player.getIp());
			GuaJiNetManager.getInstance().addBlackDevice(player.getDevice());
			// 日志记录
			BehaviorLogger.log4GM(String.valueOf(player.getPlayerData().getId()), Source.GM_OPERATION, Action.GM_FORBIDEN, 
					Params.valueOf("ItemUse_itemId", itemId), Params.valueOf("itemCount", itemCount), Params.valueOf("entity", itemEntity.getItemCount()), Params.valueOf("ip", player.getIp()));
			
			// 踢出玩家
			if (player != null) {
				player.kickout(Const.kickReason.LOGIN_FORBIDEN_VALUE);
			}
			sendError(hpCode, Status.error.ITEM_NOT_ENOUGH);
			return;
		}
		
		// 使用道具类型判断(1次行为要求一个, 不用count判断)
		int useType = itemCfg.getType();
		// 配对道具
		int needItemId = itemCfg.getNeedItem();
		ItemEntity needItemEntity = null;
		if (needItemId > 0) {
			needItemEntity = player.getPlayerData().getItemByItemId(needItemId);
			if (useType != Const.toolType.LUCK_TREASURE_VALUE) {
				if (needItemEntity == null || needItemEntity.getItemCount() < itemCount) {
					sendError(hpCode, Status.error.ITEM_NOT_ENOUGH);
					return;
				}
			}
		}
		ConsumeItems consumeItems = ConsumeItems.valueOf();
		AwardItems awardItems = new AwardItems();
		RewardInfo.Builder rewardsInfo = RewardInfo.newBuilder();
		int gemTargetItemId = 0;
		int msgType = 0;
		if (useType == Const.toolType.FRAGMENT_VALUE) {
			// 神器装备碎片合成
			CompoundCfg compoundCfg = CompoundCfg.getCompoundCfg(player.getLevel());
			if (compoundCfg == null) {
				sendError(hpCode, Status.error.CONFIG_NOT_FOUND);
				return;
			}
			int equipId = compoundCfg.randomEquipId();
			if (equipId <= 0) {
				return;
			}
			consumeItems.addChangeInfo(changeType.CHANGE_TOOLS, itemEntity.getId(), compoundCfg.getItemId(), compoundCfg.getItemCount());
			// 扣道具
			if (!consumeItems.checkConsume(player, hpCode)) {
				return;
			}
			player.consumeTools(compoundCfg.getItemId(), compoundCfg.getItemCount(), Action.TOOL_USE);
			consumeItems.pushChange(player);
			// 造装备
			EquipEntity equipEntity = player.increaseEquip(equipId, 10000, Action.TOOL_USE);
			player.getPlayerData().syncEquipInfo(equipEntity.getId());
			rewardsInfo.addShowItems(BuilderUtil.genEuiqpRewardBuilder(equipEntity));
			PlayerUtil.pushRewards(player, rewardsInfo, 2);
			
			TapDBUtil.Event_GetItem(player,player.getTapDBUId(),Action.TOOL_USE,TapDBSource.ItemUse, Const.itemType.EQUIP_VALUE* GsConst.ITEM_TYPE_BASE, equipEntity.getEquipId(), 1
					, Params.valueOf("Level", player.getLevel())
					, Params.valueOf("compoundItemId", compoundCfg.getItemId())
					, Params.valueOf("compoundCount", compoundCfg.getItemCount()));

		} else if (useType == Const.toolType.SUIT_FRAGMENT_VALUE) {
			if (itemCfg.getSuitEquipId() <= 0 || itemCfg.getSuitEquipCount() <= 0) {
				sendError(hpCode, Status.error.CONFIG_ERROR_VALUE);
				return;
			}
			
			int equipId = itemCfg.getSuitEquipId();
			
			EquipCfg equipCfg = ConfigManager.getInstance().getConfigByKey(EquipCfg.class, equipId);
			
			if (equipCfg == null) {
				sendError(hpCode, Status.error.CONFIG_ERROR_VALUE);
				return;
			}

			if (equipCfg.getSeries() != 0) {
				MutualEntity mutualEntity = player.getPlayerData().getMutualEntity();
				if (mutualEntity.getStarMap().containsKey(equipCfg.getSeries())) {
					sendError(hpCode, Status.error.CONFIG_ERROR_VALUE);
					return;
				}
			} 
//			else {	
//				sendError(hpCode, Status.error.CONFIG_ERROR_VALUE);
//				return;
//			}
			
			if (itemCfg.getNeedCoin() > 0) {
				consumeItems.addChangeInfo(changeType.CHANGE_COIN, itemCfg.getNeedCoin());
			}

			consumeItems.addChangeInfo(changeType.CHANGE_TOOLS, itemEntity.getId(), itemCfg.getId(), itemCfg.getSuitEquipCount());
			// 扣道具
			if (!consumeItems.checkConsume(player, hpCode)) {
				return;
			}
			consumeItems.consumeTakeAffect(player, Action.TOOL_USE);
			// 造装备
			EquipEntity equipEntity = player.increaseEquip(equipId, 0, Action.TOOL_USE);
			player.getPlayerData().syncEquipInfo(equipEntity.getId());
			rewardsInfo.addShowItems(BuilderUtil.genEuiqpRewardBuilder(equipEntity));
			PlayerUtil.pushRewards(player, rewardsInfo, 2);
			QuestEventBus.fireQuestEventOneTime(QuestEventType.SUIT_FRAGMENT_TIMES, player.getXid());
			
			TapDBUtil.Event_GetItem(player,player.getTapDBUId(),Action.TOOL_USE,TapDBSource.ItemUse, Const.itemType.EQUIP_VALUE* GsConst.ITEM_TYPE_BASE, equipEntity.getEquipId(), 1
					, Params.valueOf("item", itemCfg.getId())
					, Params.valueOf("count",itemCfg.getSuitEquipCount()));

		} else if (useType == Const.toolType.ELEMENT_FRAGMENT_VALUE) {
			// 根据等级获取配置
			ElementStampCfg elementStampCfg = ElementStampCfg.getCompoundCfg(player.getLevel());
			consumeItems.addChangeInfo(changeType.CHANGE_TOOLS, itemEntity.getId(), elementStampCfg.getItemId(), elementStampCfg.getItemCount());
			// 检测道具
			if (!consumeItems.checkConsume(player, hpCode)) {
				return;
			}
			// 扣除道具
			player.consumeTools(elementStampCfg.getItemId(), elementStampCfg.getItemCount(), Action.TOOL_USE);
			consumeItems.pushChange(player);
			// 获取合成之后的元素ID
			ItemInfo itemInfo = elementStampCfg.randomElementItemInfo(elementStampCfg.getElementItems(), player.getProf());
			awardItems.addItem(itemInfo);
			awardItems.rewardTakeAffectAndPush(player, Action.TOOL_USE, 1,TapDBSource.ItemUse
					,Params.valueOf("item", elementStampCfg.getItemId())
					,Params.valueOf("count", elementStampCfg.getItemCount()));

		} else if (useType == Const.toolType.GEM_VALUE) {
			// 宝石合成
			msgType = protocol.getMsgType();
			if (msgType == Const.HeroTokenMsgType.GEM_COMPOUND_ONCE_VALUE) {
				gemCompound(itemCfg, consumeItems);
			} else {
				gemLevelUpOnce(itemEntity, itemCfg, consumeItems, player.getLevel(), itemCount);
			}

		} else if (useType == Const.toolType.GIFT_VALUE || useType == Const.toolType.BOSS_CHALLENGE_TIMES_VALUE
				|| useType == Const.toolType.ELITE_MAP_BOOK_VALUE || useType == Const.toolType.ALLOANCE_VITALITY_PILL_VALUE
				|| useType == Const.toolType.AVATAR_GIFT_VALUE || useType == Const.toolType.MULTIELITE_CHALLENGE_TIMES_VALUE) {

//			if (useType == Const.toolType.FASTFIGHT_TIMES_BOOK_VALUE) {
//				if (player.getPlayerData().getStateEntity().getFastFightTimes() >= 99) {
//					sendError(hpCode, Status.error.BATTLE_FAST_FIGHT_TIME_IS_MAX_VALUE);
//					return;
//				}
//			}
			// 使用礼包
			consumeItems.addChangeInfo(changeType.CHANGE_TOOLS, itemEntity.getId(), itemEntity.getItemId(), itemCount);
			consumeItems.consumeTakeAffect(player, Action.TOOL_USE);
			if (itemCfg.getContainItem() != null && itemCfg.getContainItem().length() > 0) {
				for (int i = 0; i < itemCount; i++) {
					awardItems.appendAward(AwardItems.valueOf(itemCfg.getContainItem()));
				}
				if (useType == Const.toolType.GIFT_VALUE) {
					rewardsInfo = awardItems.rewardTakeAffectAndPush(player, Action.TOOL_USE, 2,TapDBSource.ItemUse
							,Params.valueOf("item", itemEntity.getItemId())
							,Params.valueOf("count",itemCount));

				} else {
					rewardsInfo = awardItems.rewardTakeAffectAndPush(player, Action.TOOL_USE, 1,TapDBSource.ItemUse
							,Params.valueOf("item", itemEntity.getItemId())
							,Params.valueOf("count", itemCount));

				}
			}
		} else if (useType == Const.toolType.GEM_PACKAGE_VALUE) {
			// 宝石袋
			for (int i = 0; i < itemCount; i++) {
				ItemInfo itemInfo = WeightUtil.random(itemCfg.getContainDataWeightList());
				if (itemInfo == null) {
					continue;
				}
				awardItems.addItem(AwardUtil.randomDrop(itemInfo.getType(),(int)itemInfo.getQuantity()));
			}
			player.consumeTools(itemId, itemCount, Action.TOOL_USE);
			consumeItems.addChangeInfo(changeType.CHANGE_TOOLS, itemEntity.getId(), itemId, itemCount);
			consumeItems.pushChange(player);
			awardItems.rewardTakeAffectAndPush(player, Action.TOOL_USE, 2,TapDBSource.ItemUse
					,Params.valueOf("item", itemId)
					,Params.valueOf("count", itemCount));

		} else if (useType == Const.toolType.WORDS_EXCHANGE_SPECIAL_VALUE) {
//			StateEntity stateEntity = player.getPlayerData().getStateEntity();
//			Date completeDay = stateEntity.getGongceWordDay();
//			if (completeDay == null) {
//				sendError(hpCode, Status.error.ITEM_NOT_ENOUGH_VALUE);
//				return;
//			}
//			ConsumeItems conItems = ConsumeItems.valueOf();
//			List<ItemCfg> needItemCfgs = ItemCfg.getItemCfgByType(Const.toolType.WORDS_EXCHANGE_SPECIAL_VALUE);
//			for (ItemCfg item : needItemCfgs) {
//				ItemEntity needItem = player.getPlayerData().getItemByItemId(item.getId());
//				if (needItem == null || needItem.getItemCount() < 1) {
//					sendError(hpCode, Status.error.ITEM_NOT_ENOUGH_VALUE);
//					return;
//				} else {
//					conItems.addChangeInfo(changeType.CHANGE_TOOLS, needItem.getId(), needItem.getItemId(), 1);
//				}
//			}
//			int spaceDays = GuaJiTime.calcBetweenDays(completeDay, GuaJiTime.getCalendar().getTime()) + 1;
//			WordsExchangeSpecialCfg wordsExchangeSpecialCfg = WordsExchangeSpecialCfg.getExchangeSpecialCfg(spaceDays);
//			if (wordsExchangeSpecialCfg == null) {
//				sendError(hpCode, Status.error.CONFIG_NOT_FOUND_VALUE);
//				return;
//			}
//			conItems.consumeTakeAffect(player, Action.WORDS_EXCHANGE_SPECIAL);
//			wordsExchangeSpecialCfg.getAwardItems().rewardTakeAffectAndPush(player, Action.WORDS_EXCHANGE_SPECIAL, 1);
//			stateEntity.setGongceWordDay(player.getPlayerData().getGongceCompleteDate());
//			stateEntity.notifyUpdate(true);
//			player.getPlayerData().syncStateInfo();
		} else if (useType == Const.toolType.LUCK_TREASURE_VALUE) {
			int useCount = 0;
			if (needItemEntity == null) {
				useCount = itemCount;
			} else {
				if (needItemEntity.getItemCount() < itemCount && itemCount > 0) {
					useCount = (int)needItemEntity.getItemCount();
				} else {
					useCount = itemCount;
				}
			}
			openTreasure(itemCfg, itemEntity, useCount);
		} else if (useType == Const.toolType.SOUL_STONE_VALUE) {
			// 合成佣兵书籍;
			if (!compoundBook(hpCode, protocol, itemCfg, consumeItems, awardItems, itemCount)) {
				return;
			}
		} else if (useType == Const.toolType.GOODS_COMPOUND_VALUE) {
			// 专精符文合成
			if (!compoundRuneStones(hpCode, protocol, itemCfg, consumeItems, awardItems, itemCount)) {
				return;
			}
		} else if (useType == Const.toolType.EQUIP_EXCHANGE_VALUE) {
			int profId = this.player.getProf();
			if (protocol.hasProfId() && protocol.getProfId() > 0) {
				profId = protocol.getProfId();
			}
			AwardItems equipAward = itemCfg.getEquipExchange(profId).clone();
			if (equipAward != null) {
				consumeItems.addChangeInfo(changeType.CHANGE_TOOLS, itemEntity.getId(), itemEntity.getItemId(), itemCount);
				consumeItems.consumeTakeAffect(player, Action.TOOL_USE);

				for (AwardItems.Item item : equipAward.getAwardItems()) {
					item.setCount(item.getCount() * itemCount);
				}
				equipAward.rewardTakeAffectAndPush(player, Action.TOOL_USE, 1,TapDBSource.ItemUse
						,Params.valueOf("item", itemEntity.getItemId())
						,Params.valueOf("count", itemCount));
			}
		} else if (useType == Const.toolType.TREASURE_SELITEM_VALUE) {
			
			int profId = this.player.getProf();
			if (protocol.hasProfId() && protocol.getProfId() > 0) {
				profId = protocol.getProfId();
			}
			AwardItems equipAward = itemCfg.getEquipSelMap().get(profId).clone();
			if (equipAward != null) {
				consumeItems.addChangeInfo(changeType.CHANGE_TOOLS, itemEntity.getId(), itemEntity.getItemId(), itemCount);
				consumeItems.consumeTakeAffect(player, Action.TOOL_USE);

				for (AwardItems.Item item : equipAward.getAwardItems()) {
					item.setCount(item.getCount() * itemCount);
				}
				equipAward.rewardTakeAffectAndPush(player, Action.TOOL_USE, 0,TapDBSource.ItemUse
						,Params.valueOf("item", itemEntity.getItemId())
						,Params.valueOf("count", itemCount));
			}
		} 
		else if (useType == Const.toolType.HERO_TOKEN_VALUE) {
			if (!protocol.hasMsgType()) {
				sendError(hpCode, Status.error.PARAMS_INVALID);
				return;
			}
			msgType = protocol.getMsgType();
			if (msgType == HeroTokenMsgType.CONSUME_ITEM_VALUE) {
				// 英雄令合成
				if (!compoundHeroToken(hpCode, protocol, itemCfg, consumeItems, awardItems, itemCount)) {
					return;
				}
			} else if (msgType == HeroTokenMsgType.GET_TASK_VALUE) {
				// 领取任务
				if (player.getLevel() < itemCfg.getLevelLimit()) {
					// 玩家等级小于道具要求等级
					sendError(hpCode, Status.error.TOKEN_LEVEL_LIMIT);
					return;
				}

				HeroTokenTaskEntity heroTokenTaskEntity = player.getPlayerData().getHeroTokenTaskEntity();
				if (heroTokenTaskEntity == null) {
					sendError(hpCode, Status.error.TOKEN_ENTITY_NOEXIST);
					return;
				}
				int taskCount = SysBasicCfg.getInstance().getHeroTokenTaskLimit();
				// 终身卡的影响
				ForeverCardStatus foreverCardStatus = ActivityUtil.getActivityStatus(player.getPlayerData(), Const.ActivityId.FOREVER_CARD_VALUE, -1,
						ForeverCardStatus.class);
				if (null != foreverCardStatus && foreverCardStatus.isOpen()) {
					taskCount += SysBasicCfg.getInstance().getHeroTokenTaskUpgrade();
				}
				if (heroTokenTaskEntity.getTaskCount() >= taskCount) {
					sendError(hpCode, Status.error.TOKEN_TASK_COUNT_LIMIT);
					return;
				}
				consumeItems.addChangeInfo(changeType.CHANGE_TOOLS, itemEntity.getId(), itemCfg.getId(), itemCount);
				// 扣道具
				consumeItems.consumeTakeAffect(player, Action.TOOL_USE);
				int taskId = itemCfg.getHeroTokenTaskId();
				heroTokenTaskEntity.addTask(taskId);
				heroTokenTaskEntity.notifyUpdate();
			} else {
				sendError(hpCode, Status.error.PARAMS_INVALID);
				return;
			}
		} else if (useType == Const.toolType.HOUR_PLACENENT_CARD_VALUE) {
//			// 新活动：增加每天的高速挑战券次数add by melvin
//			int ratio = ActivityUtil.getQuickBattleCardUpperLimitRatio();
//			// 每天次数限制
//			int maxCount = SysBasicCfg.getInstance().getHourCardUseMaxCount();
//			// 在活动周期内进行翻倍
//			maxCount = maxCount * ratio;
//			StateEntity stateEntity = player.getPlayerData().getStateEntity();
//
//			if (stateEntity.getHourCardUseCountOneDay() >= maxCount) {
//				sendError(hpCode, Status.error.HOUR_CARD_USER_COUNT_ONE_DAY_VALUE);
//				return;
//			}
//			
//			// 限制使用
//			if(itemCount >= maxCount){
//				sendError(hpCode, Status.error.PARAMS_INVALID);
//				return;
//			}
//			
//			// 没进行首次快速战斗，使用小时卡限制
//			/*
//			if (stateEntity.getRoleFirstFastBattle() == 1) {
//				sendError(hpCode, Status.error.FIRST_FAST_BATTLE_VALUE);
//				return;
//			}
//			*/
//			// 每日任务使用快速 扫讨圈计入  每日任务
//            Msg hawkMsg = Msg.valueOf(GsConst.MsgType.DailyQuestMsg.FAST_FIGHT,
//                    GuaJiXID.valueOf(GsConst.ObjType.PLAYER, player.getId()));
//            hawkMsg.pushParam(1);
//            GsApp.getInstance().postMsg(hawkMsg);
//            
//            
//            // 推送快速战斗任务
//            QuestEventBus.fireQuestEventOneTime(QuestEventType.QUICK_BATTLE, player.getXid());
//
//            // 7日之诗 快速战斗
//            SevenDayQuestEventBus.fireQuestEventOneTime(SevenDayEventType.FASTBATTLE, player.getXid());
//            
//			useHourCard(consumeItems, itemEntity, itemCount);
//			stateEntity.setHourCardUseCountOneDay(stateEntity.getHourCardUseCountOneDay() + 1);
//			stateEntity.notifyUpdate(true);
//			player.getPlayerData().syncStateInfo();
			return;
		} else if (useType == Const.toolType.EXP_REEL_VALUE) {
			// 经验卷轴使用
			consumeItems.addChangeInfo(changeType.CHANGE_TOOLS, itemEntity.getId(), itemEntity.getItemId(), itemCount);
			consumeItems.consumeTakeAffect(player, Action.TOOL_USE);
			if (itemCfg.getContainItem() != null && itemCfg.getContainItem().length() > 0) {
				for (int i = 0; i < itemCount; i++) {
					awardItems.appendAward(AwardItems.valueOf(itemCfg.getContainItem()));
				}
				rewardsInfo = awardItems.rewardTakeAffectAndPush(player, Action.TOOL_USE, 1,TapDBSource.ItemUse
						,Params.valueOf("item", itemEntity.getItemId())
						,Params.valueOf("count", itemCount));
			}
		} else if (useType == Const.toolType.ELITE_VALUE) {
//			if (!protocol.hasMsgType()) {
//				sendError(hpCode, Status.error.PARAMS_INVALID);
//				return;
//			}
//			if (SysBasicCfg.getInstance().getbuyAlbumItemId()!= itemId) {
//				sendError(hpCode, Status.error.PARAMS_INVALID);
//				return;
//			}
//			
//			//羁绊ID
//			msgType = protocol.getMsgType();
//			//图鉴Entity
//			ArchiveEntity entity = player.getPlayerData().getArchiveEntity();			
//			//羁绊未激活
//			if(!entity.getOpenFetters().contains(msgType)){
//				sendError(hpCode, Status.error.PARAMS_INVALID);
//				return;
//			}			
//			Map<Integer, Integer> albumMap = entity.getalbumMap();
//			
//			int step = 0;
//			if (albumMap.containsKey(msgType))
//			{
//				step = albumMap.get(msgType);
//			}
//			int needcount = SysBasicCfg.getInstance().getbuyAlbumItemCount(step);
//			if (needcount != itemCount || step >= 3) {
//				sendError(hpCode, Status.error.PARAMS_INVALID);
//				return;
//			}
//			
//			// 使用道具解鎖相簿
//			consumeItems.addChangeInfo(changeType.CHANGE_TOOLS, itemEntity.getId(), itemEntity.getItemId(), itemCount);
//			consumeItems.consumeTakeAffect(player, Action.TOOL_USE);
//			
//			entity.addalbumMap(msgType, step+1);
//			//保存DB
//			entity.reConvert();
//			entity.notifyUpdate();
		} else if (useType == Const.toolType.QUICK_GOLD_VALUE) {
			if (!useQuickGold(itemCfg,consumeItems, itemEntity, itemCount)) {
				sendError(hpCode, Status.error.PARAMS_INVALID);
				return;
			}
		} else if (useType == Const.toolType.QUICK_EXP_VALUE) {
			if (!useQuickExp(itemCfg,consumeItems, itemEntity, itemCount)) {
				sendError(hpCode, Status.error.PARAMS_INVALID);
				return;
			}
		} else if (useType == Const.toolType.QUICK_STONE_VALUE) {
			if (!useQuickStone(itemCfg,consumeItems, itemEntity, itemCount)) {
				sendError(hpCode, Status.error.PARAMS_INVALID);
				return;
			}
		}

		HPItemUseRet.Builder itemUseBuilder = HPItemUseRet.newBuilder();
		itemUseBuilder.setTargetItemId(gemTargetItemId);
		itemUseBuilder.setMsgType(msgType);
		sendProtocol(Protocol.valueOf(HP.code.ITEM_USE_S, itemUseBuilder));
	}
	
	private boolean useQuickGold(ItemCfg itemCfg,ConsumeItems consumeItems, ItemEntity itemEntity, int itemCount) {
		if (itemCfg.getHourCardTime() <= 0) {
			return false;
		}		
		NewMapCfg mapCfg = player.getPlayerData().getCurBattleMap();
		if (mapCfg == null) {
			return false;
		}
		
		consumeItems.addChangeInfo(changeType.CHANGE_TOOLS, itemEntity.getId(), itemEntity.getItemId(), itemCount);
		if (!consumeItems.consumeTakeAffect(player, Action.TOOL_USE)) {
			return false;
		}
		AwardItems awardItems = new AwardItems();
		int addcoin = (int)Math.floor((float) itemCfg.getHourCardTime()*mapCfg.getSkyCoin()*3600*itemCount);
		awardItems.addCoin(addcoin);
		awardItems.rewardTakeAffectAndPush(player, Action.TOOL_USE, 2,TapDBSource.ItemUse
				,Params.valueOf("item", itemEntity.getItemId())
				,Params.valueOf("count", itemCount));
		
		return true;
	}
	
	private boolean useQuickExp(ItemCfg itemCfg,ConsumeItems consumeItems, ItemEntity itemEntity, int itemCount) {
		if (itemCfg.getHourCardTime() <= 0) {
			return false;
		}		
		NewMapCfg mapCfg = player.getPlayerData().getCurBattleMap();
		if (mapCfg == null) {
			return false;
		}
		
		consumeItems.addChangeInfo(changeType.CHANGE_TOOLS, itemEntity.getId(), itemEntity.getItemId(), itemCount);
		if (!consumeItems.consumeTakeAffect(player, Action.TOOL_USE)) {
			return false;
		}
		int addcount = (int)Math.floor((float)itemCfg.getHourCardTime()*mapCfg.getPotion()*3600*itemCount);
		String itemStr = String.format(SysBasicCfg.getInstance().getHeroEXPItem(),addcount) ;
		AwardItems awardItems = AwardItems.valueOf(itemStr);
		awardItems.rewardTakeAffectAndPush(player, Action.TOOL_USE, 2,TapDBSource.ItemUse
				,Params.valueOf("item", itemEntity.getItemId())
				,Params.valueOf("count", itemCount));
		return true;
	}
	
	private boolean useQuickStone(ItemCfg itemCfg,ConsumeItems consumeItems, ItemEntity itemEntity, int itemCount) {
		if (itemCfg.getHourCardTime() <= 0) {
			return false;
		}		
		NewMapCfg mapCfg = player.getPlayerData().getCurBattleMap();
		if (mapCfg == null) {
			return false;
		}
		
		consumeItems.addChangeInfo(changeType.CHANGE_TOOLS, itemEntity.getId(), itemEntity.getItemId(), itemCount);
		if (!consumeItems.consumeTakeAffect(player, Action.TOOL_USE)) {
			return false;
		}
		int addcount = (int)Math.floor((float)itemCfg.getHourCardTime()*mapCfg.getStone()*itemCount);
		String itemStr = String.format(SysBasicCfg.getInstance().getHeroStoneItem(),addcount) ;
		AwardItems awardItems = AwardItems.valueOf(itemStr);
		awardItems.rewardTakeAffectAndPush(player, Action.TOOL_USE, 2,TapDBSource.ItemUse
				,Params.valueOf("item", itemEntity.getItemId())
				,Params.valueOf("count", itemCount));
		return true;
	}

	/**
	 * 使用小时卡
	 * 
	 * @param consumeItems
	 * @param itemEntity
	 * @param itemCount
	 */
	private void useHourCard(ConsumeItems consumeItems, ItemEntity itemEntity, int itemCount) {

		consumeItems.addChangeInfo(changeType.CHANGE_TOOLS, itemEntity.getId(), itemEntity.getItemId(), itemCount);
		consumeItems.consumeTakeAffect(player, Action.TOOL_USE);
		ItemCfg itemCfg = ConfigManager.getInstance().getConfigByKey(ItemCfg.class, itemEntity.getItemId());
		Msg questMsg = Msg.valueOf(GsConst.MsgType.USE_HOUR_CARD);
		questMsg.pushParam(Action.TOOL_USE_HOUR_CARD);
		questMsg.pushParam(itemCfg.getHourCardTime() * itemCount);
		GsApp.getInstance().postMsg(player.getXid(), questMsg);

	}

	/**
	 * 宝石一键合成
	 * @param itemCfg
	 * @param consumeItems
	 * @param playerLevel
	 * @param itemCount
	 * @return
	 */
	private void gemCompound(ItemCfg itemCfg, ConsumeItems consumeItems) {
		// 旧宝石屏蔽
		if (itemCfg.getId() >= 20000 && itemCfg.getId() <= 30000) {
			// 升级目标为空不可升级
			sendError(HP.code.ITEM_USE_C_VALUE, Status.error.PARAMS_INVALID_VALUE);
			return;
		}

		// 飘字
		AwardItems showAwardItems = new AwardItems();

		// 同类型宝石
		TreeSet<Integer> set = ItemCfg.getGemTypeMap().get(itemCfg.getGemType());
		for (Integer itemId : set) {
			ItemEntity itemEntity = player.getPlayerData().getItemByItemId(itemId);
			if (itemEntity == null) {
				continue;
			}

			if (itemEntity.getItemCount() <= 1) {
				continue;
			}

			ItemCfg cfg = ConfigManager.getInstance().getConfigByKey(ItemCfg.class, itemEntity.getItemId());
			if (cfg.getLevelUpItem() <= 0) {
				continue;
			}

			// 获取当前消耗品最少能合成几次
			List<ItemInfo> items = cfg.getConsumeMaterialList();
			int minGold = Integer.MAX_VALUE;
			long minCoin = Integer.MAX_VALUE;
			int minGoods = Integer.MAX_VALUE;
			for (ItemInfo itemInfo : items) {
				switch (itemInfo.getItemId()) {
				case Const.playerAttr.GOLD_VALUE:
					minGold = (int)(player.getGold() / itemInfo.getQuantity());
					break;
				case Const.playerAttr.COIN_VALUE:
					minCoin = player.getCoin() / itemInfo.getQuantity();
					break;
				default:
					break;
				}
			}

			minGoods = (int) Math.floor(itemEntity.getItemCount() / 2);

			int minCost = (int) Math.min(minGoods, Math.min(minGold, minCoin));

			// 道具不足
			if (minCost <= 0) {
				continue;
			}

			// 计算N次消耗
			List<ItemInfo> costItems = new ArrayList<>();
			for (ItemInfo item : items) {
				ItemInfo it = item.clone();
				int initCount = (int)it.getQuantity();
				it.setQuantity(initCount * minCost);
				costItems.add(it);
			}

			ConsumeItems consumeItem = new ConsumeItems();
			if (!consumeItem.addConsumeInfo(player.getPlayerData(), costItems)) {
				sendError(HP.code.ITEM_USE_C_VALUE, Status.error.ITEM_NOT_ENOUGH_VALUE);
				return;
			}

			if (!consumeItem.checkConsume(player, HP.code.ITEM_USE_C_VALUE)) {
				return;
			}
			consumeItem.consumeTakeAffect(player, Action.TOOL_USE);

			AwardItems awardItems = new AwardItems();
			ItemInfo itemInfo = new ItemInfo(Const.itemType.TOOL_VALUE * GsConst.ITEM_TYPE_BASE, cfg.getLevelUpItem(), minCost);
			awardItems.addItem(itemInfo);
			awardItems.rewardTakeAffect(player, Action.TOOL_USE);

			showAwardItems.addItem(itemInfo);
			// 推送宝石合成任务
			QuestEventBus.fireQuestEvent(QuestEventType.GEM_LEVEL, ItemUtil.calcGemLevel(itemInfo.getItemId(), itemCfg.getType()), player.getXid());
		}

		RewardInfo.Builder builder = RewardInfo.newBuilder();
		for (ItemInfo item : showAwardItems.getAwardItemInfos()) {
			RewardItem.Builder rewardItemBuilder = RewardItem.newBuilder();
			rewardItemBuilder.setItemId(item.getItemId());
			rewardItemBuilder.setItemCount(item.getQuantity());
			rewardItemBuilder.setItemType(item.getType());
			builder.addShowItems(rewardItemBuilder);
		}

		if (showAwardItems.getAwardItemInfos().size() > 0) {
			PlayerUtil.pushRewards(player, builder, 1);
		}
	}
	
	/**
	 * 宝石合成
	 * 
	 * @param itemEntity
	 * @param itemCfg
	 * @param consumeItems
	 * @param itemCount
	 * @return
	 */
	private int gemLevelUpOnce(ItemEntity itemEntity, ItemCfg itemCfg, ConsumeItems consumeItems, int playerLevel, int itemCount) {
		// 旧宝石屏蔽
		if (itemCfg.getId() >= 20000 && itemCfg.getId() <= 30000) {
			// 升级目标为空不可升级
			sendError(HP.code.ITEM_USE_C_VALUE, Status.error.PARAMS_INVALID_VALUE);
			return -1;
		}

		// 宝石升级
		if (itemCfg.getLevelUpItem() <= 0) {
			// 升级目标为空不可升级
			sendError(HP.code.ITEM_USE_C_VALUE, Status.error.ITEM_LEVEL_UP_TARGET_EMPTY);
			return -1;
		}

		List<ItemInfo> itemInfos = new ArrayList<>();
		for (ItemInfo info : itemCfg.getConsumeMaterialList()) {
			itemInfos.add(info.clone());
		}

		for (ItemInfo info : itemInfos) {
			int count = (int)info.getQuantity();
			info.setQuantity(count * itemCount);
		}

		boolean checkResult = false;
		if (null != itemInfos && itemInfos.size() > 0) {
			ConsumeItems consumeItem = new ConsumeItems();
			// 校验材料是否满足
			if (!consumeItem.addConsumeInfo(player.getPlayerData(), itemInfos)) {
				sendError(HP.code.ITEM_USE_C_VALUE, Status.error.ITEM_NOT_ENOUGH_VALUE);
				return -1;
			}
			checkResult = consumeItem.checkConsume(player, HP.code.ITEM_USE_C_VALUE);
			if (checkResult) {
				consumeItem.consumeTakeAffect(player, Action.GEM_COMPOUND);
			} else {
				sendError(HP.code.ITEM_USE_C_VALUE, Status.error.GEM_COMPOUND_RESOURCE_NOT_ENOUGH);
				return -1;
			}
		}

		// 添加一个升级后的新宝石
		AwardItems awardItems = new AwardItems();
		awardItems.addItem(Const.itemType.TOOL_VALUE * GsConst.ITEM_TYPE_BASE, itemCfg.getLevelUpItem(), itemCount);
		awardItems.rewardTakeAffectAndPush(player, Action.TOOL_USE, 1);
		// 推送宝石合成任务
		QuestEventBus.fireQuestEvent(QuestEventType.GEM_LEVEL, ItemUtil.calcGemLevel(itemCfg.getLevelUpItem(), itemCfg.getType()), player.getXid());
		return itemCfg.getLevelUpItem();
	}
	
	/**
	 * 合成符文；
	 * 
	 * @param hpCode
	 * @param protocol
	 * @param itemCfg
	 * @param consumeItems
	 * @param awardItems
	 * @param itemCount
	 * @return
	 */
	private boolean compoundRuneStones(int hpCode, HPItemUse protocol, ItemCfg itemCfg, ConsumeItems consumeItems, AwardItems awardItems,
			int itemCount) {
		return this.compoundItem(hpCode, protocol, itemCfg, consumeItems, awardItems, itemCount);
	}

	/**
	 * 合成佣兵书籍, 基于之前代码, 做部分重构, 且支持一键合成;
	 * 
	 * @param hpCode
	 * @param protocol
	 * @param itemCfg
	 * @param consumeItems
	 * @param awardItems
	 * @param itemCount
	 *            合成数量;
	 * @return true 表示合成成功, false 表示合成失败;
	 */
	private boolean compoundBook(int hpCode, HPItemUse protocol, ItemCfg itemCfg, ConsumeItems consumeItems, AwardItems awardItems, int itemCount) {
		return this.compoundItem(hpCode, protocol, itemCfg, consumeItems, awardItems, itemCount);
	}

	/**
	 * 合成英雄令
	 * 
	 * @param hpCode
	 * @param protocol
	 * @param itemCfg
	 * @param consumeItems
	 * @param awardItems
	 * @param itemCount
	 *            合成数量;
	 * @return true 表示合成成功, false 表示合成失败;
	 */
	private boolean compoundHeroToken(int hpCode, HPItemUse protocol, ItemCfg itemCfg, ConsumeItems consumeItems, AwardItems awardItems,
			int itemCount) {
		return this.compoundItem(hpCode, protocol, itemCfg, consumeItems, awardItems, itemCount);
	}

	/**
	 * 合成物品;
	 * <p>
	 * 支持书籍合成 + 符文合成；
	 * 
	 * @param hpCode
	 * @param protocol
	 * @param itemCfg
	 * @param consumeItems
	 * @param awardItems
	 * @param itemCount
	 * @return
	 */
	private boolean compoundItem(int hpCode, HPItemUse protocol, ItemCfg itemCfg, ConsumeItems consumeItems, AwardItems awardItems, int itemCount) {
		// check count
		if (itemCount <= 0) {
			return false;
		}
		// check if got top level
		if (itemCfg.getLevelUpItem() <= 0) {
			// 升级目标为空不可升级
			sendError(hpCode, Status.error.ITEM_LEVEL_UP_TARGET_EMPTY);
			return false;
		}
		// check coin
		if (itemCfg.getLevelUpCostCoins() > 0) {
			if (player.getCoin() < itemCfg.getLevelUpCostCoins() * itemCount) {
				sendError(hpCode, Status.error.COINS_NOT_ENOUGH);
				return false;
			}
		}
		// check gold
		if (itemCfg.getLevelUpCostGold() > 0) {
			if (player.getGold() < itemCfg.getLevelUpCostGold() * itemCount) {
				sendError(hpCode, Status.error.GOLD_NOT_ENOUGH);
				return false;
			}
		}
		// cost items
		if (itemCfg.getLevelUpCostItemId() > 0 && itemCfg.getLevelUpCount() > 0) {
			// 消耗道具Id不为0
			if (!player.getPlayerData().checkItemEnough(itemType.TOOL, itemCfg.getLevelUpCostItemId(), itemCfg.getLevelUpCount() * itemCount)) {
				sendError(hpCode, Status.error.ITEM_NOT_ENOUGH);
				return false;
			}

			ItemEntity costItemEntity = player.consumeTools(itemCfg.getLevelUpCostItemId(), itemCfg.getLevelUpCount() * itemCount, Action.TOOL_USE);
			consumeItems.addChangeInfo(changeType.CHANGE_TOOLS, costItemEntity.getId(), itemCfg.getLevelUpCostItemId(),
					itemCfg.getLevelUpCount() * itemCount);
		}
		// cost coins
		if (itemCfg.getLevelUpCostCoins() > 0) {
			player.consumeCoin(itemCfg.getLevelUpCostCoins() * itemCount, Action.TOOL_USE);
			consumeItems.addChangeInfo(changeType.CHANGE_COIN, itemCfg.getLevelUpCostCoins() * itemCount);
		}
		// cost golds
		if (itemCfg.getLevelUpCostGold() > 0) {
			player.consumeGold(itemCfg.getLevelUpCostGold() * itemCount, Action.TOOL_USE);
			consumeItems.addChangeInfo(changeType.CHANGE_GOLD, itemCfg.getLevelUpCostGold() * itemCount);
		}
		// add awards
		ItemInfo item = new ItemInfo(Const.itemType.TOOL_VALUE * GsConst.ITEM_TYPE_BASE, itemCfg.getLevelUpItem(), itemCount);
		awardItems.addItem(item);
		awardItems.rewardTakeAffectAndPush(player, Action.TOOL_USE, 1);

		consumeItems.pushChange(player);
		return true;
	}

	/**
	 * 道具出售
	 * 
	 * @param player
	 * @param parseProtocol
	 */
	private boolean onItemSell(HPItemSell params) {
		int itemId = params.getItemId();
		int count = params.getCount();
		if (itemId <= 0 || count <= 0) {
			player.sendError(HP.code.ITEM_USE_C_VALUE, Status.error.PARAMS_INVALID_VALUE);
			return false;
		}

		ItemEntity itemEntity = player.getPlayerData().getItemByItemId(itemId);
		if (itemEntity == null || itemEntity.getItemCount() < count) {
			player.sendError(HP.code.ITEM_USE_C_VALUE, Status.error.ITEM_NOT_ENOUGH_VALUE);
			return false;
		}

		ItemCfg itemCfg = ConfigManager.getInstance().getConfigByKey(ItemCfg.class, itemEntity.getItemId());
		if (itemCfg == null) {
			player.sendError(HP.code.ITEM_USE_C_VALUE, Status.error.CONFIG_NOT_FOUND_VALUE);
			return false;
		}
		// 该物品不能出售
		if (itemCfg.getPrice() <= 0) {
			player.sendError(HP.code.ITEM_USE_C_VALUE, Status.error.ITEM_SELL_NOT_ALLOW);
			return false;
		}

		// 消耗
		ConsumeItems.valueOf(changeType.CHANGE_TOOLS, itemEntity.getId(), itemEntity.getItemId(), count).consumeTakeAffect(player, Action.ITEM_SELL);
		AwardItems awardItems = new AwardItems();
		awardItems.addCoin(itemCfg.getPrice() * count);
		awardItems.rewardTakeAffectAndPush(player, Action.ITEM_SELL, 1);
		return true;
	}

	/**
	 * 道具兑换
	 * 
	 * @param itemCfg
	 * @param itemEntity
	 * @param itemCount
	 */
	private boolean onItemExchange(HPItemExchange params) {
		ConsumeItems consumeItems = ConsumeItems.valueOf();
		AwardItems awardItems = new AwardItems();

		int crystalCount = 0;

		for (ExchangeItem item : params.getExchangeItemList()) {
			int itemId = item.getItemId();
			int count = item.getCount();

			if (itemId <= 0 || count <= 0) {
				player.sendError(HP.code.ITEM_USE_C_VALUE, Status.error.PARAMS_INVALID_VALUE);
				return false;
			}

			ItemEntity itemEntity = player.getPlayerData().getItemByItemId(itemId);

			if (itemEntity == null || itemEntity.getItemCount() < count) {
				player.sendError(HP.code.ITEM_USE_C_VALUE, Status.error.ITEM_NOT_ENOUGH_VALUE);
				return false;
			}

			ItemCfg itemCfg = ConfigManager.getInstance().getConfigByKey(ItemCfg.class, itemEntity.getItemId());

			if (itemCfg == null) {
				player.sendError(HP.code.ITEM_USE_C_VALUE, Status.error.CONFIG_NOT_FOUND_VALUE);
				return false;
			}

			// 奖励预处理
			String itemStr = itemCfg.getExchangeStr();

			String[] a = itemStr.split("_");

			for (int i = 0; i < count; i++) {
				if (a[1].equals("1020")) {
					crystalCount++;
				}
				awardItems.appendAward(AwardItems.valueOf(itemStr));
			}
			// 扣除预处理
			consumeItems.addChangeInfo(changeType.CHANGE_TOOLS, itemEntity.getId(), itemEntity.getItemId(), count);
			// consumeItems.consumeTakeAffect(player, Action.TOOL_USE);
		}

		if (crystalCount > 0) {
			QuestEventBus.fireQuestEvent(QuestEventType.EXCHANGE_CRYSTAL_TIMES, crystalCount, player.getXid());

		}
		consumeItems.consumeTakeAffect(player, Action.TOOL_USE);
		awardItems.rewardTakeAffectAndPush(player, Action.TOOL_USE, 1);
		HPItemExchangeRet.Builder build = HPItemExchangeRet.newBuilder();
		build.setIsSuccess(true);
		player.getPlayerData().syncPlayerInfo();
		sendProtocol(Protocol.valueOf(HP.code.ITEM_EXCHANGE_S_VALUE, build));
		return true;
	}

	private void openTreasure(ItemCfg itemCfg, ItemEntity itemEntity, int itemCount) {
		// 宝箱幸运值状态
		Map<Integer, Integer> itemLuckMap = player.getPlayerData().getStateEntity().getItemLuckMap();

		// 消耗判断
		int needItemId = itemCfg.getNeedItem();
		ItemEntity needItemEntity = null;
		if (needItemId > 0) {
			needItemEntity = player.getPlayerData().getItemByItemId(needItemId);
			if (needItemEntity == null || needItemEntity.getItemCount() < itemCount) {
				sendError(HP.code.ITEM_USE_C_VALUE, Status.error.ITEM_NOT_ENOUGH);
				return;
			}
		}

		// 消耗宝箱和钥匙
		ConsumeItems consumeItems = ConsumeItems.valueOf();
		if (needItemEntity != null) {
			consumeItems.addChangeInfo(changeType.CHANGE_TOOLS, needItemEntity.getId(), needItemEntity.getItemId(), itemCount);
		}
		consumeItems.addChangeInfo(changeType.CHANGE_TOOLS, itemEntity.getId(), itemEntity.getItemId(), itemCount);
		if (!consumeItems.checkConsume(player, HP.code.ITEM_USE_C_VALUE)) {
			return;
		}
		consumeItems.consumeTakeAffect(player, Action.TOOL_USE);

		AwardItems awardItems = new AwardItems();
		for (int i = 0; i < itemCount; i++) {
			// 开宝箱
			List<ItemInfo> itemInfos = null;
			if (!itemLuckMap.containsKey(itemCfg.getId()) || itemLuckMap.get(itemCfg.getId()) < itemCfg.getNeedLuck()) {
				itemInfos = WeightUtil.calcAsRandDrop(itemCfg.getContainDataWeightList());
				if (!itemLuckMap.containsKey(itemCfg.getId())) {
					itemLuckMap.put(itemCfg.getId(), itemCfg.getAddLuck());
				} else {
					itemLuckMap.put(itemCfg.getId(), itemLuckMap.get(itemCfg.getId()) + itemCfg.getAddLuck());
				}

				BehaviorLogger.log4Service(player, Source.USER_OPERATION, Action.OPEN_TREASURE, Params.valueOf("itemId", itemCfg.getId()),
						Params.valueOf("poolType", 0), Params.valueOf("addLuck", itemCfg.getAddLuck()),
						Params.valueOf("afterLuck", itemLuckMap.get(itemCfg.getId())));
			} else {
				BehaviorLogger.log4Service(player, Source.USER_OPERATION, Action.OPEN_TREASURE, Params.valueOf("itemId", itemCfg.getId()),
						Params.valueOf("poolType", 1), Params.valueOf("beforeLuck", itemLuckMap.get(itemCfg.getId())));

				itemInfos = WeightUtil.calcAsRandDrop(itemCfg.getContainSuperItemList());
				itemLuckMap.put(itemCfg.getId(), 0);
			}
			// 更新运气值状态
			player.getPlayerData().getStateEntity().notifyUpdate(true);

			for (ItemInfo treasureItemInfo : itemInfos) {
				if (treasureItemInfo.getItemId() == 0) {
					ItemInfo treasureDropItemInfo = AwardUtil.randomDrop(treasureItemInfo.getType());
					if (treasureDropItemInfo != null) {
						// 添加到奖励信息
						awardItems.addItem(treasureDropItemInfo);
					}
				} else {
					// 添加到奖励信息
					awardItems.addItem(treasureItemInfo);
				}
			}
		}
		awardItems.rewardTakeAffectAndPush(player, Action.OPEN_TREASURE, 2,TapDBSource.ItemUse
				,Params.valueOf("item",  itemEntity.getItemId())
				,Params.valueOf("count", itemCount));

		// 记录宝箱开启所获得的奖励
		try {
			BehaviorLogger.log4Service(player, Source.USER_OPERATION, Action.OPEN_TREASURE, Params.valueOf("itemId", itemCfg.getId()),
					Params.valueOf("itemCount", itemCount), Params.valueOf("treasureItems", awardItems.toDbString()));
		} catch (Exception e) {
			MyException.catchException(e);
		}
	}
}
