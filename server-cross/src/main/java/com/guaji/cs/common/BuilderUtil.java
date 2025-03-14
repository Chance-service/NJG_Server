package com.guaji.cs.common;

import java.util.List;

import com.guaji.cs.db.BattleData;
import com.guaji.cs.db.PlayerData;
import com.guaji.cs.db.RankData;
import com.guaji.game.protocol.Battle.BattleInfo;
import com.guaji.game.protocol.Player.RoleInfo;
import com.guaji.game.protocol.CsBattle.BattleRecord;
import com.guaji.game.protocol.CsBattle.ChallengeResponse;
import com.guaji.game.protocol.CsBattle.DefenderResponse;
import com.guaji.game.protocol.CsBattle.PlayerBean;
import com.guaji.game.protocol.CsBattle.PlayerSignup;
import com.guaji.game.protocol.CsBattle.SyncRankData;

/**
 * 构建数据包
 */
public class BuilderUtil {

	/**
	 * 构建玩家池对象
	 * 
	 * @param signup
	 * @return
	 */
	public static PlayerData createPoolData(PlayerSignup signup) {
		PlayerData playerData = new PlayerData();
		playerData.setIdentify(signup.getIdentify());
		playerData.setServerName(signup.getServerName());
		playerData.setSnapshot(signup.getSnapshot());
		return playerData;
	}
	
	/**
	 * 构建账号信息
	 * 
	 * @param playerData
	 * @param rankData
	 */
	public static PlayerBean.Builder createPlayerBean(PlayerData playerData, RankData rankData) {
		RoleInfo mainRole = playerData.getSnapshot().getMainRoleInfo();
		PlayerBean.Builder builder = PlayerBean.newBuilder();
		builder.setIdentify(playerData.getIdentify());
		builder.setName(mainRole.getName());
		builder.setServerName(playerData.getServerName());
		if (rankData != null) {
			builder.setScore(rankData.getScore());
			builder.setRank(rankData.getRank());
			builder.setContinueWin(rankData.getWinTimes());
		} else {
			builder.setScore(0);
			builder.setRank(0);
			builder.setContinueWin(0);
		}
		builder.setLevel(mainRole.getLevel());
		builder.setRebirthStage(mainRole.getRebirthStage());
		builder.setRoleItemId(mainRole.getItemId());
		builder.setFightValue(mainRole.getMarsterFight());
		builder.setAvatarId(mainRole.getAvatarId());
		return builder;
	}
	
	/**
	 * 构建战报列表
	 * 
	 * @param battleData
	 * @param playerData
	 * @param rankData
	 * @return
	 */
	public static BattleRecord.Builder createBattleRecord(BattleData battleData, PlayerData playerData, RankData rankData) {
		BattleRecord.Builder builder = BattleRecord.newBuilder();
		builder.setVsInfo(BuilderUtil.createPlayerBean(playerData, rankData));
		builder.setIsWin(battleData.getWinner() > 0 ? true : false);
		builder.setScoreChange(battleData.getScoreChange());
		builder.setBattleId(battleData.getId());
		return builder;
	}
	
	/**
	 * 战斗结束--挑战者数据
	 * 
	 * @param identify
	 * @param isWin
	 * @param rankData
	 * @param battle
	 * @return
	 */
	public static ChallengeResponse.Builder createChallengeResponse(String identify, boolean isWin, RankData rankData, BattleInfo.Builder battle) {
		ChallengeResponse.Builder builder = ChallengeResponse.newBuilder();
		builder.setIdentify(identify);
		builder.setIsWin(isWin);
		builder.setScore(rankData.getScore());
		builder.setRank(rankData.getRank());
		builder.setBattle(battle);
		builder.setContinueWin(rankData.getWinTimes());
		return builder;
	}
	
	/**
	 * 战斗结束--被挑战者数据
	 * 
	 * @param identify
	 * @param rankData
	 * @param ranks
	 */
	public static DefenderResponse.Builder createDefenderResponse(String identify, RankData rankData, List<SyncRankData.Builder> ranks) {
		DefenderResponse.Builder builder = DefenderResponse.newBuilder();
		builder.setIdentify(identify);
		builder.setScore(rankData.getScore());
		builder.setRank(rankData.getRank());
		builder.setContinueWin(rankData.getWinTimes());
		if (ranks.size() > 0) {
			for (SyncRankData.Builder rank : ranks) {
				builder.addRankDatas(rank);
			}
		}
		return builder;
	}
	
	/**
	 * 同步随机排行数据
	 * 
	 * @param identify
	 * @param rank
	 * @param score
	 * @return
	 */
	public static SyncRankData.Builder createSyncRankData(String identify, int rank, int score) {
		SyncRankData.Builder builder = SyncRankData.newBuilder();
		builder.setIdentify(identify);
		builder.setRank(rank);
		builder.setScore(score);
		return builder;
	}
}
