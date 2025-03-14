package com.guaji.game.rank.customspass;

import java.util.ArrayList;
import java.util.List;

import org.guaji.app.App;
import org.guaji.app.AppObj;
import org.guaji.db.DBManager;
import org.guaji.net.protocol.Protocol;
import org.guaji.obj.ObjBase;
import org.guaji.xid.GuaJiXID;

import com.guaji.game.entity.StateEntity;
import com.guaji.game.manager.AllianceManager;
import com.guaji.game.manager.RankManager;
import com.guaji.game.manager.SnapShotManager;
import com.guaji.game.module.PlayerProfRankModule;
import com.guaji.game.player.Player;
import com.guaji.game.protocol.Const.RankType;
import com.guaji.game.protocol.HP;
import com.guaji.game.protocol.Ranks.HPTopRankListRet;
import com.guaji.game.protocol.Ranks.RankItemInfo;
import com.guaji.game.protocol.Snapshot.PlayerSnapshotInfo;
import com.guaji.game.rank.BaseRank;
import com.guaji.game.rank.IRankingObj;
import com.guaji.game.util.GsConst;

public abstract class PassRank extends BaseRank {

	@Override
	public void loadRank() {

	}

	@Override
	public void buildRankObjs(List list) {
		List<IRankingObj> topLimitRank = getTopLimitRank(GsConst.MAX_RANK_DISPLAY_NUM);
		for (IRankingObj iRankingObj : topLimitRank) {
			PassInfoRank playerInfoRank = (PassInfoRank) iRankingObj;
			int playerId = playerInfoRank.getId();
			PlayerSnapshotInfo.Builder playerSnapShot = SnapShotManager.getInstance().getPlayerSnapShot(playerId);
			int allianceId = playerSnapShot.getAllianceInfo().getAllianceId();
			String allianceName=PlayerProfRankModule.NO_ALLIANCE;
			if(AllianceManager.getInstance().getAlliance(allianceId)!=null) {
				allianceName=AllianceManager.getInstance().getAlliance(allianceId).getName();
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
			builder.setRankData(playerInfoRank.getMapId());
			builder.setAllianceId(allianceId);
			builder.setSignature(playerSnapShot.getPlayerInfo().getSignature());
			
			list.add(builder.build());
		}

	}

	@Override
	public void parseStr(String str) {
		PassInfoRank winRank = new PassInfoRank(str,this.getRankType());
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
		List<RankItemInfo> passRankInfos = new ArrayList<RankItemInfo>();
		HPTopRankListRet.Builder builder = HPTopRankListRet.newBuilder();
		passRankInfos.clear();
		
		RankManager.getInstance().getRankByType(this.getRankType(), passRankInfos);
		builder.addAllPlayerItemInfo(passRankInfos);
		builder.setRankType(this.getRankType());

		int allianceId = player.getPlayerData().getPlayerAllianceEntity().getAllianceId();
	
		String allianceName ="";
		if(AllianceManager.getInstance().getAlliance(allianceId)!=null)
		{
			allianceName=AllianceManager.getInstance().getAlliance(allianceId).getName();
		}else {
			allianceName =PlayerProfRankModule.NO_ALLIANCE;
		}
		RankItemInfo.Builder myRankItem = RankItemInfo.newBuilder();
		myRankItem.setPlayerName(player.getName());
		myRankItem.setLevel(player.getLevel());
		myRankItem.setAllianceName(allianceName);
		myRankItem.setPlayerId(playerId);
		myRankItem.setProf(player.getProf());
		PassInfoRank mySelfPassinfo = (PassInfoRank) this.getRankingObjById(playerId);
		if (mySelfPassinfo != null) {
			myRankItem.setRankNum(mySelfPassinfo.getRankPos());
			myRankItem.setRankData(mySelfPassinfo.getMapId());
		} else {
			myRankItem.setRankNum(0);
			myRankItem.setRankData(0);
		}
		myRankItem.setHeadIcon(player.getPlayerData().getPlayerEntity().getHeadIcon());
		myRankItem.setAllianceId(allianceId);
		myRankItem.setRebirthStage(player.getRebirthStage());
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

}
