package com.guaji.game.module.activity.consumeitem;

/**
 * @author 作者 E-mail:Jeremy@163.com
 * @version 创建时间：Apr 7, 2019 7:03:27 PM 类说明
 */
public enum EnumConsumItemActivty {
	CONSUMITEM_YANGCHENGDAN(119); // 所有消耗类活动

	private int value = 0;

	private EnumConsumItemActivty(int value) {
		this.value = value;
	}

	public static EnumConsumItemActivty valueOf(int value) {
		switch (value) {
		case 118:
			return CONSUMITEM_YANGCHENGDAN;
		default:
			return null;
		}
	}

	public int value() {
		return this.value;
	}
}
