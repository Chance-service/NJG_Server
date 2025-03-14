package com.guaji.game.module.activity.releaseUR2;

import org.guaji.app.AppObj;
import org.guaji.net.protocol.IProtocolHandler;
import org.guaji.net.protocol.Protocol;

import com.guaji.game.config.ActivityTimeCfg;
import com.guaji.game.config.ReleaseURTimesCfg;
import com.guaji.game.config.ReleaseURTimesCfg2;
import com.guaji.game.config.SysBasicCfg;
import com.guaji.game.item.ConsumeItems;
import com.guaji.game.log.BehaviorLogger.Action;
import com.guaji.game.module.activity.releaseUR.ReleaseURStatus;
import com.guaji.game.player.Player;
import com.guaji.game.protocol.Const;
import com.guaji.game.protocol.HP;
import com.guaji.game.protocol.Status;
import com.guaji.game.protocol.Activity3.ReleaseURResetRep;
import com.guaji.game.protocol.Activity3.ReleaseURResetRep2;
import com.guaji.game.protocol.Const.changeType;
import com.guaji.game.util.ActivityUtil;

/**
* @author 作者 E-mail:Jeremy@163.com
* @version 创建时间：2019年4月5日 下午9:31:34
* 类说明
*/
public class ReleaseURResetHandler2 implements IProtocolHandler{
	@Override
	public boolean onProtocol(AppObj appObj, Protocol protocol) {
		Player player = (Player) appObj;
		// 检测活动是否开放
		int activityId = Const.ActivityId.RELEASE_UR2_VALUE;
		ActivityTimeCfg timeCfg = ActivityUtil.getCurActivityTimeCfg(activityId);
		if (timeCfg == null) {
			// 活动已关闭
			player.sendError(protocol.getType(), Status.error.ACTIVITY_CLOSE);
			return true;
		}
		
		ReleaseURTimesCfg2 timesCfg = ReleaseURTimesCfg2.getTimesCfgByVipLevel(player.getVipLevel());
		if (timesCfg == null) {
			player.sendError(protocol.getType(), Status.error.CONFIG_NOT_FOUND);
			return true;
		}

		// 计算实际花费钻石数量
		int payGold = SysBasicCfg.getInstance().getReleaseUrResetCost();

		if (payGold > player.getGold()) {
			// 钻石不足
			player.sendError(protocol.getType(), Status.error.GOLD_NOT_ENOUGH_VALUE);
			return true;
		}
		// 扣除钻石
		if (payGold > 0) {
			player.consumeGold(payGold, Action.RELEASE_UR2);
			ConsumeItems.valueOf(changeType.CHANGE_GOLD, payGold).pushChange(player);
		}

		ReleaseURStatus status = ActivityUtil.getActivityStatus(player.getPlayerData(), activityId,
				timeCfg.getStageId(), ReleaseURStatus.class);

		// 清空领奖状态
		if (status == null) {
			// 钻石不足
			player.sendError(protocol.getType(), Status.error.GOLD_NOT_ENOUGH_VALUE);
			return true;
		}

		// 扣费 
		player.consumeGold(payGold, Action.RELEASE_UR_RESET2);
		
		status.getLotteryIndexs().clear();
		// 更新玩家的活动数据
		player.getPlayerData().updateActivity(activityId, timeCfg.getStageId());
		// 告诉客户端重置成功状态
		ReleaseURResetRep2.Builder builder = ReleaseURResetRep2.newBuilder();
		builder.setStatus(1);
		player.sendProtocol(Protocol.valueOf(HP.code.RELEASE_UR_RESET2_S_VALUE, builder));

		return false;
	}

}
