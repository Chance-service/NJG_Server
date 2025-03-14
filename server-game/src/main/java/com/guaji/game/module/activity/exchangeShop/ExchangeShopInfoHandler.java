package com.guaji.game.module.activity.exchangeShop;

import com.guaji.game.config.ActivityTimeCfg;
import com.guaji.game.entity.StateEntity;
import com.guaji.game.player.Player;
import com.guaji.game.protocol.Const;
import com.guaji.game.protocol.HP;
import com.guaji.game.protocol.Status;
import com.guaji.game.util.ActivityUtil;
import com.guaji.game.util.BuilderUtil;
import org.guaji.app.AppObj;
import org.guaji.net.protocol.IProtocolHandler;
import org.guaji.net.protocol.Protocol;

/**
 * 兑换所
 */
public class ExchangeShopInfoHandler implements IProtocolHandler {
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

        ExchangeShopStatus exchangeShopStatus = ActivityUtil.getActivityStatus(
                player.getPlayerData(), activityId, timeCfg.getStageId(), ExchangeShopStatus.class);

        if (exchangeShopStatus == null) {
            player.sendError(protocol.getType(), Status.error.DATA_NOT_FOUND_VALUE);
            return true;
        }

        int lastTime = timeCfg.calcActivitySurplusTime();

        // 获取无需重置的副将记录
        StateEntity stateEntity = player.getPlayerData().getStateEntity();

        //发送服务器礼包协议
        player.sendProtocol(Protocol.valueOf(HP.code.EXCHANGE_SHOP_INFO_S_VALUE, BuilderUtil.genExchangeShopStatus(lastTime, exchangeShopStatus, stateEntity.getExchangeMap())));

        return true;
    }

}