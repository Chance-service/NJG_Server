package com.guaji.game.module.activity.activity132;

import com.google.gson.reflect.TypeToken;
import com.guaji.game.config.LevelGiftAward132Cfg;
import com.guaji.game.config.SysBasicCfg;
import com.guaji.game.util.GsonUtil;
import org.guaji.config.ConfigManager;
import org.guaji.os.GuaJiTime;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

public class Activity132Status {

    private String giftIds;
    /**
     * 時間限制
     */
    private Date limitDate;
    /**
        * 當下觸發禮包序號
     */
    private int nowCfgId;
    
    public Activity132Status() {
        this.giftIds = GsonUtil.getJsonInstance().toJson(new ArrayList<>());
        this.limitDate = null;
        this.nowCfgId = 0;
    }

    public List<Integer> getGiftIdList() {
        return GsonUtil.getJsonInstance().fromJson(this.giftIds, new TypeToken<List<Integer>>() {
        }.getType());
    }

    public String getGiftIds() {
        return giftIds;
    }

    public void addGiftIds(int cfgId){
        List<Integer> ids = this.getGiftIdList();
        ids.add(cfgId);
        this.giftIds = GsonUtil.getJsonInstance().toJson(ids);
    }
    
    public Date getLimitDate() {
    	return this.limitDate;
    }
    
    public void setLimitDate(Date adate) {
    	this.limitDate = adate;
    }
    
    public int getNowCfgId (){
    	return this.nowCfgId;
    }
    
    public void setNowCfgId(int id) {
    	this.nowCfgId = id;
    }

    /**
     * 是否红点提示，已经达到等级，且礼包没有购买过
     */
    public boolean showRedPoint(int level) {

        Map<Object, LevelGiftAward132Cfg> cfgList = ConfigManager.getInstance().getConfigMap(LevelGiftAward132Cfg.class);

        for (LevelGiftAward132Cfg cfg : cfgList.values()) {

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
		if (getLimitDate() != null) {
			long currentTime = GuaJiTime.getMillisecond();
			long endTime = getLimitDate().getTime()
					+ hours * 3600000;
			int surplusTime = (int) ((endTime - currentTime) / 1000);
			return Math.max(surplusTime, 0);
		}
		return -1;
	}

}
