package com.guaji.game.module.activity.wordsexchangespecial;

import org.guaji.app.AppObj;
import org.guaji.config.ConfigManager;
import org.guaji.net.protocol.Protocol;
import org.guaji.net.protocol.IProtocolHandler;

import com.guaji.game.protocol.Const.changeType;
import com.guaji.game.protocol.ItemOpr.HPGongceWordCycle;
import com.guaji.game.config.ItemCfg;
import com.guaji.game.entity.ItemEntity;
import com.guaji.game.item.AwardItems;
import com.guaji.game.item.ConsumeItems;
import com.guaji.game.log.BehaviorLogger.Action;
import com.guaji.game.player.Player;
import com.guaji.game.protocol.Status;

public class WordsExchangeCycleHandler implements IProtocolHandler {

	@Override
	public boolean onProtocol(AppObj appObj, Protocol protocol) {
		Player player = (Player) appObj;
		
		HPGongceWordCycle params = protocol.parseProtocol(HPGongceWordCycle.getDefaultInstance());
		int itemId = params.getItemId();
		ItemEntity itemEntity = player.getPlayerData().getItemByItemId(itemId);
		if(itemEntity == null || itemEntity.getItemCount() == 0) {
			player.sendError(protocol.getType(), Status.error.ITEM_NOT_ENOUGH_VALUE);
			return true;
		}
		
		ItemCfg itemCfg = ConfigManager.getInstance().getConfigByKey(ItemCfg.class, itemEntity.getItemId());
		if(itemCfg == null) {
			player.sendError(protocol.getType(), Status.error.CONFIG_NOT_FOUND_VALUE);
			return true;
		}
		
		ConsumeItems consumeItems = ConsumeItems.valueOf(changeType.CHANGE_TOOLS,itemEntity.getId(),itemId,1);
		consumeItems.consumeTakeAffect(player, Action.WORDS_EXCHANGE_SPECIAL_CYCLE);
		
		AwardItems awardItems = new AwardItems();
		awardItems.addCoin(itemCfg.getPrice());
		awardItems.rewardTakeAffectAndPush(player, Action.WORDS_EXCHANGE_SPECIAL_CYCLE,1);
		
		return true;
	}

}
