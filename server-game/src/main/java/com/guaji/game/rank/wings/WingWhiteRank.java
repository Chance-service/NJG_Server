package com.guaji.game.rank.wings;

import com.guaji.game.entity.PlayerWingsEntity;
import com.guaji.game.rank.SingleRank;
import com.guaji.game.util.GsConst;
import com.guaji.game.protocol.Const.RankType;

@SingleRank(type = RankType.WING_WHITE_TIME_RANK, maxRankNum = GsConst.MAX_WING_RANK_NUM)
public class WingWhiteRank extends WingRank {
	private String sql = "from PlayerWingsEntity where whiteTime > 0 and invalid = 0 order by whiteTime asc";

	@Override
	public String getQuerySql() {
		return this.sql;
	}

	@Override
	protected void afterCloseRank() {
		// do nothing
	}

	@Override
	protected void afterOpenRank() {
		// do nothing
	}

	@Override
	public void setRankParam(PlayerWingsEntity playerWingsEntity, PlayerWingRank wingRank) {
		wingRank.setId(playerWingsEntity.getPlayerId());
		wingRank.setUseTime(playerWingsEntity.getWhiteTime());
	}
}
