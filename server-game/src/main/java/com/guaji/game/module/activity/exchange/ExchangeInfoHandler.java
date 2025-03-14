package com.guaji.game.module.activity.exchange;

import org.guaji.app.AppObj;
import org.guaji.net.protocol.Protocol;
import org.guaji.net.protocol.IProtocolHandler;

import com.guaji.game.config.ActivityTimeCfg;
import com.guaji.game.player.Player;
import com.guaji.game.util.ActivityUtil;
import com.guaji.game.util.BuilderUtil;
import com.guaji.game.protocol.Const;
import com.guaji.game.protocol.HP;
import com.guaji.game.protocol.Status;

/**
 * 限时兑换 for 77
 */
public class ExchangeInfoHandler implements IProtocolHandler 
{
	@Override
	public boolean onProtocol(AppObj appObj, Protocol protocol) 
	{
		Player player = (Player) appObj;
		// 检测活动是否开放
		int activityId = Const.ActivityId.EXCHANGE_DOUBLE_SEVEN_VALUE;
		ActivityTimeCfg timeCfg = ActivityUtil.getCurActivityTimeCfg(activityId);
		if(timeCfg == null){
			// 活动已关闭
			player.sendError(protocol.getType(), Status.error.ACTIVITY_CLOSE);
			return true;
		}
		
		ExchangeStatus exchangeStatus = ActivityUtil.getActivityStatus(
				player.getPlayerData(), activityId, timeCfg.getStageId(), ExchangeStatus.class);
		
		if(exchangeStatus == null) {
			player.sendError(protocol.getType(), Status.error.DATA_NOT_FOUND_VALUE);
			return true;
		}
		
		int lastTime = timeCfg.calcActivitySurplusTime();

		//发送服务器礼包协议
		player.sendProtocol(Protocol.valueOf(HP.code.EXCHANGE_INFO_S_VALUE, BuilderUtil.genExchangeStatus(lastTime, exchangeStatus)));
		
		return true;
	}

}