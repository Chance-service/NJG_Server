package com.guaji.game.config;

import java.util.HashMap;
import java.util.Map;

import org.guaji.config.ConfigBase;
import org.guaji.config.ConfigManager;

@ConfigManager.XmlResource(file = "xml/obonStage.xml", struct = "map")
public class ObonStageCfg extends ConfigBase {
	/**
	 * 掉落物组ID
	 */
	@Id
	private final int id;

	private final int stage;

	private final int luckyRate;

	private final int addProgress;

	private final int totalProgress;

	private final String stageReward;

	public static Map<Integer, ObonStageCfg> stageMap = new HashMap<Integer, ObonStageCfg>();

	public ObonStageCfg() {
		id = 0;
		stage = 1;
		luckyRate = 0;
		addProgress = 0;
		totalProgress = 0;
		stageReward = "";
	}

	public int getAddProgress() {
		return addProgress;
	}

	public int getTotalProgress() {
		return totalProgress;
	}

	public String getStageReward() {
		return stageReward;
	}

	public int getId() {
		return id;
	}

	public int getStage() {
		return stage;
	}

	public int getLuckyRate() {
		return luckyRate;
	}

	@Override
	protected boolean assemble() {
		stageMap.put(this.stage, this);
		return super.assemble();
	}

}
