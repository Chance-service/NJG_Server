package com.guaji.game.module.activity.taxi;

import org.guaji.app.AppObj;
import org.guaji.net.protocol.Protocol;
import org.guaji.net.protocol.IProtocolHandler;

import com.guaji.game.protocol.Activity.HPTaxiCodeInfoRet;
import com.guaji.game.config.ActivityTimeCfg;
import com.guaji.game.player.Player;
import com.guaji.game.util.ActivityUtil;
import com.guaji.game.protocol.Const;
import com.guaji.game.protocol.HP;
import com.guaji.game.protocol.Status;

public class TaxiCodeInfoHandler implements IProtocolHandler {

	@Override
	public boolean onProtocol(AppObj appObj, Protocol protocol) {
		Player player = (Player) appObj;
		// 检测活动是否开放
		int activityId = Const.ActivityId.TAXI_CODE_VALUE;
		ActivityTimeCfg timeCfg = ActivityUtil.getCurActivityTimeCfg(activityId);
		if (timeCfg == null) {
			// 活动已关闭
			player.sendError(protocol.getType(), Status.error.ACTIVITY_CLOSE);
			return true;
		}
		
		TaxiCodeStatus taxiStatus = ActivityUtil.getActivityStatus(player.getPlayerData(), activityId, 
				timeCfg.getStageId(), TaxiCodeStatus.class);
		
		HPTaxiCodeInfoRet.Builder ret = HPTaxiCodeInfoRet.newBuilder();
		ret.setIsExchanged(taxiStatus.isExchange()?1:0);
		if(taxiStatus.isExchange()){
			ret.setTaxiCode(taxiStatus.getMyTaxiCode());
		}
		player.sendProtocol(Protocol.valueOf(HP.code.TAXI_CODE_INFO_S, ret));
		return true;
	}

}
