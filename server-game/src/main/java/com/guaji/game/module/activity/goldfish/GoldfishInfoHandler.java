package com.guaji.game.module.activity.goldfish;

import org.guaji.app.AppObj;
import org.guaji.config.ConfigManager;
import org.guaji.net.protocol.Protocol;
import org.guaji.net.protocol.IProtocolHandler;

import com.guaji.game.protocol.CatchFish.FishingInfoResponse;
import com.guaji.game.config.ActivityTimeCfg;
import com.guaji.game.config.GoldfishFeaturesCfg;
import com.guaji.game.player.Player;
import com.guaji.game.util.ActivityUtil;
import com.guaji.game.protocol.Const;
import com.guaji.game.protocol.HP;
import com.guaji.game.protocol.Status;

/**
 * 捞金鱼活动初始化
 */
public class GoldfishInfoHandler implements IProtocolHandler {

	@Override
	public boolean onProtocol(AppObj appObj, Protocol protocol) {

		Player player = (Player) appObj;
		int activityId = Const.ActivityId.GOLD_FISH_VALUE;
		ActivityTimeCfg timeCfg = ActivityUtil.getCurActivityTimeCfg(activityId);
		if (timeCfg == null) {
			// 活动已关闭
			player.sendError(HP.code.FISHING_INFO_C_VALUE, Status.error.ACTIVITY_CLOSE);
			return true;
		}
		// 提取功能配置数据
		GoldfishFeaturesCfg config = ConfigManager.getInstance().getConfigByIndex(GoldfishFeaturesCfg.class, 0);
		if (null == config) {
			player.sendError(HP.code.FISHING_INFO_C_VALUE, Status.error.DATA_NOT_FOUND_VALUE);
			return true;
		}
		// 提取玩家数据
		GoldfishStatus db_data = ActivityUtil.getActivityStatus(player.getPlayerData(), activityId, timeCfg.getStageId(), GoldfishStatus.class);
		if (db_data == null) {
			player.sendError(HP.code.FISHING_INFO_C_VALUE, Status.error.DATA_NOT_FOUND_VALUE);
			return true;
		}
		// 设置免费次数
		boolean isFirst = db_data.isFirstTime();
		if (isFirst) {
			if (null == db_data.getName()) {
				db_data.setPlayerId(player.getId());
				db_data.setName(player.getName());
			}
			db_data.setFreeTimes(config.getFreeTimes());
			// 数据落地
			player.getPlayerData().updateActivity(Const.ActivityId.GOLD_FISH_VALUE, timeCfg.getStageId());
		}
		// 构建返回数据包
		FishingInfoResponse.Builder builder = FishingInfoResponse.newBuilder();
		
		builder.setFreeTimes(db_data.getFreeTimes());
		builder.setSingleCost(config.getSingleCost());
		builder.setContinuousCost(config.getContinuousCost());
		builder.setScore(db_data.getScore());
		builder.setClosetimes(timeCfg.calcActivitySurplusTime());
		
		player.sendProtocol(Protocol.valueOf(HP.code.FISHING_INFO_S_VALUE, builder));
		return true;
	}

}
