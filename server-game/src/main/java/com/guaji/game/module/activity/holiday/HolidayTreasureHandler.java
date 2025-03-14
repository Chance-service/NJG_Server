package com.guaji.game.module.activity.holiday;

import org.guaji.app.AppObj;
import org.guaji.net.protocol.Protocol;
import org.guaji.net.protocol.IProtocolHandler;

import com.guaji.game.protocol.Activity.HPHolidayTreasureRet;
import com.guaji.game.config.ActivityTimeCfg;
import com.guaji.game.player.Player;
import com.guaji.game.util.ActivityUtil;
import com.guaji.game.protocol.Const;
import com.guaji.game.protocol.HP;
import com.guaji.game.protocol.Status;

/**
 * 假日密宝
 */
public class HolidayTreasureHandler implements IProtocolHandler {

	@Override
	public boolean onProtocol(AppObj appObj, Protocol protocol) {
		Player player = (Player) appObj;
		
		// 假日密宝
		int activityId = Const.ActivityId.HOLIDAY_TREASURE_VALUE;
		ActivityTimeCfg timeCfg = ActivityUtil.getCurActivityTimeCfg(activityId);
		if(timeCfg == null || timeCfg.isEnd()){
			player.sendError(HP.code.HOLIDAY_TREASURE_C_VALUE, Status.error.ACTIVITY_CLOSE);
			return true;
		}
		
		HPHolidayTreasureRet.Builder holidayTreasureBuilder =  HPHolidayTreasureRet.newBuilder();
		holidayTreasureBuilder.setLeftTimes(timeCfg.calcActivitySurplusTime());
		
		player.sendProtocol(Protocol.valueOf(HP.code.HOLIDAY_TREASURE_S_VALUE,holidayTreasureBuilder));
		return true;
	}

}
