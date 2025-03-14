package com.guaji.game.entity;


import org.guaji.config.ConfigManager;

import com.guaji.game.config.QuestCfg;
import com.guaji.game.module.quest.QuestEvent;
import com.guaji.game.module.quest.counter.IQuestTargetCounter;
import com.guaji.game.module.quest.counter.QuestTargetCounterFactory;
import com.guaji.game.protocol.Const.QuestEventType;
import com.guaji.game.protocol.Const.QuestState;

public class QuestItem
{
	private int itemId;
	
	private long finishedCount;
	
	private int state;
	
	private transient IQuestTargetCounter counter;
	
	/**
	 * 初始化任务计数器;
	 */
	public void initCounter(QuestEntity questEntity) {
		// init counter
		QuestCfg config = ConfigManager.getInstance().getConfigByKey(QuestCfg.class, this.itemId);
		if (config == null) {
			return;
		}
		// create counter
		counter = QuestTargetCounterFactory.createCounter(config.getType(), QuestEventType.valueOf(config.getTargetType()), config.getNeedCount(), this,questEntity);
		counter.setFinishedCount(finishedCount);
	}
	
	public long getFinishedCount() {
		return finishedCount;
	}

	public void setFinishedCount(long finishedCount) {
		this.finishedCount = finishedCount;
	}
	
	/**
	 * 任务完成响应;
	 */
	public void onFinished() {
		if (this.state == QuestState.REWARD_VALUE || this.state == QuestState.FINISHED_VALUE) {
			// fuck u
			return;
		}
		this.setState(QuestState.FINISHED_VALUE);
	}

	/**
	 * 响应任务事件;
	 * 
	 * @param event
	 */
	public void onQuestEvent(QuestEvent event) {
		// 状态是否合法
		if (this.state == QuestState.FINISHED_VALUE || this.state == QuestState.REWARD_VALUE) {
			return;
		}
		if (counter == null) {
			return;
		}
		this.counter.onQuestEvent(event);
	}
	
	public int getState() {
		return state;
	}

	public void setState(int state) {
		this.state = state;
	}

	public int getItemId() {
		return itemId;
	}

	public void setItemId(int itemId) {
		this.itemId = itemId;
	}

	public IQuestTargetCounter counter() {
		return this.counter;
	}
}
