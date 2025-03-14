package com.guaji.game.module.alliance;

import java.util.Date;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;

import org.guaji.app.AppObj;
import org.guaji.net.protocol.Protocol;
import org.guaji.net.protocol.IProtocolHandler;
import org.guaji.os.GuaJiRand;
import org.guaji.os.GuaJiTime;

import com.guaji.game.protocol.Alliance.AllianceRanking;
import com.guaji.game.protocol.Alliance.ApplyAllianceState;
import com.guaji.game.protocol.Alliance.HPAllianceJoinListS;
import com.guaji.game.protocol.Const.ApplyState;
import com.guaji.game.bean.ApplyAllianceStates;
import com.guaji.game.config.SysBasicCfg;
import com.guaji.game.entity.AllianceEntity;
import com.guaji.game.entity.PlayerAllianceEntity;
import com.guaji.game.manager.AllianceManager;
import com.guaji.game.player.Player;
import com.guaji.game.util.AllianceUtil;
import com.guaji.game.util.GsConst;
import com.guaji.game.protocol.HP;

/**
 * 可加入公会列表
 */
public class AllianceJoinListHandler implements IProtocolHandler {
	@Override
	public boolean onProtocol(AppObj appObj, Protocol protocol) {
		Player player = (Player) appObj;
		// 条件筛选
		ConcurrentHashMap<Integer, AllianceEntity> allianceEntityMap = AllianceManager.getInstance().getAllianceMap();
		List<AllianceEntity> canJoinAllianceEntities = new LinkedList<AllianceEntity>();

		for (AllianceEntity allianceEntity : allianceEntityMap.values()) {
			Date date = new Date(allianceEntity.getCreateAllianceTime());
			boolean isToday = GuaJiTime.isToday(date);
			if (allianceEntity.getBeforeDayAddVitality() >= SysBasicCfg.getInstance().getAllianceListVitality() || isToday) {
				canJoinAllianceEntities.add(allianceEntity);
			}
		}

		if (canJoinAllianceEntities.size() == 0) {
			canJoinAllianceEntities.addAll(AllianceManager.getInstance().getAllianceMap().values());
		}
		GuaJiRand.randomOrder(canJoinAllianceEntities);

		HPAllianceJoinListS.Builder builder = HPAllianceJoinListS.newBuilder();
		int limitCount = GsConst.Alliance.ONE_PAGE_SIZE;
		for (int i = 0; i < canJoinAllianceEntities.size(); i++) {
			if (i >= limitCount) {
				break;
			}
			AllianceEntity allianceEntity = canJoinAllianceEntities.get(i);
			AllianceRanking.Builder bean = AllianceRanking.newBuilder();
			bean.setSort(builder.getRankingsCount() + 1);
			bean.setId(allianceEntity.getId());
			bean.setLevel(allianceEntity.getLevel());
			bean.setName(allianceEntity.getName());
			bean.setHandName(allianceEntity.getPlayerName());
			bean.setCurrnetPop(allianceEntity.getMemberList().size());
			bean.setHasCheckButton(allianceEntity.getHasCheckLeaderMail());
			bean.setMaxPoj(AllianceUtil.getAllianceMaxPop(allianceEntity.getLevel()));
			builder.addRankings(bean);
		}

		if (builder.getRankingsCount() > 0) {
			builder.setShowTag(true);
		} else {
			builder.setShowTag(false);
		}

		// 玩家申请的各个公会的状态
		PlayerAllianceEntity playerAlliance = player.getPlayerData().getPlayerAllianceEntity();
		Map<Integer, ApplyAllianceStates> applyAllianceMap = playerAlliance.getApplyAllianceDataMap();
		if (applyAllianceMap != null && applyAllianceMap.size() > 0) {
			Iterator<Entry<Integer, ApplyAllianceStates>> maps = applyAllianceMap.entrySet().iterator();
			while (maps.hasNext()) {
				Entry<Integer, ApplyAllianceStates> next = maps.next();
				if (next.getValue().getState() == ApplyState.APPLY_STATE_2_VALUE
						&& next.getValue().getRefusedJoinTime() < GuaJiTime.getMillisecond()) {
					maps.remove();
					playerAlliance.notifyUpdate(true);
					continue;
				}

				ApplyAllianceState.Builder bean = ApplyAllianceState.newBuilder();
				bean.setAllianceId(next.getKey());
				bean.setState(next.getValue().getState());
				bean.setRefusedJoinTime(next.getValue().getRefusedJoinTime());
				builder.addAllianceState(bean);
			}
		}
		player.sendProtocol(Protocol.valueOf(HP.code.ALLIANCE_JOIN_LIST_S_VALUE, builder));
		return true;
	}
}
