package org.guaji.thread;

import java.util.List;
import java.util.ArrayList;
import java.util.Collection;
import java.util.concurrent.atomic.AtomicLong;

import org.guaji.nativeapi.NativeApi;
import org.guaji.os.MyException;

/**
 * 线程池封装
 */
public class GuaJiThreadPool {
	/**
	 * 线程数目
	 */
	protected int threadNum;
	/**
	 * 池名称
	 */
	protected String poolName;
	/**
	 * 线程队列
	 */
	protected List<GuaJiThread> threadList;
	/**
	 * 运行中
	 */
	protected volatile boolean running;
	/**
	 * 等待退出循环
	 */
	protected volatile boolean waitBreak;
	/**
	 * 当前轮换索引
	 */
	protected AtomicLong turnIndex;

	/**
	 * 线程池构造
	 */
	public GuaJiThreadPool(String poolName) {
		this.running = false;
		this.waitBreak = false;		
		this.turnIndex = new AtomicLong(0);
		this.threadList = new ArrayList<GuaJiThread>();
		this.poolName = poolName;
	}

	/**
	 * 初始化(poolSize表示线程数)
	 * 
	 * @param poolSize
	 * @return
	 */
	public boolean initPool(int poolSize) {
		// 检测
//		if (!NativeApi.checkHawk()) {
//			return false;
//		}
		
		threadNum = poolSize;
		for (int i = 0; i < threadNum; i++) {
			GuaJiThread thread = new GuaJiThread();
			thread.setName(poolName + "-" + thread.getId());
			threadList.add(thread);
		}
		return true;
	}

	/**
	 * 添加执行任务(threadIdx指定线程执行)
	 * 
	 * @param task
	 * @return
	 */
	public boolean addTask(GuaJiTask task) {
		return addTask(task, -1, false);
	}

	/**
	 * 添加执行任务(threadIdx指定线程执行)
	 * 
	 * @param task
	 * @param threadIdx
	 * @param first
	 * @return
	 */
	public boolean addTask(GuaJiTask task, int threadIdx, boolean first) {
		if (running && !waitBreak) {
			if (threadIdx < 0) {
				threadIdx = (int) (turnIndex.incrementAndGet() % threadNum);
			} else {
				threadIdx = threadIdx % threadNum;
			}

			if (threadIdx >= 0 && threadIdx < threadNum) {
				if (first) {
					return threadList.get(threadIdx).insertTask(task);
				} else {
					return threadList.get(threadIdx).addTask(task);
				}
			}
		}
		return false;
	}

	/**
	 * 开始执行
	 * 
	 * @return
	 */
	public boolean start() {
		if (!running) {
			running = true;
			for (int i = 0; i < threadNum; i++) {
				threadList.get(i).start();
			}
		}
		return true;
	}

	/**
	 * 获得所有线程数
	 * 
	 * @return
	 */
	public int getThreadNum() {
		return threadNum;
	}

	/**
	 * 获取线程ID
	 * 
	 * @param threadIdx
	 * @return
	 */
	public long getThreadId(int threadIdx) {
		if (threadIdx >= 0 && threadIdx < threadNum) {
			return threadList.get(threadIdx).getId();
		}
		return 0;
	}

	/*
	 * 
	 * 取得线程堆栈列表
	 */
	public Collection<GuaJiThread> getThreadList(){
		return threadList;
	}
	
	/*
	 * 打印内容
	 * 
	 */
	public String printDump(){
		
		StringBuffer sb =new StringBuffer(String.format("{poolname=%s:",this.poolName));
		
		int index = 0;
		for(GuaJiThread thread : getThreadList()){
			
			sb.append(String.format("{index=%s:{%s},", index,thread.printDump()));
			index ++;
		}
		sb.append("}");
		return sb.toString();
	}
	
	/**
	 * 结束所有线程
	 */
	public void close(boolean waitBreak) {
		if (this.running && !this.waitBreak) {
			// 设置等待退出或者直接退出模式
			this.waitBreak = waitBreak;
			if (!waitBreak) {
				this.running = false;
			}

			// 各个线程退出
			for (int i = 0; i < threadNum; i++) {
				try {
					threadList.get(i).close(waitBreak);
				} catch (Exception e) {
					MyException.catchException(e);
				}
			}

			// 设置标记
			this.running = false;
			this.waitBreak = false;
		}
	}

	/**
	 * 查询是否开始运作(调度中)
	 * 
	 * @return
	 */
	public boolean isRunning() {
		return running;
	}

	/**
	 * 查询是否等待退出
	 * 
	 * @return
	 */
	public boolean isWaitBreak() {
		return waitBreak;
	}
}
