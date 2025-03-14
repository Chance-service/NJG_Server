package com.guaji.game.module.activity.newUR;

import java.util.Map;

import org.guaji.msg.Msg;
import org.guaji.os.GuaJiTime;
import org.guaji.xid.GuaJiXID;

import com.guaji.game.protocol.Activity3.SyncNewURInfo;
import com.guaji.game.GsApp;
import com.guaji.game.config.ActivityCfg;
import com.guaji.game.config.NewURDropCfg;
import com.guaji.game.config.NewURTimesCfg;
import com.guaji.game.config.ActivityCfg.ActivityItem;
import com.guaji.game.manager.MailManager;
import com.guaji.game.player.Player;
import com.guaji.game.player.PlayerData;
import com.guaji.game.util.ActivityUtil;
import com.guaji.game.util.GsConst;
import com.guaji.game.protocol.Const;
import com.guaji.game.protocol.Mail;

/**
 * 新手UR活动业务管理类
 */
public class NewURManager {

	/**
	 * 如果玩家达到等级才开启
	 */
	public static boolean canPlay(int level, NewURStatus status) {
		// 等级限制
		int levelLimit = getLimitLevel();
		if (level < levelLimit)
			return false;
		if (null == status)
			return false;
		// 时间限制
		int endTime = status.getActivityEndTime();
		if (endTime == 0)
			return false;
		// 时间结束
		int now = GuaJiTime.getSeconds();
		if (now > endTime)
			return false;
		return true;
	}
	
	/**
	 * 获取限制等级(默认40)
	 */
	public static int getLimitLevel() {
		ActivityItem item = ActivityCfg.getActivityItem(Const.ActivityId.NEW_UR_VALUE);
		if (null != item) {
			Map<String, Object> map = item.getParamsMap();
			Object newURLimitLevel = map.get("newURLimitLevel");
			if (null != newURLimitLevel) {
				return (int) newURLimitLevel;
			}
		}
		return 40;
	}
	
	/**
	 * 获取活动限制时间(单位：秒)
	 */
	private static int getLimitTime() {
		ActivityItem item = ActivityCfg.getActivityItem(Const.ActivityId.NEW_UR_VALUE);
		if (null != item) {
			Map<String, Object> map = item.getParamsMap();
			Object newURLimitDay = map.get("newURLimitDay");
			if (null != newURLimitDay) {
				return (int) newURLimitDay * 60 * 60 * 24;
			}
		}
		return 0;
	}
	
	/**
	 * 获取活动结束时间(单位：秒)
	 */
	public static int getInitActivityEndTime() {
		return GuaJiTime.getSeconds() + getLimitTime();
	}
	
	/**
	 * 提前5级通知玩家活动即将开启
	 */
	public static void sendNoticeMail(int playerId, int level) {
		int noticeLevel = getLimitLevel() - 5;
		if (level == noticeLevel) {
			MailManager.createMail(playerId, Mail.MailType.Normal_VALUE, GsConst.MailId.NEW_UR_MAIL, "", null);
		}
	}

	public static SyncNewURInfo.Builder generateInfo(Player player) {
		long currentTime = System.currentTimeMillis();
		NewURStatus status = ActivityUtil.getNewURStatus(player.getPlayerData());
		if (null == status)
			return null;
		NewURTimesCfg timesCfg = NewURTimesCfg.getTimesCfgByVipLevel(player.getVipLevel());
		if (null == timesCfg)
			return null;
		// 总次数
		int totalTimes = status.getTotalTimes();
		// 距离下次免费倒计时
		long lastFreeTime = status.getLastFreeTime();
		int freeCD = (int) Math.max(toMillisecond(timesCfg.getFreeCountDown()) - (currentTime - lastFreeTime), 0);
		// 距离倍数失效倒计时
		long multipStartTime = status.getMultipStartTime();
		int multipOvertime = (int) Math.max(status.getMultipOverTime() - (currentTime - multipStartTime), 0);
		int multipTimes = status.getMultiple();
		int singleCost = timesCfg.getSingleCost();
		int tenCost = timesCfg.getTenCost();
		int genLeftTimes = NewURDropCfg.getLimitTimes(totalTimes);
		int tenLeftTimes = (totalTimes / 10 + 1) * 10;
		if (genLeftTimes == -1) {
			genLeftTimes = tenLeftTimes;
		}
		int leftLimitTimes = Math.min(genLeftTimes - totalTimes, tenLeftTimes - totalTimes);

		SyncNewURInfo.Builder builder = SyncNewURInfo.newBuilder();
		int leftTime = Math.max(status.getActivityEndTime() - GuaJiTime.getSeconds(), 0);
		builder.setLeftTime(leftTime);
		builder.setFreeTreasureTimes(0);
		builder.setLeftTreasureTimes(0);
		builder.setOnceCostGold(singleCost);
		builder.setTenCostGold(tenCost);
		builder.setLeftBuffTimes(multipOvertime / 1000);
		builder.setBufMultiple(multipTimes);
		builder.setFreeCD(freeCD / 1000);
		builder.setLeftAwardTimes(leftLimitTimes);
		return builder;
	}

	public static int toMillisecond(int hour) {
		return hour * 3600 * 1000;
	}
	
	/**
	 * 初始化活动结束时间
	 */
	public static void initActivityEndTime(PlayerData playerData, int level) {
		// 等级限制
		int levelLimit = getLimitLevel();
		if (level < levelLimit)
			return;
		NewURStatus status = ActivityUtil.getNewURStatus(playerData);
		if (status.getActivityEndTime() == 0) {
			status.setActivityEndTime(getInitActivityEndTime());
			playerData.updateActivity(Const.ActivityId.NEW_UR_VALUE, 0);
			// 通知重新推送活动列表
			GuaJiXID targetXID = GuaJiXID.valueOf(GsConst.ObjType.PLAYER, playerData.getId());
			Msg msg = Msg.valueOf(GsConst.MsgType.NEW_UR_ACTIVITY, targetXID);
			GsApp.getInstance().postMsg(msg);
		}
		
	}

}
