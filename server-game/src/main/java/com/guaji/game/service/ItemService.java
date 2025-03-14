package com.guaji.game.service;

import org.guaji.app.AppObj;
import org.guaji.config.ConfigManager;
import org.guaji.msg.Msg;
import org.guaji.net.protocol.Protocol;

import com.guaji.game.config.ItemCfg;
import com.guaji.game.entity.ItemEntity;
import com.guaji.game.item.AwardItems;
import com.guaji.game.item.ConsumeItems;
import com.guaji.game.log.BehaviorLogger;
import com.guaji.game.player.Player;

import com.guaji.game.protocol.Const;
import com.guaji.game.protocol.ItemOpr;
import com.guaji.game.protocol.Status;

public class ItemService extends GameService
{
  public boolean onMessage(AppObj appObj, Msg msg)
  {
    return false;
  }
  
  public boolean onProtocol(Player player, Protocol protocol)
  {
    if (protocol.checkType(3003))
    {
      onItemSell(player, (ItemOpr.HPItemSell)protocol.parseProtocol(ItemOpr.HPItemSell.getDefaultInstance()));
      return true;
    }
    return false;
  }
  
  private void onItemSell(Player player, ItemOpr.HPItemSell params)
  {
    int itemId = params.getItemId();
    int count = params.getCount();
    if ((itemId <= 0) || (count <= 0))
    {
      player.sendError(3001, 10);
      return;
    }
    ItemEntity itemEntity = player.getPlayerData().getItemByItemId(itemId);
    if ((itemEntity == null) || (itemEntity.getItemCount() < count))
    {
      player.sendError(3001, 3002);
      return;
    }
    ItemCfg itemCfg = (ItemCfg)ConfigManager.getInstance().getConfigByKey(ItemCfg.class, Integer.valueOf(itemEntity.getItemId()));
    if (itemCfg == null)
    {
      player.sendError(3001, 11);
      return;
    }
    if (itemCfg.getPrice() <= 0)
    {
      player.sendError(3001, Status.error.ITEM_SELL_NOT_ALLOW);
      return;
    }
    ConsumeItems.valueOf(Const.changeType.CHANGE_TOOLS, itemEntity.getId(), itemEntity.getItemId(), count).consumeTakeAffect(player, BehaviorLogger.Action.ITEM_SELL);
    AwardItems awardItems = new AwardItems();
    awardItems.addCoin(itemCfg.getPrice());
    awardItems.rewardTakeAffectAndPush(player, BehaviorLogger.Action.ITEM_SELL,1);
  }
}
