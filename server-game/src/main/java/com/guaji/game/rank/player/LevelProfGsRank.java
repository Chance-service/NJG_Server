package com.guaji.game.rank.player;

import com.guaji.game.entity.PlayerEntity;
import com.guaji.game.protocol.Const.RankType;
import com.guaji.game.rank.SingleRank;
import com.guaji.game.util.GsConst;

@SingleRank(type = RankType.LEVEL_PROFGS_RANK, maxRankNum = GsConst.MAX_RANK_NUM)
public class LevelProfGsRank extends PlayerRank {

	private String sql = "from PlayerEntity where level > 0 and invalid = 0 and prof=2 order by level desc";

	@Override
	public void loadRank() {
		this.setRankType(RankType.LEVEL_PROFGS_RANK);
		super.loadRank();
		
	}
	
	@Override
	protected String getQuerySql() {
		return sql;
	}

	@Override
	protected void setRankParam(PlayerEntity playerEntity, PlayerInfoRank infoRank) {
		infoRank.setId(playerEntity.getId());
		infoRank.setLevel(playerEntity.getLevel());
		infoRank.setScore(playerEntity.getFightValue());
	}
	

}
