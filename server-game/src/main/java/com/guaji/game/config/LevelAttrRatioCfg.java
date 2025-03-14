package com.guaji.game.config;

import java.util.List;

import org.guaji.config.ConfigBase;
import org.guaji.config.ConfigManager;

@ConfigManager.XmlResource(file = "xml/levelAttrRatio.xml", struct = "list")
public class LevelAttrRatioCfg extends ConfigBase {
	/**
	 * 等级
	 */
	protected final int level;
	/**
	 * 属性Id
	 */
	private final int attrId;
	/**
	 * 系数
	 */
	private final float ratio;

	public LevelAttrRatioCfg() {
		level = 0;
		attrId = 0;
		ratio = 0f;
	}

	public int getLevel() {
		return level;
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
	 * 清理相关静态数据
	 */
	protected void clearStaticData() {
	}

	public int getAttrId() {
		return attrId;
	}

	public float getRatio() {
		return ratio;
	}

	public static float getRatio(int level, int attrId) {
		List<LevelAttrRatioCfg> list = ConfigManager.getInstance().getConfigList(LevelAttrRatioCfg.class);
		for(LevelAttrRatioCfg levelAttrRatioCfg : list) {
			if(levelAttrRatioCfg.getLevel() == level && levelAttrRatioCfg.getAttrId() == attrId) {
				return levelAttrRatioCfg.ratio;
			}
		}
		return 0f;
	}
	
}
