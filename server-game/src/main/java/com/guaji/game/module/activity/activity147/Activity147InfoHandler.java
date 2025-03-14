package com.guaji.game.module.activity.activity147;

import org.guaji.app.AppObj;
import org.guaji.net.protocol.IProtocolHandler;
import org.guaji.net.protocol.Protocol;

import com.guaji.game.config.ActivityTimeCfg;
import com.guaji.game.config.WishingWellCfg;
import com.guaji.game.player.Player;
import com.guaji.game.protocol.Activity4.WishingRequestInfo;
import com.guaji.game.protocol.Activity4.WishingWellInfo;
import com.guaji.game.protocol.Const;
import com.guaji.game.protocol.HP;
import com.guaji.game.protocol.Status;
import com.guaji.game.util.ActivityUtil;

/**
* @author 作者 E-mail:Jeremy@163.com
* @version 创建时间：2019年4月5日 下午9:30:55
* 类说明
*/
public class Activity147InfoHandler implements IProtocolHandler{
	@Override
	public boolean onProtocol(AppObj appObj, Protocol protocol) {
		Player player = (Player) appObj;
		int action = 0;
		// 检测活动是否开放
		int activityId = Const.ActivityId.ACTIVITY147_WISHING_WELL_VALUE;
		ActivityTimeCfg activityTimeCfg = ActivityUtil.getCurActivityTimeCfg(activityId);
		if (activityTimeCfg == null) {
			// 活动已关闭
			player.sendError(protocol.getType(), Status.error.ACTIVITY_CLOSE);
			return true;
		}
		
		Activity147Status status = ActivityUtil.getActivityStatus(player.getPlayerData(), activityId, activityTimeCfg.getStageId(),
				Activity147Status.class);
		
		if (null == status) {
			player.sendError(protocol.getType(), Status.error.DATA_NOT_FOUND);
			return true;
		}
		
		int kind = protocol.parseProtocol(WishingRequestInfo.getDefaultInstance()).getKind();
		
		if ((kind < WishingWellCfg.Sun)||(kind > WishingWellCfg.Star)) {
			player.sendError(protocol.getType(), Status.error.PARAMS_INVALID_VALUE);
			return true;
		}
		
		// 初始獎項
		if (status.gameEnd(kind)) {
			Activity147WishingManager.initWishing(kind, player, status, activityId, activityTimeCfg.getStageId());
		}

		WishingWellInfo.Builder builder = Activity147WishingManager.generateInfo(action,player,kind,status,activityTimeCfg);
		
		player.sendProtocol(Protocol.valueOf(HP.code.ACTIVITY147_WISHING_INFO_S, builder));
		return true;
	}
}
