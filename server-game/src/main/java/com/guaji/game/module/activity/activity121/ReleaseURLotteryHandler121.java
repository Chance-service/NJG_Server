package com.guaji.game.module.activity.activity121;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
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
import com.guaji.game.config.ReleaseURTimesCfg121;
import com.guaji.game.config.ReleaseUrLotteryCfg;
import com.guaji.game.config.ReleaseUrLotteryCfg121;
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

/**
 * @author 作者 E-mail:Jeremy@163.com
 * @version 创建时间：2019年3月14日 下午11:28:52 类说明
 */
public class ReleaseURLotteryHandler121 implements IProtocolHandler {

	@Override
	public boolean onProtocol(AppObj appObj, Protocol protocol) {
		Player player = (Player) appObj;
		// 检测活动是否开放
		int activityId = Const.ActivityId.RELEASE_UR3_VALUE;
		ActivityTimeCfg timeCfg = ActivityUtil.getCurActivityTimeCfg(activityId);
		if (timeCfg == null) {
			// 活动已关闭
			player.sendError(protocol.getType(), Status.error.ACTIVITY_CLOSE);
			return true;
		}
		ActivityTimeCfg activityTimeCfg = ActivityUtil.getCurActivityTimeCfg(activityId);

		ReleaseURTimesCfg121 timesCfg = ReleaseURTimesCfg121.getTimesCfgByVipLevel(player.getVipLevel());
		if (timesCfg == null) {
			player.sendError(protocol.getType(), Status.error.CONFIG_NOT_FOUND);
			return true;
		}

		ReleaseURStatus121 status = ActivityUtil.getActivityStatus(player.getPlayerData(), activityId,
				activityTimeCfg.getStageId(), ReleaseURStatus121.class);
		// 抽奖需要消耗的幸运值
		int needLuckyValue = SysBasicCfg.getInstance().getReleaseUrLotteryCost3();//

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
		Map<Object, ReleaseUrLotteryCfg121> configMap = ConfigManager.getInstance()
				.getConfigMap(ReleaseUrLotteryCfg121.class);
		// 遍历map中的值
		for (ReleaseUrLotteryCfg121 value : configMap.values()) {
			allCfgId.add(value.getId());
		}

		allCfgId.removeAll(status.getLotteryIndexs());

		int index = -1;
		try {
			index = GuaJiRand.randInt(0, allCfgId.size() - 1);
		} catch (MyException e) {
			e.printStackTrace();
		}
		List<Integer> surplusItems = new ArrayList<>(allCfgId);

		Integer cfgId = surplusItems.get(index);
		ReleaseUrLotteryCfg121 releasLottery = ConfigManager.getInstance().getConfigByKey(ReleaseUrLotteryCfg121.class,
				cfgId);

		if (releasLottery == null) {
			player.sendError(protocol.getType(), Status.error.ReleaseUR_CANNOT_LOTTERY_VALUE);
			return true;
		}

		status.AddLotteryIndexs(releasLottery.getId());

		// 若是最后一次抽奖,自动重置
		if (status.getLotteryIndexs().size() == ConfigManager.getInstance().getConfigMap(ReleaseUrLotteryCfg121.class)
				.size())
			status.getLotteryIndexs().clear();
		
		
		// 更新玩家的活动数据
		player.getPlayerData().updateActivity(activityId, timeCfg.getStageId());
		
		// 下发奖励
		AwardItems awards = AwardItems.valueOf(releasLottery.getAwards());
		awards.rewardTakeAffectAndPush(player, Action.RELEASELOTTERY3_AWARDS, 0);

		ReleaseURLotteryRep.Builder builder = ReleaseURLotteryRep.newBuilder();
		builder.setIndex(releasLottery.getId());
		builder.setComluckey(needLuckyValue);
		// 构建需要的道具信息
		String excInfo="";
		for (ItemInfo itemInfo : AwardItems.valueOf(releasLottery.getAwards()).getAwardItemInfos()) {
			RoleRelatedCfg cfg = ConfigManager.getInstance().getConfigByKey(RoleRelatedCfg.class, itemInfo.getItemId());
			if (cfg != null) {
			
				excInfo=cfg.getExchange();
				break;
			}
		}
		builder.setExcInfo(excInfo);
		player.sendProtocol(Protocol.valueOf(HP.code.RELEASE_UR_LOTTERY3_S_VALUE, builder));
	

		// BI 日志 (freeTimes 本次寻宝开始前今日剩余的免费次数)
		BehaviorLogger.log4Platform(player, Action.RELEASELOTTERY3_AWARDS,
				Params.valueOf("LOTTERYINDEX", releasLottery.getId()),
				Params.valueOf("awards", releasLottery.getAwards()));

		return false;
	}

}
