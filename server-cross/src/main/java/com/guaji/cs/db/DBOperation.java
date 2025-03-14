package com.guaji.cs.db;

import java.sql.SQLException;

/**
 * 数据库操作实体, 由写DB线程操作
 */
public class DBOperation {

	/**
	 * 操作类型
	 */
	public int opType;

	/**
	 * 操作参数
	 */
	public int opArgs;

	/**
	 * 设置操作类型
	 * 
	 * @param opType
	 */
	public boolean setOpType(int opType) {
		if (opType > this.opType) {
			this.opType = opType;
			return true;
		}
		return false;
	}

	/**
	 * 获取操作参数
	 * 
	 * @return
	 */
	public int getOpArgs() {
		return opArgs;
	}

	/**
	 * 设置操作参数
	 * 
	 * @param opArgs
	 */
	public void setOpArgs(int opArgs) {
		this.opArgs = opArgs;
	}

	/**
	 * 清理操作变量
	 */
	public void clearOpState() {
		opType = DbOpUtil.NONE;
		opArgs = 0;
	}

	/**
	 * 添加对象接口
	 * 
	 * @return
	 * @throws SQLException
	 */
	public boolean insert() throws Exception {
		return false;
	}

	/**
	 * 删除对象接口
	 * 
	 * @return
	 * @throws SQLException
	 */
	public boolean delete() throws Exception {
		return false;
	}

	/**
	 * 更新对象接口
	 * 
	 * @return
	 * @throws SQLException
	 */
	public boolean update() throws Exception {
		return false;
	}

	/**
	 * 执行操作
	 * 
	 * @return
	 * @throws SQLException
	 */
	public boolean execute() throws Exception {
		boolean opRet = false;
		if (opType == DbOpUtil.INSERT) {
			opRet = insert();
		} else if (opType == DbOpUtil.DELETE) {
			opRet = delete();
		} else if (opType == DbOpUtil.UPDATE) {
			opRet = update();
		}
		clearOpState();
		return opRet;
	}
}
