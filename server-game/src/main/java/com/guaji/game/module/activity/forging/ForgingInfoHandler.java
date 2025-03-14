package com.guaji.game.module.activity.forging;

import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import org.guaji.app.AppObj;
import org.guaji.config.ConfigManager;
import org.guaji.net.protocol.Protocol;
import org.guaji.net.protocol.IProtocolHandler;
import org.guaji.os.GuaJiTime;

import com.guaji.game.config.ActivityTimeCfg;
import com.guaji.game.config.ForgingFeaturesCfg;
import com.guaji.game.entity.ForgingEntity;
import com.guaji.game.manager.ForgingManager;
import com.guaji.game.player.Player;
import com.guaji.game.util.ActivityUtil;
import com.guaji.game.protocol.Const;
import com.guaji.game.protocol.GodEquipBuild.EquipInfoItem;
import com.guaji.game.protocol.GodEquipBuild.GodEquipBuildInfoRet;
import com.guaji.game.protocol.HP;
import com.guaji.game.protocol.Status;

/**
 * 神装锻造活动初始化
 */
public class ForgingInfoHandler implements IProtocolHandler {

	@Override
	public boolean onProtocol(AppObj appObj, Protocol protocol) {

		Player player = (Player) appObj;
		int activityId = Const.ActivityId.GODEQUIP_FORGING_VALUE;
		ActivityTimeCfg timeCfg = ActivityUtil.getCurActivityTimeCfg(activityId);
		if (timeCfg == null) {
			// 活动已关闭
			player.sendError(HP.code.EQUIP_BUILD_ACT_INFO_C_VALUE, Status.error.ACTIVITY_CLOSE);
			return true;
		}
		// 提取功能配置数据
		ForgingFeaturesCfg config = ConfigManager.getInstance().getConfigByIndex(ForgingFeaturesCfg.class, 0);
		if (null == config) {
			player.sendError(HP.code.EQUIP_BUILD_ACT_INFO_C_VALUE, Status.error.DATA_NOT_FOUND_VALUE);
			return true;
		}
		// 提取玩家数据
		ForgingStatus forgingStatus = ActivityUtil.getActivityStatus(player.getPlayerData(), activityId, timeCfg.getStageId(), ForgingStatus.class);
		ForgingEntity entity = ForgingManager.getInstance().getEntity();
		if (forgingStatus == null || null == entity) {
			player.sendError(HP.code.EQUIP_BUILD_ACT_INFO_C_VALUE, Status.error.DATA_NOT_FOUND_VALUE);
			return true;
		}
		// 构建返回数据包
		synchronized (entity) {
			GodEquipBuildInfoRet.Builder builder = GodEquipBuildInfoRet.newBuilder();
			builder.setSingleCost(config.getSingleCost());
			builder.setTenCost(config.getContinuousCost());
			builder.setCloseTimes(timeCfg.calcActivitySurplusTime());
			if (forgingStatus.getFreeTime() > GuaJiTime.getMillisecond()) {
				int timeCd = (int) ((forgingStatus.getFreeTime() - GuaJiTime.getMillisecond()) / 1000);
				builder.setFreeTimesCD(timeCd);
			} else {
				builder.setFreeTimesCD(0);
			}
			// 装备详细信息
			Iterator<Entry<String, Integer>> iterator = config.getLimitMap().entrySet().iterator();
			Map<String, Integer> limitMap = entity.getLimitMap();
			while (iterator.hasNext()) {
				// 配置信息
				Entry<String, Integer> entry = iterator.next();
				// 构建配置信息
				EquipInfoItem.Builder itemBuilder = EquipInfoItem.newBuilder();
				itemBuilder.setReward(entry.getKey());
				
				if (limitMap.containsKey(entry.getKey())) {
					int count = limitMap.get(entry.getKey());
					if (entry.getValue() - count > 0) {
						itemBuilder.setCurCount(entry.getValue() - count);
					} else {
						itemBuilder.setCurCount(0);
					}
				} else {
					itemBuilder.setCurCount(entry.getValue());
				}
				itemBuilder.setAllCount(entry.getValue());
				// 加入集合数据
				builder.addAllEquip(itemBuilder);
			}
			player.sendProtocol(Protocol.valueOf(HP.code.EQUIP_BUILD_ACT_INFO_S_VALUE, builder));
			return true;
		}
	}
	
}
