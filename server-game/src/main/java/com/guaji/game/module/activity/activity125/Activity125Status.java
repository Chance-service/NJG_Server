package com.guaji.game.module.activity.activity125;

import org.guaji.os.GuaJiTime;

import java.sql.Timestamp;

public class Activity125Status {

    /**
     * 今日抽奖次数（用于限定初级场每日抽奖次数上线）
     */
    private Integer lowTodayCount;

    /**
     * 抽奖日期（用于限定初级场每日抽奖次数上线）
     */
    private String lowDrawDate;

    /**
     * 初级场总抽奖次数
     */
    private Integer lowTotalCount;

    /**
     *  中级场下次免费抽奖时间（用于判定中级场免费）
     */
    private Long mediumNextFreeTime;

    /**
     * 中级场总抽奖次数
     */
    private Integer mediumTotalCount;

    /**
     * 高级场最后一次抽奖时间（用于判定高级场免费）
     */
    private Long highNextFreeTime;

    /**
     * 高级场总抽奖次数
     */
    private Integer highTotalCount;

    public Activity125Status() {
        this.lowTodayCount = 0;
        this.lowDrawDate = GuaJiTime.getDateString();
        this.lowTotalCount = 0;
        this.mediumTotalCount = 0;
        this.mediumNextFreeTime = System.currentTimeMillis();
        this.highTotalCount = 0;
        this.highNextFreeTime = System.currentTimeMillis();
    }

    public Integer getLowLeftCount(int maxCount) {
        if (!GuaJiTime.getDateString().equals(this.lowDrawDate)) {
            this.lowDrawDate = GuaJiTime.getDateString();
            this.lowTodayCount = 0;
            return maxCount;
        } else {
            if (this.lowTodayCount >= maxCount) {
                return 0;
            } else {
                return maxCount - lowTodayCount;
            }
        }
    }

    public Integer getLowTodayCount() {
        return lowTodayCount;
    }

    public void setLowTodayCount(Integer lowTodayCount) {
        this.lowTodayCount = lowTodayCount;
    }

    public String getLowDrawDate() {
        return lowDrawDate;
    }

    public void setLowDrawDate(String lowDrawDate) {
        this.lowDrawDate = lowDrawDate;
    }

    public Integer getLowTotalCount() {
        return lowTotalCount;
    }

    public void setLowTotalCount(Integer lowTotalCount) {
        this.lowTotalCount = lowTotalCount;
    }

    public Long getMediumNextFreeTime() {
        return mediumNextFreeTime;
    }

    public void setMediumNextFreeTime(Long mediumNextFreeTime) {
        this.mediumNextFreeTime = mediumNextFreeTime;
    }

    public Integer getMediumTotalCount() {
        return mediumTotalCount;
    }

    public void setMediumTotalCount(Integer mediumTotalCount) {
        this.mediumTotalCount = mediumTotalCount;
    }

    public Long getHighNextFreeTime() {
        return highNextFreeTime;
    }

    public void setHighNextFreeTime(Long highNextFreeTime) {
        this.highNextFreeTime = highNextFreeTime;
    }

    public Integer getHighTotalCount() {
        return highTotalCount;
    }

    public void setHighTotalCount(Integer highTotalCount) {
        this.highTotalCount = highTotalCount;
    }
}
