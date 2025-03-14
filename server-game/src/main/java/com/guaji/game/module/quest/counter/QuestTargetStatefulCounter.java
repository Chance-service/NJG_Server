package com.guaji.game.module.quest.counter;

import com.guaji.game.entity.QuestEntity;
import com.guaji.game.entity.QuestItem;
import com.guaji.game.module.quest.QuestEvent;
import com.guaji.game.protocol.Const.QuestEventType;

/**
 * 带状态的任务计数器, 用来实现成就类型的任务;
 * <p>
 * 比如装备强化到多少级;
 * 
 * @author crazyjohn
 *
 */
public class QuestTargetStatefulCounter extends BaseQuestTargetCounter {

	public QuestTargetStatefulCounter(QuestEventType eventType, int needCount, QuestItem quest, QuestEntity questEntity) {
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
			// 大于等于的情况不处理(增量直接赋值的条件判断，对于这种事件数小于目标数不予赋值)
			if (this.getFinishedCount() >= event.getCount()) {
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
		if (quest.getFinishedCount() >= needCount) {
			return true;
		}

		return false;
	}

}
