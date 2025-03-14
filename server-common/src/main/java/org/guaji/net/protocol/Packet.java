package org.guaji.net.protocol;

import org.guaji.octets.Marshal;

/**
 * 数据序列化和反序列化
 */
public abstract class Packet extends Marshal {
	/**
	 * 协议id
	 */
	private int type;
	
	/**
	 * 构造函数
	 */
	public Packet(int type) {
		this.type = type;
	}
	
	/**
	 * 获取协议id
	 * @return
	 */
	public int getType() {
		return type;
	}
}
