package com.guaji.game.module.activity.foreverCard;

import org.guaji.app.AppObj;
import org.guaji.net.protocol.Protocol;
import org.guaji.net.protocol.IProtocolHandler;

import com.guaji.game.protocol.Activity2.HPForeverCardRet;
import com.guaji.game.config.ActivityTimeCfg;
import com.guaji.game.config.SysBasicCfg;
import com.guaji.game.player.Player;
import com.guaji.game.util.ActivityUtil;
import com.guaji.game.protocol.Const;
import com.guaji.game.protocol.HP;
import com.guaji.game.protocol.Status;

/**
 * 玩家进入终身卡活动页面;
 */
public class ForeverCardInfoHandler implements IProtocolHandler {

	@Override
	public boolean onProtocol(AppObj appObj, Protocol protocol) {
		Player player = (Player)appObj;
		ActivityTimeCfg foreverCardCfg = ActivityUtil.getCurActivityTimeCfg(Const.ActivityId.FOREVER_CARD_VALUE);
		if (foreverCardCfg == null) {
			player.sendError(protocol.getType(), Status.error.ACTIVITY_CLOSE);
			return false;
		}
		int activityId = foreverCardCfg.getActivityId();
		int stageId = foreverCardCfg.getStageId();
		// 获取玩家终身卡状态
		ForeverCardStatus foreverCardStatus = ActivityUtil.getActivityStatus(player.getPlayerData(), activityId, stageId, ForeverCardStatus.class);
		int cardStatus = foreverCardStatus.getCardStatus();
		// 构建回复协议
		HPForeverCardRet.Builder builder = HPForeverCardRet.newBuilder();
		builder.setCardStatus(cardStatus);
		
//		if (cardStatus == GsConst.ForeverStatus.UNOPEN_UNABLE) {
			builder.setCanActivateNeedGold(foreverCardStatus.getTodayLastRecharge());
//		} 
		builder.setActivateGold(SysBasicCfg.getInstance().getActivateForeverCardGold());
		
		//还需要消费多少
		builder.setActivateConsumGold(foreverCardStatus.getTodayLastConsume());
		
		player.sendProtocol(Protocol.valueOf(HP.code.FOREVER_CARD_INFO_S_VALUE, builder));
		return true;
	}
	
}
