package com.guaji.game.module.activity.exchangeShop;

import com.guaji.game.config.ActivityTimeCfg;
import com.guaji.game.config.ExchangeShop136Cfg;
import com.guaji.game.entity.StateEntity;
import com.guaji.game.item.AwardItems;
import com.guaji.game.item.AwardItems.Item;
import com.guaji.game.item.ConsumeItems;
import com.guaji.game.item.ItemInfo;
import com.guaji.game.log.BehaviorLogger.Action;
import com.guaji.game.player.Player;
import com.guaji.game.protocol.Activity2.DoExchange;
import com.guaji.game.protocol.Activity2.HPDoExchangeRet;
import com.guaji.game.protocol.Const;
import com.guaji.game.protocol.HP;
import com.guaji.game.protocol.Status;
import com.guaji.game.util.ActivityUtil;
import com.guaji.game.util.BuilderUtil;
import org.guaji.app.AppObj;
import org.guaji.config.ConfigManager;
import org.guaji.net.protocol.IProtocolHandler;
import org.guaji.net.protocol.Protocol;

import java.util.ArrayList;
import java.util.List;


/**
 * 兑换所
 */
public class DoExchangeShopHandler implements IProtocolHandler {
    @Override
    public boolean onProtocol(AppObj appObj, Protocol protocol) {

        Player player = (Player) appObj;
        // 检测活动是否开放
        int activityId = Const.ActivityId.EXCHANGE_SHOP_VALUE;
        ActivityTimeCfg timeCfg = ActivityUtil.getCurActivityTimeCfg(activityId);

        if (timeCfg == null) {
            // 活动已关闭
            player.sendError(protocol.getType(), Status.error.ACTIVITY_CLOSE);
            return true;
        }

        if (timeCfg.isEnd()) {
            // 活动已结束
            player.sendError(protocol.getType(), Status.error.ACTIVITY_END_VALUE);
            return true;
        }

        DoExchange request = protocol.parseProtocol(DoExchange.getDefaultInstance());

        //要兑换的道具id
        String exchangeId = request.getExchangeId();
        //要兑换的道具个数
        int times = request.getExchangeTimes();

        if (times == 0 || exchangeId == null) {
            return true;
        }

        ExchangeShop136Cfg exchangeShop136Cfg = ConfigManager.getInstance().getConfigByKey(ExchangeShop136Cfg.class, exchangeId);

        List<ItemInfo> consumeInfo = new ArrayList<ItemInfo>();
        //根据兑换次数修正兑换消耗数目
        for (ItemInfo item : exchangeShop136Cfg.getExchangeConsumeInfo()) {
            ItemInfo newItem = item.clone();
            newItem.setQuantity(newItem.getQuantity() * times);
            consumeInfo.add(newItem);
        }


        ExchangeShopStatus exchangeShopStatus = ActivityUtil.getActivityStatus(
                player.getPlayerData(), activityId, timeCfg.getStageId(), ExchangeShopStatus.class);

        // 获取无需重置的副将记录
        StateEntity stateEntity = player.getPlayerData().getStateEntity();

        int alreadyTimes = 0;

        if (exchangeShop136Cfg.getIsReset()) {
            alreadyTimes = exchangeShopStatus.getExchangeCount(exchangeId);
        } else {
            alreadyTimes = stateEntity.getExchangeCountById(exchangeId);
        }

        //有限制
        if (exchangeShop136Cfg.getMaxExchangeTimes() != 0) {
            if (times + alreadyTimes > exchangeShop136Cfg.getMaxExchangeTimes()) {
                player.sendError(protocol.getType(), Status.error.ITEM_NOT_ENOUGH_VALUE);

                return true;
            } else//兑换
            {
                ConsumeItems consumeItems = new ConsumeItems();
                if (!consumeItems.addConsumeInfo(player.getPlayerData(), consumeInfo)) {
                    player.sendError(protocol.getType(), Status.error.ITEM_NOT_ENOUGH_VALUE);
                    return true;
                }

                if (!consumeItems.checkConsume(player, protocol.getType())) {
                    return true;
                }

                consumeItems.consumeTakeAffect(player, Action.EXCHANGE_SEVEN);
            }
        } else {
            //兑换
            ConsumeItems consumeItems = new ConsumeItems();
            if (!consumeItems.addConsumeInfo(player.getPlayerData(), consumeInfo)) {
                player.sendError(protocol.getType(), Status.error.ITEM_NOT_ENOUGH_VALUE);
                return true;
            }

            if (!consumeItems.checkConsume(player, protocol.getType())) {
                return true;
            }

            consumeItems.consumeTakeAffect(player, Action.EXCHANGE_SEVEN);
        }

        AwardItems awardItems = new AwardItems();
        //根据兑换次数修正兑换消耗数目
        for (Item item : exchangeShop136Cfg.getExchangeAwardInfo().getAwardItems()) {
            Item newItem = item.clone();
            newItem.setCount(newItem.getCount() * times);
            awardItems.addItem(newItem);
        }

        //记录领取日志改
        awardItems.rewardTakeAffectAndPush(player, Action.EXCHANGE_SEVEN, 2);

        if (exchangeShop136Cfg.getIsReset()) {
            exchangeShopStatus.modifyExchangeCount(exchangeId, times + alreadyTimes);
            player.getPlayerData().updateActivity(Const.ActivityId.EXCHANGE_SHOP_VALUE, timeCfg.getStageId(), true);
        } else {
            stateEntity.addExchange(exchangeId, times + alreadyTimes);
            stateEntity.notifyUpdate(true);
        }


        int lastTime = timeCfg.calcActivitySurplusTime();

        //发送服务器礼包协议
        player.sendProtocol(Protocol.valueOf(HP.code.EXCHANGE_SHOP_INFO_S_VALUE, BuilderUtil.genExchangeShopStatus(lastTime, exchangeShopStatus,stateEntity.getExchangeMap())));

        HPDoExchangeRet.Builder builder = HPDoExchangeRet.newBuilder();
        builder.setExchangeId(exchangeId);
        builder.setExchangeTimes(exchangeShopStatus.getExchangeCount(exchangeId));
        player.sendProtocol(Protocol.valueOf(HP.code.DO_EXCHANGE_SHOP_S, builder));
        return true;
    }
}
