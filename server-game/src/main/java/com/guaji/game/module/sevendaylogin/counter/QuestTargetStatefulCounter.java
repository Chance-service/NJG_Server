package com.guaji.game.module.sevendaylogin.counter;

import com.guaji.game.entity.SevenDayQuestEntity;
import com.guaji.game.entity.SevenDayQuestItem;

import com.guaji.game.module.sevendaylogin.SevenDayEvent;
import com.guaji.game.protocol.Const.SevenDayEventType;

/**
 * 带状态的任务计数器, 用来实现成就类型的任务;
 * <p>
 * 比如装备强化到多少级;
 * 
 * @author crazyjohn
 *
 */
public class QuestTargetStatefulCounter extends BaseQuestTargetCounter {

	public QuestTargetStatefulCounter(SevenDayEventType eventType, int needCount, SevenDayQuestItem quest, SevenDayQuestEntity questEntity) {
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
		if (quest.getFinishNum() >= needCount) {
			return true;
		}

		return false;
	}

}
