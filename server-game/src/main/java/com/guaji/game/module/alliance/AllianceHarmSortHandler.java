package com.guaji.game.module.alliance;

import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.guaji.app.AppObj;
import org.guaji.net.protocol.Protocol;
import org.guaji.net.protocol.IProtocolHandler;

import com.guaji.game.protocol.Alliance.AllianceHarm;
import com.guaji.game.protocol.Alliance.HPAllianceHarmSortS;
import com.guaji.game.config.AllianceCfg;
import com.guaji.game.entity.AllianceEntity;
import com.guaji.game.manager.AllianceManager;
import com.guaji.game.manager.SnapShotManager;
import com.guaji.game.player.Player;
import com.guaji.game.util.AllianceUtil;
import com.guaji.game.protocol.HP;
import com.guaji.game.protocol.Snapshot.PlayerSnapshotInfo;
import com.guaji.game.protocol.Status;

/**
 * 获取boss伤害排行
 */
public class AllianceHarmSortHandler implements IProtocolHandler {
	@Override
	public boolean onProtocol(AppObj appObj, Protocol protocol) {
		Player player = (Player) appObj;
		int allianceId = player.getPlayerData().getPlayerAllianceEntity().getAllianceId(); 
		AllianceEntity allianceEntity = AllianceManager.getInstance().getAlliance(allianceId);
		if(allianceEntity == null){
			player.sendError(protocol.getType(), Status.error.ALLIANCE_NONEXISTENT);
			return true;
		}
		if(!allianceEntity.isBossOpen()){
			player.sendError(protocol.getType(), Status.error.ALLIANCE_BOSS_NO_OPEN_ERROR);
			return true;
		}
		
		HPAllianceHarmSortS.Builder ret = HPAllianceHarmSortS.newBuilder();
		
		int sort = 1;
		
		AllianceCfg allianceCfg = AllianceUtil.getAllianceCfg(allianceEntity.getLevel(),allianceEntity.getExp());
		if(allianceCfg == null){
			player.sendError(protocol.getType(), Status.error.DATA_NOT_FOUND);
			return true;
		}
		
		//boss总血量
		int maxHp = allianceCfg.getBossHp();
		List<Entry<Integer, Integer>> entryList = new LinkedList<>();
		entryList.addAll(allianceEntity.getBossJoinMap().entrySet());
		Collections.sort(entryList,BOSS_RANK_COMPARATOR);
		for (Entry<Integer, Integer> e: entryList) {
			Integer playerId = e.getKey();
			Integer value = allianceEntity.getBossJoinMap().get(playerId);
			PlayerSnapshotInfo.Builder builder = SnapShotManager.getInstance().getPlayerSnapShot(playerId);
			if(builder != null){
				AllianceHarm.Builder bean = AllianceHarm.newBuilder();
				bean.setId(playerId);
				bean.setSort(sort);
				bean.setLevel(builder.getMainRoleInfo().getLevel());
				bean.setName(builder.getMainRoleInfo().getName());
				bean.setHarmValue(value);
				bean.setHarmPercent(AllianceUtil.calcBossHPPercent(maxHp, value));
				bean.setRebirthStage(builder.getMainRoleInfo().getRebirthStage());
				ret.addHarms(bean);
				sort++;
			}
		}
		
		if(ret.getHarmsCount() > 0)
			ret.setShowTag(true);
		else
			ret.setShowTag(false);
		
		player.sendProtocol(Protocol.valueOf(HP.code.ALLIANCE_HARMSORT_S_VALUE, ret));
		return true;
	}
	
	private final Comparator<Entry<Integer, Integer>> BOSS_RANK_COMPARATOR = new Comparator<Map.Entry<Integer,Integer>>() {

		@Override
		public int compare(Entry<Integer, Integer> o1, Entry<Integer, Integer> o2) {
			if(o1.getValue().equals(o2.getValue())) {
				return o1.getKey() - o2.getKey(); 
			}
			return o2.getValue() - o1.getValue();
		}
		
	};
	
}
