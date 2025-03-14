package com.guaji.game.module.activity.shoot;

import java.util.Map;

import org.guaji.app.AppObj;
import org.guaji.config.ConfigManager;
import org.guaji.net.protocol.Protocol;
import org.guaji.net.protocol.IProtocolHandler;
import org.guaji.os.GuaJiRand;
import org.guaji.os.GuaJiTime;
import org.guaji.os.MyException;

import com.guaji.game.protocol.Activity2.HPShootEndInfo;
import com.guaji.game.protocol.Activity2.HPShootStartReq;
import com.guaji.game.config.ActivityTimeCfg;
import com.guaji.game.config.ShootCostCfg;
import com.guaji.game.config.ShootRewardCfgA;
import com.guaji.game.config.ShootRewardCfgB;
import com.guaji.game.entity.ShootActivityEntity;
import com.guaji.game.item.AwardItems;
import com.guaji.game.item.ConsumeItems;
import com.guaji.game.log.BehaviorLogger;
import com.guaji.game.log.BehaviorLogger.Action;
import com.guaji.game.log.BehaviorLogger.Params;
import com.guaji.game.player.Player;
import com.guaji.game.util.ActivityUtil;
import com.guaji.game.protocol.Const;
import com.guaji.game.protocol.Const.changeType;
import com.guaji.game.protocol.HP;
import com.guaji.game.protocol.Status;

/**
 * 气枪打靶活动请求
 */
public class ShootActivityRequestHandler implements IProtocolHandler {

	@Override
	public boolean onProtocol(AppObj appObj, Protocol protocol) {
		Player player = (Player) appObj;
		// 检测活动是否开放
		int activityId = Const.ActivityId.SHOOT_ACTIVITY_VALUE;
		ActivityTimeCfg timeCfg = ActivityUtil.getCurActivityTimeCfg(activityId);

		ShootActivityEntity entity = ShootActivityManager.getInstance().getShootActivityEntity();
	
		if (timeCfg == null) {
			// 活动已关闭
			player.sendError(protocol.getType(), Status.error.ACTIVITY_CLOSE);
			return true;
		}

		if (!ShootActivityManager.getInstance().isShow()) {
			player.sendError(protocol.getType(), Status.error.ACTIVITY_CLOSE);
			return true;
		}

		ShootActivityInfo shootInfo = ActivityUtil.getActivityStatus(player.getPlayerData(), activityId,
				timeCfg.getStageId(), ShootActivityInfo.class);

		HPShootStartReq req = protocol.parseProtocol(HPShootStartReq.getDefaultInstance());
		// 射击类型
		int shootType = req.getShootType();
		// 射击次数
		int shootCount = req.getShootCount();
		// 射击免费类型
		int freeType = req.getFreeType();
		// 获取配置
		Map<Object, ShootCostCfg> typeCfgMap = ConfigManager.getInstance().getConfigMap(ShootCostCfg.class);
		ShootCostCfg typeCfg = typeCfgMap.get(shootType);
		if (typeCfg == null || (shootCount != 1 && shootCount != 10)) {
			// 参数错误
			player.sendError(protocol.getType(), Status.error.PARAMS_INVALID);
			return true;
		}

		// 检测免费次数时间
		int freeTime = GuaJiTime.getSeconds() - shootInfo.getTodayFindTimes(shootType);

		if (freeType == Const.shootFreeType.SHOOT_FREE_VALUE) {
			int refreshTime = typeCfg.getFreeRefreshTime() * 60 * 60;
			// 距离免费次数时间计算
			if (freeTime < refreshTime) {
				// 未到时间
				player.sendError(protocol.getType(), Status.error.PARAMS_INVALID);
				return true;
			}
			// 刷新免费次数时间
			shootInfo.refreshTime(shootType);
		} else {
			int costMoney = 0;
			if (shootCount == 1) {
				costMoney = typeCfg.getOneTimeCost();
			} else {
				costMoney = typeCfg.getTenTimeCost();
			}

			// 验证钻石
			if (player.getGold() < costMoney) {
				// 钻石不足
				player.sendError(protocol.getType(), Status.error.GOLD_NOT_ENOUGH_VALUE);
				return true;
			}
			player.consumeGold(costMoney, Action.SHOOT_ACTIVITY);
			ConsumeItems.valueOf(changeType.CHANGE_GOLD, costMoney).pushChange(player);
		}

		// 奖励计算
		shootRewardCalculate(player, shootType, shootCount, timeCfg.getStageId(), shootInfo);
		return true;
	}

	/**
	 * 射击奖励计算
	 * 
	 * @param player
	 * @param shootType
	 * @param shootCount
	 * @param stageId
	 * @param shootInfo
	 */
	private void shootRewardCalculate(Player player, int shootType, int shootCount, int stageId,
			ShootActivityInfo shootInfo) {

		AwardItems totalAwards = new AwardItems();
		// 射击结束，返回奖励
		HPShootEndInfo.Builder ret = HPShootEndInfo.newBuilder();
		ShootActivityEntity entity = ShootActivityManager.getInstance().getShootActivityEntity();
		
		if (entity.getShootState() == Const.shootRewardState.SHOOT_B_VALUE) {
			getCfgBInfo(ret, shootCount, shootInfo, shootType, totalAwards);
		} else {
			getCfgAInfo(ret, shootCount, shootInfo, shootType, totalAwards);
		}


		totalAwards.rewardTakeAffectAndPush(player, Action.SHOOT_ACTIVITY, 0);
		player.sendProtocol(Protocol.valueOf(HP.code.SHOOT_END_S_VALUE, ret));
		player.getPlayerData().updateActivity(Const.ActivityId.SHOOT_ACTIVITY_VALUE, stageId);

		BehaviorLogger.log4Platform(player, Action.SHOOT_ACTIVITY, Params.valueOf("shootType", shootType),
				Params.valueOf("shootCount", shootCount), Params.valueOf("redEnvelopeGold", totalAwards.toDbString()));

	}

	private void getCfgAInfo(HPShootEndInfo.Builder ret, int shootCount, ShootActivityInfo shootInfo, int shootType,
			AwardItems totalAwards) {
		for (int i = 0; i < shootCount; i++) {
			// 抽取总次数
			int shootTotalCount = shootInfo.getTotalFindTimes(shootType);
			// 射击奖励逻辑
			ShootRewardCfgA rewardCfg = ShootRewardCfgA.getShootRewardA(shootType, shootTotalCount);
			String[] rewardItems = rewardCfg.getItems().split(",");

			try {
				int rewardIndex = GuaJiRand.randInt(0, rewardItems.length - 1);
				if (rewardIndex < rewardItems.length) {
					AwardItems tmp = AwardItems.valueOf(rewardItems[rewardIndex]);
					totalAwards.addItemInfos(tmp.getAwardItemInfos());
					ret.addReward(rewardItems[rewardIndex]);
					shootInfo.addTotalTimes(shootType, 1);
				}
			} catch (MyException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	
	private void getCfgBInfo(HPShootEndInfo.Builder ret, int shootCount, ShootActivityInfo shootInfo, int shootType,
			AwardItems totalAwards) {
		for (int i = 0; i < shootCount; i++) {
			// 抽取总次数
			int shootTotalCount = shootInfo.getTotalFindTimes(shootType);
			// 射击奖励逻辑
			ShootRewardCfgB rewardCfg = ShootRewardCfgB.getShootRewardB(shootType, shootTotalCount);
			String[] rewardItems = rewardCfg.getItems().split(",");

			try {
				int rewardIndex = GuaJiRand.randInt(0, rewardItems.length - 1);
				if (rewardIndex < rewardItems.length) {
					
					AwardItems tmp = AwardItems.valueOf(rewardItems[rewardIndex]);
					totalAwards.addItemInfos(tmp.getAwardItemInfos());
					ret.addReward(rewardItems[rewardIndex]);
					shootInfo.addTotalTimes(shootType, 1);
				}
			} catch (MyException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}
	}
	
}
