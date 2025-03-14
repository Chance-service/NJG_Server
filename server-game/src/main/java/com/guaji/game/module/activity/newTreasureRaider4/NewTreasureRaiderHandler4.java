package com.guaji.game.module.activity.newTreasureRaider4;

import com.guaji.game.config.ActivityTimeCfg;
import com.guaji.game.config.NewTreasureRaiderDropCfg4;
import com.guaji.game.config.NewTreasureRaiderTimesCfg4;
import com.guaji.game.config.RoleRelatedCfg;
import com.guaji.game.entity.RoleEntity;
import com.guaji.game.item.AwardItems;
import com.guaji.game.item.AwardItems.Item;
import com.guaji.game.item.ConsumeItems;
import com.guaji.game.item.ItemInfo;
import com.guaji.game.log.BehaviorLogger;
import com.guaji.game.log.BehaviorLogger.Action;
import com.guaji.game.log.BehaviorLogger.Params;
import com.guaji.game.player.Player;
import com.guaji.game.protocol.Activity2.HPNewTreasureRaiderInfoSync4;
import com.guaji.game.protocol.Activity2.HPNewTreasureRaiderSearch4;
import com.guaji.game.protocol.Activity2.TreasureSearchType;
import com.guaji.game.protocol.Const;
import com.guaji.game.protocol.Const.changeType;
import com.guaji.game.protocol.HP;
import com.guaji.game.protocol.Status;
import com.guaji.game.util.ActivityUtil;
import org.guaji.app.AppObj;
import org.guaji.config.ConfigManager;
import org.guaji.net.protocol.IProtocolHandler;
import org.guaji.net.protocol.Protocol;

import java.util.ArrayList;
import java.util.List;

/**
 * 抽卡
 */
public class NewTreasureRaiderHandler4 implements IProtocolHandler {

	static final int TIMES_TYPE_SINGLE = 1;
	static final int TIMES_TYPE_TEN = 10;

	@Override // NEW_TREASURE_RAIDER_INFO_C
	public boolean onProtocol(AppObj appObj, Protocol protocol) {
		Player player = (Player) appObj;
		// 检测活动是否开放
		int activityId = Const.ActivityId.NEW_TREASURE_RAIDER4_VALUE;
		ActivityTimeCfg timeCfg = ActivityUtil.getCurActivityTimeCfg(activityId);
		if (timeCfg == null) {
			// 活动已关闭
			player.sendError(protocol.getType(), Status.error.ACTIVITY_CLOSE);
			return true;
		}

		HPNewTreasureRaiderSearch4 req = protocol.parseProtocol(HPNewTreasureRaiderSearch4.getDefaultInstance());
		int searchTimes = req.getSearchTimes();
		TreasureSearchType searchType = req.getSearchType();
		int payTimes = searchTimes;
		if (searchTimes != TIMES_TYPE_SINGLE && searchTimes != TIMES_TYPE_TEN) {
			// 活动参数错误
			player.sendError(protocol.getType(), Status.error.PARAMS_INVALID);
			return true;
		}
		NewTreasureRaiderTimesCfg4 timesCfg = NewTreasureRaiderTimesCfg4.getTimesCfgByVipLevel(0);
		if (timesCfg == null) {
			player.sendError(protocol.getType(), Status.error.CONFIG_NOT_FOUND);
			return true;
		}

		// 判断基础碎片是否满足激活
		int willActRoleId = NewTreasureRaiderDropCfg4.getWillActRoleId();
		boolean isCanAct = false;
		RoleEntity entity = player.getPlayerData().getMercenaryByItemId(willActRoleId);
		if (entity != null) {
			RoleRelatedCfg cfg = ConfigManager.getInstance().getConfigByKey(RoleRelatedCfg.class, entity.getItemId());
			if (cfg == null) {
				player.sendError(protocol.getType(), Status.error.CONFIG_NOT_FOUND);
				return true;
			}
			if (entity.getSoulCount() >= cfg.getLimitCount()) {
				isCanAct = true;
			}

		}
		// 皮肤抽卡必须再基础碎片收集完后才能进行皮肤收集
		if (searchType == TreasureSearchType.SEARCHTYPE_SKIN && !isCanAct) {
			// 活动参数错误
			player.sendError(protocol.getType(), Status.error.PARAMS_INVALID);
			return true;
		}
		
		NewTreasureRaiderStatus4 status = ActivityUtil.getActivityStatus(player.getPlayerData(), activityId,
				timeCfg.getStageId(), NewTreasureRaiderStatus4.class);
		// 使用免费抽奖的时间
		long lastFreeTime = status.getLastFreeTime();
		int singleCost = timesCfg.getSingleCost();
		int tenCost = timesCfg.getTenCost();
		// 当前系统时间
		long currentTime = System.currentTimeMillis();
		// 计算实际花费钻石数量
		int payGold = 0;
		if (searchTimes == TIMES_TYPE_TEN) {
			payGold = tenCost;
		} else {
			// 是否免费
			int freeCD = (int) Math
					.max(NewTreasureRaiderInfoHandler4.convertTimeToMillisecond(timesCfg.getFreeCountDown())
							- (currentTime - lastFreeTime), 0);
			if (freeCD == 0) {
				payGold = 0;
				payTimes = 0;
				status.setLastFreeTime(currentTime);
			} else {
				payGold = singleCost;
			}
		}
		
		payGold=(int)(payGold*timesCfg.getDiscount());
		
		if (payGold > player.getGold()) {
			// 钻石不足
			player.sendError(protocol.getType(), Status.error.GOLD_NOT_ENOUGH_VALUE);
			return true;
		}
		// 扣除钻石
		if (payGold > 0) {
			player.consumeGold(payGold, Action.NEW_TREASURE_RAIDER4);
			ConsumeItems.valueOf(changeType.CHANGE_GOLD, payGold).pushChange(player);
		}
		List<AwardItems> awardsList = new ArrayList<AwardItems>();
		List<Integer> multiples = new ArrayList<Integer>();
		// 执行抽奖逻辑
		for (int i = 1; i <= searchTimes; i++) {
			AwardItems awards = new AwardItems();
			// 总次数
			int totalTimes = 0;
			if (searchType == TreasureSearchType.SEARCHTYPE_BASIC)
				totalTimes = status.getBasicTotalTimes();
			else
				totalTimes = status.getSkinTotalTimes();
			// 掉落物品
			List<ItemInfo> searchItems = NewTreasureRaiderDropCfg4.treasureRaiderDropItem(searchType, totalTimes + 1);
			awards.addItemInfos(searchItems);
			// 翻倍
			// int multiple = status.checkMultipleTime() ? status.getMultiple() : 1;
			int multiple = 1;
			multiple = Math.max(multiple, 1);
			multiples.add(multiple);

			for (Item item : awards.getAwardItems()) {
				item.count = item.count * multiple;
			}

			awardsList.add(awards);
			status.setLuckyValue(status.getLuckyValue() + 1);
			if (searchType == TreasureSearchType.SEARCHTYPE_BASIC)
				status.setBasicTotalTimes(status.getBasicTotalTimes() + 1);
			else
				status.setSkinTotalTimes(status.getSkinTotalTimes() + 1);
			awards.rewardTakeAffectAndPush(player, Action.NEW_TREASURE_RAIDER4, 0);
		}

		HPNewTreasureRaiderInfoSync4.Builder infoBuilder = NewTreasureRaiderInfoHandler4.generateInfo(player);
		HPNewTreasureRaiderInfoSync4.Builder builder = HPNewTreasureRaiderInfoSync4.newBuilder();
		builder.setLeftTime(infoBuilder.getLeftTime());
		builder.setFreeTreasureTimes(infoBuilder.getFreeTreasureTimes());
		builder.setLeftTreasureTimes(infoBuilder.getLeftTreasureTimes());
		builder.setOnceCostGold(infoBuilder.getOnceCostGold());
		builder.setTenCostGold(infoBuilder.getTenCostGold());
		builder.setIsActiveBasic(isCanAct);
		builder.setBasicLeftAwardTimes(infoBuilder.getBasicLeftAwardTimes());
		builder.setSkinLeftAwardTimes(infoBuilder.getSkinLeftAwardTimes());

		for (AwardItems item : awardsList) {
			builder.addReward(item.toString());
		}

		builder.setFreeCD(infoBuilder.getFreeCD());
		player.sendProtocol(Protocol.valueOf(HP.code.NEW_TREASURE_RAIDER_SEARCH4_S_VALUE, builder));
		// 更新玩家的活动数据
		player.getPlayerData().updateActivity(activityId, timeCfg.getStageId());
		// BI 日志 (freeTimes 本次寻宝开始前今日剩余的免费次数)
		BehaviorLogger.log4Platform(player, Action.NEW_TREASURE_RAIDER4, Params.valueOf("searchType", searchType),
				Params.valueOf("searchTimes", searchTimes), Params.valueOf("freeTimes", searchTimes - payTimes),
				Params.valueOf("costGold", payGold), Params.valueOf("awards", awardsList));
		return true;
	}

}
