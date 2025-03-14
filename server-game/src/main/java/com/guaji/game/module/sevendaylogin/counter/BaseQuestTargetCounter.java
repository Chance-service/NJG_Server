package com.guaji.game.module.sevendaylogin.counter;


import com.guaji.game.entity.SevenDayQuestEntity;
import com.guaji.game.entity.SevenDayQuestItem;
import com.guaji.game.protocol.Const.QuestState;
import com.guaji.game.protocol.Const.SevenDayEventType;

/**
 * 任务目标计数器;
 * 
 * @author crazyjohn
 *
 */
public abstract class BaseQuestTargetCounter implements IQuestTargetCounter {
	/** 关心的任务事件 */
	protected SevenDayEventType targetEventType;
	/** 需要完成的次数 */
	protected int needCount;
	/** host */
	protected final SevenDayQuestItem quest;

	protected final SevenDayQuestEntity questEntity;

	@Override
	public SevenDayEventType getEventType() {
		return targetEventType;
	}

	public BaseQuestTargetCounter(SevenDayEventType eventType, int needCount, SevenDayQuestItem quest,
			SevenDayQuestEntity questEntity) {
		this.targetEventType = eventType;
		this.needCount = needCount;
		this.quest = quest;
		this.questEntity = questEntity;
	}

	public long getFinishedCount() {
		return quest.getFinishNum();
	}

	@Override
	public void setFinishedCount(long finishedCount) {
		this.quest.setFinishNum(finishedCount);
	}

	public SevenDayEventType getTargetType() {
		return targetEventType;
	}

	public int getNeedCount() {
		return needCount;
	}

	/**
	 * 是否是需要监听事件的状态;
	 * 
	 * @param state
	 * @return
	 */
	protected boolean isListenableState() {

		if (quest.getStatus() == QuestState.UNACTIVE_VALUE || quest.getStatus() == QuestState.FINISHED_VALUE
				|| quest.getStatus() == QuestState.REWARD_VALUE) {
			return false;
		}
		return true;
	}

}
