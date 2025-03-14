package com.guaji.game.util;


import org.guaji.net.protocol.Protocol;
import com.guaji.game.config.AdjustEventCfg;
import com.guaji.game.player.Player;
import com.guaji.game.protocol.ActionLog.HPActionLog;
import com.guaji.game.protocol.HP;

public class AdjustEventUtil {

	/**
	 * 生成玩家竞技场协议对象
	 * 
	 * @param arenaEntity
	 * @return
	 */
	public static void sentAdjustEventInfo(Player player, String eventName,int count) {
		
		if (player == null)
			return;
		AdjustEventCfg cfg = AdjustEventCfg.getEventCfgByName(eventName);
		if (cfg != null) {
			HPActionLog.Builder info = HPActionLog.newBuilder();
			info.setActionType(cfg.getId());
			info.setAndroidKey(cfg.getAndriod());
			info.setIosKey(cfg.getIos());
			info.setCount(count);
			player.sendProtocol(Protocol.valueOf(HP.code.ACTION_LOG_S_VALUE, info));
		}
	}
}
