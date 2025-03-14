package com.guaji.game.config;

import java.util.List;

import org.guaji.config.ConfigBase;
import org.guaji.config.ConfigManager;


@ConfigManager.XmlResource(file = "xml/roleBaseAttr.xml", struct = "list")
public class RoleBaseAttrCfg extends ConfigBase {
	@Id
	protected final int id;

	/**
	 * 初始等级
	 */
	protected final int level;

	/**
	 * 基础属性
	 */
	protected final String attrs;

	public RoleBaseAttrCfg() {
		id = 0;
		level = 0;
		attrs = null;
	}

	public int getId() {
		return id;
	}

	public int getLevel() {
		return level;
	}

	public String getAttrs() {
		return attrs;
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
	 * 获取配置基础属性
	 * 
	 * @param id
	 * @param level
	 * @return
	 */
	public static RoleBaseAttrCfg getRoleBaseAttrCfg(int id, int level) {
		List<RoleBaseAttrCfg> list = ConfigManager.getInstance().getConfigList(RoleBaseAttrCfg.class);
		for (RoleBaseAttrCfg cfg : list) {
			if (cfg.getId() == id && cfg.level == level) {
				return cfg;
			}
		}

		return null;
	}

	/**
	 * 清理相关静态数据
	 */
	protected void clearStaticData() {
	}
}
