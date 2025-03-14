package org.guaji.thread;

import org.guaji.cache.CacheObj;

/**
 * 线程任务对象封装
 */
public abstract class GuaJiTask extends CacheObj {
	/**
	 * 任务类型
	 */
	private int taskType;
	/**
	 * 必须被执行(退出时会检测)
	 */
	private boolean mustRun;
	
	/**
	 * 构造函数
	 * 
	 * @param taskType
	 */
	public GuaJiTask() {
		this.taskType = 0;
		this.mustRun = false;
	}
	
	/**
	 * 构造函数
	 * 
	 * @param taskType
	 */
	public GuaJiTask(int taskType) {
		this.taskType = taskType;
		this.mustRun = false;
	}

	/**
	 * 构造函数
	 * 
	 * @param mustRun
	 */
	public GuaJiTask(boolean mustRun) {
		this.taskType = 0;
		this.mustRun = mustRun;
	}
	
	/**
	 * 获取任务类型
	 * 
	 * @return
	 */
	public int getTaskType() {
		return taskType;
	}

	/**
	 * 设置任务类型
	 * 
	 * @param taskType
	 */
	public void setTaskType(int taskType) {
		this.taskType = taskType;
	}

	/**
	 * 设置必须被执行
	 * 
	 * @param mustRun
	 */
	public void setMustRun(boolean mustRun) {
		this.mustRun = mustRun;
	}
	
	/**
	 * 是否必须被执行
	 * 
	 * @return
	 */
	public boolean isMustRun() {
		return mustRun;
	}
	
	/**
	 * 任务清理
	 */
	protected void clear() {
	}
	
	/**
	 * 任务克隆
	 */
	@Override
	protected CacheObj clone() {
		return null;
	}
	
	/**
	 * 线程调用的任务执行函数
	 * 
	 * @return
	 */
	protected abstract int run();
}
