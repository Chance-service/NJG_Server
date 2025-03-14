package com.guaji.game.rank.alliance;

import java.util.ArrayList;
import java.util.List;

import org.guaji.app.App;
import org.guaji.app.AppObj;
import org.guaji.db.DBManager;
import org.guaji.net.protocol.Protocol;
import org.guaji.obj.ObjBase;
import org.guaji.xid.GuaJiXID;

import com.guaji.game.entity.AllianceEntity;
import com.guaji.game.manager.AllianceManager;
import com.guaji.game.manager.RankManager;
import com.guaji.game.player.Player;
import com.guaji.game.protocol.HP;
import com.guaji.game.protocol.Ranks.AllianceItemInfo;
import com.guaji.game.protocol.Ranks.HPTopRankListRet;
import com.guaji.game.rank.BaseRank;
import com.guaji.game.rank.IRankingObj;
import com.guaji.game.util.AllianceUtil;
import com.guaji.game.util.GsConst;

public abstract class AllianceRank extends BaseRank {

	@Override
	public void loadRank() {
		List<AllianceEntity> limitQuery = DBManager.getInstance().limitQuery(getQuerySql(), 0, maxRankNum);
		for (AllianceEntity allianceEntity : limitQuery) {

			AllianceRankInfo Info = new AllianceRankInfo();
			Info.setLevel(allianceEntity.getLevel());
			Info.setBossVitality(allianceEntity.getBossVitality());
			Info.setRankType(this.getRankType());
			setRankParam(allianceEntity, Info);
			updateRank(Info);
		}
		openRank();
	}
	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	public void buildRankObjs(List list) {
		List<IRankingObj> topLimitRank = getTopLimitRank(GsConst.MAX_RANK_DISPLAY_NUM);
		for (IRankingObj iRankingObj : topLimitRank) {
			AllianceRankInfo allianceInfoRank = (AllianceRankInfo) iRankingObj;
			int allianceId = allianceInfoRank.getId();
	 		// 公会数据
			AllianceEntity entity = AllianceManager.getInstance().getAlliance(allianceId);
			if (entity != null) {
				AllianceItemInfo.Builder itemInfo = AllianceItemInfo.newBuilder();
				itemInfo.setAllianceId(allianceId);
				itemInfo.setAllianceName(entity.getName());
				itemInfo.setLevel(entity.getLevel());
				itemInfo.setMemberNum(entity.getMemberList().size());
				itemInfo.setRankNum(allianceInfoRank.getRankPos());
				itemInfo.setRankData((long)allianceInfoRank.getRankData());
				itemInfo.setLimitJoin(entity.getJoinLimit());
				itemInfo.setCheckLeaderMail(entity.getHasCheckLeaderMail());
				itemInfo.setMaxMember(AllianceUtil.getAllianceMaxPop(entity.getLevel()));
				
				list.add(itemInfo.build());
			}
		}
	}

	@Override
	public void parseStr(String str) {
		AllianceRankInfo winRank = new AllianceRankInfo(str,this.getRankType());
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
		List<AllianceItemInfo> infos = new ArrayList<AllianceItemInfo>();
		HPTopRankListRet.Builder builder = HPTopRankListRet.newBuilder();
		infos.clear();
		RankManager.getInstance().getRankByType(this.getRankType(), infos);
		builder.setRankType(this.getRankType());
		builder.addAllAllianceItemInfo(infos);
		int myAllianceId = player.getPlayerData().getPlayerAllianceEntity().getAllianceId();
		AllianceEntity myAlliance = AllianceManager.getInstance().getAlliance(myAllianceId);
		AllianceRankInfo mySelfRank = (AllianceRankInfo) this.getRankingObjById(myAllianceId);
		if (myAlliance != null && mySelfRank != null) {
			AllianceItemInfo.Builder mySelfItemInfo = AllianceItemInfo.newBuilder();
			mySelfItemInfo.setAllianceId(myAlliance.getId());
			mySelfItemInfo.setAllianceName(myAlliance.getName());
			mySelfItemInfo.setCheckLeaderMail(myAlliance.getHasCheckLeaderMail());
			mySelfItemInfo.setMemberNum(myAlliance.getMemberList().size());
			mySelfItemInfo.setLevel(myAlliance.getLevel());
			mySelfItemInfo.setRankData(mySelfRank.getRankData());
			mySelfItemInfo.setLimitJoin(myAlliance.getJoinLimit());
			mySelfItemInfo.setRankNum(mySelfRank.getRankPos());
			mySelfItemInfo.setMaxMember(AllianceUtil.getAllianceMaxPop(myAlliance.getLevel()));
			builder.setMySelfAlliance(mySelfItemInfo);
		}
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
	 * @param mapEntity
	 * @param infoRank
	 */
	protected abstract void setRankParam(AllianceEntity allianceEntity, AllianceRankInfo infoRank);

}
