package com.guaji.game.module.quest.counter;

import com.guaji.game.entity.QuestEntity;
import com.guaji.game.entity.QuestItem;
import com.guaji.game.module.quest.QuestEvent;
import com.guaji.game.protocol.Const.QuestEventType;

/**
 * 无状态的任务计算器，用来计算次数类型的任务;
 * <p>
 * 比如快速战斗多少次;
 * 
 * @author crazyjohn
 *
 */
public class QuestTargetStatelessCouter extends BaseQuestTargetCounter {

	public QuestTargetStatelessCouter(QuestEventType eventType, int needCount,QuestItem quest, QuestEntity questEntity) {
		super(eventType, needCount, quest,questEntity);
	}

	@Override
	public void onQuestEvent(QuestEvent event) {
		// 是否在监听状态下
		if (!isListenableState()) {
			return;
		}
		
		boolean isFlag = false;
		// increment the count
		if (this.targetEventType == event.getType()) {
			this.setFinishedCount(this.getFinishedCount() + event.getCount());
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
		if(quest.getFinishedCount() >= needCount)
		{
			return true;
		}
		
		return false;
	}

}
