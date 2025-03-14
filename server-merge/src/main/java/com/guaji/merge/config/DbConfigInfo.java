package com.guaji.merge.config;

public class DbConfigInfo {

	private String ip; // 数据库地址
	private int port; // 数据库端口
	private String db; // 数据库名称
	private String username; // 用户名
	private String password; // 密码
	private int addServerId;// 是否需要提那件服务器编号前缀
	private int serverId;// 服务器编号

	/**
	 * 检查初始化数据是有有空值的
	 */
	public void checkEmpty() {
		if (ip == null || ip.equals("")) {
			throw new RuntimeException("ip为空");
		}
		if (port == 0) {
			throw new RuntimeException("port为空");
		}
		if (db == null || db.equals("")) {
			throw new RuntimeException("db为空");
		}
		if (username == null || username.equals("")) {
			throw new RuntimeException("username为空");
		}
//		if (password == null || password.equals("")) {
//			throw new RuntimeException("password为空");
//		}
		if (serverId == 0) {
			throw new RuntimeException("serverId为0");
		}
	}

	@Override
	public String toString() {
		StringBuffer sb = new StringBuffer();
		sb.append("ip=").append(ip).append(",port=").append(port)
				.append(",db=").append(db).append(",username=")
				.append(username).append(",password=").append(password)
				.append(",addServerId=").append(addServerId)
				.append(",serverId=").append(serverId);
		return sb.toString();
	}

	public String getIp() {
		return ip;
	}

	public void setIp(String ip) {
		this.ip = ip;
	}

	public int getPort() {
		return port;
	}

	public void setPort(int port) {
		this.port = port;
	}

	public String getDb() {
		return db;
	}

	public void setDb(String db) {
		this.db = db;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public int getServerId() {
		return serverId;
	}

	public void setServerId(int serverId) {
		this.serverId = serverId;
	}

	public int getAddServerId() {
		return addServerId;
	}

	public void setAddServerId(int addServerId) {
		this.addServerId = addServerId;
	}

}
