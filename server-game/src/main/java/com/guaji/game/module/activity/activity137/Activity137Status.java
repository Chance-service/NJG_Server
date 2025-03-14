package com.guaji.game.module.activity.activity137;

public class Activity137Status {


    /**
     * 活动开启时间
     */
    private long activityTime;

    /**
     * 当天第几次登录获得的活动机会
     */
    private int loginTimes;

    /**
     * 抽奖次数
     */
    private int lotteryCount;

    /**
     * 老虎机第一列
     */
    private int firstLine;

    /**
     * 老虎机第一列
     */
    private int secondLine;

    /**
     * 老虎机第一列
     */
    private int thirdLine;

    /**
     * 是否已经使用
     */
    private boolean used;


    public Activity137Status() {
        this.activityTime = System.currentTimeMillis();
        this.loginTimes = 1;
        this.lotteryCount = 0;
        this.firstLine = 10;
        this.secondLine = 10;
        this.thirdLine = 10;
        this.used = false;
    }

    public Activity137Status init() {
        this.activityTime = System.currentTimeMillis();
        this.loginTimes = 1;
        this.lotteryCount = 0;
        this.firstLine = 10;
        this.secondLine = 10;
        this.thirdLine = 10;
        this.used = false;
        return this;
    }

    public Activity137Status init(int loginTimes) {
        this.activityTime = System.currentTimeMillis();
        this.loginTimes = loginTimes;
        this.lotteryCount = 0;
        this.firstLine = 10;
        this.secondLine = 10;
        this.thirdLine = 10;
        this.used = false;
        return this;
    }

    public int calcRate() {
        if (this.firstLine == this.secondLine && this.firstLine == this.thirdLine) {
            return this.firstLine * 3;
        } else {
            int max = (this.firstLine > this.secondLine) ? this.firstLine : this.secondLine;
            max = (max > this.thirdLine) ? max : this.thirdLine;
            return max;
        }
    }

    public long getActivityTime() {
        return activityTime;
    }

    public void setActivityTime(long activityTime) {
        this.activityTime = activityTime;
    }

    public int getLoginTimes() {
        return loginTimes;
    }

    public void setLoginTimes(int loginTimes) {
        this.loginTimes = loginTimes;
    }

    public int getLotteryCount() {
        return lotteryCount;
    }

    public void setLotteryCount(int lotteryCount) {
        this.lotteryCount = lotteryCount;
    }

    public int getFirstLine() {
        return firstLine;
    }

    public void setFirstLine(int firstLine) {
        this.firstLine = firstLine;
    }

    public int getSecondLine() {
        return secondLine;
    }

    public void setSecondLine(int secondLine) {
        this.secondLine = secondLine;
    }

    public int getThirdLine() {
        return thirdLine;
    }

    public void setThirdLine(int thirdLine) {
        this.thirdLine = thirdLine;
    }

    public boolean isUsed() {
        return used;
    }

    public void setUsed(boolean used) {
        this.used = used;
    }
}
