package com.guaji.game.module.activity.gem;

import org.guaji.app.AppObj;
import org.guaji.config.ConfigManager;
import org.guaji.net.protocol.Protocol;
import org.guaji.net.protocol.IProtocolHandler;

import com.guaji.game.protocol.Activity.HPGemCompound;
import com.guaji.game.protocol.Activity.HPGemCompoundRet;
import com.guaji.game.config.ActivityTimeCfg;
import com.guaji.game.config.GemCompoundCfg;
import com.guaji.game.config.ItemCfg;
import com.guaji.game.entity.ItemEntity;
import com.guaji.game.item.AwardItems;
import com.guaji.game.item.ConsumeItems;
import com.guaji.game.log.BehaviorLogger;
import com.guaji.game.log.BehaviorLogger.Action;
import com.guaji.game.log.BehaviorLogger.Params;
import com.guaji.game.module.quest.QuestEventBus;
import com.guaji.game.player.Player;
import com.guaji.game.util.ActivityUtil;
import com.guaji.game.util.GsConst;
import com.guaji.game.util.ItemUtil;
import com.guaji.game.protocol.Const;
import com.guaji.game.protocol.Const.QuestEventType;
import com.guaji.game.protocol.Const.changeType;
import com.guaji.game.protocol.Const.itemType;
import com.guaji.game.protocol.HP;
import com.guaji.game.protocol.Status;

/**
 * 宝石工坊
 */
public class GemCompoundHandler implements IProtocolHandler {
	
	@Override
	public boolean onProtocol(AppObj appObj, Protocol protocol) {
		
		Player player = (Player) appObj;
		// 检测活动是否开放
		int activityId = Const.ActivityId.GEM_COMPOUND_VALUE;
		ActivityTimeCfg timeCfg = ActivityUtil.getCurActivityTimeCfg(activityId);
		if (timeCfg == null) {
			// 活动已关闭
			player.sendError(protocol.getType(), Status.error.ACTIVITY_CLOSE);
			return true;
		}

		HPGemCompound req = protocol.parseProtocol(HPGemCompound.getDefaultInstance());
		int levelUpGemItemId = req.getLevelUpGemItemId();
		int costGemItemId = req.getCostGemItemId();

		// 配置检测
		ItemCfg levelUpGemCfg = ConfigManager.getInstance().getConfigByKey(ItemCfg.class, levelUpGemItemId);
		ItemCfg costGemCfg = ConfigManager.getInstance().getConfigByKey(ItemCfg.class, costGemItemId);
		if (levelUpGemCfg == null || costGemCfg == null) {
			// 宝石配置不存在
			player.sendError(protocol.getType(), Status.error.GEM_CFG_NOT_EXIST);
			return true;
		}
		if (!ItemUtil.isGem(levelUpGemItemId) || !ItemUtil.isGem(costGemItemId)) {
			// 不是宝石
			player.sendError(protocol.getType(), Status.error.GEM_CFG_NOT_EXIST);
			return true;
		}

		int oldlevel = ItemUtil.calcGemLevel(levelUpGemItemId, levelUpGemCfg.getType());
		int costGemLevel = ItemUtil.calcGemLevel(costGemItemId, costGemCfg.getType());
		GemCompoundCfg compoundCfg = ConfigManager.getInstance().getConfigByKey(GemCompoundCfg.class, oldlevel);
		if (compoundCfg == null || compoundCfg.getCostGemLevel() != costGemLevel) {
			// 合成配置不存在
			player.sendError(protocol.getType(), Status.error.GEM_COMPOUND_CFG_NULL);
			return true;
		}

		// 是否同一种宝石
		boolean isSame = (levelUpGemItemId == costGemItemId);
		int costMoney = isSame ? compoundCfg.getSameCostMoney() : compoundCfg.getDiffCostMoney();
		int costGem = isSame ? compoundCfg.getSameCostGem() : compoundCfg.getDiffCostGem();

		// 配置约定>=10000为金币，<10000为钻石
		int currencyType = (costMoney >= 10000) ? Const.buyMoneyType.MONEY_COIN_VALUE : Const.buyMoneyType.MONEY_GOLD_VALUE;
		// 检测钻石或金币是否足够
		ConsumeItems consumeItems = new ConsumeItems();
		if (currencyType == Const.buyMoneyType.MONEY_COIN_VALUE) {
			if (player.getCoin() < costMoney) {
				// 金币不足
				player.sendError(protocol.getType(), Status.error.COINS_NOT_ENOUGH);
				return true;
			}
			consumeItems.addChangeInfo(changeType.CHANGE_COIN, costMoney);
		} else {
			if (player.getGold() < costMoney) {
				// 钻石不足
				player.sendError(protocol.getType(), Status.error.GOLD_NOT_ENOUGH);
				return true;
			}
			consumeItems.addChangeInfo(changeType.CHANGE_GOLD, costMoney);
		}

		if(isSame){
			if(!player.getPlayerData().checkItemEnough(itemType.TOOL, levelUpGemItemId, 1 + costGem)){
				// 宝石不足
				player.sendError(protocol.getType(), Status.error.ITEM_NOT_ENOUGH);
				return true;
			}
			
			ItemEntity costItemEntity = player.getPlayerData().getItemByItemId(levelUpGemItemId);
			consumeItems.addChangeInfo(changeType.CHANGE_TOOLS, costItemEntity.getId(), levelUpGemItemId, 1+costGem);
			consumeItems.consumeTakeAffect(player, Action.GEM_COMPOUND);
		}else{
			if(!player.getPlayerData().checkItemEnough(itemType.TOOL, levelUpGemItemId, 1)
				|| !player.getPlayerData().checkItemEnough(itemType.TOOL, costGemItemId, costGem)) {
				// 宝石不足
				player.sendError(protocol.getType(), Status.error.ITEM_NOT_ENOUGH);
				return true;
			}
			
			ItemEntity costItemEntity = player.getPlayerData().getItemByItemId(levelUpGemItemId);
			consumeItems.addChangeInfo(changeType.CHANGE_TOOLS, costItemEntity.getId(), levelUpGemItemId, 1);
			costItemEntity = player.getPlayerData().getItemByItemId(costGemItemId);
			consumeItems.addChangeInfo(changeType.CHANGE_TOOLS, costItemEntity.getId(), costGemItemId, costGem);
			consumeItems.consumeTakeAffect(player, Action.GEM_COMPOUND);
		}
		
		// 发放升级后的宝石
		int newLevelItemId = levelUpGemCfg.getLevelUpItem();
		AwardItems awards = new AwardItems();
		awards.addItem(Const.itemType.TOOL_VALUE, newLevelItemId, 1);
		awards.rewardTakeAffectAndPush(player, Action.GEM_COMPOUND,2);
		// 推送宝石升级事件
		QuestEventBus.fireQuestEvent(QuestEventType.GEM_LEVEL, ItemUtil.calcGemLevel(newLevelItemId, levelUpGemCfg.getType()), player.getXid());
		// 同步活动剩余时间
		HPGemCompoundRet.Builder ret = HPGemCompoundRet.newBuilder();
		ret.setLeftTime(timeCfg.calcActivitySurplusTime());
		player.sendProtocol(Protocol.valueOf(HP.code.GEM_COMPOUND_S_VALUE, ret));
		
		BehaviorLogger.log4Platform(player, Action.GEM_COMPOUND, Params.valueOf("compoundType", currencyType),
				Params.valueOf("costMoney", costMoney), Params.valueOf("gemItemId", newLevelItemId));
		return true;
	}

	/**
	 * 根据道具Id判断是否是宝石
	 * 
	 * @param itemId
	 * @return
	 */
	@Deprecated
	public boolean isGem(int itemId) {
		int itemType = itemId / GsConst.ITEM_TYPE_BASE;
		if (itemType == Const.toolType.GEM_VALUE) {
			return true;
		}
		return false;
	}

	/**
	 * 根据道具Id计算宝石等级
	 * 
	 * @param itemId
	 * @return
	 */
	@Deprecated
	public int calcGemLevel(int itemId) {
		if (isGem(itemId)) {
			int gemLevel = itemId % 100;
			return gemLevel;
		}
		return -1;
	}
}
