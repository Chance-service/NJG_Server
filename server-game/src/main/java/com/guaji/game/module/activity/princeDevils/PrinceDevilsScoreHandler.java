package com.guaji.game.module.activity.princeDevils;

import java.util.Map;

import org.guaji.app.AppObj;
import org.guaji.config.ConfigManager;
import org.guaji.net.protocol.Protocol;
import org.guaji.net.protocol.IProtocolHandler;

import com.guaji.game.protocol.Activity2.HPPrinceDevilsScoreExchangeReq;
import com.guaji.game.protocol.Activity2.HPPrinceDevilsScoreExchangeRes;
import com.guaji.game.config.ActivityTimeCfg;
import com.guaji.game.config.PrinceDevilsExchangeCfg;
import com.guaji.game.entity.PlayerPrinceDevilsEntity;
import com.guaji.game.item.AwardItems;
import com.guaji.game.item.ItemInfo;
import com.guaji.game.log.BehaviorLogger;
import com.guaji.game.log.BehaviorLogger.Action;
import com.guaji.game.log.BehaviorLogger.Params;
import com.guaji.game.player.Player;
import com.guaji.game.util.ActivityUtil;
import com.guaji.game.util.BuilderUtil;
import com.guaji.game.protocol.Const;
import com.guaji.game.protocol.HP;
import com.guaji.game.protocol.Status;

/**
 * 魔王宝藏活动，积分兑换
 */
public class PrinceDevilsScoreHandler implements IProtocolHandler {

	@Override
	public boolean onProtocol(AppObj appObj, Protocol protocol) {

		Player player = (Player) appObj;

		HPPrinceDevilsScoreExchangeReq req = protocol.parseProtocol(HPPrinceDevilsScoreExchangeReq.getDefaultInstance());

		int id = req.getId();
		int count = req.getCount();

		if (count <= 0) {
			// 参数错误
			player.sendError(protocol.getType(), Status.error.PARAMS_INVALID_VALUE);
			return true;
		}

		// 检测活动是否开放
		int activityId = Const.ActivityId.PRINCE_DEVILS_VALUE;
		ActivityTimeCfg timeCfg = ActivityUtil.getCurActivityTimeCfg(activityId);
		if (timeCfg == null) {
			// 活动已关闭
			player.sendError(protocol.getType(), Status.error.ACTIVITY_CLOSE);
			return true;
		}

		PlayerPrinceDevilsEntity princeDevilsEntity = player.getPlayerData().loadPrinceDevilsEntity();
		if (princeDevilsEntity.getStageId() != timeCfg.getStageId()) {
			princeDevilsEntity.resetInfo(timeCfg.getStageId());
			princeDevilsEntity.notifyUpdate();
		}

		Map<Object, PrinceDevilsExchangeCfg> cfgMap = ConfigManager.getInstance().getConfigMap(PrinceDevilsExchangeCfg.class);
		if (cfgMap == null) {
			player.sendError(protocol.getType(), Status.error.CONFIG_NOT_FOUND_VALUE);
			return true;
		}

		PrinceDevilsExchangeCfg cfg = ConfigManager.getInstance().getConfigByKey(PrinceDevilsExchangeCfg.class, id);
		if (cfg == null) {
			player.sendError(protocol.getType(), Status.error.CONFIG_NOT_FOUND_VALUE);
			return true;
		}

		// 已兑换次数
		int exchangeCount = princeDevilsEntity.getScoreExchangeCount(id);
		if (exchangeCount >= cfg.getLimitTimes() && count > cfg.getLimitTimes() - exchangeCount) {
			player.sendError(protocol.getType(), Status.error.TIME_LIMIT_ALL_BUY_TIMES_LIMIT);
			return true;
		}

		// 活动积分不足
		if (princeDevilsEntity.getScore() < cfg.getCostCredits() * count) {
			player.sendError(protocol.getType(), Status.error.ROULETTE_CREDITS_LACK);
			return true;
		}

		// 扣除积分，发奖励
		princeDevilsEntity.setScore(princeDevilsEntity.getScore() - cfg.getCostCredits() * count);
		princeDevilsEntity.addExchageInfo(id, exchangeCount + count);
		AwardItems awardItems = new AwardItems();
		for (int i = 0; i < count; i++) {
			ItemInfo info = ItemInfo.valueOf(cfg.getExchangeItems());
			awardItems.addItem(info);
		}
		awardItems.rewardTakeAffectAndPush(player, Action.PRINCE_DEVILS_EXCHANGE, 1);

		HPPrinceDevilsScoreExchangeRes.Builder ret = HPPrinceDevilsScoreExchangeRes.newBuilder();
		BuilderUtil.princeDevilsScoreBuilder(princeDevilsEntity, ret, cfgMap);
		player.sendProtocol(Protocol.valueOf(HP.code.PRINCE_DEVILS_SCORE_PANEL_S, ret));

		princeDevilsEntity.notifyUpdate(false);

		// 日志记录
		BehaviorLogger.log4Platform(player, Action.PRINCE_DEVILS_EXCHANGE, Params.valueOf("stageId", princeDevilsEntity.getStageId()),
				Params.valueOf("score", princeDevilsEntity.getScore()), Params.valueOf("deductScore", (cfg.getCostCredits() * count)),
				Params.valueOf("awards", awardItems.toString()));
		return true;
	}
}
