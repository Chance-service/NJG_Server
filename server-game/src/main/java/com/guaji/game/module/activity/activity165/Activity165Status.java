package com.guaji.game.module.activity.activity165;


import java.util.HashSet;
import java.util.Set;

import org.guaji.os.GuaJiTime;

public class Activity165Status {

    private int score;
    private long scoreTime;
    private Set<Integer> MineSet;

    public Activity165Status() {
        this.score = 0;
        this.scoreTime = System.currentTimeMillis();
        this.MineSet = new HashSet<>();
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

    public long getScoreTime() {
        return scoreTime;
    }

    public void setScoreTime(long scoreTime) {
        this.scoreTime = scoreTime;
    }

    public Set<Integer> getMineSet() {
        return this.MineSet;
    }

    public void setMineSet(int index) {
    	this.MineSet.add(index);
    }
    
    public void clearMinSet() {
    	this.MineSet.clear();
    }
}
