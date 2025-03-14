package com.guaji.game.module.quest.counter;

import com.guaji.game.entity.QuestEntity;
import com.guaji.game.entity.QuestItem;
import com.guaji.game.protocol.Const.QuestEventType;
import com.guaji.game.protocol.Const.QuestType;

/**
 * 任务计算器工厂;
 * 
 * @author crazyjohn
 *
 */
public class QuestTargetCounterFactory {

	public static IQuestTargetCounter createCounter(int type, QuestEventType eventType, int needCount, QuestItem quest,QuestEntity questEntity) {
		IQuestTargetCounter counter = null;
		if (type == QuestType.COUNT_VALUE) {
			counter = new QuestTargetStatelessCouter(eventType, needCount, quest,questEntity);
		}
		
		if (type == QuestType.STATEFUL_VALUE) 
		{
			counter = new QuestTargetStatefulCounter(eventType, needCount, quest,questEntity);
		}
		
		if(type == QuestType.LESS_THAN_COUNT_VALUE)
		{
			counter = new QuestTargetLessThanStatelessCounter(eventType, needCount, quest,questEntity);
		}
		
		if(type == QuestType.LESS_THAN_STATEFUL_VALUE)
		{
			counter = new QuestTargetLessThanStatefulCounter(eventType, needCount, quest,questEntity);
		}
		
		
		return counter;
	}

}
