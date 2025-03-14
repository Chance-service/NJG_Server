package com.guaji.game.entity;

import org.guaji.config.ConfigManager;

import com.guaji.game.config.SevenDayQuestCfg;
import com.guaji.game.module.sevendaylogin.SevenDayEvent;
import com.guaji.game.module.sevendaylogin.counter.IQuestTargetCounter;
import com.guaji.game.module.sevendaylogin.counter.QuestTargetCounterFactory;

import com.guaji.game.protocol.Const.QuestState;
import com.guaji.game.protocol.Const.SevenDayEventType;

/**
 * @author 作者 E-mail:Jeremy@163.com
 * @version 创建时间：2019年1月24日 下午5:14:46 类说明
 */
public class SevenDayQuestItem {
	/**
	 * 任务id
	 */
	private int id;
	/**
	 * 完成状态
	 */
	private int status;

	/**
	 * 已完成数量
	 */
	private long finishNum;

	private transient IQuestTargetCounter counter;

	/**
	 * 初始化任务计数器;
	 */
	public void initCounter(SevenDayQuestEntity questEntity) {
		SevenDayQuestCfg config = ConfigManager.getInstance().getConfigByKey(SevenDayQuestCfg.class, this.id);
		if (config == null) {
			return;
		}
		counter = QuestTargetCounterFactory.createCounter(config.getType(),
				SevenDayEventType.valueOf(config.getTargetType()), config.getNeedCount(), this, questEntity);
		counter.setFinishedCount(finishNum);
	}

	/**
	 * 响应任务事件;
	 * 
	 * @param event
	 */
	public void onQuestEvent(SevenDayEvent event) {
		// 状态是否合法
		if (this.status == QuestState.FINISHED_VALUE || this.status == QuestState.REWARD_VALUE) {
			return;
		}
		if (counter == null) {
			return;
		}
		this.counter.onQuestEvent(event);
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public long getFinishNum() {
		return finishNum;
	}

	public void setFinishNum(long finishNum) {
		this.finishNum = finishNum;
	}

	/**
	 * 任务完成响应;
	 */
	public void onFinished() {
		if (this.status == QuestState.REWARD_VALUE || this.status == QuestState.FINISHED_VALUE) {
			// fuck u
			return;
		}
		this.setStatus(QuestState.FINISHED_VALUE);
	}

	/**
	 * @param item
	 * @return 对比两个对象的属性值是否相等
	 */
	public boolean chkqual(SevenDayQuestItem item) {
		if (this.getFinishNum() != item.getFinishNum())
			return false;
		if (this.getStatus() != item.getStatus())
			return false;
		return true;
	}

}
