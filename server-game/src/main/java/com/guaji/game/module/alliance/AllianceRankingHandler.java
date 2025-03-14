package com.guaji.game.module.alliance;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import org.guaji.app.AppObj;
import org.guaji.net.protocol.Protocol;
import org.guaji.net.protocol.IProtocolHandler;

import com.guaji.game.protocol.Alliance.AllianceRanking;
import com.guaji.game.protocol.Alliance.HPAllianceRankingS;
import com.guaji.game.entity.AllianceEntity;
import com.guaji.game.manager.AllianceManager;
import com.guaji.game.player.Player;
import com.guaji.game.util.AllianceUtil;
import com.guaji.game.protocol.HP;

/**
 * 获取公会排行
 */
public class AllianceRankingHandler implements IProtocolHandler {
	@Override
	public boolean onProtocol(AppObj appObj, Protocol protocol) {
		Player player = (Player) appObj;
		HPAllianceRankingS.Builder ret = HPAllianceRankingS.newBuilder();
		List<AllianceEntity> allianceEntities = new LinkedList<>();
		allianceEntities.addAll(AllianceManager.getInstance().getAllianceMap().values());
		Collections.sort(allianceEntities, AllianceUtil.SORTALLIANCE);
		int sort = 1;
		for (AllianceEntity allianceEntity : allianceEntities) {
			if(sort > 10) {
				break;
			}
			AllianceRanking.Builder bean = AllianceRanking.newBuilder();
			bean.setSort(sort);
			bean.setId(allianceEntity.getId());
			bean.setLevel(allianceEntity.getLevel());
			bean.setName(allianceEntity.getName());
			bean.setHandName(allianceEntity.getPlayerName());
			bean.setCurrnetPop(allianceEntity.getMemberList().size());
			bean.setMaxPoj(AllianceUtil.getAllianceMaxPop(allianceEntity.getLevel()));
			ret.addRankings(bean);
			sort ++;
		}
		
		if(ret.getRankingsCount() > 0)
			ret.setShowTag(true);
		else
			ret.setShowTag(false);
		
		player.sendProtocol(Protocol.valueOf(HP.code.ALLIANCE_RANKING_S, ret));
		return true;
	}
}
