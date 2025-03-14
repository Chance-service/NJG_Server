package com.guaji.game.entity;

import java.util.HashSet;
import java.util.Set;

public class ExpeditionTask {
	/**
	 * 任务id
	 */
	private int id;
	/**
	 * 任务状态 0为初始状态，1为进行中（完成了，直接走奖励，上线后检测）
	 */
	private int status;

	/**
	 * 正在绑定的佣兵id
	 */
	private Set<Integer> doingRoleId;
	/**
	 * 任务完成时间
	 */
	private long completeTime;
	
	public ExpeditionTask() {
		this.doingRoleId = new HashSet<>();
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}
	
	public void setStatus(int arg)
	{
		this.status = arg;
	}
	
	public int getStatus()
	{
		return this.status;
	}

	public void setDoingRoleId(int id)
	{
		if(!this.doingRoleId.contains(id)){
			this.doingRoleId.add(id);
		}
	}

	public  Set<Integer> getDoingRoleId()
	{
		return this.doingRoleId;
	}
	
	public void clearDoingRoleId()
	{
		this.doingRoleId.clear();
	}
	
	public void setCompleteTime(long time)
	{
		this.completeTime = time;
		return;
	}
	
	public long getCompleteTime()
	{
		return this.completeTime;
	}

}
