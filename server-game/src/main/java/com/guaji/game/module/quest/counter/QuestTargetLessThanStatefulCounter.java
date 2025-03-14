package com.guaji.game.module.quest.counter;

import com.guaji.game.entity.QuestEntity;
import com.guaji.game.entity.QuestItem;
import com.guaji.game.module.quest.QuestEvent;
import com.guaji.game.protocol.Const.QuestEventType;

/**
 * 带状态的任务计数器, 用来实现成就类型的任务;
 * <p>
 * 比如排名;
 * 
 * @author crazyjohn
 *
 */
public class QuestTargetLessThanStatefulCounter extends BaseQuestTargetCounter {

	public QuestTargetLessThanStatefulCounter(QuestEventType eventType, int needCount, QuestItem quest, QuestEntity questEntity) {
		super(eventType, needCount, quest, questEntity);
	}

	@Override
	public void onQuestEvent(QuestEvent event) {
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
		if (quest.getFinishedCount() <= needCount) {
			return true;
		}

		return false;
	}

}
