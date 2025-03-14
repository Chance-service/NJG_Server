package com.guaji.game.module.sevendaylogin.counter;

import com.guaji.game.entity.SevenDayQuestEntity;
import com.guaji.game.entity.SevenDayQuestItem;
import com.guaji.game.module.sevendaylogin.SevenDayEvent;
import com.guaji.game.protocol.Const.SevenDayEventType;

/**
 * 无状态的任务计算器，用来计算次数类型的任务;
 * <p>
 * 比如快速战斗多少次;
 * 
 * @author crazyjohn
 *
 */
public class QuestTargetStatelessCouter extends BaseQuestTargetCounter {

	public QuestTargetStatelessCouter(SevenDayEventType eventType, int needCount,SevenDayQuestItem quest, SevenDayQuestEntity questEntity) {
		super(eventType, needCount, quest,questEntity);
	}

	@Override
	public void onQuestEvent(SevenDayEvent event) {
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
		if(quest.getFinishNum() >= needCount)
		{
			return true;
		}
		
		return false;
	}

	
}
