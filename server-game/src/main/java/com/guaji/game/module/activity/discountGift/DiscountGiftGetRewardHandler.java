package com.guaji.game.module.activity.discountGift;

import org.guaji.app.AppObj;
import org.guaji.config.ConfigManager;
import org.guaji.net.protocol.Protocol;
import org.guaji.net.protocol.IProtocolHandler;

import com.guaji.game.protocol.Activity2.HPDiscountGetRewardReq;
import com.guaji.game.protocol.Activity2.HPDiscountGetRewardRes;
import com.guaji.game.config.ActivityTimeCfg;
import com.guaji.game.config.DiscountGiftCfg;
import com.guaji.game.item.AwardItems;
import com.guaji.game.log.BehaviorLogger;
import com.guaji.game.log.BehaviorLogger.Action;
import com.guaji.game.log.BehaviorLogger.Params;
import com.guaji.game.log.BehaviorLogger.Source;
import com.guaji.game.player.Player;
import com.guaji.game.util.ActivityUtil;
import com.guaji.game.util.TapDBUtil.TapDBSource;
import com.guaji.game.protocol.Const;
import com.guaji.game.protocol.HP;
import com.guaji.game.protocol.Status;

/**
 * 领取打折礼包
 *
 */
public class DiscountGiftGetRewardHandler implements IProtocolHandler {
	@Override
	public boolean onProtocol(AppObj appObj, Protocol protocol) {

		Player player = (Player) appObj;

		DiscountGiftData data = ActivityUtil.getDiscountGiftData(player.getPlayerData());

		HPDiscountGetRewardReq request = protocol.parseProtocol(HPDiscountGetRewardReq.getDefaultInstance());
		int goodsId = request.getGoodsId();
		
		ActivityTimeCfg activityTimeCfg = ActivityUtil.getCurActivityTimeCfg(Const.ActivityId.DISCOUNT_GIFT_VALUE);
		if (activityTimeCfg == null) {
			player.sendError(protocol.getType(), Status.error.ACTIVITY_CLOSE);
			return false;
		}

		DiscountGiftCfg cfg = ConfigManager.getInstance().getConfigByKey(DiscountGiftCfg.class, goodsId);
		if (cfg == null) {
			player.sendError(protocol.getType(), Status.error.CONFIG_NOT_FOUND);
			return true;
		}

		OneDiscountGiftData oneData = data.getOneDiscountGiftData(goodsId);
		if (oneData == null) {
			player.sendError(protocol.getType(), Status.error.PARAMS_INVALID_VALUE);
			return false;
		}

		// 不是可领取状态
		if (oneData.getStatus() != 2) {
			player.sendError(protocol.getType(), Status.error.PARAMS_INVALID);
			return false;
		}
		
		if (cfg.getFree() == 1) {
			// 免費禮物領取等於購買,需增加購買次數
			oneData.setBuyTimes(oneData.getBuyTimes()+1);
		}
		
		// 檢查限制購買次數
		if (oneData.getBuyTimes() < cfg.getLimitNum()) {
			if (cfg.getFree() == 0) {
				oneData.setStatus(1); // 可以購買
			} else {
				oneData.setStatus(2); //免費還可以領
			}
		} else {
			oneData.setStatus(0);
		}

		AwardItems awardItems = AwardItems.valueOf(cfg.getSalegift());// 获取礼包
		awardItems.rewardTakeAffectAndPush(player, Action.DISCOUNT_GIFT_REWARD, 2,TapDBSource.Recharge,
				Params.valueOf("goodsId", goodsId),
				Params.valueOf("action", Action.DISCOUNT_GIFT_REWARD));// 记录领取日志

		player.getPlayerData().updateActivity(Const.ActivityId.DISCOUNT_GIFT_VALUE, activityTimeCfg.getStageId(), true);

		// 发送服务器礼包协议
		HPDiscountGetRewardRes.Builder builder = HPDiscountGetRewardRes.newBuilder();
		builder.setGoodsId(goodsId);
		player.sendProtocol(Protocol.valueOf(HP.code.DISCOUNT_GIFT_GET_REWARD_S, builder));

		BehaviorLogger.log4Service(player,Source.USER_OPERATION, Action.DISCOUNT_GIFT_REWARD, Params.valueOf("goodsId", goodsId),
				Params.valueOf("IsFree", cfg.getFree()),Params.valueOf("SaleGift", cfg.getSalegift()));
		return true;
	}
}
