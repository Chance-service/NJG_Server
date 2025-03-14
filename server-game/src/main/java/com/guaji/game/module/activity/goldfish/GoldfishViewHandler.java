package com.guaji.game.module.activity.goldfish;

import java.util.Set;

import org.guaji.app.AppObj;
import org.guaji.net.protocol.Protocol;
import org.guaji.net.protocol.IProtocolHandler;

import com.guaji.game.protocol.CatchFish.FishPreviewResponse;
import com.guaji.game.config.ActivityTimeCfg;
import com.guaji.game.player.Player;
import com.guaji.game.util.ActivityUtil;
import com.guaji.game.protocol.Const;
import com.guaji.game.protocol.HP;
import com.guaji.game.protocol.Status;

/**
 * 查看预览
 */
public class GoldfishViewHandler implements IProtocolHandler {

	@Override
	public boolean onProtocol(AppObj appObj, Protocol protocol) {

		Player player = (Player) appObj;
		int activityId = Const.ActivityId.GOLD_FISH_VALUE;
		ActivityTimeCfg timeCfg = ActivityUtil.getCurActivityTimeCfg(activityId);
		if (timeCfg == null) {
			// 活动已关闭
			player.sendError(HP.code.FISH_PREVIEW_C_VALUE, Status.error.ACTIVITY_CLOSE);
			return true;
		}
		// 提取玩家数据
		GoldfishStatus db_data = ActivityUtil.getActivityStatus(player.getPlayerData(), activityId, timeCfg.getStageId(), GoldfishStatus.class);
		if (db_data == null) {
			 player.sendError(HP.code.FISH_PREVIEW_C_VALUE, Status.error.DATA_NOT_FOUND_VALUE);
			return true;
		}
		// 构建返回数据包
		Set<Integer> fishId = db_data.getFishId();
		FishPreviewResponse.Builder builder = FishPreviewResponse.newBuilder();
		if (null != fishId && fishId.size() > 0) {
			builder.addAllFishId(fishId);
			builder.setIsSend(true);
		} else {
			builder.setIsSend(false);
		}

		player.sendProtocol(Protocol.valueOf(HP.code.FISH_PREVIEW_S_VALUE, builder));
		
		return true;
	}

}
