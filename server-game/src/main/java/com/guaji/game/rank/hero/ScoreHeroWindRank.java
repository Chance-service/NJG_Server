package com.guaji.game.rank.hero;

import com.guaji.game.protocol.Const.RankType;
import com.guaji.game.rank.SingleRank;
import com.guaji.game.util.GsConst;

@SingleRank(type = RankType.HERO_WIND_RANK, maxRankNum = GsConst.MAX_RANK_NUM)
public class ScoreHeroWindRank extends HeroRank {

	private String sql = "SELECT playerId,itemId,fightvalue,level,starLevel,skinId FROM (SELECT * FROM role WHERE fightvalue > 0 and invalid = 0 and roleState = 1  and type=2 and attr = 3 ORDER BY fightvalue DESC LIMIT 6000) AS XXX GROUP BY playerId ORDER BY fightvalue DESC";

	
	@Override
	public void loadRank() {
		this.setRankType(RankType.HERO_WIND_RANK);
		super.loadRank();
		
	}



	@Override
	protected String getQuerySql() {
		return sql;
	}

	@Override
	protected void setRankParam(String ParamStr, HeroInfoRank infoRank) {
		infoRank.ValueOf(ParamStr);
	}

}
