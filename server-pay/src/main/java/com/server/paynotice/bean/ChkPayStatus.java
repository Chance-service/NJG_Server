package com.server.paynotice.bean;

/**
 * @author 作者 E-mail:Jeremy@163.com
 * @version 创建时间：Mar 14, 2019 5:40:52 PM 类说明
 */
public class ChkPayStatus {
	private int code;
	private String retval;

	
	public ChkPayStatus(int code, String retval) {
		super();
		this.code = code;
		this.retval = retval;
	}

	public int getCode() {
		return code;
	}

	public void setCode(int code) {
		this.code = code;
	}

	public String getRetval() {
		return retval;
	}

	public void setRetval(String retval) {
		this.retval = retval;
	}

}
