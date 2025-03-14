package com.guaji.game.config;

import java.util.ArrayList;
import java.util.List;

import org.guaji.config.ConfigBase;
import org.guaji.config.ConfigManager;

/**
 * 佣兵升阶
 * 
 * @author Administrator
 *
 */
@ConfigManager.XmlResource(file = "xml/roleSkinDemand.xml", struct = "list")
public class RoleSkinDemand extends ConfigBase {
	private static int maxStageLevel = 0;
	@Id
	protected final int id;

	/**
	 * 初始等级
	 */
	protected final int rankLevel;

	/**
	 * 升阶消耗
	 */
	protected final String needItem;


	public RoleSkinDemand() {
		id = 0;
		rankLevel = 0;
		needItem = null;
	}

	public int getId() {
		return id;
	}

	public int getRankLevel() {
		return rankLevel;
	}

	public String getneedItem() {
		return needItem;
	}

	public static int getMaxStageLevel() {
		return maxStageLevel;
	}

	public static void setMaxStageLevel(int maxStageLevel) {
		RoleSkinDemand.maxStageLevel = maxStageLevel;
	}

	@Override
	protected boolean assemble() {
		if (rankLevel > maxStageLevel) {
			maxStageLevel = rankLevel;
		}
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
	public static RoleSkinDemand getRoleSkinDemandCfg(int id, int rankLevel) {
		List<RoleSkinDemand> list = ConfigManager.getInstance().getConfigList(RoleSkinDemand.class);
		for (RoleSkinDemand cfg : list) {
			if (cfg.getId() == id && cfg.rankLevel == rankLevel) {
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

	/**
	 * 获取当前佣兵皮膚解鎖相簿配置
	 * 
	 * @param roleId
	 * @return
	 */
	public static List<RoleSkinDemand> getCurrentRoleSkinDemandCfgList(int roleId) {
		List<RoleSkinDemand> list = new ArrayList<>();
		List<RoleSkinDemand> cfgs = ConfigManager.getInstance().getConfigList(RoleSkinDemand.class);
		for (RoleSkinDemand cfg : cfgs) {
			if (cfg.getId() == roleId) {
				list.add(cfg);
			}
		}
		return list;
	}
}
