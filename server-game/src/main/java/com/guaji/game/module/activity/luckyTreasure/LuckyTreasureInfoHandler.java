package com.guaji.game.module.activity.luckyTreasure;

import java.util.TreeMap;

import org.guaji.app.AppObj;
import org.guaji.net.protocol.Protocol;
import org.guaji.net.protocol.IProtocolHandler;
import org.guaji.config.ConfigManager;
import com.guaji.game.protocol.Activity.HPLuckyBoxRet;
import com.guaji.game.config.ActivityTimeCfg;
import com.guaji.game.config.LuckyTreasureCfg;
import com.guaji.game.player.Player;
import com.guaji.game.util.ActivityUtil;
import com.guaji.game.protocol.Const;
import com.guaji.game.protocol.HP;
import com.guaji.game.protocol.Status;

public class LuckyTreasureInfoHandler implements IProtocolHandler {
	@Override
	public boolean onProtocol(AppObj appObj, Protocol protocol) {
		Player player = (Player) appObj;
		
		// 检测活动是否开放
		int activityId = Const.ActivityId.LUCK_BOX_VALUE;
		ActivityTimeCfg timeCfg = ActivityUtil.getCurActivityTimeCfg(activityId);
		if (timeCfg == null) {
			// 活动已关闭
			player.sendError(protocol.getType(), Status.error.ACTIVITY_CLOSE);
			return true;
		}
		
		// 最大奖励id
		TreeMap<Object, LuckyTreasureCfg> luckyTreasureCfgs = (TreeMap<Object, LuckyTreasureCfg>)
				ConfigManager.getInstance().getConfigMap(LuckyTreasureCfg.class);
		int maxCfgId = luckyTreasureCfgs.lastEntry().getValue().getDays();
		
		LuckyTreasureStatus status = ActivityUtil.getActivityStatus(player.getPlayerData(), 
				activityId, timeCfg.getStageId(), LuckyTreasureStatus.class);
		HPLuckyBoxRet.Builder ret = HPLuckyBoxRet.newBuilder();
		ret.setLeftTime(timeCfg.calcActivitySurplusTime());
		int getCfgId = 0;
		if(!status.todayIsGet()){
			getCfgId = Math.min(maxCfgId, status.getRewardTimes() + 1);
		}
		ret.setCfgId(getCfgId);
		
		// 前端3天的奖励配置Id
  		ret.addItemCfgId(1);
  		ret.addItemCfgId(2);
  		// 最后一天的奖励配置修正
  		int lastCfgId = Math.max(3, status.getRewardTimes() + 1);
  		lastCfgId = Math.min(maxCfgId, status.getRewardTimes() + 1);
  		ret.addItemCfgId(lastCfgId);
  		
		player.sendProtocol(Protocol.valueOf(HP.code.LUCKBOX_INFO_S_VALUE, ret));
		return true;
	}
}
