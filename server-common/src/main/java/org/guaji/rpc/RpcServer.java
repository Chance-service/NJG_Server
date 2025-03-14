package org.guaji.rpc;

import java.util.Set;

import org.apache.mina.util.ConcurrentHashSet;
import org.guaji.log.Log;
import org.guaji.os.MyException;
import org.guaji.util.GuaJiTickable;
import org.guaji.zmq.GuaJiZmq;
import org.guaji.zmq.GuaJiZmqManager;
import org.guaji.zmq.GuaJiZmqProxy;
import org.zeromq.ZMQ;

public class RpcServer extends GuaJiTickable implements Runnable {
	/**
	 * 路由端
	 */
	GuaJiZmq router;
	/**
	 * 经销端
	 */
	GuaJiZmq dealer;
	/**
	 * 桥接对象
	 */
	GuaJiZmqProxy proxy;
	/**
	 * 工作者数量
	 */
	Set<RpcWorker> workers;
	/**
	 * 代理线程
	 */
	Thread proxyThread;
	/**
	 * 运行状况
	 */
	volatile boolean running;
	/**
	 * 事件超时
	 */
	static int eventTimeout = 5;

	/**
	 * 初始化
	 * 
	 * @param routerAddr
	 * @param dealerAddr
	 * @return
	 */
	public boolean init(String routerAddr, String dealerAddr, boolean threadMode) {
		router = GuaJiZmqManager.getInstance().createZmq(ZMQ.ROUTER);
		if (!router.bind(routerAddr)) {
			Log.errPrintln("rpc server bind router failed, addr: " + routerAddr);
			return false;
		}

		dealer = GuaJiZmqManager.getInstance().createZmq(ZMQ.DEALER);
		if (!dealer.bind(dealerAddr)) {
			Log.errPrintln("rpc server bind dealer failed, addr: " + dealerAddr);
			return false;
		}

		proxy = new GuaJiZmqProxy(router, dealer, true);
		Log.logPrintln(String.format("rpc server init success, router: %s, dealer: %s", routerAddr, dealerAddr));

		// 开启服务线程
		running = true;
		if (threadMode) {
			proxyThread = new Thread(this);
			proxyThread.start();
		}

		return true;
	}

	/**
	 * 关闭服务
	 */
	public void close() {
		running = false;
		if (proxyThread != null) {
			try {
				proxyThread.join();
			} catch (InterruptedException e) {
				MyException.catchException(e);
			}
		}

		GuaJiZmqManager.getInstance().closeZmq(router);
		GuaJiZmqManager.getInstance().closeZmq(dealer);

		proxy.clear();
		router = null;
		dealer = null;
		proxy = null;
	}

	/**
	 * 新增工作者
	 * 
	 * @param worker
	 */
	public void addWorker(RpcWorker worker) {
		if (workers == null) {
			workers = new ConcurrentHashSet<RpcWorker>();
		}
		workers.add(worker);
	}

	/**
	 * 更新状态
	 */
	protected void update(int timeout) {
		GuaJiZmqManager.getInstance().proxyZmq(proxy, timeout, true);

		// 工作者处理信息
		if (workers != null) {
			for (RpcWorker worker : workers) {
				if (worker.hasRequest(0)) {
					worker.response();
				}
			}
		}
	}

	/**
	 * 更新事件
	 */
	@Override
	public void onTick() {
		if (proxyThread == null) {
			update(0);
		}
	}

	/**
	 * 线程运行
	 */
	@Override
	public void run() {
		while (running) {
			update(eventTimeout);
		}
	}

	@Override
	public String getName() {
		return this.getClass().getSimpleName();
	}
}
