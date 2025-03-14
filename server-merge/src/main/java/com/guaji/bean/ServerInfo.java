package com.guaji.bean;

public class ServerInfo {
	private String name;
	private String state;
	private String address;
	private int port;
	private int order;
	private int id;

	
	public ServerInfo(String name, String state, String address, int port, int order, int id) {
		super();
		this.name = name;
		this.state = state;
		this.address = address;
		this.port = port;
		this.order = order;
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getState() {
		return state;
	}

	public void setState(String state) {
		this.state = state;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public int getPort() {
		return port;
	}

	public void setPort(int port) {
		this.port = port;
	}

	public int getOrder() {
		return order;
	}

	public void setOrder(int order) {
		this.order = order;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

}
