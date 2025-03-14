package com.guaji.game.service;

import org.guaji.app.AppObj;
import org.guaji.config.ConfigManager;
import org.guaji.msg.Msg;
import org.guaji.net.protocol.Protocol;

import com.guaji.game.config.EquipGodCreateCfg;
import com.guaji.game.entity.EquipEntity;
import com.guaji.game.item.ConsumeItems;
import com.guaji.game.log.BehaviorLogger;
import com.guaji.game.player.Player;
import com.guaji.game.util.BuilderUtil;
import com.guaji.game.util.PlayerUtil;
import com.guaji.game.protocol.Const;
import com.guaji.game.protocol.EquipOpr;
import com.guaji.game.protocol.Reward;
import com.guaji.game.protocol.Status;

public class EquipService extends GameService
{
  public boolean onMessage(AppObj appObj, Msg msg)
  {
    return false;
  }
  
  public boolean onProtocol(Player player, Protocol protocol)
  {
    return false;
  }
  
  private void onEquipSpecialCreate(Player player, int cfgId)
  {
    EquipGodCreateCfg equipGodCreateCfg = (EquipGodCreateCfg)ConfigManager.getInstance().getConfigByKey(EquipGodCreateCfg.class, Integer.valueOf(cfgId));
    if (equipGodCreateCfg == null)
    {
      player.sendError(5029, 11);
      return;
    }
    if ((player.getLevel() < equipGodCreateCfg.getMinLevel()) || (player.getLevel() > equipGodCreateCfg.getMaxLevel()))
    {
      player.sendError(5029, Status.error.LEVEL_NOT_LIMIT);
      return;
    }
    ConsumeItems consumeItems = ConsumeItems.valueOf(Const.changeType.CHANGE_REPUTATION_VALUE, equipGodCreateCfg.getCostreputation());
    consumeItems.addChangeInfo(Const.changeType.CHANGE_SMELT_VALUE, equipGodCreateCfg.getCostSmelt());
    if (player.getPlayerData().getPlayerEntity().getReputationValue() < equipGodCreateCfg.getCostreputation()) {
      return;
    }
    if (!consumeItems.checkConsume(player, 5029)) {
      return;
    }
    consumeItems.consumeTakeAffect(player, BehaviorLogger.Action.EQUIP_SPECIAL_CREATE);
    EquipEntity equipEntity = null;
    if (equipGodCreateCfg.isGodAttrDouble()) {
      equipEntity = player.increaseEquip(equipGodCreateCfg.getEquipId(), true, true, BehaviorLogger.Action.EQUIP_SPECIAL_CREATE);
    } else {
      equipEntity = player.increaseEquip(equipGodCreateCfg.getEquipId(), false, true, BehaviorLogger.Action.EQUIP_SPECIAL_CREATE);
    }
    player.getPlayerData().syncEquipInfo(new Long[] { Long.valueOf(equipEntity.getId()) });
    Reward.RewardInfo.Builder rewardBuilder = Reward.RewardInfo.newBuilder();
    rewardBuilder.addShowItems(BuilderUtil.genEuiqpRewardBuilder(equipEntity));
    PlayerUtil.pushRewards(player, rewardBuilder,1);
    
    player.sendProtocol(Protocol.valueOf(5030, EquipOpr.HPEquipSpecialCreateRet.newBuilder().setCfgId(cfgId)));
  }
}
