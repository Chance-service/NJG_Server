package com.guaji.game.module.activity.forging;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.guaji.app.AppObj;
import org.guaji.config.ConfigManager;
import org.guaji.msg.Msg;
import org.guaji.net.protocol.Protocol;
import org.guaji.net.protocol.IProtocolHandler;
import org.guaji.os.MyException;
import org.guaji.os.GuaJiRand;
import org.guaji.os.GuaJiTime;
import org.guaji.xid.GuaJiXID;

import com.guaji.game.GsApp;
import com.guaji.game.config.ActivityTimeCfg;
import com.guaji.game.config.ForgingFeaturesCfg;
import com.guaji.game.config.ForgingPoolCfg;
import com.guaji.game.entity.ForgingEntity;
import com.guaji.game.item.AwardItems;
import com.guaji.game.item.ConsumeItems;
import com.guaji.game.log.BehaviorLogger.Action;
import com.guaji.game.manager.ForgingManager;
import com.guaji.game.player.Player;
import com.guaji.game.util.ActivityUtil;
import com.guaji.game.util.GsConst;
import com.guaji.game.protocol.Const;
import com.guaji.game.protocol.Const.changeType;
import com.guaji.game.protocol.GodEquipBuild.EquipBuildReq;
import com.guaji.game.protocol.GodEquipBuild.EquipBuildRet;
import com.guaji.game.protocol.HP;
import com.guaji.game.protocol.Status;

/**
 * 神装锻造协议处理
 */
public class ForgingHandler implements IProtocolHandler {
	
	@Override
	public boolean onProtocol(AppObj appObj, Protocol protocol) {

		Player player = (Player) appObj;
		int activityId = Const.ActivityId.GODEQUIP_FORGING_VALUE;
		ActivityTimeCfg timeCfg = ActivityUtil.getCurActivityTimeToEndCfg(activityId);
		if (timeCfg == null) {
			// 活动已关闭
			player.sendError(HP.code.EQUIP_BUILD_EVENT_C_VALUE, Status.error.ACTIVITY_CLOSE);
			return true;
		}
		// 提取玩家数据
		ForgingStatus forgingStatus = ActivityUtil.getActivityStatus(player.getPlayerData(), activityId, timeCfg.getStageId(), ForgingStatus.class);
		// 提取功能配置数据
		ForgingFeaturesCfg config = ConfigManager.getInstance().getConfigByIndex(ForgingFeaturesCfg.class, 0);
		// 提取神器锻造数据
		ForgingEntity entity = ForgingManager.getInstance().getEntity();
		if (null == config || null == forgingStatus || null == entity) {
			player.sendError(HP.code.EQUIP_BUILD_EVENT_C_VALUE, Status.error.DATA_NOT_FOUND_VALUE);
			return true;
		}
		// 解析数据
		EquipBuildReq request = protocol.parseProtocol(EquipBuildReq.getDefaultInstance());
		synchronized (entity) {
			int forgingCount = request.getIsSingle() ? 1 : 10;
			// 抽奖次数---单抽or十连抽
			boolean isFree = forgingStatus.isCanFree();

			if (forgingCount == 10) {
				isFree = false;
			}
			int costGold = this.costGoldLogic(config, isFree, request.getIsSingle());
			// 消耗处理处理
			if (costGold > 0) {
				ConsumeItems consumeItems = ConsumeItems.valueOf(changeType.CHANGE_GOLD, costGold);
				// 消耗数据校验
				if (!consumeItems.checkConsume(player, HP.code.EQUIP_BUILD_EVENT_C_VALUE)) {
					return true;
				}
				// 扣除消耗数据
				consumeItems.consumeTakeAffect(player, Action.FORGING_EQUIP);
			}
			// 锻造处理
			List<ForgingPoolCfg> configList = this.forgingLogic(entity, forgingStatus, config, forgingCount);
			entity.getPrizeLimit();
			entity.notifyUpdate(true);
			// 个人数据更新落地
			if (isFree) {
				forgingStatus.setFreeTime(GuaJiTime.getMillisecond() + config.getInterval() * 60 * 60 * 1000);
				// ForgingManager.getInstance().addFreeTimeMap(player.getId(), forgingStatus.getFreeTime());
			}
			player.getPlayerData().updateActivity(activityId, timeCfg.getStageId());
			// 返回数据包
			EquipBuildRet.Builder builder = EquipBuildRet.newBuilder();
			StringBuffer items = new StringBuffer();

			for (ForgingPoolCfg poolCfg : configList) {
				items.append(poolCfg.getItem()).append(",");
				builder.addReward(poolCfg.getItem());
			}
			// 下发奖励
			AwardItems awardItems = AwardItems.valueOf(items.toString());
			awardItems.rewardTakeAffectAndPush(player, Action.FORGING_EQUIP, 0);
			player.sendProtocol(Protocol.valueOf(HP.code.EQUIP_BUILD_EVENT_S_VALUE, builder));
			ForgingManager.getInstance().addId(player.getId());
			// 是否需要发送邮件
			for (ForgingPoolCfg poolCfg : configList) {
				if (null != poolCfg.getRaward() && !poolCfg.getRaward().equals("")) {
					Msg msg = Msg.valueOf(GsConst.MsgType.FORGING_EQUIP_EMAIL);
					msg.pushParam(player.getName());
					// 设置参数
					msg.pushParam(poolCfg.getItem());
					msg.pushParam(poolCfg.getRaward());
					GuaJiXID targetXId = GuaJiXID.valueOf(GsConst.ObjType.MANAGER, GsConst.ObjId.FORGING_EQUIP);
					GsApp.getInstance().postMsg(targetXId, msg);
				}
			}
		}
		return true;
	}

	/**
	 * 消耗钻石数量逻辑处理
	 * 
	 * @param config
	 * @param isFree
	 * @param isSingle
	 * @return
	 */
	private int costGoldLogic(ForgingFeaturesCfg config, boolean isFree, boolean isSingle) {

		// 十连抽消耗
		if (!isSingle) {
			return config.getContinuousCost();
		}
		// 单抽消耗---是否免费
		if (isFree) {
			return 0;
		} else {
			return config.getSingleCost();
		}
	}

	/**
	 * 神器锻造逻辑（修改为10连抽必中某个东西，10连抽出11个奖励）
	 * 
	 * @param entity（全服）
	 * @param forgingStatus（玩家）
	 * @param config
	 * @param forgingCount
	 * @return
	 */
	private List<ForgingPoolCfg> forgingLogic(ForgingEntity entity, ForgingStatus forgingStatus, ForgingFeaturesCfg config, int forgingCount) {

		List<ForgingPoolCfg> configList = new ArrayList<ForgingPoolCfg>();
		for (int i = 0; i < forgingCount; i++) {
			forgingStatus.setTotalTimes(forgingStatus.getTotalTimes() + 1);
			entity.setTotalTimes(entity.getTotalTimes() + 1);

			ForgingPoolCfg pool = this.randomForging(entity, forgingStatus, config);
			if (null != pool) {
				// 更新锻造次数
				if (pool.isReset()) {
					forgingStatus.setTotalTimes(0);
				}
				configList.add(pool);
			}
		}

		// 10连抽必送一个东西，这个和客户端约定，放在最后显示
		if (forgingCount == 10) {
			Map<Integer, List<ForgingPoolCfg>> map = ForgingPoolCfg.donateMap;
			for (Integer group : map.keySet()) {
				List<ForgingPoolCfg> donateList = map.get(group);
				List<Integer> weightList = new ArrayList<Integer>();
				for (ForgingPoolCfg cfg : donateList) {
					weightList.add(cfg.getWeight());
				}
				ForgingPoolCfg cfg = GuaJiRand.randonWeightObject(donateList, weightList);
				configList.add(cfg);
				break;
			}
		}
		return configList;
	}

	/**
	 * 随机神器锻造配置数据
	 * 
	 * @param entity
	 * @param featuresConfig
	 * @param configMap
	 * @return
	 */
	private ForgingPoolCfg randomForging(ForgingEntity entity, ForgingStatus forgingStatus, ForgingFeaturesCfg featuresConfig) {

		// 获取指定奖励
		int id = featuresConfig.getRawardId(entity.getTotalTimes());
		if (id != 0) {
			ForgingPoolCfg poolConfig = ConfigManager.getInstance().getConfigByKey(ForgingPoolCfg.class, id);
			boolean isResult = this.isConfigRight(poolConfig, featuresConfig, entity);
			if (isResult) {
				return poolConfig;
			}
		}
		// 随机取数据
		int poolId = featuresConfig.getPoolId(forgingStatus.getTotalTimes());
		try {
			int totalWeight = ForgingPoolCfg.totalWeight.get(poolId);
			List<Integer> groupIds = ForgingPoolCfg.groupMap.get(poolId);
			// 随机奖励ID值
			int value = GuaJiRand.randInt(1, totalWeight);
			int accumulative = 0;
			for (int forgingId : groupIds) {
				ForgingPoolCfg poolConfig = ConfigManager.getInstance().getConfigByKey(ForgingPoolCfg.class, forgingId);
				accumulative += poolConfig.getWeight();
				if (value <= accumulative) {
					if (isFullForging(poolConfig, featuresConfig, entity)) {
						// 必中抽完了，将自己的总抽奖次数置0
						forgingStatus.setTotalTimes(0);
						return randomForging(entity, forgingStatus, featuresConfig);// 保证第一个奖池没有必中
					}
					boolean isResult = this.isConfigRight(poolConfig, featuresConfig, entity);
					if (isResult) {
						return poolConfig;
					}
				}
			}
		} catch (MyException e) {
			MyException.catchException(e);
		}
		// 随机到的物品不合法--随机从不含本服限量配置取数据
		try {
			List<Integer> groupIds = ForgingPoolCfg.groupNoLimitMap.get(poolId);
			// 随机奖励ID值
			int value = GuaJiRand.randInt(1, groupIds.size() - 1);
			int forgingId = groupIds.get(value);
			ForgingPoolCfg poolConfig = ConfigManager.getInstance().getConfigByKey(ForgingPoolCfg.class, forgingId);
			return poolConfig;
		} catch (MyException e) {
			MyException.catchException(e);
		}
		return null;
	}

	/**
	 * 随机到的物品是否合法
	 */
	private boolean isConfigRight(ForgingPoolCfg poolConfig, ForgingFeaturesCfg featuresConfig, ForgingEntity entity) {
		int number = featuresConfig.getLimitNumber(poolConfig.getItem());
		if (number == 0) {
			return true;
		}

		Map<String, Integer> limitMap = entity.getLimitMap();
		Integer limit = limitMap.get(poolConfig.getItem());
		if (null == limit) {
			limit = 0;
		}
		
		// 数据库记录的限制抽奖次数小于配置中的次数
		if (limit < number) {
			// 更新限量数据
			limit++;
			limitMap.put(poolConfig.getItem(), limit);
			return true;
		}
		return false;
	}
	
	/**
	 * 限量奖励已经发放次数大于等于全服出现个数限制
	 */
	private boolean isFullForging(ForgingPoolCfg poolConfig, ForgingFeaturesCfg featuresConfig, ForgingEntity entity) {
		int number = featuresConfig.getLimitNumber(poolConfig.getItem());
		if (number == 0) {
			return false;
		} else {
			Map<String, Integer> limitMap = entity.getLimitMap();
			Integer limit = limitMap.get(poolConfig.getItem());
			if (null == limit) {
				limit = 0;
			}
			if (limit < number) {
				return false;
			}
		}
		return true;
	}
}
