package com.guaji.game.module.activity.foreverCard;

import org.guaji.app.AppObj;
import org.guaji.net.protocol.Protocol;
import org.guaji.net.protocol.IProtocolHandler;

import com.guaji.game.protocol.Activity2.HPForeverCardRet;
import com.guaji.game.GsApp;
import com.guaji.game.config.ActivityTimeCfg;
import com.guaji.game.config.SysBasicCfg;
import com.guaji.game.item.AwardItems;
import com.guaji.game.log.BehaviorLogger;
import com.guaji.game.log.BehaviorLogger.Action;
import com.guaji.game.log.BehaviorLogger.Params;
import com.guaji.game.manager.ChatManager;
import com.guaji.game.player.Player;
import com.guaji.game.util.ActivityUtil;
import com.guaji.game.util.GsConst;
import com.guaji.game.protocol.Const;
import com.guaji.game.protocol.HP;
import com.guaji.game.protocol.Status;

/**
 * 玩家每日领取终身卡钻石奖励;
 */
public class ForeverCardDailyAwardHandler implements IProtocolHandler {

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
		// 获得玩家终身卡状态
		ForeverCardStatus foreverCardStatus = ActivityUtil.getActivityStatus(player.getPlayerData(), activityId, stageId, ForeverCardStatus.class);
		
		if (foreverCardStatus.isDrawAward()) {
			player.sendError(HP.code.FOREVER_CARD_GET_AWARD_S_VALUE, Status.error.TODAY_ALREADY_GET_FOREVER_CARD_AWARD_VALUE);
			return false;
		}
		// 发放钻石奖励
		AwardItems awards = new AwardItems();
		awards.addGold(SysBasicCfg.getInstance().getDailyGoldAward());
		awards.rewardTakeAffectAndPush(player, Action.DAILY_GOLD_AWARD,2);
		// 改变终身卡状态
		foreverCardStatus.setCardStatus(GsConst.ForeverStatus.OPEN_DRAW);
		player.getPlayerData().updateActivity(activityId, foreverCardCfg.getStageId());
		
		broadCast(player.getName());
		// 日志记录
		BehaviorLogger.log4Platform(player, Action.DAILY_GOLD_AWARD, Params.valueOf("dailyGoldAward", SysBasicCfg.getInstance().getDailyGoldAward()));
		
		// 构建回复协议
		HPForeverCardRet.Builder builder = HPForeverCardRet.newBuilder();
		builder.setCardStatus(GsConst.ForeverStatus.OPEN_DRAW);
		
		player.sendProtocol(Protocol.valueOf(HP.code.FOREVER_CARD_GET_AWARD_S_VALUE, builder));
		return true;
	}
	
	/**
	 * 世界广播
	 * 
	 * @param name
	 */
	private void broadCast(String name) {

		String worldchat = ChatManager.getMsgJson(SysBasicCfg.getInstance().getForeverDailyGoldAwardWorldChat(), name, SysBasicCfg.getInstance().getDailyGoldAward());
		GsApp.getInstance().broadcastChatWorldMsg(null, worldchat);
	}

}
