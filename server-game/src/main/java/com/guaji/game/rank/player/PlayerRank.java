package com.guaji.game.rank.player;

import java.util.ArrayList;
import java.util.List;

import org.guaji.app.App;
import org.guaji.app.AppObj;
import org.guaji.db.DBManager;
import org.guaji.net.protocol.Protocol;
import org.guaji.obj.ObjBase;
import org.guaji.xid.GuaJiXID;

import com.guaji.game.entity.PlayerEntity;
import com.guaji.game.manager.AllianceManager;
import com.guaji.game.manager.RankManager;
import com.guaji.game.manager.SnapShotManager;
import com.guaji.game.module.PlayerProfRankModule;
import com.guaji.game.player.Player;
import com.guaji.game.protocol.HP;
import com.guaji.game.protocol.Const.RankType;
import com.guaji.game.protocol.Ranks.HPTopRankListRet;
import com.guaji.game.protocol.Ranks.RankItemInfo;
import com.guaji.game.protocol.Snapshot.PlayerSnapshotInfo;
import com.guaji.game.rank.BaseRank;
import com.guaji.game.rank.IRankingObj;
import com.guaji.game.util.GsConst;

public abstract class PlayerRank extends BaseRank {

	@Override
	public void loadRank() {

		List<PlayerEntity> limitQuery = DBManager.getInstance().limitQuery(getQuerySql(), 0, maxRankNum);
		for (PlayerEntity playerEntity : limitQuery) {

			PlayerInfoRank playerInfo = new PlayerInfoRank();
			playerInfo.setRankType(this.getRankType());
			setRankParam(playerEntity, playerInfo);
			updateRank(playerInfo);
		}
		openRank();

	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	public void buildRankObjs(List list) {

		List<IRankingObj> topLimitRank = getTopLimitRank(GsConst.MAX_RANK_DISPLAY_NUM);
		for (IRankingObj iRankingObj : topLimitRank) {
			PlayerInfoRank playerInfoRank = (PlayerInfoRank) iRankingObj;
			int playerId = playerInfoRank.getId();
			PlayerSnapshotInfo.Builder playerSnapShot = SnapShotManager.getInstance().getPlayerSnapShot(playerId);
			int allianceId = playerSnapShot.getAllianceInfo().getAllianceId();
			
			String allianceName ="";
			if(AllianceManager.getInstance().getAlliance(allianceId)!=null)
			{
				allianceName=AllianceManager.getInstance().getAlliance(allianceId).getName();
			}else {
				allianceName =PlayerProfRankModule.NO_ALLIANCE;
			}
			RankItemInfo.Builder builder = RankItemInfo.newBuilder();
			builder.setPlayerName(playerSnapShot.getMainRoleInfo().getName());
			builder.setLevel(playerSnapShot.getMainRoleInfo().getLevel());
			builder.setAllianceName(allianceName);
			builder.setPlayerId(playerId);
			builder.setProf(playerSnapShot.getMainRoleInfo().getItemId());
			builder.setHeadIcon(playerSnapShot.getPlayerInfo().getHeadIcon());
			builder.setRankNum(playerInfoRank.getRankPos());
			builder.setRebirthStage(playerSnapShot.getMainRoleInfo().getRebirthStage());
			builder.setAllianceId(allianceId);
			builder.setSignature(playerSnapShot.getPlayerInfo().getSignature());
			builder.setRankData(playerInfoRank.GetRankData());
			list.add(builder.build());
		}
	}

	@Override
	public void parseStr(String str) {

		PlayerInfoRank winRank = new PlayerInfoRank(str);
		winRank.setRankType(this.getRankType());
		updateRank(winRank);
	}

	@Override
	public void sendRank(String str) {

		int playerId = Integer.parseInt(str);
		ObjBase<GuaJiXID, AppObj> objBase = App.getInstance()
				.queryObject(GuaJiXID.valueOf(GsConst.ObjType.PLAYER, playerId));
		if (objBase == null) {
			return;
		}
		Player player = (Player) objBase.getImpl();
		List<RankItemInfo> levelRankInfos = new ArrayList<RankItemInfo>();
		HPTopRankListRet.Builder builder = HPTopRankListRet.newBuilder();
		levelRankInfos.clear();

		RankManager.getInstance().getRankByType(this.getRankType(), levelRankInfos);
		builder.addAllPlayerItemInfo(levelRankInfos);
		builder.setRankType(this.getRankType());

	
			int allianceId = player.getPlayerData().getPlayerAllianceEntity().getAllianceId();
			String allianceName = allianceId == 0 ? PlayerProfRankModule.NO_ALLIANCE
					: AllianceManager.getInstance().getAlliance(allianceId).getName();
			RankItemInfo.Builder myRankItem = RankItemInfo.newBuilder();
			myRankItem.setPlayerName(player.getName());
			myRankItem.setLevel(player.getLevel());
			myRankItem.setAllianceName(allianceName);
			myRankItem.setPlayerId(playerId);
			myRankItem.setProf(player.getProf());
			myRankItem.setHeadIcon(player.getPlayerData().getPlayerEntity().getHeadIcon());
			PlayerInfoRank mySelfinfo = (PlayerInfoRank) this.getRankingObjById(playerId);
			if (mySelfinfo != null) {
				myRankItem.setRankNum(mySelfinfo.getRankPos());
				myRankItem.setRankData(mySelfinfo.GetRankData());
			} else {
				if (this.getRankType() == RankType.LEVEL_PROFCS_RANK||this.getRankType()==RankType.SCORE_PROFCS_RANK) {
					if (player.getProf() != 3) {
						myRankItem.setRankNum(-1);
						myRankItem.setRankData(-1);
					} else {
						myRankItem.setRankNum(0);
						myRankItem.setRankData(0);
					}
				} else if (this.getRankType() == RankType.LEVEL_PROFGS_RANK||this.getRankType()==RankType.SCORE_PROFGS_RANK) {
					if (player.getProf() != 2) {
						myRankItem.setRankNum(-1);
						myRankItem.setRankData(-1);
					} else {
						myRankItem.setRankNum(0);
						myRankItem.setRankData(0);
					}
				} else if (this.getRankType() == RankType.LEVEL_PROFJS_RANK||this.getRankType()==RankType.SCORE_PROFJS_RANK) {
					if (player.getProf() != 1) {
						myRankItem.setRankNum(-1);
						myRankItem.setRankData(-1);
					} else {
						myRankItem.setRankNum(0);
						myRankItem.setRankData(0);
					}
				} else {
					myRankItem.setRankNum(0);
					myRankItem.setRankData(0);
				}

			}
		
			myRankItem.setRebirthStage(player.getRebirthStage());
			myRankItem.setAllianceId(allianceId);
			myRankItem.setSignature(player.getPlayerData().getPlayerEntity().getSignature());
			builder.setMySelf(myRankItem);
		
		player.sendProtocol(Protocol.valueOf(HP.code.RANKING_LIST_S_VALUE, builder));
	}

	@Override
	protected void afterCloseRank() {

	}

	@Override
	protected void afterOpenRank() {

	}

	/**
	 * @return 返回具体执行的sql 语句
	 */
	protected abstract String getQuerySql();

	/**
	 * @param playerEntity 玩家数据
	 * @param infoRank     排行榜数据
	 */
	protected abstract void setRankParam(PlayerEntity playerEntity, PlayerInfoRank infoRank);

}
