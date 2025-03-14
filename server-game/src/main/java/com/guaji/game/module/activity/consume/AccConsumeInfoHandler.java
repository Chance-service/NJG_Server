package com.guaji.game.module.activity.consume;

import org.guaji.app.AppObj;
import org.guaji.net.protocol.Protocol;
import org.guaji.net.protocol.IProtocolHandler;

import com.guaji.game.protocol.Activity.HPAccConsumeInfoRet;
import com.guaji.game.config.ActivityTimeCfg;
import com.guaji.game.player.Player;
import com.guaji.game.util.ActivityUtil;
import com.guaji.game.protocol.Const;
import com.guaji.game.protocol.HP;
import com.guaji.game.protocol.Status;

public class AccConsumeInfoHandler implements IProtocolHandler {

	@Override
	public boolean onProtocol(AppObj appObj, Protocol protocol) {
		Player player = (Player) appObj;
		// 检测活动是否开放
		int activityId = Const.ActivityId.ACCUMULATIVE_CONSUME_VALUE;
		ActivityTimeCfg timeCfg = ActivityUtil.getCurActivityTimeCfg(activityId);
		if(timeCfg == null){
			// 活动已关闭
			player.sendError(protocol.getType(), Status.error.ACTIVITY_CLOSE);
			return true;
		}
		
		AccConsumeStatus accConsumeStatues = ActivityUtil.getActivityStatus(player.getPlayerData(), 
				activityId, timeCfg.getStageId(), AccConsumeStatus.class);
		HPAccConsumeInfoRet.Builder ret = HPAccConsumeInfoRet.newBuilder();
		ret.setAccConsumeGold(accConsumeStatues.getAccConsumeGold());
		ret.addAllGotAwardCfgId(accConsumeStatues.getGotAwardCfgIds());
		ret.setSurplusTime(timeCfg.calcActivitySurplusTime());
		player.sendProtocol(Protocol.valueOf(HP.code.ACC_CONSUME_INFO_S, ret));
		return true;
	}

}
