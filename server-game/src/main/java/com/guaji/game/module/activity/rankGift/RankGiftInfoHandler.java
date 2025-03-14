package com.guaji.game.module.activity.rankGift;


import org.guaji.app.AppObj;
import org.guaji.net.protocol.Protocol;
import org.guaji.net.protocol.IProtocolHandler;

import com.guaji.game.protocol.Activity2.HPRankGiftInfo;
import com.guaji.game.protocol.Activity2.HPRankGiftRet;
import com.guaji.game.config.ActivityCfg;
import com.guaji.game.config.ActivityTimeCfg;
import com.guaji.game.config.ActivityCfg.ActivityItem;
import com.guaji.game.manager.ActivityManager;
import com.guaji.game.manager.RankGiftManager;
import com.guaji.game.player.Player;
import com.guaji.game.util.ActivityUtil;
import com.guaji.game.protocol.Const;
import com.guaji.game.protocol.HP;
import com.guaji.game.protocol.Status;

/**
 * 排行献礼活动（开服活动）
 */
public class RankGiftInfoHandler implements IProtocolHandler {
	private Player player;
	private Protocol proto;
	private static final int HOUR_TO_MILLI = 3600 * 1000;

	@Override
	public boolean onProtocol(AppObj appObj, Protocol protocol) {
		player = (Player) appObj;
		proto = protocol;
		// 检测活动是否开放
		int activityId = Const.ActivityId.RANK_GIFT_VALUE;
		// 时间配置不对
		ActivityTimeCfg timeCfg = ActivityUtil.getCurActivityTimeCfg(activityId);
		if(timeCfg == null){
			// 活动已关闭
			player.sendError(protocol.getType(), Status.error.ACTIVITY_CLOSE);
			return false;
		}
		HPRankGiftRet.Builder builder = HPRankGiftRet.newBuilder();
		buildProtocol(builder, activityId);
		player.sendProtocol(Protocol.valueOf(HP.code.RANK_GIFT_INFO_S_VALUE, builder));
		return true;
	}
	
	/**
	 * 组装回复协议
	 * @param builder
	 * @param activityId
	 */
	private void buildProtocol(HPRankGiftRet.Builder builder, int activityId) {
		HPRankGiftInfo.Builder arenaRank = HPRankGiftInfo.newBuilder();
		HPRankGiftInfo.Builder expRank = HPRankGiftInfo.newBuilder();
		ActivityItem activityCfg = ActivityCfg.getActivityItem(activityId);
		long openTime = ((int)activityCfg.getParam("rankHours")) * HOUR_TO_MILLI;
		long cloTime = (int)activityCfg.getParam("openHours");
		long closeTime = cloTime * HOUR_TO_MILLI;
		long closeLeftTime = ActivityManager.getInstance().openServerLeftTime(closeTime);
		if (closeLeftTime == -1) {
			player.sendError(proto.getType(), Status.error.ACTIVITY_CLOSE);
			return;
		}
		long openLeftTime = ActivityManager.getInstance().openServerLeftTime(openTime);
		if (openLeftTime == -1) {
			builder.setTotalTime(openTime);
			builder.setLeftTimes(0);
		} else {
			builder.setTotalTime(openTime);
			builder.setLeftTimes(openLeftTime);
		}
		builder.setArenaRankInfo(buildArenaRank(arenaRank));
		builder.setExpRankInfo(buildExpRank(expRank));
	}
	
	/**
	 * 竞技场排名
	 * @param arenaRank
	 * @return
	 */
	private HPRankGiftInfo.Builder buildArenaRank(HPRankGiftInfo.Builder arenaRank) {
		RankGiftManager.getInstance().arenaRank(arenaRank, player.getId());
		return arenaRank;
	}
	
	/**
	 * 经验排名
	 * @param expRank
	 * @return
	 */
	private HPRankGiftInfo.Builder buildExpRank(HPRankGiftInfo.Builder expRank) {
		RankGiftManager.getInstance().expRank(expRank, player.getId());
		return expRank;
	}
}
