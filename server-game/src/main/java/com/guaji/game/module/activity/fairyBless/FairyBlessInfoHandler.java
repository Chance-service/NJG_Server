package com.guaji.game.module.activity.fairyBless;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.guaji.app.AppObj;
import org.guaji.net.protocol.Protocol;
import org.guaji.net.protocol.IProtocolHandler;

import com.guaji.game.protocol.Activity2.FairyBlessInfo;
import com.guaji.game.protocol.Activity2.SyncFairyBlessRes;
import com.guaji.game.config.ActivityTimeCfg;
import com.guaji.game.player.Player;
import com.guaji.game.util.ActivityUtil;
import com.guaji.game.protocol.Const;
import com.guaji.game.protocol.HP;
import com.guaji.game.protocol.Status;

/**
 * 仙女的保佑活动同步
 */
public class FairyBlessInfoHandler implements IProtocolHandler {

	@Override
	public boolean onProtocol(AppObj appObj, Protocol protocol) {

		Player player = (Player) appObj;
		int activityId = Const.ActivityId.FAIRY_BLESS_VALUE;
		ActivityTimeCfg timeCfg = ActivityUtil.getCurActivityTimeCfg(activityId);
		if (timeCfg == null) {
			// 活动已关闭
			player.sendError(HP.code.SYNC_FAIRY_BLESS_C_VALUE, Status.error.ACTIVITY_CLOSE);
			return true;
		}
		// 提取玩家数据
		FairyBlessStatus fairyBlesseStatus = ActivityUtil.getActivityStatus(player.getPlayerData(), activityId, timeCfg.getStageId(), FairyBlessStatus.class);
		if (fairyBlesseStatus == null) {
			player.sendError(HP.code.SYNC_FAIRY_BLESS_C_VALUE, Status.error.DATA_NOT_FOUND_VALUE);
			return true;
		}
		// 发送三种类型的状态信息
		SyncFairyBlessRes.Builder response = SyncFairyBlessRes.newBuilder();
		response.setFlower(fairyBlesseStatus.getFlowerCount());
		response.setLeftTime(timeCfg.calcActivitySurplusTime());
		List<FairyBlessInfo> fairyBlessInfoList = new ArrayList<FairyBlessInfo>();
		Map<Integer, Integer> progressMap = fairyBlesseStatus.getProgressMap();
		for (Entry<Integer, Integer> entry : progressMap.entrySet()) {
			FairyBlessInfo.Builder fairyBlessInfo = FairyBlessInfo.newBuilder();
			fairyBlessInfo.setType(entry.getKey());
			fairyBlessInfo.setProgress(entry.getValue());
			fairyBlessInfoList.add(fairyBlessInfo.build());
		}
		response.addAllFairyBlessInfo(fairyBlessInfoList);
		player.sendProtocol(Protocol.valueOf(HP.code.SYNC_FAIRY_BLESS_S_VALUE, response));
		return true;
	}
	
}
