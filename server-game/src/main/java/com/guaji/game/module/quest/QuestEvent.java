package com.guaji.game.module.quest;

import com.guaji.game.protocol.Const.QuestEventType;

/**
 * 任务事件;
 * 
 * @author crazyjohn
 *
 */
public class QuestEvent {
	/** 任务事件类型 */
	private final QuestEventType type;
	/** 次数 */
	private final long count;

	public QuestEvent(QuestEventType type, long count) {
		this.type = type;
		this.count = count;
	}

	public long getCount() {
		return count;
	}

	public QuestEventType getType() {
		return type;
	}
}
