package com.guaji.game.module.activity.newUR;

import java.util.ArrayList;
import java.util.List;

import org.guaji.app.AppObj;
import org.guaji.config.ConfigManager;
import org.guaji.log.Log;
import org.guaji.net.protocol.Protocol;
import org.guaji.net.protocol.IProtocolHandler;

import com.guaji.game.protocol.Activity3.NewURSearch;
import com.guaji.game.protocol.Activity3.SyncNewURInfo;
import com.guaji.game.config.NewURDropCfg;
import com.guaji.game.config.NewURMultipBuffCfg;
import com.guaji.game.config.NewURMultipCfg;
import com.guaji.game.config.NewURTimesCfg;
import com.guaji.game.config.SysBasicCfg;
import com.guaji.game.item.AwardItems;
import com.guaji.game.item.ConsumeItems;
import com.guaji.game.item.ItemInfo;
import com.guaji.game.item.AwardItems.Item;
import com.guaji.game.log.BehaviorLogger;
import com.guaji.game.log.BehaviorLogger.Action;
import com.guaji.game.log.BehaviorLogger.Params;
import com.guaji.game.player.Player;
import com.guaji.game.util.ActivityUtil;
import com.guaji.game.protocol.Const;
import com.guaji.game.protocol.Const.changeType;
import com.guaji.game.protocol.HP;
import com.guaji.game.protocol.Status;

/**
 * 新手UR活动
 */
public class NewURHandler implements IProtocolHandler {

	static final int TIMES_TYPE_SINGLE = 1;
	static final int TIMES_TYPE_TEN = 10;

	@Override
	public boolean onProtocol(AppObj appObj, Protocol protocol) {
		Player player = (Player) appObj;
		// 检测活动是否开放
		NewURStatus status = ActivityUtil.getNewURStatus(player.getPlayerData());
		if(null == status){
			player.sendError(protocol.getType(), Status.error.DATA_NOT_FOUND_VALUE);
			return true;
		}
		// 新增加玩家等级限制
		if (!NewURManager.canPlay(player.getLevel(), status)) {
			player.sendError(protocol.getType(), Status.error.ACTIVITY_CLOSE_VALUE);
			return true;
		}

		NewURSearch req = protocol.parseProtocol(NewURSearch.getDefaultInstance());
		int searchTimes = req.getSearchTimes();
		int payTimes = searchTimes;
		if (searchTimes != TIMES_TYPE_SINGLE && searchTimes != TIMES_TYPE_TEN) {
			// 活动参数错误
			player.sendError(protocol.getType(), Status.error.PARAMS_INVALID);
			return true;
		}
		NewURTimesCfg timesCfg = NewURTimesCfg.getTimesCfgByVipLevel(0);
		if (timesCfg == null) {
			player.sendError(protocol.getType(), Status.error.CONFIG_NOT_FOUND);
			return true;
		}
		
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
			int freeCD = (int) Math.max(NewURManager.toMillisecond(timesCfg.getFreeCountDown()) - (currentTime - lastFreeTime), 0);
			if (freeCD == 0) {
				payGold = 0;
				payTimes = 0;
				status.setLastFreeTime(currentTime);
			} else {
				payGold = singleCost;
			}
		}
		if (payGold > player.getGold()) {
			// 钻石不足
			player.sendError(protocol.getType(), Status.error.GOLD_NOT_ENOUGH_VALUE);
			return true;
		}
		// 扣除钻石
		if (payGold > 0) {
			player.consumeGold(payGold, Action.NEW_UR);
			ConsumeItems.valueOf(changeType.CHANGE_GOLD, payGold).pushChange(player);
		}
		List<AwardItems> awardsList = new ArrayList<AwardItems>();
		List<Integer> multiples = new ArrayList<Integer>();
		// 执行抽奖逻辑
		for (int i = 1; i <= searchTimes; i++) {
			AwardItems awards = new AwardItems();
			// 总次数
			int totalTimes = status.getTotalTimes();
			// 掉落物品
			List<ItemInfo> searchItems = NewURDropCfg.newURDropItem(totalTimes + 1);
			awards.addItemInfos(searchItems);
			// 翻倍
			//int multiple = status.checkMultipleTime() ? status.getMultiple() : 1;
			int multiple =1;
			multiple = Math.max(multiple, 1);
			multiples.add(multiple);
			for (Item item : awards.getAwardItems()) {
				item.count = item.count * multiple;
			}
			awardsList.add(awards);
			status.setMultiple(1);
			status.setMultipOverTime(0);
			status.setLuckyValue(status.getLuckyValue() + 1);
			status.setTotalTimes(status.getTotalTimes() + 1);
			/*-------------处理玩家下次抽奖翻倍-------------*/
			boolean isActiveMultiple = SysBasicCfg.getInstance().getNewURLimitTimes().contains(status.getTotalTimes());
			if (!isActiveMultiple) {
				// 从抽取区间配置中判断是否激活
				isActiveMultiple = NewURMultipCfg.isActiveMultiple(status.getLuckyValue());
			}
			if (isActiveMultiple) {
				int buffId = NewURMultipBuffCfg.randomItemId();
				NewURMultipBuffCfg buffCfg = ConfigManager.getInstance().getConfigByKey(NewURMultipBuffCfg.class, buffId);
				if (buffCfg != null) {
					int nexMultiple = buffCfg.getMultiple();
					int nextOverTime = NewURManager.toMillisecond(buffCfg.getOverTime());
					nexMultiple = Math.max(nexMultiple, 1);
					status.setMultiple(nexMultiple);
					status.setMultipOverTime(nextOverTime);
					status.setMultipStartTime(System.currentTimeMillis());
					status.setLuckyValue(0);
				} else {
					Log.logPrintln(String.format("%s: %s=%s", "NewURMultipBuffCfg not found.", "buffid", buffId));
				}
			}
			/*---------奖励物品--------*/
			awards.rewardTakeAffectAndPush(player, Action.NEW_UR, 0);
		}

		SyncNewURInfo.Builder infoBuilder = NewURManager.generateInfo(player);
		if (null == infoBuilder) {
			player.sendError(protocol.getType(), Status.error.DATA_NOT_FOUND_VALUE);
			return true;
		}
		SyncNewURInfo.Builder builder = SyncNewURInfo.newBuilder();
		builder.setLeftTime(infoBuilder.getLeftTime());
		builder.setFreeTreasureTimes(infoBuilder.getFreeTreasureTimes());
		builder.setLeftTreasureTimes(infoBuilder.getLeftTreasureTimes());
		builder.setOnceCostGold(infoBuilder.getOnceCostGold());
		builder.setTenCostGold(infoBuilder.getTenCostGold());

		for (AwardItems item : awardsList) {
			builder.addReward(item.toString());
		}
		for (Integer times : multiples) {
			builder.addRewardMultiple(times.intValue());
		}

		builder.setLeftBuffTimes(infoBuilder.getLeftBuffTimes());
		builder.setBufMultiple(infoBuilder.getBufMultiple());
		builder.setFreeCD(infoBuilder.getFreeCD());
		builder.setLeftAwardTimes(infoBuilder.getLeftAwardTimes());
		player.sendProtocol(Protocol.valueOf(HP.code.NEW_UR_SEARCH_S_VALUE, builder));
		// 更新玩家的活动数据
		player.getPlayerData().updateActivity(Const.ActivityId.NEW_UR_VALUE, 0);
		// BI 日志 (freeTimes 本次寻宝开始前今日剩余的免费次数)
		BehaviorLogger.log4Platform(player, Action.NEW_UR, Params.valueOf("searchTimes", searchTimes), Params.valueOf("freeTimes", searchTimes - payTimes), Params.valueOf("costGold", payGold),
				Params.valueOf("awards", awardsList));
		return true;
	}

}
