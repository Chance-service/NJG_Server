package com.guaji.game.config;

import org.guaji.config.ConfigBase;
import org.guaji.config.ConfigManager;

@ConfigManager.XmlResource(file = "xml/fightValue.xml", struct = "map")
public class FightValueCfg extends ConfigBase {
	/**
	 * 配置id
	 */
	@Id
	private final int attrId;
	/**
	 * 参数值1
	 */
	private final float param1;
	/**
	 * 参数值2
	 */
	private final float param2;

	public FightValueCfg() {
		attrId = 0;
		param1 = 0;
		param2 = 0;
	}

	public int getAttrId() {
		return attrId;
	}

	public float getParam1() {
		return param1;
	}

	public float getParam2() {
		return param2;
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

	protected void clearStaticData() {
	}
}
