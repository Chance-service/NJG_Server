package com.guaji.game.module.activity.activity177;

import java.util.Map;

import org.guaji.app.AppObj;
import org.guaji.config.ConfigManager;
import org.guaji.net.protocol.IProtocolHandler;
import org.guaji.net.protocol.Protocol;

import com.guaji.game.config.ActivityTimeCfg;
import com.guaji.game.config.FailedGift177Cfg;
import com.guaji.game.config.RechargeConfig;
import com.guaji.game.config.RechargeConfig.RechargeItem;
import com.guaji.game.player.Player;
import com.guaji.game.protocol.Activity5.Activity177FailedGiftReq;
import com.guaji.game.protocol.Activity5.Activity177FailedGiftResp;
import com.guaji.game.protocol.Activity5.FailedGiftInfo;
import com.guaji.game.protocol.Const;
import com.guaji.game.protocol.HP;
import com.guaji.game.protocol.Shop.GoodsNotice;
import com.guaji.game.protocol.Status;
import com.guaji.game.util.ActivityUtil;


public class Activity177Handler implements IProtocolHandler {
	static final int failedGift_SyncInfo = 0;
	static final int failedGift_verifyGift = 1;

    @Override
    public boolean onProtocol(AppObj appObj, Protocol protocol) {
    	// 改儲值購買,這裡僅作禮包驗證
        Player player = (Player) appObj;
        int activityId = Const.ActivityId.ACTIVITY177_Failed_Gift_VALUE;
        ActivityTimeCfg timeConfig = ActivityUtil.getCurActivityTimeCfg(activityId);
        
		if (timeConfig == null || player == null || (!timeConfig.isActiveToEnd())) {
			// 活动已关闭
			player.sendError(protocol.getType(), Status.error.ACTIVITY_CLOSE);
			return true;
		}
		
        Activity177FailedGiftReq req = protocol.parseProtocol(Activity177FailedGiftReq.getDefaultInstance());
        
        int action = req.getAction();
        
       
        Activity177Status status = ActivityUtil.getActivityStatus(player.getPlayerData(), activityId, timeConfig.getStageId(), Activity177Status.class);
        
        if (status == null) {
        	player.sendError(protocol.getType(), Status.error.DATA_NOT_FOUND_VALUE);
        }
        
		// 业务分支处理
		switch (action) {
		case failedGift_SyncInfo:
			if (status.checkRestGift()) {
				player.getPlayerData().updateActivity(activityId, timeConfig.getStageId());
			}
			SyncInfo(timeConfig,action,player,status);
			break;
		case failedGift_verifyGift:
			VerifyGift(protocol,timeConfig,action,player,status);
			break;
		default:
			player.sendError(protocol.getType(), Status.error.PARAMS_INVALID_VALUE);
			break;
		}
		return true;
        
    }
    
	/**
	 * 同步資訊
	 * @param action
	 * @param player
	 * @param timeCfg
	 * @param status
	 */
	public static void SyncInfo(ActivityTimeCfg timeConfig,int action,Player player, Activity177Status status) {
		Activity177FailedGiftResp.Builder builder = getBuilder(timeConfig,player,action,status);
		player.sendProtocol(Protocol.valueOf(HP.code.ACTIVITY177_FAILED_GIFT_S, builder));
	}
	
	private static Activity177FailedGiftResp.Builder getBuilder(ActivityTimeCfg timeConfig,Player player,int action,Activity177Status status) {
		// 返回包
		Activity177FailedGiftResp.Builder response = Activity177FailedGiftResp.newBuilder();
		
		response.setAction(action);
		
//		if (action == failedGift_SyncInfo) {
		FailedGift177Cfg cfgItem = ConfigManager.getInstance().getConfigByKey(FailedGift177Cfg.class,status.getTriggerCfgId());
		if (cfgItem != null) {
			if ((player.getCurMapId() >= cfgItem.getMinstage()) && (player.getCurMapId() <= cfgItem.getMaxstage())) {
				if ((status.calcGiftSurplusTime(cfgItem.getHours()) > 0)) {
					 FailedGiftInfo.Builder info = FailedGiftInfo.newBuilder();
					 info.setCfgId(cfgItem.getId());
					 info.setIsGot(status.Isbuy());
					 response.addGiftInfo(info);
					 response.setLimitDate(status.calcGiftSurplusTime(cfgItem.getHours()));
				}
	        }
		 }
//		} else if (action == failedGift_verifyGift) {
			
//		}
		
		return response;
	}
	
	private static void VerifyGift(Protocol protocol ,ActivityTimeCfg timeConfig,int action,Player player, Activity177Status status) {
        
		Activity177FailedGiftReq req = protocol.parseProtocol(Activity177FailedGiftReq.getDefaultInstance());
		
		if (req.getCfgId() != status.getTriggerCfgId()) {
			player.sendError(protocol.getType(), Status.error.PARAMS_INVALID);
			return ;
		}
		
		FailedGift177Cfg cfg = ConfigManager.getInstance().getConfigMap(FailedGift177Cfg.class).get(req.getCfgId());
		
        if (cfg == null) {
            player.sendError(protocol.getType(), Status.error.CONFIG_NOT_FOUND);
            return ;
        }
        
        int userLevel = player.getCurMapId();
        int goodsId = cfg.getCost();
        
        if (userLevel < cfg.getMinstage() || userLevel > cfg.getMaxstage()) {
        	player.sendError(protocol.getType(), Status.error.PARAMS_INVALID);
           return;
        }

        //验证等级条件
        if (goodsId == 0) {
            player.sendError(protocol.getType(), Status.error.PARAMS_INVALID);
            return ;
        }
       
		String platform = player.getPlatform(); //使用自身平台
		RechargeConfig rechargeConfig = RechargeConfig.getRechargeConfig(platform);

		if (rechargeConfig == null) {
		    //logger.info("recharge config cannot found, platform: {}", new Object[] { platform });
			throw new RuntimeException("recharge config cannot found");
					
		}

		Map<Integer,RechargeItem> allRechargeItem =rechargeConfig.getAllrechargeCfg();
		
		if (allRechargeItem == null || !allRechargeItem.containsKey(goodsId)) {
            player.sendError(protocol.getType(), Status.error.CONFIG_NOT_FOUND);
            return;
		}
        if (status.Isbuy()) {
            // 已購買
            player.sendError(protocol.getType(), Status.error.ACTIVITY_AWARDS_HAS_GOT);
            return;
        }
        
        if (status.calcGiftSurplusTime(cfg.getHours()) <= 0) {
            player.sendError(protocol.getType(), Status.error.ACTIVITY_CLOSE);
            return;
        }
        
        //SyncInfo(timeConfig,action,player,status);
        
        GoodsNotice.Builder builder = GoodsNotice.newBuilder();
        builder.setGoodsId(goodsId);
        
        player.sendProtocol(Protocol.valueOf(HP.code.GOODS_VERIFY_S, builder));
        
	}
}
