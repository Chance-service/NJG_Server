package com.guaji.game.config;

import org.guaji.config.ConfigBase;
import org.guaji.config.ConfigManager;
import org.guaji.os.GuaJiTime;

@ConfigManager.XmlResource(file = "xml/ReleaseURMultiple128.xml", struct = "map")
public class ReleaseURMultipleCfg128 extends ConfigBase implements Comparable<ReleaseURMultipleCfg128> {

    /**
     * 配置id
     */
    @Id
    private final int id;


    /**
     * 活动开始时间
     */
    private final String startTime;

    /**
     * 活动截至时间
     */
    private final String endTime;


    /**
     * 翻倍概率
     */
    private final int rate;


    public ReleaseURMultipleCfg128() {
        this.id = 0;
        this.startTime = "";
        this.endTime = "";
        this.rate = 0;
    }

    public int getId() {
        return id;
    }

    public String getStartTime() {
        return startTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public int getRate() {
        return rate;
    }

    @Override
    protected boolean assemble() {
        return true;
    }

    @Override
    protected boolean checkValid() {

        //List<ReleaseURMultipleCfg128> releaseURMultipleCfg128s = ConfigManager.getInstance().getConfigList(ReleaseURMultipleCfg128.class);

        boolean result = GuaJiTime.getTimeHourMinute(this.endTime) > GuaJiTime.getTimeHourMinute(this.startTime);
        if (!result) {
            throw new RuntimeException("activityTime must be closeTime >= endTime > startTime, ActivityTimeCfg id : " + this.id);
        }
        return true;
    }


    @Override
    public int compareTo(ReleaseURMultipleCfg128 o) {
        return this.id - o.getId();
    }

    /**
     * 清理相关静态数据
     */
    protected void clearStaticData() {
    }
}
