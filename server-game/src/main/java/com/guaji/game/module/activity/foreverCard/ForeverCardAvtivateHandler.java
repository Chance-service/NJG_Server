package com.guaji.game.module.activity.foreverCard;

import org.guaji.app.AppObj;
import org.guaji.net.protocol.Protocol;
import org.guaji.net.protocol.IProtocolHandler;

import com.guaji.game.protocol.Activity2.HPForeverCardRet;
import com.guaji.game.config.ActivityTimeCfg;
import com.guaji.game.config.SysBasicCfg;
import com.guaji.game.item.ConsumeItems;
import com.guaji.game.log.BehaviorLogger;
import com.guaji.game.log.BehaviorLogger.Action;
import com.guaji.game.log.BehaviorLogger.Params;
import com.guaji.game.player.Player;
import com.guaji.game.util.ActivityUtil;
import com.guaji.game.util.GsConst;
import com.guaji.game.protocol.Const;
import com.guaji.game.protocol.Const.changeType;
import com.guaji.game.protocol.HP;
import com.guaji.game.protocol.Status;

/**
 * 玩家点击激活终身卡;
 */
public class ForeverCardAvtivateHandler implements IProtocolHandler {

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
		// 获得终身卡状态
		ForeverCardStatus foreverCardStatus = ActivityUtil.getActivityStatus(player.getPlayerData(), activityId, stageId, ForeverCardStatus.class);
		
		if (!foreverCardStatus.canActivate()) {
			player.sendError(HP.code.FOREVER_CARD_ACTIVATE_S_VALUE, Status.error.CAN_NOT_ACTIVATE_FOREVER_CARD_VALUE);
			return false;
		}
		
		int payGold = SysBasicCfg.getInstance().getActivateForeverCardGold();
		if (player.getGold() < payGold) {
			// 钻石不足
			player.sendError(protocol.getType(), Status.error.GOLD_NOT_ENOUGH_VALUE);
			return false;
		}
		// 扣除钻石 
		player.consumeGold(payGold, Action.ACTIVATE_FOREVER_CARD);
		ConsumeItems.valueOf(changeType.CHANGE_GOLD, payGold).pushChange(player);
		// 同步玩家终身卡状态
		foreverCardStatus.setCardStatus(GsConst.ForeverStatus.OPEN_UNDRAW);
		player.getPlayerData().updateActivity(activityId, foreverCardCfg.getStageId());
		// 构造回复协议
		HPForeverCardRet.Builder builder = HPForeverCardRet.newBuilder();
		builder.setCardStatus(foreverCardStatus.getCardStatus());
		player.sendProtocol(Protocol.valueOf(HP.code.FOREVER_CARD_ACTIVATE_S_VALUE, builder));
		
		// 日志记录
		BehaviorLogger.log4Platform(player, Action.ACTIVATE_FOREVER_CARD, Params.valueOf("payGold", payGold));
		return true;
	}

}
