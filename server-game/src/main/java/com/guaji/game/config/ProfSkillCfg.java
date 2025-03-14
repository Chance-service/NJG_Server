package com.guaji.game.config;

import org.guaji.config.ConfigBase;
import org.guaji.config.ConfigManager;

@ConfigManager.XmlResource(file = "xml/profSkill.xml", struct = "map")
public class ProfSkillCfg extends ConfigBase {
	@Id
	protected final int id;
	/**
	 * 基础值1
	 */
	protected final int basicArg1;
	/**
	 * 基础值2
	 */
	protected final int basicArg2;
	/**
	 * 成长值1
	 */
	protected final int growArg1;
	/**
	 * 成长值2
	 */
	protected final int growArg2;
	
	public ProfSkillCfg() {
		id = 0;
		basicArg1 = 0;
		basicArg2 = 0;
		growArg1 = 0;
		growArg2 = 0;
	}

	public int getId() {
		return id;
	}
	
	public int getBasicArg1() {
		return basicArg1;
	}

	public int getBasicArg2() {
		return basicArg2;
	}

	public int getGrowArg1() {
		return growArg1;
	}

	public int getGrowArg2() {
		return growArg2;
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
}
