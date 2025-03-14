package com.guaji.game.module.activity.weekCard;

import org.guaji.app.AppObj;
import org.guaji.net.protocol.Protocol;
import org.guaji.net.protocol.IProtocolHandler;

import com.guaji.game.protocol.Activity.HPWeekCardInfoRet;
import com.guaji.game.config.ActivityTimeCfg;
import com.guaji.game.player.Player;
import com.guaji.game.util.ActivityUtil;
import com.guaji.game.util.BuilderUtil;
import com.guaji.game.protocol.Const;
import com.guaji.game.protocol.HP;
import com.guaji.game.protocol.Status;

public class WeekCardInfoHandler implements IProtocolHandler {

	@Override
	public boolean onProtocol(AppObj appObj, Protocol protocol) {
		Player player = (Player) appObj;
		int activityId = Const.ActivityId.WEEK_CARD_VALUE;
		ActivityTimeCfg activityTimeCfg = ActivityUtil.getCurActivityTimeCfg(activityId);
		if (activityTimeCfg == null) {
			player.sendError(protocol.getType(), Status.error.ACTIVITY_CLOSE);
			return true;
		}

		WeekCardStatus weekCardStatus = ActivityUtil.getActivityStatus(player.getPlayerData(),
				Const.ActivityId.WEEK_CARD_VALUE, activityTimeCfg.getStageId(), WeekCardStatus.class);
		
		//过期同步周卡状态
	
		if (weekCardStatus.getLeftDays() <= 0)
			ActivityUtil.syncWeekMonthStatus(player.getPlayerData());
		

		HPWeekCardInfoRet.Builder ret = BuilderUtil.genWeekCardInfo(weekCardStatus, activityTimeCfg);
		player.sendProtocol(Protocol.valueOf(HP.code.WEEK_CARD_INFO_S_VALUE, ret));
		return true;
	}

}
