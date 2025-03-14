package com.guaji.game.module.activity.activity186;

import java.util.Map;

import org.guaji.app.AppObj;
import org.guaji.config.ConfigManager;
import org.guaji.net.protocol.IProtocolHandler;
import org.guaji.net.protocol.Protocol;

import com.guaji.game.config.ActivityGiftAward186Cfg;
import com.guaji.game.config.ActivityTimeCfg;
import com.guaji.game.config.RechargeConfig;
import com.guaji.game.config.RechargeConfig.RechargeItem;
import com.guaji.game.player.Player;
import com.guaji.game.protocol.Activity4.Activity132LevelGiftBuyReq;
import com.guaji.game.protocol.Const;
import com.guaji.game.protocol.HP;
import com.guaji.game.protocol.Shop.GoodsNotice;
import com.guaji.game.protocol.Status;
import com.guaji.game.util.ActivityUtil;


public class Activity186BuyHandler implements IProtocolHandler {

    @Override
    public boolean onProtocol(AppObj appObj, Protocol protocol) {
    	// 改儲值購買,這裡僅作禮包驗證
        Player player = (Player) appObj;
        int activityId = Const.ActivityId.ACTIVITY186_JumpGift_VALUE;
        ActivityTimeCfg activityTimeCfg = ActivityUtil.getCurActivityTimeCfg(activityId);
        if (activityTimeCfg == null) {
            // 活动已关闭
            player.sendError(protocol.getType(), Status.error.ACTIVITY_CLOSE);
            return true;
        }
        Activity132LevelGiftBuyReq req = protocol.parseProtocol(Activity132LevelGiftBuyReq.getDefaultInstance());
        
        ActivityGiftAward186Cfg cfg = ConfigManager.getInstance().getConfigByKey(ActivityGiftAward186Cfg.class,req.getCfgId());
        
        if (cfg == null) {
            player.sendError(protocol.getType(), Status.error.CONFIG_NOT_FOUND);
            return true;
        }
                
        int goodsId = cfg.getId();
                
        String platform = player.getPlatform(); //使用自身平台
		RechargeConfig rechargeConfig = RechargeConfig.getRechargeConfig(platform);

		if (rechargeConfig == null) {
		    //logger.info("recharge config cannot found, platform: {}", new Object[] { platform });
			throw new RuntimeException("recharge config cannot found");
		}
		
		
		Map<Integer,RechargeItem> allRechargeItem =rechargeConfig.getAllrechargeCfg();
		
		if (allRechargeItem == null || !allRechargeItem.containsKey(goodsId)) {
            player.sendError(protocol.getType(), Status.error.CONFIG_NOT_FOUND);
            return true;
		}
		
		
		Activity186Status status = ActivityUtil.getActivityStatus(player.getPlayerData(), activityId, activityTimeCfg.getStageId(), Activity186Status.class);
        if (status.isAlreadyGot(req.getCfgId())) {
            // 活动奖励已领取
            player.sendError(protocol.getType(), Status.error.ACTIVITY_AWARDS_HAS_GOT);
            return true;
        }
                
        GoodsNotice.Builder builder = GoodsNotice.newBuilder();
        builder.setGoodsId(goodsId);
        
        player.sendProtocol(Protocol.valueOf(HP.code.GOODS_VERIFY_S, builder));
        
        return true;
    }
}
