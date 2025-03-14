package com.guaji.game.module.activity.salePacket;

import java.util.Date;

import org.guaji.app.AppObj;
import org.guaji.net.protocol.Protocol;
import org.guaji.net.protocol.IProtocolHandler;

import com.guaji.game.player.Player;
import com.guaji.game.util.ActivityUtil;
import com.guaji.game.util.BuilderUtil;
import com.guaji.game.util.GsConst;
import com.guaji.game.protocol.Const;
import com.guaji.game.protocol.HP;
import com.guaji.game.protocol.Status;

/**
 * 获取折扣礼包活动信息
 */

public class SalePacketInfoHandler implements IProtocolHandler {
    @Override
    public boolean onProtocol(AppObj appObj, Protocol protocol) {
        Player player = (Player) appObj;

        // 活动结束时间判断
        Date registerDate = player.getPlayerData().getPlayerEntity().getCreateTime();
        int mergeTime = player.getPlayerData().getPlayerEntity().getMergeTime();
        if (ActivityUtil.calcSalePacketActivityStatus(registerDate, mergeTime) == GsConst.SalePacketActivity.STATUS_CLOSE) {
            player.sendError(protocol.getType(), Status.error.ACTIVITY_CLOSE_VALUE);

            return true;
        }

        int lastTime = ActivityUtil.clacSalePacketSurplusTime(registerDate,mergeTime);

        SalePacketStatus salePacketStatus = ActivityUtil.getSalePacketStatus(player.getPlayerData());

        if (salePacketStatus == null) {
            return true;
        }

        // 2016-6-23 sunshengxiang口头传达修改不做第二天刷新
        if (salePacketStatus.refreshPacketTable()) {
            player.getPlayerData().updateActivity(Const.ActivityId.SALE_PACKET_VALUE, 0, true);
        }

        // 发送服务器礼包协议
        player.sendProtocol(Protocol.valueOf(HP.code.SALE_PACKET_INFO_S,
                BuilderUtil.genSalePacketStatus(lastTime, salePacketStatus)));
        return true;
    }
}
