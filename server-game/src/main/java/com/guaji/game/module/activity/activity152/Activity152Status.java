package com.guaji.game.module.activity.activity152;

import java.util.HashSet;
import java.util.Set;

public class Activity152Status {
	/**
	 * 已獲取獎品的英雄Id
	 */
	private Set<Integer> gotHeroId;
    
    public Activity152Status() {
        this.gotHeroId = new HashSet<>();
    }

    public Set<Integer> getHeroIdList() {
        return gotHeroId;
    }

    public void addHeroIds(int cfgId){
    	gotHeroId.add(cfgId);
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
        if (this.getHeroIdList().contains(cfgId)) {
            return true;
        }
        return false;
    }
    
}
