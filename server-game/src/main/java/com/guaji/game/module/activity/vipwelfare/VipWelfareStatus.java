package com.guaji.game.module.activity.vipwelfare;

import java.util.Date;

import org.guaji.os.GuaJiTime;
public class VipWelfareStatus {

	
	private int awareStatus = 0;
	
	private Date lastAwareTime;

	public int getAwareStatus() {
		return awareStatus;
	}

	public void setAwareStatus(int awareStatus) {
		this.awareStatus = awareStatus;
	}

	public Date getLastAwareTime() {
		return lastAwareTime;
	}

	public void setLastAwareTime(Date lastAwareTime) {
		this.lastAwareTime = lastAwareTime;
	}
	
	/**
	 * 是否红点提示，当前vip状态下，存在可以领取的礼包
	 */
	public boolean showRedPoint(int vipLevel)
	{
		if(vipLevel<= 0)
		{
			return false;
		}
	
		if(getLastAwareTime() == null)
		{
			return true;
		}
		else
		{
			if(!GuaJiTime.isSameDay(getLastAwareTime().getTime(), GuaJiTime.getCalendar().getTime().getTime()))
			{
				return true;
			}
		}
		
		return false;
	}

}
