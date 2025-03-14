package com.guaji.game.rank.wings;

import java.util.ArrayList;
import java.util.List;

import org.guaji.app.App;
import org.guaji.app.AppObj;
import org.guaji.db.DBManager;
import org.guaji.net.protocol.Protocol;
import org.guaji.obj.ObjBase;
import org.guaji.xid.GuaJiXID;

import com.guaji.game.protocol.Const.RankType;
import com.guaji.game.entity.PlayerWingsEntity;
import com.guaji.game.manager.AllianceManager;
import com.guaji.game.manager.RankManager;
import com.guaji.game.manager.SnapShotManager;
import com.guaji.game.module.PlayerWingModule;
import com.guaji.game.player.Player;
import com.guaji.game.rank.BaseRank;
import com.guaji.game.rank.IRankingObj;
import com.guaji.game.util.GsConst;
import com.guaji.game.protocol.HP;
import com.guaji.game.protocol.Snapshot.PlayerSnapshotInfo;
import com.guaji.game.protocol.Wings.HPWingQualityRankRet;
import com.guaji.game.protocol.Wings.WingQualityInfo;

public abstract class WingRank extends BaseRank {

	@Override
	public void loadRank() {
		List<PlayerWingsEntity> limitQuery = DBManager.getInstance().limitQuery(getQuerySql(), 0, maxRankNum);

		for (PlayerWingsEntity playerWingsEntity : limitQuery) {
			PlayerSnapshotInfo.Builder playerSnapShot = SnapShotManager.getInstance().getPlayerSnapShot(
					playerWingsEntity.getPlayerId());

			PlayerWingRank wingRank = new PlayerWingRank();
			wingRank.setPlayerLevel(playerSnapShot.getMainRoleInfo().getLevel());
			setRankParam(playerWingsEntity, wingRank);
			updateRank(wingRank);
		}
		openRank();
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	public void buildRankObjs(List list) {
		List<IRankingObj> topLimitRank = getTopLimitRank(maxRankNum);
		for (IRankingObj iRankingObj : topLimitRank) {
			PlayerWingRank winRank = (PlayerWingRank) iRankingObj;
			int playerId = winRank.getId();
			PlayerSnapshotInfo.Builder playerSnapShot = SnapShotManager.getInstance().getPlayerSnapShot(playerId);
			int allianceId = playerSnapShot.getAllianceInfo().getAllianceId();
			String allianceName = allianceId == 0 ? PlayerWingModule.NO_ALLIANCE : AllianceManager.getInstance()
					.getAlliance(allianceId).getName();
			WingQualityInfo.Builder builder = WingQualityInfo.newBuilder();
			builder.setPlayerName(playerSnapShot.getMainRoleInfo().getName());
			builder.setLevel(winRank.getPlayerLevel());
			builder.setAllianceName(allianceName);
			builder.setPlayerId(playerId);
			builder.setProf(playerSnapShot.getMainRoleInfo().getItemId());
			builder.setUseTime(winRank.getUseTime());
			builder.setRankNum(winRank.getRankPos());
			builder.setRebirthStage(playerSnapShot.getMainRoleInfo().getRebirthStage());
			list.add(builder.build());
		}
	}

	@Override
	public void parseStr(String str) {
		PlayerWingRank winRank = new PlayerWingRank(str);
		updateRank(winRank);
	}

	@Override
	public void sendRank(String str) {
		int playerId = Integer.parseInt(str);
		ObjBase<GuaJiXID, AppObj> objBase = App.getInstance().queryObject(
				GuaJiXID.valueOf(GsConst.ObjType.PLAYER, playerId));
		if (objBase == null) {
			return;
		}
		Player player = (Player) objBase.getImpl();
		List<WingQualityInfo> wingRankInfos = new ArrayList<WingQualityInfo>();
		HPWingQualityRankRet.Builder builder = HPWingQualityRankRet.newBuilder();
		builder.setVersion(1);
		for (int type = RankType.WING_WHITE_TIME_RANK_VALUE; type <= RankType.WING_ORANGE_TIME_RANK_VALUE; type++) {
			wingRankInfos.clear();
			RankManager.getInstance().getRankByType(RankType.valueOf(type), wingRankInfos);
			addWingRankInfo(builder, wingRankInfos, RankType.valueOf(type));
		}
		player.sendProtocol(Protocol.valueOf(HP.code.WING_QUALITY_RANK_S_VALUE, builder));
	}

	/**
	 * 获得翅膀排行信息;
	 * 
	 * @param builder
	 * @param wingRankInfos
	 * @param type
	 */
	private void addWingRankInfo(HPWingQualityRankRet.Builder builder, List<WingQualityInfo> wingRankInfos,
			RankType type) {
		switch (type.getNumber()) {
		case RankType.WING_WHITE_TIME_RANK_VALUE:
			builder.addAllWhiteWing(wingRankInfos);
			break;
		case RankType.WING_GREEN_TIME_RANK_VALUE:
			builder.addAllGreenWing(wingRankInfos);
			break;
		case RankType.WING_BLUE_TIME_RANK_VALUE:
			builder.addAllBlueWing(wingRankInfos);
			break;
		case RankType.WING_PURPLE_TIME_RANK_VALUE:
			builder.addAllPurpleWing(wingRankInfos);
			break;
		case RankType.WING_ORANGE_TIME_RANK_VALUE:
			builder.addAllOriginWing(wingRankInfos);
			break;
		default:
			break;
		}
	}

	/**
	 * 获取每个翅膀排行load数据的sql语句;
	 * 
	 * @return
	 */
	protected abstract String getQuerySql();

	/**
	 * 为{@link PlayerWingRank}填充必要属性;
	 * 
	 * @param playerWingsEntity
	 * @param wingRank
	 */
	protected abstract void setRankParam(PlayerWingsEntity playerWingsEntity, PlayerWingRank wingRank);
}
