package com.guaji.game.entity;

public class DailyQuestItem {
	/**
	 * 任务id
	 */
	private int id;
	/**
	 * 完成状态
	 */
	private int questStatus;

	/**
	 * 领取状态
	 */
	private int takeStatus;
	
	/**
	 * 已完成数量
	 */
	private int completeCount;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}
	
	public void setQuestStatus(int arg)
	{
		this.questStatus = arg;
	}
	
	public int getQuestStatus()
	{
		return this.questStatus;
	}

	public void setTakeStatus(int arg)
	{
		this.takeStatus = arg;
	}
	
	public int getTakeStatus()
	{
		return this.takeStatus;
	}
	
	public void setCompleteCount(int arg)
	{
		this.completeCount = arg;
		return;
	}
	
	public int getCompleteCount()
	{
		return this.completeCount;
	}

}
