package com.guaji.game.config;

import java.util.Map;

import org.guaji.config.ConfigBase;
import org.guaji.config.ConfigManager;

/**
 * 竞技场购买挑战次数相关配置
 */
@ConfigManager.XmlResource(file = "xml/arenaSnapCreateRule.xml", struct = "map")
public class ArenaSnapCreateRuleCfg extends ConfigBase {
	/**
	 * 配置id
	 */
	@Id
	protected final int id;
	/**
	 * 系统类型
	 */
	protected final int systype;
	/**
	 * 保留pvp前多少名
	 */
	protected final int maxRank;

	/**
	 * 时间
	 */
	protected final String time;

	/**
	 * 保留多少天
	 */
	protected final int holddays;

	public ArenaSnapCreateRuleCfg() {
		this.id = 0;
		this.systype = 0;
		this.maxRank = 0;
		this.holddays = 0;
		this.time = "";
	}

	public int getId() {
		return id;
	}

	public int getSystype() {
		return systype;
	}

	public int getMaxRank() {
		return maxRank;
	}

	public String getTime() {
		return time;
	}

	public int getHolddays() {
		return holddays;
	}

	@Override
	protected boolean assemble() {
		return true;
	}

	@Override
	protected boolean checkValid() {
		return true;
	}

	/**
	 * 获取商品
	 * 
	 * @param id
	 * @return
	 */
	public static ArenaSnapCreateRuleCfg getSnapCreateRuleBytype(int sysType) {
		Map<Object, ArenaSnapCreateRuleCfg> map = ConfigManager.getInstance()
				.getConfigMap(ArenaSnapCreateRuleCfg.class);
		for (ArenaSnapCreateRuleCfg cfg : map.values()) {
			if (cfg.getSystype() == sysType) {
				return cfg;
			}
		}
		return null;
	}
}
