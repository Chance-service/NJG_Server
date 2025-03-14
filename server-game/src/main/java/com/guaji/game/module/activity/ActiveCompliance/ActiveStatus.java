package com.guaji.game.module.activity.ActiveCompliance;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import org.guaji.os.GuaJiTime;

import com.guaji.game.config.SysBasicCfg;

/**
 * @author 作者 E-mail:Jeremy@163.com
 * @version 创建时间：2019年5月6日 上午12:23:35 类说明
 */
public class ActiveStatus {
	/**
	 * 活动开启时间
	 */
	private Date startDate;
	/**
	 * 已达标天数
	 */
	private int days;

	/**
	 * 是否要累加已領取天數
	 */
	private boolean isfirst;

	/**
	 * 领取奖励记录
	 */
	private Set<Integer> awardDays;

	public ActiveStatus() {
		days = 0;
		this.isfirst = true;
		awardDays = new HashSet<Integer>();
		this.startDate = GuaJiTime.getAM0Date();// 获取当前时间
		//int activityId = Const.ActivityId.ACTIVECOMPLIANCE_VALUE;
//		ActivityTimeCfg timeCfg = ActivityUtil.getCurActivityTimeCfg(activityId);
//		if (timeCfg != null) {
//			startDate = new Date(timeCfg.getlStartTime());
//		} else {
//		
//		}
	}
	
	public Date getStartDate() {
		return startDate;
	}

	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}

	public int getDays() {
		return days;
	}

	public void setDays(int days) {
		this.days = days;
	}

	public Set<Integer> getAwardDays() {
		return awardDays;
	}

	public void setAwardDays(Set<Integer> awardDays) {
		this.awardDays = awardDays;
	}

	/**
	 * 计算活动剩余时间
	 * 
	 * @return
	 */
	public long calcActivitySurplusTime() {
		long currentTime = GuaJiTime.getMillisecond();
		long endTime = GuaJiTime.getAM0Date(this.startDate).getTime()
				+ SysBasicCfg.getInstance().getActiveComplianceCycleDays() * 86400000;

		return (endTime - currentTime) / 1000;
	}

	/**
	 * 活动到期刷新活动数据
	 */
	public void refresh() {
		long currentTime = GuaJiTime.getMillisecond();
		Long nextStart = GuaJiTime.getAM0Date(this.startDate).getTime()
				+ SysBasicCfg.getInstance().getActiveComplianceCycleDays() * 86400000;
		long nextEnd = nextStart + SysBasicCfg.getInstance().getActiveComplianceCycleDays() * 86400000;
		int times = 0;
		while (nextEnd < currentTime)
		{
			//活動週期計算至該週周期
			nextStart = nextStart + SysBasicCfg.getInstance().getActiveComplianceCycleDays() * 86400000;
			nextEnd = nextStart + SysBasicCfg.getInstance().getActiveComplianceCycleDays() * 86400000;
			times++;
			if (times>100)
				break;
		}
			
		startDate = new Date(nextStart);// 获取当前时间
		days = 0;
		this.awardDays.clear();
	}

	/**
	 * @param 具体那天奖励
	 * @return
	 */
	public boolean addAwardDays(Integer day) {
		if (this.awardDays.contains(day))
			return false;

		this.awardDays.add(day);

		return true;
	}

	public boolean isIsfirst() {
		return isfirst;
	}

	public void setIsfirst(boolean isfirst) {
		this.isfirst = isfirst;
	}

}
