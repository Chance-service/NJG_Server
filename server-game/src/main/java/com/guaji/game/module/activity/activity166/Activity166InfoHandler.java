package com.guaji.game.module.activity.activity166;

import org.guaji.app.AppObj;
import org.guaji.net.protocol.IProtocolHandler;
import org.guaji.net.protocol.Protocol;

import com.guaji.game.config.ActivityTimeCfg;
import com.guaji.game.config.SysBasicCfg;
import com.guaji.game.player.Player;
import com.guaji.game.protocol.Activity5.Activity166Info;
import com.guaji.game.protocol.Const;
import com.guaji.game.protocol.HP;
import com.guaji.game.protocol.Status;
import com.guaji.game.util.ActivityUtil;

/**
* @author 作者 E-mail:Jeremy@163.com
* @version 创建时间：2019年4月5日 下午9:30:55
* 类说明
*/
public class Activity166InfoHandler implements IProtocolHandler{
	@Override
	public boolean onProtocol(AppObj appObj, Protocol protocol) {
		Player player = (Player) appObj;
		// 检测活动是否开放
		int activityId = Const.ActivityId.ACTIVITY166_CALL_OF_FRIENDSHIP_VALUE;
		ActivityTimeCfg activityTimeCfg = ActivityUtil.getCurActivityTimeCfg(activityId);
		if (activityTimeCfg == null) {
			// 活动已关闭
			player.sendError(protocol.getType(), Status.error.ACTIVITY_CLOSE);
			return true;
		}

		Activity166Info.Builder builder = generateInfo();
		
		player.sendProtocol(Protocol.valueOf(HP.code.ACTIVITY166_CALL_OF_FRIENDSHIP_INFO_S, builder));
		return true;
	}

	public static Activity166Info.Builder generateInfo() {
		
		int singleCost = SysBasicCfg.getInstance().getCallOfFriendshipSingleCost();
		int tenCost = SysBasicCfg.getInstance().getCallOfFriendshipTenCost();

		Activity166Info.Builder builder = Activity166Info.newBuilder();
		builder.setOnceCostPoint(singleCost);
		builder.setTenCostPoint(tenCost);
		
		return builder;
	}
}
