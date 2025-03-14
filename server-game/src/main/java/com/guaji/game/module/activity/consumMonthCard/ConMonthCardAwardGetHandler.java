package com.guaji.game.module.activity.consumMonthCard;

import org.guaji.app.AppObj;
import org.guaji.config.ConfigManager;
import org.guaji.net.protocol.IProtocolHandler;
import org.guaji.net.protocol.Protocol;

import com.guaji.game.config.ConsumeMonthCardCfg;
import com.guaji.game.config.MonthCardCfg;
import com.guaji.game.item.AwardItems;
import com.guaji.game.log.BehaviorLogger.Action;
import com.guaji.game.log.BehaviorLogger.Params;
import com.guaji.game.player.Player;
import com.guaji.game.protocol.Const;
import com.guaji.game.protocol.HP;
import com.guaji.game.protocol.Status;
import com.guaji.game.protocol.Activity4.ConsumeMonthCardAwardGetRet;
import com.guaji.game.util.ActivityUtil;
import com.guaji.game.util.TapDBUtil.TapDBSource;

public class ConMonthCardAwardGetHandler implements IProtocolHandler {

	@Override
	public boolean onProtocol(AppObj appObj, Protocol protocol) {

		Player player = (Player) appObj;

		ConMonthCardStatus monthCardStatus = ActivityUtil.getConMonthCardStatus(player.getPlayerData());

		if (monthCardStatus == null) {
			return true;
		}

		ConsumeMonthCardCfg cfg = ConfigManager.getInstance().getConfigByKey(ConsumeMonthCardCfg.class, 73);// 1为唯一月卡标识，不想定义宏了

		if (cfg == null) {
			player.sendError(protocol.getType(), Status.error.CONFIG_NOT_FOUND);
			return true;
		}
		
//		boolean buyReward = (monthCardStatus.getLastRewadTime() == null); //判斷是否未領取購買禮

		if (!monthCardStatus.getMonthCardAward()) {
			player.sendError(protocol.getType(), Status.error.NO_MULTIELITE_TIMES_VALUE);
			return false;
		}

		AwardItems awardItems = AwardItems.valueOf(cfg.getReward());// 获取礼包
		
//		if (buyReward) {  // 有購買禮
//			String info = cfg.getBuyReward();
//			awardItems.initByString(info);
//		}
		
		awardItems.rewardTakeAffectAndPush(player, Action.CONSUME_MONTH_CARD_REWARD, 2,TapDBSource.Month_Card,Params.valueOf("activityId", Const.ActivityId.CONSUME_MONTH_CARD_VALUE));// 记录领取日志

		player.getPlayerData().updateActivity(Const.ActivityId.CONSUME_MONTH_CARD_VALUE, 0, true);

		// 获取首充礼包的实体信息并设置新的领取状态
		ConsumeMonthCardAwardGetRet.Builder builder = ConsumeMonthCardAwardGetRet.newBuilder();
		builder.setMonthCardId(1);// 礼包id
		builder.setGetAwardTime(monthCardStatus.getLastRewadTime().getTime());// 礼包领取时间（状态）
		// 发送服务器礼包协议
		player.sendProtocol(Protocol.valueOf(HP.code.CONSUME_MONTHCARD_AWARD_S_VALUE, builder));
		return true;
	}

}
