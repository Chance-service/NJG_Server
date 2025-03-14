package com.guaji.game.module.activity.activity183;

import java.util.Date;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.guaji.config.ConfigManager;
import org.guaji.os.GuaJiTime;

import com.guaji.game.config.StageGiftAward151Cfg;

public class Activity183Status {

//	/**
//	 * 活動開啟時間
//	 */
	private  Date startDate;
	/**
	 * 已獲取獎品
	 */
	private Set<Integer> gotGiftId;
    
    public Activity183Status() {
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

    public boolean isAlreadyGot(int cfgId) {
        if (this.getGiftIdList().contains(cfgId)) {
            return true;
        }
        return false;
    }
    
}
