package com.guaji.game.module.activity.activity197;

import java.util.Collection;
import java.util.List;

import org.guaji.app.AppObj;
import org.guaji.config.ConfigManager;
import org.guaji.net.protocol.IProtocolHandler;
import org.guaji.net.protocol.Protocol;

import com.guaji.game.config.ActivityTimeCfg;
import com.guaji.game.config.pickUpGacha_listCfg;
import com.guaji.game.player.Player;
import com.guaji.game.protocol.Activity6.SuperPickUpItem;
import com.guaji.game.protocol.Activity6.SuperPickUpList;
import com.guaji.game.protocol.Activity6.SuperPickUpSync;
import com.guaji.game.protocol.Const;
import com.guaji.game.protocol.HP;
import com.guaji.game.protocol.Status;
import com.guaji.game.util.ActivityUtil;

/**
* @author 作者 E-mail:Jeremy@163.com
* @version 创建时间：2019年4月5日 下午9:30:55
* 类说明
*/
public class Activity197InfoHandler implements IProtocolHandler{
	@Override
	public boolean onProtocol(AppObj appObj, Protocol protocol) {
		Player player = (Player) appObj;
		// 检测活动是否开放
		int activityId = Const.ActivityId.ACTIVITY197_SUPER_PICKUP_VALUE;
		ActivityTimeCfg activityTimeCfg = ActivityUtil.getCurActivityTimeCfg(activityId);
		if (activityTimeCfg == null) {
			// 活动已关闭
			player.sendError(protocol.getType(), Status.error.ACTIVITY_CLOSE);
			return true;
		}
		
		Activity197Status status = ActivityUtil.getActivityStatus(player.getPlayerData(), activityId, activityTimeCfg.getStageId(),
				Activity197Status.class);
		
		if (status.checkDailyClear()) {
			// 更新玩家的活动数据
			player.getPlayerData().updateActivity(activityId, activityTimeCfg.getStageId());
		}
		
		SuperPickUpSync req = protocol.parseProtocol(SuperPickUpSync.getDefaultInstance());
		int Id = req.getId();

		SuperPickUpList.Builder builder = generateInfo(player,activityTimeCfg,Id,status,null);
		
		player.sendProtocol(Protocol.valueOf(HP.code.ACTIVITY197_SUPER_PICKUP_INFO_S, builder));
		return true;
	}

	public static SuperPickUpList.Builder generateInfo(Player player,ActivityTimeCfg timeCfg,int id,Activity197Status status,List<String> awardLsit) {
		
		SuperPickUpList.Builder builder = SuperPickUpList.newBuilder();
		builder.setId(id);
		
		int genLeftTimes = 0;
		int singleCost = 0;
		int tenCost = 0;
		int leftLimitTimes = 0;
		
		if (id != 0) {
			pickUpGacha_listCfg gachaCfg = ConfigManager.getInstance().getConfigByKey(pickUpGacha_listCfg.class, id);
			if (gachaCfg != null) {
				SuperPickUpItem.Builder pickUpInfo = SuperPickUpItem.newBuilder();
				genLeftTimes = gachaCfg.getGuarant();
				singleCost = gachaCfg.getSingleCost();
				tenCost = gachaCfg.getTenCost();
				leftLimitTimes = Math.max(genLeftTimes - status.getCounter(id), 0);
				
				pickUpInfo.setId(id);
				pickUpInfo.setLeftTime(gachaCfg.calcCloseTime());
				pickUpInfo.setFreeTimes(gachaCfg.getFreeCount()-status.getUsefree(id));
				pickUpInfo.setOnceCostGold(singleCost);
				pickUpInfo.setTenCostGold(tenCost);
				pickUpInfo.setLeftAwardTimes(leftLimitTimes);
				pickUpInfo.setTicket(gachaCfg.getTicket());
				
				if (awardLsit != null) {
					pickUpInfo.addAllReward(awardLsit);
				}
				
				builder.addAllInfo(pickUpInfo);
			}
		} else {
			Collection <pickUpGacha_listCfg> allLiveCfg = ConfigManager.getInstance().getConfigMap(pickUpGacha_listCfg.class).values();
			boolean needsave = false;
			for (pickUpGacha_listCfg LiveCfg :allLiveCfg) {
				if (LiveCfg.isActive()) {
					SuperPickUpItem.Builder pickUpInfo = SuperPickUpItem.newBuilder();
					genLeftTimes = LiveCfg.getGuarant();
					singleCost = LiveCfg.getSingleCost();
					tenCost = LiveCfg.getTenCost();
					leftLimitTimes = Math.max(genLeftTimes - status.getCounter(LiveCfg.getId()), 0);
					
					pickUpInfo.setId(LiveCfg.getId());
					pickUpInfo.setLeftTime(LiveCfg.calcCloseTime());
					pickUpInfo.setFreeTimes(LiveCfg.getFreeCount()-status.getUsefree(LiveCfg.getId()));
					pickUpInfo.setOnceCostGold(singleCost);
					pickUpInfo.setTenCostGold(tenCost);
					pickUpInfo.setLeftAwardTimes(leftLimitTimes);
					pickUpInfo.setTicket(LiveCfg.getTicket());
					//pickUpInfo.addAllReward(awardLsit);
					
					builder.addAllInfo(pickUpInfo);
				} else {
					// 檢查抽池列表順便清除不需要的計數器
					if (status.getCounter(LiveCfg.getId()) > 0) {
						status.clearCounter(LiveCfg.getId());
						needsave = true;
					}
				}
			}
			if (needsave) {
				player.getPlayerData().updateActivity(timeCfg.getActivityId(), timeCfg.getStageId());
			}
		}
		return builder;
	}
}
