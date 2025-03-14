package com.guaji.game.rank.alliance;

import com.guaji.game.entity.AllianceEntity;
import com.guaji.game.protocol.Const.RankType;
import com.guaji.game.rank.SingleRank;
import com.guaji.game.util.GsConst;

@SingleRank(type = RankType.ALLIANCE_VITALITY_RANK, maxRankNum = GsConst.MAX_RANK_NUM)
public class AllianceBossVitalityRank extends AllianceRank {

	private String sql = "from AllianceEntity where bossVitality > 0 and invalid = 0 order by bossVitality desc";


	@Override
	public void loadRank() {
		this.setRankType(RankType.ALLIANCE_VITALITY_RANK);
		super.loadRank();
		
	}
	


	@Override
	public void parseStr(String str) {
		super.parseStr(str);
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
