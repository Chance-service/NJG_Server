package com.guaji.cdk.data;

import com.guaji.cdk.util.CdkUtil;


/**
 * CDK 包含信息
 * 
 */
public class CdkInfo {
	
	//cdk ID (一批cdk生成的唯一)
	private String id;
	/**
	 * cdk字符串
	 */
	private String cdk;
	/**
	 * cdk类型
	 */
	private String type; 
	/**
	 * 奖励信息
	 */
	private String reward;
	
	/**
	 * 玩家id
	 */
	private String puid;
	/**
	 * 玩家名
	 */
	private String playername;
	
	/**
	 * 服务器Id
	 */
	private String serverId;
	
	/**
	 * 使用时间
	 */
	private String usetime;

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

	@Override
	public String toString() {
		return String.format("id=%s&cdk=%s&puid=%s&playername=%s&usetime=%s&reward=%s&serverId=%s", id, cdk, puid, playername, usetime, reward,serverId);
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

	public String getUsetime() {
		return usetime;
	}

	public void setUsetime(String usetime) {
		this.usetime = usetime;
	}
	
	public String getServerId() {
		return serverId;
	}

	public void setServerId(String serverId) {
		this.serverId = serverId;
	}

	/**
	 * 是否被使用
	 * 
	 * @return
	 */
	public boolean isBeused() {
		if (puid != null && puid.length() > 0 && !"null".equals(puid)) {
			return true;
		}

		if (playername != null && playername.length() > 0 && !"null".equals(playername)) {
			return true;
		}

		return false;
	}

	/**
	 * 设置被使用
	 */
	public boolean setUsed(String puid, String playername, String serverId) {
		this.puid = puid;
		this.playername = playername;
		this.usetime = CdkUtil.getDateString();
		this.serverId = serverId;
		return true;
	}
	
	/**
	 * 从字符串转换
	 * 
	 * @param info
	 * @return
	 */
	public boolean parse(String info) {
		String[] params = info.split("&");
		if (params != null && params.length >= 2) {
			for (String param : params) {
				String[] kv = param.split("=");
				if (kv != null && kv.length == 2 && kv[0].length() > 0 && kv[1].length() > 0) {
					if ("id".equals(kv[0])) {
						id = kv[1];
					} else if ("cdk".equals(kv[0])) {
						cdk = kv[1];
					} else if ("puid".equals(kv[0])) {
						puid = kv[1];
					} else if ("playername".equals(kv[0])) {
						playername = kv[1];
					} else if ("usetime".equals(kv[0])) {
						usetime = kv[1];
					} else if ("reward".equals(kv[0])) {
						reward = kv[1];
				    } else if ("serverId".equals(kv[0])) {
				    	serverId = kv[1];
					}else if ("type".equals(kv[0])) {
						
						type = kv[1];
					}
				}
			}
			return true;
		}
		return false;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}
	
	
}
