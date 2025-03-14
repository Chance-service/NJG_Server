package com.guaji.game.module.activity.salePacket;


import java.util.Date;

import org.guaji.app.AppObj;
import org.guaji.config.ConfigManager;
import org.guaji.net.protocol.Protocol;
import org.guaji.net.protocol.IProtocolHandler;

import com.guaji.game.protocol.Activity.GetSalePacketAward;
import com.guaji.game.protocol.Activity.HPGetSalePacketAward;
import com.guaji.game.config.SalePacketCfg;
import com.guaji.game.item.AwardItems;
import com.guaji.game.log.BehaviorLogger.Action;
import com.guaji.game.player.Player;
import com.guaji.game.util.ActivityUtil;
import com.guaji.game.util.GsConst;
import com.guaji.game.protocol.Const;
import com.guaji.game.protocol.HP;
import com.guaji.game.protocol.Status;

/**
 * 领取打折礼包
 */
public class SalePacketGetHandler implements IProtocolHandler {
    @Override
    public boolean onProtocol(AppObj appObj, Protocol protocol) {

        Player player = (Player) appObj;


        //活动结束时间判断
        Date registerDate = player.getPlayerData().getPlayerEntity().getCreateTime();
        if (ActivityUtil.calcSalePacketActivityStatus(registerDate, player.getPlayerData().getPlayerEntity().getMergeTime()) == GsConst.SalePacketActivity.STATUS_CLOSE) {
            player.sendError(protocol.getType(), Status.error.ACTIVITY_CLOSE_VALUE);
            return true;
        }

        SalePacketStatus salePacketStatus = ActivityUtil.getSalePacketStatus(player.getPlayerData());

        if (salePacketStatus == null) {
            return true;
        }

        GetSalePacketAward request = protocol.parseProtocol(GetSalePacketAward.getDefaultInstance());

        int id = request.getPacketId();


        SalePacketCfg cfg = ConfigManager.getInstance().getConfigByKey(SalePacketCfg.class, id);
        if (cfg == null) {
            player.sendError(protocol.getType(), Status.error.CONFIG_NOT_FOUND);
            return true;
        }


        if (!salePacketStatus.getPacket(id)) {
            player.sendError(protocol.getType(), Status.error.SALE_PACKET_GET_AWARD);
            return false;
        }
        //更新成可购买状态
        salePacketStatus.getInfo().get(id).setState(0);
        AwardItems awardItems = AwardItems.valueOf(cfg.getSalePacket());//获取礼包
        awardItems.rewardTakeAffectAndPush(player, Action.SALE_SALEPACKED_REWARD, 2);//记录领取日志
        player.getPlayerData().updateActivity(Const.ActivityId.SALE_PACKET_VALUE, 0, true);

        //获取首充礼包的实体信息并设置新的领取状态
        HPGetSalePacketAward.Builder builder = HPGetSalePacketAward.newBuilder();
        builder.setPacketId(id);//礼包id
        builder.setBuyTime(salePacketStatus.getInfo().get(id).getBuyTime());
        builder.setState(salePacketStatus.getInfo().get(id).getState());
        //发送服务器礼包协议
        player.sendProtocol(Protocol.valueOf(HP.code.SALE_PACKET_GET_AWARD_S, builder));

        return true;
    }
}
