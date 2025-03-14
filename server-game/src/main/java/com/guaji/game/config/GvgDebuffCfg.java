package com.guaji.game.config;

import org.guaji.config.ConfigBase;
import org.guaji.config.ConfigManager;

/**
 * GVG功能DEBUFF配置
 * 
 * @author Nannan.Gao
 * @date 2017-5-27 10:28:16
 */
@ConfigManager.XmlResource(file = "xml/gvgDebuff.xml", struct = "list")
public class GvgDebuffCfg extends ConfigBase {

	@Id
	private final int id;

	/**
	 * 连续杀敌个数
	 */
	private final int killNum;

	/**
	 * debuff值
	 */
	private final String debuffAttrs;

	public GvgDebuffCfg() {
		id = 0;
		killNum = 0;
		debuffAttrs = "";
	}

	@Override
	protected boolean assemble() {
		return true;
	}

	@Override
	protected boolean checkValid() {
		return true;
	}

	public int getId() {
		return id;
	}

	public int getKillNum() {
		return killNum;
	}

	public String getDebuffAttrs() {
		return debuffAttrs;
	}

	@Override
	public String toString() {
		return this.debuffAttrs;
	}
}
