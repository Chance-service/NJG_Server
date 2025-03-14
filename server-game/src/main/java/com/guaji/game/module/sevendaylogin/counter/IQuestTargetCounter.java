package com.guaji.game.module.sevendaylogin.counter;

import com.guaji.game.module.sevendaylogin.SevenDayEvent;
import com.guaji.game.protocol.Const.SevenDayEventType;

/**
 * 任务目标计数器接口;
 * 
 * @author crazyjohn
 *
 */
public interface IQuestTargetCounter {

	/**
	 * 是否达到了任务计数;
	 * 
	 * @return
	 */
	public boolean isUpToNeededCount();

	/**
	 * 响应任务事件;
	 * 
	 * @param event
	 */
	public void onQuestEvent(SevenDayEvent event);

	/**
	 * 设置完成次数;
	 * 
	 * @param finishedCount
	 */
	public void setFinishedCount(long finishedCount);

	/**
	 * 获取任务目标事件类型;
	 * 
	 * @return
	 */
	public SevenDayEventType getEventType();

}
