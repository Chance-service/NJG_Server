package com.guaji.game.config;

import org.guaji.config.ConfigBase;
import org.guaji.config.ConfigManager;


@ConfigManager.XmlResource(file = "xml/EighteenPrincesHelpAward.xml", struct = "map")
public class EighteenPrincesHelpAwardCfg extends ConfigBase {


	@Id
	private final int id;

	/**
	 * 奖励物品
	 */
	private final String rewards;



	public EighteenPrincesHelpAwardCfg() {
		id = 0;
		rewards = "";
	}

	public int getId() {
		return id;
	}

	public String getRewards() {
		return rewards;
	}


	@Override
	protected boolean assemble() {
		return true;
	}

	@Override
	protected boolean checkValid() {
		return true;
	}

}
