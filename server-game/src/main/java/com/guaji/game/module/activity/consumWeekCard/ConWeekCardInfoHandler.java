package com.guaji.game.module.activity.consumWeekCard;

import org.guaji.app.AppObj;
import org.guaji.net.protocol.IProtocolHandler;
import org.guaji.net.protocol.Protocol;

import com.guaji.game.config.ActivityTimeCfg;
import com.guaji.game.module.activity.weekCard.WeekCardStatus;
import com.guaji.game.player.Player;
import com.guaji.game.protocol.Const;
import com.guaji.game.protocol.HP;
import com.guaji.game.protocol.Status;
import com.guaji.game.protocol.Activity.HPWeekCardInfoRet;
import com.guaji.game.protocol.Activity4.ConsumeWeekCardInfoRet;
import com.guaji.game.util.ActivityUtil;
import com.guaji.game.util.BuilderUtil;

public class ConWeekCardInfoHandler implements IProtocolHandler{

	@Override
	public boolean onProtocol(AppObj appObj, Protocol protocol) {
		
		Player player = (Player) appObj;
		int activityId = Const.ActivityId.CONSUME_WEEK_CARD_VALUE;
		ActivityTimeCfg activityTimeCfg = ActivityUtil.getCurActivityTimeCfg(activityId);
		if (activityTimeCfg == null) {
			player.sendError(protocol.getType(), Status.error.ACTIVITY_CLOSE);
			return true;
		}

		ConWeekCardStatus weekCardStatus = ActivityUtil.getActivityStatus(player.getPlayerData(),
				Const.ActivityId.CONSUME_WEEK_CARD_VALUE, activityTimeCfg.getStageId(), ConWeekCardStatus.class);
		
		
		ConsumeWeekCardInfoRet.Builder ret = BuilderUtil.genConsumeWeekCardInfo(weekCardStatus, activityTimeCfg);
		player.sendProtocol(Protocol.valueOf(HP.code.CONSUME_WEEK_CARD_INFO_S_VALUE, ret));
		return true;
	}

	
}
