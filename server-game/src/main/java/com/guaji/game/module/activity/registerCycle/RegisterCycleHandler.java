package com.guaji.game.module.activity.registerCycle;

import org.guaji.app.AppObj;
import org.guaji.net.protocol.Protocol;
import org.guaji.net.protocol.IProtocolHandler;
import org.guaji.os.GuaJiTime;

import com.guaji.game.protocol.Activity.HPRegisterCycleRet;
import com.guaji.game.player.Player;
import com.guaji.game.protocol.HP;

/**
 * 注册天数活动
 */
public class RegisterCycleHandler implements IProtocolHandler {

	@Override
	public boolean onProtocol(AppObj appObj, Protocol protocol) {
		Player player = (Player) appObj;
		
		HPRegisterCycleRet.Builder builder =  HPRegisterCycleRet.newBuilder();
		int registerDays = GuaJiTime.calcBetweenDays(player.getPlayerData().getPlayerEntity().getCreateTime(), GuaJiTime.getCalendar().getTime()) + 1;
		builder.setRegisterSpaceDays(registerDays);
		player.sendProtocol(Protocol.valueOf(HP.code.HOLIDAY_TREASURE_S_VALUE,builder));
		return true;
	}

}
