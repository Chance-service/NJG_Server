package com.guaji.game.module;

import org.guaji.annotation.MessageHandlerAnno;
import org.guaji.annotation.ProtocolHandlerAnno;
import org.guaji.config.ConfigManager;
import org.guaji.msg.Msg;
import org.guaji.net.client.ClientSession;
import org.guaji.net.protocol.Protocol;
import org.guaji.os.GuaJiTime;

import com.guaji.game.GsApp;
import com.guaji.game.config.CrossBattleCfg;
import com.guaji.game.config.VipPrivilegeCfg;
import com.guaji.game.entity.CrossPlayerEntity;
import com.guaji.game.item.ConsumeItems;
import com.guaji.game.log.BehaviorLogger;
import com.guaji.game.log.BehaviorLogger.Action;
import com.guaji.game.log.BehaviorLogger.Params;
import com.guaji.game.log.BehaviorLogger.Source;
import com.guaji.game.manager.SnapShotManager;
import com.guaji.game.manager.crossbattle.CrossBattleManager;
import com.guaji.game.manager.crossbattle.CrossBattleService;
import com.guaji.game.manager.crossserver.CrossServerManager;
import com.guaji.game.player.Player;
import com.guaji.game.player.PlayerModule;
import com.guaji.game.util.GsConst;
import com.guaji.game.util.PlayerUtil;
import com.guaji.game.protocol.Const;
import com.guaji.game.protocol.HP;
import com.guaji.game.protocol.Snapshot.PlayerSnapshotInfo;
import com.guaji.game.protocol.Status;
import com.guaji.game.protocol.CsBattle.BattleRequest;
import com.guaji.game.protocol.CsBattle.BuyBattleTimesRequest;
import com.guaji.game.protocol.CsBattle.BuyBattleTimesResponse;
import com.guaji.game.protocol.CsBattle.ChallengeResponse;
import com.guaji.game.protocol.CsBattle.EnterState;
import com.guaji.game.protocol.CsBattle.OSMainInfoResponse;
import com.guaji.game.protocol.CsBattle.PlayerBean;
import com.guaji.game.protocol.CsBattle.PlayerIdentify;
import com.guaji.game.protocol.CsBattle.PlayerInfoSyncResponse;
import com.guaji.game.protocol.CsBattle.PlayerRank;
import com.guaji.game.protocol.CsBattle.PlayerRankRequest;
import com.guaji.game.protocol.CsBattle.PlayerRankResponse;
import com.guaji.game.protocol.CsBattle.PlayerSignup;
import com.guaji.game.protocol.CsBattle.PlayerSnapshotRequest;
import com.guaji.game.protocol.CsBattle.PlayerSnapshotResponse;
import com.guaji.game.protocol.CsBattle.PlayerVsRequest;
import com.guaji.game.protocol.CsBattle.RefreshVsResponse;
import com.guaji.game.protocol.CsBattle.StateChange;

/**
 * 跨服竞技协议处理
 */
public class PlayerCrossModule extends PlayerModule{
	
	public PlayerCrossModule(Player player) {
		super(player);
	}
	
	@Override
	public boolean onTick() {
		return super.onTick();
	}
	
	@Override
	protected boolean onPlayerLogin() {
		// 玩家登陆处理
		CrossBattleService.getInstance().loadFromDB(player.getId());
		return true;
	}
	
	@Override
	protected boolean onPlayerLogout() {
		return true;
	}

	/**
	 * 推送GVG状态
	 */
	@MessageHandlerAnno(code = GsConst.MsgType.CROSS_SATATE)
	private boolean onRankChange(Msg msg) {
		if(player.getLevel() >= CrossBattleCfg.getInstance().getOpenLevel()){
			// 取数据推送一下
			EnterState state = CrossBattleService.getInstance().isCanEnter(player.getId());
			StateChange.Builder response = StateChange.newBuilder();
			response.setState(state);
			sendProtocol(Protocol.valueOf(HP.code.PUSH_CROSS_STATE_S_VALUE, response));
		}
		return true;
	}
	
	/**
	 * 进入页面的请求信息
	 * 
	 * @param hawkProtocol
	 */
	@ProtocolHandlerAnno(code = HP.code.OSPVP_VS_INFO_C_VALUE)
	private void showMainLogic(Protocol hawkProtocol) {
		ClientSession clientSession = CrossServerManager.getInstance().getCSSession();
		if (clientSession == null || !clientSession.isActive()) {
			sendError(hawkProtocol.getType(), Status.error.CROSS_SERVER_CONNECT_FAIL);
			return;
		}
		// 跨服竞技状态判断
		EnterState state = CrossBattleService.getInstance().isCanEnter(player.getId());
		if (state != EnterState.NORMAL) {
			OSMainInfoResponse.Builder builder = OSMainInfoResponse.newBuilder();
			builder.setEnterState(state);
			PlayerBean.Builder playerBean = CrossBattleManager.getInstance().createPlayerBean(player);
			builder.setSelfInfo(playerBean);
			player.sendProtocol(Protocol.valueOf(HP.code.OSPVP_VS_INFO_S_VALUE, builder));
			return;
		}
		// 发送跨服数据
		PlayerIdentify.Builder builder = PlayerIdentify.newBuilder();
		builder.setIdentify(PlayerUtil.getPlayerIdentify(player.getId()));
		clientSession.sendProtocol(Protocol.valueOf(HP.code.PVP_VS_INFO_VALUE, builder));
	}
	
	/**
	 * 跨服PVP手动同步数据
	 * 
	 * @param hawkProtocol
	 */
	@ProtocolHandlerAnno(code = HP.code.OSPVP_SYNC_PLAYER_C_VALUE)
	private void syncDataLogic(Protocol hawkProtocol) {
		ClientSession clientSession = CrossServerManager.getInstance().getCSSession();
		if (clientSession == null || !clientSession.isActive()) {
			sendError(hawkProtocol.getType(), Status.error.CROSS_SERVER_CONNECT_FAIL);
			return;
		}
		// 跨服竞技状态判断
		EnterState state = CrossBattleService.getInstance().isCanEnter(player.getId());
		if (EnterState.NORMAL != state) {
			PlayerInfoSyncResponse.Builder builder = PlayerInfoSyncResponse.newBuilder();
			builder.setEnterState(state);
			player.sendProtocol(Protocol.valueOf(HP.code.OSPVP_SYNC_PLAYER_S_VALUE, builder));
			return;
		}
		// 刷新时间判断
		CrossPlayerEntity entity = CrossBattleService.getInstance().getCrossPlayer(player.getId());
		if (entity == null || GuaJiTime.getMillisecond() - entity.getSynchroTime() < 60000) {
			sendError(hawkProtocol.getType(), Status.error.SYNC_TIME_LIMIT);
			return;
		}
		entity.updateSynchroTime();
		PlayerSnapshotInfo.Builder snapshot = SnapShotManager.getInstance().getPlayerSnapShot(entity.getPlayerId());
		if (snapshot == null) {
			return;
		}
		// 发送跨服数据
		PlayerSignup.Builder builder = PlayerSignup.newBuilder();
		builder.setIdentify(PlayerUtil.getPlayerIdentify(player.getId()));
		builder.setServerName(GsApp.getInstance().getServerIdentify());
		builder.setSnapshot(snapshot);
		clientSession.sendProtocol(Protocol.valueOf(HP.code.SYNC_PLAYER_DATA_VALUE, builder));
	}
	
	/**
	 * 刷新对战数据
	 * 
	 * @param hawkProtocol
	 */
	@ProtocolHandlerAnno(code = HP.code.OSPVP_REFRESH_VS_INFO_C_VALUE)
	private void refreshVsLogic(Protocol hawkProtocol) {
		ClientSession clientSession = CrossServerManager.getInstance().getCSSession();
		if (clientSession == null || !clientSession.isActive()) {
			sendError(hawkProtocol.getType(), Status.error.CROSS_SERVER_CONNECT_FAIL);
			return;
		}
		// 跨服竞技状态判断
		EnterState state = CrossBattleService.getInstance().isCanEnter(player.getId());
		if (EnterState.NORMAL != state) {
			RefreshVsResponse.Builder builder = RefreshVsResponse.newBuilder();
			builder.setEnterState(state);
			player.sendProtocol(Protocol.valueOf(HP.code.OSPVP_REFRESH_VS_INFO_S_VALUE, builder));
			return;
		}
		// 发送跨服数据
		PlayerIdentify.Builder builder = PlayerIdentify.newBuilder();
		builder.setIdentify(PlayerUtil.getPlayerIdentify(player.getId()));
		clientSession.sendProtocol(Protocol.valueOf(HP.code.REFRESH_VS_INFO_VALUE, builder));
	}
	
	/**
	 * 战斗请求信息
	 * 
	 * @param hawkProtocol
	 */
	@ProtocolHandlerAnno(code = HP.code.OSPVP_VS_C_VALUE)
	private void fightLogic(Protocol hawkProtocol) {
		ClientSession session = CrossServerManager.getInstance().getCSSession();
		if (session == null || !session.isActive()) {
			sendError(hawkProtocol.getType(), Status.error.CROSS_SERVER_CONNECT_FAIL);
			return;
		}
		// 跨服竞技状态判断
		EnterState state = CrossBattleService.getInstance().isCanEnter(player.getId());
		if (EnterState.NORMAL != state) {
			ChallengeResponse.Builder builder = ChallengeResponse.newBuilder();
			builder.setEnterState(state);
			player.sendProtocol(Protocol.valueOf(HP.code.OSPVP_VS_CHALLENGE_S_VALUE, builder));
			return;
		}
		// 挑战次数判断
		boolean isHaveTimes = CrossBattleService.getInstance().battleTimes(player);
		if (isHaveTimes) {
			// 发送跨服数据
			PlayerVsRequest.Builder builder = hawkProtocol.parseProtocol(PlayerVsRequest.getDefaultInstance()).toBuilder();
			builder.setSourceId(PlayerUtil.getPlayerIdentify(player.getId()));
			session.sendProtocol(Protocol.valueOf(HP.code.PVP_VS_CHALLENGE_VALUE, builder));
		} else {
			sendError(hawkProtocol.getType(), Status.error.NO_FIGHT_TIMES);
		}
	}
	
	/**
	 * 战报列表请求
	 * 
	 * @param hawkProtocol
	 */
	@ProtocolHandlerAnno(code = HP.code.OSPVP_BATTLELIST_C_VALUE)
	private void battleListLogic(Protocol hawkProtocol) {
		ClientSession clientSession = CrossServerManager.getInstance().getCSSession();
		if (clientSession == null || !clientSession.isActive()) {
			sendError(hawkProtocol.getType(), Status.error.CROSS_SERVER_CONNECT_FAIL);
			return;
		}
		// 发送跨服数据
		PlayerIdentify.Builder builder = PlayerIdentify.newBuilder();
		builder.setIdentify(PlayerUtil.getPlayerIdentify(player.getId()));
		clientSession.sendProtocol(Protocol.valueOf(HP.code.PVP_BATTLELIST_VALUE, builder));
	}
	
	/**
	 * 查看战报请求
	 * 
	 * @param hawkProtocol
	 */
	@ProtocolHandlerAnno(code = HP.code.OSPVP_BATTLE_C_VALUE)
	private void battleDataLogic(Protocol hawkProtocol) {
		ClientSession clientSession = CrossServerManager.getInstance().getCSSession();
		if (clientSession == null || !clientSession.isActive()) {
			sendError(hawkProtocol.getType(), Status.error.CROSS_SERVER_CONNECT_FAIL);
			return;
		}
		// 发送跨服数据
		BattleRequest.Builder builder = hawkProtocol.parseProtocol(BattleRequest.getDefaultInstance()).toBuilder();
		builder.setIdentify(PlayerUtil.getPlayerIdentify(player.getId()));
		clientSession.sendProtocol(Protocol.valueOf(HP.code.PVP_BATTLE_DATA_VALUE, builder));
	}
	
	/**
	 * 查看排行榜数据
	 * 
	 * @param hawkProtocol
	 */
	@ProtocolHandlerAnno(code = HP.code.OSPVP_RANK_INFO_C_VALUE)
	private void rankMessageLogic(Protocol hawkProtocol) {
		ClientSession clientSession = CrossServerManager.getInstance().getCSSession();
		if (clientSession == null || !clientSession.isActive()) {
			sendError(hawkProtocol.getType(), Status.error.CROSS_SERVER_CONNECT_FAIL);
			return;
		}
		
		// 发送跨服数据
		PlayerIdentify.Builder builder = PlayerIdentify.newBuilder();
		builder.setIdentify(PlayerUtil.getPlayerIdentify(player.getId()));
		clientSession.sendProtocol(Protocol.valueOf(HP.code.RANK_MESSAGE_VALUE, builder));
	}
	
	/**
	 * 查看人信息
	 * 
	 * @param hawkProtocol
	 */
	@ProtocolHandlerAnno(code = HP.code.OSPVP_PLAYER_ROLES_INFO_C_VALUE)
	private void seePlayerLogic(Protocol hawkProtocol) {
		ClientSession clientSession = CrossServerManager.getInstance().getCSSession();
		if (clientSession == null || !clientSession.isActive()) {
			sendError(hawkProtocol.getType(), Status.error.CROSS_SERVER_CONNECT_FAIL);
			return;
		}
		// 数据包处理
		PlayerSnapshotRequest.Builder requestBuilder = hawkProtocol.parseProtocol(PlayerSnapshotRequest.getDefaultInstance()).toBuilder();
		String seeIdentify = requestBuilder.getSeeIdentify();
		int index = seeIdentify.lastIndexOf("*");
		String platform = seeIdentify.substring(0, index);
		// 是本服的账号处理
		if (platform.equals(GsApp.getInstance().getServerIdentify())) {
			PlayerSnapshotResponse.Builder response = PlayerSnapshotResponse.newBuilder();
			int playerId = PlayerUtil.getPlayerIdFromIdentify(seeIdentify);
			PlayerSnapshotInfo.Builder snapshot = SnapShotManager.getInstance().getPlayerSnapShot(playerId);
			response.setSnapshot(snapshot);
			player.sendProtocol(Protocol.valueOf(HP.code.OSPVP_PLAYER_ROLES_INFO_S_VALUE, response));
		} else {
			// 查看的不是本服账号信息处理
			requestBuilder.setIdentify(PlayerUtil.getPlayerIdentify(player.getId()));
			clientSession.sendProtocol(Protocol.valueOf(HP.code.PLAYER_ROLES_INFO_VALUE, requestBuilder));
		}
	}
	
	/**
	 * 购买竞技挑战次数
	 * 
	 * @param hawkProtocol
	 */
	@ProtocolHandlerAnno(code = HP.code.OSPVP_BUY_VS_NUM_C_VALUE)
	private void buyTimesLogic(Protocol hawkProtocol) {
		// 跨服竞技状态判断
		EnterState state = CrossBattleService.getInstance().isCanEnter(player.getId());
		if (EnterState.NORMAL != state) {
			BuyBattleTimesResponse.Builder builder = BuyBattleTimesResponse.newBuilder();
			builder.setEnterState(state);
			player.sendProtocol(Protocol.valueOf(HP.code.OSPVP_BUY_VS_NUM_S_VALUE, builder));
			return;
		}
		// 数据解析
		BuyBattleTimesRequest.Builder request = hawkProtocol.parseProtocol(BuyBattleTimesRequest.getDefaultInstance()).toBuilder();
		if (request.getBattleTimes() <= 0) {
			sendError(HP.code.OSPVP_BUY_VS_NUM_C_VALUE, Status.error.PARAMS_INVALID_VALUE);
			return;
		}
		// 取玩家竞技数据
		CrossPlayerEntity entity = CrossBattleService.getInstance().getCrossPlayer(player.getId());
		if (null == entity) {
			sendError(HP.code.OSPVP_BUY_VS_NUM_C_VALUE, Status.error.BUY_BATTLE_TIMES_LIMIT_VALUE);
			return;
		}
		// 购买次数是否还有
		VipPrivilegeCfg vipData = ConfigManager.getInstance().getConfigByKey(VipPrivilegeCfg.class, player.getVipLevel());
		if (vipData.getCrossBattleTimes() <= 0 || entity.getBuyTimes() + request.getBattleTimes() > vipData.getCrossBattleTimes()) {
			sendError(HP.code.OSPVP_BUY_VS_NUM_C_VALUE, Status.error.TIME_LIMIT_TODAY_BUY_TIMES_LIMIT);
			return;
		}
		// 购买消耗判断
		int needCost = CrossBattleCfg.getInstance().getCost(entity.getBuyTimes(), request.getBattleTimes());
		if (player.getGold() < needCost) {
			player.sendError(HP.code.OSPVP_BUY_VS_NUM_C_VALUE, Status.error.GOLD_NOT_ENOUGH_VALUE);
			return;
		}
		// 扣钻并推送
		player.consumeGold(needCost, Action.BUY_CROSS_BATTLE_TIMES);
		ConsumeItems.valueOf(Const.changeType.CHANGE_GOLD, needCost).pushChange(player);
		// 更新数据
		entity.updateTimes(request.getBattleTimes());;
		BehaviorLogger.log4Service(player.getId(), Source.USER_OPERATION, Action.BUY_CROSS_BATTLE_TIMES, Params.valueOf("costGold", needCost), Params.valueOf("buyTimes", request.getBattleTimes()));
		// 构建返回数据
		BuyBattleTimesResponse.Builder response = BuyBattleTimesResponse.newBuilder();
		int times = vipData.getCrossBattleTimes() - entity.getBuyTimes();
		times = times > 0 ? times : 0;
		response.setLeftTimes(entity.getBattleTimes());
		response.setLeftBuyTimes(times);
		player.sendProtocol(Protocol.valueOf(HP.code.OSPVP_BUY_VS_NUM_S_VALUE, response));
	}
	
	/**
	 * 批量查询本服玩家的跨服积分排名
	 * 
	 * @param hawkProtocol
	 */
	@ProtocolHandlerAnno(code = HP.code.OSPVP_PLAYERS_RANK_C_VALUE)
	private void playerRanks(Protocol hawkProtocol) {
		PlayerRankRequest.Builder request = hawkProtocol.parseProtocol(PlayerRankRequest.getDefaultInstance()).toBuilder();
		PlayerRankResponse.Builder response = PlayerRankResponse.newBuilder();
		if (request.getPlayerIdsList() != null && request.getPlayerIdsList().size() > 0) {
			for (int playerId : request.getPlayerIdsList()) {
				PlayerRank.Builder builder = PlayerRank.newBuilder();
				builder.setPlayerId(playerId);
				CrossPlayerEntity entity = CrossBattleService.getInstance().getCrossPlayer(playerId);
				if (entity == null) {
					continue;
				}
				builder.setRank(entity.getRank());
				builder.setScore(entity.getScore());
				response.addPlayerRanks(builder);
			}
		}
		player.sendProtocol(Protocol.valueOf(HP.code.OSPVP_PLAYERS_RANK_S_VALUE, response));
	}
	
}
