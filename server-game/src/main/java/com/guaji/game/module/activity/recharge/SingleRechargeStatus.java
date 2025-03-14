package com.guaji.game.module.activity.recharge;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.guaji.config.ConfigManager;
import org.guaji.log.Log;
import org.guaji.os.GuaJiTime;

import com.guaji.game.config.SingleRechargeCfg;
import com.guaji.game.protocol.Status;
import com.guaji.game.protocol.Activity.SingleRechargeInfo;

/**
 * 每日单项充值返利活动状态
 */
public class SingleRechargeStatus {

	/**
	 * 活动期间每日活动状态
	 */
	private Map<Integer, Set<SingleRechargeOneDayStatus>> statusMap;

	public SingleRechargeStatus() {
		init();
	}

	/**
	 * 获取可以领取的奖励ID
	 */
	public Set<Integer> getCanGetAwardIds() {
		Set<Integer> setAwardIds = new HashSet<Integer>();

		int date = Integer.valueOf(GuaJiTime.DATE_FORMATOR_DAYNUM(GuaJiTime.getAM0Date()));
		Set<SingleRechargeOneDayStatus> oneDayStatus = statusMap.get(date);
		if (null == oneDayStatus) {
			oneDayStatus = new HashSet<SingleRechargeOneDayStatus>();
		} else {
			for (SingleRechargeOneDayStatus singleRechargeOneDayStatus : oneDayStatus) {
				int rechargeTimes = singleRechargeOneDayStatus.getRechargeTimes();
				int getTimes = singleRechargeOneDayStatus.getGetTimes();
				if (rechargeTimes > 0 && getTimes < rechargeTimes) {
					setAwardIds.add(singleRechargeOneDayStatus.getId());
				}
			}
		}
		return setAwardIds;
	}

	/**
	 * 添加可领取奖励数据
	 */
	public void addAwardData(int cfgId, int date) {

		boolean isContain = false;
		Set<SingleRechargeOneDayStatus> set = statusMap.get(date);
		// 今天的没有初始化
		if (null == set || set.isEmpty()) {
			this.init();
			set = statusMap.get(date);
		}
		for (SingleRechargeOneDayStatus status : set) {
			if (status.getId() == cfgId) {
				isContain = true;
				int rechargeTimes = status.getRechargeTimes();
				int maxRechargeTimes = status.getMaxRechargeTimes();
				// 判断最大可充值次数，可以充值，但是不增加充值次数了，这样有什么影响吗？
				if (rechargeTimes < maxRechargeTimes) {
					status.setRechargeTimes(rechargeTimes + 1);
				}
				break;
			}
		}

		if (!isContain) {
			// 不包含则，新建一条记录
			SingleRechargeCfg cfg = ConfigManager.getInstance().getConfigMap(SingleRechargeCfg.class).get(cfgId);
			if (cfg == null) {
				Log.errPrintln(String.format("SingleRechargeCfg not found.id = {}", cfgId));
			} else {
				SingleRechargeOneDayStatus oneDayStatus = new SingleRechargeOneDayStatus(cfgId, 0, 1, cfg.getMaxRechargeTimes());
				statusMap.get(date).add(oneDayStatus);
			}
		}
	}

	/**
	 * 增加一次领取记录
	 */
	public int addGetTimes(int cfgId, int date) {
		boolean isContain = false;
		Set<SingleRechargeOneDayStatus> set = statusMap.get(date);
		for (Iterator<SingleRechargeOneDayStatus> iterator = set.iterator(); iterator.hasNext();) {
			SingleRechargeOneDayStatus status = (SingleRechargeOneDayStatus) iterator.next();
			if (status.getId() == cfgId) {
				isContain = true;
				int getTimes = status.getGetTimes(); // 已领取次数
				if (getTimes + 1 > status.getRechargeTimes()) {
					// 领取次数已经到达上限
					return Status.error.PARAMS_INVALID_VALUE;
				}
				status.setGetTimes(getTimes + 1);
			}
		}

		if (!isContain) {
			// 没有找到该档位
			return Status.error.PARAMS_INVALID_VALUE;
		}
		return 0;
	}

	/**
	 * 是否红点提示，是否领取过了
	 */
	public boolean showRedPoint() {
		if (getCanGetAwardIds().size() > 0) {
			return true;
		}
		return false;
	}

	/**
	 * 获取所有的领奖信息
	 */
	public List<SingleRechargeInfo> getAllInfoList() {
		List<SingleRechargeInfo> list = new ArrayList<SingleRechargeInfo>();
		int date = Integer.valueOf(GuaJiTime.DATE_FORMATOR_DAYNUM(GuaJiTime.getAM0Date()));
		Set<SingleRechargeOneDayStatus> set = statusMap.get(date);

		if (null == set || set.isEmpty()) {
			this.init();
			set = statusMap.get(Integer.valueOf(GuaJiTime.DATE_FORMATOR_DAYNUM(GuaJiTime.getAM0Date())));
		}
		for (SingleRechargeOneDayStatus status : set) {
			SingleRechargeInfo.Builder info = SingleRechargeInfo.newBuilder();
			info.setId(status.getId());
			info.setGetTimes(status.getGetTimes());
			info.setRechargeTimes(status.getRechargeTimes());
			info.setMaxRechargeTimes(status.getMaxRechargeTimes());
			list.add(info.build());
		}

		return list;
	}

	private void init() {

		Set<SingleRechargeOneDayStatus> set = new HashSet<SingleRechargeOneDayStatus>();
		Map<Object, SingleRechargeCfg> map = ConfigManager.getInstance().getConfigMap(SingleRechargeCfg.class);
		for (SingleRechargeCfg cfg : map.values()) {
			SingleRechargeOneDayStatus status = new SingleRechargeOneDayStatus(cfg.getId(), 0, 0, cfg.getMaxRechargeTimes());
			set.add(status);
		}
		this.statusMap = new HashMap<Integer, Set<SingleRechargeOneDayStatus>>();
		this.statusMap.put(Integer.valueOf(GuaJiTime.DATE_FORMATOR_DAYNUM(GuaJiTime.getAM0Date())), set);

	}

}
