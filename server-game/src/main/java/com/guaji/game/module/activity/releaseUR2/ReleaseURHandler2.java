package com.guaji.game.module.activity.releaseUR2;

import java.util.ArrayList;
import java.util.List;

import org.guaji.app.AppObj;
import org.guaji.net.protocol.IProtocolHandler;
import org.guaji.net.protocol.Protocol;

import com.guaji.game.config.ActivityTimeCfg;
import com.guaji.game.config.ReleaseURDropCfg;
import com.guaji.game.config.ReleaseURDropCfg2;
import com.guaji.game.config.ReleaseURTimesCfg;
import com.guaji.game.config.ReleaseURTimesCfg2;
import com.guaji.game.config.SysBasicCfg;
import com.guaji.game.item.AwardItems;
import com.guaji.game.item.ConsumeItems;
import com.guaji.game.item.ItemInfo;
import com.guaji.game.item.AwardItems.Item;
import com.guaji.game.log.BehaviorLogger;
import com.guaji.game.log.BehaviorLogger.Action;
import com.guaji.game.log.BehaviorLogger.Params;
import com.guaji.game.module.activity.releaseUR.ReleaseURInfoHandler;
import com.guaji.game.module.activity.releaseUR.ReleaseURStatus;
import com.guaji.game.player.Player;
import com.guaji.game.protocol.Const;
import com.guaji.game.protocol.HP;
import com.guaji.game.protocol.Status;
import com.guaji.game.protocol.Activity3.ReleaseURDraw;
import com.guaji.game.protocol.Activity3.ReleaseURDraw2;
import com.guaji.game.protocol.Activity3.ReleaseURInfo;
import com.guaji.game.protocol.Activity3.ReleaseURInfo2;
import com.guaji.game.protocol.Const.changeType;
import com.guaji.game.util.ActivityUtil;

/**
* @author 作者 E-mail:Jeremy@163.com
* @version 创建时间：2019年4月5日 下午9:30:35
* 类说明
*/
public class ReleaseURHandler2 implements IProtocolHandler{
	static final int TIMES_TYPE_SINGLE = 1;
	static final int TIMES_TYPE_TEN = 10;

	@Override // RELEASE_UR_INFO_C
	public boolean onProtocol(AppObj appObj, Protocol protocol) {
		Player player = (Player) appObj;
		// 检测活动是否开放
		int activityId = Const.ActivityId.RELEASE_UR2_VALUE;
		ActivityTimeCfg timeCfg = ActivityUtil.getCurActivityTimeCfg(activityId);
		if (timeCfg == null) {
			// 活动已关闭
			player.sendError(protocol.getType(), Status.error.ACTIVITY_CLOSE);
			return true;
		}

		ReleaseURDraw2 req = protocol.parseProtocol(ReleaseURDraw2.getDefaultInstance());
		int searchTimes = req.getTimes();
		
		int payTimes = searchTimes;
		if (searchTimes != TIMES_TYPE_SINGLE && searchTimes != TIMES_TYPE_TEN) {
			// 活动参数错误
			player.sendError(protocol.getType(), Status.error.PARAMS_INVALID);
			return true;
		}
		
		ReleaseURTimesCfg2 timesCfg = ReleaseURTimesCfg2.getTimesCfgByVipLevel(player.getVipLevel());
		if (timesCfg == null) {
			player.sendError(protocol.getType(), Status.error.CONFIG_NOT_FOUND);
			return true;
		}
		ReleaseURStatu2 status = ActivityUtil.getActivityStatus(player.getPlayerData(), activityId, timeCfg.getStageId(), ReleaseURStatu2.class);
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
			int freeCD = (int) Math.max(ReleaseURInfoHandler2.convertTimeToMillisecond(timesCfg.getFreeCountDown()) - (currentTime - lastFreeTime), 0);
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
			player.consumeGold(payGold, Action.RELEASE_UR2);
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
			List<ItemInfo> searchItems = ReleaseURDropCfg2.treasureRaiderDropItem(totalTimes + 1);
			awards.addItemInfos(searchItems);
			// 翻倍
			int multiple =1;
			multiple = Math.max(multiple, 1);
			multiples.add(multiple);
			
			for (Item item : awards.getAwardItems()) {
				item.count = item.count * multiple;
			}
			
			awardsList.add(awards);
		
			status.setLuckyValue(status.getLuckyValue() + SysBasicCfg.getInstance().getReleaseUrGiveLucky2());
			status.setTotalTimes(status.getTotalTimes() + 1);
			
			awards.rewardTakeAffectAndPush(player, Action.RELEASE_UR2, 0);
		}

		ReleaseURInfo2.Builder infoBuilder = ReleaseURInfoHandler2.generateInfo(player);
		ReleaseURInfo2.Builder builder = ReleaseURInfo2.newBuilder();
		builder.setLeftTime(infoBuilder.getLeftTime());
		builder.setFreeTreasureTimes(infoBuilder.getFreeTreasureTimes());
		builder.setLeftTreasureTimes(infoBuilder.getLeftTreasureTimes());
		builder.setOnceCostGold(infoBuilder.getOnceCostGold());
		builder.setTenCostGold(infoBuilder.getTenCostGold());
		builder.setLotterypoint(status.getLuckyValue());

		for (AwardItems item : awardsList) {
			builder.addReward(item.toString());
		}
		
		builder.setRandCostGold(0);
		builder.setLotterypoint(status.getLuckyValue());
		
		builder.addAllLatticeIndex(status.getLastRandomIndexs());
		builder.setLotteryCost(SysBasicCfg.getInstance().getReleaseUrCostLucky2());

		builder.setFreeCD(infoBuilder.getFreeCD());
		builder.setLeftAwardTimes(infoBuilder.getLeftAwardTimes());
		player.sendProtocol(Protocol.valueOf(HP.code.RELEASE_UR_DRAW2_S, builder));
		// 更新玩家的活动数据
		player.getPlayerData().updateActivity(activityId, timeCfg.getStageId());
		// BI 日志 (freeTimes 本次寻宝开始前今日剩余的免费次数)
		BehaviorLogger.log4Platform(player, Action.RELEASE_UR2, Params.valueOf("searchTimes", searchTimes),
		        Params.valueOf("freeTimes", searchTimes - payTimes), Params.valueOf("costGold", payGold), Params.valueOf("awards", awardsList));
		return true;
	}
}
