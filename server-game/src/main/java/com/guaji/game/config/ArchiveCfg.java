package com.guaji.game.config;

import org.guaji.config.ConfigBase;
import org.guaji.config.ConfigManager;

@ConfigManager.XmlResource(file = "xml/archive.xml", struct = "map")
public class ArchiveCfg extends ConfigBase {
	/**
	 * 配置id
	 */
	@Id
	protected final int id;
	/**
	 * 类型
	 */
	protected final int type;
	/**
	 * 对应的佣兵ID
	 */
	protected final int refer;
	/**
	 * 所需碎片数目
	 */
	protected final int count;

	public ArchiveCfg() {
		id = 0;
		type = 0;
		refer = 0;
		count = 0;
	}

	public int getId() {
		return id;
	}

	@Override
	protected boolean checkValid() {
		return true;
	}

	public int getType() {
		return type;
	}

	public int getRefer() {
		return refer;
	}

	public int getCount() {
		return count;
	}
}
