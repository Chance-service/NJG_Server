package com.guaji.game.module.activity.vipPackage;


import org.guaji.app.AppObj;
import org.guaji.net.protocol.Protocol;
import org.guaji.net.protocol.IProtocolHandler;

import com.guaji.game.player.Player;
import com.guaji.game.util.ActivityUtil;
import com.guaji.game.util.BuilderUtil;
import com.guaji.game.protocol.HP;

public class VipPackageInfoHandler implements IProtocolHandler{
	
	@Override
	public boolean onProtocol(AppObj appObj, Protocol protocol) 
	{
		Player player = (Player) appObj;
		
		VipPackageStatus vipPackageStatus = ActivityUtil.getVipPackageStatus(player.getPlayerData());

		if(vipPackageStatus == null) 
		{
			return true;
		}
		
		//发送服务器礼包协议
		player.sendProtocol(Protocol.valueOf(HP.code.VIP_PACKETAGE_INFO_S, BuilderUtil.genVipPackageStatus(vipPackageStatus)));
		return true;
	}

}
