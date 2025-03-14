package com.guaji.cdk.http.param;

import java.util.Map;

import org.guaji.os.MyException;

/**
 * cdk使用参数
 * 
 */
public class UseCdkParam {
	private String playerid;
	private String puid;
	private String playername;
	private String serverId;
	private String cdk;
	private String reward; // 用作返回值

	public void toLowerCase() {

		if (playerid != null) {
			playerid = playerid.toLowerCase();
		}

		if (puid != null) {
			puid = puid.toLowerCase();
		}

		if (playername != null) {
			playername = playername.toLowerCase();
		}

		if (cdk != null) {
			cdk = cdk.toLowerCase();
		}
		
		if (serverId != null) {
			serverId = serverId.toLowerCase();
		}
	}

	public String getPlayerid() {
		return playerid;
	}

	public void setPlayerid(String playerid) {
		this.playerid = playerid;
	}

	public String getPuid() {
		return puid;
	}

	public void setPuid(String puid) {
		this.puid = puid;
	}

	public String getPlayername() {
		return playername;
	}

	public void setPlayername(String playername) {
		this.playername = playername;
	}

	public String getCdk() {
		return cdk;
	}

	public void setCdk(String cdk) {
		this.cdk = cdk;
	}

	public String getReward() {
		return reward;
	}

	public void setReward(String reward) {
		this.reward = reward;
	}
	
	public String getServerId() {
		return serverId;
	}

	public void setServerId(String serverId) {
		this.serverId = serverId;
	}

	public boolean initParam(Map<String, String> params) {
		try {
			playerid = params.get("playerid");
			puid = params.get("puid");
			playername = params.get("playername");
			cdk = params.get("cdk");
			serverId = params.get("serverId");

		} catch (Exception e) {
			MyException.catchException(e);
			return false;
		}
		return true;
	}
}
