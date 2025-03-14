package com.guaji.game.config;

import java.util.Map;

import org.guaji.config.ConfigBase;
import org.guaji.config.ConfigManager;

@ConfigManager.XmlResource(file = "xml/cheatcheck.xml", struct = "map")
public class CheatCheckCfg extends ConfigBase {
	
	/**
	 * 配置id
	 */
	@Id
	private final int id;
	
	/**
	 * 戰力
	 */
	private final int power;
	
	/*
	 * 檢查範圍
	 */
	private final int checkrange;

	public CheatCheckCfg() {
		this.id = 0;
		this.power = 0;
		this.checkrange = 0;
	}

	public int getId() {
		return id;
	}

	public int getPower() {
		return power;
	}

	public int getCheckrange() {
		return checkrange;
	}

	/**
	 * 数据格式转化
	 */
	@Override
	protected boolean assemble() {
		return true;
	}

	/**
	 * 检测有消息
	 * 
	 * @return
	 */
	@Override
	protected boolean checkValid() {
		return true;
	}

	/**
	 * 清理相关静态数据
	 */
	protected void clearStaticData() {
	}
	
	/**
	 * 找出適用玩家本身檢查表
	 * @param power
	 * @return
	 */
	public static CheatCheckCfg getCheckCfg (int power) {
		Map<Object,CheatCheckCfg> MapCfg = ConfigManager.getInstance().getConfigMap(CheatCheckCfg.class);
		CheatCheckCfg cCfg = null;
		for (Object key : MapCfg.keySet()) {
			if (power >= MapCfg.get(key).getPower()) {
				cCfg =  MapCfg.get(key);
			} else {
				break;
			}
		}
		
		return cCfg;
	}
}
