package com.guaji.game.module.sevendaylogin.counter;


import com.guaji.game.entity.SevenDayQuestEntity;
import com.guaji.game.entity.SevenDayQuestItem;
import com.guaji.game.module.sevendaylogin.SevenDayEvent;
import com.guaji.game.protocol.Const.SevenDayEventType;

/**
 * 带状态的任务计数器, 用来实现成就类型的任务;
 * <p>
 * 比如排名;
 * 
 * @author crazyjohn
 *
 */
public class QuestTargetLessThanStatefulCounter extends BaseQuestTargetCounter {

	public QuestTargetLessThanStatefulCounter(SevenDayEventType eventType, int needCount, SevenDayQuestItem quest, SevenDayQuestEntity questEntity) {
		super(eventType, needCount, quest, questEntity);
	}

	@Override
	public void onQuestEvent(SevenDayEvent event) {
		// 是否在监听状态下
		if (!isListenableState()) {
			return;
		}

		// 直接替换指定的值
		if (this.targetEventType == event.getType()) {
			// 大于等于的情况不处理
			if (this.getFinishedCount() != 0 && this.getFinishedCount() <= event.getCount()) {
				return;
			}
	
			this.setFinishedCount(event.getCount());
			// make valid
			if (this.isUpToNeededCount()) {
				quest.onFinished();
			}
		

			this.questEntity.update();

		}

	}

	public boolean isUpToNeededCount() {
		
		if (quest.getFinishNum() <= needCount) {
			return true;
		}

		return false;
	}

}
