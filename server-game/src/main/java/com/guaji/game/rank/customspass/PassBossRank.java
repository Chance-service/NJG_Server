package com.guaji.game.rank.customspass;

import java.util.List;

import org.guaji.db.DBManager;

import com.guaji.game.entity.StateEntity;
import com.guaji.game.protocol.Const.RankType;
import com.guaji.game.rank.SingleRank;
import com.guaji.game.util.GsConst;

@SingleRank(type = RankType.CUSTOMPASS_BOSS_RANK, maxRankNum = GsConst.MAX_RANK_NUM)
public class PassBossRank extends PassRank {

	private String sql = "from StateEntity  order by currMapId desc";

	@Override
	public void loadRank() {
		this.setRankType(RankType.CUSTOMPASS_BOSS_RANK);
		
		List<StateEntity> limitQuery = DBManager.getInstance().limitQuery(getQuerySql(), 0, maxRankNum);
		for (StateEntity stateEntity : limitQuery) {

			PassInfoRank passInfo = new PassInfoRank();
			this.setRankType(this.getRankType());
			passInfo.setMapId(stateEntity.getCurBattleMap());
			passInfo.setId(stateEntity.getPlayerId());
			passInfo.setRankType(this.getRankType());
			updateRank(passInfo);
		}
		openRank();
	
	}

	@Override
	public void buildRankObjs(List list) {
		super.buildRankObjs(list);
	}

	@Override
	public void parseStr(String str) {
		super.parseStr(str);
	}

	

	@Override
	protected void afterCloseRank() {
		super.afterCloseRank();
	}

	@Override
	protected void afterOpenRank() {
		super.afterOpenRank();
	}

	@Override
	protected String getQuerySql() {
		return sql;
	}

}
