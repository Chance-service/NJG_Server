package com.guaji.game.module.activity.activity153;

import java.util.HashSet;
import java.util.Set;

public class Activity153Status {
	/**
	 * 已獲取獎品的英雄Id
	 */
	private Set<Integer> gotMarkId;
    
    public Activity153Status() {
        this.gotMarkId = new HashSet<>();
    }

    public Set<Integer> getMarkList() {
        return gotMarkId;
    }

    public void addCfgId(int cfgId){
    	gotMarkId.add(cfgId);
    }

    /**
     * 是否红点提示，已经达到等级，且礼包没有购买过
     */
    public boolean showRedPoint(int level) {

//        Map<Object, StageGiftAward151Cfg> cfgList = ConfigManager.getInstance().getConfigMap(StageGiftAward151Cfg.class);
//
//        for (StageGiftAward151Cfg cfg : cfgList.values()) {
//
//            if (level >= cfg.getMinLevel() && level <= cfg.getMaxLevel()) {
//            	if (calcGiftSurplusTime(cfg.getHours()) > 0) {
//	                if (!isAlreadyGot(cfg.getId())) {
//	                    return true;
//	                }
//            	}
//            }
//        }
        return false;
    }

    public boolean isAlreadyGot(int cfgId) {
        if (this.getMarkList().contains(cfgId)) {
            return true;
        }
        return false;
    }
    
}
