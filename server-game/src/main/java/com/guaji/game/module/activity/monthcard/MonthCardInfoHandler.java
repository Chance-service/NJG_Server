package com.guaji.game.module.activity.monthcard;

import org.guaji.app.AppObj;
import org.guaji.net.protocol.Protocol;
import org.guaji.net.protocol.IProtocolHandler;

import com.guaji.game.player.Player;
import com.guaji.game.util.ActivityUtil;
import com.guaji.game.util.BuilderUtil;
import com.guaji.game.protocol.Const;
import com.guaji.game.protocol.HP;

public class MonthCardInfoHandler implements IProtocolHandler {

	@Override
	public boolean onProtocol(AppObj appObj, Protocol protocol) {

		Player player = (Player) appObj;

		MonthCardStatus monthCardStatus = ActivityUtil.getMonthCardStatus(player.getPlayerData());

		if (monthCardStatus.refresh(player.getPlayerData())) {
			player.getPlayerData().updateActivity(Const.ActivityId.MONTH_CARD_VALUE, 0, true);
		}

		if (monthCardStatus != null) {
			// 过期同步周卡状态
			
			if (monthCardStatus.getLeftDays() <= 0) {
				ActivityUtil.syncWeekMonthStatus(player.getPlayerData());
			}
			
			player.sendProtocol(
					Protocol.valueOf(HP.code.MONTHCARD_INFO_S, BuilderUtil.genMonthCardStatus(monthCardStatus)));
		}
		return true;
	}

}
