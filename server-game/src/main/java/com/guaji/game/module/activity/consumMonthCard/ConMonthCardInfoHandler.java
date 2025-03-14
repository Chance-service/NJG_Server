package com.guaji.game.module.activity.consumMonthCard;

import org.guaji.app.AppObj;
import org.guaji.net.protocol.IProtocolHandler;
import org.guaji.net.protocol.Protocol;

import com.guaji.game.module.activity.monthcard.MonthCardStatus;
import com.guaji.game.player.Player;
import com.guaji.game.protocol.Const;
import com.guaji.game.protocol.HP;
import com.guaji.game.util.ActivityUtil;
import com.guaji.game.util.BuilderUtil;

public class ConMonthCardInfoHandler implements IProtocolHandler{

	@Override
	public boolean onProtocol(AppObj appObj, Protocol protocol) {
		Player player = (Player) appObj;

		ConMonthCardStatus monthCardStatus = ActivityUtil.getConMonthCardStatus(player.getPlayerData());

		if (monthCardStatus.refresh(player.getPlayerData())) {
			player.getPlayerData().updateActivity(Const.ActivityId.CONSUME_MONTH_CARD_VALUE, 0, true);
		}

		if (monthCardStatus != null) {
			// 过期同步周卡状态
		
			
			player.sendProtocol(
					Protocol.valueOf(HP.code.CONSUME_MONTHCARD_INFO_S_VALUE, BuilderUtil.genConMonthCardStatus(monthCardStatus)));
		}
		
		return true;
	}

	
}
