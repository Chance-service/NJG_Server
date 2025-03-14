package com.guaji.game.module.activity.activity134;

import com.guaji.game.config.WeekendGiftReward134Cfg;
import org.guaji.config.ConfigManager;

import java.util.HashMap;
import java.util.Map;

public class Activity134Status {

    private Map<Integer, Activity134StatusItem> statusMap;

    /**
     * 重置活动时间/活动结束时间
     */
    private long restTime;

    /**
     * 活动预开启时间
     */
    private long preOpenTime;

    /**
     * 活动正式开启时间
     */
    private long startTime;


    public Activity134Status() {

        restTime = 0;
        startTime = 0;
        preOpenTime = 0;

        statusMap = new HashMap<>();
        Map<Object, WeekendGiftReward134Cfg> map = ConfigManager.getInstance().getConfigMap(WeekendGiftReward134Cfg.class);
        for (WeekendGiftReward134Cfg cfg : map.values()) {
            Activity134StatusItem item = new Activity134StatusItem(cfg.getId(), 0, false, false, 0);
            this.statusMap.put(cfg.getType(), item);
        }
    }

    public long getPreOpenTime() {
        return preOpenTime;
    }

    public void setPreOpenTime(long preOpenTime) {
        this.preOpenTime = preOpenTime;
    }

    public long getStartTime() {
        return startTime;
    }

    public void setStartTime(long startTime) {
        this.startTime = startTime;
    }

    public long getRestTime() {
        return restTime;
    }

    public void setRestTime(long restTime) {
        this.restTime = restTime;
    }

    public Map<Integer, Activity134StatusItem> getStatusMap() {
        return statusMap;
    }

    public void setStatusMap(Map<Integer, Activity134StatusItem> statusMap) {
        this.statusMap = statusMap;
    }
}
