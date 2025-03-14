package com.guaji.game.module.alliance;

import java.util.Calendar;

import org.guaji.app.AppObj;
import org.guaji.net.protocol.Protocol;
import org.guaji.net.protocol.IProtocolHandler;
import org.guaji.os.GuaJiTime;

import com.guaji.game.protocol.Alliance.HPAllianceEnterS;
import com.guaji.game.util.AdjustEventUtil;
import com.guaji.game.util.GsConst.AdjustActionType;
import com.guaji.game.config.SysBasicCfg;
import com.guaji.game.entity.AllianceEntity;
import com.guaji.game.entity.PlayerAllianceEntity;
import com.guaji.game.manager.AllianceManager;
import com.guaji.game.player.Player;
import com.guaji.game.protocol.HP;
import com.guaji.game.protocol.Status;

/**
 * 进入公会
 */
public class AllianceEnterHandler implements IProtocolHandler {
	
	@Override
	public boolean onProtocol(AppObj appObj, Protocol protocol) {
		Player player = (Player) appObj;
		PlayerAllianceEntity playerAllianceEntity = player.getPlayerData().getPlayerAllianceEntity();
		int allianceId = playerAllianceEntity.getAllianceId(); 
		HPAllianceEnterS.Builder ret = HPAllianceEnterS.newBuilder();
		if(allianceId == 0){
			ret.setHasAlliance(false);
			player.sendProtocol(Protocol.valueOf(HP.code.ALLIANCE_ENTER_S, ret));
			return true;
		}
		
		AllianceEntity allianceEntity = AllianceManager.getInstance().getAlliance(allianceId);
		if(allianceEntity == null){
			ret.setHasAlliance(false);
			player.sendProtocol(Protocol.valueOf(HP.code.ALLIANCE_ENTER_S, ret));
			return true;
		}
		
		if(GuaJiTime.getMillisecond() > allianceEntity.getBossOpenTime()){
			allianceEntity.setBossOpenTime(AllianceEnterHandler.getNetWeekAM0Date());
			allianceEntity.setBossOpenSize(0);
			allianceEntity.notifyUpdate(true);
		}
		

				
		AllianceManager.getInstance().sendSelfData(ret, player, allianceEntity);
		player.sendProtocol(Protocol.valueOf(HP.code.ALLIANCE_CREATE_S_VALUE, AllianceManager.getInstance().getAllianceInfo(allianceEntity, player.getId(),player.getGold())));
		return true;
	}
	
	/**
	 * 获取下周星期一0点
	 * @return
	 */
	public static long getNetWeekAM0Date() {
		
		Calendar c = GuaJiTime.getCalendar();
		int day = c.get(Calendar.DAY_OF_WEEK) - 1;
		if (day == 0) {
			day = 7;
		}
		c.set(Calendar.HOUR_OF_DAY, 0);
		c.set(Calendar.MINUTE, 0);
		c.set(Calendar.SECOND, 0);
		c.set(Calendar.MILLISECOND, 0);
		c.add(Calendar.DATE, -day + 8);
		return c.getTimeInMillis();
	}

}
