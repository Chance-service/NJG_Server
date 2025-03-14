package com.guaji.game.module.activity.activity179;

import java.util.Map;

import org.guaji.app.AppObj;
import org.guaji.config.ConfigManager;
import org.guaji.net.protocol.IProtocolHandler;
import org.guaji.net.protocol.Protocol;

import com.guaji.game.config.ActivityTimeCfg;
import com.guaji.game.config.RechargeConfig;
import com.guaji.game.config.RechargeConfig.RechargeItem;
import com.guaji.game.config.ReleaseStepGiftCfg;
import com.guaji.game.player.Player;
import com.guaji.game.protocol.Activity5.GiftReq;
import com.guaji.game.protocol.Activity5.GiftResp;
import com.guaji.game.protocol.Const;
import com.guaji.game.protocol.HP;
import com.guaji.game.protocol.Shop.GoodsNotice;
import com.guaji.game.protocol.Status;
import com.guaji.game.util.ActivityUtil;

/**
 * 免費召喚領取協定
 */
public class Activity179Handler implements IProtocolHandler {
	static final int Sync = 0;
	static final int Buy_Rward = 1;

	@Override
	public boolean onProtocol(AppObj appObj, Protocol protocol) {
		// TODO Auto-generated method stub
		
		Player player = (Player) appObj;

		
		// 检测活动是否开放
		int activityId = Const.ActivityId.ACTIVITY179_Step_Gift_VALUE;
		ActivityTimeCfg timeConfig = ActivityUtil.getCurActivityTimeCfg(activityId);
		if (timeConfig == null || player == null) {
			// 活动已关闭
			player.sendError(protocol.getType(), Status.error.ACTIVITY_CLOSE);
			return true;
		}

		// 解析接受到的数据
		GiftReq request = protocol.parseProtocol(GiftReq.getDefaultInstance());

		int action = request.getAction();
				
//		// 数据人错误
//		if ( action < 0 || action > 1) {
//			// 无效参数
//			player.sendError(protocol.getType(), Status.error.PARAMS_INVALID_VALUE);
//			return true;
//		}
//		
//		if (action != Sync) {
//			if (!FunctionUnlockCfg.checkUnlock(player,null,Const.FunctionType.free1500_Unlock)){
//				player.sendError(protocol.getType(), Status.error.CONDITION_NOT_ENOUGH);
//				return true;
//			}
//		}
				
		int stageId = timeConfig.getStageId();
		
		Activity179Status status = ActivityUtil.getActivityStatus(player.getPlayerData(), activityId, stageId, Activity179Status.class);
		
		if (status == null) {
			player.sendError(protocol.getType(), Status.error.DATA_NOT_FOUND_VALUE);
			return true;
		}
				
//		if (status.getTodayCount() == -1) {
//			// 活動未觸發
//			player.sendError(protocol.getType(), Status.error.ACTIVITY_CLOSE_VALUE);
//			return true;
//		}
		
		// 业务分支处理
		switch (action) {
		case Sync:
			SyncInfo(action,player,status);
			break;
		case  Buy_Rward:
			BuyRward(protocol,action,player,status);
			break;
		default:
			player.sendError(protocol.getType(), Status.error.PARAMS_INVALID_VALUE);
			break;
		}
		return true;
	}
	
	private static void BuyRward(Protocol protocol,int action,Player player, Activity179Status status) {
					
		if (status.getTakeGoodsId() >= ReleaseStepGiftCfg.getMaxGoodsId()) {
			// 已經領了或全部領完
			player.sendError(protocol.getType(), Status.error.PARAMS_INVALID_VALUE);
			return;
		}
		
		int takeId = (status.getTakeGoodsId() == 0)? ReleaseStepGiftCfg.getMinGoodsId():status.getTakeGoodsId() + 1;
		
		ReleaseStepGiftCfg giftCfg = ConfigManager.getInstance().getConfigByKey(ReleaseStepGiftCfg.class, takeId);
		
		if (giftCfg == null) {
			player.sendError(protocol.getType(), Status.error.CONFIG_NOT_FOUND_VALUE);
			return;
		}
		
        int goodsId = giftCfg.getId();
        
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
		                
        GoodsNotice.Builder builder = GoodsNotice.newBuilder();
        builder.setGoodsId(goodsId);
        
        player.sendProtocol(Protocol.valueOf(HP.code.GOODS_VERIFY_S, builder));

//		status.setTakeGoodsId(takeId);
//		int activityId = Const.ActivityId.ACTIVITY179_Step_Gift_VALUE;
//		ActivityTimeCfg timeCfg = ActivityUtil.getCurActivityTimeCfg(activityId);
//		player.getPlayerData().updateActivity(activityId, timeCfg.getStageId());
		
//		String allaward = giftCfg.getAwards();
//		// 下发奖励
//		AwardItems awards = AwardItems.valueOf(allaward);
//		awards.rewardTakeAffectAndPush(player, Action.ACTIVITY167_FREE_SUMMON, 2);
//		
//		GiftResp.Builder response =  getBuilder(action,status);
//		
//		player.sendProtocol(Protocol.valueOf(HP.code.ACTIVITY179_STEP_GIFT_S, response));
//		
//		BehaviorLogger.log4Service(player, Source.USER_OPERATION, Action.ACTIVITY167_FREE_SUMMON,
//				Params.valueOf("takeID",status.getTakeGoodsId()),
//				Params.valueOf("allaward", allaward));
	}
	
	/**
	 * 同步資訊
	 * @param action
	 * @param player
	 * @param timeCfg
	 * @param status
	 */
	public static void SyncInfo(int action,Player player, Activity179Status status) {
		GiftResp.Builder builder = getBuilder(action,status);
		player.sendProtocol(Protocol.valueOf(HP.code.ACTIVITY179_STEP_GIFT_S, builder));
	}
	
	private static GiftResp.Builder getBuilder(int action,Activity179Status status) {
		// 返回包
		GiftResp.Builder response = GiftResp.newBuilder();
		
		response.setAction(action);
		response.setTakeId(status.getTakeGoodsId());
		
		return response;
	}

}
