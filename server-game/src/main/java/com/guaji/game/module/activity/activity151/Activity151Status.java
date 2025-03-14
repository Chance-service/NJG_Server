package com.guaji.game.module.activity.activity151;

import java.util.Date;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.guaji.config.ConfigManager;
import org.guaji.os.GuaJiTime;

import com.guaji.game.config.StageGiftAward151Cfg;

public class Activity151Status {

//	/**
//	 * 活動開啟時間
//	 */
	private  Date startDate;
	/**
	 * 已獲取獎品
	 */
	private Set<Integer> gotGiftId;
    
    public Activity151Status() {
        this.gotGiftId = new HashSet<>();
        this.startDate = null;
    }

    public Set<Integer> getGiftIdList() {
        return gotGiftId;
    }

    public void addGiftIds(int cfgId){
    	gotGiftId.add(cfgId);
    }
    
    public Date getStartDate() {
    	return this.startDate;
    }
    
    public void setStartDate(Date adate) {
    	this.startDate = adate;
    }

    /**
     * 是否红点提示，已经达到等级，且礼包没有购买过
     */
    public boolean showRedPoint(int level) {

        Map<Object, StageGiftAward151Cfg> cfgList = ConfigManager.getInstance().getConfigMap(StageGiftAward151Cfg.class);

        for (StageGiftAward151Cfg cfg : cfgList.values()) {

            if (level >= cfg.getMinLevel() && level <= cfg.getMaxLevel()) {
            	if (calcGiftSurplusTime(cfg.getHours()) > 0) {
	                if (!isAlreadyGot(cfg.getId())) {
	                    return true;
	                }
            	}
            }
        }
        return false;
    }

    public boolean isAlreadyGot(int cfgId) {
        if (this.getGiftIdList().contains(cfgId)) {
            return true;
        }
        return false;
    }
    
	/**
	 * 计算禮包剩余时间
	 * 
	 * @return
	 */
	public int calcGiftSurplusTime(int hours) {
		if (getStartDate() != null) {
			long currentTime = GuaJiTime.getMillisecond();
			long endTime = getStartDate().getTime()
					+ hours * 3600000;
			int surplusTime = (int) ((endTime - currentTime) / 1000);
			return Math.max(surplusTime, 0);
		}
		return -1;
	}

}
