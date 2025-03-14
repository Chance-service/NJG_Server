package com.guaji.game.rank.customspass;

import java.util.List;

import org.guaji.db.DBManager;

import com.guaji.game.entity.MapEntity;
import com.guaji.game.protocol.Const.RankType;
import com.guaji.game.rank.SingleRank;
import com.guaji.game.util.GsConst;

@SingleRank(type = RankType.CUSTOMPASS_TRAINING_RANK, maxRankNum = GsConst.MAX_RANK_NUM)
public class PassEliteRank extends PassRank {
	private String sql = "from MapEntity  order by length(eliteState) desc";

	@Override
	public void loadRank() {
		this.setRankType(RankType.CUSTOMPASS_TRAINING_RANK);
		List<MapEntity> limitQuery = DBManager.getInstance().limitQuery(getQuerySql(), 0, maxRankNum);
		for (MapEntity mapEntity : limitQuery) {
			mapEntity.convertMapAttr();
			PassInfoRank passInfo = new PassInfoRank();
			this.setRankType(this.getRankType());

			if (mapEntity.getEliteMapAttr().isEmpty()) {
				passInfo.setMapId(0);
			} else {
				passInfo.setMapId(mapEntity.getEliteMapAttr().get(mapEntity.getEliteMapAttr().size() - 1).getMapId());
			}
			
			passInfo.setRankType(this.getRankType());

			passInfo.setId(mapEntity.getPlayerId());
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
	protected String getQuerySql() {

		return sql;
	}

}
