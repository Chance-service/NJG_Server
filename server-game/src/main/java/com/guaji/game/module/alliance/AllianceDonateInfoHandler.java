package com.guaji.game.module.alliance;

import java.util.Map;
import java.util.Set;

import org.guaji.app.AppObj;
import org.guaji.net.protocol.IProtocolHandler;
import org.guaji.net.protocol.Protocol;
import org.guaji.os.GuaJiTime;

import com.guaji.game.entity.AllianceEntity;
import com.guaji.game.entity.PlayerAllianceEntity;
import com.guaji.game.manager.AllianceManager;
import com.guaji.game.player.Player;
import com.guaji.game.protocol.Alliance.HPAllianceDonateInfoResp;
import com.guaji.game.protocol.Status;
/**
 * 获取联盟捐献信息
 */
public class AllianceDonateInfoHandler implements IProtocolHandler {

	@Override
	public boolean onProtocol(AppObj appObj, Protocol protocol) {
		Player player = (Player) appObj;
		// 玩家公会实体
		PlayerAllianceEntity playerAllianceEntity = player.getPlayerData().getPlayerAllianceEntity();
		int allianceId = playerAllianceEntity.getAllianceId(); 
		if(allianceId == 0){
			player.sendError(protocol.getType(), Status.error.ALLIANCE_NO_JOIN);
			return true;
		}
		// 公会实体
		AllianceEntity allianceEntity = AllianceManager.getInstance().getAlliance(allianceId);
		if(allianceEntity == null){
			player.sendError(protocol.getType(), Status.error.ALLIANCE_NONEXISTENT);
			return true;
		}
		int activeValue = allianceEntity.getActiveValue();
		//today day id
		int todayDayID = GuaJiTime.getDayIDBase2000();
		//联盟活跃度
		//捐献条目
		Map<Integer,Integer> map = playerAllianceEntity.getDailyDonateMap();
		//今日是否捐献过
		int todayDonateId = -1;
		if(map!=null&&map.size()==0){
			Set<Integer> keySet = map.keySet();
			for(Integer key:keySet){
				int dayId = map.get(key);
				if(dayId!=todayDayID){
					continue;
				}
				todayDonateId = key;
				break;
			}
		}
		
		HPAllianceDonateInfoResp.Builder builder = AllianceManager.getInstance().buildAllianceDonateInfo(activeValue, todayDonateId);
		//发送给客户端
		player.sendProtocol(protocol);
		return false;
	}
}
