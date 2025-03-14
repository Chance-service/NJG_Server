package com.guaji.game.util;

import java.util.Date;

public class NjginfoData {
	int id ;
	String name;
	int level;
	int serverId;
	Date createTime;
	Date loginTime;
	int passMapId;
	int totalTime;
	double payMoney;
	
	public NjginfoData() 
	{
		this.id = 0;
		this.name = "";
		this.level = 0;
		this.createTime = null;
		this.loginTime = null;
		this.passMapId = 0;
		this.totalTime = 0;
		this.payMoney = 0.0;
	}

	public int getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	public int getLevel() {
		return level;
	}

	public int getServerId() {
		return serverId;
	}

	public Date getCreateTime() {
		return createTime;
	}

	public Date getLoginTime() {
		return loginTime;
	}

	public double getPayMoney() {
		return payMoney;
	}

	public void setId(int id) {
		this.id = id;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setLevel(int level) {
		this.level = level;
	}

	public void setServerId(int serverId) {
		this.serverId = serverId;
	}

	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}

	public void setLoginTime(Date loginTime) {
		this.loginTime = loginTime;
	}

	public void setPayMoney(double payMoney) {
		this.payMoney = payMoney;
	}
	public int getPassMapId() {
		return passMapId;
	}

	public int getTotalTime() {
		return totalTime;
	}

	public void setPassMapId(int passMapId) {
		this.passMapId = passMapId;
	}

	public void setTotalTime(int totalTime) {
		this.totalTime = totalTime;
	}
	
}
