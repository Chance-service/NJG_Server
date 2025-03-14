package com.guaji.game.config;

import org.guaji.config.ConfigBase;
import org.guaji.config.ConfigManager;

@ConfigManager.XmlResource(file = "xml/inviteAwards.xml", struct = "map")
public class InviteAwardCfg extends ConfigBase {
	@Id
	private final int id;
	private final int inviteAmount;
	private final String awards;
	
	public InviteAwardCfg(){
		id = 0;
		inviteAmount = 0;
		awards = "";
	}
	
	public int getId() {
		return id;
	}
	
	public int getInviteAmount() {
		return inviteAmount;
	}
	
	public String getAwards() {
		return awards;
	}
	
	/**
	 * 清理相关静态数据
	 */
	protected void clearStaticData() {
	}
}
