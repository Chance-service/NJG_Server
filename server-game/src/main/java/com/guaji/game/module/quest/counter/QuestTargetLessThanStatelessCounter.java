package com.guaji.game.module.quest.counter;

import com.guaji.game.entity.QuestEntity;
import com.guaji.game.entity.QuestItem;
import com.guaji.game.module.quest.QuestEvent;
import com.guaji.game.protocol.Const.QuestEventType;

/**
 * 
 * @author crazyjohn
 *
 */
public class QuestTargetLessThanStatelessCounter extends BaseQuestTargetCounter {

	public QuestTargetLessThanStatelessCounter(QuestEventType eventType, int needCount, QuestItem quest,QuestEntity questEntity) {
		super(eventType, needCount, quest,questEntity);
	}

	@Override
	public void onQuestEvent(QuestEvent event) {
		// 是否在监听状态下
		if (!isListenableState()) {
			return;
		}
		
		boolean isFlag = false;
		
		// reduce the count
		if (this.targetEventType == event.getType()) {
			this.setFinishedCount(this.getFinishedCount() - event.getCount());
			isFlag = true;
		}
		// update quest state
		if (this.isUpToNeededCount()) {
			quest.onFinished();
			isFlag = true;
		}
		
		if(isFlag){
			this.questEntity.update();
		}
	}
	
	public boolean isUpToNeededCount()
	{
		if(quest.getFinishedCount() <= needCount)
		{
			return true;
		}
		
		return false;
	}

}
