package com.guaji.game.module.activity.welfareRewardByRegDate;

import com.guaji.game.util.ActivityUtil;

import java.util.Date;

/**
 * @author 作者 E-mail:Jeremy@163.com
 * @version 创建时间：2019年5月24日 下午11:08:41 类说明
 */
public class WelfareRewardStatusByRegDate {
    /**
     * 当前阶段
     */
    private int currentStep;
    /**
     * 可不可以领奖
     */
    private boolean canPlay;

    public WelfareRewardStatusByRegDate() {
        super();
        this.currentStep = 1;
        this.canPlay = true;
    }

    public int getCurrentStep() {
        return currentStep;
    }

    public void setCurrentStep(int currentStep) {
        this.currentStep = currentStep;
    }

    public boolean canPlay() {
        return canPlay;
    }

    public boolean isCanPlayByDate(long registerTime, long activityStartTime, int activityDays, int mergeTime) {
        if (!canPlay) {
            return false;
        } else {
            this.canPlay = ActivityUtil.getLeftTimeByRegister(registerTime, activityStartTime, activityDays,mergeTime) > 0;
            return this.canPlay;
        }
    }

    public void setCanPlay(boolean canPlay) {
        this.canPlay = canPlay;
    }
}
