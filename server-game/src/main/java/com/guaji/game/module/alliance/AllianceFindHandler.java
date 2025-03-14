package com.guaji.game.module.alliance;

import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import org.guaji.app.AppObj;
import org.guaji.net.protocol.Protocol;
import org.guaji.net.protocol.IProtocolHandler;
import org.guaji.os.GuaJiTime;

import com.guaji.game.protocol.Alliance.AllianceRanking;
import com.guaji.game.protocol.Alliance.ApplyAllianceState;
import com.guaji.game.protocol.Alliance.HPAllianceFindC;
import com.guaji.game.protocol.Alliance.HPAllianceJoinListS;
import com.guaji.game.protocol.Const.ApplyState;
import com.guaji.game.bean.ApplyAllianceStates;
import com.guaji.game.entity.AllianceEntity;
import com.guaji.game.entity.PlayerAllianceEntity;
import com.guaji.game.manager.AllianceManager;
import com.guaji.game.player.Player;
import com.guaji.game.util.AllianceUtil;
import com.guaji.game.protocol.HP;
import com.guaji.game.protocol.Status;

/**
 * 公会商店列表
 */
public class AllianceFindHandler implements IProtocolHandler {
	@Override
	public boolean onProtocol(AppObj appObj, Protocol protocol) {
		Player player = (Player) appObj;

		HPAllianceFindC par = protocol.parseProtocol(HPAllianceFindC.getDefaultInstance());
		if(par.getId() <= 0){
			player.sendError(protocol.getType(), Status.error.PARAMS_INVALID);
			return true;
		}

		AllianceEntity allianceEntity = AllianceManager.getInstance().getAlliance(par.getId());
		if(allianceEntity == null){
			player.sendError(protocol.getType(), Status.error.ALLIANCE_NONEXISTENT);
			return true;
		}
		
		HPAllianceJoinListS.Builder ret = HPAllianceJoinListS.newBuilder();
		AllianceRanking.Builder builder = AllianceRanking.newBuilder();
		builder.setSort(1);
		builder.setId(allianceEntity.getId());
		builder.setLevel(allianceEntity.getLevel());
		builder.setName(allianceEntity.getName());
		builder.setHandName(allianceEntity.getPlayerName());
		builder.setCurrnetPop(allianceEntity.getMemberList().size());
		builder.setMaxPoj(AllianceUtil.getAllianceMaxPop(allianceEntity.getLevel()));
		builder.setHasCheckButton(allianceEntity.getHasCheckLeaderMail());
		ret.addRankings(builder);
		if(ret.getRankingsCount() > 0)
			ret.setShowTag(true);
		else
			ret.setShowTag(false);
		
		ret.setMaxPage(1);
		ret.setCurPage(1);
		
		//玩家申请的各个公会的状态
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
				ret.addAllianceState(bean);
			}
		}
		player.sendProtocol(Protocol.valueOf(HP.code.ALLIANCE_JOIN_LIST_S_VALUE, ret));
		return true;
	}

}
