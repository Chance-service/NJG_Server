package com.guaji.cs.battle;

import java.util.ArrayList;
import java.util.List;

import org.guaji.net.protocol.Protocol;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.protobuf.InvalidProtocolBufferException;
import com.guaji.cs.CrossServer;
import com.guaji.cs.common.BuilderUtil;
import com.guaji.cs.db.BattleData;
import com.guaji.cs.db.DBManager;
import com.guaji.cs.db.DbOpUtil;
import com.guaji.cs.db.PlayerData;
import com.guaji.cs.db.RankData;
import com.guaji.cs.net.handler.SessionHandler;
import com.guaji.cs.tick.ITickable;
import com.guaji.game.battle.BattleRole;
import com.guaji.game.battle.Battlefield;
import com.guaji.game.config.CrossBattleCfg;
import com.guaji.game.protocol.Battle;
import com.guaji.game.protocol.Battle.BattleInfo;
import com.guaji.game.protocol.HP;
import com.guaji.game.protocol.Snapshot.PlayerSnapshotInfo;
import com.guaji.game.protocol.CsBattle.BattleRecordResponse;
import com.guaji.game.protocol.CsBattle.BattleRequest;
import com.guaji.game.protocol.CsBattle.BattleResponse;
import com.guaji.game.protocol.CsBattle.ChallengeResponse;
import com.guaji.game.protocol.CsBattle.CrossBattleData;
import com.guaji.game.protocol.CsBattle.DefenderResponse;
import com.guaji.game.protocol.CsBattle.OSMainInfoResponse;
import com.guaji.game.protocol.CsBattle.PlayerIdentify;
import com.guaji.game.protocol.CsBattle.PlayerInfoSyncResponse;
import com.guaji.game.protocol.CsBattle.PlayerSignup;
import com.guaji.game.protocol.CsBattle.PlayerSnapshotRequest;
import com.guaji.game.protocol.CsBattle.PlayerSnapshotResponse;
import com.guaji.game.protocol.CsBattle.PlayerVsRequest;
import com.guaji.game.protocol.CsBattle.RankResponse;
import com.guaji.game.protocol.CsBattle.RefreshVsResponse;
import com.guaji.game.protocol.CsBattle.SyncRankData;

/**
 * 战斗管理器
 */
public class BattleManager implements ITickable {

	/**
	 * 跨服战日志
	 */
	private Logger battleLog = LoggerFactory.getLogger("BattleLog");

	/**
	 * 管理器单例
	 */
	private static final BattleManager instance = new BattleManager();
	
	/**
	 * 构造函数
	 */
	private BattleManager() {
		CrossServer.getInstance().addTickable(this);
	}

	/**
	 * 获取单例对象
	 * 
	 * @return
	 */
	public static BattleManager getInstance() {
		return instance;
	}
	
	@Override
	public void onTick() {
		// 清理当前赛季数据
		BattleService.getInstance().clearSeasonData();
		// 初始化随机排行榜
		BattleService.getInstance().randomRank();
	}
	
	/**
	 * 接收推送玩家数据
	 * 
	 * @param packet
	 */
	public void onPlayerSignup(Protocol packet) {
		CrossBattleData request = packet.parseProtocol(CrossBattleData.getDefaultInstance());
		List<PlayerSignup> signupList = request.getPlayerDataList();
		for (PlayerSignup signup : signupList) {
			battleLog.info(String.format("Push player %s data", signup.getIdentify()));
			// 构造数据对象
			PlayerData playerData = BuilderUtil.createPoolData(signup);
			BattleService.getInstance().addPlayer(playerData);
			BattleService.getInstance().addServerIdentify(playerData.getServerName());
			// 加入排行榜
			BattleService.getInstance().addRankData(playerData.getIdentify());
		}
		// 更新排行榜
		BattleService.getInstance().updateRank();
	}
	
	/**
	 * 请求打开界面
	 * 
	 * @param packet
	 */
	public void showMainLogic(Protocol packet) {
		PlayerIdentify request = packet.parseProtocol(PlayerIdentify.getDefaultInstance());
		String identify = request.getIdentify();
		// 构建返回数据
		OSMainInfoResponse.Builder builder = OSMainInfoResponse.newBuilder();
		// 取账号数据
		PlayerData playerData = BattleService.getInstance().getPlayer(identify);
		if (playerData == null) {
			return;
		}
		RankData rankData = BattleService.getInstance().getRankData(identify);
		// 填充玩家自己数据
		builder.setSelfInfo(BuilderUtil.createPlayerBean(playerData, rankData));
		// 选取对手数据
		battleLog.info(String.format("Player %s show vs players", playerData.getIdentify()));
		List<PlayerData> vsPlayers = BattleService.getInstance().choiceBattlePlayer(rankData);
		// 填充对战列表数据
		for (PlayerData vsPlayer : vsPlayers) {
			rankData = BattleService.getInstance().getRankData(vsPlayer.getIdentify());
			builder.addVsPlayers(BuilderUtil.createPlayerBean(vsPlayer, rankData));
		}
		packet.getSession().sendProtocol(Protocol.valueOf(HP.code.PVP_VS_INFO_VALUE, builder.build().toByteArray()));
	}
	
	/**
	 * 同步玩家数据
	 * 
	 * @param packet
	 */
	public void syncPlayerData(Protocol packet) {
		PlayerSignup request = packet.parseProtocol(PlayerSignup.getDefaultInstance());
		PlayerData playerData = BuilderUtil.createPoolData(request);
		battleLog.info(String.format("Refresh player %s data", playerData.getIdentify()));
		BattleService.getInstance().addPlayer(playerData);
		// 返回数据包
		PlayerInfoSyncResponse.Builder rsponse = PlayerInfoSyncResponse.newBuilder();
		rsponse.setIdentify(request.getIdentify());
		rsponse.setIsSuccess(true);
		packet.getSession().sendProtocol(Protocol.valueOf(HP.code.SYNC_PLAYER_DATA_VALUE, rsponse.build().toByteArray()));
	}
	
	/**
	 * 刷新对战数据
	 * 
	 * @param packet
	 */
	public void refreshVsLogic(Protocol packet) {
		PlayerIdentify request = packet.parseProtocol(PlayerIdentify.getDefaultInstance());
		String identify = request.getIdentify();
		// 构建返回数据
		RefreshVsResponse.Builder builder = RefreshVsResponse.newBuilder();
		builder.setIdentify(identify);
		// 取账号数据
		PlayerData playerData = BattleService.getInstance().getPlayer(identify);
		if (playerData == null) {
			return;
		}
		battleLog.info(String.format("Player %s refresh vs players data", playerData.getIdentify()));
		RankData rankData = BattleService.getInstance().getRankData(identify);
		List<PlayerData> vsPlayers = BattleService.getInstance().choiceBattlePlayer(rankData);
		// 填充对战列表数据
		for (PlayerData vsPlayer : vsPlayers) {
			rankData = BattleService.getInstance().getRankData(vsPlayer.getIdentify());
			builder.addVsPlayers(BuilderUtil.createPlayerBean(vsPlayer, rankData));
		}
		packet.getSession().sendProtocol(Protocol.valueOf(HP.code.REFRESH_VS_INFO_VALUE, builder.build().toByteArray()));
	}
	
	/**
	 * 请求战斗
	 * 
	 * @param packet
	 */
	public void challengeData(Protocol packet) {
		// 挑战者 和 被挑战者唯一标识
		PlayerVsRequest request = packet.parseProtocol(PlayerVsRequest.getDefaultInstance());
		String identify = request.getIdentify();
		String sourceId = request.getSourceId();
		battleLog.info(String.format("Battle challenge player %s ---  defender player %s", sourceId, identify));
		// 获取玩家池信息
		PlayerData challengeData = BattleService.getInstance().getPlayer(sourceId);
		PlayerData defenderData = BattleService.getInstance().getPlayer(identify);
		// 获取玩家快照
		PlayerSnapshotInfo challengeSnapshot = challengeData.getSnapshot();
		PlayerSnapshotInfo defenderSnapshot = defenderData.getSnapshot();
		// 排行数据
		RankData challengeRank = BattleService.getInstance().getRankData(sourceId);
		RankData defenderRank = BattleService.getInstance().getRankData(identify);
		// 开始战斗
		Battlefield battlefield = new Battlefield();
		List<BattleRole> challengeList = BattleUtil.getBattleRoleList(challengeSnapshot);
		List<BattleRole> defenderList = BattleUtil.getBattleRoleList(defenderSnapshot);
		BattleInfo.Builder battleResult = battlefield.fighting(Battle.battleType.BATTLE_CROSS_SERVER_VALUE, challengeList, defenderList, null);
		/** 战斗结束后处理 **/
		int scoreChange = 0;
		if (battleResult.getFightResult() > 0) {
			// 挑战者胜利
			challengeRank.addWinTimes();
			defenderRank.clearWinTimes();
			battleLog.info(String.format("Challenge win times is %d --- Defender win times is %d", challengeRank.getWinTimes(), defenderRank.getWinTimes()));
			// 积分计算
			battleLog.info(String.format("Challenge score is %d --- Defender score is %d", challengeRank.getScore(), defenderRank.getScore()));
			int challengeScore = challengeRank.getScore();
			scoreChange =  BattleService.getInstance().calcScore(challengeRank.getScore(), defenderRank.getScore(), challengeRank.getWinTimes(), true);
			challengeRank.addScore(scoreChange);
			scoreChange = BattleService.getInstance().calcScore(challengeScore, defenderRank.getScore(), challengeRank.getWinTimes(), false);
			defenderRank.addScore(scoreChange);
			// 更新排行榜
			BattleService.getInstance().updateRank();
			// 更新获取排行数据
			challengeRank = BattleService.getInstance().getRankData(sourceId);
			defenderRank = BattleService.getInstance().getRankData(identify);
			// 数据落地
			DBManager.getInstance().addDbOperation(challengeRank, DbOpUtil.UPDATE);
			DBManager.getInstance().addDbOperation(defenderRank, DbOpUtil.UPDATE);
		} else {
			// 被挑战者胜利
			challengeRank.clearWinTimes();
			battleLog.info(String.format("Challenge clear win times is %d", challengeRank.getWinTimes()));
			//挑战失败积分
			int challengeFailedScore = CrossBattleCfg.getInstance().getChanllengedFailedScore();
			challengeRank.addScore(challengeFailedScore);
			battleLog.info(String.format("Challenge failde score is %d", challengeFailedScore));
			// 更新排行榜
			BattleService.getInstance().updateRank();
			challengeRank = BattleService.getInstance().getRankData(sourceId);			
			DBManager.getInstance().addDbOperation(challengeRank, DbOpUtil.UPDATE);
		}
		// 被挑战者添加战报数据
		boolean winner = battleResult.getFightResult() <= 0 ? true : false;
		BattleData battleData = new BattleData(identify, sourceId, winner ? 1 : 0, scoreChange, battleResult.build().toByteArray());
		BattleService.getInstance().addBattleData(battleData, true);
		battleLog.info("Battle end");
		/****** 构建返回数据 ******/
		// 挑战者数据
		ChallengeResponse.Builder challenge = BuilderUtil.createChallengeResponse(sourceId, !winner, challengeRank, battleResult);
		battleLog.info(String.format("Push player %s vs players", sourceId));
		List<PlayerData> vsPlayers = BattleService.getInstance().choiceBattlePlayer(challengeRank);
		// 填充对战列表数据
		for (PlayerData vsPlayer : vsPlayers) {
			RankData rankData = BattleService.getInstance().getRankData(vsPlayer.getIdentify());
			challenge.addVsPlayers(BuilderUtil.createPlayerBean(vsPlayer, rankData));
		}
		packet.getSession().sendProtocol(Protocol.valueOf(HP.code.PVP_VS_CHALLENGE_VALUE, challenge.build().toByteArray()));
		// 被挑战者数据
		List<SyncRankData.Builder> ranks = new ArrayList<SyncRankData.Builder>();
		if(battleResult.getFightResult() > 0) {
			// 更新排行数据
			List<RankData> rankList = BattleService.getInstance().getRankList();
			synchronized(rankList) {
				for (RankData rankData : rankList) {
					SyncRankData.Builder rank = BuilderUtil.createSyncRankData(rankData.getIdentify(), rankData.getRank(), rankData.getScore());
					ranks.add(rank);
				}
			}
		}
		DefenderResponse.Builder defender = BuilderUtil.createDefenderResponse(identify, defenderRank, ranks);
		SessionHandler.getInstance().broadcastPacket(Protocol.valueOf(HP.code.PVP_VS_DEFENDER_VALUE, defender.build().toByteArray()));
	}
	
	/**
	 * 查看战报列表
	 * 
	 * @param packet
	 */
	public void seeBattleList(Protocol packet) {
		PlayerIdentify request = packet.parseProtocol(PlayerIdentify.getDefaultInstance());
		String identify = request.getIdentify();
		// 构建返回数据
		BattleRecordResponse.Builder builder = BattleRecordResponse.newBuilder();
		builder.setIdentify(identify);
		// // 取战斗数据
		List<BattleData> battleDatas = BattleService.getInstance().getBattleData(identify);
		if (battleDatas != null && battleDatas.size() > 0) {
			synchronized (battleDatas) {
				for (BattleData battleData : battleDatas) {
					PlayerData playerData = BattleService.getInstance().getPlayer(battleData.getInitiator());
					if (playerData == null) {
						break;
					}
					RankData rankData = BattleService.getInstance().getRankData(battleData.getInitiator());
					builder.addBattles(BuilderUtil.createBattleRecord(battleData, playerData, rankData));
				}
			}
		}
		packet.getSession().sendProtocol(Protocol.valueOf(HP.code.PVP_BATTLELIST_VALUE, builder.build().toByteArray()));
	}
	
	/**
	 * 查看战报数据
	 * 
	 * @param packet
	 */
	public void seeBattleData(Protocol packet) {
		BattleRequest request = packet.parseProtocol(BattleRequest.getDefaultInstance());
		String identify = request.getIdentify();
		int battleId = request.getBattleId();
		// 构建返回数据
		BattleResponse.Builder builder = BattleResponse.newBuilder();
		builder.setIdentify(identify);
		// 取战斗数据
		List<BattleData> battleDatas = BattleService.getInstance().getBattleData(identify);
		if (battleDatas != null && battleDatas.size() > 0) {
			synchronized (battleDatas) {
				for (BattleData battleData : battleDatas) {
					if (battleData.getId() != battleId) {
						continue;
					}
					try {
						BattleInfo.Builder battle = BattleInfo.parseFrom(battleData.getBattle()).toBuilder();
						builder.setBattle(battle);
						break;
					} catch (InvalidProtocolBufferException e) {
						e.printStackTrace();
					}
				}
			}
		}
		packet.getSession().sendProtocol(Protocol.valueOf(HP.code.PVP_BATTLE_DATA_VALUE, builder.build().toByteArray()));
	}

	/**
	 * 排行榜数据
	 * 
	 * @param packet
	 */
	public void rankMessage(Protocol packet) {
		PlayerIdentify request = packet.parseProtocol(PlayerIdentify.getDefaultInstance());
		String identify = request.getIdentify();
		// 构建返回数据
		RankResponse.Builder builder = RankResponse.newBuilder();
		builder.setIdentify(identify);
		List<RankData> rankList = BattleService.getInstance().getRankList();
		synchronized(rankList) {
			int showNumber = 1;
			for (RankData rankData : rankList) {
				// 取账号数据
				PlayerData playerData = BattleService.getInstance().getPlayer(rankData.getIdentify());
				if (playerData == null) {
					continue;
				}
				// 填充数据
				builder.addPlayers(BuilderUtil.createPlayerBean(playerData, rankData));
				showNumber ++;
				if (showNumber > CrossBattleCfg.getInstance().getRankShows()) {
					break;
				}
			}
			packet.getSession().sendProtocol(Protocol.valueOf(HP.code.RANK_MESSAGE_VALUE, builder.build().toByteArray()));
		}
	}

	/**
	 * 玩家快照查询
	 * 
	 * @param packet
	 */
	public void queryPlayerSnapshot(Protocol packet) {
		PlayerSnapshotRequest request = packet.parseProtocol(PlayerSnapshotRequest.getDefaultInstance());
		String seeIdentify = request.getSeeIdentify();
		// 构建返回数据
		PlayerSnapshotResponse.Builder builder = PlayerSnapshotResponse.newBuilder();
		builder.setIdentify(request.getIdentify());
		PlayerData playerData = BattleService.getInstance().getPlayer(seeIdentify);
		if (playerData == null) {
			packet.getSession().sendProtocol(Protocol.valueOf(HP.code.PLAYER_ROLES_INFO_VALUE, builder.build().toByteArray()));
			return;
		}
		builder.setSnapshot(playerData.getSnapshot());
		packet.getSession().sendProtocol(Protocol.valueOf(HP.code.PLAYER_ROLES_INFO_VALUE, builder.build().toByteArray()));
	}
}
