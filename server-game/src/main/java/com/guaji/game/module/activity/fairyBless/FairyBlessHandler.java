package com.guaji.game.module.activity.fairyBless;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.guaji.app.AppObj;
import org.guaji.net.protocol.Protocol;
import org.guaji.net.protocol.IProtocolHandler;

import com.guaji.game.protocol.Activity2.FairyBlessInfo;
import com.guaji.game.protocol.Activity2.FairyBlessReq;
import com.guaji.game.protocol.Activity2.HPRedPointInfo;
import com.guaji.game.protocol.Activity2.SyncFairyBlessRes;
import com.guaji.game.config.ActivityTimeCfg;
import com.guaji.game.config.FairyBlessCfg;
import com.guaji.game.config.SysBasicCfg;
import com.guaji.game.item.AwardItems;
import com.guaji.game.log.BehaviorLogger;
import com.guaji.game.log.BehaviorLogger.Action;
import com.guaji.game.log.BehaviorLogger.Params;
import com.guaji.game.log.BehaviorLogger.Source;
import com.guaji.game.player.Player;
import com.guaji.game.util.ActivityUtil;
import com.guaji.game.protocol.Const;
import com.guaji.game.protocol.HP;
import com.guaji.game.protocol.Status;

/**
 * 仙女的保佑
 */
public class FairyBlessHandler implements IProtocolHandler {

	@Override
	public boolean onProtocol(AppObj appObj, Protocol protocol) {

		Player player = (Player) appObj;
		int activityId = Const.ActivityId.FAIRY_BLESS_VALUE;
		ActivityTimeCfg timeCfg = ActivityUtil.getCurActivityTimeToEndCfg(activityId);
		if (timeCfg == null) {
			// 活动已关闭
			player.sendError(HP.code.FAIRY_BLESS_C_VALUE, Status.error.ACTIVITY_CLOSE);
			return true;
		}
		// 解析数据
		FairyBlessReq request = protocol.parseProtocol(FairyBlessReq.getDefaultInstance());
		int type = request.getType();
		// 提取玩家数据
		FairyBlessStatus fairyBlessStatus = ActivityUtil.getActivityStatus(player.getPlayerData(), activityId, timeCfg.getStageId(), FairyBlessStatus.class);
		Map<Integer, Integer> progressMap = fairyBlessStatus.getProgressMap();
		// 提取功能配置数据
		FairyBlessCfg config = FairyBlessCfg.typeMap.get(type);
		String rewards = config.getRandmReward(type);
		int costFlower = config.getCostFlower();
		// 判断当前剩余鲜花是不是够用
		int ownFlower = fairyBlessStatus.getFlowerCount();
		if (ownFlower < costFlower) {
			player.sendError(HP.code.FAIRY_BLESS_C_VALUE, Status.error.FLOWER_NOT_ENOUGH);
			return true;
		}
		// 更新进度
		int progress = progressMap.get(type);
		int totalProgress = config.getTotalProgress();
		if (progress >= totalProgress) {
			player.sendError(HP.code.FAIRY_BLESS_C_VALUE, Status.error.FAIRY_BLESS_PROGRESS_COMPLETE);
			return true;
		}
		progress += 1;
		// 进度完成发放奖励
		if (progress == totalProgress) {
			// 发放奖励
			AwardItems awardItems = AwardItems.valueOf(rewards.toString());
			awardItems.rewardTakeAffectAndPush(player, Action.FAIRY_BLESS, 2);
			// 清空进度从0开始
			progress = 0;
			// 统计完成的数据
			BehaviorLogger.log4Service(player, Source.USER_OPERATION, Action.FAIRY_BLESS, Params.valueOf("type", type), Params.valueOf("flag", "complete"));
			BehaviorLogger.log4Platform(player, Action.FAIRY_BLESS, Params.valueOf("type", type), Params.valueOf("flag", "complete"));
		}
		// 更新活动数据
		progressMap.put(type, progress);
		ownFlower -= costFlower;
		fairyBlessStatus.setFlowerCount(ownFlower);
		fairyBlessStatus.setProgressMap(progressMap);
		player.getPlayerData().updateActivity(activityId, timeCfg.getStageId());
		// 统计单次参加活动
		BehaviorLogger.log4Service(player, Source.USER_OPERATION, Action.FAIRY_BLESS, Params.valueOf("type", type), Params.valueOf("flag", "join"));
		BehaviorLogger.log4Platform(player, Action.FAIRY_BLESS, Params.valueOf("type", type), Params.valueOf("flag", "join"));
		// 推送同步消息
		SyncFairyBlessRes.Builder response = SyncFairyBlessRes.newBuilder();
		response.setFlower(ownFlower);
		response.setLeftTime(timeCfg.calcActivitySurplusTime());
		
		FairyBlessInfo.Builder fairyBlessInfo = FairyBlessInfo.newBuilder();
		fairyBlessInfo.setType(type);
		fairyBlessInfo.setProgress(progress);
		response.addFairyBlessInfo(fairyBlessInfo);
		player.sendProtocol(Protocol.valueOf(HP.code.SYNC_FAIRY_BLESS_S_VALUE, response));
		return true;
	}
	
	/**
	 * 充值成功后增加活动所需的鲜花
	 */
	public static void onRechargeSuccessAddFlower(Player player, int gold) {
		int activityId = Const.ActivityId.FAIRY_BLESS_VALUE;
		ActivityTimeCfg timeCfg = ActivityUtil.getCurActivityTimeToEndCfg(activityId);
		if (null != timeCfg) {
			int ratio = SysBasicCfg.getInstance().getGoldFlowerRatio();
			FairyBlessStatus fairyBlessStatus = ActivityUtil.getActivityStatus(player.getPlayerData(), activityId, timeCfg.getStageId(), FairyBlessStatus.class);
			int deltaFlower = gold / ratio;
			int ownFlower = fairyBlessStatus.getFlowerCount();
			ownFlower += deltaFlower;
			fairyBlessStatus.setFlowerCount(ownFlower);
			player.getPlayerData().updateActivity(activityId, timeCfg.getStageId());
			// 每次充值变化并且花数大于最低档的时候显示小红点 进入界面小红点消失
			List<Integer> pointList = new ArrayList<Integer>();
			int flowerCount = fairyBlessStatus.getFlowerCount();
			if (flowerCount >= FairyBlessCfg.lowestLevelFlower) {
				pointList.add(activityId);
			}
			HPRedPointInfo.Builder builder = HPRedPointInfo.newBuilder();
			builder.addAllPointActivityIdList(pointList);
			player.sendProtocol(Protocol.valueOf(HP.code.RED_POINT_LIST_SYNC_S_VALUE, builder));
		}
	}
}
