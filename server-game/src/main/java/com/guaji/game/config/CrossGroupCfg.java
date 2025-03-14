package com.guaji.game.config;

import java.util.LinkedList;
import java.util.List;

import org.guaji.config.ConfigBase;
import org.guaji.config.ConfigManager;

@ConfigManager.XmlResource(file = "xml/crossGroup.xml", struct = "list")
public class CrossGroupCfg extends ConfigBase {

	/**
	 * 跨服ID
	 */
	private final int csId;

	/**
	 * 参与的服务器列表
	 */
	private final String serverList;

	/**
	 * IP配置
	 */
	private final String ip;

	/**
	 * 端口
	 */
	private final int port;

	/**
	 * 描述
	 */
	private final String desc;
	
	private List<String> serverInfoList;

	public CrossGroupCfg() {
		this.csId = 0;
		this.serverList = "";
		this.port = 0;
		this.ip = "";
		this.desc = "";
		this.serverInfoList = new LinkedList<String>();
	}

	@Override
	protected boolean assemble() {
		if (serverList.length() > 0) {
			String[] sers = serverList.split(",");
			for (String ser : sers) {
				serverInfoList.add(ser);
			}
		}
		return true;
	}

	@Override
	protected boolean checkValid() {
		return true;
	}

	public int getCsId() {
		return csId;
	}
	
	public String getIp() {
		return ip;
	}

	public int getPort() {
		return port;
	}

	public String getDesc() {
		return desc;
	}

	/**
	 * 获取分组配置
	 * 
	 * @param serverIdentify
	 * @return
	 */
	public static CrossGroupCfg getGroupCfg(String serverIdentify) {
		for (CrossGroupCfg groupCfg : ConfigManager.getInstance().getConfigList(CrossGroupCfg.class)) {
			for (String identify : groupCfg.serverInfoList) {
				if (identify.equals(serverIdentify)) {
					return groupCfg;
				}
			}
		}
		return null;
	}

	/**
	 * 获取连接跨服服务器个数
	 * 
	 * @return
	 */
	public int getServerSize() {
		return serverInfoList.size();
	}
}
