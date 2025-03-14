package com.guaji.game.config;

import org.guaji.config.ConfigBase;
import org.guaji.config.ConfigManager;

@ConfigManager.XmlResource(file = "xml/ring.xml", struct = "map")
public class RingCfg extends ConfigBase {

	@Id
	protected final int id;

	protected final int roleId;
	/**
	 * 属性添加类型 0 佣兵 1 佣兵 + 主角
	 */
	protected final int type;

	protected final String attrs;

	protected final String buffs;

	protected final int reviveTimes;
	
	protected final String reviveAttrs;
	
	

	public RingCfg() {
		id = 0;
		type = 0;
		attrs = null;
		roleId = 0;
		buffs = null;
		reviveTimes = 0;
		reviveAttrs=null;
	
	}

	public int getId() {
		return id;
	}

	public int getType() {
		return type;
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

	public int getRoleId() {
		return roleId;
	}

	public String getBuffs() {
		return buffs;
	}

	public int getReviveTimes() {
		return reviveTimes;
	}
	
	public boolean isAddAttr() {
		
		return true;
		//return this.reviveTimes==0;
	}

	
	public String getReviveAttrs() {
		return reviveAttrs;
	}

	/**
	 * 清理相关静态数据
	 */
	protected void clearStaticData() {
	}

}
