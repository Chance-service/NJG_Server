package com.guaji.game.module.activity.goldfish;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.guaji.app.AppObj;
import org.guaji.config.ConfigManager;
import org.guaji.msg.Msg;
import org.guaji.net.protocol.Protocol;
import org.guaji.net.protocol.IProtocolHandler;
import org.guaji.os.MyException;
import org.guaji.os.GuaJiRand;
import org.guaji.os.GuaJiTime;
import org.guaji.xid.GuaJiXID;

import com.guaji.game.protocol.CatchFish.CatchFishRequest;
import com.guaji.game.protocol.CatchFish.CatchFishResponse;
import com.guaji.game.protocol.CatchFish.FishingRewards;
import com.guaji.game.GsApp;
import com.guaji.game.config.ActivityTimeCfg;
import com.guaji.game.config.GoldfishFeaturesCfg;
import com.guaji.game.config.GoldfishPropertyCfg;
import com.guaji.game.config.RoleRelatedCfg;
import com.guaji.game.config.GoldfishFeaturesCfg.SingleWeightMessage;
import com.guaji.game.entity.RoleEntity;
import com.guaji.game.item.AwardItems;
import com.guaji.game.item.ConsumeItems;
import com.guaji.game.item.ItemInfo;
import com.guaji.game.item.AwardItems.Item;
import com.guaji.game.log.BehaviorLogger.Action;
import com.guaji.game.player.Player;
import com.guaji.game.util.ActivityUtil;
import com.guaji.game.util.GsConst;
import com.guaji.game.protocol.Const;
import com.guaji.game.protocol.Const.changeType;
import com.guaji.game.protocol.HP;
import com.guaji.game.protocol.Status;

/**
 * 捞金鱼协议处理
 */
public class GoldfishFishingHandler implements IProtocolHandler {

	@Override
	public boolean onProtocol(AppObj appObj, Protocol protocol) {

		Player player = (Player) appObj;
		int activityId = Const.ActivityId.GOLD_FISH_VALUE;
		ActivityTimeCfg timeCfg = ActivityUtil.getCurActivityTimeToEndCfg(activityId);
		if (timeCfg == null) {
			// 活动已关闭
			player.sendError(HP.code.CATCH_FISH_C_VALUE, Status.error.ACTIVITY_CLOSE);
			return true;
		}
		// 提取玩家数据
		GoldfishStatus db_data = ActivityUtil.getActivityStatus(player.getPlayerData(), activityId, timeCfg.getStageId(), GoldfishStatus.class);
		// 提取功能配置数据
		GoldfishFeaturesCfg config = ConfigManager.getInstance().getConfigByIndex(GoldfishFeaturesCfg.class, 0);
		if (null == config || null == db_data) {
			player.sendError(HP.code.CATCH_FISH_C_VALUE, Status.error.DATA_NOT_FOUND_VALUE);
			return true;
		}
		// 解析数据
		CatchFishRequest request = protocol.parseProtocol(CatchFishRequest.getDefaultInstance());
		// 抽奖次数---单抽or十连抽
		int costGold = this.costGoldLogic(config, db_data.getFreeTimes(), request.getIsSingle());
		// 免费次数处理
		int freeTimes = db_data.getFreeTimes();
		// 消耗处理处理
		if (costGold > 0) {
			ConsumeItems consumeItems = ConsumeItems.valueOf(changeType.CHANGE_GOLD, costGold);
			// 消耗数据校验
			if (!consumeItems.checkConsume(player, HP.code.CATCH_FISH_C_VALUE)) {
				return true;
			}
			// 扣除消耗数据
			consumeItems.consumeTakeAffect(player, Action.FISHING_GOLDFISH);
		} else {
			freeTimes -= 1;
		}
		// 捞金鱼处理
		int fishCount = request.getIsSingle() ? 1 : 10;
		List<GoldfishPropertyCfg> configList = this.fishingLogic(db_data, config, fishCount);
		// 构建数据包
		CatchFishResponse.Builder builder = CatchFishResponse.newBuilder();
		builder.setIsSingle(request.getIsSingle());
		// 积分是否翻倍
		boolean isFirst = db_data.isFirstTime();
		// 是否记录了第一条鱼的积分
		boolean firstFish = true;
		// 积分是否翻倍
		boolean isDouble = this.scoreDouble(config);
		int sumScore = 0;
		// 下发奖励集合
		AwardItems awardItems = new AwardItems();
		// 获得过的鱼种
		Set<Integer> fishId = db_data.getFishId();
		Map<Integer, Integer> roleSoulCount = new HashMap<Integer, Integer>();
		List<RoleEntity> roles = player.getPlayerData().getMercenary();
		for(RoleEntity role : roles){
			roleSoulCount.put(role.getItemId(), role.getSoulCount());
		}
		for (GoldfishPropertyCfg _config : configList) {
			FishingRewards.Builder _builder = FishingRewards.newBuilder();
			// 获得过的鱼种数据更新 积分计算和
			int score = 0;
			if (null != _config) {
				fishId.add(_config.getId());
				score = _config.getScore();
				// 构建奖励物品
				AwardItems.Item item = new AwardItems.Item();
				item.setType(_config.getItemType());
				item.setId(_config.getItemId());
				item.setCount(_config.getItemCount());
				AwardItems.Item showItem = item.clone();
				roleSoul(showItem, roleSoulCount);
				awardItems.addItem(item);
				_builder.setReward(showItem.toString());
				_builder.setFishId(_config.getId());
			} else {
				score = config.getFailScore();
			}
			if (isDouble) {
				score = score * 2;
			} else if (isFirst && firstFish) {
				// 需要翻倍的积分记录
				score = score * 2;
				firstFish = false;
			}
			_builder.setScore(score);
			builder.addRewards(_builder);
			sumScore += score;
		}
		// 下发奖励
		awardItems.rewardTakeAffectAndPush(player, Action.FISHING_GOLDFISH, 0);
		// 积分数据更新
		sumScore += db_data.getScore();
		db_data.setScore(sumScore);
		// 更新捞金鱼免费次数数据
		if (isFirst) {
			db_data.clearData(freeTimes, GuaJiTime.getNextAM0Date());
		} else {
			db_data.setFreeTimes(freeTimes);
		}
		// 数据落地
		player.getPlayerData().updateActivity(Const.ActivityId.GOLD_FISH_VALUE, timeCfg.getStageId());
		// 返回数据包
		player.sendProtocol(Protocol.valueOf(HP.code.CATCH_FISH_S_VALUE, builder));
		// 获得积分不等于0,更新排行榜
		if (sumScore != 0) {
			Msg msg = Msg.valueOf(GsConst.MsgType.GOLDFISH_ADD_SCORE);
			msg.pushParam(sumScore);
			msg.pushParam(player);
			GuaJiXID targetXId = GuaJiXID.valueOf(GsConst.ObjType.MANAGER, GsConst.ObjId.GOLDFISH_RANK);
			GsApp.getInstance().postMsg(targetXId, msg);
		}

		return true;
	}

	/**
	 * 消耗钻石数量逻辑处理
	 * 
	 * @param config
	 * @param freeTimes
	 * @param isSingle
	 * @return
	 */
	private int costGoldLogic(GoldfishFeaturesCfg config, int freeTimes, boolean isSingle) {

		// 十连抽消耗
		if (!isSingle) {
			return config.getContinuousCost();
		}
		// 单抽消耗---是否还有免费次数
		if (freeTimes > 0) {
			return 0;
		} else {
			return config.getSingleCost();
		}
	}

	/**
	 * 捞金鱼逻辑
	 * 
	 * @param db_data
	 * @param config
	 * @param fishCount
	 * @return
	 */
	private List<GoldfishPropertyCfg> fishingLogic(GoldfishStatus db_data, GoldfishFeaturesCfg config, int fishCount) {

		List<GoldfishPropertyCfg> configList = new ArrayList<GoldfishPropertyCfg>();
		Map<Object, GoldfishPropertyCfg> configMap = ConfigManager.getInstance().getConfigMap(GoldfishPropertyCfg.class);
		for (int i = 0; i < fishCount; i++) {
			GoldfishPropertyCfg _config = this.randomFishId(false, db_data, config, configMap);
			configList.add(_config);
		}
		// 十连抽赠送一条鱼
		if (fishCount > 1) {
			GoldfishPropertyCfg _config = this.randomFishId(true, db_data, config, configMap);
			configList.add(_config);
		}
		return configList;
	}

	/**
	 * 随机捞取的鱼配置数据
	 * 
	 * @param isHandsel 是否是额外赠送
	 * @param db_data
	 * @param config
	 * @param configMap
	 * @return
	 */
	private GoldfishPropertyCfg randomFishId(boolean isHandsel, GoldfishStatus db_data, GoldfishFeaturesCfg config, Map<Object, GoldfishPropertyCfg> configMap) {

		int ListId = this.randomListId(isHandsel, config);
		// 捞到鱼的节奏
		if (0 != ListId) {
			// 更新进入奖池次数
			Map<Integer, Integer> prizePool = db_data.getPrizePool();
			Integer times = prizePool.get(ListId);
			if (null == times) {
				times = 1;
			} else {
				times++;
			}
			prizePool.put(ListId, times);
			// 进入该奖励池N次后获得逻辑
			List<Integer> fishIds = GoldfishPropertyCfg.groupMap.get(ListId);
			for (int fishId : fishIds) {
				GoldfishPropertyCfg _config = configMap.get(fishId);
				if (times == _config.getLimit()) {
					return _config;
				}
			}
			// 随机取鱼ID
			int totalWeight = GoldfishPropertyCfg.totalWeight.get(ListId);
			try {
				// 随机捕鱼ID值
				int fishIdValue = GuaJiRand.randInt(1, totalWeight);
				int accumulative = 0;
				for (int fishId : fishIds) {
					GoldfishPropertyCfg _config = ConfigManager.getInstance().getConfigByKey(GoldfishPropertyCfg.class, fishId);
					accumulative += _config.getWeight();
					if (fishIdValue <= accumulative) {
						return _config;
					}
				}
			} catch (MyException e) {
				MyException.catchException(e);
			}
		}
		return null;
	}

	/**
	 * 随机捞取的奖励池ID
	 * 
	 * @param isHandsel
	 * @param config
	 * @return
	 */
	private int randomListId(boolean isHandsel, GoldfishFeaturesCfg config) {

		try {
			// 随机捕鱼值
			int fishingWeight = GuaJiRand.randInt(1, config.getTotalWeight());
			// 捞到鱼了
			if (fishingWeight <= config.getFishingWeight() || isHandsel) {
				// 随机奖励池
				int poolWeight = 0;
				List<SingleWeightMessage> singleWeightList = null;
				if (isHandsel) {
					poolWeight = GuaJiRand.randInt(1, config.getAdditionalTotalWeight());
					singleWeightList = config.getAdditionalList();
				} else {
					poolWeight = GuaJiRand.randInt(1, config.getPoolTotalWeight());
					singleWeightList = config.getPoolWeightList();
				}
				// 返回奖励池ID
				int accumulative = 0;
				for (SingleWeightMessage message : singleWeightList) {
					accumulative += message.getWeight();
					if (poolWeight <= accumulative) {
						return message.getListId();
					}
				}
			}
		} catch (MyException e) {
			MyException.catchException(e);
		}
		return 0;
	}

	/**
	 * 捞取鱼后积分计算
	 *
	 * @param configList
	 * @param failScore
	 * @return
	 */
	private boolean scoreDouble(GoldfishFeaturesCfg config) {

		Calendar calendar = GuaJiTime.getCalendar();
		// 当前天
		int hour = calendar.get(Calendar.HOUR_OF_DAY);
		if (config.getStartTime() <= hour && hour < config.getEndTime()) {
			return true;
		}
		return false;
	}

	private void roleSoul(Item item, Map<Integer, Integer> roleSoulCount) {
		if (item.getType() / 10000 != Const.itemType.SOUL_VALUE) {
			return;
		}
		if(!roleSoulCount.containsKey(item.getId())){
			return;
		}	
		int curSoulCount = roleSoulCount.get(item.getId());	
		int total = curSoulCount + (int)item.getCount();
		RoleRelatedCfg cfg = ConfigManager.getInstance().getConfigByKey(RoleRelatedCfg.class, item.getId());
		if (cfg == null || cfg.getCostType() != GsConst.RoleSoulExchangeType.ROLE_SOUL) {
			return;
		}
		// 超出魂魄，兑换成材料
		if (total > cfg.getLimitCount()) {
			if (cfg.getExchange() != null || !cfg.getExchange().isEmpty()) {
				int sendCount = total - cfg.getLimitCount();
				int itemCount = 0;
				while (sendCount > 0) {
					itemCount += ItemInfo.valueOf(cfg.getExchange()).getQuantity();
					sendCount--;
				}
				item.setCount(itemCount);
				item.setId(ItemInfo.valueOf(cfg.getExchange()).getItemId());
				item.setType(ItemInfo.valueOf(cfg.getExchange()).getType());
			}
			roleSoulCount.put(item.getId(), cfg.getLimitCount());
		}else{
			roleSoulCount.put(item.getId(), total);
		}
	}
}
