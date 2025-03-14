package com.guaji.game.module.activity.growthFund;

import org.guaji.app.AppObj;
import org.guaji.net.protocol.Protocol;
import org.guaji.net.protocol.IProtocolHandler;

import com.guaji.game.protocol.Activity2.HPBuyGrowthFundSuccRes;
import com.guaji.game.config.SysBasicCfg;
import com.guaji.game.item.ConsumeItems;
import com.guaji.game.log.BehaviorLogger.Action;
import com.guaji.game.player.Player;
import com.guaji.game.util.ActivityUtil;
import com.guaji.game.protocol.Const;
import com.guaji.game.protocol.Const.changeType;
import com.guaji.game.protocol.HP;
import com.guaji.game.protocol.Status;

/**
 * 购买成长基金
 * */
public class GrowthFundBuyHandler implements IProtocolHandler {

	@Override
	public boolean onProtocol(AppObj appObj, Protocol protocol) {
		Player player = (Player) appObj;
		
		GrowthFundStatus status = ActivityUtil.getGrowthFundStatus(player.getPlayerData());
		
		//已经购买过
		if(status.isBought()){
			return false;
		}
		
		//VIP等级不足
		if (player.getVipLevel() < SysBasicCfg.getInstance().getGrowthVipLevel()) {
			player.sendError(protocol.getType(), Status.error.VIP_NOT_ENOUGH_VALUE);
			return false;
		}

		//钻石不足
		if(player.getGold() < SysBasicCfg.getInstance().getGrowthNeedGold()){
			player.sendError(protocol.getType(), Status.error.GOLD_NOT_ENOUGH_VALUE);
			return false;
		}
		
		//减钻石
		player.consumeGold(SysBasicCfg.getInstance().getGrowthNeedGold(), Action.BUY_GROWTH_FUND);
		ConsumeItems.valueOf(changeType.CHANGE_GOLD, SysBasicCfg.getInstance().getGrowthNeedGold()).pushChange(player);
		
		//购买成功
		status.setBought(true);
		
		//更新DB
		player.getPlayerData().updateActivity(Const.ActivityId.GROWTH_FUND_VALUE, 0,true);
		
		HPBuyGrowthFundSuccRes.Builder ret = HPBuyGrowthFundSuccRes.newBuilder();
		ret.setSucc(0);
		player.sendProtocol(Protocol.valueOf(HP.code.GROWTH_FUND_BUY_S, ret));
		
		return true;
	}

}
