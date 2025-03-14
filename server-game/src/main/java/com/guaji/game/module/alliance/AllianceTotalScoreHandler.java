package com.guaji.game.module.alliance;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import org.guaji.app.AppObj;
import org.guaji.net.protocol.IProtocolHandler;
import org.guaji.net.protocol.Protocol;

import com.guaji.game.entity.AllianceEntity;
import com.guaji.game.manager.AllianceManager;
import com.guaji.game.player.Player;
import com.guaji.game.protocol.HP;
import com.guaji.game.protocol.Alliance.AllianceScoreRanking;
import com.guaji.game.protocol.Alliance.AllianceScoreRankingS;
import com.guaji.game.util.AllianceUtil;

public class AllianceTotalScoreHandler implements IProtocolHandler {

	@Override
	public boolean onProtocol(AppObj appObj, Protocol protocol) {
		// TODO Auto-generated method stub
		Player player = (Player) appObj;
		AllianceScoreRankingS.Builder ret = AllianceScoreRankingS.newBuilder();
		List<AllianceEntity> allianceEntities = new LinkedList<>();
		
		allianceEntities.addAll(AllianceManager.getInstance().getAllianceMap().values());
		Collections.sort(allianceEntities, AllianceUtil.SORTALLIANCESCORE);
		int sort = 2;
		for (AllianceEntity allianceEntity : allianceEntities) {
			
			AllianceScoreRanking.Builder bean = AllianceScoreRanking.newBuilder();
			bean.setSort(sort);
			bean.setId(allianceEntity.getId());
			bean.setLevel(allianceEntity.getLevel());
			bean.setName(allianceEntity.getName());
			bean.setHandName(allianceEntity.getPlayerName());
			bean.setCurrnetPop(allianceEntity.getMemberList().size());
			bean.setMaxPoj(AllianceUtil.getAllianceMaxPop(allianceEntity.getLevel()));
			bean.setScore(allianceEntity.getScoreValue());
			ret.addRankings(bean);

			sort ++;
		}
		
		if(ret.getRankingsCount() > 0)
			ret.setShowTag(true);
		else
			ret.setShowTag(false);
		
		player.sendProtocol(Protocol.valueOf(HP.code.ALLIANCE_SCORE_RANK_S, ret));
		return true;
		

	}

}
