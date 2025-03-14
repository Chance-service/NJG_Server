package com.guaji.game.rank.alliance;

import java.util.List;

import org.guaji.app.App;
import org.guaji.app.AppObj;
import org.guaji.net.protocol.Protocol;
import org.guaji.obj.ObjBase;
import org.guaji.xid.GuaJiXID;

import com.guaji.game.config.WorldBossCfg;
import com.guaji.game.entity.AllianceEntity;
import com.guaji.game.manager.AllianceManager;
import com.guaji.game.manager.WorldBossInfo;
import com.guaji.game.manager.WorldBossManager;
import com.guaji.game.player.Player;
import com.guaji.game.protocol.HP;
import com.guaji.game.protocol.Const.RankType;
import com.guaji.game.protocol.Ranks.AllianceItemInfo;
import com.guaji.game.protocol.Ranks.HPTopRankListRet;
import com.guaji.game.rank.SingleRank;
import com.guaji.game.util.AllianceUtil;
import com.guaji.game.util.GsConst;
import com.guaji.game.util.Tuple2;

@SingleRank(type = RankType.ALLIANCE_BOSSHARM_RANK, maxRankNum = GsConst.MAX_RANK_NUM)
public class AllianceBossHarmRank extends AllianceRank {

	private String sql = "from AllianceEntity where bossVitality > 0 and invalid = 0 order by bossVitality desc";

	@Override
	public void loadRank() {
		this.setRankType(RankType.ALLIANCE_BOSSHARM_RANK);
	}

	@Override
	public void buildRankObjs(List list) {

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
		HPTopRankListRet.Builder builder = HPTopRankListRet.newBuilder();

		builder.setRankType(this.getRankType());
		// 获取前10名联盟信息
//		WorldBossInfo lastWorldInfo = WorldBossManager.getInstance().getLastBossInfo();
//		List<Tuple2<Integer, Long>> allianceWorldBossEntities = lastWorldInfo
//				.getAllianceRankTop(GsConst.MAX_RANK_DISPLAY_NUM);
//		int alliance_index = 0;
//		for (Tuple2<Integer, Long> allianceTup : allianceWorldBossEntities) {
//			if (allianceTup.first > 0) {
//				AllianceEntity allianceEntity = AllianceManager.getInstance().getAlliance(allianceTup.first);
//				if (allianceEntity != null) {
//					int rank = ++alliance_index;
//					AllianceItemInfo.Builder itemInfo = AllianceItemInfo.newBuilder();
//					itemInfo.setAllianceId(allianceEntity.getId());
//					itemInfo.setAllianceName(allianceEntity.getName());
//					itemInfo.setCheckLeaderMail(allianceEntity.getHasCheckLeaderMail());
//					itemInfo.setMemberNum(allianceEntity.getMemberList().size());
//					itemInfo.setLevel(allianceEntity.getLevel());
//					itemInfo.setRankData(lastWorldInfo.getCurAllianceHarmInfo(allianceEntity.getId()));
//					itemInfo.setLimitJoin(allianceEntity.getJoinLimit());
//					itemInfo.setRankNum(rank);
//					itemInfo.setMaxMember(AllianceUtil.getAllianceMaxPop(allianceEntity.getLevel()));
//					builder.addAllianceItemInfo(itemInfo);
//				}
//			}
//		}
//		int myAllianceId = player.getPlayerData().getPlayerAllianceEntity().getAllianceId();
//		int mySelfRank = lastWorldInfo.getCurAllianceRankInfo(myAllianceId);
//		AllianceEntity myAlliance = AllianceManager.getInstance().getAlliance(myAllianceId);
//		Long myHarm = lastWorldInfo.getCurAllianceHarmInfo(myAllianceId);
//		if (myAlliance != null) {
//
//			AllianceItemInfo.Builder mySelfItemInfo = AllianceItemInfo.newBuilder();
//			mySelfItemInfo.setAllianceId(myAlliance.getId());
//			mySelfItemInfo.setAllianceName(myAlliance.getName());
//			mySelfItemInfo.setCheckLeaderMail(myAlliance.getHasCheckLeaderMail());
//			mySelfItemInfo.setMemberNum(myAlliance.getMemberList().size());
//			mySelfItemInfo.setLevel(myAlliance.getLevel());
//			
//			if(lastWorldInfo.getCurAllianceInfo(myAllianceId).isEmpty()) {
//				mySelfItemInfo.setRankData(-1);
//				mySelfItemInfo.setRankNum(-1);
//			}else {
//				if (mySelfRank != 0) {
//					mySelfItemInfo.setRankData(myHarm);
//					mySelfItemInfo.setRankNum(mySelfRank);
//				} else {
//					mySelfItemInfo.setRankData(0);
//					mySelfItemInfo.setRankNum(0);
//				}
//			}
//			
//			mySelfItemInfo.setLimitJoin(myAlliance.getJoinLimit());
//
//			mySelfItemInfo.setMaxMember(AllianceUtil.getAllianceMaxPop(myAlliance.getLevel()));
//			builder.setMySelfAlliance(mySelfItemInfo);
//
//		}

		player.sendProtocol(Protocol.valueOf(HP.code.RANKING_LIST_S_VALUE, builder));
	}

	@Override
	protected void afterCloseRank() {

	}

	@Override
	protected void afterOpenRank() {

	}

	@Override
	protected String getQuerySql() {
		return sql;
	}

	@Override
	protected void setRankParam(AllianceEntity allianceEntity, AllianceRankInfo infoRank) {
		infoRank.setId(allianceEntity.getId());
		infoRank.setBossVitality(allianceEntity.getBossVitality());
		infoRank.setLevel(allianceEntity.getLevel());
	}
}
