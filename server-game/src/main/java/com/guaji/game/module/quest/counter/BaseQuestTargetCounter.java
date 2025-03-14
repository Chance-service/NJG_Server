package com.guaji.game.module.quest.counter;

import com.guaji.game.entity.QuestEntity;
import com.guaji.game.entity.QuestItem;
import com.guaji.game.protocol.Const.QuestEventType;
import com.guaji.game.protocol.Const.QuestState;

/**
 * 任务目标计数器;
 * 
 * @author crazyjohn
 *
 */
public abstract class BaseQuestTargetCounter implements IQuestTargetCounter {
	/** 关心的任务事件 */
	protected QuestEventType targetEventType;
	/** 需要完成的次数 */
	protected int needCount;
	/** host */
	protected final QuestItem quest;
	
	protected final QuestEntity questEntity;

	@Override
	public QuestEventType getEventType() {
		return targetEventType;
	}

	public BaseQuestTargetCounter(QuestEventType eventType, int needCount, QuestItem quest,QuestEntity questEntity) {
		this.targetEventType = eventType;
		this.needCount = needCount;
		this.quest = quest;
		this.questEntity = questEntity;
	}

	public long getFinishedCount() {
		return quest.getFinishedCount();
	}

	@Override
	public void setFinishedCount(long finishedCount) {
		this.quest.setFinishedCount(finishedCount);
	}

	public QuestEventType getTargetType() {
		return targetEventType;
	}

	public int getNeedCount() {
		return needCount;
	}

//	@Override
//	public boolean isUpToNeededCount() {
//		return quest.getFinishedCount() >= needCount;
//	}

	/**
	 * 是否是需要监听事件的状态;
	 * 
	 * @param state
	 * @return
	 */
	protected boolean isListenableState() {
		if (quest.getState() == QuestState.UNACTIVE_VALUE || quest.getState() == QuestState.FINISHED_VALUE || quest.getState() == QuestState.REWARD_VALUE) {
			return false;
		}
		return true;
	}

}
