package com.server.paynotice.pojo;

import java.io.Serializable;

public class Snapshot1 implements Serializable {
	private static final long serialVersionUID = 1L;
	private int id;
	private int action;
	private String userid;
	private String inserttime;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getAction() {
		return action;
	}

	public void setAction(int action) {
		this.action = action;
	}

	public String getUserid() {
		return userid;
	}

	public void setUserid(String userid) {
		this.userid = userid;
	}

	public String getInserttime() {
		return inserttime;
	}

	public void setInserttime(String inserttime) {
		this.inserttime = inserttime;
	}

}
