package com.guaji.game.module.activity.fairyBless;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import com.google.gson.reflect.TypeToken;
import com.guaji.game.config.FairyBlessCfg;
import com.guaji.game.util.GsonUtil;

/**
 * 仙女的保佑
 * @author Melvin.Mao
 * @date Jun 28, 2017 4:22:15 PM
 */
public class FairyBlessStatus {

	private int stageId;

	/**
	 * 当前拥有的鲜花
	 */
	private int flowerCount;

	/**
	 * 祈祷的进度
	 */
	private String progress;

	public FairyBlessStatus() {
		flowerCount = 0;
		stageId = 0;
		// 将各个档次的进度值初始化
		Map<Integer, Integer> progressMap = new HashMap<Integer, Integer>();
		Set<Integer> set = FairyBlessCfg.typeMap.keySet();
		for (Integer type : set) {
			progressMap.put(type, 0);
		}
		progress = GsonUtil.getJsonInstance().toJson(progressMap, new TypeToken<HashMap<Integer, Integer>>() {
		}.getType());
	}

	public int getStageId() {
		return stageId;
	}

	public void setStageId(int stageId) {
		this.stageId = stageId;
	}

	public int getFlowerCount() {
		return flowerCount;
	}

	public void setFlowerCount(int flowerCount) {
		this.flowerCount = flowerCount;
	}

	public String getProgress() {
		return progress;
	}

	public void setProgress(String progress) {
		this.progress = progress;
	}

	public Map<Integer, Integer> getProgressMap() {
		return GsonUtil.getJsonInstance().fromJson(this.progress, new TypeToken<HashMap<Integer, Integer>>() {
		}.getType());
	}

	public void setProgressMap(Map<Integer, Integer> progressMap) {
		this.progress = GsonUtil.getJsonInstance().toJson(progressMap, new TypeToken<HashMap<Integer, Integer>>() {
		}.getType());
	}

	@Override
	public String toString() {
		return "FairyBlessStatus [stageId=" + stageId + ", flowerCount=" + flowerCount + ", progress=" + progress + "]";
	}

}
