package com.guaji.game.module.activity.newTreasureRaider2;

import org.guaji.app.AppObj;
import org.guaji.config.ConfigManager;
import org.guaji.net.protocol.Protocol;
import org.guaji.net.protocol.IProtocolHandler;

import com.guaji.game.protocol.Activity2.HPNewTreasureRaiderInfoSync2;
import com.guaji.game.protocol.Activity2.TreasureSearchType;
import com.guaji.game.config.ActivityTimeCfg;
import com.guaji.game.config.NewTreasureRaiderDropCfg2;
import com.guaji.game.config.NewTreasureRaiderTimesCfg2;
import com.guaji.game.config.RoleRelatedCfg;
import com.guaji.game.entity.RoleEntity;
import com.guaji.game.player.Player;
import com.guaji.game.util.ActivityUtil;
import com.guaji.game.protocol.Const;
import com.guaji.game.protocol.HP;
import com.guaji.game.protocol.Status;

public class NewTreasureRaiderInfoHandler2 implements IProtocolHandler {

	@Override
	public boolean onProtocol(AppObj appObj, Protocol protocol) {
		Player player = (Player) appObj;
		// 检测活动是否开放
		int activityId = Const.ActivityId.NEW_TREASURE_RAIDER2_VALUE;
		ActivityTimeCfg activityTimeCfg = ActivityUtil.getCurActivityTimeCfg(activityId);
		if (activityTimeCfg == null) {
			// 活动已关闭
			player.sendError(protocol.getType(), Status.error.ACTIVITY_CLOSE);
			return true;
		}
		NewTreasureRaiderTimesCfg2 timesCfg = NewTreasureRaiderTimesCfg2.getTimesCfgByVipLevel(0);
		if (timesCfg == null) {
			player.sendError(protocol.getType(), Status.error.CONFIG_NOT_FOUND);
			return true;
		}

		HPNewTreasureRaiderInfoSync2.Builder builder = generateInfo(player);

		player.sendProtocol(Protocol.valueOf(HP.code.NEW_TREASURE_RAIDER_INFO2_S, builder));
		return true;
	}

	public static int convertTimeToMillisecond(int hour) {
		return hour * 3600 * 1000;
	}

	public static HPNewTreasureRaiderInfoSync2.Builder generateInfo(Player player) {
		int activityId = Const.ActivityId.NEW_TREASURE_RAIDER2_VALUE;
		long currentTime = System.currentTimeMillis();
		NewTreasureRaiderTimesCfg2 timesCfg = NewTreasureRaiderTimesCfg2.getTimesCfgByVipLevel(player.getVipLevel());
		ActivityTimeCfg activityTimeCfg = ActivityUtil.getCurActivityTimeCfg(activityId);
		NewTreasureRaiderStatus2 status = ActivityUtil.getActivityStatus(player.getPlayerData(), activityId,
				activityTimeCfg.getStageId(), NewTreasureRaiderStatus2.class);
		// 总次数
		int basicTotalTimes = status.getBasicTotalTimes();
		int skinTotalTimes = status.getSkinTotalTimes();

		// 距离下次免费倒计时
		long lastFreeTime = status.getLastFreeTime();
		int freeCD = (int) Math
				.max(convertTimeToMillisecond(timesCfg.getFreeCountDown()) - (currentTime - lastFreeTime), 0);
		// 距离倍数失效倒计时
		int singleCost = timesCfg.getSingleCost();
		int tenCost = timesCfg.getTenCost();

		int genBasicLeftTimes = NewTreasureRaiderDropCfg2.getLimitTimes(TreasureSearchType.SEARCHTYPE_BASIC,
				basicTotalTimes);
		int tenBasicLeftTimes = (basicTotalTimes / 10 + 1) * 10;
		if (genBasicLeftTimes == -1) {
			genBasicLeftTimes = tenBasicLeftTimes;
		}

		int genSkinLeftTimes = NewTreasureRaiderDropCfg2.getLimitTimes(TreasureSearchType.SEARCHTYPE_SKIN,
				basicTotalTimes);
		int tenSkinLeftTimes = (skinTotalTimes / 10 + 1) * 10;
		if (genSkinLeftTimes == -1) {
			genSkinLeftTimes = tenSkinLeftTimes;
		}

		// 判断基础碎片是否满足激活
		int willActRoleId = NewTreasureRaiderDropCfg2.getWillActRoleId();
		boolean isCanAct = false;
		RoleEntity entity = player.getPlayerData().getMercenaryByItemId(willActRoleId);
		if (entity != null) {
			RoleRelatedCfg cfg = ConfigManager.getInstance().getConfigByKey(RoleRelatedCfg.class, entity.getItemId());
			if (cfg != null && entity.getSoulCount() >= cfg.getLimitCount()) {
				isCanAct = true;
			}

		}
		int leftBasicLimitTimes = Math.min(genBasicLeftTimes - basicTotalTimes, tenBasicLeftTimes - basicTotalTimes);
		int leftSkinLimitTimes = Math.min(genSkinLeftTimes - skinTotalTimes, tenSkinLeftTimes - skinTotalTimes);

		HPNewTreasureRaiderInfoSync2.Builder builder = HPNewTreasureRaiderInfoSync2.newBuilder();
		builder.setLeftTime(activityTimeCfg.calcActivitySurplusTime());
		builder.setFreeTreasureTimes(0);
		builder.setLeftTreasureTimes(0);
		builder.setOnceCostGold(singleCost);
		builder.setTenCostGold(tenCost);
		builder.setFreeCD(freeCD / 1000);
		builder.setBasicLeftAwardTimes(leftBasicLimitTimes);
		builder.setSkinLeftAwardTimes(leftSkinLimitTimes);
		builder.setIsActiveBasic(isCanAct);
		return builder;
	}
}
