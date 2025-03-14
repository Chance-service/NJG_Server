package com.guaji.game.module.activity.consumWeekCard;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.guaji.config.ConfigManager;
import org.guaji.os.GuaJiTime;

import com.guaji.game.config.ConsumeWeekCardCfg;
import com.guaji.game.config.WeekCardCfg;

public class ConWeekCardStatus {

	/**
	 * 周卡开始时间
	 */
	private Date startDate = null;
	/**
	 * 当前激活周卡配置Id
	 */
	private int currentActiveCfgId = 0;

	/**
	 * 领奖记录
	 */
	private Map<Integer, Boolean> rewardMap;

	public ConWeekCardStatus() {
		rewardMap = new HashMap<Integer, Boolean>();
	}

	public Date getStartDate() {
		return startDate;
	}

	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}

	public int getCurrentActiveCfgId() {
		return currentActiveCfgId;
	}

	public void setCurrentActiveCfgId(int currentActiveCfgId) {
		this.currentActiveCfgId = currentActiveCfgId;
	}

	public Map<Integer, Boolean> getRewardMap() {
		return rewardMap;
	}

	public void setRewardMap(Map<Integer, Boolean> rewardMap) {
		this.rewardMap = rewardMap;
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
	 * 是否領過購買禮
	 * @return
	 */
//	public boolean isBuyReward() {
//		if (this.startDate == null) {
//			return true;
//		}
//		return isReward(this.startDate);
//	}

	/**
	 * 设置今日已领取
	 */
	public void putRewardToday() {
		SimpleDateFormat dateFormat = new SimpleDateFormat("YYYYMMdd");
		Integer todayDate = Integer.parseInt(dateFormat.format(GuaJiTime.getCalendar().getTime()));
		// 先清除所有的领奖记录 然后记录当天的领奖记录
		this.rewardMap.put(todayDate, true);
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

		ConsumeWeekCardCfg weekCardCfg = ConfigManager.getInstance().getConfigByKey(ConsumeWeekCardCfg.class,
				getCurrentActiveCfgId());
		if (weekCardCfg == null) {
			return 0;
		}
		int leftDays = 0;
		leftDays = weekCardCfg.getDays() - GuaJiTime.calcBetweenDays(GuaJiTime.getCalendar().getTime(), startDate);
		if (isRewardToday()) {
			leftDays -= 1;
		}
		return leftDays;
	}

	/**
	 * 清除领奖记录
	 */
	public void clearRewardMap() {
		// 之前领奖记录删除掉
		if (startDate != null) {
			rewardMap.clear();
//			SimpleDateFormat dateFormat = new SimpleDateFormat("YYYYMMdd");
//			int startTime = Integer.parseInt(dateFormat.format(startDate));
//			Iterator<Map.Entry<Integer, Boolean>> it = rewardMap.entrySet().iterator();
//			while (it.hasNext()) {
//				Map.Entry<Integer, Boolean> entry = it.next();
//				if (entry.getKey() < startTime) // 不會扣除上個周卡領激活當天的
//					it.remove();// 使用迭代器的remove()方法删除元素
//			}
		}
	}

}
