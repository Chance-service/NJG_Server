package org.guaji.thread;

import java.util.Deque;
import java.util.LinkedList;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import org.guaji.app.task.ProtoTask;
import org.guaji.app.task.TickTask;
import org.guaji.os.MyException;
import org.guaji.os.OSOperator;

/**
 * 线程类封装
 */
public class GuaJiThread extends Thread {
	/**
	 * 运行中
	 */
	protected volatile boolean running;

	/**
	 * 控制线程挂起，唤醒的对象
	 */
	private Object obj = new Object();
	/**
	 * 等待退出
	 */
	protected volatile boolean waitBreak;
	/**
	 * 线程任务队列
	 */
	protected ConcurrentLinkedQueue<GuaJiTask> taskQueue;
	/**
	 * 任务队列互斥锁
	 */
	protected Lock queueLock;
	/**
	 * 线程状态
	 */
	protected ThreadState state;
	/**
	 * 添加的任务总数
	 */
	protected long pushTaskCnt;
	/**
	 * 执行的任务总数
	 */
	protected long popTaskCnt;

	/**
	 * 状态定义
	 */
	protected enum ThreadState {
		STATE_NONE, STATE_RUNNING, STATE_CLOSING, STATE_CLOSED
	};

	/**
	 * 构造初始化
	 */
	protected GuaJiThread() {
		running = true;
		waitBreak = false;
		queueLock = new ReentrantLock();
		state = ThreadState.STATE_NONE;
		taskQueue = new ConcurrentLinkedQueue<GuaJiTask>();
	}

//	/**
//	 * 开始接口
//	 */
//	@Override
//	public synchronized void start() {
//		running = true;
//		super.start();
//
////		while (state != ThreadState.STATE_RUNNING) {
////			OSOperator.osSleep(10);
////		}
//	}

	/**
	 * 阻塞等待结束线程
	 * 
	 * @return
	 */
	public boolean close(boolean waitBreak) {
		synchronized (obj) {
			obj.notify();
		}
		if (this.running && !this.waitBreak) {
			this.waitBreak = waitBreak;
			if (!waitBreak) {
				this.running = false;
			}

			// 等待线程结束
			state = ThreadState.STATE_CLOSING;
//			try {
//				join();
//			} catch (Exception e) {
//				MyException.catchException(e);
//			}

			// 设置状态
			this.running = false;
			this.waitBreak = false;
			this.state = ThreadState.STATE_CLOSED;
		}
		return true;
	}

	/**
	 * 添加任务对象在末尾,只能使用在多任务线程
	 * 
	 * @param task
	 * @return
	 */
	protected boolean addTask(GuaJiTask task) {
		if (running && !waitBreak) {
//			queueLock.lock();
			try {
				taskQueue.add(task);
				pushTaskCnt++;

				synchronized (obj) {
					obj.notify();
				}
				return true;
			} finally {
//				queueLock.unlock();
			}
		}
		return false;
	}

	/**
	 * 添加任务对象在头,只能使用在多任务线程
	 * 
	 * @param task
	 * @return
	 */
	protected boolean insertTask(GuaJiTask task) {
		if (running && !waitBreak) {
//			queueLock.lock();
			try {
				taskQueue.offer(task);
//				taskQueue.push(task);
				pushTaskCnt++;
				synchronized (obj) {
					obj.notify();
				}
				return true;
			} finally {
//				queueLock.unlock();
			}
		}
		return false;
	}

	/**
	 * 获取Push进去的任务数
	 * 
	 * @return
	 */
	public long getPushTaskCnt() {
		return pushTaskCnt;
	}

	/**
	 * 获取Pop出来的任务数
	 * 
	 * @return
	 */
	public long getPopTaskCnt() {
		return popTaskCnt;
	}

	/**
	 * 获取任务队列
	 * 
	 * @return
	 */
	public ConcurrentLinkedQueue<GuaJiTask> getTasks() {
		return taskQueue;
	}

	/*
	 * 打印运行内容
	 * 
	 */
	public String printDump() {

		return String.format("{name=%s:[running=%s,waitBreak=%s,tasksize=%s,popTaskCnt=%s,pushTaskCnt=%s]}", getName(),
				isRunning(), isWaitBreak(), getTasks().size(), getPopTaskCnt(), getPushTaskCnt());
	}

	/**
	 * 获取线程运行状态
	 * 
	 * @return
	 */
	public boolean isRunning() {
		return running;
	}

	/**
	 * 线程是否等待执行完任务等待退出模式
	 * 
	 * @return
	 */
	public boolean isWaitBreak() {
		return waitBreak;
	}

	/**
	 * 是否等待关闭
	 * 
	 * @return
	 */
	public boolean isClosing() {
		return state == ThreadState.STATE_CLOSING;
	}

	/**
	 * 线程主执行函数
	 */
	@Override
	public void run() {
		state = ThreadState.STATE_RUNNING;
		while (running) {
			try {
				GuaJiTask task = null;
				if (!taskQueue.isEmpty()) {
//					queueLock.lock();
					try {
						if (!taskQueue.isEmpty()) {
//							task = taskQueue.pop();
							task = taskQueue.poll();
							popTaskCnt++;
						}
					} finally {
//						queueLock.unlock();
					}
				} else {
					synchronized (obj) {
						obj.wait();
					}
				}

				if (waitBreak) {
					if (task != null) {
						if (task.isMustRun()) {
							task.run();
							task.clear();
						}
					} else {
						break;
					}
				} else {
					if (task != null) {
						task.run();
						task.clear();
						// 若处理正好是1万 释放线程资源给其他线程
					

					} else {
//						OSOperator.sleep();
					}
				}
			} catch (Exception e) {
				MyException.catchException(e);
			}
		}
	}
}
