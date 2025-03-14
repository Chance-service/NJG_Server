package org.guaji.octets;

import org.guaji.os.MyException;

/**
 * 数据序列化和反序列化
 */
public abstract class Marshal {
	/**
	 * 序列化
	 * 
	 * @param stream
	 */
	public abstract void marshal(OctetsStream stream);

	/**
	 * 反序列化
	 * 
	 * @param stream
	 * @throws MyException
	 */
	public abstract void unmarshal(OctetsStream stream) throws MyException;

	/**
	 * 克隆一个对象
	 */
	@Override
	public abstract Marshal clone();

	/**
	 * 清理对象数据
	 * 
	 * @return
	 */
	public boolean clear() {
		return true;
	}
}
