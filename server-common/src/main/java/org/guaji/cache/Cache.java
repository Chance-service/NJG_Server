package org.guaji.cache;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * 对象缓存管理器(线程安全)
 */
public class Cache {
	/**
	 * 对象存根, 用作构造器
	 */
	CacheObj stub;
	/**
	 * 缓存容器
	 */
	Queue<CacheObj> cache;

	/**
	 * 构造函数
	 * 
	 * @param stub
	 */
	public Cache(CacheObj stub) {
		this(stub, 0);
	}

	/**
	 * stub为存根, count为预开辟个数
	 * 
	 * @param stub
	 * @param count
	 */
	public Cache(CacheObj stub, int count) {
		this.stub = stub;
		this.cache = new ConcurrentLinkedQueue<CacheObj>();

		// 预开辟
		for (int i = 0; i < count; i++) {
			CacheObj obj = stub.clone();
			cache.add(obj);
		}
	}

	/**
	 * 从缓存中创建对象
	 * 
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public <T> T create() {
		CacheObj obj = cache.poll();
		if (obj == null) {
			obj = stub.clone();
		}
		return (T) obj;
	}

	/**
	 * 把用完的对象放回缓存
	 * 
	 * @param obj
	 */
	public void release(CacheObj obj) {
		if (obj != null) {
			cache.add(obj);
		}
	}
}
