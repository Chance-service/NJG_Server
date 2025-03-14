package com.guaji.game.module.activity.activity177;

import java.util.Date;

import org.guaji.config.ConfigManager;
import org.guaji.os.GuaJiTime;

import com.guaji.game.config.FailedGift177Cfg;
import com.guaji.game.config.SysBasicCfg;

public class Activity177Status {
	/**
	 *	計數器()
	 */	
	private int count;
	/**
	 * 	禮包觸發時間
	 */
	private  Date triggerDate;
	/**
	 * 	禮包觸發時間
	 */
	private  int triggerCfgId;
	/**
	 * 是否已購買
	 */
	private boolean isbuy;
	
    public Activity177Status() {
        this.count = 0;
        this.triggerDate = null;
        this.triggerCfgId = 0;
        this.isbuy = false;
    }

	public int getCount() {
		return count;
	}

	public void setCount(int count) {
		this.count = count;
	}
    
    public void addCount() {
    	this.count = this.count+1;
    }

	public Date getTriggerDate() {
		return triggerDate;
	}

	public void setTriggerDate(Date triggerDate) {
		this.triggerDate = triggerDate;
	}
	
	public boolean Isbuy() {
		return isbuy;
	}

	public void setIsbuy(boolean isbuy) {
		this.isbuy = isbuy;
	}
	
	public int getTriggerCfgId() {
		return triggerCfgId;
	}

	public void setTriggerCfgId(int triggerCfgId) {
		this.triggerCfgId = triggerCfgId;
	}
	
	/**
	 * 计算禮包剩余时间
	 * 
	 * @return
	 */
	public int calcGiftSurplusTime(int hours) {
		if (getTriggerDate() != null) {
			long currentTime = GuaJiTime.getMillisecond();
			long endTime = getTriggerDate().getTime()
					+ hours * 3600000;
			int surplusTime = (int) ((endTime - currentTime) / 1000);
			return Math.max(surplusTime, 0);
		}
		return -1;
	}
	
	/**
	 * 
	 */
	public boolean checkRestGift() {
		if (getCount() >= SysBasicCfg.getInstance().getFailedGiftCount()) {
			if ((getTriggerDate() != null)&&(getTriggerCfgId() > 0)) {
				FailedGift177Cfg triggerCfg=  ConfigManager.getInstance().getConfigByKey(FailedGift177Cfg.class, getTriggerCfgId());
				if (triggerCfg != null) {
					if (calcGiftSurplusTime(triggerCfg.getHours()) <= 0) {
						setCount(0);
						setTriggerDate(null);
						setTriggerCfgId(0);
						return true;
					}
				}
			}
		}
		return false;
	}

}
