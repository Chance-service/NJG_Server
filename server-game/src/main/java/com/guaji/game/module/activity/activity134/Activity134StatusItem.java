package com.guaji.game.module.activity.activity134;

public class Activity134StatusItem {

    private int cfgId;

    private int count;

    private boolean isLottery;

    private boolean isGot;

    private int rate;

    public Activity134StatusItem() {
    }

    public Activity134StatusItem(int cfgId, int count, boolean isLottery, boolean isGot, int rate) {
        this.cfgId = cfgId;
        this.count = count;
        this.isLottery = isLottery;
        this.isGot = isGot;
        this.rate = rate;
    }

    public int getCfgId() {
        return cfgId;
    }

    public void setCfgId(int cfgId) {
        this.cfgId = cfgId;
    }


    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public boolean isLottery() {
        return isLottery;
    }

    public void setLottery(boolean lottery) {
        isLottery = lottery;
    }

    public boolean isGot() {
        return isGot;
    }

    public void setGot(boolean got) {
        isGot = got;
    }

    public int getRate() {
        return rate;
    }

    public void setRate(int rate) {
        this.rate = rate;
    }
}
