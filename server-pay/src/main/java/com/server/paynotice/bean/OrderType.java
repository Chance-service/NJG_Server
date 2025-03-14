package com.server.paynotice.bean;

/**
 * @author 作者 E-mail:Jeremy@163.com
 * @version 创建时间：2019年3月26日 下午3:54:03 类说明
 */
public enum OrderType {
	CONSUMABLE(1), SUBSCRIBE(2);

	private int value = 0;

	private OrderType(int value) { // 必须是private的，否则编译错误
		this.value = value;
	}

	public int value() {
		return this.value;
	}

	public static OrderType valueOf(int value) { // 手写的从int到enum的转换函数
		switch (value) {
		case 1:
			return SUBSCRIBE;
		case 2:
			return CONSUMABLE;
		default:
			return null;
		}
	}
}
