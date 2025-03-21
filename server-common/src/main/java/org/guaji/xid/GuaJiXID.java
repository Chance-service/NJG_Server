package org.guaji.xid;

/**
 * 对象唯一ID
 */
public class GuaJiXID implements Comparable<GuaJiXID>, Cloneable {
	/**
	 * 静态常量
	 */
	static GuaJiXID xidNull;
	/**
	 * 类型
	 */
	private int type;
	/**
	 * 真实id
	 */
	private int id;

	/**
	 * 默认构造函数
	 */
	protected GuaJiXID() {
		type = 0;
		id = 0;
	}

	/**
	 * 静态常量无效xid
	 */
	static public GuaJiXID nullXid() {
		if (xidNull == null) {
			xidNull = valueOf(0, 0);
		}
		return xidNull;
	}
	
	/**
	 * 实例化接口
	 * 
	 * @param type
	 * @return
	 */
	static public GuaJiXID valueOf(int type) {
		GuaJiXID guaJiXID = new GuaJiXID(type);
		return guaJiXID;
	}

	/**
	 * 实例化接口
	 * 
	 * @param type
	 * @param id
	 * @return
	 */
	static public GuaJiXID valueOf(int type, int id) {
		GuaJiXID guaJiXID = new GuaJiXID(type, id);
		return guaJiXID;
	}
	
	/**
	 * 构造函数
	 * 
	 * @param type
	 */
	protected GuaJiXID(int type) {
		this();
		this.type = type;
	}

	/**
	 * 私有化构造函数
	 * 
	 * @param type
	 * @param id
	 */
	protected GuaJiXID(int type, int id) {
		this(type);
		this.id = id;
	}

	/**
	 * 设置类别
	 * 
	 * @param type
	 */
	public void setType(int type) {
		this.type = type;
	}

	/**
	 * 获取类别
	 * 
	 * @return
	 */
	public int getType() {
		return type;
	}

	/**
	 * 设置真实id
	 * 
	 * @param id
	 */
	public void setId(int id) {
		this.id = id;
	}

	/**
	 * 获取真实id
	 * 
	 * @return
	 */
	public int getId() {
		return id;
	}

	/**
	 * 设置详细参数
	 * 
	 * @param type
	 * @param id
	 */
	public void set(int type, int id) {
		this.type = type;
		this.id = id;
	}

	/**
	 * 设置赋值
	 * 
	 * @param xid
	 */
	public void set(GuaJiXID xid) {
		this.type = xid.type;
		this.id = xid.id;
	}

	/**
	 * 是否有效
	 * 
	 * @return
	 */
	public boolean isValid() {
		return type > 0;
	}

	/**
	 * 获取hash线程索引
	 */
	public int getHashThread(int threadNum) {
		return id % threadNum;
	}

	/**
	 * 清理数据
	 */
	public boolean clear() {
		type = 0;
		id = 0;
		return true;
	}

	/**
	 * 对象克隆
	 */
	@Override
	public GuaJiXID clone() {
		return new GuaJiXID(type, id);
	}

	/**
	 * 相等比较
	 */
	@Override
	public boolean equals(Object xid) {
		if (xid instanceof GuaJiXID) {
			return type == ((GuaJiXID) xid).type && id == ((GuaJiXID) xid).id;
		}
		return false;
	}

	/**
	 * 转换为字符串
	 */
	@Override
	public String toString() {
		return String.format("[xid: (%d, %d)]", type, id);
	}

	/**
	 * 计算hashCode
	 */
	@Override
	public int hashCode() {
		return ((type << 24) | id);
	}

	/**
	 * 比较接口
	 */
	@Override
	public int compareTo(GuaJiXID xid) {
		// 先比较类型
		if (type < xid.type)
			return -1;
		else if (type > xid.type)
			return 1;

		// 再比较id
		if (id < xid.id)
			return -1;
		else if (id > xid.id)
			return 1;

		return 0;
	}
}
