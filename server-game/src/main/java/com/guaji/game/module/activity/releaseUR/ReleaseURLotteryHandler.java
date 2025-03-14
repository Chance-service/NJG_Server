package com.guaji.game.module.activity.releaseUR;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Set;

import org.guaji.app.AppObj;
import org.guaji.config.ConfigManager;
import org.guaji.net.protocol.IProtocolHandler;
import org.guaji.net.protocol.Protocol;
import org.guaji.os.GuaJiRand;
import org.guaji.os.MyException;

import com.guaji.game.config.ActivityTimeCfg;
import com.guaji.game.config.ReleaseURTimesCfg;
import com.guaji.game.config.ReleaseUrLotteryCfg;
import com.guaji.game.config.RoleRelatedCfg;
import com.guaji.game.config.SysBasicCfg;
import com.guaji.game.item.AwardItems;
import com.guaji.game.item.ItemInfo;
import com.guaji.game.log.BehaviorLogger;
import com.guaji.game.log.BehaviorLogger.Action;
import com.guaji.game.log.BehaviorLogger.Params;
import com.guaji.game.player.Player;
import com.guaji.game.protocol.Activity3.ReleaseURDraw;
import com.guaji.game.protocol.Activity3.ReleaseURLotteryRep;
import com.guaji.game.protocol.Activity3.ReleaseURLotteryReq;
import com.guaji.game.protocol.Const;
import com.guaji.game.protocol.HP;
import com.guaji.game.protocol.Status;
import com.guaji.game.util.ActivityUtil;
import com.guaji.game.util.WeightUtil;
import com.guaji.game.util.WeightUtil.WeightItem;

/**
 * @author 作者 E-mail:Jeremy@163.com
 * @version 创建时间：2019年3月14日 下午11:28:52 类说明
 */
public class ReleaseURLotteryHandler implements IProtocolHandler {

	@Override
	public boolean onProtocol(AppObj appObj, Protocol protocol) {
		Player player = (Player) appObj;
		// 检测活动是否开放
		int activityId = Const.ActivityId.RELEASE_UR_VALUE;
		ActivityTimeCfg timeCfg = ActivityUtil.getCurActivityTimeCfg(activityId);
		if (timeCfg == null) {
			// 活动已关闭
			player.sendError(protocol.getType(), Status.error.ACTIVITY_CLOSE);
			return true;
		}
		ActivityTimeCfg activityTimeCfg = ActivityUtil.getCurActivityTimeCfg(activityId);

		ReleaseURTimesCfg timesCfg = ReleaseURTimesCfg.getTimesCfgByVipLevel(player.getVipLevel());
		if (timesCfg == null) {
			player.sendError(protocol.getType(), Status.error.CONFIG_NOT_FOUND);
			return true;
		}

		ReleaseURStatus status = ActivityUtil.getActivityStatus(player.getPlayerData(), activityId,
				activityTimeCfg.getStageId(), ReleaseURStatus.class);
		// 抽奖需要消耗的幸运值
		int needLuckyValue = SysBasicCfg.getInstance().getReleaseUrLotteryCost();//

		if (needLuckyValue > status.getLuckyValue()) {
			player.sendError(protocol.getType(), Status.error.ReleaseUR_NOT_ENOUGH_LOTTERY_VALUE);
			return true;
		}
		status.setLuckyValue(status.getLuckyValue() - needLuckyValue);

		if (status.getLotteryIndexs().size() >= 9) {

			player.sendError(protocol.getType(), Status.error.ReleaseUR_CANNOT_LOTTERY_VALUE);
			return true;
		}

		Set<Integer> allCfgId = new HashSet<Integer>();
		Map<Object, ReleaseUrLotteryCfg> configMap = ConfigManager.getInstance()
				.getConfigMap(ReleaseUrLotteryCfg.class);
		// 遍历map中的值
		for (ReleaseUrLotteryCfg value : configMap.values()) {
			allCfgId.add(value.getId());
		}

		allCfgId.removeAll(status.getLotteryIndexs());

	
		List<Integer> surplusItems = new ArrayList<>(allCfgId);


		List<WeightItem<Integer>> itemList = new LinkedList<>();

		for (int i = 0; i < surplusItems.size(); i++) {
			ReleaseUrLotteryCfg releasLottery = ConfigManager.getInstance().getConfigByKey(ReleaseUrLotteryCfg.class,
					surplusItems.get(i));
			if (releasLottery != null) {
				WeightItem<Integer> item = WeightItem.valueOf(surplusItems.get(i), releasLottery.getWeight());
				itemList.add(item);
			}
		}

		Integer cfgId =WeightUtil.random(itemList);
		ReleaseUrLotteryCfg releasLottery = ConfigManager.getInstance().getConfigByKey(ReleaseUrLotteryCfg.class,
				cfgId);

		if (releasLottery == null) {
			player.sendError(protocol.getType(), Status.error.ReleaseUR_CANNOT_LOTTERY_VALUE);
			return true;
		}

		status.AddLotteryIndexs(releasLottery.getId());

		// 若是最后一次抽奖,自动重置
		if (status.getLotteryIndexs().size() == ConfigManager.getInstance().getConfigMap(ReleaseUrLotteryCfg.class)
				.size())
			status.getLotteryIndexs().clear();

		// 更新玩家的活动数据
		player.getPlayerData().updateActivity(activityId, timeCfg.getStageId());

		// 下发奖励
		AwardItems awards = AwardItems.valueOf(releasLottery.getAwards());
		awards.rewardTakeAffectAndPush(player, Action.RELEASELOTTERY_AWARDS, 0);

		ReleaseURLotteryRep.Builder builder = ReleaseURLotteryRep.newBuilder();
		builder.setIndex(releasLottery.getId());
		builder.setComluckey(needLuckyValue);
		// 构建需要的道具信息
		String excInfo = "";
		for (ItemInfo itemInfo : AwardItems.valueOf(releasLottery.getAwards()).getAwardItemInfos()) {
			RoleRelatedCfg cfg = ConfigManager.getInstance().getConfigByKey(RoleRelatedCfg.class, itemInfo.getItemId());
			if (cfg != null) {

				excInfo = cfg.getExchange();
				break;
			}
		}
		builder.setExcInfo(excInfo);
		player.sendProtocol(Protocol.valueOf(HP.code.RELEASE_UR_LOTTERY_S_VALUE, builder));

		// BI 日志 (freeTimes 本次寻宝开始前今日剩余的免费次数)
		BehaviorLogger.log4Platform(player, Action.RELEASELOTTERY_AWARDS,
				Params.valueOf("LOTTERYINDEX", releasLottery.getId()),
				Params.valueOf("awards", releasLottery.getAwards()));

		return false;
	}

}
