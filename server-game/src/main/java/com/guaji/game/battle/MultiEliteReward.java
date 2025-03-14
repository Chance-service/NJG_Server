package com.guaji.game.battle;

import java.util.Date;
import java.util.List;
import java.util.Map;

import org.guaji.net.protocol.Protocol;
import org.guaji.os.MyException;
import org.guaji.os.GuaJiTime;

import com.guaji.game.protocol.Battle.DropAward;
import com.guaji.game.config.MultiMapCfg;
import com.guaji.game.config.SysBasicCfg;
import com.guaji.game.entity.StateEntity;
import com.guaji.game.item.AwardItems;
import com.guaji.game.item.DropItems;
import com.guaji.game.item.ItemInfo;
import com.guaji.game.log.BehaviorLogger;
import com.guaji.game.log.BehaviorLogger.Action;
import com.guaji.game.log.BehaviorLogger.Params;
import com.guaji.game.log.BehaviorLogger.Source;
import com.guaji.game.player.Player;
import com.guaji.game.util.ActivityUtil;
import com.guaji.game.util.AwardUtil;
import com.guaji.game.protocol.Const;
import com.guaji.game.protocol.HP;
import com.guaji.game.protocol.MultiElite.MultiBattleAward;
import com.guaji.game.protocol.Reward.HPPlayerReward;
import com.guaji.game.protocol.Reward.RewardInfo;

public class MultiEliteReward {
	/**
	 * 玩家对象
	 */
	Player player;
	/**
	 * 奖励信息
	 */
	AwardItems awardItems = new AwardItems();
	/**
	 * 设置延迟发奖时间
	 */
	int delayRewardTime;
	/**
	 * 是否发放
	 */
	boolean hasReward;
	/**
	 * 掉落
	 */
	DropAward.Builder dropAward;

	public MultiEliteReward(Player player, int delayRewardTime) {
		this.player = player;
		this.hasReward = false;
		this.delayRewardTime = delayRewardTime;
		dropAward = DropAward.newBuilder();
	}

	public int getDelayRewardTime() {
		return delayRewardTime;
	}

	public void setDelayRewardTime(int delayRewardTime) {
		this.delayRewardTime = delayRewardTime;
	}

	public boolean hasReward() {
		return hasReward;
	}

	public Player getPlayer() {
		return player;
	}

	public void setPlayer(Player player) {
		this.player = player;
	}

	public AwardItems getAwardItems() {
		return awardItems;
	}

	public void setAwardItems(AwardItems awardItems) {
		this.awardItems = awardItems;
	}

	public boolean isHasReward() {
		return hasReward;
	}

	public void setHasReward(boolean hasReward) {
		this.hasReward = hasReward;
	}

	/**
	 * 计算多人副本掉落
	 * 
	 * @param multiMapCfg
	 * @param battleCount(参加人数)
	 * @param freeTimes(挑战次数)
	 * @return
	 */
	public MultiBattleAward.Builder deliverAward(MultiMapCfg multiMapCfg, int battleCount, StateEntity stateEntity) {
		if (multiMapCfg != null) {
			MultiBattleAward.Builder battleAward = MultiBattleAward.newBuilder();

			int times = 0;
			Map<Integer, Integer> battleInfoMap = stateEntity.getMultiBattleInfoMap();
			if (battleInfoMap.size() > 0) {
				times = battleInfoMap.get(multiMapCfg.getId());
			}

			int coinAward = multiMapCfg.getCoinDrop1();
			int expAward = multiMapCfg.getExpDrop1();

			switch (times) {
			case 1:
				coinAward = multiMapCfg.getCoinDrop1();
				expAward = multiMapCfg.getExpDrop1();
				break;
			case 2:
				coinAward = multiMapCfg.getCoinDrop2();
				expAward = multiMapCfg.getExpDrop2();
				break;
			case 3:
				coinAward = multiMapCfg.getCoinDrop3();
				expAward = multiMapCfg.getExpDrop3();
				break;
			default:
				break;
			}

			// 神器属性对掉落的修正
			int coinDropRate = player.getPlayerData().getMainRole().getAttribute().getValue(Const.attr.BUFF_COIN_DROP);
			int expDropRate = player.getPlayerData().getMainRole().getAttribute().getValue(Const.attr.BUFF_EXP_DROP);
			coinAward = (int) ((1.0f + 0.0001f * coinDropRate) * coinAward);
			expAward = (int) ((1.0f + 0.0001f * expDropRate) * expAward);

			// 全服掉率率
			if (SysBasicCfg.getInstance().getGlobalAwardRatio() > 1.0f) {
				coinAward = (int) (SysBasicCfg.getInstance().getGlobalAwardRatio() * coinAward);
				expAward = (int) (SysBasicCfg.getInstance().getGlobalAwardRatio() * expAward);
			}

			// 地图掉落倍率
			Date registerDate = player.getPlayerData().getPlayerEntity().getCreateTime();
			Integer mapCoinRatio = ActivityUtil.getCoinsMapRatio(registerDate);
			if (mapCoinRatio != null) {
				coinAward *= mapCoinRatio;
			}

			// 地图掉落倍率
			Integer mapExpRatio = ActivityUtil.getExpMapRatio(registerDate);
			if (mapExpRatio != null) {
				expAward *= mapExpRatio;
			}

			awardItems.addCoin(coinAward);
			awardItems.addExp(expAward);

			battleAward.setCoin(coinAward);
			battleAward.setExp(expAward);

			// 根据参战人数和挑战次数，计算奖励
			List<DropItems.Item> dropItems = multiMapCfg.getDropItemInfo().calcDrop();
			for (DropItems.Item item : dropItems) {
				ItemInfo itemInfo = null;
				// 是否从奖励组获得
				if (item.id <= 0) {
					itemInfo = AwardUtil.randomDrop(item.getType());
					itemInfo.setQuantity(itemInfo.getQuantity() * item.getCount());
				} else {
					itemInfo = new ItemInfo(item.getType(), item.getId(), item.getCount());
				}
				this.awardItems.addItem(itemInfo);
				// 添加前端展示信息
				addItemToDropAward(dropAward, itemInfo);
			}

			// 返回信息
			battleAward.setDrop(getDropAward());
			return battleAward;
		}
		return null;
	}

	/**
	 * 掉落发奖和消耗
	 * 
	 * @return
	 */
	public boolean dropTakeAffect(Action action) {
		try {
			if (!hasReward) {
				// 异步发奖
				RewardInfo.Builder builder = awardItems.rewardTakeAffect(player, action);
				hasReward = true;

				// 通知推送
				HPPlayerReward.Builder playerRewardBuilder = HPPlayerReward.newBuilder();
				playerRewardBuilder.setFlag(0);
				playerRewardBuilder.setRewards(builder);
				player.sendProtocol(Protocol.valueOf(HP.code.PLAYER_AWARD_S_VALUE, playerRewardBuilder));

				BehaviorLogger.log4Service(player, Source.SYS_OPERATION, Action.BATTLE_REWARD, Params.valueOf("action", action),
						Params.valueOf("award", awardItems.toString()));

				return true;
			}
		} catch (Exception e) {
			MyException.catchException(e);
		}
		return false;
	}

	/**
	 * 把物品信息添加到掉落奖励中
	 * 
	 * @param dropAward
	 * @param equipCfg
	 * @param count
	 */
	protected void addItemToDropAward(DropAward.Builder dropAward, ItemInfo itemInfo) {
		boolean itemExist = false;
		for (int i = 0; i < dropAward.getItemCount(); i++) {
			DropAward.Item.Builder itemBuilder = dropAward.getItemBuilder(i);
			if (itemBuilder.getItemId() == itemInfo.getItemId()) {
				// 直接获得
				itemExist = true;
				itemBuilder.setItemCount(itemBuilder.getItemCount() +(int) itemInfo.getQuantity());
			}
		}

		// 不存在, 直接添加实例
		if (!itemExist) {
			DropAward.Item.Builder itemBuilder = DropAward.Item.newBuilder();
			itemBuilder.setItemType(itemInfo.getType());
			itemBuilder.setItemId(itemInfo.getItemId());
			itemBuilder.setItemCount((int)itemInfo.getQuantity());
			dropAward.addItem(itemBuilder);
		}
	}

	/**
	 * 是否满足发奖时间
	 * 
	 * @return
	 */
	public boolean checkIsReward() {
		int currentTime = GuaJiTime.getSeconds();
		return currentTime > delayRewardTime;
	}

	public DropAward.Builder getDropAward() {
		return dropAward;
	}

	public void setDropAward(DropAward.Builder dropAward) {
		this.dropAward = dropAward;
	}

}
