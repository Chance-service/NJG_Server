package com.guaji.game.module.activity.activity178;

import org.guaji.app.AppObj;
import org.guaji.net.protocol.IProtocolHandler;
import org.guaji.net.protocol.Protocol;

import com.guaji.game.config.ActivityTimeCfg;
import com.guaji.game.config.SysBasicCfg;
import com.guaji.game.player.Player;
import com.guaji.game.protocol.Activity4.ActivityCallInfo;
import com.guaji.game.protocol.Const;
import com.guaji.game.protocol.HP;
import com.guaji.game.protocol.Status;
import com.guaji.game.util.ActivityUtil;

/**
* @author 作者 E-mail:Jeremy@163.com
* @version 创建时间：2019年4月5日 下午9:30:55
* 类说明
*/
public class Activity178InfoHandler implements IProtocolHandler{
	@Override
	public boolean onProtocol(AppObj appObj, Protocol protocol) {
		Player player = (Player) appObj;
		// 检测活动是否开放
		int activityId = Const.ActivityId.ACTIVITY178_CALL_OF_EQUIP_VALUE;
		ActivityTimeCfg activityTimeCfg = ActivityUtil.getCurActivityTimeCfg(activityId);
		if (activityTimeCfg == null) {
			// 活动已关闭
			player.sendError(protocol.getType(), Status.error.ACTIVITY_CLOSE);
			return true;
		}

		ActivityCallInfo.Builder builder = generateInfo(player);
		
		player.sendProtocol(Protocol.valueOf(HP.code.ACTIVITY178_CALL_OF_EQUIP_INFO_S_VALUE, builder));
		return true;
	}

	public static ActivityCallInfo.Builder generateInfo(Player player) {
		int activityId = Const.ActivityId.ACTIVITY178_CALL_OF_EQUIP_VALUE;
		//long currentTime = System.currentTimeMillis();

		ActivityTimeCfg activityTimeCfg = ActivityUtil.getCurActivityTimeCfg(activityId);
		Activity178Status status = ActivityUtil.getActivityStatus(player.getPlayerData(), activityId, activityTimeCfg.getStageId(),
				Activity178Status.class);
		
		// 总次数
		int totalTimes = status.getTotalTimes();
		// 上次使用免費的時間
		//long lastFreeTime = status.getLastFreeTime();
		int freeTime = -1;
//		if (!GuaJiTime.isSameDay(lastFreeTime,currentTime)) {
//			freeTime = 1;
//		}
		
		int genLeftTimes = SysBasicCfg.getInstance().getChosenOneGuarant(); // 無使用了
		int singleCost = SysBasicCfg.getInstance().getCallEquipSingleCost();
		int tenCost = SysBasicCfg.getInstance().getCallEquipTenCost();

		int leftLimitTimes = Math.max(genLeftTimes - totalTimes, 0);

		ActivityCallInfo.Builder builder = ActivityCallInfo.newBuilder();
		builder.setLeftTime(activityTimeCfg.calcActivitySurplusTime());
		builder.setFreeTimes(freeTime);
		builder.setOnceCostGold(singleCost);
		builder.setTenCostGold(tenCost);
		builder.setLeftAwardTimes(leftLimitTimes);
		
		return builder;
	}
}
