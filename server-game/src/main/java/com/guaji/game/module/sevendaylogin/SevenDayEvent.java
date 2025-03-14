package com.guaji.game.module.sevendaylogin;

import com.guaji.game.protocol.Const.SevenDayEventType;

/**
 * @author 作者 E-mail:Jeremy@163.com
 * @version 创建时间：2019年1月24日 下午5:32:50 类说明
 */
public class SevenDayEvent {
	/** 任务事件类型 */
	private final SevenDayEventType type;
	/** 次数 */
	private final long count;

	public SevenDayEvent(SevenDayEventType type, long count) {
		this.type = type;
		this.count = count;
	}

	public long getCount() {
		return count;
	}

	public SevenDayEventType getType() {
		return type;
	}
}
