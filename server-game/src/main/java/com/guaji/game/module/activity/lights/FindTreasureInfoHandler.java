package com.guaji.game.module.activity.lights;

import java.util.List;

import org.guaji.app.AppObj;
import org.guaji.config.ConfigManager;
import org.guaji.net.protocol.Protocol;
import org.guaji.net.protocol.IProtocolHandler;

import com.guaji.game.protocol.Activity2.HPFindTreasureInfoRet;
import com.guaji.game.config.ActivityTimeCfg;
import com.guaji.game.config.FindTreasureCfg;
import com.guaji.game.player.Player;
import com.guaji.game.util.ActivityUtil;
import com.guaji.game.protocol.Const;
import com.guaji.game.protocol.HP;
import com.guaji.game.protocol.Status;

/**
 * 活动 - 万家灯火
 */
public class FindTreasureInfoHandler implements IProtocolHandler{
	@Override
	public boolean onProtocol(AppObj appObj, Protocol protocol) {
		Player player = (Player) appObj;
		// 检测活动是否开放
		int activityId = Const.ActivityId.FIND_TREASURE_VALUE;
		ActivityTimeCfg timeCfg = ActivityUtil.getCurActivityTimeCfg(activityId);
		if(timeCfg == null){
			// 活动已关闭
			player.sendError(protocol.getType(), Status.error.ACTIVITY_CLOSE);
			return true;
		}
		
		HPFindTreasureInfoRet.Builder ret = HPFindTreasureInfoRet.newBuilder();
		FindTreasureStatus status = ActivityUtil.getActivityStatus(player.getPlayerData(), 
				activityId, timeCfg.getStageId(), FindTreasureStatus.class);
		List<FindTreasureCfg> typeCfgs = ConfigManager.getInstance().getConfigList(FindTreasureCfg.class);
		for(FindTreasureCfg typeCfg : typeCfgs){
			int alreadyTime = status.getTodayFindTimes(typeCfg.getType());
			ret.addLeftSearchTimes(Math.max(0, typeCfg.getOneDayMaxTimes() - alreadyTime));
		}
		ret.setLeftTime(timeCfg.calcActivitySurplusTime());
		player.sendProtocol(Protocol.valueOf(HP.code.FIND_TREASURE_INFO_S, ret));
		return true;
	}
}
