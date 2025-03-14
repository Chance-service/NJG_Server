package com.guaji.game.module.activity.goldfish;

import java.util.List;

import org.guaji.app.AppObj;
import org.guaji.config.ConfigManager;
import org.guaji.net.protocol.Protocol;
import org.guaji.net.protocol.IProtocolHandler;

import com.guaji.game.protocol.CatchFish.FishingRankResponse;
import com.guaji.game.protocol.CatchFish.RankMessage;
import com.guaji.game.config.ActivityTimeCfg;
import com.guaji.game.config.GoldfishFeaturesCfg;
import com.guaji.game.manager.GoldfishRankManager;
import com.guaji.game.player.Player;
import com.guaji.game.util.ActivityUtil;
import com.guaji.game.protocol.Const;
import com.guaji.game.protocol.HP;
import com.guaji.game.protocol.Status;

/**
 * 捞鱼活动积分排行榜
 */
public class GoldfishRankHandler implements IProtocolHandler {

	@Override
	public boolean onProtocol(AppObj appObj, Protocol protocol) {
		
		Player player = (Player) appObj;
		int activityId = Const.ActivityId.GOLD_FISH_VALUE;
		ActivityTimeCfg timeCfg = ActivityUtil.getCurActivityTimeCfg(activityId);
		if (timeCfg == null) {
			// 活动已关闭
			player.sendError(HP.code.FISHING_RANK_C_VALUE, Status.error.ACTIVITY_CLOSE);
			return true;
		}
		// 提取功能配置数据
		GoldfishFeaturesCfg config = ConfigManager.getInstance().getConfigByIndex(GoldfishFeaturesCfg.class, 0);
		if (null == config) {
			player.sendError(HP.code.FISHING_RANK_C_VALUE, Status.error.DATA_NOT_FOUND_VALUE);
			return true;
		}
		// 刷新排行数据
		GoldfishRankManager.getInstance().refreshRank();
		// 获取排行数据
		List<RankMessage.Builder> listBuilder = GoldfishRankManager.getInstance().getRankTop(config.getRankNumber());
		// 构建返回数据包
		FishingRankResponse.Builder builder = FishingRankResponse.newBuilder();
		if (null != listBuilder && listBuilder.size() > 0) {
			for(RankMessage.Builder messageBuilder : listBuilder){
				builder.addRankMessage(messageBuilder);
			}
			builder.setIsSend(true);
		} else {
			builder.setIsSend(false);
		}
		player.sendProtocol(Protocol.valueOf(HP.code.FISHING_RANK_S_VALUE, builder));
		return true;
	}

}
