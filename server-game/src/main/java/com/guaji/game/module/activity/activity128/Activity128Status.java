package com.guaji.game.module.activity.activity128;


import com.google.gson.reflect.TypeToken;
import com.guaji.game.config.SysBasicCfg;
import com.guaji.game.util.GsonUtil;

import java.util.ArrayList;
import java.util.List;

import org.guaji.os.GuaJiTime;

public class Activity128Status {

    private long nextFreeTime;
    private int score;
    private long scoreTime;
    private int totalScore;
    private String boxArray;
    private int totalTimes;


    public Activity128Status() {
        this.nextFreeTime = System.currentTimeMillis();
        this.score = 0;
        this.scoreTime = System.currentTimeMillis();
        this.totalScore = 0;
        this.boxArray = GsonUtil.getJsonInstance().toJson(new ArrayList<>());
        this.totalTimes = 0;
    }

    public long getNextFreeTime() {
        return nextFreeTime;
    }

    public int getFreeLeftTime() {
        long currentTime = System.currentTimeMillis();
        return this.nextFreeTime > currentTime ? (int) ((nextFreeTime - currentTime) / 1000) : 0;
    }

    public void setNextFreeTime(long nextFreeTime) {
        this.nextFreeTime = nextFreeTime;
    }

    public int getScore() {    	
    	if(GuaJiTime.getMillisecond()<this.scoreTime)
    		return score;
    	else
    		return 0;
    }

    public int getScoreByTime(long calcTime) {
        if (calcTime < System.currentTimeMillis()) {
            return this.score;
        } else {
            return 0;
        }
    }

    public void setScore(int score) {
        this.score = score;
    }

    public int getTotalScore() {
        return totalScore;
    }

    public void setTotalScore(int totalScore) {
        this.totalScore = totalScore;
    }

    public long getScoreTime() {
        return scoreTime;
    }

    public void setScoreTime(long scoreTime) {
        this.scoreTime = scoreTime;
    }

    public List<Integer> getBoxArrayList() {
        return GsonUtil.getJsonInstance().fromJson(this.boxArray, new TypeToken<List<Integer>>() {}.getType());
    }

    public String getBoxArray() {
        return boxArray;
    }

    public void setBoxArray(List<Integer> boxArrayList) {
        this.boxArray = GsonUtil.getJsonInstance().toJson(boxArrayList);;
    }

    public int getTotalTimes() {
        return totalTimes;
    }

    public void setTotalTimes(int totalTimes) {
        this.totalTimes = totalTimes;
    }

}
