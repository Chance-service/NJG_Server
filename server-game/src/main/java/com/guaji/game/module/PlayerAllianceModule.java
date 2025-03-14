package com.guaji.game.module;


import org.guaji.annotation.MessageHandlerAnno;
import org.guaji.msg.Msg;
import org.guaji.net.protocol.Protocol;

import com.guaji.game.protocol.Const.QuestEventType;
import com.guaji.game.config.FunctionUnlockCfg;
import com.guaji.game.entity.PlayerAllianceEntity;
import com.guaji.game.manager.ChatManager;
import com.guaji.game.module.alliance.AllianceAutoFightChangeHandler;
import com.guaji.game.module.alliance.AllianceBossFunOpenHandler;
import com.guaji.game.module.alliance.AllianceChangeNameHandler;
import com.guaji.game.module.alliance.AllianceChangePosHandler;
import com.guaji.game.module.alliance.AllianceCreateHandler;
import com.guaji.game.module.alliance.AllianceEnterHandler;
import com.guaji.game.module.alliance.AllianceFindHandler;
import com.guaji.game.module.alliance.AllianceHarmSortHandler;
import com.guaji.game.module.alliance.AllianceJoinListHandler;
import com.guaji.game.module.alliance.AllianceJoinSetHandler;
import com.guaji.game.module.alliance.AllianceMailHandler;
import com.guaji.game.module.alliance.AllianceMemberHandler;
import com.guaji.game.module.alliance.AllianceNoticeHandler;
import com.guaji.game.module.alliance.AllianceRankingHandler;
import com.guaji.game.module.alliance.AllianceReportHandler;
import com.guaji.game.module.alliance.AllianceSetBossHandler;
import com.guaji.game.module.alliance.AllianceShopBuyHandler;
import com.guaji.game.module.alliance.AllianceShopHandler;
import com.guaji.game.module.alliance.AllianceShopRefreshHandler;
import com.guaji.game.module.alliance.AllianceTotalScoreHandler;
import com.guaji.game.module.alliance.ApplyIntoAllianceHandler;
import com.guaji.game.module.alliance.ApprovalRefusedOperHandler;
import com.guaji.game.module.quest.QuestEventBus;
import com.guaji.game.player.Player;
import com.guaji.game.player.PlayerModule;
import com.guaji.game.util.GsConst;
import com.guaji.game.protocol.Const;
import com.guaji.game.protocol.HP;
import com.guaji.game.protocol.Status;

/**
 * 公会模块
 */
public class PlayerAllianceModule extends PlayerModule {
	/**
	 * 构造函数
	 * 
	 * @param player
	 */
	public PlayerAllianceModule(Player player) {
		super(player);

		listenProto(HP.code.ALLIANCE_CREATE_C, new AllianceCreateHandler());
		listenProto(HP.code.ALLIANCE_OPER_C, new AllianceChangePosHandler());
		listenProto(HP.code.ALLIANCE_MEMBER_C, new AllianceMemberHandler());
		listenProto(HP.code.ALLIANCE_RANKING_C, new AllianceRankingHandler());
		listenProto(HP.code.ALLIANCE_HARMSORT_C, new AllianceHarmSortHandler());
		listenProto(HP.code.ALLIANCE_JOINSET_C, new AllianceJoinSetHandler());
		listenProto(HP.code.ALLIANCE_NOTICE_C, new AllianceNoticeHandler());
		listenProto(HP.code.ALLIANCE_BOSSFUNOPEN_C, new AllianceBossFunOpenHandler());
		listenProto(HP.code.ALLIANCE_REPORT_C, new AllianceReportHandler());
		listenProto(HP.code.ALLIANCE_SHOP_C, new AllianceShopHandler());
		listenProto(HP.code.ALLIANCE_SHOP_BUY_C, new AllianceShopBuyHandler());
		listenProto(HP.code.ALLIANCE_SHOP_REFRESH_C, new AllianceShopRefreshHandler());
		listenProto(HP.code.ALLIANCE_JOIN_LIST_C, new AllianceJoinListHandler());
		listenProto(HP.code.ALLIANCE_ENTER_C, new AllianceEnterHandler());
		listenProto(HP.code.ALLIANCE_FIND_C, new AllianceFindHandler());
		listenProto(HP.code.ALLIANCE_AUTO_FIGHT_C, new AllianceAutoFightChangeHandler());
		listenProto(HP.code.ALLIANCE_MAIL_C, new AllianceMailHandler());
		listenProto(HP.code.APPLY_INTO_ALLIANCE_C, new ApplyIntoAllianceHandler());
		listenProto(HP.code.APPROVAL_REFUSED_OPER_C, new ApprovalRefusedOperHandler());
		listenProto(HP.code.ALLIANCE_SET_OPEN_BOSS_C, new AllianceSetBossHandler());
		listenProto(HP.code.ALLIANCE_CHANGE_NAME_C_VALUE, new AllianceChangeNameHandler());
		listenProto(HP.code.ALLIANCE_SCORE_RANK_C_VALUE, new AllianceTotalScoreHandler());
	}

	/**
	 * 玩家上线处理
	 * 
	 * @return
	 */
	protected boolean onPlayerLogin() {
		// 加载公会数据
		PlayerAllianceEntity allianceEntity = player.getPlayerData().loadPlayerAlliance();

		if (allianceEntity.getAllianceId() != 0) {
			ChatManager.getInstance().addAllianceSession(player.getSession(), allianceEntity.getAllianceId(), player.getId(),true);
		}
		// 处理离线玩家加入公会的未完成成就任务的bug
		if (player.getPlayerData().getPlayerAllianceEntity() != null && player.getPlayerData().getPlayerAllianceEntity().getAllianceId() > 0) {
			// 推送加入公会任务
			QuestEventBus.fireQuestEventOneTime(QuestEventType.JOIN_ALLIANCE, player.getXid());
		}
		return true;
	}

	/**
	 * 响应新任务;
	 * 
	 * @param msg
	 */
	@MessageHandlerAnno(code = GsConst.MsgType.NEW_QUEST_EVENT)
	private void onNewQuestEvent(Msg msg) {
		QuestEventType eventType = msg.getParam(0);
		// 加入公会
		PlayerAllianceEntity allianceEntity = player.getPlayerData().loadPlayerAlliance();
		if (eventType == QuestEventType.JOIN_ALLIANCE) {
			// 推送加入公会任务
			if (allianceEntity.getAllianceId() != 0) {
				QuestEventBus.fireQuestEventOneTime(QuestEventType.JOIN_ALLIANCE, player.getXid());
			}
		}
	}

	protected boolean onPlayerLogout() {
		ChatManager.getInstance().removeAllaiceSession(player.getPlayerData().getPlayerAllianceEntity().getAllianceId(), player.getId());
		return true;
	}

	/**
	 * 更新
	 * 
	 * @return
	 */
	@Override
	public boolean onTick() {
		return super.onTick();
	}

	/**
	 * 消息响应
	 * 
	 * @param msg
	 * @return
	 */
	@Override
	public boolean onMessage(Msg msg) {
		return true;
	}

	/**
	 * 协议响应
	 * 
	 * @param protocol
	 * @return
	 */
	@Override
	public boolean onProtocol(Protocol protocol) {
		if (isListenProto(protocol.getType())) {
			if (!FunctionUnlockCfg.checkUnlock(player,null,Const.FunctionType.guild_Unlock)){
				sendError(protocol.getType(), Status.error.CONDITION_NOT_ENOUGH);
				return true;
			}
		}
		return super.onProtocol(protocol);
	}
}
