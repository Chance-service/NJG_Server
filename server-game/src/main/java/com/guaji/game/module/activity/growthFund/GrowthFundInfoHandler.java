package com.guaji.game.module.activity.growthFund;

import org.guaji.app.AppObj;
import org.guaji.net.protocol.Protocol;
import org.guaji.net.protocol.IProtocolHandler;

import com.guaji.game.protocol.Activity2.HPGetGrowthFundInfoRes;
import com.guaji.game.player.Player;
import com.guaji.game.util.ActivityUtil;
import com.guaji.game.protocol.HP;

/**
 * 获取成长基金信息
 * */
public class GrowthFundInfoHandler implements IProtocolHandler {

	@Override
	public boolean onProtocol(AppObj appObj, Protocol protocol) {
		Player player = (Player) appObj;
		
		GrowthFundStatus status = ActivityUtil.getGrowthFundStatus(player.getPlayerData());
		
		if(status == null){
			return false;
		}
		
		HPGetGrowthFundInfoRes.Builder ret = HPGetGrowthFundInfoRes.newBuilder();
		
		ret.setBought(status.isBought());
		ret.addAllRewardId(status.getRewardIds());
		
		player.sendProtocol(Protocol.valueOf(HP.code.GROWTH_FUND_INFO_S, ret));
		
		return true;
	}

}
