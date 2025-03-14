package com.guaji.game.module.activity.vipwelfare;

import org.guaji.app.AppObj;
import org.guaji.net.protocol.Protocol;
import org.guaji.net.protocol.IProtocolHandler;

import com.guaji.game.protocol.Activity.HPVipWelfareInfoRet;
import com.guaji.game.player.Player;
import com.guaji.game.util.ActivityUtil;
import com.guaji.game.protocol.Const;
import com.guaji.game.protocol.HP;


public class VipWelfareInfoHandler implements IProtocolHandler{

	@Override
	public boolean onProtocol(AppObj appObj, Protocol protocol) {
		Player player = (Player) appObj;
		VipWelfareStatus status = ActivityUtil.getActivityStatus(player.getPlayerData(), Const.ActivityId.VIP_WELFARE_VALUE, 0, VipWelfareStatus.class);
		HPVipWelfareInfoRet.Builder builder = HPVipWelfareInfoRet.newBuilder();
		builder.setAwardStatus(status.getAwareStatus());
		player.sendProtocol(Protocol.valueOf(HP.code.VIP_WELFARE_INFO_S, builder));
		return true;
	}

}
