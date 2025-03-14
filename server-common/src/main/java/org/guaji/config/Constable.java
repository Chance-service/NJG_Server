package org.guaji.config;

import org.guaji.os.MyException;

/**
 * 不可变对象修改
 */
public class Constable {
	private boolean constLock = false;

	/**
	 * 默认构造
	 */
	public Constable() {
		constLock = false;
	}

	/**
	 * 构造
	 * 
	 * @param constLock
	 */
	public Constable(boolean constLock) {
		this.constLock = constLock;
	}

	/**
	 * 可变检测
	 * 
	 * @return
	 * @throws MyException
	 */
	public boolean constCheck() {
		if (constLock) {
			throw new RuntimeException("const object rejeck modification");
		}
		return true;
	}

	/**
	 * 锁定
	 * 
	 * @param constLock
	 * @return
	 */
	public void lockConst(boolean constLock) {
		this.constLock = constLock;
	}
}
