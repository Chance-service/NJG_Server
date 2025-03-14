package org.guaji.util;

/**
 * 可更新列表
 */
public abstract class GuaJiTickable {
	/**
	 * 是否可tick
	 */
	boolean tickable = true;

	/**
	 * 更新
	 * 
	 */
	public abstract void onTick();

	/**
	 * 获取名字
	 */
	public abstract String getName();
	
	/**
	 * 是否有效
	 * 
	 * @return
	 */
	public boolean isTickable() {
		return tickable;
	}

	/**
	 * 设置可更新
	 * 
	 * @param tickable
	 */
	public void setTickable(boolean tickable) {
		this.tickable = tickable;
	}
}
