package com.server.paynotice.util;

import com.server.paynotice.bean.SubscribeType;

/**
 * @author 作者 E-mail:Jeremy@163.com
 * @version 创建时间：Apr 23, 2019 5:30:52 PM 类说明
 */
public class Util {

	public static SubscribeType getSubTypeByName(String productName) {
		if (productName.equals("jp.co.school.battle.monthauto") || productName.equals("month")
				|| productName.equals("jp.co.school.battle.month"))
			return SubscribeType.MONTHCARD;
		if (productName.equals("jp.co.school.battle.weekauto") || productName.equals("week")
				|| productName.equals("jp.co.school.battle.week"))
			return SubscribeType.WEEKCARD;
		return SubscribeType.NONETYPE;
	}
}
