package com.guaji.game.rank.player;

import com.guaji.game.entity.PlayerEntity;
import com.guaji.game.protocol.Const.RankType;
import com.guaji.game.rank.SingleRank;
import com.guaji.game.util.GsConst;

@SingleRank(type = RankType.SCORE_ALL_RANK, maxRankNum = GsConst.MAX_RANK_NUM)
public class ScoreAllRank extends PlayerRank {

	private String sql = "from PlayerEntity where fightvalue > 0 and invalid = 0 order by fightvalue desc";

	@Override
	public void loadRank() {
		this.setRankType(RankType.SCORE_ALL_RANK);
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
