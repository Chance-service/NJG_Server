package com.guaji.game.module.activity.activity123;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.guaji.app.AppObj;
import org.guaji.config.ConfigManager;
import org.guaji.net.protocol.IProtocolHandler;
import org.guaji.net.protocol.Protocol;
import org.guaji.os.GuaJiRand;

import com.guaji.game.config.ActivityTimeCfg;
import com.guaji.game.config.AllianceBattleBuffCfg;
import com.guaji.game.config.ReleaseURTimesCfg123;
import com.guaji.game.config.ReleaseURTimesCfg2;
import com.guaji.game.config.ReleaseUrLotteryCfg123;
import com.guaji.game.config.ReleaseUrLotteryCfg2;
import com.guaji.game.config.RoleRelatedCfg;
import com.guaji.game.config.SysBasicCfg;
import com.guaji.game.item.AwardItems;
import com.guaji.game.item.ItemInfo;
import com.guaji.game.log.BehaviorLogger;
import com.guaji.game.log.BehaviorLogger.Action;
import com.guaji.game.log.BehaviorLogger.Params;
import com.guaji.game.player.Player;
import com.guaji.game.protocol.Const;
import com.guaji.game.protocol.HP;
import com.guaji.game.protocol.Status;
import com.guaji.game.protocol.Activity3.Activity123LotteryRep;
import com.guaji.game.protocol.Activity3.ReleaseURLotteryRep2;
import com.guaji.game.util.ActivityUtil;

/**
 * @author 作者 E-mail:Jeremy@163.com
 * @version 创建时间：2019年4月5日 下午9:31:17 类说明
 */
public class Activity123LotteryHandler implements IProtocolHandler {

	/**
	 * 连点成线的所有组合
	 */
	private static List<String> dotLineList;

	static {
		dotLineList = new ArrayList<>(
				Arrays.asList("0,1,2", "3,4,5", "6,7,8", "0,3,6", "1,4,7", "2,5,8", "0,4,8", "2,4,6"));
	}

	@Override
	public boolean onProtocol(AppObj appObj, Protocol protocol) {
		Player player = (Player) appObj;
		// 检测活动是否开放
		int activityId = Const.ActivityId.ACTIVITY123_UR_VALUE;
		ActivityTimeCfg timeCfg = ActivityUtil.getCurActivityTimeCfg(activityId);
		if (timeCfg == null) {
			// 活动已关闭
			player.sendError(protocol.getType(), Status.error.ACTIVITY_CLOSE);
			return true;
		}
		ActivityTimeCfg activityTimeCfg = ActivityUtil.getCurActivityTimeCfg(activityId);

		ReleaseURTimesCfg123 timesCfg = ReleaseURTimesCfg123.getTimesCfgByVipLevel(player.getVipLevel());
		if (timesCfg == null) {
			player.sendError(protocol.getType(), Status.error.CONFIG_NOT_FOUND);
			return true;
		}

		Activity123Status status = ActivityUtil.getActivityStatus(player.getPlayerData(), activityId,
				activityTimeCfg.getStageId(), Activity123Status.class);
		// 抽奖需要消耗的幸运值
		int needLuckyValue = SysBasicCfg.getInstance().getActivity123UrCostLucky();//

		if (needLuckyValue > status.getLuckyValue()) {
			player.sendError(protocol.getType(), Status.error.ReleaseUR_NOT_ENOUGH_LOTTERY_VALUE);
			return true;
		}
		status.setLuckyValue(status.getLuckyValue() - needLuckyValue);

		// 从9个数中随机出来4个
		List<Integer> randomResult = new ArrayList<>();
		// 判断随机出来的四个数是否有三个数在一条线上
		boolean isStraightLine = false;
		int rate = GuaJiRand.randInt(100);
		if (rate <= SysBasicCfg.getInstance().getAct123EvenDotsRate())
			isStraightLine = true;
		
		if (isStraightLine) {
			String[] dotLineItem = dotLineList.get(GuaJiRand.randInt(dotLineList.size() - 1)).split(",");
			for (int index = 0; index < dotLineItem.length; index++) {
				randomResult.add(Integer.parseInt(dotLineItem[index]));
			}
			status.getLastRandomIndexs().clear();
			status.getLastRandomIndexs().addAll(randomResult);
			// 更新玩家的活动数据
			player.getPlayerData().updateActivity(activityId, timeCfg.getStageId());

			ReleaseUrLotteryCfg123 lotteryCfg = ConfigManager.getInstance().getConfigByKey(ReleaseUrLotteryCfg123.class,
					timeCfg.getStageId());
			if (lotteryCfg == null) {
				player.sendError(protocol.getType(), Status.error.CONFIG_NOT_FOUND);
				return true;
			}

			// 下发奖励
			AwardItems awards = AwardItems.valueOf(lotteryCfg.getAwards());
			awards.rewardTakeAffectAndPush(player, Action.Activity123LOTTERY_AWARD, 0);
			Activity123LotteryRep.Builder builder = Activity123LotteryRep.newBuilder();
			builder.setStatus(1);
			builder.addAllLatticeIndex(randomResult);

			// 构建需要的道具信息
			String excInfo = "";
			for (ItemInfo itemInfo : AwardItems.valueOf(lotteryCfg.getAwards()).getAwardItemInfos()) {
				RoleRelatedCfg cfg = ConfigManager.getInstance().getConfigByKey(RoleRelatedCfg.class,
						itemInfo.getItemId());
				if (cfg != null) {

					excInfo = cfg.getExchange();
					break;
				}
			}

			builder.setExcInfo(excInfo);
			player.sendProtocol(Protocol.valueOf(HP.code.ACTIVITY123_UR_LOTTERY_S_VALUE, builder));

			// BI 日志 (freeTimes 本次寻宝开始前今日剩余的免费次数)
			BehaviorLogger.log4Platform(player, Action.Activity123LOTTERY_AWARD,
					Params.valueOf("awards", awards.getAwardItems()));

		} else {
			Activity123LotteryRep.Builder builder = Activity123LotteryRep.newBuilder();
			builder.setStatus(0);
			builder.addAllLatticeIndex(status.getLastRandomIndexs());
			builder.setExcInfo("");
			player.sendProtocol(Protocol.valueOf(HP.code.ACTIVITY123_UR_LOTTERY_S_VALUE, builder));

		}

		return true;
	}

}
