package com.guaji.game.module.activity.activity185;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

public class Activity185Status {

//	/**
//	 * 活動開啟時間
//	 */
	private  Date startDate;
	/**
	 * 已獲取獎品
	 */
	private Set<Integer> gotGiftId;
    
    public Activity185Status() {
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
