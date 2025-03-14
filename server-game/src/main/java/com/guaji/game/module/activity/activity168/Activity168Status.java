package com.guaji.game.module.activity.activity168;

import java.util.HashMap;
import java.util.Map;

import org.guaji.config.ConfigManager;

import com.guaji.game.config.SubScriptionCfg;

/**
 * 特權購買
 */
public class Activity168Status {
	/**
	 * 特權啟動<id,領取天數>
	 */
	private Map<Integer,Integer> activateId;

	public Activity168Status() {
		this.activateId = new HashMap<>();
	}
	
	/**
	 * 該特權是否啟動
	 * @param id
	 * @return
	 */
	public boolean isActivate(int id) {
		if (this.activateId.containsKey(id)) {
			SubScriptionCfg cfg = ConfigManager.getInstance().getConfigByKey(SubScriptionCfg.class,id);
			if (cfg == null) {
				return false;
			}
			int times = this.activateId.get(id);
			if (times >= cfg.getTimes()) {
				return false;
			}
			return true;
		}
		return false;
	}
	/**
	 * 取得啟動特權資訊
	 * @return
	 */
	public Map<Integer, Integer> getActivateId() {
		return activateId;
	}
	/**
	 * 設定特權次數
	 * @param key
	 * @param value
	 */
	public void setActivateId(int key ,int value) {
		if (this.activateId.containsKey(key)) {
			this.activateId.replace(key, value);
		} else {
			this.activateId.put(key, value);
		}
	}
}
