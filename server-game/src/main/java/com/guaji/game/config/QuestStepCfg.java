package com.guaji.game.config;

import org.guaji.config.ConfigBase;
import org.guaji.config.ConfigManager;

/**
 * 任务阶段配置;
 * 
 * @author crazyjohn
 *
 */
@ConfigManager.XmlResource(file = "xml/questStep.xml", struct = "map")
public class QuestStepCfg extends ConfigBase {

	@Id
	protected final int id;
	/** 下个阶段ID */
	protected final int nextId;
	/** 完成奖励 */
	protected final String reward;

	public QuestStepCfg() {
		this.id = 0;
		this.nextId = 0;
		this.reward = "";
	}

	public int getId() {
		return id;
	}

	public int getNextId() {
		return nextId;
	}

	public String getReward() {
		return reward;
	}

	/**
	 * 清理相关静态数据
	 */
	protected void clearStaticData() {
	}
}
