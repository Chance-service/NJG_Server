package com.guaji.game.module.activity.weekCard;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.guaji.config.ConfigManager;
import org.guaji.os.GuaJiTime;

import com.guaji.game.config.WeekCardCfg;
import com.guaji.game.util.GsConst;

public class WeekCardStatus {

	/**
	 * 周卡开始时间
	 */
	private Date startDate = null;
	/**
	 * 周卡开始时间
	 */
	private Date prepareBuyTime = null;

	/**
	 * 周卡到期时间
	 */
	private long expireTime = 0;

	/**
	 * 当前激活周卡配置Id
	 */
	private int currentActiveCfgId = 0;

	/**
	 * 领奖记录
	 */
	private Map<Integer, Boolean> rewardMap;

	public WeekCardStatus() {
		rewardMap = new HashMap<Integer, Boolean>();
	}

	public int getCurrentActiveCfgId() {
		return this.currentActiveCfgId;
	}

	public void setCurrentActiveCfgId(int currentActiveCfgId) {
		this.currentActiveCfgId = currentActiveCfgId;
	}

	public Date getStartDate() {
		return startDate;
	}

	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}

	public Date getPrepareBuyTime() {
		return prepareBuyTime;
	}

	public void setPrepareBuyTime(Date prepareBuyTime) {
		this.prepareBuyTime = prepareBuyTime;
	}

	public long getExpireTime() {
		return expireTime;
	}

	public void setExpireTime(long expireTime) {
		this.expireTime = expireTime;
	}

	/**
	 * 今天的周卡奖励是否领取
	 *
	 * @return
	 */
	public boolean isRewardToday() {
		return isReward(GuaJiTime.getCalendar().getTime());
	}

	/**
	 * 设置今日已领取
	 */
	public void putRewardToday() {
		// int days = GuaJiTime.calcBetweenDays(GuaJiTime.getCalendar().getTime(),
		// startDate);

		SimpleDateFormat dateFormat = new SimpleDateFormat("YYYYMMdd");
		Integer todayDate = Integer.parseInt(dateFormat.format(GuaJiTime.getCalendar().getTime()));
		// 先清除所有的领奖记录 然后记录当天的领奖记录
		this.rewardMap.put(todayDate, true);

	}

	/**
	 * 设置昨日补领
	 */
	public void putRewardYestDay() {
		Date yestDate = new Date(GuaJiTime.getMillisecond() - (GsConst.ONE_DAY_SEC * 1000));
		int days = GuaJiTime.calcBetweenDays(yestDate, startDate);
		this.rewardMap.put(days, true);
	}

	/**
	 * 是否在领取奖励期间
	 *
	 * @return
	 */
	public boolean isBetweenReward() {
		WeekCardCfg weekCardCfg = ConfigManager.getInstance().getConfigByKey(WeekCardCfg.class,
				getCurrentActiveCfgId());
		if (weekCardCfg == null) {
			return false;
		}

		if (this.expireTime == 0) {
			// 旧有个规则
			for (int day = 1; day <= weekCardCfg.getDays(); day++) {
				if (!this.rewardMap.containsKey(day) || !this.rewardMap.get(day)) {
					return true;
				}
			}
		} else {
			// 新的规则
			return getLeftDays() > 0 ? true : false;
		}

		return false;
	}

	/**
	 * 昨天是否领取过奖励
	 *
	 * @return
	 */
	public boolean isYestReward() {
		Date yestDate = new Date(GuaJiTime.getMillisecond() - (GsConst.ONE_DAY_SEC * 1000));
		return isReward(yestDate);
	}

	/**
	 * 是否需要补领昨日奖励
	 *
	 * @return
	 */
	public boolean isNeedYestReward() {
		Date yestDate = new Date(GuaJiTime.getMillisecond() - (GsConst.ONE_DAY_SEC * 1000));
		// 这段代码看不懂
		if (GuaJiTime.getAM0Date(yestDate).getTime() < GuaJiTime.getAM0Date(GuaJiTime.getCalendar().getTime())
				.getTime()) {
			return false;
		}
		return !isReward(yestDate);
	}

	/**
	 * 是否领取过奖励
	 *
	 * @return
	 */
	public boolean isReward(Date date) {
		// 若过期直接提示 已领过

		SimpleDateFormat dateFormat = new SimpleDateFormat("YYYYMMdd");
		int dateformat = Integer.parseInt(dateFormat.format(date));

		if (rewardMap.containsKey(dateformat)) {
			return rewardMap.get(dateformat);
		}
		// 设置当前时间的格

		return false;
	}

	/**
	 * 获得周卡剩余天数
	 *
	 * @return
	 */
	public int getLeftDays() {

		WeekCardCfg weekCardCfg = ConfigManager.getInstance().getConfigByKey(WeekCardCfg.class,
				getCurrentActiveCfgId());
		if (weekCardCfg == null) {
			return 0;
		}
		int leftDays = 0;
		if (expireTime == 0) {
			leftDays = weekCardCfg.getDays() - GuaJiTime.calcBetweenDays(GuaJiTime.getCalendar().getTime(), startDate);
			if (isRewardToday()) {
				leftDays -= 1;
			}
		} else {
			// 一天毫秒数
			double oneDay = 86400000;
			leftDays = (int) Math.ceil((double) (expireTime - System.currentTimeMillis()) / oneDay);
			leftDays = leftDays <= 0 ? 0 : leftDays;
		}
		return leftDays;
	}

	/**
	 * 否可以胜利月卡
	 *
	 * @param goodsId
	 * @return
	 */
	public boolean canLevelUp(int goodsId) {
		WeekCardCfg weekCardCfg = WeekCardCfg.getWeekCardCfgByGoodsId(currentActiveCfgId);
		if (weekCardCfg != null && weekCardCfg.getLevelUpGoodsId() == goodsId) {
			return true;
		}
		return false;
	}

	/**
	 * 充值周卡升级数据
	 */
	public void resetlevelUpData() {
		rewardMap.clear();
		startDate = GuaJiTime.getCalendar().getTime();
	}

	/**
	 * 清除领奖记录
	 */
	public void clearRewardMap() {

		// 之前领奖记录删除掉
		if (startDate != null) {
			SimpleDateFormat dateFormat = new SimpleDateFormat("YYYYMMdd");
			int startTime = Integer.parseInt(dateFormat.format(startDate));
			Iterator<Map.Entry<Integer, Boolean>> it = rewardMap.entrySet().iterator();
			while (it.hasNext()) {
				Map.Entry<Integer, Boolean> entry = it.next();
				if (entry.getKey() < startTime)
					it.remove();// 使用迭代器的remove()方法删除元素
			}
		}

		// rewardMap.clear();
	}
}
