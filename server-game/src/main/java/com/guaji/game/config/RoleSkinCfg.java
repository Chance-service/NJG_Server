package com.guaji.game.config;

import org.guaji.config.ConfigBase;
import org.guaji.config.ConfigManager;

import com.guaji.game.attribute.Attribute;

@ConfigManager.XmlResource(file = "xml/skin_NG.xml", struct = "map")
public class RoleSkinCfg extends ConfigBase {
	/**
	 * 配置id
	 */
	@Id
	protected final int id;
	/**
	 * 英雄角色裝備技能
	 */
	protected final String  own;
	/**
	 * 附加全隊技能
	 */
	protected final String  team;
	
	/**
	 * 裝備皮膚屬性變化
	 */
	private Attribute OwnAttr;
	/**
	 * 全隊屬性變化
	 */
	private Attribute TeamAttr;
		
	public RoleSkinCfg() {
		id = 0;
		own = "";
		team = "";
		OwnAttr = null;
		TeamAttr = null;
	}

	public int getId() {
		return id;
	}
	
	public String getOwn() {
		return own;
	}
	
	public String getTeam() {
		return team;
	}
	
	
	public Attribute getOwnAttr() {
		return OwnAttr;
	}
	
	public Attribute getTeamAttr() {
		return TeamAttr;
	}
			
	@Override
	protected boolean assemble() {
		this.OwnAttr = Attribute.valueOf(own);
		this.TeamAttr = Attribute.valueOf(team);
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
