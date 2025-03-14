package com.guaji.game.config;

import java.util.List;

import org.guaji.config.ConfigBase;
import org.guaji.config.ConfigManager;

@ConfigManager.XmlResource(file = "xml/allianceBattleVersus.xml", struct = "list")
public class AllianceBattleVersusCfg extends ConfigBase {
	
	/**
	 * 战斗阶段
	 */
	private final int group ;
	/**
	 * 进攻方
	 */
	private final int leftIndex ;
	/**
	 * 防守方
	 */
	private final int rightIndex ;
	/**
	 * 1 上半区 2 下半区
	 */
	private final int region ;
	
	public AllianceBattleVersusCfg(){
		this.group = 0;
		this.leftIndex = 0;
		this.rightIndex = 0;
		this.region = 0;
	}

	public int getGroup() {
		return group;
	}

	public int getRegion() {
		return region;
	}

	public int getLeftIndex() {
		return leftIndex;
	}

	public int getRightIndex() {
		return rightIndex;
	}

	public static AllianceBattleVersusCfg getBattleVersusCfg(int group,int index) {
		List<AllianceBattleVersusCfg> versusCfgList = ConfigManager.getInstance().getConfigList(AllianceBattleVersusCfg.class);
		for(AllianceBattleVersusCfg versusCfg : versusCfgList) {
			if(versusCfg.getGroup() == group && versusCfg.getLeftIndex() == index) {
				return versusCfg;
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
