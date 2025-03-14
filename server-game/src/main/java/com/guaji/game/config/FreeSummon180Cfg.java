package com.guaji.game.config;

import org.guaji.config.ConfigBase;
import org.guaji.config.ConfigManager;

@ConfigManager.XmlResource(file = "xml/FreeSummon180.xml", struct = "map")
public class FreeSummon180Cfg extends ConfigBase {
	/**
	 * @Fields id :可視為天數
	 */
	@Id
	private final int id;
	/**
	 * 獎勵
	 */
	private final String awards;
	/**
	 * 最表定最大天數
	 */
	private static int maxDay ;
	
	public FreeSummon180Cfg() {
		this.id = 0;
		this.awards = null;
		maxDay = 0;
	}
	
	@Override
	protected boolean assemble() {
		if (id > maxDay) {
			maxDay = id;
		}
		return true;
	}

	public String getAwards() {
		return awards;
	}
	/**
	 * 最大天數
	 * @return
	 */
	public static int getMaxDay() {
		return maxDay;
	}
	
}
