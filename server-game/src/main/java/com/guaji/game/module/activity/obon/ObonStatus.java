package com.guaji.game.module.activity.obon;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.guaji.log.Log;
import org.guaji.os.GuaJiTime;

import com.guaji.game.config.ObonStageCfg;

public class ObonStatus {
	/**
	 * 免费抽奖使用时间点
	 */
	private int lastFreeTime;
	/**
	 * 下次抽奖的翻倍，默认是1
	 */
	private int multiple = 1;
	/**
	 * buff剩余时间(秒)
	 */
	private int buffOverTime;
	/**
	 * 抽奖总次数
	 */
	private int totalTimes;
	/**
	 * 进度
	 */
	private String progress;
	/**
	 * 礼包状态
	 */
	private String canGetGift;

	public ObonStatus() {
		this.lastFreeTime = 0;
		this.multiple = 1;
		this.buffOverTime = 0;
		this.totalTimes = 0;
		int size = ObonStageCfg.stageMap.size();
		StringBuffer progressBuffer = new StringBuffer();
		StringBuffer canGetGiftBuffer = new StringBuffer();
		for (int i = 0; i < size; i++) {
			progressBuffer.append(0);
			canGetGiftBuffer.append(false);
			if (i < size - 1) {
				progressBuffer.append(",");
				canGetGiftBuffer.append(",");
			}
		}
		this.progress = progressBuffer.toString();
		this.canGetGift = canGetGiftBuffer.toString();
	}

	public int getLastFreeTime() {
		return lastFreeTime;
	}

	public void setLastFreeTime(int lastFreeTime) {
		this.lastFreeTime = lastFreeTime;
	}

	public int getMultiple() {
		return multiple;
	}

	public void setMultiple(int multiple) {
		this.multiple = multiple;
	}

	public int getBuffOverTime() {
		return buffOverTime;
	}

	public void setBuffOverTime(int buffOverTime) {
		this.buffOverTime = buffOverTime;
	}

	public int getTotalTimes() {
		return totalTimes;
	}

	public void setTotalTimes(int totalTimes) {
		this.totalTimes = totalTimes;
	}

	public String getProgress() {
		return progress;
	}

	public List<Integer> getAllProgress() {
		List<Integer> list = new ArrayList<Integer>();
		String[] progresses = StringUtils.split(progress, ",");
		for (String progress : progresses) {
			list.add(Integer.valueOf(progress));
		}
		return list;
	}

	public int getProgress(int stage) {
		String[] progresses = StringUtils.split(progress, ",");
		return Integer.valueOf(progresses[stage - 1]);
	}

	public void setProgress(String progress) {
		this.progress = progress;
	}

	public void setCurrentProgress(int currentStage, int currentProgress) {
		String[] progresses = StringUtils.split(progress, ",");
		progresses[currentStage - 1] = String.valueOf(currentProgress);
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < progresses.length; i++) {
			sb.append(progresses[i]);
			if (i < progresses.length - 1) {
				sb.append(",");
			}
		}
		this.progress = sb.toString();
	}

	public String getCanGetGift() {
		return canGetGift;
	}

	public boolean getCanGetGift(int stage) {
		String[] canGetGifts = StringUtils.split(canGetGift, ",");
		String bool = canGetGifts[stage - 1];
		if (bool.equals("true") || bool.equals("false")) {
			return Boolean.valueOf(bool);
		}
		Log.errPrintln(String.format("Obon activity status gift status error.canGetGift:[%s],currentStatus:[%s],stage:[%d]", canGetGift, bool, stage));
		return false;
	}

	public List<Boolean> getAllGiftStatus() {
		List<Boolean> list = new ArrayList<Boolean>();
		String[] canGetGifts = StringUtils.split(canGetGift, ",");
		for (String status : canGetGifts) {
			list.add(Boolean.valueOf(status));
		}
		return list;
	}

	public void setCanGetGift(boolean bol, int stage) {
		String[] canGetGifts = StringUtils.split(canGetGift, ",");
		canGetGifts[stage - 1] = String.valueOf(bol);
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < canGetGifts.length; i++) {
			sb.append(canGetGifts[i]);
			if (i < canGetGifts.length - 1) {
				sb.append(",");
			}
		}
		this.canGetGift = sb.toString();
	}

	public int getCurrentStage() {
		Map<Integer, ObonStageCfg> stageMap = ObonStageCfg.stageMap;
		for (Integer stage : stageMap.keySet()) {
			int maxStage = ObonManager.getMaxStage();
			if (stage == maxStage)
				return maxStage;
			int progress = getProgress(stage);
			int total = stageMap.get(stage).getTotalProgress();
			if (progress != total)
				return stage;
		}
		return 1;
	}

	/**
	 * 检查翻版是否超时(单位都是秒)
	 */
	public boolean canUseBuff() {
		int currentTime = GuaJiTime.getSeconds();
		if (this.buffOverTime > currentTime) {
			return true;
		}
		return false;
	}

	/**
	 * 该阶段是否完成
	 */
	public boolean stageCompleted(int stage) {
		ObonStageCfg cfg = ObonStageCfg.stageMap.get(stage);
		int total = cfg.getTotalProgress();
		int progress = getProgress(stage);
		if (total <= progress)
			return true;
		return false;
	}
}
