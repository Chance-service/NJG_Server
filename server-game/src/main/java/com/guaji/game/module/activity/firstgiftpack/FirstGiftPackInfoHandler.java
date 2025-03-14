package com.guaji.game.module.activity.firstgiftpack;

import org.guaji.app.AppObj;
import org.guaji.net.protocol.Protocol;
import org.guaji.net.protocol.IProtocolHandler;

import com.guaji.game.protocol.Activity.HPFirstRechargeGiftInfo;
import com.guaji.game.player.Player;
import com.guaji.game.util.ActivityUtil;
import com.guaji.game.protocol.Const;
import com.guaji.game.protocol.HP;

public class FirstGiftPackInfoHandler implements IProtocolHandler {

	@Override
	public boolean onProtocol(AppObj appObj, Protocol protocol) {
		Player player = (Player) appObj;
		//获取是否有充值
		boolean isFirstRecharge = player.getPlayerData().getPlayerEntity().getPayMoney() == 0 ? true : false;
		FirstGiftPackStatus status = ActivityUtil.getActivityStatus(player.getPlayerData(), Const.ActivityId.FIRST_GIFTPACK_VALUE, 0, FirstGiftPackStatus.class);
		if(status.getLastAwareTime() == null )
		{
			status.setGiftStatus(FirstGiftPackAwardHandler.NOTGET);
			
			if(isFirstRecharge == true)
			{//没有过充值
				status.setIsFirstPay(FirstGiftPackAwardHandler.NOFIRSTPAY);
			}
			else
			{
				status.setIsFirstPay(FirstGiftPackAwardHandler.FIRSTPAY);
			}
		}
		else 
		{
			status.setGiftStatus(FirstGiftPackAwardHandler.GOTED);
		}
		player.getPlayerData().updateActivity(Const.ActivityId.FIRST_GIFTPACK_VALUE, 0,true);
		//获取首充礼包的实体信息并设置新的领取状态
		HPFirstRechargeGiftInfo.Builder builder = HPFirstRechargeGiftInfo.newBuilder();
		builder.setGiftStatus(status.getGiftStatus());
		builder.setIsFirstPay(status.getIsFirstPay());		
		//发送服务器礼包协议
		player.sendProtocol(Protocol.valueOf(HP.code.FIRST_GIFTPACK_INFO_S, builder));
		
		return true;
	}
	
}